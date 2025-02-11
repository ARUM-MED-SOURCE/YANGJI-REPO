package kr.co.clipsoft.repository.service;

import java.util.List;

import kr.co.clipsoft.repository.model.ClipSearchDto;
import kr.co.clipsoft.repository.model.ClipUserDto;

public interface ClipUserService {
	int insert(ClipUserDto dto);
	int update(ClipUserDto dto);
	ClipUserDto get(ClipUserDto dto);
	List<ClipUserDto> getList(ClipSearchDto dto);
	int delete(ClipUserDto dto);
}
