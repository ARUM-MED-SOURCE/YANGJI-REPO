package kr.co.clipsoft.repository.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import kr.co.clipsoft.repository.ClipMyBatisTransactionManager;
import kr.co.clipsoft.repository.dao.ClipUserGroupMappingDao;
import kr.co.clipsoft.repository.model.ClipSearchDto;
import kr.co.clipsoft.repository.model.ClipUserGroupMappingDto;
import kr.co.clipsoft.repository.service.ClipMybatisSupport;
import kr.co.clipsoft.repository.service.ClipUserGroupMappingService;

@Service("clipUserGroupMappingService")
public class ClipUserGroupMappingServiceImpl extends ClipMybatisSupport implements ClipUserGroupMappingService {

	@Resource(name="clipUserGroupMappingDao")
	private ClipUserGroupMappingDao clipUserGroupMappingDao;
	
	@Override
	public int insert(ClipUserGroupMappingDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			int insertCount = clipUserGroupMappingDao.insert(dto);
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
	public int delete(ClipUserGroupMappingDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			int deleteCount = clipUserGroupMappingDao.delete(dto);
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
	public ClipUserGroupMappingDto get(ClipUserGroupMappingDto dto) {
		return clipUserGroupMappingDao.get(dto);
	}
	
	public List<ClipUserGroupMappingDto> getGroupList(ClipSearchDto dto)
	{
		return clipUserGroupMappingDao.getGroupList(dto);
	}

	public List<ClipUserGroupMappingDto> getUserList(ClipSearchDto dto)
	{
		return clipUserGroupMappingDao.getUserList(dto);
	}
}
