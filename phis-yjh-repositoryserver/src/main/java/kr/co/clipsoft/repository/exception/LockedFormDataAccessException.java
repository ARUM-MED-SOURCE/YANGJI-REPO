package kr.co.clipsoft.repository.exception;

import kr.co.clipsoft.repository.session.FormDataLockInfo;

public class LockedFormDataAccessException extends Exception {

	private static final long serialVersionUID = 1203667883296744874L;

	private FormDataLockInfo formDataLockInfo;
	
	public LockedFormDataAccessException(String message, FormDataLockInfo formDataLockInfo) {
		super(message);
		this.formDataLockInfo = formDataLockInfo;
	}

	public FormDataLockInfo getFormDataLockInfo() {
		return formDataLockInfo;
	}
}
