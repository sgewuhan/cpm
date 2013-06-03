package com.sg.common.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.DBActivator;

public class WorkService extends CommonService {

	/**
	 * project object sample { "_id" : ObjectId("4fd604607522e7a07f16e3ad"),
	 * "pm" : {//user ���� "_id" : ObjectId("4fb5ef68e7e3d859cd54e0ae"),
	 * "activate" : true, "desc" : "tsx", "name" : "̷˧ϼ", "uid" : "20103794",
	 * "email" : "tanshx@teg.cn", "thumb" : [{ "_id" :
	 * ObjectId("4fcdf8a282768b4790fbd9df"), "namespace" : "user_thumb",
	 * "fileName" : "1.png" }], "sitename" : null, "urllabel" : "....." },
	 * "planstart" : ISODate("2012-06-12T14:44:28.131Z"), "planfinish" :
	 * ISODate("2012-06-15T14:44:28.753Z"), "obsparent" :
	 * ObjectId("4fbe838c38040cb45eebd819"),//��������֯oid "template" :
	 * {//template���� "_id" : ObjectId("4fbe97ed3804e599a12f31ce"), "activate" :
	 * true, "desc" : "��Ʒ����Ŀģ��", "siteparent" :
	 * ObjectId("4fb5e513e7e35292f1713ab0"), "thumb" : [{ "_id" :
	 * ObjectId("4fd10f457d58c5488ba0ec7f"), "namespace" : "project_thumb",
	 * "fileName" : "projectcover1.png" }] }, "createfbsfromwbs" : true, "desc"
	 * : "AAA", "obsroot" : ObjectId("4fd604607522e7a07f16e3ab"),//��Ŀ���oid
	 * "folderRoot" : ObjectId("4fd604607522e7a07f16e3ac"),//��Ŀ�ļ���oid "id" :
	 * "000003", "creator" : "20103794", "creator_desc" : "tsx", "owner" :
	 * "20103794", "owner_desc" : "tsx", "createdate" :
	 * ISODate("2012-06-11T14:44:48.138Z") }
	 **/

	/**
	 * <p>
	 * Get Document template from a work template
	 * </p>
	 * 
	 * @author hua
	 * @since 1.0
	 * @param workTemplateId
	 * @return document template list, will not <code>null</code>
	 */
	public BasicDBList getDocumentTemplateOfWorkTemplate(ObjectId workTemplateId) {

		DBObject ref = new BasicDBObject().append(FIELD_WBSPARENT,
				workTemplateId).append(FIELD_TEMPLATE_TYPE,
				VALUE_WBS_DOCUMENT_TYPE);
		DBCursor cursor = workTemplateCollection.find(ref);
		BasicDBList result = new BasicDBList();
		while (cursor.hasNext()) {
			result.add(cursor.next());
		}
		return result;
	}

	/**
	 * �н���������Ҫ��
	 * 
	 * @param workData
	 * @param startImmediately
	 */
	public void createWork(DBObject workData, boolean startImmediately) {
		createWork(workData, startImmediately, null);
	}

	/**
	 * sitenameΪ��ʱ���н���������Ҫ�󣬷�֮�޽���������Ҫ��
	 * 
	 * @param workData
	 * @param startImmediately
	 * @param sitename
	 */
	public void createWork(DBObject workData, boolean startImmediately,
			String sitename) {

		// ����ģ�� ���ѡ����ģ��
		// ��ģ��Ĳ����߲�ѯ���û� ���õ��������Դ��
		// ����workData�ĳ�ʼ�����
		checkSystemId(workData);

		setupWorkResource(workData);

		// �������̶���
		setProcessDefinition(workData);

		// �����Ҫ��������������workData��ֵ
		if (startImmediately) {
			workData.put(FIELD_PROCESS_STATUS, VALUE_PROCESS_PROCESS);
			workData.put(FIELD_PROJECT_ACTUALSTART, new Date());
		} else {
			workData.put(FIELD_PROCESS_STATUS, VALUE_PROCESS_READY);
		}
		// ����ϵͳ��Ϣ
		setSystemCreateInfo(workData);

		// ���������¼
		workCollection.save(workData);

		saveUserMessage(workData);

		// �����ĵ� ����ģ����ĵ� �����µ��ĵ��ҽӵ�������
		setupDocument(workData);

		if (startImmediately) {
			// ��������
			try {
				if (sitename == null) {
					BusinessService.getWorkflowService().startWorkProcess(
							workData);
				} else {
					BusinessService.getWorkflowService().startWorkProcess(
							workData, sitename);
				}
			} catch (ServiceException e) {

				e.openMessageBox();
			}
		}
	}

