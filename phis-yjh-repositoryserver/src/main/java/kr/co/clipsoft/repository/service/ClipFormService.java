package kr.co.clipsoft.repository.service;

import java.util.HashMap;
import java.util.List;

import kr.co.clipsoft.repository.model.ClipFormAndDataDto;
import kr.co.clipsoft.repository.model.ClipFormCategoryDto;
import kr.co.clipsoft.repository.model.ClipFormDataDto;
import kr.co.clipsoft.repository.model.ClipFormDto;

public interface ClipFormService {
	int newFormData(ClipFormDto formDto, ClipFormDataDto dataDto);
	int newVersion(ClipFormDto formDto, ClipFormDataDto dataDto);
	int updateFormData(ClipFormDataDto dataDto);
	int updatePublication(ClipFormDataDto parameter);
	int updateUse(ClipFormDto parameter);
	ClipFormDto getForm(ClipFormDto parameter);
	ClipFormAndDataDto get(ClipFormDto formDto, Long formVersion, Boolean eForm_setPublicationYN);
	ClipFormAndDataDto getRecent(ClipFormDto formDto, boolean includeFormData, Boolean eForm_setPublicationYN);
	List<ClipFormAndDataDto> getList_allVersion(ClipFormDto parameter, Boolean eForm_setPublicationYN);
	List<HashMap> getList_all(ClipFormDto formDto, Boolean eForm_setPublicationYN);
	List<ClipFormDto> getList_allRegist(ClipFormDto formDto, Boolean eForm_setPublicationYN);
	List<ClipFormDto> getList_allCategoryForm(ClipFormCategoryDto formCategoryDto, Boolean eForm_setPublicationYN);
	int updateName(ClipFormDto parameter);
	int updateCategory(ClipFormDto parameter);
}
