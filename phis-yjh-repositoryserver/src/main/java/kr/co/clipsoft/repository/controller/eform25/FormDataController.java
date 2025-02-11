package kr.co.clipsoft.repository.controller.eform25;

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

import kr.co.clipsoft.repository.controller.eform25.impl.FormDataProcessComponent;
import kr.co.clipsoft.repository.exception.ClipException;
import kr.co.clipsoft.repository.session.UserAgentValidator;
import kr.co.clipsoft.repository.web.ClipHttpHeadersFactory;
import kr.co.clipsoft.repository.web.ClipResponseEntityFactory;

@Controller
@RequestMapping(value = "/eForm25", produces = "application/json; charset=UTF-8;")
public class FormDataController {
	private static final Logger logger = LoggerFactory.getLogger(FormDataController.class);

	private final Long EFORM25_PRODUCT_ID = new Long(1);
	private final String EFORM25_FORM_TYPE_NORMAL = "FORM_TYPE_001";
	
	@Autowired
	private UserAgentValidator userAgentValidator;
	
	@Autowired
	private ClipResponseEntityFactory clipResponseEntityFactory;
	
	@Autowired
	private ClipHttpHeadersFactory clipHttpHeadersFactory;
	
	@Autowired
	FormDataProcessComponent formDataProcessComponent;
	
	@RequestMapping(value = "/get/formId", method = RequestMethod.POST)
	public ResponseEntity<String> getFormId(
		HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			String result = formDataProcessComponent.getFormId(null, session, EFORM25_PRODUCT_ID, EFORM25_FORM_TYPE_NORMAL);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			ClipException.webAPIErrorLogging(logger, e);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/unlockFormData", method = RequestMethod.POST)
	public ResponseEntity<String> unlockFormData(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			String result = formDataProcessComponent.unlockFormData(parameter, session, EFORM25_PRODUCT_ID);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			ClipException.webAPIErrorLogging(logger, e);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/isLockFormData", method = RequestMethod.POST)
	public ResponseEntity<String> isLockFormData(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			String result = formDataProcessComponent.isLockFormData(parameter, session, EFORM25_PRODUCT_ID);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			ClipException.webAPIErrorLogging(logger, e);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	
	@RequestMapping(value = "/form/new", method = RequestMethod.POST)
	public ResponseEntity<String> _new(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			String result = formDataProcessComponent._new(parameter, session, EFORM25_PRODUCT_ID, EFORM25_FORM_TYPE_NORMAL);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			ClipException.webAPIErrorLogging(logger, e);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/form/new/version", method = RequestMethod.POST)
	public ResponseEntity<String> newVersion(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			String result = formDataProcessComponent.newVersion(parameter, session, EFORM25_PRODUCT_ID, EFORM25_FORM_TYPE_NORMAL);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			ClipException.webAPIErrorLogging(logger, e);
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/form/update/formData", method = RequestMethod.POST)
	public ResponseEntity<String> updateFormData(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			String result = formDataProcessComponent.updateFormData(parameter, session, EFORM25_PRODUCT_ID, EFORM25_FORM_TYPE_NORMAL);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			ClipException.webAPIErrorLogging(logger, e);
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/form/update/publication", method = RequestMethod.POST)
	public ResponseEntity<String> updatePublication(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			String result = formDataProcessComponent.updatePublication(parameter, session, EFORM25_PRODUCT_ID, EFORM25_FORM_TYPE_NORMAL);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			ClipException.webAPIErrorLogging(logger, e);
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/form/update/use", method = RequestMethod.POST)
	public ResponseEntity<String> updateUse(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session) {
		try {
			userAgentValidator.validate(request);
			
			String result = formDataProcessComponent.updateUse(parameter, session, EFORM25_PRODUCT_ID, EFORM25_FORM_TYPE_NORMAL);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			ClipException.webAPIErrorLogging(logger, e);
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/form/update/name", method = RequestMethod.POST)
	public ResponseEntity<String> updateName(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session) {
		try {
			userAgentValidator.validate(request);
			
			String result = formDataProcessComponent.updateName(parameter, session, EFORM25_PRODUCT_ID, EFORM25_FORM_TYPE_NORMAL);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			ClipException.webAPIErrorLogging(logger, e);
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/form/update/category", method = RequestMethod.POST)
	public ResponseEntity<String> updateCategory(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			String result = formDataProcessComponent.updateCategory(parameter, session, EFORM25_PRODUCT_ID, EFORM25_FORM_TYPE_NORMAL);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			ClipException.webAPIErrorLogging(logger, e);
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/form/get", method = RequestMethod.POST)
	public ResponseEntity<String> get(
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

	@RequestMapping(value = "/form/get/recent", method = RequestMethod.POST)
	public ResponseEntity<String> getRecent(
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
	
	@RequestMapping(value = "/form/getList/allVersion", method = RequestMethod.POST)
	public ResponseEntity<String> getList_allVersion(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			boolean eForm_setPublicationYN = Boolean.valueOf(request.getHeader("eForm_setPublicationYN"));
			
			String result = formDataProcessComponent.getList_allVersion(parameter, session, EFORM25_PRODUCT_ID, EFORM25_FORM_TYPE_NORMAL, eForm_setPublicationYN);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			ClipException.webAPIErrorLogging(logger, e);
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/form/getList/allCategoryForm", method = RequestMethod.POST)
	public ResponseEntity<String> getList_allCategoryForm(
		@RequestParam(value = "parameter", required = true) String parameter
		, HttpServletRequest request
		, HttpSession session
	) {
		try {
			userAgentValidator.validate(request);
			
			boolean eForm_setPublicationYN = Boolean.valueOf(request.getHeader("eForm_setPublicationYN"));
			
			String result = formDataProcessComponent.getList_allCategoryForm(parameter, session, EFORM25_PRODUCT_ID, eForm_setPublicationYN);
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			ClipException.webAPIErrorLogging(logger, e);
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
}
