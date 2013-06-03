package com.sg.db.expression;

import com.mongodb.DBCollection;
import com.sg.db.DBConstants;
import com.sg.db.Util;

public class SequenceIdProvider implements IFieldValueProvider {

	@Override
	public Object getValue(DBCollection collection, String fieldName) {
		return Util.getIncreasedID(DBConstants.getIDSCollection(), collection.getName());
	}

}
