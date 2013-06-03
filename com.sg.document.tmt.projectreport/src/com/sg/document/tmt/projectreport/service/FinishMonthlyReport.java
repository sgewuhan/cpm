package com.sg.document.tmt.projectreport.service;

import java.util.Map;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.DocumentService;
import com.sg.db.DBActivator;
import com.sg.document.basic.service.FinishPhaseReview;

public class FinishMonthlyReport extends FinishPhaseReview {

	@Override
	public Map<String, Object> run(Object parameter) {
		//标记月报通过
		String _workOid =  (String) getInputValue("p_WorkId");
		ObjectId workId = new ObjectId(_workOid);
		DocumentService documentService = BusinessService.getDocumentService();
		DBObject doc = documentService.getWorkDocument(workId, "com.sg.cpm.editor.projectmonthreport");
		if(doc!=null){
			doc.put("reportStatus", "已批准");
			documentService.saveDocument(doc);
			//标记工作的名称
			Object year = doc.get("year");
			Object month = doc.get("month");
			String workName = "月报"+year+"-"+month;
			DBCollection workCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_WORK);
			workCollection.update(new BasicDBObject().append(IDBConstants.FIELD_SYSID, workId), new BasicDBObject().append("$set", new BasicDBObject().append("desc", workName)));
			
		}
		
		return super.run(parameter);
	}


}
