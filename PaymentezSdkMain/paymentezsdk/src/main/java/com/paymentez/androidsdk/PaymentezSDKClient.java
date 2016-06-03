package com.paymentez.androidsdk;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.devicecollector.DeviceCollector;
import com.devicecollector.DeviceCollector.ErrorCode;
import com.paymentez.androidsdk.models.PaymentezCard;
import com.paymentez.androidsdk.models.PaymentezCardData;
import com.paymentez.androidsdk.models.PaymentezCarrierData;
import com.paymentez.androidsdk.models.PaymentezDebitParameters;
import com.paymentez.androidsdk.models.PaymentezResponse;
import com.paymentez.androidsdk.models.PaymentezResponseDebitCard;
import com.paymentez.androidsdk.models.PaymentezResponseListCards;
import com.paymentez.androidsdk.utils.LoggingInterceptor;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
     * @param paymentezCard PaymentezCard parameters expected.
     * @return JSONObject with the following params {"status": "", "payment_date": "", "status_detail": , "amount": , "card_data": {"account_type": "", "type": "", "number": "", "quotas": ""}, "transaction_id": "", "carrier_data": {"terminal_code": "", "unique_code": "", "acquirer_id": "", "authorization_code": ""}
     */
    public PaymentezResponse addCard(PaymentezCard paymentezCard) {

        String auth_timestamp = "" + (System.currentTimeMillis());
        Map<String,String> paramsPost = new HashMap<>();

        paramsPost.put("uid", paymentezCard.getUid());
        paramsPost.put("email", paymentezCard.getEmail());




        paramsPost.put("session_id", sessionId);
        paramsPost.put("application_code", app_code);
        paramsPost.put("ip_address", getLocalIpAddress());

        String auth_token = getAuthToken(auth_timestamp, paramsPost);
        paramsPost.put("auth_timestamp", auth_timestamp);
        paramsPost.put("auth_token", auth_token);

        paramsPost.put("expiryYear", paymentezCard.getExpiryYear());
        paramsPost.put("expiryMonth", paymentezCard.getExpiryMonth());
        paramsPost.put("holderName", paymentezCard.getCardHolder());
        paramsPost.put("number", paymentezCard.getCardNumber());
        paramsPost.put("cvc", paymentezCard.getCvc());
        paramsPost.put("card_type", paymentezCard.getType());

        PaymentezResponse paymentezResponse = callApiJSONObject(SERVER_URL + "/api/cc/add/creditcard", paramsPost);
        String verify_transaction = "";
        if(paymentezResponse.getJson().contains("verify_transaction")){
            paymentezResponse.setCode(200);
            paymentezResponse.setSuccess(true);
            paymentezResponse.setStatus("failure");

        }

        Log.i("Mio", verify_transaction);
        Log.i("Mio", ""+paymentezResponse.getCode());
        Log.i("Mio", ""+paymentezResponse.isSuccess());



        return paymentezResponse;

    }

    /**
     * @param uid User identifier. This is the identifier you use inside your application; you will receive it in notifications.
     * @return JSONArray of JSONObjects with the following params name, card_reference, expiry_year, termination, expiry_month, transaction_reference, type
     */
    public PaymentezResponseListCards listCards(String uid) {

        String auth_timestamp = "" + (System.currentTimeMillis());

        String params = "application_code=" + app_code +  "&uid="
                + uid;

        String auth_token = getAuthToken(auth_timestamp, params);

        Map<String,String> paramsPost = new HashMap<>();

        PaymentezResponse paymentezResponse = callApiJSONArray(SERVER_URL + "/api/cc/list/?" + params + "&auth_timestamp="
                + auth_timestamp + "&auth_token=" + auth_token, paramsPost);


        PaymentezResponseListCards listCards = new PaymentezResponseListCards();
        listCards.setCode(paymentezResponse.getCode());
        listCards.setSuccess(paymentezResponse.isSuccess());
        listCards.setErrorMessage(paymentezResponse.getErrorMessage());

        ArrayList<PaymentezCard> cards = new ArrayList<>();
        if(paymentezResponse.isSuccess()) {
            for (int i = 0; i < paymentezResponse.getBodyJsonArray().length(); i++) {
                String card_reference = "";
                try {
                    card_reference = paymentezResponse.getBodyJsonArray().getJSONObject(i).getString("card_reference");
                } catch (JSONException e) {}
                String type = "";
                try {
                    type = paymentezResponse.getBodyJsonArray().getJSONObject(i).getString("type");
                } catch (JSONException e) {}
                String name = "";
                try {
                    name = paymentezResponse.getBodyJsonArray().getJSONObject(i).getString("name");
                } catch (JSONException e) {}
                String termination = "";
                try {
                    termination = paymentezResponse.getBodyJsonArray().getJSONObject(i).getString("termination");
                } catch (JSONException e) {}
                String expiry_month = "";
                try {
                    expiry_month = paymentezResponse.getBodyJsonArray().getJSONObject(i).getString("expiry_month");
                } catch (JSONException e) {}
                String expiry_year = "";
                try {
                    expiry_year = paymentezResponse.getBodyJsonArray().getJSONObject(i).getString("expiry_year");
                } catch (JSONException e) {}
                String bin = "";
                try {
                    bin = paymentezResponse.getBodyJsonArray().getJSONObject(i).getString("bin");
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


        }

        listCards.setCards(cards);

        return listCards;

    }

    /**
     * @param debitParameters PaymentezDebitParameters parameters expected.
     * @return JSONObject with the following params {"status": "", "payment_date": "", "status_detail": , "amount": , "card_data": {"account_type": "", "type": "", "number": "", "quotas": ""}, "transaction_id": "", "carrier_data": {"terminal_code": "", "unique_code": "", "acquirer_id": "", "authorization_code": ""}
     */
    public PaymentezResponseDebitCard debitCard(PaymentezDebitParameters debitParameters) {

        String auth_timestamp = "" + (System.currentTimeMillis());
        Map<String,String> paramsPost = new HashMap<>();

        paramsPost.putAll(debitParameters.toHashMap());

        paramsPost.put("application_code", app_code);
        paramsPost.put("ip_address", getLocalIpAddress());
        paramsPost.put("session_id", sessionId);
        String auth_token = getAuthToken(auth_timestamp, paramsPost);
        paramsPost.put("auth_timestamp", auth_timestamp);
        paramsPost.put("auth_token", auth_token);


        PaymentezResponse paymentezResponse = callApiJSONObject(SERVER_URL + "/api/cc/debit/", paramsPost);


        PaymentezResponseDebitCard debitCard = new PaymentezResponseDebitCard();
        debitCard.setSuccess(paymentezResponse.isSuccess());
        debitCard.setCode(paymentezResponse.getCode());
        debitCard.setErrorMessage(paymentezResponse.getErrorMessage());




        if(paymentezResponse.getBodyJsonObject() != null) {

            try {
                debitCard.setStatus(paymentezResponse.getBodyJsonObject().getString("status"));
            } catch (JSONException e) {}
            try {
                String dtStart = paymentezResponse.getBodyJsonObject().getString("payment_date");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                try {
                    Date date = format.parse(dtStart);
                    debitCard.setPaymentDate(date);
                    System.out.println(date);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            } catch (JSONException e) {}
            try {
                debitCard.setAmount(paymentezResponse.getBodyJsonObject().getDouble("amount"));
            } catch (JSONException e) {}
            try {
                debitCard.setTransactionId(paymentezResponse.getBodyJsonObject().getString("transaction_id"));
            } catch (JSONException e) {}
            try {
                debitCard.setStatusDetail(paymentezResponse.getBodyJsonObject().getString("status_detail"));
            } catch (JSONException e) {}

            PaymentezCardData paymentezCardData = new PaymentezCardData();
            try {
                JSONObject jsonCardData = paymentezResponse.getBodyJsonObject().getJSONObject("card_data");

                paymentezCardData.setAccountType(jsonCardData.getString("account_type"));

                paymentezCardData.setType(jsonCardData.getString("type"));

                paymentezCardData.setNumber(jsonCardData.getString("number"));

                paymentezCardData.setQuotas(jsonCardData.getString("quotas"));
            } catch (JSONException e) {}

            debitCard.setCardData(paymentezCardData);


            PaymentezCarrierData paymentezCarrierData = new PaymentezCarrierData();
            try {
                JSONObject jsonCardData = paymentezResponse.getBodyJsonObject().getJSONObject("carrier_data");

                paymentezCarrierData.setTerminalCode(jsonCardData.getString("terminal_code"));

                paymentezCarrierData.setUniqueCode(jsonCardData.getString("unique_code"));

                paymentezCarrierData.setAcquirerId(jsonCardData.getString("acquirer_id"));

                paymentezCarrierData.setAuthorizationCode(jsonCardData.getString("authorization_code"));
            } catch (JSONException e) {}

            debitCard.setCarrierData(paymentezCarrierData);
        }

        return debitCard;

    }

    /**
     * @param uid
     * @param card_reference
     * @return
     */
    public PaymentezResponse deleteCard(String uid, String card_reference) {

        String auth_timestamp = "" + (System.currentTimeMillis());

        Map<String,String> paramsPost = new HashMap<>();
        paramsPost.put("application_code", app_code);
        paramsPost.put("uid", uid);

        paramsPost.put("card_reference", card_reference);


        String auth_token = getAuthToken(auth_timestamp, paramsPost);
        paramsPost.put("auth_timestamp", auth_timestamp);
        paramsPost.put("auth_token", auth_token);

        PaymentezResponse paymentezResponse = callApiJSONObject(SERVER_URL + "/api/cc/delete/", paramsPost);


        return paymentezResponse;

    }

    /**
     * @param transactionId
     * @param uid
     * @param verificationCode
     * @return
     */
    public PaymentezResponseDebitCard verifyWithCode(String transactionId, String uid, String verificationCode) {

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

        PaymentezResponse paymentezResponse = callApiJSONObject(SERVER_URL + "/api/cc/verify/", paramsPost);

        PaymentezResponseDebitCard debitCard = new PaymentezResponseDebitCard();
        debitCard.setSuccess(paymentezResponse.isSuccess());
        debitCard.setCode(paymentezResponse.getCode());
        debitCard.setErrorMessage(paymentezResponse.getErrorMessage());

        if(paymentezResponse.getBodyJsonObject() != null) {

            try {
                debitCard.setStatus(paymentezResponse.getBodyJsonObject().getString("status"));
            } catch (JSONException e) {
            }
            try {
                String dtStart = paymentezResponse.getBodyJsonObject().getString("payment_date");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
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
                debitCard.setAmount(paymentezResponse.getBodyJsonObject().getDouble("amount"));
            } catch (JSONException e) {
            }
            try {
                debitCard.setTransactionId(paymentezResponse.getBodyJsonObject().getString("transaction_id"));
            } catch (JSONException e) {
            }
            try {
                debitCard.setStatusDetail(paymentezResponse.getBodyJsonObject().getString("status_detail"));
            } catch (JSONException e) {
            }
        }

        return debitCard;

    }

    /**
     * @param transactionId
     * @param uid
     * @param amount
     * @return
     */
    public PaymentezResponse verifyWithAmount(String transactionId, String uid, double amount) {

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


        PaymentezResponse paymentezResponse = callApiJSONObject(SERVER_URL + "/api/cc/verify/", paramsPost);

        return paymentezResponse;

    }


    public String getLocalIpAddress() {

        OkHttpClient client = new OkHttpClient();
        String url = SERVER_URL + "/api/cc/ip";

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }




        return "127.0.0.1";
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

        System.out.println("Vale:"+urltoken);

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

    private PaymentezResponse callApiJSONArray(String url, Map<String,String> params) {
        PaymentezResponse paymentezResponse = callApi(url, params);

        String json = paymentezResponse.getJson();
        JSONArray jObjArray;
        try {
            jObjArray = new JSONArray(json);
            paymentezResponse.setBodyJsonArray(jObjArray);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

        return paymentezResponse;




    }


    private OkHttpClient client;

    private PaymentezResponse callApi(String url, Map<String,String> params){
        PaymentezResponse paymentezResponse = new PaymentezResponse();

        String json = "";

        FormBody.Builder bodyParams = new FormBody.Builder();

        for (final Map.Entry<String, String> entrySet : params.entrySet()) {
            bodyParams.add(entrySet.getKey(), entrySet.getValue());
        }

        RequestBody formBody = bodyParams.build();

        client = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .build();


        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        Response response = null;


        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()){
                json = response.body().string();
                System.out.println(json);
                System.out.println("Unexpected code " + response);

                paymentezResponse.setCode(response.code());
                if(response.code()==200){
                    paymentezResponse.setSuccess(true);
                }else{
                    paymentezResponse.setErrorMessage(json);
                }
                paymentezResponse.setJson(json);


            }else{
                paymentezResponse.setCode(response.code());
                if(response.code()==200){
                    paymentezResponse.setSuccess(true);
                }else{
                    paymentezResponse.setErrorMessage(json);
                }
                json = response.body().string();

               paymentezResponse.setJson(json);

            }


            System.out.println(json);


        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage();
            if(errorMsg.contains("No address associated with hostname")){
                errorMsg = "Are you connected to the Internet?";
            }
            paymentezResponse.setErrorMessage(errorMsg);
        }




        return paymentezResponse;
    }

    private PaymentezResponse callApiJSONObject(String url, Map<String,String> params) {
        PaymentezResponse paymentezResponse = callApi(url, params);
        String json = paymentezResponse.getJson();
        JSONObject jObjArray;
        try {
            jObjArray = new JSONObject(json);
            paymentezResponse.setBodyJsonObject(jObjArray);
        } catch (Exception e) {
            if(json!=null)
                paymentezResponse.setErrorMessage(json);
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        return paymentezResponse;
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
