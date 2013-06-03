package com.sg.common.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.drools.KnowledgeBase;
import org.drools.definition.process.Node;
import org.drools.definition.process.Process;
import org.drools.definition.process.WorkflowProcess;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.process.workitem.wsht.CommandBasedWSHumanTaskHandler;
import org.jbpm.task.AccessType;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.responsehandlers.BlockingGetTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.jbpm.workflow.core.node.ForEachNode;
import org.jbpm.workflow.core.node.HumanTaskNode;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.bpm.service.BPM;
import com.sg.bpm.service.BPMService;
import com.sg.bpm.service.HTService;
import com.sg.bpm.service.task.CommonServiceTaskHandler;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.ui.UIConstants;
import com.sg.user.UserSessionContext;

public class WorkflowService extends CommonService {

	/**
	 * 具有进程上下文要求
	 * 
	 * @return
	 */
	public String getCurrentSiteKnowledgebaseName() {

		ObjectId siteId = UserSessionContext.getSession().getSiteContextId();
		DBObject site = getSiteObject(siteId);
		if (site != null) {
			return (String) site.get(FIELD_KNOWLEDGEBASE);
		}
		return null;
	}

	/**
	 * 无进程上下文要求
	 * 
	 * @param sitename
	 * @return
	 */
	public String getSiteKnowledgebaseName(String sitename) {

		DBObject site = getSiteObject(sitename);
		if (site != null) {
			return (String) site.get(FIELD_KNOWLEDGEBASE);
		}
		return null;
	}

	/**
	 * 具有进程上下文要求
	 * 
	 * @return
	 */
	public KnowledgeBase getCurrentSiteKnowledgebase() {

		String kname = getCurrentSiteKnowledgebaseName();
		if (kname != null) {
			return BPM.getBPMService().getKnowledgeBase(kname);
		}
		return null;
	}

	public KnowledgeBase getSiteKnowledgebase(String sitename) {

		String kname = getSiteKnowledgebaseName(sitename);
		if (kname != null) {
			return BPM.getBPMService().getKnowledgeBase(kname);
		}
		return null;
	}

	public List<HumanTaskNode> getHumanNodesInProcessDefinition(
			KnowledgeBase base, String processDefinitionId) {

		Process process = base.getProcess(processDefinitionId);
		return getHumanNodesInProcessDefinition(process);
	}

	public List<HumanTaskNode> getHumanNodesInProcessDefinition(
			String processDefinitionId) {

		KnowledgeBase base = getCurrentSiteKnowledgebase();
		Process process = base.getProcess(processDefinitionId);
		return getHumanNodesInProcessDefinition(process);
	}

	public List<HumanTaskNode> getHumanNodesInProcessDefinition(Process process) {

		if (process instanceof WorkflowProcess) {
			List<HumanTaskNode> result = new ArrayList<HumanTaskNode>();
			Node[] nodes = ((WorkflowProcess) process).getNodes();
			for (int i = 0; i < nodes.length; i++) {
				Node nodesItem = nodes[i];
				if (nodesItem instanceof HumanTaskNode) {
					result.add((HumanTaskNode) nodesItem);
				} else if (nodesItem instanceof ForEachNode) {
					// 忽略多实例的节点，这可能在多实例中的活动中设置变量参与者时出现问题
				}
			}
			return result;
		}
		return null;
	}

	/**
	 * 判断节点是否需要指定人
	 * 
	 * @param workData
	 * @param node
	 * @return 指定人的obs objectid
	 * @throws ServiceException
	 */
	public NodeAssignment getNodeAssignment(DBObject workData,
			HumanTaskNode node) throws ServiceException {

		NodeAssignment nass = new NodeAssignment(node);
		// 判断有没有设置人
		DBObject assdef = (DBObject) workData
				.get(FIELD_WORK_PROCESS_ASSINGMENT_DEFINITION);
		if (assdef != null) {
			String nodeActorParameter = nass.getNodeActorParameter();
			if (nodeActorParameter != null) {
				nass.setAssignmentId((ObjectId) assdef.get(nodeActorParameter));
			}
		}
		return nass;
	}

