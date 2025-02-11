package kr.co.clipsoft.repository.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.co.clipsoft.repository.ClipMyBatisTransactionManager;
import kr.co.clipsoft.repository.dao.ClipManageIdDao;
import kr.co.clipsoft.repository.model.ClipManageIdDto;
import kr.co.clipsoft.repository.service.ClipManageIdService;
import kr.co.clipsoft.repository.service.ClipMybatisSupport;

@Service("clipManageIdService")
public class ClipManageIdServiceImpl extends ClipMybatisSupport implements ClipManageIdService {

	private static final Logger logger = LoggerFactory.getLogger(ClipManageIdServiceImpl.class);
	
	@Resource(name="clipManageIdDao")
	private ClipManageIdDao clipManageIdDao;
	
	@Override
	public ClipManageIdDto getFormId(Long productId) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			
			ClipManageIdDto dto = new ClipManageIdDto();
			dto.setProductId(productId);
			dto.setKey("FORM_ID");
			
			ClipManageIdDto resultDto = clipManageIdDao.get(dto);
			clipManageIdDao.increaseValue(resultDto);
					
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
	public ClipManageIdDto getFormCategoryId(Long productId) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			
			ClipManageIdDto dto = new ClipManageIdDto();
			dto.setProductId(productId);
			dto.setKey("FORM_CATEGORY_ID");
			
			ClipManageIdDto resultDto = clipManageIdDao.get(dto);
			clipManageIdDao.increaseValue(resultDto);
					
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
	public ClipManageIdDto get(ClipManageIdDto dto) {
		return clipManageIdDao.get(dto);
	}

	@Override
	public int increase(ClipManageIdDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			int updateCount = clipManageIdDao.increaseValue(dto);
			tm.commit();
			return updateCount;
		} catch (Exception e) {
			tm.rollback();
		} finally {
			tm.end();
		}
		
		return 0;
	}

}
