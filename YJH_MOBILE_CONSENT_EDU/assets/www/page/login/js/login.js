$(document).ready(function () { 
	params = new Object();
	isRun = false;
	deviceId = "";
	var ajax_url ="";
	var HIS_EPH = ""; 
	
	
	if (INTERFACE.USER != "") {
		if (INTERFACE.TYPE == "REAL") { 
			ajax_url = "http://emr.yjh.com/cmcnu/.live";
			HIS_EPH = "HIS204";
		} else {
			ajax_url = "http://emrdev.yjh.com/cmcnu/.live";
			HIS_EPH = "HIS204EDU"; 
		}
	} 
	

	
	$('#notice_btn').click(function () {
		$('#notice_layout').css('display', 'block');
		$('#_btnClose').css('display', 'none');
	})

	$('#help_btn').click(function () {
		$('#help_layout').css('display', 'block');
		$('#_btnClose').css('display', 'none');
	})
	$('#helpCloseBtn').click(function () {
		$('#help_layout').css('display', 'none');
		$('#_btnClose').css('display', 'block');

	});


	$('#help_btns').click(function () {
		$('#help_layouts').css('display', 'block');
		for (var i = 1; i <= 35; i++) {
			$('#help_layouts').append('<img src="../../images/menual' + i + '.PNG" alt="" style="width:100%;height:auto; position: initial;" />');
		}
		$('#_btnClose').css('display', 'none');
	})

	$(document).on("click", "#helpCloseBtns", function (e) {
		$('#help_layouts').empty();
		$('#help_layouts').append('<button type="button" id="helpCloseBtns" style="top:45px; position: fixed;    z-index: 10; right: 75px;" class="btn-close"><span>CLOSE</span></button>');

		$('#help_layouts').css('display', 'none');
		$('#_btnClose').css('display', 'block');
	});

	$('#noticeCloseBtn').click(function () {
		$('#notice_layout').css('display', 'none');
		$('#_btnClose').css('display', 'block');
		$('.notice_full_text').each(function (index) {
			$(this).css('display', 'none');
		});
		$('.notice_box').each(function (index) {
			$(this).css('border-top', '0');
			$(this).css('border-right', '0');
			$(this).css('border-left', '0');
		});

	});

	// 공지사항 항목클릭
	$(document).on("click", ".notice_box", function (e) {
		var tag = $(this).next();
		var offset = $(this).offset();
		var tag_visible = tag.css('display');
		$('.notice_full_text').each(function (index) {
			$(this).css('display', 'none');
		});
		$('.notice_box').each(function (index) {
			$(this).css('border-top', '0');
			$(this).css('border-right', '0');
			$(this).css('border-left', '0');
		});
		if (tag_visible == "block") {
			$(this).next().css('display', 'none');
			$(this).css('border-top', '0');
			$(this).css('border-right', '0');
			$(this).css('border-left', '0');
		} else {
			$(this).next().css('display', 'block');
			$(this).css('border-top', 'solid 0.3px blue');
			$(this).css('border-right', 'solid 0.3px blue');
			$(this).css('border-left', 'solid 0.3px blue');
		}
		$('.notice_table_layout').animate({
			scrollTop: offset.top
		}, 400);

	});

	// 네이티브 백 버튼 이벤트 
	COMMON.BACKKEY.addEvent();


	// include전에 cordova.exec() 호출됨.
	if (!COMMON.device.isMobile() && !COMMON.device.isTablet()) {
		onDeviceReady();
	} else {
		document.addEventListener('deviceready', onDeviceReady);
	}
	
	// 종료 버튼 이벤트
	$("#_btnClose").on("click", function () {
		if (confirm("전자동의서 앱을 종료하시겠습니까?")) {
			COMMON.plugin.terminateApp();
			return;
		}
	});

	// 로그인 버튼 이벤트
	$("#_btnLogin").on("click", function () {
		fnLoginConditionCheck();
	});

	// 인증 로그인 버튼 이벤트
	$("#_btnLoginSign").on("click", function () {
		fnSignLoginConditionCheck();
	});

	// 로그인 리스트 닫기
	$(document).on("mousedown.loginClose", function (e) {
		var $target = $(e.target),
			$elem = $(".login_id_box");

		if (!$elem.is(e.target) && $elem.has(e.target).length === 0)
			$(".login_id_list").hide();
	});

	// 최근 로그인 아이디 가져오기 
	$("#_loginId").on("focus", function () {
		if (localStorage.getItem("latelyData") != null && localStorage.getItem("latelyData") != undefined) {
			getLatelyData();
		}
	});

	// 로그인 Id 입력시 엔터 이벤트 
	$("#_loginId").on("keydown", function (e) {
		if (e.keyCode == 13) { //키가 13이면 실행 (엔터는 13)
			$("#_loginPw").trigger("focus");
			$("#_latelyList").hide();
		}
	});

	// 로그인 pw 입력시 엔터 이벤트 
	$("#_loginPw").on("keydown", function (e) {
		if (e.keyCode == 13) { //키가 13이면 실행 (엔터는 13)
			$(this).trigger("blur");
			$("#_btnLogin").trigger("click");
		}
	});

	$('.deptChoice_close').click(function () {
		$('.login_section').css('display', 'block');
		$('.choice_Dept').css('display', 'none');
		$('#_loginId').val("");
		$('#_loginPw').val("");
	});
	$('.deptChoice_enter').click(function () {
		var DeptCode = $("#_userDeptChoice option:selected").val();
		var DeptFullName = $("#_userDeptChoice option:selected").text();
		var DeptName = $("#_userDeptChoice option:selected").attr('class');

		var DataList = [];
		var DataList = DeptName.split("@");
		DeptName = DataList[0];
		var JobKindCd = DataList[1];
		var loginCheck =""; 
		var applyduty = localStorage.getItem("applyduty");
		
		if(applyduty=="Y"){
			$.ajax({
				url: 'http://emrdev.yjh.com/cmcnu/.live',
				type: 'post',
				data: 'submit_id=DRZSU11708&business_id=zz&dutplceinstcd=204&dutplcecd=' + DeptCode + '&userid=' + $('#_loginId').val(),
				dataType: 'xml',
				timeout: 10000,
				async: false,
				success: function (result) {	 
					if ($(result).find('userdutytime').length > 0) {
						$(result).find('userdutytime').each(function () {
							loginCheck = $(this).find('dutyn').text(); 
						}); 
					}  
				},
				error: function (error) {
					isRun = false;
					alert("사용자 사용시간 체크 중 문제가 발생하였습니다. 관리자에게 문의바랍니다.\n에러코드 : " + error.readyState); 
				}

			}) 
			
			// 교육서버기준
			loginCheck="Y";
			
			if(loginCheck=="N"){
				alert("근무시간이 아닙니다. 근무표를 확인하여 주십시오.\n근무시간 외에는 접속이 불가합니다.");
				$('.login_section').css('display', 'block');
				$('.choice_Dept').css('display', 'none');
				$('#_loginId').val("");
				$('#_loginPw').val("");
			}else{ 
				params.userDeptCode = DeptCode;
				params.userDeptName = DeptName;
				params.userDeptFullName = DeptFullName;
				params.jobkindcd = JobKindCd;
				localStorage.setItem("depthngnm", DeptName); 

				fnGoPage(params);
			}
		}else{
			params.userDeptCode = DeptCode;
			params.userDeptName = DeptName;
			params.userDeptFullName = DeptFullName;
			params.jobkindcd = JobKindCd;
			localStorage.setItem("depthngnm", DeptName); 

			fnGoPage(params); 
		}
		
		
	});
	
	$('#_btnSelect').click(function(){
		var instcd = $('#_HospitalList option:selected').val();
		if(instcd == ""){
			alert("기관을 선택해주세요.");
		}else{ 
		    COMMON.plugin.createJSONFile(instcd);
		}
	});
});

