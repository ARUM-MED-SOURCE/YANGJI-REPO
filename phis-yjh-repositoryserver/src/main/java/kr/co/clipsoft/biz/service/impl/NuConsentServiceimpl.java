package kr.co.clipsoft.biz.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.co.clipsoft.biz.utility.biz.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.json.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonParseException;

import kr.co.clipsoft.biz.dao.NuConsentDao;
import kr.co.clipsoft.biz.exception.BizErrorInfo;
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
import kr.co.clipsoft.biz.model.consent.NuTblLinkConsentInfoDto;
import kr.co.clipsoft.biz.service.NuConsentService;
import kr.co.clipsoft.biz.utility.PenDrawUtility;
import kr.co.clipsoft.biz.utility.img.ImageUtility;
import kr.co.clipsoft.biz.utility.img.ZipArchiveUtility;
import kr.co.clipsoft.biz.utility.xml.DataXmlUtility;
import kr.co.clipsoft.repository.ClipMyBatisTransactionManager;
import kr.co.clipsoft.repository.service.ClipMybatisSupport;

@Service("nuConsentService")
public class NuConsentServiceimpl extends ClipMybatisSupport implements NuConsentService {

    private static final Logger logger = LoggerFactory.getLogger(NuConsentServiceimpl.class);

    /**
     * 작성동의서 저장상태 - 임시저장
     */
    private final String CONSENT_STATE_TEMP = "TEMP";

    /**
     * 작성동의서 저장상태 - 진행중(인증저장 n개이상)
     */
    private final String CONSENT_STATE_ELECTR_TEMP = "ELECTR_TEMP";


    /**
     * 작성동의서 저장상태 - 구두동의
     */
    private final String CONSENT_STATE_ELECTR_VERBAL = "ELECTR_VERBAL";


    /**
     * 작성동의서 저장상태 - 의료인2인서명
     */
    private final String CONSENT_STATE_ELECTR_MULTI = "ELECTR_MULTI";

    /**
     * 작성동의서 저장상태 - 최종완료
     */
    private final String CONSENT_STATE_ELECTR_CMP = "ELECTR_CMP";

    /**
     * NAS 루트경로
     */
    @Value("#{customerProperties['server.nas.root.path']}")
    private String NAS_ROOT_PATH;

    /**
     * 녹취파일 경로
     */
    @Value("#{customerProperties['server.nas.record.path']}")
    private String NAS_REC_PATH;

    @Resource(name = "nuConsentDao")
    private NuConsentDao nuConsentDao;

    @Autowired
    private DataXmlUtility dataXmlUtility;

    @Autowired
    private OpdrSignCheckUtility opdrSignCheckUtility;

    @Autowired
    private PenDrawUtility penDrawUtility;

    @Autowired
    private ImageUtility imageUtility;

    @Autowired
    private ZipArchiveUtility zipArchiveUtility;

    @Autowired
    private LifeLongUtility lifeLongUtility;

    @Autowired
    private SignUserNmCheckUtility signUserNmCheckUtility;


    @Override
    public String getNowTime() {
        return nuConsentDao.getNowTime();
    }

    @Override
    public NuAppDto getApkVersion() {
        return nuConsentDao.getApkVersion();
    }

    @Override
    public List<NuAppDto> getApkVersionList() {
        return nuConsentDao.getApkVersionList();
    }

    @Override
    public List<NuAppDto> getPcExeVersionList() {
        return nuConsentDao.getPcExeVersionList();
    }

    @Override
    public NuConsentDeviceMstDto getDeviceMst(NuConsentDeviceMstDto paramDto) {
        return nuConsentDao.getDeviceMst(paramDto);
    }

    @Override
    public NuConsentMstDto getConsentMst(NuConsentMstDto paramDto) {
        return nuConsentDao.getConsentMst(paramDto);
    }

    @Override
    public List<NuConsentMstDto> getConsentMsts(NuConsentMstDto parameter) {
        return nuConsentDao.getConsentMsts(parameter);
    }

    @Override
    public List<NuConsentMstDetailDto> getConsentMstDetail(NuConsentMstDetailDto parameter) {

        parameter.setVisitTypeInQuery(parameter.getVisitType());
        parameter.setConsentStateInQuery(parameter.getConsentState());
        // by sangu02 2024-09-10 주치의 저장으로 인한 빠른조회 메서드 추가생성

        return nuConsentDao.getConsentMstDetail(parameter); // 기존
    }

