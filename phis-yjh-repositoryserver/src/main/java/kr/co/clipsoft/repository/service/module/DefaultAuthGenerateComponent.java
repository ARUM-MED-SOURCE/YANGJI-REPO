package kr.co.clipsoft.repository.service.module;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import kr.co.clipsoft.repository.dao.ClipFormCategoryGroupAuthDao;
import kr.co.clipsoft.repository.dao.ClipFormCategoryUserAuthDao;
import kr.co.clipsoft.repository.dao.ClipFormUserAuthDao;
import kr.co.clipsoft.repository.dao.ClipFormUserGroupAuthDao;
import kr.co.clipsoft.repository.model.ClipFormCategoryDto;
import kr.co.clipsoft.repository.model.ClipFormCategoryGroupAuthDto;
import kr.co.clipsoft.repository.model.ClipFormCategoryUserAuthDto;
import kr.co.clipsoft.repository.model.ClipFormDto;
import kr.co.clipsoft.repository.model.ClipFormUserAuthDto;
import kr.co.clipsoft.repository.model.ClipFormUserGroupAuthDto;
import kr.co.clipsoft.repository.model.ClipUserDto;
import kr.co.clipsoft.repository.model.ClipUserGroupDto;

@Component
public class DefaultAuthGenerateComponent {

	@Value("#{projectProperties['server.admin.onNewForm.defaultFormAuth']}")
	private String server_admin_onNewForm_defaultFormAuth;
	
	@Value("#{projectProperties['server.admin.onNewCategory.defaultCategoryAuth']}")
	private String server_admin_onNewCategory_defaultCategoryAuth;
	
	@Value("#{projectProperties['server.adminGroup.onNewForm.defaultFormAuth']}")
	private String server_adminGroup_onNewForm_defaultFormAuth;
	
	@Value("#{projectProperties['server.adminGroup.onNewCategory.defaultCategoryAuth']}")
	private String server_adminGroup_onNewCategory_defaultCategoryAuth;
	
	@Value("#{projectProperties['server.user.onNewForm.defaultFormAuth']}")
	private String server_user_onNewForm_defaultFormAuth;
	
	@Value("#{projectProperties['server.user.onNewCategory.defaultCategoryAuth']}")
	private String server_user_onNewCategory_defaultCategoryAuth;
	
	@Value("#{projectProperties['server.userGroup.onNewForm.defaultFormAuth']}")
	private String server_userGroup_onNewForm_defaultFormAuth;
	
	@Value("#{projectProperties['server.userGroup.onNewCategory.defaultCategoryAuth']}")
	private String server_userGroup_onNewCategory_defaultCategoryAuth;
	
	@Value("#{projectProperties['server.userGroup.onNewUserGroup.defaultFormAuth']}")
	private String server_userGroup_onNewUserGroup_defaultFormAuth;
	
	@Value("#{projectProperties['server.userGroup.onNewUserGroup.defaultCategoryAuth']}")
	private String server_userGroup_onNewUserGroup_defaultCategoryAuth;
	
	@Value("#{projectProperties['server.user.onNewUser.defaultFormAuth']}")
	private String server_user_onNewUser_defaultFormAuth;
	
	@Value("#{projectProperties['server.user.onNewUser.defaultCategoryAuth']}")
	private String server_user_onNewUser_defaultCategoryAuth;
	
	@Autowired
	private ClipFormUserAuthDao clipFormUserAuthDao;
	
	@Autowired
	private ClipFormUserGroupAuthDao clipFormUserGroupAuthDao;
	
	@Autowired
	private ClipFormCategoryUserAuthDao clipFormCategoryUserAuthDao;
	
	@Autowired
	private ClipFormCategoryGroupAuthDao clipFormCategoryGroupAuthDao;
	
	public boolean generateAuth(ClipFormDto insertFormDto) {
		generateAdminAuth(insertFormDto);
		generateAdminGroupAuth(insertFormDto);
		generateNormalUserAuth(insertFormDto);
		generateNormalUserGroupAuth(insertFormDto);
		return true;
	}

	public boolean generateAuth(ClipFormCategoryDto insertFormCategoryDto) {
		generateAdminAuth(insertFormCategoryDto);
		generateAdminGroupAuth(insertFormCategoryDto);
		generateNormalUserAuth(insertFormCategoryDto);
		generateNormalUserGroupAuth(insertFormCategoryDto);
		return true;
	}

	public boolean generateAuth(ClipUserGroupDto insertUserGroupDto) {
		generateFormNormalUserGroupAuth(insertUserGroupDto);
		generateCategoryNormalUserGroupAuth(insertUserGroupDto);
		return true;
	}

	public boolean generateAuth(ClipUserDto insertUserDto) {
		generateFormNormalUserAuth(insertUserDto);
		generateCategoryNormalUserAuth(insertUserDto);
		return true;
	} 
	
