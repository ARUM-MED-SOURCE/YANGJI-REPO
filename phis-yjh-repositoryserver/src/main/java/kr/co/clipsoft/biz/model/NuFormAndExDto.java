package kr.co.clipsoft.biz.model;

import kr.co.clipsoft.biz.model.consent.NuConsentFormExDto;

public class NuFormAndExDto extends NuConsentFormExDto {

	private Long productId;
	private String formName;
	private String formExtName;
	private String documentCode;
	private boolean useYN;
	private Long formCategoryId;
	private String keyWord;
	private String recentVersionYn;

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getFormExtName() {
		return formExtName;
	}

	public void setFormExtName(String formExtName) {
		this.formExtName = formExtName;
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

	public Long getFormCategoryId() {
		return formCategoryId;
	}

	public void setFormCategoryId(Long formCategoryId) {
		this.formCategoryId = formCategoryId;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public String getRecentVersionYn() {
		return recentVersionYn;
	}

	public void setRecentVersionYn(String recentVersionYn) {
		this.recentVersionYn = recentVersionYn;
	}

}
