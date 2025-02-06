package kr.co.clipsoft.biz.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.web.multipart.MultipartFile;

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

public interface NuConsentService {

	/**
	 * 현재시간 조회
	 * 
	 * @return
	 */
	public String getNowTime();

	/**
	 * 전자동의서 모바일 APP(APK) 정보 조회
	 * 
	 * @return NuAppDto
	 */
	public NuAppDto getApkVersion();

	/**
	 * 전자동의서 모바일 APP(APK) 정보 리스트 조회
	 * 
	 * @return NuAppDto
	 */
	public List<NuAppDto> getApkVersionList();

	/**
	 * 전자동의서 PC EXE APP 정보 리스트 조회
	 * 
	 * @return NuAppDto
	 */
	public List<NuAppDto> getPcExeVersionList();

	/**
	 * 단말기 마스터 정보 조회
	 * 
	 * @param paramDto 단말기 정보
	 * @return 단말기 마스터 정보
	 * 전자동의서 저장
	 *
	 */
	public NuConsentDeviceMstDto getDeviceMst(NuConsentDeviceMstDto paramDto);

	/**
	 * @param mstDto
	 * @param dataDto
	 * @param imageDto
	 * @param imageFiles
	 * @param certDto
	 * @param formExDto
	 * @param logDto
	 * @return
	 * @throws Exception
	 */
	public String saveConsent(NuConsentMstDto mstDto, NuConsentDataDto dataDto, NuConsentImageDto imageDto, List<MultipartFile> imageFiles,
			NuConsentImageSignDto certDto, NuConsentFormExDto formExDto, NuConsentDeviceLogsDto logDto ) throws Exception;


	/**
	 * 전자동의서 임시저장/저장 상태 정보 리스트 조회
	 * 
	 * @param paramDto
	 * @return
	 */
	public NuConsentMstDto getConsentMst(NuConsentMstDto paramDto);

	/**
	 * 전자동의서 임시저장/저장 상태 정보 리스트 조회
	 * 
	 * @param parameter NuConsentMstDto
	 * @return List<NuConsentMstDto>
	 */
	public List<NuConsentMstDto> getConsentMsts(NuConsentMstDto parameter);

	/**
	 * 전자동의서 임시저장/저장 상태 정보 리스트 조회(상세조건)
	 * 
	 * @return List<NuConsentMstDto>
	 */
	public List<NuConsentMstDetailDto> getConsentMstDetail(NuConsentMstDetailDto paramDto);

	/**
	 * 임시저장된 서식 사용여부 설정
	 * 
	 * @param parameter 전자동의서 마스터 정보
	 * @return
	 */
	public boolean updateConsentMstUseYn(NuConsentMstDto parameter);

	/**
	 * 전자동의서 임시저장 정보(dataXml/formXml) 조회
	 * 
	 * @param parameter NuConsentDataDto
	 * @return NuConsentDataDto
	 */
	public NuConsentDataDto getConsentXmlData(NuConsentDataDto parameter);

	/**
	 * 전자동의서 펜그리기 정보 저장
	 * 
	 * @param parameter NuConsentDrowDto
	 * @return
	 */
	public boolean saveConsentDrow(NuConsentDrowDto parameter);

	/**
	 * 
	 * @param parameter
	 * @return
	 * @throws Exception
	 */
	public String getConsentDrow(NuConsentDrowDto parameter) throws Exception;

	/**
	 * 전자동의서 저장된 이미지 파일 업로드(LC테크 인터페이스 테이블 연동)
	 * 
	 * @param consentMstRid
	 * 
	 * @param imageFiles     업로드 이미지 파일
	 * @param parameter
	 * @param beforImgDelete
	 * @param paramDtos      업로드 이미지 파일 정보
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public JSONObject uploadImages(Integer consentMstRid, List<MultipartFile> imageFiles, String parameter, String beforImgDelete)
			throws BusinessException, Exception;

	/**
	 * 전자동의서 녹취파일 정보 조회
	 * 
	 * @param paramDto
	 * @return
	 */
	public List<NuConsentRecordDto> getConsentRecord(NuConsentRecordDto paramDto);

	/**
	 * 전자동의서 녹취파일 업로드
	 * 
	 * @param records   업로드 녹취 파일
	 * @param paramDtos 업로드 녹취 파일 정보
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public boolean uploadRecords(List<MultipartFile> records, List<NuConsentRecordDto> paramDtos) throws IllegalStateException, IOException;

	/**
	 * 전자동의서 녹취파일 다운로드
	 * 
	 * @param request
	 * @param resonse
	 * @param paramDto
	 * @throws IOException
	 */
	public void downloadRecordFile(HttpServletRequest request, HttpServletResponse resonse, NuConsentRecordDto paramDto) throws IOException;

	/**
	 * 전자동의서 사용자별 지정 속성 저장
	 * 
	 * @param paramDto
	 */
	public NuConsentUserFormDto saveUserForm(NuConsentUserFormDto paramDto);

	/**
	 * 전자동의서 서식확장속성 조회
	 * 
	 * @param paramDto
	 * @return
	 */
	public NuConsentFormExDto getConsentFormEx(NuConsentFormExDto paramDto);

	/**
	 * 전자동의서 서식 호출 가능 여부 조회
	 * 
	 * @param paramDtos
	 * @return
	 * @throws ParseException
	 */
	public String getIsPossibleViewOpen(List<NuConsentMstDto> paramDtos, String companyCode);

	/**
	 * 전자동의서 13호서식 동적속성 전용 서비스
	 * 
	 * <pre>
	 * 13호서식에 작성한 연명서식 체크를 위한 환자기준 작성된 연명서식(9,13호 제외) 조회
	 * </pre>
	 * 
	 * @param paramDto
	 * @return
	 */
	public String getWritedLifelongKind(NuConsentMstDto paramDto);
	
	/**
	 * 주치의 리스트 조회 서비스
	 * 
	 * <pre>
	 * 수술 집도의 그룹박스 위한 서브밋
	 * </pre>
	 * 
	 * @param paramDto
	 * @return
	 */
	//public String getDoctorList(NuConsentMstDto paramDto);

	/**
	 * 전자동의서 인증저장 롤백 처리(OCX뷰어에서만 처리)
	 *
	 * @param mstDto
	 * @param dataDto
	 * @param certDto
	 * @param logDto
	 * @return
	 */
	public String rollbackSaveConsent(NuConsentMstDto mstDto, NuConsentDataDto dataDto, NuConsentImageSignDto certDto, NuConsentDeviceLogsDto logDto);

	/**
	 * OCR_TAG로 ConsentMstRid 조회
	 * 
	 * @param ocrTag
	 * @return
	 */
	public String getConsentMstRidByOcrTag(String ocrTag);

}
