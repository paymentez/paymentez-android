ccapi-lib-android
=============

PaymentezCCSDK is a library that allows developers to easily connect to the Paymentez CREDITCARDS API


Setup
-----
* In Eclipse, just import the library as an Android library project. Project > Clean to generate the binaries 
you need, like R.java, etc.
* Then, just add PaymentezCCSDK as a dependency to your existing project and you're good to go!


Minimum Requirements
-----
The following minimum requirements are needed to utilize the Android Device Collector:
* Min SDK API Level - 8
* Application with the following permissions in the Android Manifest File:
```xml
<manifest ...>
. . .
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
. . .
</manifest>
The <uses-permission> elements must be children of the <manifest> element.
NOTE: A minimum requirement is what is required to run the software successfully.
• To collect Geo Location and Device ID information (which is on by default), request the following
permissions in the manifest:
<manifest ...>
<!-- This is will enhance the device ID -->
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<!-- pick one of these two for Geo Location (FINE is preferred) -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
</manifest>
```

Example
-----
Try out the sample application
https://www.dropbox.com/s/fl9wrn9d6z1pxat/PaymentExample.apk

You can found the Code in /PaymentExample/

Usage
-----
```java
public class ExampleActivity extends Activity{
	PaymentezCCSDK paymentezsdk;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//get sessionId for kount
		String session_id = PaymentezCCSDK.getSessionId();
		System.out.println("session_id:"+session_id);
				
				
		//init library
		paymentezsdk = new PaymentezCCSDK(this, true, "ST-MX", "vgVfq0kLZveGIdD9ljGjPtt6ieYtIQ","PREPAID", "Ere68ttPklFTn89xZIhFYcqC5X8HX3Ob5qgbEkfjNfCLkud3wY");		
		
		
		//get Payment URL
		String payment_url = paymentezsdk.cardAdd("1234", "martin.mucito@gmail.com");
		System.out.println("payment_url:"+payment_url);
		
		
		
		//To list the cards related to a user
		new CallApiListCardAsyncTask().execute("1234");
		
		//To debit a user saved card
		new CallApiDebitCardAsyncTask().execute( "1234", "martin.mucito@gmail.com", "3764462951159115974", "10.0", "test", "1234567");
		//new CallApiDebitCardAsyncTask().execute( "uid", "email", "card_reference", "product_amount", "product_description", "dev_reference");
		
		
		//To delete a user saved card
		new CallApiDeleteCardAsyncTask().execute( "1234", "17974018243686635624");
	}

	private class CallApiDebitCardAsyncTask extends AsyncTask<String, Void, JSONObject>{

		@Override
		protected JSONObject doInBackground(String... params) {
			String uid = params[0];
			String email = params[1];
			String card_reference = params[2];
			String product_amount = params[3];
			String product_description = params[4];
			String dev_reference = params[5];
			
			
			return paymentezsdk.cardDebit(uid, email, card_reference, product_amount, product_description, dev_reference);
		}
		
		
		protected void onPostExecute(JSONObject json) {
			super.onPostExecute(json);
			
			System.out.println("TRANSACTION INFO");
			try {
				System.out.println(json.getString("status"));
				System.out.println(json.getString("payment_date"));
				System.out.println(json.getDouble("amount"));
				System.out.println(json.getString("transaction_id"));
				System.out.println(json.getString("status_detail"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	private class CallApiDeleteCardAsyncTask extends AsyncTask<String, Void, Boolean>{

		@Override
		protected Boolean doInBackground(String... params) {
			String uid = params[0];
			String card_reference = params[1];
			
			
			return paymentezsdk.cardDelete(uid, card_reference);
		}
		
		
		protected void onPostExecute(Boolean json) {
			super.onPostExecute(json);
			
			System.out.println("DELETE INFO");
			
			System.out.println(json);
				
			
		}
		
	}
	
	
	
	private class CallApiListCardAsyncTask extends AsyncTask<String, Void, JSONArray>{

		@Override
		protected JSONArray doInBackground(String... params) {
			String uid = params[0];
			return paymentezsdk.cardList(uid);
		}
		
		
		protected void onPostExecute(JSONArray json) {
			super.onPostExecute(json);
			for (int i = 0; i < json.length(); i++) {
				JSONObject cardObject;
				try {
					cardObject = json.getJSONObject(i);	
					System.out.println("CARD INFO");
					System.out.println(cardObject.get("name"));
					System.out.println(cardObject.get("card_reference"));
					System.out.println(cardObject.get("expiry_year"));
					System.out.println(cardObject.get("termination"));
					System.out.println(cardObject.get("expiry_month"));
					System.out.println(cardObject.get("type"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}
}
```