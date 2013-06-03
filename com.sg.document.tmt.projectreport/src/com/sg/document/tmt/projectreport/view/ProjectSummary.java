package com.sg.document.tmt.projectreport.view;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.OrganizationService;
import com.sg.common.service.WorkService;
import com.sg.db.DBActivator;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.SingleObject;
import com.sg.document.tmt.projectreport.ReportExport;
import com.sg.resource.Resource;
import com.sg.user.AuthorityResponse;
import com.sg.user.UserSessionContext;
import com.sg.widget.Widget;
import com.sg.widget.configuration.EditorConfiguration;
import com.sg.widget.dialog.ISingleObjectEditorDialogCallback;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.dialog.SingleObjectEditorDialogCallback;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.SingleObjectEditorInput;
import com.sg.widget.util.FileUtil;
import com.sg.widget.util.Util;

public class ProjectSummary extends ViewPart {

	private static final String SELECTOR = "com.sg.document.tmt.projectreport.SelectReport";
	public static final int PROJECT = 0;
	public static final int DOCUMENT = 1;
	private static final String editorId = "com.sg.cpm.editor.projectmonthreport";

	public class ProjectViewerContentProvider implements ITreeContentProvider {


		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof List) {
				return ((List) inputElement).toArray();
			}
			return null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {

			int type = getElementType(parentElement);

			if (type == PROJECT) {
				ObjectId projectId = ((SingleObject) parentElement)
						.getSystemId();
				List<DBObject> documents = workService.getProjectDocuments(
						projectId, editorId,new BasicDBObject().append("year", 1).append("month", 1));// �±�
				SingleObject[] children = new SingleObject[documents.size()];
				for (int i = 0; i < documents.size(); i++) {
					children[i] = new SingleObject(documentCollection,
							documents.get(i));
					children[i].setValue("_parent",
							(SingleObject) parentElement);
				}
				return children;
			} else if (type == DOCUMENT) {
				return null;
			} else {
				return null;
			}

		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			int type = getElementType(element);
			if (type == PROJECT) {
				return true;
			}
			return false;
		}

	}

	public class DeptLabelProvider extends LabelProvider {

		@Override
		public String getText(Object element) {
			if (element instanceof DBObject) {
				return (String) ((DBObject) element)
						.get(IDBConstants.FIELD_DESC);
			}
			return super.getText(element);
		}

	}

	// private static final String[] monthItem = new String[] { "һ��", "����",
	// "����",
	// "����", "����", "����", "����", "����", "����", "ʮ��", "ʮһ��", "ʮ����" };
	private WorkService workService;
	private TreeViewer projectViewer;
	private DBCollection projectCollection;
	private DBCollection documentCollection;
	private OrganizationService orgService;

	public ProjectSummary() {
	}

	public int getElementType(Object element) {
		if (element instanceof SingleObject) {
			String collectionName = ((SingleObject) element).getCollection()
					.getName();
			if (IDBConstants.COLLECTION_PROJECT
					.equalsIgnoreCase(collectionName)) {// ��Ŀ
				return PROJECT;
			} else if (IDBConstants.COLLECTION_DOCUMENT
					.equalsIgnoreCase(collectionName)) {// �ĵ�
				return DOCUMENT;
			}
		}
		return -1;
	}

	@Override
	public void createPartControl(Composite parent) {
		workService = BusinessService.getWorkService();
		orgService = BusinessService.getOrganizationService();
		projectCollection = DBActivator
				.getDefaultDBCollection(IDBConstants.COLLECTION_PROJECT);
		documentCollection = DBActivator
				.getDefaultDBCollection(IDBConstants.COLLECTION_DOCUMENT);

		// ����һ��ѡ��
		parent.setLayout(new FormLayout());

		Composite toolbar = createToolbar(parent);
		FormData fd = new FormData();
		toolbar.setLayoutData(fd);
		fd.top = new FormAttachment(0, 0);
		fd.height = 36;
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);

		// ����һ�������ʾ
		createProjectViewer(parent);

		fd = new FormData();
		projectViewer.getTree().setLayoutData(fd);
		fd.top = new FormAttachment(toolbar, 2);
		fd.bottom = new FormAttachment(100, 0);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);

	}

	private TreeViewer createProjectViewer(Composite parent) {
		projectViewer = new TreeViewer(parent, SWT.FULL_SELECTION);
		projectViewer.getTree().setHeaderVisible(true);
		projectViewer.getTree().setLinesVisible(true);
		
		projectViewer.getTree().setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		projectViewer.getTree().setData(RWT.CUSTOM_ITEM_HEIGHT,
				Integer.valueOf(88));
		// 1.��Ŀ��� 2.��Ŀ���� 3.�ƻ���ʼ/�ƻ����/ʵ�ʿ�ʼ/ʵ����� 4.��Ŀ��ʱ 5.��ĿԤ�� 6.��Ŀ������ 7.����
		// 1.�� 2.�������·� 3.���¹����ܽ� 4.ʱ����ɰٷֱ� 5.������������/������ɰٷֱȣ� . 6��Ŀ���� 7.���� 8.��ϯʦ

		TreeViewerColumn col = new TreeViewerColumn(projectViewer, SWT.NONE);
		col.getColumn().setText("��Ŀ");
		col.getColumn().setWidth(380);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				// ��ʾ��Ŀ���ƣ����������
				SingleObject so = (SingleObject) element;
				int type = getElementType(so);
				if (type == PROJECT) {
					Date dPlanstart = (Date) so
							.getValue(IDBConstants.FIELD_PROJECT_PLANSTART);
					Date dPlanfinish = (Date) so
							.getValue(IDBConstants.FIELD_PROJECT_PLANFINISH);
					Date dActualstart = (Date) so
							.getValue(IDBConstants.FIELD_PROJECT_ACTUALSTART);
					Date dActualfinish = (Date) so
							.getValue(IDBConstants.FIELD_PROJECT_ACTUALFINISH);
					SimpleDateFormat sdf = Util
							.getDateFormat(Util.SDF_YY_MM_DD);
					String planstart = dPlanstart == null ? "" : sdf
							.format(dPlanstart);
					String planfinish = dPlanfinish == null ? "" : sdf
							.format(dPlanfinish);
					String actualstart = dActualstart == null ? "" : sdf
							.format(dActualstart);
					String actualfinish = dActualfinish == null ? "" : sdf
							.format(dActualfinish);
					DBObject pm = (DBObject) so
							.getValue(IDBConstants.FIELD_PROJECT_PM);
					ObjectId obsparent = (ObjectId) so
							.getValue(IDBConstants.FIELD_OBSPARENT);
					DBObject team = orgService.getOBSItemData(obsparent);

					
					StringBuffer sb = new StringBuffer();

					sb.append("<b>");
					sb.append(so.getText(IDBConstants.FIELD_ID));
					sb.append("<img src='" + DataUtil.getProcessStatusImageURL(so)+ "' width='12' height='12' style='padding-right:4px;padding-top:4px;'/>");
					sb.append(DataUtil.getProcessStatus(so));
					sb.append("<br/>");
					sb.append(so.getText(IDBConstants.FIELD_DESC));
					sb.append("</b>");
					
					sb.append("<br/><small><i>");

					sb.append("�ƻ���" + planstart + "~" + planfinish + "  ʵ�ʣ�"
							+ actualstart + "~" + actualfinish);

					sb.append("<br/>");
					sb.append(pm.get("name") + "/" + pm.get("desc"));
					sb.append("  "+team.get("desc"));

					sb.append("</i></small>");

					return sb.toString();
				} else {
					String year = so.getText("year");
					String month = so.getText("month");
					String progressStatus = so.getText("progressStatus");
					
					StringBuffer sb = new StringBuffer();
					
					sb.append("<b>");
					sb.append(progressStatus+"  ");
					sb.append(year + "-" + month);
					sb.append("<br/></b>");
					sb.append(so.getText("workSummary"));

					return sb.toString();
				}

			}
		});

		col = new TreeViewerColumn(projectViewer, SWT.NONE);
		col.getColumn().setWidth(100);
		col.getColumn().setText("���ڣ��죩");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				// ��Ŀ��ʱ
				// ʱ����ɰٷֱ�
				SingleObject so = (SingleObject) element;
				int type = getElementType(element);
				if (type == PROJECT) {
					Date dPlanstart = (Date) so
							.getValue(IDBConstants.FIELD_PROJECT_PLANSTART);
					Date dPlanfinish = (Date) so
							.getValue(IDBConstants.FIELD_PROJECT_PLANFINISH);
					Date dActualstart = (Date) so
							.getValue(IDBConstants.FIELD_PROJECT_ACTUALSTART);
					Date dActualfinish = (Date) so
							.getValue(IDBConstants.FIELD_PROJECT_ACTUALFINISH);
					Date now = new Date();
					if (dActualfinish == null) {
						dActualfinish = now;
					}
					// �ƻ�����
					double planTime = (dPlanfinish.getTime() - dPlanstart
							.getTime()) / (1000d * 60 * 60 * 24);
					// ʵ�ʹ���/�Ѿ������Ĺ���
					double actualTime = (dActualfinish.getTime() - dActualstart
							.getTime()) / (1000d * 60 * 60 * 24);

					DecimalFormat df = Util.getDecimalFormat(Util.NUMBER_P2);
					DecimalFormat df1 = Util
							.getDecimalFormat(Util.NUMBER_P2_PERC);
					return "�ƻ���" + df.format(planTime) + "<br/>ʵ�ʣ�"
							+ df.format(actualTime) + "<br/>��ɣ�"
							+ df1.format(actualTime / planTime);
				} else {
					String progressStatus = so.getText("progressStatus");
					StringBuffer sb = new StringBuffer();
					if("��������".equals(progressStatus)){
						sb.append("<img src='");
						sb.append(FileUtil.getImageLocationFromInputStream(Resource.PRG_NORMAL, Resource.getDefault().getImageInputStream(Resource.PRG_NORMAL)));
						sb.append("' width='48' height='48' style='padding-left:0px;padding-top:2px;'/>");
						sb.append("<span  style=\"color:#669900\">");
					}else if("������ǰ".equals(progressStatus)){
						sb.append("<img src='");
						sb.append(FileUtil.getImageLocationFromInputStream(Resource.PRG_EARLY, Resource.getDefault().getImageInputStream(Resource.PRG_EARLY)));
						sb.append("' width='48' height='48' style='padding-left:0px;padding-top:2px;'/>");
						sb.append("<span  style=\"color:#33B5E5\">");
					}else if("�����ͺ�".equals(progressStatus)){
						sb.append("<img src='");
						sb.append(FileUtil.getImageLocationFromInputStream(Resource.PRG_LATE, Resource.getDefault().getImageInputStream(Resource.PRG_LATE)));
						sb.append("' width='48' height='48' style='padding-left:0px;padding-top:2px;'/>");
						sb.append("<span  style=\"color:#FFBB33\">");
					}else if("���س���".equals(progressStatus)){
						sb.append("<img src='");
						sb.append(FileUtil.getImageLocationFromInputStream(Resource.PRG_VLATE, Resource.getDefault().getImageInputStream(Resource.PRG_VLATE)));
						sb.append("' width='48' height='48' style='padding-left:0px;padding-top:2px;'/>");
						sb.append("<span  style=\"color:#FF4444\">");
					}else{
						sb.append("<img src='");
						sb.append(FileUtil.getImageLocationFromInputStream(Resource.PRG_NORMAL, Resource.getDefault().getImageInputStream(Resource.PRG_NORMAL)));
						sb.append("' width='48' height='48' style='padding-left:0px;padding-top:2px;'/>");
						sb.append("<span  style=\"color:black\">");
					}
					sb.append("<br/>"+progressStatus+"  ");
					sb.append("</span>");

					return sb.toString();
				}
			}
		});

		col = new TreeViewerColumn(projectViewer, SWT.NONE);
		col.getColumn().setWidth(140);
		col.getColumn().setText("Ԥ��\nʵ�ʣ���Ԫ��");

		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				// ��ĿԤ��
				// ������������/������ɰٷֱ�
				Double budget = null;
				DecimalFormat df = Util.getDecimalFormat(Util.NUMBER_P2);
				int type = getElementType(element);
				if (type == PROJECT) {
					budget = (Double) ((SingleObject) element)
							.getValue(IDBConstants.FIELD_BUDGET);
					return "Ԥ�㣺"
							+ df.format(budget == null ? 0 : budget
									.doubleValue());
				} else {
					budget = (Double) ((SingleObject) ((SingleObject) element)
							.getValue("_parent"))
							.getValue(IDBConstants.FIELD_BUDGET);
					budget = budget == null ? 0 : budget.doubleValue();
					Double actual = (Double) ((SingleObject) element)
							.getValue("costFinishedMonth");
					actual = actual == null ? 0 : actual.doubleValue();
					return "����������"
							+ df.format(actual)
							+ "<br/>�ٷֱȣ�"
							+ Util.getDecimalFormat(Util.NUMBER_P2_PERC)
									.format(actual / budget);
				}
			}
		});

		col = new TreeViewerColumn(projectViewer, SWT.NONE);
		final TreeColumn column1 = col.getColumn();
		column1.setWidth(200);
		column1.setText("�ƻ�ƫ��");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				// ��Ŀ������
				// ��Ŀ����
				int type = getElementType(element);
				if (type == PROJECT) {
					return "";
				} else {
					Object planSummary = ((SingleObject) element)
							.getValue("planSummary");

					StringBuffer sb = new StringBuffer();
					sb.append("<span style=\" word-break:normal; width:" + (column1.getWidth()-16)
							+ "; display:block; white-space:pre-wrap;word-wrap : break-word ;overflow: hidden ;\">");
					sb.append(planSummary == null ? "" : (planSummary.toString()));
					sb.append("</span>");
					return sb.toString();
				}
			}
		});
		column1.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				projectViewer.refresh(true);
			}
		});

		col = new TreeViewerColumn(projectViewer, SWT.NONE);
		final TreeColumn column2 = col.getColumn();
		column2.setWidth(200);
		column2.setText("���շ���");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				// ����
				// ����
				int type = getElementType(element);
				if (type == PROJECT) {
					return "";
				} else {
					Object riskSummary = ((SingleObject) element)
							.getValue("riskSummary");
					
					StringBuffer sb = new StringBuffer();
					sb.append("<span style=\" word-break:normal; width:" + (column2.getWidth()-16)
							+ "; display:block; white-space:pre-wrap;word-wrap : break-word ;overflow: hidden ;\">");
					sb.append(riskSummary == null ? "" : (riskSummary.toString()));
					sb.append("</span>");
					return sb.toString();
				}
			}
		});
		column2.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				projectViewer.refresh(true);
			}
		});

		col = new TreeViewerColumn(projectViewer, SWT.NONE);
		final TreeColumn column3 = col.getColumn();
		column3.setWidth(200);
		column3.setText("��ϯʦ����");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				// ��
				// ��ϯʦ����
				int type = getElementType(element);
				if (type == PROJECT) {
					return "";
				} else {
					Object comment = ((SingleObject) element)
							.getValue("comment");
					
					StringBuffer sb = new StringBuffer();
					sb.append("<span style=\" word-break:normal; width:" + (column3.getWidth()-16)
							+ "; display:block; white-space:pre-wrap;word-wrap : break-word ;overflow: hidden ;\">");
					sb.append(comment == null ? "" : (comment.toString()));
					sb.append("</span>");
					return sb.toString();
				}
			}
		});
		column3.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				projectViewer.refresh(true);
			}
		});

		projectViewer.setContentProvider(new ProjectViewerContentProvider());
		return projectViewer;
	}


	private Composite createToolbar(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new FormLayout());

		// ����ѡ��
		final ComboViewer deptSelector = new ComboViewer(panel, SWT.READ_ONLY);
		deptSelector.setLabelProvider(new DeptLabelProvider());
		deptSelector.setContentProvider(ArrayContentProvider.getInstance());
		List<DBObject> deptInput = getDeptInput();
		deptSelector.setInput(deptInput);
		FormData fd = new FormData();
		deptSelector.getCombo().setLayoutData(fd);
		fd.top = new FormAttachment(0, 2);
		fd.left = new FormAttachment(0, 2);
		fd.height = 32;

		// ȷ��
		Button query = new Button(panel, SWT.PUSH);
		query.setText("��ѯ");
		fd = new FormData();
		query.setLayoutData(fd);
		fd.top = new FormAttachment(0, 2);
		fd.left = new FormAttachment(deptSelector.getCombo(), 2);
		fd.height = 32;
		query.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) deptSelector
						.getSelection();
				if (sel == null || sel.isEmpty())
					return;
				queryProject((DBObject) sel.getFirstElement());
			}

		});

		Button export = new Button(panel, SWT.PUSH);
		export.setText("�±���");
		fd = new FormData();
		export.setLayoutData(fd);
		fd.top = new FormAttachment(0, 2);
		fd.left = new FormAttachment(query, 2);
		fd.height = 32;
		export.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				reportProject();
			}
		});

		Button open = new Button(panel, SWT.PUSH);
		open.setText("��");
		fd = new FormData();
		open.setLayoutData(fd);
		fd.top = new FormAttachment(0, 2);
		fd.left = new FormAttachment(export, 2);
		fd.height = 32;
		open.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) projectViewer
						.getSelection();
				if (sel == null || sel.isEmpty())
					return;
				open((ISingleObject)sel.getFirstElement());
			}

		});
		
		Button expand = new Button(panel, SWT.PUSH);
		expand.setText("չ��");
		fd = new FormData();
		expand.setLayoutData(fd);
		fd.top = new FormAttachment(0, 2);
		fd.left = new FormAttachment(open, 2);
		fd.height = 32;
		expand.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				projectViewer.expandAll();
			}

		});

		
		Button cpse = new Button(panel, SWT.PUSH);
		cpse.setText("�۵�");
		fd = new FormData();
		cpse.setLayoutData(fd);
		fd.top = new FormAttachment(0, 2);
		fd.left = new FormAttachment(expand, 2);
		fd.height = 32;
		cpse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				projectViewer.collapseAll();
			}

		});
		return panel;
	}

	protected void open(ISingleObject iso) {
		ISingleObjectEditorDialogCallback callback = new SingleObjectEditorDialogCallback(){
			@Override
			public boolean saveBefore(ISingleObjectEditorInput input) {
				return false;//����������
			}
		};

		int type = getElementType(iso);
		
		if(type == PROJECT){
			EditorConfiguration ec = Widget.getSingleObjectEditorConfiguration("com.sg.cpm.editor.project.edit");
			ISingleObjectEditorInput editInput = new SingleObjectEditorInput(ec,iso);
			editInput.setEditable(false);
			SingleObjectEditorDialog.OPEN(getSite().getShell(), "com.sg.cpm.editor.project.edit", editInput, callback, false);
		}else{
			EditorConfiguration ec = Widget.getSingleObjectEditorConfiguration(editorId);
			ISingleObjectEditorInput editInput = new SingleObjectEditorInput(ec,iso);
			SingleObjectEditorDialog.OPEN(getSite().getShell(), editorId, editInput, callback, false);
		}

	}

	protected void reportProject() {
		BasicDBObject data = new BasicDBObject();
		SingleObject inputdata = new SingleObject().setData(data);
		EditorConfiguration ec = Widget.getSingleObjectEditorConfiguration(SELECTOR);
		ISingleObjectEditorInput editInput = new SingleObjectEditorInput(ec,inputdata);

		ISingleObjectEditorDialogCallback callback = new SingleObjectEditorDialogCallback(){

			@Override
			public boolean saveBefore(ISingleObjectEditorInput input) {
				ISingleObject so = input.getInputData();
				ReportExport re = new ReportExport();
				re.report((String)so.getValue("year"),(String)so.getValue("month"),(ObjectId)so.getValue("dept"));
				return false;//����������
			}
			
		};
		//��ʾ�Ի���ѡ����Ŀ�Ĳ���
		SingleObjectEditorDialog.OPEN(getSite().getShell(), SELECTOR, editInput, callback, false);
		
	}

	protected void queryProject(DBObject dept) {
		List<DBObject> projectList = workService.getProjectOfOrganization(
				(ObjectId) dept.get(IDBConstants.FIELD_SYSID), true);
		ArrayList<SingleObject> input = new ArrayList<SingleObject>();
		for (int i = 0; i < projectList.size(); i++) {
			input.add(new SingleObject(projectCollection, projectList.get(i)));
		}
		projectViewer.setInput(input);
	}

	private List<DBObject> getDeptInput() {
		AuthorityResponse auth = new AuthorityResponse();
		boolean hasAuthority = UserSessionContext.hasTokenAuthority(
				UserSessionContext.TOKEN_ORG_PROJECT_ADMIN, auth);
		if (hasAuthority) {

			if (auth != null) {
				BasicDBList teamList = auth.getContextList();
				DBCollection obsCollection = DBActivator
						.getDefaultDBCollection(IDBConstants.COLLECTION_ORG);
				DBCursor cur = obsCollection.find(new BasicDBObject().append(
						IDBConstants.FIELD_SYSID,
						new BasicDBObject().append("$in", teamList)),
						new BasicDBObject().append(IDBConstants.FIELD_SYSID, 1)
								.append(IDBConstants.FIELD_DESC, 1));

				return cur.toArray();
			}
		}
		return new ArrayList<DBObject>();
	}

	@Override
	public void setFocus() {

	}

}
