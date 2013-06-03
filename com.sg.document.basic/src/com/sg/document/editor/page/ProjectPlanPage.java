package com.sg.document.editor.page;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
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
import com.sg.widget.util.Util;

public class ProjectPlanPage extends AbstractPageDelegator {

	private static final String PROJECTPLAN = "projectplan";

	private TableViewer viewer;

	private Button buttonAdd;

	private Button buttonRemove;

	protected BasicDBList inputdata;

//	private Composite parent;

//	private TableViewer ganttViewer;

//	private SashForm sash;

	private static final String EDITOR_ID = "com.sg.cpm.editor.work.create.inprojectapply";

	public ProjectPlanPage() {

	}

	@Override
	public Composite createPageContent(Composite parent, ISingleObjectEditorInput input, PageConfiguration conf) {
//		this.parent = parent;
		inputdata = (BasicDBList) input.getInputData().getValue(PROJECTPLAN);
		if (inputdata == null){
			inputdata = new BasicDBList();
			input.getInputData().setValue(PROJECTPLAN, inputdata, null, false);
		}

		GridLayout layout = new GridLayout();
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 1;
		parent.setLayout(layout);

		createToolbar(parent);
		

		createDescViewer(parent);


		viewer.setInput(inputdata);		
		reloadGantt();
		viewer.refresh();
		Table table = viewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		return null;
	}


	private void createDescViewer(Composite parent) {
		final SimpleDateFormat sdf = Util.getDateFormat(Util.SDF_YY_MM_DD);

		viewer = new TableViewer(parent, SWT.NONE);
		Table table = viewer.getTable();
		table.setData(RWT.MARKUP_ENABLED, true);
		table.setData(RWT.CUSTOM_ITEM_HEIGHT, 30);
		table.setData(RWT.CUSTOM_VARIANT, "gantt");
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(ArrayContentProvider.getInstance());
		// 创建表列
		// 创建序号列
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText("\n序号");
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

		// 创建工作内容列
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText("\n工作内容");
		col.getColumn().setWidth(240);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				DBObject dbo = (DBObject) element;
				return (String) dbo.get(IDBConstants.FIELD_DESC);
			}
		});

		// 创建负责人列
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText("\n负责人");
		col.getColumn().setWidth(80);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				DBObject dbo = (DBObject) element;
				return (String) dbo.get(IDBConstants.FIELD_WORK_PM);
			}
		});

		// 创建计划开始列
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText("\n计划开始");
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				DBObject dbo = (DBObject) element;
				Date date = (Date) dbo.get(IDBConstants.FIELD_PROJECT_PLANSTART);
				return sdf.format(date);
			}
		});

		// 创建计划完成列
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText("\n计划完成");
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				DBObject dbo = (DBObject) element;
				Date date = (Date) dbo.get(IDBConstants.FIELD_PROJECT_PLANFINISH);
				return sdf.format(date);

			}
		});

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

		buttonAdd = new Button(toolbar, SWT.PUSH);
		buttonAdd.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		buttonAdd.setImage(Resource.getImage(Resource.CREATE_WORK32));
		buttonAdd.setToolTipText(UIConstants.TEXT_CREATE_WORK);
		buttonAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				handleAdd();
			}
		});

		buttonRemove = new Button(toolbar, SWT.PUSH);
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
			refresh();
			setDirty(true);
		} else {
		}
	}
	
	

	private void reloadGantt() {

		// 去掉现有的时间列
		TableColumn[] cols = viewer.getTable().getColumns();
		for (int i = 0; i < cols.length; i++) {
			if(cols[i].getData("month")!=null){
				cols[i].dispose();
			}
		}

		// 获得计划中最早的开始时间和最晚的完成时间
		Date start = null;
		Date end = null;

		for (int i = 0; i < inputdata.size(); i++) {
			DBObject row = (DBObject) inputdata.get(i);
			Date planS = (Date) row.get(IDBConstants.FIELD_PROJECT_PLANSTART);
			if (start == null || planS.before(start)) {
				start = planS;
			}
			Date planF = (Date) row.get(IDBConstants.FIELD_PROJECT_PLANFINISH);
			if (end == null || planF.after(end)) {
				end = planF;
			}
		}

		if (start != null && end != null) {

			// gantt的时间要比计划前后增加2两个月
			Calendar ganttStart = Calendar.getInstance();
			ganttStart.setTime(start);
			ganttStart.add(Calendar.MONTH, -1);

			Calendar ganttFinish = Calendar.getInstance();
			ganttFinish.setTime(end);
			ganttFinish.add(Calendar.MONTH, 1);

			// 添加时间列
			Calendar colCal = ganttStart;
			int firstYear = 0;
			while (ganttFinish.after(colCal)) {
				int startYear = colCal.get(Calendar.YEAR);
				int startMonth = colCal.get(Calendar.MONTH);
				TableViewerColumn colv = new TableViewerColumn(viewer, SWT.CENTER);
				TableColumn column = colv.getColumn();
				column.setMoveable(false);
				column.setResizable(false);
				column.setWidth(40);
				column.setData("year", startYear);
				column.setData("month", startMonth);

				if (startYear != firstYear) {
					firstYear = startYear;
					column.setText("" + startYear + "\n" + (startMonth + 1) + "月");
				} else {
					column.setText("" + "\n" + (startMonth + 1) + "月");
				}

				colv.setLabelProvider(new GanttColumnLabelProvider(startYear, startMonth));

				colCal.add(Calendar.MONTH, 1);
			}
		}

	}

	@Override
	public void refresh() {

		reloadGantt();
		viewer.refresh();
		super.refresh();
	}



}
