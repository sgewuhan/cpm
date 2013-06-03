package com.sg.cpm.admin.auth;

import java.util.List;

import org.bson.types.ObjectId;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.WorkbenchImages;

import com.mongodb.BasicDBObject;
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
import com.sg.resource.Resource;
import com.sg.user.UserSessionContext;
import com.sg.widget.viewer.KeyNavigator;

public class AuthEditor extends AdminFunctionEditor implements ISelectionChangedListener {

	protected TreeViewer orgViewer;
	protected TableViewer targetUserViewer;
	protected TreeViewer tokenViewer;
	private DBCollection obsCollection;
	private QueryExpression authexp;
	private TreeViewer siteViewer;
	private BasicDBObject siteRoot;
	private DBCollection siteCollection;
	private TableViewer targetRoleViewer;

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		obsCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_ORG);
		siteCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_SITE);
		authexp = DBActivator.getQueryExpression(IDBConstants.EXP_QUERY_AUTH);


		super.init(site, input);
	}

	@Override
	public void createPartControl(Composite parent) {
		// 创建左右分割区
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);

		createSiteOBSTree(sashForm);

		createRightPanel(sashForm);

		sashForm.setWeights(new int[] { 2, 3 });

		update();
		
		super.createPartControl(parent);
	}

	private void createRightPanel(Composite parent) {
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);

		//上
		CTabFolder tabFolderTop = new CTabFolder(sashForm,SWT.TOP|SWT.BORDER);
