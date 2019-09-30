package com.ndn.base;

import com.ndn.objects.Dub;

import java.io.Serializable;

/**
 * contains card, dub, triple, quad, sequence,...vv
 *
 * @author dangnguyendota
 */
public interface BaseObject extends Serializable {
    /* get cards */
    Card[] getCards();

    boolean beats(BaseObject object);

    BaseObject getCopy();

    boolean contains(Card card);

    boolean contains(int value);
}
