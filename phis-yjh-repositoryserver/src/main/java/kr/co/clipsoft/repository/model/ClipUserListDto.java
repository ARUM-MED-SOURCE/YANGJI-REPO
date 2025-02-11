package kr.co.clipsoft.repository.model;


public class ClipUserListDto {
	private Long productId;
	private String userId;
	private String name;
	private Boolean useYN;
	private int totalCount;
	
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
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
