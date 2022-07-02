package org.omg.bpmn.miwg.api.output.dom.ok;

import org.omg.bpmn.miwg.api.output.dom.AbstractCheckEntry;
import org.omg.bpmn.miwg.api.output.html.OutputType;

public abstract class AbstractOKEntry extends AbstractCheckEntry {

	public AbstractOKEntry() {
		super();
	}

	@Override
	public final OutputType getOutputType() {
		return OutputType.ok;
	}

}
