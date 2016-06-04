package com.paymentez.paymentezexample;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.paymentez.androidsdk.PaymentezSDKClient;
import com.paymentez.androidsdk.models.DebitCardResponseHandler;
import com.paymentez.androidsdk.models.PaymentezCard;
import com.paymentez.androidsdk.models.PaymentezResponse;
import com.paymentez.androidsdk.models.PaymentezResponseDebitCard;
import com.paymentez.androidsdk.models.PaymentezResponseHandler;
import com.paymentez.paymentezexample.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class AddCardActivity extends AppCompatActivity {

    PaymentezSDKClient paymentezsdk;


    EditText editTextUid;
    EditText editTextEmail;
    Button callApiAddWebView;
    Button callApiAddPci;
    Button callScanCard;

    PaymentezCard paymentezCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        paymentezsdk = new PaymentezSDKClient(this, true, Constants.app_code, Constants.app_secret_key);

        editTextUid = (EditText) findViewById(R.id.editTextUid);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);

        callApiAddWebView = (Button) findViewById(R.id.callApiAddWebView);
        callApiAddWebView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(editTextUid.getText().toString().equals("") || editTextEmail.getText().toString().equals("")){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(AddCardActivity.this);
                    builder1.setMessage("all fields are required");

                    builder1.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }else {
                    paymentezsdk.addCardShowWebView(editTextUid.getText().toString(), editTextEmail.getText().toString(), AddCardActivity.this);

                }

            }
        });



        callScanCard = (Button) findViewById(R.id.callScanCard);
        callScanCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                paymentezsdk.scanCard();

            }
        });



        callApiAddPci = (Button) findViewById(R.id.callApiAddPci);
        callApiAddPci.setEnabled(false);
        callApiAddPci.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(editTextUid.getText().toString().equals("") || editTextEmail.getText().toString().equals("")){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(AddCardActivity.this);
                    builder1.setMessage("all fields are required");

                    builder1.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }else {
                    if(paymentezCard!=null){

                        paymentezCard.setUid(editTextUid.getText().toString());
                        paymentezCard.setEmail(editTextEmail.getText().toString());


                        final ProgressDialog pd = new ProgressDialog(AddCardActivity.this);
                        pd.setMessage("");
                        pd.show();

                        paymentezsdk.addCard(paymentezCard, new PaymentezResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, PaymentezResponse paymentezResponse) {
                                pd.dismiss();
                                if(!paymentezResponse.isSuccess()){
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(AddCardActivity.this);

                                    builder1.setMessage("Error: " + paymentezResponse.getErrorMessage());

                                    builder1.setCancelable(false);
                                    builder1.setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();

                                }else {
                                    if(paymentezResponse.getStatus().equals("failure") && paymentezResponse.shouldVerify()){
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(AddCardActivity.this);

                                        String message = "You must verify the transaction_id: " + paymentezResponse.getTransactionId();

                                        Log.i("Deba", message);

                                        builder1.setMessage(message);

                                        builder1.setCancelable(false);
                                        builder1.setPositiveButton("OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });
                                        AlertDialog alert11 = builder1.create();
                                        alert11.show();
                                    }else {
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(AddCardActivity.this);

                                        String message = "status: " + paymentezResponse.getStatus() +
                                                "\nmsg: " + paymentezResponse.getMsg() +
                                                "\nshouldVerify: " + paymentezResponse.shouldVerify() +
                                                "\ntransaction_id: " + paymentezResponse.getTransactionId();


                                        builder1.setMessage(message);

                                        builder1.setCancelable(false);
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

                            }


                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject){
                                pd.dismiss();
                                try {
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(AddCardActivity.this);

                                    builder1.setMessage(jsonObject.toString(4));

                                    builder1.setCancelable(false);
                                    builder1.setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }



                        });




                    }


                }

            }
        });


    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PaymentezSDKClient.PAYMENTEZ_SCAN_CARD_REQUEST_CODE) {

            if (data != null && data.hasExtra(PaymentezSDKClient.PAYMENTEZ_EXTRA_SCAN_RESULT)) {


                paymentezCard = data.getParcelableExtra(PaymentezSDKClient.PAYMENTEZ_EXTRA_SCAN_RESULT);

                if(paymentezCard!=null) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(AddCardActivity.this);

                    String message = "card: " + paymentezCard.getCardNumber() +
                            "\ncard_holder: " + paymentezCard.getCardHolder();

                    builder1.setMessage(message);

                    builder1.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                    callApiAddPci.setEnabled(true);
                }



            }
            else {


                AlertDialog.Builder builder1 = new AlertDialog.Builder(AddCardActivity.this);
                builder1.setMessage("Scan was canceled.");

                builder1.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);

        }
        // else handle other activity results
    }



}
