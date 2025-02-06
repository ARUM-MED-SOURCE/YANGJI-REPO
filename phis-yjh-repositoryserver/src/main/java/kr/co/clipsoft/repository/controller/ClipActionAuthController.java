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

import kr.co.clipsoft.repository.model.ClipActionGroupAuthDto;
import kr.co.clipsoft.repository.model.ClipActionUserAuthDto;
import kr.co.clipsoft.repository.model.ClipUserDto;
import kr.co.clipsoft.repository.model.ClipUserGroupDto;
import kr.co.clipsoft.repository.service.ClipActionAuthService;
import kr.co.clipsoft.repository.session.SessionInfoUtility;
import kr.co.clipsoft.repository.session.UserAgentValidator;
import kr.co.clipsoft.repository.web.ClipHttpHeadersFactory;
import kr.co.clipsoft.repository.web.ClipResponseEntityFactory;

@Controller
@RequestMapping(value = "/auth/action", produces = "application/json; charset=UTF-8;")
public class ClipActionAuthController {
	private static final Logger logger = LoggerFactory.getLogger(ClipActionAuthController.class);
	
	@Autowired
	private UserAgentValidator userAgentValidator;
	
	@Autowired
	private ClipResponseEntityFactory clipResponseEntityFactory;
	
	@Autowired
	private ClipHttpHeadersFactory clipHttpHeadersFactory;
	
	@Autowired
	private ClipActionAuthService clipActionAuthService;
	
	@RequestMapping(value = "/group/new", method = RequestMethod.POST)
	public ResponseEntity<String> groupNew(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			ClipActionGroupAuthDto dto = gson.fromJson(parameter, ClipActionGroupAuthDto.class);
			
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			dto.setCreateUserId(loginUserId);
			dto.setUpdateUserId(loginUserId);
			
			int insertCount = clipActionAuthService.insertActionGroupAuth(dto);
			if(insertCount != 1) {
				throw new Exception("데이터 insert 실패.");
			}
			
			ClipActionGroupAuthDto resultDto = clipActionAuthService.getActionGroupAuth(dto); 
			
			String result = gson.toJson(resultDto);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/group/delete", method = RequestMethod.POST)
	public ResponseEntity<String> groupDelete(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			ClipActionGroupAuthDto dto = gson.fromJson(parameter, ClipActionGroupAuthDto.class);
			
			int deleteCount = clipActionAuthService.deleteActionGroupAuth(dto);
			if(deleteCount != 1) {
				throw new Exception("데이터 insert 실패.");
			}
			
			JsonObject resultJson = new JsonObject();
			resultJson.addProperty("result", true);
			
			String result = gson.toJson(resultJson);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/user/new", method = RequestMethod.POST)
	public ResponseEntity<String> userNew(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			ClipActionUserAuthDto dto = gson.fromJson(parameter, ClipActionUserAuthDto.class);
			
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			dto.setCreateUserId(loginUserId);
			dto.setUpdateUserId(loginUserId);
			
			int insertCount = clipActionAuthService.insertActionUserAuth(dto);
			if(insertCount != 1) {
				throw new Exception("데이터 insert 실패.");
			}
			
			ClipActionUserAuthDto resultDto = clipActionAuthService.getActionUserAuth(dto); 
			
			String result = gson.toJson(resultDto);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/user/delete", method = RequestMethod.POST)
	public ResponseEntity<String> userDelete(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			ClipActionUserAuthDto dto = gson.fromJson(parameter, ClipActionUserAuthDto.class);
			
			int deleteCount = clipActionAuthService.deleteActionUserAuth(dto);
			if(deleteCount != 1) {
				throw new Exception("데이터 insert 실패.");
			}
			
			JsonObject resultJson = new JsonObject();
			resultJson.addProperty("result", true);
			
			String result = gson.toJson(resultJson);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/user/getList", method = RequestMethod.POST)
	public ResponseEntity<String> userGetList(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
						
			ClipActionUserAuthDto dto = gson.fromJson(parameter, ClipActionUserAuthDto.class);
			
			List<ClipActionUserAuthDto> resultDto = clipActionAuthService.getList(dto);
			
			String result = gson.toJson(resultDto);
			
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/user/getAddUserList", method = RequestMethod.POST)
	public ResponseEntity<String> userGetAddUserList(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
						
			ClipActionUserAuthDto dto = gson.fromJson(parameter, ClipActionUserAuthDto.class);
			
			List<ClipUserDto> resultDto = clipActionAuthService.getAddUserList(dto);
			
			String result = gson.toJson(resultDto);
			
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/group/getList", method = RequestMethod.POST)
	public ResponseEntity<String> groupGetList(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
						
			ClipActionGroupAuthDto dto = gson.fromJson(parameter, ClipActionGroupAuthDto.class);
			
			List<ClipActionGroupAuthDto> resultDto = clipActionAuthService.getList(dto);
			
			String result = gson.toJson(resultDto);
			
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/group/getAddGroupList", method = RequestMethod.POST)
	public ResponseEntity<String> groupGetAddGroupList(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
						
			ClipActionGroupAuthDto dto = gson.fromJson(parameter, ClipActionGroupAuthDto.class);
			
			List<ClipUserGroupDto> resultDto = clipActionAuthService.getAddGroupList(dto);
			
			String result = gson.toJson(resultDto);
			
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
}
