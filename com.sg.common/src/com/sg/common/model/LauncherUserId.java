package com.sg.common.model;


import org.eclipse.core.runtime.Assert;

import com.mongodb.DBObject;
import com.sg.bpm.service.actor.IActorIdProvider;
import com.sg.common.db.IDBConstants;


public class LauncherUserId implements IActorIdProvider {

	public LauncherUserId() {

	}

	@Override
	public String getActorId(Object[] input) {
		Assert.isTrue(input.length>=1);
		
		Assert.isTrue(input[0] instanceof DBObject);
		DBObject pm = (DBObject) ((DBObject)input[0]).get(IDBConstants.FIELD_WORK_PM);
		
		Assert.isNotNull(pm);
		

		return (String) pm.get(IDBConstants.FIELD_UID);
//		return UserSessionContext.getSession().getUserId();
	}

}
