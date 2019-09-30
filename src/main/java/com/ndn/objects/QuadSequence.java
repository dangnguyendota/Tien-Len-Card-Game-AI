package com.ndn.objects;

import com.ndn.base.BaseObject;
import com.ndn.base.Card;
import com.ndn.base.CardContainer;
import com.ndn.exeption.TLException;

/**
 * 4 đôi thông
 * @author dangnguyendota
 */
public class QuadSequence extends CardContainer implements Comparable<QuadSequence>{
    private Dub dub1;
    private Dub dub2;
    private Dub dub3;
    private Dub dub4;
    private int minValue;
    private int maxType;

    public QuadSequence(Dub dub1, Dub dub2, Dub dub3, Dub dub4){
        if(!valid(dub1, dub2, dub3, dub4)) throw new TLException("Quad Sequence exception: dub1 + 1 != dub2 or dub2 + 1 != dub3 or dub3 + 1 != dub4");
        this.dub1 = dub1;
        this.dub2 = dub2;
        this.dub3 = dub3;
        this.dub4 = dub4;
        this.minValue = dub1.getValue();
        this.maxType = dub4.getMaxType();
    }

    public static boolean valid(Dub dub1, Dub dub2, Dub dub3, Dub dub4){
        if(dub4.getValue() == Card.TWO) return false;
        return  dub1.getValue() + 1 == dub2.getValue() &&
                dub2.getValue() + 1 == dub3.getValue() &&
                dub3.getValue() + 1 == dub4.getValue();
    }
    @Override
    public Card[] getCards() {
        return new Card[]{
                dub1.getCards()[0], dub1.getCards()[1],
                dub2.getCards()[0], dub2.getCards()[1],
                dub3.getCards()[0], dub3.getCards()[1],
                dub4.getCards()[0], dub4.getCards()[1]
        };
    }

    @Override
    public boolean beats(BaseObject object) {
        if(object instanceof QuadSequence){
            QuadSequence sequence = (QuadSequence) object;
            if(minValue > sequence.minValue) return true;
            if(minValue < sequence.minValue) return false;
            return this.maxType > sequence.maxType;
        }
        if(object instanceof Quads) return true;
        if(object instanceof TripSequence) return true;
        if(object instanceof Dub) return ((Dub) object).getValue() == Card.TWO;
        if(object instanceof Card) return ((Card) object).getValue() == Card.TWO;
        return false;
    }

    @Override
    public BaseObject getCopy() {
        return new QuadSequence((Dub) dub1.getCopy(), (Dub) dub2.getCopy(), (Dub) dub3.getCopy(), (Dub) dub4.getCopy());
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof QuadSequence){
            QuadSequence sequence = (QuadSequence) o;
            return this.dub1.equals(sequence.dub1) &&
                    this.dub2.equals(sequence.dub2) &&
                    this.dub3.equals(sequence.dub3) &&
                    this.dub4.equals(sequence.dub4);
        }
        return false;
    }

    @Override
    public String toString(){
        return "{" + dub1.toString() + " " + dub2.toString() + " " + dub3.toString() + " " + dub4.toString() + "}";
    }

    @Override
    public int compareTo(QuadSequence o) {
        if(this.minValue > o.minValue) return 1;
        if(this.minValue < o.minValue) return -1;
        int c = dub4.compareTo(o.dub4);
        if(c != 0) return c;
        c = dub3.compareTo(o.dub3);
        if(c != 0) return c;
        c = dub2.compareTo(o.dub2);
        if(c != 0) return c;
        return dub1.compareTo(o.dub1);
    }
}
