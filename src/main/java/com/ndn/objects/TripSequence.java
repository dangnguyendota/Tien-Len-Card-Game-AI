package com.ndn.objects;


import com.ndn.base.BaseObject;
import com.ndn.base.Card;
import com.ndn.base.CardContainer;
import com.ndn.exeption.TLException;

/**
 * Bộ 3 đôi thông
 * @author dangnguyendota
 */
public class TripSequence extends CardContainer implements Comparable<TripSequence> {
    private Dub dub1;
    private Dub dub2;
    private Dub dub3;
    private int maxType;
    private int minValue;

    public TripSequence(Dub dub1, Dub dub2, Dub dub3){
        if(!valid(dub1, dub2, dub3)) throw new TLException("Trip Sequence exception: dub1 + 1 != dub2 or dub2 +1 != dub3");
        this.dub1 = dub1;
        this.dub2 = dub2;
        this.dub3 = dub3;
        this.minValue = dub1.getValue();
        this.maxType = dub3.getMaxType();
    }

    public static boolean valid(Dub dub1, Dub dub2, Dub dub3){
        if(dub3.getValue() == Card.TWO) return false;
        return (dub1.getValue() + 1 == dub2.getValue()) && (dub2.getValue() + 1 == dub3.getValue());
    }

    @Override
    public Card[] getCards() {
        return new Card[]{
                dub1.getCards()[0], dub1.getCards()[1],
                dub2.getCards()[0], dub2.getCards()[1],
                dub3.getCards()[0], dub3.getCards()[1]
        };
    }

    @Override
    public boolean beats(BaseObject object) {
        if(object instanceof TripSequence){
            TripSequence sequence = (TripSequence) object;
            if(minValue > sequence.minValue) return true;
            if(minValue < sequence.minValue) return false;
            return this.maxType > sequence.maxType;
        }
        if(object instanceof Card) return ((Card) object).getValue() == Card.TWO;
        return false;
    }

    @Override
    public BaseObject getCopy() {
        return new TripSequence((Dub) dub1.getCopy(), (Dub) dub2.getCopy(), (Dub) dub3.getCopy());
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof TripSequence){
            TripSequence sequence = (TripSequence) o;
            return dub1.equals(sequence.dub1) && dub2.equals(sequence.dub2) && dub3.equals(sequence.dub3);
        }
        return false;
    }

    @Override
    public String toString(){
        return "{" + dub1.toString() + " " + dub2.toString() + " " + dub3.toString() + "}";
    }

    @Override
    public int compareTo(TripSequence o) {
        if(this.minValue > o.minValue) return 1;
        if(this.minValue < o.minValue) return -1;
        int c = dub3.compareTo(o.dub3);
        if(c != 0) return c;
        c = dub2.compareTo(o.dub2);
        if(c != 0) return c;
        return dub1.compareTo(o.dub1);
    }
}
