package kr.co.clipsoft.biz.exception;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 비즈니스 로직 결과 데이터(에러코드, 에러메시지, 처리결과)
 *
 *
 */
public class BizResultInfo {

	private boolean result;
	private String errorCode;
	private String errorMsg;
	private String errorMsg2;

	public BizResultInfo() {
		this.result = true;
		this.errorCode = "";
		this.errorMsg = "";
		this.errorMsg2 = "";
	}

	public BizResultInfo(boolean result, BusinessException e) {
		this.result = result;
		this.errorCode = e.getErrorCode();
		this.errorMsg = e.getMessage();
		this.errorMsg2 = e.getMessage2();
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getErrorMsg2() {
		return errorMsg2;
	}

	public void setErrorMsg2(String errorMsg2) {
		this.errorMsg2 = errorMsg2;
	}

	public JSONObject toJSONObject() throws ParseException {
		return (JSONObject) new JSONParser().parse(toJSONString());
	}

	public String toJSONString() {
		Gson gson = new GsonBuilder().serializeNulls().create();
		return gson.toJson(this);
	}

	public String toResultOfJSONString() {
		JSONObject jsonResult = new JSONObject();
		try {
			jsonResult.put("result", this.toJSONObject());
		} catch (ParseException e) {
			jsonResult.put("result", e.getMessage());
		}
		return jsonResult.toJSONString();
	}
}
