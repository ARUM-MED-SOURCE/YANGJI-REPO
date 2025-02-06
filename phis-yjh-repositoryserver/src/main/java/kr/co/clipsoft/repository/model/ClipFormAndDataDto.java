package kr.co.clipsoft.repository.model;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class ClipFormAndDataDto {
	private Long productId;
	private Long formId;
	private String formType;
	private String formName;
	private boolean useYN;
	private Long formCategoryId;
	private Long formVersion;
	private String formData;
	private String commitComment;
	private boolean publicationYN;
	private boolean encryptionYN;
	private boolean compressionYN;
	private Date createDate;
	private String createUserId;
	private Date updateDate;
	private String updateUserId;
	
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
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
	public String getFormData() {
		return formData;
	}
	public byte[] getFormDataByteValue() {
		byte[] result = null;
		try {
			result = formData.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return result;
	}	
	public void setFormDataByteValue(byte[] formData) {
		try {
			this.formData = new String(formData, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			this.formData = "";
		}
	}
	public void setFormData(String formData) {
		this.formData = formData;
	}
	public String getCommitComment() {
		return commitComment;
	}
	public void setCommitComment(String commitComment) {
		this.commitComment = commitComment;
	}
	public boolean getPublicationYN() {
		return publicationYN;
	}
	public String getPublicationYN_stringValue() {
		return publicationYN ? "Y" : "N";
	}
	public void setPublicationYN(boolean value) {
		this.publicationYN = value;
	}
	public void setPublicationYN_stringValue(String value) {
		this.publicationYN = value.equals("Y") ? true : false;
	}
	public boolean getEncryptionYN() {
		return encryptionYN;
	}
	public String getEncryptionYN_stringValue() {
		return encryptionYN ? "Y" : "N";
	}
	public void setEncryptionYN(boolean encryptionYN) {
		this.encryptionYN = encryptionYN;
	}
	public boolean getCompressionYN() {
		return compressionYN;
	}
	public String getCompressionYN_stringValue() {
		return compressionYN ? "Y" : "N";
	}
	public void setCompressionYN(boolean compressionYN) {
		this.compressionYN = compressionYN;
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
	public String getFormType() {
		return formType;
	}
	public void setFormType(String formType) {
		this.formType = formType;
	}
	public String getFormName() {
		return formName;
	}
	public void setFormName(String formName) {
		this.formName = formName;
	}
	public Long getFormCategoryId() {
		return formCategoryId;
	}
	public void setFormCategoryId(Long formCategoryId) {
		this.formCategoryId = formCategoryId;
	}
	public boolean getUseYN() {
		return useYN;
	}
	public String getUseYN_stringValue() {
		return useYN ? "Y" : "N";
	}
	public void setUseYN(boolean value) {
		this.useYN = value;
	}
	public void setUseYN_stringValue(String value) {
		this.useYN = value.equals("Y") ? true : false;
	}
}
