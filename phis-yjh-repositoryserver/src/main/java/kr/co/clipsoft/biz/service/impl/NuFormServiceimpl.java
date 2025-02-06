package kr.co.clipsoft.biz.service.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
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

import com.google.gson.JsonObject;

import kr.co.clipsoft.biz.dao.NuConsentDao;
import kr.co.clipsoft.biz.dao.NuFormDao;
import kr.co.clipsoft.biz.dao.NuFormDataDao;
import kr.co.clipsoft.biz.model.NuDocumentDto;
import kr.co.clipsoft.biz.model.NuFormAndDataDto;
import kr.co.clipsoft.biz.model.NuFormAndExDto;
import kr.co.clipsoft.biz.model.NuFormDataDto;
import kr.co.clipsoft.biz.model.NuFormDto;
import kr.co.clipsoft.biz.model.NuTempDataDto;
import kr.co.clipsoft.biz.model.consent.NuConsentFormExDto;
import kr.co.clipsoft.biz.service.NuFormService;
import kr.co.clipsoft.biz.utility.WebUtility;
import kr.co.clipsoft.repository.ClipMyBatisTransactionManager;
import kr.co.clipsoft.repository.dao.ClipManageIdDao;
import kr.co.clipsoft.repository.model.ClipFormDto;
import kr.co.clipsoft.repository.model.ClipManageIdDto;
import kr.co.clipsoft.repository.service.ClipManageIdService;
import kr.co.clipsoft.repository.service.ClipMybatisSupport;

@Service("nuFormService")
public class NuFormServiceimpl extends ClipMybatisSupport implements NuFormService {

	private static final Logger logger = LoggerFactory.getLogger(NuFormServiceimpl.class);

	private static final String EFORM25_FORM_TYPE_NORMAL = "FORM_TYPE_001";

	@Value("#{projectProperties['server.enableCheckPublicationYN']}")
	private String enableCheckPublicationYN;

	@Value("#{projectProperties['server.enableCheckUseYN']}")
	private String enableCheckUseYN;

	@Value("#{customerProperties['server.companyCode']}")
	private String companyCode;

	@Value("#{customerProperties['server.exceution.mode']}")
	private String exceutionMode;

	@Resource(name = "nuFormDao")
	private NuFormDao nuFormDao;

	@Resource(name = "nuFormDataDao")
	private NuFormDataDao nuFormDataDao;

	@Resource(name = "nuConsentDao")
	private NuConsentDao nuConsentDao;

	@Resource(name = "clipManageIdDao")
	private ClipManageIdDao clipManageIdDao;

	@Autowired
	private ClipManageIdService clipManageIdService;

	@Autowired
	private WebUtility webUtility;

	/*
	 * eForm 의 FORM_ID 를 이용 하여 맵핑 된 DOCUMENT_CODE 정보를 조회 한다.
	 */
	@Override
	public NuDocumentDto getDocumentCode(NuDocumentDto documentDto) {
		return nuFormDao.getDocumentCode(documentDto);
	}

	@Override
	public NuDocumentDto getFormId(NuDocumentDto documentDto) {
		return nuFormDao.getFormId(documentDto);
	}

	@Override
	public String getFormId(Long productId, NuDocumentDto parameterDto) {

		NuDocumentDto resultDto = nuFormDao.getFormId(parameterDto);

		if (resultDto == null) {
			ClipManageIdDto dto = clipManageIdService.getFormId(productId);
			resultDto = new NuDocumentDto();
			resultDto.setFormId(dto.getValue());
		}

		JsonObject result = new JsonObject();
		result.addProperty("formId", resultDto.getFormId());

		return result.toString();

	}

	@Override
	public int insertDocumentCode(NuDocumentDto documentDto) {
		// TODO Auto-generated method stub
		ClipMyBatisTransactionManager tm = getTransactionManager();

		try {
			tm.start();
			int runCount = 0;

			Long documentKey = this.getDocumentId(1L);
			documentDto.setDocumentKey(documentKey);

			logger.debug("CLIP : " + documentDto.toString());

			NuDocumentDto resultParam = nuFormDao.getDocumentCode(documentDto);
			if (resultParam == null) {
				runCount = nuFormDao.insertDocumentCode(documentDto);
				if (runCount != 1) {
					throw new IOException("insert 비정상 처리.");
				}

			} else {
				runCount = nuFormDao.updateDocumentCode(documentDto);
				if (runCount != 1) {
					throw new IOException("update 비정상 처리.");
				}

			}
			tm.commit();
			return runCount;
		} catch (Exception e) {
			logger.error(e.toString());
			tm.rollback();
		} finally {
			tm.end();
		}
		return 0;
	}

