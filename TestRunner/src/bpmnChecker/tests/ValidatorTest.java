package bpmnChecker.tests;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import bpmnChecker.testBase.AbstractTest;
import bpmnChecker.tests.validation.ValidationErrorHandler;

public class ValidatorTest extends AbstractTest {

	class LocalResourcsResolver implements LSResourceResolver {
		@Override
		public LSInput resolveResource(String type, String namespaceURI,
				String publicId, String systemId, String baseURI) {
			DOMImplementationRegistry registry;
			try {
				registry = DOMImplementationRegistry.newInstance();
			} catch (Exception e) {
				return null;
			}
			DOMImplementationLS domImplementationLS = (DOMImplementationLS) registry
					.getDOMImplementation("LS");
			LSInput ret = domImplementationLS.createLSInput();

			FileInputStream fis = null;
			try {
				fis = new FileInputStream("res/schema/" + systemId);
			} catch (Exception e) {
				System.err.println(e.toString());
			}

			ret.setSystemId(systemId);
			ret.setByteStream(fis);
			return ret;
		}
	}

	@Override
	public boolean isApplicable(String fileName) {
		return fileName.endsWith("-export.bpmn")
				|| fileName.endsWith("-roundtrip.bpmn");
	}

	@Override
	public String getName() {
		return "BPMN Schema Validator";
	}

	@Override
	public void execute(String fileName) throws Throwable {
		ValidationErrorHandler eHandler = new ValidationErrorHandler();
		eHandler.setTestOutput(out);

		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		schemaFactory.setResourceResolver(new LocalResourcsResolver());

		Schema schema = schemaFactory.newSchema(new StreamSource(new File(
				"res/schema/BPMN20.xsd")));

		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		// parserFactory.setValidating(true);
		parserFactory.setNamespaceAware(true);
		parserFactory.setSchema(schema);

		SAXParser parser = parserFactory.newSAXParser();
		XMLReader reader = parser.getXMLReader();
		reader.setErrorHandler(eHandler);

		try {
			parser.parse(fileName, (DefaultHandler) null);
		} catch (Exception e) {
			issue("Validation failed", "Exception: " + e.getMessage());
			e.printStackTrace(System.out);
		}

		if (eHandler.valid())
			ok("Validation succeeded");
		else
			addIssues(eHandler.numError);
			addIssues(-1); // Make sure the following issue won't be counted.
			issue("Validation failed", "Warnings " + eHandler.numWarning
				+ ", Errors: " + eHandler.numError + ", Fatal Errors: "
				+ eHandler.numFatalError);
	}

}
