package com.sg.widget.viewer.tableviewer;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class QueryTableContentProvider implements IStructuredContentProvider {

	private Object[] elements;

	@Override
	public void dispose() {
		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	      if( newInput == null ) {
	          elements = new Object[ 0 ];
	        } else {
	          List list = ( List )newInput;
	          elements = list.toArray();
	        }
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return elements;
	}


}
