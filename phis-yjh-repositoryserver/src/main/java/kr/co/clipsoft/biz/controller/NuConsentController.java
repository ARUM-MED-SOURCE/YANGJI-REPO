package kr.co.clipsoft.biz.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
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
import org.springframework.web.multipart.MultipartFile;

import kr.co.clipsoft.biz.exception.BizResultInfo;
import kr.co.clipsoft.biz.exception.BusinessException;
import kr.co.clipsoft.biz.model.consent.NuAppDto;
import kr.co.clipsoft.biz.model.consent.NuConsentDataDto;
import kr.co.clipsoft.biz.model.consent.NuConsentDeviceLogsDto;
import kr.co.clipsoft.biz.model.consent.NuConsentDeviceMstDto;
import kr.co.clipsoft.biz.model.consent.NuConsentDrowDto;
import kr.co.clipsoft.biz.model.consent.NuConsentFormExDto;
import kr.co.clipsoft.biz.model.consent.NuConsentImageDto;
import kr.co.clipsoft.biz.model.consent.NuConsentImageSignDto;
import kr.co.clipsoft.biz.model.consent.NuConsentMstDetailDto;
import kr.co.clipsoft.biz.model.consent.NuConsentMstDto;
import kr.co.clipsoft.biz.model.consent.NuConsentRecordDto;
import kr.co.clipsoft.biz.model.consent.NuConsentUserFormDto;
import kr.co.clipsoft.biz.service.NuConsentService;
import kr.co.clipsoft.biz.utility.JSONUtility;
import kr.co.clipsoft.biz.utility.WebUtility;
import kr.co.clipsoft.repository.session.UserAgentValidator;
import kr.co.clipsoft.repository.web.ClipHttpHeadersFactory;
import kr.co.clipsoft.repository.web.ClipResponseEntity;
import kr.co.clipsoft.repository.web.ClipResponseEntityFactory;

/**
 * 전자동의서(PC/모바일) 저장 상태 관리 컨트롤러
 *
 *
 */
@Controller
@RequestMapping(value = "/biz/nu/member/viewer/eForm25/consent", produces = "application/json; charset=UTF-8;")
public class NuConsentController {

	@Value("#{projectProperties['server.characterEncoding']}")
	private String characterEncoding;
	
	/**
	 * 서버 버전 정보(Custom)
	 */
	@Value("#{customerProperties['server.version']}")
	private String serverVersion;

	@Value("#{customerProperties['server.request.host']}")
	private String requestHost;

	/**
	 * 기관(병원) 코드
	 */
	@Value("#{customerProperties['server.companyCode']}")
	private String companyCode;

	private static final Logger logger = LoggerFactory.getLogger(NuConsentController.class);

	@Autowired
	private JSONUtility jsonUtility;

	@Autowired
	private UserAgentValidator userAgentValidator;

	@Autowired
	private ClipResponseEntityFactory clipResponseEntityFactory;

	@Autowired
	private ClipHttpHeadersFactory clipHttpHeadersFactory;

	@Autowired
	private NuConsentService nuConsentService;

