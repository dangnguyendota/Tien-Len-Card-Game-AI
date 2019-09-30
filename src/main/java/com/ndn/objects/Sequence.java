package com.ndn.objects;

import com.ndn.base.BaseObject;
import com.ndn.base.Card;
import com.ndn.base.CardContainer;
import com.ndn.exeption.TLException;

/**
 * Bộ dây
 * @author dangnguyendota
 */
public class Sequence extends CardContainer implements Comparable<Sequence>{
    private Card[] cards;
    private int minValue;
    private int type;
    private int maxValue;
    private boolean homogeneity;

    public Sequence(Card[] cards){
        if(!valid(cards)) throw new TLException("Sequence exception: cards are not valid");
        this.cards = cards;
        this.minValue = cards[0].getValue();
        this.maxValue = cards[cards.length - 1].getValue();
        this.type = cards[cards.length - 1].getType();
        this.homogeneity = this.isHomogeneity();
    }

    public static boolean valid(Card[] cards){
        if(cards.length <= 2) return false;
        if(cards[cards.length - 1].getValue() == Card.TWO) return false;
        for(int i = 0; i < cards.length - 1; i++){
            if(cards[i].getValue() + 1 != cards[i + 1].getValue()) return false;
        }
        return true;
    }

    private boolean isHomogeneity(){
        for(Card card : cards){
            if(card.getType() != type) return false;
        }
        return true;
    }

    @Override
    public Card[] getCards() {
        return cards;
    }

    @Override
    public boolean beats(BaseObject object) {
        if(object instanceof Sequence) {
            Sequence sequence = (Sequence) object;
            if(cards.length != sequence.cards.length) return false;
            if (!homogeneity && sequence.homogeneity) return false;
            if(this.minValue > sequence.minValue) return true;
            if(this.minValue < sequence.minValue) return false;
            if(this.maxValue >= Card.SIX) return this.type > sequence.type;
            return true;
        }
        return false;
    }

    @Override
    public BaseObject getCopy() {
        Card[] cards = new Card[this.cards.length];
        for(int i = 0; i < cards.length; i++) cards[i] = (Card) this.cards[i].getCopy();
        return new Sequence(cards);
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Sequence){
            Sequence sequence = (Sequence) o;
            if(sequence.cards.length != cards.length) return false;
            for(int i = 0; i < cards.length; i++){
                if(!cards[i].equals(sequence.cards[i])) return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder("{");
        for (Card card : cards) {
            str.append(card.toString());
            str.append(" ");
        }
        str.append("}");
        return str.toString();
    }

    @Override
    public int compareTo(Sequence o) {
        if(this.cards.length > o.cards.length) return 1;
        if(this.cards.length < o.cards.length) return -1;
        if(this.minValue > o.minValue) return 1;
        if(this.minValue < o.minValue) return -1;
        for(int i = cards.length - 1; i >= 0; i--){
            if(cards[i].getType() > o.cards[i].getType()) return 1;
            if(cards[i].getType() < o.cards[i].getType()) return -1;
        }
        return 0;
    }

    public int getMinValue() {
        return minValue;
    }
}
