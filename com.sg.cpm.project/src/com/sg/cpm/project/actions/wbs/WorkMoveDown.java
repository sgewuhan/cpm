package com.sg.cpm.project.actions.wbs;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

import com.sg.common.db.IDBConstants;
import com.sg.db.model.CascadeObject;

public class WorkMoveDown extends WBSActions {

	@Override
	public void run(IAction action) {
		CascadeObject down = currentSelection.getDownNeighbor();
		Object downseq = down.getValue(IDBConstants.FIELD_WBSSEQ);
		Object seq = currentSelection.getValue(IDBConstants.FIELD_WBSSEQ);
		down.setValue(IDBConstants.FIELD_WBSSEQ, seq, null, false);
		currentSelection.setValue(IDBConstants.FIELD_WBSSEQ, downseq, null, false);
		down.save();
		currentSelection.save();
		CascadeObject parent = currentSelection.getParent();
		parent.sortChildren(new String[]{IDBConstants.FIELD_WBSSEQ});
		view.refresh(parent,false);
		view.select(currentSelection);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if(view.isShowDocument()){
			action.setEnabled(false);
			return;
		}
		super.selectionChanged(action, selection);
		boolean enable = action.isEnabled();
		if(enable){
			
			CascadeObject down = currentSelection.getDownNeighbor();
			if(down==null){
				action.setEnabled(false);
			}
		}
	}
}
