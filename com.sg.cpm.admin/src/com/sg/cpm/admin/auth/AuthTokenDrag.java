package com.sg.cpm.admin.auth;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class AuthTokenDrag implements DragSourceListener {

	private AuthEditor editor;

	public AuthTokenDrag(AuthEditor authEditor) {
		this.editor = authEditor;
	}

	@Override
	public void dragStart(DragSourceEvent event) {

	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection)editor.tokenViewer.getSelection();
		if(selection == null||selection.isEmpty()){
			event.data = "#";
			return;
		}
		
		Object element = selection.getFirstElement();
		if(element instanceof DBObject){
			BasicDBObject dbo = new BasicDBObject();
			dbo.putAll((DBObject)element);
			dbo.put("action", "remove");
			event.data = JSON.serialize(dbo);
		}else{
			event.data = "#";
		}
		
	}

	@Override
	public void dragFinished(DragSourceEvent event) {

	}

}
