package kr.co.clipsoft.biz.model.consent;

/**
 * 서식 확장속성 정보 관리
 *
 *
 */
public class NuConsentFormExDto {

	/**
	 * 기관코드
	 */
	private String instCd;

	/**
	 * 서식코드(병원)
	 */
	private String formCd;

	/**
	 * 서식아이디(솔루션)
	 */
	private Long formId;

	/**
	 * 서식버전
	 */
	private Long formVersion;

	/**
	 * 서식타입
	 */
	private String formType;

	/**
	 * 코사인 여부
	 */
	private String cosignYn;

	/**
	 * 시술의 프로세스 적용 여부
	 */
	private String opdrYn;

	/**
	 * 간호사 전자인증 가능 여부
	 */
	private String nursCertYn;

	/**
	 * 최종완료를 위한 전자인증 개수
	 */
	private Integer certNeedCnt;

	/**
	 * 서식에 적용된 외부정의컨트롤 개수
	 */
	private Long externalCnt;

	/**
	 * 연명서식 속성
	 */
	private String lifelongKind;

	/**
	 * 코멘트
	 */
	private String commitComment;

	/**
	 * 등록자 아이디
	 */
	private String createUserId;

	/**
	 * 등록일자
	 */
	private String createDatetime;

	/**
	 * 변경자 아이디
	 */
	private String modifyUserId;

	/**
	 * 변경일자
	 */
	private String modifyDatetime;

	/**
	 * 사용자 아이디
	 */
	private String userId;

	/**
	 * XML히스토리 관리 여부
	 */
	private String xmlHistoryYn;

	/**
	 * 입력컨트롤 입력데이터 등록 여부
	 */
	private String ctlHistoryYn;

	public String getInstCd() {
		return instCd;
	}

	public void setInstCd(String instCd) {
		this.instCd = instCd;
	}

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

	public Long getFormVersion() {
		return formVersion;
	}

	public void setFormVersion(Long formVersion) {
		this.formVersion = formVersion;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public String getCosignYn() {
		return cosignYn;
	}

	public void setCosignYn(String cosignYn) {
		this.cosignYn = cosignYn;
	}

	public Long getExternalCnt() {
		return externalCnt;
	}

	public void setExternalCnt(Long externalCnt) {
		this.externalCnt = externalCnt;
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

	public String getOpdrYn() {
		return opdrYn;
	}

	public void setOpdrYn(String opdrYn) {
		this.opdrYn = opdrYn;
	}

	public String getNursCertYn() {
		return nursCertYn;
	}

	public void setNursCertYn(String nursCertYn) {
		this.nursCertYn = nursCertYn;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getCertNeedCnt() {
		return certNeedCnt;
	}

	public void setCertNeedCnt(Integer certNeedCnt) {
		this.certNeedCnt = certNeedCnt;
	}

	public String getLifelongKind() {
		return lifelongKind;
	}

	public void setLifelongKind(String lifelongKind) {
		this.lifelongKind = lifelongKind;
	}

	public String getCommitComment() {
		return commitComment;
	}

	public void setCommitComment(String commitComment) {
		this.commitComment = commitComment;
	}

	public String getXmlHistoryYn() {
		return xmlHistoryYn;
	}

	public void setXmlHistoryYn(String xmlHistoryYn) {
		this.xmlHistoryYn = xmlHistoryYn;
	}

	public String getCtlHistoryYn() {
		return ctlHistoryYn;
	}

	public void setCtlHistoryYn(String ctlHistoryYn) {
		this.ctlHistoryYn = ctlHistoryYn;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append(this.instCd);
		sb.append(", " + this.formCd);
		sb.append(", " + this.formId);
		sb.append(", " + this.formVersion);
		sb.append(", " + this.formType);
		sb.append(", " + this.cosignYn);
		sb.append(", " + this.opdrYn);
		sb.append(", " + this.nursCertYn);
		sb.append(", " + this.certNeedCnt);
		sb.append(", " + this.externalCnt);
		sb.append(", " + this.createUserId);
		sb.append(", " + this.createDatetime);
		sb.append(", " + this.modifyUserId);
		sb.append(", " + this.modifyDatetime);
		sb.append(", " + this.userId);

		return sb.toString();
	}

}
