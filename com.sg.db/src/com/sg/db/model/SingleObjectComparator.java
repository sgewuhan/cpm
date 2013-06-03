package com.sg.db.model;

import java.util.Comparator;

public class SingleObjectComparator implements Comparator<ISingleObject> {

	private String[] keys;

	public SingleObjectComparator(String[] keys){
		this.keys = keys;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public int compare(ISingleObject o1, ISingleObject o2) {
		for(int i=0;i<keys.length;i++){
			Object value1 = o1.getValue(keys[i]);
			Object value2 = o2.getValue(keys[i]);
			if (value1 == null && value2 == null) {
				continue;
			} else if (value1 != null && value2 == null) {
				return 1;
			} else if (value1 == null && value2 != null) {
				return -1;
			} else if ((value1 instanceof Comparable) && (value2 instanceof Comparable)) {
				return ((Comparable) value1).compareTo(((Comparable) value2));
			} else {
				continue;
			}
		}
		return 0;
	}


}
