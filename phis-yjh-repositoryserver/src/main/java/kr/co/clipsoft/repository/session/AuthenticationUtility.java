package kr.co.clipsoft.repository.session;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import kr.co.clipsoft.repository.login.ClipAuthenticationProvider;

@Component
public class AuthenticationUtility {
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationUtility.class);
	
	@Autowired
	private ClipAuthenticationProvider clipAuthenticationProvider;
	
	public boolean login(HttpSession session, String userId, String password, boolean isDirectAuthentication) {
		try {
			Authentication auth = new UsernamePasswordAuthenticationToken(userId, password);
			Authentication loginAuth;
			
			if(isDirectAuthentication) {
				loginAuth = clipAuthenticationProvider.directAuthenticate(auth);
			} else {
				loginAuth = clipAuthenticationProvider.authenticate(auth);
			}
			
			SecurityContextHolder.getContext().setAuthentication(loginAuth); 
			session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
					, SecurityContextHolder.getContext());
			session.setAttribute("userLoginInfo", loginAuth.getDetails());
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}
}
