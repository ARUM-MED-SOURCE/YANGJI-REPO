/*package kr.co.clipsoft.plugin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import com.androidKmi.KmiApi;
import com.lumensoft.ks.KSBase64;
import com.lumensoft.ks.KSCertificate;
import com.lumensoft.ks.KSCertificateLoader;
import com.lumensoft.ks.KSCertificateManager;
import com.lumensoft.ks.KSException;
import com.lumensoft.ks.KSHex;
import com.lumensoft.ks.KSSha1;
import com.lumensoft.ks.KSSign;
import com.signkorea.securedata.ProtectedData;
import com.signkorea.securedata.SecureData;

import android.content.Context;
import kr.co.clipsoft.util.AsyncTaskForHttp;
import kr.co.clipsoft.util.CommonUtil;
import kr.co.clipsoft.util.Storage;

public class ElectronicSignatureByKoscom {	

	private static String TAG_NAME = "KOSCOM";
	private static ElectronicSignatureByKoscom mInstance;	
	private Context context;  
	private KSCertificate cert;	
	private final KmiApi kapi;
	
	public static ElectronicSignatureByKoscom getInstance(Context context){
        if(mInstance == null){
            mInstance = new ElectronicSignatureByKoscom(context);
        }
        return mInstance;
    }

	private ElectronicSignatureByKoscom(Context context){
		this.context = context;	
		serverIp = Storage.getInstance(context).getStorage("eSignUrl");
		serverPort = Storage.getInstance(context).getStorage("eSignPort");
//		serverIp = "10.1.2.89"; // 안암 : 10.1.2.89~90, 구로 : 10.2.2.89~90, 안산 : 10.3.2.89~90 
//		serverPort = "7001";
		cert = null;
		kapi= new KmiApi();
	}
	
	public void koscomSIgnTest() {
		init();
		downloadCert("9999B");
		verificationPassword("1q2w3e4r!!");
		electronicSignature("Y", "ABCDEFGHIJK");
	}
	
	// koscom .so  동적 라이브러리 파일 등록 
	public boolean soFileRegist(){
		boolean result = false;
		try{
			// koscom .so 동적 라이브러리 파일 등록 
			if (KSCertificateManager.libInitialize(context) == KSException.APP_NATIVE_INIT_SUCC) {
				// 앱경로 so 동적 라이브러리 로드 성공시 동작
				result = true; 
			} else if (KSCertificateManager.libInitialize() == KSException.SYSTEM_NATIVE_INIT_SUCC) {
				// System.load함수를 이용하여 so파일 로드 성공시 동작
				result = true; 
			} else {
				// so파일 로드 실패시 동작 
			}
		}catch(Exception e){
			e.printStackTrace();
			 	}
		return result;
	}
	
	// koscomSIgn 초기화 함수 :객체정보 초기화; APP 구동, 종료, 로그아웃 마다 해준다.
	// 로컬 단말에서 인증서 일괄 삭제
	public void init() {		
		String certType1 = "ou=EMR,ou=고려대학교의료원,ou=의료,o=SignKorea,c=KR";
		String certType2 = "ou=SignKorea RA,o=SignKorea,c=KR";
		// 해당 DN 체계를 가진 인증서는 모두 삭제함
//		if ( kapi.LocalDelKeyAndCert(certType) ){
//			LogWrapper.i(TAG_NAME, "[init] 로컬 인증서 삭제 성공");
//		}else{
//			LogWrapper.i(TAG_NAME, "[init] 로컬 인증서 삭제 실패 : " + kapi.errorMsg());
//		}
		
		// 로컬 단말에서 인증서 일괄 삭제 
		
		// 인증서 초기화
		cert = null;
		// 인증서 다운로드 정보 초기화
		JSONObject data = new JSONObject();
		try {
			data.put("CERT_ID", "");
			data.put("CERT_PW", "");
			Storage.getInstance(context).setStorage(data);
		} catch (JSONException e) {							
			e.printStackTrace(); 
		}
	}
	
	// 공인인증서 서버 연결 
	public boolean kmsConnect(String certId){
		boolean result = false;
		JSONObject params = new JSONObject();
		try {
			String respone = new AsyncTaskForHttp(context, "", null).execute("HospitalSvc.aspx", "GetActiveCertServerIP", params.toString(), certId, "").get();		
			if(CommonUtil.getInstance(context).isJSONObjectValid(respone)){
				JSONObject responseObj = new JSONObject(respone);
				if("0".equals(responseObj.getString("RESULT_CODE"))) {
					serverIp  = responseObj.getString("RESULT_DATA");
				}
			}	
			LogWrapper.i(TAG_NAME, "[kmsConnect] 인증서 서버 IP : " +serverIp);
			LogWrapper.i(TAG_NAME, "[kmsConnect] 인증서 서버 Port : " +serverPort);
			if(serverPort.equals("")){
				serverPort = "7001";
			}
			if ( kapi.kmsConnect(serverIp, Integer.parseInt(serverPort)) == false ){
				LogWrapper.i(TAG_NAME, "[kmsConnect] 인증서 서버 연결 실패 : " + kapi.errorMsg());
			}else{
				result = true;
				LogWrapper.i(TAG_NAME, "[kmsConnect] 인증서 서버 연결 성공");
			}
		} catch (InterruptedException | ExecutionException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWrapper.i(TAG_NAME, "[kmsConnect] 인증서 서버 연결 실패 : " + e.toString());
		}
		return result;
	}
	
	// 공인인증서 서버 연결 해제 
	public void kmsDisConnect(){
		// KMI 서버 접속 종료 
		if ( kapi.kmsDisconnect() == false ){
			LogWrapper.i(TAG_NAME, "[kmsDisconnect] 인증서 서버 연결 해제 실패 : " + kapi.errorMsg());
		}else{
			LogWrapper.i(TAG_NAME, "[kmsDisconnect] 인증서 서버 연결 해제 성공");
		}
	}
	
	// koscomSIgn 인증서 다운로드 
	public JSONObject downloadCert(String certId) {		
		JSONObject certResult = new JSONObject();
		int CertImportResult = 0;
		String message = "";
		init();		
		if(kmsConnect(certId)){	
			LogWrapper.i(TAG_NAME, "[DownloadCert] certId : " +certId);
			String strRet = kapi.GetKeyAndCert(certId);
			if ( strRet.equals("") ||  strRet == null){				
				LogWrapper.i(TAG_NAME, "인증서 다운로드 실패 : " + kapi.errorMsg());
				LogWrapper.i(TAG_NAME, "[kapi.GetKeyAndCert] strRet : " + strRet);
				CertImportResult = -1;
				message = kapi.errorMsg();
			}else{
				LogWrapper.i(TAG_NAME, "인증서 다운로드 성공 : " + strRet);
				try {
					Vector<KSCertificate> userCerts = null;
					userCerts = KSCertificateLoader.getUserCertificateListWithGpki(context);
					userCerts = KSCertificateLoader.FilterByExpiredTime(userCerts);
					LogWrapper.e(TAG_NAME, "로컬 인증서 갯수 : " + userCerts.size());
					if(userCerts.size() > 0){
						for (int i = 0; i < userCerts.size(); i++) {
							cert = (KSCertificate) userCerts.get(i);
							getTotalCertInfo(cert);
						}
					}else{
						CertImportResult = -1;
						message = "인증서 다운로드가 정상적으로 되지 않았습니다.";
					}
				} catch (KSException e) {
					e.printStackTrace();
					LogWrapper.i(TAG_NAME, "인증서 선택에 오류가 발생하였습니다.");
					LogWrapper.i(TAG_NAME, e.toString());
					CertImportResult = -1;
					message = "인증서 다운로드 중에 오류가 발생하였습니다.";
				}
			}			
			kmsDisConnect();
		}else{
			CertImportResult = -1;
			message = "인증서 서버 연결에 실패하였습니다.";
		}		
		try {
			certResult.put("RESULT_CODE", CertImportResult);
			certResult.put("RESULT_DATA", message);			
			LogWrapper.i(TAG_NAME, "[DownloadCert] RESULT_CODE : " + CertImportResult);
			LogWrapper.i(TAG_NAME, "[DownloadCert] RESULT_DATA : " + message);
		} catch (JSONException e) {
			e.printStackTrace();
			LogWrapper.i(TAG_NAME, e.toString());
		}		
		return certResult;
	}
	
	// 패스워드 검증
	public boolean verificationPassword(String certPw) {
		LogWrapper.i(TAG_NAME, "[verificationPassword] certPw : " +certPw);
		boolean result = false;
		//키파일 존재여부 확인
		if(cert != null){
			if(!cert.isKeyFileExist()) {
				LogWrapper.i(TAG_NAME, "[verificationPassword] 비밀번호 확인 실패 : 키 파일이 존재하지 않습니다.");
				LogWrapper.i(TAG_NAME, "[cert.isKeyFileExist] : FALSE");
			}else{
		        if (KSCertificateManager.checkPwd(cert, new SecureData(certPw.getBytes()))) {
					LogWrapper.i(TAG_NAME, "[verificationPassword] 인증서의 비밀번호 일치합니다.");
					result = true;
				} else{
		        	LogWrapper.i(TAG_NAME, "[verificationPassword] 인증서의 비밀번호가 일치하지 않습니다.");
		        	LogWrapper.i(TAG_NAME, "[KSCertificateManager.checkPwd] : FALSE");
				}
			}		
		}else{
			LogWrapper.i(TAG_NAME, "[verificationPassword] cert IS NULL");
			LogWrapper.i(TAG_NAME, "[cert.isKeyFileExist] : FALSE");
		}
		return result;
	}	
	
	// 전자서명
	public JSONObject electronicSignature(String esignYn, String hashCode) {
		LogWrapper.i(TAG_NAME, "[koscomSIgnElectronicSign] hashCode : " +hashCode);
		JSONObject result = new JSONObject();
		try {
			if(esignYn.equals("Y")){
				String certPw = Storage.getInstance(context).getStorage("CERT_PW");
				ProtectedData pwd = new SecureData(certPw.getBytes());
		        
				byte[] signature = KSSign.sign(0, cert, hashCode.getBytes(), pwd);				
				// 서명결과
	            byte[] encodedSignResult = KSBase64.encode(signature);        
	            String finalSignResult = new String(encodedSignResult);
	            result.put("RESULT_CODE", "0");
				result.put("RESULT_DATA", finalSignResult);				
				LogWrapper.i(TAG_NAME, "[ElectronicSign] 전자서명 성공");
				LogWrapper.i(TAG_NAME, "[ElectronicSign] 전자서명 결과 : " + finalSignResult);				
			}else{	
				result.put("RESULT_CODE", "0");
				result.put("RESULT_DATA", "");	
				LogWrapper.i(TAG_NAME, "[ElectronicSign] esignYn : " + esignYn);
				LogWrapper.i(TAG_NAME, "[ElectronicSign] 해당 서식은 전자서명을 사용하지 않습니다.");
			}
		}catch (KSException | JSONException e) {
			e.printStackTrace();			
			try {
				result.put("RESULT_CODE", "-1");
				result.put("RESULT_DATA", e.toString());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			LogWrapper.i(TAG_NAME, "[ElectronicSign] 전자서명 실패 : " + e.toString());
		}
		return result;
	}
	
	// 사용자 인증서 다운로드 여부 확인
	public boolean isCertDownload(String certId) {
		boolean result = true;
		if(cert == null){
			result = false;
		}else{
			if(!Storage.getInstance(context).getStorage("CERT_ID").equals(certId)){
				result = false;
			};
		}
		return result;
	}	
	
	public boolean localCertDown(){
		boolean isDownload = false;
		Vector<KSCertificate> userCerts = null;
		try {
			userCerts = KSCertificateLoader.getUserCertificateListWithGpki(context);
			userCerts = KSCertificateLoader.FilterByExpiredTime(userCerts);
			LogWrapper.e(TAG_NAME, "[로컬 인증서 갯수] : " + userCerts.size());
			if(userCerts.size() == 1){				
				KSCertificate userCert = (KSCertificate) userCerts.get(0);				
				getTotalCertInfo(userCert);
				isDownload = true;
			}			
		} catch (KSException e) {			
			e.printStackTrace();
			LogWrapper.e(TAG_NAME, e.toString());
		}		
		return isDownload;
	}	
	
	public JSONObject verificationCertisExpired(){
		JSONObject result =  new JSONObject();		
		try {
			result.put("RESULT_CODE", "0");
			result.put("RESULT_MESSAGE", "");
			result.put("ERROR_CODE", "0");
			result.put("ERROR_MESSAGE", "");
			
			Vector<KSCertificate> userCerts = null;
			userCerts = KSCertificateLoader.getUserCertificateListWithGpki(context);
			userCerts = KSCertificateLoader.FilterByExpiredTime(userCerts);
			LogWrapper.i(TAG_NAME, "[verificationCertisExpired] 로컬 인증서 갯수 : " + userCerts.size());
			for (int i = 0; i < userCerts.size(); i++) {
				KSCertificate userCert = (KSCertificate) userCerts.get(i);
				LogWrapper.i(TAG_NAME, "[getCertisExpired] userCert.isExpired() : " + userCert.isExpired());
				// 만료여부
				if (userCert.isExpired()) {
					result.put("RESULT_CODE", "-1");
					result.put("ERROR_MESSAGE", "인증서가 만료되었습니다.\n의료정보팀에 문의하시기 바랍니다.");
				} else {
					// 만료일 ex)2018.12.15					
					LogWrapper.i(TAG_NAME, "[getCertisExpired] getExpiredTime() : " + userCert.getExpiredTime());
					Date expiredTime = new SimpleDateFormat("yyyy-MM-dd").parse(userCert.getExpiredTime().replaceAll("\\.", "-"));
					long toDate = System.currentTimeMillis();
					LogWrapper.i(TAG_NAME, "[getCertisExpired] toDate : " + toDate);
					long expiredDate = expiredTime.getTime();
					LogWrapper.i(TAG_NAME, "[getCertisExpired] expiredDate : " + expiredDate);
					long diff = expiredDate - toDate;			 
					// Calculate difference in days
					long diffDays = diff / (24 * 60 * 60 * 1000) + 1;
					LogWrapper.i(TAG_NAME, "[getCertisExpired] diffDays : " + diffDays);
					// 30일 이내에 인증서 만료일일 경우 알림
					String interfaceType = Storage.getInstance(context).getStorage("INTERFACE_TYPE");
					long waringTerm  = 30;
					if(interfaceType.equals("DEV")){
						waringTerm = 180;
					}
					if(diffDays <= waringTerm){
						result.put("RESULT_CODE", "1");
						result.put("RESULT_MESSAGE", "["+userCert.getSubjectName()+"]님의\n\n인증서 만료일은 " + userCert.getExpiredTime() +" 입니다.\n\n인증서 만료일까지 "+diffDays+"일 남았습니다.");
					}
				}
			}		
		} catch (ParseException | JSONException | KSException e) {
			e.printStackTrace();	
			try {
				result.put("RESULT_CODE", "-1");
				result.put("ERROR_MESSAGE",  e.toString());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}			
		}		
		return result;
	}
	
	private void getTotalCertInfo(KSCertificate userCert) {

		String cr = "\n\n";

		// 버전
		String version = "버전 : " + userCert.getVersion() + cr;

		// 서명 알고리즘
		String signatureAlgorithm = "서명 알고리즘 : "
				+ userCert.getSignatureAlgorithm() + cr;

		// 주체자 이름
		String subjectName = "주체자 이름 : " + userCert.getSubjectName() + cr;

		// 주체자 Dn
		String subjectDn = "주체자 Dn : " + userCert.getSubjectDn() + cr;

		// 발급자명
		String issuerName = "발급자명 : " + userCert.getIssuerName() + cr;

		// 발급자 Dn
		String issuerDn = "발급자 Dn : " + userCert.getIssuerDn() + cr;

		// 발급자 Cn
		String issuerCn = "발급자 Cn : " + userCert.getIssuerCn() + cr;

		// 발급자 기관명
		String issuerOrg = "발급자 기관명 : " + userCert.getIssuerOrg() + cr;

		// 유효기간 시작
		String notBefore = "유효기간 시작 : " + userCert.getNotBefore() + cr;

		// 유효기간 끝
		String notAfter = "유효기간 끝 : " + userCert.getNotAfter() + cr;

		// 만료일
		String expiredTime = "만료일 : " + userCert.getExpiredTime() + cr;

		// 만료여부
		String isExpired = "만료여부 : ";
		if (userCert.isExpired()) {
			isExpired = isExpired + "만료됨" + cr;
		} else {
			isExpired = isExpired + "만료되지 아니함" + cr;
		}

		// 만료여부(int)
		// 1 : 갱신가능 일자보다 많이 남음
		// 0 : 갱신가능
		// -1 : 만료
		int isExpiredTimeInt = userCert.isExpiredTime();
		String isExpiredTime = "만료여부(int) : ";
		if (isExpiredTimeInt == 1) {
			isExpiredTime = isExpiredTime + "갱신가능 일자 아님" + cr;
		} else if (isExpiredTimeInt == 0) {
			isExpiredTime = isExpiredTime + "갱신가능" + cr;
		} else if (isExpiredTimeInt == -1) {
			isExpiredTime = isExpiredTime + "만료됨" + cr;
		}

		// oid
		
		String oid = "OID : " + userCert.getOID() + cr;

		// 정책
		String policy = "정책 : " + userCert.getPolicy() + cr;

		// 정책(번호)
		String policyNum = "정책(번호) : " + userCert.getPolicyNumString() + cr;

		// 대문자 Hex인코딩 된 공개키
		byte[] publicKey = userCert.getPublicKey();
		String publicKey_HexUpper = "공개키(Hex대문자인코딩) : "
				+ KSHex.encodeUpper(publicKey) + cr;

		// 대문자 Hex인코딩 된 본인확인 메시지
		byte[] vidMsg = userCert.getVidMsg();
		String vidMsg_HexUpper = "본인확인 메시지 : " + KSHex.encodeUpper(vidMsg) + cr;

		// 인증서 경로
		String certPath = "인증서 경로 : " + userCert.getCertPath() + cr;

		// 키 경로
		String keyPath = "키경로 : " + userCert.getKeyPath() + cr;

		// 인증서가 저장된 디렉토리 경로
		String dirPath = "디렉토리 경로 : " + userCert.getDirPath() + cr;

		// 구)방식 인증서 경로
		String path = "구)방식 인증서 경로(디렉토리 경로) : " + userCert.getPath() + cr;

		// 인증서 해쉬값(출력용)
		String hashedCert = "인증서 해쉬값(출력용) : " + userCert.getCertHashHex() + cr;

		// 인증서 해쉬값(sha1)
		KSSha1 d = new KSSha1();
		byte[] certHash = d.digest(userCert.getCertByteArray());
		String hashedCertHex = "인증서 해쉬값 : " + KSHex.encodeUpper(certHash) + cr;

		// 인증서 씨리얼 남바(string(int))
		String getSerialNumberInt = "인증서 시리얼남바(int) : "
				+ userCert.getSerialNumberInt() + cr;

		String totalCertInfo = version + signatureAlgorithm + subjectName
				+ subjectDn + issuerName + issuerDn + issuerCn + issuerOrg
				+ notBefore + notAfter + expiredTime + isExpired
				+ isExpiredTime + oid + policy + policyNum + publicKey_HexUpper
				+ vidMsg_HexUpper + certPath + keyPath + dirPath + path
				+ hashedCert + hashedCertHex + getSerialNumberInt;

		LogWrapper.e(TAG_NAME, "[getTotalCertInfo] 인증서 정보 : \n" + totalCertInfo);
	}
	
	// 오류 메시지 
	public String getErrorMessage(String koscomSIgnException) {
		String errorMessage = "[koscomSIgn]";		
		return errorMessage;
	}
}*/