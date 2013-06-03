package com.sg.cpm;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import com.sg.common.ui.UIConstants;
import com.sg.resource.Resource;

/**
 * Creates, adds and disposes actions for the menus and action bars of each
 * workbench window.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction saveAction;
	private IWorkbenchAction saveAllAction;
	private IWorkbenchAction closeAllAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	// Actions - important to allocate these only in makeActions, and then use
	// them in the fill methods. This ensures that the actions aren't recreated
	// in the fill methods.

	protected void makeActions(IWorkbenchWindow window) {
		saveAction = ActionFactory.SAVE.create(window);
		saveAction.setDisabledImageDescriptor(null);
		saveAction.setText(UIConstants.TEXT_SAVE);
		saveAction.setHoverImageDescriptor(null);
		saveAction.setImageDescriptor(Resource.getImageDescriptor(Resource.TB_SAVE32));
		register(saveAction);
		
		saveAllAction = ActionFactory.SAVE_ALL.create(window);
		saveAllAction.setText(UIConstants.TEXT_SAVEALL);
		saveAllAction.setDisabledImageDescriptor(null);
		saveAllAction.setHoverImageDescriptor(null);
		saveAllAction.setImageDescriptor(Resource.getImageDescriptor(Resource.TB_SAVEALL32));
		register(saveAllAction);
		
		closeAllAction = ActionFactory.CLOSE_ALL.create(window);
		closeAllAction.setText(UIConstants.TEXT_CLOSEALL);
		closeAllAction.setDisabledImageDescriptor(null);
		closeAllAction.setHoverImageDescriptor(null);
		closeAllAction.setImageDescriptor(Resource.getImageDescriptor(Resource.TB_CLOSEALL32));
		register(closeAllAction);

	}

	protected void fillMenuBar(IMenuManager menuBar) {
	}

	@Override
	protected void fillCoolBar(ICoolBarManager coolBar) {
		IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		coolBar.add(new ToolBarContributionItem(toolbar, UIConstants.ID_TOOLBAR));
		toolbar.add(new GroupMarker(UIConstants.ID_TOOLBAR_GROUP_PERSPECTIVE));
		toolbar.add(saveAction);
		toolbar.add(saveAllAction);
		toolbar.add(closeAllAction);
		
		toolbar.add(new GroupMarker(UIConstants.ID_TOOLBAR_GROUP_COMMON));
	}

}
