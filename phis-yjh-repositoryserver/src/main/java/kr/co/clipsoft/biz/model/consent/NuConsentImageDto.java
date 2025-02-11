package kr.co.clipsoft.biz.model.consent;

/**
 * 작성동의서 저장이미지 정보 관리
 *
 *
 */
public class NuConsentImageDto {

	/**
	 * 작성동의서 RID
	 */
	private Long consentMstRid;

	/**
	 * 작성동의서 임시저장 RID
	 */
	private Long consentDataRid;

	/**
	 * 이미지 RID
	 */
	private Long consentImageRid;

	/**
	 * 서식 ID
	 */
	private Long formId;

	/**
	 * 이미지 데이타
	 */
	private byte[] imageData;

	/**
	 * 이미지 경로
	 */
	private String imagePath;

	/**
	 * 이미지 파일명
	 */
	private String imageFilename;

	/**
	 * 이미지 중간 경로
	 */
	private String imgFileSubPath;

	/**
	 * 이미지 전체 경로
	 */
	private String imageFullPath;

	/**
	 * 이미지 파일 사이즈
	 */
	private Long imageSizeByte;

	/**
	 * 최종완료 여부
	 */
	private String completeYn;

	/**
	 * 등록자 아이디
	 */
	private String createUserId;

	/**
	 * 등록일자
	 */
	private String createDatetime;

	/**
	 * 이미지 업로드 여부
	 */
	private boolean imageUpload;

	/**
	 * LC테크 이미지 인터페이스 연동 여부
	 */
	private boolean lcTechUpload;

	public Long getConsentMstRid() {
		return consentMstRid;
	}

	public void setConsentMstRid(Long consentMstRid) {
		this.consentMstRid = consentMstRid;
	}

	public Long getConsentDataRid() {
		return consentDataRid;
	}

	public void setConsentDataRid(Long consentDataRid) {
		this.consentDataRid = consentDataRid;
	}

	public Long getConsentImageRid() {
		return consentImageRid;
	}

	public void setConsentImageRid(Long consentImageRid) {
		this.consentImageRid = consentImageRid;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getImageFilename() {
		return imageFilename;
	}

	public void setImageFilename(String imageFilename) {
		this.imageFilename = imageFilename;
	}

	public String getImageFullPath() {
		return imageFullPath;
	}

	public void setImageFullPath(String imageFullPath) {
		this.imageFullPath = imageFullPath;
	}

	public Long getImageSizeByte() {
		return imageSizeByte;
	}

	public void setImageSizeByte(Long imageSizeByte) {
		this.imageSizeByte = imageSizeByte;
	}

	public String getCompleteYn() {
		return completeYn;
	}

	public void setCompleteYn(String completeYn) {
		this.completeYn = completeYn;
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

	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}

	public String getImgFileSubPath() {
		return imgFileSubPath;
	}

	public void setImgFileSubPath(String imgFileSubPath) {
		this.imgFileSubPath = imgFileSubPath;
	}

	public boolean isLcTechUpload() {
		return lcTechUpload;
	}

	public boolean isImageUpload() {
		return imageUpload;
	}

	public void setImageUpload(boolean imageUpload) {
		this.imageUpload = imageUpload;
	}

	public void setLcTechUpload(boolean lcTechUpload) {
		this.lcTechUpload = lcTechUpload;
	}

}
