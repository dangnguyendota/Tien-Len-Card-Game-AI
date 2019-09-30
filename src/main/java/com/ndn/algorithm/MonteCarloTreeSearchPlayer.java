package com.ndn.algorithm;

import com.ndn.base.BaseObject;
import com.ndn.base.Game;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MonteCarloTreeSearchPlayer {
    private static MonteCarloTreeSearchPlayer instance;
    private final ExecutorService threadPool;

    public static MonteCarloTreeSearchPlayer getInstance(){
        if(instance == null) {
            instance = new MonteCarloTreeSearchPlayer();
        }
        return instance;
    }

    public MonteCarloTreeSearchPlayer(){
        threadPool = Executors.newFixedThreadPool(20);
    }

    public BaseObject getMove(Game game, MctsPlayerConfiguration configuration) {
        MonteCarloTreeSearch search = new MonteCarloTreeSearch();
        return search.selectMove(game, configuration);
    }

    public void start(final Game game, final MctsPlayerConfiguration configuration, final BotListener listener){
        threadPool.execute(() -> {
            try {
                BaseObject o = MonteCarloTreeSearchPlayer.this.getMove(game, configuration);
                listener.onResult(o);
            } catch (Exception e){
                listener.onResult(null);
            }
        });
    }
}
