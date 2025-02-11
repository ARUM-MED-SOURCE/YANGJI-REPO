package kr.co.clipsoft.repository.service;

import java.util.List;

import kr.co.clipsoft.repository.model.ClipSearchDto;
import kr.co.clipsoft.repository.model.ClipUserGroupDto;

public interface ClipUserGroupService {
	int newData(ClipUserGroupDto dto);
	int update(ClipUserGroupDto dto);
	int update_useYN(ClipUserGroupDto dto);
	ClipUserGroupDto get(ClipUserGroupDto dto);
	List<ClipUserGroupDto> getList(ClipSearchDto dto);
}
