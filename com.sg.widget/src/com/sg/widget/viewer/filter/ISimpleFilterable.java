package com.sg.widget.viewer.filter;


public interface ISimpleFilterable {

	String[] getFilterColumnNameList();

	String[] getFilterColumnLabelList();

	String[] getFilterColumnTypeList();

	void setSimpleFilter(SimpleFilterCondition condition);

	void addSimpleFilter(SimpleFilterCondition condition);

	void removeSimpleFilters();

}
