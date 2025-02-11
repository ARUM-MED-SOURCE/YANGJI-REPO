package kr.co.clipsoft.biz.controller;

import java.util.Arrays;
import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonObject;

import kr.co.clipsoft.biz.model.NuDocumentDto;
import kr.co.clipsoft.biz.model.NuFormAndDataDto;
import kr.co.clipsoft.biz.model.NuFormAndExDto;
import kr.co.clipsoft.biz.model.NuFormDataDto;
import kr.co.clipsoft.biz.model.NuFormDto;
import kr.co.clipsoft.biz.model.consent.NuConsentFormExDto;
import kr.co.clipsoft.biz.service.NuFormService;
import kr.co.clipsoft.biz.utility.JSONUtility;
import kr.co.clipsoft.biz.utility.xml.FormDataUtility;
import kr.co.clipsoft.repository.session.SessionInfoUtility;
import kr.co.clipsoft.repository.web.ClipHttpHeadersFactory;
import kr.co.clipsoft.repository.web.ClipResponseEntityFactory;

@Controller
@RequestMapping(value = "/biz/nu/member/form", produces = "application/json; charset=UTF-8;")
public class NuFormController {
	private static final Logger logger = LoggerFactory.getLogger(NuFormController.class);
	private static final Long PRODUCT_ID = 1L;
	private static final String EFORM25_FORM_TYPE_NORMAL = "FORM_TYPE_001";

	/**
	 * 기관(병원) 코드
	 */
	@Value("#{customerProperties['server.companyCode']}")
	private String companyCode;

	/**
	 * 디자이너 서식속성관리 화면 활성화 여부
	 */
	@Value("#{customerProperties['designer.ex.manager.enable']}")
	private String dsExMngEnable;

	/**
	 * 디자이너 서식속성관리 활성화 필드 리스트
	 */
	@Value("#{customerProperties['designer.ex.manager.enable.field']}")
	private String strDsExMngEnableFields;

	@Autowired
	private JSONUtility jsonUtility;

	@Autowired
	private ClipResponseEntityFactory clipResponseEntityFactory;

	@Autowired
	private ClipHttpHeadersFactory clipHttpHeadersFactory;

	@Autowired
	private FormDataUtility formXmlUtility;

	@Autowired
	private NuFormService nuFormService;

