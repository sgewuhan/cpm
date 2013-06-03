package com.sg.widget.viewer.tableviewer;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.db.DBActivator;
import com.sg.db.expression.query.QueryExpression;
import com.sg.db.model.IEventListener;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.SingleObject;
import com.sg.widget.Widget;
import com.sg.widget.configuration.ColumnConfiguration;
import com.sg.widget.configuration.IEditingSupportor;
import com.sg.widget.configuration.QueryTableConfiguration;
import com.sg.widget.viewer.KeyNavigator;
import com.sg.widget.viewer.labelprovider.ViewerColumnLabelProvider;
import com.sg.widget.viewer.sorter.AbstractColumnViewerSorter;
import com.sg.widget.viewer.sorter.UniSorter;

public class QueryTableViewer extends TableViewer implements IEventListener, ISelectionChangedListener, DisposeListener {

	private List<ISingleObject> result;
	private QueryTableConfiguration conf;
	private QueryExpression expression;
	private ISingleObject currentSelection;
//	private KeyNavigator key;

	public QueryTableViewer(Composite parent, int style, String id) {
		super(parent, style);

		conf = Widget.getQueryTableViewerConfiguration(id);

		initialize();
	}

	public QueryTableViewer(Composite parent, int style,
			QueryTableConfiguration conf) {
		super(parent, style);

		this.conf = conf;

		initialize();
	}
	
	public void initialize() {

		createExpression();

		initTable();

		initContentProvider();

		initColumn();

		initListener();

		if(initLoad()){
			updateData();
		}
		
		
		initKeyNavigator();
		
//		//在测试中增加的功能，双击显示JSON
//		new JSONMessage(this);
		
	}

	private void initKeyNavigator() {
		new KeyNavigator(this);
	}

	protected boolean initLoad() {
		return true;
	}

	public QueryExpression createExpression() {
		String expressionId = conf.getQuery();
		expression = (QueryExpression) DBActivator.getExpression(expressionId);
		return expression;
	}

	public QueryExpression getExpression(){
		return expression;
	}
	
	
	public void runSetInput() {
		setInput(result);
//		setItemCount(result.size());
	}
	
	public void runSetInput(List<ISingleObject> result){
		this.result = result;
		runSetInput();
	}
	
	private void updateData() {
		result = new LinkedList<ISingleObject>();

		DBCursor cursor = expression.run();
		DBCollection collection = cursor.getCollection();
		List<DBObject> queryList = cursor.toArray();
		ISingleObject so;
		for (DBObject dbo : queryList) {
			so = new SingleObject(collection, dbo);
			result.add(so);
		}
	}
	
	public void addDataObject(ISingleObject so){
		result.add(so);
		add(so);
	}
	

	public void removeDataObject(ISingleObject so) {
		result.remove(so);
		remove(so);
	}

	public void updateInputData() {
		updateData();
		runSetInput();
	}

	private void initTable() {
		Table table = getTable();
		table.setHeaderVisible(conf.isHeaderVisiable());
		table.setLinesVisible(conf.isLineVisiable());
		setUseHashlookup(conf.isUseHashlookup());
		
		//rap 1.5 m6 new feature
		if(conf.isMarkupEnabled()){
			table.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
		}
		
		int itemHeight = conf.getItemHeight();
		if(itemHeight!=0){
			table.setData( RWT.CUSTOM_ITEM_HEIGHT, itemHeight );
		}
		
//		table.setTouchEnabled(true);
//		
//		table.addGestureListener(new GestureListener() {
//			
//			@Override
//			public void gesture(GestureEvent e) {
//			}
//		});
	}

	private void initListener() {
		IDoubleClickListener dcl = conf.getDoubleClickListener();
		if (dcl != null) {
			addDoubleClickListener(dcl);
		}
		
		addPostSelectionChangedListener(this);
		
		getTable().addDisposeListener(this);
	}

	private void initColumn() {
		List<ColumnConfiguration> ccs = conf.getColumnsConfigurations();

		TableViewerColumn vColumn;
		
		for (int i = 0; i < ccs.size(); i++) {

			ColumnConfiguration cc = ccs.get(i);
			String name = cc.getName();

			vColumn = new TableViewerColumn(this, cc.getStyle());

			final TableColumn cColumn = vColumn.getColumn();
			cColumn.setMoveable(cc.isMoveable());
			cColumn.setResizable(cc.isResizable());
			cColumn.setText(name);
			cColumn.setImage(cc.getImage());
			cColumn.setWidth(cc.getWidth());


			//设置Tooltips
			cColumn.setToolTipText(cc.getToolTipText());
			
			//设置LabelProvider
			ColumnLabelProvider labelProvider = cc.getLabelProvider();

			if (labelProvider == null) {
				labelProvider = new ViewerColumnLabelProvider(cc);
			} else if (labelProvider instanceof ViewerColumnLabelProvider) {
				((ViewerColumnLabelProvider) labelProvider)
						.setColumnConfigruation(cc);
			}
			
			vColumn.setLabelProvider(labelProvider);

			//设置编辑器
			IEditingSupportor es = cc.getEditingSupport();
			if (es != null) {
				vColumn.setEditingSupport(es.createEditingSupport(this,vColumn));
			}

			//设置排序
			if (cc.isSorter()) {
				AbstractColumnViewerSorter sortor = cc.getSortor(this,
						cColumn, cc.getColumn());
				if (sortor == null) {
					new UniSorter(this, cColumn, cc.getColumn());
				}
			}
			
			
		}
	}

	private void initContentProvider() {
		IContentProvider contentProvider = conf.getContentProvider();
		if (contentProvider == null) {
			if (conf.isUserLazyContentProvider()) {
				contentProvider = new LazyQueryTableContentProvider();
			} else {
				contentProvider = new QueryTableContentProvider();
			}
		}
		setContentProvider(contentProvider);
	}

	@Override
	public IStructuredSelection getSelection() {
		return (IStructuredSelection) super.getSelection();
	}

	@Override
	public void event(String code, ISingleObject singleObject) {
		if (getTable() == null || getTable().isDisposed()) {
			singleObject.removeEventListener(this);
			return;
		}
		if (code.equals(ISingleObject.UPDATED)) {
			update(singleObject, null);
		}else if(code.equals(ISingleObject.REMOVE)){
			result.remove(singleObject);
			remove(singleObject);
		}
	}

	public void memoryClientSetting() {

	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection sel = (IStructuredSelection)event.getSelection();
		if(sel!=null&&!sel.isEmpty()){
			ISingleObject element = (ISingleObject)sel.getFirstElement();
			if(!Util.equals(element, currentSelection)){
				if(currentSelection!=null){
					currentSelection.removeEventListener(this);
				}
				currentSelection = element;
				currentSelection.addEventListener(this);
			}
		}
	}

	@Override
	public void widgetDisposed(DisposeEvent event) {
		if(currentSelection!=null){
			currentSelection.removeEventListener(this);
		}
	}


}
