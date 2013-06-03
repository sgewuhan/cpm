package com.sg.widget.viewer.tableviewer;

import java.util.List;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.sg.db.model.ISingleObject;

public class LazyQueryTableContentProvider implements ILazyContentProvider {

	private QueryTableViewer tableViewer;
	private List<ISingleObject> elements;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		tableViewer = (QueryTableViewer) viewer;
		elements =(List) newInput;
	}

	public void updateElement(int index) {
		ISingleObject jo = elements.get(index);

		tableViewer.replace(jo, index);
	}

	public void dispose() {
		// do nothing
	}

}
