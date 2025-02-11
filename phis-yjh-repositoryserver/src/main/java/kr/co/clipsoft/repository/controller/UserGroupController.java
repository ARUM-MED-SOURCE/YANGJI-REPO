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
import kr.co.clipsoft.repository.model.ClipUserGroupDto;
import kr.co.clipsoft.repository.service.ClipUserGroupService;
import kr.co.clipsoft.repository.session.SessionInfoUtility;
import kr.co.clipsoft.repository.session.UserAgentValidator;
import kr.co.clipsoft.repository.web.ClipHttpHeadersFactory;
import kr.co.clipsoft.repository.web.ClipResponseEntityFactory;

@Controller
@RequestMapping(value = "/userGroup", produces = "application/json; charset=UTF-8;")
public class UserGroupController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserGroupController.class);
	
	@Autowired
	private UserAgentValidator userAgentValidator;
	
	@Autowired
	private ClipResponseEntityFactory clipResponseEntityFactory;
	
	@Autowired
	private ClipHttpHeadersFactory clipHttpHeadersFactory;
	
	@Autowired
	private ClipUserGroupService clipUserGroupService;
	
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public ResponseEntity<String> newData(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			ClipUserGroupDto dto = gson.fromJson(parameter, ClipUserGroupDto.class);
			
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			dto.setCreateUserId(loginUserId);
			dto.setUpdateUserId(loginUserId);
			
			dto.setUseYN(true);
			
			int insertCount = clipUserGroupService.newData(dto);
			if(insertCount != 1) {
				throw new Exception("데이터 insert 실패.");
			}
			
			ClipUserGroupDto resultDto = clipUserGroupService.get(dto); 
			
			String result = gson.toJson(resultDto);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/update/useYN", method = RequestMethod.POST)
	public ResponseEntity<String> update_useYN(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			ClipUserGroupDto dto = gson.fromJson(parameter, ClipUserGroupDto.class);
			
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			dto.setUpdateUserId(loginUserId);
			
			int updateCount = clipUserGroupService.update_useYN(dto);
			if(updateCount != 1) {
				throw new Exception("데이터 update 실패.");
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
			
			ClipUserGroupDto dto = gson.fromJson(parameter, ClipUserGroupDto.class);
			
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			dto.setCreateUserId(loginUserId);
			dto.setUpdateUserId(loginUserId);
			
			int insertCount = clipUserGroupService.update(dto);
			if(insertCount != 1) {
				throw new Exception("데이터 insert 실패.");
			}
			
			ClipUserGroupDto resultDto = clipUserGroupService.get(dto); 
			
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
			String userId = null;
			Boolean useYN = null;
			if(parameterMap.get("id") != null)
			{
				userId = parameterMap.get("id").getAsString();				
			}
			
			if(parameterMap.get("useYN") != null)
			{
				useYN = parameterMap.get("useYN").getAsBoolean();				
			}						
			
			dto.setProductId(productId);
			dto.setKeyword(keyword);
			dto.setId(userId);
			dto.setUseYN(useYN);
						
			List<ClipUserGroupDto> resultDto = clipUserGroupService.getList(dto);			
			
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
