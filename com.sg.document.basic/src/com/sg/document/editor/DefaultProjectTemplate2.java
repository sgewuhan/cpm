package com.sg.document.editor;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.db.DBActivator;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.field.defaultvalue.IDefaultValueProvider;

public class DefaultProjectTemplate2 implements IDefaultValueProvider {

	private static final String PJ1_ID = "com.tmt.ProjectApply";//产品开发类项目
	private static final String PJ1_NAME = "产品开发类项目";
	
	private static final String PJ2_ID = "com.tmt.ProjectApply1";//技术支持类项目
	private static final String PJ2_NAME = "技术支持类项目";
	
	private static final String PJ3_ID = "com.tmt.ProjectApply2";//能力建设类项目
	private static final String PJ3_NAME = "能力建设类项目";
	
	private static final String PJ4_ID = "com.tmt.ProjectApply3";//基础研究类项目
	private static final String PJ4_NAME = "基础研究类项目";

	
	public DefaultProjectTemplate2() {
	}

	@Override
	public Object getDefaultValue(ISingleObject data, String key) {
		DBObject userData = BusinessService.getOrganizationService().getCurrentUserData();
		ObjectId siteId = (ObjectId) userData.get(IDBConstants.FIELD_SITEPARENT);
		String name = null;
		Object pdid = data.getData().get(IDBConstants.FIELD_PROCESS_DEFINITION_ID);
		if(PJ1_ID.equals(pdid)){
			name = PJ1_NAME;
		}else if(PJ2_ID.equals(pdid)){
			name = PJ2_NAME;
		}else if(PJ3_ID.equals(pdid)){
			name = PJ3_NAME;
		}else if(PJ4_ID.equals(pdid)){
			name = PJ4_NAME;
		}else{
			return null;
		}
		
		DBCollection pt = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_PROJECT_TEMPLATE);
		DBObject templateData = pt.findOne(new BasicDBObject()
			.append(IDBConstants.FIELD_ACTIVATE, Boolean.TRUE)
			.append(IDBConstants.FIELD_DESC, name)
			.append(IDBConstants.FIELD_SITEPARENT, siteId)
			);
		return templateData;
	}

}
