/*
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
package org.omg.bpmn.miwg.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.omg.bpmn.miwg.testresult.Output;

/**
 * <b>Experimental</b> interface attempting to allow both current test tools to
 * be invoked the same way.
 * 
 * <p>
 * Importantly this interface tests a single BPMN model at a time and operates
 * on streams rather than files to allow greater flexibility for invokers.
 * Results are also expressed in terms of the bean classes in the
 * <code>test-result</code> module.
 * 
 * @author Tim Stephenson
 * 
 */
public interface TestTool {

    String getName(); 
    
    Collection<? extends Output> getSignificantDifferences(
            InputStream expectedBpmnXml, InputStream actualBpmnXml)
            throws IOException, ParserConfigurationException;
}
