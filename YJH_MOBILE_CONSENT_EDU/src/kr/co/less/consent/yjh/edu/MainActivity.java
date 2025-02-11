/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package kr.co.less.consent.yjh.edu;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.cordova.CordovaActivity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;
import kr.co.clipsoft.util.CommonUtil;
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
 
}
