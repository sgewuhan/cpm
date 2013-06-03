package com.sg.user.db;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.Assert;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.db.DBActivator;

public class UserDB {
	
	public static final String COLLECTION_AUTHORITY = "authority";
	
	
	public static final String FIELD_CONTEXTID="contextId";
	
	public static final String FIELD_TOKENID="tokenId";
	
	public static final String FIELD_TARGETTYPE="targetType";
	
	public static final String FIELD_TARGETID="targetId";
	
	public static final String FIELD_AUTHVALUE="authValue";

	public static final String VALUE_USER = "user";

	public static final String VALUE_ROLE = "role";

	public static final String VALUE_TEAM = "team";

	public static final String COLLECTION_OBS = "obs";
	
	public static final String FIELD_USEROID = "useroid";
	
	public static final String FIELD_OBS_TYPE = "templateType";
	
	public static final String FIELD_OBS_PARENT = "obsparent";
	
	public static final String FIELD_PROJECT_ROOTOBS = "rootid";


	public static final String FIELD__ID = "_id";


	
	



	@Deprecated
	/**
	 * 这个方法不能取出只设置在组织下的角色中的用户，因此不一定能够完全获得用户所有的组
	 * @param userId
	 * @return
	 */
	public static List<ObjectId> getParentTeamIdList(ObjectId userId) {
		return getParentOBSIdList(userId,VALUE_TEAM);
	}
	
	
	public static List<ObjectId> getParentRoleIdList(ObjectId userId) {
		return getParentOBSIdList(userId,VALUE_ROLE);
	}

	public static void getParentRoleAndTeam(ObjectId userId, BasicDBList roleIdList,BasicDBList teamIdList){
		//检查准备回写的数组是否初始化
		Assert.isNotNull(roleIdList);
		Assert.isNotNull(teamIdList);
		
		//obs的集合
		DBCollection obsCol = DBActivator.getDefaultDBCollection(COLLECTION_OBS);
		
		
		//查询该用户的OBSPARENT
		DBObject ref = new BasicDBObject()
							.append(FIELD_USEROID, userId)
							.append(FIELD_PROJECT_ROOTOBS, 
									new BasicDBObject().append("$exists", false));//这种情况是属于项目上下文组织中的用户，跳过
							
		DBObject keys = new BasicDBObject()
							.append(FIELD_OBS_PARENT, 1);
		
		DBCursor cur = obsCol.find(ref, keys);//查询所有的在OBS集合中的USER节点
		
		
		
		//将这些节点的ID取出备用
		BasicDBList parentIdList = new BasicDBList();
		
		while(cur.hasNext()){
			Object parentId = cur.next().get(FIELD_OBS_PARENT);
			if(!parentIdList.contains(parentId)){
				parentIdList.add(parentId);
			}
		}
		
		
		
		//查询出这些obs上的节点
		cur = obsCol.find(new BasicDBObject().append(FIELD__ID, new BasicDBObject().append("$in", parentIdList)));
		while(cur.hasNext()){
			DBObject obsItem = cur.next();
			Object id = obsItem.get(FIELD__ID);
			if(VALUE_ROLE.equals(obsItem.get(FIELD_OBS_TYPE))){
				
				//如果是角色，需要判断这个角色是否已经加入到了角色列表
				if(roleIdList.contains(id)){
					continue;
				}else{
					
					//将角色加入到角色列表
					roleIdList.add(id);
				
					//进一步查询他的obsprent以获得组织
					//角色的上级一定是组织
					Object parentTeamId = obsItem.get(FIELD_OBS_PARENT);
					if(!teamIdList.contains(parentTeamId)){
						teamIdList.add(parentTeamId);
					}
				}

			}else if(VALUE_TEAM.equals(obsItem.get(FIELD_OBS_TYPE))){

				if(!teamIdList.contains(id)){
					teamIdList.add(id);
				}
				
			}
		}
		
		
	}
	
