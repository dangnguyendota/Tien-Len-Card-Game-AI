package com.ndn.algorithm;

import com.ndn.base.*;
import com.ndn.exeption.TLException;
import com.ndn.objects.Pass;
import com.ndn.util.DangNguyenDota;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class TienLenGame implements Game {
    private Player[] players;
    private Reward reward;
    private int maxPlayer;
    private int size;
    private int currentPlayer = 0;
    private boolean scanned;
    private int previousPlayer = 0;
    private BaseObject lastDealt;
    private boolean[] passed;
    private GameConfiguration configuration;
    private boolean first = false;
    private boolean end = false;
    private int ply;

    public TienLenGame(GameConfiguration configuration) {
        this.maxPlayer = configuration.maxPlayer;
        this.passed = new boolean[configuration.maxPlayer];
        System.arraycopy(configuration.passed, 0, passed, 0, configuration.passed.length);
        this.lastDealt = configuration.lastDealt;
        this.previousPlayer = configuration.previousPlayer;
        this.currentPlayer = configuration.currentPlayer;
        this.setConfig(configuration);
        this.size = 0;
        this.players = new Player[maxPlayer];
        this.scanned = false;
        this.first = configuration.first_turn;
        this.ply = 0;
    }

    @Override
    public void move(BaseObject object) {
        this.ply++;
        if(first) first = false;
        if (object instanceof Pass) {
            if (currentPlayer == previousPlayer) throw new TLException("Current player can not pass");
            passed[currentPlayer] = true;
            this.next();
            return;
        }
        this.lastDealt = object;
        Player player = this.players[currentPlayer];
        player.remove(object);
        this.previousPlayer = currentPlayer;
        this.next();
    }

    @Override
    public Player getCurrentPlayer() {
        return players[currentPlayer];
    }

    @Override
    public int getCurrentPlayerIndex() {
        return currentPlayer;
    }

    @Override
    public int getPreviousPlayerIndex() {
        return previousPlayer;
    }

    @Override
    public int getWinner() {
        for (int i = 0; i < maxPlayer; i++) {
            if (players[i].listAvailableMoves().size() == 0) return i;
        }
        return 0;
    }

    @Override
    public int getMaxPlayer() {
        return maxPlayer;
    }

    @Override
    public Game getCopy() {
        TienLenGame game = new TienLenGame(configuration);
        for (int i = 0; i < maxPlayer; i++) {
            game.players[i] = this.players[i].getCopy();
        }
        game.scanned = scanned;
        game.first = configuration.first_turn;
        game.size = size;
        game.currentPlayer = currentPlayer;
        game.previousPlayer = previousPlayer;
        game.maxPlayer = maxPlayer;
        game.passed = new boolean[maxPlayer];
        System.arraycopy(passed, 0, game.passed, 0, passed.length);
        game.lastDealt = lastDealt;
        if (reward != null) game.reward = reward.getCopy();
        return game;
    }

    @Override
    public ArrayList<BaseObject> getAvailableMoves() {
        Player player = this.players[currentPlayer];
        if (previousPlayer == currentPlayer) {
            ArrayList<BaseObject> list = player.listAvailableMoves();
            if(first) {
                Card card = new Card(Card.THREE, Card.SPADE);
                ArrayList<BaseObject> objects = new ArrayList<>();
                for(BaseObject object : list){
                    if(object.contains(card)) objects.add(object);
                }
                return objects;
            }
            return list;
        } else {
            return player.listAvailableMovesToAgainst(lastDealt);
        }
    }

    @Override
    public boolean end() {
        return end;
    }

    @Override
    public void playRandomly() {
        if (!scanned) throw new TLException("The game can not be played without scan");
        /* keep playing randomly while the game is not over */
        while (true) {
            if(end()) break;
            ArrayList<BaseObject> list = this.getAvailableMoves();
            if(list.size() > 0 && list.get(list.size() - 1).getCards().length == this.getCurrentPlayer().getCardLength()){
                this.move(list.get(list.size() - 1));
                break;
            }

            if (list.size() <= 0 || currentPlayer != previousPlayer) list.add(new Pass());
            BaseObject object = list.get(DangNguyenDota.random.nextInt(list.size()));
            this.move(object);
        }

        /* update reward */
        this.reward = new GameReward(maxPlayer);
        if(configuration.gang_beat){
            int winner = getWinner();
            if(players[winner].isBot()){
                for (int i = 0; i < maxPlayer; i++) {
                    if(players[i].isBot()) this.reward.setScore(i, 1);
                    else this.reward.setScore(i, 0);
                }
            } else {
                for (int i = 0; i < maxPlayer; i++) {
                    this.reward.setScore(i, players[i].listAvailableMoves().size() <= 0 ? 1 : 0);
                }
            }
        } else {
            if(configuration.using_heuristic) {
                int winner = getWinner();
                double total = - this.ply * GameConfig.PLY_FACTOR;
                for (Player player : players) total += player.getLosingScore();
                for (int i = 0; i < maxPlayer; i++) {
                    if (i == winner) this.reward.setScore(i, 1 + total);
                    else this.reward.setScore(i, - getPlayer(i).getLosingScore());
                }
            } else {
                for(int i = 0; i < maxPlayer; i++){
                    this.reward.setScore(i, players[i].listAvailableMoves().size() <= 0 ? 1 : 0);
                }
            }
        }
    }

    @Override
    public Reward getReward() {
        return reward;
    }

    @Override
    public void put(Player player) {
        if (scanned) throw new TLException("Can not put player because the game is started");
        for (int i = 0; i < maxPlayer; i++) {
            if (players[i] == null) {
                player.setIndex(i);
                player.scan();
                players[i] = player;
                size++;
                break;
            }
        }
        if(size == maxPlayer) scan();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void scan() {
        scanned = true;
        for (Player player : players) {
            if (player == null) throw new TLException("Game exception: number of players < max players");
        }
    }

    @Override
    public void next() {
        end = players[currentPlayer].listAvailableMoves().size() == 0;
        this.increaseIndex();
        while (passed[currentPlayer]) increaseIndex();
        if (currentPlayer == previousPlayer) Arrays.fill(passed, false);
    }

    @Override
    public GameConfiguration getConfig() {
        return configuration;
    }

    @Override
    public void setConfig(GameConfiguration config) {
        this.configuration = config;
    }

    @Override
    public Player getPlayer(int index) {
        return players[index];
    }

    @Override
    public boolean isPassed(int index) {
        return passed[index];
    }

    @Override
    public BaseObject getLastDealt(){
        return lastDealt;
    }

    @Override
    public String toString() {
        StringBuilder info = new StringBuilder("Game Info:\n");
        info.append("last dealt: ").append(this.lastDealt).append("\n");
        info.append("previous: ").append(this.previousPlayer).append("\n");
        info.append("current: ").append(this.currentPlayer).append("\n");
        info.append("passed: ").append(Arrays.toString(passed)).append("\n");

        for (int i = 0; i < maxPlayer; i++) {
            info.append(String.valueOf(i + 1)).append(". ");
            info.append(players[i].toString());
            info.append("\n");
        }
        return info.toString();
    }

    private void increaseIndex() {
        if (currentPlayer == maxPlayer - 1) currentPlayer = 0;
        else currentPlayer++;
    }
}
