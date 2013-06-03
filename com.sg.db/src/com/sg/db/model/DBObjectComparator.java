package com.sg.db.model;

import java.util.Comparator;

import com.mongodb.DBObject;

public class DBObjectComparator implements Comparator<DBObject> {

	private String[] keys;

	public DBObjectComparator(String[] keys){
		this.keys = keys;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public int compare(DBObject o1, DBObject o2) {
		for(int i=0;i<keys.length;i++){
			String[] keyString = keys[i].split(",");
			
			String key = keyString[0];
			int dir = 1;
			try{
				dir = Integer.parseInt(keyString[1]);
			}catch(Exception e){}
					
			Object value1 = o1.get(key);
			Object value2 = o2.get(key);
			if (value1 == null && value2 == null) {
				continue;
			} else if (value1 != null && value2 == null) {
				return 1*dir;
			} else if (value1 == null && value2 != null) {
				return -1*dir;
			} else if ((value1 instanceof Comparable) && (value2 instanceof Comparable)) {
				return ((Comparable) value1).compareTo(((Comparable) value2))*dir;
			} else {
				continue;
			}
		}
		return 0;
	}


}
