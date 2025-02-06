package kr.co.clipsoft.repository.service.impl;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import kr.co.clipsoft.repository.ClipMyBatisTransactionManager;
import kr.co.clipsoft.repository.dao.ClipActionAuthDao;
import kr.co.clipsoft.repository.dao.ClipActionGroupAuthDao;
import kr.co.clipsoft.repository.dao.ClipActionUserAuthDao;
import kr.co.clipsoft.repository.model.ClipActionGroupAuthDto;
import kr.co.clipsoft.repository.model.ClipActionUserAuthDto;
import kr.co.clipsoft.repository.model.ClipUserDto;
import kr.co.clipsoft.repository.model.ClipUserGroupDto;
import kr.co.clipsoft.repository.service.ClipActionAuthService;
import kr.co.clipsoft.repository.service.ClipMybatisSupport;

@Service("clipActionAuthService")
public class ClipActionAuthServiceImpl extends ClipMybatisSupport implements ClipActionAuthService {

	private static final Logger logger = LoggerFactory.getLogger(ClipActionAuthServiceImpl.class);
	
	@Resource(name = "clipActionAuthDao")
	private ClipActionAuthDao clipActionAuthDao;
	
	@Resource(name = "clipActionGroupAuthDao")
	private ClipActionGroupAuthDao clipActionGroupAuthDao;
	
	@Resource(name = "clipActionUserAuthDao")
	private ClipActionUserAuthDao clipActionUserAuthDao;
	
	@Override
	public int insertActionGroupAuth(ClipActionGroupAuthDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			int deleteCount = clipActionGroupAuthDao.insert(dto);
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
	public int deleteActionGroupAuth(ClipActionGroupAuthDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			int deleteCount = clipActionGroupAuthDao.delete(dto);
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
	public int insertActionUserAuth(ClipActionUserAuthDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			int count = clipActionUserAuthDao.insert(dto);
			tm.commit();
			
			return count;
		} catch (Exception e) {
			tm.rollback();
			logger.error(e.getMessage());
		} finally {
			tm.end();
		}
		return 0;
	}

	@Override
	public int deleteActionUserAuth(ClipActionUserAuthDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			int count = clipActionUserAuthDao.delete(dto);
			tm.commit();
			
			return count;
		} catch (Exception e) {
			tm.rollback();
			logger.error(e.getMessage());
		} finally {
			tm.end();
		}
		return 0;
	}

	@Override
	public ClipActionUserAuthDto getActionUserAuth(ClipActionUserAuthDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			ClipActionUserAuthDto resultDto = clipActionUserAuthDao.get(dto);
			tm.commit();
			
			return resultDto;
		} catch (Exception e) {
			tm.rollback();
			logger.error(e.getMessage());
		} finally {
			tm.end();
		}
		return null;
	}

	@Override
	public ClipActionGroupAuthDto getActionGroupAuth(ClipActionGroupAuthDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			ClipActionGroupAuthDto resultDto = clipActionGroupAuthDao.get(dto);
			tm.commit();
			
			return resultDto;
		} catch (Exception e) {
			tm.rollback();
			logger.error(e.getMessage());
		} finally {
			tm.end();
		}
		return null;
	}

	@Override
	public JsonObject getList_authAction(ClipUserDto dto) {
		try {
			JsonObject result = new JsonObject();
			
			List<HashMap> list = clipActionAuthDao.getList_authAction(dto);
			
			for (HashMap item : list) {
				String actionCode = (String) item.get("actionCode");
				
				JsonArray actionCodeElement = result.getAsJsonArray("actionCode");
				if(actionCodeElement == null) {
					actionCodeElement = new JsonArray();
					result.add(actionCode, actionCodeElement);
				}
				
				String authCode = (String) item.get("authCode");
				actionCodeElement.add(authCode);
			}
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	
	@Override
	public List<ClipActionGroupAuthDto> getList(ClipActionGroupAuthDto dto){
		return clipActionGroupAuthDao.getList(dto);
	}
	
	@Override
	public List<ClipUserGroupDto> getAddGroupList(ClipActionGroupAuthDto dto){
		return clipActionGroupAuthDao.getAddGroupList(dto);
	}
	
	@Override
	public List<ClipActionUserAuthDto> getList(ClipActionUserAuthDto dto){
		return clipActionUserAuthDao.getList(dto);
	}
	
	@Override
	public List<ClipUserDto> getAddUserList(ClipActionUserAuthDto dto){
		return clipActionUserAuthDao.getAddUserList(dto);
	}

}
