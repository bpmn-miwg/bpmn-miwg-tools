package org.omg.bpmn.miwg.xpath.checks;

import org.omg.bpmn.miwg.xpath.common.AbstractXpathCheck;
import org.omg.bpmn.miwg.xpath.common.Direction;
import org.w3c.dom.Node;

public class C_2_0_Check extends AbstractXpathCheck {

	@Override
	public String getName() {
		return "C.2.0";
	}

	@Override
	public void doExecute() throws Throwable {

		Node n, n1;

		selectCollaboration();
		{

			selectPool("Credit Card Company");
			{

				selectReferencedProcess();
				{
					checkAttributeValue("isExecutable", "false");

					navigateElement("bpmn:startEvent",
							"Receive Credit Card Information");
					checkMessageEvent();
					checkMessageFlow("Send Credit Card Information",
							Direction.Input, "bpmn:task", "Pay Order");

					navigateFollowingElement("bpmn:task", "Take Payment");

					navigateFollowingElement("bpmn:endEvent", "Send Result");
					checkMessageEvent();
					checkMessageFlow("", Direction.Output, "bpmn:task",
							"Pay Order");

				}
				pop();

			}
			pop();

			selectPool("Customer");
			{

				selectReferencedProcess();
				{
					checkAttributeValue("isExecutable", "false");

					navigateElement("bpmn:startEvent", null);

					navigateFollowingElement("bpmn:task",
							"Browse Products on Amazon");

					navigateFollowingElement("bpmn:task", "Add Item to Cart");

					n = navigateFollowingElement("bpmn:exclusiveGateway",
							"Done Shopping?");

					navigateFollowingElement("bpmn:task",
							"Browse Products on Amazon", "No");

					navigateBookmarkedElement(n);

					n1 = selectFollowingElement("bpmn:subProcess", "Checkout");
					{
						navigateElement("bpmn:startEvent", null);

						navigateFollowingElement("bpmn:task", "Pay Order");
						checkMessageFlow("Send Credit Card Information",
								Direction.Output, "bpmn:startEvent",
								"Receive Credit Card Information");
						checkMessageFlow(null, Direction.Input,
								"bpmn:endEvent", "Send Result");

						n = navigateFollowingElement("bpmn:exclusiveGateway",
								"Payment accepted?");

						navigateFollowingElement("bpmn:intermediateThrowEvent",
								"Send Order", "Yes");
						checkMessageFlow(null, Direction.Output,
								"bpmn:startEvent", "Receive Order");

						navigateFollowingElement("bpmn:endEvent", null);

						navigateBookmarkedElement(n);

						n = navigateFollowingElement("bpmn:exclusiveGateway",
								"Retry?", "No");

						navigateFollowingElement("bpmn:task", "Pay Order",
								"Yes");

						navigateBookmarkedElement(n);

						navigateFollowingElement("bpmn:endEvent", null, "No");
						checkErrorEvent();

					}
					pop();

					navigateBoundaryEvent(null);
					checkErrorEvent();

					navigateFollowingElement("bpmn:endEvent", null);

					navigateBookmarkedElement(n1);

					navigateFollowingElement("bpmn:task", "Receive items");

					navigateFollowingElement("bpmn:endEvent", null);
				}
				pop();

			}
			pop();

			selectPool("Amazon");
			{

				selectReferencedProcess();
				{

					navigateElement("bpmn:startEvent", "Receive Order");
					checkMessageEvent();
					checkMessageFlow(null, Direction.Input, "bpmn:intermediateThrowEvent", "Send Order");
					
					navigateFollowingElement("bpmn:task", "Pick items");
					
					navigateFollowingElement("bpmn:task", "Place in bin");
					
					navigateFollowingElement("bpmn:task", "Receive and Package items");
					
					navigateFollowingElement("bpmn:task", "Send to carrier dock");
					checkMessageFlow(null, Direction.Output, "bpmn:startEvent", "Pick items");
					
					navigateFollowingElement("bpmn:endEvent", null);
					
				}
				pop();

			}
			pop();
			
			selectPool("Carrier"); {
				
				selectReferencedProcess();
				{
					
					navigateElement("bpmn:startEvent", "Pick items");
					
					navigateFollowingElement("bpmn:task", "Load Truck");
					
					navigateFollowingElement("bpmn:task", "Deliver Items");
					checkMessageFlow(null, Direction.Output, "bpmn:task", "Receive items");
					
					navigateFollowingElement("bpmn:endEvent", null);
					
				}
				pop();
				
			}
			pop();

		}
		pop();
	}

}
