package com.sg.widget.part;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import com.sg.db.Util;
import com.sg.db.model.CascadeObject;
import com.sg.db.model.ISingleObject;
import com.sg.widget.Widget;
import com.sg.widget.configuration.NavigatableViewConfiguration;
import com.sg.widget.viewer.treeviewer.QueryTreeViewer;

public class NavigatableTreeView extends QueryTreeView implements ISelectionListener {

	private NavigatableViewConfiguration partConf;
	
	protected Map<String, Object> inputParaMap;
	
	protected CascadeObject root;
	
	private ListenerList masterChangeListeners = new ListenerList();

	@Override
	public void createPartControl(Composite parent) {
		loadConfiguration();

		// 侦听工作台当前选中的项目 作为当前查询的输入
		getSite().getPage().addPostSelectionListener(this);
		viewer = createViewer(parent);
		viewer.addPostSelectionChangedListener(this);
		getSite().setSelectionProvider(viewer);

		if(partConf.isInitLoad()){
			viewer.runSetInput();
		}
	}
	
	

	@Override
	protected QueryTreeViewer createViewer(Composite parent) {
		
		String confId = partConf.getQueryViewerId();

		conf = Widget.getQueryTreeViewerConfiguration(confId);

		int style = SWT.VIRTUAL;
		if (conf.isFullSelection()) {
			style = style | SWT.FULL_SELECTION;
		}
		
		if(conf.isMultiSelection()){
			style = style | SWT.FULL_SELECTION|SWT.MULTI;
		}
		
		QueryTreeViewer v = new QueryTreeViewer(parent, style, conf);
		int expandLevel = conf.getAutoExpandLevel();
		v.setAutoExpandLevel(expandLevel);
		return v;
	}
	

	protected void loadConfiguration() {
		String partId = getSite().getId();
		partConf = Widget.getNavigatablePartConfigurationByPartId(partId);
		Assert.isNotNull(partConf);
		editorConfigruationId = partConf.getSingleObjectEditorId();

	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		String firedPartId = part.getSite().getId();
		Set<String> selectionProviders = partConf.getSelectionProviderId();
		if (!selectionProviders.contains(firedPartId)) {
			return;
		}

		IStructuredSelection sel = (IStructuredSelection) selection;
		if (sel == null || sel.isEmpty()) {
			return;
		}

		Object element = sel.getFirstElement();
		ISingleObject so;

		if (element instanceof ISingleObject) {
			so = (ISingleObject) element;
		} else {
			so = (ISingleObject) Platform.getAdapterManager().getAdapter(element, ISingleObject.class);
			if (so == null) {
				return;
			}
		}
		

		
		Map<String, Object> newInputParaMap = getParametersFromPart(so,firedPartId);
		resetData(getExpression(),newInputParaMap);
	}
	
	

	protected Map<String, Object> getParametersFromPart(ISingleObject so, String firedPartId) {
		Map<String, String> parameters = partConf.getParametersMap(firedPartId);
		
		Map<String, Object> newInputParaMap = new HashMap<String, Object>();
		
		Iterator<String> iter = parameters.keySet().iterator();
		while (iter.hasNext()) {
			String srcKey = iter.next();
			Object value = so.getValue(srcKey);
			String targetKey = parameters.get(srcKey);
			newInputParaMap.put(targetKey, value);
		}
		return newInputParaMap;
	}



	protected CascadeObject getExpression() {
		return viewer.createExpression();
	}

	public void resetData(CascadeObject _root,Map<String, Object> parameters) {

		if (inputChanged(parameters)||(!Util.equals(_root, root))) {
			inputParaMap = parameters;
			_root.passParamValueMap(inputParaMap);
			viewer.setInput(_root);
			fireMasterChanged(root,_root);
			root = _root;
		}
	}

	public CascadeObject getRoot(){
		return root;
	}

	private boolean inputChanged(Map<String, Object> newInputParaMap) {
		if (inputParaMap == null && newInputParaMap == null){
			return false;
		}else if (inputParaMap != null && newInputParaMap == null){
			return true;
		}else if (inputParaMap == null && newInputParaMap != null){
			return true;
		}else{
			Set<String> oldKeySet = inputParaMap.keySet();
			Set<String> newKeySet = newInputParaMap.keySet();
			if (!oldKeySet.containsAll(newKeySet) ||! newKeySet.containsAll(oldKeySet)){
				return true;
			}else{
				Iterator<String> iter = oldKeySet.iterator();
				while(iter.hasNext()){
					String key = iter.next();
					Object oldValue = inputParaMap.get(key);
					Object newValue = newInputParaMap.get(key);
					if(!Util.equals(oldValue, newValue)){
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void dispose() {
		getSite().getPage().removePostSelectionListener(this);
		super.dispose();
	}
	
	public void addMasterChangeListener(IMasterChangeListener listener){
		masterChangeListeners.add(listener);
	}
	
	public void removeMasterChangeListener(IMasterChangeListener listener){
		masterChangeListeners.remove(listener);
	}
	
	
	private void fireMasterChanged(CascadeObject oldMaster, CascadeObject newMaster) {
		Object[] lis = masterChangeListeners.getListeners();
		for(int i=0;i<lis.length;i++){
			((IMasterChangeListener)lis[i]).masterChanged(oldMaster,newMaster);
		}
	}


	public void expand() {
		viewer.expandAll();
	}

	public void collapse() {
		viewer.collapseAll();
	}
	
}
