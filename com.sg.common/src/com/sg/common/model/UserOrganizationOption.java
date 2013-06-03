package com.sg.common.model;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.db.DBActivator;
import com.sg.db.model.ISingleObject;
import com.sg.user.AuthorityResponse;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.field.option.IOptionProvider;
import com.sg.widget.resource.Enumerate;

public class UserOrganizationOption implements IOptionProvider {

	public UserOrganizationOption() {
	}

	@Override
	public Enumerate getOption(ISingleObjectEditorInput input,ISingleObject data, String key, Object value) {
		List<Enumerate> list = new ArrayList<Enumerate>();
		Enumerate e = new Enumerate(key, data.toString(), data, list);
		

		AuthorityResponse auth = input.getAuthorityResponse();
		if(auth!=null){
			BasicDBList teamList = auth.getContextList();
			DBCollection obsCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_ORG);
			DBCursor cur = obsCollection.find(new BasicDBObject()
			.append(IDBConstants.FIELD_SYSID, 
					new BasicDBObject().append("$in",teamList)),
					new BasicDBObject()
			.append(IDBConstants.FIELD_SYSID, 1)
			.append(IDBConstants.FIELD_DESC, 1));
			
			
			while(cur.hasNext()){
				DBObject obsItem = cur.next();
				Object id = obsItem.get(IDBConstants.FIELD_SYSID);
				
				list.add(new Enumerate(id.toString(), obsItem.get(IDBConstants.FIELD_DESC).toString(), id, null));
			}
		}

		
		//**************************************************************************************************
		//权限控制可选项 放松权限的深度 不考虑该用户是否在组织中
		//**************************************************************************************************
//		AuthorityResponse auth = input.getAuthorityResponse();
//
//		List<Enumerate> list = new ArrayList<Enumerate>();
//		Enumerate e = new Enumerate(key, data.toString(), data, list);
//		BasicDBList teamList = UserSessionContext.getSession().getUserTeam();
//		DBCollection obsCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_ORG);
//		DBCursor cur = obsCollection.find(new BasicDBObject()
//								.append(IDBConstants.FIELD_SYSID, 
//										new BasicDBObject().append("$in",teamList)),
//							new BasicDBObject()
//								.append(IDBConstants.FIELD_SYSID, 1)
//								.append(IDBConstants.FIELD_DESC, 1)
//								);
//		while(cur.hasNext()){
//			DBObject obsItem = cur.next();
//			Object id = obsItem.get(IDBConstants.FIELD_SYSID);
//			
//			if(auth!=null){
//				//处理权限
//				BasicDBList contextIdList = auth.getContextList();
//				if(!contextIdList.contains(id)){
//					continue;//跳过没有得到授权的上下文选项
//				}
//			}
//			list.add(new Enumerate(id.toString(), obsItem.get(IDBConstants.FIELD_DESC).toString(), id, null));
//		}

		//**************************************************************************************************
		//权限控制可选项 放松权限的深度 不考虑该用户是否在组织中
		//**************************************************************************************************
		
		return e;
	}



}
