package com.sg.cpm.project.actions.wbs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.CascadeObject;

public class WorkMoveLeft extends WBSActions {

	@Override
	public void run(IAction action) {
		CascadeObject selection = currentSelection;

		Object[] expanded = view.getViewer().getExpandedElements();

		// 1 自己的下面所有的兄弟变成自己的儿子，并设置序号从自己的最后一个儿子的序号开始，依次保存
		// 实现方法，循环取下兄弟，修改序号，从0开始，修改wbsparent为自己的_id
		CascadeObject parent = selection.getParent();
		List<CascadeObject> neighbors = new ArrayList<CascadeObject>();
		neighbors.addAll(parent.getChildren());
		int index = neighbors.indexOf(selection);

		int seq = 1;

		List<CascadeObject> children = selection.getChildren();
		if (children.size() > 0) {
			CascadeObject lastChild = children.get(children.size() - 1);
			seq = ((Number) lastChild.getValue(IDBConstants.FIELD_WBSSEQ)).intValue() + 1;
		}

		Object myId = selection.getValue("_id");
		CascadeObject downNeighbor;
		for (int i = index + 1; i < neighbors.size(); i++) {
			downNeighbor = neighbors.get(i);

			downNeighbor.setValue(IDBConstants.FIELD_WBSPARENT, myId);
			downNeighbor.setValue(IDBConstants.FIELD_WBSSEQ, seq++);

			downNeighbor.save();
			// 为了避免重新读取数据库，直接添加节点
			parent.removeChild(downNeighbor);
			selection.addChild(downNeighbor);
		}

		// 2父的序号取出，+1作为自己的序号，取出祖父，变成自己的父，保存自己
		// 获得自己的新seq
		Number parentSeq = (Number) parent.getValue(IDBConstants.FIELD_WBSSEQ);
		seq = parentSeq.intValue() + 1;
		// 获得祖父的id
		CascadeObject grandparent = parent.getParent();
		Object grandparentId = grandparent.getValue(IDBConstants.FIELD_SYSID);
		// 修改自己的seq和wbsparent的id
		selection.setValue(IDBConstants.FIELD_WBSPARENT, grandparentId);
		selection.setValue(IDBConstants.FIELD_WBSSEQ, seq++);
		selection.save();

		neighbors = grandparent.getChildren();
		index = neighbors.indexOf(parent);
		for (int i = index + 1; i < neighbors.size(); i++) {
			downNeighbor = neighbors.get(i);
			downNeighbor.setValue(IDBConstants.FIELD_WBSSEQ, seq++);
			downNeighbor.save();
		}

		parent.removeChild(selection);
		grandparent.addChild(selection, index + 1);

		view.refresh(grandparent, true);
		view.refresh(parent, true);
		view.refresh(selection, true);

		Object[] newExpand = new Object[expanded.length + 1];
		System.arraycopy(expanded, 0, newExpand, 0, expanded.length);
		newExpand[expanded.length] = selection;
		view.getViewer().setExpandedElements(newExpand);

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

			CascadeObject parent = currentSelection.getParent();
			if (!DataUtil.isWorkObject(parent)) {
				action.setEnabled(false);
			}
		}
	}
}
