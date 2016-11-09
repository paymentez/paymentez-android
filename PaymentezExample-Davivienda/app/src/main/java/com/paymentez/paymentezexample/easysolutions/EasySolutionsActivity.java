package com.paymentez.paymentezexample.easysolutions;

import android.Manifest;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.easysol.dsb.BlockedConnectionListener;
import net.easysol.dsb.DSB;
import net.easysol.dsb.UpdateListener;
import net.easysol.dsb.device_protector.DeviceProtectorEventListener;
import net.easysol.dsb.device_protector.SIMChangeListener;
import net.easysol.dsb.malware_protector.overlay.OverlapingApp;
import net.easysol.dsb.malware_protector.overlay.OverlayListener;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.paymentez.paymentezexample.AddCardActivity;
import com.paymentez.paymentezexample.R;
import com.paymentez.paymentezexample.utils.Constants;
import com.rsa.mobilesdk.sdk.MobileAPI;

import javax.net.ssl.HttpsURLConnection;

public class EasySolutionsActivity extends ActionBarActivity {
	
	Context myContext = this;
	Button callscanDeviceStatus;
	Button callisSecureByRiskRules;
	Button callisSecureConnection;
	Button callisSecureCertificate;
	private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 153;//A unique code in this app




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_easy_solutions);

		requestPermissions();


		/*****************************************

		 Ejemplo 1.1 Inicialización del SDK

		 ****************************************/
		DSB.sdk(myContext).init(Constants.EASY_SOLUTIONS_DESARROLLO_LICENCIA);
		DSB.sdk(myContext).setEventsReportingEnabled(true);

		//Este método crea el listener para obtener los incidentes de seguridad detectados al encontrar una infección en el archivo Hosts.
		DSB.sdk(myContext).DEVICE_PROTECTOR_API.setEventsReportListener(new DeviceProtectorEventListener(){
			@Override
			public void onScanDeviceStateEvent(Map<String, String> eventMap) {
				// código del cliente
			}
			@Override
			public void onScanHostsEvent(Map<String, String> eventMap) {
				// client code
			}
		});


		/*****************************************

		 Ejemplo 1.2 Validación del Malware

		 Esto de preferencia debe hacerse en el Application del proyecto, por que si se hace en un activity, solo funcionaria en ese activity, y no en todos.

		 ****************************************/
		Application app = getApplication();
		DSB.sdk(myContext).MALWARE_PROTECTOR_API.startOverlappingProtection(app);

		//Listener para obtener información sobre una aplicación que es detectada al tratar de solaparse
		DSB.sdk(myContext).MALWARE_PROTECTOR_API.addOverlayListener(new OverlayListener() {
			@Override
			public void onSuspiciousOverlay(OverlapingApp app) { // client code
			} });



		/*****************************************

		 Ejemplo 1.3 Validación de seguridad completa

		 ****************************************/
		boolean secure = DSB.sdk(myContext).CONNECTION_PROTECTOR_API.isSecureByRiskRules();
		System.out.println("isSecureByRiskRules: "+ secure);



		/*****************************************

		 Ejemplo para 1.4 Validación de conexión segura

		 ****************************************/
		final String url_server = "https://www.google.com.mx";

		new AsyncTask<Void, Void, Boolean>() {
			ProgressDialog pdia;
			@Override
			protected void onPreExecute(){
				super.onPreExecute();
				pdia = new ProgressDialog(myContext);
				pdia.setMessage("Loading...");
				pdia.show();
			}
			@Override
			protected Boolean doInBackground(Void... params) {
				boolean isSecure = isSecureCertificate(url_server);
				return isSecure;
			}
			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);

				pdia.dismiss();
				AlertDialog.Builder builder1 = new AlertDialog.Builder(myContext);
				builder1.setMessage("Connection to " + url_server +" is Secure: " + result);

				builder1.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				AlertDialog alert11 = builder1.create();
				alert11.show();
			}

		}.execute();




		/*****************************************

		 Ejemplo para 1.6 Validación de SIM Card

		 ****************************************/
		DSB.sdk(myContext).DEVICE_PROTECTOR_API.setSimChangeListener(new SIMChangeListener() {
			@Override public void onChangedSIM(Map<String, String>
													   newSimInfo)
			{
				System.out.println("onChangedSIM: "+ newSimInfo.toString());
			}
		});











		callscanDeviceStatus = (Button) findViewById(R.id.callscanDeviceStatus);
		callscanDeviceStatus.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				scanDeviceStatus();

				AlertDialog.Builder builder1 = new AlertDialog.Builder(myContext);
				builder1.setMessage("Method Called");

				builder1.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				AlertDialog alert11 = builder1.create();
				alert11.show();

			}
		});


		callisSecureByRiskRules = (Button) findViewById(R.id.callisSecureByRiskRules);
		callisSecureByRiskRules.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(isSecureByRiskRules()){
					AlertDialog.Builder builder1 = new AlertDialog.Builder(myContext);
					builder1.setMessage("Is Secure By Risk Rules");

					builder1.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});
					AlertDialog alert11 = builder1.create();
					alert11.show();
				}else{
					AlertDialog.Builder builder1 = new AlertDialog.Builder(myContext);
					builder1.setMessage("Is NOT Secure By Risk Rules");

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
		});




		callisSecureConnection = (Button) findViewById(R.id.callisSecureConnection);
		callisSecureConnection.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

						new AsyncTask<Void, Void, Boolean>() {
							ProgressDialog pdia;
							@Override
							protected void onPreExecute(){
								super.onPreExecute();
								pdia = new ProgressDialog(myContext);
								pdia.setMessage("Loading...");
								pdia.show();
							}
							@Override
							protected Boolean doInBackground(Void... params) {
								boolean isSecure = isSecureConnection(url_server);
								return isSecure;
							}
							@Override
							protected void onPostExecute(Boolean result) {
								super.onPostExecute(result);
								pdia.dismiss();
								AlertDialog.Builder builder1 = new AlertDialog.Builder(myContext);
								builder1.setMessage("Connection to " + url_server +" is Secure: " + result);

								builder1.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int id) {
												dialog.cancel();
											}
										});
								AlertDialog alert11 = builder1.create();
								alert11.show();
							}

						}.execute();

			}
		});


		callisSecureCertificate = (Button) findViewById(R.id.callisSecureCertificate);
		callisSecureCertificate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {


				new AsyncTask<Void, Void, Boolean>() {
					ProgressDialog pdia;
					@Override
					protected void onPreExecute(){
						super.onPreExecute();
						pdia = new ProgressDialog(myContext);
						pdia.setMessage("Loading...");
						pdia.show();
					}
					@Override
					protected Boolean doInBackground(Void... params) {
						boolean isSecure = isSecureCertificate(url_server);
						return isSecure;
					}
					@Override
					protected void onPostExecute(Boolean result) {
						super.onPostExecute(result);

						pdia.dismiss();
						AlertDialog.Builder builder1 = new AlertDialog.Builder(myContext);
						builder1.setMessage("Connection to " + url_server +" is Secure: " + result);

						builder1.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});
						AlertDialog alert11 = builder1.create();
						alert11.show();
					}

				}.execute();

			}
		});










				
	}




	/*****************************************
	 
	 Este método permite habilitar o deshabilitar el servicio que envia los eventos
	 
	 ****************************************/
	
	public void setEventsReportingEnabled(){
		DSB.sdk(myContext).setEventsReportingEnabled(true);
	}
	
	/*****************************************
	 
	 Estos métodos permiten habilitar o deshabilitar el servicio de actualizaciones automaticas
	 
	 ****************************************/
	
	public void setAutomaticUpdateEnabled(){
		DSB.sdk(myContext).setAutomaticUpdateEnabled(true);
		DSB.sdk(myContext).setAutomaticUpdateInterval(120000);
	}
	
	/*****************************************
	 
	 Este método permite enviar el username de forma cifrada
	 
	 ****************************************/
	
	public void setEventReportingUsername(){
		DSB.sdk(myContext).setEventReportingUsername("username", 121212, true);
	}
	
	/*****************************************
	 
	 Estos métodos permiten obtener la informacion del dispositivo
	 
	 ****************************************/
	
	public void scanDeviceStatus(){
		DSB.sdk(myContext).DEVICE_PROTECTOR_API.scanDeviceStatus();
	}
	
	public void scanDeviceHosts(){
		 boolean scan = DSB.sdk(myContext).DEVICE_PROTECTOR_API.scanDeviceHosts();
		 if(scan){
			 //Código del cliente
		 }
		 else{
			//Código del cliente
		 }
	}
	
	/*****************************************
	 
	 Este método permite restaurar el archivo host del dispositivo
	 
	 ****************************************/
	
	public void restoreDeviceHosts(){
		DSB.sdk(myContext).DEVICE_PROTECTOR_API.restoreDeviceHosts();
	}
	
	/*****************************************
	 
	 Este método permite iniciar la protección contra el solapamiento de la aplicación
	 
	 ****************************************/
	
	public void startOverlappingProtection(){
		Application app = getApplication(); DSB.sdk(myContext).MALWARE_PROTECTOR_API.startOverlappingProtection(app);
		
	}
	
	/*****************************************
	 
	 Este método personaliza la notificación desplegada cuando es detectada una aplicación tratando de sobreponerse.
	 
	 ****************************************/
	
	public void configureOverlappingMalwareGUINotification(){
		DSB.sdk(myContext).MALWARE_PROTECTOR_API.configureOverlappingMalwareGUINotification(R.mipmap.ic_launcher, "Titulo", "Mensaje");
	}
	
	/*****************************************
	 
	 Este método recibe como parámetro la URL a conectarse y retornará un objeto nativo HttpsURLConnection.
	 
	 ****************************************/
	
	public void DSBHttpsURLConnection() throws CertificateException, IOException{
		String url = "https://www.example.com/";
		HttpsURLConnection con = DSB.sdk(myContext).CONNECTION_PROTECTOR_API.DSBHttpsURLConnection( url);
	}
	
	/*****************************************
	 
	 Este metodo le permitirá saber si la conexión a la URL es segura a través de las validaciones del Certificate Pinning y de acuerdo a la configuración de las reglas de riesgo (Risk Rules) definidas en el Easy Solutions Customer Portal,
	 
	 ****************************************/
	
	public boolean isSecureConnection(String url){

		Boolean secure = DSB.sdk(myContext).CONNECTION_PROTECTOR_API.isSecureConnection(url);

		System.out.println("isSecureConnection: "+ secure);
		return secure;

	}
	
	/*****************************************
	 
	 Este método permite analizar si es seguro utilizar el dispositivo para realizar alguna conexión (basado en las reglas definidas en el Easy Solutions Customer Portal)
	 
	 ****************************************/
	
	public boolean isSecureByRiskRules(){
		boolean secure = DSB.sdk(myContext).CONNECTION_PROTECTOR_API.isSecureByRiskRules();
		System.out.println("isSecureByRiskRules: "+ secure);
		return secure;

	}
	
	/*****************************************
	 
	 Este método permite comprobar si la conexión a la URL es segura, analizando sus certificados para proteger la conexión frente ataques de Man in the Middle;
	 
	 ****************************************/
	
	public boolean isSecureCertificate(String url){


		Boolean secure = DSB.sdk(myContext).CONNECTION_PROTECTOR_API.isSecureCertificate(url);
		System.out.println("isSecureCertificate: "+ secure);
		return secure;
	}	
	
	public void listeners(){
		
		/*****************************************
		 
		 Este método le permite implementar acciones antes y después de que el DSB Protector SDK actualice la información de aplicaciones falsas y el Blacklist de URL.
		 
		 ****************************************/	
		DSB.sdk(myContext).setUpdateListener(new UpdateListener() {
			
			@Override
			public void onPreUpdate() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPostUpdate() {
				// TODO Auto-generated method stub
				
			}
		});
		
		/*****************************************
		 
		 Este listener notifica cuando la conexión a los servidores de DSB ha sido comprometida
		 
		 ****************************************/		
		DSB.sdk(myContext).setBlockedConnectionListener(new BlockedConnectionListener() {
			
			@Override
			public void onBlockedConnection(String arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		/*****************************************
		 
		 Este método crea el listener para obtener los incidentes de seguridad detectados al encontrar una infección en el archivo Hosts.
		 
		 ****************************************/		
		DSB.sdk(myContext).DEVICE_PROTECTOR_API.setEventsReportListener(new DeviceProtectorEventListener(){
			@Override
			public void onScanDeviceStateEvent(Map<String, String> eventMap) {
			// código del cliente
			}
			@Override
			public void onScanHostsEvent(Map<String, String> eventMap) {
			// client code
			}
		});
		
		
		/*****************************************
		 
		 Listener para obtener información sobre una aplicación que es detectada al tratar de solaparse
		 
		 ****************************************/
		DSB.sdk(myContext).MALWARE_PROTECTOR_API.addOverlayListener(new OverlayListener() {
			@Override
			public void onSuspiciousOverlay(OverlapingApp app) { // client code
			} });		
	}

	public void requestPermissions(){
		List<String> permissionsList = new ArrayList<>();
		List<String>  permissionsNeeded = new ArrayList<>();


		if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION)){
			permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
		}
		if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)){
			permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
		}


		if(!permissionsList.isEmpty()){
			ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
		}

	}

	private boolean addPermission(List<String> permissionsList, String permission) {
		if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
			permissionsList.add(permission);
			if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
				return false;
			}
		}
		return true;
	}

}
