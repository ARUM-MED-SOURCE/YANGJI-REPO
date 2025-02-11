package kr.co.clipsoft.repository.session;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import kr.co.clipsoft.repository.exception.InvalidUserAgentException;

@Component
public class UserAgentValidator {	
	private final String UserAgentWindowsViewer = "CLIP e-Form Viewer \\(Windows\\)";
	private final String UserAgentAndroidViewer = "CLIP e-Form Viewer \\(Android\\)";
	private final String UserAgentIOSViewer = "CLIP e-Form Viewer \\(iOS\\)";
	private final String UserAgentDesigner = "CLIP e-Form Designer";
	private final String UserAgentManagement = "CLIP e-Form Management";
	
	@Value("#{projectProperties['server.enableCheckUserAgent']}")
	private String enableCheckUserAgent;
	
	public void validate(HttpServletRequest request) throws InvalidUserAgentException {
		if(!enableCheckUserAgent.equals("true")) {
			return;
		}
		
		String userAgent = request.getHeader("User-Agent");
		
		if(userAgent.matches(".*" + UserAgentWindowsViewer + ".*")) 
			return;
		
		if(userAgent.matches(".*" + UserAgentAndroidViewer + ".*")) 
			return;
		
		if(userAgent.matches(".*" + UserAgentIOSViewer + ".*")) 
			return;
		
		if(userAgent.matches(".*" + UserAgentDesigner + ".*")) 
			return;
		
		if(userAgent.matches(".*" + UserAgentManagement + ".*")) 
			return;
	
		throw new InvalidUserAgentException("유효하지 않은 클라이언트", userAgent);
	}
}
