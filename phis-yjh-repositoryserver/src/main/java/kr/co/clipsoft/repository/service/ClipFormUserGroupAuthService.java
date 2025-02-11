package kr.co.clipsoft.repository.service;

import java.util.List;

import kr.co.clipsoft.repository.model.ClipFormUserGroupAuthDto;
import kr.co.clipsoft.repository.model.ClipUserGroupDto;

public interface ClipFormUserGroupAuthService {
	int newData(ClipFormUserGroupAuthDto dto);
	int delete(ClipFormUserGroupAuthDto dto);
	ClipFormUserGroupAuthDto get(ClipFormUserGroupAuthDto dto);
	List<ClipFormUserGroupAuthDto> getList(ClipFormUserGroupAuthDto dto);
	List<ClipUserGroupDto> getAddGroupList(ClipFormUserGroupAuthDto dto);
}
