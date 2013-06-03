package com.sg.widget.editor;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.db.DBActivator;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.SingleObject;
import com.sg.widget.Widget;
import com.sg.widget.WidgetConstants;
import com.sg.widget.configuration.EditorConfiguration;
import com.sg.widget.configuration.FieldConfiguration;
import com.sg.widget.configuration.PageConfiguration;
import com.sg.widget.editor.export.IExportParameterProvider;
import com.sg.widget.editor.field.AbstractFieldPart;
import com.sg.widget.editor.saveHandler.IEditorSaveHandler;
import com.sg.widget.part.IAuthorityContextProvider;
import com.sg.widget.part.IFileExportable;
import com.sg.widget.util.ExcelExportJob;
import com.sg.widget.util.FileUtil;
import com.sg.widget.util.Util;
import com.sg.widget.util.WordExportJob;

public class SingleObjectEditor extends FormEditor implements IFileExportable,IAuthorityContextProvider{
	
	private static final String HISTORY = "history";
	private EditorConfiguration editorConf;
	private boolean activeCollectionAdaptable;

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		editorConf = ((ISingleObjectEditorInput)input).getConfig();
		setTitleAndImage((ISingleObjectEditorInput) input);
		super.init(site, input);
	}

	private void setTitleAndImage(ISingleObjectEditorInput input) {
		setPartName(editorConf.getName(input));
		setTitleToolTip(editorConf.getTitleToolTips(input));
		ImageDescriptor idesc = editorConf.getImageDescription(input);
		if (idesc != null)
			setTitleImage(idesc.createImage());
	}

	@Override
	protected void addPages() {
		List<PageConfiguration> pages = editorConf.getPages();
		Iterator<PageConfiguration> iter = pages.iterator();
		while (iter.hasNext()) {
			PageConfiguration cpage = iter.next();
			try {
				IFormPage page = new SingleObjectEditorFormPage(cpage.getId(),cpage.getTitle(),cpage);
				page.initialize(this);
				addPage(page);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		
		setStyle();
		editorDirtyStateChanged();
	}

	private void setStyle() {
		CTabFolder cont = (CTabFolder) getContainer();
		cont.setData(RWT.CUSTOM_VARIANT, "inEditor");
		CTabItem[] items = cont.getItems();
		for(CTabItem item:items){
			item.setData(RWT.CUSTOM_VARIANT, "inEditor");
		}
	}


	@Override
	public void doSave(IProgressMonitor monitor) {
		
		ISingleObjectEditorInput input = (ISingleObjectEditorInput) getEditorInput();
		if(!input.isEditable()){
			return;
		}
		
		//some control need some process before save, do it now. ex: fileControl
		commitPages(false);
		
		
		
		boolean valid = saveCheck();
		if(!valid){
			MessageDialog.openError(getSite().getShell(),WidgetConstants.EDITOR_SAVE, WidgetConstants.MESSAGE_INVALID_DATA);
			return;
		}

		//保存历史
		saveHistory(input);
		
		IEditorSaveHandler saveHandler = editorConf.getSaveHandler();
		boolean saved = false;
		if(saveHandler!=null){
			saved = saveHandler.doSave(input,monitor);
		}
		
		if(!saved){
			input.save(monitor);
		}
		
		
		
		//commitPart, make original value equals value.
		commitPages(true);
		
		setPartName(editorConf.getName(input));//刷新编辑器的名称
		editorDirtyStateChanged();
	}

	private void saveHistory(ISingleObjectEditorInput input) {
		Set<FieldConfiguration> fields = editorConf.getSaveHistoryFields();
		if(fields.isEmpty())
			return;
		ISingleObject inputData = input.getInputData();
		BasicDBList historyData = (BasicDBList) inputData.getValue(HISTORY);
		if(historyData == null){
			historyData = new BasicDBList();
		}
		Iterator<FieldConfiguration> iter = fields.iterator();
		DBObject historyItem = new BasicDBObject();
		DBObject historyFieldInfo = new BasicDBObject();
		
		while(iter.hasNext()){
			FieldConfiguration next = iter.next();
			String key = next.getName();
			String label = next.getLabel();
			Object value = inputData.getValue(key);
			historyItem.put(key, value);
			historyFieldInfo.put(key, label);
		}
		historyItem.put("lastsave", new Date());
		historyItem.put("metadata", historyFieldInfo);
		historyData.add(historyItem);

		inputData.setValue("history", historyData, null, false);
	}


	private boolean saveCheck() {
		boolean result = true;
		if (pages != null) {
			for (int i = 0; i < pages.size(); i++) {
				Object page = pages.get(i);
				if (page instanceof IFormPage) {
					IFormPage fpage = (IFormPage)page;
					IManagedForm mform = fpage.getManagedForm();
					if (mform != null && mform.isDirty()){
						//清理现有的message
						IMessageManager mm = mform.getMessageManager();
						if(mm!=null){
							mm.removeAllMessages();
						}

						IFormPart[] parts = mform.getParts();
						for(int j=0;j<parts.length;j++){
							if(parts[j] instanceof AbstractFieldPart){
								boolean valid = ((AbstractFieldPart) parts[j]).checkValidOnSave();
								if(!valid){
									result = false;
								}
							}
						}
					}
				}
			}
		}
		return result;
	}


	@Override
	public void doSaveAs() {
		// do nothing
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	public static IEditorPart OPEN(ISingleObjectEditorInput editorInput) {
		try {
			return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().openEditor(editorInput, WidgetConstants.UI_ID_SINGLEOBJECTEDITOR);
		} catch (PartInitException ee) {
			ee.printStackTrace();
		}
		return null;
	}
	
	public static IEditorPart OPEN(String editorConfigurationId,ISingleObject so) {
		EditorConfiguration conf = Widget.getSingleObjectEditorConfiguration(editorConfigurationId);
		SingleObjectEditorInput input = new SingleObjectEditorInput(conf,so);
		return OPEN(input);
	}
	
	public static IEditorPart CREATE(String editorConfigurationId) {
		EditorConfiguration conf = Widget.getSingleObjectEditorConfiguration(editorConfigurationId);
		String collectionName = conf.getCollection();
		SingleObject so = new SingleObject(DBActivator.getDefaultDBCollection(collectionName));
		SingleObjectEditorInput input = new SingleObjectEditorInput(conf,so);
		return OPEN(input);
	}

	@Override
	public boolean canExport() {
		return !Util.isNullOrEmptyString(editorConf.getExportType());
	}

	@Override
	public String getExportTemplatePath() {
		if("excel".equalsIgnoreCase(editorConf.getExportType())){
			return System.getProperty("user.dir")+"/export/template/"+editorConf.getName()+".xls" ;
		}else if("word".equalsIgnoreCase(editorConf.getExportType())){
			return System.getProperty("user.dir")+"/export/template/"+editorConf.getName()+".docx" ;
		}
		return null;
	}

	@Override
	public String getExportOutputPath() {
		if("excel".equalsIgnoreCase(editorConf.getExportType())){
			return WidgetConstants.PATH_TEMP+"/"+editorConf.getName()+".xls" ;
		}else if("word".equalsIgnoreCase(editorConf.getExportType())){
			return WidgetConstants.PATH_TEMP+"/"+editorConf.getName()+".docx" ;
		}
		return null;
	}

	@Override
	public Map<String, Object> getHeadData() {
		ISingleObjectEditorInput input = (ISingleObjectEditorInput) getEditorInput();
		ISingleObject data = input.getInputData();
		Map<String, Object> map = data.getValueMap();
		IExportParameterProvider epp = editorConf.getExportParameterProvider();
		if(epp!=null){
			return epp.getParameters(map);
		}else{
			return map;
		}
	}

	@Override
	public List<Object[]> getBodyData() {
		return null;
	}

	@Override
	public String getAuthorityContextCollectionName() {
		if(editorConf.isRecreateable()){
			return editorConf.getCollection();
		}else{
			return null;
		}
	}


	@Override
	public int getObjectType() {
		ISingleObjectEditorInput input = (ISingleObjectEditorInput) getEditorInput();
		return input.getInputData().getObjectType();
	}


	@Override
	public String getDisplayText() {
		ISingleObjectEditorInput input = (ISingleObjectEditorInput) getEditorInput();
		return input.getInputData().toString();
	}


	@Override
	public boolean activeCollectionAdaptable() {
		return activeCollectionAdaptable;
	}
	
	public void setActiveCollectionAdaptable(boolean activeCollectionAdaptable){
		this.activeCollectionAdaptable = activeCollectionAdaptable;
	}


	@Override
	public String getExportType() {

		return editorConf.getExportType();
	}



	@Override
	public void dispose() {
		//刷新数据
		super.dispose();
	}



	@Override
	public void export() {
		String exportTemplatePath = getExportTemplatePath();
		final String exportOutputPath = getExportOutputPath();
		Map<String, Object> headData = getHeadData();
		List<Object[]> bodyData = getBodyData();
		final Display display = getSite().getShell().getDisplay();
		
		if ("word".equalsIgnoreCase(getExportType())) {
			WordExportJob job = new WordExportJob(exportTemplatePath, exportOutputPath, headData);
			job.setUser(true);
			job.addJobChangeListener(new JobChangeAdapter() {

				@Override
				public void done(IJobChangeEvent event) {

					display.asyncExec(new Runnable() {

						public void run() {

							FileUtil.download(exportOutputPath);
						}
					});
				}
			});
			job.schedule();
		}else if("excel".equalsIgnoreCase(getExportType())){
			ExcelExportJob job = new ExcelExportJob(exportTemplatePath, exportOutputPath, headData, bodyData);
			job.setUser(true);
			job.addJobChangeListener(new JobChangeAdapter() {

				@Override
				public void done(IJobChangeEvent event) {

					display.asyncExec(new Runnable() {

						public void run() {

							FileUtil.download(exportOutputPath);
						}
					});
				}
			});
			job.schedule();
		}

	}

}
