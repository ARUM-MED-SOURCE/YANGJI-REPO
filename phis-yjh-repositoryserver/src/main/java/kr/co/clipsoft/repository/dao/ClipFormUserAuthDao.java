package kr.co.clipsoft.repository.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.clipsoft.repository.model.ClipFormUserAuthDto;
import kr.co.clipsoft.repository.model.ClipUserDto;

@Repository("clipFormUserAuthDao")
public class ClipFormUserAuthDao {
	@Autowired
	private SqlSession sqlSession;
	
	public int newData(ClipFormUserAuthDto dto) {
		return sqlSession.insert("CLIP_FORM_USER_AUTH.new", dto); 
	}
	
	public int insertDefaultAuth_newForm(ClipFormUserAuthDto dto) {
		return sqlSession.insert("CLIP_FORM_USER_AUTH.insertDefaultAuth_newForm", dto);
	}
	
	public int insertDefaultAuth_newUser(ClipFormUserAuthDto dto, String authCode) {
		HashMap parameter = new HashMap();
		parameter.put("productId", dto.getProductId());
		parameter.put("userId", dto.getUserId());
		parameter.put("authCode", authCode);
		parameter.put("createUserId", dto.getCreateUserId());
		parameter.put("updateUserId", dto.getUpdateUserId());
		
		return sqlSession.insert("CLIP_FORM_USER_AUTH.insertDefaultAuth_newUser", parameter);
	}
	
	public int delete(ClipFormUserAuthDto dto) {
		return sqlSession.delete("CLIP_FORM_USER_AUTH.delete", dto);
	}
	
	public ClipFormUserAuthDto get(ClipFormUserAuthDto dto) {
		return sqlSession.selectOne("CLIP_FORM_USER_AUTH.get", dto);
	}
	
	public List<ClipFormUserAuthDto> getList(ClipFormUserAuthDto dto) {
		return sqlSession.selectList("CLIP_FORM_USER_AUTH.getList", dto);
	}
	
	public List<ClipUserDto> getAddUserList(ClipFormUserAuthDto dto) {
		return sqlSession.selectList("CLIP_FORM_USER_AUTH.getAddUserList", dto);
	}
	
	public List<HashMap> getList_auth_getList_form(Long productId, String userId) {
		HashMap<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("productId", productId);
		parameter.put("userId", userId);
		
		return sqlSession.selectList("CLIP_FORM_USER_AUTH.getList_auth_getList_form", parameter);
	}
	
	public List<HashMap> getList_auth_getList_category(Long productId, String userId) {
		HashMap<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("productId", productId);
		parameter.put("userId", userId);
		
		return sqlSession.selectList("CLIP_FORM_USER_AUTH.getList_auth_getList_category", parameter);
	}
	
}
