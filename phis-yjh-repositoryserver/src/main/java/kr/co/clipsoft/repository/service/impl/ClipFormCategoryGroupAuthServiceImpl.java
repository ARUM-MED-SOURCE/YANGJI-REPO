package kr.co.clipsoft.repository.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.co.clipsoft.repository.ClipMyBatisTransactionManager;
import kr.co.clipsoft.repository.dao.ClipCodeDao;
import kr.co.clipsoft.repository.dao.ClipFormCategoryDao;
import kr.co.clipsoft.repository.dao.ClipFormCategoryGroupAuthDao;
import kr.co.clipsoft.repository.dao.ClipUserGroupDao;
import kr.co.clipsoft.repository.model.ClipCodeDto;
import kr.co.clipsoft.repository.model.ClipFormCategoryDto;
import kr.co.clipsoft.repository.model.ClipFormCategoryGroupAuthDto;
import kr.co.clipsoft.repository.model.ClipUserGroupDto;
import kr.co.clipsoft.repository.service.ClipFormCategoryGroupAuthService;
import kr.co.clipsoft.repository.service.ClipMybatisSupport;

@Service("clipFormCategoryGroupAuthService")
public class ClipFormCategoryGroupAuthServiceImpl extends ClipMybatisSupport implements ClipFormCategoryGroupAuthService {

	private static final Logger logger = LoggerFactory.getLogger(ClipFormCategoryGroupAuthServiceImpl.class);
	
	@Resource(name = "clipFormCategoryGroupAuthDao")
	private ClipFormCategoryGroupAuthDao clipFormCategoryGroupAuthDao;
	
	@Resource(name = "clipCodeDao")
	private ClipCodeDao clipCodeDao;
	
	@Resource(name = "clipUserGroupDao")
	private ClipUserGroupDao clipUserGroupDao;
	
	@Resource(name = "clipFormCategoryDao")
	private ClipFormCategoryDao clipFormCategoryDao;
	
	@Override
	public int newData(ClipFormCategoryGroupAuthDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			
			ClipCodeDto codeDto = new ClipCodeDto();
			codeDto.setProductId(dto.getProductId());
			codeDto.setCode(dto.getAuthCode());
			
			ClipUserGroupDto userGroupDto = new ClipUserGroupDto();
			userGroupDto.setProductId(dto.getProductId());
			userGroupDto.setUserGroupId(dto.getUserGroupId());
			
			ClipFormCategoryDto formCategoryDto = new ClipFormCategoryDto();
			formCategoryDto.setProductId(dto.getProductId());
			formCategoryDto.setFormCategoryId(dto.getFormCategoryId());
			
			ClipUserGroupDto validUserGroupDto = clipUserGroupDao.get(userGroupDto);
			ClipCodeDto validCodeDto = clipCodeDao.get(codeDto);
			ClipFormCategoryDto validFormCategoryDto = clipFormCategoryDao.get(formCategoryDto);
			
			if(validUserGroupDto == null) {
				throw new Exception("존재하지 않는 userGroupId 삽입");
			}
			
			if(validCodeDto == null) {
				throw new Exception("존재하지 않는 code 삽입");
			}
			
			if(validFormCategoryDto == null) {
				throw new Exception("존재하지 않는 formCategoryId 삽입");
			}
			
			int result = clipFormCategoryGroupAuthDao.newData(dto);
			tm.commit();
			
			return result;
		} catch (Exception e) {
			tm.rollback();
			logger.error(e.getMessage());
		} finally {
			tm.end();
		}
		return 0;
	}

	@Override
	public int delete(ClipFormCategoryGroupAuthDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			int deleteCount = clipFormCategoryGroupAuthDao.delete(dto);
			tm.commit();
			
			return deleteCount;
		} catch (Exception e) {
			tm.rollback();
			logger.error(e.getMessage());
		} finally {
			tm.end();
		}
		return 0;
	}

	@Override
	public ClipFormCategoryGroupAuthDto get(ClipFormCategoryGroupAuthDto dto) {
		return clipFormCategoryGroupAuthDao.get(dto);
	}

	@Override
	public List<ClipFormCategoryGroupAuthDto> getList(ClipFormCategoryGroupAuthDto dto) {
		return clipFormCategoryGroupAuthDao.getList(dto);
	}
	
	public List<ClipUserGroupDto> getAddGroupList(ClipFormCategoryGroupAuthDto dto){
		return clipFormCategoryGroupAuthDao.getAddGroupList(dto);
	}
}
