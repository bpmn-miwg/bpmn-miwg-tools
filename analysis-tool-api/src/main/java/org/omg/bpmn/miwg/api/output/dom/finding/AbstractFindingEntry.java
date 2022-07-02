package org.omg.bpmn.miwg.api.output.dom.finding;

import org.omg.bpmn.miwg.api.output.dom.AbstractCheckEntry;
import org.omg.bpmn.miwg.api.output.html.OutputType;

public abstract class AbstractFindingEntry extends AbstractCheckEntry {

	public AbstractFindingEntry() {
		super();
	}

	@Override
	public final OutputType getOutputType() {
		return OutputType.finding;
	}

}
