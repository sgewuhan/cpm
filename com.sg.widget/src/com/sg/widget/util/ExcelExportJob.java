package com.sg.widget.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.sg.widget.Widget;

public class ExcelExportJob extends Job {

	private static final String EXPORT_FAL = "数据导出失败";
	private static final String EXPORT_SUC = "数据导出完成";
	private static final String EXPORT_TO_EXCEL = "导出结果到Excel文件";
	private String templatePath;
	private String outputPath;
	private Map<String, Object> headData;
	private List<Object[]> bodyData;

	public ExcelExportJob(String exportTemplatePath, String exportOutputPath,
			Map<String, Object> headData, List<Object[]> bodyData) {
		super(EXPORT_TO_EXCEL);
		this.templatePath =exportTemplatePath;
		this.outputPath =exportOutputPath;
		this.headData =headData;
		this.bodyData =bodyData;	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		ExcelExportor ex = new ExcelExportor();
		try {
			ex.doExport(templatePath, outputPath, headData, bodyData, 0,monitor);
			return new Status(IStatus.OK, Widget.PLUGIN_ID, EXPORT_SUC);
		} catch (IOException e) {
			return new Status(IStatus.ERROR, Widget.PLUGIN_ID, EXPORT_FAL);
		}
	}

	public String getOutputpath() {
		return outputPath;
	}

}
