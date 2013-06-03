package com.sg.widget.dialog;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.db.DBActivator;
import com.sg.widget.util.Util;
import com.sg.widget.viewer.KeyNavigator;
import com.sg.widget.viewer.labelprovider.DBObjectColumnLabelProvider;
import com.sg.widget.viewer.sorter.UniSorter;

public class DBObjectSelectorDialog extends Dialog implements ISelectionChangedListener {

	
	private IStructuredSelection selection;
	private String title;
	private TableViewer viewer;
	private DBObject query;
	private DBObject resultFields;
	private String collectionName;
	private String[] columnsNames;

	public DBObjectSelectorDialog(Shell shell) {
		super(shell);
	}
	
	public void setQuery(DBObject query){
		this.query = query;
	}
	
	public void setResultFields(DBObject resultFields){
		this.resultFields = resultFields;
	}
	
	public void setCollection(String collectionName){
		this.collectionName = collectionName;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	protected Control createContents(Composite parent) {
		getShell().setText(title);
		parent.setLayout(new GridLayout());
		
		viewer = new TableViewer(parent, SWT.FULL_SELECTION|SWT.MULTI|SWT.VIRTUAL);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.getTable().setHeaderVisible(columnsNames!=null);
		
		if(resultFields!=null){
			int i=0;
			Iterator<String> iter = resultFields.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();
				TableViewerColumn col = new TableViewerColumn(viewer, SWT.LEFT);
				col.setLabelProvider(new DBObjectColumnLabelProvider(key));
				if(columnsNames!=null&&i<columnsNames.length){
					col.getColumn().setText(columnsNames[i++]);
					new UniSorter(viewer, col.getColumn(), key);
				}else{
					col.getColumn().setText(key);
				}
			}
		}
		
		setInput();
		
		viewer.addSelectionChangedListener(this);
		
		GridData gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		gd.heightHint = 400;
		gd.widthHint = 400;
		viewer.getControl().setLayoutData(gd);

		new KeyNavigator(viewer);
		return super.createContents(parent);
	}


	private void setInput() {
		DBCollection collection = DBActivator.getDefaultDBCollection(collectionName);
		if(collection!=null){
			DBCursor result = collection.find(query, resultFields);
			List<DBObject> input = result.toArray();
			viewer.setInput(input);
			Util.packTableViewer(viewer);
		}
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		selection = (IStructuredSelection) event.getSelection();
	}

	public IStructuredSelection getSelection(){
		return selection;
	}
	

	public static IStructuredSelection OPEN(Shell shell,String title,String colletionName,DBObject query,DBObject resultFields,String[] colnames){
		DBObjectSelectorDialog s = new DBObjectSelectorDialog(shell);
		s.setQuery(query);
		s.setResultFields(resultFields);
		s.setCollection(colletionName);
		s.setTitle(title);
		s.setColumnsNames(colnames);
		int ok = s.open();
		if(ok==Dialog.OK){
			return s.selection;
		}else{
			return null;
		}
		
	}

	private void setColumnsNames(String[] colnames) {
		columnsNames = colnames;
	}

}