    @Override
    public String saveConsent(NuConsentMstDto mstDto, NuConsentDataDto dataDto, NuConsentImageDto imageDto, List<MultipartFile> imageFiles,
                              NuConsentImageSignDto certDto, NuConsentFormExDto formExDto, NuConsentDeviceLogsDto logDto) throws BusinessException {

        // 저장 파라미터 기본값 설정
        setSaveParams(mstDto, dataDto, imageFiles, imageDto, certDto, logDto);

        // 등록된 유효 전자인증
        NuConsentImageSignDto recentImgSignDto = new NuConsentImageSignDto();
        recentImgSignDto.setDisable(false);

        // 저장 대상 서식 확장속성 조회
        formExDto = nuConsentDao.getConsentFormEx(formExDto);
        if (formExDto == null) {
            logger.error("서식 속성정보를 찾을 수 없습니다.");
            throw new BusinessException(BizErrorInfo.NO_FORM_EX_DATA);
        }

        // 서식에 지정된 전자인증 필요개수 저장데이터 필드값 설정(기본값 FORM_EX)
        String certNeedCnt = dataXmlUtility.getFormDataFieldValue(dataDto.getDataXml(), DataXmlUtility.DATA_FIELD_CERT_NEED_CNT,
                formExDto.getCertNeedCnt().toString());
        formExDto.setCertNeedCnt(Integer.valueOf(certNeedCnt));

        // 과거 환자가 작성한 동의서 리스트
        List<NuConsentMstDto> writedMstDtos = nuConsentDao.getConsentMsts(mstDto);

        // 연명서식 프로세스 - 선행 인증저장 서식 체크
        if (mstDto.isSave()) {
            lifeLongUtility.isPossibleViewSave(writedMstDtos, dataDto, formExDto);
        }

        // Lctech 인터페이스 데이터 - 연동 여부 확인
        List<NuTblLinkConsentInfoDto> listLcTechDto = null;
        if (imageDto.isLcTechUpload()) {
            listLcTechDto = imageUtility.uploadImagesLcTech(imageFiles, imageDto, mstDto);
        }

        // 등록 데이터 RID 정의
        initSaveRid(mstDto, dataDto, imageDto, imageFiles, certDto);

        // 저장 상태값(상태, 전자인증개수) 갱신
        setSaveStatus(mstDto, dataDto, imageDto, recentImgSignDto, formExDto, logDto);

        // 연명서식 프로세스 - 조건에 따라 다음 주치의 지정
        if (lifeLongUtility.isPossibleSetNextAtDocId(mstDto.getCertCnt(), formExDto)) {
            String nextAtDocId = dataXmlUtility.getFormDataFieldValue(dataDto.getDataXml(), DataXmlUtility.DATA_FIELD_NEXT_AT_DOC_ID, null);
            String nextAtDocName = dataXmlUtility.getFormDataFieldValue(dataDto.getDataXml(), DataXmlUtility.DATA_FIELD_NEXT_AT_DOC_NAME, null);

            mstDto.setCosignUserId(nextAtDocId);
            mstDto.setCosignUserName(nextAtDocName);
        }


        JSONObject resultJson = null;
        BizErrorInfo transactionError = null;

        ClipMyBatisTransactionManager tm = getTransactionManager();

        try {
            tm.start();

            // 인증저장시 의사/간호사 성명란이 접속자와 다른지 확인
            signUserNmCheckUtility.validationCheckSignUserNmDiff(mstDto.isSave(), mstDto.getUserName(), dataDto.getFormXml());

            // 동의서 중복저장 여부 확인
            validationCheckOverlapSave(mstDto);

            resultJson = saveConsentTransaction(mstDto, dataDto, imageDto, imageFiles, recentImgSignDto, certDto, listLcTechDto, logDto, formExDto);

            tm.commit();
        } catch (BusinessException e) {

            transactionError = e.getErrorData();

            logger.error("업무데이터 오류로 전자동의서 정보 저장에 실패하였습니다. : " + e.toString());
            tm.rollback();
        } catch (Exception e) {

            transactionError = BizErrorInfo.ERROR_VIEW_SAVE_DB_TRANSACTION;

            logger.error("전자동의서 정보 저장에 실패하였습니다.(DB) : " + e.toString());
            tm.rollback();
        } finally {
            tm.end();
        }

        if (transactionError != null) {
            throw new BusinessException(transactionError);
        }

        try {

            resultJson.put("result", new BizResultInfo().toJSONObject());
            resultJson.put("msgPopup", getViewSaveFinishMsgPopupInfo(mstDto.getCompleteYn(), dataDto.getDataXml()));

        } catch (ParseException e) {
            logger.error("RESULT PARSE ERROR : " + e.toString());
            throw new BusinessException(BizErrorInfo.ERROR_JSON_SETTING);
        }

        return resultJson.toJSONString();
    }




    /**
     * 저장되는 각 DTO 초기값 설정
     *
     * @param mstDto
     * @param dataDto
     * @param imageFiles
     * @param imageDto
     * @param certDto
     * @param logDto
     * @throws BusinessException
     */
    private void setSaveParams(NuConsentMstDto mstDto, NuConsentDataDto dataDto, List<MultipartFile> imageFiles, NuConsentImageDto imageDto,
                               NuConsentImageSignDto certDto, NuConsentDeviceLogsDto logDto) throws BusinessException {

        /**
         * 1. 마스터 데이터 설정
         */
        mstDto.setCreateUserId(mstDto.getUserId());
        mstDto.setCreateUserName(mstDto.getUserName());
        mstDto.setCreateUserDeptCd(mstDto.getUserDeptCd());
        mstDto.setCreateUserDeptName(mstDto.getUserDeptName());
        mstDto.setModifyUserId(mstDto.getUserId());
        mstDto.setModifyUserName(mstDto.getUserName());
        mstDto.setModifyUserDeptCd(mstDto.getUserDeptCd());
        mstDto.setModifyUserDeptName(mstDto.getUserDeptName());
        mstDto.setOpdrSignYn("N");
        mstDto.setSearchMode("normal");
        mstDto.setStartDate("20190101");
        mstDto.setEndDate(new SimpleDateFormat("yyyMMdd").format(new Date()));

        // CompleteYn에 따라 클라이언트 액션 판단(저장, 임시저장)
        if (mstDto.getCompleteYn().equals("Y")) {
            mstDto.setSave(true);
        } else {
            mstDto.setSave(false);
        }

        // 시술의서명유무 확인
        if (nuConsentDao.getOpdrYnCnt(mstDto) > 0) {
            String opdrSignYn = opdrSignCheckUtility.getOpdrSignYn(dataDto.getDataXml());
            mstDto.setOpdrSignYn(opdrSignYn);
        }

        /**
         * 2. 임시저장 데이터 설정
         */
        dataDto.setCreateUserId(mstDto.getUserId());
        dataDto.setDeviceIdentNo(logDto.getDeviceId());

        /**
         * 3. 전자인증 데이터 설정
         */
        certDto.setCreateUserId(mstDto.getUserId());
        certDto.setModifyUserId(mstDto.getUserId());

        /**
         * 4. 로그 데이터 설정
         */
        logDto.setSaveUserId(mstDto.getUserId());
        logDto.setSaveDeptCd(mstDto.getUserDeptCd());
        logDto.setFstRgstRid(mstDto.getUserId());
        logDto.setLastUpdtRid(mstDto.getUserId());

        /**
         * 5. 이미지 데이터 설정
         */
        imageDto.setFormId(mstDto.getFormId());
        imageDto.setImageFilename("img.zip");
        imageDto.setCompleteYn("Y");
        imageDto.setCreateUserId(mstDto.getUserId());
        imageDto.setImagePath("");
        imageDto.setImageFullPath("");

        if (imageFiles == null || imageFiles.size() == 0) {
            return;
        }

        byte[] imageZipData = null;
        try {
            imageZipData = zipArchiveUtility.createZip(imageFiles);
        } catch (IOException e) {
            logger.error("이미지 압축파일 생성 중 오류가 발생했습니다 : " + e.toString());
            throw new BusinessException(BizErrorInfo.ERROR_CREATE_IMAGE_ZIP);
        }

        imageDto.setImageSizeByte((long) imageZipData.length);
        imageDto.setImageData(imageZipData);
    }

