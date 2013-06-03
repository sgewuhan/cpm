package com.sg.document.workflow.validation;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.IValidationHandler;

public class ProjectPlanValidator implements IValidationHandler {

	private String message = null;
	public ProjectPlanValidator() {
	}

	@Override
	public boolean validateBeforeOpen(DBObject workData) {
		message = "";
		
		
		Object workId = workData.get(IDBConstants.FIELD_SYSID);
		DBObject doc = BusinessService.getDocumentService().getWorkDocument((ObjectId) workId, "com.sg.cpm.editor.JZ-QR-XG004A");
		if(doc==null){
			message = "您需要完成科研项目开发任务书后才能提交这个任务。";
			return false;
		}

		// P1目的意义
		Object value = doc.get("techauditor");
		if(value == null||value.toString().length()<1){
			message += "科研项目开发任务书中缺少 “指定审核的技术副主任”\n";
		}
		
		// P1目的意义
		value = doc.get("f1");
		if(value == null||value.toString().length()<1){
			message += "科研项目开发任务书中缺少 “目的意义”的描述\n";
		}
		
		// P2二、国内外研究现状与发展趋势
		value = doc.get("f2");
		if(value == null||value.toString().length()<1){
			message += "科研项目开发任务书中缺少 “国内外研究现状与发展趋势”的描述\n";
		}

		// P3三、项目实施主要内容与创新点、预期目标
		value = doc.get("f3");
		if(value == null||value.toString().length()<1){
			message += "科研项目开发任务书中缺少 “国内外研究现状与发展趋势”的描述\n";
		}
		// P4四、项目专利、情报检索情况
		value = doc.get("f4");
		if(value == null||value.toString().length()<1){
			message += "科研项目开发任务书中缺少 “项目专利、情报检索情况”的描述\n";
		}
		// P5五、项目预期成果
		value = doc.get("f5");
		if(value == null||value.toString().length()<1){
			message += "科研项目开发任务书中缺少 “项目预期成果”的描述\n";
		}
		// P6六、项目经济、社会效益分析，项目风险分析
		value = doc.get("f6");
		if(value == null||value.toString().length()<1){
			message += "科研项目开发任务书中缺少 “项目经济、社会效益分析，项目风险分析”的描述\n";
		}
		// P7七、技术关键、技术难点和主要试验内容
		value = doc.get("f7");
		if(value == null||value.toString().length()<1){
			message += "科研项目开发任务书中缺少 “技术关键、技术难点和主要试验内容”的描述\n";
		}
		// P8八、现有工作基础、条件和优势
		value = doc.get("f8");
		if(value == null||value.toString().length()<1){
			message += "科研项目开发任务书中缺少 “现有工作基础、条件和优势”的描述\n";
		}
		// P9九、项目实施方案
		value = doc.get("f9");
		if(value == null||value.toString().length()<1){
			message += "科研项目开发任务书中缺少 “项目实施方案”的描述\n";
		}
		// P10十、试验安排
		value = doc.get("f10");
		if(value == null||value.toString().length()<1){
			message += "科研项目开发任务书中缺少 “试验安排”的描述\n";
		}
		//进度计划
		value =  doc.get("projectplan");
		if(value == null||value.toString().length()<1){
			message += "科研项目开发任务书中缺少 “进度计划”的描述\n";
		}
		//经费预算
		value =  doc.get("budgetlist");
		if(value == null||value.toString().length()<1){
			message += "科研项目开发任务书中缺少 “经费预算”的描述\n";
		}
		//设备需求
		value =  doc.get("equipreq");
		if(value == null||value.toString().length()<1){
			message += "科研项目开发任务书中缺少 “设备需求”的描述\n";
		}
		
		value =  doc.get("teamdetail");
		if(value == null||value.toString().length()<1){
			message += "科研项目开发任务书中缺少 “项目团队”的描述";
		}

		return message.length()<1;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
