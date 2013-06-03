package com.sg.cpm.admin.user;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.mongodb.DBCursor;
import com.sg.common.BusinessService;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.ui.UIConstants;
import com.sg.cpm.admin.AdminFunctionEditor;
import com.sg.db.DBActivator;
import com.sg.db.expression.query.QueryExpression;
import com.sg.db.model.CascadeObject;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.SingleObject;
import com.sg.resource.Resource;
import com.sg.user.UserSessionContext;
import com.sg.widget.Widget;
import com.sg.widget.dialog.ISingleObjectEditorDialogCallback;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.dialog.SingleObjectEditorDialogCallback;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.SingleObjectEditorInput;
import com.sg.widget.util.Util;
import com.sg.widget.viewer.tableviewer.QueryTableViewer;
import com.sg.widget.viewer.treeviewer.QueryTreeViewer;

public class UserEditor extends AdminFunctionEditor implements ISelectionChangedListener {

	private static final int SITE = 0;

	private static final int USER = 1;

	private QueryTreeViewer siteViewer;

	private QueryTableViewer userViewer;

	private Button createSite;

	private Button createUser;

	private Button activeUser;

	private Button edit;

	private Button remove;

	private SingleObject currentUser = null;

	private CascadeObject currentSite = null;

	private int currentSelection;

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 1;
		parent.setLayout(layout);

		createToolbar(parent);

