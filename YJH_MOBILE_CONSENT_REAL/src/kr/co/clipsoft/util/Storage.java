package kr.co.clipsoft.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import kr.co.less.consent.yjh.real.R;
public class Storage {
	public static final String TAG = "Storage";
	 
	private static int MODE_PRIVATE = 0;	
	private SharedPreferences storage;
	
	private SharedPreferences.Editor editor;
	private Context context;
	private static Storage mInstance;
	private static String DEFAULT_RECEIVING_RATE = "-80";		// 와이파이 제한 수신률 
	private static String DEFAULT_USE_RECEIVING_RATE = "N";		// 와이파이 제한 수신률 사용 여부 
	private String useCloudServer ="" ;
	public static Storage getInstance(Context context){
        if(mInstance == null){
            mInstance = new Storage(context);
        }
        return mInstance;
    }
	
	public Storage(Context context){		
		this.context = context;
		storage = context.getSharedPreferences("storage", MODE_PRIVATE);
		editor = storage.edit();
	}
	
	public void setStorage(JSONObject data){
		Log.i(TAG, "[ 서버정보 설정 ]");
		try {
			Iterator<?> keys = data.keys();
			while( keys.hasNext() ) {
			    String key = (String)keys.next();		    			    
			    editor.putString(key, data.getString(key)); 
				editor.commit();
				Log.i(TAG, "[set3] " + key + " : " + data.getString(key));
				if("INTERFACE_TYPE".equals(key)) {
					setConfigByJson();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void setConfigByJson(){
		InputStream is = null;
		ByteArrayOutputStream os = null;
		String mode = "";
		String defaultMode = "";
		try {			
			String configFilePath = Environment.getExternalStorageDirectory().toString() + "/Documents/yjh_consent_config_real.json";	
			Log.i(TAG, "configFilePath : " + configFilePath);
			File configFile  = new File(configFilePath);
			if(configFile.exists() && configFile.isFile()){
				Log.i(TAG, "[외부 설정 정보]");
				is = new FileInputStream(configFile);	
			}else{			
				String appVersionName = CommonUtil.getInstance(context).getCurrentVersionName("kr.co.less.consent.yjh.real");
				if(appVersionName.indexOf("DEV") > -1) {
					defaultMode = "DEV";
				}else {
					defaultMode = "REAL";
				}
				mode = storage.getString("INTERFACE_TYPE", defaultMode);
				if("DEV".equals(mode)) {
					Log.i(TAG, "[개발버전 설정 정보]");
					is = context.getResources().openRawResource(R.raw.dev_configurations);
				}else {
					Log.i(TAG, "[운영버전 설정 정보]");
					is = context.getResources().openRawResource(R.raw.real_configurations);
				}
			}
			os = new ByteArrayOutputStream();
			
			int ctr;
			ctr = is.read();			
			while (ctr != -1) {
				os.write(ctr);
				ctr = is.read();
			}	
			
			JSONObject config = null;			
			if(CommonUtil.getInstance(context).isJSONValid(os.toString())) {
				config = new JSONObject(os.toString());
				Iterator<?> keys = config.keys();
				while(keys.hasNext()) {
					String key = (String)keys.next();
					String val = config.getString(key);
					Log.i(TAG, "[set_1] " + key + " : " + val);
			        editor.putString(key, val);
				}
				editor.commit();			
			}
			useCloudServer = storage.getString("useCloudServer","");
			Log.i(TAG, "[set_1] :  " + Storage.getInstance(context).getStorage("useCloudServer"));
			is.close();
			os.close();
		}catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "[setConfigByJsone] exception : " + e.toString());			
		}		
	}

	/**
	  * @Method Name : getStorage
	  * @작성일 : 2016. 5. 19.
	  * @작성자 : clipSoft_kimyk
	  * @변경이력 : 
	  * @Method 설명 : preferences에 저장된 key값에 매핑된 value를 return
	  * @param key
	  * @return
	  */
	public String getStorage(String key){
		String value = storage.getString(key, "");
		if(value.equals("")){		
			// 저장소에 해당 값들이 없을 경우 다시 설정 파일에서 가져와서 저장소에 저장 
			if(key.toLowerCase(Locale.getDefault()).indexOf("macaddress") > -1){
				value = "AA:BB:CC:DD:EE:FF";
				editor.putString(key, value);
				editor.commit();
			}else if(key.equals("ipAddrss")){
		    	value = "192.168.1.75";
		    	editor.putString(key, value);
		    	editor.commit();
		    }else if(key.equals("deviceName")){
		    	value = "DeviceName";
		    	editor.putString(key, value);
		    	editor.commit();
		    }else if(key.equals("CONSENT_APP_VERSION")){
		    	value = CommonUtil.getInstance(context).getCurrentVersionName("kr.co.less.consent.yjh.real");
		    	EFromViewer.writeLog("value: " + value);
		    	editor.putString(key, value);
		    	editor.commit();
		    }else if(key.equals("EFORM_APP_VERSION")){ 	
		    	value = CommonUtil.getInstance(context).getCurrentVersionName("kr.co.clipsoft.eform");
		    	editor.putString(key, value);
		    	editor.commit();
		    }else if(key.equals("RECEIVING_RATE")){
		    	value = DEFAULT_RECEIVING_RATE;
		    	editor.putString(key, value);
		    	editor.commit();
		    }else if(key.equals("USE_RECEIVING_RATE")){
		    	value = DEFAULT_USE_RECEIVING_RATE;
		    	editor.putString(key, value);
		    	editor.commit();		    	
		    }else {
		    	value = getConfigurations(key);
		    	editor.putString(key, value);
		    	editor.commit();
		    }
			Log.i(TAG, "[getStorage] " + key + " : " + value + "");
		}
		return value;
	}
	
	/**
	  * @Method Name : deleteStorage
	  * @작성일 : 2016. 5. 19.
	  * @작성자 : clipSoft_kimyk
	  * @변경이력 : 
	  * @Method 설명 : preferences를 삭제
	  */
	public void deleteStorage(){
		Log.i(TAG, "Android 저장소 정보 삭제");
		editor.clear().commit();
	}
	
	public String getConfigurations(String target){		
		String value = "";
		InputStream is = null;
		ByteArrayOutputStream os = null;
		String mode = "";
		String defaultMode = "";
		try {
			
			String configFilePath = Environment.getExternalStorageDirectory().toString() + "/Documents/yjh_consent_config_real.json";
//			String configFilePath = Environment.getExternalStorageState()+"/Documents/consent_config.json";
			Log.i(TAG, "configFilePath : " + configFilePath);
			File configFile  = new File(configFilePath);       

			if(configFile.exists() && configFile.isFile()){
				Log.i(TAG, "[외부 설정 정보]");
				is = new FileInputStream(configFile);	
			}else{
				String appVersionName = CommonUtil.getInstance(context).getCurrentVersionName("kr.co.less.consent.yjh.real");
				if(appVersionName.indexOf("DEV") > -1) {
					defaultMode = "DEV";
				}else {
					defaultMode = "REAL";
				}
				mode = storage.getString("INTERFACE_TYPE", defaultMode);
				if("DEV".equals(mode)) {
					Log.i(TAG, "[개발버전 설정 정보]");
					is = context.getResources().openRawResource(R.raw.dev_configurations);
				}else {
					Log.i(TAG, "[운영버전 설정 정보]");
					is = context.getResources().openRawResource(R.raw.real_configurations);
				}
			}
			os = new ByteArrayOutputStream();
			
			int ctr;
			ctr = is.read();
			while (ctr != -1) {
				os.write(ctr);
				ctr = is.read();
			}	
			JSONObject config = null;			
			if(CommonUtil.getInstance(context).isJSONValid(os.toString())) {
				config = new JSONObject(os.toString());
				Iterator<?> keys = config.keys();
				while(keys.hasNext()) {
					String key = (String)keys.next();
					String val = config.getString(key);					
					if(key.equals(target)) {
						Log.i(TAG, "[getConfigurations] val : " + val);
						value = val;
					}
			        editor.putString(key, val);
				}
				editor.commit();			
			}	
			is.close();
			os.close();
		}catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "[getConfigurations] exception : " + e.toString());
		}
		Log.i(TAG, "[getConfigurations] target : " + target + " / value : " + value);		
		return value;
	}
}
