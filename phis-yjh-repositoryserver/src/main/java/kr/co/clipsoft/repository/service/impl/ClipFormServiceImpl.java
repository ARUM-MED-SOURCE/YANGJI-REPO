package kr.co.clipsoft.repository.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kr.co.clipsoft.repository.ClipMyBatisTransactionManager;
import kr.co.clipsoft.repository.dao.ClipFormDao;
import kr.co.clipsoft.repository.dao.ClipFormDataDao;
import kr.co.clipsoft.repository.dao.ClipFormUserAuthDao;
import kr.co.clipsoft.repository.dao.ClipFormUserGroupAuthDao;
import kr.co.clipsoft.repository.dao.ClipManageIdDao;
import kr.co.clipsoft.repository.model.ClipFormAndDataDto;
import kr.co.clipsoft.repository.model.ClipFormCategoryDto;
import kr.co.clipsoft.repository.model.ClipFormDataDto;
import kr.co.clipsoft.repository.model.ClipFormDto;
import kr.co.clipsoft.repository.model.ClipManageIdDto;
import kr.co.clipsoft.repository.service.ClipFormService;
import kr.co.clipsoft.repository.service.ClipMybatisSupport;
import kr.co.clipsoft.repository.service.module.DefaultAuthGenerateComponent;

@Service("clipFormService")
public class ClipFormServiceImpl extends ClipMybatisSupport implements ClipFormService {

	private static final Logger logger = LoggerFactory.getLogger(ClipFormServiceImpl.class);

	@Value("#{projectProperties['server.defaultFormUserAuthCodeOnNewForm']}")
	private String defaultFormUserAuthCodeOnNewForm;
	
	@Value("#{projectProperties['server.defaultFormGroupAuthCodeOnNewForm']}")
	private String defaultFormGroupAuthCodeOnNewForm;
	
	@Resource(name = "clipFormDao")
	private ClipFormDao clipFormDao;

	@Resource(name = "clipFormDataDao")
	private ClipFormDataDao clipFormDataDao;

	@Resource(name = "clipManageIdDao")
	private ClipManageIdDao clipManageIdDao;

	@Resource(name = "clipFormUserAuthDao")
	private ClipFormUserAuthDao clipFormUserAuthDao;
	
	@Resource(name = "clipFormUserGroupAuthDao")
	private ClipFormUserGroupAuthDao clipFormUserGroupAuthDao; 
	
	@Autowired
	private DefaultAuthGenerateComponent defaultAuthGenerateComponent; 
	
	@Override
	public int newFormData(ClipFormDto formDto, ClipFormDataDto dataDto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
	
		try {
			tm.start();
	
			formDto.setUseYN(true);
			//dataDto.setPublicationYN(false);
			dataDto.setFormVersion(new Long(1));
	
			ClipManageIdDto manageIdDto = new ClipManageIdDto();
			manageIdDto.setProductId(formDto.getProductId());
			manageIdDto.setKey("FORM_ID");
	
			int formInsertCount = clipFormDao.insert(formDto);
			int dataInsertCount = clipFormDataDao.insert(dataDto);
			if (formInsertCount != 1 || dataInsertCount != 1) {
				throw new Exception("newFormData insert 실패");
			}
			
			defaultAuthGenerateComponent.generateAuth(formDto);
			
			tm.commit();
			return formInsertCount;
		} catch (Exception e) {
			logger.error(e.getMessage());
			tm.rollback();
		} finally {
			tm.end();
		}
		return 0;
	}

	@Override
	 public int newVersion(ClipFormDto formDto, ClipFormDataDto dataDto) {
		 ClipMyBatisTransactionManager tm = getTransactionManager();
		
		 try {
			 tm.start();
			
			 formDto.setUseYN(true);
			 //dataDto.setPublicationYN(false);;
			
			 Long newFormVersion = clipFormDataDao.getFormLastVersion(dataDto) + 1;
			 dataDto.setFormVersion(newFormVersion);
			
			 int insertDataCount = clipFormDataDao.insert(dataDto);
			 
			 if(insertDataCount != 1) {
				 throw new IOException("insert 비정상 처리.");
			 }
			
			 tm.commit();
			 return insertDataCount;
		 } catch (Exception e) {
			 tm.rollback();
		 } finally {
			 tm.end();
		 }
		 return 0;
	 }

