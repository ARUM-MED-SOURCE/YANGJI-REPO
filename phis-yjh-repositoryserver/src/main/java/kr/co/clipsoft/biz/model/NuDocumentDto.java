package kr.co.clipsoft.biz.model;

import java.util.Date;

public class NuDocumentDto {

	/*
	 * MAPPING TABLE 자동 채번 코드
	 * */
	private Long documentKey=-1L;
	/*
	 * CLIP eForm 에 지정된 문서 고유 ID
	 * */
	private Long formId=-1L;
	/*
	 * 각 사이트에서 사용 중인 문서의 고유 코드 정보
	 * */
	private String documentCode="";

	private Date createDate;
	private String createUserId;
	private Date updateDate;
	private String updateUserId;
	
	public Long getDocumentKey() {
		return documentKey;
	}
	public void setDocumentKey(Long documentKey) {
		this.documentKey = documentKey;
	}

	public Long getFormId() {
		return formId;
	}
	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public String getDocumentCode() {
		return documentCode;
	}
	public void setDocumentCode(String documentCode) {
		this.documentCode = documentCode;
	}

	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getUpdateUserId() {
		return updateUserId;
	}
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}

	public String toString()
	{
		String sDtoString = "";
		sDtoString += "[documentKey] : " + getDocumentKey();
		sDtoString += "[formId] : " + getFormId();
		sDtoString += "[documentCode] : " + getDocumentCode();
		sDtoString += "[createDate] : " + getCreateDate();
		sDtoString += "[createUserId] : " + getCreateUserId();
		sDtoString += "[updateDate] : " + getUpdateDate();
		sDtoString += "[updateUserId] : " + getUpdateUserId();
		return sDtoString;
	}
}
