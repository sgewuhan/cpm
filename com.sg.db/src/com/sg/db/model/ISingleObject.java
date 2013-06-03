package com.sg.db.model;

import java.util.Map;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public interface ISingleObject extends IJSONTranslate{

	public static final String UPDATED = "updated";

	public static final String INSERTED = "inserted";

	public static final String REMOVE = "remove";

	public Map<String, Object> getValueMap();

	public DBCollection getCollection();

	public boolean save();
	
	public boolean remove();

	public boolean isNewObject();

	public void addValueListener(IValueChangeListener listener);

	public void removeValueListener(IValueChangeListener listener);

	public Object getValue(String fieldName);
	
	public String getText(String fieldName);

	public void setValue(String key, Object newValue, Object source,
			boolean noticeFieldValueChange);

	public void removeEventListener(IEventListener listener);

	public void addEventListener(IEventListener listener);

	public void addFieldValueListener(String key, IValueChangeListener listener);

	public void removeFieldValueListener(String key, IValueChangeListener listener);

	public DBObject getData();

	public boolean hasKey(String key);

	public int getObjectType();

}
