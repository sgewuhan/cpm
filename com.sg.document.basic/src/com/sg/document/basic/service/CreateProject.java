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

	// ���µ���Щ�����Ƕ�Ӧ������workitem�Ĳ������������У�ʹ��DataAssignment�������еı���map����Щ������
	private static final String P_PROJECT_NUMBER = "p_ProjectNumber";

	private static final String P_PROJECT_TEMPLATE_OID = "p_ProjectTemplateOid";

	private static final String P_WORK_OID = "p_WorkOid";

	private static final String P_PM_OID = "p_PMOid";

	public static final String EDITOR_ID_PLAN = "com.sg.cpm.editor.JZ-QR-XG004A";// ���п���������
	public static final String EDITOR_ID_APPLY = "com.sg.cpm.editor.JZ-QR-XG003A";// ����������

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

		// �ӹ����л�ÿ��п����������ϵ���Ŀ�ļƻ���ʼ�ͼƻ������Ϣ
		List<DBObject> doclist = workService.getDocumentOfWork(workId,
				EDITOR_ID_PLAN);
		Assert.isLegal(doclist != null && doclist.size() > 0,
				"��ǰ�Ĺ�����ȱ�ٿ��п��������飬�޷�����йصļƻ���Ϣ");

		DBObject planDocument = doclist.get(0);
		// ��ȡ�ƻ���Ϣ
		Date planStart = (Date) planDocument
				.get(IDBConstants.FIELD_PROJECT_PLANSTART);
		Date planFinish = (Date) planDocument
				.get(IDBConstants.FIELD_PROJECT_PLANFINISH);

		// ��ȡ�����ŵ���Ϣ
		ObjectId obsParentId = (ObjectId) planDocument.get("dept");

		// ��ȡ��Ŀ����
		String projectDesc = (String) planDocument.get("projectdesc");

		// ���ִ�����Ϣ����һ���ִ�����Ϣ�ڷ�����ʵ�֣�ȡ�ĵ�����Ϣ
		String createUserId = (String) planDocument
				.get(IDBConstants.FIELD_CREATER);
		String createUserName = (String) planDocument
				.get(IDBConstants.FIELD_CREATER_NAME);
		
		Double budget = (Double)planDocument.get(IDBConstants.FIELD_BUDGET);

		DBObject projectData = workService.createProjectFromWork(projectId,
				projectDesc, obsParentId, pmId, planStart, planFinish,budget,
				templateId, true, true, createUserId, createUserName);

		//Ϊ��Ŀ�����˽�ɫ��ӵ�ǰ����Ŀ����
		ObjectId rootId = (ObjectId) projectData.get(IDBConstants.FIELD_PROJECT_OBS_ROOT);
		if(rootId!=null){
			DBObject role = organizationService.getRoleInProjectByName(rootId, "��Ŀ������");
			if(role!=null){
				organizationService.createOBSItem(rootId, (ObjectId)role.get(IDBConstants.FIELD_SYSID), null, pmId, IDBConstants.VALUE_OBS_USERTYPE);
			}
		}
		
		//���������е���Ŀ�Ŷӳ�Ա��ӵ���Ŀ�Ŷ���
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
		
		
		//���ص�����
		Map<String, Object> output = new HashMap<String,Object>();
		output.put("p_ProjectOid", projectData.get(IDBConstants.FIELD_SYSID).toString());
		
		//�����������ϵĹ������
		planDocument.put("projectNumber", projectId);
		documentService.saveDocument(planDocument);		
		
		return output;
	}

}
