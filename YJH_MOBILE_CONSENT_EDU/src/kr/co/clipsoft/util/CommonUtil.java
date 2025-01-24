package kr.co.clipsoft.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

public class CommonUtil {

	private Context context;
	private static CommonUtil mInstance;
	private static String TAG = "CommUtil";
	private String EFORM_PATH = Environment.getExternalStorageDirectory().toString() + "/CLIPe-Form";
	private	String IMAGE_VIEW_PATH = EFORM_PATH + "/CONSENT/ImageView";
	private	String RECORD_PATH = EFORM_PATH + "/CONSENT/RECORD";
	
	public static CommonUtil getInstance(Context context){
        if(mInstance == null){
            mInstance = new CommonUtil(context);
        }
        return mInstance;
    }
	
	public CommonUtil(Context context){
		this.context = context;
	}
	
	public String imageView(String data){
		String result = "";
    	try {
			// image download folder init
			initImageView();
			if(CommonUtil.getInstance(context).isJSONValid(data)){
				JSONObject obj = new JSONObject(data);
				JSONArray images = new JSONArray(obj.getString("RESULT_DATA"));
				for(int i = 0; i < images .length(); i++){
					JSONObject image = images.getJSONObject(i);
					String imagePath = image.getString("ImagePath");
					String imageFilename = image.getString("ImageFilename");
					String imageBase64String = image.getString("ImageBase64String");
					String fullPath = IMAGE_VIEW_PATH + "/" +  imageFilename;
					
					Log.i(TAG, "[imageView] imagePath : " + imagePath);
					Log.i(TAG, "[imageView] imageFilename : " + imageFilename);
					Log.i(TAG, "[imageView] fullPath : " + fullPath);
						
				    //데이터 base64 형식으로 Decode
				    byte[] bytePlainOrg = Base64.decode(imageBase64String, 0);   
				              
				    //byte[] 데이터  stream 데이터로 변환 후 bitmapFactory로 이미지 생성 
				    ByteArrayInputStream inStream = new ByteArrayInputStream(bytePlainOrg);
				    Bitmap bitmap = BitmapFactory.decodeStream(inStream) ;   
				
					File downloadFile = new File(fullPath);
		            OutputStream out  = new FileOutputStream(downloadFile);		
		            String extension = imageFilename.substring(imageFilename.lastIndexOf(".")+1, imageFilename.length());
		            if(extension.equals("png")){
		            	bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
		            }else{
		            	bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		            }
		            Log.i(TAG, "이미지 비트맵 변경 후 저장 성공 : " + imageFilename);
		            out.close();
		            inStream.close();
		            image.put("ImageBase64String", "");
				}
				obj.put("RESULT_DATA", images.toString());
				result = obj.toString();
			}else{
				result = data;
			}
		} catch (JSONException | IOException e1) {
			e1.printStackTrace();
			result = e1.toString();
		} 
    	return result;
	};
	
	// eform 관련 파일 삭제 
	public void deleteEFormdataFile(){
		Log.i(TAG, "[deleteFolderFile] 파일 삭제");
		// 기존 파일들 삭제 
		initImageView();
        deleteFolderFile(EFORM_PATH);
	};
	
	// 설치 관련 파일 삭제 
	public void deleteAPKFile(){
		Log.i(TAG, "[deleteAPKFile] 설치 파일 삭제");
		String apkPath = Environment.getExternalStorageDirectory().toString() + "/CLIPe-Form/CONSENT/UPDATE";
		File folder = new File(apkPath);    	
 		if(folder.exists() && folder.listFiles() != null){
			for (File file : folder.listFiles()){
				if(file.isFile()){
					String fileName = file.getName().toLowerCase(Locale.getDefault());
					if(fileName.indexOf(".apk") > -1){
						Log.i(TAG, "[deleteAPKFile] Delete File :" + file.getName());
						file.delete();
					}
				}
			}
 		}
	};
	
