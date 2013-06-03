package com.sg.widget.viewer.treeviewer;

import java.util.List;

import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

import com.sg.db.DBActivator;
import com.sg.db.model.CascadeObject;
import com.sg.db.model.IEventListener;
import com.sg.db.model.ISingleObject;
import com.sg.widget.Widget;
import com.sg.widget.configuration.ColumnConfiguration;
import com.sg.widget.configuration.IEditingSupportor;
import com.sg.widget.configuration.QueryTreeConfiguration;
import com.sg.widget.viewer.JSONMessage;
import com.sg.widget.viewer.KeyNavigator;
import com.sg.widget.viewer.labelprovider.ViewerColumnLabelProvider;
import com.sg.widget.viewer.sorter.AbstractColumnViewerSorter;
import com.sg.widget.viewer.sorter.UniSorter;

public class QueryTreeViewer extends TreeViewer implements IEventListener, ISelectionChangedListener, DisposeListener {

	private QueryTreeConfiguration conf;

	private CascadeObject root;

	private CascadeObject currentSelection;

	private KeyNavigator key;

	public QueryTreeViewer(Composite parent, int style, String id) {

		super(parent, style);

		conf = Widget.getQueryTreeViewerConfiguration(id);

		runConfiguration();
		initKeyNavigator();
	}

	public QueryTreeViewer(Composite parent, int style, QueryTreeConfiguration conf) {

		super(parent, style);

		this.conf = conf;

		runConfiguration();
		initKeyNavigator();

	}

	public void runConfiguration() {

		createExpression();

		initTree();

		initContentProvider();

		initColumn();

		initListener();

		// 在测试中增加的功能，双击显示JSON
		new JSONMessage(this);

	}

	private void initKeyNavigator() {

		key = new KeyNavigator(this);
	}

	public CascadeObject createExpression() {

		String rootId = conf.getQuery();
		root = DBActivator.getCascadeObject(rootId);
		return root;
	}

	public void runSetInput() {

		setInput(root);
	}

	public CascadeObject getExpression() {

		return root;
	}

	public void updateInputData() {

		root.rootReload();
		setInput(root);
	}

	private void initTree() {

		Tree tree = getTree();
		tree.setHeaderVisible(conf.isHeaderVisiable());
		tree.setLinesVisible(conf.isLineVisiable());
		setUseHashlookup(conf.isUseHashlookup());

		// rap 1.5 m6 new feature
		if (conf.isMarkupEnabled()) {
			tree.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		}

		int itemHeight = conf.getItemHeight();
		if (itemHeight != 0) {
			tree.setData(RWT.CUSTOM_ITEM_HEIGHT, itemHeight);
		}
	}

	private void initListener() {

		IDoubleClickListener dcl = conf.getDoubleClickListener();
		if (dcl != null) {
			addDoubleClickListener(dcl);
		}

		addPostSelectionChangedListener(this);

		getTree().addDisposeListener(this);
	}

	private void initColumn() {

		TreeViewerColumn column;
		List<ColumnConfiguration> ccs = conf.getColumnsConfigurations();
		for (int i = 0; i < ccs.size(); i++) {
			ColumnConfiguration cc = ccs.get(i);
			column = new TreeViewerColumn(this, cc.getStyle());

			column.getColumn().setMoveable(cc.isMoveable());
			column.getColumn().setResizable(cc.isResizable());
			column.getColumn().setText(cc.getName());
			column.getColumn().setImage(cc.getImage());
			column.getColumn().setWidth(cc.getWidth());
			column.getColumn().setToolTipText(cc.getToolTipText());

			ColumnLabelProvider labelProvider = cc.getLabelProvider();

			if (labelProvider == null) {
				labelProvider = new ViewerColumnLabelProvider(cc);
			} else if (labelProvider instanceof ViewerColumnLabelProvider) {
				((ViewerColumnLabelProvider) labelProvider).setColumnConfigruation(cc);
			}

			column.setLabelProvider(labelProvider);

			//设置编辑器
			IEditingSupportor es = cc.getEditingSupport();
			if (es != null) {
				column.setEditingSupport(es.createEditingSupport(this,column));
			}

			if (cc.isSorter()) {
				AbstractColumnViewerSorter sortor = cc.getSortor(this, column.getColumn(), cc.getColumn());
				if (sortor == null) {
					new UniSorter(this, column.getColumn(), cc.getColumn());
				}
			}
		}
	}

	private void initContentProvider() {

		IContentProvider contentProvider = conf.getContentProvider();
		if (contentProvider == null) {
			contentProvider = new QueryTreeContentProvider();
		}
		setContentProvider(contentProvider);
	}

	@Override
	public IStructuredSelection getSelection() {

		return (IStructuredSelection) super.getSelection();
	}

	@Override
	public void event(String code, ISingleObject singleObject) {

		if (getTree() == null || getTree().isDisposed()) {
			singleObject.removeEventListener(this);
			return;
		}
		if (code.equals(ISingleObject.UPDATED)) {
			update(singleObject, null);
		} else if (code.equals(ISingleObject.REMOVE)) {
			CascadeObject parent = ((CascadeObject) singleObject).getParent();
			refresh(parent);
			// result.remove(singleObject);
			// remove(singleObject);
		}
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {

		IStructuredSelection sel = (IStructuredSelection) event.getSelection();
		if (sel != null && !sel.isEmpty()) {
			CascadeObject element = (CascadeObject) sel.getFirstElement();
			if (!Util.equals(element, currentSelection)) {
				if (currentSelection != null) {
					currentSelection.removeEventListener(this);
				}
				currentSelection = element;
				currentSelection.addEventListener(this);
			}
		}
	}

	@Override
	public void widgetDisposed(DisposeEvent event) {

		if (currentSelection != null) {
			currentSelection.removeEventListener(this);
		}
	}

}
