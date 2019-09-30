package com.ndn.base;

import java.util.ArrayList;

/**
 * @author dangnguyendota
 */
public interface Player {
    Card[] getStartCards();

    ArrayList<BaseObject> listAvailableMoves();

    ArrayList<BaseObject> listAvailableMovesToAgainst(BaseObject object);

    void remove(BaseObject object);

    void setCards(Card[] cards);

    Player withCards(Card[] cards);

    int getIndex();

    void setIndex(int index);

    void scan();

    Player getCopy();

    boolean isBot();

    void setBot(boolean bot);

    int getCardLength();

    int getHeuristicScore();

    double getLosingScore();
}
