package org.omg.bpmn.miwg.xpath.checks;

import org.omg.bpmn.miwg.xpath.common.AbstractXpathCheck;

public class C_1_0_Check extends AbstractXpathCheck {

	@Override
	public String getName() {
		return "C.1.0";
	}

	@Override
    public void doExecute() throws Throwable {

		{

			selectElementX("//bpmn:collaboration");

			navigateElementX("bpmn:participant[@name='Team-Assistant']");
			navigateElementX("bpmn:participant[@name='Process Engine - Invoice Receipt']");

			pop();
		}
		
	}
}
