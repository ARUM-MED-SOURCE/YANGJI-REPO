package kr.co.clipsoft.repository.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.clipsoft.repository.model.ClipFormDataDto;

@Repository("clipFormDataDao")
public class ClipFormDataDao {
	@Autowired
	private SqlSession sqlSession;
	
	public int newFormData(ClipFormDataDto parameter) {
		return sqlSession.insert("CLIP_FORM_DATA.new", parameter);
	}
	
	public int insert(ClipFormDataDto parameter) {
		return sqlSession.insert("CLIP_FORM_DATA.insert", parameter);
	}
	
	public int updateFormData(ClipFormDataDto parameter) {
		return sqlSession.update("CLIP_FORM_DATA.updateFormData", parameter);
	}
	
	public int updatePublication(ClipFormDataDto parameter) {
		return sqlSession.update("CLIP_FORM_DATA.updatePublication", parameter);
	}
	
	public ClipFormDataDto get(ClipFormDataDto parameter) {
		return sqlSession.selectOne("CLIP_FORM_DATA.get", parameter);
	}
	
	public ClipFormDataDto getRecent(ClipFormDataDto parameter) {
		return sqlSession.selectOne("CLIP_FORM_DATA.getRecent", parameter);
	}
	
	public Long getFormLastVersion(ClipFormDataDto parameter) {
		return sqlSession.selectOne("CLIP_FORM_DATA.getFormLastVersion", parameter);
	}
}
