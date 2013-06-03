package com.sg.cpm.admin.organization;

import java.util.List;

import org.bson.types.ObjectId;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
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
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.viewer.tableviewer.QueryTableViewer;
import com.sg.widget.viewer.treeviewer.QueryTreeViewer;

public class OrganizationEditor extends AdminFunctionEditor implements ISelectionChangedListener {

	class DropTargetForSetListener implements DropTargetListener {

		public void dragEnter(final DropTargetEvent event) {
			event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND;
		}

		public void dragLeave(final DropTargetEvent event) {
		}

		public void dragOperationChanged(final DropTargetEvent event) {
		}

		public void dragOver(final DropTargetEvent event) {
		}

		public void drop(final DropTargetEvent event) {
			
			CascadeObject targetParent = (CascadeObject) event.item.getData();
			if (targetParent == null)
				return;

			// 如果当前的放置目标是用户，应当禁止放置
			if (DataUtil.isUserObject(targetParent)) {
				MessageDialog.openWarning(getSite().getShell(), UIConstants.TEXT_ORGEDIT, UIConstants.MESSAGE_CANNOT_SETUSER_UNDER_AUSER);
				return;
			}

			
			//如果拖动源是用户表
			if("user".equals(event.data)){
				ObjectId uoid = (ObjectId) dragSourceItem.getValue(IDBConstants.FIELD_SYSID);

				// 如果parent下已经有了这个用户，返回
				List<CascadeObject> children = targetParent.loadChildren();
				for (CascadeObject co : children) {
					if (uoid.equals(co.getValue(IDBConstants.FIELD_USEROID))) {
						MessageDialog.openWarning(getSite().getShell(), UIConstants.TEXT_ORGEDIT,
								UIConstants.MESSAGE_HAVE_A_SAME_USER_UNDER_A_ORG);
						return;
					}
				}

				
				ObjectId rootId = null;
				if (inProjectTeam(targetParent)) {
					rootId = targetParent.getSystemId();
				} else {
					rootId = (ObjectId) targetParent.getValue(IDBConstants.FIELD_ROOTID);
				}

				DBObject obsUser = DataUtil.createOBSItem(rootId, targetParent.getSystemId(), null, uoid, IDBConstants.VALUE_OBS_USERTYPE);

				targetParent.createChild(IDBConstants.EXP_CASCADE_SO_OBS, obsUser, orgCollection);
				orgViewer.refresh(targetParent, false);
			}else{
			//拖动源是obs树
				// 如果拖放源就是拖放目标本身，跳出
				if(dragSourceItem == targetParent){
					return;
				}
				
				//如果拖放源是拖放目标的下级，跳出
				List<CascadeObject> children = targetParent.loadChildren();
				for (CascadeObject co : children) {
					if (dragSourceItem== co) {
						return;
					}
				}				
				
				//更改节点的obsparent的id;
				CascadeObject sourceParent = ((CascadeObject)dragSourceItem).getParent();
				dragSourceItem.setValue(IDBConstants.FIELD_OBSPARENT, targetParent.getValue(IDBConstants.FIELD_SYSID));
				dragSourceItem.save();
				sourceParent.removeChild((CascadeObject) dragSourceItem);
				targetParent.addChild((CascadeObject) dragSourceItem);
				
				orgViewer.refresh(sourceParent);
				orgViewer.refresh(targetParent);
				
//				MessageDialog.openWarning(getSite().getShell(), UIConstants.TEXT_ORGEDIT, UIConstants.ONLY_MOVE_USERFROMUSERLIST);
				return;
				
			}
			
			

			// 将拖来的用户保存到OBS
			// DBObject obsUser = new BasicDBObject();
			// obsUser.put(IDBConstants.FIELD_SYSID, new ObjectId());
			// obsUser.put(IDBConstants.FIELD_USEROID, uoid);
			// obsUser.put(IDBConstants.FIELD_TEMPLATE_TYPE,
			// IDBConstants.VALUE_OBS_USERTYPE);
			// obsUser.put(IDBConstants.FIELD_OBSPARENT, parent.getSystemId());
			// orgCollection.insert(obsUser);

		}