	/**
	 * 
	 * @param workTemplate
	 * @throws Exception
	 */
	public void activateWorkTemplate(DBObject workTemplate) throws Exception {

		// 检查是否有流程定义
		String processDefId = (String) workTemplate
				.get(FIELD_PROCESS_DEFINITION_ID);
		if (processDefId != null) {
			// 检查流程定义中的节点上人是否都给了OBS的值
			KnowledgeBase base = getCurrentSiteKnowledgebase();
			List<HumanTaskNode> nodes = getHumanNodesInProcessDefinition(base,
					processDefId);
			if (nodes != null) {
				for (int i = 0; i < nodes.size(); i++) {
					HumanTaskNode nodesItem = nodes.get(i);

					NodeAssignment nass = getNodeAssignment(workTemplate,
							nodesItem);

					if (nass.isNotNeedAssignment()) {

					} else if (nass.isNeedAssignment()) {
						// throw new
						// Exception(UIConstants.EXCEPTION_UNCOMPLETE_ASSIGNMENT);
					} else if (nass.isAlreadyAssignment()) {

					}
				}
			}
		}

		workTemplate.put(FIELD_ACTIVATE, true);

		workTemplateCollection.update(
				new BasicDBObject().append(FIELD_SYSID,
						workTemplate.get(FIELD_SYSID)),
				new BasicDBObject().append(SET,
						new BasicDBObject().append(FIELD_ACTIVATE, true)));
	}

	public void disactivateWorkTemplate(DBObject workTemplate) {

		workTemplate.put(FIELD_ACTIVATE, false);

		workTemplateCollection.update(
				new BasicDBObject().append(FIELD_SYSID,
						workTemplate.get(FIELD_SYSID)),
				new BasicDBObject().append(SET,
						new BasicDBObject().append(FIELD_ACTIVATE, false)));
	}

	public void activateProjectTemplate(DBObject projectTemplate)
			throws Exception {

		// 取出所有的工作
		ObjectId ptId = (ObjectId) projectTemplate.get(FIELD_SYSID);
		DBCursor cur = workTemplateCollection.find(new BasicDBObject().append(
				FIELD_WBSPARENT, ptId));
		while (cur.hasNext()) {
			DBObject workTemplate = cur.next();
			// 检查是否有流程定义
			String processDefId = (String) workTemplate
					.get(FIELD_PROCESS_DEFINITION_ID);
			if (processDefId != null) {
				// 检查流程定义中的节点上人是否都给了OBS的值
				KnowledgeBase base = getCurrentSiteKnowledgebase();
				List<HumanTaskNode> nodes = getHumanNodesInProcessDefinition(
						base, processDefId);
				if (nodes != null) {
					for (int i = 0; i < nodes.size(); i++) {
						HumanTaskNode nodesItem = nodes.get(i);

						NodeAssignment nass = getNodeAssignment(workTemplate,
								nodesItem);

						if (nass.isNotNeedAssignment()) {

						} else if (nass.isNeedAssignment()) {
							throw new Exception(
									UIConstants.EXCEPTION_UNCOMPLETE_ASSIGNMENT);
						} else if (nass.isAlreadyAssignment()) {

						}
					}
				}
			}
		}
		projectTemplate.put(FIELD_ACTIVATE, true);
		projectTemplateCollection.update(
				new BasicDBObject().append(FIELD_SYSID, ptId),
				new BasicDBObject().append(SET,
						new BasicDBObject().append(FIELD_ACTIVATE, true)));
	}

	public void disactivateProjectTemplate(DBObject projectTemplate) {

		projectTemplate.put(FIELD_ACTIVATE, false);

		projectTemplateCollection.update(
				new BasicDBObject().append(FIELD_SYSID,
						projectTemplate.get(FIELD_SYSID)),
				new BasicDBObject().append(SET,
						new BasicDBObject().append(FIELD_ACTIVATE, false)));
	}

