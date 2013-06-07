package org.omg.bpmn.miwg.xpathTestRunner.tests;

import java.io.File;

import org.omg.bpmn.miwg.xpathTestRunner.testBase.AbstractXpathTest;

public class A_1_0_Test extends AbstractXpathTest {

	@Override
	public String getName() {
		return "A.1.0";
	}

	@Override
	public boolean isApplicable(File file) {
		String fn = file.getName();
		return // fn.equals("B.1.0-export.bpmn")
		fn.equals("A.1.0-roundtrip.bpmn") || fn.equals("A.1.0.bpmn");
	}

	@Override
	public void execute(File file) throws Throwable {

		{
			loadFile(file);

			selectElement("/bpmn:definitions/bpmn:process");

			pop();

		}
	}

}
