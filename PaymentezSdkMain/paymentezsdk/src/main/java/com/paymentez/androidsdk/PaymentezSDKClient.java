package com.paymentez.androidsdk;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.devicecollector.DeviceCollector;
import com.devicecollector.DeviceCollector.ErrorCode;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.paymentez.androidsdk.models.PaymentezCard;
import com.paymentez.androidsdk.models.PaymentezDebitParameters;


import cz.msebera.android.httpclient.Header;


/**
 * Created by mmucito on 24/05/16.
 */
public class PaymentezSDKClient implements DeviceCollector.StatusListener{

    public static int PAYMENTEZ_SCAN_CARD_REQUEST_CODE = 1019;
    public static String PAYMENTEZ_EXTRA_SCAN_RESULT = "com.paymentez.scan";

    private String app_code;
    private String app_secret_key;
    private boolean dev_environment;
    private String SERVER_DEV_URL = "https://ccapi-stg.paymentez.com";
    private String SERVER_PROD_URL = "https://ccapi.paymentez.com";
    private String SERVER_URL = SERVER_DEV_URL;

    private DeviceCollector dc;
    public String sessionId;
    private static final String LOG_TAG = "CheckoutTestActivity";
    private boolean running = false;
    private boolean finished = false;
    private String message;
    private Date startTime;
    private int collect_count;

    private Context mContext;

    private AsyncHttpClient clientAsync;

    String uiText;


    /**
     *
     * @param mContext Context of the Main Activity
     * @param dev_environment false to use production environment
     * @param app_code Application identifier (provided by Paymentez).
     * @param app_secret_key Application Secret key (provided by Paymentez).
     */
    public PaymentezSDKClient(Context mContext, boolean dev_environment, String app_code, String app_secret_key) {
        this.mContext = mContext;
        this.app_code = app_code;
        this.app_secret_key = app_secret_key;
        clientAsync = new AsyncHttpClient();
        clientAsync.setUserAgent("Mozilla/5.0 (Linux; Android 5.1.1; Nexus 5 Build/LMY48B; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/43.0.2357.65 Mobile Safari/537.36");
        clientAsync.setTimeout(120000);


        this.dev_environment = dev_environment;

        this.debug("Building new...");
        // No saved instances, create a new one
        this.dc = new DeviceCollector((Activity) mContext);
        // TODO Put your Merchant ID here
        this.dc.setMerchantId("500005");
        // TODO Put your data collector URL here

        if (dev_environment){
            SERVER_URL = SERVER_DEV_URL;
            this.dc.setCollectorUrl("https://tst.kaptcha.com/logo.htm");
        }else{
            SERVER_URL = SERVER_PROD_URL;
            this.dc.setCollectorUrl("https://ssl.kaptcha.com/logo.htm");
        }

        collect_count = 0;

        // Skipping Collectors
        // If we wanted to skip a test or two, we could uncomment this code
        // EnumSet<DeviceCollector.Collector> skipList =
        // EnumSet.of(
        // DeviceCollector.Collector.GEO_LOCATION);
        // dc.skipCollectors(skipList);
        this.dc.setStatusListener(this);
        getSessionId();
    }


    /**
     * Method to Scan a Card
     */
    public void scanCard(){
        Intent intent = new Intent(mContext, ScanCardActivity.class);
        ((Activity) mContext).startActivityForResult(intent, PAYMENTEZ_SCAN_CARD_REQUEST_CODE);
    }

    /**
     * @param uid User identifier. This is the identifier you use inside your application; you will receive it in notifications.
     * @param email Email of the user initiating the purchase. Format: Valid e-mail format.
     * @return An url string that must be loaded in a webview
     */
    public void addCardShowWebView(String uid, String email, Context mContext) {

        String auth_timestamp = "" + (System.currentTimeMillis());

        String params = "application_code=" + app_code + "&email="
                + Uri.encode(email) + "&session_id=" + sessionId + "&uid="
                + uid;

        String auth_token = getAuthToken(auth_timestamp, params);

        String url = SERVER_URL + "/api/cc/add/?" + params + "&auth_timestamp="
                + auth_timestamp + "&auth_token=" + auth_token;

        Intent intent = new Intent(mContext, ActivityWebView.class);
        Bundle b = new Bundle();
        b.putString("url", url);
        intent.putExtras(b);

        mContext.startActivity(intent);

    }


