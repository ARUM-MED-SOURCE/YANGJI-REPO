package kr.co.clipsoft.repository.session;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import kr.co.clipsoft.repository.login.ClipUserDetails;
import kr.co.clipsoft.repository.model.ClipFormDataDto;

@Component
@Scope(value="singleton", proxyMode=ScopedProxyMode.DEFAULT)
public class GlobalSession {
	
	@Value("#{projectProperties['server.enableFormDataLock']}")
	private boolean enableFormDataLock;
	
	private HashMap<Long, FormDataLockInfo> formDataLockDictionary;
	
	public GlobalSession() {
		formDataLockDictionary = new HashMap<Long, FormDataLockInfo>();
	}
	
	public FormDataLockInfo isLockFormData(FormDataLockInfo formDataLockInfo) {
		try {
			if(!enableFormDataLock) {
				return null;
			}
			
			FormDataLockInfo serverFormDataLockInfo = formDataLockDictionary.get(formDataLockInfo.getLockFormData().getFormId());
			if(serverFormDataLockInfo == null) {
				return null;
			}
			
			String server_userId = serverFormDataLockInfo.getUserDetails().getUserDto().getUserId();
			String parameter_userId = formDataLockInfo.getUserDetails().getUserDto().getUserId();
			if(!server_userId.equals(parameter_userId)) {
				return serverFormDataLockInfo;
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}
	
	public FormDataLockInfo lockFormData(ClipFormDataDto dataDto) {
		if(!enableFormDataLock) {
			return null;
		}
		
		ClipUserDetails userDetails = SessionInfoUtility.getLoginUserDetails();
		
		if(userDetails != null) {
			FormDataLockInfo lockInfo = new FormDataLockInfo(userDetails, dataDto, "");
			if(isLockFormData(lockInfo) == null) { 
				formDataLockDictionary.put(lockInfo.getLockFormData().getFormId(), lockInfo);
				userDetails.addFormDataLockInfo(lockInfo);
				return lockInfo;
			}
		}
		
		return null;
	}
	
	public void unlockFormData(FormDataLockInfo lockInfo) {
		if(!enableFormDataLock) {
			return;
		}
		
		if(isLockFormData(lockInfo) != null) { 
			formDataLockDictionary.remove(lockInfo.getLockFormData().getFormId());
			ClipUserDetails userDetails = SessionInfoUtility.getLoginUserDetails();
			userDetails.removeFormDataLockInfo(lockInfo);
		}
	}
	
	public void unlockFormData(ClipFormDataDto dataDto) {
		if(!enableFormDataLock) {
			return;
		}
		
		ClipUserDetails userDetails = SessionInfoUtility.getLoginUserDetails();
		FormDataLockInfo lockInfo = new FormDataLockInfo(userDetails, dataDto, "");
		
		if(isLockFormData(lockInfo) != null) { 
			formDataLockDictionary.remove(lockInfo.getLockFormData().getFormId());
			userDetails.removeFormDataLockInfo(lockInfo);
		}
	}
	
	public void unlockFormData(Authentication authentication) {
		if(!enableFormDataLock) {
			return;
		}
		
		ClipUserDetails userDetails = (ClipUserDetails) authentication.getDetails();
		List<FormDataLockInfo> lockInfoList = userDetails.getFormDataLockInfoList();
		
		for (FormDataLockInfo formDataLockInfo : lockInfoList) {
			if(userDetails.getUsername().equals(authentication.getName())) {
				Long formId = formDataLockInfo.getLockFormData().getFormId();
				formDataLockDictionary.remove(formId);
			}
		}
	}
	
	public void unlockFormData(ClipUserDetails userDetails) {
		if(!enableFormDataLock) {
			return;
		}
		
		List<FormDataLockInfo> lockInfoList = userDetails.getFormDataLockInfoList();
		
		for (FormDataLockInfo formDataLockInfo : lockInfoList) {
			if(userDetails.getUsername().equals(userDetails.getUsername())) {
				Long formId = formDataLockInfo.getLockFormData().getFormId();
				formDataLockDictionary.remove(formId);
			}
		}
	}
}
