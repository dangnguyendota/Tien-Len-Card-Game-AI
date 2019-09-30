package com.ndn.base;

public class CardContainer implements BaseObject {

    @Override
    public Card[] getCards() {
        return new Card[0];
    }

    @Override
    public boolean beats(BaseObject object) {
        return false;
    }

    @Override
    public BaseObject getCopy() {
        return null;
    }

    @Override
    public boolean contains(Card card) {
        Card[] cards = getCards();
        for(Card c : cards){
            if(c.equals(card)) return true;
        }
        return false;
    }

    @Override
    public boolean contains(int value) {
        Card[] cards = getCards();
        for(Card c : cards){
            if(c.getValue() == value) return true;
        }
        return false;
    }

}