	private void setProcessDefinition(DBObject workData) {

		// �����̶����Լ������߶�����Ϣ���Ƶ�������
		ObjectId workTemplateId = (ObjectId) workData.get(FIELD_TEMPLATE);
		if (workTemplateId == null) {
			return;
		}
		DBObject workTemplate = BusinessService.getWorkService()
				.getWorkTemplateObject(workTemplateId);
		String processDefinitionId = (String) workTemplate
				.get(FIELD_PROCESS_DEFINITION_ID);
		DBObject asgnDefinition = (DBObject) workTemplate
				.get(FIELD_WORK_PROCESS_ASSINGMENT_DEFINITION);
		workData.put(FIELD_PROCESS_DEFINITION_ID, processDefinitionId);
		workData.put(FIELD_WORK_PROCESS_ASSINGMENT_DEFINITION, asgnDefinition);
	}

	private void saveUserMessage(DBObject workData) {

		// ͬ���û����� ͬ�������������Ϣ

		ObjectId workId = (ObjectId) workData.get(FIELD_SYSID);

		ObjectId newChargerId = null;
		if (workData != null) {
			DBObject newCharger = (DBObject) workData.get(FIELD_WORK_PM);
			newChargerId = (ObjectId) newCharger.get(FIELD_SYSID);
		}

		// ͬ�������������Ϣ
		// *******************************************************************************************************

		if (DataUtil.isReady(workData) || DataUtil.isProcess(workData)) {
			// ���û��������Ϣ��Ҫ�����û�����������Ϣ
			String pd = (String) workData.get(FIELD_PROCESS_DEFINITION_ID);
			if (pd != null && pd.length() > 0) {// ֻ���渺���ˣ����Ƿ����˵�

				DataUtil.saveUserRelationInformation(null, newChargerId,
						COLLECTION_USER_WORK_IN_CHARGED, workId);

			} else {// û�����̶���ֱ�ӷ���������

				DataUtil.saveUserRelationInformation(null, newChargerId,
						COLLECTION_USER_WORK_IN_CHARGED, workId);

				BasicDBList resourceList = (BasicDBList) workData
						.get(FIELD_WORK_RESOURCE);
				DataUtil.saveUserWorkAndProjectInformation(null, resourceList,
						COLLECTION_USER_WORK_PARTTICIPATED, workId);
			}

		}
		// *******************************************************************************************************

	}

	private void setupDocument(DBObject workData) {

		ObjectId workTemplateId = (ObjectId) workData.get(FIELD_TEMPLATE);
		if (workTemplateId == null) {
			return;
		}
		// ȡ���ĵ�ģ��
		BasicDBList documentList = BusinessService.getWorkService()
				.getDocumentTemplateOfWorkTemplate(workTemplateId);
		if (documentList.isEmpty()) {
			return;
		}

		ObjectId workId = (ObjectId) workData.get(FIELD_SYSID);

		for (int i = 0; i < documentList.size(); i++) {
			DBObject documentTemplate = (DBObject) documentList.get(i);
			ObjectId templateId = (ObjectId) documentTemplate.get(FIELD_SYSID);
			BusinessService.getDocumentService().createDocumentFromTemplate(
					workId, templateId);
		}
	}

	private void setupWorkResource(DBObject workData) {

		ObjectId workTemplateId = (ObjectId) workData.get(FIELD_TEMPLATE);

		// ��Ҫ����ģ����ĵ� ����ģ����ĵ�����������
		// ��Ҫ����ģ���ƶ��Ľ�ɫ �Ŷ� ��Ա ���õ��������Դ��
		if (workTemplateId == null) {
			return;
		}
		DBObject workTemplate = BusinessService.getWorkService()
				.getWorkTemplateObject(workTemplateId);

		if (workTemplate == null) {
			return;
		}
		// ȡ���Ѿ����úõ�������Դ
		BasicDBList resource = (BasicDBList) workData.get(FIELD_WORK_RESOURCE);

		if (resource == null) {
			resource = new BasicDBList();
		}
		BasicDBList result = new BasicDBList();
		result.addAll(resource);

		// ��ȡ����������
		BasicDBList pList = (BasicDBList) workTemplate.get(FIELD_PARTICIPATE);

		if (pList != null && !pList.isEmpty()) {

			// ���ݲ��������ö�ȡ������ ���õ��������Դ��
			for (int i = 0; i < pList.size(); i++) {

				DBObject pItem = (DBObject) pList.get(i);

				ObjectId participtesItemId = (ObjectId) pItem.get(FIELD_SYSID);
				// ȡOBS�ϵĶ�Ӧ�ڵ�

				pItem = DataUtil.getDataObject(COLLECTION_ORG,
						participtesItemId);

				if (pItem == null) {
					continue;
				}

				BasicDBList userList = BusinessService.getOrganizationService()
						.getUsersUnderOBSItem(participtesItemId);

				// �Ƚ�ģ���е��û����������Ѿ���ӵ���Դ ��������ϵ���Դ�������� ��ӵ��������Դ
				for (int j = 0; j < userList.size(); j++) {
					DBObject templateUserItem = (DBObject) userList.get(j);
					if (!BusinessService.getOrganizationService()
							.existInListById(resource, templateUserItem)) {
						result.add(templateUserItem);
					}
				}
			}
		}
		workData.put(FIELD_WORK_RESOURCE, result);

	}

