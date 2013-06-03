package com.sg.db.expression;

import java.util.Date;

import com.mongodb.DBCollection;

public class CurrentTimestampValueProvider implements IFieldValueProvider{

	@Override
	public Object getValue(DBCollection collection, String fieldName) {
		return new Date().getTime();
	}


}
