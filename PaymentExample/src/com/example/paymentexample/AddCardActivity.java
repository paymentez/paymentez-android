package com.example.paymentexample;

import com.paymentez.api.PaymentezCCSDK;

import android.app.Activity;

import android.app.ProgressDialog;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddCardActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addcard);
		
		final EditText editText1 = (EditText) findViewById(R.id.editText1);
		
		final EditText editText2 = (EditText) findViewById(R.id.editText2);
		
		final PaymentezCCSDK paymentezsdk = new PaymentezCCSDK(this, false, "BOHRA", "LgDGaLb7RgJqJw1Z3mYZhwzfBZh33q","PREPAID", "Ere68ttPklFTn89xZIhFYcqC5X8HX3Ob5qgbEkfjNfCLkud3wY"); 
		
		
		final WebView webView1 = (WebView) findViewById(R.id.webView1);
		WebSettings webSettings = webView1.getSettings();
		webSettings.setJavaScriptEnabled(true);
		
		webView1.setWebViewClient(new PaymentWebViewClient());
		
		final Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {            	
            	String payment_url = paymentezsdk.cardAdd(editText1.getText().toString(), editText2.getText().toString());
            	System.out.println(payment_url);
            	webView1.loadUrl(payment_url);
            }
        });
		
	}
	
	
	
	public class PaymentWebViewClient extends WebViewClient {
		private int       webViewPreviousState;
        private final int PAGE_STARTED    = 0x1;
        private final int PAGE_REDIRECTED = 0x2;
		private boolean is_error;
		
		private ProgressDialog dialog;
		
		public PaymentWebViewClient()
		{
			super();
			is_error = false;
		}
		
		
		@Override
		public void onLoadResource (WebView view, String url)
		{
			super.onLoadResource(view, url);			
		    
		   
		}
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			webViewPreviousState = PAGE_REDIRECTED;
			view.loadUrl(url);
			return true;
		}
		
		@Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            webViewPreviousState = PAGE_STARTED;
            if (dialog == null || !dialog.isShowing()){
            	dialog = new ProgressDialog(AddCardActivity.this);
            	dialog.setCancelable(false);
            	dialog.setCanceledOnTouchOutside(false);
            	dialog.setMessage("");				
            	dialog.show();
            }
                
        }
		
		@Override
		public void onPageFinished(WebView view, String url) {
			if(url.contains("save") && !is_error)
		    {
				
		    	String cookie=getCookie(url, "pmntz_add_success");
		    	
				if(cookie!=null)
				{
					if(cookie.equalsIgnoreCase("true"))
					{
						Toast.makeText(AddCardActivity.this, "card registered successfully", Toast.LENGTH_LONG).show();
						finish();
					}
					else
					{
						Toast.makeText(AddCardActivity.this, "card not registered successfully", Toast.LENGTH_LONG).show();
						
						
					}
				}
		    }
			
			try
			{
				if (webViewPreviousState == PAGE_STARTED) {
	                dialog.dismiss();
	                dialog = null;
	            }
				
			}
			catch(Exception e){}
		}
		
		@Override
		public void onReceivedError(WebView view, int errorCod,String description, String failingUrl) {
			Toast.makeText(AddCardActivity.this, "card not registered successfully", Toast.LENGTH_LONG).show();
			
			is_error = true;
			
			
        }
		
		public String getCookie(String siteName,String CookieName){     
			String CookieValue = null;

			CookieManager cookieManager = CookieManager.getInstance();
			String cookies = cookieManager.getCookie(siteName); 
			if(cookies!=null)
			{
				String[] temp=cookies.split("[;]");
				for (String ar1 : temp ){
					if(ar1.contains(CookieName)){
						String[] temp1=ar1.split("[=]");
						CookieValue = temp1[1];
					}
				}              
			}
			return CookieValue; 
		}
	}


}
