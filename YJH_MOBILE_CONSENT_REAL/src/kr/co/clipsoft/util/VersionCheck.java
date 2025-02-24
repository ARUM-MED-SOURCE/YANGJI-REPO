package kr.co.clipsoft.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/**
  * @FileName : VersionCheck.java
  * @Project : CNUH
  * @Date : 2016. 5. 19. 
  * @작성자 : clipSoft_kimyk
  * @변경이력 : 
  * @프로그램 설명 : 앱의 버전 정보관련된  클래스
  */
public class VersionCheck {
	
	private static final String CONSENT_APP = "kr.co.less.consent.yjh.real";
	private static final String EFORM_APP = "kr.co.clipsoft.eform";
	private static final String TAG = "VersionCheck";
	
	private Context ctx;
	
	public VersionCheck(Context ctx){
		this.ctx = ctx;		
	};
	/**
	  * @Method Name : getCompareVesion
	  * @작성일 : 2016. 5. 19.
	  * @작성자 : clipSoft_kimyk
	  * @변경이력 : 
	  * @Method 설명 : 설치된 버전과 서버 버전을 비교하여 업데이트 내용 여부 확인
	  * @return
	  */
	public Boolean getCompareVesion(){
		Boolean result = false;		
		if(getUpdateVersion(CONSENT_APP) > getCurrentVersion(CONSENT_APP)){
			result = true;
		}
		if(getUpdateVersion(EFORM_APP) > getCurrentVersion(EFORM_APP)){
			result = true;
		}
		Log.i(TAG, "[ 전자동의서 ] 업데이트 버전 : " + getUpdateVersion(CONSENT_APP));
		Log.i(TAG, "[ 전자동의서 ] 현재 버전 : " + getCurrentVersion(CONSENT_APP));
		Log.i(TAG, "[ E-FORM ] 업데이트 버전 : " + getUpdateVersion(EFORM_APP));
		Log.i(TAG, "[ E-FORM ] 현재 버전  : " + getCurrentVersion(EFORM_APP));		
		return result;		
	};
	
	
	/**
	  * @Method Name : getCurrentVersion
	  * @작성일 : 2016. 5. 19.
	  * @작성자 : clipSoft_kimyk
	  * @변경이력 : 
	  * @Method 설명 : 설치 된 앱의 버전 정보를 가져온다.
	  * @param packageName
	  * @return
	  */
	public int getCurrentVersion(String packageName){
		int version = 0;		
		try {
			PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
			version = pInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}
	public String getCurrentVersionName(String packageName){
		String versionName = "";		
		try {
			PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
			versionName = pInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}
	
	/**
	  * @Method Name : getUpdateVersion
	  * @작성일 : 2016. 5. 19.
	  * @작성자 : clipSoft_kimyk
	  * @변경이력 : 
	  * @Method 설명 : 서버에 올라온 최신 앱의 버전 정보를 가져온다. 
	  * @param packageName
	  * @return
	  */
	public int getUpdateVersion(String packageName){		
		int version = 0;
		// 현재 하드코딩으로 테스트함
		if(packageName.equals(EFORM_APP)){
			version = 370;
		}else{
			version = 1;
		}
		return version;
	}
}
