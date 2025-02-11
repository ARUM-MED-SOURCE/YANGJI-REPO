package kr.co.clipsoft.biz.model.consent;

/**
 * 모바일 단말기 정보 관리
 *
 *
 */
public class NuConsentDeviceMstDto {

	/**
	 * 모바일 기기 serial no'
	 */
	private String deviceId;

	/**
	 * 모바일 기기 관리 부서 코드
	 */
	private String useDeptCd;

	/**
	 * 모바일 기기 관리 부서 명
	 */
	private String useDeptNm;

	/**
	 * 모바일 기기 관리 사용자 ID
	 */
	private String useUserId;

	/**
	 * 모바일 기기 관리 사용자 명
	 */
	private String useUserNm;

	/**
	 * 메모
	 */
	private String memo;

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

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getUseDeptCd() {
		return useDeptCd;
	}

	public void setUseDeptCd(String useDeptCd) {
		this.useDeptCd = useDeptCd;
	}

	public String getUseDeptNm() {
		return useDeptNm;
	}

	public void setUseDeptNm(String useDeptNm) {
		this.useDeptNm = useDeptNm;
	}

	public String getUseUserId() {
		return useUserId;
	}

	public void setUseUserId(String useUserId) {
		this.useUserId = useUserId;
	}

	public String getUseUserNm() {
		return useUserNm;
	}

	public void setUseUserNm(String useUserNm) {
		this.useUserNm = useUserNm;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
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
