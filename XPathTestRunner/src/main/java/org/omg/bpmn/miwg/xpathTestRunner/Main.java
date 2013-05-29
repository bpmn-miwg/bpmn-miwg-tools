package org.omg.bpmn.miwg.xpathTestRunner;

import java.io.File;
import java.util.List;

import org.omg.bpmn.miwg.xpathTestRunner.base.TestInfo;
import org.omg.bpmn.miwg.xpathTestRunner.base.TestManager;
import org.omg.bpmn.miwg.xpathTestRunner.base.TestOutput;
import org.omg.bpmn.miwg.xpathTestRunner.tests.B_1_0_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.B_2_0_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.ValidatorTest;



public class Main {

	public static void main(String[] args) throws Throwable {

		if (args.length < 2 && args.length > 3) {
			System.err.println("OMG MIWG BPMN Checker");
			System.err
					.println("Two parameters required: SOURCEFOLDER REPORTFOLDER [APPLICATION]");
			return;
		}

		TestManager manager = new TestManager();
		manager.registerTest(new ValidatorTest());
		manager.registerTest(new B_1_0_Test());
		manager.registerTest(new B_2_0_Test());

		if (args.length == 3)
			manager.limitApplication(args[2]);

		List<TestInfo> testFiles = TestInfo.findTestFiles(manager, args[0]);

		for (TestInfo tfi : testFiles) {
			File file = tfi.getFile();

			System.out.println();
			System.out.println("EXAMINING FILE " + file.getCanonicalPath());
			System.out.println();

			TestOutput out = new TestOutput(tfi, args[1]);
			try {

				tfi.printTestFileInfo(out);
				out.println();

				manager.printApplicableTests(file, out);
				out.println();

				manager.executeTests(file, out);
			} finally {
				out.close();
			}
		}

	}

}
