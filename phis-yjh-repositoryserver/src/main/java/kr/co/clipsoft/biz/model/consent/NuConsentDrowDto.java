package kr.co.clipsoft.biz.model.consent;

import java.io.UnsupportedEncodingException;

/**
 * 전자동의서 펜드로잉 그리기 정보 관리
 *
 *
 */
public class NuConsentDrowDto {

	/**
	 * 펜그리기 RID
	 */
	private Long consentDrowRid;

	/**
	 * 로그인 사용자 ID
	 */
	private String userId;

	/**
	 * 서식ID
	 */
	private Long formId;

	/**
	 * 서식버전
	 */
	private Long formVersion;

	/**
	 * 펜 그리기 내용
	 */
	private String drow;

	/**
	 * 데이터생성자ID
	 */
	private String createUserId;

	/**
	 * 데이터생성일자
	 */
	private String createDatetime;

	/**
	 * 데이터수정자ID
	 */
	private String modifyUserId;

	/**
	 * 테이터수정일자
	 */
	private String modifyDatetime;

	public Long getConsentDrowRid() {
		return consentDrowRid;
	}

	public void setConsentDrowRid(Long consentDrowRid) {
		this.consentDrowRid = consentDrowRid;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getDrow() {
		return drow;
	}

	public byte[] getDrowByteValue() {
		byte[] result = null;
		try {
			result = drow.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void setDrow(String drow) {
		this.drow = drow;
	}

	public void setDrowByteValue(byte[] drow) {
		try {
			this.drow = new String(drow, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			this.drow = "";
		}
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

}
