package com.sg.cpm.project.actions.wbs.assignment;

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
import com.sg.cpm.project.view.WBSView;
import com.sg.db.model.CascadeObject;
import com.sg.db.model.SingleObject;
import com.sg.widget.part.IMasterChangeListener;

public class TemplateMenu implements IViewActionDelegate, IMasterChangeListener {

	private WBSView view;
	private MenuManager menuManager;
	private SingleObject masterProject;
//	private boolean hasAuthority;
	private SingleObject masterWork;
	private IAction menuAction;
	
	private ImportFromTemplate importAction;
	private AssignmentAuto assignmentAuto;

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
		menuAction = action;
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
		Assert.isNotNull(menuAction);
		// 如果没有权限
//		if (!hasAuthority) {
//			menuAction.setEnabled(false);
//			return;
//		}

		// 如果项目不存在,或者没有选择任务
		if (masterProject == null ) {
			menuAction.setEnabled(false);
			return;
		}

		menuAction.setEnabled(true);

		// 如果项目不是准备和暂停中的状态
		DBObject data = masterProject.getData();
		assignmentAuto.setEnabled(DataUtil.isInactive(data)||DataUtil.isReady(data)||DataUtil.isPause(data));
		importAction.setEnabled(DataUtil.isInactive(data)||DataUtil.isReady(data));
	}

	@Override
	public void init(IViewPart view) {
		this.view = (WBSView) view;
		initControlMenu();
		this.view.addMasterChangeListener(this);
	}

	private void initControlMenu() {
		importAction = new ImportFromTemplate(this);
		assignmentAuto = new AssignmentAuto(this);

		menuManager = new MenuManager("#PopupMenu");
		menuManager.add(assignmentAuto);
		menuManager.add(importAction);
	}

	@Override
	public void masterChanged(CascadeObject oldMaster, CascadeObject newMaster) {
		if (masterProject == null && newMaster == null) {
			
			return;
		} else if (masterProject != null && (newMaster == null || newMaster.getChildren().size() == 0)) {
			//changed
			masterProject = null;
			masterWork = null;
			checkStatus();
			return;
		}

		CascadeObject newProject = newMaster.getChildren().get(0);

		if (masterProject!=null&&Util.equals(masterProject.getSystemId(), newProject.getSystemId())) {
			return;
		} else {
			// 项目的编辑权限或者读取权限
//			ISessionAuthorityControl sac = new SingleActiveObject(newProject);
//			hasAuthority = UserSessionContext.hasAuthority(new String[] { UserSessionContext.PROJECT_MANAGER }, sac);
			masterProject = newMaster.getChildren().get(0);
			masterWork = null;
			checkStatus();
		}
		
	}

	public SingleObject getMasterWork() {
		return masterWork;
	}

	public SingleObject getMasterProject() {
		return masterProject;
	}

	public WBSView getView(){
		return view;
	}
}
