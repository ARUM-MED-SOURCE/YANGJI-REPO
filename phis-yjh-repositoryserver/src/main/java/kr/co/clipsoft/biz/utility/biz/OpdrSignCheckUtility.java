package kr.co.clipsoft.biz.utility.biz;

import java.io.ByteArrayInputStream;
import java.io.File;

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
 * 시술의 업무로직 기능 담당
 *
 *
 */
@Component
public class OpdrSignCheckUtility {

	private static final Logger logger = LoggerFactory.getLogger(OpdrSignCheckUtility.class);

	private OpdrSignCheckXmlParserHandler saxParserHandler;
	private SAXParser sp;

	public OpdrSignCheckUtility() throws ParserConfigurationException, SAXException {
		saxParserHandler = new OpdrSignCheckXmlParserHandler();
		sp = SAXParserFactory.newInstance().newSAXParser();
	}

	public String getOpdrSignYn(String dataXml) {

		try {

			byte[] formDataByteValue = dataXml.getBytes("UTF-8");
			sp.parse(new ByteArrayInputStream(formDataByteValue), saxParserHandler);

			return saxParserHandler.getOpdrSignYn();

		} catch (Exception e) {
			logger.error("DATA XML 파싱에 에러가 발생하였습니다. : " + e.toString());
			e.printStackTrace();
		}

		return null;
	}

	public String getOpdrSignYn(File file) {

		try {

			sp.parse(file, saxParserHandler);

			return saxParserHandler.getOpdrSignYn();

		} catch (Exception e) {
			logger.error("DATA XML(FILE) 파싱에 에러가 발생하였습니다. : " + e.toString());
			e.printStackTrace();
		}

		return null;
	}

	class OpdrSignCheckXmlParserHandler extends DefaultHandler {

		private final String TAG_OPDR_NM = "OpdrNm";
		private final String TAG_OPDR_SIGN = "OpdrSign";

		private String opdrSignYn = "";

		private boolean isReadValue = false;

		@Override
		public void startDocument() throws SAXException {
			opdrSignYn = "N";
		}

		@Override
		public void startElement(String uri, String localName, String tagName, Attributes attributes) throws SAXException {

			if (tagName.equals(TAG_OPDR_NM) || tagName.equals(TAG_OPDR_SIGN)) {
				isReadValue = true;
			} else {
				isReadValue = false;
			}

		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {

			if (!isReadValue) {
				return;
			}

			String opdrSignValue = new String(ch, start, length).trim();

			if (opdrSignValue != null && opdrSignValue.length() > 0 && !opdrSignValue.equals("\n")) {
				opdrSignYn = "Y";
			}

		}

		public String getOpdrSignYn() {
			return opdrSignYn;
		}

	}

}
