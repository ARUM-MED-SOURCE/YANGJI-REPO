package kr.co.clipsoft.repository.service;

import java.util.List;

import kr.co.clipsoft.repository.model.ClipFormCategoryGroupAuthDto;
import kr.co.clipsoft.repository.model.ClipUserGroupDto;

public interface ClipFormCategoryGroupAuthService {
	int newData(ClipFormCategoryGroupAuthDto dto);
	int delete(ClipFormCategoryGroupAuthDto dto);
	ClipFormCategoryGroupAuthDto get(ClipFormCategoryGroupAuthDto dto);
	List<ClipFormCategoryGroupAuthDto> getList(ClipFormCategoryGroupAuthDto dto);
	List<ClipUserGroupDto> getAddGroupList(ClipFormCategoryGroupAuthDto dto);
}
