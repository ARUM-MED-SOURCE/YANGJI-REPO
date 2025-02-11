package kr.co.clipsoft.repository.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.co.clipsoft.repository.ClipMyBatisTransactionManager;
import kr.co.clipsoft.repository.dao.ClipCodeDao;
import kr.co.clipsoft.repository.dao.ClipFormDao;
import kr.co.clipsoft.repository.dao.ClipFormUserAuthDao;
import kr.co.clipsoft.repository.dao.ClipFormUserGroupAuthDao;
import kr.co.clipsoft.repository.dao.ClipUserGroupDao;
import kr.co.clipsoft.repository.model.ClipCodeDto;
import kr.co.clipsoft.repository.model.ClipFormDto;
import kr.co.clipsoft.repository.model.ClipFormUserGroupAuthDto;
import kr.co.clipsoft.repository.model.ClipUserGroupDto;
import kr.co.clipsoft.repository.service.ClipFormUserGroupAuthService;
import kr.co.clipsoft.repository.service.ClipMybatisSupport;

@Service("clipFormUserGroupAuthService")
public class ClipFormUserGroupAuthServiceImpl extends ClipMybatisSupport implements ClipFormUserGroupAuthService {

	private static final Logger logger = LoggerFactory.getLogger(ClipFormUserGroupAuthServiceImpl.class);
	
	@Resource(name = "clipFormUserGroupAuthDao")
	private ClipFormUserGroupAuthDao clipFormUserGroupAuthDao;
	
	@Resource(name = "clipCodeDao")
	private ClipCodeDao clipCodeDao;
	
	@Resource(name = "clipUserGroupDao")
	private ClipUserGroupDao clipUserGroupDao;
	
	@Resource(name = "clipFormDao")
	private ClipFormDao clipFormDao;
	
	@Override
	public int newData(ClipFormUserGroupAuthDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			
			ClipCodeDto codeDto = new ClipCodeDto();
			codeDto.setProductId(dto.getProductId());
			codeDto.setCode(dto.getAuthCode());
			
			ClipUserGroupDto userGroupDto = new ClipUserGroupDto();
			userGroupDto.setProductId(dto.getProductId());
			userGroupDto.setUserGroupId(dto.getUserGroupId());
			
			ClipFormDto formDto = new ClipFormDto();
			formDto.setProductId(dto.getProductId());
			formDto.setFormId(dto.getFormId());
			
			ClipUserGroupDto validUserGroupDto = clipUserGroupDao.get(userGroupDto);
			ClipCodeDto validCodeDto = clipCodeDao.get(codeDto);
			ClipFormDto validFormDto = clipFormDao.getForm(formDto);
			
			if(validUserGroupDto == null) {
				throw new Exception("존재하지 않는 userGroupId 삽입");
			}
			
			if(validCodeDto == null) {
				throw new Exception("존재하지 않는 code 삽입");
			}
			
			if(validFormDto == null) {
				throw new Exception("존재하지 않는 formId 삽입");
			}
			
			int result = clipFormUserGroupAuthDao.newData(dto);
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
	public int delete(ClipFormUserGroupAuthDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			int deleteCount = clipFormUserGroupAuthDao.delete(dto);
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
	public ClipFormUserGroupAuthDto get(ClipFormUserGroupAuthDto dto) {
		return clipFormUserGroupAuthDao.get(dto);
	}
	
	@Override
	public List<ClipFormUserGroupAuthDto> getList(ClipFormUserGroupAuthDto dto) {
		return clipFormUserGroupAuthDao.getList(dto);
	}
	
	@Override
	public List<ClipUserGroupDto> getAddGroupList(ClipFormUserGroupAuthDto dto){
		return clipFormUserGroupAuthDao.getAddGroupList(dto);
	}
}
