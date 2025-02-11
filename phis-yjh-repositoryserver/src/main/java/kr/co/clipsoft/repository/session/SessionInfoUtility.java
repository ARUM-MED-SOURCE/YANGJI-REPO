package kr.co.clipsoft.repository.session;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import kr.co.clipsoft.repository.login.ClipUserDetails;

public class SessionInfoUtility {
	public static String getLoginUserId(HttpSession session) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication == null) {
			return null;
		}
		
		return authentication.getName();
	}
	
	public static ClipUserDetails getLoginUserDetails() {
		try {
			return (ClipUserDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
		} catch(Exception exception) {
			return null;
		}
	}
}
