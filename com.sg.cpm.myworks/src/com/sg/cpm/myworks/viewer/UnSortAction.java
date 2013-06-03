package com.sg.cpm.myworks.viewer;

import org.eclipse.jface.action.Action;

import com.sg.common.ui.UIConstants;
import com.sg.cpm.myworks.view.WorkInBox;

public class UnSortAction extends Action {

	private MessageSortControl menu;
	private WorkInBox wib;

	public UnSortAction(WorkInBox view, MessageSortControl messageSortControl) {
		super();
		setText(UIConstants.TEXT_CANCEL_SORTING);
		this.menu = messageSortControl;
		wib = view;
	}

	@Override
	public void run() {
		wib.unSort();
		menu.updateActions(this);
	}

	
}