	// 해당 폴더의 하위 파일 삭제 
	public void deleteFolderFile(String path){
		Log.i(TAG, "[deleteFolderFile] path :" + path);
    	File folder = new File(path);    	
 		if(folder.exists() && folder.listFiles() != null){
 			Log.i(TAG, "[deleteFolderFile] deleteFile :" + folder.listFiles().length);
			for (File file : folder.listFiles()){
				if(file.isFile()){
					String fileName = file.getName().toLowerCase(Locale.getDefault());
					// 뷰어 로그만 제외하고 나머지 파일 삭제 
					if(fileName.indexOf("clipe") != 0 && fileName.indexOf(".apk") < 0){
						Log.i(TAG, "[deleteFolderFile] Delete File :" + file.getName());
						file.delete();
					}else{
						Log.i(TAG, "[deleteFolderFile] not Delete File :" + file.getName());
					}
				}
				if(file.isDirectory()){ 
					//viewer 로그나 업데이트 로그는 삭제하면 안됨
					if(file.getName().toLowerCase(Locale.getDefault()).indexOf("consent") < 0){
						deleteFolderFile(file.getPath());
					}else{
						// 로그 파일이 남아있을 경우 로그 삭제를 하면 안됨.
						if(!getRemainLog()){
							deleteFolderFile(file.getPath());
						}else{
							Log.i(TAG, "로그 파일이 남아있음 로그 삭제를 하면 안됨.");
						}
					}
				}
			}
 		}else{
 			Log.i(TAG, "파일을 삭제할 폴더가 없습니다.\nPath : " + path);
 		}
    };
    
    public boolean getRemainLog(){
    	SharedPreferences common = context.getSharedPreferences("common", Context.MODE_PRIVATE);
		boolean isRemainLog = common.getBoolean("IS_SEND_LOG", false);	
		return isRemainLog;
    }    
    
    // 이미지 뷰 관련 초기화 
    public void initImageView(){
 		// image download folder init
 		File folder = new File(IMAGE_VIEW_PATH);
 		if(!folder.exists()){
 			folder.mkdirs();
 		}else{ 			
 			if(folder != null && folder.listFiles().length > 0) {
	 			for (File file : folder.listFiles()){	 				
	 				if(file.exists()) {
	 					Log.i(TAG, "[initImageView] deleteFile :" + file.getName());
	 					file.delete();
	 				}
	 			}
 			}
 		}
    };
    
    // 앱 버전 가져오기 
    public String getCurrentVersion(String packageName){
		String version = "";	
		String name = "";
		try {
			if(packageName == null || packageName.equals("")){
				name = "kr.co.less.consent.yjh.edu";
			}else{
				name = packageName;
			}
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(name, PackageManager.GET_META_DATA);
			version = Integer.toString(pInfo.versionCode);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			version = "0";
		}
		return version;
	}
    
