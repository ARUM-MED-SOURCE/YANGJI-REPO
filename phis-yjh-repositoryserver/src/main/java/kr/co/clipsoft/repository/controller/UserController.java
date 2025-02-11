package kr.co.clipsoft.repository.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import kr.co.clipsoft.repository.model.ClipSearchDto;
import kr.co.clipsoft.repository.model.ClipUserDto;
import kr.co.clipsoft.repository.service.ClipUserService;
import kr.co.clipsoft.repository.session.SessionInfoUtility;
import kr.co.clipsoft.repository.session.UserAgentValidator;
import kr.co.clipsoft.repository.web.ClipHttpHeadersFactory;
import kr.co.clipsoft.repository.web.ClipResponseEntityFactory;

@Controller
@RequestMapping(value = "/user", produces = "application/json; charset=UTF-8;")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserAgentValidator userAgentValidator;
	
	@Autowired
	private ClipUserService clipUserService;
	
	@Autowired
	private ClipResponseEntityFactory clipResponseEntityFactory;
	
	@Autowired
	private ClipHttpHeadersFactory clipHttpHeadersFactory;
	
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public ResponseEntity<String> insert(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request 
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			ClipUserDto dto = gson.fromJson(parameter, ClipUserDto.class);
			
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			dto.setCreateUserId(loginUserId);
			dto.setUpdateUserId(loginUserId);

			int insertCount = clipUserService.insert(dto);
			if(insertCount != 1) {
				throw new Exception("데이터 insert 실패.");
			}
			
			ClipUserDto resultDto = clipUserService.get(dto);
			resultDto.setPassword(null);
			
			String result = gson.toJson(resultDto);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseEntity<String> update(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			ClipUserDto dto = gson.fromJson(parameter, ClipUserDto.class);
			
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			dto.setUpdateUserId(loginUserId);

			int updateCount = clipUserService.update(dto);
			if(updateCount != 1) {
				throw new Exception("데이터 update 실패.");
			}
			
			ClipUserDto resultDto = clipUserService.get(dto);
			resultDto.setPassword(null);
			
			String result = gson.toJson(resultDto);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/getList", method = RequestMethod.POST)
	public ResponseEntity<String> getList(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			JsonObject parameterMap = gson.fromJson(parameter, JsonObject.class);
			
			ClipSearchDto dto = new ClipSearchDto();
			
			Long productId = parameterMap.get("productId").getAsLong();
			String keyword = parameterMap.get("keyword").getAsString();
			
			dto.setProductId(productId);
			dto.setKeyword(keyword);
						
			if(parameterMap.get("id") != null && parameterMap.get("id").isJsonNull() == false)
			{
				String id = parameterMap.get("id").getAsString();
				dto.setId(id);
			}				
			
			if(parameterMap.get("useYN") != null)
			{
				Boolean useYN = parameterMap.get("useYN").getAsBoolean();
				dto.setUseYN(useYN);
			}
			
			List<ClipUserDto> resultDto = clipUserService.getList(dto);			
			
			String result = gson.toJson(resultDto);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<String> delete(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {			
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			ClipUserDto dto = gson.fromJson(parameter, ClipUserDto.class);
						
			int deleteCount = clipUserService.delete(dto);
			
			JsonObject resultJson = new JsonObject();
			if(deleteCount == 1) {
				resultJson.addProperty("deleteUser", true);
			} else {
				resultJson.addProperty("deleteUser", false);
			}
			
			String result = resultJson.toString();
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
}
