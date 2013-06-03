package com.sg.document.tmt.taskform;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.parameter.IProcessParameterDelegator;

public class GetReviewerList implements
		IProcessParameterDelegator {

	public GetReviewerList() {
	}

	@Override
	public Object getValue(String processParameter, String taskDatakey,
			BasicDBObject taskFormData) {
		List<String> actorList = new ArrayList<String>();
		
//		DBObject converData = (DBObject) taskFormData.get("act_review_convener");
//		actorList.add((String) converData.get(IDBConstants.FIELD_UID));
		
		BasicDBList userListData = (BasicDBList) taskFormData.get(taskDatakey);
		if(userListData!=null){
			for(int i=0;i<userListData.size();i++){
				DBObject user = (DBObject) userListData.get(i);
				String uid = (String) user.get(IDBConstants.FIELD_UID);
				if(!actorList.contains(uid)){
					actorList.add(uid);
				}
			}
			
		}
		return actorList;
	}

}
