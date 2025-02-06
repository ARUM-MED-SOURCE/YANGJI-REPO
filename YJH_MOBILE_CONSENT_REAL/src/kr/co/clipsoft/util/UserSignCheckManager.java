package kr.co.clipsoft.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

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

import android.os.Environment;

public class UserSignCheckManager {
	private static UserSignCheckManager instance = null;

	private DocumentBuilder builder = null;
	private XPath xpath = null;
	private HashMap<String, String[]> mapSignCheckTarget = null;
	private HashMap<String, String> mapSignCheckResult = null;

	private UserSignCheckManager() throws ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		builder = dbFactory.newDocumentBuilder();
		xpath = XPathFactory.newInstance().newXPath();

		mapSignCheckTarget = new HashMap<>();
		mapSignCheckTarget.put("drsign", new String[] { "/CertDocSign", "/ExDocSign" });
		mapSignCheckTarget.put("nrsign", new String[] { "/NurSign" });
		mapSignCheckTarget.put("patsign", new String[] { "/PatSign" });
		mapSignCheckTarget.put("procersign", new String[] { "/GuaSign" });
		//2021-12-02
		mapSignCheckTarget.put("signflag", new String[] { "/VerbalAndMulti" });
		mapSignCheckTarget.put("etcsign", new String[] {});

		mapSignCheckResult = new HashMap<>();
		mapSignCheckResult.put("drsign", "-");
		mapSignCheckResult.put("nrsign", "-");
		mapSignCheckResult.put("patsign", "-");
		mapSignCheckResult.put("procersign", "-");
		//2021-12-02
		mapSignCheckResult.put("signflag", "-");
		mapSignCheckResult.put("etcsign", "-");
	}

	/**
	 * AttachPageManager 인스턴스 가져오기
	 * 
	 * @return
	 * @throws ParserConfigurationException
	 */
	public static UserSignCheckManager getInstance() throws ParserConfigurationException {
		if (instance == null) {
			instance = new UserSignCheckManager();
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
	public void initFormXml(String dataXml) throws SAXException, IOException, XPathExpressionException {
		Document document = builder.parse(new InputSource(new StringReader(dataXml)));
		doSignCheck(document);
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
		doSignCheck(document);
	}

	/**
	 * XML 데이터에서 미비서명 체크
	 * 
	 * @param document
	 * @throws XPathExpressionException
	 */
	private void doSignCheck(Document document) throws XPathExpressionException {

		String[] externalCtlIdList = { "", "/nurse_sign", "/doctorsing23124215215219802152142", "/babysign",
				"/doctorsignrusia", "/doctor431eng", "/doctorsignjapan", "/docotor_Sign", "/nurse_babysign",
				"/nurse_Sign" };

		mapSignCheckResult.put("drsign", "-");
		mapSignCheckResult.put("nrsign", "-");
		mapSignCheckResult.put("patsign", "-");
		mapSignCheckResult.put("procersign", "-");
		//2021-12-02
		mapSignCheckResult.put("signflag", "-");
		mapSignCheckResult.put("etcsign", "-");

		for (String signId : mapSignCheckTarget.keySet()) {
			String[] outParamNames = mapSignCheckTarget.get(signId);

			for (String outParamName : outParamNames) {

				for (String externalGrpId : externalCtlIdList) {
					NodeList nodeList = (NodeList) xpath.evaluate("/form-data" + externalGrpId + outParamName, document,
							XPathConstants.NODESET);

					if (!(nodeList.getLength() > 0 && nodeList.item(0).getNodeType() == Node.ELEMENT_NODE)) {
						continue;
					}

					Element elmOutParam = (Element) nodeList.item(0); 

					//2021-12-02
					if (elmOutParam.getTextContent().trim().length() > 0) {
						if (outParamName.equals("/VerbalAndMulti")) {
							mapSignCheckResult.put(signId, elmOutParam.getTextContent().trim());
						} else {
							mapSignCheckResult.put(signId, "Y");
						}
						break;
					} else if (!mapSignCheckResult.get(signId).equals("Y")) {
						mapSignCheckResult.put(signId, "N");
					}
				}

			}

		}

	}

	public HashMap<String, String> getSignCheckResult() {
		return (HashMap<String, String>) mapSignCheckResult.clone();
	}

}