	private static List<ObjectId> getParentOBSIdList(ObjectId userId,String obstype) {
		DBCollection obsCol = DBActivator.getDefaultDBCollection(COLLECTION_OBS);
		
		
		
		//查询该用户的OBSPARENT
		DBObject ref = new BasicDBObject()
							.append(FIELD_USEROID, userId);
							
		DBObject keys = new BasicDBObject()
							.append(FIELD_OBS_PARENT, 1)
							.append(FIELD_PROJECT_ROOTOBS, 1);
		
		List<ObjectId> result = new ArrayList<ObjectId>();
		DBCursor cur = obsCol.find(ref, keys);
		while(cur.hasNext()){
			DBObject obsItem = cur.next();
			if(obsItem.get(FIELD_PROJECT_ROOTOBS)!=null){
				//这种情况是属于项目上下文组织中的用户，跳过
				continue;
			}
			
			DBObject one = obsCol.findOne(new BasicDBObject()
								.append(FIELD_OBS_TYPE, obstype)
								.append(FIELD__ID, obsItem.get(FIELD_OBS_PARENT)),
							new BasicDBObject()
								.append(FIELD__ID, 1));
			if(one!=null && !result.contains(one)){
				result.add((ObjectId) one.get(FIELD__ID));
			}
		}
		
		
		return result;
	}


	public static DBObject getAuthorityData(ObjectId contextId, ObjectId targetId, String tokenId, String targetType, 
			boolean authValue) {
		DBCollection col = DBActivator.getDefaultDBCollection(COLLECTION_AUTHORITY);
		DBObject one = col.findOne(
				new BasicDBObject()
					.append(FIELD_AUTHVALUE, authValue)
					.append(FIELD_TARGETID, targetId)
					.append(FIELD_TOKENID, tokenId)
					.append(FIELD_TARGETTYPE, targetType)
					.append(FIELD_CONTEXTID, contextId));
		return one;
	}
	

	/**
	 * 得到传入的组织上下文中具有权限的权限对象 
	 * @param teamList 组织上下文
	 * @param targetList 用户或者角色的列表，只能是ID list
	 * @param tokenId 权限ID
	 * @param targetType 授权类型user还是role
	 * @param authValue 授权值，授权为true,否则为false
	 * @return 通过权限的组织列表
	 */
	@Deprecated
	public static List<DBObject> getTeamContextAuthorityData(BasicDBList teamList, BasicDBList targetList, String tokenId, String targetType,
			boolean authValue) {
		DBCollection col = DBActivator.getDefaultDBCollection(COLLECTION_AUTHORITY);

		List<DBObject> result = new ArrayList<DBObject>();
		
		
		DBCursor cur = col.find(new BasicDBObject()
				.append(FIELD_AUTHVALUE, authValue)
				.append(FIELD_TOKENID, tokenId)
				.append(FIELD_TARGETTYPE, targetType)
				.append(FIELD_TARGETID, new BasicDBObject().append("$in", targetList)));
		
		while(cur.hasNext()){
			DBObject one = cur.next();
			ObjectId contextId = (ObjectId)one.get(FIELD_CONTEXTID);
			if(teamList.contains(contextId)){
				result.add(one);
			}
		}
		
		return result;
	}
	
	/**
	 * 得到用户所具有权限的权限对象 
	 * @param targetList 用户或者角色的列表，只能是ID list
	 * @param tokenId 权限ID
	 * @param targetType 授权类型user还是role
	 * @param authValue 授权值，授权为true,否则为false
	 * @return 通过权限的组织列表
	 */
	public static List<DBObject> getAuthorityData(BasicDBList targetList, String tokenId, String targetType,
			boolean authValue) {
		DBCollection col = DBActivator.getDefaultDBCollection(COLLECTION_AUTHORITY);

		List<DBObject> result = new ArrayList<DBObject>();
		
		
		DBCursor cur = col.find(new BasicDBObject()
				.append(FIELD_AUTHVALUE, authValue)
				.append(FIELD_TOKENID, tokenId)
				.append(FIELD_TARGETTYPE, targetType)
				.append(FIELD_TARGETID, new BasicDBObject().append("$in", targetList)));
		
		while(cur.hasNext()){
			DBObject one = cur.next();
			result.add(one);
		}
		
		return result;
	}


}
