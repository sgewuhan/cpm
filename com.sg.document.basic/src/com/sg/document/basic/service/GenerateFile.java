package com.sg.document.basic.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.bpm.service.task.ServiceProvider;
import com.sg.common.BusinessService;
import com.sg.widget.Widget;
import com.sg.widget.WidgetConstants;
import com.sg.widget.configuration.EditorConfiguration;
import com.sg.widget.editor.export.IExportParameterProvider;
import com.sg.widget.util.FileUtil;
import com.sg.widget.util.WordExportor;

public class GenerateFile extends ServiceProvider  {

	public GenerateFile() {
	}

	@Override
	public Map<String, Object> run(Object parameter) {
		String fieldName = getOperation();

		
		Object _workId = getInputValue("workId");
		if(_workId==null) return null;
		ObjectId workId = new ObjectId((String)_workId);

		Object editorId = getInputValue("editorId");
		if(editorId == null) return null;
		Object namespace = getInputValue("namespace");
		if(namespace == null){
			namespace = fieldName;
		}
		List<DBObject> docs = BusinessService.getWorkService().getDocumentOfWork(workId, (String) editorId);
		if(docs.size()<1) return null;
		
		EditorConfiguration editorConf = Widget.getSingleObjectEditorConfiguration((String) editorId);
		if(editorConf == null) return null;
		String exportTemplatePath = getExportTemplatePath(editorConf);
		final String exportOutputPath = getExportOutputPath(editorConf);
		DBObject document = docs.get(0);
		Map<String, Object> headData = getHeadData(editorConf,document);
		
		if ("word".equalsIgnoreCase(editorConf.getExportType())) {
			WordExportor ex = new WordExportor();
			try {
				ex.doExport(exportTemplatePath, exportOutputPath, headData);
				
				File file = new File(exportOutputPath);
				if(file.isFile()){
					FileInputStream in = new FileInputStream(file);
					String fileName = file.getName();
					ObjectId fid = FileUtil.upload(in, fileName, (String) namespace);
					in.close();
					
					if(fieldName == null){
						fieldName = "attachment";
					}
					
					BasicDBList fileList = (BasicDBList) document.get(fieldName);
					if(fileList==null){
						fileList = new BasicDBList();
					}
					
					if(fileList.size()==0){
						DBObject fileRec = new BasicDBObject();
						fileRec.put("_id", fid);
						fileRec.put("fileName", fileName);
						fileRec.put("namespace", namespace);
						
						fileList.add(fileRec);
					}else{
						boolean updateFile = false;
						for(int i=0;i<fileList.size();i++){
							DBObject fileRec = (DBObject) fileList.get(i);
							if(fileName.equals(fileRec.get("fileName"))){
								fileRec.put("_id", fid);
								updateFile = true;
								break;
							}
						}
						if(!updateFile){
							DBObject fileRec = new BasicDBObject();
							fileRec.put("_id", fid);
							fileRec.put("fileName", fileName);
							fileRec.put("namespace", namespace);
							fileList.add(fileRec);
						}
					}
					
					document.put(fieldName, fileList);
					BusinessService.getDocumentService().saveDocument(document);
					
				}
			} catch (Exception e) {
			}
		}
		
		
		return null;
	}

	private Map<String, Object> getHeadData(EditorConfiguration editorConf, DBObject doc) {
		Map<String, Object> map = doc.toMap();
		IExportParameterProvider epp = editorConf.getExportParameterProvider();
		if(epp!=null){
			return epp.getParameters(map);
		}else{
			return map;
		}
	}

	public String getExportTemplatePath(EditorConfiguration editorConf) {
		if("excel".equalsIgnoreCase(editorConf.getExportType())){
			return System.getProperty("user.dir")+"/export/template/"+editorConf.getName()+".xls" ;
		}else if("word".equalsIgnoreCase(editorConf.getExportType())){
			return System.getProperty("user.dir")+"/export/template/"+editorConf.getName()+".docx" ;
		}
		return null;
	}
	
	public String getExportOutputPath(EditorConfiguration editorConf) {
		if("excel".equalsIgnoreCase(editorConf.getExportType())){
			return WidgetConstants.PATH_TEMP+"/"+editorConf.getName()+".xls" ;
		}else if("word".equalsIgnoreCase(editorConf.getExportType())){
			return WidgetConstants.PATH_TEMP+"/"+editorConf.getName()+".docx" ;
		}
		return null;
	}
}
