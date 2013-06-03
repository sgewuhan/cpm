package com.sg.document.editor;

import java.util.Iterator;

import org.bson.types.ObjectId;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.OrganizationService;
import com.sg.widget.dialog.DBObjectSelectorDialog;
import com.sg.widget.editor.field.IAddTableItemHandler;

public class ChiefEngineerAddingHandler implements IAddTableItemHandler {

	private OrganizationService orgService;

	public ChiefEngineerAddingHandler() {
		orgService = BusinessService.getOrganizationService();
	}

	@Override
	public boolean addItem(BasicDBList inputData) {

		// 先获得技术中心
		DBObject dept = orgService.getTeamByName("技术中心");
		ObjectId deptId = (ObjectId) dept.get(IDBConstants.FIELD_SYSID);

		DBObject role = orgService.getRoleInTeamByName(deptId, "首席技术专家");
		BasicDBList users = orgService.getUsersInRole((ObjectId) role
				.get(IDBConstants.FIELD_SYSID));

		BasicDBList usersId = new BasicDBList();
		for (int i = 0; i < users.size(); i++) {
			usersId.add(((DBObject) users.get(i)).get(IDBConstants.FIELD_SYSID));
		}

		DBObject returnFields = new BasicDBObject();
		returnFields.put(IDBConstants.FIELD_NAME, 1);
		returnFields.put(IDBConstants.FIELD_DESC, 1);
		returnFields.put(IDBConstants.FIELD_UID, 1);

		DBObject query = new BasicDBObject();
		query.put(IDBConstants.FIELD_SYSID,
				new BasicDBObject().append("$in", usersId));

		IStructuredSelection selection = DBObjectSelectorDialog.OPEN(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell(),
				"请选择首席技术专家", IDBConstants.COLLECTION_USER, query, returnFields, new String[]{"姓名","ID"});
		if (selection != null && !selection.isEmpty()) {
			Iterator iter = selection.iterator();
			while (iter.hasNext()) {
				DBObject selectedItem = (DBObject) iter.next();
				if (selectedItem != null) {
					inputData.add(selectedItem);
				}
			}
			return true;
		}
		return false;
	}

}
