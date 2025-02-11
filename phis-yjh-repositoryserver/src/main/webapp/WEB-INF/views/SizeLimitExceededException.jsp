<%@page import="kr.co.clipsoft.biz.exception.*"%>
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	BizResultInfo errorInfo = new BizResultInfo(false, new BusinessException(BizErrorInfo.ERROR_MAX_UPLOAD_SIZE));
	out.print(errorInfo.toResultOfJSONString());
%>