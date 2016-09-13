package com.paymentez.paymentezexample.easysolutions;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.easysol.dsb.BlockedConnectionListener;
import net.easysol.dsb.DSB;
import net.easysol.dsb.UpdateListener;
import net.easysol.dsb.device_protector.DeviceProtectorEventListener;
import net.easysol.dsb.malware_protector.overlay.OverlapingApp;
import net.easysol.dsb.malware_protector.overlay.OverlayListener;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Map;

import com.paymentez.paymentezexample.R;
import com.paymentez.paymentezexample.utils.Constants;

import javax.net.ssl.HttpsURLConnection;

public class EasySolutionsActivity extends ActionBarActivity {
	
	Context myContext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_easy_solutions);

		/*****************************************

		 Este método permite inicializar el sdk Easy Solutions

		 ****************************************/

		DSB.sdk(myContext).init(Constants.EASY_SOLUTIONS_DESARROLLO_LICENCIA);
		listeners();
				
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
	
	public void isSecureConnection(){
		String url = "https://www.example.com/";
		Boolean secure = DSB.sdk(myContext).CONNECTION_PROTECTOR_API.isSecureConnection(url );
		if(secure){
			// establish the connection
		}else{
			// Close connection
		}
	}
	
	/*****************************************
	 
	 Este método permite analizar si es seguro utilizar el dispositivo para realizar alguna conexión (basado en las reglas definidas en el Easy Solutions Customer Portal)
	 
	 ****************************************/
	
	public void isSecureByRiskRules(){
		boolean secure = DSB.sdk(myContext).CONNECTION_PROTECTOR_API.isSecureByRiskRules();
		if(secure){
				//Dispositivo seguro
			}else{
				//Dispositivo inseguro
			}
	}
	
	/*****************************************
	 
	 Este método permite comprobar si la conexión a la URL es segura, analizando sus certificados para proteger la conexión frente ataques de Man in the Middle;
	 
	 ****************************************/
	
	public void isSecureCertificate(){
		String url = "https://www.example.com/";
		Boolean secure = DSB.sdk(myContext).CONNECTION_PROTECTOR_API.isSecureCertificate(url);
		if(secure){
			//Establish connection
		}else{
			// Close connection
		}
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
}
