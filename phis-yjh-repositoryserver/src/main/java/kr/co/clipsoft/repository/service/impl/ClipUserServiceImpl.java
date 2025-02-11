package kr.co.clipsoft.repository.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import kr.co.clipsoft.repository.ClipMyBatisTransactionManager;
import kr.co.clipsoft.repository.dao.ClipFormCategoryUserAuthDao;
import kr.co.clipsoft.repository.dao.ClipFormUserAuthDao;
import kr.co.clipsoft.repository.dao.ClipUserDao;
import kr.co.clipsoft.repository.model.ClipUserDto;
import kr.co.clipsoft.repository.model.ClipSearchDto;
import kr.co.clipsoft.repository.service.ClipMybatisSupport;
import kr.co.clipsoft.repository.service.ClipUserService;
import kr.co.clipsoft.repository.service.module.DefaultAuthGenerateComponent;

@Service("clipUserService")
public class ClipUserServiceImpl extends ClipMybatisSupport implements ClipUserService {
	
	private static final Logger logger = LoggerFactory.getLogger(ClipUserServiceImpl.class);
	
	@Value("#{projectProperties['server.defaultFormUserAuthCodeOnNewUser']}")
	private String defaultFormUserAuthCodeOnNewUser;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Resource(name="clipUserDao")
	private ClipUserDao clipUserDao;
	
	@Resource(name="clipFormUserAuthDao")
	private ClipFormUserAuthDao clipFormUserAuthDao;
	
	@Resource(name="clipFormCategoryUserAuthDao")
	private ClipFormCategoryUserAuthDao clipFormCategoryUserAuthDao;
	
	@Autowired
	private DefaultAuthGenerateComponent defaultAuthGenerateComponent;
	
	@Override
	public int insert(ClipUserDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			
			String password = bCryptPasswordEncoder.encode(dto.getPassword());
			dto.setPassword(password);
			int insertCount = clipUserDao.insert(dto);
			
			defaultAuthGenerateComponent.generateAuth(dto);
			
			tm.commit();
			
			return insertCount;
		} catch (Exception e) {
			logger.error(e.getMessage());
			tm.rollback();
		} finally {
			tm.end();
		}
		return 0;
	}
	
	@Override
	public int update(ClipUserDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			
			if(dto.getPassword() != null && dto.getPassword().length() > 0) {
				String password = bCryptPasswordEncoder.encode(dto.getPassword());
				dto.setPassword(password);
			} else {
				dto.setPassword(null);
			}
			
			if(dto.getName() == null || dto.getName().length() == 0) {
				dto.setName(null);	
			}
			
			int updateCount = clipUserDao.update(dto);
			
			tm.commit();
			
			return updateCount;
		} catch (Exception e) {
			logger.error(e.getMessage());
			tm.rollback();
		} finally {
			tm.end();
		}
		return 0;
	}

	@Override
	public ClipUserDto get(ClipUserDto dto) {
		return clipUserDao.get(dto);
	}

	@Override
	public List<ClipUserDto> getList(ClipSearchDto dto) {
		return clipUserDao.getList(dto);
	}
	
	@Override
	public int delete(ClipUserDto dto) {
		return clipUserDao.delete(dto);
	}	
}
