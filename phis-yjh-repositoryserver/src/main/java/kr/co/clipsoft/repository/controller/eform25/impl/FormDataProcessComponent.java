package kr.co.clipsoft.repository.controller.eform25.impl;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import kr.co.clipsoft.biz.service.NuFormService;
import kr.co.clipsoft.repository.exception.LockedFormDataAccessException;
import kr.co.clipsoft.repository.login.ClipUserDetails;
import kr.co.clipsoft.repository.model.ClipFormAndDataDto;
import kr.co.clipsoft.repository.model.ClipFormCategoryDto;
import kr.co.clipsoft.repository.model.ClipFormDataDto;
import kr.co.clipsoft.repository.model.ClipFormDto;
import kr.co.clipsoft.repository.model.ClipManageIdDto;
import kr.co.clipsoft.repository.model.ClipUserDto;
import kr.co.clipsoft.repository.service.ClipFormService;
import kr.co.clipsoft.repository.service.ClipManageIdService;
import kr.co.clipsoft.repository.session.FormDataLockInfo;
import kr.co.clipsoft.repository.session.GlobalSession;
import kr.co.clipsoft.repository.session.SessionInfoUtility;

@Component
public class FormDataProcessComponent {

	/**
	 *  외부정의컨트롤 조회/등록시 기관별 분리 작업을 위하여 상수 선언 - 20200508
	 */
	private final String EFORM25_FORM_TYPE_EXTERNAL = "FORM_TYPE_002";
	

	/**
	 *  첨지 기관별 분리 작업을 위하여 상수 선언 - 2023-05
	 */
	private final String EFORM25_FORM_TYPE_PAGETEMPLATE  = "FORM_TYPE_003";
	
	@Autowired
	private ClipFormService clipFormService;
	
	/**
	 *  외부정의컨트롤 조회/등록시 기관별 분리 작업을 위하여 서비스 선언 - 20200508
	 */
	@Autowired
	private NuFormService nuFormService;
	
	@Autowired
	private ClipManageIdService clipManageIdService;
	
	@Autowired
	private GlobalSession globalSession;
	
	public String lockFormData(
		String parameter
		, HttpSession session
		, Long productId
		, String formType) 
	{
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		JsonObject result = new JsonObject();
		
		ClipUserDetails userDetails = SessionInfoUtility.getLoginUserDetails();
		if(userDetails == null) {
			result.toString(); 
		}
		
		JsonObject parameterJson = gson.fromJson(parameter, JsonObject.class);
		long parameterFormIdValue = parameterJson.get("formId").getAsLong();
		
		ClipFormDto formDto = new ClipFormDto();
		formDto.setProductId(productId);
		formDto.setFormId(parameterFormIdValue);
		formDto.setFormType(formType);
		
		ClipFormAndDataDto formAndDataDto = clipFormService.getRecent(formDto, false, false);
		
		ClipFormDataDto dataDto = new ClipFormDataDto();
		dataDto.setProductId(formAndDataDto.getProductId());
		dataDto.setFormId(formAndDataDto.getFormId());
		dataDto.setFormVersion(formAndDataDto.getFormVersion());
		
		FormDataLockInfo formDataLockInfo = new FormDataLockInfo(userDetails, dataDto, null);
		result.addProperty("formId", dataDto.getFormId());
		if(globalSession.isLockFormData(formDataLockInfo) == null) {
			globalSession.lockFormData(dataDto);
		}		
		return result.toString();
	}
	
	public String unlockFormData(
		String parameter
		, HttpSession session
		, Long productId
	) {
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		JsonObject result = new JsonObject();
		
		JsonObject parameterJson = gson.fromJson(parameter, JsonObject.class);
		long parameterFormIdValue = parameterJson.get("formId").getAsLong();
		
		ClipUserDetails userDetails = SessionInfoUtility.getLoginUserDetails();
		List<FormDataLockInfo> formDataLockInfoList = userDetails.getFormDataLockInfoList();
		for (FormDataLockInfo formDataLockInfo : formDataLockInfoList) {
			Long formId = formDataLockInfo.getLockFormData().getFormId();
			
			if(formId.longValue() == parameterFormIdValue) {
				globalSession.unlockFormData(formDataLockInfo);
				result.addProperty("formId", formId);
				break;
			}
		}
		
		return result.toString();
	}
	
