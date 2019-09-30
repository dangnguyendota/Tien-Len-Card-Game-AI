package com.ndn.service;

import com.ndn.algorithm.GameConfiguration;
import com.ndn.algorithm.MctsPlayerConfiguration;
import com.ndn.algorithm.TienLenGame;
import com.ndn.algorithm.TienLenPlayer;
import com.ndn.base.BaseObject;
import com.ndn.base.Card;
import com.ndn.base.Game;
import com.ndn.base.Player;
import com.ndn.objects.*;

public class ReceiveData {
    /* game */
    private boolean[] passed = new boolean[4];
    private int maxPlayer = 4;
    private int previousPlayer = 0;
    private Card[] lastDealt = null;
    private String lastDealtType = null;
    private int currentPlayer = 0;
    private boolean gang_beat = false;
    private boolean first_turn = false;
    private boolean using_heuristic = true;
    private Card[][] players;
    /* bot */
    private int iterations = 1000000000; /* loop */
    private double C = Math.sqrt(2); /* factor */
    private boolean debug = false;
    private long minTime = 2000;
    private long maxTime = 7000;
    private boolean usingK = true;
    private double K = 500;

    public static ReceiveData from(Game game, MctsPlayerConfiguration conf){
        ReceiveData data = new ReceiveData();
        data.passed = game.getConfig().passed;
        data.maxPlayer = game.getConfig().maxPlayer;
        data.previousPlayer = game.getConfig().previousPlayer;
        if(game.getConfig().lastDealt != null) {
            data.lastDealt = game.getConfig().lastDealt.getCards();
            data.lastDealtType = game.getConfig().lastDealt.getClass().getSimpleName();
        }
        data.currentPlayer = game.getConfig().currentPlayer;
        data.gang_beat = game.getConfig().gang_beat;
        data.first_turn = game.getConfig().first_turn;
        data.using_heuristic = game.getConfig().using_heuristic;
        data.players = new Card[data.maxPlayer][];
        for(int i = 0; i < data.maxPlayer; i++){
            data.players[i] = game.getPlayer(i).getStartCards();
        }
        data.iterations = conf.iterations;
        data.C = conf.C;
        data.debug = conf.debug;
        data.minTime = conf.minTime;
        data.maxTime = conf.maxTime;
        data.usingK = conf.usingK;
        data.K = conf.K;
        return data;
    }

    public Game game() {
        /* load conf */
        GameConfiguration configuration = new GameConfiguration();
        configuration.passed = passed;
        configuration.maxPlayer = maxPlayer;
        configuration.previousPlayer = previousPlayer;
        configuration.currentPlayer = currentPlayer;
        configuration.gang_beat = gang_beat;
        configuration.first_turn = first_turn;
        configuration.using_heuristic = using_heuristic;
        if (lastDealt != null) {
            switch (lastDealtType) {
                case "Card":
                    configuration.lastDealt = lastDealt[0];
                    break;
                case "Dub":
                    configuration.lastDealt = new Dub(lastDealt[0], lastDealt[1]);
                    break;
                case "DubSequence":
                    configuration.lastDealt = new DubSequence(
                            new Dub(lastDealt[0], lastDealt[1]),
                            new Dub(lastDealt[2], lastDealt[3])
                    );
                    break;
                case "Quads":
                    configuration.lastDealt = new Quads(
                            lastDealt[0],
                            lastDealt[1],
                            lastDealt[2],
                            lastDealt[3]
                    );
                    break;
                case "QuadSequence":
                    configuration.lastDealt = new QuadSequence(
                            new Dub(lastDealt[0], lastDealt[1]),
                            new Dub(lastDealt[2], lastDealt[3]),
                            new Dub(lastDealt[4], lastDealt[5]),
                            new Dub(lastDealt[6], lastDealt[7])
                    );
                    break;
                case "Sequence":
                    configuration.lastDealt = new Sequence(lastDealt);
                    break;
                case "Trips":
                    configuration.lastDealt = new Trips(lastDealt[0], lastDealt[1], lastDealt[2]);
                    break;
                case "TripSequence":
                    configuration.lastDealt = new TripSequence(
                            new Dub(lastDealt[0], lastDealt[1]),
                            new Dub(lastDealt[2], lastDealt[3]),
                            new Dub(lastDealt[4], lastDealt[5])
                    );
                    break;
                default:
                    throw new IllegalArgumentException("last dealt type doesn't match");
            }
        }
        /* create game */
        Game game = new TienLenGame(configuration);
        for(Card[] cards : players){
            Player player = new TienLenPlayer();
            player.setCards(cards);
            game.put(player);
        }
        return game;
    }

    public MctsPlayerConfiguration botConf() {
        MctsPlayerConfiguration conf = new MctsPlayerConfiguration();
        conf.maxTime = maxTime;
        conf.minTime = minTime;
        conf.usingK = usingK;
        conf.debug = debug;
        conf.C = C;
        conf.iterations = iterations;
        conf.K = K;
        return conf;
    }
}
