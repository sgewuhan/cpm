package com.sg.document.editor.option;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.OrganizationService;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.field.option.IOptionProvider;
import com.sg.widget.resource.Enumerate;

public class DeptSelector implements IOptionProvider {

	private OrganizationService os;


	public DeptSelector() {
	}

	@Override
	public Enumerate getOption(ISingleObjectEditorInput input,
			ISingleObject data, String key, Object value) {
		List<Enumerate> list = new ArrayList<Enumerate>();
		//获得当前站点下的所有组织
		Enumerate e = new Enumerate(key, data.toString(), data, list);

		os = BusinessService.getOrganizationService();
		DBObject userData = os.getCurrentUserData();
		list.addAll(getSubTeam((ObjectId)userData.get(IDBConstants.FIELD_SITEPARENT),"",true));
		return e;
	}

	
	private List<Enumerate> getSubTeam(ObjectId parentId, String parentdesc,boolean cascade) {
		List<DBObject> children = os.getSubTeam(parentId);

		List<Enumerate> result = new ArrayList<Enumerate>();
		Iterator<DBObject> iter = children.iterator();
		while(iter.hasNext()){
			DBObject obsItem = iter.next();
			ObjectId id = (ObjectId) obsItem.get(IDBConstants.FIELD_SYSID);
			String desc = (String) obsItem.get(IDBConstants.FIELD_DESC);
			
			if(parentdesc.length()>0){
				desc = "/"+desc;
			}
			
			result.add(new Enumerate(parentdesc, desc, parentdesc+desc, null));
			if(cascade){
				result.addAll(getSubTeam(id,parentdesc+desc,true));
			}
		}
		return result;
	}
}
