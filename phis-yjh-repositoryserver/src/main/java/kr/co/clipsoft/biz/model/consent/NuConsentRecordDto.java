package kr.co.clipsoft.biz.model.consent;

/**
 * 녹취파일 정보 관리
 *
 *
 */
public class NuConsentRecordDto {

	/**
	 * 작성동의서 RID
	 */
	private Long consentMstRid;

	/**
	 * 녹취파일 RID
	 */
	private Long consentRecordRid;

	/**
	 * 서식 ID
	 */
	private Long formId;

	/**
	 * 녹취파일 경로
	 */
	private String recordPath;

	/**
	 * 녹취파일 명
	 */
	private String recordFileName;

	/**
	 * 녹취파일 전체경로
	 */
	private String recordFullPath;

	/**
	 * 녹취파일 사이즈
	 */
	private Long recordSizeByte;

	/**
	 * 작성완료여부
	 */
	private String completeYn;

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
	private String createDatetime;

	public Long getConsentMstRid() {
		return consentMstRid;
	}

	public void setConsentMstRid(Long consentMstRid) {
		this.consentMstRid = consentMstRid;
	}

	public Long getConsentRecordRid() {
		return consentRecordRid;
	}

	public void setConsentRecordRid(Long consentRecordRid) {
		this.consentRecordRid = consentRecordRid;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public String getRecordPath() {
		return recordPath;
	}

	public void setRecordPath(String recordPath) {
		this.recordPath = recordPath;
	}

	public String getRecordFileName() {
		return recordFileName;
	}

	public void setRecordFileName(String recordFileName) {
		this.recordFileName = recordFileName;
	}

	public String getRecordFullPath() {
		return recordFullPath;
	}

	public void setRecordFullPath(String recordFullPath) {
		this.recordFullPath = recordFullPath;
	}

	public Long getRecordSizeByte() {
		return recordSizeByte;
	}

	public void setRecordSizeByte(Long recordSizeByte) {
		this.recordSizeByte = recordSizeByte;
	}

	public String getCompleteYn() {
		return completeYn;
	}

	public void setCompleteYn(String completeYn) {
		this.completeYn = completeYn;
	}

	public String getUseYn() {
		return useYn;
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

	public String getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

}
