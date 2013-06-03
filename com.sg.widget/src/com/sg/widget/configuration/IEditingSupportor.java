package com.sg.widget.configuration;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ViewerColumn;

public interface IEditingSupportor {

	EditingSupport createEditingSupport(ColumnViewer viewer,
			ViewerColumn vColumn);

	
}
