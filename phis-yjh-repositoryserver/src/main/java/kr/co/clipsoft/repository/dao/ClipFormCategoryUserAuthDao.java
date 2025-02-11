package kr.co.clipsoft.repository.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.clipsoft.repository.model.ClipFormCategoryUserAuthDto;
import kr.co.clipsoft.repository.model.ClipUserDto;

@Repository("clipFormCategoryUserAuthDao")
public class ClipFormCategoryUserAuthDao {
	@Autowired
	private SqlSession sqlSession;
	
	public int newData(ClipFormCategoryUserAuthDto dto) {
		return sqlSession.insert("CLIP_FORM_CATEGORY_USER_AUTH.new", dto); 
	}
	
	public int insertDefaultAuth_newCategory(ClipFormCategoryUserAuthDto dto) {
		return sqlSession.insert("CLIP_FORM_CATEGORY_USER_AUTH.insertDefaultAuth_newCategory", dto);
	}
	
	public int insertDefaultAuth_newUser(ClipFormCategoryUserAuthDto dto, String authCode) {
		HashMap parameter = new HashMap();
		parameter.put("productId", dto.getProductId());
		parameter.put("userId", dto.getUserId());
		parameter.put("authCode", authCode);
		parameter.put("createUserId", dto.getCreateUserId());
		parameter.put("updateUserId", dto.getUpdateUserId());
		
		return sqlSession.insert("CLIP_FORM_CATEGORY_USER_AUTH.insertDefaultAuth_newUser", parameter);
	}
	
	public int delete(ClipFormCategoryUserAuthDto dto) {
		return sqlSession.delete("CLIP_FORM_CATEGORY_USER_AUTH.delete", dto);
	}
	
	public ClipFormCategoryUserAuthDto get(ClipFormCategoryUserAuthDto dto) {
		return sqlSession.selectOne("CLIP_FORM_CATEGORY_USER_AUTH.get", dto);
	}
	
	public List<ClipFormCategoryUserAuthDto> getList(ClipFormCategoryUserAuthDto dto) {
		return sqlSession.selectList("CLIP_FORM_CATEGORY_USER_AUTH.getList", dto);
	}
	
	public List<ClipUserDto> getAddUserList(ClipFormCategoryUserAuthDto dto) {
		return sqlSession.selectList("CLIP_FORM_CATEGORY_USER_AUTH.getAddUserList", dto);
	}
}
