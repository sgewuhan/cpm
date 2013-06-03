package com.sg.db.http;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.sg.db.DBActivator;
import com.sg.db.IServiceError;
import com.sg.db.Util;
import com.sg.db.expression.insert.InsertExpression;
import com.sg.db.expression.query.QueryExpression;

public class QueryUpdateService extends BasicDBService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2001941596097642225L;

	public QueryUpdateService() {
		super();
	}

	@Override
	protected String getJSONResponse(HttpServletRequest req,HttpServletResponse resp) {

		// ****************************************************
		// 1. 对查询表达式进行设置

		// 获得查询表达式的id
		String queryExpId = req.getParameter(KEEPS[QUERY_EXP]);
		if (Util.isNullorEmpty(queryExpId)) {
			queryExpId = req.getParameter(KEEPS[EXP_PARAM]);
			if (Util.isNullorEmpty(queryExpId))
				return IServiceError.ERR_NOT_ENOUGH_PARAMETER + " " + KEEPS[QUERY_EXP];
		}

		// 获得查询表达式
		QueryExpression queryExp = (QueryExpression) DBActivator.getExpression(queryExpId);

		// 获得输入参数
		Map<String, String> paramValueMap = getInputParameters(req);
		// 向查询表达式输入参数
		queryExp.setParamValueMap(paramValueMap);

		// 获得返回字段
		String returnFieldsNameList = req.getParameter(KEEPS[RETURN]);
		if (!Util.isNullorEmpty(returnFieldsNameList)) {
			// 向查询表达式输入返回字段
			queryExp.setReturnFieldsFromString(returnFieldsNameList);
		}

		// 获得排序字段
		String sortFieldNameList = req.getParameter(KEEPS[SORT]);
		if (!Util.isNullorEmpty(returnFieldsNameList)) {
			// 向查询表达式输入排序字段
			queryExp.setSortFieldsFromString(sortFieldNameList);
		}

		// ****************************************************
		// 2. 使用查询表达式生成用于findandmodify的对象

		// 使用查询表达式生成查询条件对象
		BasicDBObject query = queryExp.loadCondition();

		// 使用查询表达式生成字段对象
		BasicDBObject fields = queryExp.getReturnFields();

		// 使用查询表达式生成排序对象
		BasicDBObject sort = queryExp.getSortFields();

		// ****************************************************
		// 3. 获得更新表达式的id
		String updateExpId = req.getParameter(KEEPS[UPDATE_EXP]);
		if (Util.isNullorEmpty(updateExpId)) {
			return IServiceError.ERR_NOT_ENOUGH_PARAMETER + " " + KEEPS[UPDATE_EXP];
		}
		// 获得更新表达式
		InsertExpression updateExp = (InsertExpression) DBActivator.getExpression(updateExpId);
		// 向更新表达式输入参数
		updateExp.setParamValueMap(paramValueMap);
		
		// 获得更新表达式对象
		BasicDBObject update = updateExp.loadCondition();

		// 获得需要更新的collection
		DBCollection collection = updateExp.getCollection();

		// ****************************************************
		// 4. 获得findandmodify的其他参数
		boolean remove = "true".equalsIgnoreCase(req.getParameter(KEEPS[REMOVE]));

		boolean returnNew = true;//"true".equalsIgnoreCase(req.getParameter(KEEPS[RETURN_NEW]));

		boolean upsert = "true".equalsIgnoreCase(req.getParameter(KEEPS[UPSERT]));

		// ****************************************************
		// 5. 进行更新
		DBObject result = collection.findAndModify(query, fields, sort, remove, update, returnNew, upsert);

		// ****************************************************
		// 6.对返回结果进行处理
		// 这些处理包括修改date类型等等，ObjectId等等
		return JSON.serialize(Util.translateBSON(result));
	}

}
