package com.sg.cpm.admin.auth;

import org.bson.types.ObjectId;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.sg.common.db.IDBConstants;
import com.sg.common.ui.UIConstants;
import com.sg.db.DBActivator;
import com.sg.db.model.CascadeObject;

public class AuthTokenDrop implements DropTargetListener {

	private AuthEditor editor;
	private DBCollection authCollection;

	public AuthTokenDrop(AuthEditor authEditor) {
		this.editor = authEditor;
		authCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_AUTH);
	}

	public void dragEnter(final DropTargetEvent event) {
		event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND;
	}

	public void dragLeave(final DropTargetEvent event) {
	}

	public void dragOperationChanged(final DropTargetEvent event) {
	}

	public void dragOver(final DropTargetEvent event) {
	}

	public void drop(final DropTargetEvent event) {
		String jsonOBS = (String) event.data;
		if (jsonOBS.equals("#")) {
			return;
		}

		DBObject authTargetData = (DBObject) JSON.parse(jsonOBS);
		if(!"insert".equals(authTargetData.get("action"))){
			return;
		}
		
		
		Object token = event.item.getData();
		if (token instanceof CascadeObject) {// 放置到了token上
			Object targetId = authTargetData.get(IDBConstants.FIELD_SYSID);

			// 检查这个target是否已经存在
			Object[] list = editor.getAuthList((CascadeObject) token);
			for (int i = 0; i < list.length; i++) {
				if (targetId.equals(((DBObject) list[i]).get(IDBConstants.FIELD_TARGETID))) {
					MessageDialog.openWarning(editor.getEditorSite().getShell(), UIConstants.TEXT_AUTH_SETTING,
							UIConstants.MESSAGE_AUTH_HAVE_SETTED);
					return;
				}
			}
			// 添加到数据库
			ObjectId contextId = ((CascadeObject) token).getParent().getSystemId();
			Object tokenId = ((CascadeObject) token).getValue(IDBConstants.FIELD_ID);
			String type = authTargetData.get(IDBConstants.FIELD_TEMPLATE_TYPE)==null?IDBConstants.VALUE_OBS_USERTYPE:IDBConstants.VALUE_OBS_ROLETYPE;
			DBObject dbo = new BasicDBObject()
				.append(IDBConstants.FIELD_CONTEXTID, contextId)
				.append(IDBConstants.FIELD_TOKENID,tokenId)
				.append(IDBConstants.FIELD_TARGETID, targetId)
				.append(IDBConstants.FIELD_AUTHVALUE, true)
				.append(IDBConstants.FIELD_TARGETTYPE, type);
			authCollection.insert(dbo);
			
			// 刷新这个token
			editor.tokenViewer.refresh(token);
			
		} else if (token instanceof DBObject) {// 放置到了授权对象上
			return;
		}
	}

	public void dropAccept(final DropTargetEvent event) {
	}

}