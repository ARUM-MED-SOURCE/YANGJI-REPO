package kr.co.clipsoft.biz.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 펜드로잉 기능 담당
 *
 *
 */
@Component
public class PenDrawUtility {

	private static final Logger logger = LoggerFactory.getLogger(PenDrawUtility.class);

	/**
	 * DB에서 조회된 펜드로잉 그리기 xml 정보 인터페이스 반환값으로 규격 변경
	 * 
	 * @param strPenDrawingXml
	 * @return
	 */
	public String getPendrawingResult(String strPenDrawingXml) {
		String messageCode = "0";
		String description = "";

		Document docResult = null;
		Document docPenDrawing = null;

		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			docResult = documentBuilder.newDocument();
			docPenDrawing = documentBuilder.parse(new ByteArrayInputStream(strPenDrawingXml.getBytes()));

		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(strPenDrawingXml);
			return getErrorPendrawingResult("-2", "서버에서 XML 생성 오류가 발생하였습니다.");
		}

		Node nodePenDrawing = docResult.importNode(docPenDrawing.getDocumentElement(), true);

		Element elmResult = docResult.createElement("result");
		Element elmData = docResult.createElement("data");
		if (nodePenDrawing != null) {
			elmData.appendChild(nodePenDrawing);
		}
		Element elmMessage = docResult.createElement("message");
		Element elmCode = docResult.createElement("code");
		elmCode.setTextContent(messageCode);
		Element elmDescription = docResult.createElement("description");
		elmDescription.setTextContent(description);

		docResult.appendChild(elmResult);
		elmResult.appendChild(elmData);
		elmResult.appendChild(elmMessage);

		elmMessage.appendChild(elmCode);
		elmMessage.appendChild(elmDescription);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DOMSource source = new DOMSource(docResult);
		StreamResult result = new StreamResult(out);
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = null;

		try {
			transformer = transFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.transform(source, result);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(strPenDrawingXml);
			return getErrorPendrawingResult("-3", "서버에서 XML 생성 오류가 발생하였습니다.");
		}

		return new String(out.toByteArray(), StandardCharsets.UTF_8);

	}

	/**
	 * 펜그리기 불러오기 오류값 반환
	 * 
	 * @param code 오류코드
	 * @param msg  오류 메시지
	 * @return
	 */
	public String getErrorPendrawingResult(String code, String msg) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?> ");
		sb.append("<result> ");
		sb.append("    <data> ");
		sb.append("    </data> ");
		sb.append("    <message> ");
		sb.append("        <code> ");
		sb.append(code);
		sb.append("        </code> ");
		sb.append("        <description> ");
		sb.append(msg);
		sb.append("        </description> ");
		sb.append("    </message> ");
		sb.append("</result>");
		return sb.toString();
	}
}