//		tabFolder.setData(WidgetUtil.CUSTOM_VARIANT, "inEditor");

		createUserTargetList(tabFolderTop);

		createRoleTargetList(tabFolderTop);
		
		tabFolderTop.setSelection(0);
		//下
		CTabFolder tabFolderButtom = new CTabFolder(sashForm,SWT.TOP|SWT.BORDER);
		createAuthorityTable(tabFolderButtom);
		tabFolderButtom.setSelection(0);
		setupDND();

		sashForm.setWeights(new int[] { 3, 2 });
	}


	private void setupDND() {
		// 设置targetViewer为拖放源
		// 设置TokenViewer为拖放目标
		setDragSourceForSet(targetUserViewer.getControl());
		setDragSourceForSet(targetRoleViewer.getControl());
		setDropTargetForSet(tokenViewer.getControl());

		setDragSourceForUnset(tokenViewer.getControl());
		setDropTargetForUnset(targetUserViewer.getControl());
		setDropTargetForUnset(targetRoleViewer.getControl());
	}

	private void setDragSourceForSet(Control dragControl) {
		DragSource dragSource = new DragSource(dragControl, DND.DROP_MOVE);
		dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dragSource.addDragListener(new AuthTargetDrag(this));
	}

	private void setDropTargetForSet(Control dropControl) {
		DropTarget dropTarget = new DropTarget(dropControl, DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dropTarget.addDropListener(new AuthTokenDrop(this));
	}

	private void setDragSourceForUnset(Control dragControl) {
		DragSource dragSource = new DragSource(dragControl, DND.DROP_MOVE);
		dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dragSource.addDragListener(new AuthTokenDrag(this));
	}

	private void setDropTargetForUnset(Control dropControl) {
		DropTarget dropTarget = new DropTarget(dropControl, DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dropTarget.addDropListener(new AuthTargetDrop(this));
	}


	private void createRoleTargetList(CTabFolder parent) {
		CTabItem item = new CTabItem(parent, SWT.NONE);
//		item.setData(WidgetUtil.CUSTOM_VARIANT, "inEditor");
		item.setText(UIConstants.TEXT_ROLE_ELEMENT);

		targetRoleViewer = new TableViewer(parent, SWT.FULL_SELECTION);
		targetRoleViewer.setLabelProvider(new ILabelProvider() {

			@Override
			public void addListener(ILabelProviderListener listener) {
			}

			@Override
			public void dispose() {
			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			@Override
			public void removeListener(ILabelProviderListener listener) {
			}

			@Override
			public Image getImage(Object element) {
				if (!isRoleObject(element)) {
					return Resource.getImage(Resource.USER16);
				} else {
					return Resource.getImage(Resource.ROLE16);
				}
			}

			@Override
			public String getText(Object element) {
				if (isRoleObject(element)) {
					return (String) ((DBObject) element).get(IDBConstants.FIELD_DESC);
				} else {
					return DataUtil.getUserLable((DBObject) element);
				}
			}

		});
		targetRoleViewer.setContentProvider(ArrayContentProvider.getInstance());
		new KeyNavigator(targetRoleViewer);

		item.setControl(targetRoleViewer.getControl());
	}
	
	
	/**
	 * 显示站点下所有的用户和组织下的角色
	 * 
	 * @param parent
	 */
	private void createUserTargetList(CTabFolder parent) {
		CTabItem item = new CTabItem(parent, SWT.NONE);
//		item.setData(WidgetUtil.CUSTOM_VARIANT, "inEditor");
		
		item.setText(UIConstants.TEXT_USER_ELEMENT);

		targetUserViewer = new TableViewer(parent, SWT.FULL_SELECTION);
		targetUserViewer.setLabelProvider(new ILabelProvider() {

			@Override
			public void addListener(ILabelProviderListener listener) {
			}

			@Override
			public void dispose() {
			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			@Override
			public void removeListener(ILabelProviderListener listener) {
			}

			@Override
			public Image getImage(Object element) {
				if (!isRoleObject(element)) {
					return Resource.getImage(Resource.USER16);
				} else {
					return Resource.getImage(Resource.ROLE16);
				}
			}

			@Override
			public String getText(Object element) {
				if (isRoleObject(element)) {
					return (String) ((DBObject) element).get(IDBConstants.FIELD_DESC);
				} else {
					return DataUtil.getUserLable((DBObject) element);
				}
			}

		});
		targetUserViewer.setContentProvider(ArrayContentProvider.getInstance());
		new KeyNavigator(targetUserViewer);
		item.setControl(targetUserViewer.getControl());

	}

	private boolean isRoleObject(Object element) {
		if (element instanceof DBObject) {
			return IDBConstants.VALUE_OBS_ROLETYPE.equals(((DBObject) element).get(IDBConstants.FIELD_TEMPLATE_TYPE));
		}
		return false;
	}

	private void createAuthorityTable(CTabFolder parent) {
		CTabItem item = new CTabItem(parent, SWT.NONE);
		item.setText(UIConstants.TEXT_AUTH_SETTING);
		tokenViewer = new TreeViewer(parent, SWT.FULL_SELECTION);
		tokenViewer.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ISingleObject) {
					return ((ISingleObject) element).getText(IDBConstants.FIELD_DESC);

				} else if (element instanceof DBObject) {
					Object targetType = ((DBObject) element).get(IDBConstants.FIELD_TARGETTYPE);
					Object targetId = ((DBObject) element).get(IDBConstants.FIELD_TARGETID);
					if (IDBConstants.VALUE_OBS_USERTYPE.equals(targetType)) {// 是用户
						DBObject userData = DataUtil.getDataObject(IDBConstants.COLLECTION_USER, (ObjectId) targetId);
						return DataUtil.getUserLable(userData);
					} else {
						DBObject roleData = DataUtil.getDataObject(IDBConstants.COLLECTION_ORG, (ObjectId) targetId);
						return (String) roleData.get(IDBConstants.FIELD_DESC);
					}
				} else {
					return "";
				}
			}

			@Override
			public Image getImage(Object element) {
				if (element instanceof ISingleObject) {
					return WorkbenchImages.getImage(IWorkbenchGraphicConstants.IMG_OBJ_SIGNED_YES);
				} else {
					Object targetType = ((DBObject) element).get(IDBConstants.FIELD_TARGETTYPE);
					if (IDBConstants.VALUE_OBS_USERTYPE.equals(targetType)) {// 是用户
						return Resource.getImage(Resource.USER16);
					} else {
						return Resource.getImage(Resource.ROLE16);
					}
				}
			}

		});

		tokenViewer.setContentProvider(new ITreeContentProvider() {

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof CascadeObject) {
					return ((CascadeObject) inputElement).getChildren().toArray();
				}
				return null;
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				if (parentElement instanceof CascadeObject) {
					CascadeObject tokenObject = (CascadeObject) parentElement;
					if ("token".equals(tokenObject.getValue("type"))) {
						return getAuthList(tokenObject);
					}
				}
				return null;
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}

			@Override
			public boolean hasChildren(Object element) {
				Object[] children = getChildren(element);
				return children != null && children.length > 0;
			}

		});
		
		item.setControl(tokenViewer.getControl());
	}

	protected Object[] getAuthList(CascadeObject tokenObject) {
		Object tokenId = tokenObject.getValue("id");
		ObjectId contextId = tokenObject.getParent().getSystemId();
		// 查询authority 集合
		// 条件 contextId = contextid, tokenId = tokenId
		authexp.setParamValue(IDBConstants.FIELD_TOKENID, tokenId);
		authexp.setParamValue(IDBConstants.FIELD_CONTEXTID, contextId);
		authexp.setParamValue(IDBConstants.FIELD_AUTHVALUE, true);
		DBCursor cur = authexp.run();
		return cur.toArray().toArray();
	}

	/**
	 * @param parent
	 */
	private void createSiteOBSTree(Composite parent) {

		CTabFolder tab = new CTabFolder(parent, SWT.TOP|SWT.BORDER);
//		tab.setData(WidgetUtil.CUSTOM_VARIANT, "inEditor");
		
		createSiteViewer(tab);

		createOrgViewer(tab);
		
		tab.setSelection(0);

	}

	private void createSiteViewer(CTabFolder parent) {
		CTabItem item = new CTabItem(parent, SWT.NONE);
//		item.setData(WidgetUtil.CUSTOM_VARIANT, "inEditor");
		item.setText(UIConstants.TEXT_SITE);
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new FillLayout());

		siteViewer = new TreeViewer(panel, SWT.FULL_SELECTION);
		siteViewer.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public Image getImage(Object element) {
				return Resource.getImage(Resource.SITE16);
			}

			@Override
			public String getText(Object element) {
				return (String) ((DBObject) element).get(IDBConstants.FIELD_DESC);
			}

		});

		siteViewer.setContentProvider(new ITreeContentProvider() {

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public Object[] getElements(Object inputElement) {
				return (Object[]) inputElement;
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				if(parentElement instanceof DBObject){
					ObjectId parentId = (ObjectId) ((DBObject) parentElement).get(IDBConstants.FIELD_SYSID);
					DBCursor cur = siteCollection.find(new BasicDBObject().append(IDBConstants.FIELD_SITEPARENT, parentId));
					return cur.toArray().toArray();
				}
				return null;
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}

			@Override
			public boolean hasChildren(Object element) {
				Object[] children = getChildren(element);
				return children!=null&&children.length>0;
			}

		});
		siteViewer.addPostSelectionChangedListener(this);
		siteViewer.setAutoExpandLevel(TreeViewer.ALL_LEVELS);

		item.setControl(panel);
	}
	

	@Override
	public void update() {
		siteRoot = new BasicDBObject().append(IDBConstants.FIELD_SYSID, UserSessionContext.getSession().getSiteContextId())
				.append(IDBConstants.FIELD_DESC, UserSessionContext.getSession().getSiteContextName())
				.append(IDBConstants.FIELD_OBSPARENT, null);
		if(siteRoot != null){
			siteViewer.setInput(new Object[] { siteRoot });
			orgViewer.setInput(new DBObject[] { siteRoot });
			orgViewer.getTree().getColumn(0).pack();
		}else{
			siteViewer.setInput(null);
			orgViewer.setInput(null);
		}
	}

	private void createOrgViewer(CTabFolder parent) {
		CTabItem item = new CTabItem(parent, SWT.NONE);
//		item.setData(WidgetUtil.CUSTOM_VARIANT, "inEditor");

		item.setText(UIConstants.TEXT_OBS);
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new FillLayout());

		orgViewer = new TreeViewer(panel, SWT.FULL_SELECTION);

		TreeViewerColumn column = new TreeViewerColumn(orgViewer, SWT.NONE);
		column.getColumn().setWidth(350);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public Image getImage(Object element) {
				Object type = ((DBObject) element).get(IDBConstants.FIELD_TEMPLATE_TYPE);
				if (type == null) {
					return Resource.getImage(Resource.SITE16);
				} else {
					return Resource.getImage(Resource.TEAM16);
				}
			}

			@Override
			public String getText(Object element) {
				return (String) ((DBObject) element).get(IDBConstants.FIELD_DESC);
			}

		});
		orgViewer.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
		orgViewer.setContentProvider(new ITreeContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public void dispose() {
			}

			@Override
			public boolean hasChildren(Object element) {
				DBCursor cur = getObsTree((DBObject) element);
				return cur.size() > 0;
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}

			@Override
			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof Object[]) {
					return (Object[]) inputElement;
				} else {
					return getChildren(inputElement);
				}
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				DBCursor cur = getObsTree((DBObject) parentElement);
				return cur.toArray().toArray();
			}
		});
		
		new KeyNavigator(orgViewer);
		orgViewer.addPostSelectionChangedListener(this);
		item.setControl(panel);
	}

	protected DBCursor getObsTree(DBObject master) {
		DBObject ref = new BasicDBObject().append(IDBConstants.FIELD_OBSPARENT, master.get(IDBConstants.FIELD_SYSID)).append(
				IDBConstants.FIELD_TEMPLATE_TYPE, IDBConstants.VALUE_OBS_TEAMTYPE);

		DBObject keys = new BasicDBObject().append(IDBConstants.FIELD_TEMPLATE_TYPE, 1).append(IDBConstants.FIELD_DESC, 1);
		return obsCollection.find(ref, keys);
	}

	public void updateTokenViewer() {
		IStructuredSelection selection = (IStructuredSelection) tokenViewer.getSelection();
		if (selection == null || selection.isEmpty()) {
			return;
		} else {
			// 保存token的当前选择状态
			TreeItem[] selected = tokenViewer.getTree().getSelection();
			if (selected.length < 1) {
				return;
			}
			TreeItem item = selected[0].getParentItem();
			Object parentData = item.getData();
			tokenViewer.refresh(parentData);

		}
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		// 站点树上的节点变化，需要更改权限列表的对象
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		if (selection == null || selection.isEmpty()) {
			tokenViewer.setInput(null);
		} else {

			// 设置权限列表
			DBObject dbo = (DBObject) selection.getFirstElement();
			ObjectId teamOrSiteId = (ObjectId) dbo.get(IDBConstants.FIELD_SYSID);
			String type = (String) dbo.get(IDBConstants.FIELD_TEMPLATE_TYPE);
			if (type == null) {// 站点
				CascadeObject root = DBActivator.getCascadeObject(IDBConstants.EXP_CASCADE_AUTH_SITE);
				root.setParamValue(IDBConstants.FIELD_SYSID, teamOrSiteId);
				tokenViewer.setInput(root);
			} else {// 组织
				CascadeObject root = DBActivator.getCascadeObject(IDBConstants.EXP_CASCADE_AUTH_ORG);
				root.setParamValue(IDBConstants.FIELD_SYSID, teamOrSiteId);
				tokenViewer.setInput(root);
			}


			if (type == null) {// 站点
				List<DBObject> userlist = DataUtil.getUserOfSite(teamOrSiteId);
				targetUserViewer.setInput(userlist);
			} else {
				// 设置用户和角色
				List<DBObject> userlist = DataUtil.getUserOfSite(UserSessionContext.getSession().getSiteContextId());
				List<DBObject> roleList = DataUtil.getRoleOfTeam(teamOrSiteId);
				targetUserViewer.setInput(userlist);
				targetRoleViewer.setInput(roleList);
			}
		}
	}

	@Override
	public boolean needUpdate() {
		return true;
	}


}
