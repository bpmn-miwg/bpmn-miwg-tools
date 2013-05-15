package xpathTestRunner;

import java.util.List;

import xpathTestRunner.base.TestInfo;
import xpathTestRunner.base.TestManager;
import xpathTestRunner.base.TestOutput;
import xpathTestRunner.tests.B_1_0_Test;
import xpathTestRunner.tests.B_2_0_Test;
import xpathTestRunner.tests.ValidatorTest;


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
