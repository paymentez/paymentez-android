package com.paymentez.androidsdk.models;

/**
 * Created by mmucito on 03/06/16.
 */

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;

/**
 * Used to intercept and handle the responses from requests made using {@link AsyncHttpClient}, with
 * automatic parsing into a {@link JSONObject} or {@link JSONArray}. <p>&nbsp;</p> This class is
 * designed to be passed to get, post, put and delete requests with the {@link ***REMOVED***onSuccess(int,
 * Header[], JSONArray)} or {@link ***REMOVED***onSuccess(int,
 * Header[], JSONObject)} methods anonymously overridden. <p>&nbsp;</p>
 * Additionally, you can override the other event methods from the parent class.
 */
public class PaymentezResponseHandler extends TextHttpResponseHandler {

    private static final String LOG_TAG = "Presponse";
    private boolean useRFC5179CompatibilityMode = true;

    /**
     * Creates new JsonHttpResponseHandler, with JSON String encoding UTF-8
     */
    public PaymentezResponseHandler() {
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


        PaymentezResponse paymentezResponse = new PaymentezResponse();
        paymentezResponse.setCode(200);
        paymentezResponse.setSuccess(true);
        try {
            paymentezResponse.setStatus(response.getString("status"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            paymentezResponse.setMsg(response.getString("msg"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        onSuccess(statusCode, headers, paymentezResponse);
    }



    /**
     * Returns when request succeeds
     *
     * @param statusCode http response status line
     * @param headers    response headers if any
     * @param response   parsed response if any
     */
    public void onSuccess(int statusCode, Header[] headers, PaymentezResponse response) {
        AsyncHttpClient.log.w(LOG_TAG, "onSuccess(int, Header[], PaymentezResponseDebitCard) was not overriden, but callback was received");
    }





    /**
     * Creates new JsonHttpResponseHandler with given JSON String encoding
     *
     * @param encoding String encoding to be used when parsing JSON
     */
    public PaymentezResponseHandler(String encoding) {
        super(encoding);
    }

    /**
     * Creates new JsonHttpResponseHandler with JSON String encoding UTF-8 and given RFC5179CompatibilityMode
     *
     * @param useRFC5179CompatibilityMode Boolean mode to use RFC5179 or latest
     */
    public PaymentezResponseHandler(boolean useRFC5179CompatibilityMode) {
        super(DEFAULT_CHARSET);
        this.useRFC5179CompatibilityMode = useRFC5179CompatibilityMode;
    }

    /**
     * Creates new JsonHttpResponseHandler with given JSON String encoding and RFC5179CompatibilityMode
     *
     * @param encoding                    String encoding to be used when parsing JSON
     * @param useRFC5179CompatibilityMode Boolean mode to use RFC5179 or latest
     */
    public PaymentezResponseHandler(String encoding, boolean useRFC5179CompatibilityMode) {
        super(encoding);
        this.useRFC5179CompatibilityMode = useRFC5179CompatibilityMode;
    }



    /**
     * Returns when request succeeds
     *
     * @param statusCode http response status line
     * @param headers    response headers if any
     * @param response   parsed response if any
     */
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        AsyncHttpClient.log.w(LOG_TAG, "onSuccess(int, Header[], JSONArray) was not overriden, but callback was received");
    }

    /**
     * Returns when request failed
     *
     * @param statusCode    http response status line
     * @param headers       response headers if any
     * @param throwable     throwable describing the way request failed
     * @param errorResponse parsed response if any
     */
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        AsyncHttpClient.log.w(LOG_TAG, "onFailure(int, Header[], Throwable, JSONObject) was not overriden, but callback was received", throwable);
    }

    /**
     * Returns when request failed
     *
     * @param statusCode    http response status line
     * @param headers       response headers if any
     * @param throwable     throwable describing the way request failed
     * @param errorResponse parsed response if any
     */
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        AsyncHttpClient.log.w(LOG_TAG, "onFailure(int, Header[], Throwable, JSONArray) was not overriden, but callback was received", throwable);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        AsyncHttpClient.log.w(LOG_TAG, "onFailure(int, Header[], String, Throwable) was not overriden, but callback was received", throwable);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        AsyncHttpClient.log.w(LOG_TAG, "onSuccess(int, Header[], String) was not overriden, but callback was received");
    }

    @Override
    public final void onSuccess(final int statusCode, final Header[] headers, final byte[] responseBytes) {
        if (statusCode != HttpStatus.SC_NO_CONTENT) {
            Runnable parser = new Runnable() {
                @Override
                public void run() {
                    try {
                        final Object jsonResponse = parseResponse(responseBytes);
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                // In RFC5179 a null value is not a valid JSON
                                if (!useRFC5179CompatibilityMode && jsonResponse == null) {
                                    onSuccess(statusCode, headers, (String) null);
                                } else if (jsonResponse instanceof JSONObject) {
                                    onSuccess(statusCode, headers, (JSONObject) jsonResponse);
                                } else if (jsonResponse instanceof JSONArray) {
                                    onSuccess(statusCode, headers, (JSONArray) jsonResponse);
                                } else if (jsonResponse instanceof String) {
                                    // In RFC5179 a simple string value is not a valid JSON
                                    if (useRFC5179CompatibilityMode) {
                                        onFailure(statusCode, headers, (String) jsonResponse, new JSONException("Response cannot be parsed as JSON data"));
                                    } else {
                                        onSuccess(statusCode, headers, (String) jsonResponse);
                                    }
                                } else {
                                    onFailure(statusCode, headers, new JSONException("Unexpected response type " + jsonResponse.getClass().getName()), (JSONObject) null);
                                }
                            }
                        });
                    } catch (final JSONException ex) {
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                onFailure(statusCode, headers, ex, (JSONObject) null);
                            }
                        });
                    }
                }
            };
            if (!getUseSynchronousMode() && !getUsePoolThread()) {
                new Thread(parser).start();
            } else {
                // In synchronous mode everything should be run on one thread
                parser.run();
            }
        } else {
            onSuccess(statusCode, headers, new JSONObject());
        }
    }

    @Override
    public final void onFailure(final int statusCode, final Header[] headers, final byte[] responseBytes, final Throwable throwable) {
        if (responseBytes != null) {
            Runnable parser = new Runnable() {
                @Override
                public void run() {
                    try {
                        final Object jsonResponse = parseResponse(responseBytes);


                        postRunnable(new Runnable() {
                            @Override
                            public void run() {


                                String jsonString = getResponseString(responseBytes, getCharset());
                                if(jsonString.contains("verify_transaction")){
                                    final PaymentezResponse paymentezResponse = new PaymentezResponse();

                                    paymentezResponse.setCode(200);
                                    paymentezResponse.setSuccess(true);
                                    paymentezResponse.setStatus("failure");
                                    paymentezResponse.setMsg(jsonString);
                                    paymentezResponse.setJson(jsonString);
                                    paymentezResponse.setBodyJsonObject((JSONObject) jsonResponse);



                                    onSuccess(statusCode, headers, paymentezResponse);

                                }else if (!useRFC5179CompatibilityMode && jsonResponse == null) {
                                    onFailure(statusCode, headers, (String) null, throwable);
                                } else if (jsonResponse instanceof JSONObject) {
                                    onFailure(statusCode, headers, throwable, (JSONObject) jsonResponse);
                                } else if (jsonResponse instanceof JSONArray) {
                                    onFailure(statusCode, headers, throwable, (JSONArray) jsonResponse);
                                } else if (jsonResponse instanceof String) {
                                    onFailure(statusCode, headers, (String) jsonResponse, throwable);
                                } else {
                                    onFailure(statusCode, headers, new JSONException("Unexpected response type " + jsonResponse.getClass().getName()), (JSONObject) null);
                                }
                            }
                        });

                    } catch (final JSONException ex) {
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                onFailure(statusCode, headers, ex, (JSONObject) null);
                            }
                        });

                    }
                }
            };
            if (!getUseSynchronousMode() && !getUsePoolThread()) {
                new Thread(parser).start();
            } else {
                // In synchronous mode everything should be run on one thread
                parser.run();
            }
        } else {
            AsyncHttpClient.log.v(LOG_TAG, "response body is null, calling onFailure(Throwable, JSONObject)");
            onFailure(statusCode, headers, throwable, (JSONObject) null);
        }
    }

    /**
     * Returns Object of type {@link JSONObject}, {@link JSONArray}, String, Boolean, Integer, Long,
     * Double or {@link JSONObject***REMOVED***NULL}, see {@link org.json.JSONTokener***REMOVED***nextValue()}
     *
     * @param responseBody response bytes to be assembled in String and parsed as JSON
     * @return Object parsedResponse
     * @throws org.json.JSONException exception if thrown while parsing JSON
     */
    protected Object parseResponse(byte[] responseBody) throws JSONException {
        if (null == responseBody)
            return null;
        Object result = null;
        //trim the string to prevent start with blank, and test if the string is valid JSON, because the parser don't do this :(. If JSON is not valid this will return null
        String jsonString = getResponseString(responseBody, getCharset());
        if (jsonString != null) {
            jsonString = jsonString.trim();
            if (useRFC5179CompatibilityMode) {
                if (jsonString.startsWith("{") || jsonString.startsWith("[")) {
                    result = new JSONTokener(jsonString).nextValue();
                }
            } else {
                // Check if the string is an JSONObject style {} or JSONArray style []
                // If not we consider this as a string
                if ((jsonString.startsWith("{") && jsonString.endsWith("}"))
                        || jsonString.startsWith("[") && jsonString.endsWith("]")) {
                    result = new JSONTokener(jsonString).nextValue();
                }
                // Check if this is a String "my String value" and remove quote
                // Other value type (numerical, boolean) should be without quote
                else if (jsonString.startsWith("\"") && jsonString.endsWith("\"")) {
                    result = jsonString.substring(1, jsonString.length() - 1);
                }
            }
        }
        if (result == null) {
            result = jsonString;
        }
        return result;
    }

    public boolean isUseRFC5179CompatibilityMode() {
        return useRFC5179CompatibilityMode;
    }

    public void setUseRFC5179CompatibilityMode(boolean useRFC5179CompatibilityMode) {
        this.useRFC5179CompatibilityMode = useRFC5179CompatibilityMode;
    }





}
