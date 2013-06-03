package com.sg.cpm.myworks.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.types.ObjectId;
import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.MessageObject;
import com.sg.common.service.WorkService;
import com.sg.db.model.SingleObject;
import com.sg.resource.Resource;
import com.sg.user.UserSessionContext;
import com.sg.widget.util.FileUtil;
import com.sg.widget.util.Util;

public class WorkLabelProvider extends ColumnLabelProvider {

//	private WorkflowService workflowService;
	private WorkService workService;
	
	public WorkLabelProvider(){
//		workflowService = BusinessService.getWorkflowService();
		workService = BusinessService.getWorkService();
	}


	@Override
	public String getText(Object element) {

		if (element instanceof MessageObject) {
			return getWorkHtml((MessageObject) element);
		} else if (element instanceof DBObject) {
			return getDocumentHtml((DBObject) element);
		}
		return "ERROR UNSUPPORT DATA";
	}

	private String getDocumentHtml(DBObject element) {

		String imageUrl = getDocumentImageURL();
		String desc = (String) element.get(IDBConstants.FIELD_DESC);
		String creator = (String) element.get(IDBConstants.FIELD_CREATER_NAME);
		Date _createDate = (Date) element.get(IDBConstants.FIELD_CREATE_DATE);
		Date _modifyDate = (Date) element.get(IDBConstants.FIELD_MODIFY_DATE);
		SimpleDateFormat sdf = Util.getDateFormat(Util.SDF_M_D);
		String createDate = "";
		String modifyDate = "";
		try {
			createDate = sdf.format(_createDate);
		} catch (Exception e) {
		}
		try {
			modifyDate = sdf.format(_modifyDate);
		} catch (Exception e) {
		}

		StringBuilder builder = new StringBuilder();
		
		if(!Util.isMozilla40Client()){
			builder.append("<img src=\"");
			builder.append(imageUrl);
			builder.append("\" style='float:left;padding:2px 4px 2px 4px' width='48' height='48' />");
		}else{
			builder.append("文档: ");
		}

		// builder.append("<strong>");
		builder.append(desc);
		// builder.append("</strong>");

		builder.append("<small><br/>");
		builder.append("创建者: <i>" + creator + "</i>");
		builder.append(" 创建时间:<i> " + createDate + "</i>");
		builder.append("<br/>");

		builder.append("最后一次修改: <i>" + modifyDate + "</i>");

		builder.append("<br/></small>");

		return builder.toString();
	}

	public String getWorkHtml(MessageObject message) {

		String imageUrl = getWorkImageURL(message);
		String desc = (String) message.getTargetValue(IDBConstants.FIELD_DESC);
		ObjectId templateId = (ObjectId) message.getTargetValue(IDBConstants.FIELD_TEMPLATE);
		if(templateId!=null){
			DBObject template = workService.getWorkTemplateObject(templateId);
			desc = desc + "["+template.get(IDBConstants.FIELD_DESC)+"]";
		}
		
		String title = "";
		ObjectId projectId = (ObjectId) message.getTargetValue(IDBConstants.FIELD_ROOTID);
		if (projectId != null) {
			DBObject projectData = DataUtil.getDataObject(DataUtil.COLLECTION_PROJECT, projectId);
			title = " 项目: " + (String) projectData.get(IDBConstants.FIELD_DESC);
		}
		
		

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

		String planInfo = "";
		if (planstart.length() < 1 || planfinish.length() < 1) {
			planInfo = "N/A";
		} else {
			planInfo = planstart + " - " + planfinish;
		}

		String actualstart = "";
		String actualfinish = "";

		Date _actualStart = (Date) message.getTargetValue(IDBConstants.FIELD_PROJECT_ACTUALSTART);
		Date _actualFinish = (Date) message.getTargetValue(IDBConstants.FIELD_PROJECT_ACTUALFINISH);
		try {
			actualstart = sdf.format(_actualStart);
			actualfinish = sdf.format(_actualFinish);
		} catch (Exception e) {
		}

		String actualInfo = "";
		if(actualstart.length() > 0){
			actualInfo = actualstart +" - ";
		}
		if(actualfinish.length() > 0){
			actualInfo = actualInfo +" - "+actualfinish;
		}
		

		DBObject user = (DBObject) message.getTargetValue(IDBConstants.FIELD_WORK_PM);
		String charger = (String) user.get(IDBConstants.FIELD_NAME);

//		String resources = "";
//		BasicDBList resourceList = (BasicDBList) message.getTargetValue(IDBConstants.FIELD_WORK_RESOURCE);
//		if (resourceList == null || resourceList.size() < 1) {
//			resources = "N/A";
//		} else {
//			Iterator<Object> iter = resourceList.iterator();
//			while (iter.hasNext()) {
//				resources = resources + (String) ((DBObject) iter.next()).get(IDBConstants.FIELD_NAME);
//
//				if (iter.hasNext()) {
//					resources = resources + ",";
//				}
//			}
//		}

//		String createDate = "";

//		Date _date = (Date) message.getMessageValue(IDBConstants.FIELD_CREATE_DATE);
//		try {
//			createDate = sdf.format(_date);
//		} catch (Exception e) {
//		}

		StringBuilder builder = new StringBuilder();
		if(!Util.isMozilla40Client()){
			builder.append("<span style='float:left;padding:2px 4px 2px 4px'>");
			builder.append("<img src=\"");
			builder.append(imageUrl);
			builder.append("\"  width='40' height='60' />");
			builder.append("</span>");
		}
		
		builder.append("<span>");
		if (!message.isMarkRead()) {
			builder.append("<strong>");
		}

		if (message.isMarkDelete()) {
			// builder.append(getStarImageUrl(Resource.IMAGE_DEL16));
		}

		if (message.isMarkStar()) {
			builder.append(getBulletImageUrl(Resource.IMAGE_STAR16));
		}


		builder.append(desc + title);


		if (!message.isMarkRead()) {
			builder.append("</strong>");
		}

		builder.append("<small><br/>");
		builder.append(getWorkStatus(message));
		builder.append("<i>" + charger + "</i>");
//		builder.append(" 参与: <i>" + resources + "</i>");
//		builder.append("<br/>");

		builder.append(" 计划:<i>" + planInfo + "</i>");
		builder.append(" 实际:<i>" + actualInfo + "</i>");
//		builder.append(" 创建:<i>" + createDate + "</i>");
		builder.append("<br/>");


		if (message.isWorkflowData()) {
			builder.append(getWorkFlowStatus(message));
		}

		builder.append("<br/></small>");
		builder.append("</span>");

		return builder.toString();

	}

