package com.sg.document.workflow.parameters;

import com.mongodb.BasicDBObject;
import com.sg.common.workflow.parameter.IProcessParameterDelegator;


public class ProjectApplyDirectorReviewerChoice implements IProcessParameterDelegator {

	/**
	 * ���Բ���ת�������Ƿ�������Ŀ���ǽ�ö������ת��Ϊ������٣����ݵ����̲���
	 */
	public ProjectApplyDirectorReviewerChoice() {

	}

	@Override
	public Object getValue(String processParameter, String taskDatakey, BasicDBObject taskFormData) {
		return "ͨ��".equals((String) taskFormData.get(taskDatakey));
	}

}
