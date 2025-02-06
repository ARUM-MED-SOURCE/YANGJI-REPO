package kr.co.clipsoft.biz.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

@Repository("nuConsentDao")
public class NuConsentDao {

	private static final Logger logger = LoggerFactory.getLogger(NuConsentDao.class);

	@Autowired
	private SqlSession sqlSession;

	/**
	 * Consent Mst Rid 시퀀스 증가 반환
	 * 
	 * @return 증가된 Consent Mst Rid 시퀀스
	 */
	public Long getConsentMstRid() {
		return sqlSession.selectOne("NU_CONSENT.getConsentMstRid");
	}

	/**
	 * Consent Data Rid 시퀀스 증가 반환
	 * 
	 * @return 증가된 Consent Data Rid 시퀀스
	 */
	public Long getConsentDataRid() {
		return sqlSession.selectOne("NU_CONSENT.getConsentDataRid");
	}

	/**
	 * Consent DataField Rid 시퀀스 증가 반환
	 * 
	 * @return 증가된 Consent DataField Rid 시퀀스
	 */
	public Long getConsentDataFieldRid() {
		return sqlSession.selectOne("NU_CONSENT.getConsentDataFieldRid");
	}

	/**
	 * Consent Image Rid 시퀀스 증가 반환
	 * 
	 * @return 증가된 Consent Image Rid 시퀀스
	 */
	public Long getConsentImageRid() {
		return sqlSession.selectOne("NU_CONSENT.getConsentImageRid");
	}

	/**
	 * Consent Record Rid 시퀀스 증가 반환
	 * 
	 * @return 증가된 Consent Record Rid 시퀀스
	 */
	public Long getConsentRecordRid() {
		return sqlSession.selectOne("NU_CONSENT.getConsentRecordRid");
	}

	/**
	 * 서버에 등록된 모바일 APK정보 조회(단건)(모바일 전자동의서만)
	 * 
	 * @return 서버에 등록된 모바일 APK정보
	 */
	public NuAppDto getApkVersion() {
		return sqlSession.selectOne("NU_CONSENT.getApkVersion");
	}

	/**
	 * 서버에 등록된 모바일 APK정보 조회
	 * 
	 * @return 서버에 등록된 모바일 APK정보
	 */
	public List<NuAppDto> getApkVersionList() {
		return sqlSession.selectList("NU_CONSENT.getApkVersionList");
	}

	/**
	 * 서버에 등록된 PC EXE정보 조회
	 * 
	 * @return 서버에 등록된 PC EXE정보
	 */
	public List<NuAppDto> getPcExeVersionList() {
		return sqlSession.selectList("NU_CONSENT.getPcExeVersionList");
	}

	/**
	 * 현재 시간 가져오기
	 * 
	 * @return 현재시간
	 */
	public String getNowTime() {
		return sqlSession.selectOne("NU_CONSENT.getNowTime");
	}

	/**
	 * 단말기 마스터 정보 조회
	 * 
	 * @return 단말기 마스터 정보
	 */
	public NuConsentDeviceMstDto getDeviceMst(NuConsentDeviceMstDto paramDto) {
		return sqlSession.selectOne("NU_CONSENT.getDeviceMst", paramDto);
	}

	/**
	 * 전자동의서 마스터(CONSENT MST)(진행,완료 동의서) 조회
	 * 
	 * @param mstDto
	 * @return
	 */
	public NuConsentMstDto getConsentMst(NuConsentMstDto paramDto) {
		return sqlSession.selectOne("NU_CONSENT.getConsentMst", paramDto);
	}

	/**
	 * 전자동의서 마스터(CONSENT MST)(진행,완료 동의서) 리스트 조회
	 * 
	 * @param parameter 조회 대상 CONSENT DTO
	 * 
	 * @return 동의서 리스트
	 */
	public List<NuConsentMstDto> getConsentMsts(NuConsentMstDto parameter) {
		return sqlSession.selectList("NU_CONSENT.getConsentMsts", parameter);
	}


	/**
	 * 전자동의서 마스터(CONSENT MST)(진행,완료 동의서) 리스트 조회(상세조건)
	 * 
	 * @param parameter 조회 대상 CONSENT DTO
	 * @return 동의서 리스트
	 */
	public List<NuConsentMstDetailDto> getConsentMstDetail(NuConsentMstDetailDto parameter) {
		return sqlSession.selectList("NU_CONSENT.getConsentMstDetail", parameter);
	}

