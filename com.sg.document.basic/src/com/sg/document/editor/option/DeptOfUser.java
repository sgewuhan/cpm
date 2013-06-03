package com.sg.document.editor.option;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.OrganizationService;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.field.option.IOptionProvider;
import com.sg.widget.resource.Enumerate;

public class DeptOfUser implements IOptionProvider {

	private OrganizationService userService;

	public DeptOfUser() {
		userService = BusinessService.getOrganizationService();
	}

	@Override
	public Enumerate getOption(ISingleObjectEditorInput input,
			ISingleObject data, String key, Object value) {
		
		List<Enumerate> list = new ArrayList<Enumerate>();

		DBObject pm = (DBObject) data.getValue("pm");
		if(pm instanceof DBObject){
			BasicDBList result = userService.getTeamOfUser((ObjectId) pm.get(IDBConstants.FIELD_SYSID), OrganizationService.TEAM_NOPROJECT);

			if(result!=null){
				for(int i=0;i<result.size();i++){
					DBObject team = (DBObject) result.get(i);
					Enumerate e = new Enumerate(team.get(IDBConstants.FIELD_SYSID).toString(), (String)team.get(IDBConstants.FIELD_DESC),  team.get(IDBConstants.FIELD_DESC), null);
					if(!list.contains(e)){
						list.add(e);
					}
				}
			}
		}
		Enumerate e = new Enumerate(key, data.toString(), pm, list);
		return e;
	}

}
