Paymentez Android SDK
===================


 Paymentez Android SDK is a library that allows developers to easily connect to the Paymentez CREDITCARDS API

----------

***REMOVED******REMOVED***Installation

***REMOVED******REMOVED******REMOVED***Android Studio

Add this line to your app's `build.gradle` inside the `dependencies` section:

    compile 'com.paymentez.paymentezsdk:paymentezsdk:1.1.0'


***REMOVED******REMOVED******REMOVED*** ProGuard

If you're planning on optimizing your app with ProGuard, make sure that you exclude the Paymentez bindings. You can do this by adding the following to your app's `proguard.cfg` file:

    -keep class com.paymentez.android.** { *; }

***REMOVED******REMOVED*** Usage

***REMOVED******REMOVED******REMOVED*** Using the CardMultilineWidget

You can add a widget to your apps that easily handles the UI states for collecting card data.

First, add the CardMultilineWidget to your layout.

```xml
<com.paymentez.android.view.CardMultilineWidget
        android:id="@+id/card_multiline_widget"
        android:layout_alignParentTop="true"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
```

Note: a `CardMultiline` widget can only be added in the view of an `Activity` whose `Theme` descends from an `AppCompat` theme.

You can customize the view with this tags:

```xml
app:shouldShowPostalCode="true"
app:shouldShowPaymentezLogo="true"
app:shouldShowCardHolderName="true"
app:shouldShowScanCard="true"
```

In order to use any of this tags, you'll need to enable the app XML namespace somewhere in the layout.

```xml
xmlns:app="http://schemas.android.com/apk/res-auto"
```

To get a `Card` object from the `CardMultilineWidget`, you ask the widget for its card.

```java
Card cardToSave = cardWidget.getCard();
if (cardToSave == null) {
    Alert.show(mContext,
        "Error",
        "Invalid Card Data");
    return;
}
```

If the returned `Card` is null, error states will show on the fields that need to be fixed. 

Once you have a non-null `Card` object from either widget, you can call [createToken](***REMOVED***createtoken).

***REMOVED******REMOVED******REMOVED*** Init library


```java
// @param mContext Context of the Main Activity
// @param dev_environment false to use production environment
// @param app_code Application identifier (provided by Paymentez).
// @param app_secret_key Application Secret key (provided by Paymentez).
Paymentez paymentez = new Paymentez(mContext, Constants.PAYMENTEZ_IS_DEV_ENVIRONMENT, Constants.PAYMENTEZ_APP_CODE, Constants.PAYMENTEZ_APP_SECRET_KEY);
```

***REMOVED******REMOVED******REMOVED*** createToken

createToken converts sensitive card data to a single-use token which you can safely pass to your server to charge the user. 

```java
paymentez.createToken(uid, email, cardToSave, new TokenCallback() {

    public void onSuccess(Card card) {
        
        if(card!=null){
            if(card.getStatus().equals("valid")){
                Alert.show(mContext,
                        "Card Successfully Added",
                        "status: " + card.getStatus() + "\n" +
                                "Card Token: " + card.getToken() + "\n" +
                                "transaction_reference: " + card.getTransactionReference());

            }else if (card.getStatus().equals("review")) {
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
        Alert.show(mContext,
                "Error",
                "Type: " + error.getType() + "\n" +
                        "Help: " + error.getHelp() + "\n" +
                        "Description: " + error.getDescription());

        //TODO: Handle error
    }

});
```

The first argument to createToken is uid (String).
+ uid Customer identifier. This is the identifier you use inside your application; you will receive it in notifications.

The second argument to createToken is email (String).
+ email Email of the customer

The third argument to createToken is a Card object. A Card contains the following fields:

+ number: card number as a string without any separators, e.g. '4242424242424242'.
+ holderName: cardholder name.
+ expMonth: integer representing the card's expiration month, e.g. 12.
+ expYear: integer representing the card's expiration year, e.g. 2013.
+ cvc: card security code as a string, e.g. '123'.
+ type: 

The fourth argument tokenCallback is a callback you provide to handle responses from Paymentez.
It should send the token to your server for processing onSuccess, and notify the user onError.

Here's a sample implementation of the token callback:
```java
paymentez.createToken(
    uid, email, cardToSave,
    new TokenCallback() {
        public void onSuccess(Card card) {
            // Send token to your own web service
            MyServer.chargeToken(card.getToken());
        }
        public void onError(PaymentezError error) {
            Toast.makeText(getContext(),
                error.getDescription(),
                Toast.LENGTH_LONG).show();
        }
    }
);
```

`createToken` is an asynchronous call – it returns immediately and invokes the callback on the UI thread when it receives a response from Paymentez's servers.

***REMOVED******REMOVED******REMOVED*** Client-side validation helpers

The Card object allows you to validate user input before you send the information to Paymentez.

***REMOVED******REMOVED******REMOVED******REMOVED*** validateNumber

Checks that the number is formatted correctly and passes the [Luhn check](http://en.wikipedia.org/wiki/Luhn_algorithm).

***REMOVED******REMOVED******REMOVED******REMOVED*** validateExpiryDate

Checks whether or not the expiration date represents an actual month in the future.

***REMOVED******REMOVED******REMOVED******REMOVED*** validateCVC

Checks whether or not the supplied number could be a valid verification code.

***REMOVED******REMOVED******REMOVED******REMOVED*** validateCard

Convenience method to validate card number, expiry date and CVC.

***REMOVED******REMOVED*** Example apps

There are 1 example app included in the repository:

- PaymentezStore project is a full walk-through of building a shop activity, including connecting to a back end.

http://d20omjwo1khove.cloudfront.net/ccapi/paymentez-store-v1.0.apk

To build and run the example app, clone the repository and open the project.

***REMOVED******REMOVED******REMOVED*** Getting started with the Android example app

Note: both example apps require an [Android SDK](https://developer.android.com/studio/index.html) and [Gradle](https://gradle.org/) to build and run.


***REMOVED******REMOVED******REMOVED*** Building and Running the PaymentezStore

Before you can run the PaymentezStore application, you need to provide it with your APP_CODE, APP_SECRET_KEY and a sample backend.

1. If you haven't already and APP_CODE and APP_SECRET_KEY, please ask your contact on Paymentez Team for it.
2. Replace the `PAYMENTEZ_APP_CODE` and `PAYMENTEZ_APP_SECRET_KEY` constants in Constants.java with your own Paymentez Client Credentials.
3. Head to https://github.com/paymentez/example-java-backend and click "Deploy to Heroku" (you may have to sign up for a Heroku account as part of this process). Provide your Paymentez Server Credentials APP_CODE and  APP_SECRET_KEY fields under 'Env'. Click "Deploy for Free".
4. Replace the `BACKEND_URL` variable in the Constants.java file with the app URL Heroku provides you with (e.g. "https://my-example-app.herokuapp.com")

