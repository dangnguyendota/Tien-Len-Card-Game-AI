package com.ndn.base;

import com.ndn.exeption.TLException;
import com.ndn.objects.Dub;
import com.ndn.util.DangNguyenDota;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author dangnguyendota
 */
public class Card extends CardContainer implements Comparable<Card> {
    /* type */
    public final static int SPADE = 0; // bích
    public final static int CLUB = 1; // tép
    public final static int DIAMOND = 2; // rô
    public final static int HEART = 3; // cơ
    /* value */
    public final static int THREE = 0;
    public final static int FOUR = 1;
    public final static int FIVE = 2;
    public final static int SIX = 3;
    public final static int SEVEN = 4;
    public final static int EIGHT = 5;
    public final static int NINE = 6;
    public final static int TEN = 7;
    public final static int JACK = 8;
    public final static int QUEEN = 9;
    public final static int KING = 10;
    public final static int ACE = 11;
    public final static int TWO = 12;

    public static Card[] deckOfCards() {
        Card[] cards = new Card[52];
        int i = 0;
        for (int type = SPADE; type <= HEART; type++) {
            for (int value = THREE; value <= TWO; value++) {
                cards[i++] = new Card(value, type);
            }
        }
        return cards;
    }

    public static Card[] randomCards(Card[] container, int length) {
        if (container.length <= length) return container;
        ArrayList<Card> cards = new ArrayList<>(Arrays.asList(container));
        int i = 0;
        Card[] result = new Card[length];
        while (i < length) {
            int r = DangNguyenDota.random.nextInt(cards.size());
            result[i] = cards.get(r);
            cards.remove(r);
            i++;
        }
        return result;
    }

    public static Card[] removeFrom(Card[] container, Card[] removed) {
        ArrayList<Card> cards = new ArrayList<>(Arrays.asList(container));
        for (Card card : removed) {
            cards.remove(card);
        }
        return cards.toArray(new Card[0]);
    }


    private int value;
    private int type;

    public Card(int value, int type) {
        this.value = value;
        this.type = type;
    }

    public Card(String code) {
        if (code.length() != 2 && code.length() != 3) throw new TLException("cards code is not valid");
        switch (code.charAt(code.length() - 1)) {
            case '♥':
                this.type = HEART;
                break;
            case '♦':
                this.type = DIAMOND;
                break;
            case '♣':
                this.type = CLUB;
                break;
            case '♠':
                this.type = SPADE;
                break;
            default:
                throw new TLException("type is not valid");
        }

        if (code.length() == 3) {
            this.value = TEN;
            return;
        }

        switch (code.charAt(0)) {
            case 'A':
                this.value = ACE;
                break;
            case '2':
                this.value = TWO;
                break;
            case '3':
                this.value = THREE;
                break;
            case '4':
                this.value = FOUR;
                break;
            case '5':
                this.value = FIVE;
                break;
            case '6':
                this.value = SIX;
                break;
            case '7':
                this.value = SEVEN;
                break;
            case '8':
                this.value = EIGHT;
                break;
            case '9':
                this.value = NINE;
                break;
            case 'J':
                this.value = JACK;
                break;
            case 'Q':
                this.value = QUEEN;
                break;
            case 'K':
                this.value = KING;
                break;
        }
    }

    public int getValue() {
        return value;
    }

    public int getType() {
        return type;
    }

    public boolean compare(Card card) {
        return card.type == type && card.value == value;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Card) {
            Card card = (Card) o;
            return card.value == value && card.type == type;
        }
        return false;
    }

    @Override
    public Card[] getCards() {
        return new Card[]{this};
    }

    @Override
    public boolean beats(BaseObject object) {
        if (object instanceof Card) {
            Card card = (Card) object;
            if (card.value > value) return false;
            if (card.value < value) return true;
            if (card.value >= SIX) return type > card.type;
            return true;
        }
        return false;
    }

    @Override
    public BaseObject getCopy() {
        return new Card(value, type);
    }

    @Override
    public String toString() {
        String val = "";
        String type = "";
        switch (this.value) {
            case ACE:
                val = "A";
                break;
            case TWO:
                val = "2";
                break;
            case THREE:
                val = "3";
                break;
            case FOUR:
                val = "4";
                break;
            case FIVE:
                val = "5";
                break;
            case SIX:
                val = "6";
                break;
            case SEVEN:
                val = "7";
                break;
            case EIGHT:
                val = "8";
                break;
            case NINE:
                val = "9";
                break;
            case TEN:
                val = "10";
                break;
            case JACK:
                val = "J";
                break;
            case QUEEN:
                val = "Q";
                break;
            case KING:
                val = "K";
                break;
        }
        switch (this.type) {
            case SPADE:
                type = "♠";
                break;
            case CLUB:
                type = "♣";
                break;
            case DIAMOND:
                type = "♦";
                break;
            case HEART:
                type = "♥";
                break;
        }
        return val + type;
    }

    @Override
    public int compareTo(Card o) {
        if (this.value == o.value) {
            if (this.type == o.type) return 0;
            if (this.type > o.type) return 1;
            return -1;
        }
        if (this.value > o.value) return 1;
        return -1;
    }
}
