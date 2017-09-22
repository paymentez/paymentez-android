package com.paymentez.examplestore.rest;

import com.paymentez.examplestore.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mmucito on 13/09/17.
 */

public class RetrofitFactory {

    private static Retrofit retrofit = null;
    static OkHttpClient.Builder builder = new OkHttpClient().newBuilder();

    public static Retrofit getClient() {
        if (retrofit==null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
            builder.connectTimeout(3, TimeUnit.MINUTES);
            builder.readTimeout(3, TimeUnit.MINUTES);
            builder.writeTimeout(3, TimeUnit.MINUTES);

            OkHttpClient client = builder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BACKEND_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
