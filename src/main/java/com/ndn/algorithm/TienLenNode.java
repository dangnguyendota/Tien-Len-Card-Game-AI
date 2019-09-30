package com.ndn.algorithm;

import com.ndn.base.*;
import com.ndn.exeption.TLException;
import com.ndn.objects.Pass;
import com.ndn.objects.QuadSequence;
import com.ndn.objects.Quads;
import com.ndn.objects.TripSequence;
import com.ndn.util.DangNguyenDota;

import java.util.ArrayList;
import java.util.LinkedList;

public class TienLenNode implements Node {
    private Node parent;
    private BaseObject move;
    private LinkedList<Node> children;
    /* Số nước đi chưa được chọn còn lại trong node */
    private ArrayList<BaseObject> unexploredMoves;
    private Reward reward;
    private int visit;
    private int currentPlayIndex;
    private double C = Math.sqrt(2.0);
    private double A = Math.sqrt(20000);
    private double K = 0;
    private boolean usingK = false;

    TienLenNode(TienLenNode parent, BaseObject move, int player, Game game) {
        this.move = move;
        this.parent = parent;
        this.children = new LinkedList<>();
        this.currentPlayIndex = player;
        this.reward = new GameReward(game.getMaxPlayer());
        /* config node */
        if(parent != null) {
            this.setC(parent.getC());
            this.usingK = parent.usingK;
            this.K = parent.K;
        }
        /* load moves */
        this.unexploredMoves = new ArrayList<>();
        ArrayList<BaseObject> list = game.getAvailableMoves();
        if(!game.end()) {
            if (list.size() > 0 && list.get(list.size() - 1).getCards().length == game.getCurrentPlayer().getCardLength()) {
                this.unexploredMoves.add(list.get(list.size() - 1));
            } else {
                this.unexploredMoves.addAll(list);
                if(parent == null && game.getCurrentPlayerIndex() == game.getPreviousPlayerIndex()){
                    removeUnnecessaryMoves(game);
                } else {
                    if (game.getCurrentPlayerIndex() != game.getPreviousPlayerIndex()) {
                        this.unexploredMoves.add(new Pass());
                    }
                }
            }
        }
    }

    private void removeUnnecessaryMoves(Game game){
        GameConfiguration conf = game.getConfig();
        for(int i = 0; i < conf.maxPlayer; i++){
            if(game.getPlayer(i).getCardLength() <= GameConfig.LATE_CARDS){
                return;
            }
        }

        TienLenPlayer player = (TienLenPlayer) game.getCurrentPlayer();
        ArrayList<BaseObject> rmList = new ArrayList<>();
        for(BaseObject o : this.unexploredMoves){
            if((o instanceof TripSequence && player.getCardLength() > 7) || (o instanceof QuadSequence && player.getCardLength() > 9) || o instanceof Quads || (o.contains(Card.TWO))){
                rmList.add(o);
            }
        }

        for(BaseObject o : rmList){
            this.unexploredMoves.remove(o);
            for(BaseObject connector : player.getConnectors(o)){
                this.unexploredMoves.remove(connector);
            }
        }

    }

    @Override
    public Node select(Game game) {
        Node selectedNode = this;
        if (game.end() || this.unexploredMoves.size() > 0) return this;

        double maxScore = -100000000;

        for (Node child : children) {
            double uct = child.getUCT();
            if (uct > maxScore) {
                maxScore = uct;
                selectedNode = child;
            }
        }
        if (selectedNode.getMove() != null) {
            game.move(selectedNode.getMove());
        }

        return selectedNode.select(game);
    }

    @Override
    public Node expand(Game game) {
        if (this.unexploredMoves.size() <= 0) return this;
        int rd = DangNguyenDota.random.nextInt(this.unexploredMoves.size());
        BaseObject object = this.unexploredMoves.remove(rd);
        int player = game.getCurrentPlayerIndex();
        game.move(object);
        Node node = new TienLenNode(this, object, player, game);
        this.children.add(node);
        return node;
    }

    @Override
    public Reward simulate(Game game) {
        game.playRandomly();
        return game.getReward();
    }

    @Override
    public void backPropagation(Reward reward) {
        this.reward.addReward(reward);
        this.visit++;
        if (parent != null) {
            parent.backPropagation(reward);
        }
    }

    @Override
    public BaseObject getMostVisitedChildMove() {
        int mostVisit = 0;
        Node mostVisitNode = null;
        for (Node node : this.children) {
            if (node.getVisit() > mostVisit) {
                mostVisit = node.getVisit();
                mostVisitNode = node;
            }
        }

        if (mostVisitNode == null) return null;
        return mostVisitNode.getMove();
    }

    @Override
    public double getUCT() {
        double exploit = 1.0 * reward.getScoreForPlayer(currentPlayIndex) / visit;
        double discover = C * Math.sqrt(Math.log(parent.getVisit()) / visit);
        double balance = K / (K + visit);
        return exploit + discover + balance;
    }

    @Override
    public int getVisit() {
        return visit;
    }

    @Override
    public BaseObject getMove() {
        return move;
    }

    @Override
    public void printTree(String space) {
        System.out.println(space + " visit " + visit + ", reward " + reward + ", move " + move + ", current player " + currentPlayIndex + ", children " + children.size() + ", unexplored  " + unexploredMoves);
        for (Node child : children) {
            child.printTree(space + "    |");
        }
    }

    @Override
    public void printChildren() {
        StringBuilder info = new StringBuilder();
        for(Node node : children){
            TienLenNode tienLenNode = (TienLenNode) node;
            info.append(String.format("%-40s", "Node " + tienLenNode.getMove()));
            info.append("|");
            info.append(String.format("%-20s", "visit " + tienLenNode.visit));
            info.append("|");
            info.append(String.format("%-30s", "reward " + (tienLenNode.reward.getScoreForPlayer(tienLenNode.currentPlayIndex) / tienLenNode.visit)));
            info.append("\n");
        }
        System.out.println(info);
    }

    @Override
    public void setC(double c) {
        this.C = c;
    }

    @Override
    public double getC() {
        return C;
    }

    @Override
    public Reward getReward() {
        return reward;
    }

    @Override
    public void usingK() {
        this.usingK = true;
    }

    @Override
    public void setK(double K) {
        this.K = K;
    }
}
