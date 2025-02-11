package kr.co.clipsoft.repository.exception;

import org.slf4j.Logger;

public class ClipException extends Exception {
	private static final long serialVersionUID = 1L;

	public ClipException(String message) {
		super(message);
	}
	
	public static void webAPIErrorLogging(Logger logger, Exception e) {
		logger.error(e.getClass().getSimpleName() + " - " + e.getMessage());
	}
}
