package com.sg.widget.resource;

import java.util.ArrayList;
import java.util.List;

public abstract class EnumerateContribution implements IEnumerateContribution {

	public EnumerateContribution(){
		
	}
	
	@Override
	public abstract String getlabel() ;

	@Override
	public abstract String getValue() ;

	@Override
	public List<Enumerate> getChildren() {
		List<Enumerate> result = new ArrayList<Enumerate>();
		String[] ids = getChildrenIdList();
		String[] labels = getChildrenIdList();
		Object[] values = getChildrenValueList();
		for(int i=0;i<ids.length;i++){
			result.add(new Enumerate(ids[i],labels[i],values[i],null));
		}
		return result;
	}
	
	public abstract String[] getChildrenIdList();
	public abstract String[] getChildrenLabelList();
	public abstract Object[] getChildrenValueList();

	public Enumerate getEnumerate(String id){
		return new Enumerate(id,getlabel(),getValue(),getChildren());
	}
}
