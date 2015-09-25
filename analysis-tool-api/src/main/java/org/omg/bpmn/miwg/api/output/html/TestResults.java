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

package org.omg.bpmn.miwg.api.output.html;

import java.util.LinkedList;
import java.util.List;

import org.omg.bpmn.miwg.api.AnalysisJob;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * POJO representing the result from one {@link AnalysisJob}.
 * 
 * <p>
 * Example structure:
 * 
 * <pre>
 * &lt;div class="testresults"> 
 *   &lt;div class="tool">
 *     &lt;h2>Signavio Process Editor 7.0</h2>
 *       &lt;div class="test">
 *       &lt;h3>A.1.0-roundtrip</h3> 
 *       &lt;div class="issue">
 *         &lt;p>Issue description&lt;/p>
 *       &lt;/div>
 *       &lt;div class="issue">
 *         &lt;p>Sub issue description&lt;/p>
 *       &lt;/div>
 *       &lt;div> 
 *       ... 
 *     &lt;/div> 
 *     &lt;div class="test"> 
 *       ... 
 *     &lt;/div> 
 *   &lt;/div>
 *   &lt;div class="tool"> 
 *     ... 
 *   &lt;/div> 
 *   ... 
 * &lt;div>
 * </pre>
 * 
 * @author Sven
 * @see {@link HTMLAnalysisOutputWriter} for helpers to write the results to
 *      file.
 */
@Root(name = "div")
public class TestResults {

    @Attribute(name = "class", required = true)
    private String clazz = TestResults.class.getSimpleName().toLowerCase();

    @ElementList(inline = true, required = false, empty = true)
    private List<Tool> tools;

    /**
     * Adds a child {@link Tool} element to the results structure.
     * 
     * @param tool
     * 
     * @return The tool added or the existing
     */
    public Tool addTool(Tool tool) {
        Tool t = tool;
        if (this.getTools().contains(tool)) {
            t = this.getTools().get(this.getTools().indexOf(tool));
        } else {
            this.getTools().add(t);
        }

        return t;
    }

    /**
     * Adds a tool by name or returns the existing.
     * 
     * @param toolName
     * @return
     */
    public Tool addTool(String toolName) {
        Tool tool = new Tool(toolName);

        return this.addTool(tool);
    }

    

    /* Getter & Setter */

    /**
     * Initializes the list of {@link Tool}
     * 
     * @return List of {@link Tool}s
     */
    private List<Tool> getTools() {
        if (tools == null) {
            tools = new LinkedList<Tool>();
        }

        return tools;
    }

}
