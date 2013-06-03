package com.sg.db.model;

import com.sg.db.model.ISingleObject;

public interface IEventListener {

	void event(String code, ISingleObject singleObject);

}
