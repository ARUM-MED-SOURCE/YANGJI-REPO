package kr.co.clipsoft.repository.exception;

public class InvalidUserAgentException extends ClipException {
	private static final long serialVersionUID = -3069643031334714127L;
	
	private String userAgentValue;
	
	public InvalidUserAgentException(String message, String userAgentValue) {
		super(message + "(" + userAgentValue + ")");
		this.userAgentValue = userAgentValue;
	}

	public String getUserAgentValue() {
		return userAgentValue;
	}
}
