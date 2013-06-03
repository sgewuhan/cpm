package com.sg.document.editor.page;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.common.ui.UIConstants;
import com.sg.db.model.SingleObject;
import com.sg.resource.Resource;
import com.sg.widget.Widget;
import com.sg.widget.configuration.EditorConfiguration;
import com.sg.widget.configuration.PageConfiguration;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.dialog.SingleObjectEditorDialogCallback;
import com.sg.widget.editor.AbstractPageDelegator;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.SingleObjectEditorInput;

public class ProjectTeamPage extends AbstractPageDelegator {

	private static final String TEAM = "teamdetail";

	private static final String EDITOR_ID = "com.sg.cpm.editor.team.create.inprojectapply";

	private TableViewer viewer;

	private BasicDBList inputdata;

	public ProjectTeamPage() {

	}

	@Override
	public Composite createPageContent(Composite parent, ISingleObjectEditorInput input, PageConfiguration conf) {

		inputdata = (BasicDBList) input.getInputData().getValue(TEAM);
		if (inputdata == null) {
			inputdata = new BasicDBList();
			input.getInputData().setValue(TEAM, inputdata, null, false);
		}

		GridLayout layout = new GridLayout();
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 1;
		parent.setLayout(layout);

		createToolbar(parent);

		createDescViewer(parent);
		
		Table table = viewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.setInput(inputdata);
		return table;
	}

	private void createToolbar(Composite parent) {

		Composite toolbar = new Composite(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false));
		RowLayout layout = new RowLayout();
		layout.wrap = false;
		layout.pack = true;
		layout.justify = false;
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		toolbar.setLayout(layout);

		Button buttonAdd = new Button(toolbar, SWT.PUSH);
		buttonAdd.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		buttonAdd.setImage(Resource.getImage(Resource.CREATE_WORK32));
		buttonAdd.setToolTipText(UIConstants.TEXT_CREATE_WORK);
		buttonAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				handleAdd();
			}
		});

		Button buttonRemove = new Button(toolbar, SWT.PUSH);
		buttonRemove.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		buttonRemove.setImage(Resource.getImage(Resource.REMOVE_WORK32));
		buttonRemove.setToolTipText(UIConstants.TEXT_REMOVE_WORK);
		buttonRemove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				handleRemove();
			}
		});

	}

	private void createDescViewer(Composite parent) {


		viewer = new TableViewer(parent, SWT.NONE);
		Table table = viewer.getTable();
		table.setData(RWT.MARKUP_ENABLED, true);
		table.setData(RWT.CUSTOM_ITEM_HEIGHT, 30);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(ArrayContentProvider.getInstance());
		// 创建表列
		// 创建序号列
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText("序号");
		col.getColumn().setWidth(40);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				List input = (List) viewer.getInput();
				if (input != null) {
					return "" + (input.indexOf(element) + 1);
				}
				return super.getText(element);
			}
		});

		// 创建名称规划列
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText("姓名");
		col.getColumn().setWidth(60);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				DBObject dbo = (DBObject) element;
				if(dbo!=null){
					DBObject pm = (DBObject) dbo.get("pm");
					if(pm!=null){
						return (String) pm.get("name");
					}
				}
				return "";
			}
		});

		// 创建型号列
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText("所在部门");
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				DBObject dbo = (DBObject) element;
				String value = (String) dbo.get("dept");
				return value == null ? "" : value;

			}
		});


		// 创建解决途径列
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText("专业方向");
		col.getColumn().setWidth(240);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				DBObject dbo = (DBObject) element;
				String value = (String) dbo.get("direction");
				return value == null ? "" : value;

			}
		});

		// 创建用途列
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText("内部职称");
		col.getColumn().setWidth(83);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				DBObject dbo = (DBObject) element;
				String value = (String) dbo.get("title");
				return value == null ? "" : value;

			}
		});
		
		// 创建用途列
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText("主要职责");
		col.getColumn().setWidth(240);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				DBObject dbo = (DBObject) element;
				String value = (String) dbo.get("duty");
				return value == null ? "" : value;

			}
		});
	}

	protected void handleRemove() {

		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		if (sel == null || sel.isEmpty())
			return;

		Object element = sel.getFirstElement();
		inputdata.remove(element);
		setDirty(true);
		refresh();

	}

	protected void handleAdd() {

		// 显示对话框
		SingleObjectEditorDialogCallback handler = new SingleObjectEditorDialogCallback() {

			@Override
			public boolean saveBefore(ISingleObjectEditorInput input) {

				inputdata.add(input.getInputData().getData());
				return false;
			}

		};
		EditorConfiguration ec = Widget.getSingleObjectEditorConfiguration(EDITOR_ID);
		ISingleObjectEditorInput taskInput = new SingleObjectEditorInput(ec, new SingleObject().setData(new BasicDBObject()));
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		int ok = SingleObjectEditorDialog.getInstance(shell, EDITOR_ID, taskInput, handler, false).open();
		if (ok == SingleObjectEditorDialog.OK) {
			setDirty(true);
			refresh();
		} else {
		}
	}

	@Override
	public void refresh() {
		viewer.refresh();
		super.refresh();
	}
	
	
}
