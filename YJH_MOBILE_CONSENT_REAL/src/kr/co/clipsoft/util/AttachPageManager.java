package kr.co.clipsoft.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class AttachPageManager {

	private static AttachPageManager instance = null;

	private DocumentBuilder builder = null;
	private XPath xpath = null;

	/**
	 * 첨지 페이지 수
	 */
	private int attachPageCount = 0;
	/**
	 * 페이지 템플릿 페이지 수
	 */
	private int attachPagetemplateCount = 0;

	private AttachPageManager() throws ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		builder = dbFactory.newDocumentBuilder();
		xpath = XPathFactory.newInstance().newXPath();
	}

	/**
	 * AttachPageManager 인스턴스 가져오기
	 * 
	 * @return
	 * @throws ParserConfigurationException
	 */
	public static AttachPageManager getInstance() throws ParserConfigurationException {
		if (instance == null) {
			instance = new AttachPageManager();
		}
		return instance;
	}

	/**
	 * XML String 파싱
	 * 
	 * @param formXml
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public void initFormXml(String formXml) throws SAXException, IOException, XPathExpressionException {
		Document document = builder.parse(new InputSource(new StringReader(formXml)));
		searchAttachPageCount(document);
	}

	/**
	 * XML 파일 파싱
	 * 
	 * @param file
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public void initFormXml(File file) throws SAXException, IOException, XPathExpressionException {
		Document document = builder.parse(file);
		searchAttachPageCount(document);
	}

	/**
	 * XML 데이터에서 첨지, 페이지템플릿 개수 체크
	 * 
	 * @param document
	 * @throws XPathExpressionException
	 */
	private void searchAttachPageCount(Document document) throws XPathExpressionException {
		attachPageCount = 0;
		attachPagetemplateCount = 0;

		NodeList nodeList = (NodeList) xpath.evaluate("/document/form/pagemanager/order/page", document,
				XPathConstants.NODESET);

		for (int idx = 0; idx < nodeList.getLength(); idx++) {
			Node pageNode = nodeList.item(idx);

			if (pageNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element pageElm = (Element) pageNode;

			if (!(pageElm.hasAttribute("page-number") && pageElm.hasAttribute("page-type")
					&& pageElm.hasAttribute("id"))) {
				continue;
			}

			String pageType = pageElm.getAttribute("page-type");
			String pageId = pageElm.getAttribute("id");

			if (pageType.equals("Attach")) {
				attachPageCount++;
			} else if (pageType.equals("Normal") && !pageId.startsWith("page")) {
				attachPagetemplateCount++;
			}

		}
	}

	/**
	 * 첨지 페이지수 가져오기
	 * 
	 * @return
	 */
	public int getAttachPageCount() {
		return attachPageCount;
	}

	/**
	 * 페이지 템플릿 페이지수 가져오기
	 * 
	 * @return
	 */
	public int getAttachPagetemplateCount() {
		return attachPagetemplateCount;
	}

}
