package kr.co.clipsoft.repository.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public class ClipResponseEntity extends ResponseEntity<String> {
	
	public ClipResponseEntity(String body, MultiValueMap<String, String> headers, HttpStatus statusCode) {
		super(
			body != null ? body : "{}", headers, statusCode
		);
	}
	
	public ClipResponseEntity(String body) {
		super(body != null ? body : "{}", null, null);
	}
}
