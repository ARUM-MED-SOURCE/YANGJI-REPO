//
//  sf_phonegap_plugin_echo.js
//
//  Created by SungKwang Song on 3/12/14.
//
//

// 인증서 전체삭제해줘야함
function ITnadePlugin() {} //1
ITnadePlugin.prototype.setConnect = function (ip, port,id,pw) { // KMI 접속
  var callbackSuccess = function (result) {   
    if (result == "ok") {
      ITnadePlugin.prototype.getKeyAndCert(id,pw); 
    } else {
      alert("인증서 서버 접속에 실패하였습니다.\n관리자에게 문의바랍니다.")
    }   
  };

  var callbackFail = function (error) {
	    alert("ip : " + ip + " / port : " + port +"인증서를 내려받는 도중에 에러가 발생했습니다. setConnect Exception\n" + error.toString());
  };
  cordova.exec(callbackSuccess, callbackFail, "ClipsoftPlugin", "setConnect", [ip, port]);
};


ITnadePlugin.prototype.getKeyAndCert = function (id,pw) { // DN값가져오기 
  var callbackSuccess = function (result) {
    localStorage.setItem("userdn", result.dn); 
    ITnadePlugin.prototype.setDisconnect(id,pw);
  };

  var callbackFail = function (error) {
	    alert(error +" DN인증서를 내려받는 도중에 에러가 발생했습니다. getKeyAndCert Exception");
  };
  cordova.exec(callbackSuccess, callbackFail, "ClipsoftPlugin", "getKeyAndCert", [id]);

};


ITnadePlugin.prototype.setDisconnect = function (id,pw) { // KMI 접속해제
  var callbackSuccess = function (result) { 
	  ITnadePlugin.prototype.checkpwd(id,pw);
  };

  var callbackFail = function (error) {
    alert("인증서를 내려받는 도중에 에러가 발생했습니다. setDisconnect Exception");
  };
  cordova.exec(callbackSuccess, callbackFail, "ClipsoftPlugin", "setDisconnect", []);
};


ITnadePlugin.prototype.LocalDelKeyAndCert = function (dn) { 
  var callbackSuccess = function (result) {
    console.log("LocalDelKeyAndCert result : " + result); 
  };

  var callbackFail = function (error) {
	    console.log("LocalDelKeyAndCert error : " + error); 
  };
  cordova.exec(callbackSuccess, callbackFail, "ClipsoftPlugin", "LocalDelKeyAndCert", [dn]);
};

ITnadePlugin.prototype.Error_Msg = function () {
  var callbackSuccess = function (result) {
    alert("result : " + result); 
  };

  var callbackFail = function (error) {
    alert(error + "1");
  };
  cordova.exec(callbackSuccess, callbackFail, "ClipsoftPlugin", "Error_Msg", []);
};


ITnadePlugin.prototype.CertBatchDel = function (dnsuffix) {
  var callbackSuccess = function (result) {
    alert("result : " + result); 
  }; 
  var callbackFail = function (error) {
    alert(error + "1");
  };
  cordova.exec(callbackSuccess, callbackFail, "ClipsoftPlugin", "CertBatchDel", [dnsuffix]);
};

//---------------------------------------------------------------
//                      코스콤모듈시작
//---------------------------------------------------------------


ITnadePlugin.prototype.checkpwd = function (id, password) { //
  var callbackSuccess = function (result) {
    if (result.ret == true) { 
      //ITnadePlugin.prototype.getMessage(id, password); 
        $('#sign_popup').css('display','none'); 
        localStorage.setItem("signPwd", password);
        if(localStorage.getItem("cosignFirst") == "true"){
        	alert("동의서를 다시 선택바랍니다.");
		    if ($('.cosign_out').attr('class').indexOf("on") > -1) {
		          setTimeout(function() {
		              searchCosignSend();
		          }, 500);
		    } else {
		          setTimeout(function() {
		              searchCosign();
		          }, 500);
		     } 
        }else if(localStorage.getItem("myConsentFlag") == "true"){ 
        	alert("동의서를 다시 선택바랍니다.");
        	$('#myConsentSearch').trigger('click');
        }else{ 
        	searchAppVersion();
        }
    } else {
      alert("인증서 패스워드를 확인해주세요.");
    }
  };
  var callbackFail = function (error) {
  };
  cordova.exec(callbackSuccess, callbackFail, "ClipsoftPlugin", "checkPwd", [password]);
};


ITnadePlugin.prototype.getMessage = function(message,password){	//3 	인증로그인 검증
  var callbackSuccess = function(result){
    sessionStorage.setItem('sign', result.name); 
    var id = localStorage.getItem("userId");
    //alert(result.name);
    //var signId = $("#_loginId").val();
    //var signPassWord = $("#_loginPw").val();
    //localStorage.setItem("signId", signId);
    //localStorage.setItem("signPassWord", signPassWord);
    //alert(result.name);
    localStorage.setItem("signvalue", result.name);
    localStorage.setItem("signresult", "ok"); 
    //$('#sign_popup').css('display','none');
    
	//searchAppVersion();
  };
  var callbackFail = function(error){ 
	localStorage.setItem("signresult", "no");
    alert("인증서가 없는 사용자입니다.");
  };
  cordova.exec(callbackSuccess, callbackFail, "ClipsoftPlugin", "getMessage", [message, password]);
}

module.exports = new ITnadePlugin();