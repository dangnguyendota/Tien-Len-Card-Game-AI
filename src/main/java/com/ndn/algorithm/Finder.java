package com.ndn.algorithm;

import com.ndn.base.BaseObject;
import com.ndn.base.Card;
import com.ndn.objects.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Finder {
    public BaseObject[] scan(Card[] cards, Class clazz){
        if(clazz.getSimpleName().equals(Dub.class.getSimpleName())){
            return dubScan(cards);
        } else if(clazz.getSimpleName().equals(Trips.class.getSimpleName())){
            return tripsScan(cards);
        } else if(clazz.getSimpleName().equals(Quads.class.getSimpleName())){
            return quadsScan(cards);
        } else if(clazz.getSimpleName().equals(DubSequence.class.getSimpleName())){
            return dubSequenceScan(cards);
        } else if(clazz.getSimpleName().equals(TripSequence.class.getSimpleName())){
            return tripSequenceScan(cards);
        } else if(clazz.getSimpleName().equals(QuadSequence.class.getSimpleName())){
            return quadSequenceScan(cards);
        } else if(clazz.getSimpleName().equals(Sequence.class.getSimpleName())){
            return sequenceScan(cards);
        }
        return null;
    }

    /* tìm đôi */
    private Dub[] dubScan(Card[] cards){
        ArrayList<Dub> dubs = new ArrayList<>();
        for(int i = 0; i < cards.length; i++) {
            for(int j = i + 1; j < cards.length; j++){
                if(Dub.valid(cards[i], cards[j])){
                    dubs.add(new Dub(cards[i], cards[j]));
                }
            }
        }
        dubs.sort(Dub::compareTo);
        return dubs.toArray(new Dub[0]);
    }

    /* tìm bộ tam */
    private Trips[] tripsScan(Card[] cards){
        ArrayList<Trips> trips = new ArrayList<>();
        int[] count = new int[13];
        for(Card card : cards){
            count[card.getValue()] += 1;
        }
        for(int value = Card.THREE; value <= Card.TWO; value++){
            if(count[value] < 3) continue;
            if(count[value] == 3){
                ArrayList<Card> list = this.getAllCardsWithValue(cards, value);
                trips.add(new Trips(list.get(0), list.get(1), list.get(2)));
            } else if(count[value] == 4){
                Card card1 = new Card(value, Card.SPADE);
                Card card2 = new Card(value, Card.CLUB);
                Card card3 = new Card(value, Card.DIAMOND);
                Card card4 = new Card(value, Card.HEART);
                trips.add(new Trips(card1, card2, card3));
                trips.add(new Trips(card1, card2, card4));
                trips.add(new Trips(card1, card3, card4));
                trips.add(new Trips(card2, card3, card4));
            }
        }
        trips.sort(Trips::compareTo);
        return trips.toArray(new Trips[0]);
    }

    /* tìm tứ quí */
    private Quads[] quadsScan(Card[] cards){
        ArrayList<Quads> quads = new ArrayList<>();
        int[] count = new int[13];
        for(Card card : cards){
            count[card.getValue()] += 1;
        }

        for(int value = Card.THREE; value <= Card.TWO; value++){
            if(count[value] == 4){
                quads.add(new Quads(
                        new Card(value, Card.SPADE),
                        new Card(value, Card.CLUB),
                        new Card(value, Card.DIAMOND),
                        new Card(value, Card.HEART))
                );
            }
        }
        quads.sort(Quads::compareTo);
        return quads.toArray(new Quads[0]);
    }

    /* tìm bộ dây */
    private Sequence[] sequenceScan(Card[] cards){
        ArrayList<Sequence> sequences = new ArrayList<>();
        ArrayList<ArrayList<Card>> count = new ArrayList<>();
        for(int i = 0; i < 13; i++) count.add(new ArrayList<>());
        for(Card card : cards){
            count.get(card.getValue()).add(card);
        }
        int index = 0;
        int start = 0;
        int stop = 0;
        while (index < count.size()){
            if(count.get(index).size() == 0 || index == count.size() - 1){
                stop = index + 1;
                if(stop - start >= 3){
                    ArrayList<Integer[]> iFound = new ArrayList<>();
                    for(int i = start; i < stop; i++){
                        find(i, stop, new Integer[]{i}, iFound);
                    }
                    ArrayList<Card[]> foundCards = new ArrayList<>();
                    for (Integer[] anIFound : iFound) {
                        this.findSequence(new Card[0], 0, anIFound, count, foundCards);
                    }

                    for(Card[] c : foundCards){
                        if(c.length < 3) continue;
                        if(!Sequence.valid(c)) continue;
                        sequences.add(new Sequence(c));
                    }
                }
                start = index + 1;
            }
            index++;
        }
        sequences.sort(Sequence::compareTo);
        return sequences.toArray(new Sequence[0]);
    }

    /* tìm hai đôi thông */
    private DubSequence[] dubSequenceScan(Card[] cards){
        ArrayList<DubSequence> dubSequences = new ArrayList<>();
        Dub[] dubs = dubScan(cards);
        for(int i = 0; i < dubs.length; i++){
            for(int j = i + 1; j < dubs.length; j++){
                if(DubSequence.valid(dubs[i], dubs[j])){
                    dubSequences.add(new DubSequence(dubs[i], dubs[j]));
                }
            }
        }
        dubSequences.sort(DubSequence::compareTo);
        return dubSequences.toArray(new DubSequence[0]);
    }

    /* tìm ba đôi thông */
    private TripSequence[] tripSequenceScan(Card[] cards){
        ArrayList<TripSequence> tripSequences = new ArrayList<>();
        Dub[] dubs = dubScan(cards);
        if(dubs.length < 3) return new TripSequence[0];
        for(int i = 0; i < dubs.length; i++){
            for(int j = i + 1; j < dubs.length; j++){
                for(int t = j + 1; t < dubs.length; t++){
                    if(TripSequence.valid(dubs[i], dubs[j], dubs[t])){
                        tripSequences.add(new TripSequence(dubs[i], dubs[j], dubs[t]));
                    }
                }
            }
        }
        tripSequences.sort(TripSequence::compareTo);
        return tripSequences.toArray(new TripSequence[0]);
    }

    /* tìm 4 dôi thông */
    private QuadSequence[] quadSequenceScan(Card[] cards){
        ArrayList<QuadSequence> quadSequences = new ArrayList<>();
        Dub[] dubs = dubScan(cards);
        if(dubs.length < 3) return new QuadSequence[0];
        for(int i = 0; i < dubs.length; i++){
            for(int j = i + 1; j < dubs.length; j++){
                for(int t = j + 1; t < dubs.length; t++){
                    for(int k = t + 1; k < dubs.length; k++) {
                        if (QuadSequence.valid(dubs[i], dubs[j], dubs[t], dubs[k])) {
                            quadSequences.add(new QuadSequence(dubs[i], dubs[j], dubs[t], dubs[k]));
                        }
                    }
                }
            }
        }
        quadSequences.sort(QuadSequence::compareTo);
        return quadSequences.toArray(new QuadSequence[0]);
    }


    private ArrayList<Card> getAllCardsWithValue(Card[] cards, int value){
        ArrayList<Card> list = new ArrayList<>();
        for(Card card : cards){
            if(card.getValue() == value) list.add(card);
        }
        return list;
    }

    private void findSequence(Card[] cards, int currentIndex, final Integer[] arr, final ArrayList<ArrayList<Card>> lists, ArrayList<Card[]> out){
        if(currentIndex == arr.length){
            out.add(cards);
            return;
        }
        for(Card card : lists.get(arr[currentIndex])){
            Card[] tmp = new Card[cards.length + 1];
            System.arraycopy(cards, 0, tmp, 0, cards.length);
            tmp[cards.length] = card;
            findSequence(tmp, currentIndex + 1, arr, lists, out);

        }
    }

    private void find(int current, int stop, Integer[] currentIndexes, ArrayList<Integer[]> out){
        if(currentIndexes.length >= 3) out.add(currentIndexes);
        for(int i = current + 1; i < stop; i++){
            Integer[] tmp = new Integer[currentIndexes.length + 1];
            System.arraycopy(currentIndexes, 0, tmp, 0, currentIndexes.length);
            tmp[currentIndexes.length] = i;
            find(i, stop, tmp, out);
        }
    }
}
