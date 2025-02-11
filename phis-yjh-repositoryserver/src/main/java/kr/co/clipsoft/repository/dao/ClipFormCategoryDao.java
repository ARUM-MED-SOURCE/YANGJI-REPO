package kr.co.clipsoft.repository.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.clipsoft.repository.model.ClipFormCategoryDto;

@Repository("clipFormCategoryDao")
public class ClipFormCategoryDao {
	@Autowired
	private SqlSession sqlSession;
	
	public int newFormCategory(ClipFormCategoryDto dto) {
		return sqlSession.insert("CLIP_FORM_CATEGORY.newFormCategory", dto);
	}
	
	public int update(ClipFormCategoryDto dto) {
		return sqlSession.update("CLIP_FORM_CATEGORY.update", dto);
	}
	
	public int update_useYN(ClipFormCategoryDto dto) {
		return sqlSession.update("CLIP_FORM_CATEGORY.update_useYN", dto);
	}
	
	public ClipFormCategoryDto get(ClipFormCategoryDto dto) {
		return sqlSession.selectOne("CLIP_FORM_CATEGORY.get", dto);
	}
	
	public List<ClipFormCategoryDto> getList_sameParentFormCategoryId(ClipFormCategoryDto dto) {
		return sqlSession.selectList("CLIP_FORM_CATEGORY.getList_sameParentFormCategoryId", dto);
	}
	
	public List<HashMap> getList_all_categoryAndForm(Long productId, String userId, String itemName) {
		itemName = itemName.length() != 0 ? itemName : "";
		
		HashMap<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("productId", productId);
		parameterMap.put("itemName", itemName);
		parameterMap.put("userId", userId);
		return sqlSession.selectList("CLIP_FORM_CATEGORY.getList_all_categoryAndForm", parameterMap);
	}
	
	public List<ClipFormCategoryDto> getList_regist_category(Long productId, String itemName) {
		itemName = itemName.length() != 0 ? itemName : "";
		
		HashMap<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("productId", productId);
		parameterMap.put("itemName", itemName);
		return sqlSession.selectList("CLIP_FORM_CATEGORY.getList_regist_category", parameterMap);
	}
}
