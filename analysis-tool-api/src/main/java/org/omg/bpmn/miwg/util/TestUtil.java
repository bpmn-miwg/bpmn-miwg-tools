package org.omg.bpmn.miwg.util;

import java.io.File;
import java.io.FilenameFilter;

public class TestUtil {

	public static final String REPORT_FOLDER = "test-temp";
	
	public static void prepareHTMLReportFolder(String reportFolderPath) {
		File reportFolder = new File(reportFolderPath);
		reportFolder.mkdirs();
		
		File[] files = reportFolder.listFiles(new FilenameFilter() {
	
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".html");
			}
			
		});
		
		for (File file:files) {
			file.delete();
		}
	}
	
}
