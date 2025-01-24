package kr.co.clipsoft.plugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidKmi.KmiApi;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.lumensoft.ks.KSBase64;
import com.lumensoft.ks.KSCertificate;
import com.lumensoft.ks.KSCertificateLoader;
import com.lumensoft.ks.KSCertificateManager;
import com.lumensoft.ks.KSException;
import com.lumensoft.ks.KSSign;
import com.signkorea.securedata.ProtectedData;
import com.signkorea.securedata.SecureData;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.print.PrintHelper;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import kr.co.clipsoft.eform.EFormToolkit;
import kr.co.clipsoft.util.AsyncTaskForDownload;
import kr.co.clipsoft.util.AsyncTaskForHttp;
import kr.co.clipsoft.util.CallDatepicker;
import kr.co.clipsoft.util.EFromViewer;
import kr.co.clipsoft.util.LoadingBar;
import kr.co.clipsoft.util.Storage;
import kr.co.clipsoft.util.VersionCheck;
import kr.co.clipsoft.util.biz.FontManager;

public class ClipsoftPlugin extends CordovaPlugin {

	public static final String TAG = "ClipsoftPlugin";
	private static Vector<KSCertificate> userCerts = null;
	private static String EFORM_URL = "";
	private static String SERVER_URL = "";
	private static KSCertificate userCert = null;
	private LoadingBar loading = new LoadingBar();
	private Context context;
	CordovaWebView m_oWeb;

	protected void pluginInitialize() {

	}

	final KmiApi kapi = new KmiApi();

	public Boolean setConnect(String ip, int port, CallbackContext callbackContext) {
		if (kapi.kmsConnect(ip, port)) {
			System.out.println("&&&&&&&&& : " + "성공");
			callbackContext.success("ok");
		} else {
			System.out.println("&&&&&&&&& : " + "실패");
			callbackContext.error(kapi.errorMsg());
		}
		return kapi.kmsConnect(ip, port);
	}

