package kr.co.clipsoft.repository.model;

public class ClipSearchDto {

	private Long productId;
	private String id;
	private String keyword;
	private Boolean useYN;
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}	
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public Boolean getUseYN() {
		return useYN;
	}
	public String getUseYN_stringValue() {
		return useYN ? "Y" : "N";
	}
	public void setUseYN(Boolean value) {
		this.useYN = value;
	}
	public void setUseYN_stringValue(String value) {
		this.useYN = value.equals("Y") ? true : false;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