	public String isLockFormData(
		String parameter
		, HttpSession session
		, Long productId) 
	{
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		JsonObject result = new JsonObject();
		
		JsonObject parameterJson = gson.fromJson(parameter, JsonObject.class);
		
		LockedFormDataAccessException exception = checkLockFormData(parameterJson.get("formId").getAsLong());
		
		if(exception != null) {
			FormDataLockInfo lockInfo = exception.getFormDataLockInfo();
			result.addProperty("formId", lockInfo.getLockFormData().getFormId());
			result.addProperty("userId", lockInfo.getUserDetails().getUserDto().getName());
			result.addProperty("name", lockInfo.getUserDetails().getUserDto().getUserId());
			return gson.toJson(result);
		} else {
			return "{}";
		}
	}
	
	public String getFormId(
		String parameter
		, HttpSession session
		, Long productId
		, String formType
	) {
		ClipManageIdDto dto = clipManageIdService.getFormId(productId);
		if(dto == null) {
			return "{}";
		}
		
		JsonObject result = new JsonObject();
		result.addProperty("formId", dto.getValue());
		
		return result.toString();
	}
	
	public String _new(
		String parameter
		, HttpSession session
		, Long productId
		, String formType
	) throws Exception {
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
					
		ClipFormDto formDto = gson.fromJson(parameter, ClipFormDto.class);
		formDto.setProductId(productId);
		formDto.setFormType(formType);
		
		LockedFormDataAccessException exception = checkLockFormData(formDto.getFormId());
		if(exception != null) {
			throw exception;
		}
		
		ClipFormDataDto dataDto = gson.fromJson(parameter, ClipFormDataDto.class);
		dataDto.setProductId(productId);
		
		String loginUserId = SessionInfoUtility.getLoginUserId(session);
		formDto.setCreateUserId(loginUserId);
		formDto.setUpdateUserId(loginUserId);
		dataDto.setCreateUserId(loginUserId);
		dataDto.setUpdateUserId(loginUserId);
		
		int insertCount = clipFormService.newFormData(formDto, dataDto);
		if(insertCount != 1) {
			throw new Exception("데이터 insert 실패.");
		}
		
		/**
		 *  외부정의컨트롤 등록시 기관별 분리 작업을 위하여 기관코드 매핑정보 등록 - 20200508
		 */
		if(formDto.getFormType().equals(EFORM25_FORM_TYPE_EXTERNAL) || formDto.getFormType().equals(EFORM25_FORM_TYPE_PAGETEMPLATE)) {
			nuFormService.saveConsentFormEx(formDto, dataDto.getFormVersion());
		}
		
		ClipFormAndDataDto insertDto = clipFormService.get(formDto, dataDto.getFormVersion(), true);
		
		if(insertDto != null) {
			globalSession.lockFormData(dataDto);
		}
		
		return insertDto != null ? gson.toJson(insertDto) : "{}";
	}
	
