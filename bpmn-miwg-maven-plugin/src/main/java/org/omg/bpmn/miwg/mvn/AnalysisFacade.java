package org.omg.bpmn.miwg.mvn;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisResult;
import org.omg.bpmn.miwg.api.AnalysisRun;
import org.omg.bpmn.miwg.util.DOMFactory;
import org.omg.bpmn.miwg.util.HTMLAnalysisOutputWriter;
import org.omg.bpmn.miwg.xmlCompare.XmlCompareAnalysisTool;
import org.omg.bpmn.miwg.xpath.XPathAnalysisTool;
import org.omg.bpmn.miwg.xsd.XsdAnalysisTool;
import org.w3c.dom.Document;

public class AnalysisFacade {

	private Logger LOGGER = Logger.getLogger(AnalysisFacade.class);

	private File outputFolder;

	public AnalysisFacade(File outputFolder) {
		this.outputFolder = outputFolder;
	}

	public Collection<AnalysisRun> executeAnalysisJobs(
			Collection<AnalysisJob> jobs) throws Exception {
		Collection<AnalysisRun> runs = new LinkedList<AnalysisRun>();

		for (AnalysisJob job : jobs) {
			try {
				AnalysisRun run = executeAnalysisJob(job);
				runs.add(run);
			} catch (Throwable t) {
				LOGGER.error(String.format("ERROR: %1$s, %2$s",
						job.getFullApplicationName(), job.getMIWGTestCase()));
				LOGGER.error(String.format("     : %1$s, %2$s", t.getClass()
						.getName(), t.getMessage()));
			}
		}

		HTMLAnalysisOutputWriter.writeOverview(outputFolder, runs);

		return runs;
	}

	public AnalysisRun executeAnalysisJob(AnalysisJob job) throws Exception {
		XPathAnalysisTool xpathAnalysisTool = new XPathAnalysisTool();
		XsdAnalysisTool xsdAnalysisTool = new XsdAnalysisTool();
		XmlCompareAnalysisTool compareAnalysisTool = new XmlCompareAnalysisTool();

		InputStream referenceInputStream = null;
		InputStream actualInputStream = null;

		try {
			Document actualDom;
			Document referenceDom;

			// Build the DOMs for the DOMAnalysisTools
			{
				actualInputStream = job.getActualInput().getInputStream();
				//assert actualInputStream != null;

				actualDom = DOMFactory.getDocument(actualInputStream);

				//assert actualDom != null;
				if (actualInputStream != null)
					actualInputStream.close();
			}

			boolean hasReference;
			{
				hasReference = job.hasReference();
				if (hasReference) {
					referenceInputStream = job.getReferenceInput()
							.getInputStream();
					referenceDom = DOMFactory.getDocument(referenceInputStream);
					// assert referenceDom != null;
					if (referenceInputStream != null)
						referenceInputStream.close();
				} else {
					referenceInputStream = null;
					referenceDom = null;
				}
			}
			
			// Build the InputStream for the XSD tool using the input stream
			actualInputStream = job.getActualInput().getInputStream();
			//assert actualInputStream != null;

			AnalysisResult xsdResult = xsdAnalysisTool.analyzeStream(job, null,
					actualInputStream, null);

			AnalysisResult compareResult = compareAnalysisTool.analyzeDOM(job,
					referenceDom, actualDom, null);

			AnalysisResult xpathResult = xpathAnalysisTool.analyzeDOM(job,
					null, actualDom, null);

			AnalysisRun run = new AnalysisRun(job);

			run.addResult(xsdAnalysisTool, xsdResult);
			run.addResult(compareAnalysisTool, compareResult);
			run.addResult(xpathAnalysisTool, xpathResult);

			HTMLAnalysisOutputWriter.writeAnalysisResults(outputFolder, job,
					xsdAnalysisTool, xsdResult);
			HTMLAnalysisOutputWriter.writeAnalysisResults(outputFolder, job,
					compareAnalysisTool, compareResult);
			HTMLAnalysisOutputWriter.writeAnalysisResults(outputFolder, job,
					xpathAnalysisTool, xpathResult);

			return run;
		} finally {
			try {
				if (referenceInputStream != null)
					referenceInputStream.close();
			} catch (IOException e) {
				;
			}
			try {
				if (actualInputStream != null)
					actualInputStream.close();
			} catch (IOException e) {
				;
			}
		}
	}

}
