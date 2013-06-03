package com.sg.common.service;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.db.Util;
import com.sg.user.UserSessionContext;

public class OrganizationService extends CommonService {

	public static int TEAM_ALL = 0;

	public static int TEAM_PROJECT = 1;

	public static int TEAM_NOPROJECT = 2;
	
	

	/**
	 * <p>
	 * <strong>Remove user information api</strong>
	 * </p>
	 * Remove user information will do: <li>remove the row in the user
	 * collection, with _id = systemId</li> <li>remove the row in the obs
	 * collection, with useroid = systemId</li>
	 * <p>
	 * <strong>User information maybe exsit in user collection and obs
	 * collection.</strong>
	 * </P>
	 * <p>
	 * For some reason, the user add to some organizations, for example, add
	 * user to a team, role or add user to a project team or role, a duplicated
	 * row will be add into obs collection.
	 * </p>
	 * <br/>
	 * <p>
	 * This kind row in the <strong>user</strong> collection in json will like
	 * this:
	 * </p>
	 * <p>
	 * <code>{"_id" : ObjectId("4fb5eeede7e3d859cd54e0a8"),"uid" : "19950014",
	 * "desc" : "ljx","name" : "SOMEONE", "password" : "1","password_repeat" : "1",
	 * "email" : "james.liu@teg.cn","siteparent" : ObjectId("4fb5e513e7e35292f1713ab0"),
	 * "activate" : true, "creator" : "000000",  "creator_desc" : "admin",  
	 * "owner" : "000000",  "owner_desc" : "admin",  "createdate" : ISODate("2012-05-18T06:40:45.341Z")}
	 * </code>
	 * </p>
	 * <br/>
	 * <p>
	 * And in the <strong>obs</strong> collection in json will like this:
	 * </p>
	 * <p>
	 * <code>{ "_id" :ObjectId("4fb61f89e7e3cc0002ee06d8"), "templateType" : "user",
	 * "obsparent" : ObjectId("4fb61d97e7e3d92aad8b8726"), "useroid" :
	 * ObjectId("4fb5eeffe7e3d859cd54e0a9"), "creator" : "20102069",
	 * "creator_desc" : "gpp", "owner" : "20102069", "owner_desc" : "gpp",
	 * "createdate" : ISODate("2012-05-18T10:08:09.472Z") }
	 * </code>
	 * <p>
	 * Above row show you a user add to a obs node with the _id =
	 * ObjectId("4fb61d97e7e3d92aad8b8726") which stored in the field
	 * "obsparent"
	 * 
	 * @see org.bson.types.ObjectId
	 * 
	 * @since 1.0
	 * @author hua
	 * 
	 * @param systemId
	 *            user _id value, it should be ObjectId
	 */
	public void removeUser(ObjectId systemId) {

		userCollection.remove(new BasicDBObject().append(FIELD_SYSID, systemId));
		orgCollection.remove(new BasicDBObject().append(FIELD_USEROID, systemId));
	}

	/**
	 * <p>
	 * <strong>Remove user information api</strong>
	 * </p>
	 * 
	 * userData will be a MongoDB DBObject with "_id" field not
	 * <code>null</code>.
	 * 
	 * @see com.mongodb.DBObject
	 * 
	 * @since 1.0
	 * @author hua
	 * @param userData
	 */
	public void removeUser(DBObject userData) {

		Object systemId = userData.get(FIELD_SYSID);
		removeUser((ObjectId) systemId);
	}
	
	/**
	 * 
	 * @param teamId
	 */
	public BasicDBList getRolesInTeam(ObjectId teamId) {
		BasicDBList list = new BasicDBList();
		
		DBCursor cur = orgCollection.find(new BasicDBObject().append(FIELD_OBSPARENT, teamId).append(FIELD_TEMPLATE_TYPE, VALUE_OBS_ROLETYPE));
		
		while(cur.hasNext()){
			list.add(cur.next());
		}
		
		return list;
	}
	

	public DBObject getRoleInTeamByName(ObjectId teamId, String roleName) {
		return orgCollection.findOne(new BasicDBObject()
				.append(FIELD_OBSPARENT, teamId)
				.append(FIELD_TEMPLATE_TYPE, VALUE_OBS_ROLETYPE)
				.append(FIELD_DESC, roleName));
	}