// cordova가 준비된 상태
function onDeviceReady() {
 	if (COMMON.device.isMobile() && COMMON.device.isTablet()) {
		COMMON.plugin.WriteJsonFile();
		COMMON.plugin.GetDevicesUUID();
		COMMON.plugin.deleteDnValue();
		COMMON.plugin.checkJSONFile();
	}

	console.log("[ Login onDeviceReady ]");
	COMMON.plugin.storage("delete", "", null);
	COMMON.plugin.storage("get", "CONSENT_APP_VERSION", markConsentAppVersion, errorHandler);
	COMMON.plugin.storage("get", "useCloudServer", setLocalStorageUseCloudServer, errorHandler);
	COMMON.plugin.storage("get", "cloudServerUrl", setLocalStorageCloudServerUrl, errorHandler);
	COMMON.plugin.storage("get", "EFORM_APP_VERSION", markEformAppVersion, errorHandler);
	COMMON.plugin.storage("get", "macAddress", setLocalStorageMacAddress, errorHandler);
	COMMON.plugin.storage("set", {
		"INTERFACE_USER": INTERFACE.USER
	}, null, errorHandler);
	COMMON.plugin.storage("set", {
		"INTERFACE_TYPE": INTERFACE.TYPE
	}, isAppUpdate, errorHandler);
	localStorage.removeItem("userdn");
	localStorage.removeItem("docYN");
	localStorage.removeItem("cosignFirst");
	localStorage.removeItem("signPwd");
	localStorage.removeItem("myConsentFlag");
	localStorage.removeItem("licnsno");
	localStorage.removeItem("ordfild");
	localStorage.removeItem("medispclno");
	localStorage.removeItem("jobkindcd");
	localStorage.removeItem("depthngnm"); 
	localStorage.removeItem("applyduty");  
	
	var checkTime = "";
	$.ajax({
			url: 'http://emrdev.yjh.com/eform' + '/biz/nu/member/viewer/eForm25/consent/nowtime/get',
			type: 'post',
			timeout: 10000,
			async: false,
		}).done(function (data) {
			checkTime = data.nowTime;
			//checkTime = checkTime.substring(0,10);
			COMMON.plugin.compareTime(checkTime);
		})
		.fail(function (xhr, status, errorThrown) {
//			if (confirm("네트워크 통신이 원활하지 않습니다. WIFI를 확인해주세요.\n APP이 종료됩니다.")) {
//				COMMON.plugin.terminateApp();
//			} else {
//				COMMON.plugin.terminateApp();
//			}

		});
	makeInterfaceUser(); // 화면에 정보 표시
};

