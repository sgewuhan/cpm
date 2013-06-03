package com.sg.db.expression;

import com.mongodb.BasicDBObject;

public interface IConditionProvider {

	BasicDBObject getCondition();

}
