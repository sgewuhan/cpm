package com.sg.widget.dialog;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;

import com.sg.db.model.ISingleObject;
import com.sg.widget.util.Util;
import com.sg.widget.viewer.tableviewer.QueryTableViewer;

public class QueryTableSelectorDialog extends FormDialog implements ISelectionChangedListener {

	
	private String viewerId;
	private ISingleObject selection;
	private QueryTableViewer viewer;

	public QueryTableSelectorDialog(Shell shell,String id) {
		super(shell);
		viewerId = id;
	}

	@Override
	protected void createFormContent(IManagedForm mform) {
		Composite body = mform.getForm().getBody();
		viewer = new QueryTableViewer(body, SWT.FULL_SELECTION|SWT.VIRTUAL, viewerId);
		viewer.addSelectionChangedListener(this);
		super.createFormContent(mform);
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection isel = event.getSelection();
		if(Util.isNullOrEmptySelection(isel)){
			selection = null;
		}else{
			selection = (ISingleObject)((IStructuredSelection)isel).getFirstElement();
		}
	}

	public ISingleObject getSelection(){
		return selection;
	}

}
