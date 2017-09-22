
package com.paymentez.examplestore.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.paymentez.android.model.Card;

public class CreateChargeResponse {

    @SerializedName("transaction")
    @Expose
    private Transaction transaction;
    @SerializedName("card")
    @Expose
    private Card card;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

}
