package com.sg.db.model;

import java.util.List;



public interface IChildrenProvider {

	List<? extends CascadeObject> getChildren(CascadeObject parent);

	boolean continueGetChildrenFromDefinition();

}
