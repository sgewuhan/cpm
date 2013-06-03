package com.sg.common.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.jbpm.task.service.TaskClient;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.SingleObject;
import com.sg.email.MailJob;
import com.sg.widget.util.Util;

public class WorkReminder extends Job {

	private JobChangeAdapter listener;
	private OrganizationService orgService;
	private WorkflowService wfService;
	private TaskClient taskClient;
	private WorkService wkService;

	public WorkReminder() {
		super("schedule");
		this.orgService = BusinessService.getOrganizationService();
		this.wfService = BusinessService.getWorkflowService();
		this.wkService = BusinessService.getWorkService();
		this.taskClient = wfService.createTaskClient("workreminder");
		listener = new JobChangeAdapter() {

			@Override
			public void done(IJobChangeEvent event) {
				schedule(getDelay());
			}

		};
		addJobChangeListener(listener);
	}

	public void start() {
		schedule(getDelay());
	}

	private int getDelay() {
		return BusinessService.getDefault().getWorkRetrieveInterval() * 60 * 1000;
	}
	

	public void stop() {
		removeJobChangeListener(listener);
		cancel();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		List<DBObject> userIdList = orgService.getUserIdList();
		for(DBObject userData:userIdList){
			try{
				
				String userId = (String) userData.get(OrganizationService.FIELD_UID);
				ObjectId useroid = (ObjectId) userData.get(OrganizationService.FIELD_SYSID);
				String email = (String) userData.get(OrganizationService.FIELD_EMAIL);
				if(email==null||email.isEmpty()){
					continue;
				}
				
				wfService.syncHumanTask(taskClient, userId);
				HashMap<String, ArrayList<MessageObject>> messageData = wkService.getUserMessageList(useroid);
				noticeUser(messageData ,userData);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return Status.OK_STATUS;
	}

	private void noticeUser(
			HashMap<String, ArrayList<MessageObject>> messageData,
			DBObject userData) {
		//得到
		ArrayList<MessageObject> readyList = messageData.get(WorkService.VALUE_PROCESS_READY);
		ArrayList<MessageObject> processList = messageData.get(WorkService.VALUE_PROCESS_PROCESS);
//		ArrayList<MessageObject> pauseList = messageData.get(WorkService.VALUE_PROCESS_PAUSE);
//		ArrayList<MessageObject> cancelList = messageData.get(WorkService.VALUE_PROCESS_CANCEL);
//		ArrayList<MessageObject> closeList = messageData.get(WorkService.VALUE_PROCESS_CLOSE);
		
		if(readyList.isEmpty()&&processList.isEmpty()){
			return;
		}
		
		for(MessageObject mo:readyList){
			sendNotice(mo,userData);
		}
		
		for(MessageObject mo:processList){
			SingleObject work = mo.getTargetSingleObject();
			DBObject wfInfor = (DBObject) work.getValue(IDBConstants.FIELD_WFINFO);
			if(wfInfor!=null){
				String actorId = (String) wfInfor.get(IDBConstants.FIELD_WFINFO_ACTORID);
				if(userData.get(WorkService.FIELD_UID).equals(actorId)){
					String taskStatus = (String) wfInfor.get(IDBConstants.FIELD_WFINFO_TASKSTATUS);
					if(IDBConstants.VALUE_WF_STATUS_CREATED.equalsIgnoreCase(taskStatus)
							||IDBConstants.VALUE_WF_STATUS_READY.equalsIgnoreCase(taskStatus)
							||IDBConstants.VALUE_WF_STATUS_RESERVED.equalsIgnoreCase(taskStatus)){
						sendNotice(mo,userData);
					}
				}
			}
		}
	}

	private void sendNotice(final MessageObject mo, DBObject userData) {
		Date noticeTime = (Date) mo.getMessageValue(WorkService.FIELD_NOTICE_TIME);
		if(noticeTime!=null){//已经通知的不再通知
			return;
		}
		
		String emailAddress = (String) userData.get(WorkService.FIELD_EMAIL);
		String title = "您有一个工作需要处理";
		
		String content = BusinessService.getNoticeHtml();
		content = getWorkHtml(mo,userData,content);
		
		MailJob mj = new MailJob(emailAddress, title, content);
		mj.addJobChangeListener(new JobChangeAdapter(){

			@Override
			public void done(IJobChangeEvent event) {
				if(event.getResult().isOK()){
					mo.putMessageValue(IDBConstants.FIELD_NOTICE_TIME, new Date());
					mo.saveMessage();
				}
				super.done(event);
			}
			
		});
		mj.schedule();
//		BusinessService.loginfor("处理工作通知：用户"+userData.get(IDBConstants.FIELD_NAME)+mo.getTargetValue(IDBConstants.FIELD_DESC));
	}

	
	public String getWorkHtml(MessageObject message, DBObject userData,String content) {
		String username = (String) userData.get(WorkService.FIELD_NAME);
		content = content.replace("[username]", username);

		String title = "工作："+(String) message.getTargetValue(IDBConstants.FIELD_DESC);
		ObjectId templateId = (ObjectId) message.getTargetValue(IDBConstants.FIELD_TEMPLATE);
		if(templateId!=null){
			DBObject template = wkService.getWorkTemplateObject(templateId);
			title = title + "["+template.get(IDBConstants.FIELD_DESC)+"]";
		}
		ObjectId projectId = (ObjectId) message.getTargetValue(IDBConstants.FIELD_ROOTID);
		if (projectId != null) {
			DBObject projectData = DataUtil.getDataObject(DataUtil.COLLECTION_PROJECT, projectId);
			title = title+"<br/>项目: " + (String) projectData.get(IDBConstants.FIELD_DESC);
		}
		content = content.replace("[title]", title);


		String planstart = "";
		String planfinish = "";

		Date _planStart = (Date) message.getTargetValue(IDBConstants.FIELD_PROJECT_PLANSTART);
		Date _planFinish = (Date) message.getTargetValue(IDBConstants.FIELD_PROJECT_PLANFINISH);
		SimpleDateFormat sdf = Util.getDateFormat(Util.SDF_M_D);
		try {
			planstart = sdf.format(_planStart);
			planfinish = sdf.format(_planFinish);
		} catch (Exception e) {
		}
		content = content.replace("[planstart]", planstart);
		content = content.replace("[planfinish]", planfinish);

		DBObject user = (DBObject) message.getTargetValue(IDBConstants.FIELD_WORK_PM);
		String charger = (String) user.get(IDBConstants.FIELD_NAME);
		content = content.replace("[workpm]",charger);

		content = content.replace("[workstatus]", getWorkStatus(message));


		if (message.isWorkflowData()) {
			content = content.replace("[workflowmessage]", "这项工作已经定义了工作流程,当前的活动:<br/>"+getWorkFlowStatus(message));
		}else{
			content = content.replace("[workflowmessage]", "");
		}
		
		
		BasicDBList historyList = (BasicDBList) message.getTargetValue(IDBConstants.FIELD_PROCESSHISTORY);
		if(historyList==null||historyList.isEmpty()){
			content = content.replace("[workflowhistory]", "");
		}else{
			StringBuffer sb = new StringBuffer();
			sb.append("流程的历史记录:<br/>");
			for(int i=0;i<historyList.size();i++){
				sb.append(getHistoryItemText((DBObject) historyList.get(i)));
				sb.append("<hr/>");
			}
			content = content.replace("[workflowhistory]", sb.toString());
		}
		return content;

	}
	
	

	private Object getHistoryItemText(DBObject dbo) {
		String userName = (String) dbo.get(IDBConstants.FIELD_WFINFO_ACTORNAME);
		userName = userName == null ? "" : userName;
		String taskName = (String) dbo.get(IDBConstants.FIELD_WFINFO_TASKNAME);
		taskName = taskName == null ? "" : taskName;
		String taskOperation = (String) dbo.get(IDBConstants.FIELD_WF_HISTORY_TASK_OPERATION);
		taskOperation = taskOperation == null ? "" : taskOperation;
		String taskChoice = (String) dbo.get(IDBConstants.FIELD_WF_HISTORY_CHOICE);
		taskChoice = taskChoice == null ? "" : taskChoice;
		Date openDate = (Date) dbo.get(IDBConstants.FIELD_WF_HISTORY_OPEN_DATE);
		String sOpenDate = openDate == null ? "" : Util.getDateFormat(Util.SDF_YY_MM_DD_HH_MM_SS).format(openDate);
		Date closeDate = (Date) dbo.get(IDBConstants.FIELD_WF_HISTORY_CLOSE_DATE);
		String sCloseDate = closeDate == null ? "" : Util.getDateFormat(Util.SDF_YY_MM_DD_HH_MM_SS).format(closeDate);
		
		//补充信息
		Object _addInfo = dbo.get(IDBConstants.FIELD_WFINFO_ADDITIONAL);
		String additionalInfomation = null;
		if(_addInfo!=null){
			additionalInfomation = _addInfo.toString();
		}
		
		String comment = (String) dbo.get(IDBConstants.FIELD_WF_HISTORY_COMMENT);
		comment = comment == null ? "" : comment;

		
		StringBuilder builder = new StringBuilder();
		builder.append("<span style=\" word-break:normal; width:" + 500
				+ "; display:block; white-space:pre-wrap;word-wrap : break-word ;overflow: hidden ;\">");

		
		builder.append("<strong><em>");
		builder.append(userName);
		builder.append("  ");
		
		builder.append(taskName);

		builder.append("  ");
		
		if("驳回".equals(taskChoice)||"否决".equals(taskChoice)||"不通过".equals(taskChoice)||"不同意".equals(taskChoice)||"反对".equals(taskChoice)){
			builder.append("<span  style=\"color:red\">");
			builder.append(taskChoice);
			builder.append("</span >");
		}else if("整改".equals(taskChoice)){
			builder.append("<span  style=\"color:orange\">");
			builder.append(taskChoice);
			builder.append("</span >");
		}else{
			builder.append("<span  style=\"color:green\">");
			builder.append(taskChoice);
			builder.append("</span >");
		}
		
		builder.append("</em></strong><small><br/>");

		builder.append("<em>");

		builder.append(taskOperation);
		builder.append("  ");
		builder.append(sOpenDate);
		builder.append(" - ");
		builder.append(sCloseDate);

		builder.append("</em>");

		builder.append("<br/>");
		
		if(additionalInfomation!=null){
			builder.append("<strong>");
			builder.append(additionalInfomation);
			builder.append("</strong>");
			builder.append("<br/>");
		}
		builder.append(comment);

		builder.append("</small>");
		builder.append("</span>");
		
		return builder.toString();
	}

	private String getWorkFlowStatus(MessageObject message) {
		
		SingleObject work = message.getTargetSingleObject();
		DBObject wfInfor = (DBObject) work.getValue(IDBConstants.FIELD_WFINFO);
		if (wfInfor == null) {
			return "流程尚未启动";
		} else {
			Object taskName = wfInfor.get(IDBConstants.FIELD_WFINFO_TASKNAME);
			Object taskUserName = wfInfor.get(IDBConstants.FIELD_WFINFO_ACTORNAME);
			String taskStatus = (String) wfInfor.get(IDBConstants.FIELD_WFINFO_TASKSTATUS);
			String status = getStatusChinessname(taskStatus);
			return  "名称："+taskName +"<br/>状态："+status +"<br/>承担者："+taskUserName;
		}
	}

	private String getStatusChinessname(String taskStatus) {
		if(IDBConstants.VALUE_WF_STATUS_CREATED.equalsIgnoreCase(taskStatus)){
			return "已经分配";
		}
		if(IDBConstants.VALUE_WF_STATUS_READY.equalsIgnoreCase(taskStatus)){
			return "准备处理";
		}
		if(IDBConstants.VALUE_WF_STATUS_RESERVED.equalsIgnoreCase(taskStatus)){
			return "等待处理";
		}
		if(IDBConstants.VALUE_WF_STATUS_INPROGRESS.equalsIgnoreCase(taskStatus)){
			return "正在处理";
		}
		if(IDBConstants.VALUE_WF_STATUS_COMPLETE.equalsIgnoreCase(taskStatus)){
			return "完成处理";
		}
		return null;
	}


	private String getWorkStatus(MessageObject message) {

		if (message.isReady()) {
			return  "准备";
		}

		if (message.isCancel()) {
			return  "取消";
		}

		if (message.isClose()) {
			return  "完成";
		}

		if (message.isPause()) {
			return  "暂停";
		}

		if (message.isProcess()) {
			return  "进行";
		}

		return "";
	}
}
