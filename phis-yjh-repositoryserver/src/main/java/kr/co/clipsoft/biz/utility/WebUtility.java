package kr.co.clipsoft.biz.utility;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WebUtility {

	private static final Logger logger = LoggerFactory.getLogger(WebUtility.class);

	
	@Value("#{customerProperties['server.request.host']}")
	private String requestHost;
	
	@Value("#{customerProperties['server.request.url']}")
	private String requestUrl;
	
	@Value("#{customerProperties['server.request.type']}")
	private String requestType;
	
	@Value("#{customerProperties['server.request.characterEncoding']}")
	private String requestCharacterEncoding;
	
	@Value("#{customerProperties['server.response.characterEncoding']}")
	private String responseCharacterEncoding;
	
	public String sendPost(String url, String requestParam)
	{
		try
		{
			
			logger.debug("Call Url : " + url);
			logger.debug("Call Param : " + requestParam);
			
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	 
	        //add reuqest header
			
	        //con.setRequestMethod("POST");
	        //con.setRequestProperty("User-Agent", "spring");
	        //con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	        //con.setRequestProperty("Accept","*/*");
	        //con.setRequestProperty("Accept-Charset","UTF-8");
	        //con.setRequestProperty("Content-Length", ""+requestParam.length());
	        //// Send post request
	        //con.setDoOutput(true);
			
			//add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("content-type", "application/x-www-form-urlencoded;charset=" + requestCharacterEncoding);
			
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setDefaultUseCaches(false);
				        
	        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
	        wr.writeBytes(requestParam);
	        wr.flush();
	        wr.close();
	 
	        int responseCode = con.getResponseCode();
	        if(responseCode != 200)
	        {
	        	return "";
	        }
	 

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream(), responseCharacterEncoding));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
						
	        return response.toString();
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}
	
	public String sendRequestService(HashMap<String, String> map)
	{
		String requestPath = requestHost + requestUrl;
		String requestParam = getSerializableParam(map);
					
		return sendPost(requestPath, requestParam);
	}
	
	public String sendRequestService(String submitId, HashMap<String, String> map)
	{
		String reqGetParam = "submit_id=" + submitId;
		String requestPath = requestHost + requestUrl + "?" + reqGetParam;
		String requestParam = getSerializableParam(map);
		
		return sendPost(requestPath, requestParam);
	}
	

    private String getSerializableParam(HashMap<String, String> hm) {       
        String params = "";
        if(hm == null || hm.isEmpty() || hm.size() == 0){
            return "";
        }
       
        String key = "";
        String val = "";
        Iterator<String> iter = hm.keySet().iterator();
        while(iter.hasNext()){
            key = iter.next();
            val = hm.get(key);
            val = convertEncoding(val);
            
            if("".equals(params)){
                params = key+"=" + val;
            }else{
                params += "&"+key+"=" + val;
            } 
        }
       
        return params;
    }
    
    private String convertEncoding(String data) {
    	String encodingData = "";
    	try {
			encodingData=new String(data.getBytes("UTF-8"), "8859_1");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.toString());
			encodingData="";
		}
    	return encodingData;
    }
}
