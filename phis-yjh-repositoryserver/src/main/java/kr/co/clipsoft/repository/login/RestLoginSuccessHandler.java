package kr.co.clipsoft.repository.login;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

@Component
public class RestLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Value("#{projectProperties['server.characterEncoding']}")
	private String characterEncoding;
	
	private RequestCache requestCache = new HttpSessionRequestCache(); 
	
	@Override 
	public void onAuthenticationSuccess(
		HttpServletRequest request
		, HttpServletResponse response
		, Authentication authentication
	) throws IOException, ServletException { 
		handle(request, response, authentication); 
		clearAuthenticationAttributes(request); 
		
		response.setContentType("text/json"); 
		response.setCharacterEncoding(characterEncoding);		
				 
		Gson gson = new GsonBuilder().serializeNulls().create();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("authentication",	 true);
		String jsonString = gson.toJson(jsonObject);
		
		PrintWriter out = response.getWriter(); 
		out.print(jsonString); 
		out.flush(); 
		out.close();
	} 
	
	protected void handle(
		HttpServletRequest request
		, HttpServletResponse response
		, Authentication authentication
	) throws IOException, ServletException { 	
		SavedRequest savedRequest = requestCache.getRequest(request, response); 
		
		if (savedRequest == null) { 
			clearAuthenticationAttributes(request); 
			return; 
		} 
		
		String targetUrlParam = getTargetUrlParameter(); 
		
		if (isAlwaysUseDefaultTargetUrl() || (targetUrlParam != null && StringUtils.hasText(request.getParameter(targetUrlParam)))) { 
			requestCache.removeRequest(request, response); 
			clearAuthenticationAttributes(request); 
			return; 
		} 
	
		clearAuthenticationAttributes(request); 
	}
	
}