	@RequestMapping(value = "/ds/ex/config/get", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> getDesignerConfig(HttpSession session) {
		try {
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

			JsonObject jsonConfig = new JsonObject();
			jsonConfig.addProperty("enable", Boolean.parseBoolean(dsExMngEnable));
			jsonConfig.addProperty("enableField", strDsExMngEnableFields);

			return clipResponseEntityFactory.create(jsonUtility.toJson(jsonConfig), headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/getDocumentCode", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> getDocumentCode(@RequestParam(value = "parameter", required = true) String parameter, HttpSession session) {
		try {
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

			NuDocumentDto parameterDto = jsonUtility.fromJson(parameter, NuDocumentDto.class);
			parameterDto = nuFormService.getDocumentCode(parameterDto);
			if (parameterDto == null) {
				return clipResponseEntityFactory.create("{}", headers, HttpStatus.OK);
			}

			logger.debug(parameterDto.toString());

			String result = jsonUtility.toJson(parameterDto);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/insertDocumentCode", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResponseEntity<String> insertDocumentCode(@RequestParam(value = "parameter", required = true) String parameter, HttpSession session) {
		logger.debug("CLIP NuFormController : ResponseEntity<String> insertDocumentCode");
		try {

			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			NuDocumentDto parameterDto = jsonUtility.fromJson(parameter, NuDocumentDto.class);

			parameterDto.setCreateUserId(loginUserId);
			parameterDto.setUpdateUserId(loginUserId);

			int result = nuFormService.insertDocumentCode(parameterDto);

			return clipResponseEntityFactory.create(String.valueOf(result), headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/get/formId", method = RequestMethod.POST)
	public ResponseEntity<String> getFormId(@RequestParam(value = "parameter", required = true) String parameter, HttpSession session) {
		try {
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

			NuDocumentDto parameterDto = jsonUtility.fromJson(parameter, NuDocumentDto.class);
			String result = nuFormService.getFormId(PRODUCT_ID, parameterDto);

			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public ResponseEntity<String> _new(@RequestParam(value = "parameter", required = true) String parameter, HttpSession session) {

		try {

			NuFormDto formDto = jsonUtility.fromJson(parameter, NuFormDto.class);

			formDto.setProductId(PRODUCT_ID);
			formDto.setFormType(EFORM25_FORM_TYPE_NORMAL);

			NuFormDataDto dataDto = jsonUtility.fromJson(parameter, NuFormDataDto.class);
			dataDto.setProductId(PRODUCT_ID);

			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			formDto.setCreateUserId(loginUserId);
			formDto.setUpdateUserId(loginUserId);
			dataDto.setCreateUserId(loginUserId);
			dataDto.setUpdateUserId(loginUserId);

			NuDocumentDto documentDto = new NuDocumentDto();
			documentDto.setFormId(formDto.getFormId());
			documentDto.setDocumentCode(formDto.getDocumentCode());
			documentDto.setCreateUserId(loginUserId);
			documentDto.setUpdateUserId(loginUserId);

			int insertCnt = nuFormService.insertDocumentCode(documentDto);
			if (insertCnt == 0) {
				throw new Exception("Document Code 등록/수정 실패");
			}

			int insertCount = nuFormService.newFormData(formDto, dataDto);
			if (insertCount != 1) {
				throw new Exception("데이터 insert 실패.");
			}

			NuFormAndDataDto insertDto = nuFormService.get(formDto, dataDto.getFormVersion());
			String result = insertDto != null ? jsonUtility.toJson(insertDto) : "{}";

			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/newmig", method = RequestMethod.POST)
	public ResponseEntity<String> _newMig(@RequestParam(value = "parameter", required = true) String parameter, HttpSession session) {
		try {

			NuFormDto formDto = jsonUtility.fromJson(parameter, NuFormDto.class);

			formDto.setProductId(PRODUCT_ID);
			formDto.setFormType(EFORM25_FORM_TYPE_NORMAL);

			NuFormDataDto dataDto = jsonUtility.fromJson(parameter, NuFormDataDto.class);
			dataDto.setProductId(PRODUCT_ID);

			String loginUserId = SessionInfoUtility.getLoginUserId(session);

			NuDocumentDto documentDto = new NuDocumentDto();
			documentDto.setFormId(formDto.getFormId());
			documentDto.setDocumentCode(formDto.getDocumentCode());
			documentDto.setCreateUserId(dataDto.getCreateUserId());
			documentDto.setUpdateUserId(dataDto.getCreateUserId());

			NuConsentFormExDto formExDto = new NuConsentFormExDto();
			formExDto.setInstCd(companyCode);
			formExDto.setFormCd(dataDto.getDocumentCode());
			formExDto.setFormId(dataDto.getFormId());
			formExDto.setFormVersion(dataDto.getFormVersion());
			formExDto.setFormType(formDto.getFormType());
			formExDto.setCosignYn("N");

			Long externalCtlCnt = formXmlUtility.getExternalCtlCount(dataDto.getFormData());
			formExDto.setExternalCnt(externalCtlCnt);
			formExDto.setCreateUserId(dataDto.getUpdateUserId());
			formExDto.setModifyUserId(dataDto.getUpdateUserId());

			int insertCnt = nuFormService.insertDocumentCode(documentDto);
			if (insertCnt == 0) {
				throw new Exception("Document Code 등록/수정 실패");
			}

			int insertCount = nuFormService.newFormDataMig(formDto, dataDto, formExDto);
			if (insertCount != 1) {
				throw new Exception("데이터 insert 실패.");
			}

			NuFormAndDataDto insertDto = nuFormService.get(formDto, dataDto.getFormVersion());
			String result = insertDto != null ? jsonUtility.toJson(insertDto) : "{}";

			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/new/version", method = RequestMethod.POST)
	public ResponseEntity<String> newVersion(@RequestParam(value = "parameter", required = true) String parameter, HttpSession session) {
		try {

			NuFormDto formDto = jsonUtility.fromJson(parameter, NuFormDto.class);
			formDto.setProductId(PRODUCT_ID);
			formDto.setFormType(EFORM25_FORM_TYPE_NORMAL);

			NuFormDataDto dataDto = jsonUtility.fromJson(parameter, NuFormDataDto.class);
			dataDto.setProductId(PRODUCT_ID);

			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			formDto.setCreateUserId(loginUserId);
			formDto.setUpdateUserId(loginUserId);
			dataDto.setCreateUserId(loginUserId);
			dataDto.setUpdateUserId(loginUserId);

			int insertCount = nuFormService.newVersion(formDto, dataDto);
			if (insertCount != 1) {
				throw new Exception("데이터 insert 실패.");
			}

			NuFormAndDataDto resultDto = nuFormService.getRecent(formDto);
			String result = resultDto != null ? jsonUtility.toJson(resultDto) : "{}";

			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/new/versionmig", method = RequestMethod.POST)
	public ResponseEntity<String> newVersionMig(@RequestParam(value = "parameter", required = true) String parameter, HttpSession session) {
		try {

			NuFormDto formDto = jsonUtility.fromJson(parameter, NuFormDto.class);
			formDto.setProductId(PRODUCT_ID);
			formDto.setFormType(EFORM25_FORM_TYPE_NORMAL);

			NuFormDataDto dataDto = jsonUtility.fromJson(parameter, NuFormDataDto.class);
			dataDto.setProductId(PRODUCT_ID);

			int insertCount = nuFormService.newVersionMig(dataDto);
			if (insertCount != 1) {
				throw new Exception("데이터 insert 실패.");
			}

			NuFormAndDataDto resultDto = nuFormService.getRecent(formDto);
			String result = resultDto != null ? jsonUtility.toJson(resultDto) : "{}";

			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return null;
		}
	}

	@RequestMapping(value = "/update/publication", method = RequestMethod.POST)
	public ResponseEntity<String> updatePublication(@RequestParam(value = "parameter", required = true) String parameter, HttpSession session) {
		try {

			NuFormDto formDto = jsonUtility.fromJson(parameter, NuFormDto.class);
			NuFormDataDto dataDto = jsonUtility.fromJson(parameter, NuFormDataDto.class);
			formDto.setProductId(PRODUCT_ID);
			formDto.setFormType(EFORM25_FORM_TYPE_NORMAL);
			dataDto.setProductId(PRODUCT_ID);

			NuFormAndDataDto selectDto = nuFormService.get(formDto, dataDto.getFormVersion());
			if (!selectDto.getFormType().equals(EFORM25_FORM_TYPE_NORMAL)) {
				throw new Exception("FormType 오류.");
			}

			if (dataDto.getDocumentCode() == null || dataDto.getDocumentCode().equals("")) {
				dataDto.setDocumentCode(selectDto.getDocumentCode());
			}

			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			dataDto.setUpdateUserId(loginUserId);

			int updateCount = nuFormService.updatePublication(dataDto);
			if (updateCount != 1) {
				throw new Exception("데이터 update 실패.");
			}

			JsonObject resultJson = new JsonObject();
			resultJson.addProperty("formId", dataDto.getFormId());
			resultJson.addProperty("formVersion", dataDto.getFormVersion());
			resultJson.addProperty("publicationYN", dataDto.getPublicationYN());

			String result = jsonUtility.toJson(resultJson);

			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/getList/allVersion", method = RequestMethod.POST)
	public ResponseEntity<String> getList_allVersion(@RequestParam(value = "parameter", required = true) String parameter, HttpSession session) {
		try {

			NuFormDto formDto = jsonUtility.fromJson(parameter, NuFormDto.class);
			formDto.setProductId(PRODUCT_ID);
			formDto.setFormType(EFORM25_FORM_TYPE_NORMAL);

			List<NuFormAndDataDto> resultList = nuFormService.getList_allVersion(formDto);

			String result = resultList != null ? jsonUtility.toJson(resultList) : "{}";
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/ex/getList", method = RequestMethod.POST)
	public ResponseEntity<String> getClipFormAndConsentFormEx(@RequestParam(value = "parameter", required = true) String parameter,
			HttpSession session) {
		try {

			NuFormAndExDto paramDto = jsonUtility.fromJson(parameter, NuFormAndExDto.class);
			paramDto.setInstCd(companyCode);

			List<NuFormAndExDto> resultList = nuFormService.getClipFormAndConsentFormEx(paramDto);

			String result = resultList != null ? jsonUtility.toJson(resultList) : "{}";
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/ex/save", method = RequestMethod.POST)
	public ResponseEntity<String> saveConsentFormEx(@RequestParam(value = "parameter", required = true) String parameter, HttpSession session) {
		try {

			List<NuFormAndExDto> paramDtos = Arrays.asList(jsonUtility.fromJson(parameter, NuFormAndExDto[].class));

			boolean result = nuFormService.saveConsentFormEx(PRODUCT_ID, paramDtos);

			if (!result) {
				throw new Exception("서식 확장속성 저장에 실패하였습니다.");
			}

			JsonObject resultJson = new JsonObject();
			resultJson.addProperty("result", result);

			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(resultJson.toString(), headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

}