	/**
	 * 
	 * @param userId
	 * @param includeProjectWork
	 * @return
	 */
	public BasicDBList getUserLaunchedWork(String userId,
			boolean includeProjectWork) {

		BasicDBList result = new BasicDBList();

		BasicDBObject query = new BasicDBObject().append(FIELD_CREATER, userId);
		if (!includeProjectWork) {
			query.put(FIELD_WBSPARENT, null);
		}
		DBCursor cur = workCollection.find(query);
		cur.sort(new BasicDBObject().append(FIELD_CREATE_DATE, 1));
		while (cur.hasNext()) {
			result.add(cur.next());
		}

		return result;
	}

	public DBObject getWorkByProcessId(long pid) {

		return workCollection.findOne(new BasicDBObject().append(
				FIELD_WFINFO_PROCESSINSTANCEID, pid));
	}

	public void lockDocumentsOfWork(ObjectId workId, String editorId) {

		lockDocumentsOfWork(workId, editorId, true);
	}

	public void unlockDocumentsOfWork(ObjectId workId, String editorId) {

		lockDocumentsOfWork(workId, editorId, false);
	}

	public void lockDocumentsOfWork(ObjectId workId, String editorId,
			boolean lockmark) {

		BasicDBObject query = new BasicDBObject();
		query.put(FIELD_WBSPARENT, workId);
		if (editorId != null) {
			query.put(FIELD_SYSTEM_EDITOR, editorId);
		}

		BasicDBObject setting = new BasicDBObject();
		setting.put(FIELD_LOCKMARK, lockmark);
		setting.put(FIELD_LOCKREASON, VALUE_LOCKREASON_PROCESS);
		setting.put(FIELD_LOCKBY, "admin");

		docCollection.update(query, new BasicDBObject().append(SET, setting),
				false, true);

	}

	public void lockDocumentOfWorkCascade(ObjectId workId, boolean lockmark) {

		lockDocumentsOfWork(workId, null, lockmark);

		List<DBObject> subWorks = getSubworks(workId);
		for (int i = 0; i < subWorks.size(); i++) {
			lockDocumentOfWorkCascade(
					(ObjectId) subWorks.get(i).get(FIELD_SYSID), lockmark);
		}
	}

	public void lockProject(ObjectId objectId) {
		lockDocumentOfWorkCascade(objectId, true);
	}

	public void unlockProject(ObjectId objectId) {
		lockDocumentOfWorkCascade(objectId, false);
	}

	public void changeProjectStatus(ObjectId projectId, String processStatus) {

		DBCollection workCollection = DBActivator
				.getDefaultDBCollection(COLLECTION_WORK);
		workCollection
				.update(new BasicDBObject().append(FIELD_ROOTID, projectId)
						.append(FIELD_PROCESS_STATUS,
								new BasicDBObject().append("$ne",
										VALUE_PROCESS_CLOSE)),
						new BasicDBObject().append("$set", new BasicDBObject()
								.append(FIELD_PROCESS_STATUS,
										VALUE_PROCESS_CANCEL)), false, true);

		updateProjectStatus(projectId, processStatus);
	}

	public List<DBObject> getSubworks(ObjectId wbsParentWorkId) {
		DBCursor cur = workCollection.find(new BasicDBObject().append(
				FIELD_WBSPARENT, wbsParentWorkId));
		return cur.toArray();
	}

	/**
	 * 
	 * 
	 * 
	 * @param projectId
	 *            ��Ŀ��ţ���Щ��ҵ��֮Ϊ������ţ�
	 * @param projectTemplateOid
	 *            ��Ŀģ��oid
	 * @param pmOid
	 *            ��Ŀ�����˵�oid
	 * @param launchWorkOid
	 *            ������Ŀ�Ĺ���id
	 * @return
	 */
	public DBObject createProjectFromWork(String projectId,
			String projectTemplateOid, String pmOid, String launchWorkOid) {

		// ������Ŀ�Ķ���
		DBObject project = new BasicDBObject();

		// �ӹ����л�ö�Ӧ�ļƻ���ʼ�ͼƻ����

		return project;

	}

