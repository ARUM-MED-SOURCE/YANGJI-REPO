package kr.co.clipsoft.repository.service;

import java.util.List;

import com.google.gson.JsonObject;

import kr.co.clipsoft.repository.model.ClipActionGroupAuthDto;
import kr.co.clipsoft.repository.model.ClipActionUserAuthDto;
import kr.co.clipsoft.repository.model.ClipUserDto;
import kr.co.clipsoft.repository.model.ClipUserGroupDto;

public interface ClipActionAuthService {
	int insertActionGroupAuth(ClipActionGroupAuthDto dto);
	int deleteActionGroupAuth(ClipActionGroupAuthDto dto);
	int insertActionUserAuth(ClipActionUserAuthDto dto);
	int deleteActionUserAuth(ClipActionUserAuthDto dto);
	ClipActionGroupAuthDto getActionGroupAuth(ClipActionGroupAuthDto dto);
	ClipActionUserAuthDto getActionUserAuth(ClipActionUserAuthDto dto);
	JsonObject getList_authAction(ClipUserDto dto);
	List<ClipActionGroupAuthDto> getList(ClipActionGroupAuthDto dto);
	List<ClipUserGroupDto> getAddGroupList(ClipActionGroupAuthDto dto);
	List<ClipActionUserAuthDto> getList(ClipActionUserAuthDto dto);
	List<ClipUserDto> getAddUserList(ClipActionUserAuthDto dto);
}