	/**
	 * <p>
	 * <strong>find user's team api</strong>
	 * </p>
	 * 
	 * @since 1.0
	 * @author hua
	 * @param userId
	 *            user's key id, which can find user data in user collection
	 * @param resultType
	 *            int TEAM_PROJECT, TEAM_NOPROJECT,TEAM_ALL
	 * @return team data list in obs collection result will not
	 *         <code>null</code>
	 */
	public BasicDBList getTeamOfUser(ObjectId userId, int resultType) {

		BasicDBList result = new BasicDBList();

		BasicDBList obsItemList = getOBSItemOfUser(userId, resultType);

		for (int i = 0; i < obsItemList.size(); i++) {
			DBObject obsItem = (DBObject) obsItemList.get(i);
			//get obs parent
			ObjectId obsParentId = (ObjectId) obsItem.get(FIELD_OBSPARENT);
			DBObject parentItem = getOBSItemData(obsParentId);

			if (isRoleObject(parentItem)) {// is a role object, need find it's
										// parent;
				obsParentId = (ObjectId) parentItem.get(FIELD_OBSPARENT);

				DBObject parentTeam = getDBObject(orgCollection, obsParentId);

				result.add(parentTeam);

			} else if (isTeamObject(parentItem)) {
				result.add(parentItem);
			}

		}

		return result;
	}

	/**
	 * <p>
	 * <strong>get user's data in obs</strong>
	 * </p>
	 * 
	 * @since 1.0
	 * @author hua
	 * 
	 * 
	 * @param userId
	 * @param resultType
	 * @return
	 */
	private BasicDBList getOBSItemOfUser(ObjectId userId, int resultType) {

		DBObject query = new BasicDBObject();
		query.put(FIELD_USEROID, userId);
		if (resultType == TEAM_PROJECT) {// if in project team or role, rootid
											// will not null
			query.put(FIELD_ROOTID, new BasicDBObject().append(NOT, null));
		} else if (resultType == TEAM_NOPROJECT) {
			query.put(FIELD_ROOTID, null);
		}

		DBCursor cursor = orgCollection.find(query);

		BasicDBList result = new BasicDBList();

		while (cursor.hasNext()) {
			result.add(cursor.next());
		}

		return result;
	}


	/**
	 * <p>
	 * Get user list of a role
	 * </p>
	 * 
	 * @since 1.0
	 * @author hua
	 * 
	 * @param roleId
	 * @return user dataList, will not <code>null</code>
	 */
	public BasicDBList getUsersInRole(ObjectId roleId) {

		// 角色对象
		// 获得角色对象下的用户
		BasicDBList userList = new BasicDBList();

		BasicDBList userIdList = getUserIdsInRole(roleId);
		DBCursor cur = userCollection.find(new BasicDBObject().append(FIELD_SYSID, new BasicDBObject().append(IN, userIdList)));

		while (cur.hasNext()) {
			DBObject obsUser = cur.next();
			userList.add(obsUser);
		}

		return userList;
	}

	/**
	 * <p>
	 * Get userId list of a role
	 * </p>
	 * 
	 * @since 1.0
	 * @author hua
	 * 
	 * @param roleId
	 * @return user dataList, will not <code>null</code>
	 */
	public BasicDBList getUserIdsInRole(ObjectId roleId) {

		BasicDBList userIdList = new BasicDBList();
		BasicDBObject query = new BasicDBObject().append(FIELD_OBSPARENT, roleId).append(FIELD_TEMPLATE_TYPE, VALUE_OBS_USERTYPE);

		DBObject fields = new BasicDBObject().append(FIELD_USEROID, 1);

		DBCursor cur = orgCollection.find(query, fields);

		while (cur.hasNext()) {
			DBObject obsUser = cur.next();
			ObjectId userId = (ObjectId) obsUser.get(FIELD_USEROID);
			userIdList.add(userId);
		}

		return userIdList;
	}

	/**
	 * <p>
	 * Get user list of a role
	 * </p>
	 * 
	 * @since 1.0
	 * @author hua
	 * 
	 * @param teamId
	 * @param downTraverse
	 *            , pass true to search down the team,and role otherwise not
	 * @return user dataList, will not <code>null</code>
	 */
	public BasicDBList getUsersInTeam(ObjectId teamId, boolean downTraverse) {

		BasicDBList userList = new BasicDBList();

		BasicDBList userIdList = getUserIdsInTeam(teamId, downTraverse);
		DBCursor cur = userCollection.find(new BasicDBObject().append(FIELD_SYSID, new BasicDBObject().append(IN, userIdList)));
		System.out.println();
		while (cur.hasNext()) {
			DBObject obsUser = cur.next();
			userList.add(obsUser);
		}

		return userList;
	}

