package com.sg.common.service;

import java.util.HashMap;

import org.bson.types.ObjectId;
import org.drools.process.core.Work;
import org.eclipse.core.runtime.Assert;
import org.jbpm.workflow.core.node.HumanTaskNode;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.bpm.service.BPM;
import com.sg.bpm.service.RuleAssignment;
import com.sg.common.BusinessService;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;

public class NodeAssignment {

	private String actorParameter;

	private ObjectId assignmentId;

	private String ruleAssignmentName;

	private boolean isRuleAssignment;

	private boolean isDynamic;

	private boolean isStaticGroup;

	private boolean isStaticActor;

	protected NodeAssignment(HumanTaskNode node) throws ServiceException  {

		Work work = node.getWork();
		String param = (String) work.getParameter(IDBConstants.WF_ACTOR);
		if (param == null || param.length() < 1) {
			// 没有设置角色的，需要判断groupid
			param = (String) work.getParameter(IDBConstants.WF_GROUP);
			if (param != null) {
				actorParameter = DataUtil.getParameterName(param);// 截取#{}中间的内容，例如：#{act_rule_abc}
																	// 截取后变成
																	// act_rule_abc
				if(actorParameter!=null){//返回空是流程中定义了明确的组
					isDynamic = true;
				}else{
					isStaticGroup = true;
				}
				isStaticActor = false;
			}else{
				throw(new ServiceException(ServiceException.NO_WORK_ACTOR_OR_GROUP));//既没有人的定义又没有组的定义
			}
		} else {
			
			actorParameter = DataUtil.getParameterName(param);// 截取#{}中间的内容，例如：#{act_rule_abc}
																// 截取后变成
																// act_rule_abc
			if(actorParameter!=null){//返回空是流程中定义了明确的组
				isDynamic = false;
			}else{
				//如果actorParameter为空，则是流程中定义了明确的人
				isStaticActor = true;
			}
			isStaticGroup = false;
		}
		// 以act_rule_开头的
		Assert.isNotNull(actorParameter, "无法从执行人或组参数获得值："+param);

		try{
			if(actorParameter.toLowerCase().startsWith("act_rule")){
				ruleAssignmentName = BPM.getRuleService().getActorRuleName(actorParameter.substring(9));
			}
		}catch(Exception e){
		}
		if (ruleAssignmentName != null) {
			
			isRuleAssignment = true;
		}

	}
	
	public boolean isDyanmic(){
		return isDynamic;
	}

	public boolean isStaticGroup(){
		return isStaticGroup;
	}
	
	public boolean isStaticActor(){
		return isStaticActor;
	}
	
	public boolean isNotNeedAssignment() {

		return isRuleAssignment() || actorParameter == null||isDynamic;//规则指定或者是静态指定或者是动态指定
	}

	public boolean isNeedAssignment() {

		return actorParameter != null && assignmentId == null && (!isRuleAssignment())&&(!isDynamic);
	}

	public boolean isAlreadyAssignment() {

		return actorParameter != null && assignmentId != null;
	}

	public ObjectId getAssignmentId() {

		return assignmentId;
	}

	public boolean isRuleAssignment() {

		return isRuleAssignment;
	}

	public String getNodeActorParameter() {

		return actorParameter;
	}

	public void setAssignmentId(ObjectId objectId) {

		assignmentId = objectId;
	}

	public String getRuleAssignmentName() {

		return ruleAssignmentName;
	}

	public void setParameterMap(HashMap<String, String> params, Object[] context) {

		if (params.containsKey(actorParameter)) {
			return;
		}

		OrganizationService os = BusinessService.getOrganizationService();

		if (isAlreadyAssignment()) {
			DBObject obsItem = os.getOBSItemData(assignmentId);
			BasicDBList list = os.getUsersUnderOBSItem((ObjectId) obsItem.get(OrganizationService.FIELD_SYSID));
			if (list.size() > 0) {

				// 只取一个的情况：
				DBObject item = (DBObject) list.get(0);
				String userIds = (String) item.get(OrganizationService.FIELD_UID);
				params.put(actorParameter, userIds);
				// 取多个的情况 还不明确如何实现，这个问题在下面的分子rule上面依然存在

				// String userIds = "";
				// for (int i = 0; i < list.size(); i++) {
				// DBObject item = (DBObject) list.get(i);
				// String id = (String) item.get(OrganizationService.FIELD_UID);
				// userIds = userIds + id;
				// if (i != list.size() - 1) {
				// userIds = userIds + ",";
				// }
				// }
				// params.put(actorParameter, userIds);
			}
		} else if (isRuleAssignment) {
			RuleAssignment rules = BPM.getRuleService().getRuleAssignment(actorParameter.substring(9));
			String actorId = rules.getActorId(context);
			if (actorId != null) {
				params.put(actorParameter, actorId);
			}
		}
	}

}
