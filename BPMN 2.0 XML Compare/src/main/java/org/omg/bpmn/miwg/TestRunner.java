/**
 * The MIT License (MIT)
 * Copyright (c) 2013 Signavio, OMG BPMN Model Interchange Working Group
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

package org.omg.bpmn.miwg;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.omg.bpmn.miwg.bpmn2_0.comparison.Bpmn20ConformanceChecker;
import org.xml.sax.SAXException;


public class TestRunner {
	
	private static final String COMPAREFILE_EXTENSION = "-roundtrip.bpmn";
	
	/**
	 * First argument path to folder containing the reference bpmn xml files
	 * Second argument path to folder containing the bpmn files to compare with
	 * 
	 * @param args
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * 
	 */
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		
		File refFolder = new File(args[0]);
		File testFolder = new File(args[1]);
		
		if(!refFolder.isDirectory() || !testFolder.isDirectory()) {
			throw new IllegalArgumentException("Invalid path to folder");
		}
		
		Bpmn20ConformanceChecker checker = new Bpmn20ConformanceChecker();
		
		for(File bpmnFile : refFolder.listFiles()) {
			if(isBpmnFile(bpmnFile)) {
				File compareFile = getCompareFile(bpmnFile, testFolder);
				System.out.println("Comparing \n" + bpmnFile + "\n" + compareFile);
				
				String diffs = checker.checkForSignificantDifferences(bpmnFile, compareFile);
				
				System.out.println("\nDifferences found: ");
				System.out.println(diffs);
			}
		}
		
		

	}
	
	private static boolean isBpmnFile(File bpmnFile) {
		int i = bpmnFile.getName().lastIndexOf(".");
		return bpmnFile.getName().substring(i+1).equals("bpmn");
	}
	
	private static File getCompareFile(File refFile, File testFolder) {
		int i = refFile.getName().lastIndexOf(".");
		String fName = refFile.getName().substring(0, i) + COMPAREFILE_EXTENSION;
		
		return new File(testFolder, fName);
	}
}
