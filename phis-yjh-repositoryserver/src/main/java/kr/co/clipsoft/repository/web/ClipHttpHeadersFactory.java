package kr.co.clipsoft.repository.web;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class ClipHttpHeadersFactory {
	
	@Value("#{projectProperties['server.characterEncoding']}")
	private String characterEncoding;

	public HttpHeaders createCookieHeader(HttpSession session) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("set-cookie", "JSESSIONID=" + session.getId());
		headers.add("Content-Type", "application/json; charset=" + characterEncoding + ";");
		return headers;
	}
}
