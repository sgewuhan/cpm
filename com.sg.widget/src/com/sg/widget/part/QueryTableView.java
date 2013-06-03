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
import com.sg.widget.configuration.QueryTableConfiguration;
import com.sg.widget.viewer.tableviewer.QueryTableViewer;

public class QueryTableView extends ViewPart implements ISelectionChangedListener, IDocumentCreator, IUpdateablePart,
		IAuthorityContextProvider {

	protected QueryTableViewer viewer;
	protected QueryTableConfiguration conf;
	protected String editorConfigruationId;
	private boolean activeCollectionAdaptable;

	@Override
	public void createPartControl(Composite parent) {

		viewer = createViewer(parent);
		viewer.addPostSelectionChangedListener(this);
		// viewer.getControl().setTouchEnabled(true);
		// viewer.getControl().addGestureListener(new GestureListener() {
		//
		// @Override
		// public void gesture(GestureEvent e) {
		// onGesture(e);
		//
		// }
		// });
		getSite().setSelectionProvider(viewer);

		enableDocumentCreateable();
		
		viewer.runSetInput();
	}

	@SuppressWarnings("deprecation")
	public void enableDocumentCreateable() {
		// TODO Auto-generated method stub
		String partId = getSite().getId();
		editorConfigruationId = Widget.getPartCreator(partId);
	}

	// private void onGesture(GestureEvent event){
	// switch(event.detail){
	// case SWT.GESTURE_BEGIN:
	// break;
	// case SWT.GESTURE_END:
	// break;
	// case SWT.GESTURE_MAGNIFY:
	// break;
	// case SWT.GESTURE_PAN:
	// break;
	// case SWT.GESTURE_ROTATE:
	// break;
	// case SWT.GESTURE_SWIPE:
	// break;
	// default:
	// break;
	// }
	// }

	@SuppressWarnings("deprecation")
	protected QueryTableViewer createViewer(Composite parent) {
		String confId = Widget.getQueryTableViewerConfigurationIdByBindingPartId(getSite().getId());

		conf = Widget.getQueryTableViewerConfiguration(confId);

		int style = SWT.VIRTUAL;

		if (conf.isMultiSelection()) {
			style = style | SWT.MULTI;
		}

		return new QueryTableViewer(parent, style, conf);

	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public QueryTableViewer getViewer() {
		return viewer;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
	}

//	@Override
//	public ISingleObjectEditorInput getEditorInput() {
//		SingleObjectEditorInput so = new SingleObjectEditorInput((ISingleObject) viewer.getSelection().getFirstElement());
//		return so;
//	}
//
//	@Override
//	public boolean canOpenInEditor() {
//		IStructuredSelection selection = viewer.getSelection();
//		if (selection == null || selection.isEmpty()) {
//			return false;
//		}
//		ISingleObject so = (ISingleObject) selection.getFirstElement();
//		EditorConfiguration ce = Widget.getSingleObjectEditorConfigurationByCollection(so.getCollection().getName());
//		if (ce == null) {
//			return false;
//		}
//		return true;
//	}

	@Override
	public boolean needUpdate() {
		return true;
	}

	@Override
	public void update() {
		viewer.updateInputData();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public String getEditorConfigruation() {
		return editorConfigruationId;
	}
	
	@Override
	public String getAuthorityContextCollectionName() {
		if(editorConfigruationId!=null){
			EditorConfiguration editorConf = Widget.getSingleObjectEditorConfiguration(editorConfigruationId);
			if(editorConf !=null){
				return editorConf.getCollection();
			}
		}
		return null;
	}

	@Override
	public int getObjectType() {
		IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
		if(sel!=null&&!sel.isEmpty()){
			ISingleObject element = (ISingleObject)sel.getFirstElement();
			return element.getObjectType();
		}
		return TYPE_UNKNOW;
	}

	@Override
	public String getDisplayText() {
		IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
		if(sel!=null&&!sel.isEmpty()){
			ISingleObject element = (ISingleObject)sel.getFirstElement();
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
