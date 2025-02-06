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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import kr.co.clipsoft.repository.model.ClipFormCategoryGroupAuthDto;
import kr.co.clipsoft.repository.model.ClipFormCategoryUserAuthDto;
import kr.co.clipsoft.repository.model.ClipFormUserAuthDto;
import kr.co.clipsoft.repository.model.ClipFormUserGroupAuthDto;
import kr.co.clipsoft.repository.model.ClipUserDto;
import kr.co.clipsoft.repository.model.ClipUserGroupDto;
import kr.co.clipsoft.repository.service.ClipActionAuthService;
import kr.co.clipsoft.repository.service.ClipFormCategoryGroupAuthService;
import kr.co.clipsoft.repository.service.ClipFormCategoryUserAuthService;
import kr.co.clipsoft.repository.service.ClipFormUserAuthService;
import kr.co.clipsoft.repository.service.ClipFormUserGroupAuthService;
import kr.co.clipsoft.repository.session.SessionInfoUtility;
import kr.co.clipsoft.repository.session.UserAgentValidator;
import kr.co.clipsoft.repository.web.ClipHttpHeadersFactory;
import kr.co.clipsoft.repository.web.ClipResponseEntityFactory;

@Controller
@RequestMapping(value = "/auth", produces = "application/json; charset=UTF-8;")
public class AuthController {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	
	private final Long EFORM25_PRODUCT_ID = new Long(1);
	
	@Autowired
	private UserAgentValidator userAgentValidator;
	
	@Autowired
	private ClipResponseEntityFactory clipResponseEntityFactory;
	
	@Autowired
	private ClipHttpHeadersFactory clipHttpHeadersFactory;
	
	@Autowired
	private ClipFormCategoryUserAuthService clipFormCategoryUserAuthService;
	
	@Autowired
	private ClipFormUserAuthService clipFormUserAuthService;
	
	@Autowired
	private ClipFormCategoryGroupAuthService clipFormCategoryGroupAuthService; 
	
	@Autowired
	private ClipFormUserGroupAuthService clipFormUserGroupAuthService;
	
	@Autowired
	private ClipActionAuthService clipActionAuthService;
	
