package com.sg.cpm.project.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.commands.util.Util;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.mongodb.DBObject;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.ui.UIConstants;
import com.sg.db.model.CascadeObject;
import com.sg.resource.Resource;
import com.sg.widget.part.IMasterChangeListener;
import com.sg.widget.viewer.treeviewer.QueryTreeViewer;

public class NavigatorPanel implements MouseListener, IMasterChangeListener {

	private QueryTreeViewer viewer;
	private Button upButton;
	private Button downButton;
	private Button leftButton;
	private Button rightButton;
	private Button closeButton;
	private Shell navi;
	private boolean enable;
	private CascadeObject project;
	private CascadeObject currentSelection;

	public NavigatorPanel(QueryTreeViewer viewer) {
		this.viewer = viewer;
		viewer.getTree().addMouseListener(this);
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
	}

	@Override
	public void mouseDown(MouseEvent e) {
	}

	@Override
	public void mouseUp(MouseEvent e) {

		if (e.button == 3) {
			activeNavigator(e);
		}
	}

	private void activeNavigator(MouseEvent e) {

		IStructuredSelection selection = viewer.getSelection();
		if (selection == null || selection.isEmpty()) {
			setEnable(false);
			return;
		} else {
			setEnable(true);
		}

		if (project == null) {
			setEnable(false);
			return;
		} else {
			DBObject data = project.getData();
			if (DataUtil.isInactive(data)||DataUtil.isReady(data)) {
				setEnable(true);
			} else {
				setEnable(false);
				return;
			}
			
			
			//************************************************************************************************************
			//只有项目经理和项目管理员有权
			if(!DataUtil.isProjectManager(data)
					&&DataUtil.isProjectAdmin(data)){
				MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), UIConstants.TEXT_PROJECT_CONTROL, UIConstants.MESSAGE_CANNOT_CONTROL_PROJECT_PROCESS);
				setEnable(false);
				return;
			}
			//************************************************************************************************************

			
		}

		currentSelection = (CascadeObject) selection.getFirstElement();
		// 如果选中的是项目也不能出来
		if (!DataUtil.isWorkObject(currentSelection)) {
			setEnable(false);
			return;
		} else {
			setEnable(true);
		}

		if (enable) {
			createNavi();
			Rectangle b = navi.getBounds();
			Control control = (Control) e.widget;
			Point point = control.toDisplay(e.x, e.y);
			point.x = point.x - b.width / 2;
			point.y = point.y - b.height / 2;
			navi.setLocation(point);
			navi.open();
		}
	}

	private void createNavi() {
		// 显示导航
		navi = new Shell(SWT.NONE | SWT.APPLICATION_MODAL);
		GridLayout layout = new GridLayout(3, true);
		layout.horizontalSpacing = 2;
		layout.verticalSpacing = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		navi.setLayout(layout);

		upButton = new Button(navi, SWT.PUSH);
		upButton.setImage(Resource.getImage(Resource.V_UP32));
		upButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1));
		upButton.setData(RWT.CUSTOM_VARIANT, "navimenu");
		upButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				upPressed();
			}

		});

		leftButton = new Button(navi, SWT.PUSH);
		leftButton.setImage(Resource.getImage(Resource.V_LEFT32));
		leftButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		leftButton.setData(RWT.CUSTOM_VARIANT, "navimenu");
		leftButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				leftPressed();
			}

		});
		closeButton = new Button(navi, SWT.PUSH);
		closeButton.setImage(Resource.getImage(Resource.V_NAVICLOSE32));
		closeButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		closeButton.setData(RWT.CUSTOM_VARIANT, "navimenu");
		closeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				closePressed();
			}

		});

		rightButton = new Button(navi, SWT.PUSH);
		rightButton.setImage(Resource.getImage(Resource.V_RIGHT32));
		rightButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		rightButton.setData(RWT.CUSTOM_VARIANT, "navimenu");
		rightButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				rightPressed();
			}

		});

		downButton = new Button(navi, SWT.PUSH);
		downButton.setImage(Resource.getImage(Resource.V_DOWN32));
		downButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1));
		downButton.setData(RWT.CUSTOM_VARIANT, "navimenu");
		downButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				downPressed();
			}

		});

		navi.setDefaultButton(closeButton);
		closeButton.setFocus();

		navi.pack();
		navi.setData(RWT.CUSTOM_VARIANT, "navimenu");
		navi.addShellListener(new ShellListener() {

			@Override
			public void shellDeactivated(ShellEvent e) {
				navi.close();
			}

			@Override
			public void shellClosed(ShellEvent e) {
			}

			@Override
			public void shellActivated(ShellEvent e) {
			}
		});

		updateButtonStatus();

	}

	private void updateButtonStatus() {
		// 检查各个按钮的可用状态
		CascadeObject downNeighbor = currentSelection.getDownNeighbor();
		if (downNeighbor == null) {
			downButton.setEnabled(false);
		} else {
			downButton.setEnabled(true);
		}

		CascadeObject parent = currentSelection.getParent();
		if (!DataUtil.isWorkObject(parent)) {
			leftButton.setEnabled(false);
		} else {
			leftButton.setEnabled(true);
		}

		CascadeObject upNeighbor = currentSelection.getUpNeighbor();
		if (upNeighbor == null) {
			rightButton.setEnabled(false);
			upButton.setEnabled(false);
		} else {
			rightButton.setEnabled(true);
			upButton.setEnabled(true);
		}
	}

	public void setEnable(boolean b) {
		this.enable = b;
	}

	@Override
	public void masterChanged(CascadeObject oldMaster, CascadeObject newMaster) {
		if (project == null && newMaster == null) {
			setEnable(false);
			return;
		} else if (project != null && (newMaster == null || newMaster.getChildren().size() == 0)) {
			setEnable(false);
			project = null;
			return;
		}

		CascadeObject newProject = newMaster.getChildren().get(0);

		if (project!=null&&Util.equals(project.getSystemId(), newProject.getSystemId())) {
			return;
		} else {
//			// 项目的编辑权限或者读取权限
//			ISessionAuthorityControl sac = new SingleActiveObject(newProject);
//			boolean hasUpdateAuthority = UserSessionContext.hasAuthority(new String[] { UserSessionContext.PROJECT_MANAGER }, sac);
//			if (!hasUpdateAuthority) {
//				setEnable(false);
//			} else {
//				setEnable(true);
//			}

			project = newMaster.getChildren().get(0);
		}
	}

	protected void upPressed() {
		CascadeObject up = currentSelection.getUpNeighbor();
		Object upseq = up.getValue(IDBConstants.FIELD_WBSSEQ);
		Object seq = currentSelection.getValue(IDBConstants.FIELD_WBSSEQ);
		up.setValue(IDBConstants.FIELD_WBSSEQ, seq, null, false);
		currentSelection.setValue(IDBConstants.FIELD_WBSSEQ, upseq, null, false);
		up.save();
		currentSelection.save();
		CascadeObject parent = currentSelection.getParent();
		parent.sortChildren(new String[] { IDBConstants.FIELD_WBSSEQ });
		viewer.refresh(parent, false);
		viewer.setSelection(new StructuredSelection(currentSelection), true);
		updateButtonStatus();
	}

	protected void leftPressed() {
		CascadeObject selection = currentSelection;

		Object[] expanded = viewer.getExpandedElements();

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

		viewer.refresh(grandparent, true);
		viewer.refresh(parent, true);
		viewer.refresh(selection, true);

		Object[] newExpand = new Object[expanded.length + 1];
		System.arraycopy(expanded, 0, newExpand, 0, expanded.length);
		newExpand[expanded.length] = selection;
		viewer.setExpandedElements(newExpand);

		viewer.setSelection(new StructuredSelection(selection), true);
		updateButtonStatus();
	}

	protected void downPressed() {
		CascadeObject down = currentSelection.getDownNeighbor();
		Object downseq = down.getValue(IDBConstants.FIELD_WBSSEQ);
		Object seq = currentSelection.getValue(IDBConstants.FIELD_WBSSEQ);
		down.setValue(IDBConstants.FIELD_WBSSEQ, seq, null, false);
		currentSelection.setValue(IDBConstants.FIELD_WBSSEQ, downseq, null, false);
		down.save();
		currentSelection.save();
		CascadeObject parent = currentSelection.getParent();
		parent.sortChildren(new String[] { IDBConstants.FIELD_WBSSEQ });
		viewer.refresh(parent, false);
		viewer.setSelection(new StructuredSelection(currentSelection), true);
		updateButtonStatus();
	}

	protected void rightPressed() {
		// CascadeObject parent = currentSelection.getParent();
		// List<CascadeObject> children = parent.getChildren();
		// int index = children.indexOf(currentSelection);
		// CascadeObject upNeighbor = children.get(index-1);
		// view.getViewer().expandToLevel(upNeighbor, 1);
		CascadeObject selection = currentSelection;

		Object[] expanded = viewer.getExpandedElements();

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

		viewer.refresh(parent, true);
		viewer.refresh(upNeighbor, true);

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
			viewer.setExpandedElements(newExpand);
		} else {
			viewer.setExpandedElements(expanded);
		}
		viewer.setSelection(new StructuredSelection(selection), true);
		updateButtonStatus();
	}

	protected void closePressed() {
		navi.dispose();
	}

}
