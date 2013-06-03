package com.sg.common.ui;

import java.util.List;

import org.bson.types.ObjectId;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormPart;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.sg.common.BusinessService;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.expression.query.QueryExpression;
import com.sg.db.model.ISingleObject;
import com.sg.user.UserSessionContext;
import com.sg.widget.Widget;
import com.sg.widget.configuration.PageConfiguration;
import com.sg.widget.editor.IPageDelegator;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.viewer.tableviewer.QueryTableViewer;

public class WorkResourcePage implements IPageDelegator {

	private ISingleObjectEditorInput input;

	private QueryTableViewer userViewer;

	private TableViewer resourceViewer;

	private List<ISingleObject> userList;

	private BasicDBList resourceList;

	class DropTargetForSetListener implements DropTargetListener {

		public void dragEnter(final DropTargetEvent event) {

			event.feedback = DND.FEEDBACK_SCROLL;
		}

		public void dragLeave(final DropTargetEvent event) {

		}

		public void dragOperationChanged(final DropTargetEvent event) {

		}

		public void dragOver(final DropTargetEvent event) {

		}

		public void drop(final DropTargetEvent event) {
			String message = canDND();
			if (message != null) {
				MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), UIConstants.ACTION_ASSIGNMENT_TOOLTIPS, message);
				return;
			}


			String jsonOBS = (String) event.data;
			if (jsonOBS.equals("#")) {
				return;
			}

