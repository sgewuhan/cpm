package com.sg.document.editor.page;

import java.text.DecimalFormat;
import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.common.ui.UIConstants;
import com.sg.db.model.ISingleObject;
import com.sg.resource.Resource;
import com.sg.widget.Widget;
import com.sg.widget.configuration.PageConfiguration;
import com.sg.widget.editor.AbstractPageDelegator;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.resource.Enumerate;
import com.sg.widget.util.Util;

public class BudgetPage extends AbstractPageDelegator {

	private static final String MESS0 = "您需要输入一个数字";

	private static final String MESS1 = "您不能输入一个负的预算";

	private static final String MESS2 = "您单项预算累计已经超过了本项目的总预算";
	
	private static final String BUDGETLIST = "budgetlist";

	private TreeViewer viewer;

	private Enumerate rootEnum;

	private DecimalFormat amountFormat;

	private DBObject budget;

	private ISingleObject inputData;

	@Override
	public Composite createPageContent(Composite parent, ISingleObjectEditorInput input, PageConfiguration conf) {
		this.inputData = input.getInputData();
		// 读取配置
		rootEnum = Widget.getDefault().getEnumerate("cost_root");
		amountFormat = Util.getDecimalFormat(Util.RMB_MONEY);

		GridLayout layout = new GridLayout();
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 1;
		parent.setLayout(layout);

		createToolbar(parent);

		createViewer(parent);

		setInput(input);

		return null;
	}

	private void setInput(ISingleObjectEditorInput input) {

		budget = (DBObject) input.getInputData().getValue(BUDGETLIST);
		if (budget == null) {
			budget = new BasicDBObject();
			// 按照配置加载
			setupBudgetItem(rootEnum, budget);
			input.getInputData().setValue(BUDGETLIST, budget, null, false);
		}
//		ArrayList<Enumerate> inputList = new ArrayList<Enumerate>();
//		inputList.add(rootEnum);
		viewer.setInput(new Enumerate[]{rootEnum});
		viewer.expandAll();
	}

	private void setupBudgetItem(Enumerate parent, DBObject budget) {

		budget.put(parent.getId(), null);
		List<Enumerate> children = parent.getChildren();
		if (children != null)
			for (int i = 0; i < children.size(); i++) {
				Enumerate childrenItem = children.get(i);
				setupBudgetItem(childrenItem, budget);
			}
	}

	private void createViewer(Composite parent) {

		viewer = new TreeViewer(parent, SWT.FULL_SELECTION);
		viewer.getTree().setLinesVisible(true);
		viewer.getTree().setHeaderVisible(true);
		viewer.setContentProvider(new ITreeContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

			@Override
			public void dispose() {

			}

			@Override
			public boolean hasChildren(Object element) {

				List<Enumerate> children = ((Enumerate) element).getChildren();
				return children != null && (!children.isEmpty());
			}

			@Override
			public Object getParent(Object element) {

				return null;
			}

			@Override
			public Object[] getElements(Object inputElement) {

				return (Object[]) inputElement;
			}

			@Override
			public Object[] getChildren(Object parentElement) {

				return ((Enumerate) parentElement).getChildren().toArray();
			}
		});
		TreeViewerColumn col = new TreeViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText("项目");
		col.getColumn().setWidth(260);
		col.getColumn().setMoveable(false);
		col.setLabelProvider(new ColumnLabelProvider() {// 显示科目名称

			@Override
			public String getText(Object element) {

				return ((Enumerate) element).getLabel();
			}

		});

		col = new TreeViewerColumn(viewer, SWT.RIGHT);
		col.getColumn().setText("金额（人民币万元）");
		col.getColumn().setWidth(180);
		col.getColumn().setMoveable(false);
		col.setLabelProvider(new ColumnLabelProvider() {// 显示预算金额

			@Override
			public String getText(Object element) {

				double amount = getAmount((Enumerate) element);

				if (amount == 0d) {
					return "";
				}
				return amountFormat.format(amount);
			}

		});

		col = new TreeViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText("说明");
		col.getColumn().setWidth(600);
		col.getColumn().setMoveable(false);
		col.setLabelProvider(new ColumnLabelProvider() {// 显示备注

			@Override
			public String getText(Object element) {

				return ((Enumerate) element).getValue().toString();

			}

		});

		viewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				ISelection sel = event.getSelection();
				handleEdit((IStructuredSelection) sel);
			}
		});
	}

	protected double getAmount(Enumerate element) {

		List<Enumerate> children = element.getChildren();
		if (children == null || children.isEmpty()) {
			Double amount = (Double) budget.get(element.getId());
			
			return amount==null?0d:amount;
		} else {
			double amount = 0d;
			for (int i = 0; i < children.size(); i++) {
				amount = amount + getAmount(children.get(i));
			}
			return amount;
		}
	}

	protected Enumerate getChildrenById(Enumerate parent, String id) {

		if (parent.getId().equals(id)) {
			return parent;
		} else {
			List<Enumerate> children = parent.getChildren();
			if (children == null || children.isEmpty()) {
				return null;
			} else {
				for (int i = 0; i < children.size(); i++) {
					Enumerate child = getChildrenById(children.get(i), id);
					if (child != null) {
						return child;
					}
				}
				return null;
			}

		}
	}

	private void createToolbar(Composite parent) {

		Composite toolbar = new Composite(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false));
		RowLayout layout = new RowLayout();
		layout.wrap = false;
		layout.pack = true;
		layout.justify = false;
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		toolbar.setLayout(layout);

		Button buttonEdit = new Button(toolbar, SWT.PUSH);
		buttonEdit.setData(RWT.CUSTOM_VARIANT, UIConstants.WIDGET_CSS_IN_EDITOR);
		buttonEdit.setImage(Resource.getImage(Resource.EDIT_PROP32));
		buttonEdit.setToolTipText(UIConstants.TEXT_EDIT);
		buttonEdit.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
				handleEdit(sel);
			}
		});

	}

	protected void handleEdit(IStructuredSelection sel) {

		if (sel == null || sel.isEmpty()) {
			return;
		}
		Enumerate e = (Enumerate) sel.getFirstElement();
		if(e.hasChildren()){
			return;
		}
		
		IInputValidator validator = new IInputValidator() {

			@Override
			public String isValid(String newText) {

				try {
					double d = Double.parseDouble(newText);
					if (d >= 0){
						if(checkSummary(d)){
							return null;
						}else{
							return MESS2;
						}
					}else{
						return MESS1;
					}
				} catch (Exception e) {
					return MESS0;
				}
			}
		};
		Shell shell = Display.getCurrent().getActiveShell();
		InputDialog id = new InputDialog(shell, e.getLabel(), "输入预算值(人民币万元)", "", validator);
		int ok = id.open();
		if (ok == InputDialog.OK) {
			String st = id.getValue();
			try {
				double d = Double.parseDouble(st);
				budget.put(e.getId(), d);
				viewer.refresh();
				setDirty(true);
			} catch (Exception ex) {
			}
		}
	}

	protected boolean checkSummary(double d) {
		Double budget = (Double) inputData.getData().get("budget");
		if(budget==null){
			return true;
		}
		double amount = getAmount(rootEnum);
		return (amount+d)<=budget.doubleValue();
	}

}
