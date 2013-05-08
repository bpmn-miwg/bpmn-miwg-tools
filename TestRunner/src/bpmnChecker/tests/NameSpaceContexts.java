package bpmnChecker.tests;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

public class NameSpaceContexts implements NamespaceContext {
	
	public String getNamespaceURI(String prefix) {
		if ("bpmn".equals(prefix))
			return "http://www.omg.org/spec/BPMN/20100524/MODEL";
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
