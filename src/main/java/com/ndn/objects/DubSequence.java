package com.ndn.objects;

import com.ndn.base.BaseObject;
import com.ndn.base.Card;
import com.ndn.base.CardContainer;
import com.ndn.exeption.TLException;

/**
 * 2 đôi thông
 * @author dangnguyendota
 */
public class DubSequence extends CardContainer implements Comparable<DubSequence>{
    private Dub dub1;
    private Dub dub2;
    private int minValue;
    private int maxType;

    public DubSequence(Dub dub1, Dub dub2){
        if(!valid(dub1, dub2)) throw new TLException("Dub Sequence exception: dub1 + 1 != dub2");
        this.dub1 = dub1;
        this.dub2 = dub2;
        this.minValue = dub1.getValue();
        this.maxType = dub2.getMaxType();
    }

    public static boolean valid(Dub dub1, Dub dub2){
        if(dub2.getValue() == Card.TWO) return false;
        return dub1.getValue() + 1 == dub2.getValue();
    }
    @Override
    public Card[] getCards() {
        return new Card[]{
                dub1.getCards()[0], dub1.getCards()[1],
                dub2.getCards()[0], dub2.getCards()[1]
        };
    }

    @Override
    public boolean beats(BaseObject object) {
        if(object instanceof DubSequence){
            DubSequence sequence = (DubSequence) object;
            if(minValue > sequence.minValue) return true;
            if(minValue < sequence.minValue) return false;
            return this.maxType > sequence.maxType;
        }
        return false;
    }

    @Override
    public BaseObject getCopy() {
        return new DubSequence((Dub) dub1.getCopy(), (Dub) dub2.getCopy());
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof DubSequence){
            DubSequence sequence = (DubSequence) o;
            return this.dub1.equals(sequence.dub1) && this.dub2.equals(sequence.dub2);
        }
        return false;
    }

    @Override
    public String toString(){
        return "{" + dub1.toString() +" " + dub2.toString() + "}";
    }

    @Override
    public int compareTo(DubSequence o) {
        if(this.minValue > o.minValue) return 1;
        if(this.minValue < o.minValue) return -1;
        if(dub2.equals(o.dub2)) return dub1.compareTo(o.dub1);
        return dub2.compareTo(o.dub2);
    }
}