	public Long startWorkProcess(ObjectId workId) throws ServiceException {

		DBObject workData = getWorkObject(workId);
		return startWorkProcess(workData);
	}

	/**
	 * 有进程上下文要求
	 * 
	 * @param workData
	 * @return
	 * @throws ServiceException
	 */
	public Long startWorkProcess(DBObject workData) throws ServiceException {

		// 取出流程模板
		String procDefinitionId = (String) workData
				.get(FIELD_PROCESS_DEFINITION_ID);
		if (procDefinitionId == null) {
			return null;
		}

		// 根据工作数据生成用户表
		Map<String, String> userMap = getUserMapFromWorkData(workData,
				procDefinitionId);
		// 启动流程
		ProcessInstance pi = startHumanProcess(procDefinitionId, userMap,
				workData);

		return pi.getId();
	}

	/**
	 * 无进程上下文要求
	 * 
	 * @param workData
	 * @param sitename
	 * @return
	 * @throws ServiceException
	 */
	public Long startWorkProcess(DBObject workData, String sitename)
			throws ServiceException {

		// 取出流程模板
		String procDefinitionId = (String) workData
				.get(FIELD_PROCESS_DEFINITION_ID);
		if (procDefinitionId == null) {
			return null;
		}

		// 根据工作数据生成用户表
		Map<String, String> userMap = getUserMapFromWorkData(workData,
				procDefinitionId, sitename);
		// 启动流程
		ProcessInstance pi = startHumanProcess(procDefinitionId, userMap,
				workData, sitename);

		return pi.getId();
	}

	public void updateWorkProcessId(ObjectId workId, Long pid) {

		workCollection.update(new BasicDBObject().append(FIELD_SYSID, workId),
				new BasicDBObject().append(SET, new BasicDBObject().append(
						IDBConstants.FIELD_WFINFO_PROCESSINSTANCEID, pid)));
	}

	private Map<String, String> getUserMapFromWorkData(DBObject workData,
			String procDefinitionId) {

		HashMap<String, String> result = new HashMap<String, String>();

		// 取出流程

		KnowledgeBase base = getCurrentSiteKnowledgebase();
		Process process = base.getProcess(procDefinitionId);
		List<HumanTaskNode> humanNodes = getHumanNodesInProcessDefinition(process);

		for (int i = 0; i < humanNodes.size(); i++) {
			HumanTaskNode node = humanNodes.get(i);
			try {
				NodeAssignment nass = getNodeAssignment(workData, node);
				nass.setParameterMap(result, new Object[] { workData, process });
			} catch (ServiceException e) {
			}
		}

		return result;
	}

	private Map<String, String> getUserMapFromWorkData(DBObject workData,
			String procDefinitionId, String sitename) {

		HashMap<String, String> result = new HashMap<String, String>();

		// 取出流程

		KnowledgeBase base = getSiteKnowledgebase(sitename);
		Process process = base.getProcess(procDefinitionId);
		List<HumanTaskNode> humanNodes = getHumanNodesInProcessDefinition(process);

		for (int i = 0; i < humanNodes.size(); i++) {
			HumanTaskNode node = humanNodes.get(i);
			try {
				NodeAssignment nass = getNodeAssignment(workData, node);
				nass.setParameterMap(result, new Object[] { workData, process });
			} catch (ServiceException e) {
			}
		}

		return result;
	}

	/**
	 * 有站点上下文要求
	 * 
	 * @param processId
	 * @param userMap
	 * @param workData
	 * @return
	 * @throws ServiceException
	 */
	public ProcessInstance startHumanProcess(String processId,
			Map<String, String> userMap, DBObject workData)
			throws ServiceException {
		return startHumanProcess(processId, userMap, workData, null);
	}

