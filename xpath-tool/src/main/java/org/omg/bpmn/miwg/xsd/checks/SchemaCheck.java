package org.omg.bpmn.miwg.xsd.checks;

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.omg.bpmn.miwg.api.AnalysisResult;
import org.omg.bpmn.miwg.api.tools.AnalysisTool;
import org.omg.bpmn.miwg.common.AbstractCheck;
import org.omg.bpmn.miwg.common.StreamCheck;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class SchemaCheck extends AbstractCheck implements StreamCheck {

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

			InputStream is = null;
			try {
				is = getRessourceAsStreamWrapper("schema/" + systemId);
			} catch (Exception e) {
				System.err.println(e.toString());
			}

			ret.setSystemId(systemId);
			ret.setByteStream(is);
			return ret;
		}
	}

	@Override
	public String getName() {
		return "BPMN Schema Validator";
	}

	private InputStream getRessourceAsStreamWrapper(String name) {
		InputStream is = getClass().getResourceAsStream(name);
		if (is == null)
			is = getClass().getResourceAsStream("/" + name);
		return is;
	}

	public AnalysisResult execute(InputStream is, AnalysisTool analysisTool) throws Throwable {

		ValidationErrorHandler eHandler = new ValidationErrorHandler();
		eHandler.setTestOutput(out);

		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		schemaFactory.setResourceResolver(new LocalResourcsResolver());

		Schema schema = schemaFactory.newSchema(new StreamSource(
				getRessourceAsStreamWrapper("schema/BPMN20.xsd")));

		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		// parserFactory.setValidating(true);
		parserFactory.setNamespaceAware(true);
		parserFactory.setSchema(schema);

		SAXParser parser = parserFactory.newSAXParser();
		XMLReader reader = parser.getXMLReader();
		reader.setErrorHandler(eHandler);

		try {
			parser.parse(new InputSource(is), (DefaultHandler) null);
		} catch (Exception e) {
			finding("Schema validation failed", "Exception: " + e.getMessage());
			e.printStackTrace(System.out);
		}

		if (eHandler.valid()) {
			ok("Validation", "Schema validation succeeded");
		} else {
			addIssues(eHandler.numError - 1); // Make sure the following issue
												// won't be counted.
			finding("Validation failed", "Warnings " + eHandler.numWarning
					+ ", Errors: " + eHandler.numError + ", Fatal Errors: "
					+ eHandler.numFatalError);
		}

		return new AnalysisResult(resultsOK(), resultsFinding(),
				out.getMiwgOutput(), analysisTool);
	}

}
