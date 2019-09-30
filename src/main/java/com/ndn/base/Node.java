package com.ndn.base;

/**
 * @author dangnguyendota
 */
public interface Node {

    Node select(Game game);

    Node expand(Game game);

    Reward simulate(Game game);

    void backPropagation(Reward reward);

    BaseObject getMostVisitedChildMove();

    double getUCT();

    int getVisit();

    BaseObject getMove();

    void printTree(String space);

    void printChildren();

    void setC(double c);

    double getC();

    Reward getReward();

    void usingK();

    void setK(double K);
}
