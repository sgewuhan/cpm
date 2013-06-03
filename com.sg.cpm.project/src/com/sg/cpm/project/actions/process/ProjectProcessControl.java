package com.sg.cpm.project.actions.process;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.mongodb.DBObject;
import com.sg.common.db.DataUtil;
import com.sg.common.ui.UIConstants;
import com.sg.db.model.SingleObject;
import com.sg.widget.part.NavigatableTableView;

public abstract class ProjectProcessControl implements IViewActionDelegate {

//	private boolean hasAuthority;
	protected SingleObject master;
	protected NavigatableTableView view;

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection == null || selection.isEmpty()) {
			master = null;
			action.setEnabled(false);
			return;
		} else {
			SingleObject newMaster = (SingleObject) ((IStructuredSelection) selection).getFirstElement();
//			if (master != null && (!Util.equals(master.getSystemId(), newMaster.getSystemId()))) {
//				// 项目的编辑权限
//				ISessionAuthorityControl sac = new SingleActiveObject(newMaster);
//				hasAuthority = UserSessionContext.hasAuthority(new String[] { UserSessionContext.PROJECT_MANAGER }, sac);
//				if (!hasAuthority) {
//					master = newMaster;
//					action.setEnabled(false);
//					return;
//				}
//			}
			if (statusValid(newMaster.getData())) {
				action.setEnabled(true);
			} else {
				action.setEnabled(false);
			}
			master = newMaster;
			return;

		}
	}

	protected abstract boolean statusValid(DBObject data);


	@Override
	public void init(IViewPart view) {
		this.view = (NavigatableTableView) view;
	}

	public void checkStatus() {
		IAction start = ((ActionContributionItem) view.getViewSite().getActionBars().getToolBarManager()
				.find(UIConstants.ACTION_PROCESS_START)).getAction();
		IAction stop = ((ActionContributionItem) view.getViewSite().getActionBars().getToolBarManager()
				.find(UIConstants.ACTION_PROCESS_STOP)).getAction();
		IAction close = ((ActionContributionItem) view.getViewSite().getActionBars().getToolBarManager()
				.find(UIConstants.ACTION_PROCESS_CLOSE)).getAction();
		IAction cancel = ((ActionContributionItem) view.getViewSite().getActionBars().getToolBarManager()
				.find(UIConstants.ACTION_PROCESS_CANCEL)).getAction();

		if (master == null ) {
			start.setEnabled(false);
			stop.setEnabled(false);
			close.setEnabled(false);
			cancel.setEnabled(false);
			return;
		}
		DBObject data = master.getData();
		
		start.setEnabled(DataUtil.isInactive(data)||DataUtil.isReady(data)||DataUtil.isPause(data));

		stop.setEnabled(DataUtil.isProcess(data));

		close.setEnabled(DataUtil.isProcess(data)||DataUtil.isProcess(data));

		cancel.setEnabled(DataUtil.isProcess(data)||DataUtil.isProcess(data));

	}

	@Override
	public void run(IAction action) {
		view.getViewer().setSelection(new StructuredSelection(master));
	}

}
