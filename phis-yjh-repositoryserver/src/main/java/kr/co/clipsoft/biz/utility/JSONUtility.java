package kr.co.clipsoft.biz.utility;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * JSON 변환 유틸리틸
 *
 *
 */
@Component
public class JSONUtility {

	private Gson gson;

	public JSONUtility() {
		this.gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	}

	/**
	 * JSON String To Object
	 * 
	 * @param json     JSON String
	 * @param classOfT Object class Type
	 * @return Object
	 * @throws JsonSyntaxException
	 */
	public <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
		return gson.fromJson(json, classOfT);
	}

	/**
	 * Object To JSON String
	 * 
	 * @param src Object
	 * @return JSON String
	 */
	public String toJson(Object src) {
		return gson.toJson(src);
	}

}
