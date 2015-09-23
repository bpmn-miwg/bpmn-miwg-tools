package org.omg.bpmn.miwg.mvn;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;

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

	private File outputFolder;

	public AnalysisFacade(File outputFolder) {
		this.outputFolder = outputFolder;
	}

	public Collection<AnalysisRun> executeAnalysisJobs(
			Collection<AnalysisJob> jobs) throws Exception {
		Collection<AnalysisRun> runs = new LinkedList<AnalysisRun>();

		for (AnalysisJob job : jobs) {
			AnalysisRun run = executeAnalysisJob(job);
			if (run != null) {
			  runs.add(run);
			}
		}
		HTMLAnalysisOutputWriter.writeOverview(outputFolder, runs);

		return runs;
	}

	public AnalysisRun executeAnalysisJob(AnalysisJob job) throws Exception {
	  System.out.println("Executing AnalysisJob '" + job.getName() + "' ...");
		XPathAnalysisTool xpathAnalysisTool = new XPathAnalysisTool();
		XsdAnalysisTool xsdAnalysisTool = new XsdAnalysisTool();
		XmlCompareAnalysisTool compareAnalysisTool = new XmlCompareAnalysisTool();

		InputStream referenceInputStream = null;
		InputStream actualInputStream = null;

		try {
			Document actualDom = null;
			Document referenceDom = null;

			// Build the DOMs for the DOMAnalysisTools
			{
				actualInputStream = job.getActualInput().getInputStream();
				// assert actualInputStream != null;

				try {
				  actualDom = DOMFactory.getDocument(actualInputStream);
				} catch (Exception e) {
				  e.printStackTrace();
				}

				// assert actualDom != null;
				if (actualInputStream != null)
					actualInputStream.close();
			}

			boolean hasReference;
			{
				hasReference = job.hasReference();
				if (hasReference) {
					referenceInputStream = job.getReferenceInput()
							.getInputStream();
					if (referenceInputStream != null) {
					  referenceDom = DOMFactory.getDocument(referenceInputStream);
					// assert referenceDom != null;
						referenceInputStream.close();
					}
				}
			}

			if (referenceDom != null && actualDom != null) {
  			// Build the InputStream for the XSD tool using the input stream
  			actualInputStream = job.getActualInput().getInputStream();
  			// assert actualInputStream != null;
  
  			AnalysisResult xsdResult = xsdAnalysisTool.analyzeStream(job, null,
  					actualInputStream, null);
  
  			AnalysisResult compareResult = compareAnalysisTool.analyzeDOM(job,
  					referenceDom, actualDom, null);
  
  			AnalysisResult xpathResult = xpathAnalysisTool.analyzeDOM(job,
  					referenceDom, actualDom, null);
  
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
			} else {
			  return null;
			}
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
