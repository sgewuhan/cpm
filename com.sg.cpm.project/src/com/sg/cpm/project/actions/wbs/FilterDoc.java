package com.sg.cpm.project.actions.wbs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.sg.common.db.IDBConstants;
import com.sg.cpm.project.view.WBSView;
import com.sg.db.model.CascadeObject;
import com.sg.resource.Resource;

public class FilterDoc implements IViewActionDelegate {
	
	private static final String text_doc = "显示文档";
	private static final String text_no_doc = "不显示文档";
	private static final String tooltips_doc = "在任务下级同时显示交付文档";
	private static final String tooltips_no_doc = "不要在任务下级显示交付文档";
	private boolean showDocument = false;
	private WBSView view;
	private String[] expandedItems;

	@Override
	public void run(IAction action) {
		saveExpandState();
		showDocument = !showDocument;
		view.setShowDocument(showDocument);
		restoreExpandState();
		updateIcon(action);
	}

	private void restoreExpandState() {
		CascadeObject co = (CascadeObject) view.getViewer().getInput();
		Set<CascadeObject> set = getExpandedSet(co);
		view.getViewer().setExpandedElements(set.toArray());
	}

	private Set<CascadeObject> getExpandedSet(CascadeObject parent) {
		Set<CascadeObject> set = new HashSet<CascadeObject>();
		String id = parent.getValue(IDBConstants.FIELD_SYSID).toString();
		for(int i=0;i<expandedItems.length;i++){
			String item = expandedItems[i];
			if(item.equals(id)){
				set.add(parent);
			}
		}
		
		List<CascadeObject> children = parent.getChildren();
		for(int i=0;i<children.size();i++){
			set.addAll(getExpandedSet(children.get(i)));
		}
		
		return set;
	}

	private void saveExpandState() {
		//保存当前展开的对象
		Object[] expanded = view.getViewer().getExpandedElements();
		expandedItems = new String[expanded.length];
		for(int i=0;i<expanded.length;i++){
			expandedItems[i]=  ((CascadeObject)expanded[i]).getValue(IDBConstants.FIELD_SYSID).toString();
		}
	}

	private void updateIcon(IAction action) {
		if(!showDocument){
			action.setImageDescriptor(Resource.getImageDescriptor(Resource.TASK_DOC_FILTER32));
			action.setText(text_doc);
			action.setToolTipText(tooltips_doc);
		}else{
			action.setImageDescriptor(Resource.getImageDescriptor(Resource.TASK_DOC_FILTER_DISABLED32));
			action.setText(text_no_doc);
			action.setToolTipText(tooltips_no_doc);
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IViewPart view) {
		this.view = (WBSView) view;
	}
	
	

}
