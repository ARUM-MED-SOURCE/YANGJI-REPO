package kr.co.clipsoft.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.util.Log;

public class ClipHttpDownlaodConnection {
	
	static String TAG_NAME = "DOWNLOAD";
	private static final int BUFFER_SIZE = 4096;
	
	public String request(String downlodUrl, String downloadPath, String downloadFileName){
		
		Log.i(TAG_NAME, "[DOWNLOAD] ==========================================");
		Log.i(TAG_NAME, "[DOWNLOAD] Url : " + downlodUrl);
		Log.i(TAG_NAME, "[DOWNLOAD] Path : " + downloadPath);
		Log.i(TAG_NAME, "[DOWNLOAD] FileName : " + downloadFileName);
		String respone = "";
		try {
//			URL url = new URL(downlodUrl +"/"+ downloadFileName);			
			Log.i(TAG_NAME, "[DOWNLOAD] FULL URL : " + downlodUrl +"/"+ downloadFileName);
			URL url = new URL(downlodUrl +"/"+ URLEncoder.encode(downloadFileName, "utf-8").replace("+", "%20"));
			Log.i(TAG_NAME, "[DOWNLOAD] FULL URL : " + downlodUrl +"/"+ URLEncoder.encode(downloadFileName, "utf-8").replace("+", "%20"));
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
				
			int responseCode = httpConn.getResponseCode();
			
			// always check HTTP response code first
			if (responseCode == HttpURLConnection.HTTP_OK) {				
				String disposition = httpConn.getHeaderField("Content-Disposition");
				String contentType = httpConn.getContentType();
				int contentLength = httpConn.getContentLength();
		
				Log.i(TAG_NAME, "Content-Type = " + contentType);
				Log.i(TAG_NAME, "Content-Disposition = " + disposition);
				Log.i(TAG_NAME, "Content-Length = " + contentLength);
				Log.i(TAG_NAME, "fileName = " + downloadFileName);
		
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
				Log.i(TAG_NAME, "[DOWNLOAD] " + downloadFileName + " download Success!!");
			} else {
				Log.i(TAG_NAME, "[DOWNLOAD] " + downloadFileName + " download Fail!!!!");
				Log.i(TAG_NAME, "[DOWNLOAD] responseCode : " + responseCode);				
			}
			httpConn.disconnect();			
		} catch (IOException e) {
			e.printStackTrace();
			Log.i(TAG_NAME, "[DOWNLOAD] exception : " + e.toString());
		}
		Log.i(TAG_NAME, "[DOWNLOAD] ==========================================");
		return respone;
	}
		
}