function setLocalStorageCloudServerUrl(cloudServerUrl) {
	localStorage.setItem("cloudServerUrl", cloudServerUrl);
	//console.log("[ cloudServerUrl ]" + cloudServerUrl);
};

function setLocalStorageUseCloudServer(useCloudServer) {
	localStorage.setItem("useCloudServer", useCloudServer);
};

function setLocalStorageMacAddress(macAddress) {
	localStorage.setItem("MacAddress", macAddress);
};

//	최근 로그인 -  아이디 가져오기 
function getLatelyData() {
	$("#_latelyList ul li").remove();
	var latelyDataList = localStorage.getItem("latelyData").split(",");
	for (var i = 0; i < latelyDataList.length; i++) {
		var latelyData = latelyDataList[i];
		var html = "<li><a href='#'>" + latelyData + "</a></li>";
		$("#_latelyList ul").append(html);
	}
	$("#_latelyList").show();

	// 최근 로그인 항목 선택시 이벤트 
	$("#_latelyList ul li").on("click", function () {
		$("#_latelyList").hide();
		var id = $(this).text();
		$("#_loginId").val(id.trim());
		$("#_loginPw").val("");
		$("#_loginPw").trigger("focus");
	});
};

// 최근 로그인 - 아이디 저장  
function setLatelyData(userInfo) {
	var item = [];
	item.push(userInfo.userId);
	// 최근 로그인 항목들
	var latelyDataList = [];
	if (localStorage.getItem("latelyData") != null && localStorage.getItem("latelyData") != "") {
		latelyDataList = localStorage.getItem("latelyData").split(",");
		var arrangeList = arrange(latelyDataList, item);
		arrayItemToString(arrangeList);
		localStorage.setItem("latelyData", arrangeList.join(","));
	} else {
		arrayItemToString(item);
		localStorage.setItem("latelyData", item.join(","));
	}
}

