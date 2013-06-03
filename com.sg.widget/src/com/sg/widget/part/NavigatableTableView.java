package com.sg.widget.part;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import com.sg.db.Util;
import com.sg.db.expression.query.QueryExpression;
import com.sg.db.model.ISingleObject;
import com.sg.widget.Widget;
import com.sg.widget.configuration.NavigatableViewConfiguration;
import com.sg.widget.viewer.tableviewer.QueryTableViewer;

public class NavigatableTableView extends QueryTableView implements ISelectionListener {

	private NavigatableViewConfiguration partConf;
	private Map<String, Object> inputParaMap;

	@Override
	public void createPartControl(Composite parent) {
		loadConfiguration();

		// 侦听工作台当前选中的项目 作为当前查询的输入
		getSite().getPage().addPostSelectionListener(this);

		viewer = createViewer(parent);
		viewer.addPostSelectionChangedListener(this);
		getSite().setSelectionProvider(viewer);

		if (partConf.isInitLoad()) {
			viewer.runSetInput();
		}
	}

	@Override
	protected QueryTableViewer createViewer(Composite parent) {
		
		String confId = partConf.getQueryViewerId();

		conf = Widget.getQueryTableViewerConfiguration(confId);

		int style = SWT.VIRTUAL;

		if (conf.isMultiSelection()) {
			style = style | SWT.MULTI;
		}
		
		return new QueryTableViewer(parent, style, conf);

	}

	private void loadConfiguration() {
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
		}

		if (so == null) {
			return;
		}

		Map<String, String> parameters = partConf.getParametersMap(firedPartId);

		QueryExpression exp = viewer.createExpression();
		Map<String, Object> newInputParaMap = new HashMap<String, Object>();

		Iterator<String> iter = parameters.keySet().iterator();
		while (iter.hasNext()) {
			String srcKey = iter.next();
			Object value = so.getValue(srcKey);
			String targetKey = parameters.get(srcKey);
			newInputParaMap.put(targetKey, value);
		}

		if (inputChanged(newInputParaMap)) {
			inputParaMap = newInputParaMap;
			exp.passParamValueMap(inputParaMap);
			viewer.setInput(exp);
		}

	}

	private boolean inputChanged(Map<String, Object> newInputParaMap) {
		if (inputParaMap == null && newInputParaMap == null) {
			return false;
		} else if (inputParaMap != null && newInputParaMap == null) {
			return true;
		} else if (inputParaMap == null && newInputParaMap != null) {
			return true;
		} else {
			Set<String> oldKeySet = inputParaMap.keySet();
			Set<String> newKeySet = newInputParaMap.keySet();
			if (!oldKeySet.containsAll(newKeySet) || !newKeySet.containsAll(oldKeySet)) {
				return true;
			} else {
				Iterator<String> iter = oldKeySet.iterator();
				while (iter.hasNext()) {
					String key = iter.next();
					Object oldValue = inputParaMap.get(key);
					Object newValue = newInputParaMap.get(key);
					if (!Util.equals(oldValue, newValue)) {
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

	public void addDataObject(ISingleObject so) {
		viewer.add(so);
		viewer.setSelection(new StructuredSelection(so), true);
	}

}
