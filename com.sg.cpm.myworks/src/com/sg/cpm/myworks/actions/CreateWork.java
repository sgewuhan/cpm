package com.sg.cpm.myworks.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.sg.common.ui.UIConstants;
import com.sg.widget.dialog.SingleObjectEditorDialog;

public class CreateWork implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	@Override
	public void run(IAction action) {
		SingleObjectEditorDialog.getInstance(
				window.getShell(), UIConstants.EDITOR_STANDLONE_WORK_CREATE,
				null, null, false).open();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}
