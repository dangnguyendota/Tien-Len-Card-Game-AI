package com.ndn.objects;

import com.ndn.base.BaseObject;
import com.ndn.base.Card;
import com.ndn.base.CardContainer;
import com.ndn.exeption.TLException;

/**
 * Đôi
 * @author dangnguyendota
 */
public class Dub extends CardContainer implements Comparable<Dub>{
    private Card card1;
    private Card card2;
    private int maxType;
    private int minType;
    private int value;

    public Dub(Card card1, Card card2){
        if(!valid(card1, card2)) throw new TLException("Dub exception: cards have two different values");
        this.card1 = card1;
        this.card2 = card2;
        this.maxType = Math.max(card1.getType(), card2.getType());
        this.minType = Math.min(card1.getType(), card2.getType());
        this.value = card1.getValue();
    }

    @Override
    public Card[] getCards() {
        return new Card[]{card1, card2};
    }

    @Override
    public boolean beats(BaseObject object) {
        if(object instanceof Dub){
            Dub dub = (Dub) object;
            if(value > dub.value) return true;
            if(value < dub.value) return false;
            if(value >= Card.SIX) return maxType > dub.maxType;
            return true;
        }
        return false;
    }

    @Override
    public BaseObject getCopy() {
        return new Dub((Card) card1.getCopy(), (Card) card2.getCopy());
    }

    public static boolean valid(Card card1, Card card2){
        return card1.getValue() == card2.getValue();
    }

    int getMaxType() {
        return maxType;
    }

    @Override
    public String toString(){
        return "[" + card1.toString() + " " + card2.toString() + "]";
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Dub){
            Dub dub = (Dub) o;
            return dub.card1.equals(card1) && dub.card2.equals(card2);
        }
        return false;
    }

    int getValue() {
        return value;
    }

    @Override
    public int compareTo(Dub o) {
        if(this.value > o.value) return 1;
        if(this.value < o.value) return -1;
        if(this.maxType > o.maxType) return 1;
        if(this.maxType < o.maxType) return -1;
        return Integer.compare(this.minType, o.minType);
    }
}
