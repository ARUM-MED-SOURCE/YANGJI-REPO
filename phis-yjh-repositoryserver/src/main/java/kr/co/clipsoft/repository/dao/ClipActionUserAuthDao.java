package kr.co.clipsoft.repository.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.clipsoft.repository.model.ClipActionUserAuthDto;
import kr.co.clipsoft.repository.model.ClipUserDto;

@Repository("clipActionUserAuthDao")
public class ClipActionUserAuthDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public int insert(ClipActionUserAuthDto dto) {
		return sqlSession.insert("CLIP_ACTION_USER_AUTH.insert", dto);
	}
	
	public int delete(ClipActionUserAuthDto dto) {
		return sqlSession.delete("CLIP_ACTION_USER_AUTH.delete", dto);
	}
	
	public ClipActionUserAuthDto get(ClipActionUserAuthDto dto) {
		return sqlSession.selectOne("CLIP_ACTION_USER_AUTH.get", dto);
	}
	
	public List<ClipActionUserAuthDto> getList(ClipActionUserAuthDto dto) {
		return sqlSession.selectList("CLIP_ACTION_USER_AUTH.getList", dto);
	}
	
	public List<ClipUserDto> getAddUserList(ClipActionUserAuthDto dto) {
		return sqlSession.selectList("CLIP_ACTION_USER_AUTH.getAddUserList", dto);
	}
}
