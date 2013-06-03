package com.sg.cpm.myworks.view;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.sg.common.service.MessageObject;

public class WorkboxContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement==null){
			return new Object[]{};
		}else{
			List list = (List)inputElement;
			return list.toArray();
		}
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof MessageObject){
			return ((MessageObject)parentElement).getChildren();
		}
		return new Object[]{};
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof MessageObject){
			return ((MessageObject)element).hasChildren();
		}
		return false;
	}

}