	@RequestMapping(value = "/server/version/get", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<String> getServerVersion(HttpServletRequest request, HttpSession session) {
		try {
			userAgentValidator.validate(request);

			JSONObject result = new JSONObject();
			result.put("serverVersion", serverVersion);

			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result.toJSONString(), headers, HttpStatus.OK);

		} catch (Exception e) {
			logger.error(e.toString());
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/nowtime/get", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<String> getNowTime(@RequestParam(value = "parameter", required = false) String parameter, HttpServletRequest request,
			HttpSession session) {
		try {
			userAgentValidator.validate(request);

			String nowTime = nuConsentService.getNowTime();
			JSONObject result = new JSONObject();
			result.put("nowTime", nowTime);

			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(result.toJSONString(), headers, HttpStatus.OK);

		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	/**
	 * 모바일 APP(APK) 정보 조회
	 * 
	 * @param parameter
	 * @param request
	 * @param session
	 * @return 모바일 APP 정보
	 */
	@RequestMapping(value = "/apk/version/get", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<String> getAppInfo(@RequestParam(value = "parameter", required = false) String parameter, HttpServletRequest request,
			HttpSession session) {

		HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

		try {

			NuAppDto mobileDto = nuConsentService.getApkVersion();

			return clipResponseEntityFactory.create(jsonUtility.toJson(mobileDto), headers, HttpStatus.OK);

		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	/**
	 * 모바일 APP(APK) 정보 조회
	 * 
	 * @param parameter
	 * @param request
	 * @param session
	 * @return 모바일 APP 정보
	 */
	@RequestMapping(value = "/apk/version/list/get", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<String> getMobileAppInfoList(@RequestParam(value = "parameter", required = false) String parameter,
			HttpServletRequest request, HttpSession session) {

		HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

		try {
			userAgentValidator.validate(request);

			List<NuAppDto> resultList = nuConsentService.getApkVersionList();
			String result = resultList != null ? jsonUtility.toJson(resultList) : "[]";

			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);

		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	/**
	 * 전자동의서 PC EXE APP 정보 조회
	 * 
	 * @param parameter
	 * @param request
	 * @param session
	 * @return 전자동의서 PC EXE APP 정보
	 */
	@RequestMapping(value = "/exe/version/list/get", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<String> getPcExeVersionList(@RequestParam(value = "parameter", required = false) String parameter,
			HttpServletRequest request, HttpSession session) {

		HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

		try {
			userAgentValidator.validate(request);

			List<NuAppDto> resultList = nuConsentService.getPcExeVersionList();
			String result = resultList != null ? jsonUtility.toJson(resultList) : "[]";

			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);

		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/device/mst/get", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<String> getDeviceMst(@RequestParam(value = "parameter", required = true) String parameter, HttpServletRequest request,
			HttpSession session) {

		HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

		try {
			userAgentValidator.validate(request);

			NuConsentDeviceMstDto paramDto = jsonUtility.fromJson(parameter, NuConsentDeviceMstDto.class);

			NuConsentDeviceMstDto resultDto = nuConsentService.getDeviceMst(paramDto);

			return clipResponseEntityFactory.create(jsonUtility.toJson(resultDto), headers, HttpStatus.OK);

		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	/**
	 * 전자동의서 저장(임시저장)(통합)
	 * 
	 * @param parameter
	 * @param request
	 * @param session
	 * @return consentMstRid
	 */
	@RequestMapping(value = "/view/save", method = RequestMethod.POST)
	public ResponseEntity<String> saveConsent(@RequestParam(value = "parameter", required = true) String parameter,
			@RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles, HttpServletRequest request, HttpSession session) {

		HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

		try {
			userAgentValidator.validate(request);

			NuConsentMstDto mstDto = jsonUtility.fromJson(parameter, NuConsentMstDto.class);
			NuConsentDataDto dataDto = jsonUtility.fromJson(parameter, NuConsentDataDto.class);
			NuConsentImageDto imageDto = jsonUtility.fromJson(parameter, NuConsentImageDto.class);
			NuConsentImageSignDto certDto = jsonUtility.fromJson(parameter, NuConsentImageSignDto.class);
			NuConsentFormExDto formExDto = jsonUtility.fromJson(parameter, NuConsentFormExDto.class);
			NuConsentDeviceLogsDto logDto = jsonUtility.fromJson(parameter, NuConsentDeviceLogsDto.class);


			mstDto.setInstCd(companyCode);
			mstDto.setHosType(companyCode);
			formExDto.setInstCd(companyCode);

			long start = System.currentTimeMillis();
			String result = nuConsentService.saveConsent(mstDto, dataDto, imageDto, imageFiles, certDto, formExDto, logDto);
			long end = System.currentTimeMillis();
			logger.debug("/view/save-timeCheck : " + (end - start) / 1000.0);

			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);

		} catch (BusinessException e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.create(new BizResultInfo(false, e).toResultOfJSONString(), headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}

	}

	/**
	 * 전자동의서 인증저장 롤백 처리(OCX뷰어에서만 처리) - 공인 인증서 취소할경우 롤백
	 * 
	 * @param parameter
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/view/save/rollback", method = RequestMethod.POST)
	public ResponseEntity<String> rollbackSaveConsent(@RequestParam(value = "parameter", required = true) String parameter,
			HttpServletRequest request, HttpSession session) {

		HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

		try {
			userAgentValidator.validate(request);

			NuConsentMstDto mstDto = jsonUtility.fromJson(parameter, NuConsentMstDto.class);
			NuConsentDataDto dataDto = jsonUtility.fromJson(parameter, NuConsentDataDto.class);
			NuConsentImageSignDto certDto = jsonUtility.fromJson(parameter, NuConsentImageSignDto.class);
			NuConsentDeviceLogsDto logDto = jsonUtility.fromJson(parameter, NuConsentDeviceLogsDto.class);

			mstDto.setInstCd(companyCode);
			mstDto.setHosType(companyCode);

			String result = nuConsentService.rollbackSaveConsent(mstDto, dataDto, certDto, logDto);

			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);

		} catch (BusinessException e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.create(new BizResultInfo(false, e).toResultOfJSONString(), headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}

	}

	/**
	 * 전자동의서 임시저장/저장 상태 정보 조회
	 * 
	 * @param parameter
	 * @param request
	 * @param session
	 * @return 전자동의서 임시저장/저장 상태 정보 리스트
	 */
	@RequestMapping(value = "/mst/get/one", method = RequestMethod.POST)
	public ResponseEntity<String> getConsentMstOne(@RequestParam(value = "parameter", required = true) String parameter, HttpServletRequest request,
			HttpSession session) {

		HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

		try {
			userAgentValidator.validate(request);

			NuConsentMstDto paramDto = jsonUtility.fromJson(parameter, NuConsentMstDto.class);

			NuConsentMstDto result = nuConsentService.getConsentMst(paramDto);

			return clipResponseEntityFactory.create(jsonUtility.toJson(result), headers, HttpStatus.OK);

		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	/**
	 * 전자동의서 임시저장/저장 상태 정보 리스트 조회
	 * 
	 * @param parameter
	 * @param request
	 * @param session
	 * @return 전자동의서 임시저장/저장 상태 정보 리스트
	 */
	@RequestMapping(value = "/mst/get", method = RequestMethod.POST)
	public ResponseEntity<String> getConsentMst(@RequestParam(value = "parameter", required = true) String parameter, HttpServletRequest request,
			HttpSession session) {

		HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

		try {
			userAgentValidator.validate(request);

			NuConsentMstDto paramDto = jsonUtility.fromJson(parameter, NuConsentMstDto.class);
			paramDto.setInstCd(companyCode);

			List<NuConsentMstDto> resultList = nuConsentService.getConsentMsts(paramDto);
			String result = resultList != null ? jsonUtility.toJson(resultList) : "[]";

			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);

		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	/**
	 * 전자동의서 임시저장/저장 상태 정보 상세조건 리스트 조회
	 * 
	 * @param parameter
	 * @param request
	 * @param session
	 * @return 전자동의서 임시저장/저장 상태 정보 리스트
	 */
	@RequestMapping(value = "/mst/detail/get", method = RequestMethod.POST)
	public ResponseEntity<String> getConsentMstDetail(@RequestParam(value = "parameter", required = true) String parameter,
			HttpServletRequest request, HttpSession session) {

		HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

		try {
			userAgentValidator.validate(request);

			NuConsentMstDetailDto paramDto = jsonUtility.fromJson(parameter, NuConsentMstDetailDto.class);
			paramDto.setInstCd(companyCode);
			paramDto.setHosType(companyCode);

			List<NuConsentMstDetailDto> resultList = nuConsentService.getConsentMstDetail(paramDto);
			String result = resultList != null ? jsonUtility.toJson(resultList) : "[]";

			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);

		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/mst/useyn/update", method = RequestMethod.POST)
	public ResponseEntity<String> updateConsentMstUseYn(@RequestParam(value = "parameter", required = true) String parameter,
			HttpServletRequest request, HttpSession session) {

		HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

		try {
			userAgentValidator.validate(request);

			NuConsentMstDto paramDto = jsonUtility.fromJson(parameter, NuConsentMstDto.class);

			JSONObject result = new JSONObject();
			result.put("result", nuConsentService.updateConsentMstUseYn(paramDto));

			return clipResponseEntityFactory.create(result.toJSONString(), headers, HttpStatus.OK);

		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	/**
	 * 전자동의서 임시저장정보 불러오기
	 * 
	 * @param parameter
	 * @param request
	 * @param session
	 * @return 임시저장 정보(ept)
	 */
	@RequestMapping(value = "/data/formxml/get", method = RequestMethod.POST)
	public ResponseEntity<String> getConsentFormXmlData(@RequestParam(value = "parameter", required = false) String parameter,
			@RequestParam(value = "consentMstRid", required = false) Integer consentMstRid,
			@RequestParam(value = "ocrTag", required = false) String ocrTag, HttpServletRequest request, HttpSession session) {

		HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

		try {
			userAgentValidator.validate(request);

			NuConsentDataDto paramDto = new NuConsentDataDto();
			if (consentMstRid != null && consentMstRid > 0) {
				paramDto.setConsentMstRid(consentMstRid.longValue());
			} else if (ocrTag != null && !ocrTag.isEmpty()) {
				String strConsentMstRid = nuConsentService.getConsentMstRidByOcrTag(ocrTag);
				paramDto.setConsentMstRid(Long.valueOf(strConsentMstRid));
			} else {
				paramDto = jsonUtility.fromJson(parameter, NuConsentDataDto.class);
			}

			NuConsentDataDto resultDto = nuConsentService.getConsentXmlData(paramDto);

			headers.add("Content-Type", "application/xml; charset=" + characterEncoding + ";");

			return new ClipResponseEntity(resultDto.getFormXml(), headers, HttpStatus.OK);

		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}

	}

	/**
	 * 전자동의서 펜그리기 정보 저장
	 * 
	 * @param parameter
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/drow/save", method = RequestMethod.POST)
	public ResponseEntity<String> saveConsentDrow(@RequestParam(value = "parameter", required = true) String parameter, HttpServletRequest request,
			HttpSession session) {

		HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

		try {
			userAgentValidator.validate(request);

			NuConsentDrowDto paramDto = jsonUtility.fromJson(parameter, NuConsentDrowDto.class);

			JSONObject result = new JSONObject();
			result.put("result", nuConsentService.saveConsentDrow(paramDto));

			return clipResponseEntityFactory.create(result.toJSONString(), headers, HttpStatus.OK);

		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	/**
	 * 전자동의서 펜그리기 정보 불러오기
	 * 
	 * @param parameter
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/drow/get", method = RequestMethod.POST)
	public ResponseEntity<String> getConsentDrow(@RequestParam(value = "parameter", required = true) String parameter, HttpServletRequest request,
			HttpSession session) {

		HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

		try {
			userAgentValidator.validate(request);

			NuConsentDrowDto paramDto = jsonUtility.fromJson(parameter, NuConsentDrowDto.class);

			String resultData = nuConsentService.getConsentDrow(paramDto);

			headers.add("Content-Type", "application/xml; charset=" + characterEncoding + ";");

			return new ClipResponseEntity(resultData, headers, HttpStatus.OK);

		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}

	}

	/**
	 * 모바일 전자동의서 이미지 업로드처리(LC테크 인터페이스 테이블 연동)
	 * 
	 * @param images
	 * @param parameter
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/image/upload", method = RequestMethod.POST)
	public ResponseEntity<String> uploadConsentSaveImage(@RequestParam(value = "images", required = true) List<MultipartFile> images,
			@RequestParam(value = "consentMstRid", required = false) Integer consentMstRid,
			@RequestParam(value = "parameter", required = true) String parameter,
			@RequestParam(value = "beforImgDelete", required = false) String beforImgDelete, HttpServletRequest request, HttpSession session) {

		HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

		try {
			userAgentValidator.validate(request);

			JSONObject result = nuConsentService.uploadImages(consentMstRid, images, parameter, beforImgDelete);

			return clipResponseEntityFactory.create(result.toJSONString(), headers, HttpStatus.OK);

		} catch (BusinessException e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.create(new BizResultInfo(false, e).toResultOfJSONString(), headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	/**
	 * 동의서 MST RID에 해당되는 녹취 파일정보 조회
	 * 
	 * @param parameter MST RID
	 * @param request
	 * @param session
	 * @return 녹취파일 정보
	 */
	@RequestMapping(value = "/record/get", method = RequestMethod.POST)
	public ResponseEntity<String> getConsentRecord(@RequestParam(value = "parameter", required = true) String parameter, HttpServletRequest request,
			HttpSession session) {

		HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

		try {
			userAgentValidator.validate(request);

			NuConsentRecordDto paramDto = jsonUtility.fromJson(parameter, NuConsentRecordDto.class);

			List<NuConsentRecordDto> resultList = nuConsentService.getConsentRecord(paramDto);
			String result = resultList != null ? jsonUtility.toJson(resultList) : "[]";

			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);

		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	/**
	 * 녹취파일 업로드
	 * 
	 * @param records   녹취파일 리스트
	 * @param parameter 녹취파일 정보
	 * @param request
	 * @param session
	 * @return 업로드 결과(true/false)
	 */
	@RequestMapping(value = "/record/upload", method = RequestMethod.POST)
	public ResponseEntity<String> uploadConsentSaveRecord(@RequestParam(value = "records", required = true) List<MultipartFile> records,
			@RequestParam(value = "parameter", required = true) String parameter, HttpServletRequest request, HttpSession session) {

		HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

		try {
			userAgentValidator.validate(request);

			List<NuConsentRecordDto> paramDtos = Arrays.asList(jsonUtility.fromJson(parameter, NuConsentRecordDto[].class));

			boolean uploadResult = nuConsentService.uploadRecords(records, paramDtos);

			if (!uploadResult) {
				throw new Exception("녹취파일 업로드에 실패하였습니다.");
			}

			JSONObject result = new JSONObject();
			result.put("result", uploadResult);

			return clipResponseEntityFactory.create(result.toJSONString(), headers, HttpStatus.OK);

		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	/**
	 * 녹취파일 다운로드
	 * 
	 * @param parameter 녹취파일 정보
	 * @param request
	 * @param resonse
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/record/download", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> downloadConsentRecord(@RequestParam(value = "parameter", required = true) String parameter,
			HttpServletRequest request, HttpServletResponse resonse, HttpSession session) {

		try {
			userAgentValidator.validate(request);

			NuConsentRecordDto paramDto = jsonUtility.fromJson(parameter, NuConsentRecordDto.class);

			nuConsentService.downloadRecordFile(request, resonse, paramDto);

			return new ResponseEntity<String>(HttpStatus.OK);

		} catch (Exception e) {
			logger.error("녹취파일 다운로드에 실패하였습니다. : " + e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();

		}
	}

	/**
	 * 전자동의서 사용자 지정속성 저장
	 * 
	 * @param parameter
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/user/form/save", method = RequestMethod.POST)
	public ResponseEntity<String> saveUserForm(@RequestParam(value = "parameter", required = true) String parameter, HttpServletRequest request,
			HttpSession session) {

		HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

		try {
			userAgentValidator.validate(request);

			NuConsentUserFormDto paramDto = jsonUtility.fromJson(parameter, NuConsentUserFormDto.class);

			NuConsentUserFormDto resultDto = nuConsentService.saveUserForm(paramDto);
			String result = resultDto != null ? jsonUtility.toJson(resultDto) : "{}";

			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);

		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/formex/get", method = RequestMethod.POST)
	public ResponseEntity<String> getConsentFormEx(@RequestParam(value = "parameter", required = true) String parameter, HttpSession session) {

		HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

		try {
			NuConsentFormExDto paramDto = jsonUtility.fromJson(parameter, NuConsentFormExDto.class);
			paramDto.setInstCd(companyCode);

			NuConsentFormExDto resultDto = nuConsentService.getConsentFormEx(paramDto);

			return clipResponseEntityFactory.create(jsonUtility.toJson(resultDto), headers, HttpStatus.OK);

		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/isOpen", method = RequestMethod.POST)
	public ResponseEntity<String> getConsentIsWrite(@RequestParam(value = "parameter", required = true) String parameter, HttpSession session) {

		HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

		try {

			List<NuConsentMstDto> paramDtos = Arrays.asList(jsonUtility.fromJson(parameter, NuConsentMstDto[].class));

			String result = nuConsentService.getIsPossibleViewOpen(paramDtos, companyCode);

			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);

		} catch (BusinessException e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.create(new BizResultInfo(false, e).toResultOfJSONString(), headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(parameter);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

	@RequestMapping(value = "/form/daynamic/writed/lifelong/kind/get", method = RequestMethod.POST)
	public ResponseEntity<String> getWritedLifelongKind(@RequestParam(value = "patientCode", required = true) String patientCode,
			HttpSession session) {

		HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

		try {

			NuConsentMstDto paramDto = new NuConsentMstDto();
			paramDto.setPatientCode(patientCode);

			String result = nuConsentService.getWritedLifelongKind(paramDto);

			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);

		} catch (BusinessException e) {
			logger.error(e.toString());
			logger.error(patientCode);
			return clipResponseEntityFactory.create(new BizResultInfo(false, e).toResultOfJSONString(), headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(patientCode);
			return clipResponseEntityFactory.createInternalServerError();
		}
	}
	
	@RequestMapping(value = "/form/daynamic/doctorlist", method = RequestMethod.POST)
	public ResponseEntity<String> getDoctorList(@RequestParam(value = "deptCd", required = true) String deptCd,   
			HttpSession session) {

		HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);

		try {			
			WebUtility webUtil = new WebUtility();			
			
 
			String result =webUtil.sendPost(requestHost+"/cmcnu/.live?submit_id=DRMRF00114&business_id=mr&deptcd="+deptCd+"&instcd="+companyCode+"&drflag=M", ""); 
			
			return clipResponseEntityFactory.create(result, headers, HttpStatus.OK);
		} catch (BusinessException e) {
			logger.error(e.toString());
			logger.error("");
			return clipResponseEntityFactory.create(new BizResultInfo(false, e).toResultOfJSONString(), headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error("");
			return clipResponseEntityFactory.createInternalServerError();
		}
	}

}