    // 앱 버전 네임 가져오기 
    public String getCurrentVersionName(String packageName){
		String versionName = "";	
		String name = "";
		try {
			if(packageName == null || packageName.equals("")){
				name = "kr.co.less.consent.yjh.edu";
			}else{
				name = packageName;
			}
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(name, PackageManager.GET_META_DATA);
			versionName = pInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}
	
	// 이미지 파일과 녹취 파일 로컬로 저장 
	public String base64StringToFile(String data, String fileType){
    	try {
			// image download folder init
			initFolder(fileType);
			JSONObject obj = new JSONObject(data);
			JSONArray jsonAry = new JSONArray(obj.getString("RESULT_DATA"));
			try {
				for(int i = 0; i < jsonAry .length(); i++){
					JSONObject vo = jsonAry.getJSONObject(i);
//					JSONObject vo = jsonObj.getJSONObject("vo");

					if(fileType.equals("image")){
						String imagePath = vo.getString("ImagePath");
						String imageFilename = vo.getString("ImageFilename");
						String imageBase64String = vo.getString("ImageBase64String");
						String fullPath = IMAGE_VIEW_PATH + "/" +  imageFilename;
						
						Log.i(TAG, "[base64StringToFile] imagePath : " + imagePath);
						Log.i(TAG, "[base64StringToFile] imageFilename : " + imageFilename);
						Log.i(TAG, "[base64StringToFile] fullPath : " + fullPath);
							
					    //데이터 base64 형식으로 Decode
					    byte[] bytePlainOrg = Base64.decode(imageBase64String, 0);   
					              
					    //byte[] 데이터  stream 데이터로 변환 후 bitmapFactory로 이미지 생성 
					    ByteArrayInputStream inStream = new ByteArrayInputStream(bytePlainOrg);
					    Bitmap bitmap = BitmapFactory.decodeStream(inStream) ;   
					
						File downloadFile = new File(fullPath);
			            OutputStream out  = new FileOutputStream(downloadFile);		
			            String extension = imageFilename.substring(imageFilename.lastIndexOf(".")+1, imageFilename.length());
			            if(extension.equals("png")){
			            	bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			            }else{
			            	bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			            }
			            Log.i(TAG, "이미지 비트맵 변경 후 저장 성공 : " + imageFilename);
			            out.close();
			            vo.put("ImageBase64String", "");
					}else{
						String recordPath = vo.getString("RecordPath");
						String recordFilename = vo.getString("RecordFilename");
						String recordBase64String = vo.getString("RecordBase64String");		
						String recordfullPath = RECORD_PATH + "/" +  recordFilename;
						
						Log.i(TAG, "[base64StringToFile] recordPath : " + recordPath);
						Log.i(TAG, "[base64StringToFile] recordFilename : " + recordFilename);
						Log.i(TAG, "[base64StringToFile] recordfullPath : " + recordfullPath);
							
					    //데이터 base64 형식으로 Decode
					    byte[] bytePlainOrg = Base64.decode(recordBase64String, 0);   				
						File downloadFile = new File(recordfullPath);
			            OutputStream out  = new FileOutputStream(downloadFile);		
			            Log.i(TAG, "녹취 파일 로컬 저장 성공 : " + recordFilename);         
			            out.write(bytePlainOrg);
			            out.close();
			            vo.put("Record64String", "");
					}			
				}
			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}			
			obj.put("RESULT_DATA", jsonAry.toString());
			return obj.toString();
		} catch (JSONException e1) {
			e1.printStackTrace();
			return "";
		}		
	};
	
	// 폴더 파일 초기화 
	public void initFolder(String fileType){
		Log.i(TAG, "[initFolder] deleteFile");
 		File eformFolder = new File(EFORM_PATH);
 		if(!eformFolder.exists()){
 			eformFolder.mkdir();
 		}	
 		// image download folder init
 		String path = "";
 		if(fileType.equals("image")){
 			path = IMAGE_VIEW_PATH;
 		}else{
 			path = RECORD_PATH;
 		}
 		File folder = new File(path);
 		if(!folder.exists()){
 			folder.mkdir();
 		}else{
 			for (File file : folder.listFiles()){
 				if(file.exists()) {
 					file.delete();
 				}
 			}
 		}
    };
	
	// JSON 타입 체크 
	public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
	
	public boolean isNumeric(String str)
	{
	    return str.matches("[+-]?\\d*(\\.\\d+)?");
	}
	
	public int getAndroidVersion(){
		return Build.VERSION.SDK_INT;
	}
	
	public void setSharedPreferences(String sharedType, String key, String value){
		Log.i(TAG, "[setSharedPreferences] sharedType : " + sharedType);
		Log.i(TAG, "[setSharedPreferences] key : " + key);
		Log.i(TAG, "[setSharedPreferences] value : " + value);
		SharedPreferences sharedPreferences = context.getSharedPreferences(sharedType, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);		
		editor.commit();
	}
	
	public String getSharedPreferences(String sharedType, String key, String defultValue){
		Log.i(TAG, "[getSharedPreferences] sharedType : " + sharedType);
		Log.i(TAG, "[getSharedPreferences] key : " + key);
		Log.i(TAG, "[getSharedPreferences] defultValue : " + defultValue);
		SharedPreferences sharedPreferences = context.getSharedPreferences(sharedType, Context.MODE_PRIVATE);
		Log.i(TAG, "[getSharedPreferences] value : " + sharedPreferences.getString(key, defultValue));
		return sharedPreferences.getString(key, defultValue);
	}
	
	// 이미지파일의 해쉬 값 구하기 
	public String getHashcode(String filePath){	
		String hashCode = "";
		// SHA를 사용하기 위해 MessageDigest 클래스로부터 인스턴스를 얻는다.
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			
			@SuppressWarnings("resource")
			FileInputStream fis = new FileInputStream(filePath);
	      
			// 해싱할 byte배열을 넘겨준다.
	        byte[] dataBytes = new byte[1024];
	     
	        int index = 0; 
	        while ((index = fis.read(dataBytes)) != -1) {
	        	md.update(dataBytes, 0, index);
	        };
	        // 해싱된 byte 배열을 digest메서드의 반환값을 통해 얻는다.
			byte[] hashbytes = md.digest();
			
			// 보기 좋게 16진수로 만드는 작업
			StringBuilder sbuilder = new StringBuilder();
			for (int i = 0; i < hashbytes.length; i++) {
				// %02x 부분은 0 ~ f 값 까지는 한자리 수이므로 두자리 수로 보정하는 역할을 한다.
				sbuilder.append(String.format("%x", hashbytes[i] & 0xff));
			}	
			hashCode = sbuilder.toString();
		} catch (NoSuchAlgorithmException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hashCode;
	}
}
