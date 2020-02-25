package com.ndn.algorithm;

import com.ndn.base.*;
import com.ndn.exeption.TLException;
import com.ndn.objects.Pass;
import com.ndn.objects.QuadSequence;
import com.ndn.objects.Quads;
import com.ndn.objects.TripSequence;
import com.ndn.util.DangNguyenDota;
import org.rapidoid.commons.Arr;

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

                if(parent == null) {
                    removeUnintelligibleMoves();
                    checkForBeatingTwo(game);
                    removeTwoIfNotBeatingTurn(game);
                    if (removePassIfBeatingCardTurn(game)) {
                        removePass();
                    }

                }
            }
        }
    }

    private static boolean hasTripSeqOrQuadSeqOrQuad(ArrayList<BaseObject> list){
        for(BaseObject o : list) {
            if(o instanceof TripSequence || o instanceof QuadSequence || o instanceof Quads) return true;
        }
        return false;
    }

    private void removePass(){
        for(int i = 0; i < this.unexploredMoves.size(); i++) {
            if(this.unexploredMoves.get(i) instanceof Pass) {
                this.unexploredMoves.remove(i);
                break;
            }
        }
    }

    // nếu là chặn con lẻ mà còn con lẻ ko có trong bộ nào thì phải đánh lẻ
    private boolean removePassIfBeatingCardTurn(Game game) {
        ArrayList<BaseObject> l = game.getCurrentPlayer().listAvailableMoves();
        if(game.getLastDealt() != null && game.getLastDealt() instanceof Card) {
            Loop: for(BaseObject o : this.unexploredMoves) {
                if(o instanceof Card) {
                    for(BaseObject oo : l) {
                        if (!(oo instanceof Card) && oo.contains((Card) o)) {
                            continue Loop;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private Card getCardWithNearlyLeastValueAvailable(){
        Card card = null;
        int count = 0;
        Loop: for(BaseObject o : this.unexploredMoves) {
            if(o instanceof Card) {
                for(BaseObject _o : this.unexploredMoves) {
                    if(_o.equals(o)) continue;
                    if(_o.contains((Card) o)) continue Loop;
                }
                count++;
                if(count == 2) {
                    card = (Card) o;
                    break;
                }
            }
        }

        return card;
    }

    //loại trường hợp đánh 2 trước con lẻ nếu không phải turn chặn
    private void removeTwoIfNotBeatingTurn(Game game){
        if(game.getLastDealt() != null) return;
        for(int i = 0; i < game.getMaxPlayer(); i++) {
            if(i == game.getCurrentPlayerIndex()) continue;
            // có người còn 1 là thì ko được remove 2
            if(game.getPlayer(i).listAvailableMoves().size() == 1) return;
        }

        Card card = getCardWithNearlyLeastValueAvailable();
        if(card == null || card.getValue() == Card.TWO) return;
        ArrayList<BaseObject> rmList = new ArrayList<>();
        for(BaseObject o : this.unexploredMoves) {
            if(o.contains(Card.TWO)) rmList.add(o);
        }

        for(BaseObject o : rmList) {
            this.unexploredMoves.remove(o);
        }
    }

    // luôn đánh tứ quý, 3 hoặc 4 đôi thông nếu người chơi đánh 2
    private void checkForBeatingTwo(Game game){
        if(game.getLastDealt() == null) return;
        if(!game.getLastDealt().contains(Card.TWO)) return;
//        ArrayList<BaseObject> list = game.getAvailableMoves();
        // nếu bot đánh đôi 2, hoặc tam 2 mà chặn được thì chặn luôn
        if(game.getLastDealt().getCards().length >= 2 && this.unexploredMoves.size() >= 2) {
            removePass();
            return;
        }
        //nếu bot đánh 1 con 2 lẻ
        if(game.getLastDealt().getCards().length == 1) {
            if(!hasTripSeqOrQuadSeqOrQuad(this.unexploredMoves)) return;
            ArrayList<BaseObject> previousPlayerListMoves = game.getPlayer(game.getPreviousPlayerIndex()).listAvailableMoves();
            boolean containsTwo = false;
            for(BaseObject o : previousPlayerListMoves) {
                if(o instanceof Card && ((Card) o).getValue() == Card.TWO) {
                    containsTwo = true;
                    break;
                }
            }

            // nếu người chơi trước không còn 2 thì chặn luôn
            if(!containsTwo) {
                removePass();
                return;
            }

            // nếu người chơi trước có 2 thì 100% chặn nếu con vừa đánh là 2 đỏ và 90% chặn nếu là 2 đen
            Card card = game.getLastDealt().getCards()[0];
            if(card.getType() == Card.HEART || card.getType() == Card.DIAMOND) {
                removePass();
            } else {
                if(DangNguyenDota.random.nextInt(10) > 1) removePass();
            }
        }
    }

    // Nếu đánh được 2 thì không được đánh tứ quý, 3 hoặc 4 đôi thông trước.
    private void removeUnintelligibleMoves() {
        ArrayList<BaseObject> rmList = new ArrayList<>();
        ArrayList<BaseObject> rm2List = new ArrayList<>();
        boolean containsTwo = false;
        for(BaseObject o : this.unexploredMoves) {
            if(!containsTwo && (o instanceof Card)) {
                if(((Card) o).getValue() == Card.TWO) containsTwo = true;
            }

            if(o instanceof Quads || o instanceof QuadSequence || o instanceof TripSequence) {
                rmList.add(o);
            }

            if(o.contains(Card.TWO) && !(o instanceof Card)) {
                rm2List.add(o);
            }
        }

        if (rm2List.size() < this.unexploredMoves.size() - 1) {
            this.unexploredMoves.removeAll(rm2List);
        }

        if(!containsTwo) return;
        for(BaseObject o : rmList) {
            this.unexploredMoves.remove(o);
        }
    }

    // không cho tự  dưngđánh mấy bộ 3, 4 đôi thông và 2 một cách vô lý trước
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
