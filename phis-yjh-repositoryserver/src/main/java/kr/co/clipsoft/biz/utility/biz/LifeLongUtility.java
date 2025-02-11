package kr.co.clipsoft.biz.utility.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.co.clipsoft.biz.exception.BizErrorInfo;
import kr.co.clipsoft.biz.exception.BusinessException;
import kr.co.clipsoft.biz.model.consent.NuConsentDataDto;
import kr.co.clipsoft.biz.model.consent.NuConsentFormExDto;
import kr.co.clipsoft.biz.model.consent.NuConsentMstDto;
import kr.co.clipsoft.biz.utility.xml.DataXmlUtility;

/**
 * 연명서식 프로세스 로직 관리
 * 
 *
 *
 */

@Component
public class LifeLongUtility {

	/**
	 * 설정된 연명서식 정보
	 */
	private final Map<String, LifeLongForm> lifeLongForms;

	@Autowired
	private DataXmlUtility dataXmlUtility;

	/**
	 * 프로세스 동작을 위한 연명서식 종류별로 속성 지정
	 */
	public LifeLongUtility() {

		lifeLongForms = new HashMap<String, LifeLongForm>();

		// 1. 연명서식 종류별 초기화
		LifeLongForm form_1 = new LifeLongForm(LifeLongForm.LIFE_LONG_FORM_TYPE_01);
		LifeLongForm form_9 = new LifeLongForm(LifeLongForm.LIFE_LONG_FORM_TYPE_09);
		LifeLongForm form_10 = new LifeLongForm(LifeLongForm.LIFE_LONG_FORM_TYPE_10);
		LifeLongForm form_11 = new LifeLongForm(LifeLongForm.LIFE_LONG_FORM_TYPE_11);
		LifeLongForm form_12 = new LifeLongForm(LifeLongForm.LIFE_LONG_FORM_TYPE_12);
		LifeLongForm form_13 = new LifeLongForm(LifeLongForm.LIFE_LONG_FORM_TYPE_13);

		// 1-1. 연명서식 리스트에 초기화된 서식정보 적용
		lifeLongForms.put(form_1.getFormNo(), form_1);
		lifeLongForms.put(form_9.getFormNo(), form_9);
		lifeLongForms.put(form_10.getFormNo(), form_10);
		lifeLongForms.put(form_11.getFormNo(), form_11);
		lifeLongForms.put(form_12.getFormNo(), form_12);
		lifeLongForms.put(form_13.getFormNo(), form_13);

		// 2. 13호 서식 뷰어호출 제어 적용
		form_13.setControlPrecedOpenView(true);

		// 2-1. 뷰어호출 제어 조건 그룹 초기화
		List<LifeLongForm> precedingOpenViewFormGroup1 = new ArrayList<LifeLongForm>();
//		List<LifeLongForm> precedingOpenViewFormGroup2 = new ArrayList<LifeLongForm>();
		form_13.addPrecedingOpenViewFormGroup(precedingOpenViewFormGroup1);
//		form_13.addPrecedingOpenViewFormGroup(precedingOpenViewFormGroup2);

		// 2-2. 뷰어호출 제어 조건 그룹 대상 서식 적용
		// 2020.04.10 의료윤리행정팀 요청사항으로 조건 제거
		precedingOpenViewFormGroup1.add(form_9);
//		precedingOpenViewFormGroup2.add(form_1);
//		precedingOpenViewFormGroup2.add(form_10);
//		precedingOpenViewFormGroup2.add(form_11);
//		precedingOpenViewFormGroup2.add(form_12);

		// 3. 10,11,12호 서식 전자인증 제어 적용
		form_10.setControlPrecedSaveView(true);
		form_11.setControlPrecedSaveView(true);
		form_12.setControlPrecedSaveView(true);

		// 3-1. 전자인증 제어 조건 서식 적용
		form_10.setPrecedingSaveViewForm(form_9);
		form_11.setPrecedingSaveViewForm(form_9);
		form_12.setPrecedingSaveViewForm(form_9);

	}

	/**
	 * 뷰어 호출이 가능한지 판단
	 * 
	 * @param formExDto 호출하려는 서식 확장속성 정보
	 * @param mstDtos   해당 환자의 작성된 동의서 리스트
	 * @return 호출가능 여부
	 */

