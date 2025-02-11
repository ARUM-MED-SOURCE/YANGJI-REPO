package kr.co.clipsoft.biz.model;

import java.util.Date;

public class NuFormDto {

	private Long productId;
	private Long formId;
	private String formType;
	private String formName;
	private String documentCode;
	private boolean useYN;
	private Long formCategoryId;
	private Date createDate;
	private String createUserId;
	private Date updateDate;
	private String updateUserId;
	
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
	public String getDocumentCode() {
		return documentCode;
	}
	public void setDocumentCode(String documentCode) {
		this.documentCode = documentCode;
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
	public Long getFormCategoryId() {
		return formCategoryId;
	}
	public void setFormCategoryId(Long formCategoryId) {
		this.formCategoryId = formCategoryId;
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
}
