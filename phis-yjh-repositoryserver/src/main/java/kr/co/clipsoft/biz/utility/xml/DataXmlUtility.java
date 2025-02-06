package kr.co.clipsoft.biz.utility.xml;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import kr.co.clipsoft.biz.exception.BizErrorInfo;
import kr.co.clipsoft.biz.exception.BusinessException;

/**
 * 저장된 동의서 정보 파서 기능담당(DATA XML)
 *
 *
 */
@Component
public class DataXmlUtility {

	private static final Logger logger = LoggerFactory.getLogger(DataXmlUtility.class);

	/**
	 * [저장데이터필드] 최종완료를 위한 전자인증 필요 개수
	 */
	public static final String DATA_FIELD_CERT_NEED_CNT = "consent_cert_need_cnt";

	/**
	 * [저장데이터필드][연명] 다음 주치의ID
	 */
	public static final String DATA_FIELD_NEXT_AT_DOC_ID = "consent_next_atdoctid";

	/**
	 * [저장데이터필드][연명] 다음 주치의 명
	 */
	public static final String DATA_FIELD_NEXT_AT_DOC_NAME = "consent_next_atdoctnm";

	/**
	 * [저장데이터필드][연명] 첫번째 의사ID
	 */
	public static final String DATA_FIELD_FIR_SIGN_USERID = "FIR_SIGN_USERID";

	/**
	 * [저장데이터필드][연명] 두번째 의사ID
	 */
	public static final String DATA_FIELD_SEC_SIGN_USERID = "SEC_SIGN_USERID";

	/**
	 * [저장데이터필드][저장완료 후 알림] 저장 후 팝업 알림 메시지
	 */
	public static final String DATA_FIELD_VIEW_SAVE_FINISH_MSG = "consent_view_save_finish_msg";

	/**
	 * DataXml 제외 태그 리스트
	 */
	private final String[] EXCEPT_TAGS = { "page-template-data", "pen-drawing", "changed-form", "viewer-info", "form-info" };

	private DocumentBuilder builder = null;
	private XPath xpath = null;

	public DataXmlUtility() throws ParserConfigurationException {
		builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		xpath = XPathFactory.newInstance().newXPath();
	}

	/**
	 * 저장데이터 필드값 반환
	 * 
	 * @param dataXml      DATA XML
	 * @param tagName      조회대상 저장데이터필드 TAG NAME
	 * @param defaultValue 값이NULL 또는 비어있을경우 기본값
	 * @return
	 * @throws BusinessException
	 */
	public String getFormDataFieldValue(String dataXml, String tagName, String defaultValue) throws BusinessException {

		List<Element> formDataFieldElementList = getFormDataFieldElementList(dataXml);

		for (Element dataElm : formDataFieldElementList) {
			if (dataElm.getTagName().equals(tagName) && dataElm.getTextContent() != null && dataElm.getTextContent().length() > 0) {
				return dataElm.getTextContent();
			}
		}

		return defaultValue;
	}

	/**
	 * 저장데이터 필드 Element 리스트 반환
	 * 
	 * @param dataXml DATA XML
	 * @return
	 * @throws BusinessException
	 */
	private List<Element> getFormDataFieldElementList(String dataXml) throws BusinessException {

		if (dataXml == null || dataXml.length() == 0) {
			logger.error("DataXml 데이터가 존재하지 않습니다.");
			throw new BusinessException(BizErrorInfo.NO_DATA_XML);
		}

		NodeList formDataNodeList = null;
		try {
			Document document = builder.parse(new InputSource(new StringReader(dataXml)));
			NodeList nodeList = (NodeList) xpath.evaluate("/form-data", document, XPathConstants.NODESET);
			formDataNodeList = nodeList.item(0).getChildNodes();

		} catch (Exception e) {
			logger.error("DataXml 파싱중 오류가 발생하였습니다. : " + e.toString());
			formDataNodeList = null;
		}

		if (formDataNodeList == null || formDataNodeList.getLength() <= 0) {
			throw new BusinessException(BizErrorInfo.ERROR_PARSER_DATA_XML);
		}

		List<Element> formDataFieldList = new ArrayList<Element>();
		Set<String> exceptTags = new HashSet<String>(Arrays.asList(EXCEPT_TAGS));
		for (int index = 0; index < formDataNodeList.getLength(); index++) {

			Node dataNode = formDataNodeList.item(index);
			if (dataNode.getNodeType() != Node.ELEMENT_NODE || exceptTags.contains(dataNode.getNodeName())) {
				continue;
			}

			formDataFieldList.add((Element) dataNode);
		}

		return formDataFieldList;
	}

}
