package kr.co.clipsoft.repository.service;

import java.util.List;

import kr.co.clipsoft.repository.model.ClipFormCategoryUserAuthDto;
import kr.co.clipsoft.repository.model.ClipUserDto;

public interface ClipFormCategoryUserAuthService {
	int newData(ClipFormCategoryUserAuthDto dto);
	int delete(ClipFormCategoryUserAuthDto dto);
	ClipFormCategoryUserAuthDto get(ClipFormCategoryUserAuthDto dto);
	List<ClipFormCategoryUserAuthDto> getList(ClipFormCategoryUserAuthDto dto);
	List<ClipUserDto> getAddUserList(ClipFormCategoryUserAuthDto dto);
}