	@Override
	public int newFormData(NuFormDto formDto, NuFormDataDto dataDto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();

		try {
			tm.start();

			formDto.setUseYN(true);
			dataDto.setPublicationYN(false);
			dataDto.setFormVersion(new Long(1));

			ClipManageIdDto manageIdDto = new ClipManageIdDto();
			manageIdDto.setProductId(formDto.getProductId());
			manageIdDto.setKey("FORM_ID");

			int formInsertCount = nuFormDao.insert(formDto);
			int dataInsertCount = nuFormDataDao.insert(dataDto);
			if (formInsertCount != 1 || dataInsertCount != 1) {
				throw new Exception("newFormData insert 실패");
			}

			if (formDto.getFormType().equals(EFORM25_FORM_TYPE_NORMAL)) {
				NuConsentFormExDto formAndExDto = getNuFormAndExDtoRecentVersion(dataDto);
				nuConsentDao.saveConsentFormEx(formAndExDto);
			}

			String sendRequest = setRequestDocumentCode(formDto.getFormId().toString(), formDto.getDocumentCode(), dataDto.getCreateUserId());
			if (sendRequest == null) {
				throw new Exception("nU 데이터 전송 중 오류 발생");
			}

			tm.commit();
			return formInsertCount;
		} catch (Exception e) {
			logger.error(e.toString());
			tm.rollback();
		} finally {
			tm.end();
		}
		return 0;
	}

	private NuConsentFormExDto getNuFormAndExDtoRecentVersion(NuFormDataDto dataDto) {

		NuDocumentDto documentDto = new NuDocumentDto();
		documentDto.setFormId(dataDto.getFormId());
		documentDto = nuFormDao.getDocumentCode(documentDto);

		NuFormAndExDto formAndExDto = new NuFormAndExDto();
		formAndExDto.setInstCd(companyCode);
		formAndExDto.setFormCd(documentDto.getDocumentCode());
		formAndExDto.setFormId(documentDto.getFormId());
		formAndExDto.setRecentVersionYn("Y");

		List<NuFormAndExDto> listFormAndExDto = nuFormDao.getClipFormAndConsentFormEx(formAndExDto);

		if (listFormAndExDto.size() == 0) {
			formAndExDto.setFormType(EFORM25_FORM_TYPE_NORMAL);
			formAndExDto.setCosignYn("N");
			formAndExDto.setOpdrYn("N");
			formAndExDto.setNursCertYn("N");
			formAndExDto.setXmlHistoryYn("N");
			formAndExDto.setCtlHistoryYn("N");
			formAndExDto.setCertNeedCnt(1);
			formAndExDto.setExternalCnt((long) 0);
		} else {
			formAndExDto = listFormAndExDto.get(0);
		}

		formAndExDto.setFormVersion(dataDto.getFormVersion());
		formAndExDto.setCommitComment(dataDto.getCommitComment());
		formAndExDto.setUserId(dataDto.getCreateUserId());

		return formAndExDto;
	}

	@Override
	public int newFormDataMig(NuFormDto formDto, NuFormDataDto dataDto, NuConsentFormExDto formExDto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();

		try {
			tm.start();

			formDto.setUseYN(true);

			int formInsertCount = nuFormDao.insertMig(formDto);
			int dataInsertCount = nuFormDataDao.insertMig(dataDto);

			if (formInsertCount != 1 || dataInsertCount != 1) {
				tm.rollback();
				throw new Exception("newFormData insert 실패");
			}

			// nu시스템 업무서비스 호출 주석처리 - 2018.09.07 박병국
//			String sendRequest = setRequestDocumentCode(formDto.getFormId().toString(), formDto.getDocumentCode(), dataDto.getCreateUserId());
//			if(sendRequest == null)
//			{
//				throw new Exception("nU 데이터 전송 중 오류 발생");
//			}

			tm.commit();
			return formInsertCount;
		} catch (Exception e) {
			logger.error(e.toString());
			tm.rollback();
		} finally {
			tm.end();
		}
		return 0;
	}

	@Override
	public int updatePublication(NuFormDataDto formDataDto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();

		try {
			tm.start();

			int updateCount = nuFormDataDao.updatePublication(formDataDto);
			if (updateCount != 1) {
				throw new IOException("publication update 비정상 처리.");
			}

			// 데이터 저장 후 nU 에 서비스 정보 전달.
			if (!setNuUpdatePublishInfo(formDataDto.getDocumentCode(), formDataDto.getFormId(), formDataDto.getFormVersion(),
					formDataDto.getUpdateUserId())) {
				throw new Exception("nU 데이터 전송 중 오류 발생");
			}

			tm.commit();
			return updateCount;
		} catch (Exception e) {
			logger.error(e.toString());
			tm.rollback();
		} finally {
			tm.end();
		}
		return 0;
	}

