package org.omg.bpmn.miwg.scan;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class ToolsUnderTestHelperTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Test
    public void testReadToolVsnNames() {
        ToolsUnderTestHelper helper = new ToolsUnderTestHelper();
        List<String> toolVsnNames = helper.getToolVsnNames();

        assertEquals(21, toolVsnNames.size());
    }

}