	/**
	 * 전자동의서 빠른 조회 NrCosign 검색시 주치의 조건 추가 및 변경
	 *
	 * @param parameter 조회 대상 CONSENT DTO
	 * @return 동의서 리스트
	 * @author sangu02
	 * @since 2024-09-10
	 */
	public List<NuConsentMstDetailDto> getConsentMstDetailNrCosign(NuConsentMstDetailDto parameter) {
		return sqlSession.selectList("NU_CONSENT.getConsentMstDetailNrCosign", parameter);
	}


	/**
	 * 전자동의서 마스터 정보 저장
	 * 
	 * @param parameter 전자동의서 마스터 정보
	 * @return
	 */
	public int saveConsentMst(NuConsentMstDto parameter) {
		return sqlSession.insert("NU_CONSENT.saveConsentMst", parameter);
	}


	/**
	 * 임시저장된 서식 사용여부 설정
	 * 
	 * @param parameter 전자동의서 마스터 정보
	 * @return
	 */
	public int updateConsentMstUseYn(NuConsentMstDto parameter) {
		return sqlSession.insert("NU_CONSENT.updateConsentMstUseYn", parameter);
	}

	/**
	 * 전자동의서 ept/xmlData 데이터 조회(임시저장 조회)
	 * 
	 * @param parameter 조회 대상 CONSENT DTO
	 * @return 조회 결과 DTO
	 */
	public NuConsentDataDto getConsentXmlData(NuConsentDataDto parameter) {
		return sqlSession.selectOne("NU_CONSENT.getConsentXmlData", parameter);
	}

	/**
	 * 전자동의서 데이터(ept/xmlData) 등록(임시저장, 저장)
	 * 
	 * @param parameter 등록 대상 DTO
	 * @return 등록된 결과 COUNT
	 */
	public int insertConsentData(NuConsentDataDto parameter) {
		return sqlSession.insert("NU_CONSENT.insertConsentData", parameter);
	}

	/**
	 * 전자동의서 데이터(ept/xmlData) Null처리(평화이즈 요건)
	 * 
	 * @param parameter 등록 대상 DTO
	 * @return 변경된 결과 COUNT
	 */
	public int updateConsentDataNullRecent(NuConsentDataDto parameter) {
		return sqlSession.update("NU_CONSENT.updateConsentDataNullRecent", parameter);
	}

	/**
	 * 완료된 전자동의서 이미지 정보 등록
	 * 
	 * @param parameter 등록 대상 DTO
	 * @return 등록된 결과 COUNT
	 */
	public int insertConsentImage(NuConsentImageDto parameter) {
		return sqlSession.insert("NU_CONSENT.insertConsentImage", parameter);
	}

	/**
	 * 완료된 전자동의서 이미지 정보(ID) 갱신
	 * 
	 * @param imageDto
	 * @return
	 */
	public int updateConsentImage(NuConsentImageDto parameter) {
		return sqlSession.update("NU_CONSENT.updateConsentImage", parameter);
	}

	/**
	 * 전자인증된 이미지 정보 등록
	 * 
	 * @param parameter 등록 대상 DTO
	 * @return 등록된 결과 COUNT
	 */
	public int insertConsentSignImage(NuConsentImageSignDto parameter) {
		return sqlSession.insert("NU_CONSENT.insertConsentSignImage", parameter);
	}

	/**
	 * 전자동의서 펜드로잉 정보(그리기 정보) 조회
	 * 
	 * @param parameter 조회 대상 DTO
	 * @return 펜드로잉 정보
	 */
	public NuConsentDrowDto getConsentDrow(NuConsentDrowDto parameter) {
		return sqlSession.selectOne("NU_CONSENT.getConsentDrow", parameter);
	}

	/**
	 * 전자동의서 펜드로잉 정보(그리기 결과) 등록
	 * 
	 * @param parameter 등록 대상 DTO
	 * @return
	 */
	public int insertConsentDrow(NuConsentDrowDto parameter) {
		return sqlSession.insert("NU_CONSENT.insertConsentDrow", parameter);
	}

	/**
	 * 전자동의서 펜드로잉 정보(그리기 결과) 업데이트
	 * 
	 * @param parameter 업데이트 대상 DTO
	 * @return
	 */
	public int updateConsentDrow(NuConsentDrowDto parameter) {
		return sqlSession.update("NU_CONSENT.updateConsentDrow", parameter);
	}

	/**
	 * 동의서 녹취 정보 조회
	 * 
	 * @param paramDto
	 * @return
	 */
	public List<NuConsentRecordDto> getConsentRecord(NuConsentRecordDto paramDto) {
		return sqlSession.selectList("NU_CONSENT.getConsentRecord", paramDto);
	}

	/**
	 * 동의서 녹취 정보 등록
	 * 
	 * @param paramDtos
	 * @return
	 */
	public int insertConsentRecordRst(List<NuConsentRecordDto> paramDtos) {
		return sqlSession.insert("NU_CONSENT.insertConsentRecordRst", paramDtos);
	}

