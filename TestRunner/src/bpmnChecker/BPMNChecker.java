package bpmnChecker;

import java.util.List;

import bpmnChecker.tests.B_1_0_Test;
import bpmnChecker.tests.validation.ValidatorTest;

public class BPMNChecker {

	public static void main(String[] args) throws Throwable {

		if (args.length != 2) {
			System.err.println("OMG MIWG BPMN Checker");
			System.err
					.println("Two parameters required: SOURCEFOLDER REPORTFOLDER");
			return;
		}

		List<TestInfo> testFiles = TestInfo.findTestFiles(args[0]);

		TestManager manager = new TestManager();
		manager.registerTest(new ValidatorTest());
		manager.registerTest(new B_1_0_Test());

		for (TestInfo tfi : testFiles) {
			String file = tfi.getFile().getCanonicalPath();

			System.out.println();
			System.out.println("EXAMINING FILE " + file);
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
