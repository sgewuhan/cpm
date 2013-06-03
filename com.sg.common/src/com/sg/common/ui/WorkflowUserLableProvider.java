package com.sg.common.ui;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.OrganizationService;


public class WorkflowUserLableProvider  extends ColumnLabelProvider {

	private OrganizationService service;

	public WorkflowUserLableProvider() {

		super();
		service = BusinessService.getOrganizationService();
	}

	@Override
	public String getText(Object element) {
		DBObject dbo = (DBObject) element;
		String actorId = (String) dbo.get(IDBConstants.FIELD_WFINFO_ACTORID);

		DBObject userData = service.getUserByUId(actorId);
		String userImageUrl = DataUtil.getUserImageURL(userData);

		return "<img src='"+userImageUrl+"' style='float:left;padding:4px' width='64' height='64' />";
		
	}
	
	


	
}