	/**
	 * ���ĳ�������µ�ĳһ���ĵ�
	 * 
	 * @param workOid
	 *            ,�ĵ������Ĺ���id
	 * @param editorId
	 *            ,����Ϊ�գ�Ϊ��ʱ���ظ����ĵ�
	 * @return
	 */
	public List<DBObject> getDocumentOfWork(ObjectId workOid, String editorId) {

		BasicDBObject condition = new BasicDBObject();
		condition.put(FIELD_WBSPARENT, workOid);
		condition.put(FIELD_TEMPLATE_TYPE, VALUE_WBS_DOCUMENT_TYPE);

		if (editorId != null)
			condition.put(FIELD_SYSTEM_EDITOR, editorId);

		DBCursor cur = docCollection.find(condition);
		return cur.toArray();
	}

	public List<DBObject> getDocumentOfWork(ObjectId workOid) {
		return getDocumentOfWork(workOid, null);
	}

	/**
	 * ������Ŀ
	 * 
	 * @param projectId
	 *            ��Ŀ���
	 * @param obsParentId
	 *            ��Ŀ��������
	 * @param pmOid
	 *            ��Ŀ�����oid
	 * @param planStart
	 *            ��Ŀ�ƻ���ʼ
	 * @param planFinish
	 *            ��Ŀ�ƻ����
	 * @param budget
	 * @param strProjectTemplateOid
	 *            ��Ŀģ�壬����Ϊ��
	 * @param createOBSFromTemplate
	 *            �Ƿ����ģ�崴����֯�ṹ�������Ŀģ�岻Ϊ��ʱ����������Ŀģ�崴����֯�ṹ
	 * @param createFBSFromWBS
	 *            �Ƿ����WBS����FBS, �����Ŀģ�岻Ϊ�գ���������Ŀģ���WBS�ṹ����Ŀ¼�ṹ
	 * @param createUserName
	 * @param createUserId
	 */
	public DBObject createProjectFromWork(String projectId, String projectDesc,
			ObjectId obsParentId, ObjectId pmId, Date planStart,
			Date planFinish, Double budget, ObjectId templateId,
			boolean createOBSFromTemplate, boolean createFBSFromWBS,
			String createUserId, String createUserName) {
		DBObject project = new BasicDBObject();

		// ������Ŀ��oid
		ObjectId projectOid = new ObjectId();
		project.put(FIELD_SYSID, projectOid);

		// ������Ŀ�ı��
		project.put(FIELD_ID, projectId);

		// ������Ŀ������
		project.put(FIELD_DESC, projectDesc);

		// ������Ŀ����
		DBObject pmObject = getUserObject(pmId);
		project.put(FIELD_PROJECT_PM, pmObject);

		// ������Ŀ��ʹ�õ�ģ��
		DBObject templateObject = getProjectTemplateObject(templateId);
		project.put(FIELD_TEMPLATE, templateObject);

		// ������Ŀ������֯
		project.put(FIELD_OBSPARENT, obsParentId);

		// ���üƻ���ʼ
		project.put(FIELD_PROJECT_PLANSTART, planStart);

		// ���üƻ����
		project.put(FIELD_PROJECT_PLANFINISH, planFinish);

		// ������ĿԤ��
		project.put(FIELD_BUDGET, budget == null ? 0d : budget);

		// ��parent�´���һ����Ŀ��
		project.put(FIELD_PROJECT_OBS_ROOT, new ObjectId());
		// ���ø��ļ��е�id
		project.put(FIELD_FOLDER_ROOT, new ObjectId());

		// ����FBS��������
		project.put(FIELD_PROJECT_FOLDER_CREATE_FBS_AS_WBS, createFBSFromWBS);

		// ׼������
		project.put(FIELD_CREATER, createUserId);
		project.put(FIELD_CREATER_NAME, createUserName);
		project.put(FIELD_OWNER, createUserId);
		project.put(FIELD_OWNER_NAME, createUserName);
		project.put(FIELD_CREATE_DATE, new Date());

		projectCollection.insert(project);

		// ������Ŀ���
		createRootProjectTeam(project);

		if (templateId != null) {// ��ģ�壬
			// �����ģ�嶨�壬��Ҫ��ģ�嶨���������и��ƣ�������������
			DataUtil.importTemplateToProject(templateId, projectOid);
		}

		// ������Ŀ���ļ���
		DataUtil.createProjectFolder(project);

		// ͬ�����û���projectincharged
		// ����Ŀ������ͬ����user
		DataUtil.saveUserRelationInformation(null, pmId,
				COLLECTION_USER_PROJECT_IN_CHARGED, projectOid);

		return project;
	}