	/**
	 * <p>
	 * <strong>启动一个流程,无站点上下文要求</strong>
	 * </p>
	 * 
	 * <p>
	 * 该方法将保证参与者一定存在，如果不存在将添加这个参与者
	 * </p>
	 * 
	 * @param siteId
	 *            站点上下文id
	 * @param processId
	 *            流程id
	 * @param userMap
	 *            参与者变量赋值表
	 * @param workData
	 * @param inputValueMap
	 *            流程输入值赋值表
	 * @param handler
	 * @return
	 * @throws ServiceException
	 */
	public ProcessInstance startHumanProcess(String processId,
			Map<String, String> userMap, DBObject workData, String kbname)
			throws ServiceException {

		Map<String, Object> params = new HashMap<String, Object>();

		HTService ts = BPM.getHumanTaskService();
		if (userMap != null) {
			Collection<String> idSet = userMap.values();
			Iterator<String> iter = idSet.iterator();
			while (iter.hasNext()) {
				String item = iter.next();
				if (item.contains(",")) {
					String[] subItems = item.split(",");
					for (int i = 0; i < subItems.length; i++) {
						ts.addParticipateUser(subItems[i]);
					}
				} else {
					ts.addParticipateUser(item);
				}
			}

			params.putAll(userMap);
		}

		StatefulKnowledgeSession ksession = getKnowledgeSession(kbname);

		ObjectId workId = (ObjectId) workData.get(FIELD_SYSID);
		params.put("content", workId.toString());
		ProcessInstance pi = ksession.startProcess(processId, params);

		// 更新到工作的流程实例id上
		if (pi != null) {
			workCollection.update(new BasicDBObject().append(FIELD_SYSID,
					workId), new BasicDBObject().append(SET,
					new BasicDBObject().append(FIELD_WFINFO_PROCESSINSTANCEID,
							pi.getId())));
		} else {
			throw new ServiceException(ServiceException.SERVER_IS_BUSY_TRYAGAIN);
		}

		return pi;
	}

	// private void addHandler(){
	// HTService ts = BPM.getHumanTaskService();
	//
	// StatefulKnowledgeSession ksession = registeSiteSession(null);
	//
	// WorkItemHandler htHandler = ts.getWorkItemHandler(ksession);
	// CommonServiceTaskHandler stHandler = new CommonServiceTaskHandler();
	// ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
	// htHandler);
	// ksession.getWorkItemManager().registerWorkItemHandler("Service Task",
	// stHandler);
	// }

	private StatefulKnowledgeSession getKnowledgeSession(String kbname){
		
		if(kbname == null){
			kbname = getCurrentSiteKnowledgebaseName();
		}
		
		StatefulKnowledgeSession ksession = null;

		DBObject ksdata = ksessionCollection.findOne(new BasicDBObject()
				.append(IDBConstants.FIELD_KBNAME, kbname));
		BPMService bpmService = BPM.getBPMService();
		if (ksdata != null) {

			Integer sid = (Integer) ksdata.get(IDBConstants.FIELD_ID);

			if (sid != null) {
				try {
					ksession = bpmService.getSession(kbname, sid.intValue());
				} catch (Exception e) {
					ksessionCollection.remove(new BasicDBObject().append(
							FIELD_KBNAME, kbname));
					ksession = null;
				}
			}

		}

		if (ksession == null) {// 无法从持久化或者缓存中获得session
			ksession = bpmService.createSession(kbname);
			int sid = ksession.getId();
			// 保存知识库与session的索引到业务数据库
			ksessionCollection.insert(new BasicDBObject().append(
					IDBConstants.FIELD_KBNAME, kbname).append(
					IDBConstants.FIELD_ID, sid));
		}

		CommandBasedWSHumanTaskHandler htHandler = new CommandBasedWSHumanTaskHandler();
		htHandler.setSession(ksession);
		CommonServiceTaskHandler stHandler = new CommonServiceTaskHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				htHandler);
		ksession.getWorkItemManager().registerWorkItemHandler("Service Task",
				stHandler);
		return ksession;
	}
	
