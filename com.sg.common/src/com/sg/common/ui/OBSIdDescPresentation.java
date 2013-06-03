package com.sg.common.ui;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.service.OrganizationService;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.field.presentation.IValuePresentation;

public class OBSIdDescPresentation implements IValuePresentation {

	private OrganizationService service;

	public OBSIdDescPresentation() {
		
		service = BusinessService.getOrganizationService();
	}

	@Override
	public String getPresentValue(String key, ISingleObject data, Object value, String format) {
		if(value instanceof ObjectId){
			DBObject obsItem = service.getOBSItemData((ObjectId) value);
			return (String) obsItem.get("desc");
		}
		return "";
	}

}
