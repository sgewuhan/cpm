package com.sg.common.service;

import java.util.Date;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.db.DBActivator;
import com.sg.db.model.ISingleObject;
import com.sg.user.UserSessionContext;

public class CommonService implements IDBConstants {

	protected final DBCollection docCollection;

	protected final DBCollection workCollection;

	protected final DBCollection projectCollection;

	protected final DBCollection userCollection;

	protected final DBCollection orgCollection;

	protected final DBCollection workTemplateCollection;
	
	protected final DBCollection projectTemplateCollection;

	protected final DBCollection siteCollection;

	protected final DBCollection userChargedWorkCollection;

	protected final DBCollection userParticipateWorkCollection;
	
	protected final DBCollection ksessionCollection;

	protected final DBCollection folderCollection;

	protected final DBCollection noticeCollection;

	public static final String NOT = "$ne";

	public static final String IN = "$in";

	public static final String SET = "$set";
	
	public static final String PUSH = "$push";
	
	public CommonService() {

		docCollection = DBActivator.getDefaultDBCollection(COLLECTION_DOCUMENT);

		workCollection = DBActivator.getDefaultDBCollection(COLLECTION_WORK);

		projectCollection = DBActivator.getDefaultDBCollection(COLLECTION_PROJECT);

		userCollection = DBActivator.getDefaultDBCollection(COLLECTION_USER);

		orgCollection = DBActivator.getDefaultDBCollection(COLLECTION_ORG);

		workTemplateCollection = DBActivator.getDefaultDBCollection(COLLECTION_WORK_TEMPLATE);
		
		projectTemplateCollection = DBActivator.getDefaultDBCollection(COLLECTION_PROJECT_TEMPLATE);

		siteCollection = DBActivator.getDefaultDBCollection(COLLECTION_SITE);

		userChargedWorkCollection = DBActivator.getDefaultDBCollection(COLLECTION_USER_WORK_IN_CHARGED);

		userParticipateWorkCollection = DBActivator.getDefaultDBCollection(COLLECTION_USER_WORK_PARTTICIPATED);
		
		folderCollection = DBActivator.getDefaultDBCollection(COLLECTION_FOLDER);
		
		ksessionCollection = DBActivator.getDefaultDBCollection(COLLECTION_KSESSION);
		
		noticeCollection =  DBActivator.getDefaultDBCollection(COLLECTION_NOTICE);
	}

	public DBObject getDBObject(DBCollection collection, ObjectId systemId) {

		return collection.findOne(new BasicDBObject().append(FIELD_SYSID, systemId));
	}
	
	public DBObject getUserObject(ObjectId userId) {

		return getDBObject(userCollection, userId);
	}

	public DBObject getWorkTemplateObject(ObjectId systemId) {

		return getDBObject(workTemplateCollection, systemId);
	}
	
	public DBObject getProjectTemplateObject(ObjectId templateId) {

		return getDBObject(projectTemplateCollection, templateId);
	}

	public DBObject getProjectObject(ObjectId projectId) {

		return getDBObject(projectCollection, projectId);
	}

	public DBObject getSiteObject(ObjectId siteId) {

		return getDBObject(siteCollection, siteId);
	}
	
	public DBObject getSiteObject(String sitename){
		return siteCollection.findOne(new BasicDBObject().append(FIELD_DESC, sitename));
	}

	public DBObject getWorkObject(ObjectId workId) {

		return getDBObject(workCollection, workId);
	}

	/**
	 * <p>
	 * check item wheather in the list by _id
	 * </p>
	 * 
	 * @author hua
	 * @since 1.0
	 * @param targetList
	 *            targetList
	 * @param item
	 *            item
	 * @return
	 */
	public boolean existInListById(BasicDBList targetList, DBObject item) {

		for (int i = 0; i < targetList.size(); i++) {
			DBObject targetItem = (DBObject) targetList.get(i);
			if (item.get(FIELD_SYSID).equals(targetItem.get(FIELD_SYSID))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * 
	 * 
	 * @author hua
	 * @since 1.0
	 * @param data
	 */
	public void setSystemCreateInfo(DBObject data) {

		String createUserId;
		String createUserName;
		try {
			// 创建者 创建时间
			createUserId = UserSessionContext.getSession().getUserId();
			createUserName = UserSessionContext.getSession().getUserName();
		} catch (Exception e) {
			createUserId = "internal";
			createUserName = "internal";
		}
		data.put(FIELD_CREATER, createUserId);
		// 因为创建者的名字字段不显示
		data.put(FIELD_CREATER_NAME, createUserName);

		String ownerUserId = createUserId;
		data.put(FIELD_OWNER, ownerUserId);

		String ownerUserName = createUserName;
		data.put(FIELD_OWNER_NAME, ownerUserName);

		Date createDate = new Date();
		data.put(FIELD_CREATE_DATE, createDate);
	}
	
	public DBObject getSessionUserCreateInfo() {
		BasicDBObject data = new BasicDBObject();
		setSystemCreateInfo(data);
		return data;
	}

	public void setSystemModifyInfo(ISingleObject data) {

		Date modifydate = new Date();
		data.setValue(FIELD_MODIFY_DATE, modifydate, null, true);
	}
	
	public ObjectId checkSystemId(DBObject data){
		ObjectId sysid = (ObjectId) data.get(FIELD_SYSID);
		if(sysid==null){
			sysid = new ObjectId();
			data.put(FIELD_SYSID, sysid);
		}
		return sysid;
		
	}
	
}
