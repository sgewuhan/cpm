package com.sg.cpm.admin.projecttemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.drools.KnowledgeBase;
import org.drools.definition.process.Node;
import org.drools.definition.process.Process;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.jbpm.workflow.core.node.HumanTaskNode;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.sg.common.BusinessService;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.NodeAssignment;
import com.sg.common.service.ServiceException;
import com.sg.common.ui.ProcessSelectorDialog;
import com.sg.common.ui.UIConstants;
import com.sg.cpm.admin.AdminFunctionEditor;
import com.sg.db.DBActivator;
import com.sg.db.Util;
import com.sg.db.expression.query.QueryExpression;
import com.sg.db.model.CascadeObject;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.SingleObject;
import com.sg.resource.Resource;
import com.sg.user.UserSessionContext;
import com.sg.widget.Widget;
import com.sg.widget.configuration.EditorConfiguration;
import com.sg.widget.dialog.ISingleObjectEditorDialogCallback;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.dialog.SingleObjectEditorDialogCallback;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.SingleObjectEditorInput;
import com.sg.widget.viewer.tableviewer.QueryTableViewer;
import com.sg.widget.viewer.treeviewer.QueryTreeViewer;

public class ProjectTemplateEditor extends AdminFunctionEditor implements ISelectionChangedListener {

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

			String jsonOBS = (String) event.data;
			if (Util.isNullorEmpty(jsonOBS)) {
				return;
			}
			if (jsonOBS.equals("#")) {
				return;
			}
			if (event.item == null || event.item.isDisposed()) {
				return;
			}
			CascadeObject target = (CascadeObject) event.item.getData();
			if (target == null)
				return;

			// 设定参与者指派
			Object currentValue = target.getValue(IDBConstants.FIELD_PARTICIPATE);
			BasicDBList list;
			if (currentValue instanceof BasicDBList) {
				list = (BasicDBList) currentValue;
			} else {
				list = new BasicDBList();
			}
			/*
			 * java.lang.ClassCastException: java.lang.Integer cannot be cast to com.mongodb.DBObject
				at com.sg.cpm.admin.projecttemplate.ProjectTemplateEditor$DropTargetForSetListener.drop(ProjectTemplateEditor.java:125)
			 */
			DBObject assignment = DataUtil.getRefData((DBObject) JSON.parse(jsonOBS), IDBConstants.DATA_OBS_BASIC);
			list.add(assignment);
			target.setValue(IDBConstants.FIELD_PARTICIPATE, list);
			target.save();

