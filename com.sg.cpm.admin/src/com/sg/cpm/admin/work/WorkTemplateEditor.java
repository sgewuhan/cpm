package com.sg.cpm.admin.work;

import java.util.List;

import org.bson.types.ObjectId;
import org.drools.KnowledgeBase;
import org.drools.definition.process.Node;
import org.drools.definition.process.Process;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.jbpm.workflow.core.node.HumanTaskNode;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.NodeAssignment;
import com.sg.common.service.ServiceException;
import com.sg.common.ui.ProcessSelectorDialog;
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
import com.sg.widget.configuration.EditorConfiguration;
import com.sg.widget.dialog.QueryTreeSelectorDialog;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.editor.SingleObjectEditorInput;
import com.sg.widget.util.Util;
import com.sg.widget.viewer.tableviewer.QueryTableViewer;
import com.sg.widget.viewer.treeviewer.QueryTreeViewer;

public class WorkTemplateEditor extends AdminFunctionEditor {

	private Button createTemplateButton;

	private Button removeTemplateButton;

	private Button editTemplateButton;

	private Button createDocumentButton;

	private Button removeDocumentButton;

	private Button editDocumentButton;

	private Button createParticipateButton;

	private Button removeParticipateButton;

	private ObjectId siteContextId;

	private String siteContextName;

	private QueryTreeViewer orgViewer;

	private QueryTableViewer workTemplateViewer;

	private QueryTableViewer documentViewer;

	protected TableViewer userViewer;

	protected TableViewer processViewer;

	private CascadeObject currentOrg;

	protected SingleObject currentTemplate;

	protected SingleObject currentDocument;

	private EditorConfiguration workTemplateEditor;

	private EditorConfiguration deliveryTemplateEditor;

	protected DBObject currentUser;

	private Button setWorkflowButton;

	private Label processLabel;

	private Button activateTemplateButton;

	@Override
	public void createPartControl(Composite parent) {

		siteContextId = UserSessionContext.getSession().getSiteContextId();
		siteContextName = UserSessionContext.getSession().getSiteContextName();
		workTemplateEditor = Widget.getSingleObjectEditorConfiguration(UIConstants.EDITOR_WORK_TEMPLATE);
		deliveryTemplateEditor = Widget.getSingleObjectEditorConfiguration(UIConstants.EDITOR_DELIVERY_TEMPLATE);

		// 三个tabpage
		// 第一个用于显示组织结构
		// 第二个显示组织结构下的所有工作模板
		// 另一个显示一个列表 根据左边选择的列表框显示它下面的文档

		SashForm sash = new SashForm(parent, SWT.HORIZONTAL);

		createLeft(sash);

		createMiddle(sash);

		createRight(sash);

		sash.setWeights(new int[] { 1, 1, 1 });

		update();

		updateToolbarStatus();

		setupDND();

		super.createPartControl(parent);
	}

	private void setupDND() {

		setDragSourceForSet(userViewer.getControl());
		setDropTargetForSet(processViewer.getControl());

		setDragSourceForUnset(processViewer.getControl());
		setDropTargetForUnset(userViewer.getControl());
		setDropTargetForUnset(workTemplateViewer.getControl());
	}

	private void setDragSourceForSet(Control dragControl) {

		DragSource dragSource = new DragSource(dragControl, DND.DROP_MOVE);
		dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dragSource.addDragListener(new DragUser(this));
	}