	public void setDisconnect(CallbackContext callbackContext) {
		if (kapi.kmsDisconnect()) {
			JSONObject jsonObject = new JSONObject();
			callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, jsonObject));
			callbackContext.success();
		} else {
			JSONObject jsonObject = new JSONObject();
			callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, jsonObject));
			callbackContext.error("fail");
		}
	}

	public void getKeyAndCert(String id, CallbackContext callbackContext) throws JSONException {
		String dn = kapi.GetKeyAndCert(id);
		// System.out.println("id : " + id);
		// System.out.println("dn : " + dn);
		if (!dn.equals("") || dn.length() != 0) {
			JSONObject jsonobj = new JSONObject();
			jsonobj.put("dn", dn);
			callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, jsonobj));
			callbackContext.success();
		} else {
			callbackContext.error(kapi.errorMsg());
		}
	}

	public void LocalDelKeyAndCert(String dn, CallbackContext callbackContext) {
		Boolean result;
		result = kapi.LocalDelKeyAndCert(dn);
		// System.out.println("LocalDelKeyAndCert result : " + result);
		// System.out.println("LocalDelKeyAndCert dn : " + dn);

		if (result) {
			callbackContext.success("ok");
		} else {

			callbackContext.error(kapi.errorMsg());
		}
	}

	public String Error_Msg() {
		return kapi.errorMsg();
	}

	public Boolean CertBatchDel(String dnsuffix) {
		// CertBatchDel("ou=테스트지점,ou=테스트회사,ou=테스트업종,o=SignKorea,c=KR");
		return kapi.CertBatchDel(dnsuffix);
	}

	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		this.context = cordova.getActivity();
		this.m_oWeb = webView;
	}

	public boolean checkPwd(String password, CallbackContext callbackContext) throws JSONException {
		try {
			userCerts = KSCertificateLoader.getUserCertificateListWithGpki(webView.getContext());
			userCerts = KSCertificateLoader.FilterByExpiredTime(userCerts);
		} catch (KSException e) {
		}
		userCert = (KSCertificate) userCerts.elementAt(0);
		// System.out.println("userCert.getPath() : " + userCert.getPath());
		boolean ret = KSCertificateManager.checkPwd(userCert.getPath(), password);
		// System.out.println("ret결과 : " + ret);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("ret", ret);
		callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, jsonObject));
		callbackContext.success();
		return ret;
	}

	private void getMessage(String plainText, String password, CallbackContext callbackContext) throws JSONException {
		Date date = new Date();
		SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd HHmmss");
		///////////////// 하이브리드
		try {
			userCerts = KSCertificateLoader.getUserCertificateListWithGpki(webView.getContext());
			userCerts = KSCertificateLoader.FilterByExpiredTime(userCerts);
		} catch (KSException e) {
			e.printStackTrace();
			// System.out.println("error : " + e.toString());
		}
		ProtectedData pwd = new SecureData(password.getBytes());

		userCert = (KSCertificate) userCerts.elementAt(0); // 인증서 캐시등록
		plainText = plainText + formats.format(date);
		String plainTxt = plainText;
		plainTxt = Base64.encodeToString(plainText.getBytes(), 0); // 원문을 base64 인코딩 함
		byte[] signResult = null;

		try {
			// signResult = KSSign.cmsSign(userCert, plainTxt, password); //원문을 pkcs#7으로 서명
			// signResult = KSSign.cmsSign(userCert, plainTxt, "gnuh123!@#");
			// signResult = KSSign.koscomSign(userCert, plainTxt, "paul2015^^"); //원문을 코스콤
			// 서명
			signResult = KSSign.sign(KSSign.KOSCOM, userCert, plainText.getBytes(), pwd);
			// System.out.println("signResult : " +signResult );

			byte[] encodedSignResult;
			encodedSignResult = KSBase64.encode(signResult);
			if (encodedSignResult != null) {
				String finalSignResult = new String(encodedSignResult); // 서명된 서명문
				if (finalSignResult != null) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("name", finalSignResult);
					callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, jsonObject));
				}
				System.out.println("서명성공");
			}
		} catch (KSException e) {
			e.printStackTrace();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", e.getMessage());
			callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, jsonObject));
			System.out.println("서명 실패");
		}
		callbackContext.success();
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

		Log.i(TAG, "=====================================================");
		Log.i(TAG, "[action] : " + action);
		Log.i(TAG, "[data] : " + args.toString());
		Log.i(TAG, "=====================================================");
		EFORM_URL = Storage.getInstance(context).getStorage("eformUrl") + "/"; // V2 path에서 eformservice.aspx => 제거
		SERVER_URL = Storage.getInstance(context).getStorage("serverUrl") + "/"; // V2 path에서 eformservice.aspx => 제거

		// e-from 연동
		if (action.equals("loadEFormViewByGuid")) {
			try {
				this.loadEFormViewByGuid(args.getString(0), args.getString(1), args.getJSONArray(2),
						args.getJSONObject(3), callbackContext);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		// 앱종료
		else if (action.equals("terminateApp")) {
			this.terminateApp();
			return true;
		}
		// Vesrion Info
		else if (action.equals("versionInfo")) {
			this.versionInfo(callbackContext);
			return true;
		}
		// Vesrion Info
		else if (action.equals("updateAppDownload")) {
			this.updateAppDownload(args.getString(0), args.getString(1));
			return true;
		}
		// 저장소
		else if (action.equals("storage")) {
			this.storage(args, callbackContext);
			return true;
		} else if (action.equals("webserive")) {
			this.webserive(args.getString(0), args.getString(1), args.getJSONObject(2), args.getString(3),
					args.getString(4), callbackContext);
			return true;
		} else if (action.equals("datepicker")) {
			this.datepicker(args.getString(0), callbackContext);
			return true;
		} else if (action.equals("certDown")) {
			this.certDown(args.getString(0), args.getString(1), callbackContext);
			return true;
		} else if (action.equals("certInit")) {
			this.certInit();
			return true;
		} else if (action.equals("compareTime")) {
			this.compareTime(args.getString(0), callbackContext);
			return true;
		} else if (action.equals("moveSetting")) {
			this.moveToSetting();
			return true;
		} else if (action.equals("setConnect")) {
			this.setConnect(args.getString(0), args.getInt(1), callbackContext);
			return true;
		} else if (action.equals("setDisconnect")) {
			this.setDisconnect(callbackContext);
			return true;
		} else if (action.equals("getKeyAndCert")) {
			this.getKeyAndCert(args.getString(0), callbackContext);
			return true;
		} else if (action.equals("LocalDelKeyAndCert")) {
			this.LocalDelKeyAndCert(args.getString(0), callbackContext);
			return true;
		} else if (action.equals("Error_Msg")) {
			this.Error_Msg();
			return true;
		} else if (action.equals("CertBatchDel")) {
			this.CertBatchDel(args.getString(0));
			return true;
		} else if (action.equals("checkPwd")) {
			this.checkPwd(args.getString(0), callbackContext);
			return true;
		} else if (action.equals("getMessage")) {
			this.getMessage(args.getString(0), args.getString(1), callbackContext);
			return true;
		} else if (action.equals("WriteJsonFile")) {
			this.WriteJsonFile(callbackContext);
			return true;
		} else if (action.equals("checkJSONFile")) {
			try {
				this.checkJSONFile(callbackContext);
			} catch (JsonIOException e) {
				e.printStackTrace();
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
			}
			return true;
		} else if (action.equals("createJSONFile")) {
			this.createJSONFile(args.getString(0), callbackContext);
			return true;
		} else if (action.equals("GetDevicesUUID")) {
			this.GetDevicesUUID(context, callbackContext);
			return true;
		} else if (action.equals("wifiCheck")) {
			this.wifiCheck(callbackContext);
			return true;
		} else if (action.equals("deleteDnValue")) {
			this.deleteDnValue(callbackContext);
			return true;
		} else if (action.equals("loadingBar")) {
			this.loadingBar(args.getString(0), args.getString(1), callbackContext);
			return true;
		} else if (action.equals("print")) {
			this.print(args.getString(0), callbackContext);
			return true;
		}
		return false;
	}

	// JSON폴더 생성 및 파일 생성
	private void WriteJsonFile(CallbackContext callbackContext) {
		String path = Environment.getExternalStorageDirectory().toString() + "/Documents";
		String file_name = path + "/" + "yjh_consent_config_edu.json";
		File dir = new File(path);
		File dir2 = new File(file_name);

		if (dir2.exists()) {
			dir2.delete();
		}

		JSONObject obj = new JSONObject();
		try { // config.json 값 수정 by sangu02 2025/01/08
			obj.put("eformUrl", "http://emrdev.yjh.com/eform");
			obj.put("serverUrl", "http://emrdev.yjh.com");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String txt = obj.toString();

		if (!dir.isDirectory()) {
			dir.mkdir();
		}
		if (!dir2.isFile()) {
			try {
				BufferedWriter fw = new BufferedWriter(new FileWriter(file_name, true));
				// 파일안에 문자열 쓰기
				fw.write(txt);
				fw.flush();
				// 객체 닫기
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void checkJSONFile(CallbackContext callbackContext) throws JsonIOException, JsonSyntaxException,
			JSONException, IOException, org.json.simple.parser.ParseException {
		String path = Environment.getExternalStorageDirectory().toString() + "/Documents";
		String file_name = path + "/" + "yjh_consent_config_edu.json";
		File dir2 = new File(file_name);
		if (dir2.isFile()) {
			String instcd = Storage.getInstance(context).getStorage("serverUrl");
			callbackContext.success(instcd.replaceAll("[^0-9]", ""));

		} else {
			callbackContext.success("false");
		}

	}

	private void createJSONFile(String instcd, CallbackContext callbackContext) {
		String path = Environment.getExternalStorageDirectory().toString() + "/Documents";
		String file_name = path + "/" + "yjh_consent_config_edu.json";
		File dir = new File(path);
		File dir2 = new File(file_name);

		if (dir2.exists()) {
			dir2.delete();
		}

		JSONObject obj = new JSONObject();
		try { // by sangu02 2025-01-08 일단 하드코딩
			obj.put("eformUrl", "http://emrdev.yjh.com/eform/");
			obj.put("serverUrl", "http://emrdev.yjh.com/");
			obj.put("instCd", 204);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String txt = obj.toString();

		if (!dir.isDirectory()) {
			dir.mkdir();
		}
		if (!dir2.isFile()) {
			try {
				BufferedWriter fw = new BufferedWriter(new FileWriter(file_name, true));
				// 파일안에 문자열 쓰기
				fw.write(txt);
				fw.flush();
				// 객체 닫기
				fw.close();
				callbackContext.success("success");
			} catch (Exception e) {
				callbackContext.success("false");
				e.printStackTrace();
			}
		}

	}

	// 공인인증서 다운로드
	private void certDown(String id, String pw, CallbackContext callbackContext) {
		String interfaceUser = Storage.getInstance(context).getStorage("INTERFACE_USER");
		// 공인인증서 사용안하는 테스트 코드
		Log.e(TAG, "[certDown] id : " + id);
		Log.e(TAG, "[certDown] pw : " + pw);
		Log.e(TAG, "[certDown] interfaceUser : " + interfaceUser);

		JSONObject data = new JSONObject();
		try {
			data.put("isCertDown", "ok");
			Storage.getInstance(context).setStorage(data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		callbackContext.success("");
	};

	// 공인인증서 초기화
	private void certInit() {
		// 공인인증서 다운로드 여부 정보 초기화
		JSONObject data = new JSONObject();
		try {
			data.put("isCertDown", "");
			data.put("CERT_PW", "");
			Storage.getInstance(context).setStorage(data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 시간 비교 함수 : 대상 시간과 시스템 시간을 비교
	private void compareTime(String serverTime, CallbackContext callbackContext) {
		String result = "";
		DateFormat formatter = null;
		long gap = 0;
		Log.i(TAG, "[compareTime] serverTime :" + serverTime);

		try {
			formatter = new SimpleDateFormat("yyyy-MM-dd KK:mm:ss", Locale.KOREA);
			Date serverDate = (Date) formatter.parse(serverTime);
			Date current = new Date();

			// 서버 시간과 단말기 시간 차
			gap = (serverDate.getTime() - current.getTime()) / 1000;

			Log.i(TAG, "[compareTime] serverDate :" + serverDate);
			Log.i(TAG, "[compareTime] currentDate :" + current);
			Log.i(TAG, "[compareTime] gap :" + gap);
			Log.i(TAG, "[compareTime] gap(M) :" + gap / 60);

			// 1분 이내 일 경우는 OK 1분 초과 이상 일 경우 단말기 시간 변경해야함.
			if (-60 <= gap && gap <= 60) {
				result = "ok";
			} else {
				result = "서버 시간 : [ " + serverDate + "]\n단말기 시간 : [ " + current
						+ "]\n서버 시간과 단말기의 시간이 1분 이상 차이납니다.\n [환경설정 -> 일반 -> 날짜 및 시간] 에서 재설정 후 사용할 수 있습니다.\n [문의 : 내선번호 2631]";
				Log.i(TAG, "서버 시간 : [ " + serverDate + "]\n단말기 시간 : [ " + current
						+ "]\n서버 시간과 단말기의 시간이 1분 이상 차이납니다.\n[환경설정 -> 일반 -> 날짜 및 시간] 에서 재설정 후 사용할 수 있습니다.\n [문의 : 내선번호 2631]");
			}

		} catch (ParseException e) {
			e.printStackTrace();
			Log.i(TAG, "[compareTime] Error : " + e.toString());
		}
		if (result.equals("ok")) {
			callbackContext.success(result);
		} else {
			callbackContext.error(result);
		}
	};

	// 셋팅 화면으로 이동
	private void moveToSetting() {
		Intent i = new Intent(android.provider.Settings.ACTION_DATE_SETTINGS);
		terminateApp();
		context.startActivity(i);
	}

	// android datepick Call
	private void datepicker(String date, CallbackContext callbackContext) {
		CallDatepicker callDatepicker = new CallDatepicker(context, callbackContext);
		callDatepicker.showDatepicker(date);
	};

	// 웹서비스 호출
	private void webserive(String serviceName, String methodName, JSONObject params, String userId, String patientCode,
			CallbackContext callbackContext) {
		String message = "";
		if (methodName.equals("GetConsentImage")) {
			message = "작성완료 이미지 다운로드 중...";
		} else if (methodName.equals("GetConsentRecord")) {
			message = "녹취 파일 다운로드 중...";
		}
		new AsyncTaskForHttp(context, message, callbackContext).execute(serviceName, methodName, params.toString(),
				userId, patientCode);
	};

	// 로컬 save data
	private void storage(JSONArray args, CallbackContext callbackContext) {
		String type;
		try {
			type = args.getString(0);
			if (type.equals("get")) {
				String value = Storage.getInstance(context).getStorage(args.getString(1));
				Log.i(TAG, "[storage] type : " + type + "/ key : " + args.getString(1) + "/ val : " + value);
				callbackContext.success(value);
			} else if (type.equals("set")) {
				Storage.getInstance(context).setStorage(args.getJSONObject(1));
				Log.i(TAG, "[storage] type : " + type + "/ params : " + args.getJSONObject(1).toString());
				if (callbackContext != null) {
					callbackContext.success();
				}
			} else {
				Storage.getInstance(context).deleteStorage();
			}
		} catch (JSONException e) {
			EFromViewer.writeLog("plugin storage error : " + e.getMessage());
			e.printStackTrace();
		}
	};

	// Device UUID 획득
	private String GetDevicesUUID(Context mContext, CallbackContext callbackContext) {
		final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String deviceId = deviceUuid.toString();
		System.out.println("디바이스 ID : " + deviceId);
		callbackContext.success(deviceId);
		return deviceId;
	}

	// 앱 종료 플러그인
	private void terminateApp() {
		Log.i(TAG, "앱 종료");
		cordova.getActivity().finish();
	}

	// e-from viewer 플러그인w
	public void loadEFormViewByGuid(String type, String op, JSONArray consents, JSONObject params,
			CallbackContext callbackContext) throws JSONException, InterruptedException, ExecutionException {
		EFromViewer eFromViewer = new EFromViewer(context, m_oWeb, params, consents, callbackContext);
		JSONObject eFromViewerOption = new JSONObject();
		String docYN = ""; // 체크해야함
		String message = "";

		try {
			FontManager.getInstance().initFontFile(context, "/CLIPe-Form/Font/", "res/font");
			FontManager.getInstance().addExtFontName("OCRAEXT.TTF", "OCR A Extended");

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			if (type.equals("new")) { // 신규일 경우
				eFromViewerOption.put("DefaultValueClear", true); // 저장된 값 초기화 여부
				eFromViewerOption.put("isOnlyPaly", false); // 녹취 모드 여부
				eFromViewerOption.put("PageTouchEnable", true);
				eFromViewerOption.put("docYN", docYN);
				eFromViewerOption.put("SaveButtonEnable", true);
				eFromViewerOption.put("lastpage", false);
			} else if (type.equals("nurscertNew")) { // 간호전용서식 신규
				eFromViewerOption.put("DefaultValueClear", true); // 저장된 값 초기화 여부
				eFromViewerOption.put("isOnlyPaly", false); // 녹취 모드 여부
				eFromViewerOption.put("PageTouchEnable", true);
				eFromViewerOption.put("docYN", docYN);
				eFromViewerOption.put("SaveButtonEnable", false);
				eFromViewerOption.put("lastpage", false);
			} else if (type.equals("nurscertTemp")) { // 간호전용서식 임시
				eFromViewerOption.put("DefaultValueClear", false); // 저장된 값 초기화 여부
				eFromViewerOption.put("isOnlyPaly", false); // 녹취 모드 여부
				eFromViewerOption.put("PageTouchEnable", true);
				eFromViewerOption.put("docYN", docYN);
				eFromViewerOption.put("SaveButtonEnable", false);
				eFromViewerOption.put("lastpage", false);
			} else if (type.equals("nurscertEnd")) { // 간호전용서식 완료
				eFromViewerOption.put("DefaultValueClear", false);
				eFromViewerOption.put("PageTouchEnable", false);
				eFromViewerOption.put("SaveButtonEnable", false);
				eFromViewerOption.put("isOnlyPaly", false);
				eFromViewerOption.put("docYN", docYN);
				eFromViewerOption.put("lastpage", false);
			} else if (type.equals("record")) { // 음성재생일 경우
				eFromViewerOption.put("DefaultValueClear", false);
				eFromViewerOption.put("isOnlyPaly", true);
				eFromViewerOption.put("docYN", docYN);
				eFromViewerOption.put("lastpage", false);
			} else if (type.equals("end")) {// 완료동의서
				eFromViewerOption.put("DefaultValueClear", false);
				eFromViewerOption.put("PageTouchEnable", false);
				eFromViewerOption.put("SaveButtonEnable", true);
				eFromViewerOption.put("isOnlyPaly", false);
				eFromViewerOption.put("docYN", docYN);
				eFromViewerOption.put("lastpage", false);
			} else if (type.equals("cosignSend")) {// 코사인 송신일경우
				eFromViewerOption.put("DefaultValueClear", false);
				eFromViewerOption.put("PageTouchEnable", false);
				eFromViewerOption.put("SaveButtonEnable", false);
				eFromViewerOption.put("isOnlyPaly", false);
				eFromViewerOption.put("docYN", docYN);
				eFromViewerOption.put("lastpage", false);
			} else if (type.equals("endAddDoc")) { // 완료동의서이면서 시술의가 미비인경우
				JSONObject jsonObj = consents.getJSONObject(0);
				eFromViewerOption.put("DefaultValueClear", false);
				eFromViewerOption.put("PageTouchEnable", false);
				String nursCertYn = jsonObj.getString("nursCertYn");
				String jobkindCd = jsonObj.getString("jobkindcd");
				if (nursCertYn.equals("N") && !jobkindCd.substring(0, 2).equals("03")) {
					eFromViewerOption.put("SaveButtonEnable", false);
				} else {
					eFromViewerOption.put("SaveButtonEnable", true);
				}
				eFromViewerOption.put("isOnlyPaly", false);
				eFromViewerOption.put("docYN", docYN);
				eFromViewerOption.put("lastpage", true);
			} else if (type.equals("cosignTemp")) {// 코사인 임시
				eFromViewerOption.put("DefaultValueClear", false);
				eFromViewerOption.put("isOnlyPaly", false);
				eFromViewerOption.put("PageTouchEnable", true);
				eFromViewerOption.put("SaveButtonEnable", false);
				eFromViewerOption.put("docYN", docYN);
				eFromViewerOption.put("lastpage", false);
			} else if (type.equals("cosignNew")) {// 코사인 신규
				eFromViewerOption.put("DefaultValueClear", false);
				eFromViewerOption.put("isOnlyPaly", false);
				eFromViewerOption.put("PageTouchEnable", true);
				eFromViewerOption.put("SaveButtonEnable", false);
				eFromViewerOption.put("docYN", docYN);
				eFromViewerOption.put("lastpage", false);
			} else { // 그 외
				eFromViewerOption.put("DefaultValueClear", false);
				eFromViewerOption.put("isOnlyPaly", false);
				eFromViewerOption.put("PageTouchEnable", true);
				eFromViewerOption.put("SaveButtonEnable", true);
				eFromViewerOption.put("docYN", docYN);
				eFromViewerOption.put("lastpage", false);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		eFromViewer.initializeToolkit(eFromViewerOption);
		String fos = eFromViewer.makeFosString(type, op, consents);// FOS 생성
		EFormToolkit toolkit = eFromViewer.getToolkit();

		String callEFormViewerResult = "";
		callEFormViewerResult = toolkit.startEFormViewer(fos); // CLIP e-Form 호출 (FOS 문자열을 인자)
		if (!callEFormViewerResult.equals("SUCCESS")) {
			Log.i(TAG, "[loadEFormViewByGuid] Start EForm FAIL");
			switch (callEFormViewerResult) {
			case "ERROR_000":
				message = "전자동의서 Viewer에서 예상치 못한 오류가 발생 하였습니다.";
				break;
			case "ERROR_001":
				message = "전자동의서 Viewer 앱이 설치 되지 않았습니다.\n어플리케이션을 재시작 해주시기 바랍니다.";
				break;
			case "ERROR_002":
				message = "전자동의서 Viewer 앱이 최신 버전이 아닙니다.\n전산정보팀에 문의 해주시기 바랍니다.";
				break;
			case "ERROR_003":
				message = "전자동의서 Viewer에 FOS 값이 비어있습니다.\n전산정보팀에 문의 해주시기 바랍니다.";
				break;
			case "ERROR_004":
				message = "전자동의서 Viewer 지정된 시간안에 다시 호출되었습니다.\n전자동의서 Viewer를 종료 후 다시 실행해 주십시오.";
				break;
			case "ERROR_005":
				message = "현재 전자동의서 Viewer가 실행중입니다.\n전자동의서 뷰어를 종료 후 다시 실행해 주십시오.";
				break;
			case "ERROR_006":
				message = "GET_TASKS 권한이 없습니다.\n전산정보팀에 문의 해주시기 바랍니다.";
				break;
			default:
				break;
			}
			callbackContext.error(message);
		} else {
			callbackContext.success();
		}
	}

	// Vesrion Info
	private void versionInfo(CallbackContext callbackContext) {
		VersionCheck vCheck = new VersionCheck(context);

		int currentConsentAppVersion = vCheck.getCurrentVersion("kr.co.less.consent.yjh.real"); // 업무앱
		int currentEfromAppVersion = vCheck.getCurrentVersion("kr.co.clipsoft.eform"); // 뷰어앱

		try {
			JSONObject params = new JSONObject();
			JSONObject result = new JSONObject();
			params.put("consentClientVersion", Integer.toString(currentConsentAppVersion)); // 클라이언트 업무 앱 버전
			params.put("eformClientVersion", Integer.toString(currentEfromAppVersion)); // 클라이언트 뷰어 앱 버전
			params.put("consentClientVersionName", vCheck.getCurrentVersionName("kr.co.less.consent.yjh.real"));// 클라이언트앱
																												// 버전
			params.put("eformClientVersionName", vCheck.getCurrentVersionName("kr.co.clipsoft.eform")); // 클라이언트 뷰어 앱 버전
			params.put("appType", Storage.getInstance(context).getStorage("INTERFACE_USER")); // 업무 앱 타입(일반, 의무기록실, 테스트)

			String useRecevingRate = Storage.getInstance(context).getStorage("USE_RECEIVING_RATE"); // 수신률 제한 사용여부
			EFromViewer.writeLog("req Url : " + EFORM_URL + "biz/nu/member/viewer/eForm25/consent/apk/version/list/get");
			EFromViewer.writeLog("parameter : " + params.toString());
			new AsyncTaskForHttp(context, "업데이트 검사중...", callbackContext).execute(
					EFORM_URL + "biz/nu/member/viewer/eForm25/consent/apk/version/list/get", "appVersionCheckList",
					params.toString(), "", "");
		} catch (JSONException e) {
			callbackContext.error(e.getMessage());
		}
	}

	// 앱 업데이트 플러그인 (URL Link 버전)
	private void updateAppDownload(String type, String apkName) {
		Log.i(TAG, "updateAppDownload");
		new AsyncTaskForDownload(context, "APK Downloading...", null).execute(apkName);
	}

	public String readFileString(String filePath) {
		String formXml = "";
		try {
			FileInputStream fis = new FileInputStream(filePath);
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(fis));

			String temp = "";
			while ((temp = bufferReader.readLine()) != null) {
				formXml += temp;
			}
			bufferReader.close();
			fis.close();
			formXml = formXml.replaceAll("\"", "'");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return formXml;
	};

	// WIFI 수신율 체크
	private void wifiCheck(CallbackContext callbackContext) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		String ssid = wifi.getConnectionInfo().getSSID(); // for geting SSID
		String bssid = wifi.getConnectionInfo().getBSSID(); // for geting BSSID
		int ipAddress = wifi.getConnectionInfo().getIpAddress(); // for geting IP Address
		int rssi = wifi.getConnectionInfo().getRssi(); // for geting RSSI
		// rssi= -96;
		callbackContext.success(rssi);
	};

	// DN 값 경로 및 파일 모두 삭제
	private void deleteDnValue(CallbackContext callbackContext) {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/NPKI/SignKorea/user";
		File deleteFolder = new File(path);
		File[] deleteFolderList = deleteFolder.listFiles();
		// System.out.println(" 파일 : " + path);

		for (int j = 0; j < deleteFolderList.length; j++) {
			String paths = deleteFolderList[j].toString();
			File asdf = new File(paths);
			// System.out.println(" 파일 : " + paths);

			File[] awfd = asdf.listFiles();
			// System.out.println(" 파일 : " + awfd.length);
			for (int k = 0; k < awfd.length; k++) {
				// System.out.println(" 파일 : " + awfd[k]);
				awfd[k].delete();
			}
			deleteFolderList[j].delete();
		}
	};

	// native 로딩바
	private void loadingBar(String show, String message, CallbackContext callbackContext) {
		if (show.equals("show")) {
			loading.show(message, context);
		} else {
			loading.hide();
		}
	};

	private void print(String path, CallbackContext callbackContext) {
		// Get the print manager.
		PrintHelper printHelper = new PrintHelper(context);
		// Set the desired scale mode.
		printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
		path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/" + path;
		System.out.println("print :: " + path);
		Uri uri = null; 
		uri = uri.parse(path);
		// Print the bitmap.
		try {
			printHelper.printBitmap("Print Bitmap", uri);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}  
 
}
