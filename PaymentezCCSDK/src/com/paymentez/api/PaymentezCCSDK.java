package com.paymentez.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.devicecollector.DeviceCollector;
import com.devicecollector.DeviceCollector.ErrorCode;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class PaymentezCCSDK implements DeviceCollector.StatusListener {
	private String username;
	private String password;
	private String app_code;
	private String app_secret_key;
	private boolean dev_environment;
	private String SERVER_DEV_URL = "https://pmntzsec-stg.paymentez.com";
	private String SERVER_PROD_URL = "https://pmntzsec.paymentez.com";
	private String SERVER_URL = SERVER_DEV_URL;

	private static DeviceCollector dc;
	static String sessionId;
	private static final String LOG_TAG = "CheckoutTestActivity";
	private static boolean running = false;
	private static boolean finished = false;
	private String message;
	private static Date startTime;

	private Context mContext;

	String uiText;
	
	/**
	 * 
	 * @param mContext
	 * @param dev_environment
	 * @param app_code
	 * @param app_secret_key
	 * @param username
	 * @param password
	 */
	public PaymentezCCSDK(Context mContext, boolean dev_environment,
			String app_code, String app_secret_key, String username,
			String password) {
		this.mContext = mContext;
		this.app_code = app_code;
		this.app_secret_key = app_secret_key;
		this.username = username;
		this.password = password;
		this.dev_environment = dev_environment;
		if (dev_environment)
			SERVER_URL = SERVER_DEV_URL;
		else
			SERVER_URL = SERVER_PROD_URL;

		this.debug("Building new...");
		// No saved instances, create a new one
		this.dc = new DeviceCollector((Activity) mContext);
		// TODO Put your Merchant ID here
		this.dc.setMerchantId("500005");
		// TODO Put your data collector URL here
		this.dc.setCollectorUrl("https://tst.kaptcha.com/logo.htm");

		// Skipping Collectors
		// If we wanted to skip a test or two, we could uncomment this code
		// EnumSet<DeviceCollector.Collector> skipList =
		// EnumSet.of(
		// DeviceCollector.Collector.GEO_LOCATION);
		// dc.skipCollectors(skipList);
		this.dc.setStatusListener(this);
		getSessionId();
	}

	public String cardAdd(String uid, String email) {

		String auth_timestamp = "" + (System.currentTimeMillis());
		
		String params = "application_code=" + app_code + "&email="
				+ Uri.encode(email) + "&session_id=" + sessionId + "&uid="
				+ uid;
		String params2 = "application_code=" + app_code + "&email="
				+ email + "&session_id=" + sessionId + "&uid="
				+ uid;
		String auth_token = getAuthToken(auth_timestamp, params);
		
		
		return SERVER_URL + "/api/cc/add/?" + params2 + "&auth_timestamp="
				+ auth_timestamp + "&auth_token=" + auth_token;

	}
	
	public JSONArray cardList(String uid) {

		String auth_timestamp = "" + (System.currentTimeMillis());
		
		String params = "application_code=" + app_code +  "&uid="
				+ uid;

		String auth_token = getAuthToken(auth_timestamp, params);
		
		ArrayList <NameValuePair> paramsPost = new ArrayList<NameValuePair>();
		return callApiJSONArray(SERVER_URL + "/api/cc/list/?" + params + "&auth_timestamp="
				+ auth_timestamp + "&auth_token=" + auth_token, paramsPost);

	}
	
	public JSONObject cardDebit(String uid, String email, String card_reference, String product_amount, String product_description, String dev_reference) {

		String auth_timestamp = "" + (System.currentTimeMillis());
		
		
		
		
		ArrayList <NameValuePair> paramsPost = new ArrayList<NameValuePair>();
		paramsPost.add(new BasicNameValuePair("application_code", app_code));
		paramsPost.add(new BasicNameValuePair("uid", uid));
		paramsPost.add(new BasicNameValuePair("email", email)); 
		paramsPost.add(new BasicNameValuePair("card_reference", card_reference)); 
		paramsPost.add(new BasicNameValuePair("product_amount", product_amount)); 
		paramsPost.add(new BasicNameValuePair("product_description", product_description)); 
		paramsPost.add(new BasicNameValuePair("dev_reference", dev_reference)); 
		paramsPost.add(new BasicNameValuePair("ip_address", getLocalIpAddress()));		
		
		String auth_token = getAuthToken(auth_timestamp, paramsPost);
		paramsPost.add(new BasicNameValuePair("auth_timestamp", auth_timestamp));
		paramsPost.add(new BasicNameValuePair("auth_token", auth_token));
		
		
		return callApiJSONObject(SERVER_URL + "/api/cc/debit/", paramsPost);

	}
	
	public boolean cardDelete(String uid, String card_reference) {

		String auth_timestamp = "" + (System.currentTimeMillis());
		
		ArrayList <NameValuePair> paramsPost = new ArrayList<NameValuePair>();
		paramsPost.add(new BasicNameValuePair("application_code", app_code));
		paramsPost.add(new BasicNameValuePair("uid", uid));
		
		paramsPost.add(new BasicNameValuePair("card_reference", card_reference)); 
			
		
		String auth_token = getAuthToken(auth_timestamp, paramsPost);
		paramsPost.add(new BasicNameValuePair("auth_timestamp", auth_timestamp));
		paramsPost.add(new BasicNameValuePair("auth_token", auth_token));		
		
		
		return callApiBoolean(SERVER_URL + "/api/cc/delete/", paramsPost);

	}
	
	public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    String ipv4;
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4 = inetAddress.getHostAddress())) {

                                               
                        return ipv4;
                    }
                }
            }
                
            
        } catch (SocketException ex) {
            
        }
        return "";
    }
	
	
	
	private String getAuthToken(String auth_timestamp, ArrayList<NameValuePair> params) {
		Comparator<NameValuePair> comp = new Comparator<NameValuePair>() {        // solution than making method synchronized
		    @Override
		    public int compare(NameValuePair p1, NameValuePair p2) {
		      return p1.getName().compareTo(p2.getName());
		    }
		};

		
		Collections.sort(params, comp);
		String urltoken = "";
		for (NameValuePair nameValuePair : params) {
			urltoken += nameValuePair.getName() +"="+Uri.encode(nameValuePair.getValue())+"&";
		}
		
		
		urltoken += auth_timestamp + "&" + app_secret_key;
		
		
		System.out.println("Vale:"+urltoken);

		return bin2hex(getHash(urltoken)).toLowerCase();
	}
	
	private String getAuthToken(String auth_timestamp, String params) {
		String urltoken = params + "&" + auth_timestamp + "&" + app_secret_key;

		return bin2hex(getHash(urltoken)).toLowerCase();
	}

	public byte[] getHash(String password) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		digest.reset();
		return digest.digest(password.getBytes());
	}

	static String bin2hex(byte[] data) {
		return String.format("%0" + (data.length * 2) + "X", new BigInteger(1,
				data));
	}

	private JSONArray callApiJSONArray(String url, ArrayList<NameValuePair> params) {
		String json = callApi(url, params);
		JSONArray jObjArray;
		try {
			jObjArray = new JSONArray(json);
			return jObjArray;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
				
		

			
	}
	
	private String callApi(String url, ArrayList<NameValuePair> params){
		try {
			AndroidHttpClient httpClient = AndroidHttpClient
					.newInstance("PM2.0-API");
			System.out.println("Mio:"+url);
			URL urlObj = new URL(url);
			HttpPost httpPostRequest = new HttpPost(url);
			if(!params.isEmpty()){
				HttpEntity httpEntity = new UrlEncodedFormEntity(params,HTTP.UTF_8);
			    httpPostRequest.setEntity(httpEntity);		    
			}
			
			AuthScope scope = new AuthScope(urlObj.getHost(), urlObj.getPort());
			UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
					username, password);

			CredentialsProvider cp = new BasicCredentialsProvider();
			cp.setCredentials(scope, creds);
			HttpContext credContext = new BasicHttpContext();
			credContext.setAttribute(ClientContext.CREDS_PROVIDER, cp);

			
			
			HttpResponse response = httpClient.execute(httpPostRequest, credContext);

			HttpEntity httpEntity = response.getEntity();

			InputStream is = httpEntity.getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			String json = sb.toString();
			System.out.println("Mio:" + json);
			StatusLine status = response.getStatusLine();
			System.out.println("Mio:" + status.toString());

			httpClient.close();
			
			if(status.getStatusCode()!=200)
				return "500";
			else
				return json;

			

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private JSONObject callApiJSONObject(String url, ArrayList<NameValuePair> params) {
		String json = callApi(url, params);
		JSONObject jObjArray;
		try {
			jObjArray = new JSONObject(json);
			return jObjArray;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private boolean callApiBoolean(String url, ArrayList<NameValuePair> params) {
		String json = callApi(url, params);
		if(json.equals("500")){
			return false;
		}else{
			return true;
		}

		
		
	}

	/**
	 * Tell the library to stop immediately.
	 */
	public void stopNow(View view) {
		if (!this.finished && this.running && null != this.dc) {
			this.dc.stopNow();
		}
	} // end stopNow ()

	/**
	 * This method generates the sessionId and call kount Collect
	 * 
	 * @return sessionId
	 */
	public static String getSessionId() {
		// Check if we are already running
		if (!running) {
			// Check if we already finished
			if (!finished) {
				// Create a sessionID (Unique ID) that doesn't repeat over a 30
				// day
				// period per transaction
				sessionId = UUID.randomUUID().toString();
				// The device collector does not like special characters in the
				// sessionID, so let's strip them out
				sessionId = sessionId.replace("-", "");
				sessionId = "i"+sessionId.substring(1, sessionId.length());
				
				debug("Checking out with sessionid [" + sessionId + "]");
				startTime = new Date();
				dc.collect(sessionId);
				
				// we should store this sessionId somewhere so we can pass it to
				// whatever is making the RIS call down the line.
			} else {
				debug("Already completed for this transaction. Why are you"
						+ "trying to run again?");
				
			} // end if (!this.finished) / else

		} else {
			debug("Already running");
		} // end if (!this.running) / else
		
		return sessionId;
	} 

	/**
	 * Implementation of handling an error coming from the collector.
	 * 
	 * @param code
	 *            The Error code returned
	 * @param ex
	 *            The Exception that caused the code.
	 */

	@Override
	public void onCollectorError(ErrorCode code, Exception ex) {
		long totalTime = getTotalTime();

		this.finished = true;
		if (null != ex) {
			if (code.equals(ErrorCode.MERCHANT_CANCELLED)) {
				this.debug("Merchant Cancelled");
			} else {
				this.debug("Collector Failed in (" + totalTime
						+ ") ms. It had an error [" + code + "]:"
						+ ex.getMessage());
				this.debug("Stack Trace:");
				for (StackTraceElement element : ex.getStackTrace()) {
					this.debug(element.getClassName() + " "
							+ element.getMethodName() + "("
							+ element.getLineNumber() + ")");
				} // end for (StackTraceElement element : ex.getStackTrace())
			} // end if (code.equals(ErrorCode.MERCHANT_CANCELLED)) / else
		} else {
			this.debug("Collector failed in (" + totalTime
					+ ") ms. It had an error [" + code + "]:");
		} // end if (null != ex) / else
	} // end onCollectorError (ErrorCode code, Exception ex)

	/**
	 * Implementation of handling collection start. In this case we are just
	 * logging, and marking a flag as running.
	 */
	@Override
	public void onCollectorStart() {
		long totalTime = getTotalTime();
		this.debug("Starting collector (" + totalTime + ")ms....");
		this.running = true;
	} // end onCollectorStart ()

	/**
	 * Implementation of handling collection start. In this case we are just
	 * logging, and marking a flag as not running.
	 */
	@Override
	public void onCollectorSuccess() {
		long totalTime = getTotalTime();
		this.debug("Collector finished successfully in (" + totalTime + ") ms");
		this.running = false;
		this.finished = true;
		// Let other processes know it's all done here

	} // end onCollectorSuccess ()

	private long getTotalTime() {
		Date stopTime = new Date();
		return stopTime.getTime() - startTime.getTime();
	}

	/*
	 * Debug messages. Send to the view and to the logs.
	 * 
	 * @param message The message to pass to the view and logs
	 */
	private static void debug(String message) {
		Log.d(LOG_TAG, message);

	} // end debug (String message)
}
