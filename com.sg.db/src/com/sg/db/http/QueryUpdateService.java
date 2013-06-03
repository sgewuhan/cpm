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
		// 1. �Բ�ѯ���ʽ��������

		// ��ò�ѯ���ʽ��id
		String queryExpId = req.getParameter(KEEPS[QUERY_EXP]);
		if (Util.isNullorEmpty(queryExpId)) {
			queryExpId = req.getParameter(KEEPS[EXP_PARAM]);
			if (Util.isNullorEmpty(queryExpId))
				return IServiceError.ERR_NOT_ENOUGH_PARAMETER + " " + KEEPS[QUERY_EXP];
		}

		// ��ò�ѯ���ʽ
		QueryExpression queryExp = (QueryExpression) DBActivator.getExpression(queryExpId);

		// ����������
		Map<String, String> paramValueMap = getInputParameters(req);
		// ���ѯ���ʽ�������
		queryExp.setParamValueMap(paramValueMap);

		// ��÷����ֶ�
		String returnFieldsNameList = req.getParameter(KEEPS[RETURN]);
		if (!Util.isNullorEmpty(returnFieldsNameList)) {
			// ���ѯ���ʽ���뷵���ֶ�
			queryExp.setReturnFieldsFromString(returnFieldsNameList);
		}

		// ��������ֶ�
		String sortFieldNameList = req.getParameter(KEEPS[SORT]);
		if (!Util.isNullorEmpty(returnFieldsNameList)) {
			// ���ѯ���ʽ���������ֶ�
			queryExp.setSortFieldsFromString(sortFieldNameList);
		}

		// ****************************************************
		// 2. ʹ�ò�ѯ���ʽ��������findandmodify�Ķ���

		// ʹ�ò�ѯ���ʽ���ɲ�ѯ��������
		BasicDBObject query = queryExp.loadCondition();

		// ʹ�ò�ѯ���ʽ�����ֶζ���
		BasicDBObject fields = queryExp.getReturnFields();

		// ʹ�ò�ѯ���ʽ�����������
		BasicDBObject sort = queryExp.getSortFields();

		// ****************************************************
		// 3. ��ø��±��ʽ��id
		String updateExpId = req.getParameter(KEEPS[UPDATE_EXP]);
		if (Util.isNullorEmpty(updateExpId)) {
			return IServiceError.ERR_NOT_ENOUGH_PARAMETER + " " + KEEPS[UPDATE_EXP];
		}
		// ��ø��±��ʽ
		InsertExpression updateExp = (InsertExpression) DBActivator.getExpression(updateExpId);
		// ����±��ʽ�������
		updateExp.setParamValueMap(paramValueMap);
		
		// ��ø��±��ʽ����
		BasicDBObject update = updateExp.loadCondition();

		// �����Ҫ���µ�collection
		DBCollection collection = updateExp.getCollection();

		// ****************************************************
		// 4. ���findandmodify����������
		boolean remove = "true".equalsIgnoreCase(req.getParameter(KEEPS[REMOVE]));

		boolean returnNew = true;//"true".equalsIgnoreCase(req.getParameter(KEEPS[RETURN_NEW]));

		boolean upsert = "true".equalsIgnoreCase(req.getParameter(KEEPS[UPSERT]));

		// ****************************************************
		// 5. ���и���
		DBObject result = collection.findAndModify(query, fields, sort, remove, update, returnNew, upsert);

		// ****************************************************
		// 6.�Է��ؽ�����д���
		// ��Щ��������޸�date���͵ȵȣ�ObjectId�ȵ�
		return JSON.serialize(Util.translateBSON(result));
	}

}
