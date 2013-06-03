package com.sg.db.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ListenerList;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.sg.db.Util;

public class SingleObject implements ISingleObject {

	public static final String UPDATED = "updated";

	public static final String INSERTED = "inserted";

	protected static final String REMOVE = "remove";

	private Map<String, Object> fieldValueChangeListeners;

	private ListenerList valueChangeListeners;

	private ListenerList eventListeners;

	protected DBObject dbObject;

	protected DBCollection collection;

	public SingleObject() {
		initListeners();
	}

	/**
	 * 读取的一条记录
	 * 
	 * @param dbObject
	 */
	public SingleObject(DBCollection collection, DBObject dbObject) {
		this();
		this.collection = collection;
		setData(dbObject);
	}

	/**
	 * 创建一条新纪录
	 * 
	 * @param collection
	 */
	public SingleObject(DBCollection collection) {
		this();
		this.collection = collection;
		setData(new BasicDBObject());
	}

	public SingleObject setData(DBObject dbo) {
		this.dbObject = dbo;
		return this;
	}

	private void initListeners() {
		valueChangeListeners = new ListenerList();
		fieldValueChangeListeners = new HashMap<String, Object>();
		eventListeners = new ListenerList();
	}

	@Override
	public Object getValue(String key) {
		initData();

		Assert.isNotNull(dbObject);
		return dbObject.get(key);
	}
	
	public ObjectId getSystemId(){
		return (ObjectId) getValue("_id");
	}

	@Override
	public void setValue(String key, Object newValue, Object source,
			boolean noticeFieldValueChange) {
		initData();

		Assert.isNotNull(dbObject);
		Object oldValue = dbObject.get(key);
		if (!Util.equals(oldValue, newValue)) {
			dbObject.put(key, newValue);
			if (noticeFieldValueChange)
				fireFieldValueChanged(key, oldValue, newValue, source);
		}
	}
	
	public void setValue(String key,Object value){
		setValue(key,value,null,false);
	}

	protected void fireEvent(String code) {
		Object[] listeners = eventListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			IEventListener il = (IEventListener) listeners[i];
			if (il != null)
				il.event(code, this);
		}
	}

	protected void fireFieldValueChanged(String key, Object oldValue,
			Object newValue, Object source) {
		ListenerList listenerList = (ListenerList) fieldValueChangeListeners
				.get(key);
		if ((listenerList != null) && (!listenerList.isEmpty())) {
			Object[] listeners = listenerList.getListeners();
			for (int i = 0; i < listeners.length; i++) {
				IValueChangeListener listener = ((IValueChangeListener) listeners[i]);
				if (listener.equals(source))
					continue;
				listener.valueChanged(key, oldValue, newValue);
			}
		}
		if ((valueChangeListeners != null) && (!valueChangeListeners.isEmpty())) {
			Object[] listeners = valueChangeListeners.getListeners();
			for (int i = 0; i < listeners.length; i++) {
				IValueChangeListener listener = ((IValueChangeListener) listeners[i]);
				if (listener.equals(source))
					continue;
				listener.valueChanged(key, oldValue, newValue);
			}
		}
	}

	@Override
	public void addFieldValueListener(String key, IValueChangeListener listener) {
		ListenerList listenerList = (ListenerList) fieldValueChangeListeners
				.get(key);
		if (listenerList == null) {
			listenerList = new ListenerList();
		}
		listenerList.add(listener);
	}

	@Override
	public void removeFieldValueListener(String key,
			IValueChangeListener listener) {
		ListenerList listenerList = (ListenerList) fieldValueChangeListeners
				.get(key);
		if (listenerList != null) {
			listenerList.remove(listener);
		}
	}

	@Override
	public void addEventListener(IEventListener listener) {
		eventListeners.add(listener);
	}

	@Override
	public void removeEventListener(IEventListener listener) {
		eventListeners.remove(listener);
	}

	@Override
	public void addValueListener(IValueChangeListener listener) {
		valueChangeListeners.add(listener);
	}

	@Override
	public void removeValueListener(IValueChangeListener listener) {
		valueChangeListeners.remove(listener);
	}

	@Override
	public boolean isNewObject() {
		initData();
		Assert.isNotNull(dbObject);
		return dbObject.get("_id") == null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getValueMap() {
		initData();
		if (dbObject != null)
			return dbObject.toMap();
		return null;
	}

	@Override
	public DBObject getData() {
		return dbObject;
	}

	public void initData() {

	}

	@Override
	public boolean hasKey(String key) {
		initData();
		return (dbObject != null) && dbObject.keySet().contains(key);
	}

	@Override
	public boolean remove() {
		Object _id = getData().get("_id");
		collection.remove(new BasicDBObject().append("_id", _id));
		fireEvent(REMOVE);
		return true;
	}

	@Override
	public boolean save() {
		if (isNewObject()) {
			ObjectId oid = new ObjectId();
			dbObject.put("_id", oid);
			collection.insert(dbObject);
			fireEvent(INSERTED);
		} else {
			collection.save(dbObject);
			fireEvent(UPDATED);
		}
		return true;
	}

	public DBCollection getCollection() {
		return collection;
	}

	public void setCollection(DBCollection coll) {
		collection = coll;
	}

	@Override
	public String getJSONResult(Map<String, String> transferFields, Set<String> removeFields) {
		return JSON.serialize(getBSONResult(transferFields,removeFields));
	}

	@Override
	public DBObject getBSONResult(Map<String, String> transferFields, Set<String> removeFields) {
		return Util.translateBSON(dbObject,null,transferFields,removeFields);
	}

	@Override
	public String getText(String fieldName) {
		Object value = getValue(fieldName);
		if(value == null){
			return "";
		}else{
			return value.toString();
		}
	}

	@Override
	public String toString() {
		//系统默认desc字段是文本显示字段
		return (String) getValue("desc");
	}

	@Override
	public int getObjectType() {
		try{
			Number value = (Number)getValue("_type");
			if(value!=null){
				return value.intValue();
			}
		}catch(Exception e){
		}
		return 0;
	}

}
