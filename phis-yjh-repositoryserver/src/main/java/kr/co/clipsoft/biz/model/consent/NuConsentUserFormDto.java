package kr.co.clipsoft.biz.model.consent;

/**
 * 사용자별 서식 설정 정보 관리
 *
 *
 */
public class NuConsentUserFormDto {

	/**
	 * 서식코드
	 */
	private String formCd;

	/**
	 * 서식아이디
	 */
	private Long formId;

	/**
	 * 사용자아이디
	 */
	private String userId;

	/**
	 * 내동의서 설정여부
	 */
	private String fvrtYn;

	/**
	 * 등록자 아이디
	 */
	private String createUserId;

	/**
	 * 등록일시
	 */
	private String createDatetime;

	/**
	 * 변경자 아이디
	 */
	private String modifyUserId;

	/**
	 * 변경일시
	 */
	private String modifyDatetime;

	public String getFormCd() {
		return formCd;
	}

	public void setFormCd(String formCd) {
		this.formCd = formCd;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFvrtYn() {
		return fvrtYn;
	}

	public void setFvrtYn(String fvrtYn) {
		this.fvrtYn = fvrtYn;
	}

	public String getModifyUserId() {
		return modifyUserId;
	}

	public void setModifyUserId(String modifyUserId) {
		this.modifyUserId = modifyUserId;
	}

	public String getModifyDatetime() {
		return modifyDatetime;
	}

	public void setModifyDatetime(String modifyDatetime) {
		this.modifyDatetime = modifyDatetime;
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
