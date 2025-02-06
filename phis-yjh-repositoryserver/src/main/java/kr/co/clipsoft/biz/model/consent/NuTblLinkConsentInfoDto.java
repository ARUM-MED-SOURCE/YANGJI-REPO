package kr.co.clipsoft.biz.model.consent;

/**
 * 작성동의서 저장 이미지 LC테크 이미지 인터페이스 테이블 정보 관리
 *
 *
 */
public class NuTblLinkConsentInfoDto {
	/**
	 * A:초기전송,추가전송, D:Page삭제, R:재전송
	 */
	private String cmd;

	/**
	 * 환자 ID
	 */
	private String patId;

	/**
	 * 진료(수진)일자 - ordDd 동일
	 */
	private String ordDate;

	/**
	 * OCRTAG
	 */
	private String ocrTag;

	/**
	 * Image Path
	 */
	private String imgPath;

	/**
	 * Image Index
	 */
	private Integer imgIndex;

	/**
	 * 기관 코드
	 */
	private String spcId;

	/**
	 * 진료(수진)일자 - ordDate 동일
	 */
	private String ordDd;

	/**
	 * 원무등록순번
	 */
	private Long cretNo;

	/**
	 * I:입원, O:외래, E:응급
	 */
	private String patFlg;

	/**
	 * 진료과 코드
	 */
	private String examTyp;

	/**
	 * 검사 시행자 ID
	 */
	private String userId;

	/**
	 * S:성공, F:실패, P:초기상태
	 */
	private String result;

	/**
	 * 서식명
	 */
	private String examName;

	/**
	 * 서식코드
	 */
	private String formCd;

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getPatId() {
		return patId;
	}

	public void setPatId(String patId) {
		this.patId = patId;
	}

	public String getOrdDate() {
		return ordDate;
	}

	public void setOrdDate(String ordDate) {
		this.ordDate = ordDate;
	}

	public String getOcrTag() {
		return ocrTag;
	}

	public void setOcrTag(String ocrTag) {
		this.ocrTag = ocrTag;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public Integer getImgIndex() {
		return imgIndex;
	}

	public void setImgIndex(Integer imgIndex) {
		this.imgIndex = imgIndex;
	}

	public String getSpcId() {
		return spcId;
	}

	public void setSpcId(String spcId) {
		this.spcId = spcId;
	}

	public String getOrdDd() {
		return ordDd;
	}

	public void setOrdDd(String ordDd) {
		this.ordDd = ordDd;
	}

	public Long getCretNo() {
		return cretNo;
	}

	public void setCretNo(Long cretNo) {
		this.cretNo = cretNo;
	}

	public String getPatFlg() {
		return patFlg;
	}

	public void setPatFlg(String patFlg) {
		this.patFlg = patFlg;
	}

	public String getExamTyp() {
		return examTyp;
	}

	public void setExamTyp(String examTyp) {
		this.examTyp = examTyp;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getExamName() {
		return examName;
	}

	public void setExamName(String examName) {
		this.examName = examName;
	}

	public String getFormCd() {
		return formCd;
	}

	public void setFormCd(String formCd) {
		this.formCd = formCd;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
