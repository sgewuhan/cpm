package com.sg.document.editor.option;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.OrganizationService;
import com.sg.db.DBActivator;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.field.option.IOptionProvider;
import com.sg.widget.resource.Enumerate;

public class SubDeptOfWorkingDept implements IOptionProvider {

	public SubDeptOfWorkingDept() {
	}

	@Override
	public Enumerate getOption(ISingleObjectEditorInput input,
			ISingleObject data, String key, Object value) {
		List<Enumerate> list = new ArrayList<Enumerate>();
		Enumerate e = new Enumerate(key, data.toString(), data, list);

		OrganizationService os = BusinessService.getOrganizationService();
		DBObject user = os.getCurrentUserData();
		BasicDBList parentTeam = os.getTeamOfUser(
				(ObjectId) user.get(OrganizationService.FIELD_SYSID),
				OrganizationService.TEAM_NOPROJECT);

		BasicDBList teamList = new BasicDBList();
		if (parentTeam != null && parentTeam.size() > 0) {
			for (int index = 0; index < parentTeam.size(); index++) {
				List<DBObject> teamDataList = os
						.getSubTeam((ObjectId) ((DBObject)parentTeam.get(index))
								.get(OrganizationService.FIELD_SYSID));
				for (int i = 0; i < teamDataList.size(); i++) {
					teamList.add(teamDataList.get(i).get(
							OrganizationService.FIELD_SYSID));
				}
			}
		}

		DBCollection obsCollection = DBActivator
				.getDefaultDBCollection(IDBConstants.COLLECTION_ORG);
		DBCursor cur = obsCollection.find(
				new BasicDBObject().append(IDBConstants.FIELD_SYSID,
						new BasicDBObject().append("$in", teamList)),
				new BasicDBObject().append(IDBConstants.FIELD_SYSID, 1).append(
						IDBConstants.FIELD_DESC, 1));

		while (cur.hasNext()) {
			DBObject obsItem = cur.next();
			Object id = obsItem.get(IDBConstants.FIELD_SYSID);

			list.add(new Enumerate("", obsItem.get(
					IDBConstants.FIELD_DESC).toString(), id, null));
		}

		return e;
	}

}