	@Override
	public NuFormAndDataDto get(NuFormDto formDto, Long formVersion) {
		// TODO Auto-generated method stub
		return nuFormDao.get(formDto, formVersion);
	}

	@Override
	public int newVersion(NuFormDto formDto, NuFormDataDto dataDto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();

		try {
			tm.start();

			formDto.setUseYN(true);
			dataDto.setPublicationYN(false);

			Long newFormVersion = nuFormDataDao.getFormLastVersion(dataDto) + 1;
			dataDto.setFormVersion(newFormVersion);

			int insertDataCount = nuFormDataDao.insert(dataDto);

			if (insertDataCount != 1) {
				throw new IOException("insert 비정상 처리.");
			}

			if (formDto.getFormType().equals(EFORM25_FORM_TYPE_NORMAL)) {
				NuConsentFormExDto formAndExDto = getNuFormAndExDtoRecentVersion(dataDto);
				nuConsentDao.saveConsentFormEx(formAndExDto);
			}

			tm.commit();
			return insertDataCount;
		} catch (Exception e) {
			logger.error(e.toString());
			tm.rollback();
		} finally {
			tm.end();
		}
		return 0;
	}

	@Override
	public int newVersionMig(NuFormDataDto dataDto) {
		ClipMyBatisTransactionManager tm = getTransactionManager();

		try {
			tm.start();

			int insertDataCount = nuFormDataDao.insertMig(dataDto);

			if (insertDataCount != 1) {
				throw new IOException("insert 비정상 처리.");
			}

			tm.commit();
			return insertDataCount;
		} catch (Exception e) {
			logger.error(e.toString());
			tm.rollback();
		} finally {
			tm.end();
		}
		return 0;
	}

	@Override
	public NuFormAndDataDto getRecent(NuFormDto formDto) {
		return nuFormDao.getRecent(formDto);
	}

	@Override
	public List<NuFormAndDataDto> getList_allVersion(NuFormDto parameter) throws Exception {

		if (parameter.getDocumentCode() != null && parameter.getDocumentCode().length() > 0) {

			List<NuDocumentDto> listDocumentDto = nuFormDao.getList_document(parameter);
			if (listDocumentDto.size() == 0) {
				return new ArrayList<NuFormAndDataDto>();
			}

			if (listDocumentDto.size() > 1) {
				logger.error("하나의 서식코드에 다건의 서식아이디가 매핑되어 있습니다.");
				throw new Exception("하나의 서식코드에 다건의 서식아이디가 매핑되어 있습니다.");
			}

			parameter.setFormId(listDocumentDto.get(0).getFormId());
		}

		return nuFormDao.getList_allVersion(parameter);
	}

