package com.sg.widget.part;

import java.util.List;
import java.util.Map;

public interface IFileExportable {

	boolean canExport();

	String getExportTemplatePath();

	String getExportOutputPath();

	Map<String, Object> getHeadData();

	List<Object[]> getBodyData();

	String getExportType();

	void export();

}
