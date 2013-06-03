package com.sg.common.ui;

import java.util.List;

import org.bson.types.ObjectId;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.DBActivator;
import com.sg.db.Util;
import com.sg.db.model.CascadeObject;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.IValueChangeListener;
import com.sg.widget.configuration.PageConfiguration;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.editor.IPageDelegator;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.SingleObjectEditorInput;
import com.sg.widget.viewer.treeviewer.QueryTreeViewer;

public class ProjectDocumentPage implements IPageDelegator, IValueChangeListener, IFormPart, IDoubleClickListener {

	private ObjectId projectId;
	private CascadeObject exp;
	private QueryTreeViewer viewer;
	private ISingleObjectEditorInput input;
	private boolean created;

	public ProjectDocumentPage() {
	}

	@Override
	public Composite createPageContent(Composite parent,
			ISingleObjectEditorInput input, PageConfiguration conf) {
		this.input = input;
		input.getInputData().addValueListener(this);
		DBObject project = (DBObject) input.getInputData().getValue("project");
		if(project!=null){
			projectId = (ObjectId) project.get(IDBConstants.FIELD_SYSID);
		}
		
		exp = DBActivator.getCascadeObject(IDBConstants.EXP_CASCADE_WBS_WITH_DOC);
		
		
		Composite panel = new Composite(parent,SWT.NONE);
		
		panel.setLayout(new FormLayout());

		Button checkButton = new Button(panel,SWT.PUSH);
		checkButton.setText("�����Լ��");
		checkButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkProject();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		FormData fd = new FormData();
		checkButton.setLayoutData(fd);
		fd.top = new FormAttachment(0,0);
		fd.left = new FormAttachment(0,0);
		
		viewer = new QueryTreeViewer(panel, SWT.FULL_SELECTION|SWT.VIRTUAL, "com.sg.cpm.project.wbs.forcheck");
		viewer.setAutoExpandLevel(-1);
		loadProject();
		viewer.addDoubleClickListener(this);
		
		Control control = viewer.getControl();
		fd = new FormData();
		control.setLayoutData(fd);
		fd.top = new FormAttachment(checkButton,4);
		fd.left = new FormAttachment(0,0);
		fd.right = new FormAttachment(100,0);
		fd.bottom = new FormAttachment(100,0);
		created = true;
		return panel;
	}

	protected void checkProject() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		List<Object[]> unCompleteness = BusinessService.getWorkService().completenessCheck(projectId);
		if(unCompleteness.size()>0){
			String message = "���ύ�������Ŀ�ĵ�������������Ҫ���������µ��ļ��������ύ������Ҳ������ʱ����Ŀ�����к˶��Ƿ�����������Ҫ��";
			for(int i=0;i<unCompleteness.size();i++){
				DBObject doc = (DBObject) unCompleteness.get(i)[0];
				String name = (String) doc.get(IDBConstants.FIELD_DESC);
				String reason = (String) unCompleteness.get(i)[1];
				message = message +"\n"+"�ĵ���"+name+" ���⣺"+reason;
			}
			MessageDialog.openWarning(shell, "��Ŀ�ĵ������Լ��", message);
			return;
		}
		MessageDialog.openInformation(shell, "��Ŀ�ĵ������Լ��", "��Ŀ���������Լ���Ҫ��");
	}

	private void loadProject() {
		exp.setParamValue(IDBConstants.FIELD_SYSID, projectId);
		exp.rootReload();
		viewer.setInput(exp);
	}

	@Override
	public IFormPart getFormPart() {
		return this;
	}

	@Override
	public void valueChanged(String key, Object oldValue, Object newValue) {
		if(key.equals("project")){
			DBObject project = (DBObject) newValue;
			if(project!=null){
				ObjectId _projectId = (ObjectId) project.get(IDBConstants.FIELD_SYSID);
				if(!Util.equals(projectId, _projectId)){
					projectId = _projectId;
					if(created){
						loadProject();
					}
				}
			}
		}
	}

	@Override
	public void initialize(IManagedForm form) {
	}

	@Override
	public void dispose() {
		input.getInputData().removeValueListener(this);
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void commit(boolean onSave) {
		
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}

	@Override
	public void setFocus() {
		
	}

	@Override
	public boolean isStale() {
		return false;
	}

	@Override
	public void refresh() {
		
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		IStructuredSelection iSul = (IStructuredSelection) event.getSelection();
		if(iSul.isEmpty()){
			return;
		}
		ISingleObject so = (ISingleObject) iSul.getFirstElement();
		if(!DataUtil.isDocumentObject(so)){
			return;
		}
		Object editorId = so.getValue(IDBConstants.FIELD_SYSTEM_EDITOR);
		if (editorId != null) {
			ISingleObjectEditorInput editInput = new SingleObjectEditorInput(so);
			SingleObjectEditorDialog.OPEN(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), (String) editorId, editInput);
		}
	}

}
