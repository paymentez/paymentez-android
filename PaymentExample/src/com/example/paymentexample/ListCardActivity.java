package com.example.paymentexample;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.paymentez.api.PaymentezCCSDK;
import com.paymentez.api.Shipping;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ListCardActivity extends Activity {
	PaymentezCCSDK paymentezsdk;
	final int CONTEXT_MENU_DEBIT_ITEM =1;
	 final int CONTEXT_MENU_DELETE_ITEM =2;
	 ListView listView;
	 ArrayAdapter<String> listadapter;
	 JSONArray listCard;
	 String uid, email;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listcard);
		listView = (ListView) findViewById(R.id.listView1);
		registerForContextMenu(listView);
		
		final EditText editText1 = (EditText) findViewById(R.id.editText1);
		final EditText editText2 = (EditText) findViewById(R.id.editText2);
		

		paymentezsdk = new PaymentezCCSDK(this, true, "BOHRA", "4JUGvENk5ztccCFrIKFNpZzOR9dJMW","PREPAID", "Ere68ttPklFTn89xZIhFYcqC5X8HX3Ob5qgbEkfjNfCLkud3wY"); 

		
		final Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	uid = editText1.getText().toString();
            	email = editText2.getText().toString();
            	new CallApiListCardAsyncTask().execute(uid);
            }
        });
		
		
	}
	
	private class CallApiDebitCardAsyncTask extends AsyncTask<String, Void, JSONObject>{
		ProgressDialog pd;
		@Override
		protected JSONObject doInBackground(String... params) {
			String uid = params[0];
			String email = params[1];
			String card_reference = params[2];
			String product_amount = params[3];
			String product_description = params[4];
			String dev_reference = params[5];
			String seller_id = params[6];
			
			
			//return paymentezsdk.cardDebit(uid, email, card_reference, product_amount, product_description, dev_reference, seller_id);
			
			//Debit with shipping address
			Shipping shipping = new Shipping();
			shipping.setShipping_street("Av Jacutinga");
			shipping.setShipping_house_number("607");
			shipping.setShipping_city("São Paulo");
			shipping.setShipping_zip("99999-999");
			shipping.setShipping_state("SP");
			shipping.setShipping_country("BR");
			shipping.setShipping_district("");			
			shipping.setShipping_additional_address_info("");
			
			
			return paymentezsdk.cardDebit(uid, email, card_reference, product_amount, product_description, dev_reference, seller_id, shipping);
			
			
		}
		
		protected void onPreExecute(){ 
	           super.onPreExecute();
	                pd = new ProgressDialog(ListCardActivity.this);
	                pd.setMessage("");
	                pd.show();    
	        }
		
		
		protected void onPostExecute(JSONObject json) {
			super.onPostExecute(json);
			if(pd != null)    
            	pd.dismiss();
			
			AlertDialog.Builder builder1 = new AlertDialog.Builder(ListCardActivity.this);
            try {
				builder1.setMessage("status: "+json.getString("status").toString()+ "\ntransaction_id:"+json.getString("transaction_id").toString());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
		ProgressDialog pd;
		@Override
		protected Boolean doInBackground(String... params) {
			String uid = params[0];
			String card_reference = params[1];
			
			
			return paymentezsdk.cardDelete(uid, card_reference);
		}
		
		protected void onPreExecute(){ 
	           super.onPreExecute();
	                pd = new ProgressDialog(ListCardActivity.this);
	                pd.setMessage("");
	                pd.show();    
	        }
		
		
		protected void onPostExecute(Boolean json) {
			super.onPostExecute(json);
			if(pd != null)    
            	pd.dismiss();
			System.out.println("DELETE INFO");
			
			System.out.println(json);
			AlertDialog.Builder builder1 = new AlertDialog.Builder(ListCardActivity.this);
            builder1.setMessage("delete response:"+json);			
            builder1.setCancelable(false);
            builder1.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alert11 = builder1.create();
            alert11.show();
            new CallApiListCardAsyncTask().execute(uid);
		}
		
	}
	
	private class CallApiListCardAsyncTask extends AsyncTask<String, Void, JSONArray>{
		ProgressDialog pd;
		@Override
		protected JSONArray doInBackground(String... params) {
			String uid = params[0];
			return paymentezsdk.cardList(uid);
		}
		
		protected void onPreExecute(){ 
	           super.onPreExecute();
	                pd = new ProgressDialog(ListCardActivity.this);
	                pd.setMessage("");
	                pd.show();    
	        }
		
		
		protected void onPostExecute(JSONArray json) {
			super.onPostExecute(json);
			if(pd != null)    
            	pd.dismiss();
			listCard = json;
			
			ArrayList<String> values= new ArrayList<String>();
			
			
			for (int i = 0; i < json.length(); i++) {
				JSONObject cardObject;
				try {
					cardObject = json.getJSONObject(i);	
					values.add("name:"+cardObject.get("name")+"\ncard_reference:"+cardObject.get("card_reference"));
					
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
			
			
			
	        
	       

	        // Define a new Adapter
	        // First parameter - Context
	        // Second parameter - Layout for the row
	        // Third parameter - ID of the TextView to which the data is written
	        // Forth - the Array of data

	        listadapter = new ArrayAdapter<String>(ListCardActivity.this,
	          android.R.layout.simple_list_item_1, android.R.id.text1, values);


	        // Assign adapter to ListView
	        listView.setAdapter(listadapter); 
	        
		}
		
	}
	
	
	@Override
	 public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
	           
	  menu.add(Menu.NONE, CONTEXT_MENU_DEBIT_ITEM, Menu.NONE, "Debit");
	  menu.add(Menu.NONE, CONTEXT_MENU_DELETE_ITEM, Menu.NONE, "Delete");
	 }
	
	
	@Override
	 public boolean onContextItemSelected(MenuItem item) {
	 
	      AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
	      int id = (int) listadapter.getItemId(info.position);/*what item was selected is ListView*/
	      JSONObject cardObject;
		try {
			cardObject = listCard.getJSONObject(id);
		
	      switch (item.getItemId()) {
	              case CONTEXT_MENU_DEBIT_ITEM:
	            	 new CallApiDebitCardAsyncTask().execute(uid, email, cardObject.getString("card_reference"), "5.00", "test", "1234567", "");
	                    
	                   return(true);
	             case CONTEXT_MENU_DELETE_ITEM:
	            	 new CallApiDeleteCardAsyncTask().execute( uid, cardObject.getString("card_reference"));
	                   return(true);    
	      }
	      
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  return(super.onOptionsItemSelected(item));
	}

}
