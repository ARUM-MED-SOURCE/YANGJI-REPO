package kr.co.clipsoft.biz.model.consent;

import java.io.UnsupportedEncodingException;

/**
 * 작성동의서 임시저장 데이터 관리
 *
 *
 */
public class NuConsentDataDto {

	/**
	 * 작성동의서 RID
	 */
	private Long consentMstRid;

	/**
	 * 작성동의서 임시저장 RID
	 */
	private Long consentDataRid;

	/**
	 * 서식 ID
	 */
	private Long formId;

	/**
	 * 서식 FORM XML
	 */
	private String formXml;

	/**
	 * 서식 DATA XML
	 */
	private String dataXml;

	/**
	 * 단말기종류(모바일,PC)
	 */
	private String deviceType;

	/**
	 * 단말기고유ID
	 */
	private String deviceIdentNo;

	/**
	 * 작성 완료 여부
	 */
	private String completeYn;

	/**
	 * 작성자 ID
	 */
	private String createUserId;

	/**
	 * 작성일
	 */
	private String createDatetime;

	/**
	 * 저장 JSONDATA(미사용)
	 */
	private String saveDataJson;

	/**
	 * 저장 길이(미사용)
	 */
	private Long saveDataLength;

	
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

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public String getFormXml() {
		return formXml;
	}

	public byte[] getFormXmlByteValue() {
		byte[] result = null;
		try {
			result = formXml.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void setFormXml(String formXml) {
		this.formXml = formXml;
	}

	public void setFormXmlByteValue(byte[] formXml) {
		try {
			this.formXml = new String(formXml, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			this.formXml = "";
		}
	}

	public String getDataXml() {
		return dataXml;
	}

	public byte[] getDataXmlByteValue() {
		byte[] result = null;
		try {
			result = dataXml.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void setDataXml(String dataXml) {
		this.dataXml = dataXml;
	}

	public void setDataXmlByteValue(byte[] dataXml) {
		try {
			this.dataXml = new String(dataXml, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			this.dataXml = "";
		}
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceIdentNo() {
		return deviceIdentNo;
	}

	public void setDeviceIdentNo(String deviceIdentNo) {
		this.deviceIdentNo = deviceIdentNo;
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

	public String getSaveDataJson() {
		return saveDataJson;
	}

	public void setSaveDataJson(String saveDataJson) {
		this.saveDataJson = saveDataJson;
	}

	public Long getSaveDataLength() {
		return saveDataLength;
	}

	public void setSaveDataLength(Long saveDataLength) {
		this.saveDataLength = saveDataLength;
	}
 
}
