package kr.co.clipsoft.util;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import kr.co.less.consent.yjh.edu.MainActivity;

public class LoadingBar{

	private ProgressDialog loadingBar;
	private static LoadingBar mInstance;
	private static String TAG = "LoadingBar";
	
	public static LoadingBar getInstance(){
        if(mInstance == null){
            mInstance = new LoadingBar();
        }
        return mInstance;
    }
	
	public LoadingBar() {
		init();
	}
	
	public void init(){
		if(loadingBar != null){
			loadingBar.dismiss();
			loadingBar = null;
		}
		Log.i(TAG, "LoadingBar 초기화");
	}

	public void show(String message, Context context){
		Activity activity = (Activity)context;
		Log.i(TAG, "Activity 종료 여부 : " + activity.isFinishing());
		Log.i(TAG, "Activity 화면 활성화 여부  : " + MainActivity.isActivityVisible());
		// 액티비티 상태를 확인 안하면 배드토큰 예외 발생됨.
		if(!activity.isFinishing() && MainActivity.isActivityVisible() && loadingBar == null){
			loadingBar = new ProgressDialog(context);
			loadingBar.setMessage(message);
			loadingBar.setIndeterminate(false);
			loadingBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			loadingBar.setCancelable(false);
			loadingBar.show();  
			Log.i(TAG, "LoadingBar 시작");
		}else{
			Log.i(TAG, "LoadingBar 시작 안함.");
			Log.i(TAG, "Activity 비활성화 되었거나 현재 로딩바가 현재 돌고 있습니다.");	
		}
	};
	
	public void hide(){
		Log.i(TAG, "LoadingBar 종료"); 
		if(loadingBar != null){
			loadingBar.dismiss();
			loadingBar = null;
    	}
	};

}