package com.sg.document.basic.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.Assert;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.bpm.service.task.ServiceProvider;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.DocumentService;
import com.sg.common.service.OrganizationService;
import com.sg.common.service.WorkService;

public class CreateProject extends ServiceProvider {

	// 以下的这些参数是对应流程中workitem的参数，在流程中，使用DataAssignment将流程中的变量map到这些参数中
	private static final String P_PROJECT_NUMBER = "p_ProjectNumber";

	private static final String P_PROJECT_TEMPLATE_OID = "p_ProjectTemplateOid";

	private static final String P_WORK_OID = "p_WorkOid";

	private static final String P_PM_OID = "p_PMOid";

	public static final String EDITOR_ID_PLAN = "com.sg.cpm.editor.JZ-QR-XG004A";// 科研开发任务书
	public static final String EDITOR_ID_APPLY = "com.sg.cpm.editor.JZ-QR-XG003A";// 立项申请书

	private WorkService workService;

	private OrganizationService organizationService;

	private DocumentService documentService;

	public CreateProject() {

		workService = BusinessService.getWorkService();
		organizationService = BusinessService.getOrganizationService();
		documentService = BusinessService.getDocumentService();

	}

	@Override
	public Map<String, Object> run(Object parameter) {
		String projectId = (String) getInputValue(P_PROJECT_NUMBER);
		String projectTemplateOid = (String) getInputValue(P_PROJECT_TEMPLATE_OID);
		ObjectId templateId = projectTemplateOid == null ? null : new ObjectId(
				projectTemplateOid);
		String strWorkOid = (String) getInputValue(P_WORK_OID);
		ObjectId workId = new ObjectId(strWorkOid);
		String pmOid = (String) getInputValue(P_PM_OID);
		ObjectId pmId = new ObjectId(pmOid);

		// 从工作中获得科研开发任务书上的项目的计划开始和计划完成信息
		List<DBObject> doclist = workService.getDocumentOfWork(workId,
				EDITOR_ID_PLAN);
		Assert.isLegal(doclist != null && doclist.size() > 0,
				"当前的工作下缺少科研开发任务书，无法获得有关的计划信息");

		DBObject planDocument = doclist.get(0);
		// 获取计划信息
		Date planStart = (Date) planDocument
				.get(IDBConstants.FIELD_PROJECT_PLANSTART);
		Date planFinish = (Date) planDocument
				.get(IDBConstants.FIELD_PROJECT_PLANFINISH);

		// 获取负责部门的信息
		ObjectId obsParentId = (ObjectId) planDocument.get("dept");

		// 获取项目名称
		String projectDesc = (String) planDocument.get("projectdesc");

		// 部分创建信息，另一部分创建信息在服务中实现，取文档的信息
		String createUserId = (String) planDocument
				.get(IDBConstants.FIELD_CREATER);
		String createUserName = (String) planDocument
				.get(IDBConstants.FIELD_CREATER_NAME);
		
		Double budget = (Double)planDocument.get(IDBConstants.FIELD_BUDGET);

		DBObject projectData = workService.createProjectFromWork(projectId,
				projectDesc, obsParentId, pmId, planStart, planFinish,budget,
				templateId, true, true, createUserId, createUserName);

		//为项目负责人角色添加当前的项目经理
		ObjectId rootId = (ObjectId) projectData.get(IDBConstants.FIELD_PROJECT_OBS_ROOT);
		if(rootId!=null){
			DBObject role = organizationService.getRoleInProjectByName(rootId, "项目负责人");
			if(role!=null){
				organizationService.createOBSItem(rootId, (ObjectId)role.get(IDBConstants.FIELD_SYSID), null, pmId, IDBConstants.VALUE_OBS_USERTYPE);
			}
		}
		
		//将任务书中的项目团队成员添加到项目团队中
		BasicDBList teamdetail = (BasicDBList) planDocument.get("teamdetail");
		if(teamdetail!=null){
			for(int i=0;i<teamdetail.size();i++){
				DBObject team = (DBObject) teamdetail.get(i);
				DBObject member  = (DBObject) team.get("pm");
				if(member!=null){
					ObjectId uoid = (ObjectId) member.get(IDBConstants.FIELD_SYSID);
					organizationService.createOBSItem(rootId, rootId, null, uoid, IDBConstants.VALUE_OBS_USERTYPE);
				}
			}
		}
		
		
		//返回到流程
		Map<String, Object> output = new HashMap<String,Object>();
		output.put("p_ProjectOid", projectData.get(IDBConstants.FIELD_SYSID).toString());
		
		//更新任务书上的工作令号
		planDocument.put("projectNumber", projectId);
		documentService.saveDocument(planDocument);		
		
		return output;
	}

}
