package com.ndn.algorithm;

import com.ndn.base.*;
import com.ndn.objects.Dub;
import com.ndn.objects.Pass;

import java.util.ArrayList;

class MonteCarloTreeSearch {

    private boolean containsNoSet(ArrayList<BaseObject> objects) {
        for (BaseObject object : objects) {
            if (object instanceof Card) continue;
            return false;
        }
        return true;
    }

    // nếu bộ còn toàn cóc lẻ và bên kia không chặn được con cóc lẻ nào thì đánh lần lượt từ thấp lên cao
    private BaseObject getReducedMove(Game game) {
        ArrayList<BaseObject> list = game.getAvailableMoves();
        boolean no_set = containsNoSet(list);
        if (!no_set) return null;
        if (game.getCurrentPlayer().listAvailableMoves().size() != list.size()) return null;
        if (list.size() == 1) return list.get(0);

        for (int i = 0; i < game.getMaxPlayer(); i++) {
            if (i == game.getCurrentPlayerIndex() || game.isPassed(i)) continue;
            if (game.getPlayer(i).listAvailableMovesToAgainst(list.get(1)).size() != 0) {
                return null;
            }
        }
        return list.get(1);
    }

    /*
    KK 33
    2. Nếu đôi K không phải là lớn nhất bài dựa trên các lá đã đánh ra thì xử lí tiếp:
        2.1. Nếu bài user kia còn > 4 lá thì cứ đánh đôi bé trước
        2.2. Nếu user kia còn <= 4 lá thì đánh đôi K trước
     */
    private BaseObject getDoubleReducedMove(Game game) {
        ArrayList<BaseObject> list = game.getAvailableMoves();
        if (list.size() != 6) {
            return null;
        }
        int cardCount = 0;
        int dubCount = 0;
        BaseObject[] oo = new BaseObject[2];
        for(BaseObject o : list) {
            if (o instanceof Card) cardCount++;
            if (o instanceof Dub) {
                if (dubCount >= 2) return null;
                oo[dubCount] = o;
                dubCount++;

            }
        }
        if (!(cardCount == 4 && dubCount == 2)) return null;
        for(int i = 0; i < game.getMaxPlayer(); i++) {
            if (i == game.getCurrentPlayerIndex()) continue;
            ArrayList<BaseObject> playerMoves = game.getPlayer(i).listAvailableMoves();
            int c = 0;
            for(BaseObject o : playerMoves) {
                if(o instanceof Card) c++;
                if(o.beats(oo[1])) return null;
            }
            if (c > 4) {
                return oo[0];
            }
        }
        return oo[1];
    }

    BaseObject selectMove(Game game, MctsPlayerConfiguration configuration) {
        int iterations = configuration.iterations;
        /* check must move */
        ArrayList<BaseObject> list = game.getAvailableMoves();
        if (list.size() == 0) return new Pass();
        // heuristic for reduction
        BaseObject object = getReducedMove(game);
        if (object != null) return object;
        object = getDoubleReducedMove(game);
        if (object != null) return object;
        /* algorithm */
        Node root = new TienLenNode(null, null, -1, game);
        root.setC(configuration.C);
        root.setK(configuration.K);
        if (configuration.usingK) root.usingK();
        long start = System.currentTimeMillis();
        int count = 0;
        while (iterations > 0 && System.currentTimeMillis() - start < configuration.maxTime) {
            iterations--;
            /* keep playing while the ratio of winning is less than 50% */
            if (System.currentTimeMillis() - start > configuration.minTime) {
                double x = 0, y = 0;
                for (int i = 0; i < game.getMaxPlayer(); i++) {
                    if (i == game.getCurrentPlayerIndex()) x = root.getReward().getScoreForPlayer(i);
                    else y = Math.max(y, root.getReward().getScoreForPlayer(i));
                }
                if (x > y) break;
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
        if (configuration.debug) {
            System.out.println("MCTS iterations count: " + count + ", reward: " + root.getReward() + ", visited: " + root.getVisit() + ", thinking time: " + (System.currentTimeMillis() - start));
            root.printChildren();
        }

        return root.getMostVisitedChildMove();
    }
}