    /**
     * @param paymentezCard
     * @return PaymentezResponse
     */
    public void addCard(final PaymentezCard paymentezCard, final AsyncHttpResponseHandler responseHandler) {

        final String auth_timestamp = "" + (System.currentTimeMillis());
        final Map<String,String> paramsPost = new HashMap<>();

        paramsPost.put("uid", paymentezCard.getUid());
        paramsPost.put("email", paymentezCard.getEmail());
        paramsPost.put("session_id", sessionId);
        paramsPost.put("application_code", app_code);


        clientAsync.get(SERVER_URL + "/api/cc/ip", new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                String auth_token = getAuthToken(auth_timestamp, paramsPost);
                paramsPost.put("auth_timestamp", auth_timestamp);
                paramsPost.put("auth_token", auth_token);
                paramsPost.put("expiryYear", paymentezCard.getExpiryYear());
                paramsPost.put("expiryMonth", paymentezCard.getExpiryMonth());
                paramsPost.put("holderName", paymentezCard.getCardHolder());
                paramsPost.put("number", paymentezCard.getCardNumber());
                paramsPost.put("cvc", paymentezCard.getCvc());
                paramsPost.put("card_type", paymentezCard.getType());

                RequestParams params = new RequestParams(paramsPost);
                clientAsync.post(SERVER_URL + "/api/cc/add/creditcard", params, responseHandler);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String ip) {
                String auth_token = getAuthToken(auth_timestamp, paramsPost);
                paramsPost.put("auth_timestamp", auth_timestamp);
                paramsPost.put("auth_token", auth_token);
                paramsPost.put("expiryYear", paymentezCard.getExpiryYear());
                paramsPost.put("expiryMonth", paymentezCard.getExpiryMonth());
                paramsPost.put("holderName", paymentezCard.getCardHolder());
                paramsPost.put("number", paymentezCard.getCardNumber());
                paramsPost.put("cvc", paymentezCard.getCvc());
                paramsPost.put("card_type", paymentezCard.getType());

                RequestParams params = new RequestParams(paramsPost);
                clientAsync.post(SERVER_URL + "/api/cc/add/creditcard", params, responseHandler);
            }



        });


    }


    /**
     * @param uid
     * @param responseHandler
     */
    public void listCards(String uid, AsyncHttpResponseHandler responseHandler) {




        String auth_timestamp = "" + (System.currentTimeMillis());

        String params = "application_code=" + app_code +  "&uid="
                + uid;

        String auth_token = getAuthToken(auth_timestamp, params);

        Map<String,String> paramsPost = new HashMap<>();

        //clientAsync.setLoggingEnabled(true);
        //clientAsync.setLoggingLevel(Log.DEBUG);


        clientAsync.get(SERVER_URL + "/api/cc/list/?" + params + "&auth_timestamp="
                + auth_timestamp + "&auth_token=" + auth_token, null, responseHandler);

    }


    /**
     * @param debitParameters
     * @param responseHandler
     */
    public void debitCard(final PaymentezDebitParameters debitParameters, final AsyncHttpResponseHandler responseHandler) {
        final String auth_timestamp = "" + (System.currentTimeMillis());
        final Map<String,String> paramsPost = new HashMap<>();

        paramsPost.putAll(debitParameters.toHashMap());

        paramsPost.put("application_code", app_code);
        paramsPost.put("session_id", sessionId);

        clientAsync.get(SERVER_URL + "/api/cc/ip", new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                paramsPost.put("ip_address", "127.0.0.1");
                String auth_token = getAuthToken(auth_timestamp, paramsPost);
                paramsPost.put("auth_timestamp", auth_timestamp);
                paramsPost.put("auth_token", auth_token);
                RequestParams params = new RequestParams(paramsPost);
                clientAsync.post(SERVER_URL + "/api/cc/debit/", params, responseHandler);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String ip) {
                paramsPost.put("ip_address", ip);
                String auth_token = getAuthToken(auth_timestamp, paramsPost);
                paramsPost.put("auth_timestamp", auth_timestamp);
                paramsPost.put("auth_token", auth_token);
                RequestParams params = new RequestParams(paramsPost);
                clientAsync.post(SERVER_URL + "/api/cc/debit/", params, responseHandler);
            }



        });

    }

    /**
     * @param uid
     * @param card_reference
     * @return
     */
    public void deleteCard(String uid, String card_reference, AsyncHttpResponseHandler responseHandler) {

        String auth_timestamp = "" + (System.currentTimeMillis());

        Map<String,String> paramsPost = new HashMap<>();
        paramsPost.put("application_code", app_code);
        paramsPost.put("uid", uid);
        paramsPost.put("card_reference", card_reference);
        String auth_token = getAuthToken(auth_timestamp, paramsPost);
        paramsPost.put("auth_timestamp", auth_timestamp);
        paramsPost.put("auth_token", auth_token);


        RequestParams params = new RequestParams(paramsPost);
        clientAsync.post(SERVER_URL + "/api/cc/delete/", params, responseHandler);

    }

    /**
     * @param transactionId
     * @param uid
     * @param verificationCode
     * @return
     */
    public void verifyWithCode(String transactionId, String uid, String verificationCode, AsyncHttpResponseHandler responseHandler) {

        String auth_timestamp = "" + (System.currentTimeMillis());

        Map<String,String> paramsPost = new HashMap<>();
        paramsPost.put("application_code", app_code);
        paramsPost.put("transaction_id", transactionId);
        paramsPost.put("value", verificationCode);
        paramsPost.put("type", "BY_AUTH_CODE");
        paramsPost.put("uid", uid);


        String auth_token = getAuthToken(auth_timestamp, paramsPost);
        paramsPost.put("auth_timestamp", auth_timestamp);
        paramsPost.put("auth_token", auth_token);

        RequestParams params = new RequestParams(paramsPost);
        clientAsync.post(SERVER_URL + "/api/cc/verify/", params, responseHandler);




    }

    /**
     * @param transactionId
     * @param uid
     * @param amount
     * @return
     */
    public void verifyWithAmount(String transactionId, String uid, double amount, AsyncHttpResponseHandler responseHandler) {

        String auth_timestamp = "" + (System.currentTimeMillis());

        Map<String,String> paramsPost = new HashMap<>();
        paramsPost.put("application_code", app_code);
        paramsPost.put("transaction_id", transactionId);
        paramsPost.put("value", String.format( "%.2f", amount ));
        paramsPost.put("type", "BY_AMOUNT");
        paramsPost.put("uid", uid);


        String auth_token = getAuthToken(auth_timestamp, paramsPost);
        paramsPost.put("auth_timestamp", auth_timestamp);
        paramsPost.put("auth_token", auth_token);

        RequestParams params = new RequestParams(paramsPost);
        clientAsync.post(SERVER_URL + "/api/cc/verify/", params, responseHandler);


    }




    public Comparator<Map<String, String>> mapComparator = new Comparator<Map<String, String>>() {
        public int compare(Map<String, String> m1, Map<String, String> m2) {
            return m1.get("name").compareTo(m2.get("name"));
        }
    };

    private String getAuthToken(String auth_timestamp, Map<String,String> params) {



        SortedSet<String> keys = new TreeSet<String>(params.keySet());


        String urltoken = "";
        for (String key : keys) {
            String value = params.get(key);

            urltoken += key +"="+Uri.encode(value)+"&";
            // do something
        }




        urltoken += auth_timestamp + "&" + app_secret_key;

        //allanurltoken = urltoken.replaceAll("%20", "+");


        return bin2hex(getHash(urltoken)).toLowerCase();
    }

    private String getAuthToken(String auth_timestamp, String params) {
        String urltoken = params + "&" + auth_timestamp + "&" + app_secret_key;

        return bin2hex(getHash(urltoken)).toLowerCase();
    }

    public byte[] getHash(String password) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        digest.reset();
        return digest.digest(password.getBytes());
    }

    static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1,
                data));
    }









    /**
     * Tell the library to stop immediately.
     */
    public void stopNow(View view) {
        if (!this.finished && this.running && null != this.dc) {
            this.dc.stopNow();
        }
    } // end stopNow ()

    /**
     * This method generates the sessionId and call kount Collect
     *
     * @return sessionId
     */
    public String getSessionId() {
        // Check if we are already running
        if (!this.running) {
            // Check if we already finished
            if (!this.finished) {
                // Create a sessionID (Unique ID) that doesn't repeat over a 30
                // day
                // period per transaction
                sessionId = UUID.randomUUID().toString();
                // The device collector does not like special characters in the
                // sessionID, so let's strip them out
                sessionId = sessionId.replace("-", "");
                sessionId = "a"+sessionId.substring(1, sessionId.length());

                this.debug("Checking out with sessionid [" + sessionId + "]");
                this.startTime = new Date();
                this.dc.collect(sessionId);

                // we should store this sessionId somewhere so we can pass it to
                // whatever is making the RIS call down the line.
            } else {
                this.debug("Already completed for this transaction. Why are you"
                        + "trying to run again?");

            } // end if (!this.finished) / else

        } else {
            this.debug("Already running");
        } // end if (!this.running) / else

        return sessionId;
    }

    /**
     * Implementation of handling an error coming from the collector.
     *
     * @param code
     *            The Error code returned
     * @param ex
     *            The Exception that caused the code.
     */

    @Override
    public void onCollectorError(ErrorCode code, Exception ex) {
        long totalTime = getTotalTime();

        this.finished = true;
        if (null != ex) {
            if (code.equals(ErrorCode.MERCHANT_CANCELLED)) {
                this.debug("Merchant Cancelled");
            } else {
                this.debug("Collector Failed in (" + totalTime
                        + ") ms. It had an error [" + code + "]:"
                        + ex.getMessage());
                this.debug("Stack Trace:");
                for (StackTraceElement element : ex.getStackTrace()) {
                    this.debug(element.getClassName() + " "
                            + element.getMethodName() + "("
                            + element.getLineNumber() + ")");
                } // end for (StackTraceElement element : ex.getStackTrace())
            } // end if (code.equals(ErrorCode.MERCHANT_CANCELLED)) / else
        } else {
            this.debug("Collector failed in (" + totalTime
                    + ") ms. It had an error [" + code + "]:");
        } // end if (null != ex) / else

        if(code.equals(ErrorCode.RUNTIME_FAILURE)){
            this.debug("Time out try"+collect_count);
            this.getSessionId();
            collect_count++;

        }

    } // end onCollectorError (ErrorCode code, Exception ex)

    /**
     * Implementation of handling collection start. In this case we are just
     * logging, and marking a flag as running.
     */
    @Override
    public void onCollectorStart() {
        long totalTime = getTotalTime();
        this.debug("Starting collector (" + totalTime + ")ms....");
        this.running = true;
    } // end onCollectorStart ()

    /**
     * Implementation of handling collection start. In this case we are just
     * logging, and marking a flag as not running.
     */
    @Override
    public void onCollectorSuccess() {
        long totalTime = getTotalTime();
        this.debug("Collector finished successfully in (" + totalTime + ") ms");
        this.running = false;
        this.finished = true;
        // Let other processes know it's all done here

    } // end onCollectorSuccess ()

    private long getTotalTime() {
        Date stopTime = new Date();
        return stopTime.getTime() - startTime.getTime();
    }

    /*
     * Debug messages. Send to the view and to the logs.
     *
     * @param message The message to pass to the view and logs
     */
    private void debug(String message) {
        Log.d(LOG_TAG, message);

    } // end debug (String message)
}
