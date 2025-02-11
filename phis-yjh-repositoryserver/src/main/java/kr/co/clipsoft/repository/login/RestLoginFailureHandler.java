package kr.co.clipsoft.repository.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

@Component
public class RestLoginFailureHandler implements AuthenticationFailureHandler {

	@Value("#{projectProperties['server.characterEncoding']}")
	private String characterEncoding;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		Gson gson = new GsonBuilder().serializeNulls().create();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("authentication",	 false);
		String jsonString = gson.toJson(jsonObject);
		
		byte[] jsonStringBytes = jsonString.getBytes(characterEncoding);
		response.addHeader("Content-Type", "application/json; charset=" + characterEncoding + ";");
		response.getOutputStream().write(jsonStringBytes);
	}

}
