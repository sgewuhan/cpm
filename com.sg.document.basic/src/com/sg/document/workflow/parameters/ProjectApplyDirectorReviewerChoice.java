package com.sg.document.workflow.parameters;

import com.mongodb.BasicDBObject;
import com.sg.common.workflow.parameter.IProcessParameterDelegator;


public class ProjectApplyDirectorReviewerChoice implements IProcessParameterDelegator {

	/**
	 * 测试参数转换程序是否正常，目标是将枚举类型转换为布尔真假，传递到流程参数
	 */
	public ProjectApplyDirectorReviewerChoice() {

	}

	@Override
	public Object getValue(String processParameter, String taskDatakey, BasicDBObject taskFormData) {
		return "通过".equals((String) taskFormData.get(taskDatakey));
	}

}
