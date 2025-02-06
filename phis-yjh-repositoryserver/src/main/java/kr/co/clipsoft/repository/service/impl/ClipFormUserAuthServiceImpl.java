package kr.co.clipsoft.repository.service.impl;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

import kr.co.clipsoft.repository.ClipMyBatisTransactionManager;
import kr.co.clipsoft.repository.dao.ClipCodeDao;
import kr.co.clipsoft.repository.dao.ClipFormDao;
import kr.co.clipsoft.repository.dao.ClipFormUserAuthDao;
import kr.co.clipsoft.repository.dao.ClipUserDao;
import kr.co.clipsoft.repository.model.ClipCodeDto;
import kr.co.clipsoft.repository.model.ClipFormDto;
import kr.co.clipsoft.repository.model.ClipFormUserAuthDto;
import kr.co.clipsoft.repository.model.ClipUserDto;
import kr.co.clipsoft.repository.service.ClipFormUserAuthService;
import kr.co.clipsoft.repository.service.ClipMybatisSupport;

@Service("clipFormUserAuthService")
public class ClipFormUserAuthServiceImpl extends ClipMybatisSupport implements ClipFormUserAuthService {

	@Resource(name = "clipFormUserAuthDao")
	private ClipFormUserAuthDao clipFormUserAuthDao;
	
	@Resource(name = "clipCodeDao")
	private ClipCodeDao clipCodeDao;
	
	@Resource(name = "clipUserDao")
	private ClipUserDao clipUserDao;
	
	@Resource(name = "clipFormDao")
	private ClipFormDao clipFormDao;
	
	@Override
	public int newData(ClipFormUserAuthDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			
			ClipCodeDto codeDto = new ClipCodeDto();
			codeDto.setProductId(dto.getProductId());
			codeDto.setCode(dto.getAuthCode());
			
			ClipUserDto userDto = new ClipUserDto();
			userDto.setProductId(dto.getProductId());
			userDto.setUserId(dto.getUserId());
			
			ClipFormDto formDto = new ClipFormDto();
			formDto.setProductId(dto.getProductId());
			formDto.setFormId(dto.getFormId());
			
			ClipUserDto validUserDto = clipUserDao.get(userDto);
			ClipCodeDto validCodeDto = clipCodeDao.get(codeDto);
			ClipFormDto validFormDto = clipFormDao.getForm(formDto);
			
			if(validUserDto == null) {
				throw new Exception("존재하지 않는 userId 삽입");
			}
			
			if(validCodeDto == null) {
				throw new Exception("존재하지 않는 code 삽입");
			}
			
			if(validFormDto == null) {
				throw new Exception("존재하지 않는 formId 삽입");
			}
			
			int result = clipFormUserAuthDao.newData(dto);
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
	public int delete(ClipFormUserAuthDto dto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();
		
		try {
			tm.start();
			int deleteCount = clipFormUserAuthDao.delete(dto);
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
	public ClipFormUserAuthDto get(ClipFormUserAuthDto dto) {
		return clipFormUserAuthDao.get(dto);
	}
	
	@Override
	public List<ClipFormUserAuthDto> getList(ClipFormUserAuthDto dto){
		return clipFormUserAuthDao.getList(dto);
	}
	
	@Override
	public List<ClipUserDto> getAddUserList(ClipFormUserAuthDto dto){
		return clipFormUserAuthDao.getAddUserList(dto);		
	}

	@Override
	public JsonObject getList_auth_getList_form(Long productId, String userId) {
		JsonObject json = new JsonObject();

		List<HashMap> list = clipFormUserAuthDao.getList_auth_getList_form(productId, userId);
		for(HashMap map : list) {
			Long formId = (Long) map.get("formId");
			String authCode = (String) map.get("authCode");
			String auth = (String) map.get("auth");
			Boolean authValue = Boolean.valueOf(auth);
					
			JsonObject authJson = null;
			if(json.get(formId.toString()) == null) {
				authJson = new JsonObject();
				json.add(formId.toString(), authJson);
			} else {
				authJson = json.get(formId.toString()).getAsJsonObject();
			}
			
			authJson.addProperty(authCode, authValue);
		}
		
		return json;
	}

	@Override
	public JsonObject getList_auth_getList_category(Long productId, String userId) {
		JsonObject json = new JsonObject();

		List<HashMap> list = clipFormUserAuthDao.getList_auth_getList_category(productId, userId);
		for(HashMap map : list) {
			Long formCategoryId = (Long) map.get("formCategoryId");
			String authCode = (String) map.get("authCode");
			String auth = (String) map.get("auth");
			Boolean authValue = Boolean.valueOf(auth);
					
			JsonObject authJson = null;
			if(json.get(formCategoryId.toString()) == null) {
				authJson = new JsonObject();
				json.add(formCategoryId.toString(), authJson);
			} else {
				authJson = json.get(formCategoryId.toString()).getAsJsonObject();
			}
			
			authJson.addProperty(authCode, authValue);
		}
		
		return json;
	}

}
