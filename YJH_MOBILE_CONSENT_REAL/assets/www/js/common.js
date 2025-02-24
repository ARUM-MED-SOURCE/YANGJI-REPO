var COMMON;
if (!COMMON) {
	COMMON = {};
	log = {};

	COMMON.PAGE = {
		'PAGE_LIST_COUNT': 10,
		'PAGE_START_NUM': 1,
		'PAGE_END_NUM': 10,
		'PAGE_MAX_NUM': 300,
		'TITLE': '',
		'MAIN_PAGE': 'login',
		'SUB_PAGE': ''
	};

	COMMON.CONFIG = {
		'connectionType': INTERFACE.CONFIG.CONNECTION_TYPE,
		'ipAddress': INTERFACE.CONFIG.CONNECTION_URL,
		'portNumber': INTERFACE.CONFIG.CONNECTION_PORT,
		'patientCode': '',
		'userId': '',
		'dataType': '', // json or xml
		'reqType': 'post', // get,post,uploadFile,downloadFile
		'sType': '',
		'sCode': '',
		'timeoutInterval': '30000',
		'parameter': ''
	};
	// 공통저장 관련 구조체
	COMMON.LOCAL = {
		defaultConfig: {
			lang: navigator.language,
			langPath: "/lang"
		},
		page: {
			userType: ""
		},
		eform: {
			consent: [],
			user: {},
			patient: {},
			FormGuid: "",
			consentSave: {}
		},
		record: {
			params: {},
			obj: {},
			type: "",
			isRecord: false
		}
	};

	// 자동 로그아웃 설정 -----------------
	// Logout Timer 객체 정의
	COMMON.LogOutTimer = function () {
		var setting = {
			timer: null,
			limit: 60 * 60 * 1000,
			fnc: function () {
				console.log("자동 로그아웃 실행");
				// 도움말 팝업이 활성화 된 상태이면 도움말 팝업은 닫기
				if ($(".help_section").is(":visible")) {
					$(".help_section").hide();
					$(".dimmed").hide();
				}
				// 패스워드 팝업이 활성화 된 상태이면 패스워드 팝업 닫기
				if ($(".popup-pwd").is(":visible")) {
					$(".popup-pwd").removeClass("_open");
					$(".popup-pwd").hide();
					if ($("._open").length <= 0) {
						$(".dimmed").hide();
					}
				}
				$("#_reLoginId").val(getLocalStorage("userId"));
				if (INTERFACE.USER != "") {
					if (INTERFACE.TYPE == "REAL") {
						//						$("#_reLoginId").val("1065");
						//						$("#_reLoginPw").val("@@26412641");
					} else {
						//						$("#_reLoginId").val("MD01");
						//						$("#_reLoginPw").val("11");
					}
				} else {
					$("#_reLoginId").val("");
				}
				$(".popup-relogin").addClass("_open");
				if ($(".popup-relogin").show()) {
					//navigator.app.exitApp()
				}
				$(".dimmed").show();
			},
			start: function () {
				if (setting.timer == null) {
					setting.timer = window.setTimeout(setting.fnc,
						setting.limit);
				}
				console.log("[로그아웃 타이머] 시작");
			},
			stop: function () {
				if (setting.timer != null) {
					window.clearTimeout(setting.timer); 
				}
				setting.timer = null;
				console.log("[로그아웃 타이머] 중지");
			},
			reset: function () {
				//				console.log("[로그아웃 타임어] 리셋");
				if (setting.timer != null) {
					window.clearTimeout(setting.timer);
				}
				setting.timer = window.setTimeout(setting.fnc, setting.limit);
			}
		};
		document.onmousemove = function () {
			setting.reset();
		};
		return setting;
	}();

	COMMON.BACKKEY = {
		addEvent: function () {
			document.addEventListener("backbutton", function (e) {
				// if($.mobile.activePage.is('#'+COMMON.PAGE.MAIN_PAGE+"_page")){
				if (document.URL.indexOf(COMMON.PAGE.MAIN_PAGE) > 0) {
					e.preventDefault(); // event 중복
					e.stopPropagation();
					if (confirm("전자동의서 앱을 종료하시겠습니까?")) {
						COMMON.plugin.terminateApp();
						return;
					}
				} else {
					if (confirm("종료 하시겠습니까?")) {

						var ITnadePlugins = new ITnadePlugin();
						ITnadePlugins.LocalDelKeyAndCert(getLocalStorage("userdn"));

						var isOnInpatientMenu = $("#_inPatient").hasClass("on");
						var isOnEmergencyMenu = $("#_emergency").hasClass("on");
						var isOnOutpatientMenu = $("#_outPatient").hasClass("on");
						var isOnOperationMenu = $("#_operation").hasClass("on");
						var isOnRequestMenu = $("#_request").hasClass("on");
						var isOnFindMenu = $("#_find").hasClass("on");
						var isOnCosignMenu = $("#_cosign").hasClass("on");
						var isOnmyConsentMenu = $("#_myConsent").hasClass("on");

						localStorage.removeItem("deviceMenuSet");
						if (isOnInpatientMenu) {
							localStorage.setItem("deviceMenuSet", "_inPatient");
						} else if (isOnOutpatientMenu) {
							localStorage.setItem("deviceMenuSet", "_outPatient");
						} else if (isOnEmergencyMenu) {
							localStorage.setItem("deviceMenuSet", "_emergency");
						} else if (isOnOperationMenu) {
							localStorage.setItem("deviceMenuSet", "_operation");
						} else if (isOnFindMenu) {
							localStorage.setItem("deviceMenuSet", "_find");
						} else if (isOnRequestMenu) {
							localStorage.setItem("deviceMenuSet", "_request");
						} else if (isOnCosignMenu) {
							localStorage.setItem("deviceMenuSet", "_cosign");
						} else if (isOnmyConsentMenu) {// 작성 동의서 빠른 조회 기록 저장
							localStorage.setItem("deviceMenuSet", "_myConsent"); 
				            var visitType = $('input:radio[name=treatMentVisitType]:checked').val();
				            var treatementDate = Number($('#_treatStartDate').val().replace(/-/g, "")) - Number($('#_treatEndDate').val().replace(/-/g, ""));
				            localStorage.setItem("myConsentYn", $('#myConsentYn').is(":checked") ? "Y" : "N");
				            localStorage.setItem("treateMentDept", $("#treateMentDept option:selected").val());
				            localStorage.setItem("treateMentWard", $("#treateMentWard option:selected").val());
				            localStorage.setItem("treateMentDoctor", $("#treateMentDoc option:selected").val());           
				            localStorage.setItem("treatMentVisitType", visitType);
				            localStorage.setItem("treatementDate", treatementDate);
				            
				            var consentStateN = ($('#NEW').prop('checked') ? "Y" :"N");
				            var consentStateT = ($('#TEMP').prop('checked') ? "Y" :"N");
				            var consentStateE = ($('#ELECTR_CMP').prop('checked') ? "Y" : "N");
				            var consentState = consentStateN+","+consentStateT+","+consentStateE;
				            localStorage.setItem("treatMentConsentType", consentState);  
						}
						

			            // 각 탭 검색조건 설정 
			            localStorage.setItem("inpatientSet", $('#_inPatientClnDept option:selected').val());
			            localStorage.setItem("outpatientSet", $('#_outPatientClnDept option:selected').val());
			            localStorage.setItem("emergencySet", $('#_emergencyDept option:selected').val());
			            localStorage.setItem("requestSet", $('#_requestDept option:selected').val());
			            localStorage.setItem("operationSet", $('#_operationClnDept option:selected').val());
			            localStorage.setItem("findSet", $('#_findDept option:selected').val());
			            
						
						COMMON.plugin.terminateApp();
						/*
						 * COMMON.plugin.storage("delete", "", null); var index =
						 * 1 - Number(history.length); history.go(index); 박승찬 수정
						 */
						return;
					}
				}
			}, false);
		},
		removeEvent: function () {
			console.log("back Key Remove Event");
			document.removeEventListener("backbutton", function (e) {}, false);
		}
	};

	// //////////////////////
	// 정규식 관련 메서드 모음
	// //////////////////////
	COMMON.regExp = {
		number: /^(-|)\d+$/, // 숫자인지 판별
		string: /^\D$/g, // 문자인지 판별
		pixelStr: /px$/g, // px 단위인지 판별 ( 마지막이 px인지 )
		blank: /\s/g, // 공백인지 판별
		hasBlank: /^.+\s.+$/g, // 공백을 가지고 있는지 판별
		num4: /^\d{4}$/g, // 4자리의 양수인지 판별 ( YYYY )
		num6: /^\d{6}$/g, // 6자리의 양수인지 판별 ( YYYYMM )
		num8: /^\d{8}$/g, // 8자리의 양수인지 판별 ( YYYYMMDD )
		floatNumber: /^(-|)\d+(.\d+)?$/, // (.을 포함하여) 숫자인지 판별(추가 12.10.11)
		colonNumber: /^(-|)\d+(:\d+)?$/
		// (:을 포함하여) 숫자인지 판별(추가 13.11.20)
	};

	// //////////////////////
	// DEVICE 관련 메서드 모음
	// //////////////////////
	COMMON.device = {
		/**
		 * @returns {Boolean} : 안드로이드 여부
		 */
		isAndroid: function () {
			return (/Android/.test(navigator.userAgent));
		},
		/**
		 * @returns { Boolean } : 아이폰 여부
		 */
		isIphone: function () {
			return (/iPhone/.test(navigator.userAgent));
		},
		/**
		 * @returns { Boolean } : 아이패드 여부
		 */
		isIpad: function () {
			return (/iPad/.test(navigator.userAgent));
		},
		/**
		 * @returns {Boolean} : 모바일 여부
		 */
		isMobile: function () {
			return (/Mobile/.test(navigator.userAgent) || this.isIphone() || this
				.isAndroid());
		},
		/**
		 * @returns {Boolean} : 태블릿 여부
		 */
		isTablet: function () {
			return (/Android/.test(navigator.userAgent) && !(/Mobile/
				.test(navigator.userAgent)))
		},
		/**
		 * @returns {String} : landscape | portrait
		 */
		getOrientation: function () {
			var objWindow = $(window);

			if (objWindow.width() > objWindow.height()) {
				objWindow = null;
				return "landscape";
			} else {
				objWindow = null;
				return "portrait";
			}
		}
	};

	// //////////////////////
	// 유효성 검사 관련 메서드 모음
	// //////////////////////
	COMMON.validate = {
		/**
		 * 입력된 객체가 유효한 값인지를 반환함
		 * 
		 * @param obj :
		 *            {Object} : 유효성을 판단할 객체
		 * @returns {Boolean} : true ( 유효함 ) / false ( 유효하지 않음 )
		 */
		isValid: function (obj) {
			return ('undefined' != typeof obj && null != obj);
		},
		/**
		 * 입력된 객체가 유효하지 않은 값인지를 반환함
		 * 
		 * @param obj :
		 *            {Object} : 유효성을 판단할 객체
		 * @returns {Boolean} : true ( 유효하지 않음 ) / false ( 유효함 )
		 */
		isInvalid: function (obj) {
			return ('undefined' == typeof obj || null == obj);
		},
		/**
		 * 입력된 문자열이 유효한 값인지를 반환함
		 * 
		 * @param str :
		 *            {Object} : 유효성을 판단할 문자열
		 * @returns {Boolean} : true ( 유효함 ) / false ( 유효하지 않음 )
		 */
		isValidStr: function (str) {
			return (this.isValid(str) && '' != str);
		},
		/**
		 * 입력된 문자열이 유효하지 않은 값인지를 반환함
		 * 
		 * @param str :
		 *            {Object} : 유효성을 판단할 문자열
		 * @returns {Boolean} : true ( 유효하지 않음 ) / false ( 유효함 )
		 */
		isInvalidStr: function (str) {
			return (this.isInvalid(str) || '' == str);
		},
		/**
		 * 입력된 문자열이 숫자로만 이루어져 있는지 체크함
		 * 
		 * @param str :
		 *            {String} : 체크할 문자열
		 * @returns {Boolean} : true ( 숫자형 문자열 ) / false ( 다른 문자가 포함된 문자열 )
		 */
		isOnlyNum: function (str) {
			return new RegExp(COMMON.regExp.number).test(str);
		},
		/**
		 * 입력된 문자열에 숫자가 없는 문자로만 이루어져 있는지를 체크함
		 * 
		 * @param str :
		 *            {String} : 체크할 문자열
		 * @returns {Boolean} : true ( 숫자가 없는 문자열 ) / false ( 숫자가 있는 문자열 )
		 */
		isOnlyStr: function (str) {
			return new RegExp(COMMON.regExp.string).test(str);
		},
		/**
		 * 입력된 오브젝트가 숫자 객체인지 아닌지를 반환함
		 * 
		 * @param obj :
		 *            {Object} : 체크할 오브젝트
		 * @returns {Boolean} : true ( 숫자 객체 ) / false ( 그 외 객체 )
		 */
		isNumber: function (obj) {
			return ('number' == typeof obj && isFinite(obj));
		},
		/**
		 * 입력된 오브젝트가 문자 객체인지 아닌지를 반환함
		 * 
		 * @param obj :
		 *            {Object} : 체크할 오브젝트
		 * @returns {Boolean} : true ( 문자 객체 ) / false ( 그 외 객체 )
		 */
		isString: function (obj) {
			return ('string' == typeof obj);
		},
		/**
		 * 입력된 문자열이 공백을 포함하고 있는지 체크합니다.
		 * 
		 * @param str :
		 *            {String} : 체크할 문자열
		 * @returns {Boolean} : true ( 공백이 포함된 문자열 ) / false ( 공백이 없는 문자열 )
		 */
		hasBlank: function (str) {
			return new RegExp(COMMON.regExp.hasBlank).test(str);
		},
		/**
		 * 입력된 문자열이 오직 공백으로만 되어 있는지 체크함
		 * 
		 * @param str :
		 *            {String} : 체크할 문자열
		 * @returns {Boolean} : true ( 공백으로만 구성됨 ) / false ( 공백이 없거나 이외의 것이 포함됨 )
		 */
		isOnlyBlank: function (str) {
			return new RegExp(COMMON.regExp.blank).test(str);
		},
		/**
		 * 입력된 문자열이 4자리의 양수인지를 체크함 ( YYYY 형식 체크에 사용 )
		 * 
		 * @param str :
		 *            {String} : 체크할 문자열
		 * @returns {Boolean} : true ( 4자리 양수 ) / false ( 그외 문자 )
		 */
		is4Num: function (str) {
			return new RegExp(COMMON.regExp.num4).test(str);
		},
		/**
		 * 입력된 문자열이 6자리의 양수인지를 체크함 ( YYYYMM 형식 체크에 사용 )
		 * 
		 * @param str :
		 *            {String} : 체크할 문자열
		 * @returns {Boolean} : true ( 6자리 양수 ) / false ( 그외 문자 )
		 */
		is6Num: function (str) {
			return new RegExp(COMMON.regExp.num6).test(str);
		},
		/**
		 * 입력된 문자열이 8자리의 양수인지를 체크함 ( YYYYMMDD 형식 체크에 사용 )
		 * 
		 * @param str :
		 *            {String} : 체크할 문자열
		 * @returns {Boolean} : true ( 8자리 양수 ) / false ( 그외 문자 )
		 */
		is8Num: function (str) {
			return new RegExp(COMMON.regExp.num8).test(str);
		},
		/**
		 * 입력된 문자열이 숫자로만 되어 있는지 체크함
		 * 
		 * @param str :
		 *            {String} : 체크할 문자열
		 * @returns {Boolean} : true ( 숫자 ) / false ( 문자가 포함됨 )
		 */
		isNumberStr: function (str) {
			return new RegExp(COMMON.regExp.number).test(str);
		},
		/**
		 * 입력된 문자열이 실수(소숫점 가능)로만 되어 있는지 체크함
		 * 
		 * @param str :
		 *            {String} : 체크할 문자열
		 * @returns {Boolean} : true ( 숫자 ) / false ( 숫자형태가 아님 )
		 */
		isFloatNumberStr: function (str) {
			return new RegExp(COMMON.regExp.floatNumber).test(str);
		},
		// 2013.11.20 추가 [윤상환]
		/**
		 * 입력된 문자열이 시간(:와 숫자 가능)로만 되어 있는지 체크함
		 * 
		 * @param str :
		 *            {String} : 체크할 문자열
		 * @returns {Boolean} : true ( 숫자 ) / false ( 숫자형태가 아님 )
		 */
		isColonNumberStr: function (str) {
			return new RegExp(COMMON.regExp.colonNumber).test(str);
		}
	};
	// //////////////////////
	// UTIL 메서드 모음
	// //////////////////////
	COMMON.util = {
		requestMap: function () {
			var obj = {
				"INDEX": 0,
				"QUEUE": [],
				"init": function () {
					this.INDEX = 0;
					this.QUEUE = [];
				},
				"getPrevParam": function () {
					return this.QUEUE[this.INDEX - 1].PARAM;
				},
				"getParam": function () {
					return this.QUEUE[this.INDEX].PARAM;
				},
				"add": function (fn_view, fn_model, param) {
					if (param == undefined)
						param = {};
					this.QUEUE.push({
						"FN_VIEW": fn_view,
						"FN_MODEL": fn_model,
						"PARAM": param
					});
				},
				"start": function () {
					var result = this.getFunc();
					if (result != null) {
						result.FN_VIEW(result.FN_MODEL);
					} else {
						if (this.QUEUE.length <= (this.INDEX + 1)) {
							COMMON.plugin.loadingbar(false);
						}
					}
				},
				"stop": function () {
					this.INDEX = this.QUEUE.length + 1;
					COMMON.plugin.loadingbar(false);
				},
				"getFunc": function () {
					var result = {
						"FN_VIEW": null,
						"FN_MODEL": null
					};

					if (this.QUEUE.length > this.INDEX) {
						result.FN_VIEW = this.QUEUE[this.INDEX].FN_VIEW;
						result.FN_MODEL = this.QUEUE[this.INDEX].FN_MODEL;
					} else {
						result = null;
					}
					return result;
				},
				"getLoading": function () {
					var result = {
						"START": true,
						"STOP": true
					};

					if (this.QUEUE.length > this.INDEX) {
						if (this.INDEX > 0) {
							result.START = false;
						} else {
							result.START = true;
						}
						if ((this.INDEX + 1) == this.QUEUE.length) {
							result.STOP = true;
						} else {
							result.STOP = false;
						}
					}
					this.next();
					return result;
				},
				"next": function () {
					this.INDEX++;
				}
			};
			return obj;
		},
		objectCopy: function (obj) {
			if (obj != null && obj != undefined)
				return JSON.parse(JSON.stringify(obj));
			else
				return null;
		},
		// request make parameter
		makeReqParam: function (args) {
			var sConfig = COMMON.util.objectCopy(COMMON.CONFIG);
			sConfig.sCode = args.sCode;
			sConfig.serviceName = args.serviceName;
			sConfig.userId = args.userId;
			sConfig.patientCode = args.patientCode;
			sConfig.parameter = args.param;
			sConfig.reqType = args.reqType;

			return sConfig;
		},
		test: function (text) {
			cordova.exec(function (result) {
				alert("복호화 : " + result);
			}, function (message) {
				alert(message);
			}, "ClipsoftPlugin", "decrypt", [text]);
		},
		eformExit: function (code) {
			console.log("eformExit code : " + code);
			COMMON.LOCAL.eform.guid = "";
		},
		eformTempSave: function (dataXml) {
			alert("eformTempSave dataXml : " + dataXml);
			console.log(dataXml);
		},
		eformSave: function (xmlData) {
			console.log("eformSave Data : " + xmlData);
			COMMON.LOCAL.eform.guid = "";
		},
		testAlert: function (data) {
			console.log(data);
			alert(data);
		},
		addSelectOptions: function (selectboxId, options, successFn) {
			$.each(options, function (i, option) {
				if (option.Code == "" || option.Code == undefined) {
					$("#" + selectboxId).append($('<option>', {
						value: option.Name,
						text: option.Name
					}));
				} else {
					$("#" + selectboxId).append($('<option>', {
						value: option.Code,
						text: option.Name
					}));
				}
			});
			if (successFn != null && successFn != undefined) {
				successFn();
			}
		},
		clipConsoleLog: function (message) {
			if (INTERFACE.IS_CONSOLE_LOG == "TRUE") {
				console.log(message);
			}
		}
	}
	COMMON.plugin = {
		/**
		 * request sending
		 * 
		 * @param param :
		 *            {object json} : request parameter
		 * @param successFn :
		 *            {function} : request success function
		 * @param errorFn :
		 *            {function} : request error function
		 * @returns callback
		 */
		doRequest: function (options, successFn, errorFn) {
			if (!COMMON.device.isMobile() && !COMMON.device.isTablet()) {
				// web일 경우
				var url = INTERFACE.CONFIG.CONNECTION_TYPE + "://" +
					"59.11.2.207:" +
					"8091/" +
					options.serviceName;
				var requestParams = {
					"methodName": options.sCode,
					"params": JSON.stringify(options.parameter),
					"userId": options.userId,
					"deviceType": "AND",
					"deviceIdentName": "Chrome",
					"deviceIdentIP": "172.17.200.48",
					"deviceIdentMac": "E0AA96DEBD0A"
				};

				console.log("===========================================");
				console.log("doRequest : " + JSON.stringify(options));
				console.log("[Request]URL : " + url);
				console.log("[Request]parameter : " +
					JSON.stringify(requestParams));

				$
					.ajax({
						url: url,
						type: "post",
						dataType: "json",
						'Content-Type': 'application/x-www-form-urlencoded',
						crossDomain: true,
						data: requestParams,
						'timeout': 10000,
						'success': function (result) {
							console.log("[" + options.sCode + "] 결과 : ")
							console.log(result);
							console
								.log("==========================================");

							var data = result;
							if (data.RESULT_CODE == "0") {
								successFn(data.RESULT_DATA);
							} else {
								alert("Error Code : " + data.ERROR_CODE +
									" \nError Message : " +
									data.ERROR_MESSAGE + "\n" +
									options.sCode + " 호출에 실패했습니다.");
							}
						},
						'error': function (xhr, textStatus, errorThrown) {
							//								console.log(options.sCode + "호출 결과 : ")
							//								console.log(xhr.state());
							//								console.log(textStatus);
							//								console.log(errorThrown);
							//								console
							//										.log("=====================================");
							//								errorFn(textStatus);
						}
					});
			} else {
				// 네이티브일 경우
				console.log("======================");
				console.log("requestParams : " +
					JSON.stringify(options.parameter));
				console.log("======================");
				cordova.exec(function (result) {
					console.log(options.sCode + "호출 결과 : 성공")
					console.log(result);
					console.log("========================");
					var data = JSON.parse(result);
					if (data.RESULT_CODE == "0") {
						successFn(data.RESULT_DATA);
					} else {
						alert("Error Code : " + data.ERROR_CODE + " \n" +
							data.ERROR_MESSAGE + "\n\n[" + options.sCode +
							"] 호출에 실패했습니다.");
					}
				}, function (result) {
					console.log(options.sCode + "호출 결과 : 실패 ")
					console.log(result);
					console.log("========================");
					errorFn(result);
				}, "ClipsoftPlugin", "webserive", [options.serviceName,
					options.sCode, options.parameter, options.userId,
					options.patientCode
				]);
			}
		},
		loadingbar: function (visible) {
			/*
			 if(visible){
				 //CharSequence message, boolean indeterminate, boolean cancelable,
			 	ProgressIndicator.show("Loading...", true, false);
			 }else{
				 ProgressIndicator.hide();
			 } 
			 else{
			 if(visible){
			     $.mobile.loading( "show", {
			             text: "Loading...",
			             textVisible: true,
			             theme: "a",
			             textonly: false,
			             html: ""
			     });
			 }else{
				 $.mobile.loading( "hide" );
			 }
			 */
		},
		terminateApp: function () {
			if (COMMON.device.isMobile() || COMMON.device.isTablet()) {
				// cordova.exec(function(){console.log("customPlugin
				// terminateApp");}, function(){console.log("customPlugin
				// error");}, "customPlugin", "terminateApp", []);

				//var ITnadePlugins = new ITnadePlugin();
				//ITnadePlugins.LocalDelKeyAndCert(getLocalStorage("userdn"));
				navigator.app.exitApp()
			} else {
				alert("앱을 종료합니다.");
			}
		},
		datePicker: function (selectedDate, successFn, errorFn) {
			if (COMMON.device.isMobile() || COMMON.device.isTablet()) {
				cordova.exec(function (result) {
					console.log("[datePicker2] success : " + result);
					if (successFn != null) {
						successFn(result);
					}
				}, function (result) {
					console.log("[datePicker2] error : " + result);
					if (errorFn != null) {
						errorFn(result);
					}
				}, "ClipsoftPlugin", "datepicker", [selectedDate]);
			} else {
				alert("datePicker");
			}
		},
		loadEFormViewByGuid: function (type, op, consents, params) {
			COMMON.LogOutTimer.stop(); // 로그아웃 타이머 stop
			isAppUpdate();
			console.log("loadEFormViewByGuid");
			//			console.log("type : " + type);
			//			console.log("op : " + op);
			//			console.log("params : " + JSON.stringify(params));
			//			console.log("consents :" + JSON.stringify(consents));
			var data = localStorage.getItem("signPwd")
			if (COMMON.device.isMobile() || COMMON.device.isTablet()) {
				cordova.exec(function (result) {
					console.log("loadEFormViewByGuid result  " + result);
				}, function (result) {
					console.log("loadEFormViewByGuid Error : " + result);
					//alert(result);
				}, "ClipsoftPlugin", "loadEFormViewByGuid", [type, op,
					consents, params, data
				]);
			} else {
				alert("[loadEFormViewByGuid]\ntype : " + type + "\nop : " + op +
					"\nparams : " + JSON.stringify(params));
			}
		},
		WriteJsonFile: function () {
			cordova.exec(function (result) {
				alert("success")
			}, function (result) {
				alert(result)
			}, "ClipsoftPlugin", "WriteJsonFile", []);
		},
		createJSONFile: function (instcd) {
			cordova.exec(function (result) {
				if(result=="success"){
					if(confirm("설정이 완료되었습니다. 앱이 종료됩니다. 재실행바랍니다.")){
						COMMON.plugin.terminateApp();
					}else{ 
						COMMON.plugin.terminateApp();
					} 
				}else{
					if(confirm("생성에 실패하여 앱이 종료됩니다. 정보운영팀에 문의바랍니다.")){
						COMMON.plugin.terminateApp();
					}else{ 
						COMMON.plugin.terminateApp();
					} 
				}
			}, function (result) { 
			}, "ClipsoftPlugin", "createJSONFile", [instcd]);
		},
		checkJSONFile: function () {
			cordova.exec(function (result) {
				if(result=="false"){ 
					$('#selectHospital').css('display','block');
					$('.dimmed').css('display','block'); 
				}else{   
					localStorage.removeItem("ajaxUrl");
					localStorage.removeItem("ajaxNuUrl"); 
					
					localStorage.setItem("instno",result);
					//$('.box2-1').css('background','url("../images/loginLogo'+result+'.png")');
					//$(".box2-1").css('background-image','url(../images/loginLogo012.png)');
					//$("#plzLogo").css({"background":"url(../../images/loginLogo"+result+".png)", 'background-size':'340px', 'background-repeat':'no-repeat'});
					
					if (INTERFACE.USER != "") {
						if (INTERFACE.TYPE == "REAL") { 
							localStorage.setItem("ajaxUrl", "http://emr"+result+".cmcnu.or.kr/cmcnu/.live"); 
							localStorage.setItem("ajaxNuUrl", "http://emr"+result+".cmcnu.or.kr/eform");
							//HIS_EPH = "HIS015";
						}else{
							localStorage.setItem("ajaxUrl", "http://emr"+result+"edu.cmcnu.or.kr/cmcnu/.live"); 
							localStorage.setItem("ajaxNuUrl", "http://emr"+result+"edu.cmcnu.or.kr/eform"); 
							//HIS_EPH = "HIS015EDU";
						}
					}
				}
			}, function (result) { 
			}, "ClipsoftPlugin", "checkJSONFile", []);
		},
		loadingBar: function (show, message) {
			cordova.exec(function (result) {
				alert("success")
			}, function (result) {
				alert(result)
			}, "ClipsoftPlugin", "loadingBar", [show, message]);
		},
		deleteDnValue: function () {
			cordova.exec(function (result) {
				//alert("success") 
			}, function (result) {
				//alert(result)
			}, "ClipsoftPlugin", "deleteDnValue", []);
		},
		timeNotMatch: function () {
			cordova.exec(function (result) {
				//alert("success") 
			}, function (result) {
				//alert(result)
			}, "ClipsoftPlugin", "timeNotMatch", []);
		},
		GetDevicesUUID: function () {
			cordova.exec(function (result) {
				//				alert(result) 
				//				COMMON.plugin.storage("set", "DeviceId", result);
				if (INTERFACE.USER != "") {
					if (INTERFACE.TYPE == "REAL") {
						ajax_url = "http://emr.yjh.com/cmcnu/.live";
						ajax_nu_url = "http://emr.yjh.com/eform";
						//HIS_EPH = "HIS015";
					} else {
						ajax_url = "http://emredu.yjh.com/cmcnu/.live";
						ajax_nu_url = "http://emrdev.yjh.com/eform";
						//HIS_EPH = "HIS015EDU";
					}
				}
				var value = {
					//"patientCode": $("#_detailPatientCode").text() ,
					"deviceId": result
				}
				$.ajax({
					url: ajax_nu_url + '/biz/nu/member/viewer/eForm25/consent/device/mst/get',
					type: 'post',
					timeout: 5000,
					data: {
						parameter: JSON.stringify(value)
					}
				}).done(function (data) {
//					if (data == null) {
//						$('#_DeviceUUID').text("미등록");
//						$('#_DeviceUUID').css('color', 'red');
//					} else {
//						$('#_DeviceUUID').text(data.useDeptNm);
//						$('#_DeviceUUID').css('color', 'white');
//					}
					$('#_DeviceUUID').text("운영");
					$('#_DeviceUUID').css('color', 'white');
				});
//				$('#uuidQrCord').barcode(result, "code128",{showHRI:true,bgColor:"white"});
				//				alert(result);
				new QRCode(document.getElementById("uuidQrCord"), {
					text: result,
					width: 64,
					height: 64
				});
				return result;
			}, function (result) {
				alert(result)
			}, "ClipsoftPlugin", "GetDevicesUUID", []);
		},
		FileUploadTest: function (obj) {
			cordova.exec(function (result) {
				alert("success")
			}, function (result) {
				alert(result)
			}, "ClipsoftPlugin", "FileUploadTest", [obj]);
		},
		versionInfo: function (successFn, errorFn) {
			console.log("VersionInfo");
			if (COMMON.device.isMobile() || COMMON.device.isTablet()) {
				cordova.exec(function (result) {
					console.log(result);
					//					alert(result);
					var data = JSON.parse(result);
					if (successFn != null && successFn != undefined) {
						successFn(data);

					}
				}, function (result) {
					if (errorFn != null && errorFn != undefined) {
						console.log("GetAppVersion 호출 결과 : 실패 ")
						console.log(result);
						console.log("========================");
						errorFn(result + "\n 잠시 후 앱이 종료됩니다.");
						setTimeout(function () {
							//COMMON.plugin.terminateApp();
						}, 1500);
					}
				}, "ClipsoftPlugin", "versionInfo", [""])
			} else {
				var dummyData = [{
					"ConsentClientVersion": "999",
					"EformClientVersion": "999",
					"ConsentClientVersionName": "KCCH_DEV_V1.0.03",
					"EformClientVersionName": "2.5.80",
					"ConsentServerVersion": "1",
					"EformServerVersion": "1",
					"ConsentApkName": "KCCH_DEV_V1.0.02.apk",
					"EformApkName": "CLIP e-Form_v2.5.80-development-release.apk",
					"AppType": "TEST",
					"ReceivingRate": "-70",
					"UseReceivingRate": "Y"
				}];
				successFn(dummyData);
			}
		},
		updateAppDownload: function (type, apkName, successFn, errorFn) {
			console.log("updateAppDownload");
			if (COMMON.device.isMobile() || COMMON.device.isTablet()) {
				cordova.exec(function (result) {
					if (successFn != null) {
						successFn(result);
					}
				}, function (result) {
					if (errorFn != null) {
						errorFn(result);
					}
				}, "ClipsoftPlugin", "updateAppDownload", [type, apkName])
			} else {
				alert("업데이트 앱을 다운로드 중입니다.")
			}
		},
		updateViewerAppDownload: function (type, apkName, successFn, errorFn) {
			console.log("updateViewerAppDownload");
			if (COMMON.device.isMobile() || COMMON.device.isTablet()) {
				cordova.exec(function (result) {
					if (successFn != null) {
						successFn(result);
					}
				}, function (result) {
					if (errorFn != null) {
						errorFn(result);
					}
				}, "ClipsoftPlugin", "updateViewerAppDownload", [type, apkName])
			} else {
				alert("업데이트 앱을 다운로드 중입니다.")
			}
		},
		storage: function (type, params, successFn, errorFn) {
			if (COMMON.device.isMobile() || COMMON.device.isTablet()) {
				if (type == "set") {
					$.each(params, function (key, value) {
						localStorage.setItem(key, value);
					});
				}
				cordova.exec(function (result) {
					if (successFn != null) {
						successFn(result);
					}
				}, function (result) {
					if (errorFn != null) {
						errorFn(result);
					}
				}, "ClipsoftPlugin", "storage", [type, params])
			} else {
				console.log("storage type : " + type);
				if (type == "set") {
					$.each(params, function (key, value) {
						console.log(key + " : " + value);
						localStorage.setItem(key, value);
					});
					if (successFn != "" && successFn != null) {
						successFn();
					}
				} else {
					console.log("params : " + params);
					var data = localStorage.getItem(params);
					if (successFn != "" && successFn != null) {
						successFn(data);
					}
				}
			}
		},
		ftp: function (type, downloadPath, filePaths, successFn, errorFn) {
			console.log("ftp");
			if (COMMON.device.isMobile() || COMMON.device.isTablet()) {
				cordova.exec(function (result) {
					if (successFn != null) {
						successFn(result);
					}
				}, function (result) {
					if (errorFn != null) {
						errorFn(result);
					}
				}, "ClipsoftPlugin", "ftp", [type, downloadPath, filePaths])
			}
		},
		certDown: function (id, pw, successFn, errorFn) {
			console.log("certDown");
			if (COMMON.device.isMobile() || COMMON.device.isTablet()) {
				cordova.exec(function (result) {
					console.log("CertDown Result : " + result);
					successFn();
				}, function (result) {
					errorFn(result);
				}, "ClipsoftPlugin", "certDown", [id, pw])
			} else {
				alert("공인인증서를 다운로드 합니다.");
				successFn();
			}
		},
		certInit: function () {
			console.log("certInit");
			$("#_certPw").val("");
			if (COMMON.device.isMobile() || COMMON.device.isTablet()) {
				cordova.exec(function (result) {
					console.log("certInit");
				}, function (result) {
					errorFn(result);
				}, "ClipsoftPlugin", "certInit", [])
			} else {
				alert("공인인증서를 초기화합니다.");
			}
		},
		compareTime: function (params, successFn, errorFn) {
			console.log("compareTime");
			if (COMMON.device.isMobile() || COMMON.device.isTablet()) {
				cordova.exec(function (result) {}, function (result) {
					if (confirm(result)) {
						//						COMMON.plugin.terminateApp(); 
						COMMON.plugin.moveSetting();
					} else {
						COMMON.plugin.terminateApp();
					}
					// 안드로이드 설정 시간 화면으로 이동 
				}, "ClipsoftPlugin", "compareTime", [params])
			} else {
				successFn(params);
			}
		},
		imageView: function (params, successFn, errorFn) {
			console.log("imageView");
			if (COMMON.device.isMobile() || COMMON.device.isTablet()) {
				cordova.exec(function (result) {
					if (successFn != null) {
						successFn(result);
					}
				}, function (result) {
					if (errorFn != null) {
						errorFn(result);
					}
				}, "ClipsoftPlugin", "imageView", [params])
			}
		},
		showReceivingRate: function (isStart) {
			console.log("showReceivingRate");
			if (COMMON.device.isMobile() || COMMON.device.isTablet()) {
				cordova.exec(function (result) {}, function (result) {}, "ClipsoftPlugin", "showReceivingRate", [isStart])
			}
		},
		moveSetting: function () {
			console.log("moveSetting");
			// 시간 설정 화면으로 이동
			if (COMMON.device.isMobile() || COMMON.device.isTablet()) {
				cordova.exec(function () {

				}, function () {}, "ClipsoftPlugin", "moveSetting", [])
			}
		},
		wifiCheck: function (successFn) {
			console.log("wifiCheck");
			var result_data;
			// 시간 설정 화면으로 이동
			if (COMMON.device.isMobile() || COMMON.device.isTablet()) {
				cordova.exec(function (result) {
					successFn(result);
					//					if(result<-90){
					//alert("WIFI 수신 감도가 좋지 않습니다. 느려지거나 작동하지 않을 수 있습니다.");
					//					} 
				}, function (result) {
					//alert(result); 
				}, "ClipsoftPlugin", "wifiCheck", [])
			}
		},
		print: function (resource ,successFn) {
			// 시간 설정 화면으로 이동
			if (COMMON.device.isMobile() || COMMON.device.isTablet()) {
				cordova.exec(function (result) {
					successFn(result); 
				}, function (result) { 
				}, "ClipsoftPlugin", "print", [resource])
			}
		}
	};
}


function loadingbar_display() {
	if ($('#loading_bar').is(":visible")) {
		console.log("[로딩바] : 비활성화");
		$('#loading_bar').css('display', 'none');
	} else {
		console.log("[로딩바] : 활성화");
		$('#loading_bar').css('display', 'block');
	};
}

//앱 업데이트 확인 이벤트 
function isAppUpdate() {
	COMMON.plugin.versionInfo(isAppUpdateSuccessHandler, errorHandler);
};


//앱 업데이트 확인 이벤트 
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