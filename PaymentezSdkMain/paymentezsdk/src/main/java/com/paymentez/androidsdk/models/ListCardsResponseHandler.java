package com.paymentez.androidsdk.models;

/**
 * Created by mmucito on 03/06/16.
 */
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import cz.msebera.android.httpclient.Header;

/**
 * Used to intercept and handle the responses from requests made using {@link AsyncHttpClient}, with
 * automatic parsing into a {@link JSONObject} or {@link JSONArray}. <p>&nbsp;</p> This class is
 * designed to be passed to get, post, put and delete requests with the {@link ***REMOVED***onSuccess(int,
 * cz.msebera.android.httpclient.Header[], org.json.JSONArray)} or {@link ***REMOVED***onSuccess(int,
 * cz.msebera.android.httpclient.Header[], org.json.JSONObject)} methods anonymously overridden. <p>&nbsp;</p>
 * Additionally, you can override the other event methods from the parent class.
 */
public class ListCardsResponseHandler extends JsonHttpResponseHandler {

    private static final String LOG_TAG = "ListCardsResponseHandler";


    /**
     * Creates new JsonHttpResponseHandler, with JSON String encoding UTF-8
     */
    public ListCardsResponseHandler() {
        super(DEFAULT_CHARSET);
    }


    /**
     * Returns when request succeeds
     *
     * @param statusCode http response status line
     * @param headers    response headers if any
     * @param response   parsed response if any
     */
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        JSONArray paymentezResponse = response;
        PaymentezResponseListCards listCards = new PaymentezResponseListCards();
        listCards.setCode(200);
        listCards.setSuccess(true);
        listCards.setErrorMessage("");

        ArrayList<PaymentezCard> cards = new ArrayList<>();

        for (int i = 0; i < paymentezResponse.length(); i++) {
            String card_reference = "";
            try {
                card_reference = paymentezResponse.getJSONObject(i).getString("card_reference");
            } catch (JSONException e) {}
            String type = "";
            try {
                type = paymentezResponse.getJSONObject(i).getString("type");
            } catch (JSONException e) {}
            String name = "";
            try {
                name = paymentezResponse.getJSONObject(i).getString("name");
            } catch (JSONException e) {}
            String termination = "";
            try {
                termination = paymentezResponse.getJSONObject(i).getString("termination");
            } catch (JSONException e) {}
            String expiry_month = "";
            try {
                expiry_month = paymentezResponse.getJSONObject(i).getString("expiry_month");
            } catch (JSONException e) {}
            String expiry_year = "";
            try {
                expiry_year = paymentezResponse.getJSONObject(i).getString("expiry_year");
            } catch (JSONException e) {}
            String bin = "";
            try {
                bin = paymentezResponse.getJSONObject(i).getString("bin");
            } catch (JSONException e) {}


            PaymentezCard paymentezCard = new PaymentezCard();
            paymentezCard.setCardReference(card_reference);
            paymentezCard.setType(type);
            paymentezCard.setCardHolder(name);
            paymentezCard.setTermination(termination);
            paymentezCard.setExpiryMonth(expiry_month);
            paymentezCard.setExpiryYear(expiry_year);
            paymentezCard.setBin(bin);

            cards.add(paymentezCard);
        }




        listCards.setCards(cards);


        onSuccess(statusCode, headers, listCards);
    }

    /**
     * Returns when request succeeds
     *
     * @param statusCode http response status line
     * @param headers    response headers if any
     * @param response   parsed response if any
     */
    public void onSuccess(int statusCode, Header[] headers, PaymentezResponseListCards response) {
        AsyncHttpClient.log.w(LOG_TAG, "onSuccess(int, Header[], PaymentezResponseListCards) was not overriden, but callback was received");
    }




}
