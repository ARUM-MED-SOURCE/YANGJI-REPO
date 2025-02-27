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
import javax.net.ssl.*;

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
 	
    public String request(String downloadUrl, String downloadPath, String downloadFileName) {
        

        Log.i(TAG_NAME, "[DOWNLOAD] ==========================================");
        Log.i(TAG_NAME, "[DOWNLOAD] Url : " + downloadUrl);
        Log.i(TAG_NAME, "[DOWNLOAD] Path : " + downloadPath);
        Log.i(TAG_NAME, "[DOWNLOAD] FileName : " + downloadFileName);
        String response = "";

        try {
        	disableSslVerification();
            String encodedFileName = URLEncoder.encode(downloadFileName, "utf-8").replace("+", "%20");
            Log.i(TAG_NAME, "[DOWNLOAD] FULL URL : " + downloadUrl + "/" + encodedFileName);

            URL url = new URL(downloadUrl + "/" + encodedFileName);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

            int responseCode = httpConn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String contentType = httpConn.getContentType();
                int contentLength = httpConn.getContentLength();

                Log.i(TAG_NAME, "Content-Type = " + contentType);
                Log.i(TAG_NAME, "Content-Length = " + contentLength);
                Log.i(TAG_NAME, "fileName = " + downloadFileName);

                InputStream inputStream = httpConn.getInputStream();
                String saveFilePath = downloadPath + File.separator + downloadFileName;
                FileOutputStream outputStream = new FileOutputStream(saveFilePath);

                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();
                Log.i(TAG_NAME, "[DOWNLOAD] " + downloadFileName + " download Success!!");
                EFromViewer.writeLog("[DOWNLOAD] " + downloadFileName + " download Success!!");
            } else {
                Log.i(TAG_NAME, "[DOWNLOAD] " + downloadFileName + " download Fail!!!!");
                Log.i(TAG_NAME, "[DOWNLOAD] responseCode : " + responseCode);
                EFromViewer.writeLog("[DOWNLOAD] " + downloadFileName + " download Fail!!!!");
            }
            httpConn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG_NAME, "[DOWNLOAD] exception : " + e.toString());
            EFromViewer.writeLog("[DOWNLOAD] exception : " + e.toString());
        }

        Log.i(TAG_NAME, "[DOWNLOAD] ==========================================");
        return response;
    }
}
