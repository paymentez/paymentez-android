package com.paymentez.paymentezexample;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.paymentez.androidsdk.PaymentezSDKClient;
import com.paymentez.androidsdk.models.DebitCardResponseHandler;
import com.paymentez.androidsdk.models.ListCardsResponseHandler;
import com.paymentez.androidsdk.models.PaymentezCard;
import com.paymentez.androidsdk.models.PaymentezDebitParameters;
import com.paymentez.androidsdk.models.PaymentezResponse;
import com.paymentez.androidsdk.models.PaymentezResponseDebitCard;
import com.paymentez.androidsdk.models.PaymentezResponseListCards;
import com.paymentez.paymentezexample.utils.Constants;
import com.loopj.android.http.*;
import org.json.*;
import cz.msebera.android.httpclient.Header;

import java.util.ArrayList;

public class ListCardsActivity extends AppCompatActivity {

    PaymentezSDKClient paymentezsdk;
    final int CONTEXT_MENU_DEBIT_ITEM =1;
    final int CONTEXT_MENU_DELETE_ITEM =2;
    ListView listView;
    ArrayAdapter<String> listAdapter;
    ArrayList<PaymentezCard> listCard;
    String uid = "", email = "";

    EditText editTextUid;
    EditText editTextEmail;
    Button callApiListCards;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_cards);


        paymentezsdk = new PaymentezSDKClient(this, true, Constants.app_code, Constants.app_secret_key);


        listView = (ListView) findViewById(R.id.listView1);
        registerForContextMenu(listView);

        editTextUid = (EditText) findViewById(R.id.editTextUid);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);

        callApiListCards = (Button) findViewById(R.id.callApiListCards);
        callApiListCards.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                paymentezsdk.listCards(uid, new ListCardsResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, PaymentezResponseListCards response) {

                        System.out.println("SUCCESS: " + response.isSuccess());
                        System.out.println("CODE: " + response.getCode());

                        ArrayList<String> values = new ArrayList<>();

                        for (int i = 0; i < response.getCards().size(); i++) {

                            PaymentezCard card = response.getCards().get(i);
                            values.add("name:" + card.getCardHolder() + "\ncard_reference:" + card.getCardReference());

                            System.out.println("CARD INFO");
                            System.out.println(card.getCardHolder());
                            System.out.println(card.getCardReference());
                            System.out.println(card.getExpiryYear());
                            System.out.println(card.getTermination());
                            System.out.println(card.getExpiryMonth());
                            System.out.println(card.getType());
                        }
                    }


                });

            }
        });
    }


    public void getCards(){
        final ProgressDialog pd = new ProgressDialog(ListCardsActivity.this);
        pd.setMessage("");
        pd.show();
        paymentezsdk.listCards(uid, new ListCardsResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, PaymentezResponseListCards response) {
                pd.dismiss();
                System.out.println("SUCCESS: " + response.isSuccess());
                System.out.println("CODE: " + response.getCode());

                listCard = response.getCards();
                ArrayList<String> values = new ArrayList<>();

                for (int i = 0; i < response.getCards().size(); i++) {

                    PaymentezCard card = response.getCards().get(i);
                    values.add("name:" + card.getCardHolder() + "\ncard_reference:" + card.getCardReference());

                    System.out.println("CARD INFO");
                    System.out.println(card.getCardHolder());
                    System.out.println(card.getCardReference());
                    System.out.println(card.getExpiryYear());
                    System.out.println(card.getTermination());
                    System.out.println(card.getExpiryMonth());
                    System.out.println(card.getType());

                }


                listAdapter = new ArrayAdapter<String>(ListCardsActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, values);
                listView.setAdapter(listAdapter);
            }


        });
    }





    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.add(Menu.NONE, CONTEXT_MENU_DEBIT_ITEM, Menu.NONE, "Debit");
        menu.add(Menu.NONE, CONTEXT_MENU_DELETE_ITEM, Menu.NONE, "Delete");
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id = (int) listAdapter.getItemId(info.position);/*what item was selected is ListView*/
        PaymentezCard cardObject;
        cardObject = listCard.get(id);

        switch (item.getItemId()) {
            case CONTEXT_MENU_DEBIT_ITEM:
                PaymentezDebitParameters debitParameters = new PaymentezDebitParameters();

                debitParameters.setUid(uid);
                debitParameters.setEmail(email);
                debitParameters.setCardReference(cardObject.getCardReference());
                debitParameters.setProductAmount(5000);
                debitParameters.setProductDescription("test");
                debitParameters.setDevReference("prueba1");






                //Debit with shipping address
                /*
                PaymentezShipping shipping = new PaymentezShipping();
                shipping.setShipping_street("Av Jacutinga");
                shipping.setShipping_house_number("607");
                shipping.setShipping_city("São Paulo");
                shipping.setShipping_zip("99999-999");
                shipping.setShipping_state("SP");
                shipping.setShipping_country("BR");
                shipping.setShipping_district("");
                shipping.setShipping_additional_address_info("");

                debitParameters.setShipping(shipping);
                */

                final ProgressDialog pd = new ProgressDialog(ListCardsActivity.this);
                pd.setMessage("");
                pd.show();

paymentezsdk.debitCard(debitParameters, new DebitCardResponseHandler() {
    @Override
    public void onSuccess(int statusCode, Header[] headers, PaymentezResponseDebitCard paymentezResponse) {
        if(!paymentezResponse.isSuccess()){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ListCardsActivity.this);

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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ListCardsActivity.this);

                String message = "You must verify the transaction_id: " + paymentezResponse.getTransactionId();


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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ListCardsActivity.this);

                builder1.setMessage("status: " + paymentezResponse.getStatus() +
                        "\nstatus_detail: " + paymentezResponse.getStatusDetail() +
                        "\nshouldVerify: " + paymentezResponse.shouldVerify() +
                        "\ntransaction_id:" + paymentezResponse.getTransactionId());

                builder1.setCancelable(false);
                builder1.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();


                System.out.println("TRANSACTION INFO");
                System.out.println(paymentezResponse.getStatus());
                System.out.println(paymentezResponse.getPaymentDate());
                System.out.println(paymentezResponse.getAmount());
                System.out.println(paymentezResponse.getTransactionId());
                System.out.println(paymentezResponse.getStatusDetail());

                System.out.println("TRANSACTION card_data");
                System.out.println(paymentezResponse.getCardData().getAccountType());
                System.out.println(paymentezResponse.getCardData().getType());
                System.out.println(paymentezResponse.getCardData().getNumber());
                System.out.println(paymentezResponse.getCardData().getQuotas());

                System.out.println("TRANSACTION carrier_data");
                System.out.println(paymentezResponse.getCarrierData().getAuthorizationCode());
                System.out.println(paymentezResponse.getCarrierData().getAcquirerId());
                System.out.println(paymentezResponse.getCarrierData().getTerminalCode());
                System.out.println(paymentezResponse.getCarrierData().getUniqueCode());
            }
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject){
        try {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ListCardsActivity.this);

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



                return(true);
            case CONTEXT_MENU_DELETE_ITEM:

                final ProgressDialog pd2 = new ProgressDialog(ListCardsActivity.this);
                pd2.setMessage("");
                pd2.show();

paymentezsdk.deleteCard(uid, cardObject.getCardReference(), new TextHttpResponseHandler() {

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        pd2.dismiss();
        System.out.println("Failure: "+ responseString);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        pd2.dismiss();

        AlertDialog.Builder builder1 = new AlertDialog.Builder(ListCardsActivity.this);
        builder1.setMessage("Successfully Deleted!");
        builder1.setCancelable(false);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();

        getCards();
    }


});
                return(true);
        }


        return(super.onOptionsItemSelected(item));
    }
}