	@RequestMapping(value = "/is", method = RequestMethod.POST)
	public ResponseEntity<String> category_user_newData(
		HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			JsonObject resultJson = new JsonObject();
			resultJson.addProperty("authentication", true);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			String result = gson.toJson(resultJson);
			
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/category/user/new", method = RequestMethod.POST)
	public ResponseEntity<String> category_user_newData(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			ClipFormCategoryUserAuthDto dto = gson.fromJson(parameter, ClipFormCategoryUserAuthDto.class);
			dto.setProductId(EFORM25_PRODUCT_ID);
			
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			dto.setCreateUserId(loginUserId);
			dto.setUpdateUserId(loginUserId);
			
			int insertCount = clipFormCategoryUserAuthService.newData(dto);
			if(insertCount != 1) {
				throw new Exception("데이터 insert 실패.");
			}
			
			ClipFormCategoryUserAuthDto resultDto = clipFormCategoryUserAuthService.get(dto); 
			
			String result = gson.toJson(resultDto);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/category/user/delete", method = RequestMethod.POST)
	public ResponseEntity<String> category_user_delete(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			ClipFormCategoryUserAuthDto dto = gson.fromJson(parameter, ClipFormCategoryUserAuthDto.class);
			dto.setProductId(EFORM25_PRODUCT_ID);
			
			int deleteCount = clipFormCategoryUserAuthService.delete(dto);
			
			JsonObject resultJson = new JsonObject();
			if(deleteCount == 1) {
				resultJson.addProperty("deleteAuth", true);
			} else {
				resultJson.addProperty("deleteAuth", false);
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
	
	@RequestMapping(value = "/category/user/getList", method = RequestMethod.POST)
	public ResponseEntity<String> category_user_getList(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
						
			ClipFormCategoryUserAuthDto dto = gson.fromJson(parameter, ClipFormCategoryUserAuthDto.class);
			
			List<ClipFormCategoryUserAuthDto> resultDto = clipFormCategoryUserAuthService.getList(dto);
			
			String result = gson.toJson(resultDto);
			
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/category/user/getAddUserList", method = RequestMethod.POST)
	public ResponseEntity<String> category_user_getAddUserList(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
						
			ClipFormCategoryUserAuthDto dto = gson.fromJson(parameter, ClipFormCategoryUserAuthDto.class);
			
			List<ClipUserDto> resultDto = clipFormCategoryUserAuthService.getAddUserList(dto);
			
			String result = gson.toJson(resultDto);
			
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/category/group/getList", method = RequestMethod.POST)
	public ResponseEntity<String> category_group_getList(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
						
			ClipFormCategoryGroupAuthDto dto = gson.fromJson(parameter, ClipFormCategoryGroupAuthDto.class);
			
			List<ClipFormCategoryGroupAuthDto> resultDto = clipFormCategoryGroupAuthService.getList(dto);
			
			String result = gson.toJson(resultDto);
			
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/category/group/getAddGroupList", method = RequestMethod.POST)
	public ResponseEntity<String> category_group_getAddGroupList(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
						
			ClipFormCategoryGroupAuthDto dto = gson.fromJson(parameter, ClipFormCategoryGroupAuthDto.class);
			
			List<ClipUserGroupDto> resultDto = clipFormCategoryGroupAuthService.getAddGroupList(dto);
			
			String result = gson.toJson(resultDto);
			
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/category/group/new", method = RequestMethod.POST)
	public ResponseEntity<String> category_group_newData(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			ClipFormCategoryGroupAuthDto dto = gson.fromJson(parameter, ClipFormCategoryGroupAuthDto.class);
			dto.setProductId(EFORM25_PRODUCT_ID);
			
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			dto.setCreateUserId(loginUserId);
			dto.setUpdateUserId(loginUserId);
			
			int insertCount = clipFormCategoryGroupAuthService.newData(dto);
			if(insertCount != 1) {
				throw new Exception("데이터 insert 실패.");
			}
			
			ClipFormCategoryGroupAuthDto resultDto = clipFormCategoryGroupAuthService.get(dto); 
			
			String result = gson.toJson(resultDto);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/category/group/delete", method = RequestMethod.POST)
	public ResponseEntity<String> category_group_delete(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			ClipFormCategoryGroupAuthDto dto = gson.fromJson(parameter, ClipFormCategoryGroupAuthDto.class);
			dto.setProductId(EFORM25_PRODUCT_ID);
			
			int deleteCount = clipFormCategoryGroupAuthService.delete(dto);
			if(deleteCount != 1) {
				throw new Exception("데이터 insert 실패.");
			}
			
			JsonObject resultJson = new JsonObject();
			if(deleteCount == 1) {
				resultJson.addProperty("deleteAuth", true);
			} else {
				resultJson.addProperty("deleteAuth", false);
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

	@RequestMapping(value = "/form/user/new", method = RequestMethod.POST)
	public ResponseEntity<String> form_user_newData(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			ClipFormUserAuthDto dto = gson.fromJson(parameter, ClipFormUserAuthDto.class);
			dto.setProductId(EFORM25_PRODUCT_ID);
			
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			dto.setCreateUserId(loginUserId);
			dto.setUpdateUserId(loginUserId);
			
			int insertCount = clipFormUserAuthService.newData(dto);
			if(insertCount != 1) {
				throw new Exception("데이터 insert 실패.");
			}
			
			ClipFormUserAuthDto resultDto = clipFormUserAuthService.get(dto); 
			
			String result = gson.toJson(resultDto);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/form/user/delete", method = RequestMethod.POST)
	public ResponseEntity<String> form_user_delete(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			ClipFormUserAuthDto dto = gson.fromJson(parameter, ClipFormUserAuthDto.class);
			dto.setProductId(EFORM25_PRODUCT_ID);
			
			int deleteCount = clipFormUserAuthService.delete(dto);
			
			JsonObject resultJson = new JsonObject();
			if(deleteCount == 1) {
				resultJson.addProperty("deleteAuth", true);
			} else {
				resultJson.addProperty("deleteAuth", false);
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
	
	@RequestMapping(value = "/form/user/getList", method = RequestMethod.POST)
	public ResponseEntity<String> form_user_getList(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
						
			ClipFormUserAuthDto dto = gson.fromJson(parameter, ClipFormUserAuthDto.class);
			
			List<ClipFormUserAuthDto> resultDto = clipFormUserAuthService.getList(dto);
			
			String result = gson.toJson(resultDto);
			
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/form/user/getAddUserList", method = RequestMethod.POST)
	public ResponseEntity<String> form_user_getAddUserList(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
						
			ClipFormUserAuthDto dto = gson.fromJson(parameter, ClipFormUserAuthDto.class);
			
			List<ClipUserDto> resultDto = clipFormUserAuthService.getAddUserList(dto);
			
			String result = gson.toJson(resultDto);
			
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	

	@RequestMapping(value = "/form/group/getList", method = RequestMethod.POST)
	public ResponseEntity<String> form_group_getList(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
						
			ClipFormUserGroupAuthDto dto = gson.fromJson(parameter, ClipFormUserGroupAuthDto.class);
			
			List<ClipFormUserGroupAuthDto> resultDto = clipFormUserGroupAuthService.getList(dto);
			
			String result = gson.toJson(resultDto);
			
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/form/group/getAddGroupList", method = RequestMethod.POST)
	public ResponseEntity<String> form_group_getAddGroupList(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
						
			ClipFormUserGroupAuthDto dto = gson.fromJson(parameter, ClipFormUserGroupAuthDto.class);
			
			List<ClipUserGroupDto> resultDto = clipFormUserGroupAuthService.getAddGroupList(dto);
			
			String result = gson.toJson(resultDto);
			
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/form/group/new", method = RequestMethod.POST)
	public ResponseEntity<String> form_group_newData(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			ClipFormUserGroupAuthDto dto = gson.fromJson(parameter, ClipFormUserGroupAuthDto.class);
			dto.setProductId(EFORM25_PRODUCT_ID);
			
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			dto.setCreateUserId(loginUserId);
			dto.setUpdateUserId(loginUserId);
			
			int insertCount = clipFormUserGroupAuthService.newData(dto);
			if(insertCount != 1) {
				throw new Exception("데이터 insert 실패.");
			}
			
			ClipFormUserGroupAuthDto resultDto = clipFormUserGroupAuthService.get(dto); 
			
			String result = gson.toJson(resultDto);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/form/group/delete", method = RequestMethod.POST)
	public ResponseEntity<String> form_group_delete(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			ClipFormUserGroupAuthDto dto = gson.fromJson(parameter, ClipFormUserGroupAuthDto.class);
			dto.setProductId(EFORM25_PRODUCT_ID);
			
			int deleteCount = clipFormUserGroupAuthService.delete(dto);
			if(deleteCount != 1) {
				throw new Exception("데이터 insert 실패.");
			}
			
			JsonObject resultJson = new JsonObject();
			if(deleteCount == 1) {
				resultJson.addProperty("deleteAuth", true);
			} else {
				resultJson.addProperty("deleteAuth", false);
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
	
	@RequestMapping(value = "/getList/action", method = RequestMethod.POST)
	public ResponseEntity<String> getList_action(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			
			ClipUserDto dto = gson.fromJson(parameter, ClipUserDto.class);
			dto.setProductId(EFORM25_PRODUCT_ID);
			dto.setUserId(loginUserId);
			
			JsonObject resultJson = clipActionAuthService.getList_authAction(dto);
			
			String result = resultJson.toString();
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/getList/form", method = RequestMethod.POST)
	public ResponseEntity<String> getList_auth_getList_form(
		HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			
			JsonObject resultJson = clipFormUserAuthService.getList_auth_getList_form(EFORM25_PRODUCT_ID, loginUserId);
			
			String result = resultJson.toString();
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/getList/category", method = RequestMethod.POST)
	public ResponseEntity<String> getList_auth_getList_category(
		HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			
			JsonObject resultJson = clipFormUserAuthService.getList_auth_getList_category(EFORM25_PRODUCT_ID, loginUserId);
			
			String result = resultJson.toString();
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
}