	public String newVersion(
		String parameter
		, HttpSession session
		, Long productId
		, String formType
	) throws Exception {
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
					
		ClipFormDto formDto = gson.fromJson(parameter, ClipFormDto.class);
		formDto.setProductId(productId);
		formDto.setFormType(formType);
		
		LockedFormDataAccessException exception = checkLockFormData(formDto.getFormId());
		if(exception != null) {
			throw exception;
		}

		ClipFormDataDto dataDto = gson.fromJson(parameter, ClipFormDataDto.class);
		dataDto.setProductId(productId);
		
		String loginUserId = SessionInfoUtility.getLoginUserId(session);
		formDto.setCreateUserId(loginUserId);
		formDto.setUpdateUserId(loginUserId);
		dataDto.setCreateUserId(loginUserId);
		dataDto.setUpdateUserId(loginUserId);
		
		int insertCount = clipFormService.newVersion(formDto, dataDto);
		if(insertCount != 1) {
			throw new Exception("데이터 insert 실패.");
		}
		
		/**
		 *  외부정의컨트롤 등록시 기관별 분리 작업을 위하여 기관코드 매핑정보 등록 - 20200508
		 */
		if(formDto.getFormType().equals(EFORM25_FORM_TYPE_EXTERNAL) || formDto.getFormType().equals(EFORM25_FORM_TYPE_PAGETEMPLATE)) {
			nuFormService.updateClipForm(formDto);
			nuFormService.saveConsentFormEx(formDto, dataDto.getFormVersion());
		}
		
		ClipFormAndDataDto resultDto = clipFormService.getRecent(formDto, true, true);
		
		if(resultDto != null) {
			globalSession.lockFormData(dataDto);
			return gson.toJson(resultDto);
		} else {
			return null;
		}
	}
	
	public String updateFormData(
		String parameter
		, HttpSession session
		, Long productId
		, String formType
	) throws Exception {
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
					
		ClipFormDto formDto = gson.fromJson(parameter, ClipFormDto.class);
		formDto.setProductId(productId);
		formDto.setFormType(formType);
		
		LockedFormDataAccessException exception = checkLockFormData(formDto.getFormId());
		if(exception != null) {
			throw exception;
		}
		
		ClipFormDataDto dataDto = gson.fromJson(parameter, ClipFormDataDto.class);
		dataDto.setProductId(productId);
		
		ClipFormAndDataDto isValidFormDataDto = clipFormService.get(formDto, dataDto.getFormVersion(), true);
		if(isValidFormDataDto == null) {
			throw new Exception("파라메터 오류");
		}
		
		
		String loginUserId = SessionInfoUtility.getLoginUserId(session);
		formDto.setUpdateUserId(loginUserId);
		dataDto.setUpdateUserId(loginUserId);
		
		int updateCount = clipFormService.updateFormData(dataDto);
		if(updateCount != 1) {
			throw new Exception("데이터 update 실패.");
		}
		
		ClipFormAndDataDto resultDto = clipFormService.get(formDto, dataDto.getFormVersion(), true);
		
		if(resultDto != null) {
			globalSession.lockFormData(dataDto);
		}
		
		return resultDto != null ? gson.toJson(resultDto) : "{}";
	}
	
	public String updatePublication(
		String parameter
		, HttpSession session
		, Long productId
		, String formType
	) throws Exception {
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
					
		ClipFormDto formDto = gson.fromJson(parameter, ClipFormDto.class);
		ClipFormDataDto dataDto = gson.fromJson(parameter, ClipFormDataDto.class);
		formDto.setProductId(productId);
		formDto.setFormType(formType);
		dataDto.setProductId(productId);
		
		LockedFormDataAccessException exception = checkLockFormData(formDto.getFormId());
		if(exception != null) {
			throw exception;
		}
		
		ClipFormAndDataDto selectDto = clipFormService.get(formDto, dataDto.getFormVersion(), true);
		if(!selectDto.getFormType().equals(formType)) {
			throw new Exception("FormType 오류.");
		}
		
		String loginUserId = SessionInfoUtility.getLoginUserId(session);
		dataDto.setUpdateUserId(loginUserId);
		
		int updateCount = clipFormService.updatePublication(dataDto);
		if(updateCount != 1) {
			throw new Exception("데이터 update 실패.");
		}
		
		JsonObject resultJson = new JsonObject();
		resultJson.addProperty("formId", dataDto.getFormId());
		resultJson.addProperty("formVersion", dataDto.getFormVersion());
		resultJson.addProperty("publicationYN", dataDto.getPublicationYN());
		
		return gson.toJson(resultJson);
	}
	