		public void dropAccept(final DropTargetEvent event) {
		}

	}

	public SingleObject dragSourceItem;

	class DragSourceForSetListener implements DragSourceListener {

		public void dragFinished(final DragSourceEvent event) {
		}

		public void dragSetData(final DragSourceEvent event) {
			event.data = "user";
			dragSourceItem = currentUser;
		}

		public void dragStart(final DragSourceEvent event) {
		}
	}

	class DropTargetForUnsetListener implements DropTargetListener {

		public void dragEnter(final DropTargetEvent event) {
			event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND;
		}

		public void dragLeave(final DropTargetEvent event) {
		}

		public void dragOperationChanged(final DropTargetEvent event) {
		}

		public void dragOver(final DropTargetEvent event) {
		}

		public void drop(final DropTargetEvent event) {
			// 删除OBS上的节点
			// String jsonOBS = (String) event.data;
			// if (jsonOBS.equals("#")) {
			// return;
			// }
			// DBObject data = (DBObject) JSON.parse(jsonOBS);
			// orgCollection.remove(new
			// BasicDBObject().append(IDBConstants.FIELD_SYSID,
			// data.get(IDBConstants.FIELD_SYSID)));
			if (DataUtil.isUserObject(currentOrg)) {
				currentOrg.remove();
				orgViewer.setSelection(null);
			}
		}

		public void dropAccept(final DropTargetEvent event) {
		}

	}

	class DragSourceForUnsetListener implements DragSourceListener {

		public void dragFinished(final DragSourceEvent event) {
		}

		public void dragSetData(final DragSourceEvent event) {
			event.data = "obs";
			
			dragSourceItem = currentOrg;
		}

		public void dragStart(final DragSourceEvent event) {
			// 当选择的是根，不可以拖动，理论上可以通过event.doit来控制哪些可以拖动，但这个在RAP中不起作用
		}
	}

	private QueryTreeViewer orgViewer;
	private QueryTableViewer userViewer;

