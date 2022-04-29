/**
 * The MIT License (MIT)
 * Copyright (c) 2013 OMG BPMN Model Interchange Working Group
 *
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package org.omg.bpmn.miwg.schema;

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisOutput;
import org.omg.bpmn.miwg.api.tools.StreamAnalysisTool;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class SchemaAnalysisTool implements StreamAnalysisTool {

	public final static String NAME = "xsd";

	public String getName() {
		return NAME;
	}

	@Override
	public AnalysisOutput analyzeStream(AnalysisJob job,
			InputStream referenceBpmnXml, InputStream actualBpmnXml)
			throws Exception {
		AnalysisOutput out = new AnalysisOutput(job, this);

		try {

			ValidationErrorHandler eHandler = new ValidationErrorHandler();
			eHandler.setTestOutput(out);

			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schemaFactory.setResourceResolver(new LocalResourcsResolver());

			Schema schema = schemaFactory.newSchema(new StreamSource(
					getRessourceAsStreamWrapper("schema/BPMN20.xsd")));

			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setValidating(true);
			parserFactory.setNamespaceAware(true);
			parserFactory.setSchema(schema);

			SAXParser parser = parserFactory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setErrorHandler(eHandler);

			try {
				parser.parse(new InputSource(actualBpmnXml),
						(DefaultHandler) null);
			} catch (Exception e) {
				out.finding("Schema validation failed",
						"Exception: " + e.getMessage());
				e.printStackTrace(System.out);
			}

			if (eHandler.valid()) {
				out.ok("Validation", "Schema validation succeeded");
			} else {
				out.info("Validation failed: Warnings: " + eHandler.numWarning
						+ ", Errors: " + eHandler.numError + ", Fatal Errors: "
						+ eHandler.numFatalError);
			}

			return out;
		} finally {
			out.close();
		}

	}

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

	private InputStream getRessourceAsStreamWrapper(String name) {
		InputStream is = getClass().getResourceAsStream(name);
		if (is == null)
			is = getClass().getResourceAsStream("/" + name);
		return is;
	}
	/*
	 * public AnalysisOutput execute(InputStream is, AnalysisTool analysisTool)
	 * throws Throwable { AnalysisOutput out = new AnalysisOutput
	 * 
	 * ValidationErrorHandler eHandler = new ValidationErrorHandler();
	 * eHandler.setTestOutput(out);
	 * 
	 * SchemaFactory schemaFactory = SchemaFactory
	 * .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	 * schemaFactory.setResourceResolver(new LocalResourcsResolver());
	 * 
	 * Schema schema = schemaFactory.newSchema(new StreamSource(
	 * getRessourceAsStreamWrapper("schema/BPMN20.xsd")));
	 * 
	 * SAXParserFactory parserFactory = SAXParserFactory.newInstance(); //
	 * parserFactory.setValidating(true); parserFactory.setNamespaceAware(true);
	 * parserFactory.setSchema(schema);
	 * 
	 * SAXParser parser = parserFactory.newSAXParser(); XMLReader reader =
	 * parser.getXMLReader(); reader.setErrorHandler(eHandler);
	 * 
	 * try { parser.parse(new InputSource(is), (DefaultHandler) null); } catch
	 * (Exception e) { out.finding("Schema validation failed", "Exception: " +
	 * e.getMessage()); e.printStackTrace(System.out); }
	 * 
	 * if (eHandler.valid()) { out.ok("Validation",
	 * "Schema validation succeeded"); } else { out.addIssues(eHandler.numError
	 * - 1); // Make sure the following issue // won't be counted.
	 * out.finding("Validation failed", "Warnings " + eHandler.numWarning +
	 * ", Errors: " + eHandler.numError + ", Fatal Errors: " +
	 * eHandler.numFatalError); }
	 * 
	 * return new AnalysisResult(out, analysisTool); }
	 */

}
