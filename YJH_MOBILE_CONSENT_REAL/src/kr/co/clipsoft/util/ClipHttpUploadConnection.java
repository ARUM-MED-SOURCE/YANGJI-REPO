package kr.co.clipsoft.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class ClipHttpUploadConnection {
	
	static String TAG_NAME = "UPLOAD";
	String twoHyphens = "--";
	String boundary =  "*****";
	String lineEnd = "\r\n";
	private long startServiceCallTime;
	
	public String request(String serviceUrl, String serviceName, String uploadPath, String files){
		URL url = null;
        HttpURLConnection con = null;
        String respone = "";
        DataOutputStream dataOutputStream = null;
        
        startServiceCallTime = System.currentTimeMillis();
		Log.i(TAG_NAME, "=========== Request Info ==================");
		Log.i(TAG_NAME, "[ SEND UPLOAD REQUEST ]");
		Log.i(TAG_NAME, "URL : " +  serviceUrl);
		Log.i(TAG_NAME, "serviceName : " +  serviceName);
		Log.i(TAG_NAME, "uploadPath : " +  uploadPath);
		Log.i(TAG_NAME, "files : " +  files);
        try{
        	url = new URL(serviceUrl +"/"+ serviceName);
            con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);	// OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
            con.setDoInput(true);	// InputStream으로 서버로 부터 응답을 받겠다는 옵션.
            con.setUseCaches(false);
            con.setConnectTimeout(10*1000);
            
            con.setRequestMethod("POST");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary); 
            
			dataOutputStream = new DataOutputStream(con.getOutputStream());

			// add Field
			addFormField(dataOutputStream, "device", "AND");
			addFormField(dataOutputStream, "folderPath", uploadPath);
        
			// add File
		    String[] filePathList = files.split(",");
    		File[] fileList = new File[filePathList.length];
    		
    		Log.i(TAG_NAME, "[add File] File Count : " + filePathList.length);
    		
    		for(int i=0; i<filePathList.length; i++){
    			Log.i(TAG_NAME, "[add File] File : " + filePathList[i]);
    			fileList[i] = new File(filePathList[i]);    			
    			if(fileList[i].exists()) {
    				Log.i(TAG_NAME, "[add File]" + filePathList[i] + "파일이 존재합니다.");
    				addFilePart(dataOutputStream, "uploadFile", fileList[i]);
    			}else {
    				Log.i(TAG_NAME, "[add File]" + filePathList[i] + "파일이 존재하지 않습니다.");
    			}
    		}
    		
    		dataOutputStream.writeBytes(lineEnd);
		    dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
		    dataOutputStream.flush();
    		dataOutputStream.close();
    		
    		Log.i(TAG_NAME, "[ RECEIVE RESPONSE ]");
    		Log.i(TAG_NAME, "ResponseCode : " + con.getResponseCode());
		    
		    //display what returns the POST request
			StringBuilder sb = new StringBuilder();
			if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
			    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
				String line = null;  
				while ((line = br.readLine()) != null) {  
				    sb.append(line + "\n");
				}
				br.close();
				respone = sb.toString();
			}else{
			    respone = con.getResponseMessage();
			}
		}catch(Exception e){
			e.printStackTrace();
			Log.i(TAG_NAME, "[UploadConnection] Error" + e.toString());
			respone = e.toString();
		}finally{
			if(con != null){
				con.disconnect();
			}
		}
        Log.i(TAG_NAME, "respone : " + respone); 
        Log.i(TAG_NAME, "["+"UPLOAD"+"] 서비스 호출에 [성공]하였습니다.");
		Log.i(TAG_NAME, "["+"UPLOAD"+"] 응답에 걸린 시간 " + " : " + ( System.currentTimeMillis() - startServiceCallTime ) / 1000.0 );
        Log.i(TAG_NAME, " =========================================="); 
        return respone;
    }
	
	public void addFormField(DataOutputStream dataOutputStream, String key, String val){
		try {        
	        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
	        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\""+key+"\"" + lineEnd);
	        dataOutputStream.writeBytes(lineEnd);
	        dataOutputStream.writeBytes(val);
	        dataOutputStream.writeBytes(lineEnd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
    public void addFilePart(DataOutputStream dataOutputStream, String fieldName, File uploadFile) {
        String fileName = uploadFile.getName();
        String filePath = uploadFile.getPath();
        int bytesRead, bytesAvailable, bufferSize;
    	byte[] buffer;
    	int maxBufferSize = 1*1024*1024;
    	FileInputStream fileInputStream = null;
        try {
        	Log.i(TAG_NAME, "FilePath : " + fileName);
        	Log.i(TAG_NAME, "FileName : " + filePath);
	        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
	        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploadFile\"; filename=\"" + fileName +"\"" + lineEnd);
	        dataOutputStream.writeBytes(lineEnd);
	        
	        fileInputStream = new FileInputStream(filePath);
		    bytesAvailable = fileInputStream.available();
		    bufferSize = Math.min(bytesAvailable, maxBufferSize);
		    buffer = new byte[bufferSize];    		     
		    
		    // Read file
		    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		    while (bytesRead > 0) {
		    	dataOutputStream.write(buffer, 0, bufferSize);
		        bytesAvailable = fileInputStream.available();
		        bufferSize = Math.min(bytesAvailable, maxBufferSize);
		        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		    }		   
		    dataOutputStream.writeBytes(lineEnd); 
		    fileInputStream.close();
        } catch (IOException e) {
			e.printStackTrace();
		}
    }
}
