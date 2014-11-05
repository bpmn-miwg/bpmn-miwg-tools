package org.omg.bpm.miwg.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class DOMFactory {

	public static Document getDocument(InputStream stream)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(stream);
		return document;
	}

	public static Document getDocument(String fileName)
			throws FileNotFoundException, ParserConfigurationException,
			SAXException, IOException {
		return getDocument(new FileInputStream(fileName));
	}
	
	public static Document getDocument(File file)
			throws FileNotFoundException, ParserConfigurationException,
			SAXException, IOException {
		return getDocument(new FileInputStream(file));
	}
}
