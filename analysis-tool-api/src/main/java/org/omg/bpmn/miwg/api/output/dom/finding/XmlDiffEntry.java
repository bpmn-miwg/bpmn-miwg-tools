package org.omg.bpmn.miwg.api.output.dom.finding;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.NodeDetail;
import org.omg.bpmn.miwg.api.output.html.Detail;
import org.omg.bpmn.miwg.api.output.html.DetailedOutput;
import org.omg.bpmn.miwg.api.output.html.Output;
import org.omg.bpmn.miwg.api.output.html.OutputType;

public class XmlDiffEntry extends AbstractFindingEntry {

	private Difference difference;

	public XmlDiffEntry(Difference difference) {
		this.difference = difference;
	}

	public NodeDetail getReferenceDetail() {
		return difference.getControlNodeDetail();
	}

	public NodeDetail getTestDetail() {
		return difference.getTestNodeDetail();
	}

	@Override
	public String toLine() {
		NodeDetail referenceDetail = difference.getControlNodeDetail();
		NodeDetail testDetail = difference.getTestNodeDetail();

		return "Reference: " + referenceDetail.getXpathLocation() + " = "
				+ referenceDetail.getValue() + "\n" + "Test: "
				+ testDetail.getXpathLocation() + " = " + testDetail.getValue();
	}


	@Override
	public Output getHtmlOutput() {
		Output output = new Output(OutputType.finding, describeDifference(difference));
		output.setDescription(String.format(
				"Difference found in %1$s (id:%2$s)", difference.getDescription(),
				difference.getId()));

		return output;
	}

	private static DetailedOutput describeDifference(Difference difference) {
		DetailedOutput dOut = new DetailedOutput();

		// Reference xpath/value
		dOut.addDetail(printDifferenceDetail(difference.getControlNodeDetail(),
				"reference"));

		// Vendor xpath/value
		dOut.addDetail(printDifferenceDetail(difference.getTestNodeDetail(),
				"vendor"));

		return dOut;
	}

	private static Detail printDifferenceDetail(NodeDetail detail, String type) {
		Detail d = new Detail();
		d.setMessage(detail.getXpathLocation() + " :\t" + detail.getValue());
		d.setType(type);
		d.setXpath(detail.getXpathLocation());
		return d;
	}

}