	public ObjectId createRootProjectTeam(DBObject project) {

		ObjectId parentId = (ObjectId) project.get(FIELD_OBSPARENT);
		ObjectId teamId = (ObjectId) project.get(FIELD_PROJECT_OBS_ROOT);

		// ��parent���´�����Ŀ��
		DBObject obsItem = BusinessService.getOrganizationService()
				.createOBSItem(null, parentId, teamId,
						(String) project.get(FIELD_DESC), VALUE_OBS_PJTEAMTYPE);
		ObjectId pjTeamId = (ObjectId) obsItem.get(FIELD_SYSID);

		return pjTeamId;
	}

	public void updateWorkStatus(ObjectId workId, Object workStatus) {
		BasicDBObject update = new BasicDBObject();
		BasicDBObject set = new BasicDBObject();
		if (VALUE_PROCESS_CLOSE.equals(workStatus)) {
			set.put(FIELD_PROJECT_ACTUALFINISH, new Date());
		}
		set.put(FIELD_PROCESS_STATUS, workStatus);
		update.put("$set", set);
		workCollection.update(new BasicDBObject().append(FIELD_SYSID, workId),
				update);
	}

	public void updateProjectStatus(ObjectId projectId, String processStatus) {
		BasicDBObject update = new BasicDBObject();
		BasicDBObject set = new BasicDBObject();
		if (VALUE_PROCESS_CLOSE.equals(processStatus)) {
			set.put(FIELD_PROJECT_ACTUALFINISH, new Date());
		}
		set.put(FIELD_PROCESS_STATUS, processStatus);
		update.put("$set", set);
		projectCollection.update(
				new BasicDBObject().append(FIELD_SYSID, projectId), update);

	}

	public List<DBObject> getProjectAsProjectManager(ObjectId userOid) {
		DBCursor cur = projectCollection.find(new BasicDBObject().append(
				FIELD_PROJECT_PM + "." + FIELD_SYSID, userOid));
		return cur.toArray();
	}

	public List<DBObject> getProjectAsProjectManager(ObjectId userOid,
			String projectStatus) {
		DBCursor cur = projectCollection.find(new BasicDBObject().append(
				FIELD_PROJECT_PM + "." + FIELD_SYSID, userOid).append(
				FIELD_PROCESS_STATUS, projectStatus));
		return cur.toArray();
	}

	public void attachWorkDocument(ObjectId workOid) {
		DBObject work = getDBObject(workCollection, workOid);
		List<DBObject> docs = getDocumentOfWork(workOid);
		for (int i = 0; i < docs.size(); i++) {
			DBObject doc = docs.get(i);
			BasicDBList workHistroy = (BasicDBList) doc
					.get(FIELD_DOCUMENT_WORKS);
			if (workHistroy == null) {
				workHistroy = new BasicDBList();
			}
			workHistroy.add(work);
			doc.put(FIELD_DOCUMENT_WORKS, workHistroy);
			docCollection.save(doc);
		}
	}

	public void attachWorkToProject(ObjectId projectOid, ObjectId workId,
			String parentTaskName) {
		DBObject projectData = getDBObject(projectCollection, projectOid);
		attachWorkToProject(projectData, workId, parentTaskName);
	}

	public void attachWorkToProject(DBObject projectData, ObjectId workId,
			String parentTaskName) {
		DBObject work = getWorkObject(workId);
		ObjectId projectOid = (ObjectId) projectData.get(FIELD_SYSID);
		DBObject parentWorkData = getProjectWorkByName(projectOid,
				parentTaskName);
		if (parentWorkData == null)
			return;

		ObjectId parentWorkId = (ObjectId) parentWorkData.get(FIELD_SYSID);
		int maxWBSSeq = getMaxWBSSeq(parentWorkId);
		work.put(FIELD_WBSPARENT, parentWorkId);
		work.put(FIELD_WBSSEQ, maxWBSSeq);
		work.put(FIELD_TEMPLATE_TYPE, VALUE_WBS_TASK_TYPE);
		work.put(FIELD_ROOTID, projectOid);
		saveWork(work);

		// ���ĵ�����Ϊ��Ŀ�ĵ�

		// ���ĵ���rootid����Ϊ��Ŀ��id
		// ���ĵ���dbsseq����Ϊ1��wbsseq����Ϊ9999
		// ���ĵ�require����Ϊtrue
		// ���ĵ����õ���Ŀ�ĸ�Ŀ¼
		docCollection.update(
				new BasicDBObject().append(FIELD_WBSPARENT, workId),
				new BasicDBObject().append(
						SET,
						new BasicDBObject()
								.append(FIELD_ROOTID, projectOid)
								.append(FIELD_DBSSEQ, 1)
								.append(FIELD_WBSSEQ, 999)
								.append(FIELD_REQUIRED_DOCUMENT, true)
								.append(FIELD_FBSPARENT,
										projectData.get(FIELD_FOLDER_ROOT))),
				false, true);
	}

