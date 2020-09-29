package com.paymentez.android.util;

import android.util.Base64;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by mmucito on 14/09/17.
 */

public class PaymentezUtils {


    public static String SERVER_DEV_URL = "https://ccapi-stg.paymentez.com";
    public static String SERVER_PROD_URL = "https://ccapi.paymentez.com";

    private static String getUniqToken(String auth_timestamp, String paymentez_client_app_key) {
        String uniq_token_string = paymentez_client_app_key + auth_timestamp;
        return getHash(uniq_token_string);
    }

    public static String getAuthToken(String paymentez_client_app_code, String app_client_key) {
        Long tsLong = System.currentTimeMillis()/1000;
        String auth_timestamp = tsLong.toString();
        String string_auth_token = paymentez_client_app_code + ";" + auth_timestamp + ";" + getUniqToken(auth_timestamp, app_client_key);
        String auth_token = Base64.encodeToString(string_auth_token.getBytes(), Base64.NO_WRAP);
        return auth_token;
    }

    public static String getHash(String message) {
        String sha256hex = new String(Hex.encodeHex(DigestUtils.sha256(message)));
        return sha256hex;
    }


}
