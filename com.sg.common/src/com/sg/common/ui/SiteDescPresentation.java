package com.sg.common.ui;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.db.DBActivator;
import com.sg.db.expression.query.QueryExpression;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.field.presentation.IValuePresentation;

public class SiteDescPresentation implements IValuePresentation {

	public SiteDescPresentation() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getPresentValue(String key, ISingleObject data, Object value, String format) {
		// TODO Auto-generated method stub
		QueryExpression exp = DBActivator.getQueryExpression(IDBConstants.EXP_QUERY_SITE);
		exp.setParamValue(IDBConstants.PARAM_INPUT_ID, value);
		DBCursor cur = exp.run();
		if(cur.hasNext()){
			DBObject site = cur.next();
			Object desc = site.get(IDBConstants.FIELD_DESC);
			if(desc!=null){
				return desc.toString();
			}
		}
		return "";
	}

}
