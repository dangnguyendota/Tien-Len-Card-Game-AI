package com.ndn.algorithm;

import com.ndn.base.Reward;

import java.util.ArrayList;
import java.util.HashMap;

public class GameReward implements Reward {
    private ArrayList<Double> score;
    private int max;

    GameReward(int max){
        this.max = max;
        this.score = new ArrayList<>();
        for(int i = 0; i < max; i++){
            score.add(0.0);
        }
    }

    @Override
    public void addReward(Reward reward) {
        for(int i = 0; i < max; i++){
            score.set(i, score.get(i) + reward.getScoreForPlayer(i));
        }
    }

    @Override
    public void setScore(int player, double score) {
        this.score.set(player, score);
    }

    @Override
    public double getScoreForPlayer(int index) {
        return score.get(index);
    }

    @Override
    public Reward getCopy() {
        GameReward reward = new GameReward(max);
        for(int i = 0; i < score.size(); i++){
            reward.score.set(i, score.get(i));
        }
        return reward;
    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder("(");
        for(int i = 0; i < max; i++){
            str.append(score.get(i));
            if(i != max - 1) str.append(", ");
        }
        str.append(")");
        return str.toString();
    }
}
