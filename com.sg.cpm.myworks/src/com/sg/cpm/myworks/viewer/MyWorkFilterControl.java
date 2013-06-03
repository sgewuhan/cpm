package com.sg.cpm.myworks.viewer;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.sg.common.ui.UIConstants;
import com.sg.cpm.myworks.view.WorkInBox;
import com.sg.resource.Resource;


public class MyWorkFilterControl implements IViewActionDelegate {

	private WorkInBox view;

	@Override
	public void run(IAction action) {

		boolean currentState = view.switchProcessFilter();
		if(currentState){
			action.setToolTipText(UIConstants.TEXT_ONLYMYPROCESS);
			action.setImageDescriptor(Resource.getImageDescriptor(Resource.V_USER32));
		}else{
			action.setToolTipText(UIConstants.TEXT_ALLPROCESS);
			action.setImageDescriptor(Resource.getImageDescriptor(Resource.V_USERS32));
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void init(IViewPart view) {

		this.view = (WorkInBox) view;
	}

}
