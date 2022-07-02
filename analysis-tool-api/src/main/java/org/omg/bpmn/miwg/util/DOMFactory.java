package org.omg.bpmn.miwg.util;

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

    public static Document getDocument(String resourceName)
            throws FileNotFoundException, ParserConfigurationException,
            SAXException, IOException {
        return getDocument(DOMFactory.class.getResourceAsStream(resourceName));
    }

    public static Document getDocument(File file) throws FileNotFoundException,
            ParserConfigurationException, SAXException, IOException {
        System.err
                .println("WARNING! This method MAY NOT be portable as it relies on file system layout");
        return getDocument(new FileInputStream(file));
    }
}
