package kr.co.clipsoft.repository.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.clipsoft.repository.model.ClipSearchDto;
import kr.co.clipsoft.repository.model.ClipUserGroupMappingDto;

@Repository("clipUserGroupMappingDao")
public class ClipUserGroupMappingDao {
	@Autowired
	private SqlSession sqlSession;
	
	public int insert(ClipUserGroupMappingDto dto) {
		return sqlSession.insert("CLIP_USER_GROUP_MAPPING.insert", dto);
	}
	
	public int delete(ClipUserGroupMappingDto dto) {
		return sqlSession.delete("CLIP_USER_GROUP_MAPPING.delete", dto);
	}
	
	public ClipUserGroupMappingDto get(ClipUserGroupMappingDto dto) {
		return sqlSession.selectOne("CLIP_USER_GROUP_MAPPING.get", dto);
	} 
	
	public List<ClipUserGroupMappingDto> getGroupList(ClipSearchDto dto){
		return sqlSession.selectList("CLIP_USER_GROUP_MAPPING.getGroupList", dto);
	}
	
	public List<ClipUserGroupMappingDto> getUserList(ClipSearchDto dto){
		return sqlSession.selectList("CLIP_USER_GROUP_MAPPING.getUserList", dto);
	}
}
