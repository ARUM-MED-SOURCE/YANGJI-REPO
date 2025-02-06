package kr.co.clipsoft.biz.service;

import java.util.HashMap;
import java.util.List;

import kr.co.clipsoft.biz.model.NuDocumentDto;
import kr.co.clipsoft.biz.model.NuFormAndDataDto;
import kr.co.clipsoft.biz.model.NuFormAndExDto;
import kr.co.clipsoft.biz.model.NuFormDataDto;
import kr.co.clipsoft.biz.model.NuFormDto;
import kr.co.clipsoft.biz.model.NuTempDataDto;
import kr.co.clipsoft.biz.model.consent.NuConsentFormExDto;
import kr.co.clipsoft.repository.model.ClipFormDto;

public interface NuFormService {
	NuDocumentDto getDocumentCode(NuDocumentDto documentDto);

	NuDocumentDto getFormId(NuDocumentDto documentDto);

	String getFormId(Long productId, NuDocumentDto parameterDto);

	int insertDocumentCode(NuDocumentDto documentDto);

	int newFormData(NuFormDto formDto, NuFormDataDto dataDto);

	int newFormDataMig(NuFormDto formDto, NuFormDataDto dataDto, NuConsentFormExDto formExDto);

	int updatePublication(NuFormDataDto formDataDto);

	NuFormAndDataDto get(NuFormDto formDto, Long formVersion);

	int newVersion(NuFormDto formDto, NuFormDataDto dataDto);

	int newVersionMig(NuFormDataDto dataDto);

	NuFormAndDataDto getRecent(NuFormDto formDto);

	List<NuFormAndDataDto> getList_allVersion(NuFormDto parameter) throws Exception;

	NuTempDataDto insertNuTempFormData(NuTempDataDto nuTempDataDto);

	NuTempDataDto getNuTempData(String pd);

	List<NuFormAndExDto> getClipFormAndConsentFormEx(NuFormAndExDto paramDto);

	boolean saveConsentFormEx(Long productId, List<NuFormAndExDto> paramDtos);

	void saveConsentFormEx(ClipFormDto formDto, Long formVersion);

	List<HashMap> getList_all(ClipFormDto parameterDto, Boolean eForm_setPublicationYN);

	void updateClipForm(ClipFormDto formDto);

}
