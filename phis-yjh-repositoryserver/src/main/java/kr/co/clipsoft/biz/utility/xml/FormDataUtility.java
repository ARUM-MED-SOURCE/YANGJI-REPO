package kr.co.clipsoft.biz.utility.xml;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 서식 FORM DATA(XML) 파서 기능담당
 *
 *
 */
@Component
public class FormDataUtility {

	private static final Logger logger = LoggerFactory.getLogger(FormDataUtility.class);

	private FormDataXmlSaxParserHandler formDataSaxParserHandler;
	private SAXParser sp;

	public FormDataUtility() throws ParserConfigurationException, SAXException {

		formDataSaxParserHandler = new FormDataXmlSaxParserHandler();
		sp = SAXParserFactory.newInstance().newSAXParser();

	}

	public Long getExternalCtlCount(String formData) {

		try {

			byte[] formDataByteValue = formData.getBytes("UTF-8");
			sp.parse(new ByteArrayInputStream(formDataByteValue), formDataSaxParserHandler);

			return (long) formDataSaxParserHandler.getExternalControlCnt();

		} catch (Exception e) {
			logger.error("XML 파싱에 에러가 발생하였습니다. : " + e.toString());
			e.printStackTrace();
		}

		return null;
	}

	class FormDataXmlSaxParserHandler extends DefaultHandler {

		private final String TAG_CONTROL = "control";
		private final String ATTR_CONTROL_TYPE = "control-type";
		private final String ATTR_VALUE_EXTERNAL_GROUP = "ExternalGroup";

		private int externalControlCnt;

		@Override
		public void startDocument() throws SAXException {
			externalControlCnt = 0;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

			if (!qName.trim().equals(TAG_CONTROL)) {
				return;
			}

			String controlType = attributes.getValue(ATTR_CONTROL_TYPE);

			if (controlType != null && controlType.equals(ATTR_VALUE_EXTERNAL_GROUP)) {
				externalControlCnt++;
			}
		}

		public int getExternalControlCnt() {
			return externalControlCnt;
		}

	}

}
