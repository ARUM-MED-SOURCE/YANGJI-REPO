package kr.co.clipsoft.biz.model.consent;

/**
 * 작성동의서 정보 관리
 *
 *
 */
public class NuConsentMstDto {

	/*
	* NrCosign 추가
	* */
	private String nrCosign;

	public String getNrCosign() {
		return nrCosign;
	}

	public void setNrCosign(String nrCosign) {
		this.nrCosign = nrCosign;
	}


	/*
	 * =============================================
	 * 수술예약번호
	 * 2023-10-16
	 * */

	private String operRezNo;

	public String getOperRezNo() {
		return operRezNo;
	}
	public void setOperRezNo(String operRezNo) {
		this.operRezNo = operRezNo;
	}

	/*
	 * =============================================
	 * 구두동의/의료인 2인 플래그
	 * 2021-12-02
	 * */
	private String signFlag;  
	
	public String getSignFlag() {
		return signFlag == null ? "" : signFlag; 
	}
	public void setSignFlag(String signFlag) {
		this.signFlag = signFlag;
	}

	/*
	 * =============================================
	 * 구두동의/의료인 2인 이력 저장용
	 * 2021-12-02
	 * */
	// 저장플래그값
	private String verbalMultiFlag;	 
	
	// 작성상태값
	private String verbalMultiState;
	
	public String getVerbalMulti() {
		return verbalMultiFlag == null ? "" : verbalMultiFlag;
	} 
	public void setVerbalMultiFlag(String verbalMultiFlag) {
		this.verbalMultiFlag = verbalMultiFlag;
	} 	
	
	public String getVerbalMultiState() {
		return verbalMultiState == null ? "" : verbalMultiState;
	}
	public void setVerbalMultiState(String verbalMultiState) {
		this.verbalMultiState = verbalMultiState;
	}

	/**
	 * =============================================
	 */


	/**
	 * =============================================
	 */



	/*
	 * =============================================
	 * 작성동의서 빠른 조회 - 내가 작성한 동의서만 조회 Flag
	 * 2021-12-02
	 * */
	private String myWriteFlag;
	
	private String myWriteFlagYn;

	public String getMyWriteFlag() {
		return myWriteFlag;
	}
	public void setMyWriteFlag(String myWriteFlag) {
		this.myWriteFlag = myWriteFlag;
	}

	public String getMyWriteFlagYn() {
		return myWriteFlagYn;
	}

	public void setMyWriteFlagYn(String myWriteFlagYn) {
		this.myWriteFlagYn = myWriteFlagYn;
	}	
	/**
	 * =============================================
	 */
	
	

	/**
	 * 기관코드
	 */
	private String instCd;

	/**
	 * 작성동의서 RID
	 */
	private Long consentMstRid;

	/**
	 * 기관코드
	 */
	private String hosType;

	/**
	 * 환자번호
	 */
	private String patientCode;

	/**
	 * OCR TAG
	 */
	private String ocrTag;

	/**
	 * 원무 등록순번
	 */
	private Long certNo;

	/**
	 * 환자명
	 */
	private String patientName;

	/**
	 * 환자나이
	 */
	private String patientAge;

	/**
	 * 환자성별
	 */
	private String patientSex;

	/**
	 * 내원구분
	 */
	private String visitType;

	/**
	 * 진료(수진)일자
	 */
	private String clnDate;

	/**
	 * 진료부서코드
	 */
	private String clnDeptCd;

	/**
	 * 진료부서명
	 */
	private String clnDeptNm;

	/**
	 * 병동 코드
	 */
	private String wardCd;

	/**
	 * 병동 명
	 */
	private String wardName;

	/**
	 * 병실코드
	 */
	private String roomCd;

	/**
	 * 서식ID(솔루션)
	 */
	private Long formId;

	/**
	 * 서식코드(병원)
	 */
	private String formCd;

	/**
	 * 서식명
	 */
	private String formName;

	/**
	 * 서식버전
	 */
	private Long formVersion;

	/**
	 * 처방일자
	 */
	private String orderDate;

	/**
	 * 처방코드
	 */
	private String orderCd;

	/**
	 * 처방순번코드
	 */
	private String orderSeqNo;

	/**
	 * 처방명
	 */
	private String orderName;

	/**
	 * 주치의 ID
	 */
	private String atDoctId;

	/**
	 * 진료의명
	 */
	private String ordDrNm;