	private void generateCategoryNormalUserAuth(ClipUserDto insertUserDto) {
		String authArray[] = server_user_onNewUser_defaultCategoryAuth.split(",");
		
		ClipFormCategoryUserAuthDto dto = new ClipFormCategoryUserAuthDto();
		dto.setProductId(insertUserDto.getProductId());
		dto.setUserId(insertUserDto.getUserId());
		dto.setCreateUserId(insertUserDto.getCreateUserId());
		dto.setUpdateUserId(insertUserDto.getUpdateUserId());
		
		for (String authCode : authArray) {
			dto.setAuthCode(authCode);
			clipFormCategoryUserAuthDao.insertDefaultAuth_newUser(dto, authCode);
		}
	}

	private void generateFormNormalUserAuth(ClipUserDto insertUserDto) {
		String authArray[] = server_user_onNewUser_defaultFormAuth.split(",");
		
		ClipFormUserAuthDto dto = new ClipFormUserAuthDto();
		dto.setProductId(insertUserDto.getProductId());
		dto.setUserId(insertUserDto.getUserId());
		dto.setCreateUserId(insertUserDto.getCreateUserId());
		dto.setUpdateUserId(insertUserDto.getUpdateUserId());
		
		for (String authCode : authArray) {
			dto.setAuthCode(authCode);
			clipFormUserAuthDao.insertDefaultAuth_newUser(dto, authCode);
		}
	}

	private void generateAdminAuth(ClipFormDto insertFormDto) {
		String authArray[] = server_admin_onNewForm_defaultFormAuth.split(",");
		insertFormUserAuth("admin", authArray, insertFormDto);
	}
	
	private void generateAdminAuth(ClipFormCategoryDto insertFormCategoryDto) {
		String authArray[] = server_admin_onNewCategory_defaultCategoryAuth.split(",");
		insertFormUserAuth("admin", authArray, insertFormCategoryDto);
	}
	
	private void generateAdminGroupAuth(ClipFormDto insertFormDto) {
		String authArray[] = server_adminGroup_onNewForm_defaultFormAuth.split(",");
		insertFormUserGroupAuth("administrators", authArray, insertFormDto);
	}
	
	private void generateAdminGroupAuth(ClipFormCategoryDto insertFormCategoryDto) {
		String authArray[] = server_adminGroup_onNewCategory_defaultCategoryAuth.split(",");
		insertFormUserGroupAuth("administrators", authArray, insertFormCategoryDto);
	}
	
	private void generateNormalUserAuth(ClipFormDto insertFormDto) {
		String authArray[] = server_user_onNewForm_defaultFormAuth.split(",");
		
		ClipFormUserAuthDto authDto = new ClipFormUserAuthDto();
		authDto.setProductId(insertFormDto.getProductId());
		authDto.setFormId(insertFormDto.getFormId());
		authDto.setCreateUserId(insertFormDto.getCreateUserId());
		authDto.setUpdateUserId(insertFormDto.getUpdateUserId());
		
		for(String authCode : authArray) {
			authDto.setAuthCode(authCode);
			clipFormUserAuthDao.insertDefaultAuth_newForm(authDto);
		}
	}

	private void generateNormalUserAuth(ClipFormCategoryDto insertFormCategoryDto) {
		String authArray[] = server_user_onNewCategory_defaultCategoryAuth.split(",");

		ClipFormCategoryUserAuthDto authDto = new ClipFormCategoryUserAuthDto();
		authDto.setProductId(insertFormCategoryDto.getProductId());
		authDto.setFormCategoryId(insertFormCategoryDto.getFormCategoryId());
		authDto.setCreateUserId(insertFormCategoryDto.getCreateUserId());
		authDto.setUpdateUserId(insertFormCategoryDto.getUpdateUserId());
		
		for(String authCode : authArray) {
			authDto.setAuthCode(authCode);
			clipFormCategoryUserAuthDao.insertDefaultAuth_newCategory(authDto);
		}
	}

	private void generateNormalUserGroupAuth(ClipFormDto insertFormDto) {
		String authArray[] = server_userGroup_onNewForm_defaultFormAuth.split(",");
		
		ClipFormUserGroupAuthDto authDto = new ClipFormUserGroupAuthDto();
		authDto.setProductId(insertFormDto.getProductId());
		authDto.setFormId(insertFormDto.getFormId());
		authDto.setCreateUserId(insertFormDto.getCreateUserId());
		authDto.setUpdateUserId(insertFormDto.getUpdateUserId());
		
		for(String authCode : authArray) {
			authDto.setAuthCode(authCode);
			clipFormUserGroupAuthDao.insertDefaultAuth_newForm(authDto);
		}
	}