	private String getWorkFlowStatus(MessageObject message) {
		String img = getBulletImageUrl(Resource.IMAGE_WF_WORK16);
		
		SingleObject work = message.getTargetSingleObject();
		DBObject wfInfor = (DBObject) work.getValue(IDBConstants.FIELD_WFINFO);
		if (wfInfor == null) {
			return img + "流程尚未启动";
		} else {
			Object taskName = wfInfor.get(IDBConstants.FIELD_WFINFO_TASKNAME);
//			Object taskUserName = wfInfor.get(IDBConstants.FIELD_WFINFO_ACTORNAME);
//			String processDefId = (String) work.getValue(IDBConstants.FIELD_PROCESS_DEFINITION_ID);
//			String processName = workflowService.getProcessNameByProcessId(processDefId);
			String taskStatus = (String) wfInfor.get(IDBConstants.FIELD_WFINFO_TASKSTATUS);
			String status = getStatusChinessname(taskStatus);
			return img + taskName +" ["+status +"]";
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

	private String getBulletImageUrl(String key) {

		String imageUrl = FileUtil.getImageLocationFromInputStream(key, Resource.getDefault().getImageInputStream(key));
		return "<img src='" + imageUrl + "' width='10' height='10' style='padding-right:2px;padding-top:2px;padding-left:2px;'/>";
	}

	private String getWorkImageURL(MessageObject data) {

		ObjectId uoid = UserSessionContext.getSession().getUserOId();
		String resource = null;
		DBObject pmData = (DBObject) data.getTargetValue(IDBConstants.FIELD_WORK_PM);

		boolean isPM = pmData != null && uoid.equals(pmData.get(IDBConstants.FIELD_SYSID));
		if (isPM) {
			resource = Resource.IMAGE_PMTASK60;
		} else {
			resource = Resource.IMAGE_TASK60;
		}

		return FileUtil.getImageLocationFromInputStream(resource, Resource.getDefault().getImageInputStream(resource));
	}

	private String getWorkStatus(MessageObject message) {

		if (message.isReady()) {
			return getBulletImageUrl(Resource.IMAGE_WORK_READY16) + "准备";
		}

		if (message.isCancel()) {
			return getBulletImageUrl(Resource.IMAGE_WORK_CANCEL16) + "取消";
		}

		if (message.isClose()) {
			return getBulletImageUrl(Resource.IMAGE_WORK_CLOSE16) + "完成";
		}

		if (message.isPause()) {
			return getBulletImageUrl(Resource.IMAGE_WORK_PAUSE16) + "暂停";
		}

		if (message.isProcess()) {
			return getBulletImageUrl(Resource.IMAGE_WORK_PROCESS16) + "进行";
		}

		return "";
	}
	
	private String getDocumentImageURL() {

		return FileUtil.getImageLocationFromInputStream(Resource.IMAGE_DOCUMENT32, Resource.getDefault().getImageInputStream(Resource.IMAGE_DOCUMENT32));
	}

}
