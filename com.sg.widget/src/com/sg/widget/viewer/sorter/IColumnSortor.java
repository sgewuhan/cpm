package com.sg.widget.viewer.sorter;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.widgets.Item;

public interface IColumnSortor {

	AbstractColumnViewerSorter CreateSortor(ColumnViewer viewer,
			Item column, String name);

}
