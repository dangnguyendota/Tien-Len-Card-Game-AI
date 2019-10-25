package com.ndn.algorithm;

import com.ndn.base.*;
import com.ndn.exeption.TLException;
import com.ndn.objects.*;
import java.util.*;
import static com.ndn.base.GameConfig.*;

public class TienLenPlayer implements Player {
    private int index = -1;
    private boolean bot = false;
    private Card[] cards;
    private ArrayList<BaseObject> list;
    private HashMap<BaseObject, ArrayList<BaseObject>> connectors;
    private int cardLength = 13; // số bài còn lại trong tay
    private double score_lose = 0.0; // tỉ lệ với số tiền mất khi thua.

    public TienLenPlayer() {
        this.list = new ArrayList<>();
        this.connectors = new HashMap<>();
    }

    @Override
    public Card[] getStartCards() {
        return cards;
    }

    @Override
    public ArrayList<BaseObject> listAvailableMoves() {
        return list;
    }

    @Override
    public ArrayList<BaseObject> listAvailableMovesToAgainst(BaseObject object) {
        ArrayList<BaseObject> baseObjects = new ArrayList<>();
        for (BaseObject o : list) {
            if (o.beats(object)) baseObjects.add(o);
        }
        return baseObjects;
    }

    @Override
    public void remove(BaseObject object) {
        ArrayList<BaseObject> connector = connectors.get(object);
        list.remove(object);
        cardLength -= object.getCards().length;
        if (cardLength < 0) {
            throw new TLException("length of card can not less than 0");
        }

        for (BaseObject o : connector) {
            boolean flag = list.remove(o);
            if (flag) calculateScore(o);
        }
        calculateScore(object);
        if (score_lose < - 0.00001) throw new TLException("score lose can not be - " + score_lose);
    }

    private void calculateScore(BaseObject o) {
        /* calculate score lose */
        if (o instanceof Card) {
            if (((Card) o).getValue() == Card.TWO) {
                if (((Card) o).getType() < Card.DIAMOND) score_lose -= BLACK_CARD_OF_TWO;
                else score_lose -= RED_CARD_OF_TWO;
            } else score_lose -= NORMAL_CARD;
        } else if (o instanceof TripSequence) {
            score_lose -= TRIP_SEQUENCE;
        } else if (o instanceof Quads) {
            score_lose -= QUAD;
        } else if (o instanceof QuadSequence) {
            score_lose -= QUAD_SEQUENCE;
        }
    }

    @Override
    public void setCards(Card[] cards) {
        Arrays.sort(cards);
        this.cards = cards;
        this.cardLength = cards.length;
    }

    @Override
    public Player withCards(Card[] cards) {
        this.setCards(cards);
        return this;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void scan() {
        if (cards == null) throw new TLException("Cards of player can not bet null");
        list.addAll(Arrays.asList(cards));
        Finder finder = new Finder();
        list.addAll(Arrays.asList(finder.scan(cards, Dub.class)));
        list.addAll(Arrays.asList(Filter.removeUnnecessarySequences(finder.scan(cards, Sequence.class))));
        list.addAll(Arrays.asList(finder.scan(cards, DubSequence.class)));
        list.addAll(Arrays.asList(finder.scan(cards, Trips.class)));
        list.addAll(Arrays.asList(finder.scan(cards, TripSequence.class)));
        list.addAll(Arrays.asList(finder.scan(cards, Quads.class)));
        list.addAll(Arrays.asList(finder.scan(cards, QuadSequence.class)));
        for (int i = 0; i < list.size(); i++) {
            BaseObject o = list.get(i);
            /* connector */
            ArrayList<BaseObject> connector = new ArrayList<>();
            for (int j = 0; j < list.size(); j++) {
                if (i == j) continue;
                if (Filter.connect(o.getCards(), list.get(j).getCards())) {
                    connector.add(list.get(j));
                }
            }
            connectors.put(o, connector);

            /* calculate score lose */
            if (o instanceof Card) {
                if (((Card) o).getValue() == Card.TWO) {
                    if (((Card) o).getType() < Card.DIAMOND) score_lose += BLACK_CARD_OF_TWO;
                    else score_lose += RED_CARD_OF_TWO;
                } else score_lose += NORMAL_CARD;
            } else if (o instanceof TripSequence) {
                score_lose += TRIP_SEQUENCE;
            } else if (o instanceof Quads) {
                score_lose += QUAD;
            } else if (o instanceof QuadSequence) {
                score_lose += QUAD_SEQUENCE;
            }
        }
        list.sort(Comparator.comparingInt(o -> o.getCards().length));
    }


    @Override
    public Player getCopy() {
        TienLenPlayer player = new TienLenPlayer();
        player.index = this.index;
        player.list.addAll(this.list);
        player.connectors = connectors;
        player.bot = this.bot;
        player.cardLength = cardLength;
        player.score_lose = score_lose;
        return player;
    }

    @Override
    public boolean isBot() {
        return bot;
    }

    @Override
    public void setBot(boolean bot) {
        this.bot = bot;
    }

    @Override
    public int getCardLength() {
        return cardLength;
    }

    @Override
    public int getHeuristicScore() {
        return 0;
    }

    @Override
    public double getLosingScore() {
        return score_lose;
    }

    @Override
    public String toString() {
        return "Player info:\n" +
                "        Cards: " + Arrays.toString(this.cards) + "\n" +
                "   Connectors: " + connectors + "\n" +
                "         List: " + list;
    }


    public int getLongestObject() {
        return list.get(list.size() - 1).getCards().length;
    }

    public ArrayList<BaseObject> getConnectors(BaseObject key) {
        return connectors.get(key);
    }
}
