package com.sg.common.service;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.DBActivator;
import com.sg.db.model.SingleObject;

public class MessageObject {

	private DBObject messageData;
	private DBObject targetData;

	private DBCollection messageCollection;
	private DBCollection targetCollection;
	private ObjectId targetId;
	private ObjectId messageId;
	private boolean isChargedTarget;
	private DBCollection docCollection;

	public MessageObject(DBObject messageData, DBCollection messageCollection, DBCollection targetCollection, boolean isChargedTarget) throws ServiceException {
		this.messageCollection = messageCollection;
		this.targetCollection = targetCollection;

		targetId = (ObjectId) messageData.get(IDBConstants.FIELD_ID);
		messageId = (ObjectId) messageData.get(IDBConstants.FIELD_SYSID);

		this.messageData = messageData;

		targetData = targetCollection.findOne(new BasicDBObject().append(IDBConstants.FIELD_SYSID, targetId));

		if(targetData==null){
			throw new ServiceException(ServiceException.INCONSISTENT_DATA_CAUSE_BY_DELETE);
		}
		
		this.isChargedTarget = isChargedTarget;

		docCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_DOCUMENT);
	}
	
	public boolean isChargedTarget() {
		return isChargedTarget;
	}

	public void saveMessage() {
		messageCollection.save(messageData);
	}

	public void saveTarget() {
		targetCollection.save(targetData);
	}

	public void saveAll() {
		saveMessage();
		saveTarget();
	}

	public SingleObject getTargetSingleObject() {
		return new SingleObject(targetCollection, targetData);
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((messageId == null) ? 0 : messageId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageObject other = (MessageObject) obj;
		if (messageId == null) {
			if (other.messageId != null)
				return false;
		} else if (!messageId.equals(other.messageId))
			return false;
		return true;
	}

	public void putMessageValue(String field, Object value) {
		messageData.put(field, value);
	}

	public Object getMessageValue(String fieldName) {
		return messageData.get(fieldName);
	}

	public Object getTargetValue(String fieldName) {
		return targetData.get(fieldName);
	}
	

	public boolean isReady() {
		return DataUtil.isReady(targetData);
	}

	public boolean isProcess() {
		return DataUtil.isProcess(targetData);
	}

	public boolean isPause() {
		return DataUtil.isPause(targetData);
	}

	public boolean isClose() {
		return DataUtil.isClose(targetData);
	}

	public boolean isCancel() {
		return DataUtil.isCancel(targetData);
	}

	public boolean isMarkDelete() {
		return Boolean.TRUE.equals(messageData.get(IDBConstants.FIELD_MARK_DELETE));
	}

	public boolean isMarkStar() {
		return Boolean.TRUE.equals(messageData.get(IDBConstants.FIELD_MARK_STAR));
	}

	public boolean isMarkRead() {
		return Boolean.TRUE.equals(messageData.get(IDBConstants.FIELD_MARK_READ));
	}

	public boolean hasChildren() {
		if (isMarkDelete()) {
			return false;
		}

		DBObject data = docCollection.findOne(new BasicDBObject().append(IDBConstants.FIELD_WBSPARENT, targetId));
		return data != null;
	}

	public Object get(String key) {
		String[] s = key.split("@");
		if (s[0].equals("message")) {
			return messageData.get(s[1]);
		}

		if (s[0].equals("target")) {
			return targetData.get(s[1]);
		}

		return null;
	}

	public Object[] getChildren() {
		if (isMarkDelete()) {
			return new Object[] {};
		}
		DBCursor cur = docCollection.find(new BasicDBObject().append(IDBConstants.FIELD_WBSPARENT, targetId));
		return cur.toArray().toArray();
	}

	public void start() {
		targetData.put(IDBConstants.FIELD_PROCESS_STATUS, IDBConstants.VALUE_PROCESS_PROCESS);
		targetData.put(IDBConstants.FIELD_PROJECT_ACTUALSTART, new Date());
		targetCollection.save(targetData);

		//如果这个工作是带有流程定义的，需要启动流程，服务自动判断这种情况
		try {
			BusinessService.getWorkflowService().startWorkProcess(targetData);
		} catch (ServiceException e) {
			
			e.openMessageBox();
		}
		
	}

	public void finish() {
		targetData.put(IDBConstants.FIELD_PROCESS_STATUS, IDBConstants.VALUE_PROCESS_CLOSE);
		targetData.put(IDBConstants.FIELD_PROJECT_ACTUALFINISH, new Date());
		targetCollection.save(targetData);

	}

	public void markStar(boolean b) {
		messageData.put(IDBConstants.FIELD_MARK_STAR,b);
		messageCollection.save(messageData);
	}

	public void markRead(boolean b) {
		messageData.put(IDBConstants.FIELD_MARK_READ,b);
		messageCollection.save(messageData);
	}

	public void markDelete(boolean b) {
		messageData.put(IDBConstants.FIELD_MARK_DELETE,b);
		messageCollection.save(messageData);
	}


	public void markStar() {
		boolean b = !(Boolean.TRUE.equals(messageData.get(IDBConstants.FIELD_MARK_STAR)));
		markStar(b);
	}

	public void markRead() {
		boolean b = !(Boolean.TRUE.equals(messageData.get(IDBConstants.FIELD_MARK_READ)));
		markRead(b);
	}

	public int compare(MessageObject another, Map<String,Integer> keySet) {
		
		Iterator<String> iter = keySet.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			int dir = keySet.get(key).intValue();
			
			Object value1 = get(key);
			Object value2 = another.get(key);
			if (value1 == null && value2 == null) {
				continue;
			} else if (value1 != null && value2 == null) {
				return 1*dir;
			} else if (value1 == null && value2 != null) {
				return -1*dir;
			} else if ((value1 instanceof Comparable) && (value2 instanceof Comparable)) {
				return ((Comparable) value1).compareTo(((Comparable) value2))*dir;
			} else {
				continue;
			}
		}
		return 0;
	}

	public boolean isWorkflowData() {

		String processdefId = (String) targetData.get(IDBConstants.FIELD_PROCESS_DEFINITION_ID);
		return processdefId!=null&&processdefId.length()>0;
	}

	public boolean canStartTask() {

		DBObject wfinfo = (DBObject) messageData.get(IDBConstants.FIELD_WFINFO);
		return BusinessService.getWorkflowService().canStart(wfinfo);
	}

	public boolean canFinishTask() {

		DBObject wfinfo = (DBObject) messageData.get(IDBConstants.FIELD_WFINFO);
		return BusinessService.getWorkflowService().canFinish(wfinfo);
	}


}
