package com.sg.cpm.admin.auth;

import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.sg.common.db.IDBConstants;
import com.sg.db.DBActivator;

public class AuthTargetDrop implements DropTargetListener {

	private AuthEditor editor;
	private DBCollection authCollection;

	public AuthTargetDrop(AuthEditor authEditor) {
		this.editor = authEditor;
		authCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_AUTH);

	}

	@Override
	public void dragEnter(DropTargetEvent event) {

	}

	@Override
	public void dragLeave(DropTargetEvent event) {

	}

	@Override
	public void dragOperationChanged(DropTargetEvent event) {

	}

	@Override
	public void dragOver(DropTargetEvent event) {

	}

	@Override
	public void drop(DropTargetEvent event) {
		String jsonOBS = (String) event.data;
		if (jsonOBS.equals("#")) {
			return;
		}

		DBObject authTokenData = (DBObject) JSON.parse(jsonOBS);
		if(!"remove".equals(authTokenData.get("action"))){
			return;
		}
		
		authCollection.remove(new BasicDBObject().append(IDBConstants.FIELD_SYSID, authTokenData.get(IDBConstants.FIELD_SYSID)));
		
		editor.updateTokenViewer();
		
	}

	@Override
	public void dropAccept(DropTargetEvent event) {

	}

}