			// 判断如果过来的用户已经在当前的用户清单中存在了就跳出
			DBObject user = DataUtil.getRefData((DBObject) JSON.parse(jsonOBS), IDBConstants.DATA_USER_BASIC);
			ObjectId uoid = (ObjectId) user.get(IDBConstants.FIELD_SYSID);
			if (resourceList != null) {
				for (int i = 0; i < resourceList.size(); i++) {
					DBObject res = (DBObject) resourceList.get(i);
					if (res.get(IDBConstants.FIELD_SYSID).equals(uoid)) {
						Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
						MessageDialog.openWarning(shell, UIConstants.TEXT_SETPARTICIPAIED, UIConstants.TEXT_WARNING_PARTICIPAIEDEXSIT);
						return;
					}
				}
			} else {
				resourceList = new BasicDBList();
			}
			// 如果这个用户是负责人，跳出
			DBObject charger = (DBObject) input.getInputData().getValue(IDBConstants.FIELD_WORK_PM);
			if (charger != null && charger.get(IDBConstants.FIELD_SYSID).equals(uoid)) {
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				MessageDialog.openWarning(shell, UIConstants.TEXT_SETPARTICIPAIED, UIConstants.TEXT_WARNING_PARTICIPAIED_ISPM);
				return;
			}
			resourceList.add(user);
			resourceViewer.add(user);
			input.getInputData().setValue(IDBConstants.FIELD_WORK_RESOURCE, resourceList, null, false);
		}

		public void dropAccept(final DropTargetEvent event) {

		}

	}

	class DragSourceForSetListener implements DragSourceListener {

		public void dragFinished(final DragSourceEvent event) {

		}

		public void dragSetData(final DragSourceEvent event) {

			IStructuredSelection sele = userViewer.getSelection();
			if (sele != null && !sele.isEmpty()) {
				ISingleObject so = (ISingleObject) sele.getFirstElement();
				event.data = JSON.serialize(so.getData());
			} else {
				event.data = "#";// 必须要给一个字符串，否则要在javatonative出错。
			}
		}

		public void dragStart(final DragSourceEvent event) {

		}
	}

	class DropTargetForUnsetListener implements DropTargetListener {

		public void dragEnter(final DropTargetEvent event) {

			event.feedback = DND.FEEDBACK_SCROLL;
		}

		public void dragLeave(final DropTargetEvent event) {

		}

		public void dragOperationChanged(final DropTargetEvent event) {

		}

		public void dragOver(final DropTargetEvent event) {

		}

		public void drop(final DropTargetEvent event) {

			String message = canDND();
			if (message != null) {
				MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), UIConstants.ACTION_ASSIGNMENT_TOOLTIPS, message);
				return;
			}

			String jsonOBS = (String) event.data;
			if (jsonOBS.equals("#")) {
				return;
			}

			IStructuredSelection sele = (IStructuredSelection) resourceViewer.getSelection();
			if (sele != null && !sele.isEmpty()) {
				DBObject data = (DBObject) sele.getFirstElement();
				resourceViewer.remove(data);
				for (int i = 0; i < resourceList.size(); i++) {
					DBObject res = (DBObject) resourceList.get(i);
					if (res.get(IDBConstants.FIELD_SYSID).equals(data.get(IDBConstants.FIELD_SYSID))) {
						resourceList.remove(res);
						input.getInputData().setValue(IDBConstants.FIELD_WORK_RESOURCE, resourceList, null, false);
						return;
					}
				}
			} else {
				event.data = "#";// 必须要给一个字符串，否则要在javatonative出错。
			}

		}

		public void dropAccept(final DropTargetEvent event) {

		}

	}

	class DragSourceForUnsetListener implements DragSourceListener {

		public void dragFinished(final DragSourceEvent event) {

		}

		public void dragSetData(final DragSourceEvent event) {

			IStructuredSelection sele = (IStructuredSelection) resourceViewer.getSelection();
			if (sele != null && !sele.isEmpty()) {
				DBObject so = (DBObject) sele.getFirstElement();
				event.data = JSON.serialize(so);
			} else {
				event.data = "#";// 必须要给一个字符串，否则要在javatonative出错。
			}
		}

		public void dragStart(final DragSourceEvent event) {

		}
	}

	public String canDND() {

		// 如果当前的是工作对象,并且包含了工作流，这个页面不可编辑
		ISingleObject workData = input.getInputData();
		if (DataUtil.isWorkObject(workData)) {
			// 获得工作模板,如果
			ObjectId workTemplateId = (ObjectId) workData.getValue(IDBConstants.FIELD_TEMPLATE);
			if (workTemplateId == null) {
				return UIConstants.MESSAGE_NEED_SELECT_TEMPLATE;
			}
			DBObject workTemplate = BusinessService.getWorkService().getWorkTemplateObject(workTemplateId);
			String pd = (String) workTemplate.get(IDBConstants.FIELD_PROCESS_DEFINITION_ID);
			if (pd != null && pd.length() > 0) {
				return UIConstants.MESSAGE_TEMPLATE_HAS_WFDEF;
			}
		}
		return null;

	}

	private void setupDND() {

		setDragSourceForSet(userViewer.getControl());
		setDropTargetForSet(resourceViewer.getControl());

		setDropTargetForUnset(userViewer.getControl());
		setDragSourceForUnset(resourceViewer.getControl());
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

	/**
	 * 显示任务的资源清单，可以添加删除
	 */
	@Override
	public Composite createPageContent(Composite parent, ISingleObjectEditorInput input, PageConfiguration cpage) {

		this.input = input;

		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		createResourceList(sashForm);
		createUserList(sashForm);
		setupDND();
		sashForm.setWeights(new int[] { 1, 1 });
		return sashForm;
	}

	private void createUserList(Composite parent) {

		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.horizontalSpacing = 0;

		panel.setLayout(layout);

		Label label = new Label(panel, SWT.NONE);
		label.setText(UIConstants.TEXT_USER);
		userViewer = new QueryTableViewer(panel, SWT.VIRTUAL | SWT.FULL_SELECTION, Widget.getQueryTableViewerConfiguration(UIConstants.VIEWER_TABLE_SITEUSER)) {

			@Override
			protected boolean initLoad() {

				return false;
			}

		};
		QueryExpression exp = userViewer.createExpression();
		exp.setParamValue(IDBConstants.PARAM_INPUT_SITEPARENT, UserSessionContext.getSession().getSiteContextId());
		userList = DataUtil.getSiteUsers(UserSessionContext.getSession().getSiteContextId(), true);
		userViewer.runSetInput(userList);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		layoutData.heightHint = 400;
		layoutData.widthHint = 200;
		userViewer.getTable().setLayoutData(layoutData);
	}

	private void createResourceList(Composite parent) {

		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.horizontalSpacing = 0;

		panel.setLayout(layout);

		Label label = new Label(panel, SWT.NONE);
		label.setText(UIConstants.TEXT_WORKRESOURCE);

		resourceViewer = new TableViewer(panel, SWT.NONE);
		Table table = resourceViewer.getTable();
		table.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		table.setData(RWT.CUSTOM_ITEM_HEIGHT, 70);
		resourceViewer.setContentProvider(ArrayContentProvider.getInstance());
		TableViewerColumn col = new TableViewerColumn(resourceViewer, SWT.NONE);
		col.setLabelProvider(new UserLableProvider());
		col.getColumn().setWidth(350);
		resourceList = (BasicDBList) input.getInputData().getValue(IDBConstants.FIELD_WORK_RESOURCE);
		resourceViewer.setInput(resourceList);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		layoutData.heightHint = 400;
		layoutData.widthHint = 200;
		resourceViewer.getTable().setLayoutData(layoutData);
	}

	@Override
	public IFormPart getFormPart() {

		// TODO Auto-generated method stub
		return null;
	}

}