	/**
	 * 처방구분
	 */
	private String orderDiv;

	/**
	 * 작성상태
	 */
	private String consentState;

	/**
	 * 작성상태명
	 */
	private String consentStateDisp;

	/**
	 * 작성완료 여부
	 */
	private String completeYn;

	/**
	 * 작성완료 시간
	 */
	private String completeDatetime;

	/**
	 * 재작성 유무
	 */
	private String rewriteYn;

	/**
	 * 재작성 RID
	 */
	private Long rewriteConsentMstRid;

	/**
	 * 
	 */
	private String reissueYn;

	/**
	 * 
	 */
	private Long reissueConsentMstRid;

	/**
	 * 사용여부
	 */
	private String useYn;

	/**
	 * 
	 */
	private String reasonForUseN;

	/**
	 * 시술의 적용유무
	 */
	private String opdrYn;

	/**
	 * 시술의 완료여부
	 */
	private String opdrSignYn;

	/**
	 * 호출한 사용자 아이디
	 */
	private String userId;

	/**
	 * 호출한 사용자 명
	 */
	private String userName;

	/**
	 * 호출한 사용자 부서코드
	 */
	private String userDeptCd;

	/**
	 * 호출한 사용자 부서명
	 */
	private String userDeptName;

	/**
	 * 등록자 아이디
	 */
	private String createUserId;

	/**
	 * 등록자 명
	 */
	private String createUserName;

	/**
	 * 등록자 부서코드
	 */
	private String createUserDeptCd;

	/**
	 * 등록자 부서명
	 */
	private String createUserDeptName;

	/**
	 * 등록일시
	 */
	private String createDatetime;

	/**
	 * 변경자 아이디
	 */
	private String modifyUserId;

	/**
	 * 변경자 명
	 */
	private String modifyUserName;

	/**
	 * 변경자 부서코드
	 */
	private String modifyUserDeptCd;

	/**
	 * 변경자 부서명
	 */
	private String modifyUserDeptName;

	/**
	 * 변경일시
	 */
	private String modifyDatetime;

	/**
	 * 코사인 사용자 ID
	 */
	private String cosignUserId;

	/**
	 * 코사인 사용자 명
	 */
	private String cosignUserName;

	/**
	 * 코사인 진료과(병동) 코드
	 */
	private String cosignDeptCode;

	/**
	 * 코사인 진료과(병동) 명
	 */
	private String cosignDeptName;

	/**
	 * 녹취파일 개수
	 */
	private Long recordCnt;

	/**
	 * 출력 시작일시
	 */
	private String startDate;

	/**
	 * 출력 마지막일시
	 */
	private String endDate;

	/**
	 * 검색 조건
	 */
	private String searchMode;

	/**
	 * 코사인 여부
	 */
	private String cosignYn;

	/**
	 * 내동의서 전용 여부
	 */
	private String fvrtYn;

	/**
	 * 간호사 전자인증 가능 여부
	 */
	private String nursCertYn;

	/**
	 * 전자인증된 개수
	 */
	private Integer certCnt;

	/**
	 * 최종완료를 위한 전자인증 필요개수
	 */
	private Integer certNeedCnt;

	/**
	 * 연명서식 속성
	 */
	private String lifelongKind;

	/**
	 * 인증저장 여부
	 */
	private boolean save;

	public Long getConsentMstRid() {
		return consentMstRid;
	}

	public void setConsentMstRid(Long consentMstRid) {
		this.consentMstRid = consentMstRid;
	}

	public String getHosType() {
		return hosType;
	}

	public void setHosType(String hosType) {
		this.hosType = hosType;
	}

	public String getPatientCode() {
		return patientCode;
	}

