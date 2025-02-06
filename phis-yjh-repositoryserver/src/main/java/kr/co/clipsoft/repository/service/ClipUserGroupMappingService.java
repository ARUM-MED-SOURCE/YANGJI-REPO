package kr.co.clipsoft.repository.service;

import java.util.List;

import kr.co.clipsoft.repository.model.ClipSearchDto;
import kr.co.clipsoft.repository.model.ClipUserGroupMappingDto;

public interface ClipUserGroupMappingService {
	int insert(ClipUserGroupMappingDto dto);
	int delete(ClipUserGroupMappingDto dto);
	ClipUserGroupMappingDto get(ClipUserGroupMappingDto dto);
	List<ClipUserGroupMappingDto> getGroupList(ClipSearchDto dto);
	List<ClipUserGroupMappingDto> getUserList(ClipSearchDto dto);
}
