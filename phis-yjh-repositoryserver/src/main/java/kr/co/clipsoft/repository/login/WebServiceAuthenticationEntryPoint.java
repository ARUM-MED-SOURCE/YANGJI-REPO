package kr.co.clipsoft.repository.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

public class WebServiceAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

	public WebServiceAuthenticationEntryPoint(String loginUrl)  {
        super(loginUrl);
    }
	
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
    		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "{}");
    }
}
