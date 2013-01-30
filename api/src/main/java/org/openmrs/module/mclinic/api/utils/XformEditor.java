package org.openmrs.module.mclinic.api.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.mclinic.api.XformEditModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A Util class with methods for editing XML files, Includes a method to split a household xml file into
 * several files one for every patient specified in the household form
 * 
 * @author Samuel Mbugua
 */
public class XformEditor {
	
	private static Log log = LogFactory.getLog(XformEditor.class);	
	private static Document document, newXMLDocument;
	private static Element childElement;
	private final static String FORMROOTELEMENT="form";
	private static List<XformEditModel> lstModels;
	
	/**
	 * Creates a key/value pair map from an xml file
	 * @param filePath
	 */
	public static List<XformEditModel> createMapFromFile(String filePath) {
		try {
			File file = new File(filePath);

			// Create instance of DocumentBuilderFactory
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			
			// Using existing XML Document
			Document doc = docBuilder.parse(file);
			
			NodeList formNode = doc.getElementsByTagName(FORMROOTELEMENT);
			Node root = formNode.item(0);
			lstModels = new ArrayList<XformEditModel>();
			readDocument(root);
		} catch (SAXException e) {e.printStackTrace();
		} catch (IOException e) {e.printStackTrace();
		} catch (ParserConfigurationException e) {e.printStackTrace();
		}catch (Exception e) {e.printStackTrace();}
		return lstModels;
	}
	/**
	 * Sets the value of the node specified by <b> nodePath </b> to  <b>nodeValue</b>
	 * @param filePath Absolute path for the file to edit
	 * @param nodePath Qualified name for the node to edit
	 * @param nodeValue Value to set node to
	 * @return <b> true </b> if successful, otherwise <b> false </b>
	 */
	public static boolean editNode(String filePath, String nodePath, String nodeValue) {
		try {
			File file = new File(filePath);
			String nodeParent=nodePath.substring(0,nodePath.lastIndexOf(File.separatorChar));
			String nodeName=nodePath.substring(nodePath.lastIndexOf(File.separatorChar) + 1);

			// Create instance of DocumentBuilderFactory
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = factory.newDocumentBuilder();

			// Using existing XML Document
			Document doc = docBuilder.parse(file);
			XPathFactory xpf=XPathFactory.newInstance();
			XPath xp=xpf.newXPath();

			Node editNode=(Node) xp.evaluate(nodePath, doc, XPathConstants.NODE);
			if (editNode==null) {
				//try to get parent
				Node parentNode=(Node) xp.evaluate(nodeParent, doc, XPathConstants.NODE);
				if (parentNode != null) {
					editNode=doc.createElementNS("", nodeName);
					parentNode.appendChild(editNode);
					editNode.setTextContent(nodeValue);
				}
			}else
				editNode.setTextContent(nodeValue);

			if (saveXMLDocument(doc, filePath))
				return true;

		} catch (SAXException e) {e.printStackTrace();
		} catch (IOException e) {e.printStackTrace();
		} catch (ParserConfigurationException e) {e.printStackTrace();
		} catch (XPathExpressionException e) {e.printStackTrace();
		}catch (Exception e) {e.printStackTrace();}

		return false;
	}
	
