package org.omg.bpmn.miwg.xpathTestRunner.tests;

import org.omg.bpmn.miwg.xpathTestRunner.testBase.AbstractXpathTest;
import org.omg.bpmn.miwg.xpathTestRunner.testBase.Direction;
import org.w3c.dom.Node;

public class A_4_0_Test extends AbstractXpathTest {

	@Override
	public String getName() {
		return "A.4.0";
	}

	@Override
    public void execute() throws Throwable {

		selectCollaboration();

		{
			selectPool("Pool");
			selectReferencedProcess();

			navigateElement("bpmn:startEvent", "Start Event 1");

			navigateFollowingElement("bpmn:task", "Task 1");
			checkMessageFlow("Message Flow 1", Direction.Output);

			navigateFollowingElement("bpmn:task", "Task 2");
			checkMessageFlow("Message Flow 2", Direction.Input);

			navigateFollowingElement("bpmn:endEvent", "End Event 1");

			pop();
			pop();
		}

		{
			navigateElementX("/bpmn:definitions/bpmn:process[bpmn:laneSet/bpmn:lane[@name='Lane 1']]");
			selectElementX("/bpmn:definitions/bpmn:process[bpmn:laneSet/bpmn:lane[@name='Lane 2']]");

			navigateElement("bpmn:startEvent", "Start Event 2");

			Node n1 = navigateFollowingElement("bpmn:task", "Task 3");
			checkMessageFlow("Message Flow 1", Direction.Input);

			{
				selectFollowingElement("bpmn:subProcess",
						"Expanded Sub-Process 1");

				pop();
			}

			navigateFollowingElement("bpmn:task", "Task 5");
			checkMessageFlow("Message Flow 2", Direction.Output);

			navigateFollowingElement("bpmn:endEvent", "End Event 2");

			navigateElement(n1);

			{
				selectFollowingElement("bpmn:subProcess",
						"Expanded Sub-Process 2");

				navigateElement("bpmn:startEvent", "Start Event 4");

				navigateFollowingElement("bpmn:task", "Task 6");

				navigateFollowingElement("bpmn:endEvent", "End Event 4");

				pop();
			}

			navigateFollowingElement("bpmn:endEvent", "End Event 5");

			pop();
		}

		pop();

	}

}