	public void saveWork(DBObject work) {
		workCollection.save(work);
	}

	public int getMaxWBSSeq(ObjectId parentWorkId) {
		DBCursor cur = workCollection.find(
				new BasicDBObject().append(FIELD_WBSPARENT, parentWorkId),
				new BasicDBObject().append(FIELD_WBSSEQ, 1));
		cur.sort(new BasicDBObject().append(FIELD_WBSSEQ, -1));
		if (cur.hasNext()) {
			DBObject d = cur.next();
			return (Integer) d.get(FIELD_WBSSEQ);
		} else {
			return 1;
		}
	}

	public DBObject getProjectWorkByName(ObjectId projectOid, String workName) {
		return workCollection.findOne(new BasicDBObject().append(FIELD_ROOTID,
				projectOid).append(FIELD_DESC, workName));
	}

	@Deprecated
	public void attachDocumentToProject(ObjectId projectOid,
			List<DBObject> docs, String workName) {
		DBObject projectData = getDBObject(projectCollection, projectOid);
		attachDocumentToProject(projectData, docs, workName);
	}

	@Deprecated
	public void attachDocumentToProject(DBObject projectData,
			List<DBObject> docs, String workName) {
		DBObject work = null;

		List<DBObject> works = getSubworks((ObjectId) projectData
				.get(FIELD_SYSID));
		for (int i = 0; i < works.size(); i++) {
			if (workName.equals(works.get(i).get(FIELD_DESC))) {
				work = works.get(i);
				break;
			}
		}

		if (work == null) {
			if (works.size() > 0) {
				work = works.get(0);
			} else {
				return;
			}
		}

		ObjectId folderId = (ObjectId) projectData.get(FIELD_FOLDER_ROOT);
		DocumentService docService = BusinessService.getDocumentService();
		List<DBObject> targetFolder = docService.getSubFolder(folderId,
				workName);
		if (targetFolder != null && targetFolder.size() > 0) {

			DBObject folder = targetFolder.get(0);

			for (int i = 0; i < docs.size(); i++) {
				ObjectId projectOid = (ObjectId) projectData.get(FIELD_SYSID);
				DBObject dbObject = docs.get(i);
				dbObject.put(FIELD_WBSPARENT, work.get(FIELD_SYSID));
				dbObject.put(FIELD_DBSSEQ, 1);
				dbObject.put(FIELD_WBSSEQ, 9999);
				dbObject.put(FIELD_ROOTID, projectOid);
				// Ѱ����ĿĿ¼������Ϊ��Ŀ������Ŀ¼
				if (targetFolder != null && targetFolder.size() > 0) {
					dbObject.put(FIELD_FBSPARENT, folder.get(FIELD_SYSID));
				} else {
					dbObject.put(FIELD_FBSPARENT, folderId);
				}
				docCollection.save(dbObject);
			}
		}
	}

	/**
	 * 
	 * @param projectId
	 *            ObjectId
	 * @return Document list of the project, it will not null
	 */
	public List<DBObject> getProjectDocuments(ObjectId projectId) {
		return docCollection.find(
				new BasicDBObject().append(FIELD_ROOTID, projectId)).toArray();
	}

	/**
	 * 
	 * @param projectId
	 *            ObjectId
	 * @return Document list of the project, it will not null
	 */
	public List<DBObject> getProjectDocuments(ObjectId projectId,
			String editorId) {
		return docCollection.find(
				new BasicDBObject().append(FIELD_ROOTID, projectId).append(
						FIELD_SYSTEM_EDITOR, editorId)).toArray();
	}

	public List<DBObject> getProjectDocuments(ObjectId projectId,
			String editorId, DBObject sort) {
		return docCollection
				.find(new BasicDBObject().append(FIELD_ROOTID, projectId)
						.append(FIELD_SYSTEM_EDITOR, editorId)).sort(sort)
				.toArray();
	}

