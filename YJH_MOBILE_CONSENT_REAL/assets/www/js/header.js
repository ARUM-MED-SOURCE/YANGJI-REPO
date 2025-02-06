( function() {
	var scriptPaths = document.getElementsByTagName("script"),
		rootPath = "./../", // header.js 에서 root 폴더까지의 위치 개발자가 수정해줘야 하는 부분...
		headerPath = "";
	
	for(var i = 0 ; i < scriptPaths.length ; i++){
		if(scriptPaths[i].getAttribute("src").indexOf('header.js') !== -1){
			headerPath = scriptPaths[i].getAttribute("src").split('header.js')[0];
			var path =  headerPath + rootPath;
			
		    // add css
//			document.write('<link rel="stylesheet" href="' + path + 'css/default.css" />');			
//			document.write('<link rel="stylesheet" href="' + path + 'css/style.css" />'); 
//			document.write('<link rel="stylesheet" href="' + path + 'css/login.css" />');
		    
		    // add scripts
		    document.write('<script src="' + path + 'lib/js/jquery-1.12.4.min.js"></script>');		    
		    document.write('<script src="' + path + 'lib/js/jquery.xml2json.js"></script>');
		    
		    if(/Android/.test(navigator.userAgent) || /iPhone/.test(navigator.userAgent) ||/iPad/.test(navigator.userAgent) ){
		    	document.write('<script src="' + path + 'cordova.js"></script>');
		    }
		    
		    // add settings
		    document.write('<script src="' + path + 'js/configurations.js"></script>');
		    document.write('<script src="' + path + 'js/common.js"></script>');
		}
	}
}());
