package org.omg.bpmn.miwg.xpath.common;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

public class NameSpaceContexts implements NamespaceContext {

	public String getNamespaceURI(String prefix) {
		if ("bpmn".equals(prefix))
			return "http://www.omg.org/spec/BPMN/20100524/MODEL";
		if ("bpmndi".equals(prefix))
			return "http://www.omg.org/spec/BPMN/20100524/DI";
		if ("camunda".equals(prefix))
			return "http://activiti.org/bpmn";
		if ("omgdc".equals(prefix))
			return "http://www.omg.org/spec/DD/20100524/DC";
		if ("omgdi".equals(prefix))
			return "http://www.omg.org/spec/DD/20100524/DI";
		if ("signavio".equals(prefix))
			return "http://www.signavio.com";
		if ("w4style".equals(prefix))
			return "http://www.w4.eu/spec/DataComposer/20120927/Diagram/Style";
		if ("w4model".equals(prefix))
			return "http://www.w4.eu/spec/BPMN/20110701/MODEL";
		if ("w4graph".equals(prefix))
			return "http://www.w4.eu/spec/BPMN/20110930/GRAPH";

		throw new IllegalArgumentException(prefix);
	}

	@Override
	public String getPrefix(String namespaceURI) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<String> getPrefixes(String namespaceURI) {
		throw new UnsupportedOperationException();
	}

}