// 최근 로그인 - 배열 순서  최신순으로 변경하고 중복 제거
function arrange(storageList, selectedList) {
	for (var i = 0; i < selectedList.length; i++) {
		for (var j = 0; j < storageList.length; j++) {
			var userId = selectedList[i];
			var compareUserId = storageList[j];
			if (userId == compareUserId) {
				storageList.splice(j, 1);
			}
		}
		storageList.unshift(selectedList[i]);
	}
	if (storageList.length > 5) {
		storageList.pop();
	}
	return storageList;
};

// 최근 로그인 - 배열을 스트링으로 변환하기 
function arrayItemToString(array) {
	$.each(array, function (index, item) {
		array[index] = item; 
	});
};

function wifiCheckFn(wifiCheck) {
	localStorage.setItem("wifiCheckVal", wifiCheck);
}

function loadingbar_display() {
	if ($('#loading_bar').is(":visible")) {
		$('#loading_bar').css('display', 'none');
	} else {
		$('#loading_bar').css('display', 'block');
	};
}
// 로그인  - 인증서 삭제처리 / dn값도 초기화
function fnLoginConditionCheck() {

	var checkTime = "";
	$.ajax({
		url: 'http://emrdev.yjh.com/eform' + '/biz/nu/member/viewer/eForm25/consent/nowtime/get',
		type: 'post',
		timeout: 10000,
		async: false,
	}).done(function (data) {
		checkTime = data.nowTime;
	});
	COMMON.plugin.compareTime(checkTime);

	var id = $("#_loginId").val();
	var pw = $("#_loginPw").val();
	pw = encodeURIComponent(pw);
	var LoginDeptName = new Array;
	var LoginDeptCode = new Array;
	var LoginDeptMinName = new Array;
	var LoginJobKindCd = new Array;
	var idx = 0;
	var jobkindcd = "";
	var licnsno = "";
	var medispclno = "";
	var ordfild = "";
	var depthngnm = "";
	var applyduty ="";
	var applydevice = "";
	//2021-10-25
	var aprecupdtyn = "";
	
	COMMON.plugin.wifiCheck(wifiCheckFn); 

	if (INTERFACE.TYPE == "REAL") { 
		HIS_EPH = "HIS204";
	} else {
		HIS_EPH = "HIS204EDU"; 
	}
 
	var checkVal = localStorage.getItem("wifiCheckVal");
	if (checkVal < -90) {
		alert("WIFI 수신 감도가 좋지 않습니다. WIFI 통신이 잘 되는 곳에서 사용 바랍니다.");
	} else {
		 
		
			if ($("#_btnUpdate").is(':visible')) {
				alert("업데이트가 있습니다.\n업데이트 후 실행해 주시기 바랍니다.");
			} else { 
				if (id != "" && pw != "") {
					loadingbar_display();
					if (isRun == true) {
						return;
					}		
					isRun = true; 
					$.ajax({
						url: 'http://emrdev.yjh.com/cmcnu/.live?'+'submit_id=DRMRF00119&business_id=mr&userid=' + id + '&pwd=' + pw + '&instcd=204&syscd=' + HIS_EPH,
						type: 'post', 
						dataType: 'xml',
						timeout: 10000,
						success: function (result) { 
							isRun = false;
							$("#_userDeptChoice option").remove();
							if ($(result).find('data').length > 0) {
								$(result).find('data').each(function () {
									if ($(this).find('login').text() != 'false') { 
									 	applydevice = $(this).find('applydevice').text();   
									 	if(applydevice =="Y"){ 
											if ($('#_DeviceUUID').text() == "미등록") {
												alert("단말기가 등록되어있지 않습니다. \n정보운영팀에서 등록 바랍니다.");
												return false;
											}else { 
												params = { //
														"userId": $(this).find('userid').text(), // 여기다 return값 매핑시켜서 넣어주고
														"userName": $(this).find('usernm').text(),
														"userGroupName": $(this).find('depthngnm').text(),
														"userGroupCode": $(this).find('deptcd').text(),
														"userDeptName": $(this).find('depthngnm').text(),
														"userDeptFullName": $(this).find('deptnm').text(),
														"userDeptCode": $(this).find('deptcd').text(),
														"userPartCd": $(this).find('depthngnm').text(),
														"docYN": 'dn', // 데모에서는 테스트 dn이라고 생각  , 인증플래그값
														"serverTime": '' // 서버리턴시간으로 변경
													};
													$('.login_section').css('display', 'none');
													$('.choice_Dept').css('display', 'block'); 
													applyduty = $(this).find('applyduty').text();
													jobkindcd = $(this).find('jobkindcd').text(); 
													licnsno = $(this).find('licnsno').text();
													medispclno = $(this).find('medispclno').text();
													depthngnm = $(this).find('depthngnm').text();

													
													// 2021-10-25
													aprecupdtyn = $(this).find('aprecupdtyn').text();
													// ==============================================			
													
													if($(this).find('ordfild').text() != "") {
														ordfild = $(this).find('ordfild').text().trim();	
														ordfild = ordfild.replace(/\n/g,"");
													}																	
													
													$('#_userDeptChoice_txt').text($(this).find('usernm').text());
													LoginDeptCode[idx] = $(this).find('deptcd').text();
													LoginDeptName[idx] = $(this).find('deptnm').text();
													LoginDeptMinName[idx] = $(this).find('depthngnm').text();
													LoginJobKindCd[idx] = $(this).find('jobkindcd').text();
													$("#_userDeptChoice").append($('<option>', {
														class: LoginDeptMinName[idx]+"@"+LoginJobKindCd[idx],
														value: LoginDeptCode[idx],
														text: LoginDeptName[idx]
													}));
													idx = idx + 1;
											}
											
									 	}else if(applydevice =="N"){ 
									 		params = { //
													"userId": $(this).find('userid').text(), // 여기다 return값 매핑시켜서 넣어주고
													"userName": $(this).find('usernm').text(),
													"userGroupName": $(this).find('depthngnm').text(),
													"userGroupCode": $(this).find('deptcd').text(),
													"userDeptName": $(this).find('depthngnm').text(),
													"userDeptFullName": $(this).find('deptnm').text(),
													"userDeptCode": $(this).find('deptcd').text(),
													"userPartCd": $(this).find('depthngnm').text(),
													"docYN": 'dn', // 데모에서는 테스트 dn이라고 생각  , 인증플래그값
													"serverTime": '' // 서버리턴시간으로 변경
												};
												$('.login_section').css('display', 'none');
												$('.choice_Dept').css('display', 'block'); 
												applyduty = $(this).find('applyduty').text();
												jobkindcd = $(this).find('jobkindcd').text(); 
												licnsno = $(this).find('licnsno').text();
												medispclno = $(this).find('medispclno').text();
												depthngnm = $(this).find('depthngnm').text();

												// 2021-10-25
												aprecupdtyn = $(this).find('aprecupdtyn').text();
												// ==============================================
												
												
												if($(this).find('ordfild').text() != "") {
													ordfild = $(this).find('ordfild').text().trim();	
													ordfild = ordfild.replace(/\n/g,"");
												}																	
												
												$('#_userDeptChoice_txt').text($(this).find('usernm').text());
												LoginDeptCode[idx] = $(this).find('deptcd').text();
												LoginDeptName[idx] = $(this).find('deptnm').text();
												LoginDeptMinName[idx] = $(this).find('depthngnm').text();
												LoginJobKindCd[idx] = $(this).find('jobkindcd').text();
												$("#_userDeptChoice").append($('<option>', {
													class: LoginDeptMinName[idx]+"@"+LoginJobKindCd[idx],
													value: LoginDeptCode[idx],
													text: LoginDeptName[idx]
												}));
												idx = idx + 1;
									 		
									 	}
										
									} else {
										alert("사용자 정보가 없습니다.\nID나 패스워드가 일치하지 않습니다.\n다시 확인해주시기 바랍니다.");
									}
								});

								localStorage.setItem("LoginDeptMinName", JSON.stringify(LoginDeptMinName));
								localStorage.setItem("LoginDeptName", JSON.stringify(LoginDeptName));
								localStorage.setItem("LoginDeptCode", JSON.stringify(LoginDeptCode)); 
								localStorage.setItem("jobkindcd", jobkindcd);
								localStorage.setItem("applyduty", applyduty);
								localStorage.setItem("licnsno", licnsno);
								localStorage.setItem("medispclno", medispclno);
								localStorage.setItem("ordfild", ordfild);   
								localStorage.setItem("depthngnm", depthngnm); 

								// 2021-10-25   
								localStorage.setItem("aprecupdtyn", aprecupdtyn);								
								// ==============================================
							}

							loadingbar_display();
							if (INTERFACE.USER != "") {
								//fnGoPage(params);
							} else {
								COMMON.plugin.compareTime(params, fnGoPage, errorHandler);
							}
						},
						error: function (error) {
							isRun = false;
							alert("로그인 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n에러코드 : " + error.readyState);

							loadingbar_display();
						}

					})
				} else {
					if (id == "" || id == null) {
						alert("아이디를 입력하지 않았습니다.");
					} else if (pw == "" || pw == null) {
						alert("패스워드를 입력하지 않았습니다.");
					}
				}
			}

	}

}; 

