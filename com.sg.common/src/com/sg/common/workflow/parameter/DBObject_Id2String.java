package com.sg.common.workflow.parameter;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;


public class DBObject_Id2String implements IProcessParameterDelegator {

	@Override
	public Object getValue(String processParameter, String taskDatakey, BasicDBObject taskFormData) {
		DBObject dbo = (DBObject) taskFormData.get(taskDatakey);
		if(dbo!=null){
			ObjectId id = (ObjectId) dbo.get(IDBConstants.FIELD_SYSID);
			return id.toString();
		}
		return null;
	}

}
