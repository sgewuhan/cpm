package com.sg.document.editor.option;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.OrganizationService;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.field.option.IOptionProvider;
import com.sg.widget.resource.Enumerate;

public class DeptMemberSelector implements IOptionProvider {

	public DeptMemberSelector() {
	}

	@Override
	public Enumerate getOption(ISingleObjectEditorInput input,
			ISingleObject data, String key, Object value) {
		OrganizationService service = BusinessService.getOrganizationService();
		List<DBObject> userList = new ArrayList<DBObject>();

		//获得当前所在的部门
		BasicDBList his = (BasicDBList) data.getValue(IDBConstants.FIELD_PROCESSHISTORY);
		if(his!=null&&his.size()>0){
			DBObject preData = (DBObject) his.get(0);
			ObjectId teamId = (ObjectId) preData.get("dept");
			if(teamId!=null){
				BasicDBList users = service.getUsersInTeam(teamId, true);
				userList.addAll((Collection<? extends DBObject>) users);
			}
		}
		
		List<Enumerate> list = new ArrayList<Enumerate>();
		for (DBObject user : userList) {
			Enumerate e = new Enumerate((String) user
					.get(IDBConstants.FIELD_NAME),
					user.get(IDBConstants.FIELD_DESC)
					+ " "
					+ user.get(IDBConstants.FIELD_EMAIL), DataUtil
					.getRefData(user, IDBConstants.DATA_USER_BASIC),
					null);
			if(!list.contains(e)){
				list.add(e);
			}
		}
		Enumerate e = new Enumerate(key, data.toString(), data, list);
		return e;
	}

}
