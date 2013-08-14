package org.omg.bpmn.miwg.xpathTestRunner.tests;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.omg.bpmn.miwg.xpathTestRunner.base.TestInstance;
import org.omg.bpmn.miwg.xpathTestRunner.testBase.AbstractTest;
import org.omg.bpmn.miwg.xpathTestRunner.tests.validation.ValidationErrorHandler;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

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
				fis = new FileInputStream("src/main/resources/schema/"
						+ systemId);
			} catch (Exception e) {
				System.err.println(e.toString());
			}

			ret.setSystemId(systemId);
			ret.setByteStream(fis);
			return ret;
		}
	}

	@Override
	public boolean isApplicable(File file) {
		String dir = file.getParentFile().getName();
		String fileName = file.getName();
		return fileName.endsWith("-roundtrip.bpmn")
				|| (dir.equals("Reference") && fileName.endsWith(".bpmn"));
	}

	@Override
	public String getName() {
		return "BPMN Schema Validator";
	}

	@Override
	public void execute(TestInstance instance) throws Throwable {
		ValidationErrorHandler eHandler = new ValidationErrorHandler();
		eHandler.setTestOutput(out);

		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		schemaFactory.setResourceResolver(new LocalResourcsResolver());

		Schema schema = schemaFactory.newSchema(new StreamSource(new File(
				"src/main/resources/schema/BPMN20.xsd")));

		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		// parserFactory.setValidating(true);
		parserFactory.setNamespaceAware(true);
		parserFactory.setSchema(schema);

		SAXParser parser = parserFactory.newSAXParser();
		XMLReader reader = parser.getXMLReader();
		reader.setErrorHandler(eHandler);

		try {
			parser.parse(instance.getFile(), (DefaultHandler) null);
		} catch (Exception e) {
			finding("Validation failed", "Exception: " + e.getMessage());
			e.printStackTrace(System.out);
		}

		if (eHandler.valid()) {
			ok("Validation", "Validation succeeded");
		} else {
			addIssues(eHandler.numError - 1); // Make sure the following issue
												// won't be counted.
			finding("Validation failed", "Warnings " + eHandler.numWarning
					+ ", Errors: " + eHandler.numError + ", Fatal Errors: "
					+ eHandler.numFatalError);
		}
		
		instance.addFindings(resultsFinding());
		instance.addOK(resultsOK());
	}

}
