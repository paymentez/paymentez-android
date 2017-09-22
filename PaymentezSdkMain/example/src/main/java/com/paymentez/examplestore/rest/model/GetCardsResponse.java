
package com.paymentez.examplestore.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.paymentez.android.model.Card;

import java.util.List;

public class GetCardsResponse {

    @SerializedName("cards")
    @Expose
    private List<Card> cards = null;
    @SerializedName("result_size")
    @Expose
    private Integer resultSize;

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public Integer getResultSize() {
        return resultSize;
    }

    public void setResultSize(Integer resultSize) {
        this.resultSize = resultSize;
    }

}
