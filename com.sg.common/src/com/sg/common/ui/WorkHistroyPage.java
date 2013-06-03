package com.sg.common.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.WorkflowService;
import com.sg.widget.configuration.PageConfiguration;
import com.sg.widget.editor.IPageDelegator;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.util.Util;

public class WorkHistroyPage implements IPageDelegator, IFormPart, ISelectionChangedListener {

	private ISingleObjectEditorInput input;
	private TableViewer workTable;
	private SimpleDateFormat sdf;
	private WorkflowService wfService;
	private TableViewer processTable;

	public WorkHistroyPage() {
		sdf = new SimpleDateFormat(Util.SDF_YY_MM_DD_HH_MM);
		wfService = BusinessService.getWorkflowService();
	}

	@Override
	public Composite createPageContent(Composite parent,
			ISingleObjectEditorInput input, PageConfiguration conf) {
		this.input = input;
		SashForm sashForm = new SashForm(parent,SWT.VERTICAL);
		createWorkTable(sashForm);
		createProcessTable(sashForm);
		sashForm.setWeights(new int[] { 1, 3 });
		setInput();
		return sashForm;
	}

	private void createProcessTable(Composite parent) {
		processTable = new TableViewer(parent,SWT.BORDER);
		processTable.getTable().setHeaderVisible(true);
		processTable.getTable().setLinesVisible(true);
		
		TableViewerColumn column;
		
		column = new TableViewerColumn(processTable,SWT.LEFT);
		column.getColumn().setText("活动");
		column.getColumn().setWidth(190);
		column.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				DBObject data = (DBObject)element;
				String text = (String) data.get(IDBConstants.FIELD_WFINFO_TASKNAME);
				return text==null?"":text;
			}
		});

		column = new TableViewerColumn(processTable,SWT.LEFT);
		column.getColumn().setText("执行人");
		column.getColumn().setWidth(80);
		column.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				DBObject data = (DBObject)element;
				String text = (String) data.get(IDBConstants.FIELD_WFINFO_ACTORNAME);
				return text==null?"":text;
			}
		});

		column = new TableViewerColumn(processTable,SWT.LEFT);
		column.getColumn().setText("状态");
		column.getColumn().setWidth(50);
		column.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				DBObject data = (DBObject)element;
				String text = (String) data.get(IDBConstants.FIELD_WF_HISTORY_TASK_OPERATION);
				return text==null?"":text;
			}
		});

		column = new TableViewerColumn(processTable,SWT.LEFT);
		column.getColumn().setText("开始时间");
		column.getColumn().setWidth(130);
		column.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				DBObject data = (DBObject)element;
				Date date = (Date) data.get(IDBConstants.FIELD_WF_HISTORY_OPEN_DATE);
				return date==null?"":sdf.format(date);
			}
		});

		column = new TableViewerColumn(processTable,SWT.LEFT);
		column.getColumn().setText("完成时间");
		column.getColumn().setWidth(130);
		column.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				DBObject data = (DBObject)element;
				Date date = (Date) data.get(IDBConstants.FIELD_WF_HISTORY_CLOSE_DATE);
				return date==null?"":sdf.format(date);
			}
		});

		column = new TableViewerColumn(processTable,SWT.LEFT);
		column.getColumn().setText("决策");
		column.getColumn().setWidth(80);
		column.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				DBObject data = (DBObject)element;
				String text = (String) data.get(IDBConstants.FIELD_WF_HISTORY_CHOICE);
				return text==null?"":text;
			}
		});

		column = new TableViewerColumn(processTable,SWT.LEFT);
		column.getColumn().setText("说明");
		column.getColumn().setWidth(200);
		column.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				DBObject data = (DBObject)element;
				String text = (String) data.get(IDBConstants.FIELD_WF_HISTORY_COMMENT);
				return text==null?"":text;
			}
		});
		
		processTable.setContentProvider(ArrayContentProvider.getInstance());
	}

	private void createWorkTable(Composite parent) {
		workTable = new TableViewer(parent,SWT.BORDER);
		workTable.getTable().setHeaderVisible(true);
		workTable.getTable().setLinesVisible(true);
		
		TableViewerColumn column;
		
		column = new TableViewerColumn(workTable,SWT.LEFT);
		column.getColumn().setText("流程");
		column.getColumn().setWidth(350);
		column.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				DBObject data = (DBObject)element;
				String processDefId = (String) data.get(IDBConstants.FIELD_PROCESS_DEFINITION_ID);
				return wfService.getProcessNameByProcessId(processDefId);
			}
			
		});
		
		column = new TableViewerColumn(workTable,SWT.LEFT);
		column.getColumn().setText("流程状态");
		column.getColumn().setWidth(110);
		column.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				DBObject data = (DBObject)element;
				return (String) data.get(IDBConstants.FIELD_PROCESS_STATUS);
			}
		});
		

		column = new TableViewerColumn(workTable,SWT.LEFT);
		column.getColumn().setText("开始时间");
		column.getColumn().setWidth(125);
		column.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				DBObject data = (DBObject)element;
				Date date = (Date) data.get(IDBConstants.FIELD_PROJECT_ACTUALSTART);
				if(date!=null){
					return sdf.format(date);
				}
				return "";
			}
			
		});
		
		column = new TableViewerColumn(workTable,SWT.LEFT);
		column.getColumn().setText("完成时间");
		column.getColumn().setWidth(125);
		column.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				DBObject data = (DBObject)element;
				Date date = (Date) data.get(IDBConstants.FIELD_PROJECT_ACTUALFINISH);
				if(date!=null){
					return sdf.format(date);
				}
				return "";
			}
			
		});
	
		column = new TableViewerColumn(workTable,SWT.LEFT);
		column.getColumn().setText("流程负责人");
		column.getColumn().setWidth(90);
		column.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				DBObject data = (DBObject)element;
				DBObject pm = (DBObject) data.get(IDBConstants.FIELD_WORK_PM);
				if(pm!=null){
					return DataUtil.getUserLable2(pm);
				}
				return "";
			}
		});
		
		workTable.setContentProvider(ArrayContentProvider.getInstance());
		workTable.addPostSelectionChangedListener(this);
	}
	
	private void setInput(){
		BasicDBList historyList = (BasicDBList) input.getInputData().getValue(IDBConstants.FIELD_DOCUMENT_WORKS);
		workTable.setInput(historyList);
	}

	@Override
	public IFormPart getFormPart() {
		return this;
	}

	@Override
	public void initialize(IManagedForm form) {
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void commit(boolean onSave) {
		
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}

	@Override
	public void setFocus() {
	}

	@Override
	public boolean isStale() {
		return false;
	}

	@Override
	public void refresh() {
		setInput();
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection sel = (IStructuredSelection)event.getSelection();
		if(sel!=null&&!sel.isEmpty()){
			DBObject work = (DBObject) sel.getFirstElement();
			if(work!=null){
				BasicDBList processList = (BasicDBList) work.get(IDBConstants.FIELD_PROCESSHISTORY);
				processTable.setInput(processList);
				return;
			}
		}
		processTable.setInput(null);
	}

}
