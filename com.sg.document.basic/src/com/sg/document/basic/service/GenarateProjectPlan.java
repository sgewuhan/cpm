package com.sg.document.basic.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.Assert;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sg.bpm.service.task.ServiceProvider;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.db.DBActivator;

/**
 * 根据传入的workid生成对应的科研开发任务书 并且设置项目指导专家 科研开发任务书的id为com.sg.cpm.editor.JZ-QR-XG004A
 * 
 * @author hua
 * 
 */
public class GenarateProjectPlan extends ServiceProvider {

	public static final String SOURCE_EDITOR_ID = "com.sg.cpm.editor.JZ-QR-XG003A";// 项目立项申请表

	public static final String TARGET_EDITOR_ID = "com.sg.cpm.editor.JZ-QR-XG004A";// 科研开发任务书
	
	public static final String PROCESS_PARAMETER = "act_prj_director_list";

	private final DBCollection document = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_DOCUMENT);

	private static final String[] COPYFIELDS = new String[] { "wbsparent", "templateType", "fbsparent", "rootid", "projectdesc", "direction", "dept", "type", "pm",
			"pmdegree", "pmtitle", "director", "mastercnt", "directorcnt", "seniorcnt", "joniorcnt", "planstart", "planfinish", "budget", "research",
			"result_1", "result_2", "result_3", "result_4", "result_5", "result_6", "result_7", "result_8", "result_9" };

	@Override
	public Map<String, Object> run(Object parameter) {

		Assert.isNotNull(parameter, "work id is null");

		// 传入的parameter是workid
		ObjectId workId = new ObjectId((String) parameter);

		// 首先获得对应的workid的项目立项申请表文档
		DBObject doc = getSourceDocument(workId);
		Assert.isNotNull(doc, "无法获得文档，id:com.sg.cpm.editor.JZ-QR-XG003A, work id:" + parameter);

		// 获得方向首席师列表
		BasicDBList directorDataList = (BasicDBList) doc.get("director");
		Assert.isLegal(directorDataList!=null&&directorDataList.size()>0, "首席师列表为空，流程无法进行");

		
		// 构造一个科研开发任务书对象
		DBObject targetDoc = new BasicDBObject();

		// 从源文档提取对应的字段到科研开发任务书中
		copyValue(doc, targetDoc);

		// set editor id;
		targetDoc.put(IDBConstants.FIELD_SYSTEM_EDITOR, TARGET_EDITOR_ID);

		//设置文档名称
		String documentName = BusinessService.getDocumentService().getDocumentNameFromEditor(TARGET_EDITOR_ID);
		targetDoc.put(IDBConstants.FIELD_DESC, documentName);
		
		// set system information
		setSystemCreateInfo(targetDoc, workId);


		// 从源文档中提取对应的方向首席师返回给流程以设置项目指导人审核活动的执行者
		Map<String, Object> result = getProcessReturn(directorDataList);
		
		document.insert(targetDoc);

		return result;
	}

	private Map<String, Object> getProcessReturn(BasicDBList directorDataList) {

		// 参考new ServiceTaskHandler() 里面的方法
		List<String> directors = new ArrayList<String>();
		Iterator<Object> iter = directorDataList.iterator();
		while(iter.hasNext()){
			DBObject dbo = (DBObject) iter.next();
			directors.add((String) dbo.get(IDBConstants.FIELD_UID));
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(PROCESS_PARAMETER, directors);
		return result;
	}

	private void setSystemCreateInfo(DBObject data, ObjectId workId) {

		DBObject work = BusinessService.getWorkService().getWorkObject(workId);

		// 创建者 创建时间
		data.put(IDBConstants.FIELD_CREATER, work.get(IDBConstants.FIELD_CREATER));
		// 因为创建者的名字字段不显示
		data.put(IDBConstants.FIELD_CREATER_NAME, work.get(IDBConstants.FIELD_CREATER_NAME));

		data.put(IDBConstants.FIELD_OWNER, work.get(IDBConstants.FIELD_OWNER));

		data.put(IDBConstants.FIELD_OWNER_NAME, work.get(IDBConstants.FIELD_OWNER_NAME));

		data.put(IDBConstants.FIELD_CREATE_DATE, new Date());

	}

	private void copyValue(DBObject doc, DBObject targetDoc) {

		for (int i = 0; i < COPYFIELDS.length; i++) {
			targetDoc.put(COPYFIELDS[i], doc.get(COPYFIELDS[i]));
		}
	}

	private DBObject getSourceDocument(ObjectId workId) {

		return document.findOne(new BasicDBObject().append(IDBConstants.FIELD_WBSPARENT, workId)
				.append(IDBConstants.FIELD_TEMPLATE_TYPE, IDBConstants.VALUE_WBS_DOCUMENT_TYPE).append(IDBConstants.FIELD_SYSTEM_EDITOR, SOURCE_EDITOR_ID));

	}
}
