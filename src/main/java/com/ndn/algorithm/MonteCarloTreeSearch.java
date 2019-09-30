package com.ndn.algorithm;

import com.ndn.base.*;
import com.ndn.objects.Pass;

import java.util.ArrayList;

class MonteCarloTreeSearch {

    BaseObject selectMove(Game game, MctsPlayerConfiguration configuration){
        int iterations = configuration.iterations;
        /* check must move */
        ArrayList<BaseObject> list = game.getAvailableMoves();
        if(list.size() == 0) return new Pass();
        /* algorithm */
        Node root = new TienLenNode(null, null, -1, game);
        root.setC(configuration.C);
        root.setK(configuration.K);
        if(configuration.usingK) root.usingK();
        long start = System.currentTimeMillis();
        int count = 0;
        while (iterations > 0 && System.currentTimeMillis() - start < configuration.maxTime){
            iterations--;
            /* keep playing while the ratio of winning is less than 50% */
            if(System.currentTimeMillis() - start > configuration.minTime){
                double x = 0, y = 0;
                for(int i = 0; i < game.getMaxPlayer(); i++){
                    if(i == game.getCurrentPlayerIndex()) x = root.getReward().getScoreForPlayer(i);
                    else y = Math.max(y, root.getReward().getScoreForPlayer(i));
                }
                if(x > y) break;
            }

            /* continue loop */
            Game copy = game.getCopy();
            Node node = root.select(copy);
            node = node.expand(copy);
            Reward reward = node.simulate(copy);
            node.backPropagation(reward);
            count++;
        }
        /* debug */
        if(configuration.debug){
            System.out.println("MCTS iterations count: " + count + ", reward: " + root.getReward() + ", visited: " + root.getVisit() + ", thinking time: " + (System.currentTimeMillis() - start));
            root.printChildren();
        }

        return root.getMostVisitedChildMove();
    }
}