	/**
	 * <p>
	 * Get userid list of a team
	 * </p>
	 * 
	 * @since 1.0
	 * @author hua
	 * 
	 * @param teamId
	 * @param downTraverse
	 *            , pass true to search down the team,and role otherwise not
	 * @return user dataList, will not <code>null</code>
	 */
	public BasicDBList getUserIdsInTeam(ObjectId teamId, boolean downTraverse) {

		// 角色对象
		// 获得角色对象下的用户
		BasicDBList userIdList = new BasicDBList();

		BasicDBObject query = new BasicDBObject().append(FIELD_OBSPARENT, teamId);

		DBObject fields = new BasicDBObject().append(FIELD_SYSID, 1).append(FIELD_TEMPLATE_TYPE, 1).append(FIELD_USEROID, 1);

		DBCursor cur = orgCollection.find(query, fields);

		while (cur.hasNext()) {
			DBObject obsItem = cur.next();
			ObjectId obsId = (ObjectId) obsItem.get(FIELD_SYSID);
			if (VALUE_OBS_USERTYPE.equals(obsItem.get(FIELD_TEMPLATE_TYPE))) {// is
																				// a
																				// user
																				// object
				ObjectId userId = (ObjectId) obsItem.get(FIELD_USEROID);
				userIdList.add(getUserObject(userId));
			}

			if (downTraverse) {
				if (VALUE_OBS_ROLETYPE.equals(obsItem.get(FIELD_TEMPLATE_TYPE))) {
					userIdList.addAll(getUserIdsInRole(obsId));
				} else if (VALUE_OBS_TEAMTYPE.equals(obsItem.get(FIELD_TEMPLATE_TYPE))) {
					userIdList.addAll(getUserIdsInTeam(obsId, true));
				}
			}
		}

		return userIdList;
	}

	/**
	 * <p>
	 * Get user data list under a obs item
	 * </p>
	 * 
	 * if the obsitem is a user,
	 * 
	 * @since 1.0
	 * @author hua
	 * 
	 * 
	 * @param obsItemId
	 * @return
	 */
	public BasicDBList getUsersUnderOBSItem(ObjectId obsItemId) {

		BasicDBList userList = null;
		DBObject obsData = getDBObject(orgCollection, obsItemId);
		if (VALUE_OBS_USERTYPE.equals(obsData.get(FIELD_TEMPLATE_TYPE))) {
			ObjectId userOid = (ObjectId) obsData.get(FIELD_USEROID);
			userList = new BasicDBList();
			userList.add(getUserObject(userOid));
		} else if (VALUE_OBS_ROLETYPE.equals(obsData.get(FIELD_TEMPLATE_TYPE))) {
			userList = getUsersInRole(obsItemId);
		} else if (VALUE_OBS_TEAMTYPE.equals(obsData.get(FIELD_TEMPLATE_TYPE))) {
			userList = getUsersInTeam(obsItemId, true);
		}
		return userList;
	}

	/**
	 * 
	 * <p>
	 * <strong>is obs item is a role object</strong>
	 * </p>
	 * 
	 * @since 1.0
	 * @author hua
	 * 
	 * @param obsItem
	 * @return
	 */
	public boolean isRoleObject(DBObject obsItem) {

		return obsItem != null && VALUE_OBS_ROLETYPE.equals(obsItem.get(FIELD_TEMPLATE_TYPE));
	}

	/**
	 * 
	 * <p>
	 * <strong>is obs item is a team object</strong>
	 * </p>
	 * 
	 * @since 1.0
	 * @author hua
	 * 
	 * @param obsItem
	 * @return
	 */
	public boolean isTeamObject(DBObject obsItem) {

		return obsItem != null && VALUE_OBS_TEAMTYPE.equals(obsItem.get(FIELD_TEMPLATE_TYPE));
	}

	/**
	 * 
	 * <p>
	 * <strong>is obs item is a user object</strong>
	 * </p>
	 * 
	 * @since 1.0
	 * @author hua
	 * 
	 * @param obsItem
	 * @return
	 */
	public boolean isUserObject(DBObject obsItem) {

		return obsItem != null && VALUE_OBS_USERTYPE.equals(obsItem.get(FIELD_TEMPLATE_TYPE));
	}

	/**
	 * Get user data by uid
	 * @param uid
	 * @return 
	 */
	public DBObject getUserByUId(String uid) {
		
		return userCollection.findOne(new BasicDBObject().append(FIELD_UID, uid));
		
	}
	

	public DBObject getUserByUserDesc(String userDesc) {

		return userCollection.findOne(new BasicDBObject().append(FIELD_DESC, userDesc));
	}
	

	public String getOBSItemLabel(DBObject obsItem) {

		Object type = obsItem.get(FIELD_TEMPLATE_TYPE);
		if(VALUE_OBS_PJTEAMTYPE.equals(type)){
			//项目组
			return (String) obsItem.get(FIELD_DESC);
			
		}
		
		if(VALUE_OBS_ROLETYPE.equals(type)){
			//角色
			return (String) obsItem.get(FIELD_DESC);
		}
		
		if(VALUE_OBS_TEAMTYPE.equals(type)){
			//团队
			return (String) obsItem.get(FIELD_DESC);
			
		}
		if(VALUE_OBS_USERTYPE.equals(type)){
			//人
			DBObject user = getUserObject((ObjectId) obsItem.get(FIELD_UID));
			
			return user.get(FIELD_NAME)+" " + user.get(FIELD_DESC);
		}
		
		return "";
	}

