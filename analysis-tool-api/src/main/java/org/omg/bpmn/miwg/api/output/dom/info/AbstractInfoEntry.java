package org.omg.bpmn.miwg.api.output.dom.info;

import org.omg.bpmn.miwg.api.output.dom.AbstractCheckEntry;
import org.omg.bpmn.miwg.api.output.html.OutputType;

public abstract class AbstractInfoEntry extends AbstractCheckEntry {

	public AbstractInfoEntry() {
		super();
	}

	@Override
	public final OutputType getOutputType() {
		return OutputType.info;
	}

}
