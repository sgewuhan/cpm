package com.sg.widget.editor;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.Util;
import org.eclipse.ui.IPersistableElement;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sg.db.DBActivator;
import com.sg.db.model.ISingleObject;
import com.sg.user.AuthorityResponse;
import com.sg.widget.Widget;
import com.sg.widget.configuration.EditorConfiguration;

public class SingleObjectEditorInput implements ISingleObjectEditorInput {
	
	private static final String NOTNULL = "编辑器输入的数据不可为空";

	private EditorConfiguration ce;
	
	private ISingleObject data;
	
	private boolean editable = true;

	private DBCollection collection;

	private AuthorityResponse auth;

	private boolean isLocked;

	private String lockReason;

	private String lockedby;

	private ISingleObject historyData;
	
	private static final String FIELD_LOCKMARK = "lockmark";
	
	private static final String FIELD_LOCKEDBY = "lockedby";

	private static final String FIELD_LOCKREASON = "lockreason";
	

	public SingleObjectEditorInput(EditorConfiguration ce,ISingleObject data) {
		Assert.isNotNull(data,NOTNULL);
		this.ce = ce;
		String collectionName = ce.getCollection();
		collection = DBActivator.getDefaultDBCollection(collectionName);
		setInput(data);
		//锁定对象的可编辑性处理
	}

	public SingleObjectEditorInput(String id,ISingleObject data) {
		Assert.isNotNull(data,NOTNULL);
		ce = Widget.getSingleObjectEditorConfiguration(id);
		String collectionName = ce.getCollection();
		collection = DBActivator.getDefaultDBCollection(collectionName);
		setInput(data);
	}

	/**
	 * 用于根据数据打开默认的编辑器
	 * @param data
	 */
	public SingleObjectEditorInput(ISingleObject data) {
		Assert.isNotNull(data,NOTNULL);
		collection = data.getCollection();
		this.ce =  Widget.getSingleObjectEditorConfigurationByCollection(collection.getName());
		setInput(data);
	}

	public void setInput(ISingleObject data) {
		this.data = data;
		this.historyData = data;
		processLockedData();
	}

	private void processLockedData() {
		//读取锁定信息
		if(Boolean.TRUE.equals(data.getValue(FIELD_LOCKMARK))){//被锁定
			this.isLocked = true;
			this.lockReason = data.getText(FIELD_LOCKREASON);
			this.lockedby = data.getText(FIELD_LOCKEDBY);
		}else{
			this.isLocked = false;
			this.lockReason = null;
			this.lockedby = null;
		}
		
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return ce.getImageDescription();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return ce.getTitleToolTips(this);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}


	@Override
	public String getName() {
		return ce.getName(this);
	}

	@Override
	public EditorConfiguration getConfig() {
		return ce;
	}

	@Override
	public ISingleObject getInputData() {
		return data;
	}

	public boolean isEditable() {
		return editable&&(!isLocked);
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Override
	public void save(IProgressMonitor monitor) {
		data.save();
	}

	@Override
	public boolean isNewObject() {
		return data.isNewObject();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
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
		SingleObjectEditorInput other = (SingleObjectEditorInput) obj;
		if(data==null&&other.data==null){
			return true;
		}else if (data == null &&other.data != null){
			return false;
		}else if (data !=null && other.data==null){
			return false;
		}else {
			//判断collection是否一样
			DBCollection c1 = data.getCollection();
			DBCollection c2 = other.data.getCollection();
			if(!c1.equals(c2)){
				return false;
			}
			
			Object id = data.getValue("_id");
			Object otherId = other.data.getValue("_id");
			if(id!=null&&otherId!=null){
				return id.equals(otherId);
			}else{
				return mapEquals(data.getData(),other.data.getData());
			}
		}
	}
	
	private boolean mapEquals(DBObject data1, DBObject data2) {
		Set<String> keys = data1.keySet();
		Set<String> keys2 = data2.keySet();
		if(keys.containsAll(keys2)&&keys2.containsAll(keys)){
			Iterator<String> iter = keys.iterator();
			while(iter.hasNext()){
				String key = iter.next();
				Object value1 = data1.get(key);
				Object value2 = data2.get(key);
				if(!Util.equals(value1, value2)){
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public void setAuthorityResponse(AuthorityResponse resp) {
		auth = resp;
	}
	@Override
	public AuthorityResponse getAuthorityResponse(){
		return auth;
	}

	
	public boolean isLocked() {
	
		return isLocked;
	}

	
	public String getLockReason() {
	
		return lockReason;
	}

	
	public String getLockedby() {
	
		return lockedby;
	}

}
