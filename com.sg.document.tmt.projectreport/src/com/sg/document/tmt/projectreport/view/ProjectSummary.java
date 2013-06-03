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
						projectId, editorId,new BasicDBObject().append("year", 1).append("month", 1));// 月报
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

	// private static final String[] monthItem = new String[] { "一月", "二月",
	// "三月",
	// "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月" };
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
					.equalsIgnoreCase(collectionName)) {// 项目
				return PROJECT;
			} else if (IDBConstants.COLLECTION_DOCUMENT
					.equalsIgnoreCase(collectionName)) {// 文档
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

		// 创建一个选择
		parent.setLayout(new FormLayout());

		Composite toolbar = createToolbar(parent);
		FormData fd = new FormData();
		toolbar.setLayoutData(fd);
		fd.top = new FormAttachment(0, 0);
		fd.height = 36;
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);

		// 创建一个表格显示
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
		// 1.项目编号 2.项目名称 3.计划开始/计划完成/实际开始/实际完成 4.项目历时 5.项目预算 6.项目负责人 7.部门
		// 1.空 2.报告年月份 3.本月工作总结 4.时间完成百分比 5.当月新增费用/费用完成百分比， . 6项目差异 7.风险 8.首席师

		TreeViewerColumn col = new TreeViewerColumn(projectViewer, SWT.NONE);
		col.getColumn().setText("项目");
		col.getColumn().setWidth(380);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				// 显示项目名称，报告的年月
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

					sb.append("计划：" + planstart + "~" + planfinish + "  实际："
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
		col.getColumn().setText("工期（天）");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				// 项目历时
				// 时间完成百分比
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
					// 计划工期
					double planTime = (dPlanfinish.getTime() - dPlanstart
							.getTime()) / (1000d * 60 * 60 * 24);
					// 实际工期/已经发生的工期
					double actualTime = (dActualfinish.getTime() - dActualstart
							.getTime()) / (1000d * 60 * 60 * 24);

					DecimalFormat df = Util.getDecimalFormat(Util.NUMBER_P2);
					DecimalFormat df1 = Util
							.getDecimalFormat(Util.NUMBER_P2_PERC);
					return "计划：" + df.format(planTime) + "<br/>实际："
							+ df.format(actualTime) + "<br/>完成："
							+ df1.format(actualTime / planTime);
				} else {
					String progressStatus = so.getText("progressStatus");
					StringBuffer sb = new StringBuffer();
					if("正常进行".equals(progressStatus)){
						sb.append("<img src='");
						sb.append(FileUtil.getImageLocationFromInputStream(Resource.PRG_NORMAL, Resource.getDefault().getImageInputStream(Resource.PRG_NORMAL)));
						sb.append("' width='48' height='48' style='padding-left:0px;padding-top:2px;'/>");
						sb.append("<span  style=\"color:#669900\">");
					}else if("进度提前".equals(progressStatus)){
						sb.append("<img src='");
						sb.append(FileUtil.getImageLocationFromInputStream(Resource.PRG_EARLY, Resource.getDefault().getImageInputStream(Resource.PRG_EARLY)));
						sb.append("' width='48' height='48' style='padding-left:0px;padding-top:2px;'/>");
						sb.append("<span  style=\"color:#33B5E5\">");
					}else if("进度滞后".equals(progressStatus)){
						sb.append("<img src='");
						sb.append(FileUtil.getImageLocationFromInputStream(Resource.PRG_LATE, Resource.getDefault().getImageInputStream(Resource.PRG_LATE)));
						sb.append("' width='48' height='48' style='padding-left:0px;padding-top:2px;'/>");
						sb.append("<span  style=\"color:#FFBB33\">");
					}else if("严重超期".equals(progressStatus)){
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
		col.getColumn().setText("预算\n实际（万元）");

		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				// 项目预算
				// 当月新增费用/费用完成百分比
				Double budget = null;
				DecimalFormat df = Util.getDecimalFormat(Util.NUMBER_P2);
				int type = getElementType(element);
				if (type == PROJECT) {
					budget = (Double) ((SingleObject) element)
							.getValue(IDBConstants.FIELD_BUDGET);
					return "预算："
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
					return "当月新增："
							+ df.format(actual)
							+ "<br/>百分比："
							+ Util.getDecimalFormat(Util.NUMBER_P2_PERC)
									.format(actual / budget);
				}
			}
		});

		col = new TreeViewerColumn(projectViewer, SWT.NONE);
		final TreeColumn column1 = col.getColumn();
		column1.setWidth(200);
		column1.setText("计划偏差");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				// 项目负责人
				// 项目差异
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
		column2.setText("风险分析");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				// 部门
				// 风险
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
		column3.setText("首席师评价");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				// 空
				// 首席师点评
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

		// 部门选择
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

		// 确定
		Button query = new Button(panel, SWT.PUSH);
		query.setText("查询");
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
		export.setText("月报表");
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
		open.setText("打开");
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
		expand.setText("展开");
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
		cpse.setText("折叠");
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
				return false;//不继续保存
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
				return false;//不继续保存
			}
			
		};
		//显示对话框，选择项目的部门
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
