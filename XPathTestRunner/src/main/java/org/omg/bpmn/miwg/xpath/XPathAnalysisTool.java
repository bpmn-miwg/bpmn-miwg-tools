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

package org.omg.bpmn.miwg.xpath;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.omg.bpmn.miwg.api.AnalysisResult;
import org.omg.bpmn.miwg.api.AnalysisTool;
import org.omg.bpmn.miwg.common.Check;
import org.omg.bpmn.miwg.common.CheckOutput;
import org.omg.bpmn.miwg.xpath.checks.A_1_0_Check;
import org.omg.bpmn.miwg.xpath.checks.A_1_1_Check;
import org.omg.bpmn.miwg.xpath.checks.A_1_2_Check;
import org.omg.bpmn.miwg.xpath.checks.A_2_0_Check;
import org.omg.bpmn.miwg.xpath.checks.A_3_0_Check;
import org.omg.bpmn.miwg.xpath.checks.A_4_0_Check;
import org.omg.bpmn.miwg.xpath.checks.A_4_1_Check;
import org.omg.bpmn.miwg.xpath.checks.B_1_0_Check;
import org.omg.bpmn.miwg.xpath.checks.B_2_0_Check;
import org.omg.bpmn.miwg.xpath.checks.C_1_0_Check;
import org.omg.bpmn.miwg.xpath.checks.DemoTechnicalSupportCheck;
import org.omg.bpmn.miwg.xpath.common.AbstractXpathCheck;

public class XPathAnalysisTool implements AnalysisTool {

    private String checkName;
    private List<AbstractXpathCheck> registeredChecks = new LinkedList<AbstractXpathCheck>();

    public XPathAnalysisTool(String testName) {
        registerCheck(new A_1_0_Check());
        registerCheck(new A_1_1_Check());
        registerCheck(new A_1_2_Check());
        registerCheck(new A_2_0_Check());
        registerCheck(new A_3_0_Check());
        registerCheck(new A_4_0_Check());
        registerCheck(new A_4_1_Check());
        registerCheck(new B_1_0_Check());
        registerCheck(new B_2_0_Check());
        registerCheck(new C_1_0_Check());
        registerCheck(new DemoTechnicalSupportCheck());
        this.checkName = testName;
    }

    public String getName() {
        return "xpath";
    }

    private void registerCheck(AbstractXpathCheck check) {
        registeredChecks.add(check);
    }

    @Override
    public AnalysisResult runAnalysis(File testResult, 
            InputStream expectedBpmnXml, InputStream actualBpmnXml,
            String reportFolder)
            throws IOException, ParserConfigurationException {
        Check test = getCheck(testResult);
        test.init(new CheckOutput(checkName, reportFolder));

        try {
           return test.execute(actualBpmnXml);
        } catch (Throwable e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    private Check getCheck(File testResult) {
    	for (AbstractXpathCheck check : registeredChecks) {
    		if (check.isApplicable(testResult.getName())) {
    			return check;
    		}
    	}
    	throw new RuntimeException(String.format("No applicable test found for %s", testResult.getName()));
    }

}
