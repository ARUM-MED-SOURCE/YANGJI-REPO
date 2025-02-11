package kr.co.clipsoft.repository.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kr.co.clipsoft.repository.ClipMyBatisTransactionManager;
import kr.co.clipsoft.repository.dao.ClipFormCategoryGroupAuthDao;
import kr.co.clipsoft.repository.dao.ClipFormUserGroupAuthDao;
import kr.co.clipsoft.repository.dao.ClipUserGroupDao;
import kr.co.clipsoft.repository.model.ClipSearchDto;
import kr.co.clipsoft.repository.model.ClipUserGroupDto;
import kr.co.clipsoft.repository.service.ClipMybatisSupport;
import kr.co.clipsoft.repository.service.ClipUserGroupService;
import kr.co.clipsoft.repository.service.module.DefaultAuthGenerateComponent;

@Service("clipUserGroupService")
public class ClipUserGroupServiceImpl extends ClipMybatisSupport implements ClipUserGroupService {

	@Value("#{projectProperties['server.defaultCategoryGroupAuthCodeOnNewGroup']}")
	private String defaultCategoryGroupAuthCodeOnNewGroup;
	
	@Value("#{projectProperties['server.defaultFormGroupAuthCodeOnNewGroup']}")
	private String defaultFormGroupAuthCodeOnNewGroup;
	
	@Resource(name="clipUserGroupDao")
	private ClipUserGroupDao clipUserGroupDao;
	
	@Resource(name="clipFormCategoryGroupAuthDao")
	private ClipFormCategoryGroupAuthDao clipFormCategoryGroupAuthDao;
	
	@Resource(name="clipFormUserGroupAuthDao")
	private ClipFormUserGroupAuthDao clipFormUserGroupAuthDao;
	
	@Autowired
	private DefaultAuthGenerateComponent defaultAuthGenerateComponent;
	
	@Override
	public int newData(ClipUserGroupDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			int insertCount = clipUserGroupDao.insert(dto);
			tm.commit();
			
			defaultAuthGenerateComponent.generateAuth(dto);
			
			return insertCount;
		} catch (Exception e) {
			tm.rollback();
		} finally {
			tm.end();
		}
		return 0;
	}

	@Override
	public int update(ClipUserGroupDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			int insertCount = clipUserGroupDao.update(dto);
			tm.commit();
			
			return insertCount;
		} catch (Exception e) {
			tm.rollback();
		} finally {
			tm.end();
		}
		return 0;
	}
	
	@Override
	public int update_useYN(ClipUserGroupDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			int insertCount = clipUserGroupDao.update_useYN(dto);
			tm.commit();
			
			return insertCount;
		} catch (Exception e) {
			tm.rollback();
		} finally {
			tm.end();
		}
		return 0;
	}

	@Override
	public ClipUserGroupDto get(ClipUserGroupDto dto) {
		return clipUserGroupDao.get(dto);
	}
	
	public List<ClipUserGroupDto> getList(ClipSearchDto dto) {
		return clipUserGroupDao.getList(dto);
	}

}