	public void setPatientCode(String patientCode) {
		this.patientCode = patientCode;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getVisitType() {
		return visitType;
	}

	public void setVisitType(String visitType) {
		this.visitType = visitType;
	}

	public String getClnDate() {
		return clnDate;
	}

	public void setClnDate(String clnDate) {
		this.clnDate = clnDate;
	}

	public String getClnDeptCd() {
		return clnDeptCd;
	}

	public void setClnDeptCd(String clnDeptCd) {
		this.clnDeptCd = clnDeptCd;
	}

	public String getWardCd() {
		return wardCd;
	}

	public void setWardCd(String wardCd) {
		this.wardCd = wardCd;
	}

	public String getWardName() {
		return wardName;
	}

	public void setWardName(String wardName) {
		this.wardName = wardName;
	}

	public String getRoomCd() {
		return roomCd;
	}

	public void setRoomCd(String roomCd) {
		this.roomCd = roomCd;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public Long getFormVersion() {
		return formVersion;
	}

	public void setFormVersion(Long formVersion) {
		this.formVersion = formVersion;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getOrderCd() {
		return orderCd;
	}

	public void setOrderCd(String orderCd) {
		this.orderCd = orderCd;
	}

	public String getOrderSeqNo() {
		return orderSeqNo;
	}

	public void setOrderSeqNo(String orderSeqNo) {
		this.orderSeqNo = orderSeqNo;
	}

	public String getOrderName() {
		return orderName;
	}

	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}

	public String getOrderDiv() {
		return orderDiv;
	}

	public void setOrderDiv(String orderDiv) {
		this.orderDiv = orderDiv;
	}

	public String getConsentState() {
		return consentState;
	}

	public void setConsentState(String consentState) {
		this.consentState = consentState;
	}

	public String getConsentStateDisp() {
		return consentStateDisp;
	}

	public void setConsentStateDisp(String consentStateDisp) {
		this.consentStateDisp = consentStateDisp;
	}

	public String getCompleteYn() {
		return completeYn == null ? "" : completeYn;
	}

	public void setCompleteYn(String completeYn) {
		this.completeYn = completeYn;
	}

	public String getCompleteDatetime() {
		return completeDatetime;
	}

	public void setCompleteDatetime(String completeDatetime) {
		this.completeDatetime = completeDatetime;
	}

	public String getRewriteYn() {
		return rewriteYn;
	}

	public void setRewriteYn(String rewriteYn) {
		this.rewriteYn = rewriteYn;
	}

	public Long getRewriteConsentMstRid() {
		return rewriteConsentMstRid;
	}

	public void setRewriteConsentMstRid(Long rewriteConsentMstRid) {
		this.rewriteConsentMstRid = rewriteConsentMstRid;
	}

	public String getReissueYn() {
		return reissueYn;
	}

	public void setReissueYn(String reissueYn) {
		this.reissueYn = reissueYn;
	}

	public Long getReissueConsentMstRid() {
		return reissueConsentMstRid;
	}

	public void setReissueConsentMstRid(Long reissueConsentMstRid) {
		this.reissueConsentMstRid = reissueConsentMstRid;
	}

	public String getUseYn() {
		return useYn;
	}

	public void setUseYn(String useYn) {
		this.useYn = useYn;
	}

	public String getReasonForUseN() {
		return reasonForUseN;
	}

	public void setReasonForUseN(String reasonForUseN) {
		this.reasonForUseN = reasonForUseN;
	}

	public String getOpdrYn() {
		return opdrYn;
	}

	public void setOpdrYn(String opdrYn) {
		this.opdrYn = opdrYn;
	}

	public String getOpdrSignYn() {
		return opdrSignYn;
	}

	public void setOpdrSignYn(String opdrSignYn) {
		this.opdrSignYn = opdrSignYn;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getCreateUserDeptCd() {
		return createUserDeptCd;
	}

	public void setCreateUserDeptCd(String createUserDeptCd) {
		this.createUserDeptCd = createUserDeptCd;
	}

	public String getCreateUserDeptName() {
		return createUserDeptName;
	}

	public void setCreateUserDeptName(String createUserDeptName) {
		this.createUserDeptName = createUserDeptName;
	}

	public String getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	public String getModifyUserId() {
		return modifyUserId;
	}

	public void setModifyUserId(String modifyUserId) {
		this.modifyUserId = modifyUserId;
	}

	public String getModifyUserName() {
		return modifyUserName;
	}

	public void setModifyUserName(String modifyUserName) {
		this.modifyUserName = modifyUserName;
	}

	public String getModifyUserDeptCd() {
		return modifyUserDeptCd;
	}

	public void setModifyUserDeptCd(String modifyUserDeptCd) {
		this.modifyUserDeptCd = modifyUserDeptCd;
	}

	public String getModifyUserDeptName() {
		return modifyUserDeptName;
	}

	public void setModifyUserDeptName(String modifyUserDeptName) {
		this.modifyUserDeptName = modifyUserDeptName;
	}

	public String getModifyDatetime() {
		return modifyDatetime;
	}

	public void setModifyDatetime(String modifyDatetime) {
		this.modifyDatetime = modifyDatetime;
	}

	public String getCosignUserId() {
		return cosignUserId;
	}

	public void setCosignUserId(String cosignUserId) {
		this.cosignUserId = cosignUserId;
	}

	public String getCosignUserName() {
		return cosignUserName;
	}

	public void setCosignUserName(String cosignUserName) {
		this.cosignUserName = cosignUserName;
	}

	public String getCosignDeptCode() {

		try {
			Long.parseLong(cosignDeptCode);
		} catch (Exception e) {
			return null;
		}

		return cosignDeptCode;
	}

	public void setCosignDeptCode(String cosignDeptCode) {
		this.cosignDeptCode = cosignDeptCode;
	}

	public String getCosignDeptName() {
		return cosignDeptName;
	}

	public void setCosignDeptName(String cosignDeptName) {
		this.cosignDeptName = cosignDeptName;
	}

	public Long getRecordCnt() {
		return recordCnt;
	}

	public void setRecordCnt(Long recordCnt) {
		this.recordCnt = recordCnt;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getSearchMode() {
		return searchMode;
	}

	public void setSearchMode(String searchMode) {
		this.searchMode = searchMode;
	}

	public String getInstCd() {
		return instCd;
	}

	public void setInstCd(String instCd) {
		this.instCd = instCd;
	}

	public String getCosignYn() {
		return cosignYn;
	}

	public void setCosignYn(String cosignYn) {
		this.cosignYn = cosignYn;
	}

	public String getOcrTag() {
		return ocrTag;
	}

	public void setOcrTag(String ocrTag) {
		this.ocrTag = ocrTag;
	}

	public Long getCertNo() {
		return certNo;
	}

	public void setCertNo(Long certNo) {
		this.certNo = certNo;
	}

	public String getFormCd() {
		return formCd;
	}

	public void setFormCd(String formCd) {
		this.formCd = formCd;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserDeptCd() {
		return userDeptCd;
	}

	public void setUserDeptCd(String userDeptCd) {
		this.userDeptCd = userDeptCd;
	}

	public String getUserDeptName() {
		return userDeptName;
	}

	public void setUserDeptName(String userDeptName) {
		this.userDeptName = userDeptName;
	}

	public boolean getIsFormMaster() {

		boolean result = false;

		if (userDeptCd == null) {
			return result;
		}

		// 의무기록팀일 경우
		if (userDeptCd.startsWith("323")) {
			result = true;
		}

		return result;

	}

	public String getFvrtYn() {
		return fvrtYn;
	}

	public void setFvrtYn(String fvrtYn) {
		this.fvrtYn = fvrtYn;
	}

	public String getPatientAge() {
		return patientAge;
	}

	public void setPatientAge(String patientAge) {
		this.patientAge = patientAge;
	}

	public String getPatientSex() {
		return patientSex;
	}

	public void setPatientSex(String patientSex) {
		this.patientSex = patientSex;
	}

	public String getNursCertYn() {
		return nursCertYn;
	}

	public void setNursCertYn(String nursCertYn) {
		this.nursCertYn = nursCertYn;
	}

	public String getOrdDrNm() {
		return ordDrNm;
	}

	public void setOrdDrNm(String ordDrNm) {
		this.ordDrNm = ordDrNm;
	}

	public String getClnDeptNm() {
		return clnDeptNm;
	}

	public void setClnDeptNm(String clnDeptNm) {
		this.clnDeptNm = clnDeptNm;
	}

	public String getAtDoctId() {
		return atDoctId;
	}

	public void setAtDoctId(String atDoctId) {
		this.atDoctId = atDoctId;
	}

	public Integer getCertCnt() {
		return certCnt;
	}

	public void setCertCnt(Integer certCnt) {
		this.certCnt = certCnt;
	}

	public Integer getCertNeedCnt() {
		return certNeedCnt;
	}

	public void setCertNeedCnt(Integer certNeedCnt) {
		this.certNeedCnt = certNeedCnt;
	}

	public boolean isSave() {
		return save;
	}

	public void setSave(boolean save) {
		this.save = save;
	}

	public String getLifelongKind() {
		return lifelongKind;
	}

	public void setLifelongKind(String lifelongKind) {
		this.lifelongKind = lifelongKind;
	}


}