    /**
     * 각 저장 테이블 PK(RID) 설정
     *
     * @param mstDto
     * @param dataDto
     * @param imageDto
     * @param imageFiles
     * @param certDto
     * @throws BusinessException
     */
    private void initSaveRid(NuConsentMstDto mstDto, NuConsentDataDto dataDto, NuConsentImageDto imageDto, List<MultipartFile> imageFiles,
                             NuConsentImageSignDto certDto) throws BusinessException {

        // 마스터 RID 정의
        if (mstDto.getConsentMstRid() == null || mstDto.getConsentMstRid() <= 0) {
            mstDto.setConsentMstRid(nuConsentDao.getConsentMstRid());
        }

        // 데이터 RID 정의
        Long consentDataRid = nuConsentDao.getConsentDataRid();
        dataDto.setConsentMstRid(mstDto.getConsentMstRid());
        dataDto.setConsentDataRid(consentDataRid);

        // 이미지 RID 정의(인증저장이고 이미지 업로드 여부 플래그에 따라 RID 생성)
        if (mstDto.isSave() && imageDto.isImageUpload() && imageFiles != null && imageFiles.size() > 0) {
            Long consentImageRid = nuConsentDao.getConsentImageRid();
            imageDto.setConsentImageRid(consentImageRid);
        }
        imageDto.setConsentMstRid(mstDto.getConsentMstRid());
        imageDto.setConsentDataRid(dataDto.getConsentDataRid());

        // 전자인증 RID 정의
        certDto.setConsentMstRid(mstDto.getConsentMstRid());
        certDto.setConsentDataRid(dataDto.getConsentDataRid());
        certDto.setConsentImageRid(imageDto.getConsentImageRid());
    }

    /**
     * 자장 상테 설정(임시저장, 진행중, 인증저장)
     *
     * @param mstDto
     * @param dataDto
     * @param imageDto
     * @param recentImageSignDto
     * @param formExDto
     * @param logDto
     * @throws BusinessException
     */
    private void setSaveStatus(NuConsentMstDto mstDto, NuConsentDataDto dataDto, NuConsentImageDto imageDto, NuConsentImageSignDto recentImageSignDto,
                               NuConsentFormExDto formExDto, NuConsentDeviceLogsDto logDto) throws BusinessException {

        NuConsentImageSignDto imageSignParamDto = new NuConsentImageSignDto();
        imageSignParamDto.setConsentMstRid(mstDto.getConsentMstRid());

        List<NuConsentImageSignDto> imageSignDtos = nuConsentDao.getConsentImageSigns(imageSignParamDto);

        if (!(mstDto.getCompleteYn().equals("Y") || mstDto.getCompleteYn().equals("N"))) {
            logger.error("저장상태값이 명확하지 않습니다." + mstDto.getCompleteYn());
            throw new BusinessException(BizErrorInfo.UNDEFINE_FORM_SAVE_STATUS);
        }

        if (formExDto.getCertNeedCnt() < 1) {
            logger.error("전자인증 필요 개수가 잘못되었습니다." + formExDto.toString());
            throw new BusinessException(BizErrorInfo.ERROR_CERT_NEED_CNT);
        }

        mstDto.setCertCnt(imageSignDtos.size());
        mstDto.setConsentState(CONSENT_STATE_TEMP);
        logDto.setStatCd("T");

        if (mstDto.getCompleteYn().equals("Y")) {
            // 인증저장 처리

            mstDto.setConsentState(CONSENT_STATE_ELECTR_TEMP);
            mstDto.setCompleteYn("N");
            dataDto.setCompleteYn("N");
            imageDto.setCompleteYn("N");

            // 마지막 인증자의 정보와 동일할경우 재인증을 위한 비활성화 처리
            if (imageSignDtos.size() > 0 && imageSignDtos.get(0).getCreateUserId().equals(mstDto.getUserId())) {
                recentImageSignDto.setDisable(true);
                recentImageSignDto.setConsentMstRid(imageSignDtos.get(0).getConsentMstRid());
                recentImageSignDto.setConsentDataRid(imageSignDtos.get(0).getConsentDataRid());
                recentImageSignDto.setConsentImageRid(imageSignDtos.get(0).getConsentImageRid());
                recentImageSignDto.setModifyUserId(imageSignDtos.get(0).getCreateUserId());
                recentImageSignDto.setCertStatus("N");
            }

            // 비활성화 처리할 과거 전자인증 값이 없기 떄문에 신규 전자인증 카운트 증가
            if (!recentImageSignDto.isDisable()) {
                mstDto.setCertCnt(mstDto.getCertCnt() + 1);
            }

        } else {
            // 임시저장 처리

            // 마지막 인증자의 인증유효 제거 및 카운트 감소
            if (imageSignDtos.size() > 0 && imageSignDtos.get(0).getCreateUserId().equals(mstDto.getUserId())) {
                recentImageSignDto.setDisable(true);
                recentImageSignDto.setConsentMstRid(imageSignDtos.get(0).getConsentMstRid());
                recentImageSignDto.setConsentDataRid(imageSignDtos.get(0).getConsentDataRid());
                recentImageSignDto.setConsentImageRid(imageSignDtos.get(0).getConsentImageRid());
                recentImageSignDto.setCreateUserId(imageSignDtos.get(0).getCreateUserId());
                recentImageSignDto.setCertStatus("N");

                mstDto.setCertCnt(mstDto.getCertCnt() - 1);
            }

            // 전자인증 개수가 1개 이상일때 진행중 상태
            if (mstDto.getCertCnt() > 0) {
                mstDto.setConsentState(CONSENT_STATE_ELECTR_TEMP);
            }

        }

        // 최종 완료여부 판단
        if (mstDto.getCertCnt() >= formExDto.getCertNeedCnt()) {

            SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String completeDatetime = dayTime.format(new Date(System.currentTimeMillis()));

            mstDto.setCompleteDatetime(completeDatetime);

            // 2021-12-02
            // V : 구두동의 / M : 의료인2인 / N : NONE
            if (mstDto.getSignFlag().equals("1")) {
                mstDto.setVerbalMultiFlag("V,N");
            } else if (mstDto.getSignFlag().equals("2")) {
                mstDto.setVerbalMultiFlag("M,N");
            } else if (mstDto.getSignFlag().equals("3")) {
                mstDto.setVerbalMultiFlag("V,Y");
            } else if (mstDto.getSignFlag().equals("4")) {
                mstDto.setVerbalMultiFlag("M,Y");
            } else {
                mstDto.setVerbalMultiFlag("N,N");
            }

            mstDto.setConsentState(CONSENT_STATE_ELECTR_CMP);
            mstDto.setCompleteYn("Y");

            dataDto.setCompleteYn("Y");
            imageDto.setCompleteYn("Y");
            logDto.setStatCd("E");
        }

    }

