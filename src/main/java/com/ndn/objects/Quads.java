package com.ndn.objects;

import com.ndn.base.BaseObject;
import com.ndn.base.Card;
import com.ndn.base.CardContainer;
import com.ndn.exeption.TLException;

/**
 * Tứ quý
 *
 * @author dangnguyendota
 */
public class Quads extends CardContainer implements Comparable<Quads> {
    private Card card1;
    private Card card2;
    private Card card3;
    private Card card4;
    private int value;

    public Quads(Card card1, Card card2, Card card3, Card card4) {
        if (!valid(card1, card2, card3, card4)) throw new TLException("Quads exception: cards have different values");
        this.card1 = card1;
        this.card2 = card2;
        this.card3 = card3;
        this.card4 = card4;
        this.value = card1.getValue();
    }

    private static boolean valid(Card card1, Card card2, Card card3, Card card4) {
        return card1.getValue() == card2.getValue() && card2.getValue() == card3.getValue() && card3.getValue() == card4.getValue();
    }

    @Override
    public Card[] getCards() {
        return new Card[]{card1, card2, card3, card4};
    }

    @Override
    public boolean beats(BaseObject object) {
        if (object instanceof Quads) return value > ((Quads) object).value;
        if (object instanceof TripSequence) return true;
        if (object instanceof Dub) return ((Dub) object).getValue() == Card.TWO;
        if (object instanceof Card) return ((Card) object).getValue() == Card.TWO;
        return false;
    }

    @Override
    public BaseObject getCopy() {
        return new Quads((Card) card1.getCopy(), (Card) card2.getCopy(), (Card) card3.getCopy(), (Card) card4.getCopy());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Quads) {
            Quads quads = (Quads) o;
            return this.value == quads.value;
        }
        return false;
    }

    @Override
    public String toString() {
        return "[" + card1.toString() + " " + card2.toString() + " " + card3.toString() + " " + card4.toString() + "]";
    }

    @Override
    public int compareTo(Quads o) {
        if (this.value == o.value) return 0;
        return this.value > o.value ? 1 : -1;
    }
}
