package kr.co.clipsoft.repository.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import kr.co.clipsoft.repository.model.ClipFormAndDataDto;
import kr.co.clipsoft.repository.model.ClipFormCategoryDto;
import kr.co.clipsoft.repository.model.ClipFormDto;

@Repository("clipFormDao")
public class ClipFormDao {
	@Autowired
	private SqlSession sqlSession;
	
	@Value("#{projectProperties['server.enableCheckPublicationYN']}")
	private String enableCheckPublicationYN;
	
	@Value("#{projectProperties['server.enableCheckUseYN']}")
	private String enableCheckUseYN;
	
	public int insert(ClipFormDto dto) {
		return sqlSession.insert("CLIP_FORM.insert", dto);
	}
	
	public int update(ClipFormDto dto) {
		return sqlSession.update("CLIP_FORM.update", dto);
	}
	
	public int updateUse(ClipFormDto parameter) {
		return sqlSession.update("CLIP_FORM.updateUse", parameter);
	}
	
	public int updateName(ClipFormDto parameter) {
		return sqlSession.update("CLIP_FORM.updateName", parameter);
	}

	public int updateCategory(ClipFormDto parameter) {
		return sqlSession.update("CLIP_FORM.updateCategory", parameter);
	}

	public ClipFormDto getForm(ClipFormDto formDto) {
		HashMap parameter = new HashMap();
		parameter.put("productId", formDto.getProductId());
		parameter.put("formId", formDto.getFormId());
		parameter.put("formType", formDto.getFormType());
		
		parameter.put("enableCheckUseYN", enableCheckUseYN);
			
		return sqlSession.selectOne("CLIP_FORM.getForm", parameter);
	}
	
	public ClipFormAndDataDto get(ClipFormDto formDto, Long formVersion, Boolean eForm_setPublicationYN) {
		HashMap parameter = new HashMap();
		parameter.put("productId", formDto.getProductId());
		parameter.put("formId", formDto.getFormId());
		parameter.put("formVersion", formVersion);
		parameter.put("formType", formDto.getFormType());
		
		parameter.put("enableCheckUseYN", enableCheckUseYN);
		
		if(enableCheckPublicationYN.equals("true")) {
			parameter.put("eForm_setPublicationYN", eForm_setPublicationYN.toString());
		} else {
			parameter.put("eForm_setPublicationYN", "true");
		}
		
		return sqlSession.selectOne("CLIP_FORM.get", parameter);
	}
	
	public ClipFormAndDataDto getRecent(ClipFormDto formDto, boolean includeFormData, Boolean eForm_setPublicationYN) {
		HashMap parameter = new HashMap();
		parameter.put("dto", formDto);
		parameter.put("includeFormData", includeFormData);
		
		parameter.put("enableCheckUseYN", enableCheckUseYN);
		
		if(enableCheckPublicationYN.equals("true")) {
			parameter.put("eForm_setPublicationYN", eForm_setPublicationYN.toString());
		} else {
			parameter.put("eForm_setPublicationYN", "true");
		}
		
		return sqlSession.selectOne("CLIP_FORM.getRecent", parameter);
	}
	
	public List<ClipFormAndDataDto> getList_allVersion(ClipFormDto formDto, Boolean eForm_setPublicationYN) {
		HashMap<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("productId", formDto.getProductId());
		parameter.put("formId", formDto.getFormId());
		parameter.put("formType", formDto.getFormType());
		parameter.put("formName", formDto.getFormName());
		parameter.put("useYN", formDto.getUseYN());
		parameter.put("formCategoryId", formDto.getFormCategoryId());
		parameter.put("createDate", formDto.getCreateDate());
		parameter.put("createUserId", formDto.getCreateUserId());
		parameter.put("updateDate", formDto.getUpdateDate());
		parameter.put("updateUserId", formDto.getUpdateUserId());
		
		parameter.put("enableCheckUseYN", enableCheckUseYN);
		
		if(enableCheckPublicationYN.equals("true")) {
			parameter.put("eForm_setPublicationYN", eForm_setPublicationYN.toString());
		} else {
			parameter.put("eForm_setPublicationYN", "true");
		}
		
		return sqlSession.selectList("CLIP_FORM.getList_allVersion", parameter);
	}
	
	public List<HashMap> getList_all(ClipFormDto formDto, Boolean eForm_setPublicationYN) {
		HashMap<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("productId", formDto.getProductId());
		parameter.put("formType", formDto.getFormType());
		
		parameter.put("enableCheckUseYN", enableCheckUseYN);
		
		if(enableCheckPublicationYN.equals("true")) {
			parameter.put("eForm_setPublicationYN", eForm_setPublicationYN.toString());
		} else {
			parameter.put("eForm_setPublicationYN", "true");
		}
		
		return sqlSession.selectList("CLIP_FORM.getList_all", parameter);
	}
	
	public List<ClipFormDto> getList_allRegist(ClipFormDto formDto, Boolean eForm_setPublicationYN) {
		HashMap<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("productId", formDto.getProductId());
		parameter.put("formType", formDto.getFormType());
		parameter.put("formName", formDto.getFormName());
		
		parameter.put("enableCheckUseYN", enableCheckUseYN);
		
		if(enableCheckPublicationYN.equals("true")) {
			parameter.put("eForm_setPublicationYN", eForm_setPublicationYN.toString());
		} else {
			parameter.put("eForm_setPublicationYN", "true");
		}
		
		return sqlSession.selectList("CLIP_FORM.getList_allRegist", parameter);
	}
	
	public List<ClipFormDto> getList_allCategoryForm(ClipFormCategoryDto formCategoryDto, Boolean eForm_setPublicationYN) {
		HashMap<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("productId", formCategoryDto.getProductId());
		parameter.put("formCategoryId", formCategoryDto.getFormCategoryId());
		
		parameter.put("enableCheckUseYN", enableCheckUseYN);
		
		if(enableCheckPublicationYN.equals("true")) {
			parameter.put("eForm_setPublicationYN", eForm_setPublicationYN.toString());
		} else {
			parameter.put("eForm_setPublicationYN", "true");
		}
		
		return sqlSession.selectList("CLIP_FORM.getList_allCategoryform", parameter);
	}
}
