package com.paymentez.examplestore;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.paymentez.android.Paymentez;
import com.paymentez.android.model.Card;
import com.paymentez.android.rest.TokenCallback;
import com.paymentez.android.rest.model.PaymentezError;
import com.paymentez.android.view.CardMultilineWidget;
import com.paymentez.examplestore.utils.Alert;
import com.paymentez.examplestore.utils.Constants;


public class AddCardActivity extends AppCompatActivity {

    Button buttonNext;
    CardMultilineWidget cardWidget;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        mContext = this;
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }




        final String uid = Constants.USER_ID;
        final String email = Constants.USER_EMAIL;

        cardWidget = (CardMultilineWidget) findViewById(R.id.card_multiline_widget);
        buttonNext = (Button) findViewById(R.id.buttonAddCard);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonNext.setEnabled(false);

                Card cardToSave = cardWidget.getCard();
                if (cardToSave == null) {
                    buttonNext.setEnabled(true);
                    Alert.show(mContext,
                            "Error",
                            "Invalid Card Data");
                    return;
                }else{
                    final ProgressDialog pd = new ProgressDialog(AddCardActivity.this);
                    pd.setMessage("");
                    pd.show();

                    Paymentez.addCard(mContext, uid, email, cardToSave, new TokenCallback() {

                        public void onSuccess(Card card) {
                            buttonNext.setEnabled(true);
                            pd.dismiss();
                            if(card != null){
                                if(card.getStatus().equals("valid")){
                                    Alert.show(mContext,
                                            "Card Successfully Added",
                                            "status: " + card.getStatus() + "\n" +
                                                    "Card Token: " + card.getToken() + "\n" +
                                                    "transaction_reference: " + card.getTransactionReference());

                                } else if (card.getStatus().equals("review")) {
                                    Alert.show(mContext,
                                            "Card Under Review",
                                            "status: " + card.getStatus() + "\n" +
                                                    "Card Token: " + card.getToken() + "\n" +
                                                    "transaction_reference: " + card.getTransactionReference());

                                } else {
                                    Alert.show(mContext,
                                            "Error",
                                            "status: " + card.getStatus() + "\n" +
                                                    "message: " + card.getMessage());
                                }


                            }

                            //TODO: Create charge or Save Token to your backend
                        }

                        public void onError(PaymentezError error) {
                            buttonNext.setEnabled(true);
                            pd.dismiss();
                            Alert.show(mContext,
                                    "Error",
                                    "Type: " + error.getType() + "\n" +
                                            "Help: " + error.getHelp() + "\n" +
                                            "Description: " + error.getDescription());

                            //TODO: Handle error
                        }

                    });

                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
