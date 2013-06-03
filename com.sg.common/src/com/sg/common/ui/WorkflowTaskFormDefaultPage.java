package com.sg.common.ui;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.widget.configuration.PageConfiguration;
import com.sg.widget.editor.IPageDelegator;
import com.sg.widget.editor.ISingleObjectEditorInput;

public class WorkflowTaskFormDefaultPage implements IPageDelegator, IDoubleClickListener {

	public WorkflowTaskFormDefaultPage() {

	}

	@Override
	public Composite createPageContent(Composite parent, ISingleObjectEditorInput input, PageConfiguration conf) {

		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new FillLayout());
		TableViewer viewer = new TableViewer(panel, SWT.NONE);
		Table table = viewer.getTable();
		table.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		table.setData(RWT.CUSTOM_ITEM_HEIGHT, 100);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
		col.setLabelProvider(new WorkflowUserLableProvider());
		col.getColumn().setWidth(70);

		col = new TableViewerColumn(viewer, SWT.NONE);
		col.setLabelProvider(new WorkflowHistoryLableProvider());
		col.getColumn().setWidth(540);

		BasicDBList historyList = (BasicDBList) input.getInputData().getValue(IDBConstants.FIELD_PROCESSHISTORY);
//		if (historyList != null && historyList.size() > 1) {
//			Comparator<? super Object> arg1 = new Comparator<Object>() {
//
//				@Override
//				public int compare(Object arg0, Object arg1) {
//
//					Date date0 = (Date) ((DBObject) arg0).get(IDBConstants.FIELD_WF_HISTORY_DATE);
//					Date date1 = (Date) ((DBObject) arg1).get(IDBConstants.FIELD_WF_HISTORY_DATE);
//					if (date0 != null && date1 != null) {
//						return date1.compareTo(date0);
//					}
//					return 0;
//				}
//
//			};
//			// 对结果进行排序
//			Collections.sort(historyList, arg1);
//		}

		viewer.setInput(historyList);
		viewer.addDoubleClickListener(this);
		return panel;
	}

	@Override
	public IFormPart getFormPart() {
		return null;
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		IStructuredSelection isel = (IStructuredSelection)event.getSelection();
		if(isel.isEmpty()){
			return;
		}
		DBObject element = (DBObject) isel.getFirstElement();
		String comment = (String) element.get(IDBConstants.FIELD_WF_HISTORY_COMMENT);
		if(comment!=null){
			Shell shell = new Shell(Display.getCurrent(),SWT.APPLICATION_MODAL|SWT.DIALOG_TRIM);
			shell.setLayout(new FillLayout());
			shell.setBounds(100, 100, 400, 600);
			Text text = new Text(shell,SWT.MULTI|SWT.WRAP);
			text.setText(comment);
			shell.setVisible(true);
		}
	}

}