//인증로그인 
function fnSignLoginConditionCheck() {
	var id = $("#_loginId").val();
	var pw = $("#_loginPw").val();

	if ($("#_btnUpdate").is(':visible')) {
		alert("업데이트가 있습니다.\n업데이트 후 실행해 주시기 바랍니다.");
	} else {
		if (id != "" && pw != "") {
			// 화면 변수
			var paramObject = {
				"userId": id ,
				"password": pw ,
				"encryptYn": ""
			};

			// 공통 변수
			var args = {
				"sCode": "Login",
				"param": paramObject,
				"userId": id,
				"patientCode": "",
				"reqType": "dotnetWebserive2",
				"serviceName": "HospitalSvc.aspx"
			};
			var reqSetting = COMMON.util.makeReqParam(args);
			COMMON.plugin.doRequest(reqSetting, signloginSuccessHandler, errorHandler);
		} else {
			if (id == "" || id == null) {
				alert("아이디를 입력하지 않았습니다.");
			} else if (pw == "" || pw == null) {
				alert("패스워드를 입력하지 않았습니다.");
			}
		}
	}
};


// 사용자 정보를 저장하고 메인 페이지로 이동한다.
function fnGoPage(params) {
	// 최근 로그인 저장 
	setLatelyData(params);
	loadingbar_display();
	COMMON.LOCAL.eform.user = params;
	localStorage.setItem("userId", params.userId);
	localStorage.setItem("userDeptCode", params.userDeptCode);
	localStorage.setItem("userDeptName", params.userDeptName);
	localStorage.setItem("userDeptFullName", params.userDeptFullName);
	localStorage.setItem("userGroupCode", params.userGroupCode);
	localStorage.setItem("jobkindcd", params.jobkindcd);
	localStorage.setItem("docYN", params.docYN);
//	alert("사용자 인증 비밀번호 초기화");
//	localStorage.setItem("signPwd", "");
	COMMON.plugin.storage("set", params, function () {
		loadingbar_display();
		location.href = '../main/main.html';
	}, errorHandler);
};


