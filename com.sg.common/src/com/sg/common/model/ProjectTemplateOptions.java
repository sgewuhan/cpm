package com.sg.common.model;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.db.DBActivator;
import com.sg.db.expression.query.QueryExpression;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.field.option.IOptionProvider;
import com.sg.widget.resource.Enumerate;

public class ProjectTemplateOptions implements IOptionProvider {

	public ProjectTemplateOptions() {

	}

	@Override
	public Enumerate getOption(ISingleObjectEditorInput input, ISingleObject data, String key, Object value) {

		List<Enumerate> children = new ArrayList<Enumerate>();
		Enumerate e = new Enumerate(key, data.toString(), data, children);
		// 查询项目模板
		QueryExpression exp = DBActivator.getQueryExpression(IDBConstants.EXP_QUERY_PROJECT_TEMPLATE);
		DBCursor dbc = exp.run();
		while (dbc.hasNext()) {
			DBObject dbo = dbc.next();
			if (Boolean.TRUE.equals(dbo.get(IDBConstants.FIELD_ACTIVATE)))
				children.add(new Enumerate(dbo.get(IDBConstants.FIELD_SYSID).toString(), (String) dbo.get(IDBConstants.FIELD_DESC), dbo, null));
		}
		return e;
	}

}
