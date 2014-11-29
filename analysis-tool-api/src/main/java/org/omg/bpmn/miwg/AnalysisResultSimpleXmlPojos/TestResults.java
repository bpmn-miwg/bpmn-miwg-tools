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

package org.omg.bpmn.miwg.AnalysisResultSimpleXmlPojos;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * Example structure:
 * 
 * <div class="testresults"> <div class="tool"> <h2>Signavio Process Editor 7.0</h2>
 * <div class="test"> <h3>A.1.0-roundtrip</h3> <div class="issue">
 * <p>
 * Issue description
 * </p>
 * <div class="issue">
 * <p>
 * Sub issue description
 * </p>
 * </div> <div> ... </div> <div class="test"> ... </div> </div> <div
 * class="tool"> ... </div> ... <div>
 * 
 * @author Sven
 * 
 */
@Root(name = "div")
public class TestResults {

    private static final String HTML_FILE = "resultspage.html";
    private static final String RESULTS_PLACEHOLDER = "{TESTRESULTS}";

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

    /**
     * Returns a shadow copy of all tool results.
     * 
     * @return {@link List} of {@link Tool}
     */
    public List<Tool> getToolsCopy() {
        List<Tool> t = new LinkedList<Tool>();
        t.addAll(getTools());
        return t;
    }

    public String toString() {
        // RegistryMatcher m = new RegistryMatcher();
        // m.bind(String.class, new StringTransformer());
        Serializer serializer = new Persister();
        StringWriter writer = new StringWriter();

        try {
            serializer.write(this, writer);
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception occurred. See stack trace in log.";
        }
        return produceSurroundingHtml(writer);
    }

    private String produceSurroundingHtml(StringWriter writer) {
        InputStream is = TestResults.class.getClassLoader()
                .getResourceAsStream(HTML_FILE);
        String html = convertStreamToString(is);

        return html.replace(RESULTS_PLACEHOLDER, writer.toString());
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = null;
        try {
            s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } finally {
            s.close();
        }
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

    public void writeResultFile(File f) throws IOException {
        FileUtils.writeStringToFile(f, toString());
    }

}
