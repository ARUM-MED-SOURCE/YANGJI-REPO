package kr.co.clipsoft.biz.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.co.clipsoft.biz.model.NuTempDataDto;
import kr.co.clipsoft.biz.service.NuFormService;
import kr.co.clipsoft.repository.controller.eform25.impl.FormDataProcessComponent;
import kr.co.clipsoft.repository.exception.ClipException;
import kr.co.clipsoft.repository.session.UserAgentValidator;
import kr.co.clipsoft.repository.web.ClipHttpHeadersFactory;
import kr.co.clipsoft.repository.web.ClipResponseEntity;
import kr.co.clipsoft.repository.web.ClipResponseEntityFactory;

@Controller
@RequestMapping(value = "/biz/nu/member/viewer/eForm25", produces = "application/json; charset=UTF-8;")
public class NuViewerController {

	@Value("#{projectProperties['server.characterEncoding']}")
	private String characterEncoding;
	
	private static final Logger logger = LoggerFactory.getLogger(NuViewerController.class);

	private final Long EFORM25_PRODUCT_ID = new Long(1);
	private final String EFORM25_FORM_TYPE_NORMAL = "FORM_TYPE_001";
	private final String EFORM25_FORM_TYPE_EXTERNAL = "FORM_TYPE_002";
	private final String EFORM25_FORM_TYPE_PAGETEMPLATE = "FORM_TYPE_003";
	
	@Autowired
	private UserAgentValidator userAgentValidator;
	
	@Autowired
	private ClipResponseEntityFactory clipResponseEntityFactory;
	
	@Autowired
	private ClipHttpHeadersFactory clipHttpHeadersFactory;

	@Autowired
	private NuFormService nuFormService;
	
	@Autowired
	FormDataProcessComponent formDataProcessComponent;
	/*
	 * 서식 조회 관련 API
	 * /eForm/form/get/recent
	 * /eForm/form/get
	 * */

	@RequestMapping(value = "/form/get/recent", method = RequestMethod.POST)
	public ResponseEntity<String> getFormRecent(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			boolean eForm_setPublicationYN = Boolean.valueOf(request.getHeader("eForm_setPublicationYN"));
			
			String result = formDataProcessComponent.getRecent(parameter, session, EFORM25_PRODUCT_ID, EFORM25_FORM_TYPE_NORMAL, eForm_setPublicationYN);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			ClipException.webAPIErrorLogging(logger, e);
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/form/get", method = RequestMethod.POST)
	public ResponseEntity<String> getForm(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			boolean eForm_setPublicationYN = Boolean.valueOf(request.getHeader("eForm_setPublicationYN"));
			
			String result = formDataProcessComponent.get(parameter, session, EFORM25_PRODUCT_ID, EFORM25_FORM_TYPE_NORMAL, eForm_setPublicationYN);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			ClipException.webAPIErrorLogging(logger, e);
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	
	@RequestMapping(value = "/get/nuData", method = RequestMethod.POST)
	public ResponseEntity<String> getNuTempData(
			@RequestParam(value = "OP", required = true) String op
			, @RequestParam(value = "PD", required = true) String pd
			, HttpServletRequest request
			, HttpSession session
	) {

		try {
			userAgentValidator.validate(request);

			NuTempDataDto resultDto = nuFormService.getNuTempData(pd);
			String result = resultDto.getDataXml();
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			headers.add("Content-Type", "application/xml; charset=" + characterEncoding + ";");
			
			return new ClipResponseEntity(result, headers,  HttpStatus.OK);
			
		} catch (Exception e) {

			logger.error(e.toString());

			return clipResponseEntityFactory.createInternalServerError();

		}

	}
	
	/*
	 * 외부사용자 조회 관련 API
	 * /eForm/external/get/recent
	 * /eForm/external/get
	 * */
	@RequestMapping(value = "/external/get/recent", method = RequestMethod.POST)
	public ResponseEntity<String> getExternalRecent(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			boolean eForm_setPublicationYN = Boolean.valueOf(request.getHeader("eForm_setPublicationYN"));
			
			String result = formDataProcessComponent.getRecent(parameter, session, EFORM25_PRODUCT_ID, EFORM25_FORM_TYPE_EXTERNAL, eForm_setPublicationYN);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/external/get", method = RequestMethod.POST)
	public ResponseEntity<String> getExternal(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);

			boolean eForm_setPublicationYN = Boolean.valueOf(request.getHeader("eForm_setPublicationYN"));
			
			String result = formDataProcessComponent.get(parameter, session, EFORM25_PRODUCT_ID, EFORM25_FORM_TYPE_EXTERNAL, eForm_setPublicationYN);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	/*
	 * 페이지템플릿 조회 관련 API
	 * /eForm/pageTemplate/getList/all
	 * */
	@RequestMapping(value = "/pageTemplate/getList/all", method = RequestMethod.POST)
	public ResponseEntity<String> getPageTemplateList_all(
		HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			boolean eForm_setPublicationYN = Boolean.valueOf(request.getHeader("eForm_setPublicationYN"));
			
			String result = formDataProcessComponent.getList_all(null, session, EFORM25_PRODUCT_ID, EFORM25_FORM_TYPE_PAGETEMPLATE, eForm_setPublicationYN);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	/*
	 * 페이지템플릿 조회 관련 API
	 * /eForm/pageTemplate/getList/all
	 * /eForm/pageTemplate/get/recent
	 * /eForm/pageTemplate/get​
	 * */
	@RequestMapping(value = "/pageTemplate/getList/allVersion", method = RequestMethod.POST)
	public ResponseEntity<String> getPageTemplateList_allVersion(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			boolean eForm_setPublicationYN = Boolean.valueOf(request.getHeader("eForm_setPublicationYN"));
			
			String result = formDataProcessComponent.getList_allVersion(parameter, session, EFORM25_PRODUCT_ID, EFORM25_FORM_TYPE_PAGETEMPLATE, eForm_setPublicationYN);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	
	@RequestMapping(value = "/pageTemplate/get/recent", method = RequestMethod.POST)
	public ResponseEntity<String> getPageTemplateRecent(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			boolean eForm_setPublicationYN = Boolean.valueOf(request.getHeader("eForm_setPublicationYN"));
			
			String result = formDataProcessComponent.getRecent(parameter, session, EFORM25_PRODUCT_ID, EFORM25_FORM_TYPE_PAGETEMPLATE, eForm_setPublicationYN);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/pageTemplate/get", method = RequestMethod.POST)
	public ResponseEntity<String> getPageTemplate(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			boolean eForm_setPublicationYN = Boolean.valueOf(request.getHeader("eForm_setPublicationYN"));
			
			String result = formDataProcessComponent.get(parameter, session, EFORM25_PRODUCT_ID, EFORM25_FORM_TYPE_PAGETEMPLATE, eForm_setPublicationYN);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/insert/nuData", method = RequestMethod.POST)
	public ResponseEntity<String> insertNuTempData(@RequestParam(value = "parameter", required = true) String parameter,
		HttpSession session) {

		try {
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();						

			logger.debug(parameter);
			NuTempDataDto formDto = gson.fromJson(parameter, NuTempDataDto.class);				


			NuTempDataDto resultDto = nuFormService.insertNuTempFormData(formDto);			

			if(resultDto == null)
			{
				throw new Exception("nU Form 데이터 저장 실패");
			}

			String result = gson.toJson(formDto);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);

		} catch (Exception e) {

			logger.error(e.toString());

			return clipResponseEntityFactory.createInternalServerError();

		}

	}
}
