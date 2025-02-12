
package kr.co.less.consent.yjh.edu;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.apache.cordova.CordovaActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;
import kr.co.clipsoft.util.CommonUtil;
import kr.co.clipsoft.util.EFromViewer;
import kr.co.clipsoft.util.PermissionHelper;
import kr.co.clipsoft.util.Storage;

public class MainActivity extends CordovaActivity {
	BroadcastReceiver networkStateReceiver = null;
	private Context context;
	public static final boolean SUPPORT_STRICT_MODE = Build.VERSION_CODES.FROYO < Build.VERSION.SDK_INT;
	private static final int PERMISSION_REQUEST_CODE = 0;
	private static ProgressDialog loadingBar = null;
	private static boolean activityVisible;
	private static boolean isStart = false;
	private static Handler rateHandler;
	private static PermissionHelper permissionHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "[LIFE CYCLE : onCreate]");
		Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler(this)); 
		context = this;
		permissionHelper = new PermissionHelper(this);
		MainActivity.activityShow();
		super.onCreate(savedInstanceState);

		// android Webview chrome debuging
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
				WebView.setWebContentsDebuggingEnabled(true);
			}
		}

		// hajun :: 2024.12.30
		// 백그라운드 작업 수행중인 스레드 종료
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		for (Thread thread : threadSet) {
			if (thread.getName().equals(MyForegroundService.CUSTOM_THREAD_NAME) && thread.isAlive()) {
				EFromViewer.writeLog("MainActivity :: onCreate() :: 스레드 종료 요청_tname_" + thread.getName());
				thread.interrupt();
			}
		}
		
		Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler(context));
	}

	@Override
	public void onStart() {
		super.onStart();
	};

	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "[LIFE CYCLE : onResume]");
     	Intent foreIntent = new Intent(this, MyForegroundService.class);
     	stopService(foreIntent);
		MainActivity.activityShow();
		verificationPermission();
	};

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "[LIFE CYCLE : onPause]");
    	// by sangu02 foreGroundService 시작 / 뷰어 시작 시 
    	Intent foreIntent = new Intent(this, MyForegroundService.class);
    	
    	if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    		startForegroundService(foreIntent);	
    	}else {
    		startService(foreIntent);
    	}
    	
		MainActivity.activityHide();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i(TAG, "[LIFE CYCLE : onStop]");
	}

	@Override
	public void onRestart() {
		super.onRestart();
		Log.i(TAG, "[LIFE CYCLE : onRestart]");
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "[LIFE CYCLE : onDestroy]");
		MainActivity.activityHide();
		
    	// by sangu02 foreGroundService 시작 / 뷰어 시작 시 
     	Intent foreIntent = new Intent(this, MyForegroundService.class);
     	stopService(foreIntent);

		// Preferences에 저장된 내용 삭제
		Storage.getInstance(this).deleteStorage();
		Log.i(TAG, "preferences 저장 정보 삭제");

		// 저장소 권한이 없을 경우 파일을 삭제할 수 없음
		if (permissionHelper.currentAllPermisionCheck()) {
			CommonUtil.getInstance(this).deleteEFormdataFile(); // e-from 관련 파일 삭제
		}
		isStart = false;
	}

	// 로딩바 함수
	public void showLoadingBar(String message) {
		if (MainActivity.isActivityVisible() && loadingBar != null) {
			loadingBar = new ProgressDialog(MainActivity.this);
			loadingBar.setMessage(message);
			loadingBar.setIndeterminate(false);
			loadingBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			loadingBar.setCancelable(false);
			loadingBar.show();
		}
	}

	public void hideLoadingBar() {
		if (loadingBar != null) {
			loadingBar.dismiss();
			loadingBar = null;
		}
	}

	// activity Visible 여부에 따른 loadingbar 사용여부
	public static boolean isActivityVisible() {
		Log.i(TAG, "[activity] 활성화 여부 : " + activityVisible);
		return activityVisible;
	}

	public static void activityHide() {
		Log.i(TAG, "[activity] 비활성화");
		activityVisible = false;
	}

	public static void activityShow() {
		Log.i(TAG, "[activity] 활성화");
		activityVisible = true;
	}

	private void activityStart() {
		Log.i(TAG, "[MainActivity] start");
		// 화면시작
		loadUrl(launchUrl);
		isStart = true;
	};

	private void verificationPermission() {
		Log.i(TAG, "[verificationPermission] isStart : " + isStart);
		Log.i(TAG, "[verificationPermission] 권한 허용 여부");
		Log.i(TAG, "[verificationPermission] Android Version : " + CommonUtil.getInstance(context).getAndroidVersion());
		// 안드로이드 마시멜로우 버전(23)부터는 중요 권한을 사용자에게 부여받아야만 한다.
		setBatteryOptimizations();
		
		if (CommonUtil.getInstance(context).getAndroidVersion() < 23) {
			if (!isStart) {
				activityStart();
			}
		} else {
			if (permissionHelper.currentAllPermisionCheck()) {
				if (!isStart) {
					activityStart();
				}
			} else {
				permissionHelper.showRequestPermissionsDialog();
			}
		}
	};

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		Log.i(TAG, "[PermissionsResult] requestCode :" + requestCode);
		if (requestCode == PERMISSION_REQUEST_CODE && permissionHelper.hasAllPermissionsGranted(grantResults)) {
			activityStart();
		} else {
			String asdf = Environment.getExternalStorageDirectory().toString();
			Toast.makeText(context, "해당 권한들을 허용하지 않으면 앱이 정상적으로 동작하지 않습니다.", Toast.LENGTH_LONG).show();
			permissionHelper.showCustomPermissionsDialog();
		}
	}

	// 전자동의서 어플 배터리 최적화 모드 해제
	private void setBatteryOptimizations() {
		PowerManager pm = (PowerManager) getSystemService(context.POWER_SERVICE);
		boolean isWhiteListing = false;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			isWhiteListing = pm.isIgnoringBatteryOptimizations(context.getPackageName());
		}
		if (!isWhiteListing) {
			AlertDialog.Builder setdialog = new AlertDialog.Builder(MainActivity.this);
			setdialog.setTitle("권한이 필요합니다.")
					.setMessage("전자동의서를 사용하기 위해서는 \"배터리 사용량 최적화\" 목록에서 제외하는 권한이 필요합니다. 계속하시겠습니까?")
					.setCancelable(false)
					.setPositiveButton("예", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
							
							intent.setData(Uri.parse("package:" + context.getPackageName()));
							context.startActivity(intent);
						}
					}).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Toast.makeText(MainActivity.this, "해당 권한을 허용하지 않으면 앱이 정상적으로 동작하지 않습니다.\n앱을 종료합니다.", Toast.LENGTH_SHORT)
									.show();
							dialog.cancel();
							((Activity) context).finish();
						}
					}).create().show();
		}
	}
	
	/**
	 * @author sangu02
	 * @since 2024/09/06
	 * @note 로그캣 로그 저장을 시작하는 메서드
	 */
    private void startLogging() {
    	File logDirectory = new File(Environment.getExternalStorageDirectory() + "/arum_log");
		if (!logDirectory.exists()) { // 파일 없으면 생성
			logDirectory.mkdir();
		}
		
        // 현재 날짜로 파일 이름 생성
        String logFileName = logDirectory + "/logcat_"+new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".txt";
        File logFile = new File(logFileName);
        
		
		
        // logcat 명령어로 로그를 파일로 저장
        try {
            if (!logFile.exists()) {
                logFile.createNewFile(); // 파일이 없으면 생성
            }

            // logcat 명령 실행
            String[] command = new String[]{"logcat", "-f", logFile.getAbsolutePath(), "*:V"};
            Runtime.getRuntime().exec(command);

            Log.i(TAG, "Logcat started, writing logs to: " + logFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Failed to start logging to file", e);
        }
    }

/**
	 * hajun :: 2024.12.30 디바이스 메모리 상태가 변화할 때 마다 호출되는 이벤트
	 */
	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);

		switch (level) {
		case TRIM_MEMORY_RUNNING_MODERATE:
			EFromViewer.writeLog("MainActivity :: onTrimMemory() :: 디바이스 메모리 보통(TRIM_MEMORY_RUNNING_MODERATE)");
			break;

		case TRIM_MEMORY_RUNNING_LOW:
			EFromViewer.writeLog("MainActivity :: onTrimMemory() :: 디바이스 메모리 부족(TRIM_MEMORY_RUNNING_LOW)");
			break;

		case TRIM_MEMORY_RUNNING_CRITICAL:
			EFromViewer.writeLog("MainActivity :: onTrimMemory() :: 디바이스 메모리 상당히 부족(TRIM_MEMORY_RUNNING_CRITICAL)");
			break;

		case TRIM_MEMORY_COMPLETE:
			EFromViewer.writeLog("MainActivity :: onTrimMemory() :: 디바이스 메모리 매우 부족, 앱 종료 가능성 있음(TRIM_MEMORY_COMPLETE)");
			break;

		default:
			break;
		}
	}
}
