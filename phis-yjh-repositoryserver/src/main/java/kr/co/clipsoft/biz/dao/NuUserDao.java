package kr.co.clipsoft.biz.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.clipsoft.repository.model.ClipUserDto;

@Repository("nuUserDao")
public class NuUserDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public int insertUser(ClipUserDto userDto)
	{
		return sqlSession.insert("NU_USER.insertUser", userDto);
	}
	
	public ClipUserDto getUser(ClipUserDto userDto)
	{
		return sqlSession.selectOne("NU_USER.getUser", userDto);
	}
}
