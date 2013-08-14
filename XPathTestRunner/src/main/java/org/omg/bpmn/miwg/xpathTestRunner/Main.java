package org.omg.bpmn.miwg.xpathTestRunner;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.omg.bpmn.miwg.xpathTestRunner.base.TestInstance;
import org.omg.bpmn.miwg.xpathTestRunner.base.TestManager;
import org.omg.bpmn.miwg.xpathTestRunner.base.TestOutput;
import org.omg.bpmn.miwg.xpathTestRunner.base.testEntries.InfoEntry;

public class Main {

	public static void main(String[] args) throws Throwable {

		if (args.length < 2 && args.length > 3) {
			System.err.println("OMG MIWG XPATH automated test");
			System.err
					.println("Two parameters required: SOURCEFOLDER REPORTFOLDER [APPLICATION]");
			return;
		}

		TestManager manager = new TestManager();

		if (args.length == 3)
			manager.limitApplication(args[2]);

		List<TestInstance> testInstances = TestInstance.buildTestInstances(
				manager, args[0]);

		for (TestInstance testInstance : testInstances) {
			manager.executeTests(testInstance, args[1]);
		}

		Collections.sort(testInstances, new Comparator<TestInstance>() {
			@Override
		    public int compare(TestInstance o1, TestInstance o2) {
				
				if (!o1.getApplication().equals(o2.getApplication())) {
					return o1.getApplication().compareTo(o2.getApplication());
				} else {
					return o1.getTest().compareTo(o2.getTest());
				}
		    }
		});
	
		TestOutput out = new TestOutput("Overview", args[1]);
		out.push(new InfoEntry("Summary:"));
		for (TestInstance testInstance : testInstances) {
			out.println(new InfoEntry(String.format(
					"%1$-30s : %2$-20s: OK: %3$3d, Findings: %4$3d",
					testInstance.getApplication(), testInstance.getTest(),
					testInstance.getOKs(), testInstance.getFindings())));
		}
		out.pop();
		out.close();
	}
}
