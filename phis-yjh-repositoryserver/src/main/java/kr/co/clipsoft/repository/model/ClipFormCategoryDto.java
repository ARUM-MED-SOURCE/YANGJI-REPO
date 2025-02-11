package kr.co.clipsoft.repository.model;

import java.util.Date;

public class ClipFormCategoryDto {
	private Long productId;
	private Long formCategoryId;
	private String formCategoryCode;
	private Long parentFormCategoryId;
	private String formCategoryName;
	private Long seq;
	private boolean useYN;
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
	public Long getFormCategoryId() {
		return formCategoryId;
	}
	public void setFormCategoryId(Long formCategoryId) {
		this.formCategoryId = formCategoryId;
	}
	public String getFormCategoryCode() {
		return formCategoryCode;
	}
	public void setFormCategoryCode(String formCategoryCode) {
		this.formCategoryCode = formCategoryCode;
	}
	public Long getParentFormCategoryId() {
		return parentFormCategoryId;
	}
	public void setParentFormCategoryId(Long parentFormCategoryId) {
		this.parentFormCategoryId = parentFormCategoryId;
	}
	public String getFormCategoryName() {
		return formCategoryName;
	}
	public void setFormCategoryName(String formCategoryName) {
		this.formCategoryName = formCategoryName;
	}
	public Long getSeq() {
		return seq;
	}
	public void setSeq(Long seq) {
		this.seq = seq;
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
