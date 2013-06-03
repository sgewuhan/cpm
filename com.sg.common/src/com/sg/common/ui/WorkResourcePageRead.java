package com.sg.common.ui;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.IFormPart;

import com.mongodb.BasicDBList;
import com.sg.common.db.IDBConstants;
import com.sg.widget.configuration.PageConfiguration;
import com.sg.widget.editor.IPageDelegator;
import com.sg.widget.editor.ISingleObjectEditorInput;

public class WorkResourcePageRead implements IPageDelegator {


	@Override
	public Composite createPageContent(Composite parent, ISingleObjectEditorInput input, PageConfiguration cpage) {
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new FillLayout());
		TableViewer resourceViewer = new TableViewer(panel, SWT.NONE);
		Table table = resourceViewer.getTable();
		table.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		table.setData(RWT.CUSTOM_ITEM_HEIGHT, 70);
		resourceViewer.setContentProvider(ArrayContentProvider.getInstance());
		TableViewerColumn col = new TableViewerColumn(resourceViewer, SWT.NONE);
		col.setLabelProvider(new UserLableProvider());
		col.getColumn().setWidth(350);
		BasicDBList resourceList = (BasicDBList) input.getInputData().getValue(IDBConstants.FIELD_WORK_RESOURCE);
		resourceViewer.setInput(resourceList);
		return panel;
	}

	@Override
	public IFormPart getFormPart() {

		return null;
	}


}
