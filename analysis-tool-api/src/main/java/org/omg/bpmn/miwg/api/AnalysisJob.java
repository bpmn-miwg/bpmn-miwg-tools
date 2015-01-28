package org.omg.bpmn.miwg.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.omg.bpmn.miwg.api.input.AnalysisInput;
import org.omg.bpmn.miwg.api.input.FileAnalysisInput;

public class AnalysisJob {

	private String fullApplicationName; // e.g., “camunda Modeler 2.4.0”,
										// “Reference”)
	private String miwgTestCase; // e.g., “A.1.0”
	private MIWGVariant variant;
	private AnalysisInput actualInput, referenceInput;

	public AnalysisJob(String FullApplicationName, String testCase,
			MIWGVariant variant, AnalysisInput actualInput,
			AnalysisInput referenceInput) {
		this.fullApplicationName = FullApplicationName;
		this.miwgTestCase = testCase;
		this.variant = variant;
		this.actualInput = actualInput;
		this.referenceInput = referenceInput;
	}

	public AnalysisJob(String fullFileName) throws IOException {
		final String EXT_ROUNDTRIP = "-roundtrip.bpmn";
		final int EXT_ROUNDTRIP_LEN = EXT_ROUNDTRIP.length();
		final String EXT_IMPORT = "-import.bpmn";
		final int EXT_IMPORT_LEN = EXT_IMPORT.length();
		final String EXT_EXPORT = "-export.bpmn";
		final int EXT_EXPORT_LEN = EXT_EXPORT.length();

		File bpmnFile = new File(fullFileName);
		String fileName = bpmnFile.getName();
		String fileNameLower = fileName.toLowerCase();
		int fileNameLen = fileName.length();

		File folder = bpmnFile.getParentFile();

		fullApplicationName = bpmnFile.getParentFile().getName();
		if (fileNameLower.endsWith(EXT_ROUNDTRIP)) {
			variant = MIWGVariant.Roundtrip;
			miwgTestCase = fileName.substring(0, fileNameLen
					- EXT_ROUNDTRIP_LEN);
		} else if (fileNameLower.endsWith("-import.bpmn")) {
			variant = MIWGVariant.Import;
			miwgTestCase = fileName.substring(0, fileNameLen - EXT_IMPORT_LEN);
		} else if (fileNameLower.endsWith("-export.bpmn")) {
			variant = MIWGVariant.Export;
			miwgTestCase = fileName.substring(0, fileNameLen - EXT_EXPORT_LEN);
		} else if (folder.getName().toLowerCase().equals("reference")) {
			variant = MIWGVariant.Reference;
			miwgTestCase = bpmnFile.getName();
		} else {
			variant = MIWGVariant.Undefined;
			miwgTestCase = bpmnFile.getName();
		}

		if (!bpmnFile.exists())
			throw new FileNotFoundException(bpmnFile.getCanonicalPath());
		actualInput = new FileAnalysisInput(bpmnFile);

		File parentFolder = folder.getParentFile();
		File referenceFolder = new File(parentFolder, "Reference");
		if (!referenceFolder.exists())
			throw new FileNotFoundException(referenceFolder.getCanonicalPath());

		File referenceBpmnFile = new File(referenceFolder, miwgTestCase
				+ ".bpmn");

		/**
		 * In some cases, the reference file might be missing.
		 * This is problematic for the XmlCompareTool.
		 */
		if (referenceBpmnFile.exists())
			referenceInput = new FileAnalysisInput(referenceBpmnFile);
		else
			referenceInput = null;
	}

	public String getName() {
		if (variant == MIWGVariant.Reference)
			return fullApplicationName + "-" + miwgTestCase;
		else
			return fullApplicationName + "-" + miwgTestCase + "-"
					+ variant.toString().toLowerCase();
	}

	public String getFullApplicationName() {
		return fullApplicationName;
	}

	public String getMIWGTestCase() {
		return miwgTestCase;
	}

	public MIWGVariant getVariant() {
		return variant;
	}

	public AnalysisInput getActualInput() {
		return actualInput;
	}

	public boolean hasReference() {
		return referenceInput != null;
	}

	public AnalysisInput getReferenceInput() {
		return referenceInput;
	}

}
