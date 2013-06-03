package com.sg.cpm.myworks.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.bson.types.ObjectId;
import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.SingleObject;
import com.sg.resource.Resource;
import com.sg.user.UserSessionContext;
import com.sg.widget.util.FileUtil;
import com.sg.widget.util.Util;

public class MyLaunchedWorkLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {
			return getWorkHtml((SingleObject)element);
	}
	

	private String getWorkHtml(SingleObject work) {
		String imageUrl = getWorkImageURL(work);
		String desc = (String) work.getValue(IDBConstants.FIELD_DESC);
		
		String projectName = "";
		ObjectId projectId = (ObjectId) work.getValue(IDBConstants.FIELD_ROOTID);
		if(projectId!=null){
			DBObject projectData = DataUtil.getDataObject(DataUtil.COLLECTION_PROJECT, projectId);
			projectName = "项目: "+(String) projectData.get(IDBConstants.FIELD_DESC);
		}
		
		
		String planstart = "";
		String planfinish = "";

		Date _planStart = (Date) work.getValue(IDBConstants.FIELD_PROJECT_PLANSTART);
		Date _planFinish = (Date) work.getValue(IDBConstants.FIELD_PROJECT_PLANFINISH);
		SimpleDateFormat sdf = Util.getDateFormat(Util.SDF_M_D);
		try {
			planstart = sdf.format(_planStart);
			planfinish = sdf.format(_planFinish);
		} catch (Exception e) {
		}
		
		String planInfo = "";
		if(planstart.length()<1||planfinish.length()<1){
			planInfo = "未确定";
		}else{
			planInfo = planstart+" - "+planfinish;
		}


		String actualstart = "";
		String actualfinish = "";

		Date _actualStart = (Date) work.getValue(IDBConstants.FIELD_PROJECT_ACTUALSTART);
		Date _actualFinish = (Date) work.getValue(IDBConstants.FIELD_PROJECT_ACTUALFINISH);
		try {
			actualstart = sdf.format(_actualStart);
			actualfinish = sdf.format(_actualFinish);
		} catch (Exception e) {
		}
		
		String actualInfo = "";
		if(actualstart.length()<1||actualfinish.length()<1){
			actualInfo = "未确定";
		}else{
			actualInfo = planstart+" - "+planfinish;
		}
		

		DBObject user = (DBObject) work.getValue(IDBConstants.FIELD_WORK_PM);
		String charger = (String) user.get(IDBConstants.FIELD_NAME);
		
		String resources ="";
		BasicDBList resourceList = (BasicDBList)work.getValue(IDBConstants.FIELD_WORK_RESOURCE);
		if(resourceList==null||resourceList.size()<1){
			resources = "无";
		}else{
			Iterator<Object> iter = resourceList.iterator();
			int i=0;
			while(iter.hasNext()){
				resources = resources +  (String) ((DBObject) iter.next()).get(IDBConstants.FIELD_NAME);
				
				if(i<3){
					if(iter.hasNext()){
						resources = resources +",";
					}
				}else{
					resources = resources +"...";
					break;
				}
				i++;
			}
		}
		
		String createDate = "";

		Date _date = (Date) work.getValue(IDBConstants.FIELD_CREATE_DATE);
		try {
			createDate = sdf.format(_date);
		} catch (Exception e) {}
		
		
		
		StringBuilder builder = new StringBuilder();
		builder.append("<img src=\"");
		builder.append(imageUrl);
		builder.append("\" style='float:left;padding:2px 4px 2px 4px' width='40' height='60' />");

		builder.append(desc+projectName);
		

		builder.append("<small><br/>");
		builder.append("负责: <i>"+charger+"</i>");
		builder.append(" 参与: <i>"+resources+"</i>");
		builder.append("<br/>");

		builder.append("计划: <i>"+planInfo+"</i>");
		builder.append(" 实际: <i>"+actualInfo+"</i>");
		builder.append(" 创建:<i>"+createDate+"</i>");
		
		builder.append("<br/></small>");

		return builder.toString();

	}


//	private String getStarImageUrl(String key) {
//		String imageUrl = FileUtil.getImageLocationFromInputStream(key,
//				Resource.getDefault().getImageInputStream(key)); 
//		return "<img src='" + imageUrl
//		+ "' width='16' height='16' style='padding-right:4px;padding-top:2px;'/>";
//	}


	private String getWorkImageURL(SingleObject data){
		ObjectId uoid = UserSessionContext.getSession().getUserOId();		
		String resource = null;
		DBObject pmData = (DBObject)data.getValue(IDBConstants.FIELD_WORK_PM);
		boolean isPM = pmData!=null&&uoid.equals(pmData.get(IDBConstants.FIELD_SYSID));
		
		if (isPM) {
			resource = Resource.IMAGE_PMTASK60;
		} else {
			resource = Resource.IMAGE_TASK60;
		}
		
//		if(DataUtil.isReady(data.getData())){
//			if(isPM){
//				resource = Resource.IMAGE_READY32;
//			}else{
//				resource = Resource.IMAGE_T_READY32;
//			}
//		}else if(DataUtil.isCancel(data.getData())){
//			if(isPM){
//				resource = Resource.IMAGE_CANCEL32;
//			}else{
//				resource = Resource.IMAGE_T_CANCEL32;
//			}
//		}else if(DataUtil.isClose(data.getData())){
//			if(isPM){
//				resource = Resource.IMAGE_CLOSE32;
//			}else{
//				resource = Resource.IMAGE_T_CLOSE32;
//			}
//		}else if(DataUtil.isPause(data.getData())){
//			if(isPM){
//				resource = Resource.IMAGE_PAUSE32;
//			}else{
//				resource = Resource.IMAGE_T_PAUSE32;
//			}
//		}else if(DataUtil.isProcess(data.getData())){
//			if(isPM){
//				resource = Resource.IMAGE_PROCESS32;
//			}else{
//				resource = Resource.IMAGE_T_PROCESS32;
//			}
//		}else{
//			return "";
//		}
		return FileUtil.getImageLocationFromInputStream(resource,
				Resource.getDefault().getImageInputStream(resource));
	}
	

}
