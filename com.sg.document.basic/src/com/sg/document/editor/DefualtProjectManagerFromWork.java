package com.sg.document.editor;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.db.DBActivator;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.field.defaultvalue.IDefaultValueProvider;

public class DefualtProjectManagerFromWork implements IDefaultValueProvider {

	private DBCollection workCollection;

	public DefualtProjectManagerFromWork() {

		workCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_WORK);
	}

	@Override
	public Object getDefaultValue(ISingleObject data, String key) {
		ObjectId workId = (ObjectId) data.getValue(IDBConstants.FIELD_WBSPARENT);
		if(workId==null) return null;
		
		DBObject dto = workCollection.findOne(new BasicDBObject().append(IDBConstants.FIELD_SYSID, workId), new BasicDBObject().append(IDBConstants.FIELD_WORK_PM,1));
		if(dto==null){
			return null;
		}
		
		return dto.get(IDBConstants.FIELD_WORK_PM);
	}
}
