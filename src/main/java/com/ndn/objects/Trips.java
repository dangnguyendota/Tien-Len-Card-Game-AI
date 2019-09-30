package com.ndn.objects;

import com.ndn.base.BaseObject;
import com.ndn.base.Card;
import com.ndn.base.CardContainer;
import com.ndn.exeption.TLException;

/**
 * Bộ tam
 * @author dangnguyendota
 */
public class Trips extends CardContainer implements Comparable<Trips> {
    private Card card1;
    private Card card2;
    private Card card3;
    private int value;

    public Trips(Card card1, Card card2, Card card3){
        if(!valid(card1, card2, card3)) throw new TLException("Trips exception: cards have different values");
        this.card1 = card1;
        this.card2 = card2;
        this.card3 = card3;
        this.value = card1.getValue();
    }

    private static boolean valid(Card card1, Card card2, Card card3){
        return card1.getValue() == card2.getValue() && card2.getValue() == card3.getValue();
    }

    @Override
    public Card[] getCards() {
        return new Card[]{card1, card2, card3};
    }

    @Override
    public boolean beats(BaseObject object) {
        if(object instanceof Trips){
            Trips trips = (Trips) object;
            return value > trips.value;
        }
        return false;
    }

    @Override
    public BaseObject getCopy() {
        return new Trips((Card) card1.getCopy(), (Card) card2.getCopy(), (Card) card3.getCopy());
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Trips){
            Trips trips = (Trips) o;
            return this.card1.equals(trips.card1) && this.card2.equals(trips.card2) && this.card3.equals(trips.card3);
        }
        return false;
    }

    @Override
    public String toString(){
        return "{" + card1.toString() + " " + card2.toString() + " " + card3.toString() + "}";
    }

    @Override
    public int compareTo(Trips o) {
        if(this.value > o.value) return 1;
        if(this.value < o.value) return -1;
        int c = card3.compareTo(o.card3);
        if(c != 0) return c;
        c = card2.compareTo(o.card2);
        if(c != 0) return c;
        return card1.compareTo(o.card1);
    }

    public int getValue() {
        return value;
    }
}
