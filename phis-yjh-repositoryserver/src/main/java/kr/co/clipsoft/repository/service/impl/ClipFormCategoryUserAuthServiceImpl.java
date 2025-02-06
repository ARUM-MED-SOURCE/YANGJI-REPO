package kr.co.clipsoft.repository.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import kr.co.clipsoft.repository.ClipMyBatisTransactionManager;
import kr.co.clipsoft.repository.dao.ClipCodeDao;
import kr.co.clipsoft.repository.dao.ClipFormCategoryUserAuthDao;
import kr.co.clipsoft.repository.dao.ClipUserDao;
import kr.co.clipsoft.repository.model.ClipCodeDto;
import kr.co.clipsoft.repository.model.ClipFormCategoryUserAuthDto;
import kr.co.clipsoft.repository.model.ClipUserDto;
import kr.co.clipsoft.repository.service.ClipFormCategoryUserAuthService;
import kr.co.clipsoft.repository.service.ClipMybatisSupport;

@Service("clipFormCategoryUserAuthService")
public class ClipFormCategoryUserAuthServiceImpl extends ClipMybatisSupport implements ClipFormCategoryUserAuthService {

	@Resource(name = "clipFormCategoryUserAuthDao")
	private ClipFormCategoryUserAuthDao clipFormCategoryUserAuthDao;
	
	@Resource(name = "clipCodeDao")
	private ClipCodeDao clipCodeDao;
	
	@Resource(name = "clipUserDao")
	private ClipUserDao clipUserDao;
	
	@Override
	public int newData(ClipFormCategoryUserAuthDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			
			ClipCodeDto codeDto = new ClipCodeDto();
			codeDto.setProductId(dto.getProductId());
			codeDto.setCode(dto.getAuthCode());
			
			ClipUserDto userDto = new ClipUserDto();
			userDto.setProductId(dto.getProductId());
			userDto.setUserId(dto.getUserId());
			
			ClipUserDto validUserDto = clipUserDao.get(userDto);
			ClipCodeDto validCodeDto = clipCodeDao.get(codeDto);
			
			if(validUserDto == null) {
				throw new Exception("존재하지 않는 userId 삽입");
			}
			
			if(validCodeDto == null) {
				throw new Exception("존재하지 않는 code 삽입");
			}
			
			int result = clipFormCategoryUserAuthDao.newData(dto);
			tm.commit();
			
			return result;
		} catch (Exception e) {
			tm.rollback();
		} finally {
			tm.end();
		}
		return 0;
	}

	@Override
	public int delete(ClipFormCategoryUserAuthDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			int deleteCount = clipFormCategoryUserAuthDao.delete(dto);
			tm.commit();
			
			return deleteCount;
		} catch (Exception e) {
			tm.rollback();
		} finally {
			tm.end();
		}
		return 0;
	}

	@Override
	public ClipFormCategoryUserAuthDto get(ClipFormCategoryUserAuthDto dto) {
		return clipFormCategoryUserAuthDao.get(dto);
	}

	@Override
	public List<ClipFormCategoryUserAuthDto> getList(ClipFormCategoryUserAuthDto dto){
		return clipFormCategoryUserAuthDao.getList(dto);
	}
	
	@Override
	public List<ClipUserDto> getAddUserList(ClipFormCategoryUserAuthDto dto){
		return clipFormCategoryUserAuthDao.getAddUserList(dto);
	}
}
