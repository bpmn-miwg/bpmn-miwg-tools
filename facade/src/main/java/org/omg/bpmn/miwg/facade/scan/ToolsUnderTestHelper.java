package org.omg.bpmn.miwg.facade.scan;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

/**
 * Convenience class that reads the JSON configuration of tools for whom
 * submissions exist and expose it in simple convenient methods.
 *
 * @author Tim Stephenson
 */
public class ToolsUnderTestHelper {
    public static final String TOOLS_TESTED = "/tools-tested-by-miwg.json";

    public List<String> getToolVsnNames() {
        List<String> toolNames = new ArrayList<String>();
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream(ToolsUnderTestHelper.TOOLS_TESTED);
            JsonReader reader = Json.createReader(new InputStreamReader(is));
            JsonObject rootObj = reader.readObject();
            JsonArray toolsArr = rootObj.getJsonArray("tools");
            for (JsonValue v : toolsArr) {
                JsonObject tool = (JsonObject) v;
                String toolFolderName = tool.getString("tool") + " "
                        + tool.getString("version");
                toolNames.add(toolFolderName);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ":" + e.getMessage());
            throw e;
        }
        return toolNames;
    }
}
