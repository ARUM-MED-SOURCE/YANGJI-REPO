package kr.co.clipsoft.biz.utility.biz;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import kr.co.clipsoft.biz.exception.BizErrorInfo;
import kr.co.clipsoft.biz.exception.BusinessException;

/**
 * 
 * <pre>
 * 1. 특정컨트롤(지정된 저장 데이터 필드가 매핑된)의 값이 일치하는지 여부 판단
 * 2. 인증저장시 로그인 사용자 이름이 의사/간호사 성명란에 포함되어있는지 확인
 * </pre>
 *
 *
 */

@Component
public class SignUserNmCheckUtility {

	private static final Logger logger = LoggerFactory.getLogger(SignUserNmCheckUtility.class);

	/**
	 * 의사/간호사 성명 저장데이터 필드 명
	 */
	private final String[] DATA_FIELD_SIGN_USER_NM = { "CertDocNm", "ExDocNm", "NurNm" };

	/**
	 * 의사성명란에 사용자 명이 포함 되지 않음
	 */
	public enum CHECK_RESULT {
		/**
		 * 파라미터가 매핑되지 않았습니다.
		 */
		NO_MAPPING_PARAMETER,
		/**
		 * 의사/간호사 성명란에 사용자 명이 포함
		 */
		SAME_NM_VALUE,

		/**
		 * 의사/간호사 성명란에 사용자 명이 포함 되지 않음
		 */
		DIFFERENT_NM_VALUE
	}

	private DocumentBuilder builder = null;
	private XPath xpath = null;

	public SignUserNmCheckUtility() throws ParserConfigurationException {
		builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		xpath = XPathFactory.newInstance().newXPath();
	}

	/**
	 * 인증저장시 의사/간호사 성명란이 접속자와 다른지 확인
	 * 
	 * @param save     인증저장 여부
	 * @param userName 접속자 이릅
	 * @param formXml  eptXml
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public void validationCheckSignUserNmDiff(boolean save, String userName, String formXml)
			throws SAXException, IOException, XPathExpressionException, BusinessException {

		if (!save) {
			return;
		}

		if (isEptz(formXml)) {
			formXml = decompressionEptzData(formXml);
		}

		Document document = builder.parse(new InputSource(new StringReader(formXml)));
		Element elmForm = (Element) xpath.evaluate("/document/form", document, XPathConstants.NODE);
		NodeList ctlNodeList = elmForm.getElementsByTagName("control");

		CHECK_RESULT checkResult = getSignUserNmCheckResult(ctlNodeList, userName);
		if (checkResult == CHECK_RESULT.DIFFERENT_NM_VALUE) {
			throw new BusinessException(BizErrorInfo.ERROR_USER_NM_DIFFERENT);
		}

	}

	/**
	 * 컨트롤 리스트에 조건(의사/간호사 성명)에 부합한 사용자이름이 포함되어있는지 체크
	 * 
	 * @param ctlNodeList
	 * @param userNm
	 * @return
	 */
	private CHECK_RESULT getSignUserNmCheckResult(NodeList ctlNodeList, String userNm) {

		CHECK_RESULT result = CHECK_RESULT.NO_MAPPING_PARAMETER;

		if (ctlNodeList == null) {
			return result;
		}

		for (int idx = 0; idx < ctlNodeList.getLength(); idx++) {

			Node ctlNode = ctlNodeList.item(idx);

			if (ctlNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			String ctlType = getAttributeByValue((Element) ctlNode, "control-type", "");
			if (!(ctlType.equals("InlineTextEdit") || ctlType.equals("MultilineTextEdit"))) {
				continue;
			}

			String ctlValue = getSignUserNmValue((Element) ctlNode);
			if (ctlValue == null) {
				continue;
			}

			if (ctlValue.contains(userNm)) {
				return CHECK_RESULT.SAME_NM_VALUE;
			} else {
				result = CHECK_RESULT.DIFFERENT_NM_VALUE;
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
	private String getSignUserNmValue(Element ctlElm) {

		if (!ctlElm.hasAttribute("data-field")) {
			return null;
		}

		for (String signUserNmDataField : DATA_FIELD_SIGN_USER_NM) {
			if (ctlElm.getAttribute("data-field").equals(signUserNmDataField)) {
				return ctlElm.getAttribute("value");
			}
		}

		return null;
	}

	/**
	 * Attr Name에 해당되는 Value 조회
	 * 
	 * @param elm
	 * @param attrName     조회 대상 Attr Name
	 * @param defaultValue Attr이 존재하지 않을시 기본값
	 * @return 조회값
	 */
	private String getAttributeByValue(Element elm, String attrName, String defaultValue) {

		if (!elm.hasAttribute(attrName)) {
			return defaultValue;
		}

		return elm.getAttribute(attrName);
	}

	/**
	 * 해당 eptData가 압축된 데이터인지 확인
	 * 
	 * @param eptData
	 * @return 압축 여부
	 */
	private boolean isEptz(String eptData) {

		if (eptData.startsWith("<?xml")) {
			return false;
		}

		return true;
	}

	/**
	 * eptzData 압축 해제
	 * 
	 * @param eptzData
	 * @return eptData
	 */
	private String decompressionEptzData(String eptzData) {

		String eptData = null;
		InputStream gzipIs = null;
		ByteArrayOutputStream baos = null;

		try {

			byte[] compressed = Base64.decode(eptzData.getBytes());
			gzipIs = new GZIPInputStream(new ByteArrayInputStream(compressed));
			baos = new ByteArrayOutputStream();

			int sChunk = 1024;
			byte[] buffer = new byte[sChunk];
			int length;
			while ((length = gzipIs.read(buffer, 0, sChunk)) != -1) {
				baos.write(buffer, 0, length);
			}
			baos.flush();

			eptData = baos.toString();

		} catch (Exception e) {
			eptData = null;
			logger.error("FORM XML 압축해제 오류1 : " + e.toString());
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					logger.error("FORM XML 압축해제 오류2 : " + e.toString());
				}
			}
			if (gzipIs != null) {
				try {
					gzipIs.close();
				} catch (IOException e) {
					logger.error("FORM XML 압축해제 오류3 : " + e.toString());
				}
			}
		}

		if (eptData == null || eptData.isEmpty()) {
			throw new NullPointerException("EPT DATA 변환 실패");
		}

		return eptData;
	}
}
