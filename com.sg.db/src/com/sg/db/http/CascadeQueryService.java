package com.sg.db.http;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sg.db.DBActivator;
import com.sg.db.IServiceError;
import com.sg.db.Util;
import com.sg.db.model.CascadeObject;

public class CascadeQueryService extends BasicDBService{

	@Override
	protected String getJSONResponse(HttpServletRequest req,HttpServletResponse resp){

		String expId = getExpressiontId(req);
		CascadeObject expression = DBActivator.getCascadeObject(expId);
		if (expression != null) {
			if (expression instanceof CascadeObject) {
				return doQuery(expression,req);
			}
		}
		return IServiceError.ERR_INVALID_EXPRESSION;
	}
	
	protected String getExpressiontId(HttpServletRequest req) {
		return req.getParameter(KEEPS[EXP_PARAM]);
	}

	protected String doQuery(CascadeObject expression, HttpServletRequest req) {

		
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
		String sortFieldNameList =  getSortFieldNameList(req);
		if (!Util.isNullorEmpty(sortFieldNameList)) {
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
	
	protected String getSortFieldNameList(HttpServletRequest req) {
		return req.getParameter(KEEPS[SORT]);
	}

	protected String getReturnFieldsNameList(HttpServletRequest req) {
		return req.getParameter(KEEPS[RETURN]);
	}
	
	protected String getLimit(HttpServletRequest req) {
		return req.getParameter(KEEPS[LIMIT]);
	}

	protected String getSkip(HttpServletRequest req) {
		return req.getParameter(KEEPS[SKIP]);
	}

}