	private Button createTeam;
	private Button createRole;
	private Button edit;
	private Button remove;
	private CascadeObject currentOrg;
	private SingleObject currentUser;
	private DBCollection orgCollection;
	private Button filterProjectTeam;
	private boolean enableProjectTeamFilter = true;
	private ViewerFilter filter;

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		filter = new ViewerFilter(){

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				boolean filtered = !DataUtil.isProjectTeamObject((ISingleObject) element);
				return filtered;
			}
			
		};

		orgCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_ORG);
		super.init(site, input);

	}

	/**
	 * 判断这个节点是否是在项目节点中
	 * @param element
	 * @return
	 */
	protected boolean inProjectTeam(CascadeObject element) {
		if(DataUtil.isProjectTeamObject(element)){
			return true;
		}else{
			CascadeObject parent = element.getParent();
			if(parent!=null){
				return inProjectTeam(parent);
			}
		}
			
		return false;
	}

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

		createOrg(sashForm);

		createUser(sashForm);

		sashForm.setWeights(new int[] { 1,2 });

		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		updateButtons();

		setupDND();
		
		update();
		super.createPartControl(parent);
	}

	private void createUser(Composite parent) {
		CTabFolder tabFolderTop = new CTabFolder(parent,SWT.TOP);
		CTabItem item = new CTabItem(tabFolderTop, SWT.NONE);

		item.setText(UIConstants.TEXT_USER);
//		group.setLayout(new FillLayout());
		userViewer = new QueryTableViewer(tabFolderTop, SWT.VIRTUAL | SWT.FULL_SELECTION,
				Widget.getQueryTableViewerConfiguration(UIConstants.VIEWER_TABLE_SITEUSER2)) {

			@Override
			protected boolean initLoad() {
				return false;
			}

		};
		userViewer.addPostSelectionChangedListener(this);
		item.setControl(userViewer.getControl());
		tabFolderTop.setSelection(0);
	}

	private void createOrg(Composite parent) {
		CTabFolder tabFolderTop = new CTabFolder(parent,SWT.TOP);
		CTabItem item = new CTabItem(tabFolderTop, SWT.NONE);

		item.setText(UIConstants.TEXT_OBS);
//		item.setLayout(new FillLayout());
		orgViewer = new QueryTreeViewer(tabFolderTop, SWT.VIRTUAL | SWT.FULL_SELECTION,
				Widget.getQueryTreeViewerConfiguration(UIConstants.VIEWER_TREE_ORG));
		orgViewer.setAutoExpandLevel(3);
		orgViewer.addPostSelectionChangedListener(this);
		item.setControl(orgViewer.getControl());
		handleFilterProjectTeam();
		tabFolderTop.setSelection(0);
	}
	
	@Override
	public void update() {
		CascadeObject orgInput = orgViewer.createExpression();
		orgInput.setParamValue(IDBConstants.FIELD_SYSID, UserSessionContext.getSession().getSiteContextId()).setParamValue(
				IDBConstants.FIELD_DESC, UserSessionContext.getSession().getSiteContextName());
		orgViewer.setInput(orgInput);

		QueryExpression userInput = userViewer.createExpression();
		userInput.setParamValue(IDBConstants.PARAM_INPUT_SITEPARENT, UserSessionContext.getSession().getSiteContextId());
		List<ISingleObject> result = DataUtil.getSiteUsers(UserSessionContext.getSession().getSiteContextId(), true);
		userViewer.runSetInput(result);
		
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
		
		filterProjectTeam = new Button(toolbar,SWT.PUSH);
		filterProjectTeam.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		filterProjectTeam.setImage(Resource.getImage(Resource.FILTER32));
		filterProjectTeam.setToolTipText(UIConstants.TEXT_CREATE_TEAM);
		filterProjectTeam.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleFilterProjectTeam();
			}
		});
		

		createTeam = new Button(toolbar, SWT.PUSH);
		createTeam.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		createTeam.setImage(Resource.getImage(Resource.CREATE_TEAM32));
		createTeam.setToolTipText(UIConstants.TEXT_CREATE_TEAM);
		createTeam.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleCreateTeam();
			}
		});

		createRole = new Button(toolbar, SWT.PUSH);
		createRole.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		createRole.setImage(Resource.getImage(Resource.CREATE_ROLE32));
		createRole.setToolTipText(UIConstants.TEXT_CREATE_ROLE);
		createRole.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleCreateRole();
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


	private void setupDND() {
		setDragSourceForSet(userViewer.getControl());
		setDropTargetForSet(orgViewer.getControl());

		setDropTargetForUnset(userViewer.getControl());
		setDragSourceForUnset(orgViewer.getControl());
	}

	private void setDragSourceForSet(Control dragControl) {
		DragSource dragSource = new DragSource(dragControl, DND.DROP_MOVE);
		dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dragSource.addDragListener(new DragSourceForSetListener());
	}

	private void setDropTargetForSet(Control dropControl) {
		DropTarget dropTarget = new DropTarget(dropControl, DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dropTarget.addDropListener(new DropTargetForSetListener());
	}

	private void setDragSourceForUnset(Control dragControl) {
		DragSource dragSource = new DragSource(dragControl, DND.DROP_MOVE);
		dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dragSource.addDragListener(new DragSourceForUnsetListener());
	}

	private void setDropTargetForUnset(Control dropControl) {
		DropTarget dropTarget = new DropTarget(dropControl, DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dropTarget.addDropListener(new DropTargetForUnsetListener());
	}

	protected void handleRemove() {
		Shell shell = getEditorSite().getShell();
		boolean ok = MessageDialog.openQuestion(shell, UIConstants.TEXT_REMOVE, "" + currentOrg + UIConstants.MESSAGE_QUESTION_DELETE);
		if (!ok)
			return;
		if (DataUtil.isProjectTeamObject(currentOrg)) {// 选中的是项目组
			// 判断显示是否存在，如果不存在，可以删除，否则禁止删除
			QueryExpression exp = DBActivator.getQueryExpression(IDBConstants.EXP_QUERY_PROJECT);
			exp.setParamValue(IDBConstants.PARAM_PROJECT_OBS_ROOT, currentOrg.getSystemId());
			DBCursor cur = exp.run();
			if (cur.hasNext()) {
				MessageDialog.openInformation(shell, UIConstants.TEXT_REMOVE, "" + currentOrg
						+ UIConstants.MESSAGE_CANNOT_PROJECT_TEAM_HAS_PROJECT);
				return;
			}
		}
		currentOrg.remove(true);
	}
	

	protected void handleFilterProjectTeam() {
		if(enableProjectTeamFilter){
			orgViewer.setFilters(new ViewerFilter[]{filter});
			filterProjectTeam.setImage(Resource.getImage(Resource.FILTER32));
		}else{
			orgViewer.setFilters(new ViewerFilter[]{});
			filterProjectTeam.setImage(Resource.getImage(Resource.FILTER_DISABLE32));
		}

		enableProjectTeamFilter = !enableProjectTeamFilter;
	}

	protected void handleEdit() {
		Shell shell = getEditorSite().getShell();
		DataUtil.editOBSItemUI(shell, currentOrg);

	}

	protected void handleCreateRole() {
		handlerCreate(IDBConstants.VALUE_OBS_ROLETYPE);
	}

	protected void handleCreateTeam() {
		handlerCreate(IDBConstants.VALUE_OBS_TEAMTYPE);
	}

	private void handlerCreate(String obsType) {
		int ok = DataUtil.createOBSItemUI(getSite().getShell(), currentOrg, obsType);

		if (ok == SingleObjectEditorDialog.OK) {
			orgViewer.refresh(currentOrg, false);
//			orgViewer.expandToLevel(currentOrg, AbstractTreeViewer.ALL_LEVELS);
		}
	}


	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if (event.getSelectionProvider() == orgViewer) {
			if (event == null || event.getSelection().isEmpty()) {
				currentOrg = null;
			} else {
				currentOrg = (CascadeObject) ((StructuredSelection) event.getSelection()).getFirstElement();
			}
		} else {
			if (event == null || event.getSelection().isEmpty()) {
				currentUser = null;
			} else {
				currentUser = (SingleObject) ((StructuredSelection) event.getSelection()).getFirstElement();
			}
		}
		updateButtons();
	}

	public boolean isRemoveableOrg(CascadeObject currentOrg2) {
		return DataUtil.isTeamObject(currentOrg) || DataUtil.isRoleObject(currentOrg) || DataUtil.isUserObject(currentOrg);
	}

	private void updateButtons() {
		if (currentOrg == null) {
			createTeam.setEnabled(false);
			createRole.setEnabled(false);
			edit.setEnabled(false);
			remove.setEnabled(false);
		} else {
			if (DataUtil.isTeamObject(currentOrg)) {
				// 如果当前选中的是组织
				createTeam.setEnabled(true);
				createRole.setEnabled(true);
				edit.setEnabled(true);
				remove.setEnabled(true);
			} else if (DataUtil.isRoleObject(currentOrg)) {
				// 如果当前选中的是角色
				createTeam.setEnabled(false);
				createRole.setEnabled(false);
				edit.setEnabled(true);
				remove.setEnabled(true);
			} else if (DataUtil.isUserObject(currentOrg)) {
				// 如果当前选中的是用户
				createTeam.setEnabled(false);
				createRole.setEnabled(false);
				edit.setEnabled(false);
				remove.setEnabled(false);
			} else if (DataUtil.isProjectTeamObject(currentOrg)) {
				// 如果当前选中的是用户
				createTeam.setEnabled(false);
				createRole.setEnabled(false);
				edit.setEnabled(false);
				remove.setEnabled(true);

			} else {
				createTeam.setEnabled(true);
				createRole.setEnabled(true);
				edit.setEnabled(false);
				remove.setEnabled(false);
			}
		}
	}

	@Override
	public boolean needUpdate() {
		return true;
	}

}
