package kr.co.clipsoft.biz.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.co.clipsoft.biz.model.NuDocumentDto;
import kr.co.clipsoft.biz.model.NuFormAndDataDto;
import kr.co.clipsoft.biz.model.NuFormAndExDto;
import kr.co.clipsoft.biz.model.NuFormDto;
import kr.co.clipsoft.repository.model.ClipFormDto;

@Repository("nuFormDao")
public class NuFormDao {

	@Autowired
	private SqlSession sqlSession;

	public NuDocumentDto getDocumentCode(NuDocumentDto documentDto) {

		return sqlSession.selectOne("NU_FORM.getDocumentCode", documentDto);
	}

	public NuDocumentDto getFormId(NuDocumentDto documentDto) {
		return sqlSession.selectOne("NU_FORM.getFormId", documentDto);
	}

	public int insertDocumentCode(NuDocumentDto documentDto) {
		return sqlSession.insert("NU_FORM.insertDocumentCode", documentDto);
	}

	public int updateDocumentCode(NuDocumentDto documentDto) {
		return sqlSession.insert("NU_FORM.updateDocumentCode", documentDto);
	}

	public int insert(NuFormDto dto) {
		return sqlSession.insert("NU_FORM.insert", dto);
	}

	public int insertMig(NuFormDto dto) {
		return sqlSession.insert("NU_FORM.insertMig", dto);
	}

	public NuFormAndDataDto get(NuFormDto formDto, Long formVersion) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("productId", formDto.getProductId());
		map.put("formId", formDto.getFormId());
		map.put("formVersion", formVersion);
		map.put("formType", formDto.getFormType());

		return sqlSession.selectOne("NU_FORM.get", map);
	}

	public NuFormAndDataDto getRecent(NuFormDto formDto) {
		return sqlSession.selectOne("NU_FORM.getRecent", formDto);
	}

	public List<NuFormAndDataDto> getList_allVersion(NuFormDto formDto) {
		return sqlSession.selectList("NU_FORM.getList_allVersion", formDto);
	}

	public List<NuDocumentDto> getList_document(NuFormDto parameter) {
		return sqlSession.selectList("NU_FORM.getList_document", parameter);
	}

	public List<NuFormAndExDto> getClipFormAndConsentFormEx(NuFormAndExDto parameter) {
		return sqlSession.selectList("NU_FORM.getClipFormAndConsentFormEx", parameter);
	}

	public List<HashMap> getList_all(HashMap<String, Object> parameter) {
		return sqlSession.selectList("NU_FORM.getList_all", parameter);
	}

	public int updateClipForm(ClipFormDto formDto) {
		return sqlSession.update("NU_FORM.updateClipForm", formDto);
	}

}
