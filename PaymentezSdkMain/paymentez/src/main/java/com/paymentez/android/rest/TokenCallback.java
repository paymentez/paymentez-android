package com.paymentez.android.rest;

import com.paymentez.android.model.Card;
import com.paymentez.android.rest.model.CreateTokenResponse;
import com.paymentez.android.rest.model.PaymentezError;

/**
 * An interface representing a callback to be notified about the results of
 * {@link CreateTokenResponse} creation or requests
 */
public interface TokenCallback {

    /**
     * PaymentezError callback method.
     * @param error the error that occurred.
     */
    void onError(PaymentezError error);

    /**
     * Success callback method.
     * @param card the {@link Card} that was found or created.
     */
    void onSuccess(Card card);
}