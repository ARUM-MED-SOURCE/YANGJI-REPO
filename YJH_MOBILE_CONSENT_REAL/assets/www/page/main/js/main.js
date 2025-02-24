var requestMap = COMMON.util.requestMap();

$(document).ready(function () {
	 
    var cosignListValue = 0;
    setTimeout(function () {
        cosignListValue = searchCosign();
        if (cosignListValue > 0) {
            $('.cosign_new_img').css('display', 'block');
        } else {
            $('.cosign_new_img').css('display', 'none');
        }
    }, 10000); 

    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
    if (month < 10) {
        month = "0" + month;
    }
    if (day < 10) {
        day = "0" + day;
    }
    today = year + "" + month + "" + day;

    if (INTERFACE.USER != "") {
        if (INTERFACE.TYPE == "REAL") {
            ajax_url = "http://emr.yjh.com/cmcnu/.live";
            ajax_nu_url = "http://emr.yjh.com/eform";
            HIS_EPH = "HIS204";
        } else {
            ajax_url = "http://emrdev.yjh.com/cmcnu/.live";
            ajax_nu_url = "http://emrdev.yjh.com/eform";
//            ajax_nu_url = "http://10.110.54.60:8080/eform";
            HIS_EPH = "HIS204EDU";
        }
//    	ajax_url = localStorage.getItem("ajaxUrl");
//    	ajax_nu_url = localStorage.getItem("ajaxNuUrl");
    }
    Date.prototype.YYYYMMDDHHMMSS = function () {
        var yyyy = this.getFullYear().toString();
        var MM = pad(this.getMonth() + 1, 2);
        var dd = pad(this.getDate(), 2);
        var hh = pad(this.getHours(), 2);
        var mm = pad(this.getMinutes(), 2);
        var ss = pad(this.getSeconds(), 2);

        return yyyy + MM + dd + hh + mm + ss;
    };

    function getDate() {
        d = new Date();
        //alert(d.YYYYMMDDHHMMSS());
    }

    function pad(number, length) {

        var str = '' + number;
        while (str.length < length) {
            str = '0' + str;
        }

        return str;

    }
    
     
    // 검사실 이벤트
   $('#_laboratoryClnDept').on("change",function(){
	   var selectVal = $('#_laboratoryClnDept option:selected').val();
	   
	   searchCheckupRoom(selectVal);
   }) ;

   $("#_laboratoryInit").on("click", function () {
	   laboratoryInit();
   });
   
   function laboratoryInit(){
	    commonInit();
	    selectboxInit("_laboratoryClnDept"); // 병동 초기화
	    selectboxInit("_laboratoryRoom"); // 진료과 초기화
	    selectboxInit("_laboratoryCheck"); // 담당의사 초기화 
	    $("#_laboratoryStartDate").val(getDay("-", "", "", -3));
	    $("#_laboratoryEndDate").val(getDay("-", "", "", ""));
	    // fnSearchCharge("");
	    // fnSearchDoctor(""); 
	    //searchCharge(""); 
   }
   
	
	//   instcd:기관코드
	//   pid: 환자번호 (없을땐 빈값)
	//   fromdd: 시작일(필수)
	//   todd: 종료일(필수)
	//   prcpexecdeptcdid: 지원부서코드(필수)
	//   srchflag: 미예약/예약/실시 구분값(필수)
	//   basecd: 검사실코드(전체일땐 빈값)
   $("#_laboratorySearch").click(function(){

       commonInit();
       
       $("#_laboratoryList ul").remove(); 

	   var fromdd = $('#_laboratoryStartDate').val(); 
	   fromdd = fromdd.replace(/-/gi, "");
	   var todd = $('#_laboratoryEndDate').val();
	   todd = todd.replace(/-/gi, "");
	   var pid = $('#_laboratoryPatientCode').val();
	   var prcpexecdeptcdid = $("#_laboratoryClnDept option:selected").val();
	   var basecd = ($("#_laboratoryRoom option:selected").text() == "--전체--" ? "" : $("#_laboratoryRoom option:selected").val());
	   var srchflag = $('#_laboratoryCheck option:selected').val();
	   //http://emrdev.yjh.com/cmcnu/.live?submit_id=DRMRF00126&business_id=mr&instcd=204&formdd=20200107&todd=20200710&pid=&prcpexecdeptcdid=3050109000&srchflag=acpt&bacd=
	   if(prcpexecdeptcdid == "" || srchflag == ""){
		   COMMON.plugin.loadingBar("hide", "");
		   alert("지원 부서와 구분은 필수로 선택되어야 합니다.");
	   }else{ 
	       COMMON.plugin.loadingBar("show", "검색중입니다."); 
		   $.ajax({
	           url: ajax_url,
	           type: 'post',
	           data: 'submit_id=DRMRF00126&business_id=mr&instcd=204&fromdd='+ fromdd +'&todd='+ todd 
	           +'&pid='+ pid+'&prcpexecdeptcdid='+ prcpexecdeptcdid+'&srchflag='+ srchflag+'&basecd='+ basecd, 
	           dataType: 'xml',
	           timeout: 40000,
	           success: function (result) {   
	               if ($(result).find('list').length > 0) {
	                   $(result).find('list').each(function(){ 
                           var ul = $("#_laboratoryListTemplate ul").clone();
                           var sex = ($(this).find('sa').text().replace(/[^a-z]/gi, "") == "M") ? "남자" : "여자";
                           var age = $(this).find('sa').text().replace(/[^0-9]/g, "");
                           var reqdt = $(this).find('reqdt').text();
                           reqdt = reqdt.substring(0,4) + "-" + reqdt.substring(4,6)+"-" + reqdt.substring(6,8) + " "+reqdt.substring(8,10)+":"+reqdt.substring(10,12);
                           var repldt = $(this).find('repldt').text();
                           repldt = repldt.substring(0,4) + "-" + repldt.substring(4,6)+"-" + repldt.substring(6,8) + " "+repldt.substring(8,10)+":"+repldt.substring(10,12);
                          
                           ul.find("._laboratoryPatientCode").text($(this).find('pid').text()); // 환자 등록번호
                           ul.find("._laboratoryPatientName").text($(this).find('hngnm').text()); // 환자 이름  
                           ul.find("._laboratoryAge").text($(this).find('sa').text()); // 회신과 
                           ul.find("._laboratoryDeptName").text(vauleNullCheck($(this).find('orddeptnm').text(), '-')); // 진료과
                           ul.find("._laboratoryPrcpdd").text($(this).find('prcpdd').text()); // 담당의사


                           var visitType = $(this).find('ordtype ').text();
                           var patient = new Object();
                           var Birthday = $(this).find('rrgstno1').text();
                           patient.rrgstfullno = $(this).find('rrgstno1').text();
                           Birthday = Birthday.substring(0, 8);
                           patient.AdmissionDate = $(this).find('orddd').text(); 
                           patient.Age = age;
                           patient.Sex = sex;
                           patient.zipnm = $(this).find('zipnm').text();
                           patient.hometel = $(this).find('hometel').text();
                           patient.mpphontel = $(this).find('mpphontel').text(); 
                           patient.ClnDeptCode = $(this).find('orddeptcd').text();
                           patient.ClnDeptName = $(this).find('orddeptnm').text();
                           patient.ChargeId = $(this).find('atdoctid').text();
                           patient.ChargeName = $(this).find('atdoctnm').text(); // 주치의
                           patient.medispclnm = $(this).find('medispclnm').text(); // 주치의
                           // 2021-10-25
                           patient.medispclid = $(this).find('medispclid').text(); // 주치의
                           patient.DoctorId = $(this).find('orddrid').text();
                           patient.DoctorName = $(this).find('orddrnm').text(); // 진료의
                           patient.PatientCode = $(this).find('pid').text();
                           patient.fulrgstno = Birthday + "XXXXXX" //$(this).find('rrgstno1').text();
                           patient.PatientName = $(this).find('hngnm').text();
                           patient.deptengabbr = $(this).find('deptengabbr').text();
                           patient.Room = $(this).find('roomcd').text();
                           patient.VisitType = visitType;
                           patient.Ward = $(this).find('roomcd').text();
                           patient.Birthday = $(this).find('brthdd').text();
                           patient.Room = $(this).find('roomcd').text();
                           patient.Cretno = $(this).find('cretno').text();
                           patient.diagnm = $(this).find('diagnm').text(); // 진단명 
                           patient.diagengnm = $(this).find('diagengnm').text(); // 진단명
                           patient.wardcd = $(this).find('wardcd').text();  
                           patient.prcpdd = $(this).find('prcpdd').text();  
                           
                           ul.attr("attr-data", JSON.stringify(patient));

                           // 해당 환자 클릭 이벤트
                           ul.on("click", function () {
                               $("#_laboratoryList ul").removeClass("on");
                               $(this).addClass("on");

                               // 동명인여부확인
                               $("._patientDetailInfo").hide();
                               $(".patient_info").attr("attr-data", "");
                               $("._detail").text("");

                               // 연관 리스트 초기화
                               $("#_relationConsentList").empty();
                               // 검색 동의서 리스트 초기화
                               $("#_consentList").empty();
                               // 작성 동의서 리스트 초기화
                               $("#_consentALLList").empty();

                               var isOnEmergencyMenu = $("#_emergency").hasClass("on");

                               var data = $(this).attr("attr-data");
                               // logAlert("해당 환자정보 검색 : " + data);
                               data = JSON.parse(data);
                               isSameName("_laboratoryList", data);
                               if (data.PatientCode != undefined && data.PatientCode != "") { 
                                   fnSearchPatientDetailInfo_submit(data);
                               } else {
                                   alert("환자 상세 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다.")
                               }
                           });  

                  			if($(this).find('saveyn').text() == "Y"){ 
                      			ul.append('<div id="writeStateCircle"></div>'); 
                      		}
                  			if($(this).find('saveyn').text() == "T"){ 
                      			ul.append('<div id="writeStateCircleTemp"></div>'); 
                      		} 
                   			
                               $("#_laboratoryList").append(ul); 
                           
                           //$("#_requestList").append(ul);
                       
	                	   
	                   })
	               }else {
	                    makeNoDataHtml("_laboratoryList");
	                }
	    		   COMMON.plugin.loadingBar("hide", "");
	           },error: function(error){

	    		   COMMON.plugin.loadingBar("hide", "");
	        	   alert(JSON.stringify(error));
	           } 
	
	       });
	   }
	   
   });
    

    setTimeout(function () {
        checkReadyConsent();
    }, 6000); 
    
    // 작성동의서 빠른 조회 - 처방동의서 시간별 체크(1분)
    setInterval(function () {  
    	checkReadyConsent();
    }, 60000);
    
    // 작성동의서 빠른 조회 - 처방동의서
    function checkReadyConsent(){
        var date = new Date();
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var day = date.getDate();
        var dayMinus = day - 1;
        if (month < 10) {
            month = "0" + month;
        }
        if (day < 10) {
            day = "0" + day;
        }
        if (dayMinus < 10) {
        	dayMinus = "0" + dayMinus;
        }
        var time = year + "" + month + "" + day;
        var timeMinus = year + "" + month + "" + dayMinus;
        
        $.ajax({
            url: "http://emr.yjh.com/cmcnu/webapps/mr/mr/formmngtweb/.live",
            type: 'post',
            data: 'submit_id=DRMRF00122&business_id=mr&instcd=204&startdate='+ timeMinus +'&enddate='+ time +'&userdeptcd='+ getLocalStorage("userDeptCode"),
            dataType: 'xml',
            timeout: 40000,
            success: function (result) { 
                if ($(result).find('item').length > 0) {
                    $(result).find('item').each(function(){
                    	var result = ($(this).find('result').text()=="Y") ? true : false;
                    	if(result){  //작성 해야할 것 있음 
                    		$('.consent_new_img').css('display','block');
                    	}else{       //작성 해야할 것 없음
                    		$('.consent_new_img').css('display','none');
                    	}
                    })
                }
            } 

        });
    }


    $('.consentStateCheckBox').click(function(){       
        if($(this).find('input').prop('checked')){
            $(this).find('input').prop('checked',false); 
        }else{
            $(this).find('input').prop('checked',true); 
        }
    }) 
    
    // 2022-02-03
    $('#ELECTR_MULTI, #ELECTR_VERBAL').click(function(){    	
        if($(this).prop('checked')){
            $(this).prop('checked',false); 
        }else{
            $(this).prop('checked',true); 
        }
    });   


    $('#myWriteFlagLabel').on("click", function(){ 
    	if($('#myWriteFlag').prop('checked')){
    		$('#myWriteFlag').prop('checked',false);
    	}else{
    		$('#myWriteFlag').prop('checked',true);    		
    	}    	
    })
    // ======================================================


    // 임시저장 삭제
    // 인증저장 삭제 추가 2021-10-25
    $(document).on("click", ".tempConsentDelete", function () {
        var jsObj = JSON.parse($(this).parent().attr('attr-data')); 
        
        if(jsObj.ConsentState == "TEMP"){ 
            var value = {
                //"patientCode": $("#_detailPatientCode").text() ,
                "consentMstRid": jsObj.consentMstRid,
                "useYn": "N",
                "modifyUserId": getLocalStorage("userId"),
                "modifyUserName": getLocalStorage("userName"),
                "modifyUserDeptCd": getLocalStorage("userDeptCode"),
                "modifyUserDeptName": getLocalStorage("userDeptName")
            };

            if (confirm("임시저장된 서식을 삭제하시겠습니까?")) { 
            	COMMON.plugin.loadingBar("show", "삭제 중입니다.");
                $.ajax({
                    url: ajax_nu_url + '/biz/nu/member/viewer/eForm25/consent/mst/useyn/update',
                    type: 'post',
                    timeout: 40000,
                    data: {
                        parameter: JSON.stringify(value)
                    }
                }).done(function (data) { 
                	if(data.result==true){
                        $.ajax({
                            url: ajax_url,
                            type: 'post',
                            data: 'submit_id=DXMRF00115&business_id=mr&instcd=204&ocrtag=' + jsObj.ocrTag + "&updtdt=" + new Date().YYYYMMDDHHMMSS() + "&updtdeptcd=" + getLocalStorage("userDeptCode") +
                                "&updtuserid=" + getLocalStorage("userId") + "&pagecnt=1" + "&scanpagecnt=0&scanyn=N&mstatcd=E&hstatcd=D&rid=" + jsObj.consentMstRid +
                                "&drsign=-&nrsign=-&patsign=-&procersign=-&etcsign=-&printsource=M",
                            dataType: 'xml',
                            timeout: 40000,
                            success: function (result) {
                            	COMMON.plugin.loadingBar("hide", "");
                                alert("임시저장 동의서 삭제가 완료되었습니다.");
                                fnAllSearchConsent();
                            },
                            error: function (error) {
                            	COMMON.plugin.loadingBar("hide", "");
                                alert("임시저장 동의서 삭제 실패하였습니다. 잠시 후 다시 시도해주세요. \n nU Server Error!");
                            } 
                        }); 
                	}else{
                		COMMON.plugin.loadingBar("hide", "");
                        alert("임시저장 동의서 삭제 실패하였습니다. 잠시 후 다시 시도해주세요.\n eForm Server Error!");            		
                	}
                });
            } else {
                alert("임시저장 삭제를 취소하였습니다.");
            } 
        }else{ 
            if (confirm("인증저장된 서식을 삭제 요청하시겠습니까?")) { 
                var detail = JSON.parse($(".patient_info").attr("attr-data"));                  
                
                var date = new Date();
                var year = date.getFullYear();
                var month = date.getMonth() + 1
                var day = date.getDate();
                if (month < 10) {
                    month = "0" + month;
                }
                if (day < 10) {
                    day = "0" + day;
                }
                var today = year + "" + month + "" + day;
                
            	// jsObj
            	var pid = jsObj.patientCode;
            	var orddd = detail.AdmissionDate;
            	var cretno = jsObj.certNo;
            	var apdd = today;
            	var apdeptcd = getLocalStorage("userDeptCode");
            	var apuserid= getLocalStorage("userId");
            	var signuserid = detail.medispclid;
            	var resncnts = jsObj.ocrTag;
            	var updtcnts= jsObj.FormName; 
            	var jobkindcd = localStorage.getItem("jobkindcd");
            	
            	COMMON.plugin.loadingBar("show", "삭제 요청 중입니다.");
                $.ajax({
                    url: 'http://emr.yjh.com/cmcnu/webapps/mr/mr/formmngtweb/.live?' + 'submit_id=DXMRF00117&business_id=mr&instcd=204&pid='+ pid 
                    +'&orddd='+ orddd +'&cretno='+cretno + '&apdd='+ apdd +'&apdeptcd='+ apdeptcd +'&apuserid='+ apuserid + '&signuserid='+signuserid+'&resncnts='+resncnts+'&updtcnts='+updtcnts + "&jobkindcd="+jobkindcd,
                    type: 'post',
                    dataType: 'xml',
                    timeout: 40000,
                    success: function (result) {  
                        if ($(result).find('result').length > 0) {
                            $(result).find('result').each(function (index) {
                            	if(index == 0){ 
                                	if($(this).find('result').text() == "S"){
                                    	COMMON.plugin.loadingBar("hide", ""); 
                                        alert("인증저장 동의서 삭제요청이 완료되었습니다.");
                                        fnAllSearchConsent(); 
                                	}else if($(this).find('result').text() == "C"){
                                    	COMMON.plugin.loadingBar("hide", ""); 
                                        alert("이미 삭제 요청이 완료된 작성 건입니다."); 
                                	}else{ 
                                    	COMMON.plugin.loadingBar("hide", ""); 
                                        alert("인증저장 동의서 삭제요청이 실패하었습니다. 잠시 후 재시도해주세요.\nERRORRESULT :" + $(this).find('result').text());
                                	}
                            	}
                            });
                        }  
                    },
                    error: function (error) {
                    	COMMON.plugin.loadingBar("hide", "");
                        alert("인증저장 동의서 삭제요청이 실패하였습니다. 잠시 후 다시 시도해주세요. \n nU Server Error!");
                    } 
                })
            }else{
                alert("인증저장 삭제를 취소하였습니다.");
            }
        }
    });


    // 코사인 시간별 체크(5분)
    setInterval(function () {
        var cosignListValue = 0;
        setTimeout(function () {
            cosignListValue = searchCosign();
            if (cosignListValue > 0) {
                $('.cosign_new_img').css('display', 'block');
            } else {
                $('.cosign_new_img').css('display', 'none');
            }
        }, 10000);
    }, 300000);


    $('.agree_3').click(function () {
        $('.select_tab').removeClass('on');
        $(this).addClass('on');
    });

    // 도움말
    $('.btn-arrow-right').click(function () {
        $('.help_box_1').css('display', 'none');
        $('.help_box_2').css('display', 'block');
        $('.btn-arrow-left').removeClass('off');
        $('.btn-arrow-right').addClass('off');
        $('.page_1').removeClass('on');
        $('.page_2').addClass('on');
    });

    $('.btn-arrow-left').click(function () {
        $('.help_box_1').css('display', 'block');
        $('.help_box_2').css('display', 'none');
        $('.btn-arrow-right').removeClass('off');
        $('.btn-arrow-left').addClass('off');
        $('.page_1').addClass('on');
        $('.page_2').removeClass('on');
    });

    $('.btn-close').click(function () {
        hideHelp();
    });

    var cc = new Object;
    var LoginDeptCode = new Object;
    var LoginDeptMinName = new Object;
    LoginDeptName = JSON.parse(localStorage.getItem("LoginDeptName"));
    LoginDeptCode = JSON.parse(localStorage.getItem("LoginDeptCode"));
    LoginDeptMinName = JSON.parse(localStorage.getItem("LoginDeptMinName"));

     for (var index = 0; index < LoginDeptName.length; index++) {
         $("#_userDeptChange").append($('<option>', {
             class: LoginDeptMinName[index],
             value: LoginDeptCode[index],
             text: LoginDeptName[index]
         })); 
     }

    // ====================== 의뢰탭 시작 ===================

    // 진료과 변경 이벤트
    $("#_requestDept").on("change", function () {
        var clnDept = $("#_requestDept option:selected").val();
        SearchRequestDoctor(clnDept);
    });

    // 검색 이벤트
    $("#_requestSearch").on("click", function () {
        $("#_requestList ul").remove();
        var fromdd = $("#_requestCalender").val(); // 진료일
        var todd = $("#_requestCalender_2").val(); // 진료일
        fromdd = fromdd.replace(/-/gi, "");
        todd = todd.replace(/-/gi, "");

        var orddeptcd = ($("#_requestDept option:selected").val() == "") ? "-" : $("#_requestDept option:selected").val(); // 진료과
        var orddrid = ($("#_requestDoc option:selected").val() == "") ? "-" : $("#_requestDoc option:selected").val(); // 진료의
        var reqflag = ($("#_requestCheck option:selected").val() == "") ? "-" : $("#_requestCheck option:selected").val(); // 의뢰구분
        var replyn = ($("#_requestReply option:selected").val() == "") ? "-" : $("#_requestReply option:selected").val(); // 회신구분
        var ioflag = ($("#_requestDiag option:selected").val() == "") ? "-" : $("#_requestDiag option:selected").val(); // 진료구분
        var writeState = $('#_requestWriteState option:selected').val(); // 작성상태
        var orddeptnm = $("#_requestDept option:selected").text(); // 회신과
        COMMON.plugin.wifiCheck(wifiCheckFn);

        var checkVal = localStorage.getItem("wifiCheckVal");
        if (checkVal < -90) {
            alert("WIFI 수신 감도가 좋지 않습니다. WIFI 통신이 잘 되는 곳에서 사용 바랍니다.");
        } else {
            if (searchRequestConditionCheck()) { 
                //loadingbar_display();
                COMMON.plugin.loadingBar("show", "검색중입니다.");
                $.ajax({
                    url: "http://emr.yjh.com/cmcnu/webapps/mr/mr/formmngtweb/.live",
                    data: "submit_id=DRMRF00123&business_id=mr&instcd=204&pid=&fromdd="+fromdd+"&todd="+todd+"&orddeptcd="+orddeptcd+"&orddrid="+orddrid
                            +"&reqflag="+reqflag+"&replyn="+replyn+"&ioflag="+ioflag,
                    type: 'get',
                    dataType: 'xml',
                    timeout: 40000,
                    success: function (result) {
                        if ($(result).find('list').length > 0) {
                            $(result).find('list').each(function () {
                                var ul = $("#_requestListTemplate ul").clone();
                                var sex = ($(this).find('sa').text().replace(/[^a-z]/gi, "") == "M") ? "남자" : "여자";
                                var age = $(this).find('sa').text().replace(/[^0-9]/g, "");
                                var reqdt = $(this).find('reqdt').text();
                                reqdt = reqdt.substring(0,4) + "-" + reqdt.substring(4,6)+"-" + reqdt.substring(6,8) + " "+reqdt.substring(8,10)+":"+reqdt.substring(10,12);
                                var repldt = $(this).find('repldt').text();
                                repldt = repldt.substring(0,4) + "-" + repldt.substring(4,6)+"-" + repldt.substring(6,8) + " "+repldt.substring(8,10)+":"+repldt.substring(10,12);
                               
                                ul.find("._requestPatientCode").text($(this).find('pid').text()); // 환자 등록번호
                                ul.find("._requestAge").text($(this).find('sa').text()); // 환자 나이 / 성별 
                                ul.find("._requestPatientName").text($(this).find('hngnm').text()); // 환자 이름
                                ul.find("._requestRequestDept").text($(this).find('reqdeptnm').text()); // 의뢰과
                             
                                ul.find("._requestRequestDate").text((($(this).find('reqdt').text() == "") ? "-" : reqdt)); // 의뢰일자
                                ul.find("._requestRequestDoc").text(($(this).find('requsernm').text() == "") ? "-" : $(this).find('requsernm').text()); // 의뢰의사

                                ul.find("._requestReplyDept").text(orddeptnm); // 회신과
                                ul.find("._requestReplyDate").text((($(this).find('repldd').text() == "") ? "-" : repldt)); // 회신일자
                                ul.find("._requestReplyDoc").text(($(this).find('recvusernm').text() == "") ? "-" : $(this).find('recvusernm').text()); // 회신의사
                                

                                var visitType = $(this).find('ordtype ').text();
                                var patient = new Object();
                                var Birthday = $(this).find('rrgstno1').text();
                                patient.rrgstfullno = $(this).find('rrgstno1').text();
                                Birthday = Birthday.substring(0, 8);
                                patient.AdmissionDate = $(this).find('orddd').text(); 
                                patient.Age = age;
                                patient.Sex = sex;
                                patient.zipnm = $(this).find('zipnm').text();
                                patient.hometel = $(this).find('hometel').text();
                                patient.mpphontel = $(this).find('mpphontel').text(); 
                                patient.ClnDeptCode = $(this).find('orddeptcd').text();
                                patient.ClnDeptName = $(this).find('orddeptnm').text();
                                patient.ChargeId = $(this).find('atdoctid').text();
                                patient.ChargeName = $(this).find('atdoctnm').text(); // 주치의
                                patient.medispclnm = $(this).find('medispclnm').text(); // 주치의
                                // 2021-10-25
                                patient.medispclid = $(this).find('medispclid').text(); // 주치의
                                patient.DoctorId = $(this).find('orddrid').text();
                                patient.DoctorName = $(this).find('orddrnm').text(); // 진료의
                                patient.PatientCode = $(this).find('pid').text();
                                patient.fulrgstno = Birthday + "XXXXXX" //$(this).find('rrgstno1').text();
                                patient.PatientName = $(this).find('hngnm').text();
                                patient.deptengabbr = $(this).find('deptengabbr').text();
                                patient.Room = $(this).find('roomcd').text();
                                patient.VisitType = visitType;
                                patient.Ward = $(this).find('roomcd').text();
                                patient.Birthday = $(this).find('brthdd').text();
                                patient.Room = $(this).find('roomcd').text();
                                patient.Cretno = $(this).find('cretno').text();
                                patient.diagnm = $(this).find('diagnm').text(); // 진단명
                                patient.diagengnm = $(this).find('diagengnm').text(); // 진단명
                                patient.wardcd = $(this).find('wardcd').text(); // 진단명

                                //===============의뢰탭 고유값=======================
                                patient.dschdd = $(this).find('dschdd').text(); // 퇴원일자
                                patient.cnstflagd = $(this).find('cnstflag').text(); // 의뢰구분
                                patient.recvdeptcd = $(this).find('recvdeptcd').text(); // 회신과코드
                                patient.recvdeptnm = $(this).find('recvdeptnm').text(); // 회신과명
                                patient.recvuserid = $(this).find('recvuserid').text(); // 회신의ID
                                patient.recvusernm = $(this).find('recvusernm').text(); // 회신의명
                                patient.replyn = $(this).find('replyn').text(); // 회신여부 
                                patient.repldd = $(this).find('repldd').text(); // 회신일자
                                patient.replhm = $(this).find('replhm').text(); // 회신시간
                                patient.repldt = $(this).find('repldt').text(); // 회신일시
                                patient.reqdeptcd = $(this).find('reqdeptcd').text(); // 의뢰과코드
                                patient.reqdeptnm = $(this).find('reqdeptnm').text(); // 의뢰과명
                                patient.requserid = $(this).find('requserid').text(); // 의뢰과ID
                                patient.requsernm = $(this).find('requsernm').text(); // 의뢰의명
                                patient.reqdd = $(this).find('reqdd').text(); // 의뢰일자
                                patient.reqhm = $(this).find('reqhm').text(); // 의뢰시간
                                patient.reqdt = $(this).find('reqdt').text(); // 의뢰일시
                                patient.eryn = $(this).find('eryn').text(); // 응급여부

                                ul.attr("attr-data", JSON.stringify(patient));
 
                                // 해당 환자 클릭 이벤트
                                ul.on("click", function () {
                                    $("#_requestList ul").removeClass("on");
                                    $(this).addClass("on");

                                    // 동명인여부확인
                                    $("._patientDetailInfo").hide();
                                    $(".patient_info").attr("attr-data", "");
                                    $("._detail").text("");

                                    // 연관 리스트 초기화
                                    $("#_relationConsentList").empty();
                                    // 검색 동의서 리스트 초기화
                                    $("#_consentList").empty();
                                    // 작성 동의서 리스트 초기화
                                    $("#_consentALLList").empty();

                                    var isOnEmergencyMenu = $("#_emergency").hasClass("on");

                                    var data = $(this).attr("attr-data");
                                    // logAlert("해당 환자정보 검색 : " + data);
                                    data = JSON.parse(data);
                                    isSameName("_requestList", data);
                                    if (data.PatientCode != undefined && data.PatientCode != "") {

                                        fnSearchPatientDetailInfo_submit(data);
                                    } else {
                                        alert("환자 상세 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다.")
                                    }
                                });  
                                
                                if(writeState =="" || writeState =="A"){  
                        			if($(this).find('saveyn').text() == "Y"){ 
                            			ul.append('<div id="writeStateCircle"></div>'); 
                            		}
                        			if($(this).find('saveyn').text() == "T"){ 
                            			ul.append('<div id="writeStateCircleTemp"></div>'); 
                            		} 
                                    $("#_requestList").append(ul);
                            	}else if(writeState=="N"){
                            		if($(this).find('saveyn').text() == "N"){  
                                        $("#_requestList").append(ul);
                            		}
                            	}else if(writeState=="T"){
                            		if($(this).find('saveyn').text() == "T"){ 
                            			ul.append('<div id="writeStateCircleTemp"></div>');
                                        $("#_requestList").append(ul);
                            		}
                            	}
 
                            	else{
                            		if($(this).find('saveyn').text() == "Y"){ 
                            			ul.append('<div id="writeStateCircle"></div>');
                                        $("#_requestList").append(ul);
                            		}
                            	}  
                                
                                //$("#_requestList").append(ul);
                            });
                        } else {
                            makeNoDataHtml("_requestList");
                        }
                        COMMON.plugin.loadingBar("hide", "");
                        isNextRequest();
                    },
                    error: function (error) {
                    	COMMON.plugin.loadingBar("hide", "");
                        alert("환자 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + JSON.stringify(error));
                    }

                })
            }
        }

    });

    // ======================= 의뢰탭 종료 ======================

    //  ===================== 시술의 시작 ====================

    // 시술의 팝업 - 시술의 추가
    $('.Consent_add_Treatment').click(function () {
        var tereatName = $('#addTreatmentName').val();
        if (tereatName == "") {
            alert("빈 값은 추가할 수 없습니다. 확인 바랍니다.");
        } else {
            $('.treatementExam').css('display', 'none');
            $('#treatmentRadio').append('<div class="treatementName" style="width:100%;height:50px; border-bottom:solid 1px gray;display:table;">' +
                '<div style="display:table-cell;vertical-align:middle; width:310px;height:50px;text-align:left; padding-left:15px; " class="treatmentList">' + tereatName + '</div>' +
                '<button class="deleteTreatment" style="display:table-cell;vertical-align:middle;margin-right:5px;width:30px;height:30px;margin-top:5px;"><img src="../../images/error.png" style="width:100%;height:100%;border:0px" alt="" /></button></div>');
            $('#addTreatmentName').val('');
            localStorage.setItem('treatmentVal', $('#treatmentRadio').html());
        }
    });

    // 시술의 팝업 - 시술의 선택
    $(document).on('click', '.treatementName', function () {
        $('.dimmed').css('display', 'none');
        $('.Treatment_List').css('display', 'none');
        if (getLocalStorage("signPwd") == "" || getLocalStorage("signPwd") == null || getLocalStorage("signPwd") == undefined) {
            $('#sign_popup').css('display', 'block');
            $('#_certpassword').focus();
            check_consent = $(this);
        } else {
            if ($(this).text() == "입력 없음") {
                localStorage.setItem('treatmentnm', "");
            } else {
                localStorage.setItem('treatmentnm', $(this).find('.treatmentList').text());
            }
            var consent = new Object();
            consent = COMMON.LOCAL.eform.myConsent;
            if (consent.cosignFlag == "1") {
                var consent_obj = {
                    "patientCode": consent.patientCode,
                    "FormId": consent.formId,
                    "FormName": consent.formName,
                    "FormVersion": consent.formVersion,
                    "treatmentnm": $(this).find('.treatmentList').text()
                };
                if ($(this).text() == "입력 안함") {
                    if (consent.nursCertYn == "N" && localStorage.getItem("jobkindcd").substring(0, 2) != "03") {
                        searchAppVersionMyConsent(consent_obj, "nurscertEnd");
                    } else {
                        searchAppVersionMyConsent(consent_obj, "end");
                    }
                } else {
                    searchAppVersionMyConsent(consent_obj, "endAddDoc");
                }
            } else {
                searchAppEndVersion();
            }
        }
    });

    // 시술의 팝업 - 시술의 삭제 
    $(document).on('click', '.deleteTreatment', function () {
        $(this).parent().remove();
        if ($('#treatmentRadio').find('div').length == 1) {
            $('.treatementExam').css('display', 'block');
        }
        localStorage.setItem('treatmentVal', $('#treatmentRadio').html());
        return false;
    });

    $('#treatCloseBtn').click(function () {
        $('.dimmed').css('display', 'none');
        $('.Treatment_List').css('display', 'none');
    });
    // =============================================================== 

    // ==========================작성 동의서 빠른 조회 시작==============================

    // 작성 동의서 빠른 조회 - 내 서식 추가/삭제(전체동의서검색)
    $(document).on('click', '._consentListMyConsent', function () {
        var thisobj = $(this);
        if ($(this).find('input').prop('checked')) {
            var data = $(this).parent().attr("attr-data");
            var dataObj = JSON.parse(data);
            var value = {
                "formCd": dataObj.FormCd,
                "formId": dataObj.FormId,
                "userId": getLocalStorage("userId"),
                "fvrtYn": "N"
            }
            //loadingbar_display();
            COMMON.plugin.loadingBar("show", "내 서식 추가 중입니다.");
            $.ajax({
                url: ajax_nu_url + '/biz/nu/member/viewer/eForm25/consent/user/form/save',
                type: 'post',
                timeout: 40000,
                data: {
                    parameter: JSON.stringify(value)
                }
            }).done(function (data) {
            	COMMON.plugin.loadingBar("hide", "");
                thisobj.find('input').prop('checked', false);
                thisobj.find('img').attr('src', '../../images/star.png');
            });
        } else {
            var data = $(this).parent().attr("attr-data");
            var dataObj = JSON.parse(data);
            var value = {
                "formCd": dataObj.FormCd,
                "formId": dataObj.FormId,
                "userId": getLocalStorage("userId"),
                "fvrtYn": "Y"
            };
            COMMON.plugin.loadingBar("show", "내 서식 추가 중입니다.");
            $.ajax({
                url: ajax_nu_url + '/biz/nu/member/viewer/eForm25/consent/user/form/save',
                type: 'post',
                timeout: 40000,
                data: {
                    parameter: JSON.stringify(value)
                }
            }).done(function (data) {
            	COMMON.plugin.loadingBar("hide", "");
                thisobj.find('input').prop('checked', true);
                thisobj.find('img').attr('src', '../../images/star_check.png');
            });
        }
    });

    // 작성동의서 빠른 조회 - 진료과/병동 이벤트
    $('#treateMentDept').on('change', function () {
        var selectVal = $('#treateMentDept option:selected').val();
        if (selectVal == "") {
            $('#treateMentWard').prop('disabled', false);
        } else {
            $('#treateMentWard').prop('disabled', true);
        }

        var clnDept = $("#treateMentDept option:selected").val();
        if (clnDept == "2370100000") {
            $('#myconsentChange').text('구분');
        } else {
            $('#myconsentChange').text('진료의');
        }

        searchDoctorTreat(selectVal);
    });

    $('#treateMentWard').on('change', function () {
        var selectVal = $('#treateMentWard option:selected').val();
        if (selectVal == "") {
            $('#treateMentDept').prop('disabled', false);
        } else {
            $('#treateMentDept').prop('disabled', true);
        }
    });

    // 작성동의서 빠른 조회 - 검색 
    $('#myConsentSearch').click(function () {
        var fvrtYn = $('#myConsentYn').is(":checked") ? "Y" : "N";
        var patientCode = $('#myConsentPatientCd').val();
        var clnDeptCd = $("#treateMentDept option:selected").val();
        var wardCd = $("#treateMentWard option:selected").val();
        var atDoctId = $("#treateMentDoc option:selected").val();
        var visitType = $('input:radio[name=treatMentVisitType]:checked').val();
        // 2021-12-02
        var myWriteFlag = $('#myWriteFlag').is(":checked") ? "Y" : "";
        var consentStateParam = "";
        if($('#NEW').prop('checked')){
            if(consentStateParam==""){
                consentStateParam = consentStateParam+ "NEW";
            }else{
                consentStateParam = consentStateParam+ ",NEW";
            }
        }
        if($('#TEMP').prop('checked')){
            if(consentStateParam==""){
                consentStateParam = consentStateParam+ "TEMP";
            }else{
                consentStateParam = consentStateParam+ ",TEMP";
            }
        } 
        if($('#ELECTR_TEMP').prop('checked')){
            if(consentStateParam==""){
                consentStateParam = consentStateParam+ "ELECTR_TEMP";
            }else{
                consentStateParam = consentStateParam+ ",ELECTR_TEMP";
            }
        } 
        if($('#ELECTR_CMP').prop('checked')){
            if(consentStateParam==""){
                consentStateParam = consentStateParam+ "ELECTR_CMP";
            }else{
                consentStateParam = consentStateParam+ ",ELECTR_CMP";
            }
        } 
 
        
        // 2022-02-03
        var verbalMultiState = "";
        if($('#ELECTR_VERBAL').prop('checked')){
            if(verbalMultiState=="")
            	verbalMultiState = verbalMultiState+ "'V,N'";
            else
            	verbalMultiState = verbalMultiState+ ",'V,N'";
            
            
            if(consentStateParam=="")
            	consentStateParam = 'ELECTR_CMP';
        } 
        if($('#ELECTR_MULTI').prop('checked')){
            if(verbalMultiState=="")
            	verbalMultiState = verbalMultiState+ "'M,N'";
            else
            	verbalMultiState = verbalMultiState+ ",'M,N'";
            
            if(consentStateParam=="")
            	consentStateParam = 'ELECTR_CMP';
        }  
         
        
        //var consentState = $('input:radio[name=treatMentConsentType]:checked').val();
        var SearchResultAry = new Array();

        // 2021-12-02 
        // 2022-02-03
        var value = {
            "fvrtYn": fvrtYn,
            "patientCode": patientCode,
            "clnDeptCd": clnDeptCd,
            "wardCd": wardCd,
            "atDoctId": atDoctId,
            "visitType": visitType,
            "startDate": $("#_treatStartDate").val().replace(/-/g, ""),
            "endDate": $("#_treatEndDate").val().replace(/-/g, ""),
            "consentState": consentStateParam,
            "userId": getLocalStorage("userId"),
            "userDeptCd": getLocalStorage("userDeptCode"),
            "myWriteFlag" : myWriteFlag,
            "verbalMultiState" : verbalMultiState
        };  
        
        // 20210603 작성동의서 빠른조회
        var startDate = $("#_treatStartDate").val().replace(/-/g, "");
        var endDate = $("#_treatEndDate").val().replace(/-/g, "");
        var stDate = new Date(startDate.substring(0,4),startDate.substring(4,6),startDate.substring(6,8));
        var edDate = new Date(endDate.substring(0,4),endDate.substring(4,6),endDate.substring(6,8));
        
        var btMs = edDate.getTime() - stDate.getTime() ;
        var btDay = btMs / (1000*60*60*24) ;  


        // 2021-10-25  
        var checkDays = 4;
        if(fvrtYn == "Y" || patientCode != "" || clnDeptCd != "" || wardCd != "" || atDoctId != "" || myWriteFlag == "Y"){
            checkDays = 11;
        }    
            
        if(btDay > checkDays-1 || btDay < -1 ){
           alert("출력일 범위가 "+ (checkDays-1) +"일을 넘을 수 없습니다. 출력일을 재설정해주세요.");
        }else{
        // ---------------------------------------------------------------------- 
        	   COMMON.plugin.loadingBar("show", "검색 중입니다.");
               $.ajax({
                   url: ajax_nu_url + '/biz/nu/member/viewer/eForm25/consent/mst/detail/get',
                   type: 'post',
                   timeout: 40000,
                   data: {
                       parameter: JSON.stringify(value)
                   }
               }).done(function (data) {
                   $('.treatMentConsent').empty();
                   if (data.length == 0) {
                       $('.treatMentConsent').append('<div style="width: 100%;height: 50%;text-align: center;vertical-align: middle;margin-top: 40%;">조회된 결과가 없습니다.</div>');
                   } else {
                	// 2021-12-02 
                	   var idx = 0;
                       $.each(data, function (index, item) { 
                       	// 2021-12-02 
                    	   idx = idx+1;
                           var ul = $('#treatMentConsentTemplates').clone();
                           var DATA_JSON = JSON.parse(JSON.stringify(data[index]));
                           ul.css('display', 'block');

                           // 2021-12-02 
                           if (DATA_JSON.consentState == "TEMP") { // 임시저장
                               if (DATA_JSON.cosignDeptCode == "-1" || DATA_JSON.cosignDeptCode == undefined) { // 코사인 지정X
                            	   // 2021-10-25 
                            	   if(DATA_JSON.useYn != "Y"){ 
	                                   ul.find('.btn_flow').addClass('flow_tmp');
	                                   ul.find('.btn_flow').removeClass('flow_end');
	                                   ul.find('.btn_flow').removeClass('flow_cosign');
	                                   ul.find('.btn_flow').removeClass('flow_new');
	                               	   ul.find('.btn_flow').removeClass('flow_ing');
	                                   ul.find('.flow_treatment').css('visibility', 'hidden');
	                                   ul.find('.btn_flow').text('임시삭제');
	                                   ul.css('opacity','0.3');
                            	   }else{ 
	                                   ul.find('.btn_flow').addClass('flow_tmp');
	                                   ul.find('.btn_flow').removeClass('flow_end');
	                                   ul.find('.btn_flow').removeClass('flow_cosign');
	                                   ul.find('.btn_flow').removeClass('flow_new');
	                               	   ul.find('.btn_flow').removeClass('flow_ing');
	                                   ul.find('.flow_treatment').css('visibility', 'hidden');
	                                   ul.find('.btn_flow').text('임시저장');   
                            	   }
                               } else { // 코사인 지정O
                                   ul.find('.btn_flow').addClass('flow_cosign');
                                   ul.find('.btn_flow').removeClass('flow_end');
                                   ul.find('.btn_flow').removeClass('flow_tmp');
                                   ul.find('.btn_flow').removeClass('flow_new');
                               	   ul.find('.btn_flow').removeClass('flow_ing');
                                   ul.find('.flow_treatment').css('visibility', 'hidden');
                                   ul.find('.btn_flow').text('Co-sign');
                               }
                           } else if(DATA_JSON.consentState == "ELECTR_TEMP"){
                           	   ul.find('.btn_flow').addClass('flow_ing');
                               ul.find('.btn_flow').removeClass('flow_end');
                               ul.find('.btn_flow').removeClass('flow_new');
                               ul.find('.btn_flow').removeClass('flow_tmp');
                               ul.find('.btn_flow').removeClass('flow_cosign');
                               ul.find('.flow_treatment').css('visibility', 'hidden');
                               ul.find('.btn_flow').text("진행("+ DATA_JSON.certCnt + "/" + DATA_JSON.certNeedCnt +")");
                           } else if(DATA_JSON.consentState == "NEW"){
                           	   ul.find('.btn_flow').addClass('flow_new');
                               ul.find('.btn_flow').removeClass('flow_end');
                               ul.find('.btn_flow').removeClass('flow_ing');
                               ul.find('.btn_flow').removeClass('flow_tmp');
                               ul.find('.btn_flow').removeClass('flow_cosign');
                               ul.find('.flow_treatment').css('visibility', 'hidden'); 
                               DATA_JSON['nowrite'] = "true";
                               ul.find('.btn_flow').text("작성대기");
                           } 
 
                           else { // 인증저장
                               ul.find('.btn_flow').addClass('flow_end'); 
                               ul.find('.btn_flow').removeClass('flow_new');
                               ul.find('.btn_flow').removeClass('flow_tmp');
                               ul.find('.btn_flow').removeClass('flow_cosign');
                           	   ul.find('.btn_flow').removeClass('flow_ing');
                               ul.find('.btn_flow').text('완료');
                               if (DATA_JSON.opdrYn == "Y" && DATA_JSON.opdrSignYn == "N") { // 시술의 미비
                                   ul.find('.flow_treatment').css('visibility', 'visible'); 
                               } else {
                                   ul.find('.flow_treatment').css('visibility', 'hidden');
                               }
                               
                               // 2022-02-03
                               // 2021-10-25
                               if(DATA_JSON.verbalMultiFlag == "V,N"){
                                   ul.find('.flow_treatment').css('visibility', 'visible'); 
                            	   ul.find('.flow_treatment').text("구두동의");
                                   ul.find('.flow_treatment').css('background-color', '#F4B183'); 
                               }else if(DATA_JSON.verbalMultiFlag == "M,N"){
                                   ul.find('.flow_treatment').css('visibility', 'visible'); 
                            	   ul.find('.flow_treatment').text("응급동의");
                                   ul.find('.flow_treatment').css('background-color', '#F4B183'); 
                               }else if(DATA_JSON.verbalMultiFlag == "V,Y"){
                                   ul.find('.flow_treatment').css('visibility', 'visible'); 
                            	   ul.find('.flow_treatment').text("구두완료");
                                   ul.find('.flow_treatment').css('background-color', '#5B9BD5'); 
                               }else if(DATA_JSON.verbalMultiFlag == "M,Y"){
                                   ul.find('.flow_treatment').css('visibility', 'visible'); 
                            	   ul.find('.flow_treatment').text("응급완료");
                                   ul.find('.flow_treatment').css('background-color', '#5B9BD5');
                               }
                               
                           } 
                           ul.find('.treatmentNum').text(DATA_JSON.patientCode);
                           ul.find('.treatmentPatientNm').text(DATA_JSON.patientName);
                           ul.find('.treatmentSA').text(DATA_JSON.patientSex + "/" + DATA_JSON.patientAge);
                           ul.find('.treatmentDeptNm').text(DATA_JSON.clnDeptNm);
                           ul.find('.treatmentNm').text(DATA_JSON.ordDrNm == null ? "-" : DATA_JSON.ordDrNm);
                           ul.find('.treatmentWard').text((DATA_JSON.wardName == null ? "-" : DATA_JSON.wardName) + "/" + (DATA_JSON.roomCd == null ? "-" : DATA_JSON.roomCd));

//                           if(DATA_JSON.roomCd == null){
//                           	DATA_JSON['roomCd'] =' - ';
//                           }
                           ul.find('.treatmentConsentNm').text("[" + (DATA_JSON.createDatetime == null ? " - " : DATA_JSON.createDatetime) + " / " + (DATA_JSON.createUserName == null ? " - " : DATA_JSON.createUserName) + "] " + DATA_JSON.formName);
                           if(DATA_JSON.consentState == "NEW"){
                               DATA_JSON['cosignFlag'] = '2';   
                           }else{
                               DATA_JSON['cosignFlag'] = '1';                    	
                           }
                           DATA_JSON['certPwd'] = getLocalStorage("signPwd");
                           DATA_JSON['userId'] = getLocalStorage("userId");
                           DATA_JSON['userName'] = getLocalStorage("userName");
                           DATA_JSON['userDeptCd'] = getLocalStorage("userDeptCode");
                           DATA_JSON['userDeptName'] = getLocalStorage("userDeptName");
                           DATA_JSON['jobkindcd'] = localStorage.getItem("jobkindcd");
                           DATA_JSON['consent_certneedcnt'] = DATA_JSON.certNeedCnt;
                           DATA_JSON['licnsno'] = localStorage.getItem("licnsno");
                           DATA_JSON['ordfild'] = localStorage.getItem("ordfild");
                           DATA_JSON['depthngnm'] = localStorage.getItem("depthngnm");
                           DATA_JSON['medispclno'] = localStorage.getItem("medispclno");
                           DATA_JSON['lifelong_kind'] = (DATA_JSON.lifelongKind == null ? "" : DATA_JSON.lifelongKind);
                           
                           ul.attr('attr-data', JSON.stringify(DATA_JSON));
                           //2021-12-02
                           if(myWriteFlag == "Y"){
                        	   if(DATA_JSON.myWriteFlagYn != "0")
                        		   $('.treatMentConsent').append(ul);
                           }else{
                        	   $('.treatMentConsent').append(ul);   
                           }
                           

                           // 작성 동의서 빠른 조회 - 조회 결과 클릭 이벤트
                           ul.on("click", function () {
                               var data = $(this).attr("attr-data");
                               cosign_Flag = "1";
                               if (data != "") {
                                   var consentInfo = JSON.parse(data);

                                   COMMON.LOCAL.eform.myConsent = consentInfo;
                                   var consent_obj = {
                                       "patientCode": consentInfo.patientCode,
                                       "FormId": consentInfo.formId,
                                       "FormName": consentInfo.formName,
                                       "FormVersion": consentInfo.formVersion
                                   }; 
                                   if (consentInfo.consentState == "ELECTR_CMP") { // 완료서식
                                       var consents = [];
                                       consents[consents.length] = JSON.parse(data);
                                       COMMON.LOCAL.eform.consent = consents;
                                       if (consentInfo.opdrYn == "Y" && consentInfo.opdrSignYn == "N") { // 시술의 미비
                                           if (consents.length > 0) {
                                               if (getLocalStorage("signPwd") == "" || getLocalStorage("signPwd") == null || getLocalStorage("signPwd") == undefined) {
                                                   $('#sign_popup').css('display', 'block');
                                                   $('#_certpassword').focus();
                                                   localStorage.setItem("myConsentFlag", "true");
                                                   check_consent = $(this);
                                               } else {
                                                   $('.dimmed').css('display', 'block');
                                                   $('.Treatment_List').css('display', 'block');
                                               }
                                           } else {
                                               alert("선택한 동의서가 없습니다.\n동의서를 선택해주세요.");
                                           }
                                       } else {
                                           if (consents.length > 0) {
                                               if (getLocalStorage("signPwd") == "" || getLocalStorage("signPwd") == null || getLocalStorage("signPwd") == undefined) {
                                                   $('#sign_popup').css('display', 'block');
                                                   $('#_certpassword').focus();
                                                   localStorage.setItem("myConsentFlag", "true");
                                                   check_consent = $(this);
                                               } else {
                                                   if (consentInfo.nursCertYn == "N" && localStorage.getItem("jobkindcd").substring(0, 2) != "03") {
                                                       searchAppVersionMyConsent(consent_obj, "nurscertEnd");
                                                   } else {
                                                       searchAppVersionMyConsent(consent_obj, "end");
                                                   }
                                               }
                                           } else {
                                               alert("선택한 동의서가 없습니다.\n동의서를 선택해주세요.");
                                           }
                                       }
                                   }else if(consentInfo.consentState == "NEW"){ // 처방동의서
                                       var consents = [];
                                       var OpenCheckObj = JSON.parse(data);
                                       var ocrAdd = JSON.parse(data); 
                                       ocrAdd.ocrTag = CreateOcrTag();
                                        
                                       
                                       // =========================================
                                       //20210630 작성동의서 빠른 조회 - 비급여 개발 1차 
                                       if(ocrAdd.tmp04 == "ADDFORM"){   
	                                       	 var prcpcd =  ocrAdd.orderCd;
	                                       	 var prcpno =  ocrAdd.orderNo;
	                                       	 var formcd =  ocrAdd.formCd;
	                                       	 var pid = ocrAdd.patientCode;
	                                       	 var cretno = ocrAdd.certNo;
	                                       	 var orddd = ocrAdd.clnDate;
	                                       	 var ordtype = ocrAdd.visitType;
	                                         var nonseqno = ocrAdd.orderSeqNo;
	                                       	 $.ajax({
	                                       		   url: "http://emr.yjh.com/cmcnu/webapps/mr/mr/formmngtweb/.live",   
	                                               data: "submit_id=DRMRF00128&business_id=mr&instcd=204&prcpcd=" + prcpcd + "&prcpno=" + prcpno + "&formcd=" + formcd + "&pid="+pid+"&cretno="+cretno+"&orddd="+orddd+"&ordtype="+ordtype , 
	                                               type: 'get',
	                                               async : false,
	                                               dataType: 'xml',
	                                               timeout: 40000,
	                                               success: function (result) {
	                                               	  if ($(result).find('prcpinfo').length > 0) { 
	                                                       var idx = 0;
	                                                       $(result).find('prcpinfo').each(function () { 
	                                                        	 ocrAdd['nonseqno'] = nonseqno;  
	                                                         	 ocrAdd['addform'] = 'Y2';  
	                                                        	 ocrAdd['userId'] = getLocalStorage("userId");
	                                                    		 if(idx == 0){
	                                                    			 ocrAdd.seqno =  "'"+$(this).find('seqno').text()+"'";  
	                                                    		 }else{
	                                                    			 ocrAdd.seqno =  ocrAdd.seqno + "," + "'"+$(this).find('seqno').text()+"'"; 
	                                                    		 }                      
	                                                    		 ocrAdd['addnonBene'+idx] = $(this).find('prcpnm').text();
	                                                    		 var scoreamt = $(this).find('scoreamt').text();
	                                                    		 scoreamt = scoreamt.replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ",");
	                                                    		 ocrAdd['addestiCost'+idx] = scoreamt;
	                                                    		 idx = idx+1;  
	                                                        })  
	                                                        ocrAdd['nonIndex'] = idx; 
	                                                     } 
	                                                 },
	                                                 error: function (error) {
	                                                     alert("일반 동의서 비급여 항목 조회 중 오류가 발생하였습니다.\n관리자에게 문의 바랍니다.");
	                                                 }
	                                       	 })
                                       }
                                       // =========================================
                                       //20210722 작성동의서 빠른 조회 - 비급여 개발 2차 
                                       else if(ocrAdd.tmp01 == "NONPAY"){   
	                                       	 var prcpcd =  ocrAdd.orderCd;
	                                       	 var prcpno =  ocrAdd.orderNo;
	                                       	 var formcd =  ocrAdd.formCd;
	                                       	 var pid = ocrAdd.patientCode;
	                                       	 var cretno = ocrAdd.certNo;
	                                       	 var orddd = ocrAdd.clnDate;
	                                       	 var ordtype = ocrAdd.visitType; 
	                                       	 //0722
	                                       	 if(ocrAdd.orderDate == null){
	                                       		 ocrAdd.orderDate = ocrAdd.clnDate;
	                                       	 }
	                                       	 $.ajax({
	                                       		 url: ajax_url,   
	                                             data: "submit_id=DRMRF00121&business_id=mr&instcd=204&pid=" + pid + "&orddd=" + orddd + "&cretno=" + cretno + "&ordtype=" + ordtype, 
	                                             type: 'get', 
	                                               async : false,
	                                               dataType: 'xml',
	                                               timeout: 40000,
	                                               success: function (result) {
	                                               	  if ($(result).find('nonpayitem').length > 0) { 
	                                                       var idx = 0;
	                                                       $(result).find('nonpayitem').each(function () {  
	                                                        	 ocrAdd['userId'] = getLocalStorage("userId"); 
	                                                    		 if(idx == 0){
	                                                    			 ocrAdd.seqno =  "'"+$(this).find('seqno').text()+"'";  
	                                                    		 }else{
	                                                    			 ocrAdd.seqno =  ocrAdd.seqno + "," + "'"+$(this).find('seqno').text()+"'"; 
	                                                    		 }                      
	                                                    		 ocrAdd['nonBene'+idx] = $(this).find('prcpnm').text();
	                                                    		 var scoreamt = $(this).find('scoreamt').text();
	                                                    		 scoreamt = scoreamt.replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ",");
	                                                    		 ocrAdd['estiCost'+idx] = scoreamt;
	                                                    		 idx = idx+1;  
	                                                        })  
	                                                        ocrAdd['nonIndex'] = idx;  
	                                                     } 
	                                                 },
	                                                 error: function (error) {
	                                                     alert("비급여 항목 조회 중 오류가 발생하였습니다.\n관리자에게 문의 바랍니다.");
	                                                 }
	                                       	 })
                                       }
                                       // ========================================= 
                                       // ========================================= 
                                       
                                       data = JSON.stringify(ocrAdd); 
                                       consents[consents.length] = JSON.parse(data); 
                                       COMMON.LOCAL.eform.consent = consents;  
                                       
                                       var valueAry = new Array();  
               	                       var valueObj = new Object();
               	                       valueObj.patientCode = OpenCheckObj.patientCode;
               	                       valueObj.formId = OpenCheckObj.formId;
               	                       valueObj.formVersion = OpenCheckObj.formVersion;                 	
               	                        	
               	                       valueAry.push(valueObj);			 
               	                       	
                                       var openCheckVal = "";
                                       var openCheckErrCd = "";
                                       var openCheckErrMsg = "";
                                       var openCheckErrMsg2 = "";
                                       $.ajax({
                                           url: ajax_nu_url + '/biz/nu/member/viewer/eForm25/consent/isOpen',
                                           type: 'post',
                                           timeout: 40000,
                                           async : false,
                                           data: {
                                               parameter: JSON.stringify(valueAry)
                                           }
                                       }).done(function (data) {
                                           $.each(data, function (index, item) {   
                                           	openCheckVal = data[index].result;
                                           	openCheckErrCd = data[index].errorCode;
                                           	openCheckErrMsg = data[index].errorMsg;
                                           	openCheckErrMsg2 = data[index].errorMsg2;    
                                           });
                                       }).fail(function (xhr, status, errorThrown) { 
                                           alert("서식작성여부 조회 중 오류가 발생했습니다. 관리자에게 문의바랍니다. " + errorThrown)
                                       });    
                                        
                                       if(openCheckVal){ 
                                       	if (consents.length > 0) { 
                                               if (getLocalStorage("signPwd") == "" || getLocalStorage("signPwd") == null || getLocalStorage("signPwd") == undefined) {
                                                   $('#sign_popup').css('display', 'block');
                                                   localStorage.setItem("myConsentFlag", "true");
                                                   $('#_certpassword').focus();
                                                   check_consent = $(this);
                                               } else {
                                                   // 의사가 아니면서 간호서식이면
                                                   if (consentInfo.nursCertYn == "N" && localStorage.getItem("jobkindcd").substring(0, 2) != "03") {
                                                       searchAppVersionMyConsent(consent_obj, "nowrite");
                                                   } else {
                                                       searchAppVersionMyConsent(consent_obj, "nowrite");
                                                   }
                                               } 
                                           }
       	                            }else{
       	                            	alert("ErrorCode : " + openCheckErrCd+ "\nErrorMsg : "+openCheckErrMsg);
       	                                COMMON.plugin.loadingBar("hide", "뷰어를 실행하는 중입니다.");
       	                            }
                                   }else { // 미완료서식
                                       var consents = [];
                                       consents[consents.length] = JSON.parse(data);
                                       COMMON.LOCAL.eform.consent = consents;
                                       if (consents.length > 0) {
                                           //localStorage.setItem("signPwd", "paul2015^^"); 
                                           if (getLocalStorage("signPwd") == "" || getLocalStorage("signPwd") == null || getLocalStorage("signPwd") == undefined) {
                                               $('#sign_popup').css('display', 'block');
                                               localStorage.setItem("myConsentFlag", "true");
                                               $('#_certpassword').focus();
                                               check_consent = $(this);
                                           } else {
                                               // 의사가 아니면서 간호서식이면
                                               if (consentInfo.nursCertYn == "N" && localStorage.getItem("jobkindcd").substring(0, 2) != "03") {
                                                   searchAppVersionMyConsent(consent_obj, "nurscertTemp");
                                               } else {
                                                   searchAppVersionMyConsent(consent_obj, "temp");
                                               }
                                           }

                                       } else {
                                           alert("선택한 동의서가 없습니다.\n동의서를 선택해주세요.");
                                       }
                                   }
                               }

                           });
                       });
                       // 2021-12-02
                       $('#treatCnt').text('총 조회 건수 : '+idx+"건")
                   }
                   COMMON.plugin.loadingBar("hide", "");
               }).fail(function (xhr, status, errorThrown) { 
               	COMMON.plugin.loadingBar("hide", "");
                   alert("작성동의서 빠른 조회 중 오류가 발생했습니다. 관리자에게 문의바랍니다. " + errorThrown);
               });
       }
     
    })
    // ===========================================


    // ==================과변경하기 시작==================

    // 과 변경하기 - 클릭
    $('#Dept_Change_Btn').click(function () {
        $('.dimmed').css('display', 'block');
        $('.Change_Dept').css('display', 'block');
    });

    // 과 변경하기 - 변경하기 클릭
    $('.deptChange_enter').click(function () {
        var _userDeptCode = $("#_userDeptChange option:selected").val();
        var _userDeptFullName = $("#_userDeptChange option:selected").text();
        var _userDeptName = $("#_userDeptChange option:selected").attr('class');
        var userDeptNameAry = new Array();
        userDeptNameAry = _userDeptName.split('@');
        _userDeptName = userDeptNameAry[0];
        
        if (_userDeptCode == "") {
            alert("과를 선택해주세요.");
        } else {
            $('.dimmed').css('display', 'none');
            $('.Change_Dept').css('display', 'none');
            localStorage.setItem("userDeptCode", _userDeptCode);
            localStorage.setItem("userDeptName", _userDeptName);
            localStorage.setItem("userDeptFullName", _userDeptFullName);
			localStorage.setItem("depthngnm", _userDeptName); 
            $('#_userDept').text(getLocalStorage("userDeptName"));

            if ($('.gnb_7').attr('class').indexOf("on") > -1) {
                if ($('.cosign_out').attr('class').indexOf("on") > -1) {
                    setTimeout(function () {
                        searchCosignSend();
                    }, 500);

                } else {
                    setTimeout(function () {
                        searchCosign();
                    }, 500);
                }
            }
        }
    });

    // 과 변경하기 - 취소 클릭
    $('.deptChange_close').click(function () {
        $('.dimmed').css('display', 'none');
        $('.Change_Dept').css('display', 'none');

    });

    // ============================================


    // ==============코사인 지정 팝업 시작==================

    // 코사인 지정 팝업 - 닫기
    $('.Consent_open_close').click(function () {
        $('.Consent_List').css('display', 'none');
        $('#Consent_List_Big_div').empty();
        $('.dimmed').css('display', 'none');
    });

    // 코사인 지정 팝업 - 열기
    $('.Consent_open_enter').click(function () {
        var Obj = new Array();
        var attr_data = new Object();
        $('._CosignDeptValue').each(function (index) {
            attr_data = JSON.parse($(this).parent().parent().attr("attr-data"));
            attr_data['cosignDeptCode'] = $(this).parent().parent().find('select:enabled').find('option:selected').val();
            attr_data['cosignDeptName'] = $(this).parent().parent().find('select:enabled').find('option:selected').text();
            //attr_data['cosignFlag'] = cosign_Flag ; 
            attr_data['userId'] = getLocalStorage("userId");
            attr_data['certPwd'] = getLocalStorage("signPwd");
            attr_data['jobkindcd'] = localStorage.getItem("jobkindcd");
            Obj.push(attr_data);
        });
        COMMON.LOCAL.eform.consent = Obj;
        if (attr_data.cosignFlag == "0") { //   
            COMMON.plugin.storage("get", "isCertDown", isCertDown);
        } else { // 코사인탭,작성동의서 빠른 조회
            if (attr_data.nursCertYn == "N" && localStorage.getItem("jobkindcd").substring(0, 2) != "03") {
                COMMON.plugin.loadEFormViewByGuid("nurscertTemp", "VIEWER_FORM", COMMON.LOCAL.eform.consent, attr_data);
            } else {
                COMMON.plugin.loadEFormViewByGuid("temp", "VIEWER_FORM", COMMON.LOCAL.eform.consent, attr_data);
            }

        }
    });

    // 코사인 수신탭 클릭
    $('.cosign_in').click(function () {
        $('#_cosignConsentList').empty();
        searchCosign();
        $(this).addClass('on');
        $('.cosign_out').removeClass('on');
    });

    // 코사인 송신탭 클릭
    $('.cosign_out').click(function () {
        $('#_cosignConsentList').empty();
        //		searchCosign();
        searchCosignSend();
        $(this).addClass('on');
        $('.cosign_in').removeClass('on');
    });
    // ======================== 코사인 지정 끝 ===================

    // if(INTERFACE.USER == "TEST"){
    // // 로그아웃 체크시간 설정(default : 10분)
    COMMON.LogOutTimer.limit = 60 * 60 * 1000;
    // }
    // 로그아웃 타이머 실행
    COMMON.LogOutTimer.start();
    // 자동 로그아웃 설정 end -----------------    

    // 네이티브 백 버튼 이벤트
    COMMON.BACKKEY.addEvent();

    // include전에 cordova.exec() 호출되는 이슈가 있기 때문에
    if (!COMMON.device.isMobile() && !COMMON.device.isTablet()) {
        onDeviceReady();
    } else {
        document.addEventListener('deviceready', onDeviceReady);
    }

    // 도웅말 팝업 이벤트 ------------------------------
    $(".btn-help").on("click", function () {
        //		showHelp();
        $('#help_layouts').empty();
        $('#help_layouts').append('<button type="button" id="helpCloseBtns" style="top:45px; position: fixed;    z-index: 10; right: 75px;" class="btn-close"><span>CLOSE</span></button>');
        $('#help_layouts').css('display', 'block');
        for (var i = 1; i <= 17; i++) {
            $('#help_layouts').append('<img src="../../images/menual' + i + '.jpg" alt="" style="width:100%;height:auto; position: initial;" />');
        }
        //addEventHelp();
    });

    $(document).on("click", "#helpCloseBtns", function (e) {
        $('#help_layouts').empty();
        $('#help_layouts').append('<button type="button" id="helpCloseBtns" style="top:45px; position: fixed;    z-index: 10; right: 75px;" class="btn-close"><span>CLOSE</span></button>');
        $('#help_layouts').css('display', 'none');
    });

    // 도움말 설정
    function showHelp(div) {
        var help = div ? $(div) : $(".help_box_1");
        $(".help_section").show();
        $(".dimmed").show();
        posHelp(help);
    }

    function hideHelp() {
        $(".help_section").hide();
        $(".dimmed").hide();
    }

    function posHelp(div) {
        div.find("dl").each(function (i) {
            var $this = $(this),
                $this_width = $this.width(),
                $target = $this.data("targetClass"),
                $align = $this.data("align"),
                $target_left = 0,
                $target_top = 0,
                $target_width = 0;

            // target
            $target = $target.split(" ");
            $target_elem = $("." + $target.join(" ."));

            // target_position
            $target_left = $target_elem.offset().left,
                $target_top = $target_elem.offset().top,
                $target_width = Number($target_elem.outerWidth());

            // 위치설정
            if ($align == "left") {
                $this.css({
                    "left": $target_left + $target_width,
                    "top": $target_top
                });
            }
            if ($align == "center") {
                $this.css({
                    "left": $target_left + (($target_width - $this_width) / 2),
                    "top": $target_top
                });
            }
        });
    }

    function addEventHelp() {
        // 도움말 paging
        $(".help_section").off(".helpSection").on("click.helpSection", ".btn-arrow", function () {
            //alert("aa");
            var $this = $(this),
                $parent = $this.parent(),
                $type = $this.data("type");
            // 내용변경
            if (!$this.hasClass("off")) {
                // 버튼
                $parent.find("button").removeClass("off");
                $this.addClass("off");
                // 내용
                $(".help_box").hide();
                $(".help_box_" + $type).show();
                // page
                $(".paging li").removeClass("on");
                $(".paging li.page_" + $type).addClass("on");
                // 내용 보이기
                showHelp(".help_box_" + $type);
            }
            // 도움말 닫기
        }).on("click.helpSection", ".btn-close", function () {
            hideHelp();
        });
    };
    // 도웅말 팝업 이벤트 END ------------------------------

    // 재로그인 팝업 이벤트 -----------------------------
    $(document).on("mousedown.loginClose", function (e) {
        var $target = $(e.target),
            $elem = $(".login_id_box");
        if (!$elem.is(e.target) && $elem.has(e.target).length === 0) {
            $(".login_id_list").hide();
        }
    });

    // 최근 로그인 아이디 가져오기
    $("#_reLoginId").on("focus", function () {
//        if (localStorage.getItem("latelyData") != null && localStorage.getItem("latelyData") != undefined) {
//            getLatelyData();
//        }
    });

    // 로그인 Id 입력시 엔터 이벤트
    $("#_reLoginId").on("keydown", function (e) {
        if (e.keyCode == 13) { // 키가 13이면 실행 (엔터는 13)
            $("#_reLoginPw").val("");
            $("#_reLoginPw").trigger("focus");
            $("#_latelyList").hide();
        }
    });

    // 로그인 pw 입력시 엔터 이벤트
    $("#_reLoginPw").on("keydown", function (e) {
        if (e.keyCode == 13) { // 키가 13이면 실행 (엔터는 13)
            $(this).trigger("blur");
            $("#_reLoginOk").trigger("click");
        }
    });

    // 재로그인 팝업 이벤트 END ---------------------------

    // 알림 팝업 확인 버튼 이벤트
    $("._noticeOk").on("click", function () {
        popupHide("popup-notice");
    });

    // 로그아웃 버튼 이벤트
    $("#_btnLogOut").on("click", function () {
        if (confirm("로그인 화면으로 이동하시겠습니까?")) {
            //  var ITnadePlugins = new ITnadePlugin();
            // ITnadePlugins.LocalDelKeyAndCert(getLocalStorage("userdn"));
            var isOnInpatientMenu = $("#_inPatient").hasClass("on");
            var isOnEmergencyMenu = $("#_emergency").hasClass("on");
            var isOnOutpatientMenu = $("#_outPatient").hasClass("on");
            var isOnOperationMenu = $("#_operation").hasClass("on");
            var isOnFindMenu = $("#_find").hasClass("on");
            var isOnCosignMenu = $("#_cosign").hasClass("on");
            var isOnmyConsentMenu = $("#_myConsent").hasClass("on");
            var isOnRequestMenu = $("#_request").hasClass("on");
            var isOnLaboratoryMenu = $("#_laboratory").hasClass("on");
						
            // 대메뉴 기록 저장
            localStorage.removeItem("deviceMenuSet");
            if (isOnInpatientMenu) {
                localStorage.setItem("deviceMenuSet", "_inPatient");
            } else if (isOnOutpatientMenu) {
                localStorage.setItem("deviceMenuSet", "_outPatient");
            } else if (isOnEmergencyMenu) {
                localStorage.setItem("deviceMenuSet", "_emergency");
            } else if (isOnOperationMenu) {
                localStorage.setItem("deviceMenuSet", "_operation");
            }else if (isOnRequestMenu) {
                localStorage.setItem("deviceMenuSet", "_request");
            }  else if (isOnFindMenu) {
                localStorage.setItem("deviceMenuSet", "_find");
            } else if (isOnCosignMenu) {
                localStorage.setItem("deviceMenuSet", "_cosign");
            } else if (isOnmyConsentMenu) {
                localStorage.setItem("deviceMenuSet", "_myConsent");
            } else if (isOnLaboratoryMenu) {
                localStorage.setItem("deviceMenuSet", "_laboratory");
            }

            // 각 탭 검색조건 설정 
            localStorage.setItem("inpatientSet", $('#_inPatientClnDept option:selected').val());
            localStorage.setItem("outpatientSet", $('#_outPatientClnDept option:selected').val());
            localStorage.setItem("emergencySet", $('#_emergencyDept option:selected').val());
            localStorage.setItem("requestSet", $('#_requestDept option:selected').val());
            localStorage.setItem("operationSet", $('#_operationClnDept option:selected').val());
            localStorage.setItem("findSet", $('#_findDept option:selected').val()); 
            localStorage.setItem("laboratorySet", $('#_laboratoryClnDept option:selected').val());


            // 작성 동의서 빠른 조회 기록 저장
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
            var consentStateET = ($('#ELECTR_TEMP').prop('checked') ? "Y" : "N");
            var consentState = consentStateN+","+consentStateT+","+consentStateE+","+consentStateET;
            localStorage.setItem("treatMentConsentType", consentState);
            

            location.href = "../login/login.html";
            return;
        }
    });

    // 메뉴 버튼 이벤트
    $("#_menus li").on("click", function () {
        var menuId = $(this).attr("id");
        menuClickEvent(menuId.replace("_", ""));
    });

    // 캘린더 버튼 이벤트
    $("._calendar").on("click", function () {
        var selectedDate = "";
        var type = $(this).attr("type");
        if (type == "button") {
            calendar = $(this).prev();
        } else {
            calendar = $(this);
        }
        COMMON.plugin.datePicker(
            calendar.val(),
            function (result) {
                if (result == "" || result == undefined || result == null) {
                    alert("진료일은 삭제할 수 없습니다.");
                    calendar.val(getDay("-"));
                } else {
                    // 외래 메뉴에서 진료일을 변경할 경우
                    calendar.val(result);
                    if (getActiveMenuId() == "_outPatient") {
                        var outClnDept = $("#_outPatientClnDept option:selected").val();
                        if (outClnDept != "") {
                            // fnSearchOutDoctor(outClnDept);
                            SearchOutDoctor(outClnDept);
                        }
                    }
                }
            },
            errorHandler
        );
    });

    // 캘린더 입력 이벤트
    $(".txt_date").on("change", function () {
        var day = $(this).val();
        if (day == "") {
            $(this).val(getDay("-"));
        }
        if (getActiveMenuId() == "_outPatient") {
            var outClnDept = $("#_outPatientClnDept option:selected").val();
            if (outClnDept != "") {
                SearchOutDoctor(outClnDept);
                // fnSearchOutDoctor(outClnDept);
            }
        }
    });

    // 패스워드 팝업 확인 버튼 이벤트
    $("#_btnPopupOk").on("click", function () {
        var id = getLocalStorage("userId");
        var pw = $("#_certpassword").val();
        searchAppVersion(); // will be deleted (인증서dn이 없어서, 임시로 무조건 열리게 해둠)
        if (pw != "" && pw != undefined) {
            var ITnadePlugins = new ITnadePlugin();
            ITnadePlugins.setConnect("192.168.4.17", '6001', getLocalStorage("userId"), pw);
//            COMMON.plugin.certDown(id, pw, consentOnClickEvent,errorHandler);
        } else {
            alert("공인인증서 패스워드를 입력하지 않았습니다.");
        }
    });

    // 패스워드 팝업 취소 버튼 이벤트
    $("#_btnPopupCancle").on("click", function () {
        popupHide("popup-pwd");
        $("#_certpassword").val("");
    });

 
    // 재로그인 버튼 이벤트
    $("#_reLoginOk").on("click", function () {
        var id = $("#_reLoginId").val();
        var pw = $("#_reLoginPw").val();
    	var jobkindcd = "";
    	var licnsno = "";
    	var medispclno = "";
    	var ordfild = "";
    	var depthngnm = "";
    	var idx=0;
    	var applyduty = "";

    	var LoginDeptName = new Array;
    	var LoginDeptCode = new Array;
    	var LoginDeptMinName = new Array;
    	var LoginJobKindCd = new Array;
        if (id != "" && pw != "") {
            COMMON.plugin.wifiCheck(wifiCheckFn);

            var checkVal = localStorage.getItem("wifiCheckVal");
            if (checkVal < -90) {
                alert("WIFI 수신 감도가 좋지 않습니다. WIFI 통신이 잘 되는 곳에서 사용 바랍니다.");
            } else { 
                var loginCheck ="";  
                

   			 $.ajax({
                    url: ajax_url,
                    type: 'post',
                    data: 'submit_id=DRMRF00119&business_id=mr&userid=' + id + '&pwd=' + pw + '&instcd=204&syscd=' + HIS_EPH,
                    dataType: 'xml',
                    timeout: 40000,
                    success: function (result) { 
                        if ($(result).find('data').length > 0) {
                            $(result).find('data').each(function () {
                                if ($(this).find('login').text() != 'false') {
                                    applyduty =  $(this).find('applyduty').text();
                                    // 패스워드 초기화
                                    $("#_reLoginPw").val("");
                                    
                                    // 교육서버기준
//                                    applyduty = "N";
                                } else {
                                    alert("사용자 정보가 없습니다.\nID나 패스워드가 일치하지 않습니다.\n다시 확인해주시기 바랍니다.");
                                }
                            }); 
                            

                            if(applyduty == "Y"){
                            	$.ajax({
                        			url: 'http://emr.yjh.com/cmcnu/.live',
                        			type: 'post',
                        			data: 'submit_id=DRZSU11708&business_id=zz&dutplceinstcd=204&dutplcecd=' + getLocalStorage("userDeptCode") + '&userid=' + getLocalStorage("userId"),
                        			dataType: 'xml',
                        			async: false,
                        			timeout: 10000,
                        			success: function (result) {		
                        				if ($(result).find('userdutytime').length > 0) {
                        					$(result).find('userdutytime').each(function () {
                        						loginCheck = $(this).find('dutyn').text();
                        					});  
                        					// 교육서버기준
                        					//loginCheck="Y";
                        	        		if(loginCheck =="N"){
                        	        			alert("근무시간이 아닙니다. 근무표를 확인하여 주십시오.\n근무시간 외에는 접속이 불가합니다.");
                        	        		}else{ 
                                                popupHide("popup-relogin");
            									COMMON.LogOutTimer.start();  
                        	        		}
                        				}  
                        			},
                        			error: function (error) { 
                        				alert("사용자 사용시간 체크 중 문제가 발생하였습니다. 관리자에게 문의바랍니다.\n에러코드 : " + error.readyState);

                        			} 
                        		})
                            }else if(applyduty == "N"){ 
                                popupHide("popup-relogin");
								COMMON.LogOutTimer.start();  
                            }
                        }  
                    },
                    error: function (error) {
                        alert("로그인 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + error);
                    } 
                });
   			 
   			 
   			 
                
        		
                
               
            }

        } else {
            if (id == "" || id == null) {
                alert("아이디를 입력하지 않았습니다.");
            } else if (pw == "" || pw == null) {
                alert("패스워드를 입력하지 않았습니다.");
            }
        }
    });


    // ---------------------------------------------------------


    // 재로그인 로그아웃 버튼 이벤트
    $("#_reLoginCancle").on("click", function () {
        popupHide("popup-relogin");
        COMMON.plugin.storage("delete", "", null);
        var index = 1 - Number(history.length);
        history.go(index);
    });

    // 검색목록 펼치기 / 닫기
    $(".btn-toggle-form").on("click", function () {
        var $btn = $(this),
            $form = $btn.parent().siblings(".form_section"),
            $close = $btn.hasClass("close");
        // 펼치기
        if ($close) {
            $btn.removeClass("close");
            $form.removeClass("hide");
            // 닫기
        } else {
            $btn.addClass("close");
            $form.addClass("hide");
        }
    });

    $(".btn-toggle-form1").on("click", function () {
        var $btn = $(this),
            $form = $btn.parent().siblings(".form_section1"),
            $close = $btn.hasClass("close");
        // 펼치기
        if ($close) {
            $btn.removeClass("close");
            $form.removeClass("hide");
            // 닫기
        } else {
            $btn.addClass("close");
            $form.addClass("hide");
        }
    });

    $(".btn-toggle-form2").on("click", function () {
        var $btn = $(this),
            $form = $btn.parent().siblings(".form_section2"),
            $close = $btn.hasClass("close");
        // 펼치기
        if ($close) {
            $btn.removeClass("close");
            $form.removeClass("hide");
            // 닫기
        } else {
            $btn.addClass("close");
            $form.addClass("hide");
        }
    });

    $(".btn-toggle-form3").on("click", function () {
        var $btn = $(this),
            $form = $btn.parent().siblings(".form_section3"),
            $close = $btn.hasClass("close");
        // 펼치기
        if ($close) {
            $btn.removeClass("close");
            $form.removeClass("hide");
            // 닫기
        } else {
            $btn.addClass("close");
            $form.addClass("hide");
        }
    });

    $(".btn-toggle-form4").on("click", function () {
        var $btn = $(this),
            $form = $btn.parent().siblings(".form_section4"),
            $close = $btn.hasClass("close");
        // 펼치기
        if ($close) {
            $btn.removeClass("close");
            $form.removeClass("hide");
            // 닫기
        } else {
            $btn.addClass("close");
            $form.addClass("hide");
        }
    });

    $(".btn-toggle-form5").on("click", function () {
        var $btn = $(this),
            $form = $btn.parent().siblings(".form_section5"),
            $close = $btn.hasClass("close");
        // 펼치기
        if ($close) {
            $btn.removeClass("close");
            $form.removeClass("hide");
            // 닫기
        } else {
            $btn.addClass("close");
            $form.addClass("hide");
        }
    });

    // 환자 리스트 항목 정렬
    $("._sort").on("click", function () {
        var menuId = $("#_menus .on").attr("id") + "List";
        var key = $(this).attr("attr-key");
        var orderby = $(this).attr("attr-orderby");
        if (orderby == "asc") {
            $(this).attr("attr-orderby", "desc");
        } else {
            $(this).attr("attr-orderby", "asc");
        }
        patientListSort(menuId, key, orderby);
    });


    // ----------------입원 환자 검색 영역 ------------------------
    // 진료과 변경시 이벤트
    $("#_inPatientClnDept").on("change", function () {
        var clnDept = $("#_inPatientClnDept option:selected").val();
        //searchCharge(clnDept);
        // fnSearchCharge(clnDept);
        searchDoctor(clnDept);
        // fnSearchDoctor(clnDept);
    });

    // 입원 환자 검색 버튼 이벤트
    isRun_3 = false;
    $("#_inPatientSearch").on("click", function () {
        var ward = $("#_inPatientWard option:selected").val(); // 병동
        var clnDept = $("#_inPatientClnDept option:selected").val(); // 과
        var Doc = $("#_inPatientDoctor option:selected").val(); // 주치의
        // alert(ward);
        if (ward == "" && clnDept == "" && Doc == "") {
            alert("검색조건을 지정해주세요.");
        } else {
            commonInit();
            if (isRun_3 == true) {
                return;
            }
            isRun_3 = true;
            InpatientSearch_submit();
        }

        // fnInpatientSearch();
    });

    $('#myConsentInit').click(function () {
        
        myConsentInit();
    });

    // 입원 환자 초기화 버튼 이벤트
    $("#_inPatientInit").on("click", function () {
        inPatientInit();
    });
    // -----------------------------------------------------


    // ----------------외래 환자 검색 영역 ------------------------
    // 진료과 변경시 이벤트
    $("#_outPatientClnDept").on("change", function () {
        $("#_outPatientDoctor option").remove();
        $("#_outPatientDoctor").append($('<option>', {
            value: '',
            text: '진료의'
        }));
        var clnDate = $("#_outPatientCalendar").val().replace(/-/g, "");
        var clnDept = $("#_outPatientClnDept option:selected").val();
        // 2370100000
        if (clnDept == "2370100000") {
            $('#outPatientChange').text('진료과/구분');
        } else {
            $('#outPatientChange').text('진료과/진료의');
        }
        if (clnDate != "" && clnDept != "") {
            SearchOutDoctor(clnDept);
            // fnSearchOutDoctor(clnDept);
        }
    });

    // 외래 환자 검색 버튼 이벤트
    isRun_4 = false;
    $("#_outPatientSearch").on("click", function () {
        if (searchOutpatientConditionCheck()) {
            commonInit();
            if (isRun_4 == true) {
                return;
            }
            isRun_4 = true;
            outPatientSearch_submit();
            // outPatientSearch();
        }
    });

    // 외래 환자 초기화 버튼 이벤트
    $("#_outPatientInit").on("click", function () {
        outPatientInit();
    });

    // -----------------------------------------------------


    // ----------------응급 환자 검색 영역 ------------------------
    // 응급 환자 검색 버튼 이벤트
    isRun_5 = false;
    $("#_emergencySearch").on("click", function () {
        commonInit();

        if (isRun_5 == true) {
            return;
        }
        isRun_5 = true;
        emergencySearch_submit();
        // emergencySearch();
    });
    // 응급 환자 초기화 버튼 이벤트
    $("#_emergencyInit").on("click", function () {
        emergencyInit();
    });
    // ----------------------------------------------------


    // ----------------수술 환자 검색 영역 ------------------------
    // 수술 환자 검색 버튼 이벤트
    isRun_6 = false;
    $("#_operationSearch").on("click", function () {
        commonInit();
        if (isRun_6 == true) {
            return;
        }
        isRun_6 = true;
        operationSearch_submit();
    });
    // 수술 환자 초기화 버튼 이벤트
    $("#_operationInit").on("click", function () {
        operationInit();
    });

    // 수술 진료과 변경 이벤트
    isRun_2 = false;
    $("#_operationClnDept").on("change", function () {
        var clnDept = $("#_operationClnDept option:selected").val(); 
        searchOperDoctor(clnDept);
    });
    // -------------------------------------------------------


    // ----------------검사실 환자 검색 영역 ------------------------
    // 임시 라디오 버튼 이벤트 : 체크된 것을 한 번더 클릭할 경우 체크 해제
    var isTempChecked = false;
    $(':radio[name="laboratoryType"]').on("click", function (e) {
        // 해당 옵션 체크 여부 확인 후
        if (isTempChecked == false) {
            $(this).prop("checked", true);
            isTempChecked = true;
        } else {
            $(this).prop("checked", false);
            isTempChecked = false;
        }
    });

    // 검사부서 선택시 이벤
    $("#_laboratorDept").on("change", function () {
        var inspectionDept = $("#_laboratorDept option:selected").val();
        if (inspectionDept != "" && inspectionDept != undefined) {
            fnSearchInspectionDept(inspectionDept);
            $("#_laboratoryRoom").prop("disabled", false);
        } else {
            selectboxInit("_laboratoryRoom");
            $("#_laboratoryRoom").prop("disabled", true);
        }
    });

    // 검사실 환자 검색 버튼 이벤트
//    $("#_laboratorySearch").on("click", function () {
//        var inspectionDept = $("#_laboratoryRoom option:selected").val();
//        if (inspectionDept != "none" && inspectionDept != undefined) {
//            commonInit();
//            laboratorySearch();
//        } else {
//            alert("검사실을 선택하지 않았습니다.");
//        }
//    });
    // 검사실 환자 초기화 버튼 이벤트
    $("#_requestInit").on("click", function () {
        requestInit();
    });
    // ---------------------------=-------------------------


    // ----------------검색 환자 검색 영역 ------------------------
    // 검색 환자 검색 버튼 이벤트
    $("#_findSearch").on("click", function () {
        var patientName = $("#_findPatientName").val();
        var patientCode = $("#_findPatientCode").val();
        // 환자 검색 조건 : 환자코드 , 환자코드+진료과, 환자명, 환자명 + 진료과
        if (patientName == "" && patientCode == "") {
            alert("환자번호나 환자명이 입력되지 않았습니다.");
        } else {
            commonInit();
            findPatientSearch();
        }
    });
    // 검색 환자 초기화 버튼 이벤트
    $("#_findInit").on("click", function () {
        findInit();
    });
    // ---------------------------------------------------


    // ----------------동의서 검색 영역 ------------------------
    // 동의서 검색 show / hide

    $(".agree_tab").on("click", "h2", function () {
        var $this = $(this),
            $parent = $this.parent(),
            $idx = $this.data("idx");
        var agree_1_list = $(".cnts_3 .agree_1_list");
        // 동의서 검색일경우
        if ($idx == "1") {
            // $parent.find(".agree_2").removeClass("on");
            $('.select_tab').removeClass('on');
            $parent.find(".agree_1").addClass("on");

            $(".agree_1_section").removeClass("hide");
            $(".agree_2_section").addClass("hide");

            // 리스트를 초기화 하고 동의서 검색을 함.
            $("#_consentList li").remove();

            $("#_contentSearch").trigger("click");

            // 리스트 border-top 색상 수정.
            agree_1_list.css('border-top', '2px solid #002e86');
        }
    });

    $('#close_btn').click(function () {
        $('#mainDiv').css('display', 'none');
        $('.small_Image').remove();
        $('#imgMain').prop('src', '');
    });
    // *** 이미지뷰어 상단바 ��릭효과
    $('#bodyDiv').click(function () {
        if ($('#headerDiv').hasClass('fixed')) {
            if ($('#headerDiv').is(':visible')) {
                $('#headerDiv').css('display', 'none');
            } else {
                $('#headerDiv').css('display', 'block');
            }
        }
    });



    function onLoadMainImage(mainImg) {
        $("#imgMain").attr("src", mainImg);
    }

    $(document).on("click", ".class1", function () {
        onLoadMainImage($(this).attr("src"));
    });

    $(document).on("click", ".small_Image", function () {
        $("body").scrollTop(0);
    });

    $(".agree_tab").on("click", "h4", function () {
        var $this = $(this),
            $parent = $this.parent(),
            $idx = $this.data("idx");
        var agree_2_list = $(".cnts_3 .agree_2_list");
        // 작성 동의서 검색일 경우
        if ($idx == "2") {
            $parent.find(".agree_1").removeClass("on");
            $parent.find(".agree_2").addClass("on");

            $(".agree_1_section").addClass("hide");
            $(".agree_2_section").removeClass("hide");

            // 검색일을 초기화시키고 리스트를 초기화하고 동의서 검색을 함.
            $(":radio[name=radio_cmt_type]").prop("checked", false);
            $("input:radio[name=radio_cmt_type]:input[value='A']").prop("checked", true);
            $("#_efromCmtClnDept").prop("disabled", true);
            $("#_consentStartDate").val(getDay("-", "", "", -10)); // 여기
            $("#_consentendDate").val(getDay("-"));
            $("#_consentAllList li").remove();
            $("#_contentAllSearch").trigger("click");

            // 리스트 border-top 색상 수정.
            agree_2_list.css('border-top', '2px solid #002e86');
        }
    });

    // 아코디언 이벤트
    $(".agree_1_section").on("click", "._groupType", function () {
        var $this = $(this).find(".btn-accordion"),
            $list = $this.parent().siblings("ul"),
            $list_li = $list.find("li"),
            $list_h = 0,
            $slide = false; // 슬라이드 효과

        // list height
        $list_li.each(function (i, n) {
            var $this = $(this);
            $list_h += $this.outerHeight();
        });

        // 펼치기
        if (!$this.hasClass("on")) {
            $this.addClass("on");
            if ($slide) {
                $list.height($list_h);
            }
            $list.addClass("on");

            // 닫기
        } else {
            $this.removeClass("on");
            if ($slide) {
                $list.height(0);
            }
            $list.removeClass("on");
        }
    });

    // 연관동의서 아코디언 이벤트
    $(".relation_section").on("click", "._groupType", function () {
        var $this = $(this).find(".btn-accordion"),
            $list = $this.parent().siblings("ul"),
            $list_li = $list.find("li"),
            $list_h = 0,
            $slide = false; // 슬라이드 효과

        // list height
        $list_li.each(function (i, n) {
            var $this = $(this);
            $list_h += $this.outerHeight();
        });

        // 펼치기
        if (!$this.hasClass("on")) {
            $this.addClass("on");
            if ($slide) {
                $list.height($list_h);
            }
            $list.addClass("on");

            // 닫기
        } else {
            $this.removeClass("on");
            if ($slide) {
                $list.height(0);
            }
            $list.removeClass("on");
        }
    });


    // 동의서 검색 버튼 이벤트
    $("#_contentSearch").on("click", function () {
        var patientCode = $("#_detailPatientCode").text();

        var searchType = $(":radio[name='_consentSearchType']:checked").val();
        if (patientCode != "" && patientCode != undefined) {
            fnSearchConsent();
        } else {
            alert("환자 선택을 하지 않았습니다.");
        }
    });

    // 동의서 검색 키워드 이벤트
    $("#_searchKeyword").on("keydown", function (e) {
        if (e.keyCode == 13) {
            var patientCode = $("#_detailPatientCode").text();
            if (patientCode != "" && patientCode != undefined) {
                fnSearchConsent();
            } else {
                alert("환자 선택을 하지 않았습니다.");
            }
            $(this).trigger("blur"); // 소프트 키보드 내려가게 하기 위해서
        }
    });

    // 동의서 전체 검색 버튼 이벤트
    $("#_contentAllSearch").on("click", function () {
        var patientCode = $("#_detailPatientCode").text();
        var startDate = $("#_consentStartDate").val();
        var endDate = $("#_consentendDate").val();

        if (patientCode != "" && patientCode != undefined) {
            if (termOfValidity(startDate, endDate)) {
                fnAllSearchConsent();
            }
        } else {
            alert("환자 선택을 하지 않았습니다.");
        }
    });

    // 동의서 not set 라디오 버튼 이벤트
    $('input:radio[name=_consentSearchType]').change(function () {
        var searchType = $(":radio[name='_consentSearchType']:checked").val();
        localStorage.setItem("consentSearchType", searchType);
        if (searchType == "A") {
            $("#_consentList li").remove();
            $("#_efromClnDept").prop("disabled", false);
            $('#_searchKeyword').attr('disabled', false);
            $('#_contentSearch').attr('disabled', false);
            $('#_searchKeyword').attr('placeholder', "동의서를 검색하세요.");
            $("#_contentSearch").trigger("click");
        } else if (searchType == "B") {
            $('#_searchKeyword').val('');
            $('#_searchKeyword').attr('disabled', true);
            $("#_contentSearch").trigger("click");
            $('#_contentSearch').attr('disabled', true);
            $('#_searchKeyword').attr('placeholder', "세트 / 즐겨찾기 동의서는 조회가 불가능합니다.");
        } else if (searchType == "C") {
            $('#_searchKeyword').val('');
            $('#_searchKeyword').attr('disabled', true);
            $("#_efromClnDept").prop("disabled", true);
            $("#_contentSearch").trigger("click");
            $('#_contentSearch').attr('disabled', true);
            $('#_searchKeyword').attr('placeholder', "코사인 동의서는 조회가 불가능합니다.");

        } else {
            $('#_searchKeyword').val('');
            $('#_searchKeyword').attr('disabled', true);
            $("#_efromClnDept").prop("disabled", true);
            $("#_contentSearch").trigger("click");
            $('#_contentSearch').attr('disabled', true);
            $('#_searchKeyword').attr('placeholder', "세트 / 즐겨찾기 동의서는 조회가 불가능합니다.");
        }
    });

    // 동의서 검색 : 과별 변경 이벤트
    $("#_efromClnDept").on("change", function () {
        $("#_contentSearch").trigger("click");
    });

    // 작성 동의서 : 라디오 버튼 이벤트
    $('input:radio[name=radio_cmt_type]').change(function () {
        var searchType = $(":radio[name='radio_cmt_type']:checked").val();
        if (searchType == "A") {
            $("#_efromCmtClnDept").prop("disabled", true);
        } else {
            $("#_efromCmtClnDept").prop("disabled", false);
        }
        $("#_contentAllSearch").trigger("click");
    });

    // 작성동의서의 확인부서 변경시 이벤트
    $("#_efromCmtClnDept").on("change", function () {
        $("#_contentAllSearch").trigger("click");
    });

    

    $('#myConsentYnLabel').on("click", function(){ 
    	if($('#myConsentYn').prop('checked')){
    		$('#myConsentYn').prop('checked',false);
    	}else{
    		$('#myConsentYn').prop('checked',true);
    		
    	}
    	
    })
    
    $('#NEW').click(function(){
        if($(this).prop('checked')){
            $(this).prop('checked',false); 
        }else{
            $(this).prop('checked',true); 
        }
    })
    $('#TEMP').click(function(){
        if($(this).prop('checked')){
            $(this).prop('checked',false); 
        }else{
            $(this).prop('checked',true); 
        }
    })
    $('#ELECTR_TEMP').click(function(){
        if($(this).prop('checked')){
            $(this).prop('checked',false); 
        }else{
            $(this).prop('checked',true); 
        }
    })
    $('#ELECTR_CMP').click(function(){
        if($(this).prop('checked')){
            $(this).prop('checked',false); 
        }else{
            $(this).prop('checked',true); 
        }
    })

});


// cordova가 준비된 상태
function onDeviceReady() {
    console.log("[ Main onDeviceReady ]");
    $("#_inPatientCalendar").val(getDay("-", "", "", "")); 
    $("#_outPatientCalendar").val(getDay("-"));
    $("#_emergencyCalender").val(getDay("-", "", "", ""));
    $("#_operationCalender").val(getDay("-", "", "", ""));
    $("#_requestCalender").val(getDay("-", "", "", -7));
    $("#_requestCalender_2").val(getDay("-", "", "", ""));
    $("#_findCalender").val(getDay("-", "", "", ""));
    $("#_laboratoryEndDate").val(getDay("-", "", "", ""));
    $("#_laboratoryStartDate").val(getDay("-", "", "", ""));
    
    // 사용자 정보 설정 
    $("#_userId").text((getLocalStorage("userId") == "") ? "-" : getLocalStorage("userId"));
    $("#_userName").text((getLocalStorage("userName") == "") ? "-" : getLocalStorage("userName"));
    $("#_userDept").text((getLocalStorage("userDeptName") == "") ? "-" : getLocalStorage("userDeptName"));
    $("input:radio[name=_consentSearchType]:input[value='" + getLocalStorage("consentSearchType") + "']").prop("checked", true);
    // 화면 초기화
    if (getLocalStorage("treatmentVal") != "") {
        $('#treatmentRadio').empty();
        $('#treatmentRadio').append(getLocalStorage("treatmentVal"));
    }
    //localStorage.setItem("treateMentDept",);
    //treateMentDept 
    if (getLocalStorage("myConsentYn") == "Y") {
        $('#myConsentYn').prop('checked', true);
    } else {
        $('#myConsentYn').prop('checked', false);
    }
    // treatementDate

    $('#treatmentRadioType').find('input').each(function () {
        if (getLocalStorage("treatMentVisitType") == $(this).val()) {
            $(this).prop('checked', true);
        }
    });
//    $('#treatmentRadioConsent').find('input').each(function () {
//        if (getLocalStorage("treatMentConsentType") == $(this).val()) {
//            $(this).prop('checked', true);
//        }
//    });
    var consentType = new Array();
    consentType = getLocalStorage("treatMentConsentType").split(",");

    if(consentType[0]=="Y"){
    	$('#NEW').prop('checked',true);
    }
    if(consentType[1]=="Y"){
    	$('#TEMP').prop('checked',true);
    }
    if(consentType[2]=="Y"){
    	$('#ELECTR_CMP').prop('checked',true);
    }
    if(consentType[3]=="Y"){
    	$('#ELECTR_TEMP').prop('checked',true);
    }
    
    //    localStorage.setItem("inpatientSet",$('#_inPatientClnDept option:selected').val());
    //    localStorage.setItem("outpatientSet", $('#_outPatientClnDept option:selected').val());
    //    localStorage.setItem("emergencySet",$('#_emergencyDept option:selected').val());
    //    localStorage.setItem("operationSet", $('#_operationClnDept option:selected').val());
    //    localStorage.setItem("findSet", $('#_findDept option:selected').val()); 

    pageInit();
    
    setTimeout(function () {
        $('#treateMentDept').find('option').each(function () {
            if (getLocalStorage("treateMentDept") == $(this).val()) {
                $(this).prop('selected', "selected"); 

                var selectVal = $('#treateMentDept option:selected').val();
                searchDoctorTreat(selectVal);
            }
        });
        $('#treateMentWard').find('option').each(function () {
            if (getLocalStorage("treateMentWard") == $(this).val()) {
                $(this).prop('selected', "selected");
            }
        }); 

        if (!userDefaultSetting("dept", "_inPatientClnDept")) {
            if (!userDefaultSetting("dept", "_inPatientWard")) {
                if (!userDefaultSetting(getLocalStorage("inpatientSet"), "_inPatientClnDept")) {
                    userDefaultSetting(getLocalStorage("inpatientSet"), "_inPatientWard")
                };
            }
        }
        if (!userDefaultSetting("dept", "_outPatientClnDept")) {
            userDefaultSetting(getLocalStorage("outpatientSet"), '_outPatientClnDept');
        }
        if (!userDefaultSetting("dept", "_emergencyDept")) {
            userDefaultSetting(getLocalStorage("emergencySet"), '_emergencyDept'); 
        }
        if (!userDefaultSetting("dept", "_operationClnDept")) {
            userDefaultSetting(getLocalStorage("operationSet"), '_operationClnDept');
        }
        if (!userDefaultSetting("dept", "_requestDept")) {
            userDefaultSetting(getLocalStorage("requestSet"), '_requestDept');
        } 
        if (!userDefaultSetting("dept", "_findDept")) {
            userDefaultSetting(getLocalStorage("findSet"), '_findDept');
        }  
        
        userDefaultSetting(getLocalStorage("laboratorySet"), '_laboratoryClnDept'); 

    }, 2000);

};

// 재로그인 초기화
function reLoginInit(params) {
    //console.log("reLoginInit : " + JSON.stringify(params));
    setLatelyData(params);
    COMMON.plugin.storage("set", params, null, null);
    COMMON.LOCAL.eform.user = params;
    localStorage.setItem("userId", params.userId);
    localStorage.setItem("userDeptName", params.userDeptName);
    localStorage.setItem("userName", params.userName);
    localStorage.setItem("userDeptCode", params.userDeptCode);
    localStorage.setItem("userGroupCode", params.userGroupCode);

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
    onDeviceReady();
}

// 화면 초기화
function pageInit() {
    searchCommonData();
};

// 초기 화면 로그인 사용자 정보 셋팅
function userPatientSearchSetting() {
    var userDeptCode = getLocalStorage("userDeptCode"); // 사용자 진료과
    var userPartCd = getLocalStorage("userPartCd"); // 사용자 근무지
    var userId = getLocalStorage("userId"); // 사용자 ID

    // 진료과 셋팅
    if (!selectboxDefalutSelected("_inPatientClnDept", userDeptCode)) {
        userDeptCode = "";
    }
    inpatientCommonDoctorData(userDeptCode);
    selectboxDefalutSelected("_efromClnDept", userDeptCode); // 동의서 검색 과별
    // 사용자과로 설정
    selectboxDefalutSelected("_efromCmtClnDept", userPartCd); // 작성동의서 검색 확인부서
    // 사용자 근무지로 설정
};

// 공통 데이터 조회 여기
function searchCommonData() {
    // localStorage.setItem("userId", "MD01");
    var userDeptCode = getLocalStorage("userDeptCode");
    // 화면 변수

    searchWard();
    searchOutPatientClnDept();
    searchInEmerOperClnDept();
    searchFindClnDept();
    searchCosignClnDept();
    searchCheckupDept();
    var deviceMenuSet = localStorage.getItem("deviceMenuSet");
    $('#' + deviceMenuSet).trigger('click');  
    // searchOperationTypeList(); //고정으로인하여 사용X
};

// 검사실 지원부서
function searchCheckupDept() { 
    $.ajax({
        url: ajax_url,
        data: "submit_id=DRMRF00124&business_id=mr&instcd=204",
        type: 'get',
        dataType: 'xml',
        timeout: 40000,
        success: function (result) {
            clearOption('_laboratoryClnDept', '지원부서');
            if ($(result).find('dept').length > 0) {
                $(result).find('dept').each(function () {
//                	if($(this).find('prcpexecdeptcdid').text()=="3030000000"){
//                		alert($(this).find('prcpexecdeptcdnm').text());
//                	}
                    $("#_laboratoryClnDept").append($('<option>', {
                        value: $(this).find('prcpexecdeptcdid').text(),
                        text: $(this).find('prcpexecdeptcdnm').text()
                    })); 
                });

            }
            isNextRequest();
        },
        error: function (error) {
            alert("검사실 지원부서 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + error);
        }

    })
}

// 검사실 조회
function searchCheckupRoom(DeptCd) { 
    $.ajax({
        url: ajax_url,
        data: "submit_id=DRMRF00125&business_id=mr&instcd=204&basesuppdeptcd="+DeptCd,
        type: 'get',
        dataType: 'xml',
        timeout: 40000,
        success: function (result) {
            clearOption('_laboratoryRoom', '검사실');
            if ($(result).find('initexcuroom').length > 0) {
                $(result).find('initexcuroom').each(function () {
                    $("#_laboratoryRoom").append($('<option>', {
                        value: $(this).find('basecd').text(),
                        text: $(this).find('basecdflagdesc').text()
                    }));

                });

            }
            isNextRequest();
        },
        error: function (error) {
            alert("검사실 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + error);
        }

    })
}

// 병동 조회
function searchWard() {
    $.ajax({
        url: ajax_url,
        data: "submit_id=DRMRF00113&business_id=mr&instcd=204&orddeptflag=W&drflag=M&selecttype=A&orddeptflag=W",
        type: 'get',
        dataType: 'xml',
        timeout: 40000,
        success: function (result) {
            clearOption('_inPatientWard', '병동');
            clearOption('_CosignWardList', '병동');
            clearOption('treateMentWard', '병동');
            if ($(result).find('dept').length > 0) {
                $(result).find('dept').each(function () {
                    $("#_inPatientWard").append($('<option>', {
                        value: $(this).find('deptcd').text(),
                        text: $(this).find('depthngnm').text()
                    }));
                    $("#_CosignWardList").append($('<option>', {
                        value: $(this).find('deptcd').text(),
                        text: $(this).find('depthngnm').text()
                    }));
                    $("#treateMentWard").append($('<option>', {
                        value: $(this).find('deptcd').text(),
                        text: $(this).find('depthngnm').text()
                    }));

                });

            }
            isNextRequest();
        },
        error: function (error) {
            alert("병동조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + error);
        }
    });

}




// 진료과 조회 - 입원,응급,수술
function searchInEmerOperClnDept() {
    $.ajax({
        url: ajax_url,
        data: "submit_id=DRMRF00113&business_id=mr&instcd=204&orddeptflag=D&drflag=M&selecttype=A",
        type: 'get',
        dataType: 'xml',
        timeout: 40000,
        success: function (result) {
            clearOption('_inPatientClnDept', '진료과');
            clearOption('_operationClnDept', '집도과');
            clearOption('_emergencyDept', '진료과');

            if ($(result).find('dept').length > 0) {
                $(result).find('dept').each(function () { 
                    $("#_inPatientClnDept").append($('<option>', {
                        value: $(this).find('deptcd').text(),
                        text: $(this).find('depthngnm').text()
                    }));
                    $("#_emergencyDept").append($('<option>', {
                        value: $(this).find('deptcd').text(),
                        text: $(this).find('depthngnm').text()
                    }));
                    $("#_operationClnDept").append($('<option>', {
                        value: $(this).find('deptcd').text(),
                        text: $(this).find('depthngnm').text()
                    }));
                });

            }
            isNextRequest();
        },
        error: function (error) {
            alert("진료과 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + error);
        }

    });

}

//진료과 조회 - 외래
function searchOutPatientClnDept() {
    $.ajax({
        url: ajax_url,
        data: "submit_id=DRMRF00113&business_id=mr&instcd=204&orddeptflag=O&drflag=M&selecttype=A",
        type: 'get',
        dataType: 'xml',
        timeout: 40000,
        success: function (result) {
            clearOption('_outPatientClnDept', '진료과');
            clearOption('treateMentDept', '진료과');
            clearOption('_requestDept', '진료과');

            if ($(result).find('dept').length > 0) {
                $(result).find('dept').each(function () {
                    $("#_outPatientClnDept").append($('<option>', {
                        value: $(this).find('deptcd').text(),
                        text: $(this).find('depthngnm').text()
                    }));
                    $("#treateMentDept").append($('<option>', {
                        value: $(this).find('deptcd').text(),
                        text: $(this).find('depthngnm').text()
                    }));
                    $("#_requestDept").append($('<option>', {
                        value: $(this).find('deptcd').text(),
                        text: $(this).find('depthngnm').text()
                    }));
                });

            }
            isNextRequest();
        },
        error: function (error) {
            alert("진료과 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + error);
        }
    });
}
//진료과 조회 - 검색
function searchFindClnDept() {
    $.ajax({
        url: ajax_url,
        data: "submit_id=DRMRF00113&business_id=mr&instcd=204&orddeptflag=A&drflag=M&selecttype=A",
        type: 'get',
        dataType: 'xml',
        timeout: 40000,
        success: function (result) {
            clearOption('_findDept', '진료과');
            $('#_CosignDeptList option').remove();
            $("#_CosignDeptList").append($('<option>', {
                value: '',
                text: '진료과 지정'
            }));

            if ($(result).find('dept').length > 0) {
                $(result).find('dept').each(function () {
                    $("#_findDept").append($('<option>', {
                        value: $(this).find('deptcd').text(),
                        text: $(this).find('depthngnm').text()
                    }));
                    $("#_CosignDeptList").append($('<option>', {
                        value: $(this).find('deptcd').text(),
                        text: $(this).find('depthngnm').text()
                    }));
                });

            }
            isNextRequest();
        },
        error: function (error) {
            alert("진료과 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + error);
        }

    });

}

//진료과 조회 - 검색
function searchCosignClnDept() {
    $.ajax({
        url: ajax_url,
        data: "submit_id=DRMRF00113&business_id=mr&instcd=204&orddeptflag=D&drflag=M&selecttype=A",
        type: 'get',
        dataType: 'xml',
        timeout: 40000,
        success: function (result) {
            $('#_CosignDeptList option').remove();
            $("#_CosignDeptList").append($('<option>', {
                value: '',
                text: '진료과 지정'
            }));

            if ($(result).find('dept').length > 0) {
                $(result).find('dept').each(function () {
                    $("#_CosignDeptList").append($('<option>', {
                        value: $(this).find('deptcd').text(),
                        text: $(this).find('depthngnm').text()
                    }));
                });

            }
            isNextRequest();
        },
        error: function (error) {
            alert("진료과 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + error);
        }

    })
}

// 수술 구분 조회
function searchOperationTypeList() {
    // 담당의조회
    $.ajax({
        url: ajax_url,
        data: "submit_id=DRMRF00115&business_id=mr&cdgrupid=M0013",
        type: 'get',
        dataType: 'xml',
        timeout: 40000,
        success: function (result) {
            clearOption('_operationOperationType', '수술구분');
            if ($(result).find('list').length > 0) {
                $(result).find('list').each(function () {
                    $("#_operationOperationType").append($('<option>', {
                        value: $(this).find('cdid').text(),
                        text: $(this).find('cdnm').text()
                    }));

                });

            }
            isNextRequest();
        },
        error: function (error) {
            alert("수술구분 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + error);
        }

    })
}



// 공통 데이터 조회 결과
function searchCommonDataSuccessHandler(resData) {
    if (!$.isEmptyObject(resData)) {
//        console.log("==============================");
//        console.log("GetWard : " + JSON.stringify(resData["GetWard"]));
//        console.log("GetClnDept : " + JSON.stringify(resData["GetClnDept"]));
//        console.log("GetInspectionGroupDeptList : " + JSON.stringify(resData["GetInspectionGroupDeptList"]));
//        console.log("GetOperationTypeList : " + JSON.stringify(resData["GetOperationTypeList"]));
//        console.log("GetCommonData : " + JSON.stringify(resData["GetCommonData"]));
//        console.log("==============================");

        if (!$.isEmptyObject(resData["GetWard"])) {
            wardSuccessHandler(resData["GetWard"]);
        } else {
            console.log("병동 조회 결과가 없습니다.");
        }
        if (!$.isEmptyObject(resData["GetClnDept"])) {
            clnDeptSuccessHandler(resData["GetClnDept"]);
        } else {
            console.log("과별 조회 결과가 없습니다.");
        }
        if (!$.isEmptyObject(resData["GetInspectionGroupDeptList"])) {
            inspectionGroupSuccessHandler(resData["GetInspectionGroupDeptList"]);
        } else {
            console.log("검사구분 조회 결과가 없습니다.");
        }
        if (!$.isEmptyObject(resData["GetOperationTypeList"])) {
            getOperationTypeListSuccessHandler(resData["GetOperationTypeList"]);
        } else {
            console.log("수술구분 조회 결과가 없습니다.");
        }
        if (!$.isEmptyObject(resData["GetCommonData"])) {
            searchWarningMessageSuccessHandler(resData["GetCommonData"]);
        } else {
            console.log("경고메시지 조회 결과가 없습니다.");
        }
        userPatientSearchSetting();
    } else {
        console.log("공통 데이터 조회가 결과가 없습니다.");
    }
};


// 공통 데이터 조회
function inpatientCommonDoctorData(userDeptCode) {
    searchDoctor("");
    //searchCharge("");
    /*
     * // 화면 변수 var paramObject = { "ServiceList": [{ "serviceType": "H",
     * "methodName": "GetCharge", "params": { "clnDept": userDeptCode } }, {
     * "serviceType": "H", "methodName": "GetDoctor", "params": { "clnDept":
     * userDeptCode } }] };
     *  // 공통 변수 var args = { "sCode": "InitializeServiceData", "param":
     * paramObject, "userId": getLocalStorage("userId"), "patientCode":
     * COMMON.LOCAL.eform.patient.PatientCode, "reqType": "webserive",
     * "serviceName": "HospitalSvc.aspx" }; var reqSetting =
     * COMMON.util.makeReqParam(args); COMMON.plugin.doRequest(reqSetting,
     * inpatientCommonDoctorDataSuccessHandler, errorHandler);
     */
};


// 2017.08.08 알림 메시지 조회
function searchWarningMessage() {
    // 화면 변수
    var paramObject = {};
    // 공통 변수
    var args = {
        "sCode": "GetCommonData",
        "param": paramObject,
        "userId": getLocalStorage("userId"),
        "patientCode": COMMON.LOCAL.eform.patient.PatientCode,
        "reqType": "webserive",
        "serviceName": "ConsentSvc.aspx"
    };
    var reqSetting = COMMON.util.makeReqParam(args);
    COMMON.plugin.doRequest(reqSetting, searchWarningMessageSuccessHandler, errorHandler);
};

// 알림 메시지 조회 조회 결과
function searchWarningMessageSuccessHandler(resData) {
    console.log("알림메시지 조회 결과 :" + JSON.stringify(resData));
    if (!$.isEmptyObject(resData)) {
        localStorage.setItem("ParamJson", resData.ParamJson);
    }
    isNextRequest();
};

// 입원 - 병동 조회
function fnSearhWard() {
    // 화면 변수
    var paramObject = {};
    // 공통 변수
    var args = {
        "sCode": "GetWard",
        "param": paramObject,
        "userId": getLocalStorage("userId"),
        "patientCode": COMMON.LOCAL.eform.patient.PatientCode,
        "reqType": "webserive",
        "serviceName": "HospitalSvc.aspx"
    };
    var reqSetting = COMMON.util.makeReqParam(args);
    COMMON.plugin.doRequest(reqSetting, wardSuccessHandler, errorHandler);

};

// 병동 조회 결과
function wardSuccessHandler(resData) {
    // 모두 삭제
    $("#_inPatientWard option").remove();
    // Title option add
    $("#_inPatientWard").append($('<option>', {
        value: '',
        text: '병동'
    }));
    COMMON.util.addSelectOptions("_inPatientWard", resData);
    isNextRequest();
};

// 담당의 조회
function fnSearchCharge(clnDept) {
    // 화면 변수
    var paramObject = {
        "clnDept": clnDept // 사용자 소속과
    };
    // 공통 변수
    var args = {
        "sCode": "GetCharge",
        "param": paramObject,
        "userId": getLocalStorage("userId"),
        "patientCode": COMMON.LOCAL.eform.patient.PatientCode,
        "reqType": "webserive",
        "serviceName": "HospitalSvc.aspx"
    };
    var reqSetting = COMMON.util.makeReqParam(args);
    COMMON.plugin.doRequest(reqSetting, chargeSuccessHandler, errorHandler);
};

// 담당의 조회 결과
function chargeSuccessHandler(resData) {
    // 모두 삭제
    $("#_inPatientCharge option").remove();
    $("#_inPatientCharge").append($('<option>', {
        value: '',
        text: '담당의'
    }));
    COMMON.util.addSelectOptions("_inPatientCharge", resData); // 입원 담당의 set
    isNextRequest();
};

// 담당의사 조회
function fnSearchDoctor(clnDept) {
    // 화면 변수
    var paramObject = {
        "clnDept": clnDept // 사용자 진료과
    };
    // 공통 변수
    var args = {
        "sCode": "GetDoctor",
        "param": paramObject,
        "userId": getLocalStorage("userId"),
        "patientCode": COMMON.LOCAL.eform.patient.PatientCode,
        "reqType": "webserive",
        "serviceName": "HospitalSvc.aspx"
    };
    var reqSetting = COMMON.util.makeReqParam(args);
    COMMON.plugin.doRequest(reqSetting, doctorSuccessHandler, errorHandler);
};

// 담당의사 조회 결과
function doctorSuccessHandler(resData) {
    // 모두 삭제
    $("#_inPatientDoctor option").remove();
    $("#_inPatientDoctor").append($('<option>', {
        value: '',
        text: '담당의사'
    }));
    COMMON.util.addSelectOptions("_inPatientDoctor", resData);
    isNextRequest();
};

// 외래 진료의 조회
function fnSearchOutDoctor() {
    // 화면 변수
    var paramObject = {
        "clnDept": $("#_outPatientClnDept option:selected").val(), // 진료과 코드 
        "clnDate": $("#_outPatientCalendar").val().replace(/-/g, "") // 진료일
    };
    // 공통 변수
    var args = {
        "sCode": "GetOutDoctor",
        "param": paramObject,
        "userId": getLocalStorage("userId"),
        "patientCode": COMMON.LOCAL.eform.patient.PatientCode,
        "reqType": "webserive",
        "serviceName": "HospitalSvc.aspx"
    };
    var reqSetting = COMMON.util.makeReqParam(args);
    COMMON.plugin.doRequest(reqSetting, outDoctorSuccessHandler, errorHandler);
};

// 외래 진료의 조회 결과
function outDoctorSuccessHandler(resData) {
    // 모두 삭제
    $("#_outPatientDoctor option").remove();
    $("#_outPatientDoctor").append($('<option>', {
        value: '',
        text: '진료의'
    }));
    COMMON.util.addSelectOptions("_outPatientDoctor", resData);
    if (selectboxDefalutSelected("_outPatientDoctor", getLocalStorage("userId"))) {
        $("#_outPatientSearch").trigger("click");
    }
};

// 과별 조회
function fnSeachClnDept() {
    // 화면 변수
    var paramObject = {};

    // 공통 변수
    var args = {
        "sCode": "GetClnDept",
        "param": paramObject,
        "userId": getLocalStorage("userId"),
        "patientCode": COMMON.LOCAL.eform.patient.PatientCode,
        "reqType": "webserive",
        "serviceName": "HospitalSvc.aspx"
    };
    var reqSetting = COMMON.util.makeReqParam(args);
    COMMON.plugin.doRequest(reqSetting, clnDeptSuccessHandler, errorHandler);

};

// 과별 조회 결과
function clnDeptSuccessHandler(resData) {
    // 입원 진료과 설정
    $("#_inPatientClnDept option").remove();
    $("#_inPatientClnDept").append($('<option>', {
        value: '',
        text: '진료과'
    }));
    COMMON.util.addSelectOptions("_inPatientClnDept", resData);

    // 외래 진료과 설정
    $("#_outPatientClnDept option").remove();
    $("#_outPatientClnDept").append($('<option>', {
        value: '',
        text: '진료과'
    }));
    COMMON.util.addSelectOptions("_outPatientClnDept", resData);

    // 수술 수술과 설정
    $("#_operationClnDept option").remove();
    $("#_operationClnDept").append($('<option>', {
        value: '',
        text: '수술과'
    }));
    COMMON.util.addSelectOptions("_operationClnDept", resData);

    // 검색에 진료과 설정
    $("#_findDept option").remove();
    $("#_findDept").append($('<option>', {
        value: '',
        text: '진료과'
    }));
    COMMON.util.addSelectOptions("_findDept", resData);

    // 동의서 검색에 진료과 설정
    $("#_efromClnDept option").remove();
    COMMON.util.addSelectOptions("_efromClnDept", resData);
    isNextRequest();
};


// 확인 부서 조회
function searchGetViewClnDept() {
    // 화면 변수
    var paramObject = {};

    // 공통 변수
    var args = {
        "sCode": "GetViewClnDept",
        "param": paramObject,
        "userId": getLocalStorage("userId"),
        "patientCode": COMMON.LOCAL.eform.patient.PatientCode,
        "reqType": "webserive",
        "serviceName": "HospitalSvc.aspx"
    };
    var reqSetting = COMMON.util.makeReqParam(args);
    COMMON.plugin.doRequest(reqSetting, searchGetViewClnDeptSuccessHandler, errorHandler);
};

// 확인 부서 조회 결과
function searchGetViewClnDeptSuccessHandler(resData) {
    // 동의서 검색에 진료과 설정
    $("#_efromCmtClnDept option").remove();
    COMMON.util.addSelectOptions("_efromCmtClnDept", resData);

    isNextRequest();
};



// 검사 구분 조회
function fnSearchInspectionGroup() {
    // 화면 변수
    var paramObject = {};
    // 공통 변수
    var args = {
        "sCode": "GetInspectionGroupDeptList",
        "param": paramObject,
        "userId": getLocalStorage("userId"),
        "patientCode": COMMON.LOCAL.eform.patient.PatientCode,
        "reqType": "webserive",
        "serviceName": "HospitalSvc.aspx"
    };
    var reqSetting = COMMON.util.makeReqParam(args);
    COMMON.plugin.doRequest(reqSetting, inspectionGroupSuccessHandler, errorHandler);
};

// 검사구분 조회 결과
function inspectionGroupSuccessHandler(resData) {
    // 모두 삭제
    $("#_laboratorDept option").remove();
    $("#_laboratorDept").append($('<option>', {
        value: '',
        text: '검사구분'
    }));
    console.log("검사구분 조회 결과 : " + JSON.stringify(resData));
    COMMON.util.addSelectOptions("_laboratorDept", resData);
    isNextRequest();
};
 
// 수술 구분 조회
function fnGetOperationTypeList() {
    // 화면 변수
    var paramObject = {};
    // 공통 변수
    var args = {
        "sCode": "GetOperationTypeList",
        "param": paramObject,
        "userId": getLocalStorage("userId"),
        "patientCode": COMMON.LOCAL.eform.patient.PatientCode,
        "reqType": "webserive",
        "serviceName": "HospitalSvc.aspx"
    };
    var reqSetting = COMMON.util.makeReqParam(args);
    COMMON.plugin.doRequest(reqSetting, getOperationTypeListSuccessHandler, errorHandler);
};

// 수술구분 조회 결과
function getOperationTypeListSuccessHandler(resData) {
    // 모두 삭제
    $("#_operationOperationType option").remove();
    $("#_operationOperationType").append($('<option>', {
        value: '',
        text: '수술구분'
    }));

    console.log("수술 구분 조회 결과 : " + JSON.stringify(resData));
    COMMON.util.addSelectOptions("_operationOperationType", resData);
    isNextRequest();
};

// ------ 공통 검색 영역 END --------------------

// 입원 - 환자 조회 (submit)
function InpatientSearch_submit() {

    $("#_inPatientList ul").remove();
    var clnDate = $("#_inPatientCalendar").val(); // 진료일
    clnDate = clnDate.replace(/-/gi, "")
    var ward = ($("#_inPatientWard option:selected").val() == "") ? "-" : $("#_inPatientWard option:selected").val(); // 병동
    var clnDept = ($("#_inPatientClnDept option:selected").val() == "") ? "-" : $("#_inPatientClnDept option:selected").val(); // 진료과
    var dortorId = ($("#_inPatientDoctor option:selected").val() == "") ? "-" : $("#_inPatientDoctor option:selected").val(); // 주치의
    var writeState = $('#_inPatientWriteState option:selected').val();
    var nodataflag = false;
    $("#_inPatientList ul").remove();
    COMMON.plugin.wifiCheck(wifiCheckFn);

    var checkVal = localStorage.getItem("wifiCheckVal");
    if (checkVal < -90) {
        isRun_3 = false;
        alert("WIFI 수신 감도가 좋지 않습니다. WIFI 통신이 잘 되는 곳에서 사용 바랍니다.");
    } else {
        //loadingbar_display(); 
        COMMON.plugin.loadingBar("show", "환자를 검색 중입니다.");
        $.ajax({
            url: ajax_url,
            data: "submit_id=DRMRF00116&business_id=mr&instcd=204&ordtype=I&pid=&orddd=" + clnDate + "&orddeptcd=" + clnDept + "&orddrid=" + dortorId + "&atdoctid=-&elbulbodstat=-&wardcd=" + ward + "&patflagstat=-&opstatcd=-&srchflag=-&currentdd=" + today,
            type: 'get',
            dataType: 'xml',
            timeout: 30000,
            success: function (result) {
                isRun_3 = false;
                //console.log(ajax_url + "&" + "submit_id=DRMRF00116&business_id=mr&instcd=204&ordtype=I&pid=&orddd=" + clnDate + "&orddeptcd=" + clnDept + "&orddrid=" + dortorId + "&atdoctid=-&elbulbodstat=-&wardcd=" + ward + "&patflagstat=-&opstatcd=-&srchflag=-&currentdd=" + today);
                if ($(result).find('list').length > 0) {
                    $(result).find('list').each(function () {
                        var ul = $("#_inPatientListTemplate ul").clone();
                        var sex = ($(this).find('sa').text().replace(/[^a-z]/gi, "") == "M") ? "남자" : "여자";
                        var age = $(this).find('sa').text().replace(/[^0-9]/g, "");
                        ul.find("._inPatientCode").text($(this).find('pid').text()); // 환자
                        // 등록번호
                        ul.find("._inPatientAge").text($(this).find('sa').text()); // 환자
                        // 나이/
                        // 성별
                        // ul.find("._inPatientSex").text((patient.Sex == "M") ? "남"
                        // : "여"); // 환자 성별
                        ul.find("._inPatientName").text($(this).find('hngnm').text()); // 환자
                        // 성명
                        ul.find("._inPatientclnDeptName").text($(this).find('orddeptnm').text()); // 환자
                        // 진료과명
                        ul.find("._inPatientRoom").text((($(this).find('roomcd').text() == "") ? "-" : $(this).find('roomcd').text())); // 병동
                        ul.find("._inPatientChargeName").text(($(this).find('atdoctnm').text() == "") ? "-" : $(this).find('atdoctnm').text()); // 주치의
                        //레지던트
                        ul.find("._inPatientDoctorName").text(($(this).find('medispclnm').text() == "") ? "-" : $(this).find('medispclnm').text()); // 담당의
                        //교수님

                        var visitType = $(this).find('ordtype').text();
                        var patient = new Object();
                        var Birthday = $(this).find('rrgstno1').text();
                        patient.rrgstfullno = $(this).find('rrgstno1').text();
                        Birthday = Birthday.substring(0, 8);
                        patient.AdmissionDate = $(this).find('orddd').text();
                        patient.Age = age;
                        patient.Sex = sex;
                        patient.zipnm = $(this).find('zipnm').text();
                        patient.hometel = $(this).find('hometel').text();
                        patient.mpphontel = $(this).find('mpphontel').text(); 
                        patient.ClnDeptCode = $(this).find('orddeptcd').text();
                        patient.ClnDeptName = $(this).find('orddeptnm').text();
                        patient.ChargeId = $(this).find('atdoctid').text();
                        patient.ChargeName = $(this).find('atdoctnm').text(); // 주치의
                        patient.medispclnm = $(this).find('medispclnm').text(); 
                        // 2021-10-25
                        patient.medispclid = $(this).find('medispclid').text(); // 주치의
                        patient.DoctorId = $(this).find('orddrid').text();
                        patient.DoctorName = $(this).find('orddrnm').text(); // 진료의
                        patient.PatientCode = $(this).find('pid').text();
                        patient.fulrgstno = Birthday + "XXXXXX" //$(this).find('rrgstno1').text();
                        patient.PatientName = $(this).find('hngnm').text();
                        patient.deptengabbr = $(this).find('deptengabbr').text();
                        patient.Room = $(this).find('roomcd').text();
                        patient.VisitType = visitType;
                        patient.Ward = $(this).find('roomcd').text();
                        patient.Birthday = $(this).find('brthdd').text();
                        patient.Room = $(this).find('roomcd').text();
                        patient.Cretno = $(this).find('cretno').text();
                        patient.diagnm = $(this).find('diagnm').text(); // 진단명
                        patient.diagengnm = $(this).find('diagengnm').text(); // 
                        patient.wardcd = $(this).find('wardcd').text(); // 
                        patient.opnm = $(this).find('opnm').text(); // 

                        // patient_ary.push(patient);

                        ul.attr("attr-data", JSON.stringify(patient));

                        isTodayInpatient(ul, patient);
                        // 해당 환자 클릭 이벤트
                        ul.on("click", function () {
                            $("#_inPatientList ul").removeClass("on");
                            $(this).addClass("on");

                            // 동명인여부확인
                            $("._patientDetailInfo").hide();
                            $(".patient_info").attr("attr-data", "");
                            $("._detail").text("");

                            // 연관 리스트 초기화
                            $("#_relationConsentList").empty();
                            // 검색 동의서 리스트 초기화
                            $("#_consentList").empty();
                            // 작성 동의서 리스트 초기화
                            $("#_consentALLList").empty();

                            var isOnEmergencyMenu = $("#_emergency").hasClass("on");

                            var data = $(this).attr("attr-data");
                            // logAlert("해당 환자정보 검색 : " + data);
                            data = JSON.parse(data);
                            // alert(JSON.stringify(data.PatientCode));
                            // 동명인여부확인

                            // var searchType =
                            // $(':radio[name="_consentSearchType"]:checked').val();
                            // if(searchType=="C"){
                            // $('#_contentSearch').trigger("click");
                            // }else if(searchType=="S"){
                            // $('#radio_tmp_14').trigger("click");
                            // }else{
                            // $('#radio_tmp_15').trigger("click");
                            // }
                            isSameName("_inPatientList", data);
                            if (data.PatientCode != undefined && data.PatientCode != "") {

                                fnSearchPatientDetailInfo_submit(data);
                            } else {
                                alert("환자 상세 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다.")
                            }
                        });

                    	if(writeState =="" || writeState =="A"){  
                			if($(this).find('saveyn').text() == "Y"){ 
                    			ul.append('<div id="writeStateCircle"></div>'); 
                    		}
                			if($(this).find('saveyn').text() == "T"){ 
                    			ul.append('<div id="writeStateCircleTemp"></div>'); 
                    		}
                			                          $("#_inPatientList").append(ul);
                    	}else if(writeState=="N"){
                    		if($(this).find('saveyn').text() == "N"){  
                                $("#_inPatientList").append(ul);
                    		}
                    	}else if(writeState=="T"){
                    		if($(this).find('saveyn').text() == "T"){ 
                    			ul.append('<div id="writeStateCircleTemp"></div>');
                                $("#_inPatientList").append(ul);
                    		}
                    	} 
                    	else{
                    		if($(this).find('saveyn').text() == "Y"){ 
                    			ul.append('<div id="writeStateCircle"></div>');
                                $("#_inPatientList").append(ul);
                    		}
                    	}
                    });
                } else {
                    makeNoDataHtml("_inPatientList");
                }
                if(nodataflag){ 
        	        makeNoDataHtml("_inPatientList");
                }
                COMMON.plugin.loadingBar("hide", "");
                isNextRequest();
            },
            error: function (error) {
                isRun_3 = false;
                COMMON.plugin.loadingBar("hide", "");
                alert("환자 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + JSON.stringify(error));
            }

        })
    }


}


// 외래 - 환자 조회 (submit)
function outPatientSearch_submit() {
    var clnDate = $("#_outPatientCalendar").val(); // 진료일
    clnDate = clnDate.replace(/-/gi, "")
    var clnDept = ($("#_outPatientClnDept option:selected").val() == "") ? "-" : $("#_outPatientClnDept option:selected").val(); // 진료과
    var dortorId = ($("#_outPatientDoctor option:selected").val() == "") ? "-" : $("#_outPatientDoctor option:selected").val(); // 주치의
    $("#_outPatientList ul").remove();
    var writeState = $('#_outPatientWriteState option:selected').val();
    var date = new Date();
    var nodataflag = false;
    var year = date.getFullYear();
    var month = date.getMonth() + 1
    var day = date.getDate();
    if (month < 10) {
        month = "0" + month;
    }
    if (day < 10) {
        day = "0" + day;
    }
    var today = year + "" + month + "" + day;

    COMMON.plugin.wifiCheck(wifiCheckFn);

    var checkVal = localStorage.getItem("wifiCheckVal");
    if (checkVal < -90) {
        isRun_4 = false;
        alert("WIFI 수신 감도가 좋지 않습니다. WIFI 통신이 잘 되는 곳에서 사용 바랍니다.");
    } else {
        //loadingbar_display();
        COMMON.plugin.loadingBar("show", "환자를 검색 중입니다.");
        $.ajax({
            url: ajax_url,
            data: "submit_id=DRMRF00116&business_id=mr&instcd=204&ordtype=O&pid=&orddd=" + clnDate + "&orddeptcd=" + clnDept + "&orddrid=" + dortorId + "&atdoctid=-&elbulbodstat=-&wardcd=&patflagstat=-&opstatcd=-&srchflag=-&currentdd=" + today,
            type: 'get',
            dataType: 'xml',
            timeout: 40000,
            success: function (result) {
                isRun_4 = false;
                //console.log(result);
                if ($(result).find('list').length > 0) {
                    $(result).find('list').each(function () {
                        var ul = $("#_outPatientListTemplate ul").clone();
                        var sex = ($(this).find('sa').text().replace(/[^a-z]/gi, "") == "M") ? "남자" : "여자";
                        var age = $(this).find('sa').text().replace(/[^0-9]/g, "");
                        ul.find("._outPatientCode").text($(this).find('pid').text()); // 환자
                        // 등록번호
                        ul.find("._outPatientAge").text($(this).find('sa').text()); // 환자
                        // 나이/
                        // 성별
                        // ul.find("._inPatientSex").text((patient.Sex == "M") ? "남"
                        // : "여"); // 환자 성별
                        ul.find("._outPatientName").text($(this).find('hngnm').text()); // 환자
                        // 성명
                        var orddeptcd_orddrnm = vauleNullCheck($(this).find('orddeptnm').text(), '-') + "/" + vauleNullCheck($(this).find('medispclnm').text(), '-');
                        ul.find("._outPatientDoctorName").text(orddeptcd_orddrnm); // 담당의사

                        var visitType = $(this).find('ordtype').text();

                        var patient = new Object();
                        var Birthday = $(this).find('rrgstno1').text();
                        patient.rrgstfullno = $(this).find('rrgstno1').text();

                        patient.AdmissionDate = $(this).find('orddd').text();
                        patient.Age = age;
                        patient.Sex = sex;
                        patient.zipnm = $(this).find('zipnm').text();
                        patient.hometel = $(this).find('hometel').text();
                        patient.mpphontel = $(this).find('mpphontel').text(); 
                        patient.ClnDeptCode = $(this).find('orddeptcd').text();
                        Birthday = Birthday.substring(0, 8);
                        patient.fulrgstno = Birthday + "XXXXXX" //$(this).find('rrgstno1').text();
                        patient.ClnDeptName = $(this).find('orddeptnm').text();
                        patient.ChargeId = $(this).find('atdoctid').text();
                        patient.ChargeName = $(this).find('atdoctnm').text(); // 주치의
                        patient.medispclnm = $(this).find('medispclnm').text(); 
                        // 2021-10-25
                        patient.medispclid = $(this).find('medispclid').text(); // 주치의
                        patient.DoctorId = $(this).find('orddrid').text();
                        if(clnDept=="2370200000"){ 
                            patient.DoctorName = $(this).find('healexamnm').text(); // 건진구분
                            patient.diagnm = $(this).find('pkgnm').text(); // 패키지명
                        }else{
                            patient.DoctorName = $(this).find('orddrnm').text(); // 진료의
                            patient.diagnm = $(this).find('diagnm').text(); // 진단명
                        }
                        patient.diagengnm = $(this).find('diagengnm').text(); // 진단명
                        patient.PatientCode = $(this).find('pid').text();
                        patient.PatientName = $(this).find('hngnm').text();
                        patient.Room = $(this).find('roomcd').text();
                        patient.VisitType = visitType;
                        patient.Ward = $(this).find('roomcd').text();
                        patient.deptengabbr = $(this).find('deptengabbr').text();
                        patient.Birthday = $(this).find('brthdd').text();
                        patient.Room = $(this).find('roomcd').text();
                        patient.Cretno = $(this).find('cretno').text();
                        patient.wardcd = $(this).find('wardcd').text(); 

                        // patient_ary.push(patient);

                        ul.attr("attr-data", JSON.stringify(patient));

                        isTodayInpatient(ul, patient);

                        // 해당 환자 클릭 이벤트
                        ul.on("click", function () {
                            $("#_outPatientList ul").removeClass("on");
                            $(this).addClass("on");

                            // 동명인여부확인 해야함
                            $("._patientDetailInfo").hide();
                            $(".patient_info").attr("attr-data", "");
                            $("._detail").text("");
                            // 연관 리스트 초기화
                            $("#_relationConsentList li").remove();
                            // 검색 동의서 리스트 초기화
                            $("#_consentList li").remove();
                            // 작성 동의서 리스트 초기화
                            $("#_consentALLList li").remove();

                            var isOnEmergencyMenu = $("#_emergency").hasClass("on");
                            var data = $(this).attr("attr-data");
                            // logAlert("해당 환자정보 검색 : " + data);
                            data = JSON.parse(data);
                            // alert(JSON.stringify(data.PatientCode));
                            // 동명인여부확인 
                            isSameName("_outPatientList", data);
                            if (data.PatientCode != undefined && data.PatientCode != "") {
                                fnSearchPatientDetailInfo_submit(data);
                            } else {
                                alert("환자 상세 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다.")
                            }
                        }); 
                        
                        if(writeState =="" || writeState =="A"){  
                			if($(this).find('saveyn').text() == "Y"){ 
                    			ul.append('<div id="writeStateCircle"></div>'); 
                    		}
                			if($(this).find('saveyn').text() == "T"){ 
                    			ul.append('<div id="writeStateCircleTemp"></div>'); 
                    		}
 
                            $("#_outPatientList").append(ul);
                    	}else if(writeState=="N"){
                    		if($(this).find('saveyn').text() == "N"){  
                                $("#_outPatientList").append(ul);
                    		}
                    	}else if(writeState=="T"){
                    		if($(this).find('saveyn').text() == "T"){ 
                    			ul.append('<div id="writeStateCircleTemp"></div>');
                                $("#_outPatientList").append(ul);
                    		}
                    	}
 
						
                    	else{
                    		if($(this).find('saveyn').text() == "Y"){ 
                    			ul.append('<div id="writeStateCircle"></div>');
                                $("#_outPatientList").append(ul);
                    		}
                    	} 
                    });
                } else {
                    makeNoDataHtml("_outPatientList");
                }
                if(nodataflag){
                	makeNoDataHtml("_outPatientList");
                }
            	COMMON.plugin.loadingBar("hide", "");
                isNextRequest();
            },
            error: function (error) {
            	COMMON.plugin.loadingBar("hide", "");
                isRun_4 = false;
                alert("환자 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + JSON.stringify(error));
            }

        })
    }

}


// 응급 - 환자 조회 (submit)
function emergencySearch_submit() {
    $("#_emergencyList ul").remove();
    var clnDate = $("#_emergencyCalender").val(); // 진료일
    clnDate = clnDate.replace(/-/gi, "")
    var searchType = $("#_emerVisitType option:selected").val();
    var clnDept = ($("#_emergencyDept option:selected").val() == "") ? "-" : $("#_emergencyDept option:selected").val(); // 진료과
    var date = new Date();
    var nodataflag = false;
    var writeState = $('#_emrWriteState option:selected').val();
    var year = date.getFullYear();
    var month = date.getMonth() + 1
    var day = date.getDate();
    if (month < 10) {
        month = "0" + month;
    }
    if (day < 10) {
        day = "0" + day;
    }
    var today = year + "" + month + "" + day;

    COMMON.plugin.wifiCheck(wifiCheckFn);

    var checkVal = localStorage.getItem("wifiCheckVal");
    if (checkVal < -90) {
        isRun_5 = false;
        alert("WIFI 수신 감도가 좋지 않습니다. WIFI 통신이 잘 되는 곳에서 사용 바랍니다.");
    } else {
        //loadingbar_display();
        COMMON.plugin.loadingBar("show", "환자를 검색 중입니다.");
        $.ajax({
            url: ajax_url,
            data: "submit_id=DRMRF00116&business_id=mr&instcd=204&ordtype=E&pid=&orddd=" + clnDate + "&orddeptcd=" + clnDept + "&orddrid=-&atdoctid=-&elbulbodstat=-&wardcd=&patflagstat=-&opstatcd=-&srchflag=" + searchType + "&currentdd=" + today,
            type: 'get',
            dataType: 'xml',
            timeout: 40000,
            success: function (result) {
                isRun_5 = false;
                //alert(ajax_url+"?"+"submit_id=DRMRF00116&business_id=mr&instcd=204&ordtype=E&pid=&orddd=" + clnDate + "&orddeptcd=" + clnDept + "&orddrid=-&atdoctid=-&elbulbodstat=-&wardcd=&patflagstat=-&opstatcd=-&srchflag="+searchType+"&currentdd=" + today);
                if ($(result).find('list').length > 0) {
                    $(result).find('list').each(function () {
                        var ul = $("#_emergencyListTemplate ul").clone();
                        //						alert($(this).find('bed').text());
                        var sex = ($(this).find('sa').text().replace(/[^a-z]/gi, "") == "M") ? "남자" : "여자";
                        var age = $(this).find('sa').text().replace(/[^0-9]/g, "");
                        ul.find("._emergencyCode").text($(this).find('pid').text()); // 환자
                        // 등록번호
                        ul.find("._emergencyAge").text($(this).find('sa').text()); // 환자
                        // 나이/
                        // 성별
                        // ul.find("._inPatientSex").text((patient.Sex == "M") ? "남"
                        // : "여"); // 환자 성별
                        ul.find("._emergencyName").text($(this).find('hngnm').text()); // 환자
                        // 성명
                        ul.find("._emergencyChargeName").text(($(this).find('ermedispclnm').text() == "") ? "-" : $(this).find('ermedispclnm').text()); // 담당의사
                        // 병상번호
                        ul.find("._emergencyEmergencyArea").text(($(this).find('bed').text() == "") ? "-" : $(this).find('bed').text()); // 담당의사

                        // ul.find("._emergencyEmergencyArea").text((patient.Bedno
                        // == "") ? "ER" : "ER - " + patient.Bedno); // 환자 응급구역
                        ul.find("._emergencyClnDeptCode").text($(this).find('erorddeptnm').text());

                        var visitType = $(this).find('ordtype').text();

                        var patient = new Object();
                        var Birthday = $(this).find('rrgstno1').text();
                        patient.rrgstfullno = $(this).find('rrgstno1').text();
                        patient.AdmissionDate = $(this).find('orddd').text();
                        patient.Age = age;
                        patient.Sex = sex;
                        patient.zipnm = $(this).find('zipnm').text();
                        patient.hometel = $(this).find('hometel').text();
                        patient.mpphontel = $(this).find('mpphontel').text(); 
                        patient.ClnDeptCode = $(this).find('orddeptcd').text();
                        patient.ClnDeptName = $(this).find('orddeptnm').text();
                        patient.ChargeId = $(this).find('atdoctid').text();
                        patient.ChargeName = $(this).find('ermedispclnm').text(); // 주치의 
                        // 2021-10-25
                        patient.medispclid = $(this).find('ermedispclid').text(); // 주치의
                        patient.medispclnm = $(this).find('ermedispclnm').text(); // 주치의
                        patient.DoctorId = $(this).find('orddrid').text();
                        patient.DoctorName = $(this).find('orddrnm').text(); // 진료의
                        patient.PatientCode = $(this).find('pid').text();
                        patient.PatientName = $(this).find('hngnm').text();
                        patient.Room = $(this).find('roomcd').text();
                        patient.diagnm = $(this).find('diagnm').text(); // 진단명
                        patient.diagengnm = $(this).find('diagengnm').text(); // 진단명

                        patient.deptengabbr = $(this).find('deptengabbr').text();
                        patient.VisitType = visitType;
                        patient.Ward = $(this).find('roomcd').text();
                        patient.Birthday = $(this).find('brthdd').text();
                        patient.Room = $(this).find('roomcd').text();
                        patient.Cretno = $(this).find('cretno').text();
                        Birthday = Birthday.substring(0, 8);
                        patient.fulrgstno = Birthday + "XXXXXX" //$(this).find('rrgstno1').text();
                        patient.wardcd = $(this).find('wardcd').text(); // 진단명

                        patient.Bed = $(this).find('bed').text();

                        // patient_ary.push(patient);

                        ul.attr("attr-data", JSON.stringify(patient));

                       
                        // 해당 환자 클릭 이벤트
                        ul.on("click", function () {
                            $("#_emergencyList ul").removeClass("on");
                            $(this).addClass("on");

                            // 동명인여부확인 해야함
                            $("._patientDetailInfo").hide();
                            $(".patient_info").attr("attr-data", "");
                            $("._detail").text("");
                            // 연관 리스트 초기화
                            $("#_relationConsentList li").remove();
                            // 검색 동의서 리스트 초기화
                            $("#_consentList li").remove();
                            // 작성 동의서 리스트 초기화
                            $("#_consentALLList li").remove();

                            var isOnEmergencyMenu = $("#_emergency").hasClass("on");
                            var data = $(this).attr("attr-data");
                            // logAlert("해당 환자정보 검색 : " + data);
                            data = JSON.parse(data);
                             //alert(data.ClnDeptCode + " : " + data.ClnDeptName);
                            // 동명인여부확인
                            isSameName("_emergencyList", data);
                            if (data.PatientCode != undefined && data.PatientCode != "") {
                                fnSearchPatientDetailInfo_submit(data);
                            } else {
                                alert("환자 상세 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다.")
                            }

                        }); 

                        
                        if(writeState =="" || writeState =="A"){  
                			if($(this).find('saveyn').text() == "Y"){ 
                    			ul.append('<div id="writeStateCircle"></div>'); 
                    		}
                			if($(this).find('saveyn').text() == "T"){ 
                    			ul.append('<div id="writeStateCircleTemp"></div>'); 
                    		} 
                            $("#_emergencyList").append(ul);
                    	}else if(writeState=="N"){
                    		if($(this).find('saveyn').text() == "N"){  
                                $("#_emergencyList").append(ul);
                    		}
                    	}else if(writeState=="T"){
                    		if($(this).find('saveyn').text() == "T"){ 
                    			ul.append('<div id="writeStateCircleTemp"></div>');
                                $("#_emergencyList").append(ul);
                    		}
                    	}
 
                        
                    	else{
                    		if($(this).find('saveyn').text() == "Y"){ 
                    			ul.append('<div id="writeStateCircle"></div>');
                                $("#_emergencyList").append(ul);
                    		}
                    	} 
                    });
                } else {
                    makeNoDataHtml("_emergencyList");
                }
                if(nodataflag){
        	        makeNoDataHtml("_emergencyList"); 
                }
            	COMMON.plugin.loadingBar("hide", "");
                isNextRequest();
            },
            error: function (error) {
            	COMMON.plugin.loadingBar("hide", "");
                isRun_5 = false;
                alert("환자 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + JSON.stringify(error));
            }

        })

    }

}


// 수술 - 환자 조회 (submit)
function operationSearch_submit() {
    $("#_operationList ul").remove();
    var clnDate = $("#_operationCalender").val(); // 수술일
    clnDate = clnDate.replace(/-/gi, "")
    var clnDept = ($("#_operationClnDept option:selected").val() == "") ? "-" : $("#_operationClnDept option:selected").val(); // 진료과
    var Doctor = ($("#_OperationDoctor option:selected").val() == "") ? "-" : $("#_OperationDoctor option:selected").val(); // 집도의
    var OperationType = ($("#_operationOperationType option:selected").val() == "") ? "-" : $("#_operationOperationType option:selected").val(); // 마취구분

    var writeState = $('#_OperationWriteState option:selected').val();
    var nodataflag = false;
    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() + 1
    var day = date.getDate();
    if (month < 10) {
        month = "0" + month;
    }
    if (day < 10) {
        day = "0" + day;
    }
    var today = year + "" + month + "" + day;

    COMMON.plugin.wifiCheck(wifiCheckFn);

    var checkVal = localStorage.getItem("wifiCheckVal");
    if (checkVal < -90) {
        isRun_6 = false;
        alert("WIFI 수신 감도가 좋지 않습니다. WIFI 통신이 잘 되는 곳에서 사용 바랍니다.");
    } else {
        COMMON.plugin.loadingBar("show", "환자를 검색 중입니다.");
        //loadingbar_display();
        $.ajax({
            url: ajax_url,
            data: "submit_id=DRMRF00116&business_id=mr&instcd=204&ordtype=P&pid=&orddd=" + clnDate + "&orddeptcd=" + clnDept + "&orddrid=" + Doctor + "&atdoctid=-&elbulbodstat=-&wardcd=&patflagstat=-&opstatcd=" + OperationType + "&srchflag=-&currentdd=" + today,
            type: 'get',
            dataType: 'xml',
            timeout: 40000,
            success: function (result) {
                isRun_6 = false;
                //console.log(result);
                if ($(result).find('list').length > 0) {
                    $(result).find('list').each(function () {
                        var ul = $("#_operationListTemplate ul").clone();
                        var sex = ($(this).find('sa').text().replace(/[^a-z]/gi, "") == "M") ? "남자" : "여자";
                        var age = $(this).find('sa').text().replace(/[^0-9]/g, "");
                        ul.find("._operationCode").text($(this).find('pid').text()); // 환자
                        // 등록번호
                        ul.find("._operationAge").text($(this).find('sa').text() + "/" + $(this).find('anstmthdnm').text()); // 환자
                        // 나이/
                        // 성별
                        // ul.find("._inPatientSex").text((patient.Sex == "M") ? "남"
                        // : "여"); // 환자 성별
                        ul.find("._operationName").text($(this).find('hngnm').text()); // 환자
                        // 성명
                        ul.find("._operationOperationRoom").text((($(this).find('oproomnm').text() == "") ? "-" : $(this).find('oproomnm').text())); // 수술룸

                        ul.find("._operationOperationDate").text($(this).find('roomcd').text()); // 수술일
                        // opcnfmdd
                        ul.find("._operationOperationDept").text($(this).find('perfdeptnm').text()); //


                        var _operationAnesthesiaType = "";
                        if ($(this).find('orddeptnm').text() == "01") {
                            _operationAnesthesiaType = "전신마취";
                        } else if ($(this).find('orddeptnm').text() == "02") {
                            _operationAnesthesiaType = "부위마취";
                        } else {
                            _operationAnesthesiaType = "국소마취";
                        }
                        ul.find("._operationAnesthesiaType").text(_operationAnesthesiaType); // 마취구분

                        var visitType = $(this).find('ordtype').text();

                        var patient = new Object();
                        var Birthday = $(this).find('rrgstno1').text();
                        patient.rrgstfullno = $(this).find('rrgstno1').text();
                        patient.AdmissionDate = $(this).find('orddd').text();
                        patient.Age = age;
                        patient.Sex = sex;
                        patient.zipnm = $(this).find('zipnm').text();
                        patient.hometel = $(this).find('hometel').text();
                        patient.mpphontel = $(this).find('mpphontel').text(); 
           //             alert("1 : " + $(this).find('perfdeptcd').text()+"\n2 : " + $(this).find('orddeptcd').text());
                        
                        patient.ClnDeptCode = $(this).find('perfdeptcd').text();
                        patient.ClnDeptName = $(this).find('perfdeptnm').text();
                        patient.ChargeId = $(this).find('atdoctid').text();
                        patient.anstmthdcd = $(this).find('anstmthdcd').text();
                        patient.ChargeName = $(this).find('atdoctnm').text(); // 주치의
                        patient.medispclnm = $(this).find('medispclnm').text(); 
                        // 2021-10-25
                        patient.medispclid = $(this).find('medispclid').text(); // 주치의
                        patient.DoctorId = $(this).find('orddrid').text();
                        patient.DoctorName = $(this).find('orddrnm').text(); // 진료의
                        patient.PatientCode = $(this).find('pid').text();
                        patient.PatientName = $(this).find('hngnm').text();
                        patient.Room = $(this).find('roomcd').text();
                        patient.VisitType = visitType;
                        patient.Ward = $(this).find('roomcd').text();
                        patient.Birthday = $(this).find('brthdd').text();
                        patient.Room = $(this).find('roomcd').text();
                        patient.Cretno = $(this).find('cretno').text();
                        Birthday = Birthday.substring(0, 8);
                        patient.fulrgstno = Birthday + "XXXXXX" //$(this).find('rrgstno1').text();
                        patient.diagnm = $(this).find('diagnm').text(); // 
                        patient.diagengnm = $(this).find('diagengnm').text(); // 진단명
                        patient.wardcd = $(this).find('wardcd').text(); // 

                        patient.deptengabbr = $(this).find('deptengabbr').text();
                        patient.opnm = $(this).find('opnm').text(); // 수술명
                        patient.OperationRoom = $(this).find('oproomnm').text(); // 수술명
                        patient.OperationDept = $(this).find('perfdeptnm').text(); // 수술명
                        patient.perfdrnm = $(this).find('perfdrnm').text(); // 집도의 명


                        // patient_ary.push(patient);

                        ul.attr("attr-data", JSON.stringify(patient));

                        // 해당 환자 클릭 이벤트
                        ul.on("click", function () {
                            $("#_operationList ul").removeClass("on");
                            $(this).addClass("on");

                            // 동명인여부확인 해야함
                            $("._patientDetailInfo").hide();
                            $(".patient_info").attr("attr-data", "");
                            $("._detail").text("");
                            // 연관 리스트 초기화
                            $("#_relationConsentList li").remove();
                            // 검색 동의서 리스트 초기화
                            $("#_consentList li").remove();
                            // 작성 동의서 리스트 초기화
                            $("#_consentALLList li").remove();

                            var isOnEmergencyMenu = $("#_emergency").hasClass("on");
                            var data = $(this).attr("attr-data");
                            // logAlert("해당 환자정보 검색 : " + data);
                            data = JSON.parse(data);
                            //alert(data.VisitType)
                            // alert(JSON.stringify(data.PatientCode));
                            // 동명인여부확인
                            isSameName("_operationList", data);
                            if (data.PatientCode != undefined && data.PatientCode != "") {
                                fnSearchPatientDetailInfo_submit(data);
                            } else {
                                alert("환자 상세 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다.")
                            }

                        });  
                        
                        
                        if(writeState =="" || writeState =="A"){  
                			if($(this).find('saveyn').text() == "Y"){ 
                    			ul.append('<div id="writeStateCircle"></div>'); 
                    		}
                			if($(this).find('saveyn').text() == "T"){ 
                    			ul.append('<div id="writeStateCircleTemp"></div>'); 
                    		}	 
                			
                            $("#_operationList").append(ul);
                    	}else if(writeState=="N"){
                    		if($(this).find('saveyn').text() == "N"){  
                                $("#_operationList").append(ul);
                    		}
                    	}else if(writeState=="T"){
                    		if($(this).find('saveyn').text() == "T"){ 
                    			ul.append('<div id="writeStateCircleTemp"></div>');
                                $("#_operationList").append(ul);
                    		}
                    	} 
                        
                    	else{
                    		if($(this).find('saveyn').text() == "Y"){ 
                    			ul.append('<div id="writeStateCircle"></div>');
                                $("#_operationList").append(ul);
                    		}
                    	} 
                    });
                } else {
                    makeNoDataHtml("_operationList");
                }
                if(nodataflag){
        	        makeNoDataHtml("_operationList");
                }
            	COMMON.plugin.loadingBar("hide", "");
                isNextRequest();
            },
            error: function (error) {
            	COMMON.plugin.loadingBar("hide", "");
                isRun_6 = false;
                alert("환자 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + JSON.stringify(error));
            }

        })
    }

}

// 환자정보 - 환자정보 조회
function fnSearchPatientDetailInfo_submit(patient) {
    //	alert(JSON.stringify(patient));
    // alert(JSON.stringify(patient));  
    // 환자 정보 초기화 
    $("._patientDetailInfo").hide();
    $(".patient_info").attr("attr-data", "");
    $("._detail").text("");
    // 연관 리스트 초기화
    $("#_relationConsentList").empty();
    // 검색 동의서 리스트 초기화
    $("#_consentList").empty();
    // 작성 동의서 리스트 초기화
    $("#_consentALLList").empty();

    var date_val = "";
    if ($("#_inPatient").hasClass("on")) {
        var clnDate = $("#_inPatientCalendar").val(); // 진료일
        clnDate = clnDate.replace(/-/gi, "")
        date_val = clnDate;
    }else {
        var searchType = $("#_findVisitType option:selected").val();
        if ($("#_find").hasClass("on") && searchType == "I") {
            var clnDate = $("#_inPatientCalendar").val(); // 진료일
            clnDate = clnDate.replace(/-/gi, "")
            date_val = clnDate;
        } else {
            date_val = patient.AdmissionDate;
        }
    }

    var isOnEmergencyMenu = $("#_emergency").hasClass("on");
    var isOnOperationyMenu = $("#_operation").hasClass("on");
    var isOnOutpatientMenu = $("#_outPatient").hasClass("on");
    var isOnLaboratoryMenu= $("#_laboratory").hasClass("on");
    
    // AdmissionDate / PatientCode / ClnDeptCode / VisitType  
    $("#_detailPatientCode").text(patient.PatientCode);
    $("#_detailPatientName").text(patient.PatientName);
    if (isOnOperationyMenu) {
        $("#_detailClnDeptCode").text(vauleNullCheck(patient.ClnDeptName, "-")); // patient.deptengabbr
    } else {
        $("#_detailClnDeptCode").text(vauleNullCheck(patient.ClnDeptName, patient.deptengabbr)); // patient.deptengabbr
    }
    // ========= 20210722 진단명 ================= 
	  var date = new Date();
	  var year = date.getFullYear();
	  var month = date.getMonth() + 1
	  var day = date.getDate();
	  if (month < 10)
	      month = "0" + month;
	  if (day < 10)
	      day = "0" + day;
	  var today = year + "" + month + "" + day;
	  
	  var ajax_data = "";
	  ajax_data = "submit_id=DRMRF00116&business_id=mr&instcd=204&ordtype="+patient.VisitType+"&hngnm=&pid=" + patient.PatientCode + "&orddd=" + patient.AdmissionDate + "&orddeptcd=-&orddrid=-&atdoctid=-&elbulbodstat=-&wardcd=-&patflagstat=-&opstatcd=-&srchflag=-&currentdd=" + today+"&cretno="+patient.Cretno;
	  $.ajax({
	      url: ajax_url,
	      data: ajax_data,
	      type: 'get',
	      dataType: 'xml',
	      async: false,
	      timeout: 40000,
	      success: function (result) { 
	          if ($(result).find('list').length > 0) {
	          	var idx = 0;
	              $(result).find('list').each(function () { 
	              	if(idx==1){
	              		return false;
	              	}  
	                  patient.diagnm = $(this).find('diagnm').text(); // 진단명  
	                  idx++; 
	              });
	          }   
	      },
	      error: function (error) {
	          alert("환자 상세 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + JSON.stringify(error));
	      }
	
	  })
  // =============================================
    
    
    $("#_detailDiagName").text(vauleNullCheck(patient.diagnm, "-")); // 진단명
    $("#_detailChargeName").text(vauleNullCheck(patient.ChargeName, "-")); // 담당
    $("#_detailClnDeptCode").text(vauleNullCheck(patient.ClnDeptName, "-"));
    var birthday = patient.Birthday; 
    birthday = birthday.substring(2, 4) + "년 " + birthday.substring(4, 6) + "월 " + birthday.substring(6, 8) + "일"
    $("#_detailDiagBirthday").text(vauleNullCheck(birthday, "-"));

    if (isOnEmergencyMenu) {
        $('#_detailRoomNo').text(patient.Ward);
        $("#_detailDoctorName").text(" - " + "  ,   "); // 주치
    } else if (isOnOutpatientMenu) {
        var clnDept = ($("#_outPatientClnDept option:selected").val() == "") ? "-" : $("#_outPatientClnDept option:selected").val(); // 진료과
        
        if(clnDept=="2370100000"){ 
            patient.diagnm = ''; // 패키지명
        }        
        $('#_detailRoomNo').text("-");
        $("#_detailDoctorName").text(vauleNullCheck(patient.DoctorName, " - ") + "  ,   "); // 주치 
    }else if(isOnLaboratoryMenu){
    	$('#_detailRoomNo').text(vauleNullCheck(patient.Ward, '-'));
        
    	$("#_detailDoctorName").text(vauleNullCheck(patient.medispclnm, " - ") + "  ,   "); // 주치 
    } else {
        $('#_detailRoomNo').text(vauleNullCheck(patient.Ward, '-'));
        $("#_detailDoctorName").text(vauleNullCheck(patient.DoctorName, " - ") + "  ,   "); // 주치 
    }

    // 내원구분에 따른 입원일 or 진료일 설정
    if (patient.VisitType == "O") {
        $("#_detailAdmissionDateTitle").text("진료일");
    	$("#_detailDoctorTitle").text("진료의"); 
        $("#_detailAdmissionDate").text(patient.AdmissionDate + " , "); // 진료일
    } else if (patient.VisitType == "P") {
        $("#_detailAdmissionDateTitle").text("수술일");
    	$("#_detailDoctorTitle").text("집도의"); 
        $("#_detailAdmissionDate").text(patient.AdmissionDate + " , "); // 진료일
    } else {
        $("#_detailAdmissionDateTitle").text("입원일");
    	$("#_detailDoctorTitle").text("주치의"); 
        // 입원일 경우 입원일에 병동,병실 추가
        var inpatientAdmissionDate = patient.AdmissionDate; // 입원일
        /*
         * if (patient.Ward != undefined && patient.Ward != null &&
         * patient.Ward != "") { rooomNo += " / " +
         * patient.Ward; if (patient.Room != undefined &&
         * patient.Room != null && patient.Room != "") { rooomNo += " / " +
         * patient.Room; } }
         */
        $("#_detailAdmissionDate").text(inpatientAdmissionDate + " , "); // 입원일
    }

    $("._patientDetailInfo").show();
    COMMON.LOCAL.eform.patient = patient; // 상세보기 서브밋결과
    
    //--------------------------------- 비급여 2차
    localStorage.setItem("patientInfo",JSON.stringify(patient));
    //----------------------------------------
    
    $(".patient_info").attr("attr-data", JSON.stringify(patient));
    $(".patient_info").off("click");
    $(".patient_info").on("click", function () {
        var info = $(this).attr("attr-data");
        logAlert(info);
    });


    // 연관 동의서 검색
    searchNowriteConsent();

    // 해당 동의서 검색
    var isEformConsentTab = $(".agree_tab h2").eq(0).hasClass("on");
    if (isEformConsentTab) {
        // $("input:radio[name=_consentSearchType]:input[value='D']").prop("checked",
        // true);
        // $("#_efromClnDept").prop("disabled", false);
        // if (!selectboxDefalutSelected("_efromClnDept",
        // getLocalStorage("userDeptCode"))) {
        // selectboxDefalutSelected("_efromClnDept",
        // detailInfo.ClnDeptCode)
        // }
        //$("#_contentSearch").trigger("click");
        fnSearchConsent();
    } else {
        $("#_contentAllSearch").trigger("click");
    }
};

// 검색 - 환자 조회
function findPatientSearch() {
    var searchType = $("#_findVisitType option:selected").val();
    var paramObject = {};
    // 공통 변수
    var args = {};
    var patient_ary = new Array();
    
    // 검색 구분에 따라서 입원이면 입원 조회 / 외래이면 외래 조회
    $("#_findList ul").remove();
    var writeState = $('#_findWriteState option:selected').val(); 

    var ul = $("#_findListTemplate ul").clone();
    var clnDate = $("#_findCalender").val(); // 검색일
    clnDate = clnDate.replace(/-/gi, "")
    var clnDept = ($("#_findDept option:selected").val() == "") ? "-" : $("#_findDept option:selected").val(); // 진료과
    var PatientName = encodeURI($("#_findPatientName").val()); // 환자이름
    var PatientCode = $("#_findPatientCode").val(); // 환자코드

    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() + 1
    var day = date.getDate();
    if (month < 10)
        month = "0" + month;
    if (day < 10)
        day = "0" + day;
    var today = year + "" + month + "" + day;
    var ajax_data = "";
    if (searchType == "I") {
        ajax_data = "submit_id=DRMRF00116&business_id=mr&instcd=204&ordtype=I&hngnm=" + PatientName + "&pid=" + PatientCode + "&orddd=" + clnDate + "&orddeptcd=" + clnDept + "&orddrid=-&atdoctid=-&elbulbodstat=-&wardcd=-&patflagstat=-&opstatcd=-&srchflag=-&currentdd=" + today;
    } else if (searchType == "O") {
        ajax_data = "submit_id=DRMRF00116&business_id=mr&instcd=204&ordtype=O&hngnm=" + PatientName + "&pid=" + PatientCode + "&orddd=" + clnDate + "&orddeptcd=" + clnDept + "&orddrid=-&atdoctid=-&elbulbodstat=-&wardcd=-&patflagstat=-&opstatcd=-&srchflag=-&currentdd=" + today;
    } else if (searchType == "E") {
        ajax_data = "submit_id=DRMRF00116&business_id=mr&instcd=204&ordtype=E&hngnm=" + PatientName + "&pid=" + PatientCode + "&orddd=" + clnDate + "&orddeptcd=" + clnDept + "&orddrid=-&atdoctid=-&elbulbodstat=-&wardcd=-&patflagstat=-&opstatcd=-&srchflag=-&currentdd=" + today;
    } else {
        ajax_data = "submit_id=DRMRF00116&business_id=mr&instcd=204&ordtype=P&hngnm=" + PatientName + "&pid=" + PatientCode + "&orddd=" + clnDate + "&orddeptcd=" + clnDept + "&orddrid=-&atdoctid=-&elbulbodstat=-&wardcd=-&patflagstat=-&opstatcd=-&srchflag=-&currentdd=" + today;
    }
    COMMON.plugin.wifiCheck(wifiCheckFn);

    var checkVal = localStorage.getItem("wifiCheckVal");
    if (checkVal < -90) {
        alert("WIFI 수신 감도가 좋지 않습니다. WIFI 통신이 잘 되는 곳에서 사용 바랍니다.");
    } else {
        $.ajax({
            url: ajax_url,
            data: ajax_data,
            type: 'get',
            dataType: 'xml',
            timeout: 40000,
            success: function (result) {
                //console.log(ajax_url + "?" + ajax_data);
                if ($(result).find('list').length > 0) {
                    $(result).find('list').each(function () {
                        var visitType = $(this).find('ordtype').text();
                        var patient = new Object();
                        var Birthday = $(this).find('rrgstno1').text();
                        patient.rrgstfullno = $(this).find('rrgstno1').text();
                        var sex = ($(this).find('sa').text().replace(/[^a-z]/gi, "") == "M") ? "남자" : "여자";
                        var age = $(this).find('sa').text().replace(/[^0-9]/g, "");
                        patient.AdmissionDate = $(this).find('orddd').text();
                        patient.sa = $(this).find('sa').text();
                        patient.Age = age;
                        patient.Sex = sex;
                        patient.zipnm = $(this).find('zipnm').text();
                        patient.hometel = $(this).find('hometel').text();
                        patient.mpphontel = $(this).find('mpphontel').text(); 
                        patient.ClnDeptCode = $(this).find('orddeptcd').text();
                        patient.ClnDeptName = $(this).find('orddeptnm').text();
                        patient.deptengabbr = $(this).find('deptengabbr').text();
                        patient.ChargeId = $(this).find('atdoctid').text();
                        patient.ChargeName = $(this).find('atdoctnm').text(); // 주치의
                        patient.medispclnm = $(this).find('medispclnm').text(); 
                        // 2021-10-25
                        patient.medispclid = $(this).find('medispclid').text(); // 주치의
                        patient.DoctorId = $(this).find('orddrid').text();
                        patient.DoctorName = $(this).find('orddrnm').text(); // 진료의
                        patient.PatientCode = $(this).find('pid').text();
                        patient.PatientName = $(this).find('hngnm').text();
                        patient.erorddeptnm = $(this).find('erorddeptnm').text()
                        patient.Room = $(this).find('roomcd').text();
                        patient.diagnm = $(this).find('diagnm').text(); // 진단명
                        patient.diagengnm = $(this).find('diagengnm').text(); // 진단명
                        patient.wardcd = $(this).find('wardcd').text(); // 진단명
 
                        patient.deptengabbr = $(this).find('deptengabbr').text();
                        patient.VisitType = visitType;
                        patient.Ward = $(this).find('roomcd').text();
                        patient.Birthday = $(this).find('brthdd').text(); 
                        patient.Room = $(this).find('roomcd').text();
                        patient.perfdeptnm = $(this).find('perfdeptnm').text();
                        patient.Cretno = $(this).find('cretno').text();
                        Birthday = Birthday.substring(0, 8);
                        patient.fulrgstno = Birthday + "XXXXXX" //$(this).find('rrgstno1').text();
                        
                        // 2022-02-03
                        patient.etcsignflagvn = $(this).find('etcsignflagvn').text();
                        patient.etcsignflagmn = $(this).find('etcsignflagmn').text();
                        patient.etcsignflagvy = $(this).find('etcsignflagvy').text();
                        patient.etcsignflagmy = $(this).find('etcsignflagmy').text();
                        patient.saveyn = $(this).find('saveyn').text();
                        
                        
                        
                        patient_ary.push(patient);
                        
                        
                        
                        $("#_findList").append(ul);
                        

                    });
                } else {
                    makeNoDataHtml("_findList");
                }
                isNextRequest();
                findPatientSearchSuccessHandler(patient_ary);
            },
            error: function (error) {
                alert("환자 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + JSON.stringify(error));
            }

        })
    }
};

// 검색 - 환자조회 결과
function findPatientSearchSuccessHandler(resData) {
    $("#_findList ul").remove();
    var nodataflag = false;
    var patients = resData;
    var writeState = $('#_findWriteState option:selected').val();
    // 2022-02-03
    var verbalMultiState = $('#_findVerbalMulti option:selected').val();
    
    if ($.isEmptyObject(patients)) {
        makeNoDataHtml("_findList");
    } else {
        for (var i = 0; i < patients.length; i++) {
            var patient = patients[i];
            var ul = $("#_findListTemplate ul").clone();
            ul.find("._findPatientCode").text(patient.PatientCode); // 환자 등록번호 
            //ul.find("._findAge").text(ageFloor(patient.Age) + " / " + ((patient.Sex == "M") ? "남" : "여")); // 환자
            ul.find("._findAge").text(patient.sa); // 환자
            ul.find("._findSex").text((patient.Sex == "M") ? "남" : "여"); // 환자
            //			patient.erorddeptnm						// 성별
            ul.find("._findPatientName").text(patient.PatientName); // 환자 성명
            if (patient.ChargeName != "" && patient.ChargeName != undefined) {
                ul.find("._findChargeName").text(patient.ChargeName); // 환자
                // 담당의사명
            } else {
                ul.find("._findChargeName").text(patient.DoctorName); // 환자
                // 담당의사명
            }

            ul.find("._findBirthday").text(patient.Birthday);

            var searchType = $("#_findVisitType option:selected").val();

            if (searchType == "I") { // 입원일 경우
                $("._find .list_header .hospital ._sort").text("입원일자");
                $("._find .list_header .etc ._sort").text("재원과");
                ul.find("._findClinicalDate").text(patient.AdmissionDate); // 구분
                ul.find("._findClnDeptName").text(patient.ClnDeptName);
            } else if (searchType == "P") { // 수술일 경우
                $("._find .list_header .hospital ._sort").text("수술일자");
                $("._find .list_header .etc ._sort").text("수술과");
                ul.find("._findClinicalDate").text(patient.AdmissionDate); // 구분
                ul.find("._findClnDeptName").text(patient.perfdeptnm);
            } else if (searchType == "E") { // 응급일 경우
                $("._find .list_header .hospital ._sort").text("진료일자");
                $("._find .list_header .etc ._sort").text("진료과");
                ul.find("._findClinicalDate").text(patient.AdmissionDate); // 진료일자
                ul.find("._findClnDeptName").text(vauleNullCheck(patient.erorddeptnm, "-")); // 진료과
            } else { // 외래일 경우
                $("._find .list_header .hospital ._sort").text("진료일자");
                $("._find .list_header .etc ._sort").text("진료과");
                ul.find("._findClinicalDate").text(patient.AdmissionDate); // 진료일자
                ul.find("._findClnDeptName").text(vauleNullCheck(patient.deptengabbr, "-")); // 진료과
            }

            if (patient.VisitType == "") {
                patient.VisitType = $("#_findVisitType option:selected").val();
            }

            ul.attr("attr-data", JSON.stringify(patient));
            // 해당 환자 클릭 이벤트
            ul.on("click", function () {
                $("#_findList ul").removeClass("on");
                $(this).addClass("on");
                var data = $(this).attr("attr-data");
                logAlert("해당 환자정보 검색 : " + data);
                data = JSON.parse(data);
                isSameName("_findList", data);

                if (data.PatientCode != undefined && data.PatientCode != "") {

                    isRuns = false;
                    fnSearchPatientDetailInfo_submit(data);

                }
            });


            // 2022-02-03 내용추가 및 수정
            if(writeState =="" || writeState =="A"){  
    			if(patient.saveyn == "Y"){ 
        			ul.append('<div id="writeStateCircle"></div>'); 
        		}
    			if(patient.saveyn == "T"){ 
        			ul.append('<div id="writeStateCircleTemp"></div>'); 
        		} 
	          	if(patient.etcsignflagvn == "Y" || patient.etcsignflagmn == "Y"){  
	          		ul.append('<div id="writeStateCircleVM"></div>');
	        	}
	          	if(patient.etcsignflagvy == "Y" || patient.etcsignflagmy == "Y"){  
	        		ul.append('<div id="writeStateCircleEndVM"></div>');
	        	}	          	

    			if(verbalMultiState == "VM"){ 
    	          	if(patient.etcsignflagvn == "Y" || patient.etcsignflagmn == "Y"){  
    	                $("#_findList").append(ul);
    	        	}
    	        }else if(verbalMultiState == "VN"){
    	           	if(patient.etcsignflagvn == "Y"){  
    	                $("#_findList").append(ul);
    	        	}                        	
    	        }else if(verbalMultiState == "MN"){
    	           	if(patient.etcsignflagmn == "Y"){  
    	                $("#_findList").append(ul);
    	           	}                        	                        	
    	        }else if(verbalMultiState == "VY"){
    	           	if(patient.etcsignflagvy == "Y"){  
    	                $("#_findList").append(ul);
    	        	}    
    	        }else if(verbalMultiState == "MY"){ 
    	           	if(patient.etcsignflagmy== "Y"){  
    	                $("#_findList").append(ul);
    	           	}                        	                               	
    	        }else{
                    $("#_findList").append(ul);
    	        }
        	}else if(writeState=="N"){
        		if(patient.saveyn == "N"){  
                    //$("#_findList").append(ul);
        			if(verbalMultiState == "VM"){ 
        	          	if(patient.etcsignflagvn == "Y" || patient.etcsignflagmn == "Y"){  
        	          		ul.append('<div id="writeStateCircleVM"></div>');
        	                $("#_findList").append(ul);
        	        	}
        	        }else if(verbalMultiState == "VN"){
        	           	if(patient.etcsignflagvn == "Y"){  
        	        		ul.append('<div id="writeStateCircleVM"></div>');
        	                $("#_findList").append(ul);
        	        	}                        	
        	        }else if(verbalMultiState == "MN"){
        	           	if(patient.etcsignflagmn == "Y"){  
        	        		ul.append('<div id="writeStateCircleVM"></div>');
        	                $("#_findList").append(ul);
        	           	}                        	                        	
        	        }else if(verbalMultiState == "VY"){
        	           	if(patient.etcsignflagvy == "Y"){  
        	        		ul.append('<div id="#writeStateCircleEndVM"></div>');
        	                $("#_findList").append(ul);
        	        	}    
        	        }else if(verbalMultiState == "MY"){ 
        	           	if(patient.etcsignflagmy== "Y"){  
        	        		ul.append('<div id="#writeStateCircleEndVM"></div>');
        	                $("#_findList").append(ul);
        	           	}                        	                               	
        	        }else{
                        $("#_findList").append(ul);
        	        }
        		}
        	}else if(writeState=="T"){
        		if(patient.saveyn == "T"){ 
        			ul.append('<div id="writeStateCircleTemp"></div>');
                    //$("#_findList").append(ul);
        		}
	          	if(patient.etcsignflagvn == "Y" || patient.etcsignflagmn == "Y"){  
	          		ul.append('<div id="writeStateCircleVM"></div>');
	        	}
	          	if(patient.etcsignflagvy == "Y" || patient.etcsignflagmy == "Y"){  
	        		ul.append('<div id="writeStateCircleEndVM"></div>');
	        	}	          	

    			if(verbalMultiState == "VM"){ 
    	          	if(patient.etcsignflagvn == "Y" || patient.etcsignflagmn == "Y"){  
    	                $("#_findList").append(ul);
    	        	}
    	        }else if(verbalMultiState == "VN"){
    	           	if(patient.etcsignflagvn == "Y"){  
    	                $("#_findList").append(ul);
    	        	}                        	
    	        }else if(verbalMultiState == "MN"){
    	           	if(patient.etcsignflagmn == "Y"){  
    	                $("#_findList").append(ul);
    	           	}                        	                        	
    	        }else if(verbalMultiState == "VY"){
    	           	if(patient.etcsignflagvy == "Y"){  
    	                $("#_findList").append(ul);
    	        	}    
    	        }else if(verbalMultiState == "MY"){ 
    	           	if(patient.etcsignflagmy== "Y"){  
    	                $("#_findList").append(ul);
    	           	}                        	                               	
    	        }else{
                    $("#_findList").append(ul);
    	        }
    			
        	}else{
        		if(patient.saveyn == "Y"){ 
        			ul.append('<div id="writeStateCircle"></div>');
    	          	if(patient.etcsignflagvn == "Y" || patient.etcsignflagmn == "Y"){  
    	          		ul.append('<div id="writeStateCircleVM"></div>');
    	        	}
    	          	if(patient.etcsignflagvy == "Y" || patient.etcsignflagmy == "Y"){  
    	        		ul.append('<div id="writeStateCircleEndVM"></div>');
    	        	}	          	

        			if(verbalMultiState == "VM"){ 
        	          	if(patient.etcsignflagvn == "Y" || patient.etcsignflagmn == "Y"){  
        	                $("#_findList").append(ul);
        	        	}
        	        }else if(verbalMultiState == "VN"){
        	           	if(patient.etcsignflagvn == "Y"){  
        	                $("#_findList").append(ul);
        	        	}                        	
        	        }else if(verbalMultiState == "MN"){
        	           	if(patient.etcsignflagmn == "Y"){  
        	                $("#_findList").append(ul);
        	           	}                        	                        	
        	        }else if(verbalMultiState == "VY"){
        	           	if(patient.etcsignflagvy == "Y"){  
        	                $("#_findList").append(ul);
        	        	}    
        	        }else if(verbalMultiState == "MY"){ 
        	           	if(patient.etcsignflagmy== "Y"){  
        	                $("#_findList").append(ul);
        	           	}                        	                               	
        	        }else{
                        $("#_findList").append(ul);
        	        }
        			
        		}
        	}  
            
          
        }
       
        if(nodataflag){
	        makeNoDataHtml("_findList"); 
        	
        }
    }
};

// 환자정보 - 연관 동의서 검색
function searchNowriteConsent() {
    var detail = JSON.parse($(".patient_info").attr("attr-data")); 
    var i = 0;

    COMMON.plugin.wifiCheck(wifiCheckFn);

    var checkVal = localStorage.getItem("wifiCheckVal");
    if (checkVal < -90) {
        alert("WIFI 수신 감도가 좋지 않습니다. WIFI 통신이 잘 되는 곳에서 사용 바랍니다.");
    } else {
        $.ajax({
            url: ajax_url,   
            data: "submit_id=DRMRF00121&business_id=mr&instcd=204&pid=" + detail.PatientCode + "&orddd=" + detail.AdmissionDate + "&cretno=" + detail.Cretno + "&ordtype=" + detail.VisitType, 
            type: 'get',
            dataType: 'xml',
            timeout: 40000,
            success: function (result) {

                $("#_relationConsentList li").remove();
                if ($(result).find('item').length > 0) {
                    var i = 0;
                    $(result).find('item').each(function () {
                        $("#_relationConsentList").removeClass("accordion");
                        $("#_relationConsentList").removeClass("no_data");

                        // var consent = consents[i];
                        var li = $("#_relationConsentListTemplate li").clone();
                        i = i + 1;
                        // 체크박스 추가
                        var checkBoxHtml = "";
                        checkBoxHtml += '<span style="margin-left: 5px;">' + (i) + '.</span>';
                        checkBoxHtml += '<span class="form_box form_box_checkbox" style="margin-left: 5px;">';
                        checkBoxHtml += '	<input type="checkbox" id="CHECK_R_' + i + '" class="__relationConsentListListCheckbox" /><label for="CHECK_R_' + i + '">선택</label>';
                        checkBoxHtml += '</span>';

                        li.prepend(checkBoxHtml);


                        var consent = new Object();
                        

                        
                        
                        consent.seqno = $(this).find('seqno').text();
                        consent.FormFromDt = $(this).find('formfromdt').text();
                        consent.FormCd = $(this).find('formcd').text();
                        consent.FormName = $(this).find('formnm').text();
                        consent.FormExtnNm = $(this).find('formextnnm').text();
                        consent.FormPrntNm = $(this).find('formprntnm').text();
                        consent.FormRid = $(this).find('formrid').text();
                        consent.FormGuid = $(this).find('formguid').text()
                        consent.FormId = $(this).find('formguid').text();
                        consent.FormVersion = $(this).find('formversion').text();
                        consent.cosignYn = $(this).find('cosign_yn').text();
                        consent.FormDepthngNm = $(this).find('formdepthngnm').text();
                        consent.eco = $(this).find('eco').text();
                        consent.nurscert_yn = $(this).find('nurscert_yn').text();
                        consent.fvrt_yn = $(this).find('fvrt_yn').text();
                        consent.prcpdd = $(this).find('prcpdd').text();
                        consent.prcpno = $(this).find('prcpno').text();
                        consent.prcpcd = $(this).find('prcpcd').text();
                        consent.prcpnm = $(this).find('prcpnm').text();
                        
                        
                        
                        consent.prcphistno = $(this).find('prcphistno').text();
                        consent.consent_certneedcnt = $(this).find('consent_certneedcnt').text();
                        consent.lifelong_kind = $(this).find('lifelong_kind').text();
 
                        consent.patientCode = detail.PatientCode;
                        consent.pid = $(this).find('pid').text();
                        consent.orddd = $(this).find('orddd').text();
                        consent.cretno = $(this).find('cretno').text();
                        consent.ordtype = $(this).find('ordtype').text();
                        consent.opdr_yn = $(this).find('opdr_yn').text();
                        consent.tmp01 = $(this).find('tmp01').text();
                        consent.tmp02 = $(this).find('tmp02').text(); 
                        consent.tmp03 = $(this).find('tmp03').text();
                        consent.tmp04 = $(this).find('tmp04').text(); 
                        consent.tmp05 = $(this).find('tmp05').text();
                        consent.tmp06 = $(this).find('tmp06').text(); 
                        consent.tmp07 = $(this).find('tmp07').text();
                        consent.tmp08 = $(this).find('tmp08').text(); 
                        consent.tmp09 = $(this).find('tmp09').text();
                        consent.tmp10 = $(this).find('tmp10').text();


                        //----------------------------------                        
                        //처방 동의서 수정 - 비급여 
                        
                        if($(this).find('tmp01').text() == "NONPAY"){ 
                            var patientInfo = JSON.parse($(".patient_info").attr("attr-data")); 
                            //alert($(result).find('nonpayitem').length);
                            var idx = 0;
                        	 $(result).find('nonpayitem').each(function () { 
                        		 if(idx == 0){
                             		consent.seqno =  "'"+$(this).find('seqno').text()+"'"; 
                        		 }else{
                              		consent.seqno =  consent.seqno + "," + "'"+$(this).find('seqno').text()+"'";                         			 
                        		 }
                        		
                        		patientInfo['nonBene'+idx] = $(this).find('prcpnm').text();
                        		var scoreamt = $(this).find('scoreamt').text();
                        		scoreamt = scoreamt.replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ",");
                        		patientInfo['estiCost'+idx] = scoreamt;
                        		idx = idx+1; 
                        	 });
                        	  
                     		$(".patient_info").attr("attr-data", "");
                    	    $(".patient_info").attr("attr-data", JSON.stringify(patientInfo));
                        	 COMMON.LOCAL.eform.patient = patientInfo; 
                        }  
                        
                        consent.cosignFlag = "0";
                        if ($(this).find('cosign_yn').text() == "N") {
                            consent.cosignDeptCode = "";
                        }
                      //  consent.ocrTag = CreateOcrTag();
                        consent.nowrite = "true";
                        consent.OrderYn = '';
                        
                        
                        //----------------------------------
                        // 20210603 일반 동의서 비급여 항목 추가 
                        if(consent.tmp04 == "ADDFORM"){  
                            var patientInfo = JSON.parse($(".patient_info").attr("attr-data")); 
                        	var prcpcd =  consent.prcpcd;
                        	var prcpno =  consent.prcpno;
                        	var formcd =  consent.FormCd;
                        	var pid = consent.pid;
                        	var cretno = consent.cretno;
                        	var orddd = consent.orddd;
                        	var ordtype = consent.ordtype;
                            var nonseqno = consent.seqno ;
                        	 $.ajax({
                        		  url: "http://emr.yjh.com/cmcnu/webapps/mr/mr/formmngtweb/.live",   
                                  data: "submit_id=DRMRF00128&business_id=mr&instcd=204&prcpcd=" + prcpcd + "&prcpno=" + prcpno + "&formcd=" + formcd + "&pid="+pid+"&cretno="+cretno+"&orddd="+orddd+"&ordtype="+ordtype , 
                                  type: 'get',
                                  async : false,
                                  dataType: 'xml',
                                  timeout: 40000,
                                  success: function (result) {
                                	  //console.log( "뿌쑝 : http://emr.yjh.com/cmcnu/webapps/mr/mr/formmngtweb/.live"+"submit_id=DRMRF00128&business_id=mr&instcd=204&prcpcd=" + prcpcd + "&prcpno=" + prcpno + "&formcd=" + formcd + "&pid="+pid+"&cretno="+cretno+"&orddd="+orddd+"&ordtype="+ordtype);
                                      if ($(result).find('prcpinfo').length > 0) { 
                                          var idx = 0;
                                          $(result).find('prcpinfo').each(function () {  
                                        	 consent['nonseqno'] = nonseqno;  
                                        	 consent['addform'] = 'Y';  
                                     		 if(idx == 0){
                                     			consent.seqno =  "'"+$(this).find('seqno').text()+"'";  
                                     		 }else{
                                     			consent.seqno =  consent.seqno + "," + "'"+$(this).find('seqno').text()+"'"; 
                                     		 }                                     		 

                                     		 consent['addnonBene'+idx] = $(this).find('prcpnm').text();
                                     		 var scoreamt = $(this).find('scoreamt').text();
                                     		 scoreamt = scoreamt.replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ",");
                                     		 consent['addestiCost'+idx] = scoreamt;
                                     		 idx = idx+1;  
                                          }) 
                                          consent['nonIndex'] = idx;
                                    	// $(".patient_info").attr("attr-data", "");
                                       	// $(".patient_info").attr("attr-data", JSON.stringify(patientInfo));
                                         //COMMON.LOCAL.eform.patient = patientInfo;   
                                      } 
                                  },
                                  error: function (error) {
                                      alert("일반 동의서 비급여 항목 조회 중 오류가 발생하였습니다.\n관리자에게 문의 바랍니다.");
                                  }
                        	 })
                        }      
                        console.log("0526after : "+JSON.stringify(consent));
                        //----------------------------------

                        

                        if (consent.FormExtnNm == "-") {
                            li.find("._formName").text(consent.FormName);
                        } else {
                            li.find("._formName").text(consent.FormName + "[" + consent.FormExtnNm + "]");
                        }
                        li.attr("attr-data", JSON.stringify(consent));

                        // ----------------------------------------------------------
                        // 해당 동의서 클릭 이벤트
                        li.find(".list_txt").on("click", function () {
                            $("#_relationConsentList li").removeClass("on");
                            $(this).parent().parent().addClass("on");
                            var data = $(this).parent().attr("attr-data");
                            cosign_Flag = "0";

                            // 체크된 모든 항목들 가져오기
                            var lis = $("#_relationConsentList .__relationConsentListListCheckbox:checked");
                            var consents = [];
                            var consentNames = ""; 

                            // 여기
                            var consentInfo = JSON.parse($(this).parent().attr("attr-data")); 
                            //var ocrAdd = JSON.parse(consentInfo);
                            
                            
                            
                            
                             
                            consentInfo = JSON.stringify(consentInfo);  
                            $(this).parent().attr("attr-data",consentInfo);
                            
                            
                            lis.each(function () {
                                var consent = $(this).parent().parent().attr("attr-data");
                                var ocrAdd = JSON.parse(consent);
                                var ocrtagValue =  CreateOcrTag(); 
                                

                                ocrAdd.ocrTag = ocrtagValue;  
                                consent = JSON.stringify(ocrAdd); 
                                
                                consents[consents.length] = consent;
                                var data = JSON.parse(consent);
                                consentNames += (consentNames == "") ? data.FormName : "\n," + data.FormName;
                            });
                            COMMON.LOCAL.eform.consent = consents;
                            if (consents.length < 1) {
                                var consent = $(this).parent().attr("attr-data");
                                var ocrAdd = JSON.parse(consent);
                                var ocrtagValue =  CreateOcrTag(); 
                                if(ocrtagValue =="error"){
                                	errorCheck = "Y";
                                } 
                                ocrAdd.ocrTag = ocrtagValue;                  
                                consent = JSON.stringify(ocrAdd);
                 
                                
                                
                                consents[consents.length] = consent;
                                
                                var data = JSON.parse(consent);
                                consentNames += (consentNames == "") ? data.FormName : "\n," + data.FormName;
                                COMMON.LOCAL.eform.consent = consents;
                            }
                            

                            var openCheck = JSON.parse($(this).parent().attr("attr-data"));
                            var patiCode = $("#_detailPatientCode").text();

                            var valueAry = new Array();
                             
                            if(lis.length==0){
    	                        for (var i = 0; i < consents.length; i++) {
    	                        	var checkConsent = JSON.parse($(this).parent().attr("attr-data")); 
    	                            var valueObj = new Object();
    	                        	valueObj.patientCode = patiCode;
    	                        	valueObj.formId = checkConsent.FormId;
    	                        	valueObj.formVersion = checkConsent.FormVersion;                 	
    	                        	
    	                        	valueAry.push(valueObj);							
    							} 
                            }else{
                                lis.each(function (index){
                                	var checkConsent = JSON.parse($(this).parent().parent().attr("attr-data"));
                                    var valueObj = new Object();
                                	valueObj.patientCode = patiCode;
                                	valueObj.formId = checkConsent.FormId;
                                	valueObj.formVersion = checkConsent.FormVersion;                 	
                                	
                                	valueAry.push(valueObj);
                                })  
                            } 

                            var openCheckVal = "";
                            var openCheckErrCd = "";
                            var openCheckErrMsg = "";
                            var openCheckErrMsg2 = "";
                            $.ajax({
                                url: ajax_nu_url + '/biz/nu/member/viewer/eForm25/consent/isOpen',
                                type: 'post',
                                timeout: 40000,
                                async : false,
                                data: {
                                    parameter: JSON.stringify(valueAry)
                                }
                            }).done(function (data) {
                                $.each(data, function (index, item) {   
                                	//alert(JSON.stringify(data[index]))
                                	openCheckVal = data[index].result;
                                	openCheckErrCd = data[index].errorCode;
                                	openCheckErrMsg = data[index].errorMsg;
                                	openCheckErrMsg2 = data[index].errorMsg2;    
                                });
                            }).fail(function (xhr, status, errorThrown) { 
                                alert("서식작성여부 조회 중 오류가 발생했습니다. 관리자에게 문의바랍니다. " + errorThrown)
                            });    
                            
                        if(openCheckVal){ 
                                if (getLocalStorage("signPwd") == "" || getLocalStorage("signPwd") == null || getLocalStorage("signPwd") == undefined) {
                                    $('#sign_popup').css('display', 'block');
                                    $('#_certpassword').focus();
                                    check_consent = $(this);
                                } else {
                                    searchAppVersion();
                                }
                        }else{
                        	alert("ErrorCode : " + openCheckErrCd+ "\nErrorMsg : "+openCheckErrMsg);
                            COMMON.plugin.loadingBar("hide", "뷰어를 실행하는 중입니다.");
                        }  
                        });
                        $("#_relationConsentList").append(li);

                    });
                } else {
                    $("#_relationConsentList").addClass("no_data");
                    var noDataHtml = "	<li><span>조회된 결과가 없습니다.</span></li>";
                    $("#_relationConsentList").append(noDataHtml);
                }
            },
            error: function (error) {
                alert("환자 정보 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + JSON.stringify(error));
            }
        })


    }

}; 
 

// 전자 동의서 검색
function fnSearchConsent() {
    $("#_consentList li").remove();
    // -- 비급여 2차수정
    var detail = JSON.parse($(".patient_info").attr("attr-data")); 
    
    var searchType = $(':radio[name="_consentSearchType"]:checked').val();
    var SearchResultAry = new Array();
    // SET 동의서 검색 여부
    if (searchType == "S") {
        var deptCd = getLocalStorage("userDeptCode"); // 로그인사용자 과 

        COMMON.plugin.wifiCheck(wifiCheckFn);
        var checkVal = localStorage.getItem("wifiCheckVal");
        if (checkVal < -90) {
            alert("WIFI 수신 감도가 좋지 않습니다. WIFI 통신이 잘 되는 곳에서 사용 바랍니다.");
        } else {
            COMMON.plugin.loadingBar("show", "과SET 동의서 검색 중입니다.");
            //loadingbar_display();
            $.ajax({
                url: ajax_url, // ajax_url,

                // -- 비급여 2차수정
                data: "submit_id=DRMRF00117&business_id=mr&instcd=204&deptcd=" + deptCd + "&userid=" + getLocalStorage("userId") + "&ioflag="+detail.VisitType, 
                type: 'get',
                dataType: 'xml',
                success: function (result) {
                    var SearchResult = new Object();
                    if ($(result).find('item').length > 0) {
                        $(result).find('item').each(function () {
                            var SearchResult = new Object();
                            SearchResult.depth = $(this).find('depth').text();
                            if ($(this).find('depth').text() == "2") {
                                SearchResult.path = $(this).find('path').text(); 
                                SearchResult.itemindxseq = $(this).find('itemindxseq').text();
                            }
                            SearchResult.supitemindxseq = $(this).find('supitemindxseq').text();
                            SearchResult.FormCd = $(this).find('formcd').text();
                            SearchResult.FormName = $(this).find('formnm').text();
                            SearchResult.FormExtnNm = $(this).find('formextnnm').text();
                            SearchResult.FormPrntNm = $(this).find('formprntnm').text();
                            SearchResult.FormId = $(this).find('formguid').text();
                            SearchResult.FormRid = $(this).find('formrid').text();
                            SearchResult.FormGuid = $(this).find('formguid').text();

                            SearchResult.FormFromDt = $(this).find('formfromdt').text();
                            SearchResult.FormVersion = $(this).find('formversion').text();
                            SearchResult.cosignYn = $(this).find('cosign_yn').text();
                            SearchResult.FormDepthngNm = $(this).find('formdepthngnm').text();
                            SearchResult.nurscert_yn = $(this).find('nurscert_yn').text();
                            SearchResult.fvrt_yn = $(this).find('fvrt_yn').text();
                            SearchResult.consent_certneedcnt = $(this).find('cert_need_cnt').text();
                            SearchResult.lifelong_kind = $(this).find('lifelong_kind').text();
                            
                            
                            // -- 비급여 2차수정
                            SearchResult.path = $(this).find('path').text();
                            SearchResult.indxnm = $(this).find('indxnm').text();
                            SearchResult.itemindxseq = $(this).find('itemindxseq').text();

                            
                            SearchResult.eco = $(this).find('eco').text();
                            if ($(this).find('cosign_yn').text() == "N") {
                                SearchResult.cosignDeptCode = "";
                            }
                            SearchResult.cosignFlag = "0";
                            SearchResultAry.push(SearchResult);
                        });
                    }
                	COMMON.plugin.loadingBar("hide", "");
                    consentSearchSuccessHandler(SearchResultAry, "S");

                },
                error: function (error) {
                	COMMON.plugin.loadingBar("hide", "");
                    alert("SET 동의서 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + JSON.stringify(error));
                }
            })
        }

    } else if (searchType == "B") { // 즐겨찾기조회
        var userId = getLocalStorage("userId"); // 로그인사용자 과

        COMMON.plugin.wifiCheck(wifiCheckFn);

        var checkVal = localStorage.getItem("wifiCheckVal");
        if (checkVal < -90) {
            alert("WIFI 수신 감도가 좋지 않습니다. WIFI 통신이 잘 되는 곳에서 사용 바랍니다.");
        } else {
            COMMON.plugin.loadingBar("show", "즐겨찾기 동의서 검색 중입니다.");
            $.ajax({
                url: ajax_url,
                // -- 비급여 2차수정
                data: "submit_id=DRMRF00118&business_id=mr&instcd=204&userid=" + userId + "&ioflag="+detail.VisitType,
                type: 'get',
                dataType: 'xml',
                success: function (result) {
                    var SearchResult = new Object();
                    if ($(result).find('item').length > 0) {
                        $(result).find('item').each(function () {

                            var SearchResult = new Object();
                            // depth 2 폴더명
                            // depth 3 하위
                            // depth 2 itemindxseq == depth 3 supitemindxseq 동일폴더
                            SearchResult.depth = $(this).find('depth').text();
                            if ($(this).find('depth').text() == "2") {
                                SearchResult.path = $(this).find('path').text(); 
                                SearchResult.itemindxseq = $(this).find('itemindxseq').text();
                            }
                            SearchResult.FormFromDt = $(this).find('formfromdt').text();
                            SearchResult.supitemindxseq = $(this).find('supitemindxseq').text();
                            SearchResult.FormCd = $(this).find('formcd').text();
                            SearchResult.FormName = $(this).find('formnm').text();
                            SearchResult.FormExtnNm = $(this).find('formextnnm').text();
                            SearchResult.FormPrntNm = $(this).find('formprntnm').text();
                            SearchResult.FormRid = $(this).find('formrid').text();
                            SearchResult.FormId = $(this).find('formguid').text();
                            SearchResult.FormGuid = $(this).find('formguid').text();
                            SearchResult.FormVersion = $(this).find('formversion').text();
                            SearchResult.cosignYn = $(this).find('cosign_yn').text();
                            SearchResult.FormDepthngNm = $(this).find('formdepthngnm').text();
                            SearchResult.nurscert_yn = $(this).find('nurscert_yn').text();
                            SearchResult.fvrt_yn = $(this).find('fvrt_yn').text();
                            SearchResult.consent_certneedcnt = $(this).find('cert_need_cnt').text();
                            SearchResult.lifelong_kind = $(this).find('lifelong_kind').text();
                            
                            // -- 비급여 2차수정
                            SearchResult.path = $(this).find('path').text();
                            SearchResult.indxnm = $(this).find('indxnm').text();
                            SearchResult.itemindxseq = $(this).find('itemindxseq').text();

                            SearchResult.eco = $(this).find('eco').text();
                            SearchResult.cosignFlag = "0";
                            if ($(this).find('cosign_yn').text() == "N") {
                                SearchResult.cosignDeptCode = "";
                            }
                            SearchResultAry.push(SearchResult);
                        });
                    }
                	COMMON.plugin.loadingBar("hide", "");
                    consentSearchSuccessHandler(SearchResultAry, "B");
                },
                error: function (error) {
                	COMMON.plugin.loadingBar("hide", "");
                    alert("즐겨찾기 동의서 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + JSON.stringify(error));
                }
            })
        }

    } else if (searchType == "C") {
        var keyword = "";
        var SearchResultAry = new Array();

        COMMON.plugin.wifiCheck(wifiCheckFn);
        var checkVal = localStorage.getItem("wifiCheckVal");
        if (checkVal < -90) {
            alert("WIFI 수신 감도가 좋지 않습니다. WIFI 통신이 잘 되는 곳에서 사용 바랍니다.");
        } else {
            COMMON.plugin.loadingBar("show", "Co-sign 동의서 검색 중입니다.");
            $.ajax({
                url: ajax_url, // ajax_url,
                data: "submit_id=DRMRF00120&business_id=mr&instcd=204&formnm=" + keyword + "&userid=" + getLocalStorage("userId"), // "submit_id=DRMRF00120&ep_interface=emr&business_id=mr&instcd=204&formnm="+keyword,
                type: 'get',
                dataType: 'xml',
                success: function (result) {
                    //console.log(ajax_url + "?" + "submit_id=DRMRF00120&business_id=mr&instcd=204&formnm=" + keyword) // "submit_id=DRMRF00120&ep_interface=emr&business_id=mr&instcd=204&formnm="+keyword)
                    if ($(result).find('item').length > 0) {
                        $(result).find('item').each(function () {
                            if ($(this).find('cosign_yn').text() == "Y") {
                                var SearchResult = new Object();
                                SearchResult.FormFromDt = $(this).find('formfromdt').text();
                                SearchResult.FormCd = $(this).find('formcd').text();
                                SearchResult.FormName = $(this).find('formnm').text();
                                SearchResult.FormExtnNm = $(this).find('formextnnm').text();
                                SearchResult.FormPrntNm = $(this).find('formprntnm').text();
                                SearchResult.FormRid = $(this).find('formrid').text();
                                SearchResult.FormGuid = $(this).find('formguid').text()
                                SearchResult.FormId = $(this).find('formguid').text();
                                SearchResult.FormVersion = $(this).find('formversion').text();
                                SearchResult.cosignYn = $(this).find('cosign_yn').text();
                                SearchResult.FormDepthngNm = $(this).find('formdepthngnm').text();
                                SearchResult.nurscert_yn = $(this).find('nurscert_yn').text();
                                SearchResult.fvrt_yn = $(this).find('fvrt_yn').text();
                                SearchResult.consent_certneedcnt = $(this).find('cert_need_cnt').text();
                                SearchResult.lifelong_kind = $(this).find('lifelong_kind').text();

                                SearchResult.eco = $(this).find('eco').text();
                                SearchResult.cosignFlag = "0";
                                if ($(this).find('cosign_yn').text() == "N") {
                                    SearchResult.cosignDeptCode = "";
                                }
                                SearchResultAry.push(SearchResult);
                            }
                        });
                    }
                	COMMON.plugin.loadingBar("hide", "");
                    consentSearchSuccessHandler(SearchResultAry, "C");
                },
                error: function (error) {
                	COMMON.plugin.loadingBar("hide", "");
                    alert("동의서 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + JSON.stringify(error));
                }

            })
        }

    } else {
    	var key = $("#_searchKeyword").val().trim();
        var keyword = encodeURI(key);
        var SearchResultAry = new Array();

        COMMON.plugin.wifiCheck(wifiCheckFn);

        var checkVal = localStorage.getItem("wifiCheckVal");
        if (checkVal < -90) {
            alert("WIFI 수신 감도가 좋지 않습니다. WIFI 통신이 잘 되는 곳에서 사용 바랍니다.");
        } else {
            COMMON.plugin.loadingBar("show", "전체 동의서 검색 중입니다.");
            $.ajax({
                url: ajax_url, // ajax_url,
                data: "submit_id=DRMRF00120&business_id=mr&instcd=204&formnm=" + keyword + "&userid=" + getLocalStorage("userId"), // "submit_id=DRMRF00120&ep_interface=emr&business_id=mr&instcd=204&formnm="+keyword,
                type: 'get',
                dataType: 'xml',
                success: function (result) {
                     if ($(result).find('item').length > 0) {
                        $(result).find('item').each(function () {
                            var SearchResult = new Object();
                            SearchResult.FormFromDt = $(this).find('formfromdt').text();
                            SearchResult.FormCd = $(this).find('formcd').text();
                            SearchResult.FormName = $(this).find('formnm').text();
                            SearchResult.FormExtnNm = $(this).find('formextnnm').text();
                            SearchResult.FormPrntNm = $(this).find('formprntnm').text(); 
                            SearchResult.FormRid = $(this).find('formrid').text();
                            SearchResult.FormGuid = $(this).find('formguid').text()
                            SearchResult.FormId = $(this).find('formguid').text();
                            SearchResult.FormVersion = $(this).find('formversion').text();
                            SearchResult.cosignYn = $(this).find('cosign_yn').text();
                            SearchResult.FormDepthngNm = $(this).find('formdepthngnm').text();
                            SearchResult.eco = $(this).find('eco').text();
                            SearchResult.nurscert_yn = $(this).find('nurscert_yn').text();
                            SearchResult.fvrt_yn = $(this).find('fvrt_yn').text();
                            SearchResult.consent_certneedcnt = $(this).find('cert_need_cnt').text(); 
                            SearchResult.lifelong_kind = $(this).find('lifelong_kind').text(); 
                            SearchResult.cosignFlag = "0"; 
                            
                            if ($(this).find('cosign_yn').text() == "N") {
                                SearchResult.cosignDeptCode = "";
                            }
                            SearchResultAry.push(SearchResult);
                        });
                    }
                    COMMON.plugin.loadingBar("hide", "");
                    consentSearchSuccessHandler(SearchResultAry, "A");
                },
                error: function (error) {
                	COMMON.plugin.loadingBar("hide", "");
                    alert("동의서 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + JSON.stringify(error));
                }

            })
        }

    }

};
// 코사인 조회 결과
function searchCosignSuccessHandler(resData, type) {
    $("#_cosignConsentList li").remove();
    $("#_cosignConsentList").removeClass("accordion");
    $("#_cosignConsentList").removeClass("no_data"); 

    var consents = resData;
    if ($.isEmptyObject(consents)) {
        $("#_cosignConsentList").addClass("no_data");
        var noDataHtml = "	<li><span>조회된 결과가 없습니다.</span></li>";
        $("#_cosignConsentList").append(noDataHtml);
    } else {
        $("#_cosignConsentList li").remove();
        $("#_cosignConsentList").removeClass("no_data");
        var consents = resData;
        for (var i = 0; i < consents.length; i++) {
            var consent = consents[i];
            var li = $("#_cosignConsentListTemplate li").clone();
            // 임시저장 or 완료저장
            if (consent.ConsentState == "TEMP") {
                //						li.prepend("<span class='list_notice'><span class='btn_flow flow_cosign'>코사인</span></span>");
                //						li.prepend("<span class='list_notice'><span class='btn_flow flow_tmp' style='font-size:15px;'>코사인</span></span>");
                // 녹취 파일이 있을경우 녹취 버튼 추가
                if (consent.RecordCnt != undefined && consent.RecordCnt != 0) {
                    addRecordBtn(li, "");
                }
            }
            
            if(consent.cosignFlag =="3"){
            	li.find("._formName").text("[" + consent.CreateDateTime.substring(0, 10) + "] " + consent.FormName + "[" + consent.cosignUserName + "]"); // 동의서
            }else{
            	li.find("._formName").text("[" + consent.CreateDateTime.substring(0, 10) + "] " + consent.FormName + "[" + consent.cosignDeptName + "]"); // 동의서
            }
            

            li.find(".sendPatientCode").text(consent.patientCode);
            li.find(".sendPatientName").text(consent.patientName);
            li.find(".sendWard").text(consent.roomCd);
            if (consent.ModifyUserName == undefined) {
                li.find(".sendUserDept").text(consent.modifyUserDeptName); // 동의서    consent.modifyUserName 
                li.find(".sendUserName").text(consent.modifyUserName); // 동의서    consent.modifyUserName 
            } else {
                li.find(".sendUserDept").text(consent.modifyUserDeptName); // 동의서    consent.modifyUserName 
                li.find(".sendUserName").text(consent.ModifyUserName); // 동의서    consent.modifyUserName  
            }
            // +" / " + consent.patientCode + " / " + consent.patientName + " / " + consent.roomCd

            li.attr("attr-data", JSON.stringify(consent));
            // 해당 동의서 클릭 이벤트 1a
            li.children().not(".form_box").not(".list_notice").on("click", function () {
                cosign_Flag = "1";
                $("#_cosignConsentList li").removeClass("on");
                $(this).parent().parent().addClass("on");
                var data = $(this).parent().attr("attr-data");
                //console.log("[일반] 해당 동의서 정보 검색 : " + data);
                if (data != "") {
                    var consentInfo = JSON.parse(data);
                    // DATA 
                    var consents = [];
                    var data_obj = JSON.parse(data);
                    consents[consents.length] = JSON.parse(data);
                    COMMON.LOCAL.eform.consent = consents;  
                    var cosign_obj = {
                        "patientCode": data_obj.patientCode,
                        "FormId": data_obj.FormId,
                        "FormName": data_obj.FormName,
                        "FormVersion": data_obj.FormVersion 
                    }
                    if (consents.length > 0) {
                        if (getLocalStorage("signPwd") == "" || getLocalStorage("signPwd") == null || getLocalStorage("signPwd") == undefined) {
                            $('#sign_popup').css('display', 'block');
                            localStorage.setItem("cosignFirst", "true");
                            $('#_certpassword').focus();
                            check_consent = $(this);
                        } else {
                            searchAppVersionCosign(cosign_obj, type);
                        }
                    } else {
                        alert("선택한 동의서가 없습니다.\n동의서를 선택해주세요.");
                    }
                }
            });
            $("#_cosignConsentList").append(li);
        }


    }
}


// 전자 동의서 검색 결과
function consentSearchSuccessHandler(resData, type) {
    $("#_consentList li").remove();
    $("#_consentList").removeClass("accordion");
    $("#_consentList").removeClass("no_data");

    var consents = resData;
    if ($.isEmptyObject(consents)) {
        $("#_consentList").addClass("no_data");
        var noDataHtml = "	<li><span>조회된 결과가 없습니다.</span></li>";
        $("#_consentList").append(noDataHtml);
    } else {
        // SET 동의서 검색일 경우 아코디언 방식으로 출력
        if (type == "S" || type == "B") {
            $("#_consentList").addClass("accordion");
            var tempFolderMstRids = [];
            var tempFolderNames = [];
            var folderMstRids = [];
            var folderNames = [];

            // depth 2 폴더명
            // depth 3 하위
            // depth 2 itemindxseq == depth 3 supitemindxseq 동일폴더

            for (var i = 0; i < consents.length; i++) {
                var consent = consents[i];
                if (consent.depth == "2") {
                    tempFolderNames.push(consent.path);
                    tempFolderMstRids.push(consent.itemindxseq);
                }
            }

            $.each(tempFolderMstRids, function (index, el) {
                if ($.inArray(el, folderMstRids) === -1) {
                    folderMstRids.push(el);
                }
            });

            $.each(tempFolderNames, function (index, el) {
                if ($.inArray(el, folderNames) === -1) {
                    folderNames.push(el);
                }
            });
 
            var consentCount = 1;
            // var test = '<input type="checkbox" style="z-index:9999;" />';
            for (var j = 0; j < folderMstRids.length; j++) {
                var groupCount = 0;
                var accordionHtml = "";
                accordionHtml += "<li>";
                // 비급여 2차 수정
                if(folderNames[j].indexOf("비급여")>-1){
                	accordionHtml += "<div class='_groupType'><span>" + ' 　' + folderNames[j] + "</span><button type='button' class='btn-accordion  on'><span>펼치기/닫기</span></button></div>";
                }else{
                	accordionHtml += "<div class='_groupType'><span>" + '<input type="checkbox" id="FOLDER_CHECK_C_' + j + '" class="_consentListCheckbox _consentList_folder_Checkbox" /><label for="FOLDER_CHECK_C_' + j + '">' + ' ' + folderNames[j] + "</span><button type='button' class='btn-accordion  on'><span>펼치기/닫기</span></button></div>";
                }
                accordionHtml += "	<ul class='on'>";

                for (var i = 0; i < consents.length; i++) {
                    var consent = consents[i];
                    if (consent.supitemindxseq == folderMstRids[j]) {
                        var li = $("#_consentListTemplate").clone();
                        // 체크박스 추가
                        var checkBoxHtml = ''; //<input type="checkbox" style="z-index:9999;" />

                        checkBoxHtml += '<span>' + consentCount + '.</span>';
                         

                        // -- 비급여 2차 수정   ------------------------------------------                
                        if(consent.path == "NONPAY"){  
                        	checkBoxHtml += '<span class="form_box form_box_checkbox" style="margin-left: 5px; height:35px;">';
                        	checkBoxHtml += '	<input type="checkbox" style="visibility:hidden" id="CHECK_C_' + i + '" class="_consentListCheckbox _consentList_Checkbox" /><label style="display:none"  for="CHECK_C_' + i + '">선택</label>';
                        }else{
                        	checkBoxHtml += '<span class="form_box form_box_checkbox" style="margin-left: 5px;">';
                        	checkBoxHtml += '	<input type="checkbox" id="CHECK_C_' + i + '" class="_consentListCheckbox _consentList_Checkbox" /><label for="CHECK_C_' + i + '">선택</label>';	
                        }
                        checkBoxHtml += '</span>';
                        li.find("li").prepend(checkBoxHtml);                        
                         
                        if(consent.path == "NONPAY"){  
                                li.find("._formName").text(consent.indxnm); 
                        }else{
                        	if (consent.FormExtnNm == "-") {
                                li.find("._formName").text(consent.FormName);

                            } else {
                                li.find("._formName").text(consent.FormName + "[" + consent.FormExtnNm + "]");
                            }
                        }
                        
                        // -----------------------------------------
                        
                        if (consent.eco == "N" || consent.eco == "" || consent.eco == undefined) {
                            li.find("li").append("<div class='eco_text_div'>OCR</div>");
                        } else {
                            if (consent.cosignYn == "Y") {
                                li.find("li").append("<div class='cosign_text_div'>Co-sign</div>");
                            }
                        } 
                        li.find("li").attr("attr-data", JSON.stringify(consent));
                        accordionHtml += li.html();
                        groupCount++;
                        consentCount++;
                    }
                }
                accordionHtml += "	</ul>";
                accordionHtml += "</li>";
                $("#_consentList").append(accordionHtml);
            }


            $("#_consentList").find("._consentList_folder_Checkbox").on("change", function () {
                var temp = $(this).attr("id");
                var id = "#_consentList #" + temp;
                var control = $(this).parent().parent().parent().find("._consentListCheckbox");
                if (control != null) {
                    if ($(id).is(":checked")) {
                        control.prop('checked', true);
                    } else {
                        control.prop('checked', false);
                    }
                }
            });
            // 해당 동의서 클릭 이벤트
            $("#_consentList ul li").find("._formName").on("click", function () {  
                $("#_consentList li").removeClass("on");
                var data = $(this).parent().attr("attr-data");  
                
                // -- 비급여 2차
                var tag = $(this).parent();
                
                
                var ecoval = JSON.parse(data);

                if (ecoval.eco == "N" || ecoval.eco == "" || ecoval.eco == undefined) {
                    alert("'OCR' 전용 서식입니다. nU에서 출력하여 작성하세요.");
                } else {
                    COMMON.plugin.loadingBar("show", "뷰어를 실행하는 중입니다.");
                    // 체크된 모든 항목들 가져오기
                    var lis = $("#_consentList ._consentList_Checkbox:checked");
                    var consents = [];
                    var consentNames = "";
                    cosign_Flag = "0";
                    var ecoFlag = false;
                    var cosign_Flags = false;
                    var cosignFlags = "";
                    var nurscert_Flag = false;
                    var nurscertFlag = ""; 
                    var errorCheck = "";
                    var patientInfo = JSON.parse(getLocalStorage("patientInfo"));   
                    
                    // -- 비급여 2차 수정
                	if(ecoval.path == "NONPAY"){
                		var setno = ecoval.itemindxseq;    
                	     $.ajax({
                             url: ajax_url, // ajax_url,
                             data: "submit_id=DRMRF00127&business_id=mr&instcd=204&setno=" + setno, 
                             type: 'get',
                             async : false,
                             dataType: 'xml',
                             success: function (result) { 
                                 COMMON.plugin.loadingBar("hide", "뷰어를 실행하는 중입니다."); 
                                 
                                 if ($(result).find('forminfo').length > 0) {
                                     var submitFormResult = new Object(); 
                                     $(result).find('forminfo').each(function () { 
                                    	 submitFormResult.FormName = $(this).find('formnm').text();
                                    	 submitFormResult.FormExtnNm = $(this).find('formextnnm').text();
                                    	 submitFormResult.FormPrntNm = $(this).find('formprntnm').text();
                                    	 submitFormResult.paper = $(this).find('paper').text();
                                    	 submitFormResult.eco = $(this).find('eco').text();
                                    	 submitFormResult.ocrseq = $(this).find('ocrseq').text();
                                    	 submitFormResult.FormGuid = $(this).find('formguid').text();
                                    	 submitFormResult.FormId = $(this).find('formguid').text();
                                    	 submitFormResult.FormVersion = $(this).find('formversion').text();
                                    	 submitFormResult.FormRid = $(this).find('formrid').text();
                                    	 submitFormResult.FormFromDt = $(this).find('formfromdt').text();
                                    	 submitFormResult.FormCd = $(this).find('formcd').text();
                                    	 submitFormResult.cosign_yn = $(this).find('cosign_yn').text();
                                    	 submitFormResult.opdr_yn = $(this).find('opdr_yn').text();
                                    	 submitFormResult.nurscert_yn = $(this).find('nurscert_yn').text();
                                    	 submitFormResult.cert_need_cnt = $(this).find('cert_need_cnt').text();  
                                    	 submitFormResult.nowrite = "true";
                                    	 submitFormResult.cosignFlag = "0";
                                    	 submitFormResult.setno = setno;
                                    	 submitFormResult.path = "NONPAY";
                                    	 submitFormResult.itemindxseq = ecoval.itemindxseq;
                                    	 submitFormResult.consent_certneedcnt = "";
                                         if ($(this).find('cosign_yn').text() == "N") {
                                        	 submitFormResult.cosignDeptCode = "";
                                         } 

                                         submitFormResult.tmp01 = "NONPAYOUTPATIENT";
                                         submitFormResult.tmp02 = $(this).find('tmp02').text(); 
                                         submitFormResult.tmp03 = $(this).find('tmp03').text();
                                         submitFormResult.tmp04 = $(this).find('tmp04').text(); 
                                         submitFormResult.tmp05 = $(this).find('tmp05').text();
                                         submitFormResult.tmp06 = $(this).find('tmp06').text(); 
                                         submitFormResult.tmp07 = $(this).find('tmp07').text();
                                         submitFormResult.tmp08 = $(this).find('tmp08').text(); 
                                         submitFormResult.tmp09 = $(this).find('tmp09').text();
                                         submitFormResult.tmp10 = $(this).find('tmp10').text();
                                         submitFormResult.lifelong_kind = $(this).find('lifelong_kind').text();

                                     });
                                     // 서식정보 호출하게끔
                                     tag.attr("attr-data",JSON.stringify(submitFormResult));
                                     data = tag.attr("attr-data");
                                     var consentNonpay = data; 
                                     var consentsNonpay = []; 
                                     
                                     var ocrAdd = JSON.parse(consentNonpay); 
                                     ocrAdd.ocrTag = CreateOcrTag();
                                     ocrAdd.patientCode = patientInfo.PatientCode;
                                     consentNonpay = JSON.stringify(ocrAdd); 
                                     consentsNonpay[consentsNonpay.length] = consentNonpay;  
                                     COMMON.LOCAL.eform.consent = consentsNonpay; 
 
                                     var idx = 0;
                                     $(result).find('list').each(function () {  
                                 		 if(idx == 0){
                                 			patientInfo.prcpcd =  $(this).find('prcpcd').text(); 
                                 			patientInfo.prcpnm =  $(this).find('prcpnm').text(); 
                                 		 }else{
                                 			patientInfo.prcpcd =  patientInfo.prcpcd + "," +$(this).find('prcpcd').text();  
                                 			patientInfo.prcpnm =  patientInfo.prcpnm + ",,," +$(this).find('prcpnm').text();                         			 
                                 		 }
                                 		
                                 		patientInfo['nonBene'+idx] = $(this).find('prcpnm').text();
                                 		var scoreamt = $(this).find('scoreamt').text();
                                 		scoreamt = scoreamt.replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ",");
                                 		patientInfo['estiCost'+idx] = scoreamt;
                                 		idx = idx+1; 
                                     }); 
                                 	  
                              		$(".patient_info").attr("attr-data", "");
                             	    $(".patient_info").attr("attr-data", JSON.stringify(patientInfo));
                                 	 COMMON.LOCAL.eform.patient = patientInfo;   
                                 } 

                             },
                             error: function (error) { 
                                 alert("비급여 동의서 정보 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + JSON.stringify(error));
                             }
                         })       
                          
                         openCheckVal = true;
                         if(openCheckVal){ 
                         		  if (cosignFlags.indexOf("Y") > -1 && cosignFlags.indexOf("N") > -1) {
                                       cosign_Flags = true;
                                   }
                                   if (nurscertFlag.indexOf("Y") > -1 && nurscertFlag.indexOf("N") > -1) {
                                       nurscert_Flag = true;
                                   }
                                   if (ecoFlag != true && cosign_Flags != true && nurscert_Flag != true) {
                                       //searchAppVersion(); 
                                       COMMON.plugin.loadingBar("hide", "뷰어를 실행하는 중입니다.");
                                       if (getLocalStorage("signPwd") == "" || getLocalStorage("signPwd") == null || getLocalStorage("signPwd") == undefined) {
                                           $('#sign_popup').css('display', 'block');
                                           $('#_certpassword').focus();
                                           check_consent = $(this);
                                       } else {
                                           searchAppVersion();
                                       }
                                   }
                                   if (ecoFlag == true) {
                                       COMMON.plugin.loadingBar("hide", "뷰어를 실행하는 중입니다.");
                                       alert("nU 종이 전용 서식이 선택되었습니다. OCR 표기된 서식은 종이로 출력하여 사용바랍니다.");
                                   }
                                   if (cosign_Flags) {
                                       COMMON.plugin.loadingBar("hide", "뷰어를 실행하는 중입니다.");
                                       alert("Co-sign 서식과 일반 서식이 동시 선택되었습니다. Co-sign 표기된 서식은 Co-sign 서식끼리 선택 후 사용바랍니다.");
                                   }
                                   if(ecoFlag != true && cosign_Flags != true){
                                       if (nurscert_Flag) {
                                           localStorage.setItem("nurscertAndDoctor", "true");                                  
                                           COMMON.plugin.loadingBar("hide", "뷰어를 실행하는 중입니다.");
                                           if (getLocalStorage("signPwd") == "" || getLocalStorage("signPwd") == null || getLocalStorage("signPwd") == undefined) {
                                               $('#sign_popup').css('display', 'block');
                                               $('#_certpassword').focus();
                                               check_consent = $(this);
                                           } else {
                                               searchAppVersion();
                                           } 
                                       }  
                                   }  
                       }else{
                       	   alert("ErrorCode : " + openCheckErrCd+ "\nErrorMsg : "+openCheckErrMsg);
                           COMMON.plugin.loadingBar("hide", "뷰어를 실행하는 중입니다.");
                       }   
                	}else{  
                		 lis.each(function() { 
                             var consent = $(this).parent().parent().attr("attr-data");

                             var ocrAdd = JSON.parse(consent);
                             var ocrtagValue =  CreateOcrTag(); 
                             if(ocrtagValue =="error"){
                             	errorCheck = "Y";
                             } 
                             ocrAdd.ocrTag = ocrtagValue;
                             ocrAdd.patientCode = patientInfo.PatientCode;
                             consent = JSON.stringify(ocrAdd);
                             var data = JSON.parse(consent);
                             if (data.eco == "N" || data.eco == "" || data.eco == undefined) {
                                 ecoFlag = true;
                             }
                             cosignFlags = cosignFlags + data.cosignYn;
                             nurscertFlag = nurscertFlag + data.nurscert_yn;
                             consents[consents.length] = consent;
                             var data = JSON.parse(consent);
                             consentNames += (consentNames == "") ? data.FormName : "\n," + data.FormName;

                         });
                         COMMON.LOCAL.eform.consent = consents;
                         
                         // 체크안했을때
                         if (consents.length < 1) { 
                             var consent = $(this).parent().attr("attr-data");

                             var ocrAdd = JSON.parse(consent);
                             var ocrtagValue =  CreateOcrTag(); 
                             if(ocrtagValue =="error"){
                             	errorCheck = "Y";
                             } 
                             ocrAdd.ocrTag = ocrtagValue;                        

                             ocrAdd.patientCode = patientInfo.PatientCode;
                             consent = JSON.stringify(ocrAdd);

                             consents[consents.length] = consent;
                             var data = JSON.parse(consent);
                             consentNames += (consentNames == "") ? data.FormName : "\n," + data.FormName;
                             COMMON.LOCAL.eform.consent = consents;
                         }


                        if(errorCheck != "Y"){
	                        var openCheck = JSON.parse($(this).parent().attr("attr-data"));
	                        var patiCode = $("#_detailPatientCode").text();
	
	                        var valueAry = new Array();
	                         
	                        if(lis.length==0){
	                            for (var i = 0; i < consents.length; i++) {
	                            	var checkConsent = JSON.parse($(this).parent().attr("attr-data")); 
	                                var valueObj = new Object();
	                            	valueObj.patientCode = patiCode;
	                            	valueObj.formId = checkConsent.FormId;
	                            	valueObj.formVersion = checkConsent.FormVersion;                 	
	                            	
	                            	valueAry.push(valueObj);							
	    						} 
	                        }else{
	                            lis.each(function (index){
	                            	var checkConsent = JSON.parse($(this).parent().parent().attr("attr-data"));
	                                var valueObj = new Object();
	                            	valueObj.patientCode = patiCode;
	                            	valueObj.formId = checkConsent.FormId;
	                            	valueObj.formVersion = checkConsent.FormVersion;                 	
	                            	
	                            	valueAry.push(valueObj);
	                            })  
	                        } 
	
	                        var openCheckVal = "";
	                        var openCheckErrCd = "";
	                        var openCheckErrMsg = "";
	                        var openCheckErrMsg2 = "";
	                        $.ajax({
	                            url: ajax_nu_url + '/biz/nu/member/viewer/eForm25/consent/isOpen',
	                            type: 'post',
	                            timeout: 40000,
	                            async : false,
	                            data: {
	                                parameter: JSON.stringify(valueAry)
	                            }
	                        }).done(function (data) {
	                            $.each(data, function (index, item) {   
	                            	openCheckVal = data[index].result;
	                            	openCheckErrCd = data[index].errorCode;
	                            	openCheckErrMsg = data[index].errorMsg;
	                            	openCheckErrMsg2 = data[index].errorMsg2;    
	                            });
	                        }).fail(function (xhr, status, errorThrown) { 
	                            alert("서식작성여부 조회 중 오류가 발생했습니다. 관리자에게 문의바랍니다. " + errorThrown)
	                        });    
	                        
	                        
	                        if(openCheckVal){ 
	                        		  if (cosignFlags.indexOf("Y") > -1 && cosignFlags.indexOf("N") > -1) {
	                                      cosign_Flags = true;
	                                  }
	                                  if (nurscertFlag.indexOf("Y") > -1 && nurscertFlag.indexOf("N") > -1) {
	                                      nurscert_Flag = true;
	                                  }
	                                  if (ecoFlag != true && cosign_Flags != true && nurscert_Flag != true) {
	                                      //searchAppVersion(); 
	                                      COMMON.plugin.loadingBar("hide", "뷰어를 실행하는 중입니다.");
	                                      if (getLocalStorage("signPwd") == "" || getLocalStorage("signPwd") == null || getLocalStorage("signPwd") == undefined) {
	                                          $('#sign_popup').css('display', 'block');
	                                          $('#_certpassword').focus();
	                                          check_consent = $(this);
	                                      } else {
	                                          searchAppVersion();
	                                      }
	                                  }
	                                  if (ecoFlag == true) {
	                                      COMMON.plugin.loadingBar("hide", "뷰어를 실행하는 중입니다.");
	                                      alert("nU 종이 전용 서식이 선택되었습니다. OCR 표기된 서식은 종이로 출력하여 사용바랍니다.");
	                                  }
	                                  if (cosign_Flags) {
	                                      COMMON.plugin.loadingBar("hide", "뷰어를 실행하는 중입니다.");
	                                      alert("Co-sign 서식과 일반 서식이 동시 선택되었습니다. Co-sign 표기된 서식은 Co-sign 서식끼리 선택 후 사용바랍니다.");
	                                  }
	                                  if(ecoFlag != true && cosign_Flags != true){
	                                      if (nurscert_Flag) {
	                                          localStorage.setItem("nurscertAndDoctor", "true");                                  
	                                          COMMON.plugin.loadingBar("hide", "뷰어를 실행하는 중입니다.");
	                                          if (getLocalStorage("signPwd") == "" || getLocalStorage("signPwd") == null || getLocalStorage("signPwd") == undefined) {
	                                              $('#sign_popup').css('display', 'block');
	                                              $('#_certpassword').focus();
	                                              check_consent = $(this);
	                                          } else {
	                                              searchAppVersion();
	                                          } 
	                                      }  
	                                  }  
	                      }else{
	                      	alert("ErrorCode : " + openCheckErrCd+ "\nErrorMsg : "+openCheckErrMsg);
	                          COMMON.plugin.loadingBar("hide", "뷰어를 실행하는 중입니다.");
	                      }  
	                        
                	}else{ 
                    	alert("OCR TAG 생성 중 오류가 발생했습니다.\n잠시 후 다시 시도해주세요.")
                    } 
                        
                	} 
                	//---------------------------------------------
                    
                    
                  
                }

            });
        } else {
            for (var i = 0; i < consents.length; i++) {
                var consent = consents[i];
                var li = $("#_consentListTemplate li").clone();
                // 1. 체크박스 동의서명 ==> 1. 체크박스 체크박스 동의서명
                // 체크박스 추가
                var checkBoxHtml = "";
                var checkBoxHtmls = "";
                // var checkBoxHtmls = "";
                checkBoxHtml += '<span style="width: 20px; text-align: center;" >' + (i + 1) + '.</span>';
                checkBoxHtml += '<span class="form_box form_box_checkbox" style="margin-left: 5px;">';
                checkBoxHtml += '	<input type="checkbox" id="CHECK_C_' + i + '" class="_consentListCheckbox" /><label for="CHECK_C_' + i + '">선택</label>';
                checkBoxHtml += '</span>';
                if (consent.fvrt_yn == "N") {
                    checkBoxHtmls += '<span class="form_box form_box_checkboxs _consentListMyConsent" style="width:35px;height:35px;">';
                    checkBoxHtmls += '	<img src="../../images/star.png" alt="" style="width:100%;height:100%;border:0px solid; margin-left:5px;"/>';
                    checkBoxHtmls += '	<input type="checkbox" id="CHECK_C_' + i + '" class="_consentListCheckboxs" />';
                    checkBoxHtmls += '</span>';
                } else {
                    checkBoxHtmls += '<span class="form_box form_box_checkboxs _consentListMyConsent" style="width:35px;height:35px;">';
                    checkBoxHtmls += '	<img src="../../images/star_check.png" alt="" style="width:100%;height:100%;border:0px solid; margin-left:5px;"/>';
                    checkBoxHtmls += '	<input type="checkbox" id="CHECK_C_' + i + '" class="_consentListCheckboxs" checked/>';
                    checkBoxHtmls += '</span>';
                }

                li.prepend(checkBoxHtml);
                if (consent.FormExtnNm == "-") {
                    li.find("._formName").text(consent.FormName);
                } else {
                    li.find("._formName").text(consent.FormName + "[" + consent.FormExtnNm + "]");
                }
                //              if (consent.nurscert_yn == "Y") {
                //                  li.find("._formName").addClass("nurscert_yn");
                //              }
                if (consent.eco == "N" || consent.eco == "" || consent.eco == undefined) {
                    li.append("<div class='eco_text_div'>OCR</div>");
                } else {

                    if (consent.cosignYn == "Y") {
                        li.append("<div class='cosign_text_div'>Co-sign</div>");
                    }
                    //					if(consent.nurscert_yn == "Y"){ 
                    //						li.append("<div class='cosign_text_div'>간호사</div>"); 
                    //					}
                }
                li.append(checkBoxHtmls);

                // li.append(checkBoxHtmls);
                li.attr("attr-data", JSON.stringify(consent));

                // 해당 동의서 클릭 이벤트  
                li.find(".list_txt").on("click", function () {  
                	//alert('클릭4');	// will be deleted              	 
                	
                    $("#_consentList li").removeClass("on");
                    $(this).parent().parent().addClass("on"); 
                    var data = $(this).parent().attr("attr-data");
                    var ecoFlag = false;
                    var cosign_Flags = false;
                    var nurscert_Flag = false;
                    var cosignFlags = "";
                    var nurscertFlag = ""; 
                    var ecoval = JSON.parse(data);

                    if (ecoval.eco == "N" || ecoval.eco == "" || ecoval.eco == undefined) {
                        alert("'OCR' 전용 서식입니다. nU에서 출력하여 작성하세요.");
                    } else {
                        COMMON.plugin.loadingBar("show", "뷰어를 실행하는 중입니다.");
                        cosign_Flag = "0";
                        // 체크된 모든 항목들 가져오기
                        var lis = $("#_consentList ._consentListCheckbox:checked");
                        var consents = [];
                        var consentNames = "";  
                        var patientInfo = JSON.parse($(".patient_info").attr("attr-data"));
                        
                        lis.each(function (index) {
                            var consent = $(this).parent().parent().attr("attr-data");
                            check_consent_list = $(this);
                            var ocrAdd = JSON.parse(consent);
                            ocrAdd.ocrTag = CreateOcrTag(); 
                            ocrAdd.patientCode = patientInfo.PatientCode;
                            consent = JSON.stringify(ocrAdd);
                            consents[consents.length] = consent;
                            var data = JSON.parse(consent);
                            if (data.eco == "N" || data.eco == "" || data.eco == undefined) {
                                ecoFlag = true;
                            } 
                            nurscertFlag = nurscertFlag + data.nurscert_yn;
                            cosignFlags = cosignFlags + data.cosignYn;
                            consentNames += (consentNames == "") ? data.FormName : "\n," + data.FormName; 
                        }); 
                        COMMON.LOCAL.eform.consent = consents;
                        
                        if (consents.length < 1) {
                            var consent = $(this).parent().attr("attr-data"); 
                            var ocrAdd = JSON.parse(consent); 
                            ocrAdd.ocrTag = CreateOcrTag();
                            ocrAdd.patientCode = patientInfo.PatientCode;
                            consent = JSON.stringify(ocrAdd);
                            consents[consents.length] = consent;
                            var data = JSON.parse(consent);
 
                            consentNames += (consentNames == "") ? data.FormName : "\n," + data.FormName;
                            COMMON.LOCAL.eform.consent = consents;
                        } 
                       
                        var openCheck = JSON.parse($(this).parent().attr("attr-data"));
                        var patiCode = $("#_detailPatientCode").text();

                        var valueAry = new Array();
                         
                        if(lis.length==0){
	                        for (var i = 0; i < consents.length; i++) {
	                        	var checkConsent = JSON.parse($(this).parent().attr("attr-data")); 
	                            var valueObj = new Object();
	                        	valueObj.patientCode = patiCode;
	                        	valueObj.formId = checkConsent.FormId;
	                        	valueObj.formVersion = checkConsent.FormVersion;                 	
	                        	
	                        	valueAry.push(valueObj);							
							} 
                        }else{
                            lis.each(function (index){
                            	var checkConsent = JSON.parse($(this).parent().parent().attr("attr-data"));
                                var valueObj = new Object();
                            	valueObj.patientCode = patiCode;
                            	valueObj.formId = checkConsent.FormId;
                            	valueObj.formVersion = checkConsent.FormVersion;                 	
                            	
                            	valueAry.push(valueObj);
                            })  
                        } 

                        var openCheckVal = "";
                        var openCheckErrCd = "";
                        var openCheckErrMsg = "";
                        var openCheckErrMsg2 = "";
                        $.ajax({
                            url: ajax_nu_url + '/biz/nu/member/viewer/eForm25/consent/isOpen',
                            type: 'post',
                            timeout: 40000,
                            async : false,
                            data: {
                                parameter: JSON.stringify(valueAry)
                            }
                        }).done(function (data) {
                            $.each(data, function (index, item) {   
                            	openCheckVal = data[index].result;
                            	openCheckErrCd = data[index].errorCode;
                            	openCheckErrMsg = data[index].errorMsg;
                            	openCheckErrMsg2 = data[index].errorMsg2;    
                            });
                        }).fail(function (xhr, status, errorThrown) { 
                            alert("서식작성여부 조회 중 오류가 발생했습니다. 관리자에게 문의바랍니다. " + errorThrown)
                        });    
                        if(openCheckVal){
                        	  if (cosignFlags.indexOf("Y") > -1 && cosignFlags.indexOf("N") > -1) {
                                  cosign_Flags = true;
                              }
                              if (nurscertFlag.indexOf("Y") > -1 && nurscertFlag.indexOf("N") > -1) {
                                  nurscert_Flag = true;
                              } 
                              if (ecoFlag != true && cosign_Flags != true && nurscert_Flag != true) {
                                  COMMON.plugin.loadingBar("hide", "뷰어를 실행하는 중입니다."); 
                                  // 전자인증서 검사
                                  if (getLocalStorage("signPwd") == "" || getLocalStorage("signPwd") == null || getLocalStorage("signPwd") == undefined) {
                                      $('#sign_popup').css('display', 'block');
                                      $('#_certpassword').focus();
                                      check_consent = $(this);
                                  } else {
                                      searchAppVersion();
                                  }
                              }

                              
                              if (ecoFlag == true) {
                                  COMMON.plugin.loadingBar("hide", "뷰어를 실행하는 중입니다.");
                                  alert("nU 종이 전용 서식이 선택되었습니다. OCR 표기된 서식은 종이로 출력하여 사용바랍니다.");
                              }
                              if (cosign_Flags) {
                                  COMMON.plugin.loadingBar("hide", "뷰어를 실행하는 중입니다.");
                                  alert("Co-sign 서식과 일반 서식이 동시 선택되었습니다. Co-sign 표기된 서식은 Co-sign 서식끼리 선택 후 사용바랍니다.");
                              }
                              if(ecoFlag != true && cosign_Flags != true){
                                  if (nurscert_Flag) {
                                      localStorage.setItem("nurscertAndDoctor", "true"); 
                                      COMMON.plugin.loadingBar("hide", "뷰어를 실행하는 중입니다.");
                                      alert("여기2?");
                                      if (getLocalStorage("signPwd") == "" || getLocalStorage("signPwd") == null || getLocalStorage("signPwd") == undefined) {
                                          $('#sign_popup').css('display', 'block');
                                          $('#_certpassword').focus();
                                          check_consent = $(this);
                                      } else {
                                          searchAppVersion();
                                      } 
                                  }  
                              }
                        }else{
                          	alert("ErrorCode : " + openCheckErrCd+ "\nErrorMsg : "+openCheckErrMsg);
                            COMMON.plugin.loadingBar("hide", "뷰어를 실행하는 중입니다.");
                        }  
                    }  
                });
                $("#_consentList").append(li);
            }

        }
    }
};


/*
 * 클라우드 서버에서 사용하는 즐겨찾기 동의서 리스트 가져오는 함수
 */
var TempConsentList;

// 작성 동의서 검색
function fnAllSearchConsent() {
    $("#_consentAllList li").remove();
    var searchType = $(":radio[name='radio_cmt_type']:checked").val();
    var viewDeptCode = $("#_efromCmtClnDept option:selected").val();
    var detailPatient = JSON.parse($(".patient_info").attr("attr-data"));
    var SearchResultAry = new Array();

    var value = {
        "patientCode": $("#_detailPatientCode").text(),
        "startDate": $("#_consentStartDate").val().replace(/-/g, ""),
        "endDate": $("#_consentendDate").val().replace(/-/g, ""),
        "userDeptCd": getLocalStorage("userDeptCode"), // "4040000000",
        "searchMode": "normal"
    }
    //alert(getLocalStorage("userDeptCode"))

    COMMON.plugin.wifiCheck(wifiCheckFn);

    var checkVal = localStorage.getItem("wifiCheckVal");
    if (checkVal < -90) {
        alert("WIFI 수신 감도가 좋지 않습니다. WIFI 통신이 잘 되는 곳에서 사용 바랍니다.");
    } else {
        $.ajax({
            url: ajax_nu_url + '/biz/nu/member/viewer/eForm25/consent/mst/get',
            type: 'post',
            timeout: 40000,
            data: {
                parameter: JSON.stringify(value)
            }
        }).done(function (data) {
            $.each(data, function (index, item) {
                var DATA_JSON = JSON.parse(JSON.stringify(data[index]));
                var SearchResult = new Object();
                SearchResult.consentMstRid = DATA_JSON.consentMstRid; 
                SearchResult.consentStateDisp = DATA_JSON.consentStateDisp;
                SearchResult.hosType = DATA_JSON.hosType;
                SearchResult.patientCode = DATA_JSON.patientCode;
                SearchResult.patientName = DATA_JSON.patientName;
                SearchResult.visitType = DATA_JSON.visitType;
                SearchResult.clnDate = DATA_JSON.clnDate;
                SearchResult.clnDeptCd = DATA_JSON.clnDeptCd;
                SearchResult.wardCd = DATA_JSON.wardCd;
                SearchResult.wardName = DATA_JSON.wardName;
                SearchResult.roomCd = DATA_JSON.roomCd;
                SearchResult.FormId = DATA_JSON.formId;
                SearchResult.FormName = DATA_JSON.formName;
                SearchResult.orderDate = DATA_JSON.orderDate;
                SearchResult.orderCd = DATA_JSON.orderCd;
                SearchResult.orderSeqNo = DATA_JSON.orderSeqNo;
                SearchResult.orderName = DATA_JSON.orderName;
                SearchResult.orderDiv = DATA_JSON.orderDiv;
                SearchResult.ConsentState = DATA_JSON.consentState;
                SearchResult.completeYn = DATA_JSON.completeYn;
                SearchResult.completeDatetime = DATA_JSON.completeDatetime;
                SearchResult.createUserId = DATA_JSON.createUserId;
                SearchResult.createUserName = DATA_JSON.createUserName;
                SearchResult.createUserDeptCd = DATA_JSON.createUserDeptCd;
                SearchResult.createUserDeptName = DATA_JSON.createUserDeptName;
                SearchResult.CreateDateTime = DATA_JSON.createDatetime;
                //						SearchResult.createDatetime = DATA_JSON.createDatetime;
                SearchResult.modifyUserId = DATA_JSON.modifyUserId;
                SearchResult.modifyUserName = DATA_JSON.modifyUserName;
                SearchResult.modifyUserDeptCd = DATA_JSON.modifyUserDeptCd;
                SearchResult.modifyUserDeptName = DATA_JSON.modifyUserDeptName;
                SearchResult.modifyDatetime = DATA_JSON.modifyDatetime;
                SearchResult.FormVersion = DATA_JSON.formVersion;
                SearchResult.recordCnt = DATA_JSON.recordCnt;
                SearchResult.cosignYn = DATA_JSON.cosignYn;
                SearchResult.useYn = DATA_JSON.useYn;
                SearchResult.cosignDeptCode = DATA_JSON.cosignDeptCode;
                SearchResult.cosignDeptName = DATA_JSON.cosignDeptName;
                if (SearchResult.cosignDeptCode != undefined) {
                    SearchResult.writeConsentCosign = "true";
                }
                SearchResult.formCd = DATA_JSON.formCd;
                SearchResult.ocrTag = DATA_JSON.ocrTag;
                SearchResult.certNo = DATA_JSON.certNo;

                SearchResult.cosignFlag = "0";
                SearchResult.jobkindcd = localStorage.getItem("jobkindcd");
                SearchResult.opdrYn = DATA_JSON.opdrYn;
                SearchResult.opdrSignYn = DATA_JSON.opdrSignYn;
                SearchResult.nursCertYn = DATA_JSON.nursCertYn;
                SearchResult.certCnt = DATA_JSON.certCnt;
                SearchResult.consent_certneedcnt = DATA_JSON.certNeedCnt; 
                SearchResult.lifelong_kind = DATA_JSON.lifelongKind;

                SearchResult.userId = getLocalStorage("userId");
                SearchResult.userName = getLocalStorage("userName");
                SearchResult.userDeptCd = getLocalStorage("userDeptCode");
                SearchResult.userDeptName = getLocalStorage("userDeptName");
                //2022-02-03
                SearchResult.verbalMultiFlag = DATA_JSON.verbalMultiFlag;
                SearchResultAry.push(SearchResult);
            }) 
            consentAllSearchSuccessHandler(SearchResultAry);
        }).fail(function (xhr, status, errorThrown) { 
            alert("작성동의서 조회 중 오류가 발생했습니다. 관리자에게 문의바랍니다. " + errorThrown)
        });
    }

};

// 작성 동의서 검색 결과
function consentAllSearchSuccessHandler(resData) {
    $("#_consentAllList li").remove();
    $("#_consentAllList").removeClass("no_data");
    var consents = resData;

    if ($.isEmptyObject(consents)) {
        $("#_consentAllList").addClass("no_data");
        var noDataHtml = "	<li><span>조회된 결과가 없습니다.</span></li>";
        $("#_consentAllList").append(noDataHtml);
    } else {
        for (var i = 0; i < consents.length; i++) {
            var consent = consents[i];
            var li = $("#_writeConsent li").clone();
            // 임시저장 or 완료저장 
            if (consent.ConsentState == "TEMP") {
                if (consent.cosignDeptCode == "-1" || consent.cosignDeptCode == undefined) {
                    if (consent.useYn == "N") {
                        li.prepend("<span class='list_notices'><span class='btn_flow flow_delete'>임시삭제</span></span>");
                        li.css('opacity', '0.3');
                        //  li.addClass('deleteLi');
                    } else {
                        li.prepend("<span class='list_notice'><span class='btn_flow flow_tmp'>임시</span></span>");
                    }
                } else {
                    li.prepend("<span class='list_notice'><span class='btn_flow flow_cosign'>Co-sign</span></span>");
                }

            } else if (consent.ConsentState == "ELECTR_CMP") {
                li.prepend("<span class='list_notice'><span class='btn_flow flow_end'>완료</span></span>");
   
                //2022-02-03
                if(consent.verbalMultiFlag == "V,N"){
                	li.find('.list_notice').append("<span class='btn_flow flow_multi_verbal' style='margin-top:9px;'>구두동의</span>");
               }else if(consent.verbalMultiFlag == "M,N"){
            	   li.find('.list_notice').append("<span class='btn_flow flow_multi_verbal' style='margin-top:9px;'>응급동의</span>");
               }else if(consent.verbalMultiFlag == "V,Y"){
            	   li.find('.list_notice').append("<span class='btn_flow flow_multi_verbal_end' style='margin-top:9px;'>구두완료</span>");
               }else if(consent.verbalMultiFlag == "M,Y"){
            	   li.find('.list_notice').append("<span class='btn_flow flow_multi_verbal_end' style='margin-top:9px;'>응급완료</span>");
               }
                
                // 시술미비 
                if (consent.opdrYn == "Y" && consent.opdrSignYn == "N") {
                    li.find('.list_notice').append("<span class='btn_flow flow_treatment' style='margin-top:9px;'>시술의</span>");
                }
            } else if (consent.ConsentState == "FAIL") {
                li.prepend("<span class='noti fail' style='display:block;'>실패</span>");
            } else if (consent.ConsentState == "D") {
                li.prepend("<span class='list_notice'><span class='btn_flow flow_delete'>인증삭제</span></span>");
                li.css('opacity', '0.3');
                li.addClass('deleteLi');
            } else if(consent.ConsentState == "ELECTR_TEMP"){
            	li.prepend("<span class='list_notice'><span class='btn_flow flow_ing'>진행("+consent.certCnt+"/"+consent.consent_certneedcnt+")</span></span>");
            }
            

            // 녹취 파일이 있을경우 녹취 버튼 추가
            if (consent.recordCnt != undefined && consent.recordCnt != 0) {
                addRecordBtn(li, "");
            }
            
            li.find("._formInfo").text("[" + consent.CreateDateTime + " / " + consent.modifyUserDeptName + " / " + consent.modifyUserName + " ] "); // 동의서
            if (consent.FormName.length > 31) {
                li.find('._formInfo').css('height', '20px');
            };
            li.find("._formName").text(consent.FormName); // 동의서


            // 동의서 삭제버튼
            // 인증 저장 삭제 추가 2021-10-25
            if(getLocalStorage("aprecupdtyn") == "Y"){
            	if ( consent.ConsentState == "TEMP" && (consent.cosignDeptCode == "-1" || consent.cosignDeptCode == undefined)) {
                    if (consent.modifyUserDeptCd == getLocalStorage("userDeptCode") && consent.useYn == "Y") {
                        li.append("<div class='tempConsentDelete' style='z-index:9400; border: 0px currentColor;min-width:40px; position:flex;right:0px; width: 40px; height: 40px; margin-top: 10px; float: right;' > <img class='tempDelete' style='border: 0px currentColor;  width: 100%; height: 100%;' src='../../images/trash_delete.png'></img></div>")
                    }
                }else if (consent.ConsentState == "ELECTR_CMP" && (consent.cosignDeptCode == "-1" || consent.cosignDeptCode == undefined)) {
                    if (consent.modifyUserDeptCd == getLocalStorage("userDeptCode") && consent.useYn == "Y") {
                        li.append("<div class='tempConsentDelete' style='z-index:9400; border: 0px currentColor;min-width:40px; position:flex;right:0px; width: 40px; height: 40px; margin-top: 10px; float: right;' > <img class='tempDelete' style='border: 0px currentColor;  width: 100%; height: 100%;' src='../../images/trash_delete.png'></img></div>")
                    }else if (localStorage.getItem("jobkindcd").substring(0, 2) != "03" && consent.useYn == "Y"){
                        li.append("<div class='tempConsentDelete' style='z-index:9400; border: 0px currentColor;min-width:40px; position:flex;right:0px; width: 40px; height: 40px; margin-top: 10px; float: right;' > <img class='tempDelete' style='border: 0px currentColor;  width: 100%; height: 100%;' src='../../images/trash_delete.png'></img></div>")
                    }
                }
            }else{
            	if (( consent.ConsentState == "TEMP") && (consent.cosignDeptCode == "-1" || consent.cosignDeptCode == undefined)) {
                    if (consent.modifyUserDeptCd == getLocalStorage("userDeptCode") && consent.useYn == "Y") {
                        li.append("<div class='tempConsentDelete' style='z-index:9400; border: 0px currentColor;min-width:40px; position:flex;right:0px; width: 40px; height: 40px; margin-top: 10px; float: right;' > <img class='tempDelete' style='border: 0px currentColor;  width: 100%; height: 100%;' src='../../images/trash_delete.png'></img></div>")
                    }
                }
            }
            
            li.find("._formUserInfo").text(); // 동의서

            li.attr("attr-data", JSON.stringify(consent));

            // 해당 동의서 클릭 이벤트 
            li.not('.deleteLi').children().not(".form_box").not(".list_notice").not(".tempConsentDelete").on("click", function () {
                // console.log("[일반] 해당 동의서 정보 검색 : " + data);
                $("#_consentAllList li").removeClass("on");
                $(this).parent().parent().addClass("on");
                var data = $(this).parent().attr("attr-data");
 
                cosign_Flag = "0";
                if (data != "") {
                    var consentInfo = JSON.parse(data);
                    COMMON.LOCAL.eform.myConsent = consentInfo; 
                    if (consentInfo.ConsentState == "ELECTR_CMP") {

                        var consents = [];
                        consents[consents.length] = JSON.parse(data);
                        COMMON.LOCAL.eform.consent = consents;
                        if (consentInfo.opdrYn == "Y" && consentInfo.opdrSignYn == "N") {
                            if (consents.length > 0) {
                                if (getLocalStorage("signPwd") == "" || getLocalStorage("signPwd") == null || getLocalStorage("signPwd") == undefined) {
                                    $('#sign_popup').css('display', 'block');
                                    $('#_certpassword').focus();
                                    check_consent = $(this);
                                } else {
                                    $('.dimmed').css('display', 'block');
                                    $('.Treatment_List').css('display', 'block');
                                }
                            } else {
                                alert("선택한 동의서가 없습니다.\n동의서를 선택해주세요.");
                            }
                        } else {
                            if (consents.length > 0) {
                                if (getLocalStorage("signPwd") == "" || getLocalStorage("signPwd") == null || getLocalStorage("signPwd") == undefined) {
                                    $('#sign_popup').css('display', 'block');
                                    $('#_certpassword').focus();
                                    check_consent = $(this);
                                } else {
                                    searchAppEndVersion();
                                }
                            } else {
                                alert("선택한 동의서가 없습니다.\n동의서를 선택해주세요.");
                            }
                        }
                    } else {
                        var consents = [];
                        consents[consents.length] = JSON.parse(data);
                        COMMON.LOCAL.eform.consent = consents;
                        if (consents.length > 0) { 
                            if (getLocalStorage("signPwd") == "" || getLocalStorage("signPwd") == null || getLocalStorage("signPwd") == undefined) {
                                $('#sign_popup').css('display', 'block');
                                $('#_certpassword').focus();
                                check_consent = $(this);
                            } else {
                                searchAppVersion();
                            }

                        } else {
                            alert("선택한 동의서가 없습니다.\n동의서를 선택해주세요.");
                        }
                    }
                }

            });
            $("#_consentAllList").append(li);
        }
    }
};

// 재작성 버튼 추가
function addRewriteBtn(li) {
    var rewriteBtn = "<span class='list_btn _consentRewrite'><button type='button' class='btn-mod-agree'><span>수정</span></button></span>";
    li.find(".list_notice").append(rewriteBtn);
    li.find("._consentRewrite").off().on("click", function () {
        var data = $(this).parent().attr("attr-data");
        //console.log("[재작성] 해당 동의서 정보 검색 : " + data);
        if (data != "") {
            var consentInfo = JSON.parse(data);
            var consents = [];
            consentInfo.rewriteConsentMstRid = consentInfo.ConsentMstRid;
            consents[consents.length] = consentInfo;
            COMMON.LOCAL.eform.consent = consents;
            if (consents.length > 0) {

                searchAppVersion();
                /*if (getLocalStorage("signvalue") == "" || getLocalStorage("signvalue") == null || getLocalStorage("signvalue") == undefined) {
                	$('#sign_popup').css('display', 'block');
                	$('#_certpassword').focus();
                	check_consent = $(this);
                } else {
                	searchAppVersion();
                }
                */
            }
        }
    });
};

// 녹취 버튼 추가
function addRecordBtn(li, type) {
    var recordBtn = "<span class='btn_flow flow_record _consentRecord'>녹취</span>";
    li.find(".list_notice").append(recordBtn);
    li.find("._consentRecord").off().on("click", function () {
        var data = $(this).parent().attr("attr-data");
        //console.log("[녹취] 해당 동의서 정보 검색 : " + data);
        if (data != "") {
            var consentInfo = JSON.parse(data);
            var consents = [];
            consentInfo.rewriteConsentMstRid = consentInfo.ConsentMstRid;
            consents[consents.length] = consentInfo;
            COMMON.LOCAL.eform.consent = consents;
            if (consents.length > 0) {
                var detailPatient = JSON.parse($(".patient_info").attr("attr-data"));
                if (isViewePossibility(detailPatient)) {
                    var consents = COMMON.LOCAL.eform.consent;
                    var consentNames = "";
                    for (var i = 0; i < consents.length; i++) {
                        var consent = null;
                        if (jQuery.type(consents[i]) === "string") {
                            consent = JSON.parse(consents[i]);
                        } else {
                            consent = consents[i];
                        }
                        if (consent.FormName == undefined) {
                            consentNames += (consentNames == "") ? consent.ConsentName : ",\n" + consent.ConsentName;
                        } else {
                            consentNames += (consentNames == "") ? consent.FormName : ",\n" + consent.FormName;
                        }
                    }
                    if (confirm(consentNames + "\n\n선택한 동의서 [" + consents.length + "]개를 녹음 재생으로 열겠습니까?")) {
                        COMMON.LOCAL.record.isRecord = true;
                        COMMON.plugin.storage("get", "isCertDown", isCertDown);
                    }
                }
            }
        }
    });
};

// 앱 버전 정보
function searchAppVersion() {
	//alert('2');	// will be deleted
    var detailPatient = JSON.parse($(".patient_info").attr("attr-data"));
    // if (isViewePossibility(detailPatient)) { 
    var consents = COMMON.LOCAL.eform.consent;
    var consentNames = "";
    for (var i = 0; i < consents.length; i++) {

        var consent = null;
        if (jQuery.type(consents[i]) === "string") {
            consent = JSON.parse(consents[i]);
        } else {
            consent = consents[i];
        }
        if (consent.cosignYn == "Y") {
            var div_clone = $('#Consent_List_clone').clone();

            div_clone.find('.Consent_Title').text('');
            div_clone.find('.Consent_Title').text(consent.FormName);
            div_clone.addClass('_cosignBigList');
            div_clone.find('#_CosignDeptList').addClass('_CosignDeptValue');

            if (consent.cosignDeptCode != undefined) {
                div_clone.find('#_CosignDeptList option').each(function () {
                    if ($(this).val() == consent.cosignDeptCode) {
                        $(this).attr("selected", "selected");
                        $(this).parent().parent().find("#_CosignWardList").prop('disabled', true);

                    }
                });
                div_clone.find('#_CosignWardList option').each(function () {
                    if ($(this).val() == consent.cosignDeptCode) {
                        $(this).attr("selected", "selected");
                        $(this).parent().parent().find("#_CosignDeptList").prop('disabled', true);

                    }
                });
            }

            div_clone.find("#_CosignDeptList").on("change", function () {
                if ($(this).find('option:selected').val() == "") {
                    $(this).parent().find("#_CosignWardList").prop('disabled', false);
                    $(this).parent().find('.cosignDeptValue').val($(this).find('option:selected').val());
                } else {
                    $(this).parent().find("#_CosignWardList").prop('disabled', true);
                    $(this).parent().find('.cosignDeptValue').val($(this).find('option:selected').val());
                }
            });
            div_clone.find("#_CosignWardList").on("change", function () {
                if ($(this).find('option:selected').val() == "") {
                    $(this).parent().find("#_CosignDeptList").prop('disabled', false);
                    $(this).parent().find('.cosignDeptValue').val($(this).find('option:selected').val());
                } else {
                    $(this).parent().find("#_CosignDeptList").prop('disabled', true);
                    $(this).parent().find('.cosignDeptValue').val($(this).find('option:selected').val());
                }
            });

            div_clone.attr("attr-data", JSON.stringify(consent));
            $('#Consent_List_Big_div').append(div_clone);
            if (consent.FormName == undefined) {
                consentNames += (consentNames == "") ? consent.ConsentName : ",\n" + consent.ConsentName;
            } else {
                consentNames += (consentNames == "") ? consent.FormName : ",\n" + consent.FormName;
            }
            $('.Consent_List').css('display', 'block');
            $('.dimmed').css('display', 'block');
            //console.log("버전 ::: " + JSON.stringify(consent));


        } else {
            COMMON.plugin.storage("get", "isCertDown", isCertDown);
        }


    }
};

function searchAppVersionCosign(obj, type) {
    var detailPatient = obj;
    //JSON.parse($(".patient_info").attr("attr-data"));
    // if (isViewePossibility(detailPatient)) {
    var consents = COMMON.LOCAL.eform.consent;
    var consentNames = "";
    for (var i = 0; i < consents.length; i++) {
        var consent = null;
        if (jQuery.type(consents[i]) === "string") {
            consent = JSON.parse(consents[i]);
        } else {
            consent = consents[i];
        }
        if (type == "CS") { // 코사인 송신탭 
            COMMON.plugin.loadEFormViewByGuid("cosignSend", "VIEWER_FORM", COMMON.LOCAL.eform.consent, obj);
        }else {
        	if(consent.cosignFlag=="3"){ 
        	      var attr_data = new Object();
        	       attr_data = COMMON.LOCAL.eform.consent;   
	       	       //alert("1 : "+JSON.stringify(attr_data));
	    	       //alert("2 : "+JSON.stringify(COMMON.LOCAL.eform.consent));
        	       var fakeObj = new Object();
        	       fakeObj.fake = "fake";
        	       
        	       COMMON.LOCAL.eform.consent = attr_data;
        	       if (attr_data.cosignFlag == "0") { //   
        	           COMMON.plugin.storage("get", "isCertDown", isCertDown);
        	       } else { // 코사인탭,작성동의서 빠른 조회
        	           if (attr_data.nursCertYn == "N" && localStorage.getItem("jobkindcd").substring(0, 2) != "03") {
        	               COMMON.plugin.loadEFormViewByGuid("nurscertTemp", "VIEWER_FORM", COMMON.LOCAL.eform.consent, fakeObj);
        	           } else {
        	               COMMON.plugin.loadEFormViewByGuid("temp", "VIEWER_FORM", COMMON.LOCAL.eform.consent, fakeObj);
        	           } 
        	       } 
            }else{
            	var div_clone = $('#Consent_List_clone').clone(); 
	            div_clone.find('.Consent_Title').text('');
	            div_clone.find('.Consent_Title').text(consent.FormName);
	            div_clone.addClass('_cosignBigList');
	            div_clone.find('#_CosignDeptList').addClass('_CosignDeptValue');
	
	            div_clone.find("#_CosignDeptList").on("change", function () {
	                if ($(this).find('option:selected').val() == "") {
	                    $(this).parent().find("#_CosignWardList").prop('disabled', false);
	                    $(this).parent().find('.cosignDeptValue').val($(this).find('option:selected').val());
	
	                } else {
	                    $(this).parent().find("#_CosignWardList").prop('disabled', true);
	                    $(this).parent().find('.cosignDeptValue').val($(this).find('option:selected').val());
	                }
	            });
	            div_clone.find("#_CosignWardList").on("change", function () {
	                if ($(this).find('option:selected').val() == "") {
	                    $(this).parent().find("#_CosignDeptList").prop('disabled', false);
	                    $(this).parent().find('.cosignDeptValue').val($(this).find('option:selected').val());
	                } else {
	                    $(this).parent().find("#_CosignDeptList").prop('disabled', true);
	                    $(this).parent().find('.cosignDeptValue').val($(this).find('option:selected').val());
	                }
	            });
	
	            div_clone.attr("attr-data", JSON.stringify(consent));
	            $('#Consent_List_Big_div').append(div_clone);
	
	            if (consent.FormName == undefined) {
	                consentNames += (consentNames == "") ? consent.ConsentName : ",\n" + consent.ConsentName;
	            } else {
	                consentNames += (consentNames == "") ? consent.FormName : ",\n" + consent.FormName;
	            }
	            $('.Consent_List').css('display', 'block');
	            $('.dimmed').css('display', 'block');
	            //console.log("버전 ::: " + JSON.stringify(consent));
            }
        }

    }
}


function searchAppVersionMyConsent(obj, type) {
    var detailPatient = obj;
    var consents = COMMON.LOCAL.eform.consent;
    var consentNames = "";

    for (var i = 0; i < consents.length; i++) {

        var consent = null;
        if (jQuery.type(consents[i]) === "string") {
            consent = JSON.parse(consents[i]);
        } else {
            consent = consents[i];
        }
        if (consent.cosignYn == "Y" && consent.consentStateDisp != "완료") {
            var div_clone = $('#Consent_List_clone').clone();

            div_clone.find('.Consent_Title').text('');
            div_clone.find('.Consent_Title').text(consent.formName);
            div_clone.addClass('_cosignBigList');
            div_clone.find('#_CosignDeptList').addClass('_CosignDeptValue');

            if (consent.cosignDeptCode != undefined) {
                div_clone.find('#_CosignDeptList option').each(function () {
                    if ($(this).val() == consent.cosignDeptCode) {
                        $(this).attr("selected", "selected");
                        $(this).parent().parent().find("#_CosignWardList").prop('disabled', true);

                    }
                });
                div_clone.find('#_CosignWardList option').each(function () {
                    if ($(this).val() == consent.cosignDeptCode) {
                        $(this).attr("selected", "selected");
                        $(this).parent().parent().find("#_CosignDeptList").prop('disabled', true);

                    }
                });
            }

            div_clone.find("#_CosignDeptList").on("change", function () {
                if ($(this).find('option:selected').val() == "") {
                    $(this).parent().find("#_CosignWardList").prop('disabled', false);
                    $(this).parent().find('.cosignDeptValue').val($(this).find('option:selected').val());

                } else {
                    $(this).parent().find("#_CosignWardList").prop('disabled', true);
                    $(this).parent().find('.cosignDeptValue').val($(this).find('option:selected').val());
                }
            });
            div_clone.find("#_CosignWardList").on("change", function () {
                if ($(this).find('option:selected').val() == "") {
                    $(this).parent().find("#_CosignDeptList").prop('disabled', false);
                    $(this).parent().find('.cosignDeptValue').val($(this).find('option:selected').val());
                } else {
                    $(this).parent().find("#_CosignDeptList").prop('disabled', true);
                    $(this).parent().find('.cosignDeptValue').val($(this).find('option:selected').val());
                }
            });

            div_clone.attr("attr-data", JSON.stringify(consent));
            $('#Consent_List_Big_div').append(div_clone);

            if (consent.FormName == undefined) {
                consentNames += (consentNames == "") ? consent.ConsentName : ",\n" + consent.ConsentName;
            } else {
                consentNames += (consentNames == "") ? consent.FormName : ",\n" + consent.FormName;
            }
            $('.Consent_List').css('display', 'block');
            $('.dimmed').css('display', 'block');
            //console.log("버전 ::: " + JSON.stringify(consent));


        } else {
            COMMON.plugin.loadEFormViewByGuid(type, "VIEWER_FORM", COMMON.LOCAL.eform.consent, obj);
            //COMMON.plugin.storage("get", "isCertDown", isCertDown);
        }



        //        if (type == "temp") {
        //            COMMON.plugin.loadEFormViewByGuid("temp", "VIEWER_FORM", COMMON.LOCAL.eform.consent, obj);
        //        } else if (type == "end") {
        //            COMMON.plugin.loadEFormViewByGuid("end", "VIEWER_FORM", COMMON.LOCAL.eform.consent, obj);
        //        } else if (type == "endAddDoc") {
        //        }
    }
}

function searchAppEndVersion() {
    var detailPatient = JSON.parse($(".patient_info").attr("attr-data"));
    // if (isViewePossibility(detailPatient)) {
    var consents = COMMON.LOCAL.eform.consent;
    var consentNames = "";
    for (var i = 0; i < consents.length; i++) {
        var consent = null;
        if (jQuery.type(consents[i]) === "string") {
            consent = JSON.parse(consents[i]);
        } else {
            consent = consents[i];
        }
        if (consent.FormName == undefined) {
            consentNames += (consentNames == "") ? consent.ConsentName : ",\n" + consent.ConsentName;
        } else {
            consentNames += (consentNames == "") ? consent.FormName : ",\n" + consent.FormName;
        }
        //console.log("버전 ::: " + JSON.stringify(consent));
    }
    COMMON.plugin.storage("get", "isCertDown", isCertDown);
    //if (confirm(consentNames + "\n\n선택한 동의서 [" + consents.length + "]개를 뷰어로  열겠습니까?"))
    // {  COMMON.plugin.storage("get", "isCertDown", isCertDown);} 
};

// 앱 버전 정보 검색
function searchAppVersionSuccessHandler(resData) {
    if (!$.isEmptyObject(resData)) {
        var appInfo = resData[0];
        var clientVersion = appInfo.ConsentClientVersion;
        var serverVersion = appInfo.ConsentServerVersion;

        if (Number(clientVersion) < Number(serverVersion)) {
            alert("전자동의서 업데이트가 있습니다.\n업데이트 후 사용 할 수 있습니다.\n자동 로그아웃 됩니다.");
            COMMON.plugin.storage("delete", "", null);
            var index = 1 - Number(history.length);
            history.go(index);
        } else {
            var detailPatient = JSON.parse($(".patient_info").attr("attr-data"));
            if (isViewePossibility(detailPatient)) {
                var consents = COMMON.LOCAL.eform.consent;
                var consentNames = "";
                for (var i = 0; i < consents.length; i++) {
                    var consent = null;
                    if (jQuery.type(consents[i]) === "string") {
                        consent = JSON.parse(consents[i]);
                    } else {
                        consent = consents[i];
                    }
                    if (consent.FormName == undefined) {
                        consentNames += (consentNames == "") ? consent.ConsentName : ",\n" + consent.ConsentName;
                    } else {
                        consentNames += (consentNames == "") ? consent.FormName : ",\n" + consent.FormName;
                    }
                    //console.log("버전 ::: " + JSON.stringify(consent));
                }
                if (confirm(consentNames + "\n\n선택한 동의서 [" + consents.length + "]개를 뷰어로 열겠습니까?")) {
                    COMMON.plugin.storage("get", "isCertDown", isCertDown);
                }
            }
        }
    }
};

function isViewePossibility(detailPatient) {
    var result = true;
    var message = "";
    if (detailPatient.PatientCode == undefined || detailPatient.PatientCode == null || detailPatient.PatientCode == "") {
        result = false;
        message = "해당 환자의 등록코드 정보가 없습니다.";
    } else if (detailPatient.ClnDeptCode == undefined || detailPatient.ClnDeptCode == null || detailPatient.ClnDeptCode == "") {
        result = false;
        message = "해당 환자의 진료과 정보가 없습니다.";
    } else if (detailPatient.VisitType == "I" && (detailPatient.AdmissionDate == undefined || detailPatient.AdmissionDate == null || detailPatient.AdmissionDate == "")) {
        result = false;
        message = "해당 환자의 입원일 정보가 없습니다.";
    } else if (detailPatient.VisitType == "O" && (detailPatient.ClinicalDate == undefined || detailPatient.ClinicalDate == null || detailPatient.ClinicalDate == "")) {
        result = false;
        message = "해당 환자의 진료일 정보가 없습니다.";
    }
    if (message != "") {
        message += "\n전자동의서 뷰어로 연결 할 수 없습니다.";
        alert(message);
    }
    return result;
}

// 전자동의서 다운로드 여부 확인
function isCertDown(result) {
    /*
     * if(result == "ok"){
     */
    consentOnClickEvent();
    /*
     * }else{ if(INTERFACE.USER != ""){ $("#_certPw").val("password"); }
     * popupShow("popup-pwd"); }
     */
}

// 동의서 리스트 클릭 이벤트
function consentOnClickEvent() {
	//alert('1');	// will be deleted
    popupHide("popup-pwd");
    // 연속 출력이 있으므로 공통된 환자와 사용자 정보만 설정하고 서식과 관련된 정보는 네이티브에서 처리함.
    var consent = COMMON.LOCAL.eform.consent[0]; // 동의서 정보
    var detailPatient = JSON.parse($(".patient_info").attr("attr-data"));
    var params = {
        "userId": getLocalStorage("userId"), // 사용자 ID
        "modifyUserDeptCode": getLocalStorage("userDeptCode"), // 사용자 과 Code
        "patientCode": detailPatient.PatientCode, // 환자 등록번호
        "clnDeptCode": detailPatient.ClnDeptCode, // 진료과 코드
        "formRid": "", // 서식 RID(Native에서 적용)
        "formCd": consent.FormCd, // 서식 코드(Native에서 적용)
        "consentMstRid": consent.ConsentMstRid, // 동의서 Id (default : -1일 경우 신규)
        "rewriteConsentMstRid": "0",
        "deviceType": "AND", // 장비 유형 (WIN, AND, IOS, PRT)
        "deviceIdentNo": "", // 장비 식별자(Native에서 적용)
        "vistType": detailPatient.VisitType, // 외래(O) or 입원(I) 구분
        "hosType": "ARUM", // 병원구분 ARUM으로 고정
        "clnDate": (detailPatient.VisitType == "I") ? detailPatient.AdmissionDate : detailPatient.ClinicalDate,
        "ward": detailPatient.Ward, // 병동 코드(Native에서 적용)
        "orderDiv": consent.OrderYn, // 처방구분(Native에서 적용)
        "orderDate": consent.OrderDate, // 처방일(Native에서 적용)
        "orderName": consent.OrderName, // 처방명(Native에서 적용)
        "orderCd": consent.OrderCd, // 처방코드(Native에서 적용)
        "createUserId": getLocalStorage("userId"), // 작성자명
        "createUserName": getLocalStorage("userName"), // 작성자명
        "modifyUserId": getLocalStorage("userId"), // 수정자명
        "modifyUserName": getLocalStorage("userName"), // 수정자명
        "recordFileJson": "", // 녹취 파일 Path(Native에서 적용)
        "useCase": "EMR", // 업무 구분 (모바일 : EMR로고정)
        "imageFileJson": "", // 동의서 
        // "certTarget" : "", // 동의서 저장시에만 쓰이는 서명데이터 원본 (native에서 추가)/
        // "certResult": "", // 동의서 저장시에만 쓰이는 서명 데이터 결과 (native에서 추가)*/
    };
    var obj = {
        "serviceName": "ConsentSvc.aspx",
        "methodName": "",
        "params": params,
        // "detail" : detailPatient//sub_data,
        "detail": JSON.parse($(".patient_info").attr("attr-data"))
    };

    // 완료 동의서일 경우  
    if (consent.ConsentState == "ELECTR_CMP" ) { 
        var cosent_obj = new Object();
        if (typeof consent === 'object') {
            cosent_obj = consent;
        } else {
            cosent_obj = JSON.parse(consent);
        }
        if (consent.opdrYn == "Y" && consent.opdrSignYn == "N") {
            if (cosent_obj.nursCertYn == "N" && localStorage.getItem("jobkindcd").substring(0, 2) != "03") {
                localStorage.setItem("ConsentType", "nurscertEnd");
            } else {
                localStorage.setItem("ConsentType", "endAddDoc");
            }
        } else {
            if (cosent_obj.nursCertYn == "N" && localStorage.getItem("jobkindcd").substring(0, 2) != "03") {
                localStorage.setItem("ConsentType", "nurscertEnd");
            } else {
                localStorage.setItem("ConsentType", "end");
            }
        }
        makeCommmonParamForEform();
    } else {
        // 임시 동의서 
        if (consent.ConsentState == "TEMP" || consent.ConsentState == "ELECTR_TEMP") {
            if (consent.writeConsentCosign == "true") {
                COMMON.LOCAL.record.type = "end"
                localStorage.setItem("ConsentType", "cosignTemp");
                makeCommmonParamForEform();
            } else { 
                var cosent_obj = new Object();
                if (typeof consent === 'object') {
                    cosent_obj = consent;
                } else {
                    cosent_obj = JSON.parse(consent);
                } 
                if (cosent_obj.nursCertYn == "N" && localStorage.getItem("jobkindcd").substring(0, 2) != "03") {
                    localStorage.setItem("ConsentType", "nurscertTemp");
                } else {
                    COMMON.LOCAL.record.type = "temp"
                    localStorage.setItem("ConsentType", "temp");
                } 
                makeCommmonParamForEform();
            }
        }
        // 신규 동의서
        else {       	

            var cosent_obj = new Object();
            if (typeof consent === 'object') {
                cosent_obj = consent;
            } else {
                cosent_obj = JSON.parse(consent);
            }
            
            
            COMMON.LOCAL.eform.consentSave = obj; 
            if (localStorage.getItem("jobkindcd").substring(0, 2) == "03") {
            	if (cosent_obj.nowrite == "true") {
            		localStorage.setItem("ConsentType", "nowrite");
            	} else {
            		localStorage.setItem("ConsentType", "new");
            	}
            } else {
                if (getLocalStorage("nurscertAndDoctor") == "true") {
                    localStorage.setItem("nurscertAndDoctor", "false"); 
                    localStorage.setItem("ConsentType", "nurscertNew");
                } else {
                    if (cosent_obj.nurscert_yn == "N") { 
                        localStorage.setItem("ConsentType", "nurscertNew");
                    } else {
                        if (cosent_obj.nowrite == "true") {
                            localStorage.setItem("ConsentType", "nowrite");
                        } else {
                            localStorage.setItem("ConsentType", "new");
                        }
                    }
                }
            } 
            makeCommmonParamForEform();
        }
    }
}

// 서식 공통 파라미터 필드
function makeCommmonParamForEform() {
    var patient = COMMON.LOCAL.eform.patient;
    var consent = COMMON.LOCAL.eform.consent[0]; // 동의서 정보
    var docName = "";
    var url = "";
    if (localStorage.getItem("jobkindcd").substring(0, 2) == "03") {
        docName = getLocalStorage("userName");
        url = "http://emr.yjh.com/cmcnu/webapps/mr/mr/formmngtweb/.live?submit_id=DRMRF02317&business_id=mr&userid=" + getLocalStorage("userId");
    }
     
    var viewerCommonParams = { 
            "certPwd": getLocalStorage("signPwd"),
            "I_DEVICE_TYPE": "AND", // 접근 기기 구분 (모바일에서는 AND로 고정)
            // ,"I_FORM_CD" : "" // 동의서 서식 코드 : 서식 정보
            // ,"I_OCR_FORM_CD" : "" // OCR 서식코드 : 서식 정보
            // ,"I_FORM_NUMBER" : "" // 서식지번호 : 서식 정보
            "ophngnm": patient.opnm,
            "opdrnm": patient.perfdrnm,
            "pid": patient.PatientCode, // 환자 등록번호 // 서식지번호 : 서식 정보
            "patientCode": patient.PatientCode, // 환자 등록번호
            "formnm": consent.FormPrntNm, // 
            "formnm1": consent.FormPrntNm, // 
            "formminnm": consent.FormName, // 
            "formId": consent.FormId,
            "PATNM": patient.PatientName, // 환자명
            "telno": patient.mpphontel, // 환자 전화번호
            "mpphontel": patient.mpphontel, // 환자 휴대폰 ㅆ전화번호
            "sexkor": patient.Sex, // 환자 성별
            "sex": patient.Sex, // 환자 성별
            "sa": patient.Sex + "/" + patient.Age, // 환자 나이
            "brthdd": patient.Birthday, // 환자 생년월일
            "zipnm": patient.zipnm, // 환자 주소 
            "addr": patient.zipnm, // 환자  집주소
            "visitType": patient.VisitType, // 내원 구분
            "rrgstno": patient.fulrgstno, // 주민등록번호co
            "fulrgstno": patient.fulrgstno, // 주민등록번호
            "rrgstfullno": patient.rrgstfullno, // 주민등록번호
            "docYN": "Y", // getLocalStorage("docYN")
            "orddd": patient.AdmissionDate.substring(0, 4) + "/" + patient.AdmissionDate.substring(4, 6) + "/" + patient.AdmissionDate.substring(6, 8), // 진료  일자  or  입원일자
            "ordddDate": patient.AdmissionDate, // 진료  일자  or  입원일자
            "ORDDEPTCD": patient.deptengabbr, // 진료과 명(파라미터매핑을위함)
            "ordDeptCds": patient.ClnDeptCode, // 진료과 코드
            // "ORDDEPTCD": patient.ClnDeptNum, // 진료과 번호
            "ORDDEPTNM": patient.ClnDeptName, // 진료과 명
            "diagcd": patient.maindiagcd, // 진단명 코드.
            "diaghngnm": patient.diagnm, // 진단명
            "diagengnm": patient.diagengnm, // 진단명
            "ROOMCD": patient.Ward, // 병실
            "I_ROOM": patient.Room, // 병동
            "orddrid": patient.DoctorId, // 진료의 ID
            "orddrnm": patient.DoctorName, // 진료의 명
            "atdoctid": patient.ChargeId, // 주치의 ID
            "atdoctname": patient.ChargeName, // 주치의 명
            "atdoctnm": docName, //  (로그인사용자명)
            "maindocnm": patient.medispclnm,
            "userId": getLocalStorage("userId"), // 사용자 ID
            "usernm": getLocalStorage("userName"), // 사용자명
            "userDeptCd": getLocalStorage("userDeptCode"), // 사용자 부서코드
            "userDeptNm": getLocalStorage("userDeptName"), // 사용자 부서명
            // "I_PRINT_CNT" : "", // 출력횟수 : 서식 정보
            // "I_USE_CASE" : "", // 업무구분 (모바일에서는 사용하지 않음)
            "I_DEFAULT_POPUP_URL": "", // 서식 팝업 URL : 서식 정보
            "I_VISIT_TYPE_NUMBER": patient.VisitTypeNum, // 내원 구분 숫자
            "I_BEDNO": patient.Bedno, // 환자 침대번호 
            "I_OP_DATE": patient.OperationDate,
            "logo_imge": "http://emr.yjh.com/cmcnu/webapps/images/report/biglogo204.png",
            //"logo_imge": "http://emr.yjh.com/cmcnu/webapps/images/report/biglogo013.png",
            "docSignImgUrl": url,
            "certTarget": consent.FormId,
            "certResult": getLocalStorage("sign"),
            "Cretno": patient.Cretno,
            "wardCd": patient.wardcd,
            "LicenceNo": localStorage.getItem("licnsno"),
            "barcode": consent.ocrTag,
            "treatmentnm": localStorage.getItem("treatmentnm"),
            "jobkindcd" : localStorage.getItem("jobkindcd"),
            "medispclno": localStorage.getItem("medispclno"),
            "ordfild": localStorage.getItem("ordfild"),
            "depthngnm": localStorage.getItem("depthngnm") 
        };
    var patientIdx = 0;
    for(var key in patient){

    	patientIdx = patientIdx+1;
    	if(patientIdx >= 27){
    		viewerCommonParams[key] = patient[key];
    	} 
    } 
    // 2017.06.21 공용 파라메터 자동매핑
    if (patient.ParamCommonJson != undefined && patient.ParamCommonJson != "" && patient.ParamCommonJson != null) {
        if (!$.isEmptyObject(JSON.parse(patient.ParamCommonJson))) {
            var commonJson = JSON.parse(patient.ParamCommonJson);
            $.each(commonJson, function (key, value) {
                viewerCommonParams[key] = value;
            });
        }
    }

    // 2017.08.09 경고 메시지 추가
    var warningMessage = getLocalStorage("ParamJson")
    //console.log("warningMessage : " + warningMessage);
    if (warningMessage != undefined && warningMessage != "" && warningMessage != null && warningMessage != "undefined") {
        if (!$.isEmptyObject(JSON.parse(warningMessage))) {
            var paramJson = JSON.parse(warningMessage);
            $.each(paramJson, function (key, value) {
                //console.log("[key : " + key + " |" + "value : " + value + "]");
                viewerCommonParams[key] = value;
            });
        }
    }

    // 전자동의서 공통 컬럼셋팅
    COMMON.LOCAL.eform.consentSave.detail = JSON.parse($(".patient_info").attr("attr-data"));

    COMMON.LOCAL.eform.consentSave.patient = viewerCommonParams;
    // 동의서 정보 확인
    //console.log("================ [ 동의서 정보 확인 ] ===========================");
    //console.log("서식파라메타정보 : " + JSON.stringify(COMMON.LOCAL.eform.consentSave.patient));
    console.log("서식정보 : " + COMMON.LOCAL.eform.consent);
    //console.log("환자상세정보 : yt" + JSON.stringify(COMMON.LOCAL.eform.consentSave.detail));
    //console.log("======================================================");
    // 

    //console.log("이것이다 : " + JSON.stringify(COMMON.LOCAL.eform.consentSave));
    COMMON.plugin.loadEFormViewByGuid(getLocalStorage("ConsentType"), "VIEWER_FORM", COMMON.LOCAL.eform.consent, COMMON.LOCAL.eform.consentSave);
}

// 동의서 상태 조회 ConsentState() {
function searchGetConsentState() {
    var paramObject = {
        "consentMstRid": COMMON.LOCAL.eform.consent[0].ConsentMstRid
    };

    // 공통 변수
    var args = {
        "sCode": "GetConsentState",
        "param": paramObject,
        "userId": getLocalStorage("userId"),
        "patientCode": COMMON.LOCAL.eform.patient.PatientCode,
        "reqType": "webserive",
        "serviceName": "ConsentSvc.aspx"
    };
    var reqSetting = COMMON.util.makeReqParam(args);
    COMMON.plugin.doRequest(reqSetting, searchGetConsentStateSuccessHandler, errorHandler);
};

// 동의서 상태 조회 결과
function searchGetConsentStateSuccessHandler(resData) {
    var state = resData;
    var currentState = COMMON.LOCAL.eform.consent[0].ConsentState;
    if (!$.isEmptyObject(state)) {
        //console.log(state);
        if (state == currentState) {
            consentStatComparison();
        } else {
            var message = "";
            if (state == "DELETE") {
                message = "해당 동의서는 삭제되었습니다.\n리스트가 재조회 됩니다.";
                $("#_consentList li").remove();
                fnAllSearchConsent();
            } else {
                message = "해����� 동의서의 상태가 변경되었습니다.\n리스트가 재조회 됩니다.";
                $("#_consentList li").remove();
                fnAllSearchConsent();
            }
            alert(message);
        }
    }
};

// -----------------functions---------------------------------
// 공통 초기화
function commonInit() {
    // 접기 펼치기 초기화
    $(".btn-toggle-form").removeClass("close");
    $(".btn-toggle-form").parent().siblings(".form_section").removeClass("hide");
    $(".btn-toggle-form").parent().siblings(".form_section1").removeClass("hide");
    $(".btn-toggle-form").parent().siblings(".form_section2").removeClass("hide");
    $(".btn-toggle-form").parent().siblings(".form_section3").removeClass("hide");
    $(".btn-toggle-form").parent().siblings(".form_section4").removeClass("hide");
    $(".btn-toggle-form").parent().siblings(".form_section5").removeClass("hide");


    // 환자 검색 목록 초기화
    //	$("#_inPatientList ul").remove();
    //	$("#_outPatientList ul").remove();
    //	$("#_emergencyList ul").remove();
    //	$("#_operationList ul").remove();
    //	$("#_laboratoryList ul").remove();
    //	$("#_findList ul").remove();
    // 환자 상세 영역 초기화
    $("._patientDetailInfo").hide();
    $("._detail").text("");

    // 연관 영역 초기화
    $("#_relationConsentList li").remove();

    // 동의서 검색 영역 초기화
    $("#_consentList li").remove();
    $("#_consentAllList li").remove();

    // 동의서 검색 선택
    $(".agree_1").addClass("on");
    $(".agree_2").removeClass("on");
    $(".agree_1_section").removeClass("hide");
    $(".agree_2_section").addClass("hide");

    $("#_inPatientList ul").removeClass("on");
    $("#_outPatientList ul").removeClass("on");
    $("#_emergencyList ul").removeClass("on");
    $("#_operationList ul").removeClass("on");
    $("#_findList ul").removeClass("on");

    // $(":radio[name=_consentSearchType]").prop("checked", false);
    $("input:radio[name=_consentSearchType]:input[value='D']").prop("checked", true);
    $("#_efromClnDept").prop("disabled", false);
    selectboxDefalutSelected("_efromClnDept", getLocalStorage("userDeptCode"));
    selectboxDefalutSelected("_efromCmtClnDept", getLocalStorage("userPartCd"));
};

// 입원 - 환자조회 초기화
function inPatientInit() {
    commonInit();
    selectboxInit("_inPatientWard"); // 병동 초기화
    selectboxInit("_inPatientClnDept"); // 진료과 초기화
    $('#radio_tmp_12').trigger("click");
    $("#_inPatientCalendar").val(getDay("-", "", "", ""));
    // fnSearchCharge("");
    // fnSearchDoctor("");
    searchDoctor("");
    //searchCharge("");

    selectboxInit("_inPatientCharge"); // 담당의 초기화
    selectboxInit("_inPatientDoctor"); // 담당의사 초기화
};

function myConsentInit() {
    commonInit();
    $('#myConsentPatientCd').val('');
    selectboxInit("treateMentWard"); // 병동 초기화
    selectboxInit("treateMentDept"); // 진료과 초기화
    $('#treateMentWard').prop('disabled', false);
    $('#treateMentDept').prop('disabled', false);
    $("#_treatStartDate").val(getDay("-", "", "", -3));
    $("#_treatEndDate").val(getDay("-", "", "", ""));
    
    //2022-02-03
    $('#myConsentYn').prop('checked', false);
    $('#myWriteFlag').prop('checked', false);
    $('#ELECTR_VERBAL').prop('checked',false);
    $('#ELECTR_MULTI').prop('checked',false);    
    //=============================================
    
    $('#treatMentVisitTypeAll').prop('checked', true);
    $('#treatMentConsentAll').prop('checked', true);

    $('.treatMentConsent').empty();

}

// 외래 - 환자조회 초기
function outPatientInit() {
    commonInit();
    $("#_outPatientCalendar").val(getDay("-"));
    selectboxInit("_outPatientClnDept");
    $('#radio_tmp_12').trigger("click");
    $("#_outPatientDoctor option").remove();
    $("#_outPatientDoctor").append($('<option>', {
        value: '',
        text: '진료의'
    }));
};

// 응급 - 환자조회 초기
function emergencyInit() {
    commonInit();
    $('#radio_tmp_12').trigger("click");
    $("#_emergencyCalender").val(getDay("-", "", "", ""));
    selectboxInit("_emerVisitType");
    selectboxInit("_emrWriteState");
    selectboxInit("_emergencyDept");
};

// 수술 - 환자조회 초기
function operationInit() {
    commonInit();
    $('#radio_tmp_12').trigger("click");
    $("#_operationCalender").val(getDay("-", "", "", ""));
    selectboxInit("_operationClnDept");
    selectboxInit("_operationAnesthesiaType");
    selectboxInit("_operationOperationType");
};
// 검색 - 환자조회 초기
function findInit() {
    commonInit();
    $('#radio_tmp_12').trigger("click");
    $("#_findPatientCode").val("");
    $("#_findPatientName").val("");
    $("#_findCalender").val(getDay("-", "", "", ""));
    selectboxInit("_findDept");
    selectboxInit("_findVisitType");
    selectboxInit("_findWriteState");
    // 2022-02-03
    selectboxInit("_findVerbalMulti");    
};
// 의뢰 - 환자조회 초기 
function requestInit() {
    commonInit();
    selectboxInit("_requestDiag");
    selectboxInit("_requestReply");
    selectboxInit("_requestCheck");
    selectboxInit("_requestDept");
    selectboxInit("_requestDoc");
    $("#_requestCalender").val(getDay("-"));
    $("#_requestCalender_2").val(getDay("-"));
};



// 대메뉴 이벤트
function menuClickEvent(menuId) {
    var className = menuId;
    var userGroupCode = getLocalStorage("userGroupCode");
    var h3_tit = $("#h3_tit");
    var patient_info = $('#_detailPatientCode');

    if (menuId == "inPatient") {
        COMMON.LOCAL.page.isFirst = true;
        COMMON.LOCAL.page.isConsentFirst = true;
        //inPatientInit();
        //        if (!selectboxDefalutSelected("_inPatientClnDept", userGroupCode)) {
        //            userGroupCode = "";
        //        }
        var clnDept = $("#_inPatientClnDept option:selected").val();
        if (clnDept != "") {
            //searchDoctor(clnDept);
        }

        className = "admission";
        // 대메뉴 색상에 따라 디자인 변경
        h3_tit.attr('class', 'h3_tit inPatient');
        patient_info.css('color', '#144c9a');
        $("#_consentStartDate").val(getDay("-", "", "", -5));
        $("#_consentendDate").val(getDay("-"));
        $('.Consent_List').css('display', 'none');
        $(".cnts_1").hide();
        $(".cnts_2").show();
        $(".cnts_3").show();
        $('.cosignListBigLayout').css('display', 'none');
        $('#treateMentBigLayout').css('display', 'none');
    } else if (menuId == "outPatient") {
        //outPatientInit();
        //        if(!userDefaultSetting("dept", "_outPatientClnDept")){ 
        //userDefaultSetting("dept", "_outPatientDoctor")
        //        } 

        //        SearchOutDoctor("");
        //        // fnSearchOutDoctor(getLocalStorage("userGroupCode")); 
        //        if (!selectboxDefalutSelected("_outPatientClnDept", userGroupCode)) {
        //            userGroupCode = "";
        //        }

        var clnDept = $("#_outPatientClnDept option:selected").val();
        if (clnDept != "") {
            //SearchOutDoctor(clnDept);
        }
        className = "outPatient";
        // 대메뉴 색상에 따라 디자인 변경
        h3_tit.attr('class', 'h3_tit outPatient');
        $('.Consent_List').css('display', 'none');
        patient_info.css('color', '#144c9a');
        $(".cnts_1").hide();
        $(".cnts_2").show();
        $(".cnts_3").show();
        $('.cosignListBigLayout').css('display', 'none');
        $('#treateMentBigLayout').css('display', 'none');
    } else if (menuId == "emergency") {

        //emergencyInit();
        className = "emergency";
        // 대메뉴 색상에 따라 디자인 변경
        h3_tit.attr('class', 'h3_tit emergency');
        patient_info.css('color', '#144c9a');
        $('.Consent_List').css('display', 'none');
        // cosignOption("none");
        $(".cnts_1").hide();
        $(".cnts_2").show();
        $(".cnts_3").show();
        $('.cosignListBigLayout').css('display', 'none');
        $('#treateMentBigLayout').css('display', 'none');
    } else if (menuId == "operation") {
        //operationInit();
        //userDefaultSetting("dept", "_operationClnDept");
        className = "operation";
        // 대메뉴 색상에 따라 디자인 변경
        h3_tit.attr('class', 'h3_tit operation');
        $('.Consent_List').css('display', 'none');
        patient_info.css('color', '#144c9a');
        // cosignOption("none");
        $(".cnts_1").hide();
        $(".cnts_2").show();
        $(".cnts_3").show();
        $('.cosignListBigLayout').css('display', 'none');
        $('#treateMentBigLayout').css('display', 'none');
    } else if (menuId == "request") {
        //laboratoryInit();
        className = "request";
        // 대메뉴 색상에 따라 디자인 변경
        $('.Consent_List').css('display', 'none');
        h3_tit.attr('class', 'h3_tit request');
        patient_info.css('color', '#144c9a');
        // cosignOption("none");
        $(".cnts_1").hide();
        $(".cnts_2").show();
        $(".cnts_3").show();
        $('.cosignListBigLayout').css('display', 'none');
        $('#treateMentBigLayout').css('display', 'none');
    } else if (menuId == "find") {
        //findInit();
        //		$('.cosignListBigLayout').css('display','block');
        className = "search";
        // 대메뉴 색상에 따라 디자인 변경
        h3_tit.attr('class', 'h3_tit find');
        patient_info.css('color', '#144c9a');
        $('.Consent_List').css('display', 'none');
        $(".cnts_1").hide();
        $(".cnts_2").show();
        $(".cnts_3").show();
        $('.cosignListBigLayout').css('display', 'none');
        $('#treateMentBigLayout').css('display', 'none');
        // cosignOption("none");\
    } else if (menuId == "myConsent") {
        className = "myConsent";
        h3_tit.attr('class', 'h3_tit find');
        patient_info.css('color', '#144c9a');
        $('#treateMentBigLayout').css('display', 'contents');
        $('.cosignListBigLayout').css('display', 'none');
        if (getLocalStorage('treatementDate') != "") {
            $("#_treatStartDate").val(getDay("-", "", "", Number(getLocalStorage('treatementDate'))));
            $("#_treatEndDate").val(getDay("-", "", "", ""));
        }
        $(".cnts_1").hide();
        $(".cnts_2").hide();
        $(".cnts_3").hide();
        $('#treateMentBigLayout .cnts_1').css('display', 'block');
//        $("#_treatStartDate").val(getDay("-", "", "", -3));
//        $("#_treatEndDate").val(getDay("-", "", "", ""));
//    		} else {
    } else if (menuId == "laboratory") {  
        //emergencyInit();
        className = "laboratory";
        // 대메뉴 색상에 따라 디자인 변경
        h3_tit.attr('class', 'h3_tit laboratory');
        patient_info.css('color', '#144c9a');
        $('.Consent_List').css('display', 'none');
        // cosignOption("none");
        $(".cnts_1").hide();
        $(".cnts_2").show();
        $(".cnts_3").show();
        $('.cosignListBigLayout').css('display', 'none');
        $('#treateMentBigLayout').css('display', 'none');
    } else {
        $('.cosignListBigLayout').css('display', 'block');
        className = "cosign";
        // 대메뉴 색상에 따라 디자인 변경
        $('.Consent_List').css('display', 'none');
        h3_tit.attr('class', 'h3_tit find');
        patient_info.css('color', '#144c9a');
        $(".cnts_1").hide();
        $('#treateMentBigLayout').css('display', 'none');
        searchCosign();
    }
    $("#_menus li").removeClass("on");
    $("#_" + menuId).addClass("on");
    $("._" + menuId).show();
    commonInit();
};

function searchCosign() {
    var idx = 0;
    var SearchResultAry = new Array();
    var value = {
        //"patientCode": $("#_detailPatientCode").text() ,
        "searchMode": "cosign-receive",
        "userDeptCd": getLocalStorage("userDeptCode"),
        "userId": getLocalStorage("userId")
    }
 
    COMMON.plugin.wifiCheck(wifiCheckFn);

    var checkVal = localStorage.getItem("wifiCheckVal");
    if (checkVal < -90) { 
    	COMMON.plugin.loadingBar("hide", "");
        alert("WIFI 수신 감도가 좋지 않습니다. WIFI 통신이 잘 되는 곳에서 사용 바랍니다.");
    } else {
        //loadingbar_display();
    	COMMON.plugin.loadingBar("show", "코사인 수신 서식 검색 중입니다.");
        $.ajax({
                url: ajax_nu_url + '/biz/nu/member/viewer/eForm25/consent/mst/get',
                type: 'post',
                async: false,
                timeout: 40000,
                data: {
                    parameter: JSON.stringify(value)
                }
            }).done(function (data) { 
                $.each(data, function (index, item) { 
                    idx = idx + 1;
                    var DATA_JSON = JSON.parse(JSON.stringify(data[index]));
                    var SearchResult = new Object();
                    SearchResult.consentMstRid = DATA_JSON.consentMstRid;
                    SearchResult.hosType = DATA_JSON.hosType;
                    SearchResult.patientCode = DATA_JSON.patientCode;
                    SearchResult.patientName = DATA_JSON.patientName;
                    SearchResult.visitType = DATA_JSON.visitType;
                    SearchResult.clnDate = DATA_JSON.clnDate;
                    SearchResult.clnDeptCd = DATA_JSON.clnDeptCd;
                    SearchResult.WardCd = DATA_JSON.wardCd;
                    SearchResult.wardName = DATA_JSON.wardName;
                    SearchResult.roomCd = DATA_JSON.roomCd;
                    SearchResult.FormId = DATA_JSON.formId;
                    SearchResult.FormName = DATA_JSON.formName;
                    SearchResult.orderDate = DATA_JSON.orderDate;
                    SearchResult.orderCd = DATA_JSON.orderCd;
                    SearchResult.orderSeqNo = DATA_JSON.orderSeqNo;
                    SearchResult.orderName = DATA_JSON.orderName;
                    SearchResult.orderDiv = DATA_JSON.orderDiv;
                    SearchResult.ConsentState = DATA_JSON.consentState;
                    SearchResult.completeYn = DATA_JSON.completeYn;
                    SearchResult.completeDatetime = DATA_JSON.completeDatetime;
                    SearchResult.createUserId = DATA_JSON.createUserId;
                    SearchResult.createUserName = DATA_JSON.createUserName;
                    SearchResult.createUserDeptCd = DATA_JSON.createUserDeptCd;
                    SearchResult.createUserDeptName = DATA_JSON.createUserDeptName;
                    SearchResult.CreateDateTime = DATA_JSON.createDatetime;
                    SearchResult.modifyUserId = DATA_JSON.modifyUserId;
                    SearchResult.modifyUserName = DATA_JSON.modifyUserName;
                    SearchResult.modifyUserDeptCd = DATA_JSON.modifyUserDeptCd;
                    SearchResult.modifyUserDeptName = DATA_JSON.modifyUserDeptName;
                    SearchResult.modifyDatetime = DATA_JSON.modifyDatetime;
                    SearchResult.cosignYn = DATA_JSON.cosignYn;
                    SearchResult.FormVersion = DATA_JSON.formVersion;
                    SearchResult.cosignUserId = DATA_JSON.cosignUserId;
                    SearchResult.cosignUserName = DATA_JSON.cosignUserName;
                    SearchResult.cosignDeptCode = DATA_JSON.cosignDeptCode;
                    SearchResult.cosignDeptName = DATA_JSON.cosignDeptName;
                    SearchResult.formCd = DATA_JSON.formCd;
                    SearchResult.ocrTag = DATA_JSON.ocrTag;
                    SearchResult.certNo = DATA_JSON.certNo;
                    SearchResult.nursCertYn = DATA_JSON.nursCertYn;
                    SearchResult.userId = getLocalStorage("userId");
                    SearchResult.userName = getLocalStorage("userName");
                    SearchResult.userDeptCd = getLocalStorage("userDeptCode");
                    SearchResult.userDeptName = getLocalStorage("userDeptName");
                    SearchResult.jobkindcd = localStorage.getItem("jobkindcd");
                    SearchResult.certPwd = localStorage.getItem("signPwd");
                    SearchResult.certCnt = DATA_JSON.certCnt;
                    SearchResult.consent_certneedcnt = DATA_JSON.certNeedCnt;
                    SearchResult.licnsno = localStorage.getItem("licnsno");
                    SearchResult.ordfild = localStorage.getItem("ordfild");
                    SearchResult.depthngnm = localStorage.getItem("depthngnm");
                    SearchResult.medispclno  = localStorage.getItem("medispclno");
                    SearchResult.lifelong_kind = DATA_JSON.lifelongKind; 
                    // 수정부분
                    if(SearchResult.lifelong_kind == null){
                    	SearchResult.cosignFlag = "1";
                    }else{
                    	SearchResult.cosignFlag = "3";
                    }
                    
                    SearchResultAry.push(SearchResult);
                })
 
                searchCosignSuccessHandler(SearchResultAry, "C");  
                
                COMMON.plugin.loadingBar("hide", "");
            }).fail(function (xhr, status, errorThrown) {
            	COMMON.plugin.loadingBar("hide", "");
                alert("코사인 수신 조회 중 오류가 발생했습니다." + errorThrown)

            });
    }

    return idx;
}

function searchCosignSend() {
    var SearchResultAry = new Array();
    var value = {
        //"patientCode": $("#_detailPatientCode").text() ,
        "searchMode": "cosign-send",
        "userId": getLocalStorage("userId")
    }
    //			alert(JSON.stringify(value));
    COMMON.plugin.wifiCheck(wifiCheckFn);
    var checkVal = localStorage.getItem("wifiCheckVal");
    if (checkVal < -90) { 
    	COMMON.plugin.loadingBar("hide", "");
        alert("WIFI 수신 감도가 좋지 않습니다. WIFI 통신이 잘 되는 곳에서 사용 바랍니다.");
    } else {
        //loadingbar_display();
    	COMMON.plugin.loadingBar("show", "코사인 송신 서식 검색 중입니다.");
        $.ajax({
            url: ajax_nu_url + '/biz/nu/member/viewer/eForm25/consent/mst/get',
            type: 'post',
            timeout: 40000,
            data: {
                parameter: JSON.stringify(value)
            }
        }).done(function (data) { 
            $.each(data, function (index, item) {
                //							console.log(index+' : '+JSON.stringify(data[index])) 
                var DATA_JSON = JSON.parse(JSON.stringify(data[index]));
                var SearchResult = new Object();
                SearchResult.consentMstRid = DATA_JSON.consentMstRid;
                SearchResult.hosType = DATA_JSON.hosType;
                SearchResult.patientCode = DATA_JSON.patientCode;
                SearchResult.patientName = DATA_JSON.patientName;
                SearchResult.visitType = DATA_JSON.visitType;
                SearchResult.clnDate = DATA_JSON.clnDate;
                SearchResult.clnDeptCd = DATA_JSON.clnDeptCd;
                SearchResult.WardCd = DATA_JSON.wardCd;
                SearchResult.wardName = DATA_JSON.wardName;
                SearchResult.roomCd = DATA_JSON.roomCd;
                SearchResult.FormId = DATA_JSON.formId;
                SearchResult.FormName = DATA_JSON.formName;
                SearchResult.orderDate = DATA_JSON.orderDate;
                SearchResult.orderCd = DATA_JSON.orderCd;
                SearchResult.orderSeqNo = DATA_JSON.orderSeqNo;
                SearchResult.orderName = DATA_JSON.orderName;
                SearchResult.orderDiv = DATA_JSON.orderDiv;
                SearchResult.ConsentState = 'ELECTR_CMP'; //DATA_JSON.consentState;
                SearchResult.completeYn = DATA_JSON.completeYn;
                SearchResult.completeDatetime = DATA_JSON.completeDatetime;
                SearchResult.createUserId = DATA_JSON.createUserId;
                SearchResult.createUserName = DATA_JSON.createUserName;
                SearchResult.createUserDeptCd = DATA_JSON.createUserDeptCd;
                SearchResult.createUserDeptName = DATA_JSON.createUserDeptName;
                SearchResult.CreateDateTime = DATA_JSON.createDatetime;
                SearchResult.modifyUserId = DATA_JSON.modifyUserId;
                SearchResult.modifyUserName = DATA_JSON.modifyUserName;
                SearchResult.modifyUserDeptCd = DATA_JSON.modifyUserDeptCd;
                SearchResult.modifyUserDeptName = DATA_JSON.modifyUserDeptName;
                SearchResult.modifyDatetime = DATA_JSON.modifyDatetime;
                SearchResult.FormVersion = DATA_JSON.formVersion;
                SearchResult.cosignUserId = DATA_JSON.cosignUserId;
                SearchResult.cosignUserName = DATA_JSON.cosignUserName;
                SearchResult.cosignDeptCode = DATA_JSON.cosignDeptCode;
                SearchResult.cosignDeptName = DATA_JSON.cosignDeptName;
                SearchResult.cosignYn = DATA_JSON.cosignYn;
                SearchResult.formCd = DATA_JSON.formCd;
                SearchResult.ocrTag = DATA_JSON.ocrTag;
                SearchResult.certNo = DATA_JSON.certNo;
                SearchResult.userId = getLocalStorage("userId");
                SearchResult.userName = getLocalStorage("userName");
                SearchResult.userDeptCd = getLocalStorage("userDeptCode");
                SearchResult.userDeptName = getLocalStorage("userDeptName");
                SearchResult.jobkindcd = localStorage.getItem("jobkindcd");
                SearchResult.licnsno = localStorage.getItem("licnsno");
                SearchResult.ordfild = localStorage.getItem("ordfild");
                SearchResult.depthngnm = localStorage.getItem("depthngnm");
                SearchResult.medispclno  = localStorage.getItem("medispclno");
                SearchResult.certCnt = DATA_JSON.certCnt;
                SearchResult.consent_certneedcnt = DATA_JSON.certNeedCnt;
                SearchResult.lifelong_kind = DATA_JSON.lifelongKind;
                // 수정부분
                if(SearchResult.lifelong_kind == null){
                	SearchResult.cosignFlag = "1";
                }else{
                	SearchResult.cosignFlag = "3";
                } 
                
                SearchResultAry.push(SearchResult);
            })

            COMMON.plugin.loadingBar("hide", "");
            searchCosignSuccessHandler(SearchResultAry, "CS");
        }).fail(function (xhr, status, errorThrown) { 
        	COMMON.plugin.loadingBar("hide", "");
            alert("코사인 송신 조회 중 오류가 발생했습니다. 관리자에게 문의바랍니다. " + errorThrown)
        });
    }
}

// select box init
function selectboxInit(selectboxId) {
    $("#" + selectboxId + " option").attr("selected", "");
    $("#" + selectboxId + " option").eq(0).prop("selected", "selected");
    //console.log("[셀렉터 박스 초기화 : " + selectboxId + "] text : " + $("#" + selectboxId + " option").eq(0).text() + " / val: " + $("#" + selectboxId + " option").eq(0).val());
};

// 리스트 정렬
function patientListSort(listId, key, orderby) {

    COMMON.plugin.loadingBar("show", "정렬 중입니다.");
    localStorage.setItem("sortlistId", listId);
    localStorage.setItem("sortlistKey", key);
    localStorage.setItem("sortlistOrderby", orderby);
    //console.log("[리스트 정렬] List : " + listId + " / key : " + key);
    var org = $("#" + listId).children('ul').get();
    var list = $("#" + listId).children('ul').get();
    var count = 1;
    var column = key;
    if (listId == "_findList" && $("#_findVisitType option:selected").val() == "O" && key == "AdmissionTime") {
        column = "ClinicalDate";
    } 
    if (listId == "_myConsentList") {
        list = $('.treatMentConsent').children('div').get();
    }
    list.sort(function (a, b) {
        console.log("START " + count + "====================================");
        console.log("리스트 정렬 : " + list);
        console.log("리스트 정렬 : " + $(a).attr("attr-data"));
        console.log("리스트 정렬 : " + $(b).attr("attr-data"));
        var current = JSON.parse($(a).attr("attr-data"))[column];
        var next = JSON.parse($(b).attr("attr-data"))[column];

        if (column == "Bedno" || column == "OperationRoom") {
            if (current.length < 2)
                current = "0" + current;
            if (next.length < 2)
                next = "0" + next;
        }

        var result;
        if (orderby == "asc")
            result = (current < next) ? -1 : (current > next) ? 1 : 0;
        else
            result = (current > next) ? -1 : (current < next) ? 1 : 0;

//        console.log("[orderby : " + orderby + "] current : " + current + " / next : " + next + " => " + result);
//        console.log("END====================================");
        count = count + 1;
        return result;
    });
    $.each(list, function (index, row) {
        if (listId == "_myConsentList") {
            $('.treatMentConsent').append(row);
        } else {
            $("#" + listId).append(row);
        }

    });

    COMMON.plugin.loadingBar("hide", "정렬 중입니다.");
};

// localStorage get Item
function getLocalStorage(item) {
    //console.log("[getLocalStorage] " + item + " : " + localStorage.getItem(item));
    var value = "";
    if (localStorage.getItem(item) != null) {
        value = localStorage.getItem(item);
    }
    return value;
}

// 날짜 구하기
function getDay(formatType, adjustYaer, adjustMonth, dadjustDay) {
    var gap = 0;
    if (adjustYaer != "" && adjustYaer != null)
        gap = Number(gap + 365 * adjustYaer);
    if (adjustMonth != "" && adjustMonth != null)
        gap = Number(gap + 30 * adjustMonth);
    if (dadjustDay != "" && dadjustDay != null)
        gap = Number(gap + dadjustDay);

    var date = calDate(formatType, gap);

    console.log("[getDay] 날짜표기형식 : " + formatType + " / 현재일과 날짜 차이 : " + gap + " / 날짜 : " + date);
    return date;
}

// 날짜 구하기
function calDate(formatType, gapDay) {
    var caledmonth, caledday, caledYear;
    var toDay = new Date();
    var day = new Date(Date.parse(toDay) + gapDay * 1000 * 60 * 60 * 24);
    caledYear = day.getFullYear();
    if (day.getMonth() < 9)
        caledmonth = '0' + (day.getMonth() + 1);
    else
        caledmonth = day.getMonth() + 1;
    if (day.getDate() <= 9)
        caledday = '0' + day.getDate();
    else
        caledday = day.getDate();

    return caledYear + formatType + caledmonth + formatType + caledday;
}

// 검색 기간 유효 여부
function termOfValidity(startDay, endDay) {
    if (startDay != "" && startDay != undefined && endDay != "" && endDay != undefined) {
        var startDate = new Date(startDay);
        var endDate = new Date(endDay);
        if (startDate > endDate) {
            alert("검색 시작일이 종료일 보다는 클 수가 없습니다.");
            return false;
        } else {
            return true;
        }
    } else {
        if (startDay == "" || startDay == undefined)
            alert("검색 시작일을 설정하지 않았습니다.");
        else
            alert("검색 종료일을 설정하지 않았습니다.");

        return false;
    }
}

// list no Data
function makeNoDataHtml(listId) {
    var html = "";
    html += "<ul class='no_data'>";
    html += "	<li><span>조회된 결과가 없습니다.</span></li>";
    html += "</ul>";
    $("#" + listId).append(html);
}

// age 소수점 아래 버림
function ageFloor(age) {
    var result = "";
    if (age != undefined && age != "")
        result = Math.floor(age)
    return result;
};

// 셀렉트 박스 디폴트 값 선택하기
function selectboxDefalutSelected(selectboxId, target) {
    var result = false;
    var clnDepts = $("#" + selectboxId + " option");
    for (var i = 0; i < clnDepts.length; i++) {
        if (clnDepts.eq(i).val() == target) {
            clnDepts.eq(i).prop("selected", "selected");
            result = true;
        }
    } 
    
    if(selectboxId == "_inPatientClnDept"){
        var clnDept = $("#_inPatientClnDept option:selected").val();
        searchDoctor(clnDept);  
    }else if(selectboxId == "_outPatientClnDept"){
        var clnDept = $("#_outPatientClnDept option:selected").val();
        SearchOutDoctor(clnDept);
    }else if(selectboxId == "_operationClnDept"){
        var clnDept = $("#_operationClnDept option:selected").val();
        searchOperDoctor(clnDept); 
    }else if(selectboxId == "_requestDept"){
        var clnDept = $("#_requestDept option:selected").val();
        SearchRequestDoctor(clnDept);
    }else if(selectboxId == "_requestDept"){
        var clnDept = $("#_requestDept option:selected").val();
        searchDoctorTreat(clnDept);
    }else if(selectboxId == "_laboratoryClnDept"){
        var clnDept = $("#_laboratoryClnDept option:selected").val();
        searchCheckupRoom(clnDept);
    }
    return result;
}

// 사용자 디폴트 셋팅
function userDefaultSetting(type, selectboxId) {
    var target = "";
    if (type == "dept")
        target = getLocalStorage("userDeptCode");
    else
        target = type;

    var result = selectboxDefalutSelected(selectboxId, target);

    COMMON.LOCAL.page.isFirst = false;

    return result;
}

// 입원 환자 조회 조건 검사
function searchInpatientConditionCheck() {
    var condition = true;
    var ward = $("#_inPatientWard option:selected").val();
    var clnDept = $("#_inPatientClnDept option:selected").val();
    var dortorId = $("#_inPatientDoctor option:selected").val();
    var chargeId = $("#_inPatientCharge option:selected").val();

    if (ward == "" && clnDept == "" && dortorId == "" && chargeId == "") {
        alert("[병동] 또는 [진료과]중에 한 항목은 필수로 선택해야합니다.");
        condition = false;
        return;
    }
    return condition;
};

// 외래 환자 조회 조건 검사
function searchOutpatientConditionCheck() {
    var condition = true;
    var clnDate = $("#_outPatientCalendar").val().replace(/-/g, "");
    var clnDept = $("#_outPatientClnDept option:selected").val();
    if (clnDate == "") {
        alert("진료일을 선택하지 않았습니다.\n진료일을 선택해주세요.");
        condition = false;
    }
    if (clnDept == "") {
        alert("진료과를 선택하지 않았습니다.\n진료과를 선택해주세요.");
        condition = false;
    }
    return condition;
};
// 의뢰 환자 조회 조건 검사
function searchRequestConditionCheck() {
    var condition = true;
    var clnDept = $("#_requestDept option:selected").val();
    if (clnDept == "") {
        alert("진료과를 선택하지 않았습니다.\n진료과를 선택해주세요.");
        condition = false;
    }
    return condition;
};



// 뷰어에서 리턴될때 전체 동의서 항목일 경우 리스트 갱신
function eformReturnCallback(message) {
	
    if (message != "")
    	alert(message);
    
    COMMON.LogOutTimer.start(); // 로그아웃 타이머 스타트

    // 연관 동의서 재조회
    $("#_relationConsentList li").remove();
    $('.Consent_List').css('display', 'none');
    $('.dimmed').css('display', 'none');
    $('#Consent_List_Big_div').empty();
    localStorage.setItem('treatmentnm', "");
    // 작성동의서일 경우만 동의서 재조회
    if ($(".agree_2").attr("class").indexOf("on" ) > -1) {
        $("#_consentList li").remove();
        fnAllSearchConsent();
    }
    $('#loading_bar').css('display', 'none');
    COMMON.plugin.loadingBar("hide", "뷰어를 실행하는 중입니다.");
    if ($('.gnb_7').attr('class').indexOf("on") > -1) {
        if ($('.cosign_out').attr('class').indexOf("on") > -1) {
            setTimeout(function () {
                searchCosignSend();
            }, 500);

        } else {
            setTimeout(function () {
                searchCosign();
            }, 500);
        }
    } else if ($('.gnb_8').attr('class').indexOf("on") > -1) {
        $('#myConsentSearch').trigger('click');
        setTimeout(function () {
            if($('#NEW').prop('checked')== false){
             	patientListSort(getLocalStorage("sortlistId"), getLocalStorage("sortlistKey"), getLocalStorage("sortlistOrderby"));
         	}
        }, 1000);   
    } else {
        searchNowriteConsent();
    }
}

// 한글 성별명
function getSexKorName(type) {
    return (type == "M") ? "남" : "여";
}


// 동명이인 여부
function isSameName(listId, patient) {
    var result = false;
    var seen = patient;
    $("#" + listId + " ul").each(function () {
        var see = JSON.parse($(this).attr("attr-data"));
        if (seen.PatientCode != see.PatientCode) {
            if (seen.PatientName == see.PatientName)
                result = true;
        }
    });
    if (result)
        alert("선택한 환자는 동명이인이 있습니다. 유의바랍니다.");
}



function vauleNullCheck(value, defaultValue) {
    var result = "";
    if (value == undefined || value == "")
        result = defaultValue;
    else
        result = value;
    return result;
}

// 금일 환자 여부(입원과 응급에서만 사용함.)
function isTodayInpatient(ul, patient) {
    var admissionDate = (patient.AdmissionDate == undefined ? patient.detailDate : patient.AdmissionDate);
    if (getDay("") == admissionDate)
        ul.addClass("today");
};

// 활성 메뉴 ID
function getActiveMenuId() {
    var activeMenu = "";
    var menus = $("#_menus li");
    for (var i = 0; i < menus.length; i++) {
        if (menus.eq(i).hasClass('on'))
            activeMenu = menus.eq(i).attr("id");
    }
    return activeMenu;
}

// 동의서 상태 비교
function consentStatComparison() {
    var consent = COMMON.LOCAL.eform.consent[0];
    if (COMMON.LOCAL.record.type != "image") {
        // 재작성일 경우 rewriteConsentMstRid을 설정 후 연동
        // if (COMMON.LOCAL.record.type == "rewrite")
        // COMMON.LOCAL.record.obj.params.rewriteConsentMstRid =
        // consent.ConsentMstRid;

        // 녹취 파일이 있을 경우 녹취를 다운로드하고 뷰어로 연동
        // if (consent.RecordCnt != undefined && consent.RecordCnt > 0)
        // searchConsentRecordFilePath();
        // else
        COMMON.plugin.loadEFormViewByGuid(COMMON.LOCAL.record.type, "VIEWER_DATA", COMMON.LOCAL.eform.consent, COMMON.LOCAL.record.obj);

    } else {
        searchConsentImageFilePath();
    }
}

function isNextRequest() {
    if (requestMap.QUEUE.length > 1) {
        requestMap.next();
        requestMap.start();
    } else {
        requestMap.stop();
    }
}

// 최근 로그인 - 아이디 저장
function setLatelyData(userInfo) {
    var item = [];
    item.push(userInfo.userId);
    // 최근 결재자 항목들
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


// 최근 로그인 - 배열 순서 최신순으로 변경하고 중복 제거
function arrange(storageList, selectedList) {
    for (var i = 0; i < selectedList.length; i++) {
        for (var j = 0; j < storageList.length; j++) {
            var userId = selectedList[i];
            var compareUserId = storageList[j];
            if (userId == compareUserId) {
                storageList.splice(j, 1);
                //console.log(storageList);
            }
        }
        storageList.unshift(selectedList[i]);
    }
    if (storageList.length > 5)
        storageList.pop();
    return storageList;
};

// 최근 로그인 - 아이디 가져오기
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
        $("#_reLoginId").val(id.trim());
        $("#_reLoginPw").val("");
        $("#_reLoginPw").trigger("focus");
    });
};

// 최근 로그인 - 배열을 스트링으로 변환하기
function arrayItemToString(array) {
    $.each(array, function (index, item) {
        array[index] = item;
    });
};

// 알림 팝업 창 사용
function noticeAlert(message, title) {
    $("._noticeMessage").text(message);
    if (title != undefined && title != null && title != "")
        $("._noticeTitle").text(title);
    else
        $("._noticeTitle").text("알림");
    popupShow("popup-notice");
}

function popupShow(targetId) {
    $("." + targetId).addClass("_open");
    $("." + targetId).show();
    $(".dimmed").show();
}

function popupHide(targetId) {
    $("." + targetId).removeClass("_open");
    $("." + targetId).hide();
    if ($("._open").length <= 0)
        $(".dimmed").hide();
}

function logAlert(message) {
    if (INTERFACE.IS_ALTER == "TRUE")
        alert(message);
}

function verificationAlert(message) {
    alert(message);
}

function isConsoleLog(message) {
    if (INTERFACE.IS_ALTER == "TRUE")
        console.log(message);
};

function errorHandler(errorMessage) {
    requestMap.init();
    alert(errorMessage);
};

// 담당의 조회(레지)
function searchCharge(deptcd) {
    if (deptcd == "" || deptcd == undefined) {
        clearOption('_inPatientCharge', '지정의');
    } else {
        $.ajax({
            url: ajax_url,
            data: "submit_id=DRMRF00114&deptcd=" + deptcd + "&instcd=204&drflag=A&nmdispflag=D",
            type: 'get',
            dataType: 'xml',
            timeout: 40000,
            success: function (result) {
                $("#_inPatientCharge option").remove();
                $("#_inPatientCharge").append($('<option>', {
                    value: '',
                    text: '지정의'
                }));
                if ($(result).find('usercombo').length > 0) {
                    $(result).find('usercombo').each(function () {
                        $("#_inPatientCharge").append($('<option>', {
                            value: $(this).find('userid').text(),
                            text: $(this).find('usernm').text()
                        }));
                    });
                }
                isNextRequest();
            },
            error: function (error) {
                alert("지정의 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + error);
            }

        })

    }

}

// 담당의사 조회(교수님) - 주치의
function searchDoctor(deptcd) {
    if (deptcd == "" || deptcd == undefined) {
        $("#_inPatientDoctor option").remove();
        $("#_inPatientDoctor").append($('<option>', {
            value: '',
            text: '주치의'
        }));
    } else {
        $.ajax({
            url: ajax_url,
            data: "submit_id=DRMRF00114&business_id=mr&deptcd=" + deptcd + "&instcd=204&drflag=M&nmdispflag=D",
            type: 'get',
            dataType: 'xml',
            timeout: 40000,
            success: function (result) {
                $("#_inPatientDoctor option").remove();
                $("#_inPatientDoctor").append($('<option>', {
                    value: '',
                    text: '주치의'
                }));
                if ($(result).find('usercombo').length > 0) {
                    $(result).find('usercombo').each(function () {
                        $("#_inPatientDoctor").append($('<option>', {
                            value: $(this).find('userid').text(),
                            text: $(this).find('usernm').text()
                        }));
                    });
                }
                isNextRequest();
            },
            error: function (error) {
                alert("주치의 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + error);
            }

        })
    }

}

// 진료의 조회- 외래
function SearchOutDoctor(deptcd) {
    if (deptcd == "" || deptcd == undefined) {
        $("#_outPatientDoctor option").remove();
        $("#_outPatientDoctor").append($('<option>', {
            value: '',
            text: '진료의'
        }));
    } else {
        $.ajax({
            url: ajax_url,
            data: "submit_id=DRMRF00114&business_id=mr&deptcd=" + deptcd + "&instcd=204&drflag=&nmdispflag=D",
            type: 'get',
            dataType: 'xml',
            timeout: 40000,
            success: function (result) {
                $("#_outPatientDoctor option").remove();
                $("#_outPatientDoctor").append($('<option>', {
                    value: '',
                    text: '진료의'
                }));
                if ($(result).find('usercombo').length > 0) {
                    $(result).find('usercombo').each(function () {
                        $("#_outPatientDoctor").append($('<option>', {
                            value: $(this).find('userid').text(),
                            text: $(this).find('usernm').text()
                        }));

                    });

                }
                isNextRequest();
            },
            error: function (error) {
                alert("진료의 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + error);
            }

        })
    }
}
// 집도의 조회  - 수술
function searchOperDoctor(clnDept){
	 if (clnDept == "" || clnDept == undefined) {
         $("#_OperationDoctor option").remove();
         $("#_OperationDoctor").append($('<option>', {
             value: '',
             text: '집도의'
         }));
     } else {
         COMMON.plugin.wifiCheck(wifiCheckFn);

         var checkVal = localStorage.getItem("wifiCheckVal");
         if (checkVal < -90) {
             isRun_2 = false;
             alert("WIFI 수신 감도가 좋지 않습니다. WIFI 통신이 잘 되는 곳에서 사용 바랍니다.");
         } else {
             if (isRun_2 == true) {
                 return;
             }
             isRun_2 = true;
             $.ajax({
                 url: ajax_url,
                 data: "submit_id=DRMRF00114&business_id=mr&deptcd=" + clnDept + "&instcd=204&drflag=M&nmdispflag=D",
                 type: 'get',
                 dataType: 'xml',
                 timeout: 40000,
                 success: function (result) {
                     isRun_2 = false;
                     $("#_OperationDoctor option").remove();
                     $("#_OperationDoctor").append($('<option>', {
                         value: '',
                         text: '집도의'
                     }));
                     if ($(result).find('usercombo').length > 0) {
                         $(result).find('usercombo').each(function () {
                             $("#_OperationDoctor").append($('<option>', {
                                 value: $(this).find('userid').text(),
                                 text: $(this).find('usernm').text()
                             }));

                         });

                     }
                     isNextRequest();
                 },
                 error: function (error) {
                     isRun_2 = false;
                     alert("집도의 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + error);
                 }

             });
         }

     }
}

// 진료의 조회- 의뢰
function SearchRequestDoctor(deptcd) {
    if (deptcd == "" || deptcd == undefined) {
        $("#_requestDoc option").remove();
        $("#_requestDoc").append($('<option>', {
            value: '',
            text: '진료의'
        }));
    } else {
        $.ajax({
            url: ajax_url,
            data: "submit_id=DRMRF00114&business_id=mr&deptcd=" + deptcd + "&instcd=204&drflag=&nmdispflag=D",
            type: 'get',
            dataType: 'xml',
            timeout: 40000,
            success: function (result) {
                $("#_requestDoc option").remove();
                $("#_requestDoc").append($('<option>', {
                    value: '',
                    text: '진료의'
                }));
                if ($(result).find('usercombo').length > 0) {
                    $(result).find('usercombo').each(function () {
                        $("#_requestDoc").append($('<option>', {
                            value: $(this).find('userid').text(),
                            text: $(this).find('usernm').text()
                        }));

                    });

                }
                isNextRequest();
            },
            error: function (error) {
                alert("진료의 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + error);
            }

        })
    }
}


//담당의사 조회(교수님) - 주치의
function searchDoctorTreat(deptcd) {
    if (deptcd == "" || deptcd == undefined) {
        $("#treateMentDoc option").remove();
        $("#treateMentDoc").append($('<option>', {
            value: '',
            text: '주치의'
        }));
    } else {
        $.ajax({
            url: ajax_url,
            data: "submit_id=DRMRF00114&business_id=mr&deptcd=" + deptcd + "&instcd=204&drflag=M&nmdispflag=D",
            type: 'get',
            dataType: 'xml',
            timeout: 40000,
            success: function (result) {
                $("#treateMentDoc option").remove();
                $("#treateMentDoc").append($('<option>', {
                    value: '',
                    text: '주치의'
                }));
                if ($(result).find('usercombo').length > 0) {
                    $(result).find('usercombo').each(function () {
                        $("#treateMentDoc").append($('<option>', {
                            value: $(this).find('userid').text(),
                            text: $(this).find('usernm').text()
                        }));

                    });
                }
                isNextRequest();
            },
            error: function (error) {
                alert("주치의 조회 중 문제가 발생하였습니다. 관리자에게 문의바랍니다\n" + error);
            }

        })
    }

}


function clearOption(tag_id, texts) {
    $('#' + tag_id + ' option').remove();
    $('#' + tag_id).append($('<option>', {
        value: '',
        text: texts
    }));
}

function cosignOption(dept) {
    if (dept == "outPatient") {
        $('#_cosignText').css('display', 'block');
        $('#cosignList').css('display', 'block');
        // $('#_cosignDoctorList').css('width','50%');
    } else {
        $('#_cosignText').css('display', 'none');
        $('#cosignList').css('display', 'none');
    }
}

function loadingbar_display() {
	alert("이거탔다!");
//    if ($('#loading_bar').is(":visible")) {
//        //console.log("[로딩바asdf] : 비활성화");
//        $('#loading_bar').css('display', 'none');
//    } else {
//        //console.log("[로딩바asdf] : 활성화");
//        $('#loading_bar').css('display', 'block');
//    };
}  

function CreateOcrTag() {
    var seqNum;

    COMMON.plugin.wifiCheck(wifiCheckFn);

    var checkVal = localStorage.getItem("wifiCheckVal");
    if (checkVal < -90) {
        alert("WIFI 수신 감도가 좋지 않습니다. WIFI 통신이 잘 되는 곳에서 사용 바랍니다.");
    } else {
        var ymd = "";
        $.ajax({
            url:  'http://emr.yjh.com/eform' + '/biz/nu/member/viewer/eForm25/consent/nowtime/get',
            type: 'post',
            timeout: 40000,
            async: false,
        }).done(function (data) {
            ymd = data.nowTime; 
        }).fail(function (xhr, status, errorThrown) { 
            alert("서버시간 확인 중 오류가 발생했습니다. 관리자에게 문의바랍니다. " + errorThrown)
        });
        ymd = ymd.substring(0, 10).replace(/-/g, "");
        $.ajax({
            url: ajax_url,
            data: "submit_id=DRMRF02318&instcd=204&business_id=mr&ocrseqcnt=1",
            type: 'get',
            async: false,
            dataType: 'xml',
            timeout: 40000,
            success: function (result) {
                if ($(result).find('ocrseq').length > 0) {
                    $(result).find('ocrseq').each(function () {
                        seqNum = ymd + $(this).find('seq').text(); 
                    });
                }
            },
            error: function (error) {
            	seqNum = "error";
            	alert("OCRTAG값을 가져오는 도중 오류가 발생하였습니다.");
            }

        }) 
        return seqNum;

    }
}


function wifiCheckFn(wifiCheck) {
    localStorage.setItem("wifiCheckVal", wifiCheck);
    //alert(localStorage.getItem("wifiCheckVal"));
}

function getTime() {
    $.ajax({
        url: 'http://emr.yjh.com/eform' + '/biz/nu/member/viewer/eForm25/consent/nowtime/get',
        type: 'post',
        timeout: 40000,
        async: false,
    }).done(function (data) {
        localStorage.setItem("nowTime", data.nowTime);
    });
} 