    /**
     * 뷰어 저장 DTO 테이블 저장 트랜젝션
     *
     * @param mstDto
     * @param dataDto
     * @param imageDto
     * @param imageFiles
     * @param recentCertDto
     * @param certDto
     * @param listLcTechDto
     * @param logDto
     * @return
     * @throws BusinessException
     * @throws Exception
     */
    private JSONObject saveConsentTransaction(NuConsentMstDto mstDto, NuConsentDataDto dataDto, NuConsentImageDto imageDto,
                                              List<MultipartFile> imageFiles, NuConsentImageSignDto recentCertDto, NuConsentImageSignDto certDto,
                                              List<NuTblLinkConsentInfoDto> listLcTechDto, NuConsentDeviceLogsDto logDto, NuConsentFormExDto formExDto)
            throws BusinessException, Exception {

        /**
         * 2020-08-06 평화이즈 요건 반영(용량이슈)
         *
         * <pre>
         * 용량 이슈로 인한 로직 변경
         * 1. 이미지 백업 제거
         * 2. FORM_XML 최신데이터만 유지 & DATA_XML 저장 제외(SQL 처리)
         * </pre>
         *
         */
        imageDto.setImageData(null);

        if (formExDto.getXmlHistoryYn() != null && formExDto.getXmlHistoryYn().equals("N")) {
            nuConsentDao.updateConsentDataNullRecent(dataDto);
        }
        // 2020-08-06 평화이즈 요건 반영 END

        JSONObject resultJson = new JSONObject();
        resultJson.put("consentMstRid", mstDto.getConsentMstRid());
        resultJson.put("consentDataRid", dataDto.getConsentDataRid());
        resultJson.put("consentImageRid", imageDto.getConsentImageRid());
        resultJson.put("consentState", mstDto.getConsentState());
        resultJson.put("reUpload", false);

        // 마스터정보 저장
        nuConsentDao.saveConsentMst(mstDto);

        // FORM XML DATA 등록
        nuConsentDao.insertConsentData(dataDto);

        // 이전 전자인증 비활성화 처리
        if (recentCertDto.isDisable()) {
            nuConsentDao.updateConsentSignImage(recentCertDto);
        }

        // 로그 등록
        nuConsentDao.insertConsentDeviceLog(logDto);


        // 임시저장일경우 처리 종료
        if (!mstDto.isSave()) {
            return resultJson;
        }

        // 전자인증 등록
        if (certDto.isEmptyCertData()) {
            logger.error("전자인증 값이 누락되어있습니다.");
            throw new BusinessException(BizErrorInfo.NO_CERT_DATA);
        }
        nuConsentDao.insertConsentSignImage(certDto);

        // 이미지 정보 등록
        boolean imgReUpload = saveConsentTransactionImage(imageDto, imageFiles, listLcTechDto, true);
        resultJson.put("reUpload", imgReUpload);

        return resultJson;
    }

