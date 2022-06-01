package org.omg.bpmn.miwg.xpath.util;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public abstract class AbstractCheck {

	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	protected Document doc;


	public AbstractCheck() {
		factory = null;
		builder = null;
		doc = null;
	}

	protected void loadResource(InputStream is) throws Throwable {
		factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		builder = factory.newDocumentBuilder();
		doc = builder.parse(is);
	}


}
