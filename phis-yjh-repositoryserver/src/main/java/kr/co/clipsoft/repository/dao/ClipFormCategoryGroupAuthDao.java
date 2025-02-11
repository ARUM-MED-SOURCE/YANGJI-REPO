package kr.co.clipsoft.repository.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.clipsoft.repository.model.ClipFormCategoryGroupAuthDto;
import kr.co.clipsoft.repository.model.ClipUserGroupDto;

@Repository("clipFormCategoryGroupAuthDao")
public class ClipFormCategoryGroupAuthDao {
	@Autowired
	private SqlSession sqlSession;
	
	public int newData(ClipFormCategoryGroupAuthDto dto) {
		return sqlSession.insert("CLIP_FORM_CATEGORY_GROUP_AUTH.new", dto); 
	}
	
	public int insertDefaultAuth_newGroup(ClipFormCategoryGroupAuthDto dto) {
		return sqlSession.insert("CLIP_FORM_CATEGORY_GROUP_AUTH.insertDefaultAuth_newGroup", dto);
	}
	
	public int insertDefaultAuth_newCategory(ClipFormCategoryGroupAuthDto dto) {
		return sqlSession.insert("CLIP_FORM_CATEGORY_GROUP_AUTH.insertDefaultAuth_newCategory", dto);
	}
	
	public int delete(ClipFormCategoryGroupAuthDto dto) {
		return sqlSession.delete("CLIP_FORM_CATEGORY_GROUP_AUTH.delete", dto);
	}
	
	public int delete_userGroupDto(ClipUserGroupDto dto) {
		return sqlSession.delete("CLIP_FORM_CATEGORY_GROUP_AUTH.delete_userGroupDto", dto);
	}
	
	public int delete_formCategoryDto(ClipUserGroupDto dto) {
		return sqlSession.delete("CLIP_FORM_CATEGORY_GROUP_AUTH.delete_formCategoryDto", dto);
	}
	
	public ClipFormCategoryGroupAuthDto get(ClipFormCategoryGroupAuthDto dto) {
		return sqlSession.selectOne("CLIP_FORM_CATEGORY_GROUP_AUTH.get", dto);
	}
	
	public List<ClipFormCategoryGroupAuthDto> getList(ClipFormCategoryGroupAuthDto dto) {
		return sqlSession.selectList("CLIP_FORM_CATEGORY_GROUP_AUTH.getList", dto);
	}
	
	public List<ClipUserGroupDto> getAddGroupList(ClipFormCategoryGroupAuthDto dto) {
		return sqlSession.selectList("CLIP_FORM_CATEGORY_GROUP_AUTH.getAddGroupList", dto);
	}
}
