package com.sg.widget.util;

import java.io.IOException;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.sg.widget.Widget;

public class WordExportJob extends Job {

	private static final String EXPORT_FAL = "数据导出失败";

	private static final String EXPORT_SUC = "数据导出完成";

	private static final String EXPORT_TO_WORD = "导出结果到Word文件";

	private String templatePath;

	private String outputPath;

	private Map<String, Object> paraData;

	public WordExportJob(String exportTemplatePath, String exportOutputPath, Map<String, Object> paraData) {

		super(EXPORT_TO_WORD);
		this.templatePath = exportTemplatePath;
		this.outputPath = exportOutputPath;
		this.paraData = paraData;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		WordExportor ex = new WordExportor();
		try {
			ex.doExport(templatePath, outputPath, paraData);
			return new Status(IStatus.OK, Widget.PLUGIN_ID, EXPORT_SUC);
		} catch (IOException | XmlException | OpenXML4JException e) {
			return new Status(IStatus.ERROR, Widget.PLUGIN_ID, EXPORT_FAL+"\n"+e.getMessage());
		}
	}

	public String getOutputpath() {

		return outputPath;
	}

}
