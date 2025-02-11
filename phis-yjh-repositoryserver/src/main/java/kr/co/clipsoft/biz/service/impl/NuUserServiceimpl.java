package kr.co.clipsoft.biz.service.impl;

import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import kr.co.clipsoft.biz.dao.NuActionUserAuthDao;
import kr.co.clipsoft.biz.dao.NuUserDao;
import kr.co.clipsoft.biz.model.NuActionUserAuthDto;
import kr.co.clipsoft.biz.model.NuUserDto;
import kr.co.clipsoft.biz.model.NuUserDto.DeptInfo;
import kr.co.clipsoft.biz.service.NuUserService;
import kr.co.clipsoft.biz.utility.WebUtility;
import kr.co.clipsoft.repository.ClipMyBatisTransactionManager;
import kr.co.clipsoft.repository.model.ClipUserDto;
import kr.co.clipsoft.repository.service.ClipMybatisSupport;

@Service("nuUserService")
public class NuUserServiceimpl extends ClipMybatisSupport implements NuUserService {

	private static final Logger logger = LoggerFactory.getLogger(NuUserServiceimpl.class);
	private static final Long PRODUCT_ID = 1L;
	private final String ACTION_CODE_EXTERNER_CONTROL = "ACTION_001";
	private final String AUTH_CODE_REG = "ACTION_AUTH_001";

	@Value("#{customerProperties['server.companyCode']}")
	private String companyCode;

	@Value("#{customerProperties['server.companySysCode']}")
	private String companySysCode;

	@Value("#{customerProperties['server.exceution.mode']}")
	private String exceutionMode;

	@Autowired
	private WebUtility webUtility;

	@Resource(name = "nuUserDao")
	private NuUserDao nuUserDao;

	@Resource(name = "nuActionUserAuthDao")
	private NuActionUserAuthDao nuActionUserAuthDao;

	@Override
	public boolean loginNu(String userId, String password) {

		// TODO : NU 로그인 요청
		String resultXml = sendRequestToNuLoginService(userId, password);

		// TODO : NU 로그인 결과 판단
		NuUserDto nuUserDto = isValidNuUserIdAndPassword(resultXml);

		if (nuUserDto == null || !nuUserDto.isLoginResult()) {
			return false;
		}
		nuUserDto.setUserId(userId);

		return saveClipUserAuth(nuUserDto);
	}

