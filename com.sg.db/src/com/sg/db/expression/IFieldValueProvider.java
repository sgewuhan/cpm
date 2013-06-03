package com.sg.db.expression;

import com.mongodb.DBCollection;


public interface IFieldValueProvider {

	public Object getValue(DBCollection collection,String fieldName);

}
