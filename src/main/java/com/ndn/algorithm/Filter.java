package com.ndn.algorithm;

import com.ndn.base.BaseObject;
import com.ndn.base.Card;
import com.ndn.objects.Sequence;
import com.ndn.objects.Trips;

import java.util.ArrayList;

class Filter {

    static BaseObject[] removeUnnecessarySequences(BaseObject[] sequences) {
        boolean[] checked = new boolean[sequences.length];
        ArrayList<BaseObject> out = new ArrayList<>();

        for (int i = 0; i < sequences.length; i++) {
            if (checked[i]) continue;
            checked[i] = true;
            ArrayList<Sequence> same = new ArrayList<>();
            Sequence smin = (Sequence) sequences[i];
            same.add(smin);
            for (int j = 0; j < sequences.length; j++) {
                if (checked[j]) continue;
                Sequence s = (Sequence) sequences[j];
                if (s.getCards().length != smin.getCards().length) continue;
                if (s.getMinValue() != smin.getMinValue()) continue;
                checked[j] = true;
                same.add(s);
            }
            same.sort((o1, o2) -> {
                for (int i1 = o1.getCards().length - 1; i1 >= 0; i1--) {
                    if (i1 == o1.getCards().length - 1) {
                        if (o1.getCards()[i1].getType() > o2.getCards()[i1].getType()) return 1;
                        if (o1.getCards()[i1].getType() < o2.getCards()[i1].getType()) return -1;
                    } else {
                        if (o1.getCards()[i1].getType() < o2.getCards()[i1].getType()) return 1;
                        if (o1.getCards()[i1].getType() > o2.getCards()[i1].getType()) return -1;
                    }
                }
                return 0;
            });

            same = findSequences(same);
            out.addAll(same);
        }
        return out.toArray(new BaseObject[0]);
    }

    private static ArrayList<Sequence> findSequences(ArrayList<Sequence> sorted) {
        ArrayList<Sequence> found = new ArrayList<>();
        for (int i = sorted.size() - 1; i >= 0; i--) {
            boolean available = true;
            for (Sequence f : found) {
                if (connect(sorted.get(i).getCards(), f.getCards())) {
                    available = false;
                    break;
                }
            }
            if (available) found.add(sorted.get(i));
        }
        return found;
    }

    static BaseObject[] removeUnnecessaryTrips(BaseObject[] trips) {
        boolean[] checked = new boolean[trips.length];
        ArrayList<BaseObject> out = new ArrayList<>();

        for (int i = 0; i < trips.length; i++) {
            if (checked[i]) continue;
            checked[i] = true;
            Trips smin = (Trips) trips[i];
            for (int j = 0; j < trips.length; j++) {
                if (checked[j]) continue;
                Trips s = (Trips) trips[j];
                if (s.getValue() != smin.getValue()) continue;
                checked[j] = true;
                if (s.getValue() < smin.getValue()) smin = s;
            }
            out.add(smin);
        }

        return out.toArray(new BaseObject[0]);
    }

    static boolean connect(Card[] card1, Card[] card2) {
        for (Card c1 : card1) {
            for (Card c2 : card2) {
                if (c1.equals(c2)) return true;
            }
        }
        return false;
    }
}
