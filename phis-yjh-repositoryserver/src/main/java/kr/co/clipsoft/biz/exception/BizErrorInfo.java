package kr.co.clipsoft.biz.exception;

/**
 * 비즈니스 로직 예외 처리 에러코드 관리
 *
 *
 */
public enum BizErrorInfo {

	NO_FORM_EX_DATA("SE0001", "서식 속성정보를 찾을 수 없습니다.", ""), 
	NO_KIND_LIFE_LONG_FORM("SE0002", "대상 서식의 연명의료 속성이 잘못 지정되어있습니다.", ""),
	NO_REQUEST_PARAM("SE003", "요청 데이터가 명확하지 않습니다.\r\n관리자에게 문의해주시기 바랍니다.", ""),
	NO_SAVE_LIFE_LONG_FORM_CASE1("SE004", "임종과정에 있는 환자 판단서(9호)가 인증저장되어야 작성이 가능합니다.", ""),
	NO_SAVE_LIFE_LONG_FORM_CASE2("SE005", "임종과정에 있는 환자 판단서(9호)가 인증저장되어야 작성이 가능합니다.", ""),
	NO_SAVE_LIFE_LONG_FORM_CASE3("SE006", "담당의사와 전문의는 동일한 의사가 작성할 수 없습니다.", "담당의사와 전문의 ID가 동일합니다."),
	ERROR_CERT_NEED_CNT("SE007", "서식에 지정된 전자인증 필요 개수가 잘못되었습니다.", ""), 
	NO_CERT_DATA("SE008", "전자인증 값이 누락되어있습니다.", ""),
	ERROR_IMAGE_UPLOAD_NAS("SE009", "저장중 서버에서 오류가 발생하였습니다.\r\n관리자에게 문의해주시기 바랍니다.", "NAS에 이미지업로드에 오류가 발생하였습니다."),
	ERROR_VIEW_SAVE_DB_TRANSACTION("SE010", "저장중 서버에서 오류가 발생하였습니다.\r\n관리자에게 문의해주시기 바랍니다.", "뷰어 저장 데이터 트랜젝션 처리에 오류가 발생하였습니다."),
	ERROR_JSON_SETTING("SE011", "서버에서 오류가 발생하였습니다.\r\n관리자에게 문의해주시기 바랍니다.", "서버에서 JSON 데이터 설정중 오류가 발생하였습니다."),
	ERROR_CREATE_IMAGE_ZIP("SE012", "저장중 서버에서 오류가 발생하였습니다.\r\n관리자에게 문의해주시기 바랍니다.", "이미지 압축파일 생성중 오류가 발생하였습니다."),
	ERROR_GET_FORM_DATA_FIELD_LIST("SE013", "저장중 서버에서 오류가 발생하였습니다.\r\n관리자에게 문의해주시기 바랍니다.", "저장데이터 필드 추출중 오류가 발생하였습니다."),
	UNDEFINE_FORM_SAVE_STATUS("SE014", "저장중 서버에서 오류가 발생하였습니다.\r\n관리자에게 문의해주시기 바랍니다.", "저장상태값이 명확하지 않습니다."),
	NO_REQUEST_PARAM_IMAGE_FILES("SE015", "저장중 서버에서 오류가 발생하였습니다.\r\n관리자에게 문의해주시기 바랍니다.", "업로드 대상 이미지 파일이 없습니다."),
	NO_HAVE_LCTECH_INTERFACE_DATA("SE016", "저장중 서버에서 오류가 발생하였습니다.\r\n관리자에게 문의해주시기 바랍니다.", "LC테크 연동데이터가 존재하지 않습니다. 이미지 업로드 확인이 필요합니다."),
	NO_DATA_XML("SE017", "서버에서 오류가 발생하였습니다.\r\n관리자에게 문의해주시기 바랍니다.", "DataXml 데이터가 존재하지 않습니다."), 
	ERROR_PARSER_DATA_XML("SE018", "서버에서 오류가 발생하였습니다.\r\n관리자에게 문의해주시기 바랍니다.", "DataXml 파싱중 오류가 발생하였습니다."),
	NO_UPDATE_DB_DATA("SE019", "데이터 정상처리(update,delete)건이 발생하지 않았습니다.", ""),
	NO_MODIFYDATETIME_DATA("SE020", "저장에 필요한 데이터가 누락되었습니다.\r\n(과거 수정시간)\r\n관리자에게 문의해주시기 바랍니다.", ""),
	ERROR_ALREADY_SAVE("SE021", "다른 단말기에서 이미 저장을 진행하였습니다.\r\n작성동의서를 다시 조회후 재작성 바랍니다.", ""),
	ERROR_USER_NM_DIFFERENT("SE022", "의사,간호사 서명란에는 로그인 사용자명이 입력되어있어야 합니다.", ""),
	ERROR_MAX_UPLOAD_SIZE("SE023", "저장 정보 용량이 초과되었습니다.\r\n첨지 또는 카메라 이미지 제거 후 다시 저장해주시기 바랍니다.", ""),
	NO_FORM_XML("SE024", "서버에서 오류가 발생하였습니다.\r\n관리자에게 문의해주시기 바랍니다.", "FormXml 데이터가 존재하지 않습니다.");

	private String code;
	private String msg;
	private String msg2;

	BizErrorInfo(String code, String msg, String msg2) {
		this.code = code;
		this.msg = msg;
		this.msg2 = msg2;
	}

	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public String getMsg2() {
		return msg2;
	}

}
