package com.paymentez.androidsdk.models;

import java.util.ArrayList;

/**
 * Created by mmucito on 31/05/16.
 */
public class PaymentezResponseListCards  extends PaymentezResponse{

    private ArrayList<PaymentezCard> cards;



    public ArrayList<PaymentezCard> getCards() {
        return cards;
    }

    public void setCards(ArrayList<PaymentezCard> cards) {
        this.cards = cards;
    }


}