	public List<Object[]> completenessCheck(ObjectId projectId) {
		ArrayList<Object[]> result = new ArrayList<Object[]>();
		// ���project��Ӧ����Ŀģ��
		DBObject proj = getProjectObject(projectId);
		DBObject template = (DBObject) proj.get(FIELD_TEMPLATE);
		if (template == null) {
			return result;
		}

		// ��õ�ǰ����Ŀ�ĵ�
		List<DBObject> documents = getProjectDocuments(projectId);

		ObjectId templateId = (ObjectId) template.get(FIELD_SYSID);

		List<DBObject> requiredDocuments = new ArrayList<DBObject>();
		getRequiredDocuments(templateId, requiredDocuments);

		for (int i = 0; i < requiredDocuments.size(); i++) {
			DBObject required = requiredDocuments.get(i);
			check(required, documents, result);
		}

		return result;
	}

	private void check(DBObject required, List<DBObject> documents,
			List<Object[]> result) {
		Object _name = required.get(FIELD_DESC);
		boolean hasDocument = false;
		boolean hasAttchment = false;
		for (int i = 0; i < documents.size(); i++) {
			Object name = documents.get(i).get(FIELD_DESC);
			if (_name.equals(name)) {
				hasDocument = true;
				if (Boolean.TRUE.equals(required.get("checkattachment"))) {
					// ����ļ��Ƿ���и���
					Object attachment = documents.get(i).get("attachment");
					if (attachment instanceof BasicDBList) {
						if (((BasicDBList) attachment).size() > 0) {
							hasAttchment = true;
							break;
						}
					}
				} else {
					hasAttchment = true;
				}
			}
		}

		if (!hasDocument) {
			result.add(new Object[] { required, "ȱ�ٱ����ύ���ĵ���¼" });
		}
		if (!hasAttchment) {
			result.add(new Object[] { required, "�ĵ���¼ȱ�ٱ���ĸ����ļ�" });
		}
	}

	private void getRequiredDocuments(ObjectId wbsParentId,
			List<DBObject> requiredDocuments) {
		DBCursor cur = workTemplateCollection.find(new BasicDBObject().append(
				FIELD_WBSPARENT, wbsParentId));
		while (cur.hasNext()) {
			DBObject template = cur.next();
			if (VALUE_WBS_DOCUMENT_TYPE.equals(template
					.get(FIELD_TEMPLATE_TYPE))) {
				if (Boolean.TRUE.equals(template.get(FIELD_REQUIRED_DOCUMENT))) {
					requiredDocuments.add(template);
				}
			} else {
				getRequiredDocuments((ObjectId) template.get(FIELD_SYSID),
						requiredDocuments);
			}
		}
	}

	public void startProject(ObjectId projectId) {

		projectCollection.update(
				new BasicDBObject().append(FIELD_SYSID, projectId),
				new BasicDBObject().append(
						SET,
						new BasicDBObject().append(FIELD_PROCESS_STATUS,
								VALUE_PROCESS_PROCESS).append(
								FIELD_PROJECT_ACTUALSTART, new Date())));

		// *************************************************************************
		// ��Ŀ�����������״̬��Ϊ׼����ͬ���û�������Ϣ���ݣ�
		DBCollection workCollection = DBActivator
				.getDefaultDBCollection(COLLECTION_WORK);
		// workCollection.update(
		// new BasicDBObject()
		// .append(FIELD_ROOTID, projectId),
		// new BasicDBObject()
		// .append(FIELD_PROCESS_STATUS, VALUE_PROCESS_READY)
		// , false, true);

		// ����������Ϣ
		DBCursor cur = workCollection.find(new BasicDBObject().append(
				FIELD_ROOTID, projectId));
		DBObject work, chargerData;
		BasicDBList resource;
		while (cur.hasNext()) {
			work = cur.next();
			// ���������״̬
			work.put(FIELD_PROCESS_STATUS, VALUE_PROCESS_READY);
			workCollection.save(work);

			ObjectId id = (ObjectId) work.get(FIELD_SYSID);
			chargerData = (DBObject) work.get(FIELD_WORK_PM);
			resource = (BasicDBList) work.get(FIELD_WORK_RESOURCE);

			// �������������ȱ���ͣ��ʹ�ÿ�ʼ�ָ��ģ���ô���Ѿ���������֪ͨ
			// ��Ҫ��ɾ����������
			userParticipateWorkCollection.remove(new BasicDBObject().append(
					FIELD_ID, id));
			userChargedWorkCollection.remove(new BasicDBObject().append(
					FIELD_ID, id));
			// ͬ��user��������
			if (chargerData != null) {
				DataUtil.saveUserRelationInformation(null,
						(ObjectId) chargerData.get(FIELD_SYSID),
						COLLECTION_USER_WORK_IN_CHARGED, id);
			}

			// ͬ��user���������
			if (resource != null) {
				Iterator<Object> iter = resource.iterator();
				while (iter.hasNext()) {
					DBObject user = (DBObject) iter.next();
					DataUtil.saveUserRelationInformation(null,
							(ObjectId) user.get(FIELD_SYSID),
							COLLECTION_USER_WORK_PARTTICIPATED, id);
				}
			}
		}

		// *************************************************************************
	}