    /**
     * 이미지 정보 등록
     *
     * @param imageDto      이미지 정보 DTO
     * @param imageFiles    이미지 파일 정보
     * @param listLcTechDto LC테크 인터페이스 정보
     * @return 이미지 재업로드 결과(재업로드 true)
     */
    private boolean saveConsentTransactionImage(NuConsentImageDto imageDto, List<MultipartFile> imageFiles,
                                                List<NuTblLinkConsentInfoDto> listLcTechDto, boolean isBeforImgDelete) {

        boolean reUpload = false;

        // 이미지 데이터 등록
        if (imageDto.isImageUpload()) {

            if (imageFiles == null || imageFiles.size() == 0) {
                logger.error("업로드 대상 이미지 파일이 없습니다.");
                throw new BusinessException(BizErrorInfo.NO_REQUEST_PARAM_IMAGE_FILES);
            }

            nuConsentDao.insertConsentImage(imageDto);
        } else {
            nuConsentDao.updateConsentImage(imageDto);
        }

        // LCTech 인터페이스 데이터 등록여부 확인
        if (!imageDto.isLcTechUpload()) {
            return reUpload;
        }

        if (listLcTechDto == null || listLcTechDto.size() == 0) {
            logger.error("LC테크 연동데이터가 없습니다.");
            throw new BusinessException(BizErrorInfo.NO_HAVE_LCTECH_INTERFACE_DATA);
        }

        // 삭제처리 여부 확인 및 삭제처리
        if (isBeforImgDelete) {
            String patId = listLcTechDto.get(0).getPatId();
            String ordDate = listLcTechDto.get(0).getOrdDate();
            String ocrTag = listLcTechDto.get(0).getOcrTag();

            reUpload = deleteBeforSaveImageNuTblLink(patId, ordDate, ocrTag);
        }

        nuConsentDao.insertTblLinkConsentInfo(listLcTechDto);

        return reUpload;
    }

    /**
     * LC테크 인터페이스 테이블에 이미 저장된 이미지가 있다면 삭제처리
     *
     * @param patId
     * @param ordDate
     * @param ocrTag
     * @return 재업로드 여부
     */
    private boolean deleteBeforSaveImageNuTblLink(String patId, String ordDate, String ocrTag) {

        boolean reUpload = false;

        NuTblLinkConsentInfoDto deleteLcImageParam = new NuTblLinkConsentInfoDto();
        deleteLcImageParam.setPatId(patId);
        deleteLcImageParam.setOrdDate(ordDate);
        deleteLcImageParam.setOcrTag(ocrTag);

        deleteLcImageParam = nuConsentDao.getTblLinkConsentInfo(deleteLcImageParam);

        if (deleteLcImageParam == null || !deleteLcImageParam.getCmd().equals("A")) {
            return reUpload;
        }

        reUpload = true;

        String pkExceptData = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date(System.currentTimeMillis()));
        deleteLcImageParam.setCmd("D");
        deleteLcImageParam.setImgPath(pkExceptData);

        List<NuTblLinkConsentInfoDto> deleteList = new ArrayList<NuTblLinkConsentInfoDto>();
        deleteList.add(deleteLcImageParam);

        nuConsentDao.insertTblLinkConsentInfo(deleteList);

