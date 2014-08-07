package com.example;



import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.devicecollector.DeviceCollector;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.devicecollector.DeviceCollector;
import com.devicecollector.DeviceCollector.ErrorCode;
import com.paymentez.api.PaymentezCCSDK;
import com.paymentez.api.R;
import com.paymentez.api.R.layout;
import com.paymentez.api.R.menu;

import java.util.Date;
import java.util.UUID;

public class ExampleActivity extends Activity{
	PaymentezCCSDK paymentezsdk;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
				
				
		//init library
		paymentezsdk = new PaymentezCCSDK(this, true, "BOHRA", "4JUGvENk5ztccCFrIKFNpZzOR9dJMW","PREPAID", "Ere68ttPklFTn89xZIhFYcqC5X8HX3Ob5qgbEkfjNfCLkud3wY");		
		
		
		
		System.out.println("session_id:"+paymentezsdk.sessionId);
				
		
		//get Payment URL
		String payment_url = paymentezsdk.cardAdd("1234", "martin.mucito@gmail.com");
		System.out.println("payment_url:"+payment_url);
		//you must enable JavaScript for your WebView. 
		//example:
		//final WebView webView1 = (WebView) findViewById(R.id.webView1);
		//WebSettings webSettings = webView1.getSettings();
		//webSettings.setJavaScriptEnabled(true);
		//webView1.loadUrl(payment_url);
		
		
		
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
