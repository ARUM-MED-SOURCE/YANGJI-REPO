package kr.co.clipsoft.repository.service;

import java.util.HashMap;
import java.util.List;

import kr.co.clipsoft.repository.model.ClipFormCategoryDto;

public interface ClipFormCategoryService {
	ClipFormCategoryDto newFormCategory(ClipFormCategoryDto dto);
	int update(ClipFormCategoryDto dto);
	int update_useYN(ClipFormCategoryDto dto);
	ClipFormCategoryDto get(ClipFormCategoryDto dto);
	List<HashMap> getList_all_categoryAndForm(Long productId, String userId, String itemName);
	List<ClipFormCategoryDto> getList_regist_category(Long productId, String itemName);
}