	public boolean isPossibleViewOpen(NuConsentFormExDto formExDto, List<NuConsentMstDto> mstDtos) {

		// 1. 서식확장속성 유효성 검사
		if (formExDto == null) {
			throw new BusinessException(BizErrorInfo.NO_FORM_EX_DATA);
		}

		// 2. 연명서식 속성 유무 확인
		if (formExDto.getLifelongKind() == null || formExDto.getLifelongKind().isEmpty()) {
			// 연명서식이 아니기 때문에 작성가능
			return true;
		}

		// 3. 지정된 연명서식 유무 유효성 검사
		if (!lifeLongForms.containsKey(formExDto.getLifelongKind())) {
			throw new BusinessException(BizErrorInfo.NO_KIND_LIFE_LONG_FORM);
		}

		// 4. 작성하려는 연명서식 정보 가져오기
		LifeLongForm lifeLongForm = lifeLongForms.get(formExDto.getLifelongKind());

		// 5. 작성된 동의서에서 호출하려는 연명서식의 선행서식들이 각각 전자인증이 최소 1번 이상이 되어있는지 판단
		if (!validationCheckBizCase1(lifeLongForm, mstDtos)) {
			throw new BusinessException(BizErrorInfo.NO_SAVE_LIFE_LONG_FORM_CASE1);
		}

		return true;
	}

	/**
	 * 뷰어 저장이 가능한지 판단
	 * 
	 * @param mstDtos   해당 환자의 작성된 동의서 정보 리스트
	 * @param dataDto   저장 동의서 dataXml 정보
	 * @param formExDto 호출되는 서식 확장속성 정보
	 * @return 뷰어 저장이 가능여부
	 */
	public boolean isPossibleViewSave(List<NuConsentMstDto> mstDtos, NuConsentDataDto dataDto, NuConsentFormExDto formExDto) {

		// 1. 서식확장속성 유효성 검사
		if (formExDto == null) {
			throw new BusinessException(BizErrorInfo.NO_FORM_EX_DATA);
		}

		// 2. 연명서식 속성 유무 확인
		if (formExDto.getLifelongKind() == null || formExDto.getLifelongKind().isEmpty()) {
			// 연명서식이 아니기 때문에 작성가능
			return true;
		}

		// 3. 등록된 연명서식 유효성 검사
		if (!lifeLongForms.containsKey(formExDto.getLifelongKind())) {
			throw new BusinessException(BizErrorInfo.NO_KIND_LIFE_LONG_FORM);
		}

		// 4. 저장하려는 연명서식 정보 가져오기
		LifeLongForm lifeLongForm = lifeLongForms.get(formExDto.getLifelongKind());

		// 5. 선행인증저장 체크 대상 여부 판단
		if (!validationCheckBizCase2(lifeLongForm, mstDtos)) {
			throw new BusinessException(BizErrorInfo.NO_SAVE_LIFE_LONG_FORM_CASE2);
		}

		// 6. 담당의사와 전문의 동일 여부 판단
		if (!validationCheckBizCase3(dataDto)) {
			throw new BusinessException(BizErrorInfo.NO_SAVE_LIFE_LONG_FORM_CASE3);
		}

		return true;
	}

	/**
	 * 다음 주치의 지정 가능여부 반환
	 * 
	 * @param certCnt   현재 전자인증 개수
	 * @param formExDto 서식 확장속성
	 * @return 다음 주치의 지정 가능여부
	 */
	public boolean isPossibleSetNextAtDocId(int certCnt, NuConsentFormExDto formExDto) {

		// 1. 전자인증이 1개가 아니면 다음 주치의 지정 취소
		if (certCnt != 1) {
			return false;
		}

		// 2. 서식확장속성 유효성 검사
		if (formExDto == null) {
			throw new BusinessException(BizErrorInfo.NO_FORM_EX_DATA);
		}

		// 3. 연명서식 속성 유무 확인
		if (formExDto.getLifelongKind() == null || formExDto.getLifelongKind().isEmpty()) {
			return false;
		}

		// 4. 등록된 연명서식 유효성 검사
		if (lifeLongForms.containsKey(formExDto.getLifelongKind())) {
			return true;
		}

		return false;
	}

