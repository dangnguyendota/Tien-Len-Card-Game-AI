package com.ndn.base;

/**
 * @author dangnguyendota
 */
public interface Reward {
    void addReward(Reward reward);

    void setScore(int player, double score);

    double getScoreForPlayer(int index);

    Reward getCopy();
}
