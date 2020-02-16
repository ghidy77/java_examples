package utils.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLExample {

	private static final String LOCALPATH = "src/main/resources/";
	private static String filepath = LOCALPATH + "example.xml";
	private static String schemapath = LOCALPATH + "schema.xsd";

	public static void main(String[] args) throws ParserConfigurationException {
		// Parse XML file using javax.xml.parsers.DocumentBuilder
		DocumentBuilder dBuilder;
		Document doc = null;
		dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		try {
			doc = dBuilder.parse(new File(filepath));
		} catch (SAXException | IOException e) {
			System.out.println("Invalid File / Xml");
			System.out.println(e.getMessage());
		}

		System.out.println("Root element is: <" + doc.getDocumentElement().getNodeName() + ">");

		NodeList childNodes = doc.getDocumentElement().getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String name = node.getNodeName();
				String value = node.getTextContent();
				System.out.println("Node name: '" + name + "' has text content: " + value);
			}
		}

		// Validate XML against schema.xsd
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		// Validate will throw exception only if xml is not valid against the schema
		try {
			Schema schema = factory.newSchema(new File(schemapath));
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(new File(filepath)));
			System.out.println("XML is valid. No exception was thrown");
		} catch (SAXException | IOException e) {
			System.out.println("Invalid XML. Exception found: " + e.getMessage());
		}
	}
}
