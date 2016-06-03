package com.paymentez.androidsdk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.paymentez.androidsdk.models.PaymentezCard;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class ScanCardActivity extends AppCompatActivity {

    private int MY_SCAN_REQUEST_CODE = 10344;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_card);




        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true);

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            PaymentezCard paymentezCard = null;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";

                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                }



                if (scanResult.getCardType().getDisplayName(null) != null) {
                    resultDisplayStr += "Card Type: " + scanResult.getCardType().getDisplayName(null) + "\n";
                }





                String typeCard;
                switch(scanResult.getCardType().getDisplayName(null)){
                    case "AmEx":
                        typeCard = "ax";
                        break;
                    case "MasterCard":
                        typeCard = "mc";
                        break;
                    case "Visa":
                        typeCard = "vi";
                        break;
                    case "DinersClub":
                        typeCard = "di";
                        break;
                    default:
                        typeCard = "Unknown";
                        break;
                }

                paymentezCard = new PaymentezCard();
                paymentezCard.setType(typeCard);
                paymentezCard.setExpiryYear(String.valueOf(scanResult.expiryYear));
                paymentezCard.setExpiryMonth(String.valueOf(scanResult.expiryMonth));
                paymentezCard.setCardHolder(scanResult.cardholderName);
                paymentezCard.setCardNumber(scanResult.cardNumber);
                paymentezCard.setCvc(scanResult.cvv);

                data.putExtra(PaymentezSDKClient.PAYMENTEZ_EXTRA_SCAN_RESULT,paymentezCard);



            }
            else {
                resultDisplayStr = "Scan was canceled.";
            }








            setResult(PaymentezSDKClient.PAYMENTEZ_SCAN_CARD_REQUEST_CODE, data);
            finish();

        }
        // else handle other activity results
    }
}
