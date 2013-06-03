package com.sg.widget.model;

import com.mongodb.DBCollection;
import com.sg.db.model.ISingleObject;
@Deprecated
public class SingleActiveObject implements ActiveObject {

	private ISingleObject singleObject;

	public SingleActiveObject(ISingleObject so) {
		this.singleObject = so;
	}

	@Override
	public void remove() {
		singleObject.remove();
	}

	@Override
	public String getCollection() {
		DBCollection collection = singleObject.getCollection();
		if(collection!=null){
			return collection.getName();
		}
		return null;
	}

	@Override
	public int getObjectType() {
		return singleObject.getObjectType();
	}

	@Override
	public String getDisplayText() {
		return singleObject.toString();
	}
	
	

}
