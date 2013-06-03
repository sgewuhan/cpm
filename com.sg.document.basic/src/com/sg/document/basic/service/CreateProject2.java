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

	//���µ���Щ�����Ƕ�Ӧ������workitem�Ĳ������������У�ʹ��DataAssignment�������еı���map����Щ������
	private static final String P_PROJECT_NUMBER = "p_ProjectNumber";
	
	private static final String P_PROJECT_TEMPLATE_OID = "p_ProjectTemplateOid";

	private static final String P_WORK_OID = "p_WorkOid";
	
	private static final String P_PM_OID = "p_PMOid";
	
	public static final String EDITOR_ID = "com.sg.cpm.editor.JZ-QR-XG003A--1";// ί�п�������

	
	
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
		
		//�ӹ����л�ÿ��п����������ϵ���Ŀ�ļƻ���ʼ�ͼƻ������Ϣ
		WorkService workService = BusinessService.getWorkService();
		List<DBObject> doclist = workService.getDocumentOfWork(workId,EDITOR_ID);
		Assert.isLegal(doclist!=null&&doclist.size()>0, "��ǰ�Ĺ�����ȱ�ټ���֧��ί�е����޷�����йصļƻ���Ϣ");
		
		DBObject doc = doclist.get(0);
		//��ȡ�ƻ���Ϣ
		Date planStart = new Date();
		Date planFinish = (Date) doc.get(IDBConstants.FIELD_PROJECT_PLANFINISH);
		
		//��ȡ�����ŵ���Ϣ
		ObjectId obsParentId = (ObjectId) doc.get("dept");
		
		//��ȡ��Ŀ����
		String projectDesc = (String) doc.get("projectdesc");
		
		//���ִ�����Ϣ����һ���ִ�����Ϣ�ڷ�����ʵ�֣�ȡ�ĵ�����Ϣ
		String createUserId = (String) doc.get(IDBConstants.FIELD_CREATER);
		String createUserName = (String) doc.get(IDBConstants.FIELD_CREATER_NAME);
		Double budget = (Double)doc.get(IDBConstants.FIELD_BUDGET);

		DBObject projectData = workService.createProjectFromWork(projectId,projectDesc,obsParentId,pmId,planStart,planFinish,budget,templateId,true,true,createUserId,createUserName);
		
		//Ϊ��Ŀ�����˽�ɫ��ӵ�ǰ����Ŀ����
		OrganizationService organizationService = BusinessService.getOrganizationService();
		ObjectId obsRoot = (ObjectId) projectData.get(IDBConstants.FIELD_PROJECT_OBS_ROOT);
		if(obsRoot!=null){
			DBObject role = organizationService.getRoleInProjectByName(obsRoot, "��Ŀ������");
			if(role!=null){
				organizationService.createOBSItem(obsRoot, (ObjectId)role.get(IDBConstants.FIELD_SYSID), null, pmId, IDBConstants.VALUE_OBS_USERTYPE);
			}
		}
		
		Map<String, Object> output = new HashMap<String,Object>();
		output.put("p_ProjectOid", projectData.get(IDBConstants.FIELD_SYSID).toString());
		return output;
	}

}
