package kr.co.clipsoft.repository.service.impl;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kr.co.clipsoft.repository.ClipMyBatisTransactionManager;
import kr.co.clipsoft.repository.dao.ClipFormCategoryDao;
import kr.co.clipsoft.repository.dao.ClipFormCategoryGroupAuthDao;
import kr.co.clipsoft.repository.dao.ClipFormCategoryUserAuthDao;
import kr.co.clipsoft.repository.dao.ClipManageIdDao;
import kr.co.clipsoft.repository.model.ClipFormCategoryDto;
import kr.co.clipsoft.repository.model.ClipManageIdDto;
import kr.co.clipsoft.repository.service.ClipFormCategoryService;
import kr.co.clipsoft.repository.service.ClipMybatisSupport;
import kr.co.clipsoft.repository.service.module.DefaultAuthGenerateComponent;

@Service("clipFormCategoryService")
public class ClipFormCategoryServiceImpl extends ClipMybatisSupport implements ClipFormCategoryService {

	@Value("#{projectProperties['server.defaultCategoryGroupAuthCodeOnNewCategory']}")
	private String defaultCategoryGroupAuthCodeOnNewCategory;
	
	@Value("#{projectProperties['server.defaultCategoryUserAuthCodeOnNewCategory']}")
	private String defaultCategoryUserAuthCodeOnNewCategory;
	
	@Resource(name="clipManageIdDao")
	private ClipManageIdDao clipManageIdDao;
	
	@Resource(name="clipFormCategoryDao")
	private ClipFormCategoryDao clipFormCategoryDao;
	
	@Resource(name="clipFormCategoryGroupAuthDao")
	private ClipFormCategoryGroupAuthDao clipFormCategoryGroupAuthDao;
	
	@Autowired
	private DefaultAuthGenerateComponent defaultAuthGenerateComponent;
	
	@Resource(name="clipFormCategoryUserAuthDao")
	private ClipFormCategoryUserAuthDao clipFormCategoryUserAuthDao;
	
	@Override
	public ClipFormCategoryDto newFormCategory(ClipFormCategoryDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			
			ClipManageIdDto manageIdDto = new ClipManageIdDto();
			manageIdDto.setProductId(dto.getProductId());
			manageIdDto.setKey("FORM_CATEGORY_ID");
			
			manageIdDto = clipManageIdDao.get(manageIdDto);
			dto.setFormCategoryId(manageIdDto.getValue());
			
			clipManageIdDao.increaseValue(manageIdDto);
			
			int result = clipFormCategoryDao.newFormCategory(dto);
			resetSeq(dto);
			
			defaultAuthGenerateComponent.generateAuth(dto);
			
			ClipFormCategoryDto insertDto = clipFormCategoryDao.get(dto);
			
			tm.commit();
			return insertDto;
		} catch(Exception exception) { 
			tm.rollback();
		} finally {
			tm.end();
		}
		return null;
	}

	@Override
	public ClipFormCategoryDto get(ClipFormCategoryDto dto) {
		return clipFormCategoryDao.get(dto);
	}

	@Override
	public int update(ClipFormCategoryDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			
			int result = clipFormCategoryDao.update(dto);
			
			if(dto.getSeq() != null) {
				resetSeq(dto);
			}
					
			tm.commit();
			return result;
		} catch(Exception exception) { 
			tm.rollback();
		} finally {
			tm.end();
		}
		return 0;
	}
	
	@Override
	public int update_useYN(ClipFormCategoryDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			
			int result = clipFormCategoryDao.update_useYN(dto);
			
			if(dto.getSeq() != null) {
				resetSeq(dto);
			}
					
			tm.commit();
			return result;
		} catch(Exception exception) { 
			tm.rollback();
		} finally {
			tm.end();
		}
		return 0;
	}

	@Override
	public List<HashMap> getList_all_categoryAndForm(Long productId, String userId, String itemName) {
		return clipFormCategoryDao.getList_all_categoryAndForm(productId, userId, itemName);
	}
	
	@Override
	public List<ClipFormCategoryDto> getList_regist_category(Long productId, String itemName) {
		return clipFormCategoryDao.getList_regist_category(productId,itemName);
	}

	private boolean resetSeq(ClipFormCategoryDto dto) {
		try {
			List<ClipFormCategoryDto> list = clipFormCategoryDao.getList_sameParentFormCategoryId(dto);
			
			for (int i = 0; i < list.size(); i++) {
				ClipFormCategoryDto item = list.get(i);
				item.setSeq(new Long(i+1));
				clipFormCategoryDao.update(item);
			}
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