//	private StatefulKnowledgeSession getSiteSession(String sitename) {
//
//		StatefulKnowledgeSession ksession = null;
//
//		String kbname;
//		if (sitename == null) {
//			kbname = getCurrentSiteKnowledgebaseName();
//		} else {
//			kbname = getSiteKnowledgebaseName(sitename);
//		}
//		// 查询有没有持久化的session,如果有，取出，如果没有创建并保存
//		return getKnowledgeSession(kbname);
//	}

	public BasicDBObject addProcessTask(Task task, String userId,
			ObjectId workId) {

		DBCollection collection;

		// 根据userId获得用户的oid
		DBObject userData = BusinessService.getOrganizationService()
				.getUserByUId(userId);
		ObjectId userOid = (ObjectId) userData.get(FIELD_SYSID);

		// 查询用户负责的任务列表
		DBObject o = new BasicDBObject().append(FIELD_USEROID, userOid).append(
				FIELD_ID, workId);
		DBObject fields = new BasicDBObject().append(FIELD_SYSID, 1);
		DBObject data = userChargedWorkCollection.findOne(o, fields);
		if (data == null) {
			data = userParticipateWorkCollection.findOne(o, fields);
			collection = userParticipateWorkCollection;
		} else {
			collection = userChargedWorkCollection;
		}

		ObjectId refUserWorkId;
		if (data == null) {
			refUserWorkId = new ObjectId();
			data = new BasicDBObject();
			data.put(FIELD_SYSID, refUserWorkId);
			data.put(FIELD_USEROID, userOid);
			data.put(FIELD_ID, workId);
			setSystemCreateInfo(data);
			userParticipateWorkCollection.insert(data);
			collection = userParticipateWorkCollection;
		} else {
			refUserWorkId = (ObjectId) data.get(FIELD_SYSID);
		}

		Long taskId = task.getId();
		Status taskStatus = task.getTaskData().getStatus();
		String processId = task.getTaskData().getProcessId();
		long pid = task.getTaskData().getProcessInstanceId();
		String taskName = task.getNames().get(0).getText();
		String taskComment = task.getDescriptions().get(0).getText();
		long parentTaskId = task.getTaskData().getParentId();

		BasicDBObject wfInfo = new BasicDBObject();

		setWorkflowInfoData(wfInfo, taskId, parentTaskId, taskName,
				taskComment, taskStatus.name(), pid, processId,
				(String) userData.get(IDBConstants.FIELD_NAME), userId);

		// 系统根据流程id和任务名称以及任务状态确定编辑器
		collection.update(new BasicDBObject()
				.append(FIELD_SYSID, refUserWorkId), new BasicDBObject()
				.append(SET, new BasicDBObject().append(
						IDBConstants.FIELD_WFINFO, wfInfo)));

		// 修改任务的当前状态
		workCollection.update(new BasicDBObject().append(FIELD_SYSID, workId),
				new BasicDBObject().append(SET, new BasicDBObject().append(
						IDBConstants.FIELD_WFINFO, wfInfo)));

		// 根据任务Process参数判断是否自动启动或者自动完成

		return wfInfo;
	}

	private void setWorkflowInfoData(BasicDBObject wfInfo, Long taskId,
			Long parentTaskId, String taskName, String taskComment,
			String status, long pid, String processId, String userName,
			String userId) {

		wfInfo.put(FIELD_WFINFO_TASKID, taskId);
		wfInfo.put(FIELD_WFINFO_PARENTTASKID, parentTaskId);
		wfInfo.put(FIELD_WFINFO_TASKNAME, taskName);
		wfInfo.put(FIELD_WFINFO_TASKCOMMENT, taskComment);
		wfInfo.put(FIELD_WFINFO_TASKSTATUS, status);
		wfInfo.put(FIELD_WFINFO_PROCESSINSTANCEID, pid);
		wfInfo.put(FIELD_WFINFO_PROCESSID, processId);
		wfInfo.put(FIELD_WFINFO_ACTORNAME, userName);
		wfInfo.put(FIELD_WFINFO_ACTORID, userId);

	}
	
	public DBObject startTask(DBObject wfInfo, ObjectId workId,
			BasicDBObject workProcessHistoryRecord) throws ServiceException {
		return startTask(wfInfo,null,workId,workProcessHistoryRecord,null);
	}	

	public DBObject startTask(DBObject wfInfo, String userId,ObjectId workId,
			BasicDBObject workProcessHistoryRecord,String siteName) throws ServiceException {
		if(userId == null){
			userId = UserSessionContext.getSession().getUserId();
		}
		
		if (wfInfo == null) {
			throw new ServiceException(ServiceException.NO_WORKFLOWINFO);
		}

		long taskId = ((Long) wfInfo.get(IDBConstants.FIELD_WFINFO_TASKID))
				.longValue();


		TaskClient taskClient = getTaskClient(userId);
		BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();

		taskClient.start(taskId, userId, operationResponseHandler);
		operationResponseHandler.waitTillDone(1000);

		// 更新工作流信息

		// 保存到工作的流程历史
		if (workProcessHistoryRecord == null) {
			workProcessHistoryRecord = new BasicDBObject();
		}
		workProcessHistoryRecord.put(FIELD_WF_HISTORY_OPEN_DATE, new Date());
		workProcessHistoryRecord.put(FIELD_WFINFO_ACTORID,
				wfInfo.get(FIELD_WFINFO_ACTORID));
		workProcessHistoryRecord.put(FIELD_WFINFO_ACTORNAME,
				wfInfo.get(FIELD_WFINFO_ACTORNAME));
		workProcessHistoryRecord.put(FIELD_WFINFO_TASKNAME,
				wfInfo.get(FIELD_WFINFO_TASKNAME));
		workProcessHistoryRecord.put(FIELD_WFINFO_TASKID,
				wfInfo.get(FIELD_WFINFO_TASKID));

		saveProcessHistory(workId, workProcessHistoryRecord,
				VALUE_WF_TASK_OPER_START);

		BlockingGetTaskResponseHandler getTaskResponsehandler = new BlockingGetTaskResponseHandler();
		taskClient.getTask(taskId, getTaskResponsehandler);
		Task task = getTaskResponsehandler.getTask(1000);

		return addProcessTask(task, userId, workId);

	}

	public DBObject completeTask(DBObject wfInfo, String userId,
			ObjectId workId, Map<String, Object> inputParameter,
			DBObject workProcessHistoryRecord,String kbName) throws ServiceException {
		
		if(userId == null){
			userId = UserSessionContext.getSession().getUserId();
		}
		// 如果任务可以开始，必须先开始
		if (canStart(wfInfo)) {
			startTask(wfInfo, userId,workId, null,kbName);
		}

		if (wfInfo == null) {
			throw new ServiceException(ServiceException.NO_WORKFLOWINFO);
		}

		long taskId = ((Long) wfInfo.get(IDBConstants.FIELD_WFINFO_TASKID))
				.longValue();

		if (workProcessHistoryRecord == null) {
			workProcessHistoryRecord = new BasicDBObject();
		}
		workProcessHistoryRecord.put(IDBConstants.FIELD_WF_HISTORY_CLOSE_DATE,
				new Date());
		workProcessHistoryRecord.put(FIELD_WFINFO_ACTORID,
				wfInfo.get(FIELD_WFINFO_ACTORID));
		workProcessHistoryRecord.put(FIELD_WFINFO_ACTORNAME,
				wfInfo.get(FIELD_WFINFO_ACTORNAME));
		workProcessHistoryRecord.put(FIELD_WFINFO_TASKNAME,
				wfInfo.get(FIELD_WFINFO_TASKNAME));
		workProcessHistoryRecord.put(FIELD_WFINFO_TASKID,
				wfInfo.get(FIELD_WFINFO_TASKID));

		TaskClient taskClient = getTaskClient(userId);
		BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
		if (inputParameter != null) {
			ContentData contentData = marshal(inputParameter);
			// ContentMarshallerHelper.marshal(inputParameter, new
			// ContentMarshallerContext(), null);
			taskClient.complete(taskId, userId, contentData,
					operationResponseHandler);
		} else {
			taskClient.complete(taskId, userId, null, operationResponseHandler);
		}

		operationResponseHandler.waitTillDone(1000);

		// 更新工作流信息

		BlockingGetTaskResponseHandler getTaskResponsehandler = new BlockingGetTaskResponseHandler();
		taskClient.getTask(taskId, getTaskResponsehandler);
		Task task = getTaskResponsehandler.getTask(1000);
		long workItemId = task.getTaskData().getWorkItemId();
		StatefulKnowledgeSession session = getKnowledgeSession(kbName);
		session.getWorkItemManager().completeWorkItem(workItemId,
				inputParameter);

		// 保存到工作的流程历史
		saveProcessHistory(workId, workProcessHistoryRecord,
				VALUE_WF_TASK_OPER_COMPLETE);

		return addProcessTask(task, userId, workId);
	}

	public DBObject completeTask(DBObject wfInfo, ObjectId workId,
			Map<String, Object> inputParameter,
			DBObject workProcessHistoryRecord) throws ServiceException {

		return completeTask(wfInfo, null, workId, inputParameter,
				workProcessHistoryRecord,null);

	}

	private TaskClient getTaskClient(String userId) {
		TaskClient tc = BPM.getHumanTaskService().getTaskClient(userId);
		// String sitename =
		// BusinessService.getOrganizationService().getSiteNameofUserId(userId);
		// getSiteSession(sitename);
		return tc;
	}

	public TaskClient createTaskClient(String clientId) {
		return BPM.getHumanTaskService().createTaskClient(clientId);
	}

	private ContentData marshal(Map<String, Object> inputParameter) {
		ContentData contentData = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(inputParameter);
			out.close();
			contentData = new ContentData();
			contentData.setContent(bos.toByteArray());
			contentData.setAccessType(AccessType.Inline);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return contentData;
	}

	public boolean canStart(DBObject wfinfo) {

		// created, ready,reserved 都可以开始
		if (wfinfo != null) {
			Object status = wfinfo.get(IDBConstants.FIELD_WFINFO_TASKSTATUS);
			if (Status.Created.name().equals(status)) {
				return true;
			}
			if (Status.Ready.name().equals(status)) {
				return true;
			}
			if (Status.Reserved.name().equals(status)) {
				return true;
			}
		}
		return false;
	}

	public boolean canFinish(DBObject wfinfo) {

		if (wfinfo != null) {
			Object status = wfinfo.get(IDBConstants.FIELD_WFINFO_TASKSTATUS);
			if (Status.Completed.name().equals(status)) {
				return false;
			}
			if (Status.Error.name().equals(status)) {
				return false;
			}
			if (Status.Failed.name().equals(status)) {
				return false;
			}
			if (Status.Obsolete.name().equals(status)) {
				return false;
			}
			if (Status.Exited.name().equals(status)) {
				return false;
			}
			return true;
		}
		return false;
	}

	public void syncCurrentHumanTask(String userId) {
		// // 得到该用户的站点
		// String siteName = BusinessService.getOrganizationService()
		// .getSiteNameofUserId(userId);

		TaskClient taskClient = getTaskClient(userId);
		// 获得该用户的工作流潜在分配信息

		syncHumanTask(taskClient, userId);
	}

	public void syncHumanTask(TaskClient taskClient, String userId) {
		BlockingTaskSummaryResponseHandler taskSummaryResponseHandler = new BlockingTaskSummaryResponseHandler();
		taskClient.getTasksAssignedAsPotentialOwner(userId, "en-UK",
				taskSummaryResponseHandler);

		List<TaskSummary> tslist = taskSummaryResponseHandler.getResults();
		BlockingGetTaskResponseHandler getTaskResponsehandler;
		for (int i = 0; i < tslist.size(); i++) {
			TaskSummary item = tslist.get(i);
			getTaskResponsehandler = new BlockingGetTaskResponseHandler();

			taskClient.getTask(item.getId(), getTaskResponsehandler);

			Task task = getTaskResponsehandler.getTask(1000);

			// 根据流程实例号取出workID
			ObjectId workId = getWorkofProcessInstance(item
					.getProcessInstanceId());
			if (workId != null) {
				addProcessTask(task, userId, workId);
			}
		}
	}

	public ObjectId getWorkofProcessInstance(long processInstanceId) {

		DBObject work = workCollection.findOne(new BasicDBObject().append(
				FIELD_WFINFO_PROCESSINSTANCEID, processInstanceId),
				new BasicDBObject().append(FIELD_SYSID, 1));
		if (work != null) {
			return (ObjectId) work.get(FIELD_SYSID);
		}
		return null;
	}

	public void addUserInBPM(String uid) {

		BPM.getHumanTaskService().addParticipateUser(uid);
	}

	public void saveProcessHistory(ObjectId workId, DBObject rec,
			String operation) {

		rec.put(FIELD_WF_HISTORY_TASK_OPERATION, operation);

		DBObject work = getWorkObject(workId);
		BasicDBList history = (BasicDBList) work.get(FIELD_PROCESSHISTORY);
		if (history == null) {
			history = new BasicDBList();
			history.add(rec);
		} else {
			if (operation.equals(VALUE_WF_TASK_OPER_START)) {// 如果是开始，直接插入到第一条记录
				history.add(0, rec);
			} else if (operation.equals(VALUE_WF_TASK_OPER_COMPLETE)) {// 如果是完成，找到开始的记录，更新到该记录
				Object newInfoActorid = rec.get(FIELD_WFINFO_ACTORID);
				Object newInfoTaskid = rec.get(FIELD_WFINFO_TASKID);

				DBObject startItem = null;
				for (int i = 0; i < history.size(); i++) {
					// 符合条件的记录是，FIELD_WFINFO_ACTORID和FIELD_WFINFO_TASKID都相同的记录
					DBObject hi = (DBObject) history.get(i);
					Object oldInfoActorid = hi.get(FIELD_WFINFO_ACTORID);
					Object oldInfoTaskid = hi.get(FIELD_WFINFO_TASKID);
					Object oldInfoOperation = hi
							.get(FIELD_WF_HISTORY_TASK_OPERATION);

					if (oldInfoActorid.equals(newInfoActorid)
							&& newInfoTaskid.equals(oldInfoTaskid)
							&& oldInfoOperation
									.equals(VALUE_WF_TASK_OPER_START)) {
						startItem = hi;
						break;
					}
				}

				// 更新查找到的开始的记录
				if (startItem != null) {
					history.remove(startItem);

					// 将完成的信息写入到记录中
					Iterator<String> iter = rec.keySet().iterator();
					while (iter.hasNext()) {
						String key = iter.next();
						startItem.put(key, rec.get(key));
					}

					history.add(0, startItem);
				}
			}
		}
		workCollection.update(new BasicDBObject().append(FIELD_SYSID, workId),
				new BasicDBObject().append(SET, new BasicDBObject(
						FIELD_PROCESSHISTORY, history)));

	}

	public String getProcessNameByProcessId(String processDefId) {
		KnowledgeBase kbase = getCurrentSiteKnowledgebase();
		Collection<Process> processList = kbase.getProcesses();
		Iterator<Process> iter = processList.iterator();
		while (iter.hasNext()) {
			Process process = iter.next();
			if (processDefId.equals(process.getId())) {
				return process.getName();
			}
		}

		return null;
	}

	public Process getProcessByProcessId(String processDefId) {
		KnowledgeBase kbase = getCurrentSiteKnowledgebase();
		Collection<Process> processList = kbase.getProcesses();
		Iterator<Process> iter = processList.iterator();
		while (iter.hasNext()) {
			Process process = iter.next();
			if (processDefId.equals(process.getId())) {
				return process;
			}
		}

		return null;
	}

}
