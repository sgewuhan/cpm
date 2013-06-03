package com.sg.cpm.project.actions.wbs;

import org.eclipse.core.internal.commands.util.Util;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.sg.cpm.project.view.WBSView;
import com.sg.db.model.CascadeObject;
import com.sg.widget.part.IMasterChangeListener;

public class WBSActions implements IViewActionDelegate, IMasterChangeListener {

	protected CascadeObject currentSelection;
	protected WBSView view;
	protected CascadeObject project;

	@Override
	public void run(IAction action) {
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		//编辑可用性
		if(view.isShowDocument()){
			action.setEnabled(false);
			return;
		}
		
		if (!selection.isEmpty()) {
			currentSelection = (CascadeObject) ((IStructuredSelection) selection).getFirstElement();
		} else {
			currentSelection = null;
		}
		//考虑权限
		action.setEnabled(currentSelection != null );
	}

	@Override
	public void init(IViewPart view) {
		this.view = (WBSView) view;
		this.view.addMasterChangeListener(this);
	}

	@Override
	public void masterChanged(CascadeObject oldMaster, CascadeObject newMaster) {
		if(Util.equals(project, newMaster)){
			return;
		}
		this.project = newMaster;
	}

}
