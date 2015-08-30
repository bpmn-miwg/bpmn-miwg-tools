package org.omg.bpmn.miwg.xpath.checks;

import org.omg.bpmn.miwg.xpath.common.AbstractXpathCheck;
import org.omg.bpmn.miwg.xpath.common.Direction;
import org.w3c.dom.Node;

public class DemoTechnicalSupportCheck extends AbstractXpathCheck {

	@Override
	public String getName() {
		return "Technical Support";
	}

	@Override
	public void doExecute() throws Throwable {

		{
			Node n1, n2;

			selectCollaboration();

			{

				navigateElement("bpmn:participant", "Customer");

			}

			{
				selectProcessByParticipant("Front Office");

				navigateElement("bpmn:startEvent", "Customer issue report");
				checkMessageEvent();
				checkMessageFlow("", Direction.Input);

				navigateFollowingElement("bpmn:manualTask",
						"Get issue description from customer");
				checkMessageFlow("", Direction.Input, "bpmn:participant",
						"Customer");
				checkMessageFlow("", Direction.Output, "bpmn:participant",
						"Customer");

				n1 = navigateFollowingElement("bpmn:exclusiveGateway", "");

				navigateFollowingElement("bpmn:manualTask",
						"Provide solution to customer",
						"Able to provide solution");
				checkMessageFlow("", Direction.Input, "bpmn:participant",
						"Customer");
				checkMessageFlow("", Direction.Output, "bpmn:participant",
						"Customer");

				n2 = navigateFollowingElement("bpmn:exclusiveGateway", "");

				navigateFollowingElement("bpmn:endEvent",
						"Customer issue resolved", "Solution is effective");

				navigateBookmarkedElement(n2);

				navigateFollowingElement("bpmn:manualTask",
						"Get issue description from customer");

				navigateFollowingElement("bpmn:exclusiveGateway", "");

				navigateFollowingElement("bpmn:manualTask",
						"Inform customer the issue is going to be escalated");
				checkMessageFlow("", Direction.Output, "bpmn:participant",
						"Customer");

				navigateFollowingElement("bpmn:manualTask",
						"Request 1st level support");
				checkMessageFlow("", Direction.Output, "bpmn:startEvent",
						"1st level Issue");

				n1 = navigateFollowingElement("bpmn:intermediateCatchEvent",
						"Solution received from 1st level of support");
				checkMessageFlow("", Direction.Input, "bpmn:manualTask",
						"Provide solution to Front Office");
				checkMessageEvent();

				navigateFollowingElement("bpmn:manualTask",
						"Provide solution to customer");

				navigateBookmarkedElement(n1);

				pop();

				pop();
			}

			{
				selectProcessByParticipant("1st Level Techical Support Agent");

				navigateElement("bpmn:startEvent", "1st level Issue");
				checkMessageEvent();
				checkMessageFlow("", Direction.Input, "bpmn:manualTask",
						"Request 1st level support");

				navigateFollowingElement("bpmn:manualTask",
						"Find solution 1st level issue");

				n1 = navigateFollowingElement("bpmn:exclusiveGateway", "");

				navigateFollowingElement("bpmn:manualTask",
						"Provide solution to Front Office",
						"Able to provide 1st level solution");
				checkMessageFlow("", Direction.Output,
						"bpmn:intermediateCatchEvent",
						"Solution received from 1st level of support");

				navigateFollowingElement("bpmn:endEvent",
						"Issue handled by 1st level support");

				navigateBookmarkedElement(n1);

				navigateFollowingElement("bpmn:manualTask",
						"Request 2nd level support",
						"Unable to provide 1st level solution");

				navigateFollowingElement("bpmn:intermediateCatchEvent",
						"Solution received from 2nd level of support");
				checkMessageEvent();
				checkMessageFlow("", Direction.Input, "bpmn:manualTask",
						"Provide solution to 1st level support");

				navigateFollowingElement("bpmn:manualTask",
						"Provide solution to Front Office");

				pop();
			}

			{
				selectProcessByParticipant("2nd Level Techical Support Agent");

				navigateElement("bpmn:startEvent", "2nd level Issue");
				checkMessageEvent();
				checkMessageFlow("", Direction.Input, "bpmn:manualTask",
						"Request 2nd level support");

				navigateFollowingElement("bpmn:manualTask",
						"Find solution 2nd level issue");

				n1 = navigateFollowingElement("bpmn:exclusiveGateway", "");

				navigateFollowingElement("bpmn:manualTask",
						"Provide solution to 1st level support",
						"Able to provide 2nd level solution");
				checkMessageFlow("", Direction.Output,
						"bpmn:intermediateCatchEvent",
						"Solution received from 2nd level of support");

				navigateFollowingElement("bpmn:endEvent",
						"Issue handled by 2 nd level support");

				navigateBookmarkedElement(n1);

				navigateFollowingElement("bpmn:manualTask",
						"Request supplier support",
						"Unable to provide 2nd level solution");

				navigateFollowingElement("bpmn:intermediateCatchEvent",
						"Solution received from supplier");
				checkMessageEvent();
				checkMessageFlow("", Direction.Input, "bpmn:manualTask",
						"Provide solution to 2nd level support");

				navigateFollowingElement("bpmn:manualTask",
						"Provide solution to 1st level support");

				pop();
			}

			{
				selectProcessByParticipant("Supplier");

				navigateElement("bpmn:startEvent", "Supplier Issue");
				checkMessageEvent();
				checkMessageFlow("", Direction.Input);

				navigateFollowingElement("bpmn:manualTask",
						"Find solution supplier issue");

				navigateFollowingElement("bpmn:manualTask",
						"Provide solution to 2nd level support");
				checkMessageFlow("", Direction.Output,
						"bpmn:intermediateCatchEvent",
						"Solution received from supplier");

				navigateFollowingElement("bpmn:endEvent",
						"Issue handled by supplier");

				pop();
			}

			pop();

		}
	}
}
