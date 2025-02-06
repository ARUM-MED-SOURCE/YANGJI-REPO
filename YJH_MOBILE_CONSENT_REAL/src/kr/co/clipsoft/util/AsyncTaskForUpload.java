package kr.co.clipsoft.util;

import org.apache.cordova.CallbackContext;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
 
public class AsyncTaskForUpload extends AsyncTask<String, String, String> {	
    private static final String TAG = "AsyncTaskFoUpLoad";
//    private ProgressDialog progDailog = null;
    private Context context = null;   
    private CallbackContext callbackContext;
    private String serviceName;
    private String uploadPath;
    private String files;
    private String popupMessage;
    
    public AsyncTaskForUpload(Context context, String message, CallbackContext callbackContext) {
		// TODO Auto-generated constructor stub
    	Log.i(TAG, "========= AsyncTaskFoUpLoad Start ==================");
    	this.context = context;
    	this.callbackContext = callbackContext;
    	if(!message.equals("")){
    		this.popupMessage = message;
    	}else{
    		this.popupMessage = "";
    	}
	}
    
    /**
     * Before starting background thread Show Progress Bar Dialog
     * */
    @Override
    protected void onPreExecute() {    	
        super.onPreExecute();
        // Loading 바 생성
        if(!"".equals(popupMessage)) {
        	LoadingBar.getInstance().show(popupMessage, context);
        }
    }
    
	@Override
    protected String doInBackground(String... params) {
			Log.i(TAG, "[ serviceName : " + params[0]+ " ]");
			Log.i(TAG, "[ uploadPath  : " + params[1]+ " ]");
			Log.i(TAG, "[ files 		 : " + params[2]+ " ]");
        	
			serviceName  = params[0];
			uploadPath = params[1];
			files = params[2];
        	
        	String result = "";	
        	
        	ClipHttpUploadConnection httpUploadCon = new ClipHttpUploadConnection();
        	
        	String serviceUrl = Storage.getInstance(context).getStorage("uploadUrl");
        	
        	result = httpUploadCon.request(serviceUrl, serviceName, uploadPath, files);

        return result;
    }	

    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
    }

    // request가 끝나고 호출되는 함수
    @Override
    protected void onPostExecute(String respones) {
    	Log.i(TAG, "========= AsyncTaskFoHttp End ==================");
    	if(callbackContext != null){
	    	if(CommonUtil.getInstance(context).isJSONValid(respones)){
				callbackContext.success(respones);
			}else{
				callbackContext.error(respones);	
			}
    	}
    	LoadingBar.getInstance().init();
    };
    
    @Override
    protected void onCancelled() {
        // TODO 작업이 취소된후에 호출된다.
        super.onCancelled();
        LoadingBar.getInstance().init();
    }
}
	