	public String updateUse(
		String parameter
		, HttpSession session
		, Long productId
		, String formType	
	) throws Exception {
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
					
		ClipFormDto formDto = gson.fromJson(parameter, ClipFormDto.class);
		formDto.setProductId(productId);
		formDto.setFormType(formType);
		
		String loginUserId = SessionInfoUtility.getLoginUserId(session);
		formDto.setUpdateUserId(loginUserId);
		
		int updateCount = clipFormService.updateUse(formDto);
		if(updateCount != 1) {
			throw new Exception("데이터 update 실패.");
		}
		
		JsonObject resultJson = new JsonObject();
		resultJson.addProperty("formId", formDto.getFormId());
		resultJson.addProperty("useYN", formDto.getUseYN());
		
		return gson.toJson(resultJson);
	}
	
	public String updateName(
			String parameter
			, HttpSession session
			, Long productId
			, String formType	
		) throws Exception {
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
						
			ClipFormDto formDto = gson.fromJson(parameter, ClipFormDto.class);
			formDto.setProductId(productId);
			formDto.setFormType(formType);
			
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			formDto.setUpdateUserId(loginUserId);
			
			int updateCount = clipFormService.updateName(formDto);
			if(updateCount != 1) {
				throw new Exception("데이터 update 실패.");
			}
			
			JsonObject resultJson = new JsonObject();
			resultJson.addProperty("formId", formDto.getFormId());
			resultJson.addProperty("formName", formDto.getFormName());
			
			return gson.toJson(resultJson);
		}
	
	public String updateCategory(
			String parameter
			, HttpSession session
			, Long productId
			, String formType	
		) throws Exception {
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
						
			ClipFormDto formDto = gson.fromJson(parameter, ClipFormDto.class);
			formDto.setProductId(productId);
			formDto.setFormType(formType);
			
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			formDto.setUpdateUserId(loginUserId);
			
			int updateCount = clipFormService.updateCategory(formDto);
			if(updateCount != 1) {
				throw new Exception("데이터 update 실패.");
			}
			
			JsonObject resultJson = new JsonObject();
			resultJson.addProperty("formId", formDto.getFormId());
			resultJson.addProperty("formCategoryId", formDto.getFormCategoryId());
			
			return gson.toJson(resultJson);
		}
	
	public String get(
		String parameter
		, HttpSession session
		, Long productId
		, String formType
		, Boolean eForm_setPublicationYN
	) throws Exception {
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		
		ClipFormDto formDto = gson.fromJson(parameter, ClipFormDto.class);
		formDto.setProductId(productId);
		formDto.setFormType(formType);
		
		ClipFormDataDto dataDto = gson.fromJson(parameter, ClipFormDataDto.class);
		
		ClipFormAndDataDto resultDto = clipFormService.get(formDto, dataDto.getFormVersion(), eForm_setPublicationYN);
		
		if(resultDto != null) {
			ClipUserDetails userDetails = SessionInfoUtility.getLoginUserDetails();
			
			JsonObject parameterJson = gson.fromJson(parameter, JsonObject.class);
			boolean isLock = parameterJson.get("lock") != null ? parameterJson.get("lock").getAsBoolean() : false;
			
			FormDataLockInfo formDataLockInfo = new FormDataLockInfo(userDetails, dataDto, null);
			if(userDetails != null && isLock && globalSession.isLockFormData(formDataLockInfo) == null) {
				globalSession.lockFormData(dataDto);
			}
		}
		
		String resultJsonString = resultDto != null ? gson.toJson(resultDto) : "{}";
		return resultJsonString;
	}
	
	public String getRecent(
		String parameter
		, HttpSession session
		, Long productId
		, String formType
		, Boolean eForm_setPublicationYN
	) {
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		
		JsonObject paramJson = gson.fromJson(parameter, JsonObject.class);
		boolean includeFormData = paramJson.get("includeFormData") != null ? paramJson.get("includeFormData").getAsBoolean() : true;
		
		ClipFormDto formDto = new ClipFormDto();
		formDto.setProductId(productId);
		formDto.setFormType(formType);
		formDto.setFormId(paramJson.get("formId").getAsLong());
		
		ClipFormAndDataDto resultDto = clipFormService.getRecent(formDto, includeFormData, eForm_setPublicationYN);
		
		String resultJsonString = resultDto != null ? gson.toJson(resultDto) : "{}";
		return resultJsonString;
	}
	
