package kr.co.clipsoft.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import kr.co.clipsoft.plugin.MultipartUtility;

public class AsyncTaskForHttp extends AsyncTask<String, String, String> {
	private static final String TAG = "AsyncTaskFoHttp";
	private Context context = null;
	private CallbackContext callbackContext;
	private String serviceName;
	private String methodName;
	private String type;
	private String parameters;
	private String userId;
	private String patientCode;
	private String popupMessage;
	private JSONArray jsonary;
	private String url_param;
	private URL url;

	public AsyncTaskForHttp(Context context, String message, CallbackContext callbackContext) {
		// TODO Auto-generated constructor stub
		Log.i(TAG, "========= AsyncTaskFoHttp Start ==================");
		this.context = context;
		this.callbackContext = callbackContext;
		if (!message.equals("")) {
			this.popupMessage = message;
		} else {
			this.popupMessage = "Loading...";
		}
	}
 
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// Loading 바 생성
		LoadingBar.getInstance().show(popupMessage, context);
	}

	@Override
	protected String doInBackground(String... params) {

		Log.i(TAG, "[ Count 			: " + params.length + " ]");
		Log.i(TAG, "[ URL 	: " + params[0] + " ]");
		Log.i(TAG, "[ TYPE 	: " + params[1] + " ]");
		Log.i(TAG, "[ parameters 		: " + params[2] + " ]");
		Log.i(TAG, "[ userId 	: " + params[3] + " ]");
		Log.i(TAG, "[ patientCode 	: " + params[4] + " ]");
		EFromViewer.writeLog("param type : " + params[1] );
		url_param = params[0];
		type = params[1];
		parameters = params[2];
		userId = params[2];
		patientCode = params[3];

		if (type.equals("upload")) {
			System.out.println("UPLOAD진입");
			List<String> results = null;
			JSONArray imgAry = null;
			JSONArray imgArys = null;
			MultipartUtility mu;
			ArrayList<File> listFile = new ArrayList<File>();
			try {
				mu = new MultipartUtility(url_param, "UTF-8");

				try {
					imgAry = new JSONArray(parameters);
					mu.addFormField("parameter", parameters);
					// 추후 변경예정
					mu.addFormField("consentMstRid", imgAry.getJSONObject(0).getString("consentMstRid"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				for (int i = 0; i < imgAry.length(); i++) {
					String path = "";
					try {
						JSONObject imgObj = imgAry.getJSONObject(i);
						path = imgObj.getString("imgRealPath"); 
						File f = new File(path); 
						listFile.addAll(Arrays.asList(f));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				for (File file : listFile) { 
					mu.addFilePart("images", file);
				}

				results = mu.finish(); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return results.toString();
		} else if (type.equals("audio")) { 
			List<String> results = null;
			JSONArray audioAry = null;
			MultipartUtility mu;
			ArrayList<File> listFile = new ArrayList<File>();
			String sizecheck = "";
			try {
				mu = new MultipartUtility(url_param, "UTF-8");
				mu.addFormField("parameter", parameters);

				try {
					audioAry = new JSONArray(parameters);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				for (int i = 0; i < audioAry.length(); i++) {
					String path = "";
					try {
						JSONObject audioObj = audioAry.getJSONObject(i);
						path = audioObj.getString("recordRealPath"); 
						File f = new File(path);
						long fileSize = f.length();

						if (fileSize > 2097152) {  
							sizecheck = "over";
						} 
						listFile.addAll(Arrays.asList(f));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} 
				if(!sizecheck.equals("over")) {
					for (File file : listFile) { 
						mu.addFilePart("records", file);
					} 
					results = mu.finish(); 
				}else { 
					return "sizeOver";
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return results.toString();

		} else if (type.equals("audioDown")) {
			JSONArray jsonParams = null;
			String result = "";
			try {
				jsonParams = new JSONArray(parameters);
				url = new URL(url_param);
				for (int i = 0; i < jsonParams.length(); i++) {
					fileDownload(url, jsonParams.getJSONObject(i).getString("recordFullPath"),
							jsonParams.getJSONObject(i).getString("recordFileName"));
				} 
			} catch (JSONException e) { 
				e.printStackTrace();
			} catch (MalformedURLException e) { 
				e.printStackTrace();
			}

			return result;

		} else if (type.equals("sign")) {
			URL newurl;
			Bitmap bitmap;
			String base64 = "";
			try {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
				newurl = new URL(url_param);
				bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
				base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return base64;
		} else if (type.equals("gettime")) {

			try {
				url = new URL(url_param);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			String result = "";

			JSONObject jsonParams = null;
			try {
				if (parameters != "" && parameters != null) {
					jsonParams = new JSONObject(parameters);
				}
				ClipHttpURLConnection httpUrlCon = new ClipHttpURLConnection();

				String serviceUrl = Storage.getInstance(context).getStorage("serviceUrl");

				JSONObject commonParam = new JSONObject();

				// ===============================
				result = httpUrlCon.request(jsonParams, url);  
			} catch (JSONException e) {
				e.printStackTrace();
				Log.i(TAG, e.toString());
				result = "ERR! : " + e.toString();
			} catch (Exception ee) {
				ee.printStackTrace();
				Log.i(TAG, ee.toString());
				result = "ERR! : " + ee.toString();
			}

			return result;
		} else if (type.equals("save")) {
			List<String> results = null;
			String path = "";
			JSONObject imgAry = null;
			JSONArray imgArys = null;
			MultipartUtility mu;
			ArrayList<File> listFile = new ArrayList<File>();

			String result = "";

			try {

				try {
					imgAry = new JSONObject(parameters);

					mu = new MultipartUtility(url_param, "UTF-8");
					mu.addFormField("parameter", parameters);

					if (imgAry.getString("completeYn").equals("Y")) { // 인증저장일때
						path = imgAry.getString("imgRealPath");
						String[] pathary = path.split(",");

						for (int i = 0; i < pathary.length; i++) { 
							File f = new File(pathary[i]); 
							listFile.addAll(Arrays.asList(f));
						}

						for (File file : listFile) { 
							mu.addFilePart("imageFiles", file);
						}
					} 
					
					results = mu.finish();
					System.out.println("송신결과 :  " + results);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return results.toString();

		} else {

			try {
				url = new URL(url_param);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			String result = "";

			JSONObject jsonParams = null;
			try {
				if (parameters != "" && parameters != null) {
					jsonParams = new JSONObject(parameters);
				}
				ClipHttpURLConnection httpUrlCon = new ClipHttpURLConnection();

				String serviceUrl = Storage.getInstance(context).getStorage("serviceUrl");

				JSONObject commonParam = new JSONObject();

				// ===============================

				result = httpUrlCon.request(jsonParams, url);
				// CommonUtil.getInstance(context).base64StringToFile(result, "record");
				// 이미지 호출 시에는 base64 스트링을 파일로 저장 
			} catch (JSONException e) {
				e.printStackTrace();
				Log.i(TAG, e.toString());
				result = "ERR! : " + e.toString();
			} catch (Exception ee) {
				ee.printStackTrace();
				Log.i(TAG, ee.toString());
				result = "ERR! : " + ee.toString();
			}
			if (type.equals("appVersionCheck")) {
				VersionCheck vCheck = new VersionCheck(context);

				int currentConsentAppVersion = vCheck.getCurrentVersion("kr.co.less.consent.yjh.real"); // 업무앱
				int currentEfromAppVersion = vCheck.getCurrentVersion("kr.co.clipsoft.eform"); // 뷰어앱

				JSONObject versioncheckResult = new JSONObject();
				try {
					versioncheckResult = new JSONObject(result);
					versioncheckResult.put("consentClientVersion", Integer.toString(currentConsentAppVersion));
					versioncheckResult.put("eformClientVersion", Integer.toString(currentEfromAppVersion)); // 클라이언트 뷰어
																											// 앱 버전
					versioncheckResult.put("consentClientVersionName",
							vCheck.getCurrentVersionName("kr.co.less.consent.yjh.real"));// 클라이언트
					versioncheckResult.put("eformClientVersionName",
							vCheck.getCurrentVersionName("kr.co.clipsoft.eform"));
					versioncheckResult.put("appType", Storage.getInstance(context).getStorage("INTERFACE_USER"));
					String useRecevingRate = Storage.getInstance(context).getStorage("USE_RECEIVING_RATE"); // 수신률 제한

				} catch (JSONException e) {
					e.printStackTrace();
				}
				System.out.println("앱버전체크결과 : " + versioncheckResult.toString());

				return versioncheckResult.toString();

			} else if (type.equals("appVersionCheckList")) {
				VersionCheck vCheck = new VersionCheck(context);

				int currentConsentAppVersion = vCheck.getCurrentVersion("kr.co.less.consent.yjh.real"); // 업무앱
				int currentEfromAppVersion = vCheck.getCurrentVersion("kr.co.clipsoft.eform"); // 뷰어앱

				JSONArray versioncheckResult = new JSONArray();
				JSONArray versioncheckResults = new JSONArray();
				try {
					versioncheckResult = new JSONArray(result);
					for (int i = 0; i < versioncheckResult.length(); i++) {
						JSONObject minObj = (JSONObject) versioncheckResult.get(i);
						minObj.put("consentClientVersion", Integer.toString(currentConsentAppVersion));
						minObj.put("eformClientVersion", Integer.toString(currentEfromAppVersion)); // 클라이언트 뷰어
																									// 앱 버전
						minObj.put("consentClientVersionName",
								vCheck.getCurrentVersionName("kr.co.less.consent.yjh.real"));// 클라이언트
						minObj.put("eformClientVersionName", vCheck.getCurrentVersionName("kr.co.clipsoft.eform")); 
						versioncheckResults.put(minObj);
					}

					String useRecevingRate = Storage.getInstance(context).getStorage("USE_RECEIVING_RATE"); // 수신률 제한
																											// 사용여부

				} catch (JSONException e) {
					e.printStackTrace();
				}
				System.out.println("앱버전체크결과 : " + versioncheckResults.toString());

				return versioncheckResult.toString();
			}
			return result;
		}

	}

	protected void onProgressUpdate(String... progress) {
		// setting progress percentage
	}

	// request가 끝나고 호출되는 함수
	@Override
	protected void onPostExecute(String respones) {
		Log.i(TAG, "========= AsyncTaskFoHttp End ==================");
		if (callbackContext != null) {
			if (CommonUtil.getInstance(context).isJSONValid(respones)) {
				callbackContext.success(respones);
			} else {
				callbackContext.error(respones);
			}
		}
		LoadingBar.getInstance().init();
	};

	@Override
	protected void onCancelled() {
		// 작업이 취소된후에 호출된다.
		super.onCancelled();
		LoadingBar.getInstance().init();
	}

	public String fileDownload(URL url, String recordFullPath, String recordFileName) {
		String requestParams = "";
		String respone = "";

		HttpURLConnection con = null;
		long startServiceCallTime = System.currentTimeMillis();
		try {

			JSONObject parameter = new JSONObject();
			parameter.put("recordFullPath", recordFullPath);
			parameter.put("recordFileName", recordFileName);
			System.out.println("파일명 : " + recordFileName);
			System.out.println("파일패스 : " + recordFullPath);

			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("parameter", parameter.toString());

			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (postData.length() != 0)
					postData.append('&');
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}

			byte[] postDataBytes = postData.toString().getBytes("UTF-8");

			con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true); // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
			con.setDoInput(true); // InputStream으로 서버로 부터 응답을 받겠다는 옵션.

			con.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
			con.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("Accept", "application/json");
			con.setConnectTimeout(1000 * 10);
			con.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			con.getOutputStream().write(postDataBytes);

			int len = con.getContentLength();

			byte[] tmpByte = new byte[len];

			InputStream is = con.getInputStream();
			String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();

			File file = new File(rootPath + "/CLIPe-Form/AUDIO/" + parameter.getString("recordFileName"));
			File dir = file.getParentFile();
			if (!(dir.exists() && dir.isDirectory())) {
				dir.mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(file);
			int read;
			while (true) {

				read = is.read(tmpByte);
				if (read <= 0) {
					break;
				}
				fos.write(tmpByte, 0, read); // file 생성
			}
			is.close();
			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
			respone = e.toString();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return respone;
	}

}
