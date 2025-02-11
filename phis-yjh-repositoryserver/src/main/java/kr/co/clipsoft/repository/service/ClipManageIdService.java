package kr.co.clipsoft.repository.service;

import kr.co.clipsoft.repository.model.ClipManageIdDto;

public interface ClipManageIdService {
	ClipManageIdDto getFormId(Long productId);
	ClipManageIdDto getFormCategoryId(Long productId);
	ClipManageIdDto get(ClipManageIdDto dto);
	int increase(ClipManageIdDto dto);
}
