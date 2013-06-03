package com.sg.document.tmt.projectreport.action;

import org.bson.types.ObjectId;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.mongodb.BasicDBObject;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.SingleObject;
import com.sg.document.tmt.projectreport.ReportExport;
import com.sg.widget.Widget;
import com.sg.widget.configuration.EditorConfiguration;
import com.sg.widget.dialog.ISingleObjectEditorDialogCallback;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.dialog.SingleObjectEditorDialogCallback;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.SingleObjectEditorInput;

public class Report implements IWorkbenchWindowActionDelegate {

	private Shell shell;
	private static String editorId = "com.sg.document.tmt.projectreport.SelectReport";

	@Override
	public void run(IAction action) {
		BasicDBObject data = new BasicDBObject();
		SingleObject inputdata = new SingleObject().setData(data);
		EditorConfiguration ec = Widget.getSingleObjectEditorConfiguration(editorId);
		ISingleObjectEditorInput editInput = new SingleObjectEditorInput(ec,inputdata);

		ISingleObjectEditorDialogCallback callback = new SingleObjectEditorDialogCallback(){

			@Override
			public boolean saveBefore(ISingleObjectEditorInput input) {
				ISingleObject so = input.getInputData();
				ReportExport re = new ReportExport();
				re.report((String)so.getValue("year"),(String)so.getValue("month"),(ObjectId)so.getValue("dept"));
				return false;//不继续保存
			}
			
		};
		//显示对话框，选择项目的部门
		SingleObjectEditorDialog.OPEN(shell, editorId, editInput, callback, false);
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void dispose() {

	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.shell = window.getShell();
	}

}