	/**
	 * 환자가 작성한 동의서 중 연명서식 정보 가져오기
	 * 
	 * @param mstDtos 환자가 작성한 동의서 리스트
	 * @return 연명서식 정보 리스트
	 */
	private Map<String, LifeLongForm> getWrtiedLifeLongForms(List<NuConsentMstDto> mstDtos) {

		Map<String, LifeLongForm> wrtiedLifeLongForms = new HashMap<String, LifeLongForm>();

		if (mstDtos == null) {
			return wrtiedLifeLongForms;
		}

		for (NuConsentMstDto nuConsentMstDto : mstDtos) {

			if (!lifeLongForms.containsKey(nuConsentMstDto.getLifelongKind())) {
				continue;
			}

			LifeLongForm form = new LifeLongForm(nuConsentMstDto.getLifelongKind());
			form.setCertCnt(nuConsentMstDto.getCertCnt());
			wrtiedLifeLongForms.put(form.getFormNo(), form);
		}

		return wrtiedLifeLongForms;
	}

	/**
	 * 작성된 동의서에서 호출하려는 연명서식의 선행서식들이 각각 전자인증이 최소 1번 이상이 되어있는지 판단
	 * 
	 * @param lifeLongForm 호출하려는 연명서식 정보
	 * @param mstDtos      환자가 작성한 동의서 리스트
	 * @return 호출가능 여부
	 */
	private boolean validationCheckBizCase1(LifeLongForm lifeLongForm, List<NuConsentMstDto> mstDtos) {

		// 1. 뷰어호출 제어 서식인지 판단
		if (!lifeLongForm.isControlPrecedOpenView()) {
			return true;
		}

		// 2. 전자인증(최소 1번이상)된 선행서식 그룹 카운트
		int preGroupSaveCnt = 0;

		// 3. 환자가 작성한 서식 리스트 가져오기
		Map<String, LifeLongForm> wrtiedLifeLongForms = getWrtiedLifeLongForms(mstDtos);

		// 4. 선행서식 그룹 탐색
		// 뷰어 호출이 되기 위해서는 9호 서식이 최소 1번 이상 인증저장이 되어야 한다.
		for (List<LifeLongForm> precedingOpenViewFormGroup : lifeLongForm.getPrecedingOpenViewFormGroup()) {

			// 4-1. 선행서식 그룹내 서식 리스트 탐색
			for (LifeLongForm preOpenViewForm : precedingOpenViewFormGroup) {

				// 4-2. 작성한 동의서중 대상 선행서식 유무 판단
				if (!wrtiedLifeLongForms.containsKey(preOpenViewForm.getFormNo())) {
					continue;
				}

				// 4-3. 작성한 동의서중 대상 선행서식 정보 가져오기
				LifeLongForm writeForm = wrtiedLifeLongForms.get(preOpenViewForm.getFormNo());

				// 4-4. 작성한 선행서식이 전자인증이 1번 이상인지 판단
				if (writeForm.getCertCnt() > 0) {
					preGroupSaveCnt++;
					break;
				}

			}

		}

		// 5. 전자인증 그룹 카운트가 그룹개수 이상 인증이 되어있으면 호출 가능
		if (preGroupSaveCnt < lifeLongForm.getPrecedingOpenViewFormGroup().size()) {
			return false;

		}

		return true;
	}

	/**
	 * 저장하려는 동의서의 선행인증저장 서식이 존재(인증저장여부)하는지 체크
	 * 
	 * @param lifeLongForm 저장하려는 연명서식 정보
	 * @param mstDtos      해당 환자의 작성된 동의서 정보 리스트
	 * @return true : 선행인증저장 서식 존재, false : 선행인증저장 서식 없음
	 */
	private boolean validationCheckBizCase2(LifeLongForm lifeLongForm, List<NuConsentMstDto> mstDtos) {

		if (!lifeLongForm.isControlPrecedSaveView()) {
			return true;
		}

		// 저장하려는 서식의 선행 전자인증 서식정보
		LifeLongForm preSaveViewForm = lifeLongForm.getPrecedingSaveViewForm();

		// 환자가 작성한 동의서 정보 리스트
		Map<String, LifeLongForm> wrtiedLifeLongForms = getWrtiedLifeLongForms(mstDtos);

		// 작성한 동의서중 대상 저장하려는 동의서 선행서식 존재 유무 판단
		if (!wrtiedLifeLongForms.containsKey(preSaveViewForm.getFormNo())) {
			return false;
		}

		// 작성한 동의서중 현재 저장하려는 선행서식 정보 가져오기
		LifeLongForm writeForm = wrtiedLifeLongForms.get(preSaveViewForm.getFormNo());

		// 선행서식이 전자인증이 1번 이상인지 판단
		if (writeForm.getCertCnt() <= 0) {
			return false;
		}

		return true;
	}

