package com.sg.widget.part;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.sg.db.model.ISingleObject;
import com.sg.widget.Widget;
import com.sg.widget.configuration.EditorConfiguration;
import com.sg.widget.configuration.QueryTreeConfiguration;
import com.sg.widget.viewer.treeviewer.QueryTreeViewer;

public class QueryTreeView extends ViewPart implements ISelectionChangedListener, IDocumentCreator, IUpdateablePart,
		IAuthorityContextProvider {

	protected QueryTreeViewer viewer;
	protected QueryTreeConfiguration conf;
	protected String editorConfigruationId;
	private boolean activeCollectionAdaptable;

	@Override
	public void createPartControl(Composite parent) {
		viewer = createViewer(parent);
		viewer.addPostSelectionChangedListener(this);
		getSite().setSelectionProvider(viewer);
		enableDocumentCreateable();

		viewer.runSetInput();
	}

	@SuppressWarnings("deprecation")
	public void enableDocumentCreateable() {
		String partId = getSite().getId();
		editorConfigruationId = Widget.getPartCreator(partId);

	}

	protected QueryTreeViewer createViewer(Composite parent) {
		String confId = Widget.getQueryTreeViewerConfigurationIdByBindingPartId(getSite().getId());

		conf = Widget.getQueryTreeViewerConfiguration(confId);

		int style = SWT.VIRTUAL;
		if (conf.isFullSelection()) {
			style = style | SWT.FULL_SELECTION;
		}

		if (conf.isMultiSelection()) {
			style = style | SWT.FULL_SELECTION | SWT.MULTI;
		}

		QueryTreeViewer v = new QueryTreeViewer(parent, style, conf);
		int expandLevel = conf.getAutoExpandLevel();
		v.setAutoExpandLevel(expandLevel);
		return v;

	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public QueryTreeViewer getViewer() {
		return viewer;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
	}

	// @Override
	// public ISingleObjectEditorInput getEditorInput() {
	// SingleObjectEditorInput so = new SingleObjectEditorInput((ISingleObject)
	// viewer.getSelection().getFirstElement());
	// return so;
	// }
	//
	// @Override
	// public boolean canOpenInEditor() {
	// IStructuredSelection selection = viewer.getSelection();
	// if (selection == null || selection.isEmpty()) {
	// return false;
	// }
	// ISingleObject so = (ISingleObject) selection.getFirstElement();
	// DBCollection collection = so.getCollection();
	// if (collection != null) {
	// String name = collection.getName();
	// if (name != null) {
	// EditorConfiguration ce =
	// Widget.getSingleObjectEditorConfigurationByCollection(name);
	// return ce != null;
	// }
	// }
	// return false;
	// }

	@Override
	public boolean needUpdate() {
		return true;
	}

	@Override
	public void update() {
		viewer.updateInputData();
	}

	@Override
	public String getEditorConfigruation() {
		return editorConfigruationId;
	}

	@Override
	public String getAuthorityContextCollectionName() {
		if (editorConfigruationId != null) {
			EditorConfiguration editorConf = Widget.getSingleObjectEditorConfiguration(editorConfigruationId);
			if (editorConf != null) {
				return editorConf.getCollection();
			}
		}
		return null;
	}

	@Override
	public int getObjectType() {
		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		if (sel != null && !sel.isEmpty()) {
			ISingleObject element = (ISingleObject) sel.getFirstElement();
			return element.getObjectType();
		}
		return TYPE_UNKNOW;
	}

	@Override
	public String getDisplayText() {
		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		if (sel != null && !sel.isEmpty()) {
			ISingleObject element = (ISingleObject) sel.getFirstElement();
			return element.toString();
		}
		return "";
	}

	@Override
	public boolean activeCollectionAdaptable() {
		return activeCollectionAdaptable;
	}
	
	public void setActiveCollectionAdaptable(boolean activeCollectionAdaptable){
		this.activeCollectionAdaptable = activeCollectionAdaptable;
	}
}
