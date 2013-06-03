package com.sg.widget.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.sg.widget.part.NavigatableTreeView;

public class Expand implements IViewActionDelegate {

	private NavigatableTreeView view;

	@Override
	public void run(IAction action) {
		view.expand();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		
	}

	@Override
	public void init(IViewPart view) {
		this.view = (NavigatableTreeView)view;
	}

}