//앱 업데이트 확인 이벤트 
function isAppUpdate() {
	COMMON.plugin.versionInfo(isAppUpdateSuccessHandler, errorHandler);
};

// 앱 업데이트 확인 이벤트 
function isAppUpdateSuccessHandler(resData) {
	var versionInfo = resData; 

	if ($.isEmptyObject(resData)) {
		$("#_btnUpdate").hide();
		alert("버전 정보가 없습니다.");
	} else {
		var viewerVersion;
		var clientVersion; 
		for (var i = 0; i < versionInfo.length; i++) { 
			if(versionInfo[i].appType == "C"){
				clientVersion = versionInfo[i];
			}else if(versionInfo[i].appType == "E"){
				viewerVersion = versionInfo[i];
			} 
		}   
		var confimText = "";
		var confimTexts = "";
		var apkName = "";
		var viewerApkName = "";
		var isUpdate = false;
		var isViewerUpdate = false; 

		// 업무앱의 서버 버전과 클라이언트 버전 비교
		if (Number(clientVersion.appVersion) > Number(clientVersion.consentClientVersion)) {
			$("#_btnUpdate").show();
			$("#_btnUpdateConsent").show();
			apkName = clientVersion.apkName;
			confimText += "전자동의서";
			isUpdate = true;
		} else {
			$("#_btnUpdateConsent").hide();
		}
		// 뷰어앱의 서버 버전과 클라이언트 버전 비교
		if (Number(viewerVersion.appVersion) > Number(viewerVersion.eformClientVersion)) {
			$("#_btnUpdate").show();
			$("#_btnUpdateConsent").show();
			viewerApkName = viewerVersion.apkName;
			confimTexts += "뷰어";
			isViewerUpdate = true;
		} else {
			$("#_btnUpdateConsent").hide();
		}

		if (isUpdate) {
			confimText += " 업데이트가 있습니다. \n업데이트 후 사용 할 수 있습니다.\n업데이트를 하시겠습니까?";
			if (confirm(confimText) == true) {
				COMMON.plugin.updateAppDownload("update", apkName);
			} else {
				COMMON.plugin.terminateApp();
			}
		}
		if (isViewerUpdate) {
			if (viewerVersion.eformClientVersion == "0") {
				confimTexts += "가 설치되어 있지 않습니다. \n설치 후 사용 할 수 있습니다. 설치 하시겠습니까?";
			} else {
				confimTexts += " 업데이트가 있습니다. \n업데이트 후 사용 할 수 있습니다.\n업데이트를 하시겠습니까?";
			}

			if (confirm(confimTexts) == true) {
				COMMON.plugin.updateAppDownload("update", viewerApkName);
			} else {
				COMMON.plugin.terminateApp();
			}
		}
	}
};

// 업무 버전 표시 
function markConsentAppVersion(appVersionName) {
	$("#_consentVersion").text(appVersionName);
};

// e-Form 버전 표시 
function markEformAppVersion(appVersionName) {
	$("#_eformVersion").text(appVersionName);
};

// 배포 버전용이 아니라면 해당 설정 표시
function makeInterfaceUser() {
	if (INTERFACE.TYPE == "REAL" && INTERFACE.USER == "") {
		$("#_interfaceType").hide();
	} else {
		$("#_interfaceType").text("MODE : " + INTERFACE.USER + " / Interface : " + INTERFACE.TYPE);
		$("#_interfaceType").show();
	}
};

function errorHandler(errorMessage) {
	alert(errorMessage);
};