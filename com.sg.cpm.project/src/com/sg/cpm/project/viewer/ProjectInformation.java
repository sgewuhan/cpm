package com.sg.cpm.project.viewer;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.types.ObjectId;
import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.resource.Resource;
import com.sg.widget.util.FileUtil;
import com.sg.widget.util.Util;

public class ProjectInformation extends ColumnLabelProvider {

	private SimpleDateFormat sdf;

	@Override
	public String getText(Object element) {
		ISingleObject row = (ISingleObject) element;
		String id = row.getText(IDBConstants.FIELD_ID);

		String desc = row.getText(IDBConstants.FIELD_DESC);

		String dept = "";
		Object value = row.getValue(IDBConstants.FIELD_OBSPARENT);
		if (value != null) {
			DBObject orgdata = DataUtil.simpleQuery(IDBConstants.EXP_QUERY_OBS, IDBConstants.PARAM_INPUT_ID, value);
			if (orgdata != null) {
				dept = (String) orgdata.get(IDBConstants.FIELD_DESC);
			}
		}

		Object pmValue = row.getValue(IDBConstants.FIELD_PROJECT_PM);
		String pm = "";
		if (pmValue != null && (pmValue instanceof DBObject)) {
			pm = "" + ((DBObject) pmValue).get(IDBConstants.FIELD_NAME) + "/" + ((DBObject) pmValue).get(IDBConstants.FIELD_DESC);
		}

		String planstart = "";
		String planfinish = "";

		Date _planStart = (Date) row.getValue(IDBConstants.FIELD_PROJECT_PLANSTART);
		Date _planFinish = (Date) row.getValue(IDBConstants.FIELD_PROJECT_PLANFINISH);
		sdf = Util.getDateFormat(Util.SDF_YY_MM_DD);
		try {
			planstart = sdf.format(_planStart);
			planfinish = sdf.format(_planFinish);
		} catch (Exception e) {

		}

		String imageUrl = "<img src='"+getImageURL(row)+"' style='float:left;padding:2px' width='72' height='72' />";

		String status = DataUtil.getProcessStatus(row);
		String statusImageURL = "<img src='" + DataUtil.getProcessStatusImageURL(row)
				+ "' width='16' height='16' style='padding-right:4px;padding-top:2px;'/>";
		StringBuilder builder = new StringBuilder();

		builder.append(imageUrl);
		
		builder.append("<b>"+id+"  </b>");
		builder.append(statusImageURL);
		builder.append("<small><i>"+status+"</i></small>");
		builder.append("<br/>");

		builder.append("<b>"+desc+"</b>");
		builder.append("<br/>");

		builder.append("<small><i>"+pm+" "+dept+"</i>");
		builder.append("<br/>");

		builder.append("<i>  "+planstart+" - "+planfinish+"</i></small>");
		builder.append("<br/>");

		return builder.toString();
	}

	private String getImageURL(ISingleObject row) {
		BasicDBList thumbList = (BasicDBList) row.getValue(IDBConstants.FIELD_THUMB);

		String imgLocation = null;
		if (thumbList != null && !thumbList.isEmpty()) {
			DBObject dbo = (DBObject) thumbList.iterator().next();

			String namespace = (String) dbo.get("namespace");
			ObjectId fileObjectid = (ObjectId) dbo.get("_id");

			imgLocation = FileUtil.getImageLocationFromDatabase(namespace, fileObjectid);
		}

		if (imgLocation == null) {
			imgLocation = FileUtil
					.getImageLocationFromInputStream(Resource.IMAGE_PROJECTFOLDER72, Resource.getDefault().getImageInputStream(Resource.IMAGE_PROJECTFOLDER72));
		}
		if (imgLocation != null)
			return FileUtil.getImageUrl(imgLocation);
		return null;
	}
	

}
