package kr.co.clipsoft.biz.model;

import java.util.List;

public class NuUserDto {

	private boolean loginResult;
	private String loginResultMsg;
	private String userId;
	private String userNm;
	private String instCd;
	private String instNm;
	private List<DeptInfo> deptInfos;
	private String jobKindCd;
	private String jobPosCd;

	public boolean isLoginResult() {
		return loginResult;
	}

	public void setLoginResult(boolean loginResult) {
		this.loginResult = loginResult;
	}

	public String getLoginResultMsg() {
		return loginResultMsg;
	}

	public void setLoginResultMsg(String loginResultMsg) {
		this.loginResultMsg = loginResultMsg;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserNm() {
		return userNm;
	}

	public void setUserNm(String userNm) {
		this.userNm = userNm;
	}

	public String getInstCd() {
		return instCd;
	}

	public void setInstCd(String instCd) {
		this.instCd = instCd;
	}

	public String getInstNm() {
		return instNm;
	}

	public void setInstNm(String instNm) {
		this.instNm = instNm;
	}

	public List<DeptInfo> getDeptInfos() {
		return deptInfos;
	}

	public void setDeptInfos(List<DeptInfo> deptInfos) {
		this.deptInfos = deptInfos;
	}

	public String getJobKindCd() {
		return jobKindCd;
	}

	public void setJobKindCd(String jobKindCd) {
		this.jobKindCd = jobKindCd;
	}

	public String getJobPosCd() {
		return jobPosCd;
	}

	public void setJobPosCd(String jobPosCd) {
		this.jobPosCd = jobPosCd;
	}

	public class DeptInfo {
		private String deptCd;
		private String deptNm;

		public DeptInfo(String deptCd, String deptNm) {
			this.deptCd = deptCd;
			this.deptNm = deptNm;
		}

		public String getDeptCd() {
			return deptCd;
		}

		public void setDeptCd(String deptCd) {
			this.deptCd = deptCd;
		}

		public String getDeptNm() {
			return deptNm;
		}

		public void setDeptNm(String deptNm) {
			this.deptNm = deptNm;
		}

	}

}
