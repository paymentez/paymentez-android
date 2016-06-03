package com.paymentez.androidsdk.models;

/**
 * Created by mmucito on 03/06/16.
 */

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Used to intercept and handle the responses from requests made using {@link AsyncHttpClient}, with
 * automatic parsing into a {@link JSONObject} or {@link JSONArray}. <p>&nbsp;</p> This class is
 * designed to be passed to get, post, put and delete requests with the {@link ***REMOVED***onSuccess(int,
 * Header[], JSONArray)} or {@link ***REMOVED***onSuccess(int,
 * Header[], JSONObject)} methods anonymously overridden. <p>&nbsp;</p>
 * Additionally, you can override the other event methods from the parent class.
 */
public class VerifyResponseHandler extends JsonHttpResponseHandler {

    private static final String LOG_TAG = "ListCardsResponseHandler";


    /**
     * Creates new JsonHttpResponseHandler, with JSON String encoding UTF-8
     */
    public VerifyResponseHandler() {
        super(DEFAULT_CHARSET);
    }


    /**
     * Returns when request succeeds
     *
     * @param statusCode http response status line
     * @param headers    response headers if any
     * @param response   parsed response if any
     */
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        PaymentezResponseDebitCard debitCard = new PaymentezResponseDebitCard();
        debitCard.setSuccess(true);
        debitCard.setCode(200);
        //debitCard.setErrorMessage(paymentezResponse.getErrorMessage());

        if(response != null) {

            try {
                debitCard.setStatus(response.getString("status"));
            } catch (JSONException e) {
            }
            try {
                String dtStart = response.getString("payment_date");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                try {
                    Date date = format.parse(dtStart);
                    debitCard.setPaymentDate(date);
                    System.out.println(date);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            } catch (JSONException e) {
            }
            try {
                debitCard.setAmount(response.getDouble("amount"));
            } catch (JSONException e) {
            }
            try {
                debitCard.setTransactionId(response.getString("transaction_id"));
            } catch (JSONException e) {
            }
            try {
                debitCard.setStatusDetail(response.getString("status_detail"));
            } catch (JSONException e) {
            }
        }

        onSuccess(statusCode, headers, debitCard);
    }

    /**
     * Returns when request succeeds
     *
     * @param statusCode http response status line
     * @param headers    response headers if any
     * @param response   parsed response if any
     */
    public void onSuccess(int statusCode, Header[] headers, PaymentezResponseDebitCard response) {
        AsyncHttpClient.log.w(LOG_TAG, "onSuccess(int, Header[], PaymentezResponseDebitCard) was not overriden, but callback was received");
    }




}
