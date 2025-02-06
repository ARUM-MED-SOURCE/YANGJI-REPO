package kr.co.clipsoft.repository.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.clipsoft.repository.model.ClipSearchDto;
import kr.co.clipsoft.repository.model.ClipUserDto;

@Repository("clipUserDao")
public class ClipUserDao {
	@Autowired
	private SqlSession sqlSession;
	
	public int insert(ClipUserDto dto) {
		return sqlSession.insert("CLIP_USER.insert", dto);
	}
	
	public int update(ClipUserDto dto) {
		return sqlSession.update("CLIP_USER.update", dto);
	}
	
	public ClipUserDto get(ClipUserDto dto) {
		return sqlSession.selectOne("CLIP_USER.get", dto);
	}
	
	public List<ClipUserDto> getList(ClipSearchDto dto) {
		return sqlSession.selectList("CLIP_USER.getList", dto);
	}
	
	public int delete(ClipUserDto dto) {
		return sqlSession.delete("CLIP_USER.delete", dto);
	}	
}
