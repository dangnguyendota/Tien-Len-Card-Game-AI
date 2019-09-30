import com.ndn.algorithm.Finder;
import com.ndn.base.Card;
import com.ndn.objects.*;

import java.util.ArrayList;
import java.util.Arrays;

public class Logic {
    private static Card[] from(String string) {
        string = string.replace(" ", "");
        String[] split = string.split(",");
        Card[] cards = new Card[split.length];
        for (int i = 0; i < split.length; i++) {
            cards[i] = new Card(split[i]);
        }
        return cards;
    }

    public static void main(String[] args) {
        Card[] cards = Card.randomCards(Card.deckOfCards(), 13);
        Arrays.sort(cards);
        Finder finder = new Finder();
        System.out.println("[Bài]            " + Arrays.toString(cards));
        System.out.println("[Đôi]            " + Arrays.toString(finder.scan(cards, Dub.class)));
        System.out.println("[Tam]            " + Arrays.toString(finder.scan(cards, Trips.class)));
        System.out.println("[Tứ]             " + Arrays.toString(finder.scan(cards, Quads.class)));
        System.out.println("[Hai đôi thông]  " + Arrays.toString(finder.scan(cards, DubSequence.class)));
        System.out.println("[Ba đôi thông]   " + Arrays.toString(finder.scan(cards, TripSequence.class)));
        System.out.println("[Bốn đôi thông]  " + Arrays.toString(finder.scan(cards, QuadSequence.class)));
        System.out.println("[Dây]            " + Arrays.toString(finder.scan(cards, Sequence.class)));
    }
}
