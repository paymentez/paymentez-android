package com.paymentez.examplestore.utils;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

/**
 * Created by mmucito on 22/09/17.
 */

public class Alert {
    public static void show(Context mContext, String title, String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setTitle(title);
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