	@Override
	 public int updateFormData(ClipFormDataDto dataDto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
	
	 	try {
	 		tm.start();
		
			int dataUpdateCount = clipFormDataDao.updateFormData(dataDto);
			
			if(dataUpdateCount != 1) {
				throw new IOException("update 비정상 처리.");
			}
		
			tm.commit();
	
			return dataUpdateCount;
	 	} catch (Exception e) {
	 		tm.rollback();
	 	} finally {
	 		tm.end();
	 	}
	 	return 0;
	 }
	
	 @Override
	 public int updatePublication(ClipFormDataDto parameter) {
		 ClipMyBatisTransactionManager tm = getTransactionManager();
		
		 try {
			 tm.start();
			
			 int updateCount = clipFormDataDao.updatePublication(parameter);
			 if(updateCount != 1) {
				 throw new IOException("update 비정상 처리.");
			 }
			 tm.commit();
			 return updateCount;
		 } catch (Exception e) {
			 tm.rollback();
		 } finally {
			 tm.end();
		 }
		 return 0;
	 }
	
	 @Override
	 public int updateUse(ClipFormDto parameter) {
	 ClipMyBatisTransactionManager tm = getTransactionManager();
	
		 try {
			 tm.start();
			
			 int updateCount = clipFormDao.updateUse(parameter);
			 if(updateCount != 1) {
				 throw new IOException("update 비정상 처리.");
			 }
			 tm.commit();
			 return updateCount;
		 } catch (Exception e) {
			 tm.rollback();
		 } finally {
			 tm.end();
		 }
	 	return 0;
	 }
	 
	 @Override
	 public int updateName(ClipFormDto parameter) {
	 ClipMyBatisTransactionManager tm = getTransactionManager();
	
		 try {
			 tm.start();
			
			 int updateCount = clipFormDao.updateName(parameter);
			 if(updateCount != 1) {
				 throw new IOException("update 비정상 처리.");
			 }
			 tm.commit();
			 return updateCount;
		 } catch (Exception e) {
			 tm.rollback();
		 } finally {
			 tm.end();
		 }
	 	return 0;
	 }
	 
	@Override
	public int updateCategory(ClipFormDto parameter) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		try {
			tm.start();
			
			int updateCount = clipFormDao.updateCategory(parameter);
			if(updateCount != 1) {
				throw new IOException("update 비정상 처리.");
			}
			tm.commit();
			return updateCount;
		} catch (Exception e) {
				tm.rollback();
		} finally {
			tm.end();
		}
		return 0;
	}	 
	
	@Override
	public ClipFormDto getForm(ClipFormDto parameter) {
		return clipFormDao.getForm(parameter);
	} 
	 
	@Override
	public ClipFormAndDataDto get(ClipFormDto formDto, Long formVersion, Boolean eForm_setPublicationYN) {
		return clipFormDao.get(formDto, formVersion, eForm_setPublicationYN);
	}

	@Override
	public ClipFormAndDataDto getRecent(ClipFormDto formDto, boolean includeFormData, Boolean eForm_setPublicationYN) {
		return clipFormDao.getRecent(formDto, includeFormData, eForm_setPublicationYN);
	}

	@Override
	public List<ClipFormAndDataDto> getList_allVersion(ClipFormDto parameter, Boolean eForm_setPublicationYN) {
		return clipFormDao.getList_allVersion(parameter, eForm_setPublicationYN);
	}

	@Override
	public List<HashMap> getList_all(ClipFormDto formDto, Boolean eForm_setPublicationYN) {
		return clipFormDao.getList_all(formDto, eForm_setPublicationYN);
	}
	
	@Override
	public List<ClipFormDto> getList_allRegist(ClipFormDto formDto, Boolean eForm_setPublicationYN) {
		return clipFormDao.getList_allRegist(formDto, eForm_setPublicationYN);
	}	
	
	@Override
	public List<ClipFormDto> getList_allCategoryForm(ClipFormCategoryDto formCategoryDto, Boolean eForm_setPublicationYN){
		return clipFormDao.getList_allCategoryForm(formCategoryDto, eForm_setPublicationYN);
	}

}
