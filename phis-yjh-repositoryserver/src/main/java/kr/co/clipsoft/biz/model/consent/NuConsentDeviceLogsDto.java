package kr.co.clipsoft.biz.model.consent;

/**
 * 전자동의서 서비스 처리결과 로그 관리(임시저장/인증저장 사용)
 *
 *
 */
public class NuConsentDeviceLogsDto {

	/**
	 * 전자동의서 저장일시
	 */
	private String saveDt;

	/**
	 * 모바일 기기 serial no
	 */
	private String deviceId;

	/**
	 * OCRTAG
	 */
	private String ocrTag;

	/**
	 * 구분 (T : 임시저장, C : 인증저장, R : 롤백[OCX])
	 */
	private String statCd;

	/**
	 * 전자동의서 저장자 ID
	 */
	private String saveUserId;

	/**
	 * 전자동의서 저장자 부서코드
	 */
	private String saveDeptCd;

	/**
	 * 전자동의서 작성기기 IP
	 */
	private String deviceIp;

	/**
	 * 전자동의서 작성기기 MACADDRESS
	 */
	private String deviceMac;

	/**
	 * 최초등록자ID
	 */
	private String fstRgstRid;

	/**
	 * 최초등록일시
	 */
	private String fstRgstDt;

	/**
	 * 최종수정자ID
	 */
	private String lastUpdtRid;

	/**
	 * 최종수정일시
	 */
	private String lastUpdtDt;

	public String getSaveDt() {
		return saveDt;
	}

	public void setSaveDt(String saveDt) {
		this.saveDt = saveDt;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getOcrTag() {
		return ocrTag;
	}

	public void setOcrTag(String ocrTag) {
		this.ocrTag = ocrTag;
	}

	public String getStatCd() {
		return statCd;
	}

	public void setStatCd(String statCd) {
		this.statCd = statCd;
	}

	public String getSaveUserId() {
		return saveUserId;
	}

	public void setSaveUserId(String saveUserId) {
		this.saveUserId = saveUserId;
	}

	public String getSaveDeptCd() {
		return saveDeptCd;
	}

	public void setSaveDeptCd(String saveDeptCd) {
		this.saveDeptCd = saveDeptCd;
	}

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}

	public String getDeviceMac() {
		return deviceMac;
	}

	public void setDeviceMac(String deviceMac) {
		this.deviceMac = deviceMac;
	}

	public String getFstRgstRid() {
		return fstRgstRid;
	}

	public void setFstRgstRid(String fstRgstRid) {
		this.fstRgstRid = fstRgstRid;
	}

	public String getFstRgstDt() {
		return fstRgstDt;
	}

	public void setFstRgstDt(String fstRgstDt) {
		this.fstRgstDt = fstRgstDt;
	}

	public String getLastUpdtRid() {
		return lastUpdtRid;
	}

	public void setLastUpdtRid(String lastUpdtRid) {
		this.lastUpdtRid = lastUpdtRid;
	}

	public String getLastUpdtDt() {
		return lastUpdtDt;
	}

	public void setLastUpdtDt(String lastUpdtDt) {
		this.lastUpdtDt = lastUpdtDt;
	}

}
