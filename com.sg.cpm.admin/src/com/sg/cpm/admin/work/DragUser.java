package com.sg.cpm.admin.work;

import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.sg.common.db.IDBConstants;


public class DragUser implements DragSourceListener {


	public DragUser(WorkTemplateEditor editor) {
		
	}

	@Override
	public void dragStart(DragSourceEvent event) {


	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		DragSource src = (DragSource)event.getSource();
		Table control = (Table) src.getControl();
		TableItem[] selectedItems = control.getSelection();
		if(selectedItems == null || selectedItems.length <1){
			event.data = "#";
			return;
		}

		DBObject element = new BasicDBObject().append("operation", "set").append("data",((DBObject) selectedItems[0].getData()).get(IDBConstants.FIELD_SYSID));
		event.data = JSON.serialize(element);
	}

	@Override
	public void dragFinished(DragSourceEvent event) {

	}

}
