package com.paymentez.androidsdk.utils;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by mmucito on 24/05/16.
 */
public class LoggingInterceptor implements Interceptor {
    @Override public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        Log.i("LoggingInterceptor",String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), bodyToString(request)));

        Response response = chain.proceed(request);


        String body = response.body().string();
        String bodyError = body + "/n code: " + response.code();
        MediaType contentType = response.body().contentType();

        long t2 = System.nanoTime();
        Log.i("LoggingInterceptor",String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, bodyError));

        ResponseBody newBody = ResponseBody.create(contentType, body);
        return response.newBuilder().body(newBody).build();
    }


    private static String bodyToString(final Request request){

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
