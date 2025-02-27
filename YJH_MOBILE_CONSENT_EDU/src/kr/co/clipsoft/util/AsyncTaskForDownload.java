package kr.co.clipsoft.util;

import java.io.File;

import org.apache.cordova.CallbackContext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

public class AsyncTaskForDownload extends AsyncTask<String, String, String> {
	private static final String TAG = "AsyncTaskForDownload";
	private Context context = null;
	private String popupMessage;
	private String apkFileName;
	private String downloadPath;

	public AsyncTaskForDownload(Context context, String message, CallbackContext callbackContext) {
		Log.i(TAG, "========= AsyncTaskForDownload Start ==================");
		this.context = context;
		if (!message.equals("")) {
			this.popupMessage = message;
		} else {
			this.popupMessage = "";
		}
	}

	/**
	 * Before starting background thread Show Progress Bar Dialog
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// Loading 바 생성
		if (!"".equals(popupMessage)) {
			LoadingBar.getInstance().show(popupMessage, context);
		}
	}

	@Override
	protected String doInBackground(String... params) {
		Log.i(TAG, "[ fileName : " + params[0] + " ]");
		apkFileName = params[0] + ".apk";

		String result = "";

		ClipHttpDownlaodConnection httpDownloadCon = new ClipHttpDownlaodConnection();

		String downloadUrl = "https://192.168.4.30/apk/dev"; // by sangu02 2025-02-24 FTP 다운로드 주소
		downloadPath = Environment.getExternalStorageDirectory().toString() + "/CLIPe-Form/CONSENT/UPDATE";
		// downloadPath = Environment.getExternalStorageDirectory().toString() +
		// "/Download";

		File eformDirectory = new File(Environment.getExternalStorageDirectory() + "/CLIPe-Form");
		File updateDirectory = new File(eformDirectory + "/CONSENT/UPDATE");

		if (!updateDirectory.exists()) {
			updateDirectory.mkdirs();
		}
		result = httpDownloadCon.request(downloadUrl, downloadPath, apkFileName);

		return result;
	}

	protected void onProgressUpdate(String... progress) {
		// setting progress percentage
	}

	// request가 끝나고 호출되는 함수
	@Override
	protected void onPostExecute(String respones) {
		LoadingBar.getInstance().init();
		Log.i(TAG, "========= AsyncTaskFoHttp End ==================");

		if (apkFileName != null && apkFileName != "") {
			Log.i(TAG, "[onPostExecute] APK 다운로드 완료 후 업데이트 시작 : " + apkFileName);
			Uri apkPath;
			String Fullpath = downloadPath + '/' + apkFileName;
			// android 누가 버전부터 파일공유시에 FileProvider를 사용해야함.
			String authorities = context.getPackageName() + ".fileprovider";
			apkPath = FileProvider.getUriForFile(context, authorities, new File(downloadPath, apkFileName));
			Intent intent = new Intent();

			intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(apkPath, "application/vnd.android.package-archive");
			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

			this.context.startActivity(intent);

		} else {
			Log.i(TAG, "[onPostExecute] apk파일이 없어서 업데이트를 할 수 없습니다.");
		}

		((Activity) context).finish();
	};

	@Override
	protected void onCancelled() {
		// 작업이 취소된후에 호출된다.
		super.onCancelled();
		LoadingBar.getInstance().init();
	}
}
