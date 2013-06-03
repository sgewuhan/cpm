package com.sg.widget.viewer.sorter;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.widgets.Item;

public class UniSorterProvider implements IColumnSortor {

	@Override
	public AbstractColumnViewerSorter CreateSortor(
			ColumnViewer viewer, Item column,String columneName) {
		return new UniSorter( viewer,column,columneName);
	}

}