	private void setDropTargetForSet(Control dropControl) {

		DropTarget dropTarget = new DropTarget(dropControl, DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dropTarget.addDropListener(new DropToProcess(this));
	}

	private void setDragSourceForUnset(Control dragControl) {

		DragSource dragSource = new DragSource(dragControl, DND.DROP_MOVE);
		dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dragSource.addDragListener(new DragProcess(this));
	}

	private void setDropTargetForUnset(Control dropControl) {

		DropTarget dropTarget = new DropTarget(dropControl, DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dropTarget.addDropListener(new DropToUnset(this));
	}

	private void createLeft(SashForm parent) {

		CTabFolder tabFolder = new CTabFolder(parent, SWT.TOP | SWT.BORDER);
		CTabItem cti = new CTabItem(tabFolder, SWT.NONE);
		cti.setText(UIConstants.TEXT_ORG_ELEMENT);
		Control leftControl = createOrgControl(tabFolder);
		cti.setControl(leftControl);
		tabFolder.setSelection(0);

	}

	private void createMiddle(SashForm parent) {

		CTabFolder tabFolder = new CTabFolder(parent, SWT.TOP | SWT.BORDER);
		CTabItem cti = new CTabItem(tabFolder, SWT.NONE);
		cti.setText(UIConstants.TEXT_WORKTEMPLATE);

		SashForm panel = new SashForm(tabFolder, SWT.VERTICAL);
		createWorkTemplateControl(panel);
		createParticipateControl(panel);

		panel.setWeights(new int[] { 3, 2 });

		cti.setControl(panel);
		tabFolder.setSelection(0);
	}

	private void createRight(SashForm parent) {

		CTabFolder tabFolder = new CTabFolder(parent, SWT.TOP | SWT.BORDER);
		CTabItem item1 = new CTabItem(tabFolder, SWT.NONE);
		item1.setText(UIConstants.TEXT_WORKTEMPLATE_DELIVERY);
		Control rightControl0 = createDocumentControl(tabFolder);
		item1.setControl(rightControl0);

		CTabItem item2 = new CTabItem(tabFolder, SWT.NONE);
		item2.setText(UIConstants.TEXT_WORKTEMPLATE_WORKFLOW);
		Control rightControl1 = createWorkFlowControl(tabFolder);
		item2.setControl(rightControl1);

		tabFolder.setSelection(0);
	}

	private Control createOrgControl(Composite parent) {

		orgViewer = new QueryTreeViewer(parent, SWT.VIRTUAL | SWT.FULL_SELECTION, Widget.getQueryTreeViewerConfiguration(UIConstants.VIEWER_TREE_ORG_TEAM));
		orgViewer.setAutoExpandLevel(3);
		orgViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				// 选择组织后
				ISelection isel = event.getSelection();
				if (Util.isNullOrEmptySelection(isel)) {
					currentOrg = null;
				} else {
					currentOrg = (CascadeObject) ((IStructuredSelection) isel).getFirstElement();
				}

				updateWorkTemplateViewer();

				updateToolbarStatus();

			}
		});
		return orgViewer.getControl();
	}

	private Control createWorkTemplateControl(Composite parent) {

		Composite bg = new Composite(parent, SWT.NONE);
		bg.setLayout(new FormLayout());

		Composite toolbarbg = getToolbar(bg);
		createWorkTemplateToolbar(toolbarbg);

		workTemplateViewer = new QueryTableViewer(bg, SWT.FULL_SELECTION, Widget.getQueryTableViewerConfiguration(UIConstants.VIEWER_TABLE_WORKTEAMPLATE));

		workTemplateViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				// 选择组织后
				ISelection isel = event.getSelection();
				if (Util.isNullOrEmptySelection(isel)) {
					currentTemplate = null;
				} else {
					currentTemplate = (SingleObject) ((IStructuredSelection) isel).getFirstElement();
				}

				updateDocumentViewer();
				updateParticipateViewer();
				updateProcessViewer();

				updateToolbarStatus();
			}
		});

		layoutContent(toolbarbg, workTemplateViewer.getControl());

		return bg;
	}

	private void createWorkTemplateToolbar(Composite parent) {

		parent.setLayout(getToolbarLayout());

		createTemplateButton = new Button(parent, SWT.PUSH);
		createTemplateButton.setImage(Resource.getImage(Resource.CREATE_WORK32));
		createTemplateButton.setToolTipText(UIConstants.TEXT_CREATE_WORK_TEMPLATE);
		createTemplateButton.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		createTemplateButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				handleCreateTemplate();
			}

		});

		removeTemplateButton = new Button(parent, SWT.PUSH);
		removeTemplateButton.setImage(Resource.getImage(Resource.REMOVE_WORK32));
		removeTemplateButton.setToolTipText(UIConstants.TEXT_CREATE_WORK_TEMPLATE);
		removeTemplateButton.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		removeTemplateButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				handleRemoveTemplate();
			}

		});

		editTemplateButton = new Button(parent, SWT.PUSH);
		editTemplateButton.setImage(Resource.getImage(Resource.EDIT_PROP32));
		editTemplateButton.setToolTipText(UIConstants.TEXT_EDIT_WORK_TEMPLATE);
		editTemplateButton.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		editTemplateButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				handleEditTemplate();
			}

		});

		activateTemplateButton = new Button(parent, SWT.PUSH);
		activateTemplateButton.setImage(Resource.getImage(Resource.CONNECT32));
		activateTemplateButton.setToolTipText(UIConstants.TEXT_ACTIVATE_TEMPLATE);
		activateTemplateButton.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		activateTemplateButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				handleActivateTemplate();
			}

		});

	}

	private Control createDocumentControl(Composite parent) {

		Composite bg = new Composite(parent, SWT.NONE);
		bg.setLayout(new FormLayout());

		Composite toolbarbg = getToolbar(bg);
		createDocumentToolbar(toolbarbg);

		documentViewer = new QueryTableViewer(bg, SWT.FULL_SELECTION, Widget.getQueryTableViewerConfiguration(UIConstants.VIEWER_TABLE_WORKTEAMPLATE));

		documentViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				// 选择组织后
				ISelection isel = event.getSelection();
				if (Util.isNullOrEmptySelection(isel)) {
					currentDocument = null;
				} else {
					currentDocument = (SingleObject) ((IStructuredSelection) isel).getFirstElement();
				}

				updateToolbarStatus();
			}
		});

		layoutContent(toolbarbg, documentViewer.getControl());

		return bg;

	}

	private void createDocumentToolbar(Composite parent) {

		parent.setLayout(getToolbarLayout());

		createDocumentButton = new Button(parent, SWT.PUSH);
		createDocumentButton.setImage(Resource.getImage(Resource.CREATE_DELIVERY32));
		createDocumentButton.setToolTipText(UIConstants.TEXT_CREATE_DOC_TEMPLATE);
		createDocumentButton.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		createDocumentButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				handleCreateDocument();
			}

		});

		removeDocumentButton = new Button(parent, SWT.PUSH);
		removeDocumentButton.setImage(Resource.getImage(Resource.REMOVE_DELIVERY32));
		removeDocumentButton.setToolTipText(UIConstants.TEXT_REMOVE_DOC_TEMPLATE);
		removeDocumentButton.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		removeDocumentButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				handleRemoveDocument();
			}

		});

		editDocumentButton = new Button(parent, SWT.PUSH);
		editDocumentButton.setImage(Resource.getImage(Resource.EDIT_PROP32));
		editDocumentButton.setToolTipText(UIConstants.TEXT_EDIT_DOC_TEMPLATE);
		editDocumentButton.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		editDocumentButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				handleEditDocument();
			}

		});

	}

	private Control createWorkFlowControl(Composite parent) {

		Composite bg = new Composite(parent, SWT.NONE);
		bg.setLayout(new FormLayout());

		Composite toolbarbg = getToolbar(bg);
		createWorkFlowToolbar(toolbarbg);

		// 创建工作流节点定义表格
		processViewer = new TableViewer(bg, SWT.FULL_SELECTION);
		processViewer.setContentProvider(ArrayContentProvider.getInstance());
		TableViewerColumn column = new TableViewerColumn(processViewer, SWT.LEFT);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public Image getImage(Object element) {

				try {
					NodeAssignment nass = BusinessService.getWorkflowService().getNodeAssignment(currentTemplate.getData(), (HumanTaskNode) element);
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

				try {
					NodeAssignment nass = BusinessService.getWorkflowService().getNodeAssignment(currentTemplate.getData(), (HumanTaskNode) element);

					if (nass.isAlreadyAssignment()) {
						ObjectId assignmentId = nass.getAssignmentId();
						return BusinessService.getOrganizationService().getOBSItemLabel(assignmentId);
					} else if (nass.isNeedAssignment()) {
						return UIConstants.TEXT_NEEDASSIGN;
					} else if (nass.isNotNeedAssignment()) {
						if (nass.isRuleAssignment()) {
							return nass.getRuleAssignmentName() + UIConstants.TEXT_RULE_ASSIGNMENT;
						}else if(nass.isDyanmic()){
							return UIConstants.TEXT_DYNAMIC_ASSIGNMENT;
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
					NodeAssignment nass = BusinessService.getWorkflowService().getNodeAssignment(currentTemplate.getData(), (HumanTaskNode) element);
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
		layoutContent(toolbarbg, processViewer.getControl());

		return bg;
	}

	private void createWorkFlowToolbar(Composite toolbarbg) {

		toolbarbg.setLayout(getToolbarLayout());

		setWorkflowButton = new Button(toolbarbg, SWT.PUSH);
		setWorkflowButton.setImage(Resource.getImage(Resource.WORKFLOW32));
		setWorkflowButton.setToolTipText(UIConstants.TEXT_WORKFLOWSELETE);
		setWorkflowButton.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		setWorkflowButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				handleSetWorkflow();
			}

		});

		processLabel = new Label(toolbarbg, SWT.NONE);
		processLabel.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
	}

	private Control createParticipateControl(Composite parent) {

		Composite bg = new Composite(parent, SWT.NONE);
		bg.setLayout(new FormLayout());

		Composite toolbarbg = getToolbar(bg);
		createParticipateToolbar(toolbarbg);

		userViewer = new TableViewer(bg, SWT.FULL_SELECTION);
		userViewer.setContentProvider(ArrayContentProvider.getInstance());
		TableViewerColumn col = new TableViewerColumn(userViewer, SWT.NONE);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public Image getImage(Object element) {

				if (IDBConstants.VALUE_OBS_TEAMTYPE.equals(((DBObject) element).get(IDBConstants.FIELD_TEMPLATE_TYPE))) {
					return Resource.getImage(Resource.TEAM16);

				} else if (IDBConstants.VALUE_OBS_USERTYPE.equals(((DBObject) element).get(IDBConstants.FIELD_TEMPLATE_TYPE))) {
					return Resource.getImage(Resource.USER16);

				} else if (IDBConstants.VALUE_OBS_ROLETYPE.equals(((DBObject) element).get(IDBConstants.FIELD_TEMPLATE_TYPE))) {
					return Resource.getImage(Resource.ROLE16);

				}

				return super.getImage(element);
			}

			@Override
			public String getText(Object element) {

				if (element instanceof DBObject) {
					return BusinessService.getOrganizationService().getOBSItemLabel((DBObject) element);
				}
				return "";
			}

		});
		col.getColumn().setWidth(350);

		userViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				// 选择组织后
				ISelection isel = event.getSelection();
				if (Util.isNullOrEmptySelection(isel)) {
					currentUser = null;
				} else {
					currentUser = (DBObject) ((IStructuredSelection) isel).getFirstElement();
				}

				updateToolbarStatus();
			}
		});

		layoutContent(toolbarbg, userViewer.getControl());

		return bg;
	}

	private void createParticipateToolbar(Composite parent) {

		parent.setLayout(getToolbarLayout());

		createParticipateButton = new Button(parent, SWT.PUSH);
		createParticipateButton.setImage(Resource.getImage(Resource.CREATE_USER32));
		createParticipateButton.setToolTipText(UIConstants.TEXT_ADD_PATICIPATE);
		createParticipateButton.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		createParticipateButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				handleCreateParticipate();
			}

		});

		removeParticipateButton = new Button(parent, SWT.PUSH);
		removeParticipateButton.setImage(Resource.getImage(Resource.REMOVE_USER32));
		removeParticipateButton.setToolTipText(UIConstants.TEXT_REMOVE_PATICIPATE);
		removeParticipateButton.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		removeParticipateButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				handleRemoveParticipate();
			}

		});

	}

	private void layoutContent(Control toolbarbg, Control control) {

		FormData data = new FormData();
		control.setLayoutData(data);
		data.top = new FormAttachment(toolbarbg, 1);
		data.left = new FormAttachment(0, 1);
		data.right = new FormAttachment(100, -1);
		data.bottom = new FormAttachment(100, -1);

	}

	private Layout getToolbarLayout() {

		RowLayout layout = new RowLayout();
		layout.wrap = false;
		layout.pack = true;
		layout.justify = false;
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		return layout;
	}

	private Composite getToolbar(Composite bg) {

		Composite toolbarbg = new Composite(bg, SWT.NONE);
		FormData layoutData = new FormData();
		toolbarbg.setLayoutData(layoutData);
		layoutData.top = new FormAttachment(0, 0);
		layoutData.left = new FormAttachment(0, 0);
		layoutData.right = new FormAttachment(100, 0);
		layoutData.height = 34;
		return toolbarbg;
	}

	@Override
	public void update() {

		CascadeObject orgInput = orgViewer.createExpression();
		orgInput.setParamValue(IDBConstants.FIELD_SYSID, siteContextId).setParamValue(IDBConstants.FIELD_DESC, siteContextName);
		orgViewer.setInput(orgInput);
		updateWorkTemplateViewer();
	}

	protected void updateWorkTemplateViewer() {

		if (currentOrg == null) {
			workTemplateViewer.setInput(null);
		} else {
			QueryExpression exp = workTemplateViewer.getExpression();
			exp.setParamValue(IDBConstants.PARAM_INPUT_OBSPARENT, currentOrg.getSystemId());
			workTemplateViewer.updateInputData();
		}
	}

	protected void updateDocumentViewer() {

		if (currentTemplate == null) {
			documentViewer.setInput(null);
		} else {
			QueryExpression exp = documentViewer.getExpression();
			exp.setParamValue(IDBConstants.PARAM_INPUT_WBSPARENT, currentTemplate.getSystemId());
			documentViewer.updateInputData();
		}
	}

	protected void updateParticipateViewer() {

		if (currentTemplate == null) {
			userViewer.setInput(null);
		} else {
			BasicDBList userList = (BasicDBList) currentTemplate.getValue(IDBConstants.FIELD_PARTICIPATE);
			userViewer.setInput(userList);
		}
	}

	private void updateProcessViewer() {

		if (currentTemplate == null) {
			processViewer.setInput(null);
			processLabel.setText("");
		} else {
			String processId = (String) currentTemplate.getValue(IDBConstants.FIELD_PROCESS_DEFINITION_ID);
			if (processId == null) {
				processViewer.setInput(null);
				processLabel.setText("");
			} else {
				KnowledgeBase base = BusinessService.getWorkflowService().getCurrentSiteKnowledgebase();
				Process processDefinition = base.getProcess(processId);
				if (processDefinition != null) {
					List<HumanTaskNode> nodeList = BusinessService.getWorkflowService().getHumanNodesInProcessDefinition(processDefinition);
					processViewer.setInput(nodeList);
					Util.packTableViewer(processViewer);
					processLabel.setText(getProcessDefinitionLabel(processDefinition));
				} else {
					processViewer.setInput(null);
					processLabel.setText("<strong>" + UIConstants.TEXT_UNAVILABLE_PROCESS_DEFINITION + "</strong>");
				}
			}
		}

	}

	private String getProcessDefinitionLabel(Process processDefinition) {

		String link = "<strong><i>" + processDefinition.getName() + "</i></strong>";
		return link;
	}

	private void updateToolbarStatus() {

		createTemplateButton.setEnabled(currentOrg != null);
		editTemplateButton.setEnabled(currentTemplate != null);
		removeTemplateButton.setEnabled(currentTemplate != null);

		createDocumentButton.setEnabled(currentTemplate != null);
		editDocumentButton.setEnabled(currentDocument != null);
		removeDocumentButton.setEnabled(currentDocument != null);

		createParticipateButton.setEnabled(currentTemplate != null);
		removeParticipateButton.setEnabled(currentUser != null);

		setWorkflowButton.setEnabled(currentTemplate != null);

		activateTemplateButton.setEnabled(currentTemplate != null);
		updateActivateTemplateButton();
	}

	private void updateActivateTemplateButton() {

		if (currentTemplate == null) {
			return;
		}
		if (Boolean.TRUE.equals(currentTemplate.getValue(IDBConstants.FIELD_ACTIVATE))) {
			activateTemplateButton.setImage(Resource.getImage(Resource.DISCONNECT32));
		} else {
			activateTemplateButton.setImage(Resource.getImage(Resource.CONNECT32));
		}
	}

	protected void handleCreateTemplate() {

		if (currentOrg == null) {
			return;
		}

		SingleObject so = new SingleObject(DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_WORK_TEMPLATE));

		// 给出obsparent的值
		ObjectId parentId = (ObjectId) currentOrg.getSystemId();
		so.setValue(IDBConstants.FIELD_OBSPARENT, parentId);
		so.setValue(IDBConstants.FIELD_TEMPLATE_TYPE, IDBConstants.VALUE_WBS_TASK_TYPE);
		SingleObjectEditorInput editInput = new SingleObjectEditorInput(workTemplateEditor, so);

		// create
		SingleObjectEditorDialog soed = SingleObjectEditorDialog.getInstance(getSite().getShell(), workTemplateEditor.getId(), editInput, null, true);
		int ok = soed.open();
		if (ok == SingleObjectEditorDialog.OK) {
			ISingleObject data = soed.getInputData();
			workTemplateViewer.addDataObject(data);
		}

	}

	protected void handleEditTemplate() {

		if (currentTemplate == null) {
			return;
		}
		SingleObjectEditorInput editInput = new SingleObjectEditorInput(workTemplateEditor, currentTemplate);

		SingleObjectEditorDialog soed = SingleObjectEditorDialog.getInstance(getSite().getShell(), workTemplateEditor.getId(), editInput, null, true);
		soed.open();
	}

	protected void handleRemoveTemplate() {

		if (currentTemplate == null) {
			return;
		}

		// 删除文档模板
		ObjectId id = currentTemplate.getSystemId();
		DBCollection workTemplateCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_WORK_TEMPLATE);

		BasicDBList list = new BasicDBList();
		list.add(new BasicDBObject().append(IDBConstants.FIELD_WBSPARENT, id));
		list.add(new BasicDBObject().append(IDBConstants.FIELD_SYSID, id));
		workTemplateCollection.remove(new BasicDBObject().append("$or", list));

		workTemplateViewer.removeDataObject(currentTemplate);

	}

	protected void handleCreateDocument() {

		if (currentTemplate == null) {
			return;
		}

		SingleObject so = new SingleObject(DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_WORK_TEMPLATE));

		// 给出obsparent的值
		ObjectId parentId = (ObjectId) currentTemplate.getSystemId();
		so.setValue(IDBConstants.FIELD_WBSPARENT, parentId);
		so.setValue(IDBConstants.FIELD_TEMPLATE_TYPE, IDBConstants.VALUE_WBS_DOCUMENT_TYPE);
		SingleObjectEditorInput editInput = new SingleObjectEditorInput(deliveryTemplateEditor, so);

		// create
		SingleObjectEditorDialog soed = SingleObjectEditorDialog.getInstance(getSite().getShell(), deliveryTemplateEditor.getId(), editInput, null, true);
		int ok = soed.open();
		if (ok == SingleObjectEditorDialog.OK) {
			ISingleObject data = soed.getInputData();
			documentViewer.addDataObject(data);
		}
	}

	protected void handleEditDocument() {

		if (currentDocument == null) {
			return;
		}
		SingleObjectEditorInput editInput = new SingleObjectEditorInput(deliveryTemplateEditor, currentDocument);

		SingleObjectEditorDialog soed = SingleObjectEditorDialog.getInstance(getSite().getShell(), deliveryTemplateEditor.getId(), editInput, null, true);
		soed.open();
	}

	protected void handleRemoveDocument() {

		if (currentDocument == null) {
			return;
		}

		// 删除文档模板
		ObjectId id = currentDocument.getSystemId();
		DBCollection workTemplateCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_WORK_TEMPLATE);

		BasicDBList list = new BasicDBList();
		list.add(new BasicDBObject().append(IDBConstants.FIELD_SYSID, id));
		workTemplateCollection.remove(new BasicDBObject().append("$or", list));

		documentViewer.removeDataObject(currentDocument);
	}

	protected void handleCreateParticipate() {

		// 显示当前组织下的所有人
		QueryTreeSelectorDialog selector = new QueryTreeSelectorDialog(getSite().getShell(), UIConstants.VIEWER_TREE_ORG2);
		selector.setParameters(IDBConstants.FIELD_SYSID, currentOrg.getSystemId());
		selector.setParameters(IDBConstants.FIELD_DESC, currentOrg.getValue(IDBConstants.FIELD_DESC));
		selector.setFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {

				boolean filtered = !DataUtil.isProjectTeamObject((ISingleObject) element);
				return filtered;
			}

		});
		selector.setTitle(UIConstants.TEXT_ADD_PATICIPATE);
		int ok = selector.open();
		if (ok == Dialog.OK) {
			ISingleObject so = selector.getSelection();
			if (so == null) {
				return;
			}

			BasicDBList userList = (BasicDBList) currentTemplate.getValue(IDBConstants.FIELD_PARTICIPATE);
			if (userList == null) {
				userList = new BasicDBList();
			}

			Object id = so.getValue(IDBConstants.FIELD_SYSID);
			Object name;
			Object type;
			if (DataUtil.isUserObject(so)) {
				ObjectId userId = (ObjectId) so.getValue(IDBConstants.FIELD_USEROID);
				DBObject user = BusinessService.getOrganizationService().getUserObject(userId);
				name = user.get(IDBConstants.FIELD_NAME);
				so.setValue(IDBConstants.FIELD_DESC, name, null, false);// add
																		// to
																		// display
			}

			type = so.getValue(IDBConstants.FIELD_TEMPLATE_TYPE);
			name = so.getValue(IDBConstants.FIELD_DESC);
			userList.add(new BasicDBObject().append(IDBConstants.FIELD_SYSID, id).append(IDBConstants.FIELD_DESC, name)
					.append(IDBConstants.FIELD_TEMPLATE_TYPE, type));
			currentTemplate.setValue(IDBConstants.FIELD_PARTICIPATE, userList);
			currentTemplate.save();
			userViewer.add(so.getData());
		}
	}

	protected void handleRemoveParticipate() {

		BasicDBList userList = (BasicDBList) currentTemplate.getValue(IDBConstants.FIELD_PARTICIPATE);
		if (userList == null) {
			return;
		}
		userList.remove(currentUser);
		currentTemplate.setValue(IDBConstants.FIELD_PARTICIPATE, userList);
		currentTemplate.save();
		userViewer.remove(currentUser);
	}

	protected void handleSetWorkflow() {

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

	/**
	 * 激活任务模板
	 */
	protected void handleActivateTemplate() {

		if (currentTemplate == null) {
			return;
		}

		Boolean activate = Boolean.TRUE.equals(currentTemplate.getValue(IDBConstants.FIELD_ACTIVATE));

		if (!activate) {
			try {
				BusinessService.getWorkflowService().activateWorkTemplate(currentTemplate.getData());
			} catch (Exception e) {
				MessageDialog.openWarning(getEditorSite().getShell(), UIConstants.TEXT_ACTIVATE_TEMPLATE, e.getMessage());
				return;
			}
		} else {
			BusinessService.getWorkflowService().disactivateWorkTemplate(currentTemplate.getData());
		}

		workTemplateViewer.update(currentTemplate, null);

		updateActivateTemplateButton();

	}


}
