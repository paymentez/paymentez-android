package com.paymentez.android.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a JSON model used in the Paymentez Api.
 */
public abstract class PaymentezJsonModel {

    @NonNull
    public abstract Map<String, Object> toMap();

    @NonNull
    public abstract JSONObject toJson();

    @Override
    public String toString() {
        return this.toJson().toString();
    }

    static void putPaymentezJsonModelMapIfNotNull(
        @NonNull Map<String, Object> upperLevelMap,
        @NonNull @Size(min = 1) String key,
        @Nullable PaymentezJsonModel jsonModel){
        if (jsonModel == null) {
            return;
        }
        upperLevelMap.put(key, jsonModel.toMap());
    }


    static void putPaymentezJsonModelIfNotNull(
            @NonNull JSONObject jsonObject,
            @NonNull @Size(min = 1) String key,
            @Nullable PaymentezJsonModel jsonModel) {
        if (jsonModel == null) {
            return;
        }

        try {
            jsonObject.put(key, jsonModel.toJson());
        } catch (JSONException ignored) {}
    }

    static void putPaymentezJsonModelListIfNotNull(
            @NonNull Map<String, Object> upperLevelMap,
            @NonNull @Size(min = 1) String key,
            @Nullable List<? extends PaymentezJsonModel> jsonModelList) {
        if (jsonModelList == null) {
            return;
        }

        List<Map<String, Object>> mapList = new ArrayList<>();
        for (int i = 0; i < jsonModelList.size(); i++) {
            mapList.add(jsonModelList.get(i).toMap());
        }
        upperLevelMap.put(key, mapList);
    }


    static void putPaymentezJsonModelListIfNotNull(
            @NonNull JSONObject jsonObject,
            @NonNull @Size(min = 1) String key,
            @Nullable List<? extends PaymentezJsonModel> jsonModelList) {
        if (jsonModelList == null) {
            return;
        }

        try {
            JSONArray array = new JSONArray();
            for (PaymentezJsonModel model : jsonModelList) {
                array.put(model.toJson());
            }
            jsonObject.put(key, array);
        } catch (JSONException ignored) {}
    }
}
