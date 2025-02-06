package kr.co.clipsoft.repository.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ClipResponseEntityFactory {
	@Value("#{projectProperties['server.characterEncoding']}")
	private String characterEncoding;
	
	public ClipResponseEntity create(String jsonString) {
		if(jsonString == null || jsonString.length() == 0) {
			jsonString = "{}";
		}
		return new ClipResponseEntity(jsonString, null, null);
	}
	
	public ClipResponseEntity create(String jsonString, HttpHeaders headers, HttpStatus statusCode) {
		if(jsonString == null || jsonString.length() == 0) {
			jsonString = "{}";
		}
		
		headers.add("Content-Type", "application/json; charset=" + characterEncoding + ";");
		
		return new ClipResponseEntity(jsonString, headers, statusCode);
	}
	
	public ClipResponseEntity createInternalServerError() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=" + characterEncoding + ";");
		return new ClipResponseEntity(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
