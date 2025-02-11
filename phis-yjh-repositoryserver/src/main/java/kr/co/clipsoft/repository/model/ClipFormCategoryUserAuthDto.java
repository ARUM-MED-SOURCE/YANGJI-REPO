package kr.co.clipsoft.repository.model;

import java.util.Date;

public class ClipFormCategoryUserAuthDto {
	private Long productId;
	private String userId;
	private Long formCategoryId;
	private String authCode;
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
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Long getFormCategoryId() {
		return formCategoryId;
	}
	public void setFormCategoryId(Long formCategoryId) {
		this.formCategoryId = formCategoryId;
	}
	public String getAuthCode() {
		return authCode;
	}
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
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