	public String getList_allVersion(
		String parameter
		, HttpSession session
		, Long productId
		, String formType
		, Boolean eForm_setPublicationYN
	) {
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		
		ClipFormDto formDto = gson.fromJson(parameter, ClipFormDto.class);
		formDto.setProductId(productId);
		formDto.setFormType(formType);
		
		List<ClipFormAndDataDto> resultList = clipFormService.getList_allVersion(formDto, eForm_setPublicationYN);
		
		String resultJsonString = resultList != null ? gson.toJson(resultList) : "{}";
		return resultJsonString;
	}
	
	public String getList_all(
		String parameter
		, HttpSession session
		, Long productId
		, String formType
		, Boolean eForm_setPublicationYN
	) {
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		
		ClipFormDto parameterDto = new ClipFormDto(); 
		parameterDto.setProductId(productId);
		parameterDto.setFormType(formType);
		
		/**
		 *  외부정의컨트롤 조회시 기관별 분리 작업을 위하여 별도 서비스 호출 - 20200508
		 */
		List<HashMap> resultList = null;
		if(parameterDto.getFormType().equals(EFORM25_FORM_TYPE_EXTERNAL) || parameterDto.getFormType().equals(EFORM25_FORM_TYPE_PAGETEMPLATE)) {
			resultList = nuFormService.getList_all(parameterDto, eForm_setPublicationYN);
		}else {
			resultList = clipFormService.getList_all(parameterDto, eForm_setPublicationYN);
		}
		
		String resultJsonString = resultList != null ? gson.toJson(resultList) : "{}";
		return resultJsonString;
	}
	
	public String getList_allRegist(
			String parameter
			, HttpSession session
			, Long productId
			, String formType
			, Boolean eForm_setPublicationYN
		) {
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			ClipFormDto formDto = gson.fromJson(parameter, ClipFormDto.class);
			formDto.setProductId(productId);
			formDto.setFormType(formType);
			
			List<ClipFormDto> resultList = clipFormService.getList_allRegist(formDto, eForm_setPublicationYN);
			
			String resultJsonString = resultList != null ? gson.toJson(resultList) : "{}";
			return resultJsonString;
		}
	
	public String getList_allCategoryForm(
			String parameter
			, HttpSession session
			, Long productId
			, Boolean eForm_setPublicationYN
		) {
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
			ClipFormCategoryDto parameterDto = gson.fromJson(parameter, ClipFormCategoryDto.class);
			parameterDto.setProductId(productId);
			
			List<ClipFormDto> resultList = clipFormService.getList_allCategoryForm(parameterDto, eForm_setPublicationYN);
			
			String resultJsonString = resultList != null ? gson.toJson(resultList) : "{}";
			return resultJsonString;
		}
	
	private LockedFormDataAccessException checkLockFormData(Long formId) {
		ClipUserDetails loginUserDetails = SessionInfoUtility.getLoginUserDetails();
		
		ClipFormDataDto formDataDto = new ClipFormDataDto();
		formDataDto.setFormId(formId);
		
		FormDataLockInfo parameterLockInfo = new FormDataLockInfo(loginUserDetails, formDataDto, null);
		FormDataLockInfo lockInfo = globalSession.isLockFormData(parameterLockInfo);
		
		if( lockInfo != null ) {
			ClipUserDto userDto = parameterLockInfo.getUserDetails().getUserDto();
			String message = String.format("userId : %s, name : %s, formId : %d"
					, userDto.getUserId()
					, userDto.getName()
					, formId);
			return new LockedFormDataAccessException(message, lockInfo);
		} else {
			return null;
		}
	}
}
