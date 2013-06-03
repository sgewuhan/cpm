package com.sg.common.ui;

import java.util.Date;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.widget.util.Util;

public class WorkflowHistoryLableProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {

		DBObject dbo = (DBObject) element;

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

//	/**
//	 * 每个字12个像素计算风格，每隔45个汉字加一个<br/>
//	 * 
//	 * @param comment
//	 * @return
//	 */
//	private String getWrappedComment(String comment) {
//		if(comment==null||comment.length()<45){
//			return comment;
//		}
//		StringBuffer s = new StringBuffer(comment);
//		for (int index = 0; index < s.length(); index++) {
//			if (index % 50 == 0) {
//				s.insert(index, "<br/>");
//			}
//		}
//		return s.length()>5?s.substring(5):s.toString();
//	}
	
}
