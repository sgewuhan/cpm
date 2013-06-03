package com.sg.cpm.admin.auth;

import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class AuthTargetDrag implements DragSourceListener {




	public AuthTargetDrag(AuthEditor authEditor) {
	}

	public void dragFinished(final DragSourceEvent event) {
	}

	public void dragSetData(final DragSourceEvent event) {
		//
		DragSource src = (DragSource)event.getSource();
		Table control = (Table) src.getControl();
		TableItem[] selectedItems = control.getSelection();
		if(selectedItems == null || selectedItems.length <1){
			event.data = "#";
			return;
		}

		DBObject element = (DBObject) selectedItems[0].getData();
		BasicDBObject dbo = new BasicDBObject();
		dbo.putAll((DBObject)element);
		dbo.put("action", "insert");
		event.data = JSON.serialize(dbo);
		
	}

	public void dragStart(final DragSourceEvent event) {
	}
}
