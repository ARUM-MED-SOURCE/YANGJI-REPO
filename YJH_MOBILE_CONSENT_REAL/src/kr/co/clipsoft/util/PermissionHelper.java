package kr.co.clipsoft.util;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.util.Log;

public class PermissionHelper {

	private static final int PERMISSION_REQUEST_CODE = 0;
	private static final String TAG = "PermissionHelper";
	private Context context;
	private static boolean isShowingPermissionDialog = false;

	public PermissionHelper(Context context) {
		this.context = context;
		isShowingPermissionDialog = false;
	}

	// 모든 권한 확인(저장소 권한과 로케이션 권한)
	public boolean currentAllPermisionCheck() {
		boolean result = true;
		
		int internetPermissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET);
		int readExternalStoragePermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.READ_EXTERNAL_STORAGE);
		int externalStoragePermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.WRITE_EXTERNAL_STORAGE);
		int readPhoneStatePermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.READ_PHONE_STATE);

		int networkStatePermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.ACCESS_NETWORK_STATE);
		int wifiStatePermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.ACCESS_WIFI_STATE);
		int changeWifiStatePermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.CHANGE_WIFI_STATE);
		int broadcastStickyPermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.BROADCAST_STICKY);
		int locationPermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.ACCESS_COARSE_LOCATION);

		// 2024-12-02 by sangu02
		// Android 13(버전 33)에서만 알림 권한 체크
		// 구버젼 라이브러리로 인해 Manifest.permission에 열거되어있지 않기때문에 String값으로 직접넘긴다.
		int notificationPermissionCheck = (Build.VERSION.SDK_INT >= 33)
				? ContextCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS")
				: PackageManager.PERMISSION_GRANTED; // Android 14 미만에서는 기본값으로 허용
				
		if (internetPermissionCheck == PackageManager.PERMISSION_DENIED
				|| readExternalStoragePermissionCheck == PackageManager.PERMISSION_DENIED
				|| externalStoragePermissionCheck == PackageManager.PERMISSION_DENIED
				|| readPhoneStatePermissionCheck == PackageManager.PERMISSION_DENIED
				|| networkStatePermissionCheck == PackageManager.PERMISSION_DENIED
				|| wifiStatePermissionCheck == PackageManager.PERMISSION_DENIED
				|| changeWifiStatePermissionCheck == PackageManager.PERMISSION_DENIED
				|| broadcastStickyPermissionCheck == PackageManager.PERMISSION_DENIED
				|| locationPermissionCheck == PackageManager.PERMISSION_DENIED
				|| notificationPermissionCheck == PackageManager.PERMISSION_DENIED) {
			result = false;
		}

		Log.i(TAG,
				"[currentAllPermisionCheck] 저장소 권한 : "
						+ ((externalStoragePermissionCheck == PackageManager.PERMISSION_DENIED) ? "PERMISSION_DENIED"
								: "PERMISSION_GRANTED"));
		Log.i(TAG,
				"[currentAllPermisionCheck] 로케이션 권한 : "
						+ ((locationPermissionCheck == PackageManager.PERMISSION_DENIED) ? "PERMISSION_DENIED"
								: "PERMISSION_GRANTED"));
		Log.i(TAG, "[currentAllPermisionCheck] 모든 권한 허용 여부 : " + result);
		return result;
	}

	// 시스템 퍼미션 팝업 사용여부 : 최초 한 번만 시스템의 권한 요청 다이얼로그를 사용하고 그 이후부터는 커스텀 권한 요청 다이얼로그를
	// 사용해야함.
	public void showRequestPermissionsDialog() {
		String isUsed = CommonUtil.getInstance(context).getSharedPreferences("PERMISSION", "IS_USED", "FALSE");
		Log.i(TAG, "[isUsedSystemPermissionsDialog] 시스템 권한 다이얼로그 사용 여부 : " + isUsed);
		if (isUsed.equals("FALSE")) {
			CommonUtil.getInstance(context).setSharedPreferences("PERMISSION", "IS_USED", "TRUE");
			showSystemPermissionsDialog();
		} else {
			if (!currentAllPermisionCheck()) {
				if (!isShowingPermissionDialog) {
					showCustomPermissionsDialog();
				}
			}
		}
	}

	// 권한 요청 함수
	public void showSystemPermissionsDialog() {
		// Dangerous permissions 들만 사용자에게 권한 요청
//		int externalStoragePermissionCheck = ContextCompat.checkSelfPermission(context,
//				Manifest.permission.WRITE_EXTERNAL_STORAGE); // 저장관련(파일 읽기 /쓰기에 필요)
//		int locationPermissionCheck = ContextCompat.checkSelfPermission(context,
//				Manifest.permission.ACCESS_COARSE_LOCATION); // 로케이션 관련 (mobile 통신에 필요)
//		int PhoneStatePermissionCheck = ContextCompat.checkSelfPermission(context,
//				Manifest.permission.READ_PHONE_STATE);
//		int internetPermissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET);
//		

		
		int internetPermissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET);
		int readExternalStoragePermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.READ_EXTERNAL_STORAGE);
		int externalStoragePermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.WRITE_EXTERNAL_STORAGE);
		int readPhoneStatePermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.READ_PHONE_STATE);

		int networkStatePermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.ACCESS_NETWORK_STATE);
		int wifiStatePermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.ACCESS_WIFI_STATE);
		int changeWifiStatePermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.CHANGE_WIFI_STATE);
		int broadcastStickyPermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.BROADCAST_STICKY);
		int locationPermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.ACCESS_COARSE_LOCATION);
		
		Log.i(TAG, "[checkPermission] externalStoragePermissionCheck :  " + externalStoragePermissionCheck);
		Log.i(TAG, "[checkPermission] locationPermissionCheck :  " + locationPermissionCheck);
		Log.i(TAG, "[checkPermission] PhoneStatePermissionCheck :  " + readPhoneStatePermissionCheck);
		List<String> permissionsList = new ArrayList<String>();

		if (internetPermissionCheck == PackageManager.PERMISSION_DENIED
				|| readExternalStoragePermissionCheck == PackageManager.PERMISSION_DENIED
				|| externalStoragePermissionCheck == PackageManager.PERMISSION_DENIED
				|| readPhoneStatePermissionCheck == PackageManager.PERMISSION_DENIED
				|| networkStatePermissionCheck == PackageManager.PERMISSION_DENIED
				|| wifiStatePermissionCheck == PackageManager.PERMISSION_DENIED
				|| changeWifiStatePermissionCheck == PackageManager.PERMISSION_DENIED
				|| broadcastStickyPermissionCheck == PackageManager.PERMISSION_DENIED
				|| locationPermissionCheck == PackageManager.PERMISSION_DENIED) {
			

			if (internetPermissionCheck == PackageManager.PERMISSION_DENIED) {
				permissionsList.add(Manifest.permission.INTERNET);
				Log.i(TAG, "[Permission Request] 기기 정보 권한 추가");
			}
			if (readExternalStoragePermissionCheck == PackageManager.PERMISSION_DENIED) {
				permissionsList.add(Manifest.permission.READ_PHONE_STATE);
				Log.i(TAG, "[Permission Request] 기기 정보 권한 추가");
			} 
			if (externalStoragePermissionCheck == PackageManager.PERMISSION_DENIED) {
				permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
				Log.i(TAG, "[Permission Request] 저장공간 권한 추가");
			}
			if (readPhoneStatePermissionCheck == PackageManager.PERMISSION_DENIED) {
				permissionsList.add(Manifest.permission.READ_PHONE_STATE);
				Log.i(TAG, "[Permission Request] 기기 정보 권한 추가");
			}
			if (networkStatePermissionCheck == PackageManager.PERMISSION_DENIED) {
				permissionsList.add(Manifest.permission.ACCESS_NETWORK_STATE);
				Log.i(TAG, "[Permission Request] 기기 정보 권한 추가");
			}
			if (wifiStatePermissionCheck == PackageManager.PERMISSION_DENIED) {
				permissionsList.add(Manifest.permission.ACCESS_WIFI_STATE);
				Log.i(TAG, "[Permission Request] 기기 정보 권한 추가");
			}
			if (changeWifiStatePermissionCheck == PackageManager.PERMISSION_DENIED) {
				permissionsList.add(Manifest.permission.CHANGE_WIFI_STATE);
				Log.i(TAG, "[Permission Request] 기기 정보 권한 추가");
			}
			if (broadcastStickyPermissionCheck == PackageManager.PERMISSION_DENIED) {
				permissionsList.add(Manifest.permission.BROADCAST_STICKY);
				Log.i(TAG, "[Permission Request] 기기 정보 권한 추가");
			} 
			// Mobile망일 경우 네트워크 정보를 가져오기 위해서 LOCATION 권한이 필요함.
			if (locationPermissionCheck == PackageManager.PERMISSION_DENIED) {
				permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
				Log.i(TAG, "[Permission Request] 로케이션 권한 추가");
			}
		}
		
		// 2024-10-21
		// Android 13 이상에서 알림 권한 추가
		if (Build.VERSION.SDK_INT >= 33) {
			int notificationPermissionCheck = ContextCompat.checkSelfPermission(context,
					"android.permission.POST_NOTIFICATIONS");
			if (notificationPermissionCheck == PackageManager.PERMISSION_DENIED) {
				permissionsList.add("android.permission.POST_NOTIFICATIONS");
				Log.i(TAG, "[Permission Request] 알림 권한 추가");
			}
		}
		
		String[] permissions = new String[permissionsList.size()];
		permissions = permissionsList.toArray(permissions);
		ActivityCompat.requestPermissions((Activity) context, permissions, PERMISSION_REQUEST_CODE);
	}

	// 권한 요청 결과
	public boolean hasAllPermissionsGranted(int[] grantResults) {
		boolean result = true;
		for (int grantResult : grantResults) {
			if (grantResult == PackageManager.PERMISSION_DENIED) {
				result = false;
			}
		}
		Log.i(TAG, "[hasAllPermissionsGranted ] 모든 권한 확인 : " + result);
		return result;
	}

	// 권한 요청 커스텀 팝업
	public void showCustomPermissionsDialog() {
		Log.i(TAG, "[showCustomPermissionDialog]");
		try {
			final boolean isNoti = isNotiPermission();
			String dialogMessage = "전자동의서를 사용하기 위해서는 해당 권한들이 필요합니다.\n[설정] -> [권한]으로 이동 후 허용해주시기 바랍니다.\n거부를 선택하시면 앱이 종료됩니다.";
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
			// 팝업 안내 메시지 부분으로 string.xml에서 설정한 메시지를 노출합니다.
			if (isNoti) {
				dialogMessage = "신규 기기는 알림 권한이 필요합니다.\n[설정] -> [알림]으로 이동 후 허용해주시기 바랍니다.\n거부를 선택하시면 앱이 종료됩니다.";
			}
			
			dialogBuilder.setTitle("권한 요청"); // 팝업 창 타이틀
			dialogBuilder.setMessage(dialogMessage);
			dialogBuilder.setCancelable(false);

			// 거부 클릭 이벤트
			dialogBuilder.setNegativeButton("거부", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.i(TAG, "[showRequestPermissionDialog] 거부 클릭 ");
					isShowingPermissionDialog = false;
					dialog.cancel();
					((Activity) context).finish();
				}
			});

			// 설정 클릭 이벤트
			dialogBuilder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.i(TAG, "[showRequestPermissionDialog] 설정 클릭 ");
					isShowingPermissionDialog = false;
					dialog.cancel();
					// 설정 화면으로 이동
					if(isNoti) {
						moveNotiSetting();
					}else {
						moveSetting();
					}
				}
			});
			AlertDialog alertDialog = dialogBuilder.create();
			alertDialog.show();
			isShowingPermissionDialog = true;
		} catch (Exception e) {
			e.toString();
			Log.e(TAG, "[showCustomPermissionsDialog] Exception : " + e.toString());
		}
	}

	// 설정화면으로 이동
	public void moveSetting() {
		Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.setData(Uri.parse("package:" + "kr.co.less.consent.yjh.real"));
		((Activity) context).startActivity(intent);
	}
	
	/**
	 * by sangu02
	 * 2024/10/23
	 * 알림 설정 화면으로 이동하게 되어있음
	 * 해당 함수를 탄다는 건 안드로이드 13이상이라는 것
	 */
	public void moveNotiSetting() {
		Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
		intent.putExtra(Settings.EXTRA_APP_PACKAGE, "kr.co.less.consent.yjh.real");
		((Activity) context).startActivity(intent);
	}
	

	/**
	 * by sangu02
	 * 
	 * @return 알림권한만 permission이 없는지
	 */
	public boolean isNotiPermission() {

		int internetPermissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET);
		int readExternalStoragePermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.READ_EXTERNAL_STORAGE);
		int externalStoragePermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.WRITE_EXTERNAL_STORAGE);
		int readPhoneStatePermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.READ_PHONE_STATE);

		int networkStatePermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.ACCESS_NETWORK_STATE);
		int wifiStatePermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.ACCESS_WIFI_STATE);
		int changeWifiStatePermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.CHANGE_WIFI_STATE);
		int broadcastStickyPermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.BROADCAST_STICKY);
		int locationPermissionCheck = ContextCompat.checkSelfPermission(context,
				Manifest.permission.ACCESS_COARSE_LOCATION);
		int notificationPermissionCheck = (Build.VERSION.SDK_INT >= 33)
				? ContextCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS")
				: PackageManager.PERMISSION_GRANTED;

		// 알림 권한만 DENIED 상태인지
		if (internetPermissionCheck == PackageManager.PERMISSION_GRANTED
				&& readExternalStoragePermissionCheck == PackageManager.PERMISSION_GRANTED
				&& externalStoragePermissionCheck == PackageManager.PERMISSION_GRANTED
				&& readPhoneStatePermissionCheck == PackageManager.PERMISSION_GRANTED
				&& networkStatePermissionCheck == PackageManager.PERMISSION_GRANTED
				&& wifiStatePermissionCheck == PackageManager.PERMISSION_GRANTED
				&& changeWifiStatePermissionCheck == PackageManager.PERMISSION_GRANTED
				&& broadcastStickyPermissionCheck == PackageManager.PERMISSION_GRANTED
				&& locationPermissionCheck == PackageManager.PERMISSION_GRANTED
				&& notificationPermissionCheck == PackageManager.PERMISSION_DENIED) {
			return true;
		} else {
			return false;
		}
	}
}
