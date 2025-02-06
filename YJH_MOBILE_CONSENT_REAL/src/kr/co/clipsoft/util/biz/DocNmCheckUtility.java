package kr.co.clipsoft.util.biz;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 1. 특정컨트롤(지정된 저장 데이터 필드가 매핑된)의 값이 일치하는지 여부 판단 2. 로그인 사용자 이름이 의사성명란에 포함되어있는지 확인
 * 
 * @author Pakbg
 *
 */
public class DocNmCheckUtility {

	/**
	 * 의사성명 저장데이터 필드 명
	 */
	private final String[] DATA_FIELD_DOCNM = { "CertDocNm", "ExDocNm", "NurNm" };
	// private final String[] DATA_FIELD_OPDRNM = { "OpdrNm" };

	/**
	 * 파라미터가 매핑되지 않았습니다.
	 */
	public static final int NO_MAPPING_PARAMETER = 0;

	/**
	 * 의사성명란에 사용자 명이 포함
	 */
	public static final int SAME_DOCNM_VALUE = 1;

	/**
	 * 의사성명란에 사용자 명이 포함 되지 않음
	 */
	public static final int DIFFERENT_DOCNM_VALUE = 2;

	private static DocNmCheckUtility instance = null;

	private DocumentBuilder builder = null;
	// private XPath xpath = null;

	private int result = -1;
	// private boolean hasPageTemplate = false;

	public int getResult() {
		return result;
	}

	// public boolean hasPageTemplate() {
	// return hasPageTemplate;
	// }

	private DocNmCheckUtility() throws ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		builder = dbFactory.newDocumentBuilder();
		// xpath = XPathFactory.newInstance().newXPath();
	}

	/**
	 * AttachPageManager 인스턴스 가져오기
	 * 
	 * @return
	 * @throws ParserConfigurationException
	 */
	public static DocNmCheckUtility getInstance() throws ParserConfigurationException {
		if (instance == null) {
			instance = new DocNmCheckUtility();
		}
		return instance;
	}

	/**
	 * XML String 파싱
	 * 
	 * @param dataXml
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public void initDataXml(String eptXml, String userNm) throws SAXException, IOException, XPathExpressionException {
		Document document = builder.parse(new InputSource(new StringReader(eptXml)));
		checkDocNmValue(document, userNm);
	}

	/**
	 * XML 파일 파싱
	 * 
	 * @param file
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public void initDataXml(File eptfile, String userNm) throws SAXException, IOException, XPathExpressionException {
		Document document = builder.parse(eptfile);
		checkDocNmValue(document, userNm);
	}

	public void initDataXml(Document eptDocument, String userNm)
			throws SAXException, IOException, XPathExpressionException {
		checkDocNmValue(eptDocument, userNm);
	}

	private void checkDocNmValue(Document document, String userNm) throws XPathExpressionException {

		NodeList ctlNodeList = document.getElementsByTagName("control");

		// hasPageTemplate = hasPageTemplate(document);
		result = getDocNmCheckValue(ctlNodeList, userNm);
	}

	/**
	 * 컨트롤 리스트에 조건(의사성명)에 부합한 사용자이름이 포함되어있는지 체크
	 * 
	 * @param ctlNodeList
	 * @param userNm
	 * @return
	 */
	private int getDocNmCheckValue(NodeList ctlNodeList, String userNm) {

		int result = NO_MAPPING_PARAMETER;

		for (int idx = 0; idx < ctlNodeList.getLength(); idx++) {

			Node ctlNode = ctlNodeList.item(idx);

			if (ctlNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element ctlElm = (Element) ctlNode;
			String value = getDocNmValue(ctlElm);

			if (value == null) {
				continue;
			}

			if (value.contains(userNm)) {
				return SAME_DOCNM_VALUE;
			} else {
				result = DIFFERENT_DOCNM_VALUE;
			}
		}
		return result;
	}

	/**
	 * 컨트롤에 지정된 저장데이터 필드가 매핑되어있으며 해당 value추출
	 * 
	 * @param ctlElm
	 * @return 컨트롤 값
	 */
	private String getDocNmValue(Element ctlElm) {

		if (!ctlElm.hasAttribute("data-field")) {
			return null;
		}

		String[] dataFieldList = DATA_FIELD_DOCNM;
		// if (hasPageTemplate) {
		// dataFieldList = DATA_FIELD_OPDRNM;
		// } else {
		// dataFieldList = DATA_FIELD_DOCNM;
		// }

		for (String docNmDataField : dataFieldList) {
			if (ctlElm.getAttribute("data-field").equals(docNmDataField)) {
				return ctlElm.getAttribute("value");
			}
		}

		return null;
	}

	// private boolean hasPageTemplate(Document document) throws
	// XPathExpressionException {
	//
	// NodeList nodeList = (NodeList)
	// xpath.evaluate("/document/form/pagemanager/order/page", document,
	// XPathConstants.NODESET);
	//
	// for (int idx = 0; idx < nodeList.getLength(); idx++) {
	// Node pageNode = nodeList.item(idx);
	//
	// if (pageNode.getNodeType() != Node.ELEMENT_NODE) {
	// continue;
	// }
	//
	// Element pageElm = (Element) pageNode;
	//
	// if (!(pageElm.hasAttribute("page-number") &&
	// pageElm.hasAttribute("page-type")
	// && pageElm.hasAttribute("id"))) {
	// continue;
	// }
	//
	// String pageType = pageElm.getAttribute("page-type");
	// String pageId = pageElm.getAttribute("id");
	//
	// if (pageType.equals("Normal") && !pageId.startsWith("page")) {
	// return true;
	// }
	//
	// }
	//
	// return false;
	// }
}
