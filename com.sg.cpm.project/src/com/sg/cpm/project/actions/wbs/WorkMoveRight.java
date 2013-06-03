package com.sg.cpm.project.actions.wbs;

import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

import com.sg.common.db.IDBConstants;
import com.sg.db.model.CascadeObject;

public class WorkMoveRight extends WBSActions {

	@Override
	public void run(IAction action) {
		// CascadeObject parent = currentSelection.getParent();
		// List<CascadeObject> children = parent.getChildren();
		// int index = children.indexOf(currentSelection);
		// CascadeObject upNeighbor = children.get(index-1);
		// view.getViewer().expandToLevel(upNeighbor, 1);
		CascadeObject selection = currentSelection;

		Object[] expanded = view.getViewer().getExpandedElements();

		// 自己的下兄弟seq-1
		CascadeObject parent = selection.getParent();
		List<CascadeObject> children = parent.getChildren();
		int index = children.indexOf(selection);
		int seq = ((Number) selection.getValue(IDBConstants.FIELD_WBSSEQ)).intValue();
		for (int i = index + 1; i < children.size(); i++) {
			CascadeObject downNeighbor = children.get(i);
			downNeighbor.setValue(IDBConstants.FIELD_WBSSEQ, seq++);
			downNeighbor.save();
		}

		// 自己变成自己上兄弟的最后一个儿子
		CascadeObject upNeighbor = children.get(index - 1);
		children = upNeighbor.getChildren();
		if (children.size() > 0) {
			CascadeObject lastChild = children.get(children.size() - 1);
			seq = ((Number) lastChild.getValue(IDBConstants.FIELD_WBSSEQ)).intValue() + 1;
		} else {
			seq = 1;
		}
		selection.setValue(IDBConstants.FIELD_WBSPARENT, upNeighbor.getValue(IDBConstants.FIELD_SYSID));
		selection.setValue(IDBConstants.FIELD_WBSSEQ, seq);
		selection.save();

		parent.removeChild(selection);
		upNeighbor.addChild(selection);

		view.refresh(parent, true);
		view.refresh(upNeighbor, true);

		boolean has = false;
		for (int i = 0; i < expanded.length; i++) {
			if (expanded[i] == upNeighbor) {
				has = true;
				break;
			}
		}

		if (!has) {
			Object[] newExpand = new Object[expanded.length + 1];
			System.arraycopy(expanded, 0, newExpand, 0, expanded.length);
			newExpand[expanded.length] = upNeighbor;
			view.getViewer().setExpandedElements(newExpand);
		} else {
			view.getViewer().setExpandedElements(expanded);
		}

		view.select(selection);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if(view.isShowDocument()){
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
