package kr.co.clipsoft.repository.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.clipsoft.repository.model.ClipFormUserGroupAuthDto;
import kr.co.clipsoft.repository.model.ClipUserGroupDto;

@Repository("clipFormUserGroupAuthDao")
public class ClipFormUserGroupAuthDao {
	@Autowired
	private SqlSession sqlSession;
	
	public int newData(ClipFormUserGroupAuthDto dto) {
		return sqlSession.insert("CLIP_FORM_USER_GROUP_AUTH.new", dto); 
	}
	
	public int insertDefaultAuth_allForm(ClipFormUserGroupAuthDto dto, String authCode) {
		HashMap parameter = new HashMap();
		parameter.put("productId", dto.getProductId());
		parameter.put("userGroupId", dto.getUserGroupId());
		parameter.put("authCode", authCode);
		parameter.put("createUserId", dto.getCreateUserId());
		parameter.put("updateUserId", dto.getUpdateUserId());
		
		return sqlSession.insert("CLIP_FORM_USER_GROUP_AUTH.insertDefaultAuth_allForm", parameter);
	}
	
	public int insertDefaultAuth_newForm(ClipFormUserGroupAuthDto dto) {
		return sqlSession.insert("CLIP_FORM_USER_GROUP_AUTH.insertDefaultAuth_newForm", dto);
	}
	
	public int delete(ClipFormUserGroupAuthDto dto) {
		return sqlSession.delete("CLIP_FORM_USER_GROUP_AUTH.delete", dto);
	}
	
	public int delete_userGroupDto(ClipUserGroupDto dto) {
		return sqlSession.delete("CLIP_FORM_USER_GROUP_AUTH.delete_userGroupDto", dto);
	}
	
	public ClipFormUserGroupAuthDto get(ClipFormUserGroupAuthDto dto) {
		return sqlSession.selectOne("CLIP_FORM_USER_GROUP_AUTH.get", dto);
	}
	
	public List<ClipFormUserGroupAuthDto> getList(ClipFormUserGroupAuthDto dto) {
		return sqlSession.selectList("CLIP_FORM_USER_GROUP_AUTH.getList", dto);
	}
	
	public List<ClipUserGroupDto> getAddGroupList(ClipFormUserGroupAuthDto dto) {
		return sqlSession.selectList("CLIP_FORM_USER_GROUP_AUTH.getAddGroupList", dto);
	}
}