        return reUpload;
    }

    /**
     * 저장이 완료된 후 출력할 알림메시지 정보 가져오기
     *
     * @param dataXml 알림메시지가 들어있는 dataXml
     * @return
     */
    private JSONObject getViewSaveFinishMsgPopupInfo(String completeYn, String dataXml) {

        JSONObject jsonMsgPopup = new JSONObject();
        boolean visible = true;
        String saveFinishMsg = dataXmlUtility.getFormDataFieldValue(dataXml, DataXmlUtility.DATA_FIELD_VIEW_SAVE_FINISH_MSG, "");

        if (saveFinishMsg.isEmpty()) {
            visible = false;
        }

        // 인증저장된 동의서만 호출
        if (!completeYn.equals("Y")) {
            visible = false;
        }

        jsonMsgPopup.put("visible", visible);
        jsonMsgPopup.put("msg", saveFinishMsg);

        return jsonMsgPopup;
    }

    /**
     * 동의서 중복저장 여부 확인
     *
     * <pre>
     * 작성된 동의서일경우 아래 조건 확인
     * 1. 임시저장일경우 이전 저장된 시간과 불일치시 저장 불가능
     *    -> 다른 단말기에서 이미 저장
     * </pre>
     * <p>
     * * @param mstDto
     */
    private void validationCheckOverlapSave(NuConsentMstDto mstDto) {

        NuConsentMstDto beforMstDto = nuConsentDao.getConsentMst(mstDto);

        // 신규 동의서 저장 가능
        if (beforMstDto == null) {
            return;
        }

        // 인증저장 버튼 클릭일경우 동의서 저장 가능
        if (mstDto.isSave()) {
            return;
        }

        // 필요 비교 파라미터 누락
        if (mstDto.getModifyDatetime() == null || mstDto.getModifyDatetime().isEmpty()) {
            throw new BusinessException(BizErrorInfo.NO_MODIFYDATETIME_DATA);
        }

        // 다른 클라이언트에서 저장 진행됨(수정시간 다름)
        if (!mstDto.getModifyDatetime().equals(beforMstDto.getModifyDatetime())) {
            throw new BusinessException(BizErrorInfo.ERROR_ALREADY_SAVE);
        }

    }

    @Override
    public boolean updateConsentMstUseYn(NuConsentMstDto parameter) {

        int result = nuConsentDao.updateConsentMstUseYn(parameter);
        return result > 0 ? true : false;
    }

    @Override
    public NuConsentDataDto getConsentXmlData(NuConsentDataDto parameter) {
        return nuConsentDao.getConsentXmlData(parameter);
    }

    @Override
    public boolean saveConsentDrow(NuConsentDrowDto parameter) {

        int saveCnt = 0;

        if (nuConsentDao.getConsentDrow(parameter) == null) {
            saveCnt = nuConsentDao.insertConsentDrow(parameter);
        } else {
            saveCnt = nuConsentDao.updateConsentDrow(parameter);
        }

        if (saveCnt > 0) {
            return true;
        }

        return false;
    }

    @Override
    public String getConsentDrow(NuConsentDrowDto parameter) {
        NuConsentDrowDto resultDto = nuConsentDao.getConsentDrow(parameter);

        if (resultDto == null) {
            return penDrawUtility.getErrorPendrawingResult("-1", "서식에 해당되는 펜그리기 정보가 없습니다.");
        }

        return penDrawUtility.getPendrawingResult(resultDto.getDrow());
    }

    @Override
    public JSONObject uploadImages(Integer consentMstRid, List<MultipartFile> imageFiles, String parameter, String beforImgDelete)
            throws BusinessException, Exception {

        JSONParser parser = new JSONParser();
        JSONArray params = (JSONArray) parser.parse(parameter);

        if (params.size() == 0) {
            throw new JsonParseException("JSON DATA가 없습니다.");
        }

        if (imageFiles == null || imageFiles.size() == 0) {
            throw new NullPointerException("IMAGE DATA가 없습니다.");
        }

        JSONObject resultJson = new JSONObject();
        resultJson.put("result", false);
        resultJson.put("reUpload", false);

        JSONObject param = (JSONObject) params.get(0);

        NuConsentMstDto mstDto = new NuConsentMstDto();
        mstDto.setPatientCode((String) param.get("patId"));
        mstDto.setClnDate((String) param.get("ordDate"));
        mstDto.setOcrTag((String) param.get("ocrTag"));
        mstDto.setInstCd((String) param.get("spcId"));
        mstDto.setCertNo(Long.valueOf((String) param.get("cretNo")));
        mstDto.setVisitType((String) param.get("patFlg"));
        mstDto.setClnDeptCd((String) param.get("examTyp"));
        mstDto.setUserId((String) param.get("userId"));
        mstDto.setFormName((String) param.get("examName"));
        mstDto.setFormCd((String) param.get("formCd"));

        byte[] imageZipData = zipArchiveUtility.createZip(imageFiles);

        NuConsentImageDto imageDto = new NuConsentImageDto();
        imageDto.setImageUpload(true);
        imageDto.setLcTechUpload(true);
        imageDto.setConsentImageRid(nuConsentDao.getConsentImageRid());
        imageDto.setConsentMstRid(consentMstRid.longValue());
        imageDto.setConsentDataRid(null);
        imageDto.setImgFileSubPath((String) param.get("imgFileSubPath"));
        imageDto.setImageSizeByte((long) imageZipData.length);
        imageDto.setImageData(imageZipData);
        imageDto.setImagePath("");
        imageDto.setImageFullPath("");
        imageDto.setImageFilename("img.zip");
        imageDto.setCompleteYn("Y");
        imageDto.setFormId(0L);
        imageDto.setCreateUserId(mstDto.getUserId());

        List<NuTblLinkConsentInfoDto> listLcTechDto = imageUtility.uploadImagesLcTech(imageFiles, imageDto, mstDto);

        BizErrorInfo transactionError = null;

        ClipMyBatisTransactionManager tm = getTransactionManager();
        try {
            tm.start();

            boolean isBeforImgDelete = true;
            if (beforImgDelete != null && beforImgDelete.equals("N")) {
                isBeforImgDelete = false;
            }

            // 이미지 정보 등록
            boolean imgReUpload = saveConsentTransactionImage(imageDto, imageFiles, listLcTechDto, isBeforImgDelete);

            tm.commit();

            resultJson.put("result", true);
            resultJson.put("reUpload", imgReUpload);
            resultJson.put("consentMstRid", consentMstRid);
            resultJson.put("consentImageRid", imageDto.getConsentImageRid());

        } catch (BusinessException e) {

            transactionError = e.getErrorData();

            logger.error("업무데이터 오류로 이미지 정보 저장에 실패하였습니다. : " + e.toString());
            tm.rollback();
        } catch (Exception e) {

            transactionError = BizErrorInfo.ERROR_VIEW_SAVE_DB_TRANSACTION;

            logger.error("이미지 정보 저장에 실패하였습니다.(DB) : " + e.toString());
            tm.rollback();
        } finally {
            tm.end();
        }

        if (transactionError != null) {
            throw new BusinessException(transactionError);
        }

        return resultJson;
    }

    @Override
    public List<NuConsentRecordDto> getConsentRecord(NuConsentRecordDto paramDto) {
        return nuConsentDao.getConsentRecord(paramDto);
    }

    @Override
    public boolean uploadRecords(List<MultipartFile> records, List<NuConsentRecordDto> paramDtos) throws IllegalStateException, IOException {

        HashMap<String, NuConsentRecordDto> mapRecordFileInfos = new HashMap<String, NuConsentRecordDto>();
        for (NuConsentRecordDto item : paramDtos) {
            mapRecordFileInfos.put(item.getRecordFileName(), item);
        }

        if (records.size() != mapRecordFileInfos.size()) {
            logger.error("녹취 파일 개수와 파일정보 개수가 일치하지 않습니다.");
            return false;
        }

        for (MultipartFile recordFile : records) {

            Long consentRecordRid = nuConsentDao.getConsentRecordRid();

            String recordFileName = recordFile.getOriginalFilename();

            NuConsentRecordDto recordFileInfo = mapRecordFileInfos.get(recordFileName);
            String recordFileSubPath = recordFileInfo.getRecordPath();

            String recordFileDirPath = NAS_ROOT_PATH + NAS_REC_PATH + recordFileSubPath;
            File recordFileDir = new File(recordFileDirPath);
            if (!(recordFileDir.exists() && recordFileDir.isDirectory())) {
                recordFileDir.mkdirs();
            }

            File frecordFile = new File(recordFileDir.getPath() + File.separator + recordFileName);
            recordFile.transferTo(frecordFile);
            recordFileInfo.setConsentRecordRid(consentRecordRid);
            recordFileInfo.setRecordFullPath(recordFileSubPath + recordFileName);
            recordFileInfo.setRecordSizeByte(frecordFile.length());

        }

        nuConsentDao.insertConsentRecordRst(paramDtos);

        return true;
    }

    @Override
    public void downloadRecordFile(HttpServletRequest request, HttpServletResponse response, NuConsentRecordDto paramDto) throws IOException {

        String recFilePath = NAS_ROOT_PATH + NAS_REC_PATH + paramDto.getRecordFullPath();
        File recFile = new File(recFilePath);

        if (!(recFile.exists() && recFile.isFile())) {
            throw new IOException("녹취파일 업로드에 실패하였습니다.");
        }

        String userAgent = request.getHeader("User-Agent");

        boolean ie = userAgent.indexOf("MSIE") > -1;

        String fileName = null;
        if (ie) {
            fileName = URLEncoder.encode(recFile.getName(), "utf-8");
        } else {
            fileName = new String(recFile.getName().getBytes("utf-8"));
        }

        response.setContentType("application/download; utf-8");
        response.setContentLength((int) recFile.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\";");
        response.setHeader("Content-Transfer-Encoding", "binary");
        OutputStream out = response.getOutputStream();
        FileInputStream fis = null;

        fis = new FileInputStream(recFile);
        FileCopyUtils.copy(fis, out);

        if (fis != null) {
            try {
                fis.close();
            } catch (Exception e) {
                logger.error("FileInputStream Close Fail : " + e.toString());
            }
        }

        out.flush();

    }

    @Override
    public NuConsentUserFormDto saveUserForm(NuConsentUserFormDto paramDto) {
        nuConsentDao.savesaveUserForm(paramDto);
        return paramDto;
    }

    @Override
    public NuConsentFormExDto getConsentFormEx(NuConsentFormExDto paramDto) {
        NuConsentFormExDto resultDto = nuConsentDao.getConsentFormEx(paramDto);
        if (resultDto == null) {
            resultDto = new NuConsentFormExDto();
            resultDto.setInstCd(paramDto.getInstCd());
            resultDto.setFormId(paramDto.getFormId());
            resultDto.setFormVersion(paramDto.getFormVersion());
            resultDto.setExternalCnt(0l);
            resultDto.setCosignYn("N");
            resultDto.setXmlHistoryYn("N");
            resultDto.setCtlHistoryYn("N");
            resultDto.setOpdrYn("N");
            resultDto.setNursCertYn("N");
            resultDto.setCertNeedCnt(1);
        }
        return resultDto;
    }

    @Override
    public String getIsPossibleViewOpen(List<NuConsentMstDto> paramDtos, String companyCode) {

        if (paramDtos == null || paramDtos.size() == 0) {
            logger.error("환자/서식 정보리스트 데이터가 없습니다.");
            throw new BusinessException(BizErrorInfo.NO_REQUEST_PARAM);
        }

        BizResultInfo bizReuslt = new BizResultInfo();

        for (NuConsentMstDto paramMstDto : paramDtos) {

            paramMstDto.setInstCd(companyCode);

            NuConsentFormExDto paramFormExDto = new NuConsentFormExDto();
            paramFormExDto.setInstCd(paramMstDto.getInstCd());
            paramFormExDto.setFormId(paramMstDto.getFormId());
            paramFormExDto.setFormVersion(paramMstDto.getFormVersion());

            paramMstDto.setSearchMode("normal");
            paramMstDto.setStartDate("20190101");
            paramMstDto.setEndDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));

            NuConsentFormExDto formExDto = nuConsentDao.getConsentFormEx(paramFormExDto);
            List<NuConsentMstDto> mstDtos = nuConsentDao.getConsentMsts(paramMstDto);

            try {
                // 연명 프로세스에 따라 뷰어호출 가능여부 판단
                lifeLongUtility.isPossibleViewOpen(formExDto, mstDtos);
                bizReuslt.setResult(true);
            } catch (BusinessException e) {
                bizReuslt = new BizResultInfo(false, e);
                break;
            }

        }

        return bizReuslt.toResultOfJSONString();
    }

    @Override
    public String getWritedLifelongKind(NuConsentMstDto paramDto) {

        List<NuConsentMstDto> resultDtos = nuConsentDao.getWritedLifelongKinds(paramDto);

        JSONObject jsonResult = new JSONObject();
        JSONArray jsonDropDownItems = new JSONArray();
        JSONObject jsonItemWritedLifeLognKind = new JSONObject();
        JSONObject jsonItemNoneSelect = new JSONObject();
        JSONObject jsonItem1 = new JSONObject();
        JSONObject jsonItem2 = new JSONObject();

        jsonItemNoneSelect.put("Caption", "선택안함");
        jsonItemNoneSelect.put("ItemValue", "NONE");

        jsonItemWritedLifeLognKind.put("Caption", "자동선택");
        jsonItemWritedLifeLognKind.put("ItemValue", "NONE");

        if (resultDtos.size() > 0) {
            jsonItemWritedLifeLognKind.put("ItemValue", resultDtos.get(0).getLifelongKind());
        }

        jsonItem1.put("Item", jsonItemWritedLifeLognKind);
        jsonItem2.put("Item", jsonItemNoneSelect);

        jsonDropDownItems.add(jsonItem1);
        jsonDropDownItems.add(jsonItem2);

        jsonResult.put("DropDownItems", jsonDropDownItems);

        return jsonResult.toJSONString();
    }


