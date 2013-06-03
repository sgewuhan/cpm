package com.sg.document.editor.option;

import java.util.ArrayList;
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

public class NamedRoleUnderRootOrg implements IOptionProvider {

	private OrganizationService orgService;

	public NamedRoleUnderRootOrg() {
		orgService = BusinessService.getOrganizationService();
	}

	@Override
	public Enumerate getOption(ISingleObjectEditorInput input,
			ISingleObject data, String key, Object value) {
		List<Enumerate> list = new ArrayList<Enumerate>();
		//获得当前站点下的所有组织
		Enumerate e = new Enumerate(key, data.toString(), data, list);

		
		// 先获得技术中心
		DBObject dept = orgService.getTeamByName("技术中心");
		ObjectId deptId = (ObjectId) dept.get(IDBConstants.FIELD_SYSID);

		DBObject role = orgService.getRoleInTeamByName(deptId, "技术中心副主任");
		BasicDBList users = orgService.getUsersInRole((ObjectId) role
				.get(IDBConstants.FIELD_SYSID));
		for(int i=0;i<users.size();i++){
			DBObject user = (DBObject) users.get(i);
			list.add(new Enumerate((String) user
					.get(IDBConstants.FIELD_UID),
					(String) user.get(IDBConstants.FIELD_NAME), DataUtil.getRefData(user, IDBConstants.DATA_USER_BASIC),null));
		}
		
		return e;
	}

}