		// 用Sash分割，左边显示项目的模板，右边显示任务模板
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);

		createSite(sashForm);

		createUser(sashForm);

		sashForm.setWeights(new int[] { 1,2 });

		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		updateButtons();

		super.createPartControl(parent);
	}

	private void createSite(Composite parent) {
		CTabFolder tabFolderTop = new CTabFolder(parent,SWT.TOP);
		CTabItem item = new CTabItem(tabFolderTop, SWT.NONE);
		item.setText(UIConstants.TEXT_SITE);

		siteViewer = new QueryTreeViewer(tabFolderTop, SWT.VIRTUAL | SWT.FULL_SELECTION,
				Widget.getQueryTreeViewerConfiguration(UIConstants.VIEWER_TREE_SITE));

		siteViewer.addPostSelectionChangedListener(this);
		siteViewer.setAutoExpandLevel(QueryTreeViewer.ALL_LEVELS);

		update();
		item.setControl(siteViewer.getControl());
		tabFolderTop.setSelection(0);
	}

	private void createUser(Composite parent) {
		CTabFolder tabFolderTop = new CTabFolder(parent,SWT.TOP);
		CTabItem item = new CTabItem(tabFolderTop, SWT.NONE);

		item.setText(UIConstants.TEXT_USER);
		userViewer = new QueryTableViewer(tabFolderTop, SWT.VIRTUAL | SWT.FULL_SELECTION,
				Widget.getQueryTableViewerConfiguration(UIConstants.VIEWER_TABLE_SITEUSER2));

		userViewer.addPostSelectionChangedListener(this);
		
		item.setControl(userViewer.getControl());
		tabFolderTop.setSelection(0);
	}

	@Override
	public void update() {
		CascadeObject root = siteViewer.createExpression();
		root.setParamValue(IDBConstants.FIELD_SYSID, UserSessionContext.getSession().getSiteContextId()).setParamValue(
				IDBConstants.FIELD_DESC, UserSessionContext.getSession().getSiteContextName());
		siteViewer.setInput(root);
	}

	private void createToolbar(Composite parent) {
		Composite toolbar = new Composite(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
		RowLayout layout = new RowLayout();
		layout.wrap = false;
		layout.pack = true;
		layout.justify = false;
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		toolbar.setLayout(layout);

		createSite = new Button(toolbar, SWT.PUSH);
		createSite.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		createSite.setImage(Resource.getImage(Resource.CREATE_SITE32));
		createSite.setToolTipText(UIConstants.TEXT_CREATE_SITE);
		createSite.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleCreateSite();
			}
		});

		createUser = new Button(toolbar, SWT.PUSH);
		createUser.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		createUser.setImage(Resource.getImage(Resource.CREATE_USER32));
		createUser.setToolTipText(UIConstants.TEXT_CREATE_USER);
		createUser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleCreateUser();
			}
		});

		activeUser = new Button(toolbar, SWT.PUSH);
		activeUser.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		activeUser.setImage(Resource.getImage(Resource.ACTIVATE_USER32));
		activeUser.setToolTipText(UIConstants.TEXT_ACTIVATE_USER);
		activeUser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleActiveUser();
			}
		});

		edit = new Button(toolbar, SWT.PUSH);
		edit.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		edit.setImage(Resource.getImage(Resource.EDIT_PROP32));
		edit.setToolTipText(UIConstants.TEXT_EDIT);
		edit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleEdit();
			}
		});

		remove = new Button(toolbar, SWT.PUSH);
		remove.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		remove.setImage(Resource.getImage(Resource.REMOVE32));
		remove.setToolTipText(UIConstants.TEXT_REMOVE);
		remove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleRemove();
			}
		});
	}

	protected void handleActiveUser() {
		currentUser.setValue(IDBConstants.FIELD_ACTIVATE, !(Boolean.TRUE.equals(currentUser.getValue(IDBConstants.FIELD_ACTIVATE))));
		currentUser.save();
	}

	private void updateButtons() {
		createSite.setEnabled(currentSite != null);
		createUser.setEnabled(currentSite != null);
		activeUser.setEnabled(currentUser != null);

		if (currentUser != null) {

			if (Boolean.TRUE.equals(currentUser.getValue(IDBConstants.FIELD_ACTIVATE))) {
				activeUser.setToolTipText(UIConstants.TEXT_DISACTIVATE_USER);
				activeUser.setImage(Resource.getImage(Resource.DISACTIVATE_USER32));
			} else {
				activeUser.setToolTipText(UIConstants.TEXT_ACTIVATE_USER);
				activeUser.setImage(Resource.getImage(Resource.ACTIVATE_USER32));
			}
		}

		if (currentSelection == SITE) {
			if (currentSite == null || currentSite.getParent().getParent() == null) {
				remove.setEnabled(false);// 顶级站点不可删除
				edit.setEnabled(false);// 顶级站点不可编辑
			} else {
				remove.setEnabled(true);//
				edit.setEnabled(true);//
			}
		} else {
			remove.setEnabled(true);//
			edit.setEnabled(true);//
		}
	}

	protected void handleCreateUser() {
		ISingleObjectEditorDialogCallback cb = new SingleObjectEditorDialogCallback() {

			@Override
			public boolean saveBefore(ISingleObjectEditorInput input) {
				SingleObject data = (SingleObject) input.getInputData();
				Object uid = data.getValue(IDBConstants.FIELD_UID);
				if (Util.isNullOrEmptyString(uid)) {
					data.setValue(IDBConstants.FIELD_UID, DataUtil.createUID('0', 6));// 设置用户ID
				}

				// 将用户名转换为小写
				String userName = (String) data.getValue(IDBConstants.FIELD_DESC);
				if (userName == null) {
					userName = "";
				}
				data.setValue(IDBConstants.FIELD_DESC, userName);
				data.setValue(IDBConstants.FIELD_SITEPARENT, currentSite.getSystemId());
				data.setValue(IDBConstants.FIELD_ACTIVATE, true);

				return super.saveBefore(input);
			}

			@Override
			public boolean saveAfter(ISingleObjectEditorInput input) {

				String uid = (String) input.getInputData().getValue(IDBConstants.FIELD_UID);
				BusinessService.getWorkflowService().addUserInBPM(uid);
				
				return super.saveAfter(input);
			}
			
			
			

		};
		SingleObjectEditorDialog soed = SingleObjectEditorDialog.getInstance(getSite().getShell(), UIConstants.EDITOR_USER_CREATE, null,
				cb, false);
		int ok = soed.open();
		if (ok == SingleObjectEditorDialog.OK) {
			ISingleObject so = soed.getInputData();
			userViewer.addDataObject(so);
			userViewer.setSelection(new StructuredSelection(so));
		}
	}

	protected void handleCreateSite() {
		ISingleObjectEditorDialogCallback cb = new SingleObjectEditorDialogCallback() {

			@Override
			public boolean saveBefore(ISingleObjectEditorInput input) {
				SingleObject data = (SingleObject) input.getInputData();
				data.setValue(IDBConstants.FIELD_SITEPARENT, currentSite.getSystemId());
				return super.saveBefore(input);
			}

		};
		SingleObjectEditorDialog soed = SingleObjectEditorDialog.getInstance(getSite().getShell(), UIConstants.EDITOR_SITE, null, cb, true);
		int ok = soed.open();
		if (ok == SingleObjectEditorDialog.OK) {
			ISingleObject so = soed.getInputData();
			CascadeObject child = currentSite.createChild(IDBConstants.EXP_CASCADE_SO_SITE, so.getData(),
					DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_SITE));
			siteViewer.refresh(currentSite);
			siteViewer.expandToLevel(currentSite, AbstractTreeViewer.ALL_LEVELS);
			siteViewer.setSelection(new StructuredSelection(child), true);
		}
	}

	protected void handleEdit() {

		Shell shell = getEditorSite().getShell();

		if (currentSelection == USER) {
			// 编辑用户
			SingleObjectEditorInput editInput = new SingleObjectEditorInput(currentUser);
			ISingleObjectEditorDialogCallback call = new SingleObjectEditorDialogCallback() {

				@Override
				public boolean saveBefore(ISingleObjectEditorInput input) {
					SingleObject data = (SingleObject) input.getInputData();

					// 将用户名转换为小写
					String userName = (String) data.getValue(IDBConstants.FIELD_DESC);
					if (userName == null) {
						userName = "";
					}
					data.setValue(IDBConstants.FIELD_DESC, userName);

					return super.saveBefore(input);
				}
			};
			SingleObjectEditorDialog.OPEN(shell, UIConstants.EDITOR_USER_EDIT, editInput, call, false);
		} else {
			// 编辑站点
			SingleObjectEditorInput editInput = new SingleObjectEditorInput(currentSite);
			SingleObjectEditorDialog.OPEN(shell, UIConstants.EDITOR_SITE, editInput, null, true);
		}

	}

	protected void handleRemove() {
		Shell shell = getEditorSite().getShell();

		if (currentSelection == USER) {
			// 删除用户
			boolean ok = MessageDialog.openQuestion(shell, UIConstants.TEXT_REMOVE_USER, "" + currentUser
					+ UIConstants.MESSAGE_QUESTION_DELETE);
			if (!ok)
				return;

			//如果该用户在某个组织下 需要级联删除
			BusinessService.getOrganizationService().removeUser(currentUser.getSystemId());
			userViewer.removeDataObject(currentUser);
		} else {
			// 删除站点
			// 如果站点有下级节点不可删除
			if (currentSite.loadChildren().size() > 0) {
				MessageDialog.openInformation(shell, UIConstants.TEXT_REMOVE_SITE, currentSite
						+ UIConstants.MESSAGE_CANNOT_DELETE_SITE_HAS_SUBSITE);
				return;
			}

			// 删除用户
			boolean ok = MessageDialog.openQuestion(shell, UIConstants.TEXT_REMOVE_SITE, "" + currentSite
					+ UIConstants.MESSAGE_QUESTION_DELETE);
			if (!ok)
				return;

			// 如果站点有用户不可删除
			QueryExpression exp = DBActivator.getQueryExpression(IDBConstants.EXP_QUERY_USER);
			exp.setParamValue(IDBConstants.PARAM_INPUT_SITEPARENT, currentSite.getSystemId());
			DBCursor cur = exp.run();
			if (cur.hasNext()) {
				MessageDialog.openInformation(shell, UIConstants.TEXT_REMOVE_SITE, currentSite
						+ UIConstants.MESSAGE_CANNOT_DELETE_SITE_HAS_USER);
				return;
			}

			currentSite.remove();
			siteViewer.setSelection(null);
		}
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if (event.getSelectionProvider() == siteViewer) {// select site
			if (event == null || event.getSelection().isEmpty()) {
				currentSite = null;
				userViewer.setInput(null);
			} else {
				currentSite = (CascadeObject) ((StructuredSelection) event.getSelection()).getFirstElement();
				QueryExpression exp = userViewer.createExpression();
				exp.setParamValue(IDBConstants.PARAM_INPUT_SITEPARENT, currentSite.getSystemId());
				userViewer.updateInputData();
			}
			currentSelection = SITE;
		} else {
			if (event == null || event.getSelection().isEmpty()) {
				currentUser = null;
			} else {
				currentUser = (SingleObject) ((StructuredSelection) event.getSelection()).getFirstElement();
			}
			currentSelection = USER;
		}

		updateButtons();
	}

	@Override
	public boolean needUpdate() {
		return true;
	}

}
