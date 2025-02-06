package kr.co.clipsoft.repository.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.clipsoft.repository.model.ClipManageIdDto;

@Repository("clipManageIdDao")
public class ClipManageIdDao {
	@Autowired
	private SqlSession sqlSession;
	
	public ClipManageIdDto get(ClipManageIdDto dto) {
		return sqlSession.selectOne("CLIP_MANAGE_ID.get", dto);
	}
	
	public int increaseValue(ClipManageIdDto dto) {
		return sqlSession.update("CLIP_MANAGE_ID.increaseValue", dto);
	}
}
