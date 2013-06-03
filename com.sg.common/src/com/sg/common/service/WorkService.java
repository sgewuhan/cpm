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
	 * "pm" : {//user 对象 "_id" : ObjectId("4fb5ef68e7e3d859cd54e0ae"),
	 * "activate" : true, "desc" : "tsx", "name" : "谭帅霞", "uid" : "20103794",
	 * "email" : "tanshx@teg.cn", "thumb" : [{ "_id" :
	 * ObjectId("4fcdf8a282768b4790fbd9df"), "namespace" : "user_thumb",
	 * "fileName" : "1.png" }], "sitename" : null, "urllabel" : "....." },
	 * "planstart" : ISODate("2012-06-12T14:44:28.131Z"), "planfinish" :
	 * ISODate("2012-06-15T14:44:28.753Z"), "obsparent" :
	 * ObjectId("4fbe838c38040cb45eebd819"),//所属的组织oid "template" :
	 * {//template对象 "_id" : ObjectId("4fbe97ed3804e599a12f31ce"), "activate" :
	 * true, "desc" : "产品类项目模板", "siteparent" :
	 * ObjectId("4fb5e513e7e35292f1713ab0"), "thumb" : [{ "_id" :
	 * ObjectId("4fd10f457d58c5488ba0ec7f"), "namespace" : "project_thumb",
	 * "fileName" : "projectcover1.png" }] }, "createfbsfromwbs" : true, "desc"
	 * : "AAA", "obsroot" : ObjectId("4fd604607522e7a07f16e3ab"),//项目组的oid
	 * "folderRoot" : ObjectId("4fd604607522e7a07f16e3ac"),//项目文件夹oid "id" :
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
	 * 有进程上下文要求
	 * 
	 * @param workData
	 * @param startImmediately
	 */
	public void createWork(DBObject workData, boolean startImmediately) {
		createWork(workData, startImmediately, null);
	}

	/**
	 * sitename为空时，有进程上下文要求，反之无进程上下文要求
	 * 
	 * @param workData
	 * @param startImmediately
	 * @param sitename
	 */
	public void createWork(DBObject workData, boolean startImmediately,
			String sitename) {

		// 处理模板 如果选择了模板
		// 将模板的参与者查询出用户 放置到任务的资源中
		// 处理workData的初始化编号
		checkSystemId(workData);

		setupWorkResource(workData);

		// 处理流程定义
		setProcessDefinition(workData);

		// 如果需要立即启动，设置workData的值
		if (startImmediately) {
			workData.put(FIELD_PROCESS_STATUS, VALUE_PROCESS_PROCESS);
			workData.put(FIELD_PROJECT_ACTUALSTART, new Date());
		} else {
			workData.put(FIELD_PROCESS_STATUS, VALUE_PROCESS_READY);
		}
		// 保存系统信息
		setSystemCreateInfo(workData);

		// 保存任务记录
		workCollection.save(workData);

		saveUserMessage(workData);

		// 处理文档 根据模板的文档 创建新的文档挂接到任务下
		setupDocument(workData);

		if (startImmediately) {
			// 启动流程
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

		// 将流程定义以及参与者定义信息复制到任务中
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

		// 同步用户数据 同步负责的任务信息

		ObjectId workId = (ObjectId) workData.get(FIELD_SYSID);

		ObjectId newChargerId = null;
		if (workData != null) {
			DBObject newCharger = (DBObject) workData.get(FIELD_WORK_PM);
			newChargerId = (ObjectId) newCharger.get(FIELD_SYSID);
		}

		// 同步参与的任务信息
		// *******************************************************************************************************

		if (DataUtil.isReady(workData) || DataUtil.isProcess(workData)) {
			// 如果没有流程信息需要保存用户工作关联信息
			String pd = (String) workData.get(FIELD_PROCESS_DEFINITION_ID);
			if (pd != null && pd.length() > 0) {// 只保存负责人，就是发起人的

				DataUtil.saveUserRelationInformation(null, newChargerId,
						COLLECTION_USER_WORK_IN_CHARGED, workId);

			} else {// 没有流程定义直接发给所有人

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
		// 取出文档模板
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

		// 需要创建模板的文档 并将模板的文档挂在任务下
		// 需要根据模板制定的角色 团队 成员 放置到任务的资源中
		if (workTemplateId == null) {
			return;
		}
		DBObject workTemplate = BusinessService.getWorkService()
				.getWorkTemplateObject(workTemplateId);

		if (workTemplate == null) {
			return;
		}
		// 取出已经设置好的任务资源
		BasicDBList resource = (BasicDBList) workData.get(FIELD_WORK_RESOURCE);

		if (resource == null) {
			resource = new BasicDBList();
		}
		BasicDBList result = new BasicDBList();
		result.addAll(resource);

		// 读取参与者设置
		BasicDBList pList = (BasicDBList) workTemplate.get(FIELD_PARTICIPATE);

		if (pList != null && !pList.isEmpty()) {

			// 根据参与者设置读取参与者 放置到任务的资源中
			for (int i = 0; i < pList.size(); i++) {

				DBObject pItem = (DBObject) pList.get(i);

				ObjectId participtesItemId = (ObjectId) pItem.get(FIELD_SYSID);
				// 取OBS上的对应节点

				pItem = DataUtil.getDataObject(COLLECTION_ORG,
						participtesItemId);

				if (pItem == null) {
					continue;
				}

				BasicDBList userList = BusinessService.getOrganizationService()
						.getUsersUnderOBSItem(participtesItemId);

				// 比较模板中的用户和任务上已经添加的资源 如果任务上的资源不包含的 添加到任务的资源
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
	 *            项目编号（有些企业称之为工作令号）
	 * @param projectTemplateOid
	 *            项目模板oid
	 * @param pmOid
	 *            项目负责人的oid
	 * @param launchWorkOid
	 *            发起项目的工作id
	 * @return
	 */
	public DBObject createProjectFromWork(String projectId,
			String projectTemplateOid, String pmOid, String launchWorkOid) {

		// 创建项目的对象
		DBObject project = new BasicDBObject();

		// 从工作中获得对应的计划开始和计划完成

		return project;

	}

	/**
	 * 获得某个工作下的某一类文档
	 * 
	 * @param workOid
	 *            ,文档所属的工作id
	 * @param editorId
	 *            ,可以为空，为空时返回各种文档
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
	 * 创建项目
	 * 
	 * @param projectId
	 *            项目编号
	 * @param obsParentId
	 *            项目所属部门
	 * @param pmOid
	 *            项目经理的oid
	 * @param planStart
	 *            项目计划开始
	 * @param planFinish
	 *            项目计划完成
	 * @param budget
	 * @param strProjectTemplateOid
	 *            项目模板，可以为空
	 * @param createOBSFromTemplate
	 *            是否根据模板创建组织结构，如果项目模板不为空时，将按照项目模板创建组织结构
	 * @param createFBSFromWBS
	 *            是否根据WBS创建FBS, 如果项目模板不为空，将按照项目模板的WBS结构创建目录结构
	 * @param createUserName
	 * @param createUserId
	 */
	public DBObject createProjectFromWork(String projectId, String projectDesc,
			ObjectId obsParentId, ObjectId pmId, Date planStart,
			Date planFinish, Double budget, ObjectId templateId,
			boolean createOBSFromTemplate, boolean createFBSFromWBS,
			String createUserId, String createUserName) {
		DBObject project = new BasicDBObject();

		// 设置项目的oid
		ObjectId projectOid = new ObjectId();
		project.put(FIELD_SYSID, projectOid);

		// 设置项目的编号
		project.put(FIELD_ID, projectId);

		// 设置项目的名称
		project.put(FIELD_DESC, projectDesc);

		// 设置项目经理
		DBObject pmObject = getUserObject(pmId);
		project.put(FIELD_PROJECT_PM, pmObject);

		// 设置项目所使用的模板
		DBObject templateObject = getProjectTemplateObject(templateId);
		project.put(FIELD_TEMPLATE, templateObject);

		// 设置项目所属组织
		project.put(FIELD_OBSPARENT, obsParentId);

		// 设置计划开始
		project.put(FIELD_PROJECT_PLANSTART, planStart);

		// 设置计划完成
		project.put(FIELD_PROJECT_PLANFINISH, planFinish);

		// 设置项目预算
		project.put(FIELD_BUDGET, budget == null ? 0d : budget);

		// 在parent下创建一个项目组
		project.put(FIELD_PROJECT_OBS_ROOT, new ObjectId());
		// 设置根文件夹的id
		project.put(FIELD_FOLDER_ROOT, new ObjectId());

		// 设置FBS创建规则
		project.put(FIELD_PROJECT_FOLDER_CREATE_FBS_AS_WBS, createFBSFromWBS);

		// 准备保存
		project.put(FIELD_CREATER, createUserId);
		project.put(FIELD_CREATER_NAME, createUserName);
		project.put(FIELD_OWNER, createUserId);
		project.put(FIELD_OWNER_NAME, createUserName);
		project.put(FIELD_CREATE_DATE, new Date());

		projectCollection.insert(project);

		// 创建项目组根
		createRootProjectTeam(project);

		if (templateId != null) {// 有模板，
			// 如果有模板定义，需要把模板定义的任务进行复制，并产生交付物
			DataUtil.importTemplateToProject(templateId, projectOid);
		}

		// 创建项目根文件夹
		DataUtil.createProjectFolder(project);

		// 同步到用户的projectincharged
		// 将项目负责人同步到user
		DataUtil.saveUserRelationInformation(null, pmId,
				COLLECTION_USER_PROJECT_IN_CHARGED, projectOid);

		return project;
	}

	public ObjectId createRootProjectTeam(DBObject project) {

		ObjectId parentId = (ObjectId) project.get(FIELD_OBSPARENT);
		ObjectId teamId = (ObjectId) project.get(FIELD_PROJECT_OBS_ROOT);

		// 在parent组下创建项目组
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

		// 将文档更改为项目文档

		// 将文档的rootid更改为项目的id
		// 将文档的dbsseq更改为1，wbsseq更改为9999
		// 将文档require更改为true
		// 将文档放置到项目的根目录
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
				// 寻找项目目录中名称为项目启动的目录
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
		// 获得project对应的项目模板
		DBObject proj = getProjectObject(projectId);
		DBObject template = (DBObject) proj.get(FIELD_TEMPLATE);
		if (template == null) {
			return result;
		}

		// 获得当前的项目文档
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
					// 检查文件是否具有附件
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
			result.add(new Object[] { required, "缺少必须提交的文档记录" });
		}
		if (!hasAttchment) {
			result.add(new Object[] { required, "文档记录缺少必须的附件文件" });
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
		// 项目启动，任务的状态改为准备（同步用户任务消息数据）
		DBCollection workCollection = DBActivator
				.getDefaultDBCollection(COLLECTION_WORK);
		// workCollection.update(
		// new BasicDBObject()
		// .append(FIELD_ROOTID, projectId),
		// new BasicDBObject()
		// .append(FIELD_PROCESS_STATUS, VALUE_PROCESS_READY)
		// , false, true);

		// 保存任务消息
		DBCursor cur = workCollection.find(new BasicDBObject().append(
				FIELD_ROOTID, projectId));
		DBObject work, chargerData;
		BasicDBList resource;
		while (cur.hasNext()) {
			work = cur.next();
			// 更改任务的状态
			work.put(FIELD_PROCESS_STATUS, VALUE_PROCESS_READY);
			workCollection.save(work);

			ObjectId id = (ObjectId) work.get(FIELD_SYSID);
			chargerData = (DBObject) work.get(FIELD_WORK_PM);
			resource = (BasicDBList) work.get(FIELD_WORK_RESOURCE);

			// 如果这个任务是先被暂停后被使用开始恢复的，那么是已经有了任务通知
			// 需要先删除，后新增
			userParticipateWorkCollection.remove(new BasicDBObject().append(
					FIELD_ID, id));
			userChargedWorkCollection.remove(new BasicDBObject().append(
					FIELD_ID, id));
			// 同步user任务负责人
			if (chargerData != null) {
				DataUtil.saveUserRelationInformation(null,
						(ObjectId) chargerData.get(FIELD_SYSID),
						COLLECTION_USER_WORK_IN_CHARGED, id);
			}

			// 同步user任务参与者
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
	 * 列出部门下的所有项目
	 * 
	 * @param deptId
	 * @param cascade
	 *            是否级联列出部门下级的部门的项目
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
				// 删除message
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
				// 删除message
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
