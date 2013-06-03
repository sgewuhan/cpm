package com.sg.widget.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.sg.db.model.ISingleObject;
import com.sg.widget.WidgetConstants;

public class Remove implements IWorkbenchWindowActionDelegate {

	protected ISingleObject selected;
	protected IWorkbenchWindow window;

	@Override
	public void run(IAction action) {
		boolean ok = MessageDialog.openQuestion(window.getShell(), getRemoveTitle(), getRemoveMessage());
		if (!ok)
			return;
		selected.remove();
	}

	protected String getRemoveTitle() {
		return WidgetConstants.MESSAGE_REMOVE;
	}

	protected String getRemoveMessage() {
		return "" + selected + WidgetConstants.MESSAGE_REMOVE;
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection == null || selection.isEmpty()) {
			selected = null;
			return;
		} else {
			Object element = ((StructuredSelection) selection).getFirstElement();
			if (element instanceof ISingleObject) {
				selected = (ISingleObject) element;
			} else {
				selected = null;
			}
		}
		updateActionStatus(action);
	}

	protected void updateActionStatus(IAction action) {
		if (selected == null) {
			action.setEnabled(false);
		} else {
			action.setEnabled(true);
		}

	}

	@Override
	public void dispose() {

	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}