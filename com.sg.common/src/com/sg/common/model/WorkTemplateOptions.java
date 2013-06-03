package com.sg.common.model;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.field.option.IOptionProvider;
import com.sg.widget.resource.Enumerate;

public class WorkTemplateOptions implements IOptionProvider {

	public WorkTemplateOptions() {

	}

	@Override
	public Enumerate getOption(ISingleObjectEditorInput input, ISingleObject data, String key, Object value) {

		List<Enumerate> list = new ArrayList<Enumerate>();
		Enumerate e = new Enumerate(key, data.toString(), data, list);

		// 选择当前用户所在组织 以及下级组织的 工作模板
		BasicDBList result = DataUtil.getAvilebleWorkTemplate();

		if (result != null) {
			for (int i = 0; i < result.size(); i++) {
				DBObject item = (DBObject) result.get(i);
				String path = getPath(item);
				BasicDBList templateList = (BasicDBList) item.get(IDBConstants.DUMMY_FIELD_TEAMPLATE);
				for (int j = 0; j < templateList.size(); j++) {
					DBObject template = (DBObject) templateList.get(j);

					// 如果是没有开启的模板，不能添加到列表中
					if (Boolean.TRUE.equals(template.get(IDBConstants.FIELD_ACTIVATE))) {

						list.add(new Enumerate(path, (String) template.get(IDBConstants.FIELD_DESC), template
								.get(IDBConstants.FIELD_SYSID), null));
					}
				}
			}
		}

		return e;
	}

	private String getPath(DBObject item) {

		DBObject parent = (DBObject) item.get(IDBConstants.DUMMY_FIELD_PARENT);
		String path = (String) item.get(IDBConstants.FIELD_DESC);
//		if (path.length() > 6) {
//			path = path.substring(0, 5) + "...";
//		}
		if (parent != null) {
			path = getPath(parent) + "/" + path;
		}
		return path;
	}

}
