package kr.co.clipsoft.repository.service;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import kr.co.clipsoft.repository.ClipMyBatisTransactionManager;

@Service("myBatisSupport")
public class ClipMybatisSupport {
	@Autowired(required = false)
	@Qualifier("sqlSession")
	protected SqlSessionTemplate sqlSession;

	@Autowired
	ApplicationContext applicationContext;

	public ClipMyBatisTransactionManager getTransactionManager() {
		return applicationContext.getBean(ClipMyBatisTransactionManager.class);
	}
}
