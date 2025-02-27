package kr.co.clipsoft.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.util.Log;

public class ClipHttpDownlaodConnection {
	
	static String TAG_NAME = "DOWNLOAD";
	private static final int BUFFER_SIZE = 4096;
	
    // ssl security Exception 방지
 	public void disableSslVerification(){
 		// TODO Auto-generated method stub
 		try
 	    {
 	        // Create a trust manager that does not validate certificate chains
 	        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
 	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
 	                return null;
 	            }
 	            public void checkClientTrusted(X509Certificate[] certs, String authType){
 	            }
 	            public void checkServerTrusted(X509Certificate[] certs, String authType){
 	            }
 	        }
 	        };
 	
 	        // Install the all-trusting trust manager
 	        SSLContext sc = SSLContext.getInstance("SSL");
 	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
 	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
 	
 	        // Create all-trusting host name verifier
 	        HostnameVerifier allHostsValid = new HostnameVerifier() {
 	            public boolean verify(String hostname, SSLSession session){
 	                return true;
 	            }
 	        };
 	
 	        // Install the all-trusting host verifier
 	        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
 	    } catch (NoSuchAlgorithmException e) {
 	        e.printStackTrace();
 	    } catch (KeyManagementException e) {
 	        e.printStackTrace();
 	    }
 	}
	
	public String request(String downlodUrl, String downloadPath, String downloadFileName){
		
		Log.i(TAG_NAME, "[DOWNLOAD] ==========================================");
		Log.i(TAG_NAME, "[DOWNLOAD] Url : " + downlodUrl);
		Log.i(TAG_NAME, "[DOWNLOAD] Path : " + downloadPath);
		Log.i(TAG_NAME, "[DOWNLOAD] FileName : " + downloadFileName);
		String respone = "";
		try {
			disableSslVerification();
//			URL url = new URL(downlodUrl +"/"+ downloadFileName);			
			Log.i(TAG_NAME, "[DOWNLOAD] FULL URL : " + downlodUrl +"/"+ downloadFileName);
			URL url = new URL(downlodUrl +"/"+ URLEncoder.encode(downloadFileName, "utf-8").replace("+", "%20"));
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
				
			int responseCode = httpConn.getResponseCode();
			
			// always check HTTP response code first
			if (responseCode == HttpURLConnection.HTTP_OK) {				
				String disposition = httpConn.getHeaderField("Content-Disposition");
				String contentType = httpConn.getContentType();
				int contentLength = httpConn.getContentLength();
		
				// opens input stream from the HTTP connection
				InputStream inputStream;
				
				inputStream = httpConn.getInputStream();				
				
				String saveFilePath = downloadPath + File.separator + downloadFileName;
		
				// opens an output stream to save into file
				FileOutputStream outputStream = new FileOutputStream(saveFilePath);
		
				int bytesRead = -1;
				byte[] buffer = new byte[BUFFER_SIZE];
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}		
				outputStream.close();
				inputStream.close();		
			} else {
				Log.i(TAG_NAME, "[DOWNLOAD] " + downloadFileName + " download Fail!!!!");
				Log.i(TAG_NAME, "[DOWNLOAD] responseCode : " + responseCode);				
			}
			httpConn.disconnect();			
		} catch (IOException e) {
			e.printStackTrace();
			Log.i(TAG_NAME, "[DOWNLOAD] exception : " + e.toString());
			EFromViewer.writeLog("HTTP 예외 발생 : " + e.toString());
		} catch (Exception e1) {
			// TODO: handle exception
			EFromViewer.writeLog("그냥 예외 : " + e1.toString());
		}
		Log.i(TAG_NAME, "[DOWNLOAD] ==========================================");
		return respone;
	}
		
}