	/**
	 * 담당의사와 전문의 ID 동일 여부 판단
	 * 
	 * @param dataDto 저장 동의서 dataXml 정보
	 * @return true : 담당의사, 전문의 ID 같거나 데이터 존재하지 않음, false : 담당의사, 전문의 ID 다름
	 */
	private boolean validationCheckBizCase3(NuConsentDataDto dataDto) {

		String firSignUesrId = dataXmlUtility.getFormDataFieldValue(dataDto.getDataXml(), DataXmlUtility.DATA_FIELD_FIR_SIGN_USERID, null);
		String secSignUesrId = dataXmlUtility.getFormDataFieldValue(dataDto.getDataXml(), DataXmlUtility.DATA_FIELD_SEC_SIGN_USERID, null);

		// 데이터 존재유무 판단
		if (firSignUesrId == null || secSignUesrId == null) {
			return true;
		}

		if (firSignUesrId.trim().equals(secSignUesrId.trim())) {
			return false;
		}

		return true;
	}

	/**
	 * 연명서식 정보 관리
	 *
	 *
	 */
	class LifeLongForm {

		/**
		 * 연명의료계획서(1호)
		 */
		public static final String LIFE_LONG_FORM_TYPE_01 = "1";

		/**
		 * 임종과정에 있는 환자 판단서-연명(9호)
		 */
		public static final String LIFE_LONG_FORM_TYPE_09 = "9";

		/**
		 * 연명의료중단등결정에 대한 환자의사 확인서(10호)
		 */
		public static final String LIFE_LONG_FORM_TYPE_10 = "10";

		/**
		 * 연명의료중단등결정에 대한 환자의사 확인서(11호)
		 */
		public static final String LIFE_LONG_FORM_TYPE_11 = "11";

		/**
		 * 연명의료중단등결정에 대한 친권자 및 환자가족 의사 확인서(12호)
		 */
		public static final String LIFE_LONG_FORM_TYPE_12 = "12";

		/**
		 * 연명의료중단등결정 이행서(13호)
		 */
		public static final String LIFE_LONG_FORM_TYPE_13 = "13";

		/**
		 * 연명서식 번호
		 */
		private String formNo;

		/**
		 * 전자인증된 개수
		 */
		private int certCnt;

		/**
		 * 선행 전자인증 서식 결과에 따라 뷰어호출 제어 여부
		 */
		private boolean controlPrecedOpenView;

		/**
		 * 뷰어호출 제어 조건 서식 그룹정보
		 */
		private final List<List<LifeLongForm>> precedingOpenViewFormGroup;

		/**
		 * 선행 전자인증 서식 결과에 따라 전자인증 제어 여부
		 */
		private boolean controlPrecedSaveView;

		/**
		 * 전자인증 제어 조건 서식 정보
		 */
		private LifeLongForm precedingSaveViewForm;

		public LifeLongForm(String formNo) {
			this.formNo = formNo;
			this.certCnt = 0;
			this.precedingOpenViewFormGroup = new ArrayList<List<LifeLongForm>>();
			this.controlPrecedOpenView = false;
			this.controlPrecedSaveView = false;
		}

		public void addPrecedingOpenViewFormGroup(List<LifeLongForm> precedingOpenViewFormGroup) {
			this.precedingOpenViewFormGroup.add(precedingOpenViewFormGroup);
		}

		public String getFormNo() {
			return formNo;
		}

		public int getCertCnt() {
			return certCnt;
		}

		public void setCertCnt(int certCnt) {
			this.certCnt = certCnt;
		}

		public boolean isControlPrecedOpenView() {
			return controlPrecedOpenView;
		}

		public void setControlPrecedOpenView(boolean controlPrecedOpenView) {
			this.controlPrecedOpenView = controlPrecedOpenView;
		}

		public boolean isControlPrecedSaveView() {
			return controlPrecedSaveView;
		}

		public void setControlPrecedSaveView(boolean controlPrecedSaveView) {
			this.controlPrecedSaveView = controlPrecedSaveView;
		}

		public List<List<LifeLongForm>> getPrecedingOpenViewFormGroup() {
			return precedingOpenViewFormGroup;
		}

		public LifeLongForm getPrecedingSaveViewForm() {
			return precedingSaveViewForm;
		}

		public void setPrecedingSaveViewForm(LifeLongForm form) {
			this.precedingSaveViewForm = form;
		}

	}

}
