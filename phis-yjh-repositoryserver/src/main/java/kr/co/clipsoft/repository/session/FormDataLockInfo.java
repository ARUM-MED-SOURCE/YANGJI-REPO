package kr.co.clipsoft.repository.session;

import kr.co.clipsoft.repository.login.ClipUserDetails;
import kr.co.clipsoft.repository.model.ClipFormDataDto;

public class FormDataLockInfo {
	private ClipUserDetails userDetails;
	private ClipFormDataDto lockFormData;
	private String ipAdress;
	
	public FormDataLockInfo(ClipUserDetails userDetails, ClipFormDataDto lockFormData, String ipAdress) {
		this.userDetails = userDetails;
		this.lockFormData = lockFormData;
		this.ipAdress = ipAdress;
	}
	
	public ClipUserDetails getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(ClipUserDetails userDetails) {
		this.userDetails = userDetails;
	}
	public ClipFormDataDto getLockFormData() {
		return lockFormData;
	}
	public void setLockFormData(ClipFormDataDto lockFormData) {
		this.lockFormData = lockFormData;
	}
	public String getIpAdress() {
		return ipAdress;
	}
	public void setIpAdress(String ipAdress) {
		this.ipAdress = ipAdress;
	}
	
	
}
