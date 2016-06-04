package com.paymentez.androidsdk.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mmucito on 30/05/16.
 */
public class PaymentezResponse {
    private boolean success = false;
    private String errorMessage;
    private int code;
    private JSONObject bodyJsonObject;
    private JSONArray bodyJsonArray;
    private String json;
    private String status;
    private String msg;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public JSONObject getBodyJsonObject() {
        return bodyJsonObject;
    }

    public void setBodyJsonObject(JSONObject bodyJsonObject) {
        this.bodyJsonObject = bodyJsonObject;
    }

    public JSONArray getBodyJsonArray() {
        return bodyJsonArray;
    }

    public void setBodyJsonArray(JSONArray bodyJsonArray) {
        this.bodyJsonArray = bodyJsonArray;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean shouldVerify(){
        if(getJson()!=null) {
            if (getJson().contains("verify_transaction")) {
                return true;
            }
        }
        return false;
    }

    public String getTransactionId(){
        String verify_transaction="";
        try {
            JSONArray jsonArray = getBodyJsonObject().getJSONArray("details");
            for(int i= 0; i < jsonArray.length(); i++){
                String jsonObject = jsonArray.getString(i);
                jsonObject = jsonObject.replace("\\\"","");

                Log.i("Mio", jsonObject);
                JSONObject jo = new JSONObject(jsonObject);

                try{
                    verify_transaction = jo.getString("verify_transaction");
                    if(!verify_transaction.equals("")){
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return verify_transaction;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
