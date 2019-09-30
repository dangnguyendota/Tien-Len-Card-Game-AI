package com.ndn.objects;

import com.ndn.base.BaseObject;
import com.ndn.base.Card;
import com.ndn.base.CardContainer;

public class Pass extends CardContainer {
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
        return this;
    }

    @Override
    public String toString(){
        return "Pass";
    }

    @Override
    public boolean equals(Object o){
        return o instanceof Pass;
    }
}