	public String getOBSItemLabel(ObjectId obsItemOId) {
		
		DBObject obsItem = getOBSItemData(obsItemOId);
		
		return getOBSItemLabel(obsItem) ;
		
	}

	public DBObject getOBSItemData(ObjectId obsItemOId) {

		return getDBObject(orgCollection, obsItemOId);
	}
	
	public DBObject getCurrentUserData(){
		ObjectId uoid = UserSessionContext.getSession().getUserOId();
		return getUserObject(uoid);
	}

	@Deprecated
	public List<DBObject> getUserIdList() {
		return userCollection.find().toArray();
	}
	
	public List<DBObject> getUserIdList2(){
		return userCollection.find(new BasicDBObject(),new BasicDBObject().append(FIELD_UID, 1)).toArray();
	}
	
	/**
	 * 
	 * Create OBS节点
	 * 
	 * @param rootId
	 *            项目id项目组通常作为根组织存在 ,如果不为空，将向obs的rootid字段传递根组织的id,创建根组织本身时这个将被忽略
	 * @param parentId
	 *            上级id
	 * @param itemId
	 *            本级id,可以传null，传空时返回id
	 * @param desc
	 *            ,当创建用户时，传递用户的oid,其他情况传描述
	 * @param obstype
	 *            ，传递obs的类型 VALUE_OBS_ROLETYPE, VALUE_OBS_USERTYPE
	 *            ,VALUE_OBS_PJTEAMTYPE ,VALUE_OBS_TEAMTYPE
	 * @return 该obs节点的oid
	 */
	public DBObject createOBSItem(ObjectId rootId, ObjectId parentId, ObjectId itemId, Object desc, String obstype) {

		if (itemId == null) {
			itemId = new ObjectId();
		}
		DBObject obsItem = new BasicDBObject();
		if (rootId != null && (!Util.equals(rootId, itemId))) {
			obsItem.put(FIELD_ROOTID, rootId);
		}
		obsItem.put(FIELD_SYSID, itemId);
		obsItem.put(FIELD_TEMPLATE_TYPE, obstype);
		obsItem.put(FIELD_OBSPARENT, parentId);

		if (VALUE_OBS_USERTYPE.equals(obstype)) {
			obsItem.put(FIELD_USEROID, desc);
		} else {
			obsItem.put(FIELD_DESC, desc);
		}
		
		setSystemCreateInfo(obsItem);

		orgCollection.insert(obsItem);

		return obsItem;
	}

	public List<DBObject> getSubTeam(ObjectId parentTeamId) {
		DBCursor cur = orgCollection.find(new BasicDBObject().append(FIELD_OBSPARENT, parentTeamId).append(FIELD_TEMPLATE_TYPE, VALUE_OBS_TEAMTYPE));
		return cur.toArray();
	}

	/**
	 * 列出下级组织
	 * @param parentId
	 * @param cascade
	 * @return 不会为null
	 */
	public List<DBObject> getSubTeam(ObjectId parentId, boolean cascade) {
		
		List<DBObject> result = new ArrayList<DBObject>();
		DBCursor cur = orgCollection.find(new BasicDBObject().append(FIELD_OBSPARENT, parentId).append(FIELD_TEMPLATE_TYPE, VALUE_OBS_TEAMTYPE));
		while(cur.hasNext()){
			DBObject next = cur.next();
			result.add(next);
			if(cascade){
				result.addAll(getSubTeam((ObjectId) next.get(FIELD_SYSID),true));
			}
		}
		return result;
	}

	public String getOBSPath(ObjectId deptId) {
		DBObject data = getOBSItemData(deptId);
		if(data==null){
			return null;
		}
		ObjectId parentId = (ObjectId) data.get(FIELD_OBSPARENT);
		String parentDesc = getOBSPath(parentId);
		String desc  = (String) data.get(FIELD_DESC);
		if(parentDesc!=null){
			return parentDesc +"/"+desc;
		}else{
			return desc;
		}
	}

	public DBObject getRoleInProjectByName(ObjectId parentId, String roleName) {
		return orgCollection.findOne(new BasicDBObject().append(FIELD_OBSPARENT, parentId).append(FIELD_DESC, roleName));
	}

	public DBObject getTeamByName(String name) {
		return orgCollection.findOne(new BasicDBObject().append(FIELD_DESC, name));
	}

	
	public DBObject getSiteOfUserId(String userId){
		DBObject userData = getUserByUId(userId);
		ObjectId siteId = (ObjectId) userData.get(FIELD_SITEPARENT);
		return getSiteObject(siteId);
	}
	
	public String getSiteNameofUserId(String userId){
		DBObject siteData = getSiteOfUserId(userId);
		return (String) siteData.get(FIELD_DESC);
	}

}
