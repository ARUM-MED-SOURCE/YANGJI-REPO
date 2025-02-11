package kr.co.clipsoft.biz.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.clipsoft.biz.model.NuFormAndExDto;
import kr.co.clipsoft.biz.model.NuFormDataDto;
import kr.co.clipsoft.biz.model.NuTempDataDto;
import kr.co.clipsoft.repository.model.ClipManageIdDto;

@Repository("nuFormDataDao")
public class NuFormDataDao {
	@Autowired
	private SqlSession sqlSession;

	public int insert(NuFormDataDto parameter) {
		return sqlSession.insert("NU_FORM_DATA.insert", parameter);
	}

	public int insertMig(NuFormDataDto parameter) {
		return sqlSession.insert("NU_FORM_DATA.insertMig", parameter);
	}

	public int updatePublication(NuFormDataDto parameter) {
		return sqlSession.update("NU_FORM_DATA.updatePublication", parameter);
	}

	public int updateCommitComment(NuFormAndExDto parameter) {
		return sqlSession.update("NU_FORM_DATA.updateCommitComment", parameter);
	}

	public Long getFormLastVersion(NuFormDataDto parameter) {
		return sqlSession.selectOne("NU_FORM_DATA.getFormLastVersion", parameter);
	}

	public int insertNuTempData(NuTempDataDto parameter) {
		return sqlSession.insert("NU_FORM_DATA.insertenUFormData", parameter);
	}

	public int insertNewManageId(ClipManageIdDto manageDto) {
		return sqlSession.insert("NU_FORM_DATA.insertNewManageId", manageDto);
	}

	public NuTempDataDto getNuTempFormData(NuTempDataDto parameter) {
		return sqlSession.selectOne("NU_FORM_DATA.getNuTempFormData", parameter);
	}
}
