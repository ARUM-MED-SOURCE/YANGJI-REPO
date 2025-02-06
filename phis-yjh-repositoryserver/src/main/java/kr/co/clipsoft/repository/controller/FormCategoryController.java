package kr.co.clipsoft.repository.controller;

import java.util.HashMap;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import kr.co.clipsoft.repository.model.ClipFormCategoryDto;
import kr.co.clipsoft.repository.service.ClipFormCategoryService;
import kr.co.clipsoft.repository.session.SessionInfoUtility;
import kr.co.clipsoft.repository.session.UserAgentValidator;
import kr.co.clipsoft.repository.web.ClipHttpHeadersFactory;
import kr.co.clipsoft.repository.web.ClipResponseEntityFactory;

@Controller
@RequestMapping(value = "/category", produces = "application/json; charset=UTF-8;")
public class FormCategoryController {
	private static final Logger logger = LoggerFactory.getLogger(FormCategoryController.class);
	
	@Autowired
	private UserAgentValidator userAgentValidator;
	
	@Autowired
	private ClipResponseEntityFactory clipResponseEntityFactory;
	
	@Autowired
	private ClipHttpHeadersFactory clipHttpHeadersFactory;
	
	@Autowired
	ClipFormCategoryService formCategoryService;
	
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
						
			ClipFormCategoryDto parameterDto = gson.fromJson(parameter, ClipFormCategoryDto.class);
			
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			parameterDto.setCreateUserId(loginUserId);
			parameterDto.setUpdateUserId(loginUserId);
			
			parameterDto.setUseYN(true);
			
			ClipFormCategoryDto insertDto = formCategoryService.newFormCategory(parameterDto);
			if(insertDto == null) {
				throw new Exception("데이터 insert 실패.");
			}
			
			String result = gson.toJson(insertDto);
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
						
			ClipFormCategoryDto parameterDto = gson.fromJson(parameter, ClipFormCategoryDto.class);
			
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			parameterDto.setCreateUserId(loginUserId);
			parameterDto.setUpdateUserId(loginUserId);
			
			int updateCount = formCategoryService.update(parameterDto);
			if(updateCount != 1) {
				throw new Exception("데이터 insert 실패.");
			}
			
			ClipFormCategoryDto insertDto = formCategoryService.get(parameterDto);
			
			String result = gson.toJson(insertDto);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
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
						
			ClipFormCategoryDto parameterDto = gson.fromJson(parameter, ClipFormCategoryDto.class);
			
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			parameterDto.setUpdateUserId(loginUserId);
			
			int updateCount = formCategoryService.update_useYN(parameterDto);
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
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/getList/allCategoryAndForm", method = RequestMethod.POST)
	public ResponseEntity<String> getList_all(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
						
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			
			JsonObject parameterMap = gson.fromJson(parameter, JsonObject.class);
			String itemName = parameterMap.get("itemName").getAsString();
			
			Long productId = parameterMap.get("productId").getAsLong();
			
			List<HashMap> list = formCategoryService.getList_all_categoryAndForm(productId, loginUserId, itemName);
			gson.toJson(list);
			
			HttpHeaders httpHeader = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(gson.toJson(list), httpHeader, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/getList/registCategory", method = RequestMethod.POST)
	public ResponseEntity<String> getList_regist(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
									
			JsonObject parameterMap = gson.fromJson(parameter, JsonObject.class);
			String itemName = parameterMap.get("itemName").getAsString();
			
			Long productId = parameterMap.get("productId").getAsLong();
			
			List<ClipFormCategoryDto> list = formCategoryService.getList_regist_category(productId,itemName);
			gson.toJson(list);
			
			HttpHeaders httpHeader = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(gson.toJson(list), httpHeader, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
}