	public void projectAutoAssign(ObjectId projectId) {
		DataUtil.autoAssignment(projectId);
	}

	/**
	 * �г������µ�������Ŀ
	 * 
	 * @param deptId
	 * @param cascade
	 *            �Ƿ����г������¼��Ĳ��ŵ���Ŀ
	 * @return
	 */
	public List<DBObject> getProjectOfOrganization(ObjectId deptId,
			boolean cascade) {
		List<ObjectId> obsParentList = new ArrayList<ObjectId>();
		obsParentList.add(deptId);
		if (cascade) {
			OrganizationService orgService = BusinessService
					.getOrganizationService();
			List<DBObject> subTeamList = orgService.getSubTeam(deptId, true);
			for (int i = 0; i < subTeamList.size(); i++) {
				DBObject team = subTeamList.get(i);
				ObjectId teamId = (ObjectId) team.get(FIELD_SYSID);
				if (!obsParentList.contains(teamId)) {
					obsParentList.add(teamId);
				}
			}
		}

		DBCursor cur = projectCollection.find(new BasicDBObject().append(
				IDBConstants.FIELD_OBSPARENT,
				new BasicDBObject().append(IN, obsParentList)));

		return cur.toArray();
	}

	public HashMap<String, ArrayList<MessageObject>> getUserMessageList(ObjectId useroid) {
		
		ArrayList<MessageObject> readyList = new ArrayList<MessageObject>();
		ArrayList<MessageObject> processList = new ArrayList<MessageObject>();
		ArrayList<MessageObject> pauseList = new ArrayList<MessageObject>();
		ArrayList<MessageObject> closeList = new ArrayList<MessageObject>();
		ArrayList<MessageObject> cancelList = new ArrayList<MessageObject>();
		DBCursor cur1 = userChargedWorkCollection.find(new BasicDBObject()
				.append(IDBConstants.FIELD_USEROID, useroid).append(
						IDBConstants.FIELD_MARK_DELETE,
						new BasicDBObject().append("$ne", true)));

		while (cur1.hasNext()) {
			DBObject message = cur1.next();
			try {
				MessageObject messageObject = new MessageObject(message,
						userChargedWorkCollection, workCollection, true);
				appendWorkToInput(messageObject, readyList, processList,
						pauseList, closeList, cancelList);
			} catch (ServiceException e) {
				// ɾ��message
				userChargedWorkCollection.remove(message);
			}
		}

		DBCursor cur2 = userParticipateWorkCollection.find(new BasicDBObject()
				.append(IDBConstants.FIELD_USEROID, useroid).append(
						IDBConstants.FIELD_MARK_DELETE,
						new BasicDBObject().append("$ne", true)));

		while (cur2.hasNext()) {
			DBObject message = cur2.next();
			try {
				MessageObject messageObject = new MessageObject(message,
						userParticipateWorkCollection, workCollection, false);
				appendWorkToInput(messageObject, readyList, processList,
						pauseList, closeList, cancelList);
			} catch (ServiceException e) {
				// ɾ��message
				userParticipateWorkCollection.remove(message);
			}
		}
		
		HashMap<String, ArrayList<MessageObject>> messageResult = new HashMap<String,ArrayList<MessageObject>>();
		messageResult.put(VALUE_PROCESS_READY, readyList);
		messageResult.put(VALUE_PROCESS_PROCESS, processList);
		messageResult.put(VALUE_PROCESS_PAUSE, pauseList);
		messageResult.put(VALUE_PROCESS_CLOSE, closeList);
		messageResult.put(VALUE_PROCESS_CANCEL, cancelList);
		
		return messageResult;
	}

	private void appendWorkToInput(MessageObject messageModel,
			ArrayList<MessageObject> readyList,
			ArrayList<MessageObject> processList,
			ArrayList<MessageObject> pauseList,
			ArrayList<MessageObject> closeList,
			ArrayList<MessageObject> cancelList) {
		if (messageModel.isReady()) {
			if (!readyList.contains(messageModel))
				readyList.add(messageModel);
		} else if (messageModel.isProcess()) {
			if (!processList.contains(messageModel))
				processList.add(messageModel);
		} else if (messageModel.isPause()) {
			if (!pauseList.contains(messageModel))
				pauseList.add(messageModel);
		} else if (messageModel.isClose()) {
			if (!closeList.contains(messageModel))
				closeList.add(messageModel);
		} else if (messageModel.isCancel()) {
			if (!cancelList.contains(messageModel))
				cancelList.add(messageModel);
		}
	}

}
