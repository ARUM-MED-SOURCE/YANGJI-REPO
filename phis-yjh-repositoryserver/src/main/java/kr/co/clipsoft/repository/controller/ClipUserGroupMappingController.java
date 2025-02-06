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
import kr.co.clipsoft.repository.model.ClipUserGroupMappingDto;
import kr.co.clipsoft.repository.service.ClipUserGroupMappingService;
import kr.co.clipsoft.repository.session.SessionInfoUtility;
import kr.co.clipsoft.repository.session.UserAgentValidator;
import kr.co.clipsoft.repository.web.ClipHttpHeadersFactory;
import kr.co.clipsoft.repository.web.ClipResponseEntityFactory;

@Controller
@RequestMapping(value = "/userGroup/mapping", produces = "application/json; charset=UTF-8;")
public class ClipUserGroupMappingController {
	
	private static final Logger logger = LoggerFactory.getLogger(ClipUserGroupMappingController.class);
	
	@Autowired
	private UserAgentValidator userAgentValidator;
	
	@Autowired
	private ClipResponseEntityFactory clipResponseEntityFactory;
	
	@Autowired
	private ClipHttpHeadersFactory clipHttpHeadersFactory;
	
	@Autowired
	private ClipUserGroupMappingService clipUserGroupMappingService;
	
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
			
			ClipUserGroupMappingDto dto = gson.fromJson(parameter, ClipUserGroupMappingDto.class);
			
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			dto.setCreateUserId(loginUserId);
			dto.setUpdateUserId(loginUserId);
			
			int insertCount = clipUserGroupMappingService.insert(dto);
			if(insertCount != 1) {
				throw new Exception("데이터 insert 실패.");
			}
			
			ClipUserGroupMappingDto resultDto = clipUserGroupMappingService.get(dto); 
			
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
			
			ClipUserGroupMappingDto dto = gson.fromJson(parameter, ClipUserGroupMappingDto.class);
			
			int deleteCount = clipUserGroupMappingService.delete(dto);
			if(deleteCount != 1) {
				throw new Exception("데이터 delete 실패.");
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
	
	@RequestMapping(value = "/getGroupList", method = RequestMethod.POST)
	public ResponseEntity<String> getGroupList(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			JsonObject parameterMap = gson.fromJson(parameter, JsonObject.class);
			
			Long productId = parameterMap.get("productId").getAsLong();
			String keyword = parameterMap.get("keyword").getAsString();
			String userId = parameterMap.get("id").getAsString();
			
			ClipSearchDto dto = new ClipSearchDto();
			dto.setProductId(productId);
			dto.setKeyword(keyword);
			dto.setId(userId);
			
			List<ClipUserGroupMappingDto> resultDto = clipUserGroupMappingService.getGroupList(dto);			
			
			String result = gson.toJson(resultDto);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/getUserList", method = RequestMethod.POST)
	public ResponseEntity<String> getUserList(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			JsonObject parameterMap = gson.fromJson(parameter, JsonObject.class);
			
			Long productId = parameterMap.get("productId").getAsLong();
			String keyword = parameterMap.get("keyword").getAsString();
			String userGroupId = parameterMap.get("id").getAsString();
			
			ClipSearchDto dto = new ClipSearchDto();
			dto.setProductId(productId);
			dto.setKeyword(keyword);
			dto.setId(userGroupId);
			
			List<ClipUserGroupMappingDto> resultDto = clipUserGroupMappingService.getUserList(dto);			
			
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
