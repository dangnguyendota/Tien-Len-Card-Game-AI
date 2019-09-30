package com.ndn.service;

import com.ndn.base.BaseObject;
import com.ndn.base.Card;

public class SendData {
    Card[] cards;
    String type;
    public SendData(BaseObject object){
       this.cards = object.getCards();
        this.type = object.getClass().getSimpleName();
    }
}