	private String setRequestDocumentCode(String formId, String documentCode, String userId) {
		try {

			String submitId = "DXMRF00110";
			HashMap<String, String> map = new HashMap<String, String>();

			if (exceutionMode.equals("CLIP")) {
				map.put("OP", "DESIGNER_CATEGORY");
				map.put("PD",
						"<data><action>GET</action><params><param name=\"uid\"><![CDATA[admin]]></param><param name=\"rid\"><![CDATA[*]]></param><param name=\"level\"><![CDATA[*]]></param><param name=\"adaptername\"><![CDATA[defaultadapter]]></param></params><commonparams><param name=\"USER_RID\"><![CDATA[1]]></param><param name=\"USER_ID\"><![CDATA[admin]]></param><param name=\"USER_NAME\"><![CDATA[관리자]]></param><param name=\"USE_YN\"><![CDATA[Y]]></param></commonparams></data>");
			} else {

				map.put("business_id", "mr");
				map.put("userid", userId);
				map.put("formcd", documentCode);
				map.put("instcd", companyCode);
				map.put("guid", "" + formId);
				map.put("formid", formId);
			}

			return webUtility.sendRequestService(submitId, map);
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}

	private Long getDocumentId(Long productId) {

		ClipManageIdDto dto = new ClipManageIdDto();
		dto.setProductId(productId);
		dto.setKey("DOCUMENT_KEY");

		ClipManageIdDto resultDto = clipManageIdDao.get(dto);
		clipManageIdDao.increaseValue(resultDto);

		return resultDto.getValue();
	}

	private boolean setNuUpdatePublishInfo(String formCd, Long formId, Long formVersion, String userId) {

		try {
			String submitId = "DXMRF00111";

			// 정상적으로 저장된 경우 nU i/f 호출 하여 nU로 게시 정보를 전달 한다.
			HashMap<String, String> map = new HashMap<String, String>();

			if (exceutionMode.equals("CLIP")) {
				map.put("OP", "DESIGNER_CATEGORY");
				map.put("PD",
						"<data><action>GET</action><params><param name=\"uid\"><![CDATA[admin]]></param><param name=\"rid\"><![CDATA[*]]></param><param name=\"level\"><![CDATA[*]]></param><param name=\"adaptername\"><![CDATA[defaultadapter]]></param></params><commonparams><param name=\"USER_RID\"><![CDATA[1]]></param><param name=\"USER_ID\"><![CDATA[admin]]></param><param name=\"USER_NAME\"><![CDATA[관리자]]></param><param name=\"USE_YN\"><![CDATA[Y]]></param></commonparams></data>");
			} else {
				map.put("business_id", "mr");
				map.put("formcd", formCd);
				map.put("instcd", companyCode);
				map.put("guid", "" + formId);
				map.put("formid", "" + formId);
				map.put("dataimg", "");
				map.put("version", "" + formVersion);
				map.put("userid", userId);
			}

			String nuResult = webUtility.sendRequestService(submitId, map);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = dbFactory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(nuResult)));

			XPath xpath = XPathFactory.newInstance().newXPath();

			HashMap<String, String> item = new HashMap<String, String>();

			NodeList nodeList = (NodeList) xpath.evaluate("/root/message/code", document, XPathConstants.NODESET);

			if (nodeList != null && nodeList.getLength() > 0) {
				String MessageCode = nodeList.item(0).getTextContent();

				if (!MessageCode.equals("0")) {
					String sErrorMessage;
					NodeList node = (NodeList) xpath.evaluate("/root/message/description", document, XPathConstants.NODESET);

					if (node != null && node.getLength() > 0) {
						throw new Exception(node.item(0).getTextContent());
					} else {
						throw new Exception("nU 서버에서  오류를 반환 하였습니다.");
					}
				}
			} else {
				throw new Exception(nuResult);
			}
			return true;
		} catch (Exception e) {
			logger.error(e.toString());
			return false;
		}
	}

	private Long getDataRid(Long productId) throws IOException {
		ClipManageIdDto dto = new ClipManageIdDto();
		dto.setProductId(productId);
		dto.setKey("NU_TEMP_DATA");
		ClipManageIdDto resultDto = clipManageIdDao.get(dto);

		if (resultDto == null) {
			resultDto = new ClipManageIdDto();
			resultDto.setProductId(1L);
			resultDto.setKey("NU_TEMP_DATA");
			resultDto.setCreateUserId("admin");
			resultDto.setUpdateUserId("admin");
			resultDto.setValue(1L);

			if (nuFormDataDao.insertNewManageId(resultDto) != 1) {
				throw new IOException("insert 비정상 처리.");
			}
		} else {
			clipManageIdDao.increaseValue(resultDto);
		}

		return resultDto.getValue();
	}

	@Override
	public NuTempDataDto insertNuTempFormData(NuTempDataDto nuTempDataDto) {

		ClipMyBatisTransactionManager tm = getTransactionManager();

		try {
			tm.start();
			int runCount = 0;

			NuDocumentDto docDto = new NuDocumentDto();

			docDto.setFormId(Long.parseLong(nuTempDataDto.getFormId()));
			docDto.setDocumentCode(nuTempDataDto.getFormCode());

			docDto = getDocumentCode(docDto);

			if (docDto == null) {
				throw new Exception("nU Form 데이터 저장 실패");
			}
			nuTempDataDto.setDataIdx(1L);
			nuTempDataDto.setFormRid(Long.parseLong(nuTempDataDto.getFormId()));
			nuTempDataDto.setFormCode("" + docDto.getDocumentCode());

			nuTempDataDto.setDataRid(getDataRid(1L));

			runCount = nuFormDataDao.insertNuTempData(nuTempDataDto);

			if (runCount != 1) {
				throw new IOException("insert 비정상 처리.");
			}
			tm.commit();

		} catch (Exception e) {

			logger.error(e.toString());
			tm.rollback();
			nuTempDataDto = null;

		} finally {
			tm.end();
		}

		return nuTempDataDto;

	}

	@Override
	public NuTempDataDto getNuTempData(String pd) {

		NuTempDataDto paramDto = new NuTempDataDto();
		paramDto.setDataRid(getPdToDataRid(pd));

		NuTempDataDto resultDto = nuFormDataDao.getNuTempFormData(paramDto);

		if (resultDto.getEncoding().equals("UTF8")) {
			return resultDto;
		}

		byte[] formDataByteArray = resultDto.getFormDataByteArray();
		try {

			String formDataEUCKR = new String(formDataByteArray, "EUC-KR");
			String formDataUTF8 = new String(formDataEUCKR.getBytes("UTF-8"), "UTF-8");

			resultDto.setDataXml(formDataUTF8);
		} catch (UnsupportedEncodingException e) {
			resultDto = null;
			logger.error(e.toString());
		}

		return resultDto;
	}

	private Long getPdToDataRid(String pd) {

		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document xmlDoc = builder.parse(new InputSource(new StringReader(pd)));
			Element root = xmlDoc.getDocumentElement();
			NodeList nodeListParam = root.getElementsByTagName("params").item(0).getChildNodes();

			for (int i = 0; i < nodeListParam.getLength(); i++) {
				Node nodeRow = nodeListParam.item(i);

				if (nodeRow.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}

				Element element = (Element) nodeRow;
				String name = element.getAttribute("name");

				if (name.equals("rid")) {
					return new Long(element.getTextContent());
				}

			}

			return null;

		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}

	}

	@Override
	public List<NuFormAndExDto> getClipFormAndConsentFormEx(NuFormAndExDto paramDto) {
		return nuFormDao.getClipFormAndConsentFormEx(paramDto);
	}

	@Override
	public boolean saveConsentFormEx(Long productId, List<NuFormAndExDto> paramDtos) {

		boolean reuslt = true;

		ClipMyBatisTransactionManager tm = getTransactionManager();

		try {

			tm.start();

			for (NuFormAndExDto paramDto : paramDtos) {

				paramDto.setProductId(productId);
				paramDto.setInstCd(companyCode);
				paramDto.setFormCd(paramDto.getDocumentCode());

				NuFormDto formDto = new NuFormDto();
				formDto.setProductId(paramDto.getProductId());
				formDto.setFormId(paramDto.getFormId());
				formDto.setFormType(paramDto.getFormType());

				NuFormAndDataDto formAndDataDto = nuFormDao.get(formDto, paramDto.getFormVersion());

				if (formAndDataDto == null) {
					throw new Exception("서식정보를 찾을 수 없습니다.");
				}

				if (!formAndDataDto.getCommitComment().equals(paramDto.getCommitComment())) {
					nuFormDataDao.updateCommitComment(paramDto);
				}

				nuConsentDao.saveConsentFormEx(paramDto);
			}

			tm.commit();

		} catch (Exception e) {

			logger.error(e.toString());
			tm.rollback();
			reuslt = false;

		} finally {
			tm.end();
		}

		return reuslt;
	}

	@Override
	public List<HashMap> getList_all(ClipFormDto formDto, Boolean eForm_setPublicationYN) {

		HashMap<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("productId", formDto.getProductId());
		parameter.put("formType", formDto.getFormType());

		parameter.put("enableCheckUseYN", enableCheckUseYN);

		if (enableCheckPublicationYN.equals("true")) {
			parameter.put("eForm_setPublicationYN", eForm_setPublicationYN.toString());
		} else {
			parameter.put("eForm_setPublicationYN", "true");
		}

		parameter.put("instCd", companyCode);

		return nuFormDao.getList_all(parameter);
	}

	@Override
	public void saveConsentFormEx(ClipFormDto formDto, Long formVersion) {

		NuConsentFormExDto consentFormExDto = new NuConsentFormExDto();
		consentFormExDto.setInstCd(companyCode);
		consentFormExDto.setFormCd("0000000000");
		consentFormExDto.setFormId(formDto.getFormId());
		consentFormExDto.setFormVersion(formVersion);
		consentFormExDto.setFormType(formDto.getFormType());
		consentFormExDto.setCosignYn("N");
		consentFormExDto.setXmlHistoryYn("N");
		consentFormExDto.setCtlHistoryYn("N");
		consentFormExDto.setExternalCnt((long) 0);
		consentFormExDto.setUserId(formDto.getCreateUserId());

		nuConsentDao.saveConsentFormEx(consentFormExDto);
	}

	@Override
	public void updateClipForm(ClipFormDto formDto) {
		nuFormDao.updateClipForm(formDto);
	}

}
