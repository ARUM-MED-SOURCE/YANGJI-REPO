package kr.co.clipsoft.biz.model.consent;

/**
 * 전자동의서 프로그램 정보 관리
 *
 *
 */
public class NuAppDto {

	/**
	 * 등록순번
	 */
	private Long seqNo;

	/**
	 * 프로그램 종류(업무,솔루션)
	 */
	private String appType;

	/**
	 * 버전
	 */
	private String appVersion;

	/**
	 * 설치파일명
	 */
	private String apkName;

	/**
	 * 비고사항
	 */
	private String memo;

	/**
	 * 사용여부
	 */
	private String useYn;

	/**
	 * 등록자 아이디
	 */
	private String createUserId;

	/**
	 * 등록일시
	 */
	private String createDateTime;

	public Long getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(Long seqNo) {
		this.seqNo = seqNo;
	}

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

	public String getUseYn() {
		return useYn;
	}

	public String getMemo() {
		return memo;
	}

	public byte[] getMemoByteValue() {
		byte[] result = null;
		try {
			result = memo.getBytes("UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public void setMemoByteValue(byte[] memo) {
		try {
			this.memo = new String(memo, "UTF-8");
		} catch (Exception e) {
			this.memo = "";
		}
	}

	public void setUseYn(String useYn) {
		this.useYn = useYn;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(String createDateTime) {
		this.createDateTime = createDateTime;
	}

}
