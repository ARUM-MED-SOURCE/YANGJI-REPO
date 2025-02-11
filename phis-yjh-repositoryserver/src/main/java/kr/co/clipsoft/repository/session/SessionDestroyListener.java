package kr.co.clipsoft.repository.session;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;

@Component
public class SessionDestroyListener implements ApplicationListener<SessionDestroyedEvent> {

	@Autowired
	private GlobalSession globalSession;
	
	@Override
	public void onApplicationEvent(SessionDestroyedEvent event) {
		List<SecurityContext> securityContextList = event.getSecurityContexts();
        
        for (SecurityContext securityContext : securityContextList) {
        		Authentication authentication = securityContext.getAuthentication();
        		globalSession.unlockFormData(authentication);
        }
	}

}
