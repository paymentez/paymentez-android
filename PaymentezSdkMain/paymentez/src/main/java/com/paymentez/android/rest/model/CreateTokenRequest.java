
package com.paymentez.android.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.paymentez.android.model.Card;

public class CreateTokenRequest{

    @SerializedName("session_id")
    @Expose
    private String sessionId;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("card")
    @Expose
    private Card card;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

}
