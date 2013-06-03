package com.sg.widget.viewer.search;


public interface ISearchable {

	public boolean search(String text, int style);
	
	public boolean searchNext();
	
	public boolean searchPrevious();
	
	public boolean hasNextMatchedItem();
	
	public boolean hasPreviousMatchedItem();
	
}
