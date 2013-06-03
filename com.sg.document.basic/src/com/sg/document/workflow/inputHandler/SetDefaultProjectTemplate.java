package com.sg.document.workflow.inputHandler;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.ITaskFormInputHandler;
import com.sg.common.workflow.TaskFormConfig;
import com.sg.db.DBActivator;
import com.sg.db.expression.query.QueryExpression;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.SingleObject;

public class SetDefaultProjectTemplate implements ITaskFormInputHandler {

	public SetDefaultProjectTemplate() {
	}

	@Override
	public ISingleObject getTaskFormInputData(DBObject taskFormData,
			TaskFormConfig taskFormConfig) {
		Object pdid = taskFormData.get(IDBConstants.FIELD_PROCESS_DEFINITION_ID);
		String templateName = null;
		if("com.tmt.ProjectApply2".equals(pdid)){
			templateName = "技术中心-技术支持类项目";
		}
		
		if(templateName!=null){
			QueryExpression exp = DBActivator.getQueryExpression(IDBConstants.EXP_QUERY_PROJECT_TEMPLATE);
			DBCursor dbc = exp.run();
			while(dbc.hasNext()){
				DBObject templateData = dbc.next();
				if(templateName.equals(templateData.get(IDBConstants.FIELD_DESC))){
					taskFormData.put("template", templateData);
					break;
				}
			}
		}
		
		return new SingleObject().setData(taskFormData);
	}

}
