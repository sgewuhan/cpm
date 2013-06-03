package com.sg.document.basic.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.Assert;

import com.mongodb.DBObject;
import com.sg.bpm.service.task.ServiceProvider;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.OrganizationService;
import com.sg.common.service.WorkService;


public class CreateProject2 extends ServiceProvider  {

	//以下的这些参数是对应流程中workitem的参数，在流程中，使用DataAssignment将流程中的变量map到这些参数中
	private static final String P_PROJECT_NUMBER = "p_ProjectNumber";
	
	private static final String P_PROJECT_TEMPLATE_OID = "p_ProjectTemplateOid";

	private static final String P_WORK_OID = "p_WorkOid";
	
	private static final String P_PM_OID = "p_PMOid";
	
	public static final String EDITOR_ID = "com.sg.cpm.editor.JZ-QR-XG003A--1";// 委托开发申请

	
	
	public CreateProject2() {

	}

	@Override
	public Map<String, Object> run(Object parameter) {
		String projectId = (String) getInputValue(P_PROJECT_NUMBER);
		String projectTemplateOid = (String) getInputValue(P_PROJECT_TEMPLATE_OID);
		ObjectId templateId = projectTemplateOid==null?null:new ObjectId(projectTemplateOid);
		String strWorkOid = (String) getInputValue(P_WORK_OID);
		ObjectId workId = new ObjectId(strWorkOid);
		String pmOid = (String) getInputValue(P_PM_OID );
		ObjectId pmId = new ObjectId(pmOid);
		
		//从工作中获得科研开发任务书上的项目的计划开始和计划完成信息
		WorkService workService = BusinessService.getWorkService();
		List<DBObject> doclist = workService.getDocumentOfWork(workId,EDITOR_ID);
		Assert.isLegal(doclist!=null&&doclist.size()>0, "当前的工作下缺少技术支持委托单，无法获得有关的计划信息");
		
		DBObject doc = doclist.get(0);
		//获取计划信息
		Date planStart = new Date();
		Date planFinish = (Date) doc.get(IDBConstants.FIELD_PROJECT_PLANFINISH);
		
		//获取负责部门的信息
		ObjectId obsParentId = (ObjectId) doc.get("dept");
		
		//获取项目名称
		String projectDesc = (String) doc.get("projectdesc");
		
		//部分创建信息，另一部分创建信息在服务中实现，取文档的信息
		String createUserId = (String) doc.get(IDBConstants.FIELD_CREATER);
		String createUserName = (String) doc.get(IDBConstants.FIELD_CREATER_NAME);
		Double budget = (Double)doc.get(IDBConstants.FIELD_BUDGET);

		DBObject projectData = workService.createProjectFromWork(projectId,projectDesc,obsParentId,pmId,planStart,planFinish,budget,templateId,true,true,createUserId,createUserName);
		
		//为项目负责人角色添加当前的项目经理
		OrganizationService organizationService = BusinessService.getOrganizationService();
		ObjectId obsRoot = (ObjectId) projectData.get(IDBConstants.FIELD_PROJECT_OBS_ROOT);
		if(obsRoot!=null){
			DBObject role = organizationService.getRoleInProjectByName(obsRoot, "项目负责人");
			if(role!=null){
				organizationService.createOBSItem(obsRoot, (ObjectId)role.get(IDBConstants.FIELD_SYSID), null, pmId, IDBConstants.VALUE_OBS_USERTYPE);
			}
		}
		
		Map<String, Object> output = new HashMap<String,Object>();
		output.put("p_ProjectOid", projectData.get(IDBConstants.FIELD_SYSID).toString());
		return output;
	}

}
