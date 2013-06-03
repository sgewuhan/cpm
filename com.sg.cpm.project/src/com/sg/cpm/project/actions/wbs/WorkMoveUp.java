package com.sg.cpm.project.actions.wbs;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

import com.sg.common.db.IDBConstants;
import com.sg.db.model.CascadeObject;

public class WorkMoveUp extends WBSActions {

	@Override
	public void run(IAction action) {
		CascadeObject up = currentSelection.getUpNeighbor();
		Object upseq = up.getValue(IDBConstants.FIELD_WBSSEQ);
		Object seq = currentSelection.getValue(IDBConstants.FIELD_WBSSEQ);
		up.setValue(IDBConstants.FIELD_WBSSEQ, seq, null, false);
		currentSelection.setValue(IDBConstants.FIELD_WBSSEQ, upseq, null, false);
		up.save();
		currentSelection.save();
		CascadeObject parent = currentSelection.getParent();
		parent.sortChildren(new String[]{IDBConstants.FIELD_WBSSEQ});
		view.refresh(parent, false);
		view.select(currentSelection);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (view.isShowDocument()) {
			action.setEnabled(false);
			return;
		}
		super.selectionChanged(action, selection);
		boolean enable = action.isEnabled();
		if (enable) {

			CascadeObject up = currentSelection.getUpNeighbor();
			if (up == null) {
				action.setEnabled(false);
			}
		}
	}

}
