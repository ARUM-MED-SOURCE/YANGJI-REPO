package kr.co.clipsoft.repository.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.clipsoft.repository.model.ClipSearchDto;
import kr.co.clipsoft.repository.model.ClipUserDto;
import kr.co.clipsoft.repository.model.ClipUserGroupDto;

@Repository("clipUserGroupDao")
public class ClipUserGroupDao {
	@Autowired
	private SqlSession sqlSession;
	
	public int insert(ClipUserGroupDto dto) {
		return sqlSession.insert("CLIP_USER_GROUP.insert", dto);
	}
	
	public int update(ClipUserGroupDto dto) {
		return sqlSession.update("CLIP_USER_GROUP.update", dto);
	}
	
	public int update_useYN(ClipUserGroupDto dto) {
		return sqlSession.update("CLIP_USER_GROUP.update_useYN", dto);
	}
	
	public ClipUserGroupDto get(ClipUserGroupDto dto) {
		return sqlSession.selectOne("CLIP_USER_GROUP.get", dto);
	}
	
	public List<ClipUserGroupDto> getList(ClipSearchDto dto){
		return sqlSession.selectList("CLIP_USER_GROUP.getList", dto);
	}
}
