package org.openmrs.module.mclinic.web.controller.io;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.mclinic.api.MclinicXform;
import org.openmrs.module.mclinic.api.service.MclinicService;
import org.openmrs.module.mclinic.api.utils.MclinicConstants;
import org.openmrs.module.xforms.XformConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Creates an formList XMl from which a phone will get resources
 * 
 * @author Samuel Mbugua
 */

@Controller
public class DownloadXformController {
	private static Log log = LogFactory.getLog(DownloadXformController.class);
	private static final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder docBuilder;
	
	@RequestMapping(value="/module/mclinic/download/xformList", method=RequestMethod.POST)
	protected void getFormList(HttpServletRequest request, HttpServletResponse response) throws IOException {
		GZIPInputStream gzipInputStream = new GZIPInputStream(new BufferedInputStream(request.getInputStream()));
		DataInputStream dataInputStream = new DataInputStream(gzipInputStream);
		int program = dataInputStream.readInt();
		try {
			
			MclinicService mhs = (MclinicService) Context.getService(MclinicService.class);
			List<MclinicXform> xformsList = mhs.getDownloadableXformsByProgram(mhs.getProgramConfiguration(program));

			docBuilder = docBuilderFactory.newDocumentBuilder();

			String xml = "<?xml version='1.0' encoding='UTF-8' ?>";
			if("mclinic".equalsIgnoreCase(request.getParameter("type"))){
				xml += "\n<xforms>";
				for (MclinicXform mclinicXform : xformsList) {
					Document doc = docBuilder.parse(IOUtils.toInputStream(mclinicXform.getXformXml()));
					NodeList nodeList = doc.getElementsByTagName("form");
					Node rootNode= nodeList.item(0);
					NamedNodeMap nodeMap=rootNode.getAttributes();
					String formId = nodeMap.getNamedItem("id").getNodeValue();
					String fileName = mclinicXform.getXformName();
					fileName=formatXmlString(fileName);
					xml += "\n  <xform>";
					xml += "\n <id>" + formId + "</id>";
					xml += "\n <name>" + fileName.replace('_', ' ').substring(0,fileName.lastIndexOf(".")) + "</name>";
					xml += "</xform>";
				}
				xml += "\n</xforms>";
			} else {
				xml += "\n<forms>";
				for (MclinicXform mclinicXform : xformsList) {
					String fileName = mclinicXform.getXformName();
					fileName=formatXmlString(fileName);
					String url = request.getRequestURL().toString();
					String fileUrl = url.substring(0, url.lastIndexOf('/') + 1);
					fileUrl += "form.form?file==";
					xml += "\n  <form ";
					xml += "url=\"" + fileUrl + fileName + "\">";
					xml += fileName.replace('_', ' ').substring(0,fileName.lastIndexOf("."));
					xml += "</form>";
				}
				xml += "\n</forms>";
			}
			response.setHeader(MclinicConstants.HTTP_LOCATION_HEADER_NAME, MclinicConstants.HTTP_LOCATION_HEADER_VALUE);			response.setContentType(XformConstants.HTTP_HEADER_CONTENT_TYPE_XML);
			response.getOutputStream().print(xml);
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}
	
	@RequestMapping(value="/module/mclinic/download/xform", method=RequestMethod.GET)
	protected void getForm(HttpServletRequest request, HttpServletResponse response) {
		MclinicService mhs = (MclinicService) Context.getService(MclinicService.class);
		String strformId = request.getParameter("formId");
		String fileName = request.getParameter("file");
		if ("mclinic".equals(request.getParameter("type"))) {
			try {
				Integer formId = Integer.parseInt(strformId);
				MclinicXform xform = mhs.getDownloadableXformByFormId(formId);
				response.setHeader(MclinicConstants.HTTP_LOCATION_HEADER_NAME, MclinicConstants.HTTP_LOCATION_HEADER_VALUE);
				response.setHeader(XformConstants.HTTP_HEADER_CONTENT_DISPOSITION, XformConstants.HTTP_HEADER_CONTENT_DISPOSITION_VALUE + xform.getXformName());
				response.setCharacterEncoding(XformConstants.DEFAULT_CHARACTER_ENCODING);
				response.getWriter().print(xform.getXformXml());
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
			}
		} else {
			//Here we hope that string fileName is not null
			try {
				MclinicXform xform = mhs.getDownloadableXformByName(fileName);
				response.setHeader(MclinicConstants.HTTP_LOCATION_HEADER_NAME, MclinicConstants.HTTP_LOCATION_HEADER_VALUE);
				response.setHeader(XformConstants.HTTP_HEADER_CONTENT_DISPOSITION, XformConstants.HTTP_HEADER_CONTENT_DISPOSITION_VALUE + xform.getXformName());
				response.setCharacterEncoding(XformConstants.DEFAULT_CHARACTER_ENCODING);
				response.getWriter().print(xform.getXformXml());
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
			}
		}
	}
	
	private String formatXmlString(String aString){
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(aString);
		char character =  iterator.current();
		while (character != CharacterIterator.DONE ){
			if (character == '<') {
				result.append("&lt;");
			}
			else if (character == '>') {
				result.append("&gt;");
			}
			else if (character == '\"') {
				result.append("&quot;");
			}
			else if (character == '\'') {
				//result.append("&#039;");
				//result.append("&apos;");
				result.append(character);
			}
			else if (character == '&') {
				result.append("&amp;");
			}
			else {
				//the char is not a special one
				//add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}
}