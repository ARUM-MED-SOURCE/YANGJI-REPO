package kr.co.clipsoft.repository.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.clipsoft.repository.model.ClipActionGroupAuthDto;
import kr.co.clipsoft.repository.model.ClipUserDto;
import kr.co.clipsoft.repository.model.ClipUserGroupDto;

@Repository("clipActionGroupAuthDao")
public class ClipActionGroupAuthDao {
	@Autowired
	private SqlSession sqlSession;
	
	public int insert(ClipActionGroupAuthDto dto) {
		return sqlSession.insert("CLIP_ACTION_GROUP_AUTH.insert", dto);
	}
	
	public int delete(ClipActionGroupAuthDto dto) {
		return sqlSession.insert("CLIP_ACTION_GROUP_AUTH.delete", dto);
	}
	
	public ClipActionGroupAuthDto get(ClipActionGroupAuthDto dto) {
		return sqlSession.selectOne("CLIP_ACTION_GROUP_AUTH.get", dto);
	}
	
	public List<ClipActionGroupAuthDto> getList(ClipActionGroupAuthDto dto) {
		return sqlSession.selectList("CLIP_ACTION_GROUP_AUTH.getList", dto);
	}
	
	public List<ClipUserGroupDto> getAddGroupList(ClipActionGroupAuthDto dto) {
		return sqlSession.selectList("CLIP_ACTION_GROUP_AUTH.getAddGroupList", dto);
	}
}
