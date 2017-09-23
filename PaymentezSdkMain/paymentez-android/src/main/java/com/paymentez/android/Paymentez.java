package com.paymentez.android;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kount.api.DataCollector;
import com.paymentez.android.model.Card;
import com.paymentez.android.rest.PaymentezService;
import com.paymentez.android.rest.PaymenezClient;
import com.paymentez.android.rest.TokenCallback;
import com.paymentez.android.rest.model.CreateTokenRequest;
import com.paymentez.android.rest.model.CreateTokenResponse;
import com.paymentez.android.rest.model.ErrorResponse;
import com.paymentez.android.rest.model.PaymentezError;
import com.paymentez.android.rest.model.User;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by mmucito on 24/05/16.
 */
public class Paymentez{

    private boolean is_dev;

    static int MERCHANT_ID = 500005;
    static int KOUNT_ENVIRONMENT = DataCollector.ENVIRONMENT_TEST;

    Context mContext;
    final PaymentezService paymentezService;

    /**
     *
     * @param mContext Context of the Main Activity
     * @param is_dev false to use production environment
     * @param app_code Application identifier (provided by Paymentez).
     * @param app_secret_key Application Secret key (provided by Paymentez).
     */
    public Paymentez(Context mContext, boolean is_dev, String app_code, String app_secret_key) {
        this.mContext = mContext;
        this.is_dev = is_dev;
        if (this.is_dev){
            KOUNT_ENVIRONMENT = DataCollector.ENVIRONMENT_TEST;

        }else{
            KOUNT_ENVIRONMENT = DataCollector.ENVIRONMENT_PRODUCTION;

        }

        paymentezService = PaymenezClient.getClient(mContext, is_dev, app_code, app_secret_key).create(PaymentezService.class);

    }




    /**
     * The simplest way to create a token, using a {@link Card} and {@link TokenCallback}. T
     *
     * @param uid User identifier. This is the identifier you use inside your application; you will receive it in notifications.
     * @param email Email of the user initiating the purchase. Format: Valid e-mail format.
     * @param card the {@link Card} used to create this payment token
     * @param callback a {@link TokenCallback} to receive either the token or an error
     */
    public void createToken(@NonNull final String uid, @NonNull final String email, @NonNull final Card card, @NonNull final TokenCallback callback) {


        User user = new User();
        user.setId(uid);
        user.setEmail(email);

        CreateTokenRequest createTokenRequest = new CreateTokenRequest();
        createTokenRequest.setSessionId(getSessionId());
        createTokenRequest.setCard(card);
        createTokenRequest.setUser(user);

        paymentezService.createToken(createTokenRequest).enqueue(new Callback<CreateTokenResponse>() {
            @Override
            public void onResponse(Call<CreateTokenResponse> call, Response<CreateTokenResponse> response) {
                CreateTokenResponse createTokenResponse = response.body();
                if(response.isSuccessful()) {
                    callback.onSuccess(createTokenResponse.getCard());
                    return;
                }else {
                    PaymentezError error
                            = new PaymentezError("Exception", "", "General Error");
                    try {
                        Gson gson = new GsonBuilder().create();
                        ErrorResponse errorResponse = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                        callback.onError(errorResponse.getError());
                        return;
                    } catch (Exception e) {
                        try {
                            error = new PaymentezError("Exception", "Http Code: " + response.code(), response.message());
                        } catch (Exception e2) {
                        }
                    }
                    callback.onError(error);
                    return;

                }
            }

            @Override
            public void onFailure(Call<CreateTokenResponse> call, Throwable e) {
                PaymentezError error
                        = new PaymentezError("Network Exception",
                        "Invoked when a network exception occurred communicating to the server.", e.getLocalizedMessage());
                callback.onError(error);
                return;
            }
        });
    }


    /**
     * The session ID is a parameter Paymentez use for fraud purposes.
     *
     * @return session_id
     */
    public String getSessionId(){
        String sessionID = UUID.randomUUID().toString();
        final String deviceSessionID = sessionID.replace("-", "");


        // Configure the collector
        final DataCollector dataCollector = com.kount.api.DataCollector.getInstance();
        if(is_dev)
            dataCollector.setDebug(true);
        else
            dataCollector.setDebug(false);
        dataCollector.setContext(mContext);
        dataCollector.setMerchantID(MERCHANT_ID);
        dataCollector.setEnvironment(KOUNT_ENVIRONMENT);
        dataCollector.setLocationCollectorConfig(DataCollector.LocationConfig.COLLECT);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                dataCollector.collectForSession(deviceSessionID, new com.kount.api.DataCollector.CompletionHandler() {
                    @Override
                    public void completed(String s) {

                    }

                    @Override
                    public void failed(String s, final DataCollector.Error error) {
                        Log.d("Collector", s + " - " + error.getDescription());
                    }

                });
            }
        });

        return deviceSessionID;
    }

}
