package com.sg.db.http;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.DBObject;
import com.sg.db.DBActivator;
import com.sg.db.IServiceError;
import com.sg.db.Util;
import com.sg.db.expression.DBExpression;
import com.sg.db.expression.insert.InsertExpression;
import com.sg.db.expression.query.QueryExpression;
import com.sg.db.expression.remove.RemoveExpression;
import com.sg.db.expression.update.UpdateExpression;

public class DBService extends BasicDBService {
	
	@Override
	protected String getJSONResponse(HttpServletRequest req,HttpServletResponse resp) {
		String expId = getExpressiontId(req);
		DBExpression expression = DBActivator.getExpression(expId);
		if (expression != null) {

			if (expression instanceof QueryExpression) {
				return doQuery((QueryExpression) expression, req);
			} else if (expression instanceof InsertExpression) {
				return doInsert((InsertExpression) expression, req);
			} else if (expression instanceof UpdateExpression) {
				return doUpdate((UpdateExpression) expression, req);
			} else if (expression instanceof RemoveExpression) {
				return doRemove((RemoveExpression) expression, req);
			}

		}
		return IServiceError.ERR_INVALID_EXPRESSION;

	}

	protected String doUpdate(UpdateExpression updateExpression, HttpServletRequest req) {
		// 获取updateExpression 的表达式id
//		String updateExpId = getUpdateExpressionId(req);
//		if (Util.isNullorEmpty(updateExpId)) {
//			updateExpId = getExpressiontId(req);
//		}
//
//		if (Util.isNullorEmpty(updateExpId)) {
//			return IServiceError.ERR_NOT_ENOUGH_PARAMETER;
//		}
//		
//		UpdateExpression updateExpression = (UpdateExpression) DBActivator.getExpression(updateExpId);
//		if (updateExpression == null) {
//			return IServiceError.ERR_NULL_UPDATEOBJECT;
//		}

		Boolean isMultiUpdate = isMultiUpdate(req);
		if (isMultiUpdate != null) {
			updateExpression.setMulti(isMultiUpdate);
		}

		Boolean isUpsertUpdate = isUpsertUpdate(req);
		if (isUpsertUpdate != null) {
			updateExpression.setUpsert(isUpsertUpdate);
		}

		Map<String, String> inputParameterMap = getInputParameters(req);
		updateExpression.setParamValueMap(inputParameterMap);
		DBObject result = updateExpression.getBSONResult(null,null);
		
		if ((Integer) result.get(UpdateExpression.COUNT) > 0) {
			return IServiceError.SUCCESS;
		} else {
			return IServiceError.WAR_UPDATE_NO_RESULT;
		}
	}

	protected String doInsert(InsertExpression expression, HttpServletRequest req) {
		// 读取输入参数
		Map<String, String> inputParaMap = getInputParameters(req);
		expression.setParamValueMap(inputParaMap);

		Map<String,String> transferFields = getTransferFieldsForOutput();
		Set<String> removeFields = getRemovedFieldsForOutput();
		return expression.getJSONResult(transferFields,removeFields);
	}

	/**
	 * 
	 * @param expression
	 * @param req
	 * @return JSON格式的数据
	 */
	protected String doQuery(QueryExpression expression, HttpServletRequest req) {

		// 1. 读取输入参数
		Map<String, String> inputParaMap = getInputParameters(req);
		expression.setParamValueMap(inputParaMap);

		// 2. 读取skip和limit参数
		String skip = getSkip(req);
		String limit = getLimit(req);

		expression.setSkipAndLimit(skip, limit);

		// 3. 读取返回字段参数
		String returnFieldsNameList = getReturnFieldsNameList(req);
		if (!Util.isNullorEmpty(returnFieldsNameList)) {
			expression.setReturnFieldsFromString(returnFieldsNameList);
		}

		// 4. 读取排序
		String sortFieldNameList = getSortFieldNameList(req);
		if (!Util.isNullorEmpty(returnFieldsNameList)) {
			expression.setSortFieldsFromString(sortFieldNameList);
		}

		Map<String,String> transferFields = getTransferFieldsForOutput();
		Set<String> removeFields = getRemovedFieldsForOutput();
		return expression.getJSONResult(transferFields,removeFields);
	}

	protected Set<String> getRemovedFieldsForOutput() {
		return null;
	}

	protected Map<String, String> getTransferFieldsForOutput() {
		return null;
	}

	protected String doRemove(RemoveExpression expression, HttpServletRequest req) {
		// 1. 读取输入参数
		Map<String, String> inputParaMap = getInputParameters(req);
		expression.setParamValueMap(inputParaMap);
	
		return expression.getJSONResult(null,null);
	}

	protected String getExpressiontId(HttpServletRequest req) {
		return req.getParameter(KEEPS[EXP_PARAM]);
	}

	protected String getUpdateExpressionId(HttpServletRequest req) {
		return req.getParameter(KEEPS[UPDATE_EXP]);
	}

	protected String getSortFieldNameList(HttpServletRequest req) {
		return req.getParameter(KEEPS[SORT]);
	}

	protected String getReturnFieldsNameList(HttpServletRequest req) {
		return req.getParameter(KEEPS[RETURN]);
	}

	protected Boolean isUpsertUpdate(HttpServletRequest req) {
		Map<String, String[]> parameterMap = req.getParameterMap();
		if (parameterMap.containsKey(KEEPS[UPSERT])) {
			return "true".equalsIgnoreCase(req.getParameter(KEEPS[UPSERT]));
		}
		return null;
	}

	protected Boolean isMultiUpdate(HttpServletRequest req) {
		Map<String, String[]> parameterMap = req.getParameterMap();
		if (parameterMap.containsKey(KEEPS[MULTI])) {
			return "true".equalsIgnoreCase(req.getParameter(KEEPS[MULTI]));
		}
		return null;
	}

	protected String getLimit(HttpServletRequest req) {
		return req.getParameter(KEEPS[LIMIT]);
	}

	protected String getSkip(HttpServletRequest req) {
		return req.getParameter(KEEPS[SKIP]);
	}

}
