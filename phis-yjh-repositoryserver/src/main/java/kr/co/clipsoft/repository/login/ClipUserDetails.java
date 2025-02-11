package kr.co.clipsoft.repository.login;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import kr.co.clipsoft.repository.model.ClipUserDto;
import kr.co.clipsoft.repository.session.FormDataLockInfo;

public class ClipUserDetails implements UserDetails {

	private static final long serialVersionUID = -4450269958885980297L;
    private String userName;
    private String password;
    private ClipUserDto userDto;
    private HashMap<Long, FormDataLockInfo> lockFormDataInfoMap;

    public ClipUserDetails(String userName, String password) {
    		this.userName = userName;
    		this.password = password;
    		lockFormDataInfoMap = new HashMap<Long, FormDataLockInfo>();
	}
    
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();   
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
         
        return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	public ClipUserDto getUserDto() {
		return userDto;
	}

	public void setUserDto(ClipUserDto userDto) {
		this.userDto = userDto;
	}

	public void addFormDataLockInfo(FormDataLockInfo info) {
		lockFormDataInfoMap.put(info.getLockFormData().getFormId(), info);
	}
	
	public void removeFormDataLockInfo(FormDataLockInfo info) {
		lockFormDataInfoMap.remove(info.getLockFormData().getFormId());
	}
	
	public FormDataLockInfo getFormDataLockInfo(Long formId) {
		return lockFormDataInfoMap.get(formId);
	}
	 
	public List<FormDataLockInfo> getFormDataLockInfoList() {
		ArrayList<FormDataLockInfo> list = new ArrayList<FormDataLockInfo>(lockFormDataInfoMap.values());
		return list;
	}
}
