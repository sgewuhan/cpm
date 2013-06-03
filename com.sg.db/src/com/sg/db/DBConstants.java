package com.sg.db;

import com.mongodb.DBCollection;

public class DBConstants {
	public static final String IDS = "ids";
	private static DBCollection IDS_COLLECTION ;
	
	
	public static DBCollection getIDSCollection(){
		if(IDS_COLLECTION==null){
			IDS_COLLECTION = DBActivator.getDefaultDBCollection(IDS);
		}
		return IDS_COLLECTION;
	}
}