			wbsViewer.update(target, null);
		}

		public void dropAccept(final DropTargetEvent event) {

		}

	}

	class DragSourceForSetListener implements DragSourceListener {

		public void dragFinished(final DragSourceEvent event) {

		}

		public void dragSetData(final DragSourceEvent event) {

			if (DataUtil.isRoleTemplateObject(currentObject) || DataUtil.isTeamTemplateObject(currentObject)) {
				DBObject dbo = new BasicDBObject();
				dbo.put(IDBConstants.FIELD_SYSID, currentObject.getValue(IDBConstants.FIELD_SYSID));
				dbo.put(IDBConstants.FIELD_DESC, currentObject.getValue(IDBConstants.FIELD_DESC));
				event.data = JSON.serialize(dbo);
			} else {
				event.data = "#";// 必须要给一个字符串，否则要在javatonative出错。
			}
		}

		public void dragStart(final DragSourceEvent event) {

			// 当选择的是根，不可以拖动，理论上可以通过event.doit来控制哪些可以拖动，但这个在RAP中不起作用
			if (!DataUtil.isRoleTemplateObject(currentObject) && !DataUtil.isTeamTemplateObject(currentObject)) {
				event.doit = false;
			}
		}
	}

	class DropTargetForUnsetListener implements DropTargetListener {

		public void dragEnter(final DropTargetEvent event) {

			// event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL |
			// DND.FEEDBACK_EXPAND;
		}

		public void dragLeave(final DropTargetEvent event) {

		}

		public void dragOperationChanged(final DropTargetEvent event) {

		}

		public void dragOver(final DropTargetEvent event) {

		}

		public void drop(final DropTargetEvent event) {

			String id = (String) event.data;
			if (id.equals("#")) {
				return;
			}

			CascadeObject wbsItem = getCurrentWBS();
			if (wbsItem == null) {
				return;
			}
			wbsItem.setValue(IDBConstants.FIELD_PARTICIPATE, null);
			wbsItem.save();

			wbsViewer.update(wbsItem, null);
		}

		public void dropAccept(final DropTargetEvent event) {

		}

	}

	class DragSourceForUnsetListener implements DragSourceListener {

		public void dragFinished(final DragSourceEvent event) {

		}

		public void dragSetData(final DragSourceEvent event) {

			if (DataUtil.isProjectTemplateObject(currentObject) || DataUtil.isWorkTemplateObject(currentObject)
					|| DataUtil.isDeliveryTemplateObject(currentObject)) {
				event.data = currentObject.getValue(IDBConstants.FIELD_SYSID).toString();
			} else {
				event.data = "#";// 必须要给一个字符串，否则要在javatonative出错。
			}
		}

		public void dragStart(final DragSourceEvent event) {

			// 当选择的是根，不可以拖动，理论上可以通过event.doit来控制哪些可以拖动，但这个在RAP中不起作用
			if (!DataUtil.isProjectTemplateObject(currentObject) && !DataUtil.isWorkTemplateObject(currentObject)
					&& !DataUtil.isDeliveryTemplateObject(currentObject)) {
				event.doit = false;
			}
		}
	}

	private QueryTableViewer projectTemplateViewer;

	private QueryTreeViewer wbsViewer;

	private Button createProjectTemplate;

	private Button createWorkTemplate;

	private Button createDeliveryTemplate;

	private Button createTeamTemplate;

	private Button createRoleTemplate;

	private Button edit;

	private Button remove;

	private Button activateTemplateButton;

	private QueryTreeViewer obsViewer;

	private ISingleObject currentObject = null;

	private TableViewer processViewer;

	private CTabItem processLabel;

	private Button setWorkflowButton;

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

		createProjectTemplateList(sashForm);

		SashForm middle = new SashForm(sashForm, SWT.VERTICAL);

		createWBSTemplate(middle);
		createOBSTemplate(middle);
		middle.setWeights(new int[] { 1, 1 });

		createWorkflowTemplate(sashForm);
		sashForm.setWeights(new int[] { 2, 3, 3 });

		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		updateButtons();

		super.createPartControl(parent);

	}

	private void createProjectTemplateList(Composite parent) {

		CTabFolder tabFolder = new CTabFolder(parent, SWT.TOP | SWT.BORDER);
		CTabItem cti = new CTabItem(tabFolder, SWT.NONE);

		cti.setText(UIConstants.TEXT_PROJECT_TEMPLATE);

		projectTemplateViewer = new QueryTableViewer(tabFolder, SWT.VIRTUAL | SWT.FULL_SELECTION,
				Widget.getQueryTableViewerConfiguration(UIConstants.VIEWER_TABLE_PROJECT_TEMPLATE)) {

			@Override
			protected boolean initLoad() {

				return false;
			}
		};

		update();

		projectTemplateViewer.addPostSelectionChangedListener(this);

		cti.setControl(projectTemplateViewer.getControl());
		tabFolder.setSelection(0);
	}

	@Override
	public void update() {

		QueryExpression exp = projectTemplateViewer.createExpression();

		// ****************************************************************************
		// 站点管理员可以看到站点下所有的模板
		// //取得editor上传入的上下文
		// FunctionEditorInput input = (FunctionEditorInput) getEditorInput();
		// IAuthorityResponse auth = input.getAuth();
		// BasicDBList contextList = auth.getContextList();

		// if(contextList!=null){
		// exp.setParamValue(IDBConstants.PARAM_INPUT_OBSPARENT, contextList);
		// }

		exp.setParamValue(IDBConstants.PARAM_INPUT_SITEPARENT, UserSessionContext.getSession().getSiteContextId());

		projectTemplateViewer.updateInputData();

	}

	private void createWBSTemplate(Composite parent) {

		CTabFolder tabFolder = new CTabFolder(parent, SWT.TOP | SWT.BORDER);
		CTabItem cti = new CTabItem(tabFolder, SWT.NONE);

		cti.setText(UIConstants.TEXT_WBS_WITH_DOC);

		wbsViewer = new QueryTreeViewer(tabFolder, SWT.VIRTUAL | SWT.FULL_SELECTION,
				Widget.getQueryTreeViewerConfiguration(UIConstants.VIEWER_TREE_WBS_TEMPLATE));

		wbsViewer.addPostSelectionChangedListener(this);
		wbsViewer.setAutoExpandLevel(QueryTreeViewer.ALL_LEVELS);
		setDropTargetForSet(wbsViewer.getControl());
		setDragSourceForUnset(wbsViewer.getControl());

		cti.setControl(wbsViewer.getControl());
		tabFolder.setSelection(0);
	}

	private void createOBSTemplate(Composite parent) {

		CTabFolder tabFolder = new CTabFolder(parent, SWT.TOP | SWT.BORDER);
		CTabItem cti = new CTabItem(tabFolder, SWT.NONE);
		cti.setText(UIConstants.TEXT_OBS_WITH_ROLE);
		obsViewer = new QueryTreeViewer(tabFolder, SWT.VIRTUAL | SWT.FULL_SELECTION,
				Widget.getQueryTreeViewerConfiguration(UIConstants.VIEWER_TREE_OBS_TEMPLATE));

		obsViewer.addPostSelectionChangedListener(this);
		obsViewer.setAutoExpandLevel(QueryTreeViewer.ALL_LEVELS);
		setDragSourceForSet(obsViewer.getControl());
		setDropTargetForUnset(obsViewer.getControl());

		cti.setControl(obsViewer.getControl());
		tabFolder.setSelection(0);
	}

	private void createWorkflowTemplate(Composite parent) {

		CTabFolder tabFolder = new CTabFolder(parent, SWT.TOP | SWT.BORDER);
		processLabel = new CTabItem(tabFolder, SWT.NONE);
		processLabel.setText(UIConstants.TEXT_WORKFLOWSELETE);

		// 创建工作流节点定义表格
		processViewer = new TableViewer(tabFolder, SWT.FULL_SELECTION);
		processViewer.setContentProvider(ArrayContentProvider.getInstance());
		TableViewerColumn column = new TableViewerColumn(processViewer, SWT.LEFT);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public Image getImage(Object element) {

				CascadeObject wbsItem = getCurrentWBS();
				try {
					NodeAssignment nass = BusinessService.getWorkflowService().getNodeAssignment(wbsItem.getData(), (HumanTaskNode) element);
					if (nass.isAlreadyAssignment()) {
						return Resource.getImage(Resource.ACTVITI_1_16);
					} else if (nass.isNeedAssignment()) {
						return Resource.getImage(Resource.ACTVITI_2_16);
					} else if (nass.isNotNeedAssignment()) {
						return Resource.getImage(Resource.ACTVITI_3_16);
					}
				} catch (ServiceException e) {
					return e.getIcon();
				}

				return null;
			}

			@Override
			public String getText(Object element) {

				return ((Node) element).getName();

			}

		});

		column = new TableViewerColumn(processViewer, SWT.LEFT);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				CascadeObject wbsItem = getCurrentWBS();
				try {
					NodeAssignment nass = BusinessService.getWorkflowService().getNodeAssignment(wbsItem.getData(), (HumanTaskNode) element);

					if (nass.isAlreadyAssignment()) {
						ObjectId assignmentId = nass.getAssignmentId();
						return BusinessService.getOrganizationService().getOBSItemLabel(assignmentId);
					} else if (nass.isNeedAssignment()) {
						return UIConstants.TEXT_NEEDASSIGN;
					} else if (nass.isNotNeedAssignment()) {
						if (nass.isRuleAssignment()) {
							return nass.getRuleAssignmentName() + UIConstants.TEXT_RULE_ASSIGNMENT;
						} else {
							return UIConstants.TEXT_NOTNEEDASSIGN;
						}
					}
				} catch (ServiceException e) {
					return e.getMessage();
				}
				return "";
			}

			@Override
			public Color getForeground(Object element) {

				try {

					CascadeObject wbsItem = getCurrentWBS();
					NodeAssignment nass = BusinessService.getWorkflowService().getNodeAssignment(wbsItem.getData(), (HumanTaskNode) element);
					if (nass.isNotNeedAssignment()) {
						if (nass.isRuleAssignment()||nass.isDyanmic()) {
							return getEditorSite().getShell().getDisplay().getSystemColor(SWT.COLOR_GRAY);
						}
					}

				} catch (ServiceException e) {

				}
				return super.getForeground(element);
			}

		});

		processLabel.setControl(processViewer.getControl());
		tabFolder.setSelection(0);
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

		activateTemplateButton = new Button(toolbar, SWT.PUSH);
		activateTemplateButton.setImage(Resource.getImage(Resource.CONNECT32));
		activateTemplateButton.setToolTipText(UIConstants.TEXT_ACTIVATE_TEMPLATE);
		activateTemplateButton.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		activateTemplateButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				handleActivateTemplate();
			}

		});

		createProjectTemplate = new Button(toolbar, SWT.PUSH);
		createProjectTemplate.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		createProjectTemplate.setImage(Resource.getImage(Resource.CREATE_PROJECTTEMPLATE32));
		createProjectTemplate.setToolTipText(UIConstants.TEXT_CREATE_PROJECT_TEMPLATE);
		createProjectTemplate.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				handleCreateProjectTemplate();
			}
		});

		createWorkTemplate = new Button(toolbar, SWT.PUSH);
		createWorkTemplate.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		createWorkTemplate.setImage(Resource.getImage(Resource.CREATE_WORK32));
		createWorkTemplate.setToolTipText(UIConstants.TEXT_CREATE_WORK_TEMPLATE);
		createWorkTemplate.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				handleCreateWorkTemplate();
			}
		});

		createDeliveryTemplate = new Button(toolbar, SWT.PUSH);
		createDeliveryTemplate.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		createDeliveryTemplate.setImage(Resource.getImage(Resource.CREATE_DELIVERY32));
		createDeliveryTemplate.setToolTipText(UIConstants.TEXT_CREATE_DOC_TEMPLATE);
		createDeliveryTemplate.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				handleCreateDeliveryTemplate();
			}
		});

		createTeamTemplate = new Button(toolbar, SWT.PUSH);
		createTeamTemplate.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		createTeamTemplate.setImage(Resource.getImage(Resource.CREATE_TEAM32));
		createTeamTemplate.setToolTipText(UIConstants.TEXT_CREATE_TEAM_TEMPLATE);
		createTeamTemplate.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				handleCreateTeamTemplate();
			}
		});

		createRoleTemplate = new Button(toolbar, SWT.PUSH);
		createRoleTemplate.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		createRoleTemplate.setImage(Resource.getImage(Resource.CREATE_ROLE32));
		createRoleTemplate.setToolTipText(UIConstants.TEXT_CREATE_ROLE_TEMPLATE);
		createRoleTemplate.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				handleCreateRoleTemplate();
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

		setWorkflowButton = new Button(toolbar, SWT.PUSH);
		setWorkflowButton.setImage(Resource.getImage(Resource.WORKFLOW32));
		setWorkflowButton.setToolTipText(UIConstants.TEXT_WORKFLOWSELETE);
		setWorkflowButton.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		setWorkflowButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				handleSetWorkflow();
			}

		});
	}

	private void updateButtons() {

		edit.setEnabled(currentObject != null);
		remove.setEnabled(currentObject != null);

		CascadeObject currentWBS = getCurrentWBS();
		if (currentWBS == null) {
			createWorkTemplate.setEnabled(false);
			createDeliveryTemplate.setEnabled(false);
			setWorkflowButton.setEnabled(false);
		} else {
			createWorkTemplate.setEnabled(!DataUtil.isDeliveryTemplateObject(currentWBS));
			createDeliveryTemplate.setEnabled(DataUtil.isWorkTemplateObject(currentWBS));
			setWorkflowButton.setEnabled(DataUtil.isWorkTemplateObject(currentWBS));
		}

		CascadeObject currentOBS = getCurrentOBS();
		if (currentOBS == null) {
			createRoleTemplate.setEnabled(false);
			createTeamTemplate.setEnabled(false);
		} else {
			createTeamTemplate.setEnabled(!DataUtil.isRoleTemplateObject(currentOBS));
			createRoleTemplate.setEnabled(true);
		}
	}

	private void setViewerInput(Object projectTemplateId, QueryTreeViewer viewer) {

		CascadeObject root = viewer.createExpression();
		root.setParamValue(IDBConstants.FIELD_SYSID, projectTemplateId);
		viewer.setInput(root);
	}

	public CascadeObject getCurrentWBS() {

		IStructuredSelection ssel = wbsViewer.getSelection();
		if (ssel != null && !ssel.isEmpty()) {
			return (CascadeObject) ssel.getFirstElement();
		}
		return null;
	}

	public CascadeObject getCurrentOBS() {

		IStructuredSelection ssel = obsViewer.getSelection();
		if (ssel != null && !ssel.isEmpty()) {
			return (CascadeObject) ssel.getFirstElement();
		}
		return null;
	}

	public SingleObject getCurrentProjectTemplate() {

		IStructuredSelection ssel = projectTemplateViewer.getSelection();
		if (ssel != null && !ssel.isEmpty()) {
			return (SingleObject) ssel.getFirstElement();
		}
		return null;
	}

	protected void handleCreateProjectTemplate() {

		// 为模板添加站点
		ISingleObjectEditorDialogCallback call = new SingleObjectEditorDialogCallback() {

			@Override
			public boolean saveBefore(ISingleObjectEditorInput input) {

				input.getInputData().setValue(IDBConstants.FIELD_SITEPARENT, UserSessionContext.getSession().getSiteContextId(), null, false);
				return super.saveBefore(input);
			}
		};
		SingleObjectEditorDialog soed = SingleObjectEditorDialog.getInstance(getSite().getShell(), UIConstants.EDITOR_PROJECT_TEMPLATE, null, call, true);
		int ok = soed.open();
		if (ok == SingleObjectEditorDialog.OK) {
			ISingleObject so = soed.getInputData();
			projectTemplateViewer.add(so);
			projectTemplateViewer.setSelection(new StructuredSelection(so));
		}
	}

	protected void handleCreateDeliveryTemplate() {

		createWBSTemplate(UIConstants.EDITOR_DELIVERY_TEMPLATE, IDBConstants.FIELD_DBSSEQ, IDBConstants.VALUE_WBS_DOCUMENT_TYPE);
	}

	protected void handleCreateWorkTemplate() {

		createWBSTemplate(UIConstants.EDITOR_WORK_TEMPLATE, IDBConstants.FIELD_WBSSEQ, IDBConstants.VALUE_WBS_TASK_TYPE);
	}

	private void createWBSTemplate(String editorConfId, String seqFieldName, Object wbsType) {

		EditorConfiguration editorConfiguration = Widget.getSingleObjectEditorConfiguration(editorConfId);

		CascadeObject parent = getCurrentWBS();
		SingleObject so = new SingleObject(DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_WORK_TEMPLATE));

		// 给出wbsparent的值
		ObjectId parentId = (ObjectId) parent.getValue(IDBConstants.FIELD_SYSID);
		so.setValue(IDBConstants.FIELD_WBSPARENT, parentId, null, false);
		// 获得当前最大的seq
		QueryExpression exp = DBActivator.getQueryExpression(IDBConstants.EXP_QUERY_WORKTEMPLATE_BY_WBSPARENT);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(IDBConstants.FIELD_WBSPARENT, parentId);
		parameters.put(IDBConstants.FIELD_TEMPLATE_TYPE, IDBConstants.VALUE_WBS_TASK_TYPE);
		exp.passParamValueMap(parameters);
		exp.setSortFieldsFromString(seqFieldName + ",-1");
		exp.setSkipAndLimit(null, "1");
		exp.setReturnFieldsFromString(seqFieldName + ",1");
		DBCursor cursor = exp.run();
		int nextVal = 1;// 任务序号从1开始
		if (cursor.hasNext()) {
			DBObject dbo = cursor.next();
			Object maxSeq = dbo.get(IDBConstants.FIELD_WBSSEQ);
			if (maxSeq instanceof Number) {
				nextVal = ((Number) maxSeq).intValue() + 1;
			}
		}
		so.setValue(seqFieldName, nextVal, null, false);
		// 如果没有wbs代码的，wbs为9999,确保他们能够在排序时排到任务的最后
		if (!seqFieldName.equals(IDBConstants.FIELD_WBSSEQ)) {
			so.setValue(IDBConstants.FIELD_WBSSEQ, 9999, null, false);
		}
		so.setValue(IDBConstants.FIELD_TEMPLATE_TYPE, wbsType, null, false);
		SingleObjectEditorInput editInput = new SingleObjectEditorInput(editorConfiguration, so);

		// create
		SingleObjectEditorDialog soed = SingleObjectEditorDialog.getInstance(getSite().getShell(), editorConfId, editInput, null, true);
		int ok = soed.open();
		if (ok == SingleObjectEditorDialog.OK) {
			ISingleObject data = soed.getInputData();
			// reload
			parent.createChild(IDBConstants.EXP_CASCADE_SO_WBS_TEMPLATE, data.getData(),
					DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_WORK_TEMPLATE));
			parent.sortChildren(new String[] { IDBConstants.FIELD_WBSSEQ });
			wbsViewer.refresh(parent, false);
			wbsViewer.expandToLevel(parent, 1);
		}
	}

	protected void handleCreateRoleTemplate() {

		createOBSTemplate(UIConstants.EDITOR_ROLE_TEMPLATE, IDBConstants.VALUE_OBS_ROLETYPE);
	}

	protected void handleCreateTeamTemplate() {

		createOBSTemplate(UIConstants.EDITOR_TEAM_TEMPLATE, IDBConstants.VALUE_OBS_TEAMTYPE);
	}

	private void createOBSTemplate(String editorConfId, Object wbsType) {

		EditorConfiguration editorConfiguration = Widget.getSingleObjectEditorConfiguration(editorConfId);

		CascadeObject parent = getCurrentOBS();
		SingleObject so = new SingleObject(DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_ORG_TEMPLATE));

		// 给出wbsparent的值
		ObjectId parentId = (ObjectId) parent.getValue(IDBConstants.FIELD_SYSID);
		so.setValue(IDBConstants.FIELD_OBSPARENT, parentId, null, false);

		so.setValue(IDBConstants.FIELD_TEMPLATE_TYPE, wbsType, null, false);
		SingleObjectEditorInput editInput = new SingleObjectEditorInput(editorConfiguration, so);

		// create
		SingleObjectEditorDialog soed = SingleObjectEditorDialog.getInstance(getSite().getShell(), editorConfId, editInput, null, true);
		int ok = soed.open();
		if (ok == SingleObjectEditorDialog.OK) {
			ISingleObject data = soed.getInputData();
			// reload
			parent.createChild(IDBConstants.EXP_CASCADE_SO_OBS_TEMPLATE, data.getData(),
					DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_ORG_TEMPLATE));
			obsViewer.refresh(parent, false);
			obsViewer.expandToLevel(parent, 1);
		}
	}

	protected void handleEdit() {

		if (currentObject == null)
			return;
		Shell shell = getEditorSite().getShell();

		SingleObjectEditorInput editInput = new SingleObjectEditorInput(currentObject);

		if (DataUtil.isProjectTemplateObject(currentObject)) {// 选择的是项目模板
			SingleObjectEditorDialog.OPEN(shell, UIConstants.EDITOR_PROJECT_TEMPLATE, editInput, null, true);
		} else if (DataUtil.isWorkTemplateObject(currentObject)) {
			SingleObjectEditorDialog.OPEN(shell, UIConstants.EDITOR_WORK_TEMPLATE, editInput, null, true);
		} else if (DataUtil.isDeliveryTemplateObject(currentObject)) {
			SingleObjectEditorDialog.OPEN(shell, UIConstants.EDITOR_DELIVERY_TEMPLATE, editInput, null, true);
		} else if (DataUtil.isTeamTemplateObject(currentObject)) {
			SingleObjectEditorDialog.OPEN(shell, UIConstants.EDITOR_ROLE_TEMPLATE, editInput, null, true);
		} else if (DataUtil.isRoleTemplateObject(currentObject)) {
			SingleObjectEditorDialog.OPEN(shell, UIConstants.EDITOR_TEAM_TEMPLATE, editInput, null, true);
		}
	}

	protected void handleRemove() {

		/*
		 * 这个地方没有考虑垃圾数据的产生，删除项目模板后，需要级联删除对应的任务交付物等
		 */
		if (DataUtil.isProjectTemplateObject(currentObject)) {// 选择的是项目模板
			boolean ok = MessageDialog.openQuestion(getSite().getShell(), UIConstants.TEXT_REMOVE_PROJECT, "" + currentObject
					+ UIConstants.MESSAGE_QUESTION_DELETE);
			if (!ok)
				return;
			currentObject.remove();
			projectTemplateViewer.setSelection(new StructuredSelection());
		} else if (DataUtil.isWorkTemplateObject(currentObject)) {
			handleRemoveWBS();
		} else if (DataUtil.isDeliveryTemplateObject(currentObject)) {
			handleRemoveWBS();
		} else if (DataUtil.isTeamTemplateObject(currentObject)) {
			handleRemoveOBS();
		} else if (DataUtil.isRoleTemplateObject(currentObject)) {
			handleRemoveOBS();
		}
	}

	protected void handleRemoveWBS() {

		CascadeObject element = getCurrentWBS();
		if (DataUtil.isWorkTemplateObject(element)) {
			boolean ok = MessageDialog.openQuestion(getSite().getShell(), UIConstants.TEXT_REMOVE_WORK, "" + element + UIConstants.MESSAGE_QUESTION_DELETE);
			if (!ok)
				return;
		} else if (DataUtil.isDeliveryTemplateObject(element)) {
			boolean ok = MessageDialog.openQuestion(getSite().getShell(), UIConstants.TEXT_REMOVE_DOC, "" + element + UIConstants.MESSAGE_QUESTION_DELETE);
			if (!ok)
				return;
		}
		// 下兄弟任务序号调整
		CascadeObject parent = element.getParent();
		int seq = ((Number) element.getValue(IDBConstants.FIELD_WBSSEQ)).intValue();

		List<CascadeObject> children = parent.getChildren();
		int index = children.indexOf(element);
		for (int i = index + 1; i < children.size(); i++) {
			CascadeObject child = children.get(i);
			if (DataUtil.isWorkTemplateObject(child)) {// 只调整任务的，不调整交付物的
				child.setValue(IDBConstants.FIELD_WBSSEQ, seq++);
				child.save();
			}
		}

		element.remove(true);

		parent.loadChildren();
		wbsViewer.refresh(parent, false);
	}

	protected void handleRemoveOBS() {

		CascadeObject element = getCurrentOBS();
		CascadeObject parent = element.getParent();
		if (DataUtil.isTeamTemplateObject(element)) {
			boolean ok = MessageDialog.openQuestion(getSite().getShell(), UIConstants.TEXT_REMOVE_TEAM, "" + element + UIConstants.MESSAGE_QUESTION_DELETE);
			if (!ok)
				return;

		} else if (DataUtil.isRoleTemplateObject(element)) {
			boolean ok = MessageDialog.openQuestion(getSite().getShell(), UIConstants.TEXT_REMOVE_ROLE, "" + element + UIConstants.MESSAGE_QUESTION_DELETE);
			if (!ok)
				return;

		}
		element.remove(true);
		parent.loadChildren();
		obsViewer.refresh(parent, false);
	}

	protected void handleSetWorkflow() {

		CascadeObject currentwbs = getCurrentWBS();
		SingleObject currentTemplate = null;
		if (DataUtil.isWorkTemplateObject(currentwbs)) {
			currentTemplate = currentwbs;
		}
		// 选择站点内的工作流

		// 判断当前的工作模板是否已经关联了工作流
		if (currentTemplate == null) {
			return;
		}
		String orignalProcessId = (String) currentTemplate.getValue(IDBConstants.FIELD_PROCESS_DEFINITION_ID);
		// 如果关联了工作流需要提示
		if (orignalProcessId != null) {
			boolean yes = MessageDialog.openQuestion(getSite().getShell(), UIConstants.TEXT_WORKFLOWSELETE, UIConstants.MESSAGE_OVERIDE_WORKFLOWDEFINITION);
			if (!yes) {
				return;
			}
		}
		// 如果没有关联工作流，给出一个工作流对象的选择框
		ProcessSelectorDialog d = new ProcessSelectorDialog(getSite().getShell());
		Process selectedProcess = null;
		int ok = d.open();
		if (ok == Window.OK) {
			selectedProcess = d.getSelection();
			if (selectedProcess != null) {
				currentTemplate.setValue(IDBConstants.FIELD_PROCESS_DEFINITION_ID, selectedProcess.getId());
				currentTemplate.save();
				updateProcessViewer();
			} else {
				if (orignalProcessId != null) {
					boolean yes = MessageDialog.openQuestion(getSite().getShell(), UIConstants.TEXT_WORKFLOWSELETE,
							UIConstants.MESSAGE_OVERIDE_WORKFLOWDEFINITION_WITHNULL);
					if (yes) {
						currentTemplate.setValue(IDBConstants.FIELD_PROCESS_DEFINITION_ID, null);// 清除流程绑定
						currentTemplate.setValue(IDBConstants.FIELD_WORK_PROCESS_ASSINGMENT_DEFINITION, null);// 清楚任务指派
						currentTemplate.save();
						updateProcessViewer();
					}
				}
			}
		}

	}

	protected void handleActivateTemplate() {

		SingleObject currentTemplate = getCurrentProjectTemplate();

		if (currentTemplate == null) {
			return;
		}

		Boolean activate = Boolean.TRUE.equals(currentTemplate.getValue(IDBConstants.FIELD_ACTIVATE));

		if (!activate) {
			try {
				BusinessService.getWorkflowService().activateProjectTemplate(currentTemplate.getData());
			} catch (Exception e) {
				MessageDialog.openWarning(getEditorSite().getShell(), UIConstants.TEXT_ACTIVATE_TEMPLATE, e.getMessage());
				return;
			}
		} else {
			BusinessService.getWorkflowService().disactivateProjectTemplate(currentTemplate.getData());
		}

		projectTemplateViewer.update(currentTemplate, null);

		updateActivateTemplateButton();

	}

	private void updateActivateTemplateButton() {

		SingleObject currentTemplate = getCurrentProjectTemplate();
		if (currentTemplate == null) {
			return;
		}
		if (Boolean.TRUE.equals(currentTemplate.getValue(IDBConstants.FIELD_ACTIVATE))) {
			activateTemplateButton.setImage(Resource.getImage(Resource.DISCONNECT32));
		} else {
			activateTemplateButton.setImage(Resource.getImage(Resource.CONNECT32));
		}
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {

		if (event == null || event.getSelection().isEmpty()) {
			currentObject = null;
		} else {
			currentObject = (ISingleObject) ((StructuredSelection) event.getSelection()).getFirstElement();
		}
		if (event.getSelectionProvider() == projectTemplateViewer) {
			if (currentObject == null) {
				wbsViewer.setInput(null);
				obsViewer.setInput(null);
				updateProcessViewer();
			} else {
				Object projectTemplateId = ((ISingleObject) currentObject).getValue(IDBConstants.FIELD_SYSID);
				Assert.isNotNull(projectTemplateId);
				setViewerInput(projectTemplateId, wbsViewer);
				setViewerInput(projectTemplateId, obsViewer);
			}
		} else if (event.getSelectionProvider() == wbsViewer) {
			// 对流层面板设置输入
			updateProcessViewer();
		}
		updateButtons();
	}

	private void updateProcessViewer() {

		CascadeObject currentwbs = getCurrentWBS();
		ISingleObject currentTemplate = null;
		if (DataUtil.isWorkTemplateObject(currentwbs)) {
			currentTemplate = currentwbs;
		}
		if (currentTemplate == null) {
			processViewer.setInput(null);
			processLabel.setText(UIConstants.TEXT_WORKFLOWSELETE);
		} else {
			String processId = (String) currentTemplate.getValue(IDBConstants.FIELD_PROCESS_DEFINITION_ID);
			if (processId == null) {
				processViewer.setInput(null);
				processLabel.setText(UIConstants.TEXT_WORKFLOWSELETE);
			} else {
				KnowledgeBase base = BusinessService.getWorkflowService().getCurrentSiteKnowledgebase();
				Process processDefinition = base.getProcess(processId);
				if (processDefinition != null) {
					List<HumanTaskNode> nodeList = BusinessService.getWorkflowService().getHumanNodesInProcessDefinition(processDefinition);
					processViewer.setInput(nodeList);
					packTableViewer(processViewer);
					processLabel.setText(processDefinition.getName());
				} else {
					processViewer.setInput(null);
					processLabel.setText(UIConstants.TEXT_UNAVILABLE_PROCESS_DEFINITION);
				}
			}
		}

	}

	private void packTableViewer(TableViewer viewer) {

		int count = viewer.getTable().getColumnCount();
		for (int i = 0; i < count; i++) {
			viewer.getTable().getColumn(i).pack();
		}
	}

	@Override
	public boolean needUpdate() {

		return true;
	}

}
