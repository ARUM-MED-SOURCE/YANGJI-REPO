package kr.co.clipsoft.biz.model.consent;

import java.io.UnsupportedEncodingException;

/**
 * 작성동의서 인증저장 정보 관리
 *
 *
 */
public class NuConsentImageSignDto {

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
	 * 전자인증 원문값
	 */
	private String certTarget;

	/**
	 * 전자인증 결과값
	 */
	private String certResult;

	/**
	 * 전자인증 활성화 상태
	 */
	private String certStatus;

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
	 * 비활성화 여부
	 */
	private boolean disable;

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

	public String getCertTarget() {
		return certTarget;
	}

	public byte[] getCertTargetByteValue() {
		byte[] result = null;
		try {
			result = certTarget.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void setCertTarget(String certTarget) {
		this.certTarget = certTarget;
	}

	public void setCertTargetByteValue(byte[] certResult) {
		try {
			this.certTarget = new String(certResult, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			this.certTarget = "";
		}
	}

	public String getCertResult() {
		return certResult;
	}

	public byte[] getCertResultByteValue() {
		byte[] result = null;
		try {
			result = certResult.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void setCertResult(String certResult) {
		this.certResult = certResult;
	}

	public void setCertResultByteValue(byte[] certResult) {
		try {
			this.certResult = new String(certResult, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			this.certResult = "";
		}
	}

	public String getCertStatus() {
		return certStatus;
	}

	public void setCertStatus(String certStatus) {
		this.certStatus = certStatus;
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

	public boolean isDisable() {
		return disable;
	}

	public void setDisable(boolean disable) {
		this.disable = disable;
	}

	public boolean validationCheck() {
		if (this.certTarget == null || this.certTarget.length() == 0) {
			return false;
		}
		if (this.certResult == null || this.certResult.length() == 0) {
			return false;
		}
		return true;
	}

	public boolean isEmptyCertData() {

		if (this.certTarget == null || this.certTarget.isEmpty()) {
			return true;
		}

		if (this.certResult == null || this.certResult.isEmpty()) {
			return true;
		}

		return false;
	}

}
