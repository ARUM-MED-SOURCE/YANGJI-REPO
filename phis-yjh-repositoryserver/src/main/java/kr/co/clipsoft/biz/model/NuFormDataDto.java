package kr.co.clipsoft.biz.model;

import kr.co.clipsoft.repository.model.ClipFormDataDto;

public class NuFormDataDto extends ClipFormDataDto {
	private String documentCode;
	private String userId;

	@Override
	public String getCommitComment() {
		if (super.getCommitComment() == null) {
			return "";
		}
		return super.getCommitComment();
	}

	public String getDocumentCode() {
		return documentCode;
	}

	public void setDocumentCode(String documentCode) {
		this.documentCode = documentCode;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
