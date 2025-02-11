package kr.co.clipsoft.biz.controller;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.co.clipsoft.biz.utility.WebUtility;
import kr.co.clipsoft.repository.session.SessionInfoUtility;
import kr.co.clipsoft.repository.web.ClipHttpHeadersFactory;
import kr.co.clipsoft.repository.web.ClipResponseEntityFactory;


@Controller
@RequestMapping(value = "/biz/nu/member/category", produces = "application/json; charset=UTF-8;")
public class NuCategoryController {
	
	private static final Logger logger = LoggerFactory.getLogger(NuCategoryController.class);
	private static final Long PRODUCT_ID = 1L;
	
	@Value("#{customerProperties['server.companyCode']}")
	private String companyCode;
	
	@Value("#{customerProperties['server.exceution.mode']}")
	private String exceutionMode;

	@Autowired
	private WebUtility webUtility;
	
	@Autowired
	private ClipResponseEntityFactory clipResponseEntityFactory;
	
	@Autowired
	private ClipHttpHeadersFactory clipHttpHeadersFactory;
	
	
	@RequestMapping(value = "/getList", method = RequestMethod.POST)
	@ResponseBody
	public String getList(HttpSession session) {
		try {
			Object userLoginInfo = session.getAttribute("userLoginInfo");
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return "{}";
	}

	@RequestMapping(value = "/getList/CategoryAndForm", method = RequestMethod.POST)
	public ResponseEntity<String> getList_all(
			HttpSession session
			, @RequestParam(value = "parameter", required = true) String parameter) {
		
			try {
				Gson gson = new GsonBuilder()
						   .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
							
				HashMap parameterMap = gson.fromJson(parameter, HashMap.class);
				
				List<HashMap> list = getList_all_categoryAndForm(session, parameterMap);
				gson.toJson(list);
				
				HttpHeaders httpHeader = clipHttpHeadersFactory.createCookieHeader(session);
				return clipResponseEntityFactory.create(gson.toJson(list), httpHeader, HttpStatus.OK);
			} catch (Exception e) {
				logger.error(e.toString());
				return clipResponseEntityFactory.createInternalServerError();
			}
		}

	private List<HashMap> getList_all_categoryAndForm(HttpSession session, HashMap parameterMap) {
		try
		{
			String itemName = (String) parameterMap.get("itemName");
			String effective = parameterMap.get("effective").toString();
			String myInstitution = parameterMap.get("myInstitution").toString();
			String loginUserId = SessionInfoUtility.getLoginUserId(session);
			
			String resultXml =  sendRequestToNuCategoryService(effective, myInstitution, loginUserId, itemName);
			
			if(resultXml.isEmpty())
			{
				return new ArrayList<HashMap>();
			}

			return xmlDataToHashTable(resultXml);
		}
		catch(Exception e)
		{
			logger.error(e.toString());
			return new ArrayList<HashMap>();
		}
	}
	
	private String sendRequestToNuCategoryService(String effective, String myInstitution, String userId, String itemName) {
		try {
			
			String submitId = "";
			HashMap<String, String> map = new HashMap<String, String>();
			
			if(exceutionMode.equals("CLIP"))
			{
				map.put("OP", "DESIGNER_CATEGORY");
				map.put("PD", "<data><action>GET</action><params><param name='uid'><![CDATA[admin]]></param><param name='rid'><![CDATA[*]]></param><param name='level'><![CDATA[*]]></param><param name='adaptername'><![CDATA[defaultadapter]]></param></params><commonparams></commonparams></data>");
			}
			else
			{
				if(itemName.isEmpty())
				{
					submitId = "DRMRF00111";
				}
				else
				{
					submitId = "DRMRF00112";
					map.put("formnm", itemName);
				}
				
				map.put("business_id", "mr");
				map.put("userid", userId);
				map.put("instcd", companyCode);
				map.put("effective", effective);
				map.put("myinstitution", myInstitution);
			}
			
	        return webUtility.sendRequestService(submitId, map);
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}
	
	private List<HashMap> xmlDataToHashTable(String xml)
	{
		try {
			List<HashMap> aList = new ArrayList<HashMap>();
			logger.debug(xml);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
				builder = dbFactory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(xml)));
			
			XPath xpath = XPathFactory.newInstance().newXPath();

			HashMap<String, String> item = new HashMap<String, String>();
			

			if(exceutionMode.equals("CLIP"))
			{
				/*
				 * CLIP 내부 테스트 모듈
				 * */
				NodeList nodeList = (NodeList)xpath.evaluate("/result/data/rows/row", document, XPathConstants.NODESET);
				
				logger.debug("NodeList Count : " + nodeList.getLength());	

		        for( int idx=0; idx<nodeList.getLength(); idx++ ){
		        	item = new HashMap<String, String>();
		        			        	
				    String Seq = (nodeList.item(idx).getAttributes().getNamedItem("seq") == null)? "" : nodeList.item(idx).getAttributes().getNamedItem("seq").getTextContent();
				    String isform = (nodeList.item(idx).getAttributes().getNamedItem("isform") == null)? "" : nodeList.item(idx).getAttributes().getNamedItem("isform").getTextContent();
				    String doctype = (nodeList.item(idx).getAttributes().getNamedItem("doctype") == null)? "TYPE3" : nodeList.item(idx).getAttributes().getNamedItem("doctype").getTextContent();
				    String name = (nodeList.item(idx).getAttributes().getNamedItem("name") == null)? "" : nodeList.item(idx).getAttributes().getNamedItem("name").getTextContent();
				    String extname = (nodeList.item(idx).getAttributes().getNamedItem("extname") == null)? "" : nodeList.item(idx).getAttributes().getNamedItem("extname").getTextContent();
				    String guid = (nodeList.item(idx).getAttributes().getNamedItem("guid") == null)? "" : nodeList.item(idx).getAttributes().getNamedItem("guid").getTextContent();
				    String categoryid = (nodeList.item(idx).getAttributes().getNamedItem("categoryid") == null)? "" : nodeList.item(idx).getAttributes().getNamedItem("categoryid").getTextContent();
				    String iswrite = (nodeList.item(idx).getAttributes().getNamedItem("iswrite") == null)? "true" : nodeList.item(idx).getAttributes().getNamedItem("iswrite").getTextContent();
				    String prid = (nodeList.item(idx).getAttributes().getNamedItem("prid") == null)? "" : nodeList.item(idx).getAttributes().getNamedItem("prid").getTextContent();
				    String rid = (nodeList.item(idx).getAttributes().getNamedItem("rid") == null)? "" : nodeList.item(idx).getAttributes().getNamedItem("rid").getTextContent();
				    String formcd = (nodeList.item(idx).getAttributes().getNamedItem("formcd") == null)? "" : nodeList.item(idx).getAttributes().getNamedItem("formcd").getTextContent();
				    String rgstinstcd = (nodeList.item(idx).getAttributes().getNamedItem("rgstinstcd") == null)? "" : nodeList.item(idx).getAttributes().getNamedItem("rgstinstcd").getTextContent();
				    String langflag = (nodeList.item(idx).getAttributes().getNamedItem("langflag") == null)? "" : nodeList.item(idx).getAttributes().getNamedItem("langflag").getTextContent();
				    String formid = (nodeList.item(idx).getAttributes().getNamedItem("formid") == null)? "" : nodeList.item(idx).getAttributes().getNamedItem("formid").getTextContent();
				    
				    String deptCount = "0";
				    String deptName = "";
					item.put("seq" ,Seq);
					item.put("itemType" ,isform.equals("true") ? "form" : "category");
					item.put("itemId", isform.equals("true") ? guid : rid);
					item.put("itemName" ,name);
					item.put("exnName" ,extname);
					item.put("doctype" ,doctype);
					item.put("guid" ,guid);
					item.put("categoryid" ,categoryid);
					item.put("iswrite" ,iswrite);
					item.put("parentItemId" ,prid);
					item.put("formcd",formcd);				
					item.put("rgstinstcd",rgstinstcd);
					item.put("langflag",langflag);

					if(iswrite.equals("true"))
					{
						item.put("authcode", "CURDPI");
					}
					else
					{
						item.put("authcode", "R");
					}					

		        	aList.add(item);
		        }
			}
			else
			{
				/*
				 * nU Response Data
				 * */
				NodeList nodeList = (NodeList)xpath.evaluate("/root/data/rows/row", document, XPathConstants.NODESET);
				logger.debug("NodeList Count : " + nodeList.getLength());
				
		        for( int idx=0; idx<nodeList.getLength(); idx++ ){

		        	if(nodeList.item(idx).hasChildNodes())
		        	{
			        	item = new HashMap<String, String>();
						NodeList childList = nodeList.item(idx).getChildNodes();

						String Seq = "";
						String isform =""; 
						String doctype =""; 
						String name = "";
						String extname = "";
						String guid =  "";
						String categoryid = "";
						String iswrite =  "";
						String prid = "";
						String rid =  "";
						String formcd ="";  
						String rgstinstcd ="";  
						String langflag =  "";
						String formid =  "";
						
						String deptCount = "0";
						String deptName = "";

				        for( int childIdx=0; childIdx<childList.getLength(); childIdx++ ){
			        	
				        	if(childList.item(childIdx).getNodeName().equals("seq")){
				        		Seq = childList.item(childIdx).getTextContent();
				        	}
				        	if(childList.item(childIdx).getNodeName().equals("isform")){
				        		isform = childList.item(childIdx).getTextContent();
				        	}		
				        	if(childList.item(childIdx).getNodeName().equals("doctype")){
				        		doctype = childList.item(childIdx).getTextContent();
				        	}		
				        	if(childList.item(childIdx).getNodeName().equals("name")){
				        		name = childList.item(childIdx).getTextContent();
				        	}		
				        	if(childList.item(childIdx).getNodeName().equals("extname")){
				        		extname = childList.item(childIdx).getTextContent();
				        	}		
				        	if(childList.item(childIdx).getNodeName().equals("guid")){
				        		guid = childList.item(childIdx).getTextContent();
				        	}		
				        	if(childList.item(childIdx).getNodeName().equals("categoryid")){
				        		categoryid = childList.item(childIdx).getTextContent();
				        	}	
				        	if(childList.item(childIdx).getNodeName().equals("iswrite")){
				        		iswrite = childList.item(childIdx).getTextContent();
				        	}		
				        	if(childList.item(childIdx).getNodeName().equals("prid")){
				        		prid = childList.item(childIdx).getTextContent();
				        	}		
				        	if(childList.item(childIdx).getNodeName().equals("rid")){
				        		rid =  childList.item(childIdx).getTextContent();
				        	}			
				        	if(childList.item(childIdx).getNodeName().equals("formcd")){
				        		formcd = childList.item(childIdx).getTextContent();
				        	}		
				        	if(childList.item(childIdx).getNodeName().equals("rgstinstcd")){
				        		rgstinstcd = childList.item(childIdx).getTextContent();				        		
				        	}	
				        	if(childList.item(childIdx).getNodeName().equals("langflag")){
				        		langflag = childList.item(childIdx).getTextContent();				        		
				        	}	
				        	if(childList.item(childIdx).getNodeName().equals("formid")){
				        		formid = childList.item(childIdx).getTextContent();				        		
				        	}	
				        	else
				        	{
								item.put(childList.item(childIdx).getNodeName() ,childList.item(childIdx).getTextContent());
				        	}
				        }
				        

						item.put("seq" ,Seq);
						item.put("itemType" ,isform.equals("true") ? "form" : "category");
						item.put("parentItemId" ,prid);
						item.put("itemId", isform.equals("true") ? guid: rid);
						
						item.put("itemName" ,name);
						item.put("exnName" ,extname);
						item.put("doctype" ,doctype);
						item.put("guid" ,guid);
						item.put("categoryid" ,categoryid);
						item.put("iswrite" ,iswrite);
						item.put("formcd",formcd);				
						item.put("rgstinstcd",rgstinstcd);
						item.put("langflag",langflag);

						if(iswrite.equals("true"))
						{
							item.put("authcode", "CURDPI");
						}
						else
						{
							item.put("authcode", "R");
						}		
			        	aList.add(item);
		        	}
		        }
				
			}
			
			return aList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
			return new ArrayList<HashMap>();
		}
		
	}
}
