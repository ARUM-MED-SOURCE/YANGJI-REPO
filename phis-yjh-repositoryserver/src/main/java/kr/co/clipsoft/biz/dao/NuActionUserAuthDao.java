package kr.co.clipsoft.biz.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.clipsoft.biz.model.NuActionUserAuthDto;

@Repository("nuActionUserAuthDao")
public class NuActionUserAuthDao {
	@Autowired
	private SqlSession sqlSession;

	public int save(NuActionUserAuthDto dto) {
		return sqlSession.insert("NU_ACTION_USER_AUTH.save", dto);
	}
}
