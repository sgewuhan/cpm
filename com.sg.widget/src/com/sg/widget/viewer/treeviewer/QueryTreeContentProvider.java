package com.sg.widget.viewer.treeviewer;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.sg.db.model.CascadeObject;

public class QueryTreeContentProvider implements ITreeContentProvider {

//	private QueryTreeViewer viewer;

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
//		this.viewer = (QueryTreeViewer) viewer;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		CascadeObject[] result = ((CascadeObject) inputElement).getChildren().toArray(new CascadeObject[]{});
//		for(CascadeObject co :result){
//			co.addEventListener(viewer);
//		}
		return result;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return getElements(parentElement);
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return ((CascadeObject) element).getChildrenCount()>0;
	}

}