	private void generateNormalUserGroupAuth(ClipFormCategoryDto insertFormCategoryDto) {
		String authArray[] = server_userGroup_onNewCategory_defaultCategoryAuth.split(",");
		
		ClipFormCategoryGroupAuthDto authDto = new ClipFormCategoryGroupAuthDto();
		authDto.setProductId(insertFormCategoryDto.getProductId());
		authDto.setFormCategoryId(insertFormCategoryDto.getFormCategoryId());
		authDto.setCreateUserId(insertFormCategoryDto.getCreateUserId());
		authDto.setUpdateUserId(insertFormCategoryDto.getUpdateUserId());
		
		for(String authCode : authArray) {
			authDto.setAuthCode(authCode);
			clipFormCategoryGroupAuthDao.insertDefaultAuth_newCategory(authDto);
		}
	}

	private void generateFormNormalUserGroupAuth(ClipUserGroupDto insertUserGroupDto) {
		String authArray[] = server_userGroup_onNewUserGroup_defaultFormAuth.split(",");
		
		ClipFormUserGroupAuthDto dto = new ClipFormUserGroupAuthDto();
		dto.setProductId(insertUserGroupDto.getProductId());
		dto.setUserGroupId(insertUserGroupDto.getUserGroupId());
		dto.setCreateUserId(insertUserGroupDto.getCreateUserId());
		dto.setUpdateUserId(insertUserGroupDto.getUpdateUserId());
		
		for(String authCode : authArray) {
			dto.setAuthCode(authCode);
			clipFormUserGroupAuthDao.insertDefaultAuth_allForm(dto, authCode);
		}
	}

	private void generateCategoryNormalUserGroupAuth(ClipUserGroupDto insertUserGroupDto) {
		String authArray[] = server_userGroup_onNewUserGroup_defaultCategoryAuth.split(",");
	
		ClipFormCategoryGroupAuthDto dto = new ClipFormCategoryGroupAuthDto();
		dto.setProductId(insertUserGroupDto.getProductId());
		dto.setUserGroupId(insertUserGroupDto.getUserGroupId());
		dto.setCreateUserId(insertUserGroupDto.getCreateUserId());
		dto.setUpdateUserId(insertUserGroupDto.getUpdateUserId());
		
		for(String authCode : authArray) {
			dto.setAuthCode(authCode);
			clipFormCategoryGroupAuthDao.insertDefaultAuth_newGroup(dto);
		}
	}

	private void insertFormUserAuth(String userId, String[] authArray, ClipFormDto insertFormDto) {
		ClipFormUserAuthDto formUserAuthDto = new ClipFormUserAuthDto();
		formUserAuthDto.setProductId(insertFormDto.getProductId());
		formUserAuthDto.setUserId(userId);
		formUserAuthDto.setFormId(insertFormDto.getFormId());
		formUserAuthDto.setCreateUserId(insertFormDto.getCreateUserId());
		formUserAuthDto.setUpdateUserId(insertFormDto.getUpdateUserId());
		
		for (String authCode : authArray) {
			formUserAuthDto.setAuthCode(authCode);
			clipFormUserAuthDao.newData(formUserAuthDto);
		}
	}
	
	private void insertFormUserAuth(String userId, String[] authArray, ClipFormCategoryDto insertFormCategoryDto) {
		ClipFormCategoryUserAuthDto dto = new ClipFormCategoryUserAuthDto();
		dto.setProductId(insertFormCategoryDto.getProductId());
		dto.setUserId(userId);
		dto.setFormCategoryId(insertFormCategoryDto.getFormCategoryId());
		dto.setCreateUserId(insertFormCategoryDto.getCreateUserId());
		dto.setUpdateUserId(insertFormCategoryDto.getUpdateUserId());
		
		for (String authCode : authArray) {
			dto.setAuthCode(authCode);
			clipFormCategoryUserAuthDao.newData(dto);
		}
	}
	
	private void insertFormUserGroupAuth(String userGroupId, String[] authArray, ClipFormDto insertFormDto) {
		ClipFormUserGroupAuthDto dto = new ClipFormUserGroupAuthDto();
		dto.setProductId(insertFormDto.getProductId());
		dto.setUserGroupId(userGroupId);
		dto.setFormId(insertFormDto.getFormId());
		dto.setCreateUserId(insertFormDto.getCreateUserId());
		dto.setUpdateUserId(insertFormDto.getUpdateUserId());
		
		for (String authCode : authArray) {
			dto.setAuthCode(authCode);
			clipFormUserGroupAuthDao.newData(dto);
		}
	}
	
	private void insertFormUserGroupAuth(String userGroupId, String[] authArray, ClipFormCategoryDto insertFormCategoryDto) {
		ClipFormCategoryGroupAuthDto dto = new ClipFormCategoryGroupAuthDto();
		dto.setProductId(insertFormCategoryDto.getProductId());
		dto.setUserGroupId(userGroupId);
		dto.setFormCategoryId(insertFormCategoryDto.getFormCategoryId());
		dto.setCreateUserId(insertFormCategoryDto.getCreateUserId());
		dto.setUpdateUserId(insertFormCategoryDto.getUpdateUserId());
		
		for (String authCode : authArray) {
			dto.setAuthCode(authCode);
			clipFormCategoryGroupAuthDao.newData(dto);
		}
	}
}
