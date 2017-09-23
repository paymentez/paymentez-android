
package com.paymentez.android.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ErrorResponse {

    @SerializedName("error")
    @Expose
    private PaymentezError error;

    public PaymentezError getError() {
        return error;
    }

    public void setError(PaymentezError error) {
        this.error = error;
    }

}
