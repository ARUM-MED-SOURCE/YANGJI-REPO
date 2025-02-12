var INTERFACE = {};

//INTERFACE.TYPE = "DEV";
INTERFACE.TYPE = "REAL";

INTERFACE.USER = "";
//INTERFACE.USER = "TEST";	// 테스트용  : 로그인 기본 셋팅, 공인인증서 해제 
INTERFACE.USER = "REAL"; // 운영서버

// alert 로그 사용 여부 
//INTERFACE.IS_ALTER = "TRUE";
INTERFACE.IS_ALTER = "FALSE";

// clip 로그 사용 여부 
//INTERFACE.IS_CONSOLE_LOG = "TRUE";                   
INTERFACE.IS_CONSOLE_LOG = "FALSE";
                                                                                                                                                                                         
/* Web 테스트 용 개발서버 */
INTERFACE.CONFIG = {
    'CONNECTION_TYPE'	:	'http'
    ,'CONNECTION_URL'	:	''
    ,'CONNECTION_PORT'	:	'8088/KCCH'
};

if(INTERFACE.TYPE == "DEV"){
	INTERFACE.CONFIG.CONNECTION_URL = '163.152.145.196';
}else{
	INTERFACE.CONFIG.CONNECTION_URL = '163.152.145.196';
}



/* 운영서버 */
//INTERFACE.CONFIG = {
//    'CONNECTION_TYPE'	:	'',
//    'CONNECTION_URL'	:	'',
//    'CONNECTION_PORT'	:	'',
//    'CONTEXT_URL'		:	'',
//    'USER_ID'			:	'',
//    
//    'WEB_PORT' 		:	'',
//    'FILE_PORT' 		:	'',
//    
//    'ATTACH_SERVER_URL' :	'',
//    'ATTACH_PORT' 	:	'',
//    'EXTER_URL_IMAGE'	: 	''
//};