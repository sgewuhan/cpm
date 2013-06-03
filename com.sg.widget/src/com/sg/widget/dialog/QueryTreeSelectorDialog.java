package com.sg.widget.dialog;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.sg.db.model.CascadeObject;
import com.sg.db.model.ISingleObject;
import com.sg.widget.util.Util;
import com.sg.widget.viewer.treeviewer.QueryTreeViewer;

public class QueryTreeSelectorDialog extends Dialog implements ISelectionChangedListener {

	
	private String viewerId;
	private ISingleObject selection;
	private QueryTreeViewer viewer;
	private Map<String, Object> parameters;
	private String title;
	private ViewerFilter filter;

	public QueryTreeSelectorDialog(Shell shell,String id) {
		super(shell);
		viewerId = id;
		parameters = new HashMap<String,Object>();
	}

	@Override
	protected Control createContents(Composite parent) {
		getShell().setText(title);
		parent.setLayout(new GridLayout());
		
		viewer = new QueryTreeViewer(parent, SWT.FULL_SELECTION|SWT.VIRTUAL, viewerId);

		CascadeObject expression = viewer.getExpression();
		expression.passParamValueMap(parameters);
		
		viewer.addSelectionChangedListener(this);
		
		GridData gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		gd.heightHint = 400;
		gd.widthHint = 400;
		viewer.getControl().setLayoutData(gd);
		viewer.setAutoExpandLevel(QueryTreeViewer.ALL_LEVELS);

		viewer.updateInputData();
		
		if(filter!=null){
			viewer.setFilters(new ViewerFilter[]{filter});
		}
		return super.createContents(parent);
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
//		ISelection isel = viewer.getSelection();
//		if(Util.isNullOrEmptySelection(isel)){
//			return null;
//		}else{
//			return (ISingleObject)((IStructuredSelection)isel).getFirstElement();
//		}
		return selection;
	}
	
	public void setParameters(String key,Object value){
		parameters.put(key, value);
	}

	public void updateData(){
		viewer.updateInputData();
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setFilter(ViewerFilter viewerFilter) {
		this.filter = viewerFilter;
	}
}
