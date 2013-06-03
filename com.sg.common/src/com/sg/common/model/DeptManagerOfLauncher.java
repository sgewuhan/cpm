package com.sg.common.model;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.Assert;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.bpm.service.actor.IActorIdProvider;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.OrganizationService;

public class DeptManagerOfLauncher implements IActorIdProvider {

	private static final String DEPT_MANAGER = "���ž���";

	public DeptManagerOfLauncher() {

	}

	@Override
	public String getActorId(Object[] input) {
		
		Assert.isTrue(input.length>=1);
		
		Assert.isTrue(input[0] instanceof DBObject);
		DBObject pm = (DBObject) ((DBObject)input[0]).get(IDBConstants.FIELD_WORK_PM);
		
		Assert.isNotNull(pm);

		OrganizationService os = BusinessService.getOrganizationService();

		ObjectId launcherOid = (ObjectId) pm.get(IDBConstants.FIELD_SYSID);

		BasicDBList result = os.getTeamOfUser(launcherOid, OrganizationService.TEAM_NOPROJECT);
		if (!result.isEmpty()) {
			DBObject team = (DBObject) result.get(0);
			BasicDBList roles = os.getRolesInTeam((ObjectId) team.get(IDBConstants.FIELD_SYSID));
			for (int i = 0; i < roles.size(); i++) {
				DBObject role = (DBObject) roles.get(i);
				if (DEPT_MANAGER.equals(role.get(IDBConstants.FIELD_DESC))) {
					// ��øý�ɫ�µ����г�Ա
					BasicDBList users = os.getUsersInRole((ObjectId) role.get(IDBConstants.FIELD_SYSID));
					if (users.size() < 1) {
						return null;
					}
					// ֻȡ��һ�� 
					 DBObject usersItem = (DBObject) users.get(0);
					 return (String) usersItem.get(IDBConstants.FIELD_UID);

					// ȡ����������μ�NodAssignment��150����
//					String actors = "";
//					for (int k = 0; k < users.size(); k++) {
//						DBObject usersItem = (DBObject) users.get(k);
//						String uoid = (String) usersItem.get(IDBConstants.FIELD_UID);
//						actors = actors + uoid;
//						if (k != (users.size() - 1)) {
//							actors = actors + ",";
//						}
//					}
//					return actors;
				}
			}
		}

		return null;
	}

}
