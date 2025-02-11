package kr.co.clipsoft.repository.service;

import java.util.List;

import com.google.gson.JsonObject;

import kr.co.clipsoft.repository.model.ClipFormUserAuthDto;
import kr.co.clipsoft.repository.model.ClipUserDto;

public interface ClipFormUserAuthService {
	int newData(ClipFormUserAuthDto dto);
	int delete(ClipFormUserAuthDto dto);
	ClipFormUserAuthDto get(ClipFormUserAuthDto dto);
	List<ClipFormUserAuthDto> getList(ClipFormUserAuthDto dto);
	List<ClipUserDto> getAddUserList(ClipFormUserAuthDto dto);
	JsonObject getList_auth_getList_form(Long productId, String userId);
	JsonObject getList_auth_getList_category(Long productId, String userId);
}