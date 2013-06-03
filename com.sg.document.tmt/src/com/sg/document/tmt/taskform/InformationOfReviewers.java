package com.sg.document.tmt.taskform;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.IWorkflowInfoProvider;

public class InformationOfReviewers implements IWorkflowInfoProvider {

	public InformationOfReviewers() {
	}

	@Override
	public Object getWorkflowInformation(DBObject workData) {
		StringBuffer information = new StringBuffer();
		// 获得会议主持人的信息
		DBObject act_review_convener = (DBObject) workData.get("act_review_convener");
		if(act_review_convener!=null){
			information.append("确定评审会主持人：");
			information.append(act_review_convener.get(IDBConstants.FIELD_NAME));
		}
		BasicDBList act_reviewer_list = (BasicDBList) workData.get("act_reviewer_list");
		if(act_reviewer_list !=null &&act_reviewer_list .size()>0){
			information.append(",确定专家：");
			for(int i=0;i<act_reviewer_list.size();i++){
				DBObject user = (DBObject) act_reviewer_list.get(i);
				if(i!=0){
					information.append(",");
				}
				information.append(user.get(IDBConstants.FIELD_NAME));
			}
		}
		
		return information.toString();
	}

}