	public static boolean editNodeList(String filePath, String nodeListPath, String nodePath, String nodeValue) {
		try {
			File file = new File(filePath);
			
			// Create instance of DocumentBuilderFactory
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
	
			// Using existing XML Document
			Document doc = docBuilder.parse(file);
	
			XPathFactory xpf=XPathFactory.newInstance();
			XPath xp=xpf.newXPath();
			Node listNode = (Node) xp.evaluate(nodeListPath, doc, XPathConstants.NODE);
			NodeList nodeList=listNode.getChildNodes();
			for (int s = 0; s < nodeList.getLength(); s++) {
				Node individualNode = nodeList.item(s);
				Node editNode=(Node) xp.evaluate(nodePath,individualNode,XPathConstants.NODE);
				if (editNode!=null)
					editNode.setTextContent(nodeValue);
			}
			if (saveXMLDocument(doc, filePath))
				return true;
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 *  Initiates the splitter to create individual files
	 */
	public static void createIndividualFiles(Document doc) {
		document = doc;
		try {
			splitDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns element value
	 * @param node element (it is XML tag)
	 * @return Element value otherwise empty String
	 */
	private final static String getElementValue(Node node) {
		Node child;
		if (node != null) {
			if (node.hasChildNodes()) {
				for (child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
					if (child.getNodeType() == Node.TEXT_NODE) {
						return child.getNodeValue();
					}
				}
			}
		}
		return "";
	}
	
	public static void readDocument(Node node) {
		XformEditModel xformEditModel = new XformEditModel();
		xformEditModel.setNodeName(node.getNodeName());
        // get attributes of element
        if (node.hasChildNodes() && node.getFirstChild().getNodeName()!="#text"){
        	NamedNodeMap attributes = node.getAttributes();
        	String strAttributes = "";
        	for (int i = 0; i < attributes.getLength(); i++) {
        		Node attribute = attributes.item(i);
        		strAttributes = strAttributes + attribute.getNodeName() + "=" + attribute.getNodeValue() + " ";
        	}
        	xformEditModel.setNodeAttributes(strAttributes);
        }else 
        	xformEditModel.setNodeValue(getElementValue(node));
        lstModels.add(xformEditModel);
        // read all child nodes recursively
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                readDocument(child);
            }
        }
    }

	/**
	 * Writes node and all child nodes into an xml Document
	 * @param node XML node from from XML tree
	 * @param element XML Element to write node to
	 */
	private static void writeDocument(Node node, Element element) {
		// get element name
		String nodeName = node.getNodeName();
		
		// get element value
		String nodeValue = getElementValue(node);

		//Add this node to the xml document
		childElement = newXMLDocument.createElement(nodeName);
		
		// get attributes of element and add to node
		NamedNodeMap attributes = node.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			childElement.setAttribute(attribute.getNodeName(), attribute.getNodeValue());
		}
		
		if (node.hasChildNodes()) {
			element.appendChild(childElement);
			childElement.appendChild(newXMLDocument.createTextNode(nodeValue));
			element=childElement;
		}else {
			childElement.appendChild(newXMLDocument.createTextNode(nodeValue));
			element.appendChild(childElement);
		}
		
		// write all child nodes recursively
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				writeDocument(child,element);
			}
		}
	}
	
	/**
	 * Splits a document to create several sub documents
	 * @throws ParserConfigurationException
	 */
	private static void splitDocument() throws ParserConfigurationException {
		NodeList nodeList = document.getElementsByTagName("individuals");
		Node individualsList = nodeList.item(0);
				
		NodeList individuals = individualsList.getChildNodes();
		for (int s = 0; s < individuals.getLength(); s++) {
			Node individualNode = individuals.item(s);
			
			//Create a new document for this individual
			createNewXmlDoc();
			
			//add root element
			Element root = createRootElement();
			
			//add header information 
			addHeaderNode(root);
			
			//write the nodes
			NodeList individualProperties = individualNode.getChildNodes();
			for (int i = 0; i < individualProperties.getLength(); i++) {
				writeDocument(individualProperties.item(i),root);
			}
			
			//save the individual document
			String targetFileName=MclinicUtil.getMclinicQueueDir().getAbsolutePath() +
			"/" + generateFileName(new Date()) + ".xml";
			saveXMLDocument(newXMLDocument,targetFileName);
		}
	}
	
	/**
	 * Creates a root element for the new xml document
	 * @throws ParserConfigurationException
	 */
	private static Element createRootElement() throws ParserConfigurationException {
		NodeList nodeList = document.getElementsByTagName(FORMROOTELEMENT);
		Node formNode= nodeList.item(0);
		NamedNodeMap attributes = formNode.getAttributes();
		
		Element rootElement=newXMLDocument.createElement(formNode.getNodeName()); 
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			rootElement.setAttribute(attribute.getNodeName(), attribute.getNodeValue());
		}
		newXMLDocument.appendChild(rootElement);
		return rootElement;
	}
	
	/** Adds a header node list to a patient form 
	 * @param root
	 * @throws ParserConfigurationException
	 */
	private static void addHeaderNode(Element root) throws ParserConfigurationException {
		NodeList nodeList = document.getElementsByTagName("header");
		Node headerNode= nodeList.item(0);
				
		Element headerElement = newXMLDocument.createElement(headerNode.getNodeName());
		root.appendChild(headerElement);
		NodeList headerNodeList = headerNode.getChildNodes();
		for (int i = 0; i < headerNodeList.getLength(); i++) {
			writeDocument(headerNodeList.item(i),headerElement);
		}
	}

	
	/**
	 * Saves XML Document into XML file.
	 * @param doc XML document to save
	 * @param filePath absolute path for the xml file to save
	 * @return <B>true</B> if method success <B>false</B> otherwise
	 */
	public static boolean saveXMLDocument(Document doc, String filePath) {
		File xmlOutputFile = new File(filePath);
		FileOutputStream fos;
		Transformer transformer;
		try {
			fos = new FileOutputStream(xmlOutputFile);
			// Use a Transformer for output
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(fos);
			// transform source into result will do save
			transformer.transform(source, result);
		} catch (FileNotFoundException e) {
			log.error("Error occurred: " + e.getMessage());
			return false;
		} catch (TransformerConfigurationException e) {
			 log.error("Transformer configuration error: " + e.getMessage());
			return false;
		} catch (TransformerException e) {
			log.error("Error transform: " + e.getMessage());
		}
		return true;
	}

	/**
	 * Instantiates a new instance of an xml document
	 * @throws ParserConfigurationException
	 */
	private static void createNewXmlDoc() throws ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		newXMLDocument = documentBuilder.newDocument();
	}
	
	
	/**
	 * Generate a random alphanumeric string 
	 * @return a random string
	 */
	private static String generateFileName(Date date) {
		// format to print date in filename
		DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd-HHmm-ssSSS");
		
		// use current date if none provided
		if (date == null)
			date = new Date();
		
		StringBuilder filename = new StringBuilder();
		
		// the start of the filename is the time so we can do some sorting
		filename.append(dateFormat.format(date));
		
		// the end of the filename is a random number between 0 and 10000
		filename.append((int) (Math.random() * 10000));
		
		return filename.toString();
	}
}
