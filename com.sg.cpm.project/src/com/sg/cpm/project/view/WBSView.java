package com.sg.cpm.project.view;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import com.sg.common.db.IDBConstants;
import com.sg.common.ui.UIConstants;
import com.sg.db.DBActivator;
import com.sg.db.model.CascadeObject;
import com.sg.widget.part.NavigatableTreeView;

public class WBSView extends NavigatableTreeView {

	private boolean showDocument = false;
	
//	private IAction moveRight;
//	
//	private IAction moveLeft;
//	
//	private IAction moveUp;
//	
//	private IAction moveDown;

	private IAction createTask;

	private IAction removeTask;

//	private IAction editTask;

	private CascadeObject exp;

	private IAction createDoc;

	private NavigatorPanel navi;

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		setActiveCollectionAdaptable(false);
		
		navi = new NavigatorPanel(viewer);
		addMasterChangeListener(navi);
	}

	public void reload(CascadeObject parent, boolean updateLabels) {
		parent.loadChildren();
		viewer.refresh(parent, updateLabels);
	}


	public void select(CascadeObject object) {
		viewer.setSelection(new StructuredSelection(object), true);
	}

	public void refresh(CascadeObject object, boolean updateLabels) {
		viewer.refresh(object, updateLabels);
	}

	public void setShowDocument(boolean b) {
		showDocument = b;
//		moveRight = moveRight != null ? moveRight : ((ActionContributionItem) getViewSite().getActionBars().getToolBarManager()
//				.find(UIConstants.ACTION_MOVE_RIHGT)).getAction();
//		moveLeft = moveLeft != null ? moveLeft : ((ActionContributionItem) getViewSite().getActionBars().getToolBarManager()
//				.find(UIConstants.ACTION_MOVE_LEFT)).getAction();
//		moveUp = moveUp != null ? moveUp
//				: ((ActionContributionItem) getViewSite().getActionBars().getToolBarManager().find(UIConstants.ACTION_MOVE_UP)).getAction();
//		moveDown = moveDown != null ? moveDown : ((ActionContributionItem) getViewSite().getActionBars().getToolBarManager()
//				.find(UIConstants.ACTION_MOVE_DOWN)).getAction();
		createTask = createTask != null ? createTask : ((ActionContributionItem) getViewSite().getActionBars().getToolBarManager()
				.find(UIConstants.ACTION_CREATE_WORK)).getAction();
		removeTask = removeTask != null ? removeTask : ((ActionContributionItem) getViewSite().getActionBars().getToolBarManager()
				.find(UIConstants.ACTION_REMOVE)).getAction();
		createDoc = createDoc != null ? createDoc : ((ActionContributionItem) getViewSite().getActionBars().getToolBarManager()
				.find(UIConstants.ACTION_CREATE_DOC)).getAction();

		if (showDocument) {
			navi.setEnable(false);
//			moveRight.setEnabled(false);
//			moveLeft.setEnabled(false);
//			moveUp.setEnabled(false);
//			moveDown.setEnabled(false);
//			moveRight.setToolTipText(UIConstants.ACTION_MOVE_RIHGT_TOOLTIPS + UIConstants.TEXT_UNAVILABLE_TOOLTIPS);
//			moveLeft.setToolTipText(UIConstants.ACTION_MOVE_LEFT_TOOLTIPS + UIConstants.TEXT_UNAVILABLE_TOOLTIPS);
//			moveUp.setToolTipText(UIConstants.ACTION_MOVE_UP_TOOLTIPS + UIConstants.TEXT_UNAVILABLE_TOOLTIPS);
//			moveDown.setToolTipText(UIConstants.ACTION_MOVE_DOWN_TOOLTIPS + UIConstants.TEXT_UNAVILABLE_TOOLTIPS);
			createTask.setEnabled(false);
			removeTask.setEnabled(false);
			createDoc.setEnabled(true);
			//«–ªªœ‘ æ
			exp = DBActivator.getCascadeObject(IDBConstants.EXP_CASCADE_WBS_WITH_DOC);
		} else {
			navi.setEnable(true);
//			moveRight.setToolTipText(UIConstants.ACTION_MOVE_RIHGT_TOOLTIPS);
//			moveLeft.setToolTipText(UIConstants.ACTION_MOVE_LEFT_TOOLTIPS);
//			moveUp.setToolTipText(UIConstants.ACTION_MOVE_UP_TOOLTIPS);
//			moveDown.setToolTipText(UIConstants.ACTION_MOVE_DOWN_TOOLTIPS);
			createDoc.setEnabled(false);
			//«–ªªœ‘ æ
			exp = DBActivator.getCascadeObject(IDBConstants.EXP_CASCADE_WBS);
		}
		
		resetData(exp,inputParaMap);

	}

	public boolean isShowDocument() {
		return showDocument;
	}

	@Override
	protected CascadeObject getExpression() {
		if (showDocument) {
			return DBActivator.getCascadeObject(IDBConstants.EXP_CASCADE_WBS_WITH_DOC);
		}else{
			return DBActivator.getCascadeObject(IDBConstants.EXP_CASCADE_WBS);
		}
	}

	@Override
	public void update() {
		if (showDocument) {
			exp = DBActivator.getCascadeObject(IDBConstants.EXP_CASCADE_WBS_WITH_DOC);
		} else {
			exp = DBActivator.getCascadeObject(IDBConstants.EXP_CASCADE_WBS);
		}
		resetData(exp,inputParaMap);
	}

	
	
}
