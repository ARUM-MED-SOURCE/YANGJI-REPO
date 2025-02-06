package kr.co.clipsoft.repository.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.clipsoft.repository.model.ClipActionGroupAuthDto;
import kr.co.clipsoft.repository.model.ClipActionUserAuthDto;
import kr.co.clipsoft.repository.model.ClipUserDto;
import kr.co.clipsoft.repository.model.ClipUserGroupDto;

@Repository("clipActionAuthDao")
public class ClipActionAuthDao {
	@Autowired
	private SqlSession sqlSession;
	
	public List<HashMap> getList_authAction(ClipUserDto dto) {
		return sqlSession.selectList("CLIP_ACTION_USER_AUTH.getList_auth_action", dto);
	}
}
