package kr.co.clipsoft.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lumensoft.ks.KSBase64;
import com.lumensoft.ks.KSCertificate;
import com.lumensoft.ks.KSCertificateLoader;
import com.lumensoft.ks.KSException;
import com.lumensoft.ks.KSSign;
import com.signkorea.securedata.ProtectedData;
import com.signkorea.securedata.SecureData;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import kr.co.clipsoft.eform.EFormToolkit;
import kr.co.clipsoft.eform.event.ExitEventArgs;
import kr.co.clipsoft.eform.event.IEventHandler;
import kr.co.clipsoft.eform.event.PenDrawingEventArgs;
import kr.co.clipsoft.eform.event.ResultEventArgs;
import kr.co.clipsoft.eform.event.ViewerActionEventArgs;
import kr.co.clipsoft.eform.information.ResultRecordFile;
import kr.co.clipsoft.eform.information.RunOption;
import kr.co.clipsoft.eform.type.enumtype.LogLevel;
import kr.co.clipsoft.eform.type.enumtype.Notification;
import kr.co.clipsoft.eform.type.enumtype.Position;
import kr.co.clipsoft.eform.type.enumtype.SaveType;
import kr.co.clipsoft.eform.type.enumtype.ScreenOrientation;
import kr.co.clipsoft.eform.type.enumtype.TextInputAreaLimit;
import kr.co.clipsoft.util.biz.DocNmCheckUtility;
import kr.co.clipsoft.util.biz.FontManager;
import kr.co.clipsoft.util.biz.SaveValidationCheckManager;

public class EFromViewer {

	private static final String TAG = "E-FORM Viewer";

	private static String EFORM_URL; // 서식 URL
	private static String SERVER_URL; // 서식 URL
	private static String MODE; // 서식 URL
	private static String INSTCODE; // 기관코드
	private static String SERVICE_URL;
	private static String REUPLOAD_FLAG;
	private static String popupMsg = "";
	private static Vector<KSCertificate> userCerts = null;
	private static KSCertificate userCert = null;
	private EFormToolkit _toolkit; // 최초 1회에 한하여 생성한다.
	private Context context;
	private CordovaWebView m_oWeb;
	private JSONObject requestOptions;
	private JSONObject requestOptionParams;
	private JSONArray consents;
	private int consentsCount;
	private int imageCount;
	private int imageCountPlus;
	private CallbackContext callbackContext;
	private String isConsentStatComparisonString;
	private boolean isOnlyPlay;
	private boolean isOnlyRead;
	private boolean isNotSave;
	private String paramUserId;
	private String paramPatientCode;
	private String uploadPath;
	private boolean lastpage;
	private String interfaceUser;
	private String interfaceType;
	private String pageOpenTime;
	private String cosignFlag;
	private String newOrTemp;
	private static KSCertificate selectedCert = null;

	
	/*
	 * @author sangU02
	 * 
	 * @since 2024/06/10
	 * 
	 * @param funcName(실행 함수명)
	 */
	public static void writeLog(String funcName) {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		File logDirectory = new File(Environment.getExternalStorageDirectory() + "/arum_log");
		if (!logDirectory.exists()) { // 파일 없으면 생성
			logDirectory.mkdir();
		}

		try (FileWriter fileWriter = new FileWriter(
				Environment.getExternalStorageDirectory() + "/arum_log/timer_log_" + dateFormat.format(now) + ".txt",
				true); PrintWriter printWriter = new PrintWriter(fileWriter)) {
			// 로그 메시지 작성
			printWriter.println("[" + now + "] : " + funcName);

		} catch (IOException e) {
			// 예외 발생 시 처리
			System.err.println("Error writing to log file: " + e.getMessage());
		}
	}
	
	public EFromViewer(Context context, CordovaWebView m_oWeb, JSONObject params, JSONArray consents,
			CallbackContext callbackContext) {
		this.context = context;
		this.m_oWeb = m_oWeb;
		this.requestOptions = params;
		this.requestOptionParams = params;
		Log.i("EFromViewer", "[requestOptions ]: " + params);

		Log.i("EFromViewer", "[consents ]: " + consents);
		this.consents = consents;
		this.callbackContext = callbackContext;
		// this.uploadPath = getUploadPath();
		this.interfaceUser = Storage.getInstance(context).getStorage("INTERFACE_USER");
		this.interfaceType = Storage.getInstance(context).getStorage("INTERFACE_TYPE");

		consentsCount = 0;
		isConsentStatComparisonString = "";
		EFORM_URL = Storage.getInstance(context).getStorage("eformUrl") + "/"; // V2 path에서 eformservice.aspx => 제거
		SERVER_URL = Storage.getInstance(context).getStorage("serverUrl") + "/"; // V2 path에서 eformservice.aspx => 제거
		INSTCODE = "204"; // by sangu02 2025-01-08 기관코드 공백으로 인한 수정
		MODE = Storage.getInstance(context).getStorage("mode");
		SERVICE_URL = Storage.getInstance(context).getStorage("serviceUrl");// + "/ConsentSvc.aspx" ;
		Log.i("EFromViewer", "[EFORM_URL ]: " + EFORM_URL);
	}

