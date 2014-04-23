/**
 * The MIT License (MIT)
 * Copyright (c) 2013 OMG BPMN Model Interchange Working Group
 *
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package org.omg.bpmn.miwg.xpathTestRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.omg.bpmn.miwg.api.TestTool;
import org.omg.bpmn.miwg.testresult.Output;
import org.omg.bpmn.miwg.xpathTestRunner.base.TestOutput;
import org.omg.bpmn.miwg.xpathTestRunner.testBase.Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.A_1_0_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.A_2_0_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.A_3_0_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.A_4_0_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.A_4_1_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.B_1_0_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.B_2_0_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.DemoTechnicalSupportTest;
import org.omg.bpmn.miwg.xpathTestRunner.tests.ValidatorTest;

public class XPathTestTool implements TestTool {

    private String testName;
    private Map<String, Test> registeredTests = new HashMap<String, Test>();
    private String outputFolder;

    public XPathTestTool(String testName, String outputFolder) {
        registerTest(new ValidatorTest());
        registerTest(new B_1_0_Test());
        registerTest(new B_2_0_Test());
        registerTest(new A_1_0_Test());
        registerTest(new A_2_0_Test());
        registerTest(new A_3_0_Test());
        registerTest(new A_4_0_Test());
        registerTest(new A_4_1_Test());
        registerTest(new DemoTechnicalSupportTest());
        this.testName = testName;
        this.outputFolder = outputFolder;
    }

    public String getName() {
        return "xpath";
    }

    private void registerTest(Test test) {
        registeredTests.put(test.getName(), test);
    }

    @Override
    public Collection<? extends Output> getSignificantDifferences(
            InputStream expectedBpmnXml, InputStream actualBpmnXml)
            throws IOException, ParserConfigurationException {
        Test test = getTest();
        test.init(new TestOutput(testName, outputFolder));

        List<? extends Output> outputs = null;
        try {
            outputs = test.execute(actualBpmnXml);
        } catch (Throwable e) {
            throw new IOException(e.getMessage(), e);
        }
        return outputs;
    }

    private Test getTest() {
        if (registeredTests.containsKey(testName)) {
            return registeredTests.get(testName);
        } else {
            throw new RuntimeException("No test registered with name: "
                    + testName);
        }
    }

}