//	@Override
//	public String getDoctorList(NuConsentMstDto paramDto) {
//
//	//	List<NuConsentMstDto> resultDtos = nuConsentDao.getDoctorList(paramDto);
//
//		JSONObject jsonResult = new JSONObject();
//		JSONArray jsonDropDownItems = new JSONArray();
//		JSONObject jsonItemWritedLifeLognKind = new JSONObject();
//		JSONObject jsonItemNoneSelect = new JSONObject();
//		JSONObject jsonItem1 = new JSONObject();
//		JSONObject jsonItem2 = new JSONObject();
//
//		jsonItemNoneSelect.put("Caption", "선택안함");
//		jsonItemNoneSelect.put("ItemValue", "NONE");
//
//		jsonItemWritedLifeLognKind.put("Caption", "자동선택");
//		jsonItemWritedLifeLognKind.put("ItemValue", "NONE");
//
////		if (resultDtos.size() > 0) {
////			jsonItemWritedLifeLognKind.put("ItemValue", resultDtos.get(0).getLifelongKind());
////		}
//
//		jsonItem1.put("Item", jsonItemWritedLifeLognKind);
//		jsonItem2.put("Item", jsonItemNoneSelect);
//
//		jsonDropDownItems.add(jsonItem1);
//		jsonDropDownItems.add(jsonItem2);
//
//		jsonResult.put("DropDownItems", jsonDropDownItems);
//
//		return jsonResult.toJSONString();
//	}

    @Override
    public String rollbackSaveConsent(NuConsentMstDto mstDto, NuConsentDataDto dataDto, NuConsentImageSignDto certDto,
                                      NuConsentDeviceLogsDto logDto) {

        // 변경된 MST 정보 조회
        NuConsentMstDto beformMstDto = nuConsentDao.getConsentMst(mstDto);
        // 전자인증 후 등록된 ConsentData 카운트 조회
        int consentDataCnt = nuConsentDao.getConsentDataCnt(dataDto);

        /**
         * MST 정보 롤백 설정
         */
        mstDto.setConsentState(CONSENT_STATE_TEMP);
        mstDto.setCompleteYn("N");
        mstDto.setCompleteDatetime("");
        mstDto.setOpdrSignYn("N");
        mstDto.setOcrTag(beformMstDto.getOcrTag());

        if (beformMstDto.getCertCnt() > 0) {
            mstDto.setCertCnt(beformMstDto.getCertCnt() - 1);
        } else {
            mstDto.setCertCnt(0);
        }

        // ConsentData Row Data 1건 이하 일경우 삭제(미사용) 플래그 처리
        if (consentDataCnt <= 1) {
            mstDto.setUseYn("N");
            mstDto.setCosignDeptCode(null);
        }

        mstDto.setModifyUserId(beformMstDto.getModifyUserId());
        mstDto.setModifyUserName(beformMstDto.getModifyUserName());
        mstDto.setModifyUserDeptCd(beformMstDto.getModifyUserDeptCd());
        mstDto.setModifyUserDeptName(beformMstDto.getModifyUserDeptName());

        /**
         * IMAGE_SIGN 정보 롤백 설정
         */
        certDto.setCertStatus("N");
        certDto.setModifyUserId(beformMstDto.getModifyUserId());
        certDto.setConsentImageRid(null);

        /**
         * LOG 롤백 이력 적용
         */
        logDto.setStatCd("R");
        logDto.setOcrTag(beformMstDto.getOcrTag());
        logDto.setSaveUserId(beformMstDto.getModifyUserId());
        logDto.setSaveDeptCd(beformMstDto.getModifyUserDeptCd());
        logDto.setFstRgstRid(beformMstDto.getModifyUserId());
        logDto.setLastUpdtRid(beformMstDto.getModifyUserId());

        BizErrorInfo transactionError = null;
        ClipMyBatisTransactionManager tm = getTransactionManager();

        try {
            tm.start();

            if (nuConsentDao.deleteConsentData(dataDto) != 1) {
                new BusinessException(BizErrorInfo.NO_UPDATE_DB_DATA);
            }

            if (nuConsentDao.saveConsentMst(mstDto) != 1) {
                new BusinessException(BizErrorInfo.NO_UPDATE_DB_DATA);
            }

            if (consentDataCnt <= 1) {
                if (nuConsentDao.updateConsentMstUseYn(mstDto) != 1) {
                    new BusinessException(BizErrorInfo.NO_UPDATE_DB_DATA);
                }
            }

            if (nuConsentDao.updateConsentSignImage(certDto) != 1) {
                new BusinessException(BizErrorInfo.NO_UPDATE_DB_DATA);
            }

            if (nuConsentDao.insertConsentDeviceLog(logDto) != 1) {
                new BusinessException(BizErrorInfo.NO_UPDATE_DB_DATA);
            }

            tm.commit();
        } catch (BusinessException e) {

            transactionError = e.getErrorData();

            logger.error("업무데이터 원복처리에 실패하였습니다. : " + e.toString());
            tm.rollback();
        } catch (Exception e) {

            transactionError = BizErrorInfo.NO_UPDATE_DB_DATA;

            logger.error("업무데이터 원복처리에 오류가 발생하였습니다. : " + e.toString());
            tm.rollback();
        } finally {
            tm.end();
        }

        if (transactionError != null) {
            throw new BusinessException(transactionError);
        }

        JSONObject resultJson = new JSONObject();

        try {
            resultJson.put("result", new BizResultInfo().toJSONObject());
        } catch (ParseException e) {
            logger.error("RESULT PARSE ERROR[saveConsentRollback] : " + e.toString());
            throw new BusinessException(BizErrorInfo.ERROR_JSON_SETTING);
        }

        return resultJson.toJSONString();
    }

    @Override
    public String getConsentMstRidByOcrTag(String ocrTag) {
        NuConsentMstDto param = new NuConsentMstDto();
        param.setOcrTag(ocrTag);
        return nuConsentDao.getConsentMstRidByOcrTag(param);
    }


}