	// e-from toolkit 초기화
	@SuppressWarnings("incomplete-switch")
	public void initializeToolkit(JSONObject eFromViewerOption) throws JSONException {
		_toolkit = new EFormToolkit(context);
		String docYN = "";
		try {
			docYN = eFromViewerOption.getString("docYN");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// 뷰어 실행시 동작에 관한 기본값을 설정 하는 역할
		RunOption runOption = new RunOption();

		// OCRTAG 폰트설정
		String fontInfo = FontManager.getInstance().getExternalFontInformation();
		// default Option
		runOption.setScreenOrientation(ScreenOrientation.Sensor); // 가로세로 전환 모드
		runOption.setFirstPageLoad(true); // 서식 로딩 시 첫 페이지만 로딩
		runOption.setUseCaching(false); // 로컬 파일 생성하여 캐싱 사용 여부
		runOption.setLogLevel(LogLevel.DEBUG); // 로그 레벨
		runOption.setFosLogging(true);
		runOption.setExternalFontInformation(fontInfo);

		runOption.setTextInputAreaLimit(TextInputAreaLimit.All); // 라벨, 한줄입력글상자 , 여러줄입력글상자 범위제한 모두 적용
		runOption.setTextInputAreaLimitContainControlsSetting(true);// 텍스트 입력 제한 기능 사용 시 컨트롤의 설정을 반영할지 여부
		runOption.setTextInputAreaLimitNotification(Notification.Toast); // 범위 초과시 토스트 메시지로 표현
		//
		// // 세컨드펜세팅
		// JSONObject drawingPen = new JSONObject();
		// JSONObject color = new JSONObject();
		// JSONObject color2 = new JSONObject();
		// JSONObject secondPen = new JSONObject();
		// color.put("a", 255);
		// color.put("r", 255);
		// color.put("g", 91);
		// color.put("b", 71);
		//
		// color2.put("a", 255);
		// color2.put("r", 255);
		// color2.put("g", 99);
		// color2.put("b", 71);
		//
		// drawingPen.put("width", 25);
		// drawingPen.put("color", color);
		// drawingPen.put("retain-last-setting", true);
		//
		// secondPen.put("width", 5);
		// secondPen.put("color", color2);
		// secondPen.put("retain-last-setting", true);
		//
		// drawingPen.put("second-pen", secondPen);
		//
		// runOption.setDrawingPen(drawingPen.toString());

		// SET Toolbar Option
		try {
			// runOption record 일경우 적용 시켜야함.
			runOption.setParameterDefaultValueClear(eFromViewerOption.getBoolean("DefaultValueClear")); // 서식 저장된 파라미터
																										// 초기화
			// 음성재생 모드일 경우
			isOnlyPlay = eFromViewerOption.getBoolean("isOnlyPaly");
			isOnlyRead = eFromViewerOption.getBoolean("PageTouchEnable");
			isNotSave = eFromViewerOption.getBoolean("SaveButtonEnable");
			lastpage = eFromViewerOption.getBoolean("lastpage");
			if (isOnlyPlay) {
				runOption.setUseExitConfirmDialog(false);
				runOption.setPageTouchEnable(false); // 화면 터치 이벤트 방지(ReadOnly)
			} else if (!isOnlyRead) {
				runOption.setUseExitConfirmDialog(false);
				runOption.setPageTouchEnable(true); // 화면 터치 이벤트 방지(ReadOnly)
			}

			EFromToolBarOption eFromToolbarOption = new EFromToolBarOption(isOnlyPlay, isOnlyRead, isNotSave,
					interfaceUser, interfaceType, docYN);
			String toolbarOption = eFromToolbarOption.getToolBarOptionToString();
			Log.i(TAG, " [runOption] 사용자 타입 : " + interfaceUser);
			Log.i(TAG, " [runOption] 인터페이스 타입 : " + interfaceType);
			Log.i(TAG, " [runOption] 음성 재생 모드 : " + isOnlyPlay);
			Log.i(TAG, " [runOption] toolBarOption : " + toolbarOption + " ] ");
			runOption.setUiStyle(toolbarOption); // 툴바 및 UI 관련 옵션 설정
		} catch (JSONException e) {
			e.printStackTrace();
			Log.i(TAG, " [runOption] Exception : " + e.toString());
		}
		// 기본 옵션
		runOption.setInitializeScrollOnPageMove(true); // 페이지 이동시 스크롤 초기화

		// 입력 텍스트 박스 범위 제한 박승찬 추가
		// runOption.setTextInputAreaLimit(TextInputAreaLimit.All); // 라벨, 한줄입력글상자 ,
		// 여러줄입력글상자 범위제한 모두 적용
		// runOption.setTextInputAreaLimitContainControlsSetting(true);// 텍스트 입력 제한 기능
		// 사용 시 컨트롤의 설정을 반영할지 여부
		// runOption.setTextInputAreaLimitNotification(Notification.Toast); // 범위 초과시
		// 토스트 메시지로 표현

		// 필수입력 항상 체크여부
		runOption.setAlwaysDisplayRequiredInput(true);

		// 다중 녹취 여부
		runOption.setSaveRecordFileIntoMultipleParts(true);

		// 첨지 옵션
		runOption.setAttachPagePosition(Position.Next); // 다음 페이지에 추가 (Confirm, Previous, Next, Last)

		// 페이지템블릿 옵션
		runOption.setPageTemplatePosition(Position.Last); // 다음 페이지에 추가 (Confirm, Previous, Next, Last)

		// 카메라 첨지 옵션
		runOption.setUseCameraAttachPage(true);

		// 펜드로잉 모드일때 하단 이동 버튼 보이기 여부
		runOption.setBottomToolbarButtonsVisibleOnPenDrawMode(true);

		// SAVE OPTION
		runOption.setReturnDataXmlOnSave(true); // 저장시 dataXml 리턴 여부
		runOption.setDataXmlSaveAsFileOnSave(false); // 저장시 dataXml 리턴 시 file 여부(true => file, false => String)
		runOption.setReturnImageOnSave(true); // 저장시 이미지 파일 리턴 여부
		runOption.setImageSaveOption("{" + " 'dpi' 			: 150 " + " ,'gray' 		: false "
				+ " ,'encode' 		: 'jpg' " + " ,'start-index' 	: 0 " + " ,'quality' 		: 100 " + "}"); // 저장 시
																												// 이미지
																												// 파일 옵션

		// 저장 시 ept 파일 리턴 여부
		runOption.setReturnTempDocumentOnSave(true);
		// 저장시 dataXml에 펜드로잉 정보 저장 여부
		runOption.setIncludesDrawingOnDataXml(false);

		// TEMP SAVE OPTION
		runOption.setReturnDataXmlOnTempSave(true); // 임시 저장시 dataXml 리턴 여부
		runOption.setDataXmlSaveAsFileOnTempSave(false); // 임시 저장시 dataXml 리턴 시 file 여부(true => file, false => String)
		runOption.setIncludesDrawingOnTempDocument(true); // 임시저장시 펜드로잉 정보 저장 여부
		runOption.setParameterAsFileNameOnSave("filename"); // 파일 생성시 파일명을 설정
		runOption.setUseInputControlsInRepeatSection(true); // 머릿말
		// runOption.setReturnTempDocumentOnTempSave(false); // 저장시 ept 파일 로컬에 저장 여부
		// runOption.setDeletePrivateTempFilesForDaysFromToday(7); // 입력한 이전 날짜에 생성된
		// 외부에서 접근 못하는 영역에 저장된 임시저장 파일을 삭제함.

		// 검사실이고 임시 검색한 경우에만 해당 옵션 적용 : 사인부분으로자동 이동 기능
		// runOption.setFirstPageLoad(false);
		// runOption.setScrollPositionOnDocumentLoad(VerticalAlign.Bottom);
		if (lastpage) {
			runOption.setFirstVisiblePageOnDocumentLoad("LastPage");
		}
		//

		// 외부 사용자 컨트롤 URL 변경 : 서버가 변경될때마 서식의 URL을 변경할 수 없기 때문에 서식의 URL에 상관없이 서버의 URL로
		// 변경해줌.
		runOption.setExternalControlDefinedPath(EFORM_URL);

		// 2017.07.17 컨트롤 초기화에 실패했을 경우 저장 이벤트를 동작하지 못하게 설정
		runOption.setPreventSaveAtInitializationError(SaveType.Save); // SaveType.Save, SaveType.TempSave,
																		// SaveType.TempSave2

		// 서식 로드시에 펜드로잉 정보 사용 여부
		runOption.setPenDrawingLoadOnDocumentLoad(false);

		// 펜드로잉 제한 영역 지정 (Header, Footer, Body)
		// runOption.setPreventPenDrawingOnSection(SectionType.Header);

		runOption.setRunAsRepositoryV2(true); // 신서버 대응 API 박승찬 20190201 추가 true => V2 , false => 기존서버

		// Option Set
		_toolkit.setRunOption(runOption);

		// 펜드로잉 저장 및 불러오기 이벤트핸들러
		_toolkit.setPenDrawingEventHandler(new IEventHandler<PenDrawingEventArgs>() {
			@Override
			public void eventReceived(Object arg0, PenDrawingEventArgs event) {
				Log.e(TAG, "펜드로잉 : " + event.getPenDrawing());
				String saveResult = eformSaveDrow(event.getPenDrawing());
				if (saveResult.equals("")) {
					Log.e(TAG, "펜드로잉 저장 성공");
					_toolkit.sendEFormViewerOkEvent(); // 정상 처리 되었다고 뷰어에 이벤트 전달
				} else {
					Log.e(TAG, "펜드로잉 저장 실패 : " + saveResult);
					_toolkit.sendEFormViewerCancelEvent(saveResult);
				}
			}
		});

		// 저장, 임시저장 시 발생되는이벤트
		_toolkit.setResultEventHandler(new IEventHandler<ResultEventArgs>() {
			@Override
			public void eventReceived(Object sender, ResultEventArgs event) {

				String dataXml = ""; // dataXml // String dataXmlPath = ""; // dataXml File Path
				String formFilePath = ""; // FormXml File Path(.ept file)
				boolean DocNmCheck = true;
				boolean saveResult = false;
				String resultMessage = "";
				boolean audioUploadResult = false;
				JSONArray imageUploadResultAry = new JSONArray();
				JSONObject imageUploadResult = new JSONObject();
				String result = "";
				String defaultErrorMessage = "전자동의서 저장에 실패하였습니다.";
				Log.i(TAG, "로컬 경로 : " + Environment.getExternalStorageState());
				Log.i(TAG, " [======== 이벤트 핸들러 ========]");
				Log.i(TAG, " [ getResultCode : " + event.getResultCode() + " ]");

				long totalStartTime = System.currentTimeMillis();
				Log.i(TAG, "저장시작시간  : " + totalStartTime);
				Log.i(TAG, "[======== 저장 핸들러 ========]");
				switch (event.getResultCode()) {
				case SAVE:
					try {
						writeLog("type is save");
						dataXml = event.getDataXml(); // Data xml 문자열
						// dataXmlPath = event.getDataXmlPath(); // Data Xml Path
						formFilePath = event.getTempFilePath(); // ept 저장 경로
						consentsCount = event.getFormOpenSequence() - 1; // Form List Index
						JSONObject consent = new JSONObject(consents.getString(consentsCount));

						String userId = "";
						if (consent.getString("cosignFlag").equals("1") || consent.getString("cosignFlag").equals("2")
								|| consent.getString("cosignFlag").equals("3")) {
							userId = consent.getString("userName");
						} else {
							userId = requestOptions.getJSONObject("patient").getString("usernm");
						}

						JSONObject param_consent = requestOptionParams;

						// 신규 , 임시저장된 서식일 경우
						if (consent.isNull("consentStateDisp") || consent.getString("consentStateDisp").equals("임시")) {
							// 시술의 미비 체크 함수
							DocNmCheckUtility util = DocNmCheckUtility.getInstance();
							util.initDataXml(new File(formFilePath), userId);
							// System.out.println("3");
							if (util.getResult() == 1 || util.getResult() == 0) {
								DocNmCheck = true;
							} else {
								defaultErrorMessage = "전자동의서 저장에 실패하였습니다.\n의사/간호사 서명란에는 로그인 사용자명이 입력되어있어야 합니다.";
								DocNmCheck = false;
							}
						}
						writeLog("is name validate success");
						if (DocNmCheck) {
							// 녹취 파일 처리
							String audioPath = "";
							ArrayList<String> audiosPath = audioFileUpload(event.getAudioPath());
							for (int i = 0; i < audiosPath.size(); i++) {
								if (i == 0) {
									audioPath = audiosPath.get(i);
								} else {
									audioPath = audioPath + "@" + audiosPath.get(i);
								}
							}

							if (!event.getAudioPath().isEmpty()) {
								if (audiosPath.equals("")) {
									audioUploadResult = false;
								} else {
									audioUploadResult = true;
								}
							} else {
								audioUploadResult = true;
							}
							// 완료 이미지 처리
							ArrayList<String> imagePaths = event.getImagePath();
							imageCount = event.getTotalPagesCount();
							imageCountPlus = event.getTotalPagesCountInSystemField();

							// 저장된 이미지 경로
							String hashCode = imageHash(imagePaths);
							String signature = "";

							// System.out.println("5");
							// 전자서명
							long signatureStartTime = System.currentTimeMillis();
							JSONObject eSignResult;

							if (consent.getString("cosignFlag").equals("0")) {
								JSONObject jsonobj = requestOptions.getJSONObject("patient");
								eSignResult = electronicSignature("Y", hashCode, jsonobj.getString("certPwd"));
							} else {
								eSignResult = electronicSignature("Y", hashCode, consent.getString("certPwd"));
							}
							if (eSignResult.getString("RESULT_CODE").equals("-1")) { // 전자서명실패
								result = "signResultError";
								resultMessage = result;
								writeLog("elecSignFailed");
								// System.out.println("6");
							} else {// 전자서명성공
								signature = eSignResult.getString("RESULT_DATA");

								JSONArray imageArray = new JSONArray();
								// JSONObject timeObject = new JSONObject();
								// timeObject.put("asdf", "asdf");
								// String strDdd = service_submit(
								// EFORM_URL + "/biz/nu/member/viewer/eForm25/consent/nowtime/get", "gettime",
								// timeObject.toString());
								// JSONObject serverTime = new JSONObject(strDdd);
								// String strToday = serverTime.getString("nowTime");
								// String timeYYYYmmdd = strToday.substring(0, 10).replaceAll("-", "");

								// 저장 결과 저장
								long saveStartTime = System.currentTimeMillis();

								String patientCd = "";

								if (!consent.isNull("patientCode")) {
									patientCd = consent.getString("patientCode");
								} else {
									patientCd = consent.getString("PatientCode");
								}
								SaveValidationCheckManager svc = new SaveValidationCheckManager();
								Boolean patientCheck = svc.isSamePidAndOcrTagInForm(readFileString(formFilePath),
										patientCd, consent.getString("ocrTag"));
								writeLog("ocr validation success");
								// patientCheck = false;
								writeLog("patientCheck 결과 : " + patientCheck);

								if (patientCheck) {
									result = eformSaveData(dataXml, "save", formFilePath, imagePaths, hashCode,
											signature, audioPath, "", "");
								} else {
									result = "patientCheck";
								}
								logTimeGap("저장에 걸린 시간", saveStartTime);

								if (result.equals("consentSaveError")) {
									saveResult = false;
									resultMessage = result;
								} else if (result.equals("recordUploadError")) {
									saveResult = false;
									resultMessage = result;
								} else if (result.equals("DXMRF00115/ERROR")) {
									saveResult = false;
									resultMessage = result;
								} else if (result.equals("DXMRF00114/ERROR")) {
									saveResult = false;
									resultMessage = result;
								} else if (result.indexOf("오류가") > -1) { // 이미지업로드 submit 에러메시지
									saveResult = false;
									resultMessage = result;
								} else if (result.indexOf("ErrorCode :") > -1) { //
									saveResult = false;
									resultMessage = result;
								} else if (result.indexOf("patientCheck") > -1) { //
									saveResult = false;
									resultMessage = "내부 오류가 발생되어 저장에 실패하였습니다.\n뷰어 종료 후 재호출하여 다시 작성바랍니다.";
								} else if (result.indexOf("msgPop") > -1) { //
									saveResult = true;
									resultMessage = result;
								} else {
									saveResult = true;
									resultMessage = result;
								}

							}
						} else {
							saveResult = false;
						}

					} catch (JSONException e) {
						e.printStackTrace();
						saveResult = false;
					} catch (Exception e) {
						e.printStackTrace();
						saveResult = false;
					}
					break;

				case TEMP_SAVE:
					try {
						Log.i(TAG, " [======== 임시 저장  핸들러 ========]");
						dataXml = event.getDataXml(); // Data xml 문자열
						formFilePath = event.getTempFilePath();
						JSONObject consent;
						consentsCount = event.getFormOpenSequence() - 1; // Form List Index
						consent = new JSONObject(consents.getString(consentsCount));

						// 임저자장 파일 경로
						// dataXmlPath = event.getDataXmlPath(); // Data Xml Path
						// 녹취 파일
						// 녹취 파일 처리
						String audioPath = "";
						ArrayList<String> audiosPath = audioFileUpload(event.getAudioPath());

						for (int i = 0; i < audiosPath.size(); i++) {
							if (i == 0) {
								audioPath = audiosPath.get(i);
							} else {
								audioPath = audioPath + "@" + audiosPath.get(i);
							}
						}

						audioUploadResult = true;

						imageCount = event.getTotalPagesCount();
						ArrayList<String> sub = null;
						// 임시 저장 결과 저장
						if (audioUploadResult) {
							String patientCd = "";

							if (!consent.isNull("patientCode")) {
								patientCd = consent.getString("patientCode");
							} else {
								patientCd = consent.getString("PatientCode");
							}
							SaveValidationCheckManager svc = new SaveValidationCheckManager();
							Boolean patientCheck = svc.isSamePidAndOcrTagInForm(readFileString(formFilePath), patientCd,
									consent.getString("ocrTag"));
							// patientCheck = false;
							System.out.println("patientCheck 결과 : " + patientCheck);

							long tempSaveStartTime = System.currentTimeMillis();

							if (patientCheck) {
								result = eformSaveData(dataXml, "temp", formFilePath, sub, "", "", audioPath, "", "");
								logTimeGap("임시저장에 걸린 시간", tempSaveStartTime);
							} else {
								result = "patientCheck";
							}

							if (result.equals("consentSaveError")) {
								saveResult = false;
								resultMessage = result;
							} else if (result.equals("recordUploadError")) {
								saveResult = false;
								resultMessage = result;
							} else if (result.equals("DXMRF00115/ERROR")) {
								saveResult = false;
								resultMessage = result;
							} else if (result.equals("DXMRF00114/ERROR")) {
								saveResult = false;
								resultMessage = result;
							} else if (result.indexOf("오류가") > -1) { // 이미지업로드 submit 에러메시지
								saveResult = false;
								resultMessage = result;
							} else if (result.indexOf("ErrorCode :") > -1) { //
								saveResult = false;
								resultMessage = result;
							} else if (result.indexOf("patientCheck") > -1) { //
								saveResult = false;
								resultMessage = "내부 오류가 발생되어 저장에 실패하였습니다.\n뷰어 종료 후 재호출하여 다시 작성바랍니다.";
							} else if (result.indexOf("msgPop") > -1) { //
								saveResult = true;
								resultMessage = result;
							} else {
								saveResult = true;
								resultMessage = result;
							}
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				default:
					saveResult = false;
					defaultErrorMessage = isConsentStatComparisonString;
					break;
				}

				logTimeGap("총 저장 걸린 시간", totalStartTime);

				Log.i(TAG, " [ 저장결과 : " + resultMessage + " ]");
				Log.i(TAG, " [ 저장결과 : " + saveResult + " ]"); // 저장, 임시 저장 후에는 반드시 아래 두 결과 중 하나의 이벤트를 전달해야 한다.
				
				if (!saveResult) {// 이미지삭제처리
					// _toolkit.sendEFormViewerOkEvent(); // 정상 처리 되었다고 뷰어에 이벤트 전달 } else {
					writeLog("[저장이나 임시저장이 정상적으로 되지 않았습니다.]");
					writeLog("[저장 실패 오류 발생]"); // 정상처리가 되지 않고, 안된 이유를 문자열로 담아 뷰어에 이벤트 전달
					writeLog("result MEssage : " + resultMessage);
					// 녹취파일 유지하기위함
					if (resultMessage.indexOf("녹취 파일의 용량이 초과되었습니다") < -1) {
						CommonUtil.getInstance(context).deleteEFormdataFile();
					}

					if (!defaultErrorMessage.equals("전자동의서 저장에 실패하였습니다.\\n다시 저장해주세요.")) {
						_toolkit.sendEFormViewerCancelEvent(defaultErrorMessage + " \n" + resultMessage);
					} else {
						_toolkit.sendEFormViewerCancelEvent(resultMessage);
					}
				} else if (saveResult && resultMessage.substring(0, 3).equals("msg")) {
					CommonUtil.getInstance(context).deleteEFormdataFile(); // 이미지삭제처리
					resultMessage = resultMessage.replaceAll("msgPop", "");
					m_oWeb.loadUrl("javascript:eformReturnCallback('" + resultMessage + "');"); // 자바스크립트 함수호출
					// popupMsg = resultMessage;
					_toolkit.sendEFormViewerOkEvent(); // 정상 처리 되었다고 뷰어에 이벤트 전달

				} else {
					Log.i(TAG, "[저장이나 임시저장이 정상적으로 되었습니다.]");
					CommonUtil.getInstance(context).deleteEFormdataFile(); // 이미지삭제처리
					_toolkit.sendEFormViewerOkEvent(); // 정상 처리 되었다고 뷰어에 이벤트 전달
				}

			}
		});

		// 뷰어 종료시 발생되는 이벤트
		_toolkit.setExitEventHandler(new IEventHandler<ExitEventArgs>() {

			@Override
			public void eventReceived(Object sender, ExitEventArgs e) {
				Log.i(TAG, "[ E-Form Viewer 종료 이벤트 ] Code : " + e.getResultCode());
				switch (e.getResultCode()) {
				case EXIT: // 정상종료
					Log.i(TAG, "[정상종료]");
					CommonUtil.getInstance(context).deleteEFormdataFile();
					// 종료 후 화면 리스트 재조회
					// if(!popupMsg.equals(""))
					// m_oWeb.loadUrl("javascript:eformReturnCallback('"+ popupMsg+"');"); // 자바스크립트
					// 함수호출
					// else
					// m_oWeb.loadUrl("javascript:eformReturnCallback('');"); // 자바스크립트 함수호출

					m_oWeb.loadUrl("javascript:eformReturnCallback('');"); // 자바스크립트 함수호출
					break;
				case ERROR_EXIT: // 비정상 종료 ( 뷰어 초기화 & 서식 로드 중 오류 발생 시 )
					String errorMessage = e.getErrorMessage(); // 오류메시지
					Log.i(TAG, "[비정상종료] ErrorMessage : " + errorMessage);
					// 종료 후 화면에 에러메시지 전달
					m_oWeb.loadUrl("javascript:eformReturnCallback('" + errorMessage + "');"); // 자바스크립트 함수호출
					break;
				}
			}
		});

		// 뷰어 페이지이동 발생되는 이벤트
		_toolkit.setViewerActionEventHandler(new IEventHandler<ViewerActionEventArgs>() {
			@Override
			public void eventReceived(Object sender, ViewerActionEventArgs e) {
				switch (e.getViewerActionEventType()) {
				case MoveFirstPage: // 첫 페이지로 이동
					break;
				case MovePreviousPage: // 이전페이지
					break;
				case MoveNextPage: // 다음페이지
					break;
				case MoveLastPage: // 마지막 페이지 이동
					break;
				case MoveSelectionPage: // 특정 페이지 이동
					break;
				default:
					break;
				}
			}
		});
	}

	// fos xml 만들기
	public String makeFosString(String type, String op, JSONArray consents) {
		Log.i(TAG, "[makeFosString] type : " + type);
		Log.i(TAG, "[makeFosString] op : " + op);
		Log.i(TAG, "[makeFosString] EFORM_URL : " + EFORM_URL);
		Log.i(TAG, "[makeFosString]  consents :::: " + consents);

		newOrTemp = type;
		String fos = "";
		switch (type) {
		case "new": // 신규
			fos += "<?xml version='1.0' encoding='utf-8'?>";
			fos += "<fos version='1.0'>";
			fos += "	<global>";
			fos += "		<parameters>"; // form-list의 모든 서식에 적용될 파라미터
			fos += makeFosGlobalParameters("");
			fos += "		</parameters>";
			fos += makeFosPageTemplate(EFORM_URL);
			fos += " 	</global>";
			fos += "	<form-list>"; // 각 서식에 적용될 파라미터
			fos += makeFosFormList(type, EFORM_URL, op, consents);
			fos += "	</form-list>";
			fos += "</fos>";
			break;
		case "nurscertNew": // 간호전용서식 신규
			fos += "<?xml version='1.0' encoding='utf-8'?>";
			fos += "<fos version='1.0'>";
			fos += "	<global>";
			fos += "		<parameters>"; // form-list의 모든 서식에 적용될 파라미터
			fos += makeFosGlobalParameters("");
			fos += "		</parameters>";
			fos += makeFosPageTemplate(EFORM_URL);
			fos += " 	</global>";
			fos += "	<form-list>"; // 각 서식에 적용될 파라미터
			fos += makeFosFormList(type, EFORM_URL, op, consents);
			fos += "	</form-list>";
			fos += "</fos>";
			break;
		case "nurscertTemp": // 간호전용서식 임시
			fos += "<?xml version='1.0' encoding='utf-8'?>";
			fos += "<fos version='1.0'>";
			fos += "	<global>";
			fos += "		<parameters>"; // form-list의 모든 서식에 적용될 파라미터
			fos += "		</parameters>";
			fos += makeFosPageTemplate(EFORM_URL);
			fos += " 	</global>";
			fos += "	<form-list>";
			fos += makeFosFormList(type, EFORM_URL, op, consents);
			fos += "	</form-list>";
			fos += "</fos>";
			break;

		case "nowrite": // 처방동의서
			fos += "<?xml version='1.0' encoding='utf-8'?>";
			fos += "<fos version='1.0'>";
			fos += "	<global>";
			fos += "		<parameters>"; // form-list의 모든 서식에 적용될 파라미터
			fos += makeFosGlobalParameters("");
			fos += "		</parameters>";
			fos += makeFosPageTemplate(EFORM_URL);
			fos += " 	</global>";
			fos += "	<form-list>"; // 각 서식에 적용될 파라미터
			fos += makeFosFormList(type, EFORM_URL, op, consents);
			fos += "	</form-list>";
			fos += "</fos>";
			break;

		case "temp": // 임시
			fos += "<?xml version='1.0' encoding='utf-8'?>";
			fos += "<fos version='1.0'>";
			fos += "	<global>";
			fos += "		<parameters>"; // form-list의 모든 서식에 적용될 파라미터
			fos += "		</parameters>";
			fos += makeFosPageTemplate(EFORM_URL);
			fos += " 	</global>,";
			fos += "	<form-list>";
			fos += makeFosFormList(type, EFORM_URL, op, consents);
			fos += "	</form-list>";
			fos += "</fos>";
			break;

		case "end": // 완료
			fos += "<?xml version='1.0' encoding='utf-8'?>";
			fos += "<fos version='1.0'>";
			fos += "	<global>";
			fos += "		<parameters>"; // form-list의 모든 서식에 적용될 파라미터
			fos += "		</parameters>";
			fos += makeFosPageTemplate(EFORM_URL);
			fos += " 	</global>";
			fos += "	<form-list>";
			fos += makeFosFormList(type, EFORM_URL, op, consents);
			fos += "	</form-list>";
			fos += "</fos>";
			break;

		case "endAddDoc": // 완료+시술의 미비
			fos += "<?xml version='1.0' encoding='utf-8'?>";
			fos += "<fos version='1.0'>";
			fos += "	<global>";
			fos += "		<parameters>"; // form-list의 모든 서식에 적용될 파라미터
			fos += "		</parameters>";
			fos += makeFosPageTemplate(EFORM_URL);
			fos += " 	</global>";
			fos += "	<form-list>";
			fos += makeFosFormList(type, EFORM_URL, op, consents);
			fos += "	</form-list>";
			fos += "</fos>";
			break;

		case "nurscertEnd": // 간호서식 완료
			fos += "<?xml version='1.0' encoding='utf-8'?>";
			fos += "<fos version='1.0'>";
			fos += "	<global>";
			fos += "		<parameters>"; // form-list의 모든 서식에 적용될 파라미터
			fos += "		</parameters>";
			fos += makeFosPageTemplate(EFORM_URL);
			fos += " 	</global>";
			fos += "	<form-list>";
			fos += makeFosFormList(type, EFORM_URL, op, consents);
			fos += "	</form-list>";
			fos += "</fos>";
			break;

		case "cosignTemp": // 코사인 임시
			fos += "<?xml version='1.0' encoding='utf-8'?>";
			fos += "<fos version='1.0'>";
			fos += "	<global>";
			fos += "		<parameters>"; // form-list의 모든 서식에 적용될 파라미터
			fos += "		</parameters>";
			fos += makeFosPageTemplate(EFORM_URL);
			fos += " 	</global>";
			fos += "	<form-list>";
			fos += makeFosFormList(type, EFORM_URL, op, consents);
			fos += "	</form-list>";
			fos += "</fos>";
			break;
		case "cosignNew": // 코사인 신규
			fos += "<?xml version='1.0' encoding='utf-8'?>";
			fos += "<fos version='1.0'>";
			fos += "	<global>";
			fos += "		<parameters>"; // form-list의 모든 서식에 적용될 파라미터
			fos += "		</parameters>";
			fos += makeFosPageTemplate(EFORM_URL);
			fos += " 	</global>";
			fos += "	<form-list>";
			fos += makeFosFormList(type, EFORM_URL, op, consents);
			fos += "	</form-list>";
			fos += "</fos>";
			break;

		case "cosignSend": // 코사인 송신
			fos += "<?xml version='1.0' encoding='utf-8'?>";
			fos += "<fos version='1.0'>";
			fos += "	<global>";
			fos += "		<parameters>"; // form-list의 모든 서식에 적용될 파라미터
			fos += "		</parameters>";
			fos += makeFosPageTemplate(EFORM_URL);
			fos += " 	</global>";
			fos += "	<form-list>";
			fos += makeFosFormList(type, EFORM_URL, op, consents);
			fos += "	</form-list>";
			fos += "</fos>";
			break;

		case "rewrite":
			fos += "<?xml version='1.0' encoding='utf-8'?>";
			fos += "<fos version='1.0'>";
			fos += "	<global>";
			fos += "		<parameters>"; // form-list의 모든 서식에 적용될 파라미터
			fos += "		</parameters>";
			fos += makeFosPageTemplate(EFORM_URL);
			fos += " 	</global>";
			fos += "	<form-list>";
			fos += makeFosFormList(type, EFORM_URL, op, consents);
			fos += "	</form-list>";
			fos += "</fos>";
			break;

		case "record":
			fos += "<?xml version='1.0' encoding='utf-8'?>";
			fos += "<fos version='1.0'>";
			fos += "	<global>";
			fos += makeFosPageTemplate(EFORM_URL);
			fos += "		<parameters>"; // form-list의 모든 서식에 적용될 파라미터
			fos += "		</parameters>";
			fos += " 	</global>";
			fos += "	<form-list>";
			fos += makeFosFormList(type, EFORM_URL, op, consents);
			fos += "	</form-list>";
			fos += "</fos>";
			break;

		case "storage":
			fos += "<?xml version='1.0' encoding='utf-8' ?>";
			fos += "<fos version='1.0'>";
			fos += "	<global>";
			fos += "    	<parameters><!--모든 서식에 적용될 파라미터-->";
			fos += "		</parameters>";
			fos += "	</global>";
			fos += "	<form-list><!--각 서식별로 적용될 파라미터-->";
			fos += "		<form name='' open-sequence='1' path='" + EFORM_URL + "'>";
			fos += "			<form-get-parameters> ";
			fos += "			</form-get-parameters>";
			fos += "			<connection connection-type='xml' name='xmlConn'>";
			fos += "				<connection-prop prop-type='setdata-service-url' value='http://emrdev.ncc.re.kr/EMR/EPPSERVER/eformservice.aspx' data-post-key='DataXml'>";
			fos += "				</connection-prop>";
			fos += "			</connection>";
			fos += "			<parameters>";
			fos += "			</parameters>";
			fos += "		</form>";
			fos += "	</form-list>";
			fos += "</fos>";
			break;
		default:
			break;
		}

		return fos;
	}

	public static byte[] base64Dec(byte[] buffer) {
		return org.apache.commons.codec.binary.Base64.encodeBase64(buffer);
	}

	public static File binaryToFile(String binaryFile, String filePath, String fileName) {
		if ((binaryFile == null || "".equals(binaryFile)) || (filePath == null || "".equals(filePath))
				|| (fileName == null || "".equals(fileName))) {
			return null;
		}

		FileOutputStream fos = null;

		File fileDir = new File(filePath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		File destFile = new File(filePath + fileName);
		// System.out.println("녹취 저장경로 :" + filePath + fileName);
		byte[] buff = binaryFile.getBytes();
		String toStr = new String(buff);
		byte[] b64dec = base64Dec(buff);

		try {
			fos = new FileOutputStream(destFile);
			fos.write(b64dec);
			fos.close();
		} catch (IOException e) {
			System.out.println(
					"Exception position : FileUtil - binaryToFile(String binaryFile, String filePath, String fileName)");
		}

		return destFile;
	}

	public String makeFosFormList(String type, String path, String op, JSONArray consents) {
		String formListXml = "";
		Log.i(TAG, "[makeFosFormList] type : " + type);
		try {
			for (int i = 0; i < consents.length(); i++) {

				JSONObject consent = new JSONObject(consents.getString(i));

				// System.out.println("CONSENT ::: " + consent.toString());

				cosignFlag = consent.getString("cosignFlag");
				Boolean recordFlag = false;

				if (!consent.isNull("recordCnt")) {
					// 녹취정보 있을경우 녹취 추가
					if (!consent.getString("recordCnt").equals("0")) {
						JSONObject recordParams = new JSONObject();
						JSONObject recordDownloadParams = new JSONObject();
						// 정보조회
						recordParams.put("consentMstRid", consent.getString("consentMstRid"));
						// ******
						String respone = service_submit(EFORM_URL + "/biz/nu/member/viewer/eForm25/consent/record/get",
								type, recordParams.toString());
						JSONArray responeAry = new JSONArray(respone);
						// for (int j = 0; j < responeAry.length(); j++) {
						// System.out.println("responeAry : " + responeAry.get(j));
						// }
						// 녹취파일다운로드
						String respones = service_submit(
								EFORM_URL + "/biz/nu/member/viewer/eForm25/consent/record/download", "audioDown",
								responeAry.toString());

						recordFlag = true;
					}
				}

				// 코사인탭, 작성동의서 빠른조회에서 접근 시
				if (cosignFlag.equals("1") || cosignFlag.equals("3")) {

					String formName = "";

					if (!consent.isNull("FormName")) {
						formName = consent.getString("FormName");
					} else {
						formName = consent.getString("formName");
					}
					String fos = "<form name='" + formName + "' open-sequence='" + (i + 1) + "' path='" + EFORM_URL
							+ "/biz/nu/member/viewer/eForm25/consent/data/formxml/get' ";
					if (type.equals("end") || type.equals("endAddDoc") || type.equals("nurscertEnd")) {
						fos = fos + "document-state='completed'";
					}
					fos = fos + "><form-get-parameters>" + "<post-param key='use-repository'>" + "  <![CDATA[false]]>"
							+ "</post-param>" + "<post-param key='parameter'>" + "<![CDATA[ { \"consentMstRid\": "
							+ consent.getString("consentMstRid") + " } ]]>" + "</post-param>"
							+ " </form-get-parameters>" + "<connection connection-type='xml' name='xmlConn'>"
							+ "<connection-prop prop-type='setdata-service-url' value='" + EFORM_URL
							+ "/biz/nu/member/viewer/eForm25/consent/data/formxml/get' data-post-key='DataXml'>"
							+ "</connection-prop>" + "</connection>" + "<parameters>";
					if (type.equals("endAddDoc")) {
						JSONObject params = requestOptionParams;
						fos = fos + "<param key='treatmentnm'><![CDATA[" + params.getString("treatmentnm")
								+ "]]></param>";
						if (consent.getString("jobkindcd").substring(0, 2).equals("03")) {
							fos = fos + "<param key='atdoctnm_2'><![CDATA[" + consent.getString("userName")
									+ "]]></param>";
							String url = SERVER_URL
									+ "/cmcnu/webapps/mr/mr/formmngtweb/.live?submit_id=DRMRF02317&business_id=mr&userid="
									+ consent.getString("userId");
							String asdf = service_submit(url, "sign", "");
							fos = fos + "<param key='docSignImg_2'><![CDATA[" + asdf + "]]></param>";
						}
					} else {
						if (consent.getString("jobkindcd").substring(0, 2).equals("03") && !type.equals("end")) {
							fos = fos + "<param key='atdoctnm'><![CDATA[" + consent.getString("userName")
									+ "]]></param>";
							String signurl = SERVER_URL
									+ "/cmcnu/webapps/mr/mr/formmngtweb/.live?submit_id=DRMRF02317&business_id=mr&userid="
									+ consent.getString("userId");
							String signResults = service_submit(signurl, "sign", "");
							fos = fos + "<param key='docSignImg'><![CDATA[" + signResults + "]]></param>";

							// fos = fos + makeFosGlobalParameters(consent.getString("userId"));
						} else if (!consent.getString("jobkindcd").substring(0, 2).equals("03") && !type.equals("end")
								&& !type.equals("nurscertEnd")) {
							fos = fos + "<param key='usernm'><![CDATA[" + consent.getString("userName") + "]]></param>";
						}
					}
					// 연명의료 서식
					fos = fos + "<param key='consent_certcnt'><![CDATA[" + consent.getString("certCnt") + "]]></param>";
					fos = fos + "<param key='consent_certneedcnt'><![CDATA[" + consent.getString("consent_certneedcnt")
							+ "]]></param>";
					fos = fos + "<param key='consent_userid'><![CDATA[" + consent.getString("userId") + "]]></param>";

					// 작성상태
					if (!consent.isNull("ConsentState"))
						fos = fos + "<param key='consent_save_state'><![CDATA[" + consent.getString("ConsentState")
								+ "]]></param>";
					else
						fos = fos + "<param key='consent_save_state'><![CDATA[" + consent.getString("consentState")
								+ "]]></param>";

					if (consent.getString("jobkindcd").substring(0, 2).equals("03")) {
						if (consent.getString("certCnt").equals("0")) {
							fos += "<param key='consent_doc1_nm'><![CDATA[" + consent.getString("userName")
									+ "]]></param>";
							// 서명
							String url = SERVER_URL
									+ "/cmcnu/webapps/mr/mr/formmngtweb/.live?submit_id=DRMRF02317&business_id=mr&userid="
									+ consent.getString("userId");
							String signResult = service_submit(url, "sign", "");
							fos += "<param key='consent_doc1_sign'><![CDATA[" + signResult + "]]></param>";

							fos += "<param key='consent_doc1_licnsno'><![CDATA[" + consent.getString("licnsno")
									+ "]]></param>";
							fos += "<param key='consent_doc1_medispclno'><![CDATA[" + consent.getString("medispclno")
									+ "]]></param>";
							fos += "<param key='consent_doc1_ordfild'><![CDATA[" + consent.getString("depthngnm")
									+ "]]></param>";
						} else {
							fos += "<param key='consent_doc2_nm'><![CDATA[" + consent.getString("userName")
									+ "]]></param>";
							// 서명
							String url = SERVER_URL
									+ "/cmcnu/webapps/mr/mr/formmngtweb/.live?submit_id=DRMRF02317&business_id=mr&userid="
									+ consent.getString("userId");
							String signResult = service_submit(url, "sign", "");
							fos += "<param key='consent_doc2_sign'><![CDATA[" + signResult + "]]></param>";

							fos += "<param key='consent_doc2_licnsno'><![CDATA[" + consent.getString("licnsno")
									+ "]]></param>";
							fos += "<param key='consent_doc2_medispclno'><![CDATA[" + consent.getString("medispclno")
									+ "]]></param>";
							fos += "<param key='consent_doc2_ordfild'><![CDATA[" + consent.getString("depthngnm")
									+ "]]></param>";
						}
					}

					// formListXml += "<param key='consent_atdoctid'><![CDATA[" +
					// params.getString("atdoctid") + "]]></param>";
					// formListXml += "<param key='consent_atdoctnm'><![CDATA[" +
					// params.getString("atdoctname") + "]]></param>";
					// formListXml += "<param key='addr'><![CDATA[" +
					// params.getString("zipnm") + "]]></param>";
					// formListXml += "<param key='mpphontel'><![CDATA[" +
					// params.getString("mpphontel") + "]]></param>";
					// formListXml += "<param key='consent_fulrgstno'><![CDATA[" +
					// patientDetail.getString("fulrgstno") + "]]></param>";

					fos = fos + "</parameters>  <attachments>" + makeFosLoadPendrawing();

					if (type.equals("endAddDoc")) {
						fos = fos + "<page-template-list>" + "          <page-template path='" + EFORM_URL
								+ "' request-encode='utf-8'" + "          insert-type='FirstOrLast' "
								+ "          insert-position='Last'>" + "            <template-get-parameters>"
								+ "              <post-param key='parameter'>"
								+ "                <![CDATA[{\"formId\":\"2681\",\"formVersion\":\"-1\"}]]>"
								+ "              </post-param> " + "            </template-get-parameters>"
								+ "          </page-template>" + "        </page-template-list>";
					}

					// 2022-02-03 운영갈때 formId수정
					if(!consent.isNull("verbalMultiFlag")) {
						if(consent.getString("verbalMultiFlag").equals("V,N") || consent.getString("verbalMultiFlag").equals("M,N") ) {
							fos = fos + "<page-template-list>" + "          <page-template path='"
									+ EFORM_URL + "' request-encode='utf-8'" + "          insert-type='FirstOrLast' "
									+ "          insert-position='Last'>" + "            <template-get-parameters>"
									+ "              <post-param key='parameter'>"
									+ "                <![CDATA[{\"formId\":\"7714\",\"formVersion\":\"-1\"}]]>"
									+ "              </post-param> " + "            </template-get-parameters>"
									+ "          </page-template>" + "        </page-template-list>";
						}
					}
					
					if (recordFlag) {
						fos = fos + makeFosRecordFiles() + "</attachments>" + "</form>";
					} else {
						fos = fos + "</attachments>" + "</form>";
					}
					formListXml = fos;
				} else if (cosignFlag.equals("2")) { // 작성동의서 빠른 조회 - 처방동의서
					SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date time = new Date();
					String ClientTime = format1.format(time);

					pageOpenTime = ClientTime;
					paramUserId = consent.getString("userId");

					String guid = UUID.randomUUID().toString();
					formListXml += "<form name= ' " + consent.getString("formPrntNm") + " ' open-sequence='" + (i + 1)
							+ "' path='" + path + "' request-encode='utf-8' response-encode='utf-8'>";

					formListXml += "   <parameters>";
					formListXml += "      <param key='filename'><![CDATA[" + guid + "]]></param>"; // 저장시 레코드나 이미지

					formListXml += "      <param key='formCd'><![CDATA[" + consent.getString("formCd") + "]]></param>"; // V2에서
					formListXml += "      <param key='ocrtag'><![CDATA[ +" + consent.getString("ocrTag")
							+ "+ ]]></param>"; // ocrTag

					formListXml += "      <param key='I_FORM_VERSION'><![CDATA[" + consent.getString("formVersion")
							+ "]]></param>";
					formListXml += "      <param key='formnm'><![CDATA[" + consent.getString("formPrntNm")
							+ "]]></param>"; //
					formListXml += "      <param key='formnm1'><![CDATA[" + consent.getString("formPrntNm")
							+ "]]></param>"; //
					formListXml += "      <param key='PID'><![CDATA[" + consent.getString("patientCode")
							+ "]]></param>";
					formListXml += "      <param key='bottom_user'><![CDATA[" + consent.getString("userName")
							+ "]]></param>";
					formListXml += "      <param key='logo_imge'><![CDATA["
							+ "http://emr013.cmcnu.or.kr/cmcnu/webapps/images/report/biglogo013.png" + "]]></param>";
					formListXml += "      <param key='PATNM'><![CDATA[" + consent.getString("patientName")
							+ "]]></param>";
					// 20210722
					if (consent.isNull("orderDate")) {
						formListXml += "      <param key='orddd'><![CDATA["
								+ consent.getString("clnDate").substring(0, 4) + "/"
								+ consent.getString("clnDate").substring(4, 6) + "/"
								+ consent.getString("clnDate").substring(6, 8) + "]]></param>";
					} else {
						formListXml += "      <param key='orddd'><![CDATA["
								+ consent.getString("orderDate").substring(0, 4) + "/"
								+ consent.getString("orderDate").substring(4, 6) + "/"
								+ consent.getString("orderDate").substring(6, 8) + "]]></param>";
					}
					formListXml += "      <param key='ROOMCD'><![CDATA[" + consent.getString("roomCd") + "]]></param>";

					formListXml += "      <param key='rrgstno'><![CDATA[" + consent.getString("rrgstNo").substring(0, 8)
							+ "XXXXXX" + "]]></param>";
					formListXml += "      <param key='ORDDEPTCD'><![CDATA[" + consent.getString("clnDeptAbbr")
							+ "]]></param>";

					formListXml += "      <param key='bottom_time'><![CDATA[" + ClientTime + "]]></param>";
					formListXml += "      <param key='barcode'><![CDATA[" + consent.getString("ocrTag") + "]]></param>";

					// ===========================20200626 환자 정보추가
					if (!consent.isNull("brthdd")) {
						formListXml += "      <param key='brthdd'><![CDATA[" + consent.getString("brthdd")
								+ "]]></param>";
					}
					if (!consent.isNull("zipnm")) {
						formListXml += "      <param key='addr'><![CDATA[" + consent.getString("zipnm") + "]]></param>";
					}
					if (!consent.isNull("mpphontel")) {
						formListXml += "      <param key='telno'><![CDATA[" + consent.getString("mpphontel")
								+ "]]></param>";
						formListXml += "      <param key='mpphontel'><![CDATA[" + consent.getString("mpphontel")
								+ "]]></param>";
					}
					if (!consent.isNull("hometel")) {
						formListXml += "      <param key='hometel'><![CDATA[" + consent.getString("hometel")
								+ "]]></param>";
					}
					if (!consent.isNull("patientSex")) {
						formListXml += "      <param key='sex'><![CDATA["
								+ (consent.getString("patientSex").equals("F") ? "여자" : "남자") + "]]></param>";
					}
					// 작성상태 - 처방동의서 신규이므로 공백
					formListXml = formListXml + "<param key='consent_save_state'><![CDATA[]]></param>";

					// ============================================

					// 진단명
					if (!consent.isNull("diagNm") || !consent.getString("diagNm").equals("")) {
						formListXml += " <param key='diaghngnm'><![CDATA[" + consent.getString("diagNm")
								+ "]]></param>";
					}
					// 진단명 영문
					if (!consent.isNull("diagEngNm") || !consent.getString("diagEngNm").equals("")) {
						formListXml += " <param key='diagengnm'><![CDATA[" + consent.getString("diagEngNm")
								+ "]]></param>";
					}

					// 연명의료서식
					formListXml += "<param key='consent_certcnt'><![CDATA[0]]></param>";
					formListXml += "<param key='consent_certneedcnt'><![CDATA["
							+ consent.getString("consent_certneedcnt") + "]]></param>";
					formListXml += "<param key='consent_userid'><![CDATA[" + consent.getString("userId")
							+ "]]></param>";

					if (consent.getString("jobkindcd").substring(0, 2).equals("03")) {
						formListXml = formListXml + "<param key='atdoctnm'><![CDATA[" + consent.getString("userName")
								+ "]]></param>";
						String signurl = SERVER_URL
								+ "/cmcnu/webapps/mr/mr/formmngtweb/.live?submit_id=DRMRF02317&business_id=mr&userid="
								+ consent.getString("userId");
						String signResults = service_submit(signurl, "sign", "");
						formListXml = formListXml + "<param key='docSignImg'><![CDATA[" + signResults + "]]></param>";

						if (consent.getString("certCnt").equals("0")) {
							formListXml += "<param key='consent_doc1_nm'><![CDATA[" + consent.getString("userName")
									+ "]]></param>";
							// 서명
							String url = SERVER_URL
									+ "/cmcnu/webapps/mr/mr/formmngtweb/.live?submit_id=DRMRF02317&business_id=mr&userid="
									+ consent.getString("userId");
							String signResult = service_submit(url, "sign", "");
							formListXml += "<param key='consent_doc1_sign'><![CDATA[" + signResult + "]]></param>";

							formListXml += "<param key='consent_doc1_licnsno'><![CDATA[" + consent.getString("licnsno")
									+ "]]></param>";
							formListXml += "<param key='consent_doc1_medispclno'><![CDATA["
									+ consent.getString("medispclno") + "]]></param>";
							formListXml += "<param key='consent_doc1_ordfild'><![CDATA["
									+ consent.getString("depthngnm") + "]]></param>";
						} else {
							formListXml += "<param key='consent_doc2_nm'><![CDATA[" + consent.getString("userName")
									+ "]]></param>";
							// 서명
							String url = SERVER_URL
									+ "/cmcnu/webapps/mr/mr/formmngtweb/.live?submit_id=DRMRF02317&business_id=mr&userid="
									+ consent.getString("userId");
							String signResult = service_submit(url, "sign", "");
							formListXml += "<param key='consent_doc2_sign'><![CDATA[" + signResult + "]]></param>";

							formListXml += "<param key='consent_doc2_licnsno'><![CDATA[" + consent.getString("licnsno")
									+ "]]></param>";
							formListXml += "<param key='consent_doc2_medispclno'><![CDATA["
									+ consent.getString("medispclno") + "]]></param>";
							formListXml += "<param key='consent_doc2_ordfild'><![CDATA["
									+ consent.getString("depthngnm") + "]]></param>";
						}
					}

					String tmp01 = consent.getString("tmp01") == "null" ? "" : consent.getString("tmp01");
					String tmp02 = consent.getString("tmp02") == "null" ? "" : consent.getString("tmp02");
					String tmp03 = consent.getString("tmp03") == "null" ? "" : consent.getString("tmp03");
					String tmp04 = consent.getString("tmp04") == "null" ? "" : consent.getString("tmp04");
					String tmp05 = consent.getString("tmp05") == "null" ? "" : consent.getString("tmp05");
					String tmp06 = consent.getString("tmp06") == "null" ? "" : consent.getString("tmp06");
					String tmp07 = consent.getString("tmp07") == "null" ? "" : consent.getString("tmp07");
					String tmp08 = consent.getString("tmp08") == "null" ? "" : consent.getString("tmp08");
					String tmp09 = consent.getString("tmp09") == "null" ? "" : consent.getString("tmp09");
					String tmp10 = consent.getString("tmp10") == "null" ? "" : consent.getString("tmp10");

					formListXml += "<param key='tmp01'><![CDATA[" + tmp01 + "]]></param>";
					formListXml += "<param key='tmp02'><![CDATA[" + tmp02 + "]]></param>";
					formListXml += "<param key='tmp03'><![CDATA[" + tmp03 + "]]></param>";
					formListXml += "<param key='tmp04'><![CDATA[" + tmp04 + "]]></param>";
					formListXml += "<param key='tmp05'><![CDATA[" + tmp05 + "]]></param>";
					formListXml += "<param key='tmp06'><![CDATA[" + tmp06 + "]]></param>";
					formListXml += "<param key='tmp07'><![CDATA[" + tmp07 + "]]></param>";
					formListXml += "<param key='tmp08'><![CDATA[" + tmp08 + "]]></param>";
					formListXml += "<param key='tmp09'><![CDATA[" + tmp09 + "]]></param>";
					formListXml += "<param key='tmp10'><![CDATA[" + tmp10 + "]]></param>";

					// formListXml += "<param key='consent_atdoctid'><![CDATA[" +
					// params.getString("atdoctid") + "]]></param>";
					// formListXml += "<param key='consent_atdoctnm'><![CDATA[" +
					// params.getString("atdoctname") + "]]></param>";
					// formListXml += "<param key='addr'><![CDATA[" +
					// params.getString("zipnm") + "]]></param>";
					// formListXml += "<param key='mpphontel'><![CDATA[" +
					// params.getString("mpphontel") + "]]></param>";
					// formListXml += "<param key='consent_fulrgstno'><![CDATA[" +
					// patientDetail.getString("fulrgstno") + "]]></param>";

					// ---------------------------------------------------
					// 20210722 작성 동의서 빠른조회 비급여 동의서 항목 추가

					if (!consent.isNull("tmp01") && consent.getString("tmp01").equals("NONPAY")) {
						for (int j = 0; j <= Integer.parseInt(consent.getString("nonIndex")) - 1; j++) {
							formListXml = formListXml + "<param key='nonBene" + j + "'><![CDATA["
									+ consent.getString("nonBene" + j) + "]]></param>";
							formListXml = formListXml + "<param key='estiCost" + j + "'><![CDATA["
									+ consent.getString("estiCost" + j) + "]]></param>";
						}
					}

					// ---------------------------------------------------
					// ---------------------------------------------------
					// 20210630 일반 동의서 비급여 항목 추가

					if (!consent.isNull("addform") && consent.getString("addform").equals("Y2")) {
						for (int j = 0; j <= Integer.parseInt(consent.getString("nonIndex")) - 1; j++) {
							formListXml = formListXml + "<param key='addnonBene" + j + "'><![CDATA["
									+ consent.getString("addnonBene" + j) + "]]></param>";
							formListXml = formListXml + "<param key='addestiCost" + j + "'><![CDATA["
									+ consent.getString("addestiCost" + j) + "]]></param>";
						}
					}

					// ---------------------------------------------------

					formListXml += "   </parameters>";
					formListXml += "   <attachments>";

					// ============================================
					// 펜드로잉 정보 가져오기
					formListXml += makeFosLoadPendrawing();

					// ---------------------------------------------------
					// 20210630 일반 동의서 비급여 항목 추가
					if (!consent.isNull("addform")) {
						if (consent.getString("addform").equals("Y2")) {
							formListXml = formListXml + "<page-template-list>" + "          <page-template path='"
									+ EFORM_URL + "' request-encode='utf-8'" + "          insert-type='FirstOrLast' "
									+ "          insert-position='Last'>" + "            <template-get-parameters>"
									+ "              <post-param key='parameter'>"
									+ "                <![CDATA[{\"formId\":\"2681\",\"formVersion\":\"-1\"}]]>"
									+ "              </post-param> " + "            </template-get-parameters>"
									+ "          </page-template>" + "        </page-template-list>";
						}
					}
					// ---------------------------------------------------

					formListXml += "   </attachments>";
					formListXml += "   <form-get-parameters>";
					formListXml += "      <post-param key='parameter'><![CDATA[{\"formId\":\""
							+ consent.getString("formId") + "\",\r\n";
					formListXml += "         \"formVersion\":\"-1\"}]]></post-param>";
					formListXml += "   </form-get-parameters>";
					formListXml += "</form>";

				} else {
					JSONObject params = requestOptions.getJSONObject("patient");
					JSONObject patientDetail = new JSONObject(requestOptions.getString("detail"));
					SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date time = new Date();
					String ClientTime = format1.format(time);
					// System.out.println("PARAMS ::: " + params.toString());
					// System.out.println("PATIENTDETAIL ::: " + patientDetail.toString());

					pageOpenTime = ClientTime;
					paramUserId = params.getString("userId");
					// 신규서식일때
					if (type.equals("new") || type.equals("cosignNew") || type.equals("nurscertNew")
							|| type.equals("nowrite")) {

						paramPatientCode = patientDetail.getString("PatientCode");

						String guid = UUID.randomUUID().toString();
						formListXml += "<form name= ' " + consent.getString("FormPrntNm") + " ' open-sequence='"
								+ (i + 1) + "' path='" + path + "' request-encode='utf-8' response-encode='utf-8'>";

						formListXml += "   <parameters>";
						formListXml += "      <param key='filename'><![CDATA[" + guid + "]]></param>"; // 저장시 레코드나 이미지

						formListXml += "      <param key='formCd'><![CDATA[" + consent.getString("FormCd")
								+ "]]></param>"; // V2에서
						formListXml += "      <param key='ocrtag'><![CDATA[ +" + consent.getString("ocrTag")
								+ "+ ]]></param>"; // ocrTag

						formListXml += "      <param key='I_FORM_VERSION'><![CDATA[" + consent.getString("FormVersion")
								+ "]]></param>";
						formListXml += "      <param key='formnm'><![CDATA[" + consent.getString("FormPrntNm")
								+ "]]></param>"; //
						formListXml += "      <param key='formnm1'><![CDATA[" + consent.getString("FormPrntNm")
								+ "]]></param>"; //
						formListXml += "      <param key='PID'><![CDATA[" + paramPatientCode + "]]></param>";
						formListXml += "      <param key='bottom_user'><![CDATA[" + params.getString("usernm")
								+ "]]></param>";
						formListXml += "      <param key='bottom_time'><![CDATA[" + ClientTime + "]]></param>";
						formListXml += "      <param key='barcode'><![CDATA[" + consent.getString("ocrTag")
						+ "]]></param>";
						 
						// 연명의료 서식
						formListXml += "<param key='consent_certcnt'><![CDATA[0]]></param>";
						formListXml += "<param key='consent_certneedcnt'><![CDATA["
								+ consent.getString("consent_certneedcnt") + "]]></param>";
						formListXml += "<param key='consent_userid'><![CDATA[" + params.getString("userId")
								+ "]]></param>";

						if (params.getString("jobkindcd").substring(0, 2).equals("03")) {
							formListXml += "<param key='consent_doc1_nm'><![CDATA[" + params.getString("usernm")
									+ "]]></param>";
							// 서명
							String url = SERVER_URL
									+ "/cmcnu/webapps/mr/mr/formmngtweb/.live?submit_id=DRMRF02317&business_id=mr&userid="
									+ params.getString("userId");
							String signResult = service_submit(url, "sign", "");
							formListXml += "<param key='consent_doc1_sign'><![CDATA[" + signResult + "]]></param>";
							formListXml += "<param key='consent_doc1_licnsno'><![CDATA[" + params.getString("LicenceNo")
									+ "]]></param>";
							formListXml += "<param key='consent_doc1_medispclno'><![CDATA["
									+ params.getString("medispclno") + "]]></param>";
							formListXml += "<param key='consent_doc1_ordfild'><![CDATA[" + params.getString("depthngnm")
									+ "]]></param>";
						}
						formListXml += "<param key='consent_atdoctid'><![CDATA[" + params.getString("orddrid")
								+ "]]></param>";
						formListXml += "<param key='consent_atdoctnm'><![CDATA[" + params.getString("orddrnm")
								+ "]]></param>";
						formListXml += "<param key='addr'><![CDATA[" + params.getString("zipnm") + "]]></param>";
						formListXml += "<param key='mpphontel'><![CDATA[" + params.getString("mpphontel")
								+ "]]></param>";
						formListXml += "<param key='consent_fulrgstno'><![CDATA[" + params.getString("rrgstfullno")
								+ "]]></param>";
						if (type.equals("nowrite")) {
							formListXml += "<param key='tmp01'><![CDATA[" + consent.getString("tmp01") + "]]></param>";
							formListXml += "<param key='tmp02'><![CDATA[" + consent.getString("tmp02") + "]]></param>";
							formListXml += "<param key='tmp03'><![CDATA[" + consent.getString("tmp03") + "]]></param>";
							formListXml += "<param key='tmp04'><![CDATA[" + consent.getString("tmp04") + "]]></param>";
							formListXml += "<param key='tmp05'><![CDATA[" + consent.getString("tmp05") + "]]></param>";
							formListXml += "<param key='tmp06'><![CDATA[" + consent.getString("tmp06") + "]]></param>";
							formListXml += "<param key='tmp07'><![CDATA[" + consent.getString("tmp07") + "]]></param>";
							formListXml += "<param key='tmp08'><![CDATA[" + consent.getString("tmp08") + "]]></param>";
							formListXml += "<param key='tmp09'><![CDATA[" + consent.getString("tmp09") + "]]></param>";
							formListXml += "<param key='tmp10'><![CDATA[" + consent.getString("tmp10") + "]]></param>";

						}

						// ---------------------------------------------------
						// 20210603 일반 동의서 비급여 항목 추가
						if (!consent.isNull("addform") && consent.getString("addform").equals("Y")) {
							for (int j = 0; j <= Integer.parseInt(consent.getString("nonIndex")) - 1; j++) {
								formListXml = formListXml + "<param key='addnonBene" + j + "'><![CDATA["
										+ consent.getString("addnonBene" + j) + "]]></param>";
								formListXml = formListXml + "<param key='addestiCost" + j + "'><![CDATA["
										+ consent.getString("addestiCost" + j) + "]]></param>";
							}
						}
						// ---------------------------------------------------

						// 작성상태 - 신규는 공백으로
						formListXml = formListXml + "<param key='consent_save_state'><![CDATA[]]></param>";

						formListXml += "   </parameters>";
						formListXml += "   <attachments>";

						// ============================================
						// 펜드로잉 정보 가져오기
						formListXml += makeFosLoadPendrawing();

						// ---------------------------------------------------
						// 20210603 일반 동의서 비급여 항목 추가
						if (!consent.isNull("addform")) {
							if (consent.getString("addform").equals("Y")) {
								formListXml = formListXml + "<page-template-list>" + "          <page-template path='"
										+ EFORM_URL + "' request-encode='utf-8'"
										+ "          insert-type='FirstOrLast' " + "          insert-position='Last'>"
										+ "            <template-get-parameters>"
										+ "              <post-param key='parameter'>"
										+ "                <![CDATA[{\"formId\":\"2681\",\"formVersion\":\"-1\"}]]>"
										+ "              </post-param> " + "            </template-get-parameters>"
										+ "          </page-template>" + "        </page-template-list>";
							}
						}
						// ---------------------------------------------------

						formListXml += "   </attachments>";
						formListXml += "   <form-get-parameters>";
						if (type.equals("new") || type.equals("cosignNew") || type.equals("nurscertNew")
								|| type.equals("nowrite")) {
							formListXml += "      <post-param key='parameter'><![CDATA[{\"formId\":\""
									+ consent.getString("FormId") + "\",\r\n";
							formListXml += "         \"formVersion\":\"-1\"}]]></post-param>";
						} else {
							formListXml += "      <post-param key='use-repository'><![CDATA[false]]></post-param>";
							formListXml += "      <post-param key='methodName'><![CDATA[GetTempSaveXml]]></post-param>";
							formListXml += "      <post-param key='params'><![CDATA["
									+ consent.getString("ConsentMstRid") + "]]></post-param>";
						}
						formListXml += "   </form-get-parameters>";
						formListXml += "</form>";

					} else {// 신규아닌서식들

						String fos = "<form name='" + consent.getString("FormName") + "' open-sequence='" + (i + 1)
								+ "' path='" + EFORM_URL + "/biz/nu/member/viewer/eForm25/consent/data/formxml/get' ";
						if (type.equals("end") || type.equals("endAddDoc") || type.equals("nurscertEnd")) {
							fos = fos + "document-state='completed'";
						}
						fos = fos + "><form-get-parameters>" + "<post-param key='use-repository'>"
								+ "  <![CDATA[false]]>" + "</post-param>" + "<post-param key='parameter'>"
								+ "<![CDATA[ { \"consentMstRid\": " + consent.getString("consentMstRid") + " } ]]>"
								+ "</post-param>" + " </form-get-parameters>"
								+ "<connection connection-type='xml' name='xmlConn'>"
								+ "<connection-prop prop-type='setdata-service-url' value='" + EFORM_URL
								+ "/biz/nu/member/viewer/eForm25/consent/data/formxml/get' data-post-key='DataXml'>"
								+ "</connection-prop>" + "</connection>" + "<parameters>";
						if (type.equals("endAddDoc")) {
							fos = fos + "<param key='treatmentnm'><![CDATA[" + params.getString("treatmentnm")
									+ "]]></param>";
							if (consent.getString("jobkindcd").substring(0, 2).equals("03")) {
								fos = fos + "<param key='atdoctnm_2'><![CDATA[" + consent.getString("userName")
										+ "]]></param>";
								String url = SERVER_URL
										+ "/cmcnu/webapps/mr/mr/formmngtweb/.live?submit_id=DRMRF02317&business_id=mr&userid="
										+ params.getString("userId");
								String asdf = service_submit(url, "sign", "");
								fos = fos + "<param key='docSignImg_2'><![CDATA[" + asdf + "]]></param>";
							}
						} else {
							if (consent.getString("jobkindcd").substring(0, 2).equals("03") && !type.equals("end")
									&& !type.equals("nurscertEnd")) {
								fos = fos + "<param key='atdoctnm'><![CDATA[" + consent.getString("userName")
										+ "]]></param>";

								String signurl = SERVER_URL
										+ "/cmcnu/webapps/mr/mr/formmngtweb/.live?submit_id=DRMRF02317&business_id=mr&userid="
										+ consent.getString("userId");
								String signResults = service_submit(signurl, "sign", "");
								fos = fos + "<param key='docSignImg'><![CDATA[" + signResults + "]]></param>";

								// fos = fos + makeFosGlobalParameters("");
							} else if (!consent.getString("jobkindcd").substring(0, 2).equals("03")
									&& !type.equals("end") && !type.equals("nurscertEnd")) {
								fos = fos + "<param key='usernm'><![CDATA[" + consent.getString("userName")
										+ "]]></param>";
							}
						}

						// 작성상태
						if (!consent.isNull("ConsentState"))
							fos = fos + "<param key='consent_save_state'><![CDATA[" + consent.getString("ConsentState")
									+ "]]></param>";
						else
							fos = fos + "<param key='consent_save_state'><![CDATA[" + consent.getString("consentState")
									+ "]]></param>";

						// 연명의료 서식
						fos = fos + "<param key='consent_certcnt'><![CDATA[" + consent.getString("certCnt")
								+ "]]></param>";
						fos = fos + "<param key='consent_certneedcnt'><![CDATA["
								+ consent.getString("consent_certneedcnt") + "]]></param>";
						fos = fos + "<param key='consent_userid'><![CDATA[" + params.getString("userId")
								+ "]]></param>";

						if (params.getString("jobkindcd").substring(0, 2).equals("03")) {
							if (consent.getString("certCnt").equals("0")) {
								fos += "<param key='consent_doc1_nm'><![CDATA[" + params.getString("usernm")
										+ "]]></param>";
								// 서명
								String url = SERVER_URL
										+ "/cmcnu/webapps/mr/mr/formmngtweb/.live?submit_id=DRMRF02317&business_id=mr&userid="
										+ params.getString("userId");
								String signResult = service_submit(url, "sign", "");

								fos += "<param key='consent_doc1_sign'><![CDATA[" + signResult + "]]></param>";

								fos += "<param key='consent_doc1_licnsno'><![CDATA[" + params.getString("LicenceNo")
										+ "]]></param>";
								fos += "<param key='consent_doc1_medispclno'><![CDATA[" + params.getString("medispclno")
										+ "]]></param>";
								fos += "<param key='consent_doc1_ordfild'><![CDATA[" + params.getString("depthngnm")
										+ "]]></param>";
							} else {
								fos += "<param key='consent_doc2_nm'><![CDATA[" + params.getString("usernm")
										+ "]]></param>";
								// 서명
								String url = SERVER_URL
										+ "/cmcnu/webapps/mr/mr/formmngtweb/.live?submit_id=DRMRF02317&business_id=mr&userid="
										+ params.getString("userId");
								String signResult = service_submit(url, "sign", "");
								fos += "<param key='consent_doc2_sign'><![CDATA[" + signResult + "]]></param>";

								fos += "<param key='consent_doc2_licnsno'><![CDATA[" + params.getString("LicenceNo")
										+ "]]></param>";
								fos += "<param key='consent_doc2_medispclno'><![CDATA[" + params.getString("medispclno")
										+ "]]></param>";
								fos += "<param key='consent_doc2_ordfild'><![CDATA[" + params.getString("depthngnm")
										+ "]]></param>";
							}
						}

						// formListXml += "<param key='consent_atdoctid'><![CDATA[" +
						// params.getString("atdoctid") + "]]></param>";
						// formListXml += "<param key='consent_atdoctnm'><![CDATA[" +
						// params.getString("atdoctname") + "]]></param>";
						// formListXml += "<param key='addr'><![CDATA[" +
						// params.getString("zipnm") + "]]></param>";
						// formListXml += "<param key='mpphontel'><![CDATA[" +
						// params.getString("mpphontel") + "]]></param>";
						// formListXml += "<param key='consent_fulrgstno'><![CDATA[" +
						// params.getString("rrgstfullno") + "]]></param>";

						fos = fos + "</parameters>  <attachments>" + makeFosLoadPendrawing();
						
						// 2022-02-03 운영갈때 formId수정
						if(!consent.isNull("verbalMultiFlag")) {
							if(consent.getString("verbalMultiFlag").equals("V,N") || consent.getString("verbalMultiFlag").equals("M,N") ) {
								fos = fos + "<page-template-list>" + "          <page-template path='"
										+ EFORM_URL + "' request-encode='utf-8'" + "          insert-type='FirstOrLast' "
										+ "          insert-position='Last'>" + "            <template-get-parameters>"
										+ "              <post-param key='parameter'>"
										+ "                <![CDATA[{\"formId\":\"7714\",\"formVersion\":\"-1\"}]]>"
										+ "              </post-param> " + "            </template-get-parameters>"
										+ "          </page-template>" + "        </page-template-list>";
							}
						}
							

						if (recordFlag) {
							fos = fos + makeFosRecordFiles() + "</attachments>" + "</form>";
						} else {
							fos = fos + "</attachments>" + "</form>";
						}
						formListXml = fos;
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			// fos 확인 위한 txt파일 생성
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ERRORTEXT.txt");
			try {
				FileWriter fw = new FileWriter(file, true);
				fw.write(e.toString());
				fw.flush();
				fw.close();
			} catch (IOException es) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.i(TAG, "formListXml : " + formListXml);
		return formListXml;
	};

	public String makeFosPageTemplate(String path) {
		String parameters = "";
		parameters += "<page-template path='" + path + "' request-encode ='utf-8' response-encode ='utf-8'>";
		parameters += "	<template-get-parameters>";
		parameters += "		<post-param key='parameter'><![CDATA[{}]]>";
		// parameters += " <![CDATA[PAGE_TEMPLATE]]>";
		// parameters += " </post-param>";
		// parameters += " <post-param key='PD'>";
		// parameters += " <![CDATA[";
		// parameters += " <data>";
		// parameters += " <action>GET_LIST</action>";
		// parameters += " <params>";
		// parameters += " <param name='adaptername'>defaultadapter</param>";
		// parameters += " </params>";
		// parameters += " </data>";
		// parameters += " ]]>";
		parameters += "	 	</post-param>";
		parameters += "	</template-get-parameters>";
		parameters += "</page-template>";
		Log.i(TAG, "여기까지?2");
		return parameters;
	}

	// FOS에 펜드로잉 정보 불러오기
	public String makeFosLoadPendrawing() throws JSONException {

		String penDrawingUrl = EFORM_URL + "/biz/nu/member/viewer/eForm25/consent/drow/get";

		String parameters = "";
		try {
			JSONObject params = new JSONObject();
			if (cosignFlag.equals("1") || cosignFlag.equals("2") || cosignFlag.equals("3")) {

				JSONObject consent = new JSONObject(consents.getString(consentsCount));
				params.put("userId", consent.getString("userId"));
				// params.put("formRid", consent.getString("FormRid"));
				String formId = "";
				String formVersion = "";

				if (!consent.isNull("FormVersion")) {
					formVersion = consent.getString("FormVersion");
				} else {
					formVersion = consent.getString("formVersion");
				}
				if (!consent.isNull("FormId")) {
					formId = consent.getString("FormId");
				} else {
					formId = consent.getString("formId");
				}
				params.put("formId", formId);
				params.put("formVersion", formVersion);
				params.put("drow", "");
			} else {
				JSONObject consent = new JSONObject(consents.getString(consentsCount));
				final JSONObject patients = requestOptions.getJSONObject("patient");

				params.put("userId", patients.getString("userId"));
				params.put("formId", consent.getString("FormId"));
				params.put("formVersion", consent.getString("FormVersion"));
				params.put("drow", "");

			}

			parameters += "		<pen-drawing>";
			parameters += "			<document path='" + penDrawingUrl + "'>";
			parameters += "				<pen-drawing-get-parameters>";
			parameters += "					<post-param key='parameter'><![CDATA[" + params.toString()
					+ "]]></post-param>";
			parameters += "				</pen-drawing-get-parameters>";
			parameters += "			</document>";
			parameters += "		</pen-drawing>";

		} catch (JSONException e) {
			e.printStackTrace();
			Log.i(TAG, "[makeFosLoadPendrawing] exception : " + e.toString());
		}
		Log.e(TAG, "[makeFosLoadPendrawing] pen-drawing : " + parameters);
		return parameters;
	}

	// FOS에 저장된 녹취 파일 추가
	public String makeFosRecordFiles() {
		String recordFilePath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/CLIPe-Form/AUDIO/";
		File recordFolder = new File(recordFilePath);

		String parameters = "";
		parameters += "		<record-files>";
		// 녹취 파일 폴더에 파일이 있으면
		if (recordFolder.exists() && recordFolder.isDirectory()) {
			for (File file : recordFolder.listFiles()) {
				if (file.isFile()) {
					parameters += "<record-file name='" + file.getName() + "' path='" + file.getPath() + "' />";
				}
			}
		}
		parameters += "		</record-files>";

		Log.e(TAG, "[makeFosRecordFiles] record-files : " + parameters);
		return parameters;
	}

	public String convertUrlToBase64(String url) {
		URL newurl;
		Bitmap bitmap;
		String base64 = "";
		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			newurl = new URL(url);
			bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
			base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return base64;
	}

	// FOS 전역 파라메타 만들기
	public String makeFosGlobalParameters(String userId) {
		String parameters = "";
		try {
			JSONObject patients = new JSONObject();
			String url = "";
			String asdf = "";
			if (!requestOptions.isNull("patient")) {
				patients = requestOptions.getJSONObject("patient");
				url = patients.getString("docSignImgUrl");
			} else {
				url = EFORM_URL + "/cmcnu/webapps/mr/mr/formmngtweb/.live?submit_id=DRMRF02317&business_id=mr&userid="
						+ userId;
			}
			if (url != null && url.length() != 0) {
				asdf = service_submit(url, "sign", "");
				patients.put("docSignImg", asdf);
			}
			// 환자 정보
			if (!requestOptions.isNull("patient")) {
				parameters += object2param(requestOptions.getJSONObject("patient"), "patient");
			} else {
				parameters += object2param(patients, "patient");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// System.out.println("파라미터는 이것 :" + parameters);
		return parameters;
	}

	// KEY, Value => <param key='"+key+"'><![CDATA["+val+"]]></param>로 추가
	private String object2param(JSONObject obj, String type) {
		String param = "";
		if (obj != null) {
			try {
				Iterator<?> keys = obj.keys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					String val = (String) obj.getString(key);
					Log.i(TAG, "[" + key + " : " + val + " ]");
					if (val != "" && val != null) {
						// 팝업 DEFAULT POPUP URL
						if (key.equals("I_DEFAULT_POPUP_URL")) {
							String defaultPopupUrl = Storage.getInstance(context).getStorage("serviceUrl");// +"/";
							param += "<param key='" + key + "'><![CDATA[" + defaultPopupUrl + "]]></param>";
						} else {
							param += "<param key='" + key + "'><![CDATA[" + val + "]]></param>";
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.i(TAG, param);
		}
		return param;
	}

	public EFormToolkit getToolkit() {
		return _toolkit;
	};

	// 펜드로우잉 저장
	public String eformSaveDrow(String drowData) {
		String respone = "";
		String result_st = "";
		try {
			JSONObject params = new JSONObject();
			JSONObject consent = new JSONObject(consents.getString(consentsCount));
			Log.i(TAG, "consentsCount : " + consentsCount);
			Log.i(TAG, "consent : " + consents.getString(consentsCount));
			if (paramUserId != null && paramUserId.length() != 0) {
				params.put("userId", paramUserId);
				params.put("createUserId", paramUserId);
				params.put("modifyUserId", paramUserId);
			} else {
				params.put("userId", consent.getString("userId"));
				params.put("createUserId", consent.getString("userId"));
				params.put("modifyUserId", consent.getString("userId"));
			}

			// params.put("formRid", consent.getString("FormRid"));
			if (consent.isNull("FormId") || consent.getString("FormId").equals("")) {
				params.put("formId", consent.getString("formId"));
			} else {
				params.put("formId", consent.getString("FormId"));
			}
			if (consent.isNull("FormVersion") || consent.getString("FormVersion").equals("")) {
				params.put("formVersion", consent.getString("formVersion"));
			} else {
				params.put("formVersion", consent.getString("FormVersion"));
			}
			params.put("drow", drowData);

			// Log.i(TAG, "[eformSaveDrow] userId : " + paramUserId);
			// Log.i(TAG, "[eformSaveDrow] formRid : " + consent.getString("FormRid"));
			// Log.i(TAG, "[eformSaveDrow] formId : " + consent.getString("FormId"));
			// Log.i(TAG, "[eformSaveDrow] formVersion : " +
			// consent.getString("FormVersion"));
			// Log.i(TAG, "[eformSaveDrow] drow : " + drowData);
			String url = EFORM_URL + "/biz/nu/member/viewer/eForm25/consent/drow/save";
			String type = "drow";

			respone = service_submit(url, type, params.toString());
			Log.i(TAG, "[SaveDrow] respone : " + respone);
			JSONObject result = new JSONObject(respone);
			if (!result.getString("result").equals("true")) {
				result_st = "펜드로잉 저장중에 오류가 발생했습니다.\n다시 시도해주세요.";
			} else {
				result_st = "펜드로잉 저장에 성공하였습니다.";
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.i(TAG, "saveDrow Error : " + e.toString());
			result_st = "펜드로잉 저장중에 오류가 발생했습니다.\n다시 시도해주세요.";
		}
		return result_st;
	}

	// e-from viewer : save or tempSave result
	public String eformSaveData(String dataXml, String type, String formXmlPath, ArrayList<String> imagePaths,
			String hashCode, String signature, String audioPaths, String consentMstRid, String consentImageRid) {
		Log.i(TAG, "저장타입 : " + type);
		String saveResult = "";
		// URL url = new URL("");
		String url = "";

		try {
			JSONObject timeObject = new JSONObject();
			timeObject.put("asdf", "asdf");
			writeLog("jie");
			String strDdd = service_submit(EFORM_URL + "/biz/nu/member/viewer/eForm25/consent/nowtime/get", "gettime",
					timeObject.toString());
			writeLog("savetime : " + strDdd);
			JSONObject serverTime = new JSONObject(strDdd);
			String strToday = serverTime.getString("nowTime"); // yyyy-mm-dd hh24:MM:ss
			String strTodayNu = strToday.replaceAll("-", "");
			strTodayNu = strTodayNu.replaceAll(":", "");
			strTodayNu = strTodayNu.replaceAll(" ", "");
			String timeYYYYmmdd = strToday.substring(0, 10).replaceAll("-", ""); // yyyymmdd

			Boolean popupMsgResult = false;
			String popupMsgVal = "";
			String returnConsentState = "";

			final JSONObject params = new JSONObject();
			JSONObject param_consent = requestOptionParams;
			// 코사인코드 갱신안되서넘어옴
			JSONObject consent = new JSONObject(consents.getString(consentsCount));
			String patientCode = "";

			// =============================
			String[] AudioPathSave = audioPaths.split("@");

			if (AudioPathSave.length > 0) {
				params.put("recordCnt", AudioPathSave.length);
			} else {
				params.put("recordCnt", 0);
			}

			if (consent.isNull("CreateDateTime") || consent.getString("CreateDateTime").equals("")) {
				params.put("createDatetime", pageOpenTime);
			} else {
				params.put("createDatetime", consent.getString("CreateDateTime"));
			}
			if (type.equals("save")) {
				String pathAry = "";
				for (int j = 0; j < imagePaths.size(); j++) {
					if (j == 0) {
						pathAry = imagePaths.get(j);
					} else {
						pathAry = pathAry + "," + imagePaths.get(j);
					}
				}
				// 이미지 경로
				params.put("imgRealPath", pathAry);
			}

			url = "http://emrdev.yjh.com/eform/biz/nu/member/viewer/eForm25/consent/view/save";// EFORM_URL +
																										// "biz/nu/member/viewer/eForm25/consent/view/save";

			// 2021-12-02
			HashMap<String, String> signval = new HashMap<>();
			try {
				UserSignCheckManager signManager = UserSignCheckManager.getInstance();
				signManager.initFormXml(dataXml.replaceAll("\"", "'"));
				signval = signManager.getSignCheckResult();
			} catch (Exception e) {
				e.printStackTrace();
				signval = new HashMap<>();
			}

			// File file = new File(
			// Environment.getExternalStorageDirectory().getAbsolutePath() +
			// "/signflag.txt");
			// try {
			// FileWriter fw = new FileWriter(file, true);
			// fw.write(signval.get("signflag"));
			// fw.flush();
			// fw.close();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			// 코사인탭에서 접근했을때
			if (consent.getString("cosignFlag").equals("1") || consent.getString("cosignFlag").equals("3")) {

				// MSTRID
				params.put("consentMstRid", consent.getString("consentMstRid"));
				// 환자번호
				params.put("patientCode", consent.getString("patientCode"));
				// 환자이름
				params.put("patientName", consent.getString("patientName"));
				// 진료or입원일자
				params.put("clnDate", consent.getString("clnDate"));// clnDate로 수정해야함
				// OCRTAG
				params.put("ocrTag", consent.getString("ocrTag"));
				// 진료부서코드
				params.put("clnDeptCd", consent.getString("clnDeptCd"));
				// 처방일자
				params.put("orderDate", consent.getString("orderDate"));
				// 처방명
				if (!consent.isNull("orderName") && !consent.getString("orderName").equals("")) {
					params.put("orderName", consent.getString("orderName"));
				}
				// 내원구분
				params.put("visitType", consent.getString("visitType"));
				// 원무등록번호
				params.put("certNo", consent.getString("certNo"));
				// 병실
				params.put("roomCd", consent.getString("roomCd"));
				// 병동/진료과코드
				if (!consent.isNull("WardCd")) {
					params.put("wardCd", consent.getString("WardCd"));
				} else {
					if (consent.isNull("wardCd")) {
						params.put("wardCd", "");
					} else {
						params.put("wardCd", consent.getString("wardCd"));
					}
				}
				// 병동/진료과명
				params.put("wardName", consent.getString("wardName"));
				// 서식 Form XML
				params.put("formXml", readFileString(formXmlPath));
				// 서식 Data XML
				params.put("dataXml", dataXml.replaceAll("\"", "'"));

				// 코사인 deptcode
				if (!consent.isNull("cosignDeptCode") && !consent.getString("cosignDeptCode").equals("0")) {
					params.put("cosignDeptCode", consent.getString("cosignDeptCode"));
					Log.i(TAG, "formCosignCode : " + consent.getString("cosignDeptCode"));
				}
				// 코사인 deptname
				if (!consent.isNull("cosignDeptName") && !consent.getString("cosignDeptName").equals("")) {
					params.put("cosignDeptName", consent.getString("cosignDeptName"));
					Log.i(TAG, "cosignDeptName : " + consent.getString("cosignDeptName"));
				}

				// 서식코드
				params.put("formCd", consent.getString("formCd"));
				// 서식 아이디
				if (!consent.isNull("FormId")) {
					params.put("formId", consent.getString("FormId"));
				} else {
					params.put("formId", consent.getString("formId"));
				}
				// 서식버전
				if (!consent.isNull("FormVersion")) {
					params.put("formVersion", consent.getString("FormVersion"));
				} else {
					params.put("formVersion", consent.getString("formVersion"));
				}
				// 사용자아이디
				params.put("userId", consent.getString("userId"));
				// 수정자명
				params.put("userName", consent.getString("userName"));
				// 수정자과코드
				params.put("userDeptCd", consent.getString("userDeptCd"));
				// 수정자과명
				params.put("userDeptName", consent.getString("userDeptName"));

				// 완료 저장시에만 추가되는 컬럼
				if (type.equals("save")) {
					params.put("certResult", signature);
					params.put("certTarget", hashCode);
					params.put("completeYn", "Y");
					requestOptions.put("methodName", "SaveComplete");
					params.put("lcTechUpload", "true");
					params.put("imgFileSubPath", "/" + timeYYYYmmdd + "/" + consent.getString("patientCode") + "/");
					params.put("imageUpload", "true");
				} else {
					params.put("completeYn", "N");
					requestOptions.put("methodName", "SaveTempData"); // 임시저장 서비스
				}

			} else if (consent.getString("cosignFlag").equals("2")) { // 작성동의서 빠른조회 - 처방동의서
				String mstRid = "";

				// mstRID
				if (consent.getString("consentMstRid").equals("null")) {
					mstRid = null;
				}
				params.put("consentMstRid", mstRid);
				// 환자번호
				params.put("patientCode", consent.getString("patientCode"));
				// 환자이름
				params.put("patientName", consent.getString("patientName"));

				// 진료or입원일자
				params.put("clnDate", consent.getString("clnDate")); // clnDate로 수정해야함
				// OCRTAG
				params.put("ocrTag", consent.getString("ocrTag"));
				// 진료부서
				params.put("clnDeptCd", consent.getString("clnDeptCd"));
				// 처방일자
				params.put("orderDate", consent.getString("orderDate"));
				// 처방명
				params.put("orderName", consent.getString("orderName"));
				// 내원구분
				params.put("visitType", consent.getString("visitType"));
				// 원무등록번호
				params.put("certNo", consent.getString("certNo"));
				// 병실****
				if (!consent.isNull("roomCd") && !consent.getString("roomCd").equals("")) {
					params.put("roomCd", consent.getString("roomCd"));
				}
				// 병동/진료과코드
				params.put("wardCd", consent.getString("clnDeptCd"));
				// 병동/진료과명
				params.put("wardName", consent.getString("clnDeptNm"));
				// 서식 Form XML
				params.put("formXml", readFileString(formXmlPath));
				// 서식 Data XML
				params.put("dataXml", dataXml.replaceAll("\"", "'"));

				// 코사인 과명
				if (!consent.isNull("cosignDeptCode") && !consent.getString("cosignDeptCode").equals("")) {
					params.put("cosignDeptCode", consent.getString("cosignDeptCode"));
					Log.i(TAG, "cosignDeptCode : " + consent.getString("cosignDeptCode"));
				}
				// 코사인 과명
				if (!consent.isNull("cosignDeptName") && !consent.getString("cosignDeptName").equals("")) {
					params.put("cosignDeptName", consent.getString("cosignDeptName"));
					Log.i(TAG, "cosignDeptName : " + consent.getString("cosignDeptName"));
				}
				// 서식코드
				params.put("formCd", consent.getString("formCd"));
				// 서식 아이디
				params.put("formId", consent.getString("formId"));
				// 서식 버전
				params.put("formVersion", consent.getString("formVersion"));
				// 사용자ID
				params.put("userId", consent.getString("userId"));
				// 사용자명
				params.put("userName", consent.getString("userName"));
				// 생성자과코드
				params.put("userDeptCd", consent.getString("userDeptCd"));
				// 생성자과명
				params.put("userDeptName", consent.getString("userDeptName"));
				// 주치의 ID
				params.put("orddrId", consent.getString("atDoctId"));
				// 서식fromdt
				params.put("formFromDt", consent.getString("formFromDt"));

				// 완료 저장시에만 추가되는 컬럼
				if (type.equals("save")) {
					params.put("completeYn", "Y");
					requestOptions.put("methodName", "SaveComplete");
					params.put("certTarget", hashCode);
					params.put("certResult", signature);
					params.put("lcTechUpload", "true");
					params.put("imgFileSubPath", "/" + timeYYYYmmdd + "/" + consent.getString("patientCode") + "/");
					params.put("imageUpload", "true");
				} else {
					params.put("completeDatetime", "");
					params.put("completeYn", "N");
					requestOptions.put("methodName", "SaveTempData"); // 임시저장 서비스
				}
			} else { // 코사인탭 이외에서 접근했을때
				final JSONObject paramss = requestOptions.getJSONObject("detail");
				final JSONObject patients = requestOptions.getJSONObject("patient");

				// 임시저장된 서식
				if (!consent.isNull("consentStateDisp") && !consent.getString("consentStateDisp").equals("")) {
					// mstRID
					params.put("consentMstRid", consent.getString("consentMstRid"));
					// 환자번호
					params.put("patientCode", consent.getString("patientCode"));
					// 환자이름
					params.put("patientName", consent.getString("patientName"));
					// 진료or입원일자
					params.put("clnDate", consent.getString("clnDate")); // clnDate로 수정해야함
					// OCRTAG
					params.put("ocrTag", consent.getString("ocrTag"));
					// 진료부서
					params.put("clnDeptCd", consent.getString("clnDeptCd"));
					// 처방일자
					params.put("orderDate", consent.getString("orderDate"));

					// 처방명
					if (!consent.isNull("orderName") && !consent.getString("orderName").equals("")) {
						params.put("orderName", consent.getString("orderName"));
						Log.i(TAG, "orderName : " + consent.getString("orderName"));
					}
					// 내원구분
					params.put("visitType", consent.getString("visitType"));
					// 원무등록번호
					params.put("certNo", consent.getString("certNo"));
					// 병실
					if (!consent.isNull("roomCd") && !consent.getString("roomCd").equals("")) {
						params.put("roomCd", consent.getString("roomCd"));
					}
					// 병동/진료과코드
					if (!consent.isNull("WardCd")) {
						params.put("wardCd", consent.getString("WardCd"));
					} else {
						if (consent.isNull("wardCd")) {
							params.put("wardCd", "");
						} else {
							params.put("wardCd", consent.getString("wardCd"));
						}
					}
					// 병동/진료과명
					params.put("wardName", consent.getString("wardName"));
					// 서식 Form XML
					params.put("formXml", readFileString(formXmlPath));
					// 서식 Data XML
					params.put("dataXml", dataXml.replaceAll("\"", "'"));
					// cosign dept code
					if (!consent.isNull("cosignDeptCode") && !consent.getString("cosignDeptCode").equals("0")) {
						params.put("cosignDeptCode", consent.getString("cosignDeptCode"));
						Log.i(TAG, "formCosignCode : " + consent.getString("cosignDeptCode"));
					}
					// cosign dept name
					if (!consent.isNull("cosignDeptName") && !consent.getString("cosignDeptName").equals("")) {
						params.put("cosignDeptName", consent.getString("cosignDeptName"));
						Log.i(TAG, "cosignDeptName : " + consent.getString("cosignDeptName"));
					}
					// 서식코드
					params.put("formCd", consent.getString("formCd"));
					// 서식 아이디
					params.put("formId", consent.getString("FormId"));
					// 서식버전
					params.put("formVersion", consent.getString("FormVersion"));
					// 사용자아이디
					params.put("userId", patients.getString("userId"));
					// 수정자명
					params.put("userName", patients.getString("usernm"));
					// 수정자과코드
					params.put("userDeptCd", patients.getString("userDeptCd"));
					// 수정자과명
					params.put("userDeptName", patients.getString("userDeptNm"));
					// 주치의 ID
					params.put("orddrId", patients.getString("orddrid"));

					// 서식fromdt
					// params.put("formFromDt", consent.getString("formFromDt"));

					// 완료 저장시에만 추가되는 컬럼
					if (type.equals("save")) {
						params.put("certResult", signature);
						params.put("certTarget", hashCode);
						params.put("completeYn", "Y");
						params.put("lcTechUpload", "true");
						params.put("imgFileSubPath", "/" + timeYYYYmmdd + "/" + consent.getString("patientCode") + "/");
						params.put("imageUpload", "true");
						requestOptions.put("methodName", "SaveComplete");
					} else {
						params.put("completeYn", "N");
						requestOptions.put("methodName", "SaveTempData"); // 임시저장 서비스
					}
				} else { // 신규 서식
							// 환자번호
					params.put("patientCode", paramss.getString("PatientCode"));
					// 환자이름
					params.put("patientName", paramss.getString("PatientName"));
					// 진료or입원일자
					params.put("clnDate", patients.getString("ordddDate"));
					// OCRTAG
					params.put("ocrTag", consent.getString("ocrTag"));
					// 진료부서
					params.put("clnDeptCd", patients.getString("ordDeptCds"));
					// 처방일자
					params.put("orderDate", patients.getString("ordddDate"));
					// 처방명
					if (!patients.isNull("diaghngnm") && !patients.getString("diaghngnm").equals("")) {
						params.put("orderName", patients.getString("diaghngnm"));
						Log.i(TAG, "orderName : " + patients.getString("diaghngnm"));
					}
					// 내원구분
					params.put("visitType", patients.getString("visitType"));
					// 원무등록번호
					params.put("certNo", patients.getString("Cretno"));
					// 병실
					params.put("roomCd", patients.getString("ROOMCD"));
					// 병동/진료과코드
					params.put("wardCd", patients.getString("wardCd"));
					// 병동/진료과명
					params.put("wardName", patients.getString("ORDDEPTNM"));
					// 서식 Form XML
					params.put("formXml", readFileString(formXmlPath));
					// 서식 Data XML
					params.put("dataXml", dataXml.replaceAll("\"", "'"));
					// 서식fromdt
					if (newOrTemp.equals("new") || newOrTemp.equals("cosignNew") || newOrTemp.equals("nurscertNew")
							|| newOrTemp.equals("nowrite")) {
						params.put("formFromDt", consent.getString("FormFromDt"));
					}
					// 코사인 과코드
					if (!consent.isNull("cosignDeptCode") && !consent.getString("cosignDeptCode").equals("")) {
						params.put("cosignDeptCode", consent.getString("cosignDeptCode"));
						Log.i(TAG, "cosignDeptCode : " + consent.getString("cosignDeptCode"));
					}
					// 코사인 과명
					if (!consent.isNull("cosignDeptName") && !consent.getString("cosignDeptName").equals("")) {
						params.put("cosignDeptName", consent.getString("cosignDeptName"));
						Log.i(TAG, "cosignDeptName : " + consent.getString("cosignDeptName"));
					}
					// 서식코드
					if (!consent.isNull("FormCd") && !consent.getString("FormCd").equals("")) {
						params.put("formCd", consent.getString("FormCd"));
						Log.i(TAG, "formCd : " + consent.getString("FormCd"));
					} else {
						params.put("formCd", consent.getString("formCd"));
					}
					// 서식 아이디
					params.put("formId", consent.getString("FormId"));
					// 서식 버전
					if (!consent.isNull("formVersion") && !consent.getString("formVersion").equals("0")) {
						params.put("formVersion", consent.getString("formVersion"));
						Log.i(TAG, "formVersion : " + consent.getString("formVersion"));
					} else {
						params.put("formVersion", consent.getString("FormVersion"));
					}
					;
					// 주치의 ID
					params.put("orddrId", patients.getString("orddrid"));
					// 사용자ID
					params.put("userId", patients.getString("userId"));
					// 사용자명
					params.put("userName", patients.getString("usernm"));
					// 생성자과코드
					params.put("userDeptCd", patients.getString("userDeptCd"));
					// 생성자과명
					params.put("userDeptName", patients.getString("userDeptNm"));
					// 완료 저장시에만 추가되는 컬럼
					if (type.equals("save")) {
						params.put("completeYn", "Y");
						requestOptions.put("methodName", "SaveComplete");
						params.put("certTarget", hashCode);
						params.put("certResult", signature);
						params.put("lcTechUpload", "true");
						params.put("imgFileSubPath", "/" + timeYYYYmmdd + "/" + paramss.getString("PatientCode") + "/");
						params.put("imageUpload", "true");
					} else {
						params.put("completeYn", "N");
						requestOptions.put("methodName", "SaveTempData"); // 임시저장 서비스
					}
				}
			}

			// 무결성 방지위함
			if (!consent.isNull("modifyDatetime")) {
				params.put("modifyDatetime",
						consent.getString("modifyDatetime").equals("null") ? "" : consent.getString("modifyDatetime"));

			}
			// 공통 Device Information
			String deviceId = GetDevicesUUID(context, callbackContext);
			params.put("deviceType", "AND");
			params.put("deviceId", deviceId);
			params.put("deviceIp", getLocalIpAddress());
			params.put("deviceMac", getMACAddress("wlan0"));

			String saveRespone = "";

			// fos 확인 위한 txt파일 생성
//			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/consent_2022_02_03.txt");
//			try {
//				FileWriter fw = new FileWriter(file, true);
//				fw.write(consent.toString());
//				fw.flush();
//				fw.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

			// 2021-12-02
			// 2022-02-03
			if (type.equals("save")) {
				if (dataXml.replaceAll("\"", "'").indexOf("의료행위에 관한 설명·동의 변경사항 고지서") > -1) {
					if(!consent.isNull("verbalMultiFlag")) {
						if(consent.getString("verbalMultiFlag").equals("V,N")) {
							params.put("signFlag", "3"); 
						}else if(consent.getString("verbalMultiFlag").equals("M,N")) {
							params.put("signFlag", "4"); 
						}
					}else { 
						params.put("signFlag", "none");
					}
				} else {
					params.put("signFlag", signval.get("signflag"));
				}
			}

			writeLog("param setting done");
			saveRespone = service_submit(url, "save", params.toString());
			JSONArray saveMstResult = new JSONArray(saveRespone); // 500 / -1
		
			writeLog(saveMstResult.toString());
			if (!saveMstResult.getJSONObject(0).isNull("consentState"))
				returnConsentState = saveMstResult.getJSONObject(0).getString("consentState");

			if (saveMstResult.getJSONObject(0).getJSONObject("result").getString("result").equals("false")) { // consent/save
																												// 실패
				saveResult = "ErrorCode : "
						+ saveMstResult.getJSONObject(0).getJSONObject("result").getString("errorCode")
						+ "\nErrorMsg  : "
						+ saveMstResult.getJSONObject(0).getJSONObject("result").getString("errorMsg");
			} else { // consent/save 성공
				if (saveMstResult.getJSONObject(0).getJSONObject("msgPopup").getString("visible").equals("true")) {
					popupMsgResult = Boolean
							.valueOf(saveMstResult.getJSONObject(0).getJSONObject("msgPopup").getString("visible"))
							.booleanValue();
					popupMsgVal = saveMstResult.getJSONObject(0).getJSONObject("msgPopup").getString("msg");
				}
				saveResult = "true";

				if (saveMstResult.getJSONObject(0).getJSONObject("result").getString("result").equals("true")) {
					// 녹취 처리
					if (audioPaths != "" && audioPaths != null) {
						try {

							JSONArray AudioArray = new JSONArray();
							String[] AudioPath = audioPaths.split("@");
							JSONArray AudioUploadResultAry = new JSONArray();
							JSONObject AudioUploadResult = new JSONObject();
							for (int j = 0; j < AudioPath.length; j++) {
								JSONObject AudioObject = new JSONObject();
								AudioObject.put("consentMstRid",
										saveMstResult.getJSONObject(0).getString("consentMstRid"));
								AudioObject.put("formId", params.getString("formId"));

								// path , name지정
								AudioObject.put("recordPath", "/" + timeYYYYmmdd + "/" + params.getString("patientCode")
										+ "/" + params.getString("ocrTag") + "/");
								AudioObject.put("recordFileName", AudioPath[j].substring(37, AudioPath[j].length()));
								AudioObject.put("recordRealPath", "/storage/emulated/0/CLIPe-Form/Audio/"
										+ AudioPath[j].substring(37, AudioPath[j].length()));

								if (type.equals("save")) {
									AudioObject.put("completeYn", "Y");//
								} else {
									AudioObject.put("completeYn", "N");//
								}
								AudioObject.put("useYn", "Y");// 레코드파일사용여부
								AudioObject.put("createUserId", params.getString("userId"));
								AudioArray.put(AudioObject);

								String result = service_submit(
										EFORM_URL + "/biz/nu/member/viewer/eForm25/consent/record/upload", "audio",
										AudioArray.toString());
								if (result.equals("sizeOver")) {
									saveResult = "sizeOver";
								} else {
									AudioUploadResultAry = new JSONArray(result);
									AudioUploadResult = AudioUploadResultAry.getJSONObject(0);
									saveResult = "true";
								}

							}
						} catch (Exception e) {
							e.printStackTrace();
							Log.i(TAG, "저장 결과 : " + e.toString());
							saveResult = "전자동의서 저장중 오류가 발생했습니다. 다시 시도해주세요. Exception/AudioSaveError" + e.toString();
						}
					}
					if (saveResult.equals("true")) {
						int attachPageCount = 0;
						int attachPagetemplateCount = 0;
						try {
							AttachPageManager atManager = AttachPageManager.getInstance();
							atManager.initFormXml(readFileString(formXmlPath));
							attachPageCount = atManager.getAttachPageCount();
							attachPagetemplateCount = atManager.getAttachPagetemplateCount();

						} catch (Exception e1) {
							e1.printStackTrace();
						}

						// nu 데이터 저장
						System.out.println(params.toString());
						System.out.println(params.getString("clnDeptCd"));
						// 신규작성
						if (consent.isNull("consentMstRid") || consent.getString("consentMstRid").equals("")) {
							saveRespone = "";
							JSONObject nuParam = new JSONObject();
							url = SERVER_URL + "cmcnu/.live?submit_id=DXMRF00114&business_id=mr";
							writeLog("nuUpload url : " + url);
							nuParam.put("instcd", INSTCODE);
							writeLog("instCd : " + INSTCODE);
							nuParam.put("ocrtag", consent.getString("ocrTag"));
							nuParam.put("pid", params.getString("patientCode"));

							nuParam.put("orddd", params.getString("orderDate"));

							nuParam.put("cretno", params.getString("certNo"));
							nuParam.put("ordtype", params.getString("visitType"));

							//

							nuParam.put("orddeptcd", params.getString("clnDeptCd"));

							//
							nuParam.put("orddrid", params.getString("orddrId"));

							nuParam.put("formcd", params.getString("formCd"));
							nuParam.put("formfromdt", params.getString("formFromDt"));

							nuParam.put("fstprntdt", strTodayNu);

							nuParam.put("fstprntdeptcd", params.getString("userDeptCd"));
							nuParam.put("fstprntid", params.getString("userId"));
							int imagePage = imageCount - attachPagetemplateCount - attachPageCount;
							int imageAllPage = imageCount + attachPagetemplateCount + attachPageCount;

							nuParam.put("updtdt", strTodayNu);
							nuParam.put("updtdeptcd", params.getString("userDeptCd"));
							nuParam.put("updtuserid", params.getString("userId"));
							nuParam.put("pagecnt", imagePage);
							if (type.equals("save")) {
								nuParam.put("scanyn", "Y");
								// 연명 서식 아닐 경우
								if (consent.getString("lifelong_kind").equals("")
										|| consent.getString("lifelong_kind").equals("null")) {
									nuParam.put("hstatcd", "C");
								} else {// 연명 서식일 경우
									int certneedcnt = Integer.parseInt(consent.getString("consent_certneedcnt"));
									int certcnt = 0;

									if (returnConsentState.equals("ELECTR_TEMP")) {
										nuParam.put("hstatcd", "G"); // 진행플래그
									} else {
										nuParam.put("hstatcd", "C"); // 완료플래그
									}

									// if (certneedcnt - 1 == certcnt) {
									// nuParam.put("hstatcd", "C"); // 완료플래그
									// } else {
									// nuParam.put("hstatcd", "G"); // 진행플래그
									// }
								}
							} else {
								nuParam.put("scanyn", "N");
								nuParam.put("hstatcd", "P");
							}

							nuParam.put("mstatcd", "E");
							nuParam.put("rid", saveMstResult.getJSONObject(0).getString("consentMstRid"));
							nuParam.put("drsign", signval.get("drsign"));
							nuParam.put("nrsign", signval.get("nrsign"));
							nuParam.put("patsign", signval.get("patsign"));
							nuParam.put("procersign", signval.get("procersign"));
							nuParam.put("etcsign", signval.get("etcsign"));
							nuParam.put("printsource", "M");

							saveRespone = service_submit(url, "nuUpload", nuParam.toString());
							if (saveRespone.indexOf("정상 처리") > -1) {
								if (type.equals("save") && (newOrTemp.equals("new") || newOrTemp.equals("cosignNew")
										|| newOrTemp.equals("nurscertNew") || newOrTemp.equals("nowrite"))) {

									url = SERVER_URL + "cmcnu/.live?submit_id=DXMRF00115&business_id=mr";
									
									JSONObject nuParams = new JSONObject();
									nuParams.put("instcd", INSTCODE);
									nuParams.put("ocrtag", consent.getString("ocrTag"));

									SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
									Date to;
									SimpleDateFormat sdformat = new SimpleDateFormat("yyyyMMddHHmmss");

									try {
										to = format.parse(strTodayNu);
										Calendar cal = Calendar.getInstance();
										cal.setTime(to);
										cal.add(Calendar.SECOND, 1);

										strTodayNu = sdformat.format(cal.getTime());
									} catch (ParseException e) {
										e.printStackTrace();
									}

									nuParams.put("updtdt", strTodayNu);
									nuParams.put("updtdeptcd", params.getString("userDeptCd"));
									nuParams.put("updtuserid", params.getString("userId"));
									nuParams.put("pagecnt", imagePage);
									if (type.equals("save")) {
										nuParams.put("scanyn", "Y");

										// 연명 서식 아닐 경우
										if (consent.getString("lifelong_kind").equals("")
												|| consent.getString("lifelong_kind").equals("null")) {
											nuParams.put("hstatcd", "C");
										} else {// 연명 서식일 경우
											int certneedcnt = Integer
													.parseInt(consent.getString("consent_certneedcnt"));
											int certcnt = 0;

											if (returnConsentState.equals("ELECTR_TEMP")) {
												nuParams.put("hstatcd", "G"); // 진행플래그
											} else {
												nuParams.put("hstatcd", "C"); // 완료플래그
											}
											// if (certneedcnt - 1 == certcnt) {
											// nuParams.put("hstatcd", "C"); // 완료플래그
											// } else {
											// nuParams.put("hstatcd", "G"); // 진행플래그
											// }
											// nuParams.put("hstatcd", "G"); // 진행플래그
										}

										nuParams.put("scanpagecnt", imageCount);
									} else {
										nuParams.put("scanyn", "N");
										nuParams.put("hstatcd", "P");
									}
									nuParams.put("mstatcd", "E");
									nuParams.put("rid", saveMstResult.getJSONObject(0).getString("consentMstRid"));
									nuParams.put("drsign", signval.get("drsign"));
									nuParams.put("nrsign", signval.get("nrsign"));
									nuParams.put("patsign", signval.get("patsign"));
									nuParams.put("procersign", signval.get("procersign"));
									nuParams.put("etcsign", signval.get("etcsign"));

									nuParams.put("printsource", "M");
									saveRespone = service_submit(url, "nuUpload", nuParams.toString());

									if (saveRespone.indexOf("정상 처리") < -1) {
										saveResult = "DXMRF00115/ERROR";
									} else {
										saveResult = "true";
									}
								}
							} else {
								saveResult = "DXMRF00114/ERROR";
							}

						} else { // 작성동의서
							int imagePage = imageCount - attachPagetemplateCount - attachPageCount;
							int imageAllPage = imageCount + attachPagetemplateCount + attachPageCount;

							JSONObject nuParams = new JSONObject();
							nuParams.put("instcd", INSTCODE);
							nuParams.put("ocrtag", consent.getString("ocrTag"));
							nuParams.put("updtdt", strTodayNu);
							nuParams.put("updtdeptcd", params.getString("userDeptCd"));
							nuParams.put("updtuserid", params.getString("userId"));
							nuParams.put("pagecnt", imagePage);
							if (type.equals("save")) {
								nuParams.put("scanyn", "Y");

								// 연명 서식 아닐 경우
								if (consent.isNull("lifelong_kind")
										|| consent.getString("lifelong_kind").equals("null")) {
									nuParams.put("hstatcd", "C");
								} else {// 연명 서식일 경우
									int certneedcnt = Integer.parseInt(consent.getString("consent_certneedcnt"));
									int certcnt = Integer.parseInt(consent.getString("certCnt"));

									if (returnConsentState.equals("ELECTR_TEMP")) {
										nuParams.put("hstatcd", "G"); // 진행플래그
									} else {
										nuParams.put("hstatcd", "C"); // 완료플래그
									}
									// if (certneedcnt - 1 == certcnt) {
									// nuParams.put("hstatcd", "C"); // 완료플래그
									// } else {
									// nuParams.put("hstatcd", "G"); // 진행플래그
									// }
								}
								nuParams.put("scanpagecnt", imageCount);
							} else {
								nuParams.put("scanyn", "N");
								nuParams.put("hstatcd", "P");
							}
							nuParams.put("mstatcd", "E");
							nuParams.put("rid", saveMstResult.getJSONObject(0).getString("consentMstRid"));
							nuParams.put("drsign", signval.get("drsign"));
							nuParams.put("nrsign", signval.get("nrsign"));
							nuParams.put("patsign", signval.get("patsign"));
							nuParams.put("procersign", signval.get("procersign"));
							nuParams.put("etcsign", signval.get("etcsign"));

							nuParams.put("printsource", "M");

							saveRespone = "";
							url = SERVER_URL + "cmcnu/.live?submit_id=DXMRF00115&business_id=mr";
							saveRespone = service_submit(url, "nuUpload", nuParams.toString());
							if (saveRespone.indexOf("정상 처리") < -1) {
								saveResult = "DXMRF00115/ERROR";
							} else {
								saveResult = "true";
							}
						}

						if (saveResult.equals("true") && !consent.isNull("nowrite")) {

							if (!consent.isNull("tmp01") && consent.getString("tmp01").equals("NONPAY")) {
								JSONObject nuParam = new JSONObject();
								url = SERVER_URL + "cmcnu/.live?submit_id=DXMRF00113&business_id=mr";

								nuParam.put("instcd", INSTCODE);
								nuParam.put("ordtype", params.getString("visitType"));
								// nuParam.put("prcpdd", consent.getString("prcpdd"));

								if (!consent.isNull("prcpdd") && !consent.getString("prcpdd").equals("")) {
									nuParam.put("prcpdd", consent.getString("prcpdd"));
								} else {
									if (!consent.isNull("prcpdd") && !consent.getString("prcpdd").equals("")) {
										nuParam.put("prcpdd", consent.getString("prcpdd"));
									} else {
										nuParam.put("prcpdd", consent.getString("orderDate"));
									}
								}
								if (!consent.isNull("prcpno") && !consent.getString("prcpno").equals("")) {
									nuParam.put("prcpno", consent.getString("prcpno"));
								} else {
									nuParam.put("prcpno", consent.getString("orderNo"));
								}
								nuParam.put("prcphistno", "");
								nuParam.put("ocrtag", consent.getString("ocrTag"));

								nuParam.put("seqno", consent.getString("seqno"));

								if (!consent.isNull("FormCd") && !consent.getString("FormCd").equals("")) {
									nuParam.put("formcd", consent.getString("FormCd"));
								} else {
									nuParam.put("formcd", consent.getString("formCd"));
								}
								nuParam.put("userid", params.getString("userId"));
								nuParam.put("nonpayyn", "Y");
								System.out.println("NONPAY ::: " + nuParam.toString());
								saveRespone = service_submit(url, "NONPAY", nuParam.toString());

								if (saveRespone.indexOf("정상 처리") < -1) {
									saveResult = "DXMRF00113/ERROR";
								} else {
									saveResult = "true";
								}

							}
							// -----------------------------------------------------------
							// 20210630 일반 동의서 비급여 항목 추가
							else if (!consent.isNull("addform") && consent.getString("addform").equals("Y2")) {
								JSONObject nuParam = new JSONObject();
								url = SERVER_URL + "cmcnu/.live?submit_id=DXMRF00113&business_id=mr";

								nuParam.put("instcd", INSTCODE);
								nuParam.put("ordtype", consent.getString("visitType"));

								nuParam.put("prcpdd", consent.getString("orderDate"));

								nuParam.put("prcpno", consent.getString("orderNo"));
								nuParam.put("prcphistno", "");
								nuParam.put("ocrtag", consent.getString("ocrTag"));

								nuParam.put("seqno", consent.getString("seqno"));
								nuParam.put("formcd", consent.getString("formCd"));

								nuParam.put("userid", consent.getString("userId"));

								// 추가
								nuParam.put("nonseqno", consent.getString("nonseqno"));
								nuParam.put("calcnonyn", "Y");

								System.out.println("NONPAY ::: " + nuParam.toString());
								saveRespone = service_submit(url, "NONPAY", nuParam.toString());

								if (saveRespone.indexOf("정상 처리") < -1) {
									saveResult = "DXMRF00113/ERROR";
								} else {
									saveResult = "true";
								}
							}
							// -----------------------------------------------------------
							// 20210603 일반 동의서 비급여 항목 추가
							else if (!consent.isNull("addform") && consent.getString("addform").equals("Y")) {
								JSONObject nuParam = new JSONObject();
								url = SERVER_URL + "cmcnu/.live?submit_id=DXMRF00113&business_id=mr";

								nuParam.put("instcd", INSTCODE);
								nuParam.put("ordtype", params.getString("visitType"));
								// nuParam.put("prcpdd", consent.getString("prcpdd"));

								if (!consent.isNull("prcpdd") && !consent.getString("prcpdd").equals("")) {
									nuParam.put("prcpdd", consent.getString("prcpdd"));
								} else {
									nuParam.put("prcpdd", consent.getString("orderDate"));
								}
								if (!consent.isNull("prcpno") && !consent.getString("prcpno").equals("")) {
									nuParam.put("prcpno", consent.getString("prcpno"));
								} else {
									nuParam.put("prcpno", consent.getString("orderNo"));
								}
								nuParam.put("prcphistno", "");
								nuParam.put("ocrtag", consent.getString("ocrTag"));

								nuParam.put("seqno", consent.getString("seqno"));

								if (!consent.isNull("FormCd") && !consent.getString("FormCd").equals("")) {
									nuParam.put("formcd", consent.getString("FormCd"));
								} else {
									nuParam.put("formcd", consent.getString("formCd"));
								}
								nuParam.put("userid", params.getString("userId"));

								// 추가
								nuParam.put("nonseqno", consent.getString("nonseqno"));
								nuParam.put("calcnonyn", "Y");

								System.out.println("NONPAY ::: " + nuParam.toString());
								saveRespone = service_submit(url, "NONPAY", nuParam.toString());

								if (saveRespone.indexOf("정상 처리") < -1) {
									saveResult = "DXMRF00113/ERROR";
								} else {
									saveResult = "true";
								}
							}
							// -----------------------------------------------------------
							// 비급여 2차수정
							else if (!consent.isNull("tmp01")
									&& consent.getString("tmp01").equals("NONPAYOUTPATIENT")){
								JSONObject nuParam = new JSONObject();
								JSONObject patients = requestOptions.getJSONObject("patient");
								url = SERVER_URL + "cmcnu/.live?submit_id=DXMRF00116&business_id=mr";

								nuParam.put("instcd", INSTCODE);
								nuParam.put("pid", params.getString("patientCode"));
								nuParam.put("orddd", params.getString("orderDate"));

								if (!params.isNull("cretno") && !params.getString("cretno").equals("")) {
									nuParam.put("cretno", params.getString("cretno"));
								} else {
									nuParam.put("cretno", params.getString("certNo"));
								}
								nuParam.put("setno", consent.getString("setno"));
								nuParam.put("prcpcd", patients.getString("prcpcd"));
								nuParam.put("prcpnm", patients.getString("prcpnm"));
								nuParam.put("ocrtag", consent.getString("ocrTag"));
								nuParam.put("userid", params.getString("userId"));

								saveRespone = service_submit(url, "nuUpload", nuParam.toString());

							} else {
								JSONObject nuParam = new JSONObject();
								url = SERVER_URL + "cmcnu/.live?submit_id=DXMRF00113&business_id=mr";

								nuParam.put("instcd", INSTCODE);
								nuParam.put("ordtype", params.getString("visitType"));
								// nuParam.put("prcpdd", consent.getString("prcpdd"));

								if (!consent.isNull("prcpdd") && !consent.getString("prcpdd").equals("")) {
									nuParam.put("prcpdd", consent.getString("prcpdd"));
								} else {
									nuParam.put("prcpdd", consent.getString("orderDate"));
								}
								if (!consent.isNull("prcpno") && !consent.getString("prcpno").equals("")) {
									nuParam.put("prcpno", consent.getString("prcpno"));
								} else {
									nuParam.put("prcpno", consent.getString("orderNo"));
								}
								nuParam.put("prcphistno", "");
								nuParam.put("ocrtag", consent.getString("ocrTag"));

								if (!consent.isNull("seqno") && !consent.getString("seqno").equals("")) {
									nuParam.put("seqno", consent.getString("seqno"));
								} else {
									nuParam.put("seqno", consent.getString("orderSeqNo"));
								}

								if (!consent.isNull("FormCd") && !consent.getString("FormCd").equals("")) {
									nuParam.put("formcd", consent.getString("FormCd"));
								} else {
									nuParam.put("formcd", consent.getString("formCd"));
								}
								nuParam.put("userid", params.getString("userId"));

								saveRespone = service_submit(url, "nuUpload", nuParam.toString());

								if (saveRespone.indexOf("정상 처리") < -1) {
									saveResult = "DXMRF00113/ERROR";
								} else {
									saveResult = "true";
								}
							}

							// fos 확인 위한 txt파일 생성
//							File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DXMRF00113_RESULT.txt");
//							try {
//								FileWriter fw = new FileWriter(file, true);
//								fw.write(saveRespone);
//								fw.flush();
//								fw.close();
//							} catch (IOException es) {
//								// TODO Auto-generated catch block
//								es.printStackTrace();
//							}
						}
					} else if (saveResult.equals("sizeOver")) {
						saveResult = "전자동의서 저장중 오류가 발생했습니다. \n녹취 파일의 용량이 초과되었습니다. \n최대 20분까지 녹취가 가능합니다.\n녹취파일 삭제 혹은 재녹취 후 다시 시도해주세요.";
					}

				}
			}
			if (popupMsgResult) {
				saveResult = "msgPop" + popupMsgVal;

			}

		} catch (JSONException e) {
			e.printStackTrace();
			Log.i(TAG, "저장 결과 : " + e.toString());
			saveResult = "전자동의서 저장중 오류가 발생했습니다. 다시 시도해주세요.";
		}

		return saveResult;
	};

	public boolean paramsNullCheck(String methodName, JSONObject params) {
		boolean result = true;
		try {
			if (params != null) {
				Iterator<?> keys = params.keys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					if (params.getString(key) == null) {
						if (methodName.equals("SaveTempData")) {
							if (key.equals("patientCode") || key.equals("formRid") || key.equals("formId")
									|| key.equals("formXml") || key.equals("formVersion") || key.equals("dataXml")) {
								result = false;
							}
						} else {
							if (key.equals("patientCode") || key.equals("formRid") || key.equals("formId")
									|| key.equals("formXml") || key.equals("formVersion") || key.equals("dataXml")
									|| key.equals("certTarget") || key.equals("certResult")) {
								result = false;
							}
						}
					}
				}
			} else {
				result = false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	};

	// read File to String
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

	public String imageChangePng2Jpg(String imageFileName) {
		String outputPath = "";
		try {
			String extension = imageFileName.substring(imageFileName.lastIndexOf(".") + 1, imageFileName.length());
			outputPath = imageFileName.replace(extension, "jpg");
			Log.i(TAG, "[PNG->JPG] orgFileName : " + imageFileName);
			Log.i(TAG, "[PNG->JPG] outputPath : " + outputPath);
			Bitmap bitmap = BitmapFactory.decodeFile(imageFileName);
			int quality = 96;
			FileOutputStream fileOutStr = new FileOutputStream(outputPath);
			BufferedOutputStream bufOutStr = new BufferedOutputStream(fileOutStr);
			bitmap.compress(CompressFormat.JPEG, quality, bufOutStr);
			bufOutStr.flush();
			bufOutStr.close();

			File oldImage = new File(imageFileName);
			if (oldImage.exists()) {
				oldImage.delete();
			}
		} catch (FileNotFoundException exception) {
			exception.printStackTrace();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		return outputPath;
	}

	private String imageHash(ArrayList<String> imagePaths) {
		String hashCode = "";
		long imagHashTotalStartTime = System.currentTimeMillis();
		// 이미지 해쉬
		for (int i = 0; i < imagePaths.size(); i++) {
			String imagePath = imagePaths.get(i);
			// 이미지의 해쉬코드
			long imagHashStartTime = System.currentTimeMillis();
			hashCode += CommonUtil.getInstance(context).getHashcode(imagePath);
			Log.i(TAG, "imagePath[" + i + "] : " + imagePath);
			Log.i(TAG, "hash : " + hashCode);
			logTimeGap("이미지 해쉬에 걸린 시간", imagHashStartTime);
		}
		logTimeGap("이미지 해쉬에 총 걸린 시간", imagHashTotalStartTime);
		return hashCode;
	}

	// 파일 업로드 경로 : 환자번호(앞에서3자리)/환자번호(4번째에서3자리)/환자번호/
	private String getUploadPath() {
		String path = "";
		try {
			Log.i(TAG, "박승찬4");
			JSONObject params = requestOptions.getJSONObject("detail");
			String patientCode = params.getString("PatientCode");
			path = patientCode.substring(0, 3) + "/" + patientCode.substring(4, 4) + "/" + patientCode + "/"; /// 박승찬
																												/// 환자번호
																												/// 수정
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return path;
	}

	private ArrayList<String> audioFileUpload(ArrayList<ResultRecordFile> audioList) {
		String result = "";
		ArrayList<String> audioPaths = new ArrayList<String>();
		if (audioList != null && !audioList.isEmpty()) {
			JSONObject filePathObject = new JSONObject();

			for (int i = 0; i < audioList.size(); i++) {
				ResultRecordFile audio = audioList.get(i);
				if (audio.isRecordedFileFromViewer()) {
					String audioPath = audio.getPath();
					Log.i(TAG, "audio[" + i + "] : " + audioPath);
					audioPaths.add(audioPath);
				}
				// File file = new File(audioPath);
				// filePathObject.put("recordFile" + i, uploadPath + file.getName());
			}
			// result = uploadFiles(audioPaths, filePathObject);
		}
		// result = "true";
		return audioPaths;
	}

	// 파일 업로드
	private String uploadFiles(ArrayList<String> fileList, JSONObject filePathObject) {
		String result = "";
		// 완료 이미지 파일들
		String[] filePaths = fileList.toArray(new String[fileList.size()]);
		String files = Arrays.toString(filePaths).replaceAll("\\[|\\]", "").replaceAll(", ", ",");
		// System.out.println(" uploadFiles :" + files);
		// System.out.println("fileList.size() :" + fileList.size());
		boolean ftpUploadResult = false;
		int i = 0;
		while (i < 3) {
			if (!ftpUploadResult) {
				Log.i(TAG, "FTP 업로드 시도 : " + (i + 1) + " 번째 시도");
				ftpUploadResult = fileUpload(files, fileList.size());
			} else {
				break;
			}
			i++;
		}

		// 이부분 임의로넣음
		// ftpUploadResult = true;
		if (ftpUploadResult) {
			result = filePathObject.toString();
		}
		return result;
	};

	// FTP 파일 업로드
	public boolean fileUpload(String files, int fileTotalCount) {
		long saveFileUploadTime = System.currentTimeMillis();
		boolean result = false;
		String uploadResult = "";
		/*
		 * try { uploadResult = new AsyncTaskForUpload(context, "업로드중...",
		 * callbackContext) .execute("UpLoad.aspx", uploadPath, files).get();
		 * 
		 * Log.i(TAG, "upload Result : " + uploadResult); JSONObject jsonResult = new
		 * JSONObject(uploadResult); String resultCode =
		 * jsonResult.getString("RESULT_CODE"); if ("0".equals(resultCode)) { JSONObject
		 * jsonResultData = jsonResult.getJSONObject("RESULT_DATA"); int successCount =
		 * jsonResultData.getInt("successCount"); if (fileTotalCount == successCount) {
		 * result = true; Log.i(TAG, "파일이 모두 정상적으로 업로드 되었습니다."); } else { Log.i(TAG,
		 * "파일 업로드 중에 몇 개가 업로드 되지 않았습니다. 재시도합니다."); } } else { Log.i(TAG,
		 * "파일 업로드가 정상적으로 되지 않았습니다. 재시도합니다."); } } catch (InterruptedException e) {
		 * e.printStackTrace(); Log.i(TAG, "파일 업로드중 오류가 발생하였습니다. 재시도합니다."); } catch
		 * (ExecutionException e) { e.printStackTrace(); Log.i(TAG,
		 * "파일 업로드중 오류가 발생하였습니다. 재시도합니다."); } catch (JSONException e) {
		 * e.printStackTrace(); }
		 */
		logTimeGap("파일 업로드 시간", saveFileUploadTime);
		// return result;
		return true;
	}

	public static boolean isNumeric(String str) {
		return str.matches("[+-]?\\d*(\\.\\d+)?");
	}

	public boolean isJSONObjectValid(String test) {
		try {
			new JSONObject(test);
		} catch (JSONException ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean isJSONArrayValid(String test) {
		try {
			new JSONArray(test);
		} catch (JSONException ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	public void logTimeGap(String msg, long startTime) {
		long currentTime = System.currentTimeMillis();
		Log.i(TAG, msg + " : " + (currentTime - startTime) / 1000.0);
	}

	public String service_submit(String url, String type, String datas) {
		String result = "";
		try {
			result = new AsyncTaskForHttp(context, "", callbackContext)
					.execute(url, type, datas, paramUserId, paramPatientCode).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
			result = "ERR! : " + e.toString();
		} catch (ExecutionException e) {
			e.printStackTrace();
			result = "ERR! : " + e.toString();
		} catch (Exception e) {
			e.printStackTrace();
			result = "ERR! : " + e.toString();
		}
		return result;
	}

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String getMACAddress(String interfaceName) {
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				if (interfaceName != null) {
					if (!intf.getName().equalsIgnoreCase(interfaceName))
						continue;
				}
				byte[] mac = intf.getHardwareAddress();
				if (mac == null)
					return "";
				StringBuilder buf = new StringBuilder();
				for (int idx = 0; idx < mac.length; idx++)
					buf.append(String.format("%02X:", mac[idx]));
				if (buf.length() > 0)
					buf.deleteCharAt(buf.length() - 1);
				return buf.toString();
			}
		} catch (Exception ex) {
		} // for now eat exceptions

		return "";
	}

	private String GetDevicesUUID(Context mContext, CallbackContext callbackContext) {
		final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String deviceId = deviceUuid.toString();
		callbackContext.success(deviceId);
		return deviceId;
	}

	public JSONObject electronicSignature(String esignYn, String hashCode, String password) {
		JSONObject result = new JSONObject();
		// System.out.println("hashCode Length : " + hashCode.length());
		try {
			userCerts = KSCertificateLoader.getUserCertificateListWithGpki(context);
			userCerts = KSCertificateLoader.FilterByExpiredTime(userCerts);

			userCert = (KSCertificate) userCerts.elementAt(0); // 인증서 캐시등록

			if (esignYn.equals("Y")) {
				String certPw = password;
				ProtectedData pwd = new SecureData(certPw.getBytes());

				byte[] signature = KSSign.sign(KSSign.KOSCOM, userCert, hashCode.getBytes(), pwd);
				// 서명결과
				byte[] encodedSignResult = KSBase64.encode(signature);
				String finalSignResult = new String(encodedSignResult);
				result.put("RESULT_CODE", "0");
				result.put("RESULT_DATA", finalSignResult);
				// System.out.println("서명결과 : " + finalSignResult);
				// System.out.println("서명결과 길이 : " + finalSignResult.length());
			} else {
				result.put("RESULT_CODE", "0");
				result.put("RESULT_DATA", "");
			}
		} catch (KSException | JSONException e) {
			e.printStackTrace();
			writeLog("sign catch Exception : " + e.getMessage());
			try {
				result.put("RESULT_CODE", "-1");
				result.put("RESULT_DATA", e.toString());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return result;
	}

	public static int countChar(String str, char ch) {
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == ch) {
				count++;
			}
		}
		return count;
	}

}