	/**
	 * 전자동의서 저장(임시저장) 로그 등록
	 * 
	 * @param paramDto
	 * @return
	 */
	public int insertConsentDeviceLog(NuConsentDeviceLogsDto paramDto) {
		return sqlSession.insert("NU_CONSENT.insertConsentDeviceLog", paramDto);
	}

	/**
	 * 전자동의서 사용자 지정 속성 저장
	 * 
	 * @param paramDto
	 * @return
	 */
	public int savesaveUserForm(NuConsentUserFormDto paramDto) {
		return sqlSession.insert("NU_CONSENT.saveUserForm", paramDto);
	}

	/**
	 * 조건에 맞는 시술의서명이 가능한 동의서 카운트 조회
	 * 
	 * @param mstDto
	 * @return
	 */
	public int getOpdrYnCnt(NuConsentMstDto paramDto) {
		return sqlSession.selectOne("NU_CONSENT.getOpdrYnCnt", paramDto);
	}

	/**
	 * LcTech 인터페이스 데이터 등록
	 * 
	 * @param paramDtos
	 * @return
	 */
	public int insertTblLinkConsentInfo(List<NuTblLinkConsentInfoDto> paramDtos) {
		return sqlSession.insert("NU_CONSENT.insertTblLinkConsentInfo", paramDtos);
	}

	/**
	 * 전자동의서 CDIS서버에 이전에 업로드된 마지막 이미지 정보 조회
	 * 
	 * @param param
	 * @return
	 */
	public NuTblLinkConsentInfoDto getTblLinkConsentInfo(NuTblLinkConsentInfoDto paramDto) {
		return sqlSession.selectOne("NU_CONSENT.getTblLinkConsentInfo", paramDto);
	}

	/**
	 * 전자동의서 서식 확장속성 정보 저장
	 * 
	 * @param paramDto
	 * @return
	 */
	public int saveConsentFormEx(NuConsentFormExDto paramDto) {
		return sqlSession.update("NU_CONSENT.saveConsentFormEx", paramDto);
	}

	/**
	 * 전자동의서 서식 확장속성 조회
	 * 
	 * @param paramDto
	 * @return
	 */
	public NuConsentFormExDto getConsentFormEx(NuConsentFormExDto paramDto) {
		return sqlSession.selectOne("NU_CONSENT.getConsentFormEx", paramDto);
	}

	/**
	 * 작성동의서 유효한 전자인증값 조회
	 * 
	 * @param consentMstRid
	 * @return
	 */
	public List<NuConsentImageSignDto> getConsentImageSigns(NuConsentImageSignDto paramDto) {
		return sqlSession.selectList("NU_CONSENT.getConsentImageSigns", paramDto);
	}

	/**
	 * 전자인증 유효값 업데이트
	 * 
	 * @param certDto
	 */
	public int updateConsentSignImage(NuConsentImageSignDto certDto) {
		return sqlSession.update("NU_CONSENT.updateConsentSignImage", certDto);
	}

	/**
	 * 환자기준 작성한 연명서식(9,13호 제외) 속성 조회
	 * 
	 * @param paramDto
	 * @return
	 */
	public List<NuConsentMstDto> getWritedLifelongKinds(NuConsentMstDto paramDto) {
		return sqlSession.selectList("NU_CONSENT.getWritedLifelongKinds", paramDto);
	}

	/**
	 * 수술 집도의 - 주치의 리스트 조회
	 * 
	 * @param paramDto
	 * @return
	 */
//	public List<NuConsentMstDto> getDoctorList(NuConsentMstDto paramDto) {
//		return sqlSession.selectList("NU_CONSENT.getDoctorList", paramDto);
//	}

	/**
	 * ConsentData 개수 조회
	 * 
	 * @param paramDto
	 * @return
	 */
	public int getConsentDataCnt(NuConsentDataDto paramDto) {
		return sqlSession.selectOne("NU_CONSENT.getConsentDataCnt", paramDto);
	}

	/**
	 * ConsentData 삭제
	 * 
	 * @param dataDto
	 */
	public int deleteConsentData(NuConsentDataDto paramDto) {
		return sqlSession.delete("NU_CONSENT.deleteConsentData", paramDto);
	}

	/**
	 * OCR_TAG로 ConsentMstRid 조회
	 * 
	 * @param param
	 * @return
	 */
	public String getConsentMstRidByOcrTag(NuConsentMstDto param) {
		return sqlSession.selectOne("NU_CONSENT.getConsentMstRidByOcrTag", param);
	}

}
