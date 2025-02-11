package kr.co.clipsoft.repository.model;

import java.util.Date;

public class ClipUserDto {
	private Long productId;
	private String userId;
	private String password;
	private String name;
	private Boolean useYN;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getUseYN() {
		return useYN;
	}
	public void setUseYN(Boolean useYN) {
		this.useYN = useYN;
	}
	public String getUseYN_stringValue() {
		return useYN ? "Y" : "N";
	}
	public void setUseYN_stringValue(String useYN) {
		this.useYN = useYN.equals("Y") ? true : false;
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
