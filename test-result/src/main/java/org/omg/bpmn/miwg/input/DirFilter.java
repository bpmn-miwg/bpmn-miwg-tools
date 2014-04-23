package org.omg.bpmn.miwg.input;

import java.io.File;
import java.io.FileFilter;

public class DirFilter implements FileFilter {
    @Override
    public boolean accept(File pathname) {
        return pathname.isDirectory();
    }
}
