package kr.co.clipsoft.repository.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.clipsoft.repository.model.ClipCodeDto;

@Repository("clipCodeDao")
public class ClipCodeDao {
	@Autowired
	private SqlSession sqlSession;
	
	public ClipCodeDto get(ClipCodeDto dto) {
		return sqlSession.selectOne("CLIP_CODE.get", dto);
	}
}
