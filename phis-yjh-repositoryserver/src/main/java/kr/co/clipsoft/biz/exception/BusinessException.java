package kr.co.clipsoft.biz.exception;

/**
 * 비즈니스 로직 예외처리 핸들러
 *
 *
 */
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final BizErrorInfo errorData;

	public BusinessException(BizErrorInfo errorData) {
		this.errorData = errorData;
	}

	public BizErrorInfo getErrorData() {
		return errorData;
	}

	@Override
	public String getMessage() {
		return this.errorData.getMsg();
	}

	public String getMessage2() {
		return this.errorData.getMsg2();
	}

	public String getErrorCode() {
		return this.errorData.getCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[CODE : " + errorData.getCode() + "]");
		sb.append("[MESSAGE : " + errorData.getMsg() + "]");
		sb.append("[MESSAGE2 : " + errorData.getMsg2() + "]");
		return sb.toString();
	}

}
