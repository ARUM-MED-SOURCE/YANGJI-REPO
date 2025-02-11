package kr.co.clipsoft.repository.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import kr.co.clipsoft.repository.session.GlobalSession;

@Component
public class RestLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

	@Value("#{projectProperties['server.characterEncoding']}")
	private String characterEncoding;
	
	@Autowired
	private GlobalSession globalSession;
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		String targetUrl = determineTargetUrl(request, response); 
		
		if (response.isCommitted()) { 
			logger.debug("Response has already been committed. Unable to redirect to " + targetUrl); 
		}
		
		globalSession.unlockFormData(authentication);
		
		Gson gson = new GsonBuilder().serializeNulls().create();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("authentication",	 false);
		String jsonString = gson.toJson(jsonObject);
		
		byte[] jsonStringBytes = jsonString.getBytes(characterEncoding);
		response.addHeader("Content-Type", "application/json; charset=" + characterEncoding + ";");
		response.getOutputStream().write(jsonStringBytes);
	}
}
