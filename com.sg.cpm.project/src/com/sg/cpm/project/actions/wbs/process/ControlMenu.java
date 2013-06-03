package com.sg.cpm.project.actions.wbs.process;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.internal.provisional.action.ToolBarManager2;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.mongodb.DBObject;
import com.sg.common.db.DataUtil;
import com.sg.common.ui.UIConstants;
import com.sg.cpm.project.view.WBSView;
import com.sg.db.model.CascadeObject;
import com.sg.db.model.SingleObject;
import com.sg.widget.part.IMasterChangeListener;

public class ControlMenu implements IViewActionDelegate, IMasterChangeListener {

	private WBSView view;
	private MenuManager menuManager;
	private WorkStart start;
	private WorkStop stop;
	private WorkClose close;
	private WorkCancel cancel;
	private SingleObject masterProject;
	private SingleObject masterWork;
	private IAction controlAction;

	@Override
	public void run(IAction action) {
		ToolBarManager2 tm = (ToolBarManager2) view.getViewSite().getActionBars().getToolBarManager();
		int index = tm.indexOf(action.getId())-1;
		ToolBar control = (ToolBar) tm.getControl2();
		Menu menu = menuManager.createContextMenu(control);
		
		Point hl = control.toDisplay(0, 0);
		hl.y += control.getBounds().height + 2;
		hl.x += index*42+2 ;
				
		menu.setLocation(hl);
		menu.setVisible(true);

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		controlAction = action;
		if (selection == null || selection.isEmpty()) {
			masterWork = null;
		} else {
			SingleObject selected = (SingleObject) ((IStructuredSelection) selection).getFirstElement();
			if (DataUtil.isWorkObject(selected)) {
				masterWork = selected;
			} else {
				masterWork = null;
			}
		}

		checkStatus();

	}

	public void checkStatus() {
		Assert.isNotNull(controlAction);
		// 如果没有权限
//		if (!hasAuthority) {
//			controlAction.setEnabled(false);
//			controlAction.setToolTipText(UIConstants.ACTION_CONTROL_TOOLTIPS + UIConstants.TEXT_UNAVILABLE_TOOLTIPS_NO_AUTH);
//			return;
//		}

		// 如果项目不存在,或者没有选择任务
		if (masterProject == null || masterWork == null) {
			controlAction.setEnabled(false);
			controlAction.setToolTipText(UIConstants.ACTION_CONTROL_TOOLTIPS + UIConstants.TEXT_UNAVILABLE_TOOLTIPS_NO_SELECTION);
			return;
		}

		DBObject projectData = masterProject.getData();
		// 如果项目不是进行中的状态
		if (!DataUtil.isPause(projectData)) {
			controlAction.setEnabled(false);
			controlAction.setToolTipText(UIConstants.ACTION_CONTROL_TOOLTIPS + UIConstants.TEXT_UNAVILABLE_TOOLTIPS_PROJECT_NOT_WIP);
			return;
		}
		controlAction.setEnabled(true);
		controlAction.setToolTipText(UIConstants.ACTION_CONTROL_TOOLTIPS);

		DBObject workData = masterWork.getData();
		
		start.setEnabled(DataUtil.isInactive(workData)||DataUtil.isReady(workData)||DataUtil.isPause(workData));

		stop.setEnabled(DataUtil.isProcess(workData));

		close.setEnabled(DataUtil.isProcess(workData)||DataUtil.isPause(workData));

		cancel.setEnabled(DataUtil.isProcess(workData)||DataUtil.isPause(workData));

	}

	@Override
	public void init(IViewPart view) {
		this.view = (WBSView) view;
		initControlMenu();
		this.view.addMasterChangeListener(this);
	}

	private void initControlMenu() {
		start = new WorkStart(this);
		stop = new WorkStop(this);
		close = new WorkClose(this);
		cancel = new WorkCancel(this);

		menuManager = new MenuManager("#PopupMenu");
		menuManager.add(start);
		menuManager.add(stop);
		menuManager.add(close);
		menuManager.add(cancel);
	}

	@Override
	public void masterChanged(CascadeObject oldMaster, CascadeObject newMaster) {
		if (Util.equals(newMaster, masterProject)) {
			return;
		} else {
			if (newMaster == null || newMaster.getChildren().size() < 1) {
				masterProject = null;
				return;
			} else {
				masterProject = newMaster.getChildren().get(0);
			}
		}
	}

	public SingleObject getMasterWork() {
		return masterWork;
	}

}