	private String sendRequestToNuLoginService(String userId, String password) {
		try {

			String submitId = "DRMRF00110";
			HashMap<String, String> map = new HashMap<String, String>();

			if (exceutionMode.equals("CLIP")) {
				String patten = "<data><action>LOGON</action><params><param name='adaptername'>defaultadapter</param><param name='uid'>%s</param><param name='pwd'>%s</param></params></data>";
				map.put("OP", "DESIGNER_LOGIN");
				map.put("PD", String.format(patten, userId, password));
			} else {
				map.put("business_id", "mr");
				map.put("userid", userId);
				map.put("pwd", password);
				map.put("instcd", companyCode);
			}

			return webUtility.sendRequestService(submitId, map);
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}

	private NuUserDto isValidNuUserIdAndPassword(String xml) {
		try {
			logger.debug("LOGIN : " + xml);

			if (xml.isEmpty()) {
				return null;
			}

			NuUserDto nuUserDto = new NuUserDto();
			HashMap<String, String> returnValue = new HashMap<String, String>();

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbFactory.newDocumentBuilder();

			Document document = builder.parse(new InputSource(new StringReader(xml)));

			XPath xpath = XPathFactory.newInstance().newXPath();

			if (exceutionMode.equals("CLIP")) {
				/*
				 * CLIP 내부 테스트 모듈
				 */
				NodeList nodeList = (NodeList) xpath.evaluate("/result/data/commonparams/param", document, XPathConstants.NODESET);

				logger.debug("NodeList Count : " + nodeList.getLength());

				for (int idx = 0; idx < nodeList.getLength(); idx++) {
					if (nodeList.item(idx).hasChildNodes()) {
						returnValue.put(nodeList.item(idx).getAttributes().getNamedItem("name").getTextContent(),
								nodeList.item(idx).getChildNodes().item(0).getTextContent());
						logger.debug(nodeList.item(idx).getAttributes().getNamedItem("name").getTextContent() + " : "
								+ nodeList.item(idx).getChildNodes().item(0).getTextContent());
					} else {
						returnValue.put(nodeList.item(idx).getAttributes().getNamedItem("name").getTextContent(),
								nodeList.item(idx).getTextContent());
						logger.debug(nodeList.item(idx).getAttributes().getNamedItem("name").getTextContent() + " : "
								+ nodeList.item(idx).getTextContent());
					}
				}
			} else {
				/*
				 * nU Response Data
				 */
				NodeList nodeList = (NodeList) xpath.evaluate("/root/data", document, XPathConstants.NODESET);

				logger.debug("NodeList Count : " + nodeList.getLength());

				if (nodeList.getLength() == 1) {
					NodeList childList = nodeList.item(0).getChildNodes();

					for (int idx = 0; idx < childList.getLength(); idx++) {
						if (childList.item(idx).hasChildNodes()) {
							returnValue.put(childList.item(idx).getNodeName(), childList.item(idx).getChildNodes().item(0).getTextContent());
							logger.debug(childList.item(idx).getNodeName() + " : " + childList.item(idx).getChildNodes().item(0).getTextContent());
						} else {
							returnValue.put(childList.item(idx).getNodeName(), childList.item(idx).getTextContent());
							logger.debug(childList.item(idx).getNodeName() + " : " + childList.item(idx).getTextContent());
						}
					}
				}
			}

			if (returnValue.get("login").equals("true")) {
				nuUserDto.setLoginResult(true);
				nuUserDto.setUserNm(returnValue.get("name"));
			} else {
				nuUserDto.setLoginResult(false);
			}

			return nuUserDto;
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}

	@Override
	public NuUserDto loginNu2(String userId, String password) {

		// TODO : NU 로그인 요청
		String resultXml = sendRequestToNuLoginService2(userId, password);

		// TODO : NU 로그인 결과 판단
		NuUserDto resultDto = getNuLoginUserResult2(userId, resultXml);

		if (resultDto == null) {
			logger.error("로그인 사용자 데이터 변환에 실패하였습니다.");
			return null;
		}

		if (!resultDto.isLoginResult()) {
			return resultDto;
		}

		if (!saveClipUserAuth(resultDto)) {
			return null;
		}

		return resultDto;
	}

	private String sendRequestToNuLoginService2(String userId, String password) {
		try {
			String submitId = "DRMRF00119";
			HashMap<String, String> map = new HashMap<String, String>();
			password = URLEncoder.encode(password);

			map.put("business_id", "mr");
			map.put("userid", userId);
			map.put("pwd", password);
			map.put("instcd", companyCode);
			map.put("syscd", companySysCode);

			return webUtility.sendRequestService(submitId, map);
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}

	private NuUserDto getNuLoginUserResult2(String userId, String xml) {

		try {
			logger.debug("LOGIN : " + xml);

			if (xml.isEmpty()) {
				return null;
			}

			XPath xPath = XPathFactory.newInstance().newXPath();
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbFactory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(xml)));

			NuUserDto resultDto = new NuUserDto();

			String loginRst = getTxtContentsByXpathOne(document, xPath, "/root/message/code");
			String loginRstMsg = getTxtContentsByXpathOne(document, xPath, "/root/message/description");

			if (loginRst != null && loginRst.equals("0")) {
				resultDto.setLoginResult(true);
			} else {
				resultDto.setLoginResult(false);
			}
			resultDto.setLoginResultMsg(loginRstMsg);

			if (!resultDto.isLoginResult()) {
				return resultDto;
			}

			resultDto.setUserId(userId);
			resultDto.setUserNm(getTxtContentsByXpathOne(document, xPath, "/root/data/usernm"));
			resultDto.setInstCd(getTxtContentsByXpathOne(document, xPath, "/root/data/instcd"));
			resultDto.setInstNm(getTxtContentsByXpathOne(document, xPath, "/root/data/instnm"));
			resultDto.setJobKindCd(getTxtContentsByXpathOne(document, xPath, "/root/data/jobkindcd"));
			resultDto.setJobPosCd(getTxtContentsByXpathOne(document, xPath, "/root/data/jobposcd"));

			ArrayList<DeptInfo> deptInfos = new ArrayList<DeptInfo>();

			NodeList usrIfRootNodeList = (NodeList) xPath.evaluate("/root/data", document, XPathConstants.NODESET);
			for (int rootIndex = 0; rootIndex < usrIfRootNodeList.getLength(); rootIndex++) {

				if (usrIfRootNodeList.item(rootIndex).getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}

				NodeList usrIfNodeList = usrIfRootNodeList.item(rootIndex).getChildNodes();

				String deptCd = "";
				String deptNm = "";

				for (int index = 0; index < usrIfNodeList.getLength(); index++) {
					if (usrIfNodeList.item(index).getNodeType() != Node.ELEMENT_NODE) {
						continue;
					}

					Element elm = (Element) usrIfNodeList.item(index);

					if (elm.getTagName().equals("deptcd")) {
						deptCd = elm.getTextContent().trim();
					}

					if (elm.getTagName().equals("depthngnm")) {
						deptNm = elm.getTextContent().trim();
					}
				}

				deptInfos.add(resultDto.new DeptInfo(deptCd, deptNm));
			}

			resultDto.setDeptInfos(deptInfos);

			return resultDto;
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}

	}

	private String getTxtContentsByXpathOne(Document document, XPath xPath, String strXPath) throws XPathExpressionException {

		NodeList nodeList = (NodeList) xPath.evaluate(strXPath, document, XPathConstants.NODESET);

		String result = null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
				result = nodeList.item(i).getTextContent().trim();
				break;
			}
		}
		return result;
	}

	private boolean saveClipUserAuth(NuUserDto nuUserDto) {

		boolean result = true;

		ClipUserDto userParamDto = new ClipUserDto();
		userParamDto.setUserId(nuUserDto.getUserId());
		userParamDto.setProductId(PRODUCT_ID);
		ClipUserDto clipUserDto = nuUserDao.getUser(userParamDto);

		if (clipUserDto != null) {
			return result;
		}

		clipUserDto = new ClipUserDto();
		clipUserDto.setProductId(PRODUCT_ID);
		clipUserDto.setUserId(nuUserDto.getUserId());
		clipUserDto.setName(nuUserDto.getUserNm());
		clipUserDto.setCreateUserId("admin");
		clipUserDto.setUpdateUserId("admin");
		clipUserDto.setPassword("$2a$04$fFtAapTw/em73hflBjasle10GUP5cdYH3rFNdvGF/WWCTf3cyJ1hG");

		// 외부사용자정의컨트롤 등록 권한 강제 부여
		NuActionUserAuthDto userAuth = new NuActionUserAuthDto();
		userAuth.setProductId(PRODUCT_ID);
		userAuth.setUserId(nuUserDto.getUserId());
		userAuth.setActionCode(ACTION_CODE_EXTERNER_CONTROL);
		userAuth.setAuthCode(AUTH_CODE_REG);
		userAuth.setCreateUserId("admin");
		userAuth.setUpdateUserId("admin");

		ClipMyBatisTransactionManager tm = getTransactionManager();
		try {
			tm.start();
			nuUserDao.insertUser(clipUserDto);
			nuActionUserAuthDao.save(userAuth);
			tm.commit();
		} catch (Exception e) {
			logger.error("로그인 사용자 권한 부여에 실패하였습니다. : " + e.toString());
			tm.rollback();
			result = false;
		} finally {
			tm.end();
		}

		return result;
	}
}
