package kr.co.clipsoft.util.biz;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class SaveValidationCheckManager {

	/**
	 * 전달되는 파라미터와 서식내의 환자번호, OCRTAG비교하여 저장 유효성 검사
	 * 
	 * @param formXml formXml
	 * @param pid     저장하려는 환자번호
	 * @param ocrTag  저장하려는 OCR_TAG
	 * @return 파라미터 일치여부
	 */
	public static boolean isSamePidAndOcrTagInForm(String formXml, String pid, String ocrTag) {
		boolean result = false;

		InputSource is = null;
		Document document = null;
		XPath xpath = null;

		try {
			ocrTag = ocrTag.replaceAll("[+]", "").trim();

			is = new InputSource(new StringReader(formXml));
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			xpath = XPathFactory.newInstance().newXPath();

			Node nodeOcrTag = (Node) xpath.evaluate("document/global/fields/field-set[@field-type=\"parameter\"]/field[@name=\"ocrtag\"]", document,
					XPathConstants.NODE);
			Node nodePid = (Node) xpath.evaluate("document/global/fields/field-set[@field-type=\"parameter\"]/field[@name=\"PID\"]", document,
					XPathConstants.NODE);

			String formOcrTag = nodeOcrTag.getAttributes().getNamedItem("value").getTextContent().replaceAll("[+]", "").trim();
			String formPid = nodePid.getAttributes().getNamedItem("value").getTextContent().trim();

			if (pid.equals(formPid) && ocrTag.equals(formOcrTag)) {
				result = true;
			}

		} catch (Exception e) {
			System.out.println("isSamePidAndOcrTagInForm ERROR : " + e.toString());
		} finally {

			if (xpath != null) {
				xpath = null;
			}

			if (document != null) {
				document = null;
			}

			if (is != null) {
				is = null;
			}

		}

		return result;

	}

}
