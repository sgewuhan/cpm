package com.sg.vault.view;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.mongodb.BasicDBList;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.CascadeObject;
import com.sg.widget.part.ColumnAutoResizer;
import com.sg.widget.part.NavigatableTreeView;

public abstract class DocumentNavigatorView extends NavigatableTreeView {
	

	public CascadeObject dragItem;


	private void setupDND() {
		setDragSourceForSet(viewer.getControl());
		setDropTargetForSet(viewer.getControl());
	}

	public abstract String getFolderManagementAuthCode();

	private void setDragSourceForSet(Control dragControl) {
		DragSource dragSource = new DragSource(dragControl, DND.DROP_MOVE);
		dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dragSource.addDragListener(new MoveDocumentAndFolderDragListener(this));
	}

	private void setDropTargetForSet(Control dropControl) {
		DropTarget dropTarget = new DropTarget(dropControl, DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dropTarget.addDropListener(new MoveDocumentAndFolderDropListener(this));
	}

	@Override
	public void createPartControl(Composite parent) {
		setActiveCollectionAdaptable(false);
		loadConfiguration();

		// 侦听工作台当前选中的项目 作为当前查询的输入
		getSite().getPage().addPostSelectionListener(this);

		viewer = createViewer(parent);
		
		getSite().setSelectionProvider(viewer);
		root = viewer.getExpression();

		update();

		setupDND();
		
		new ColumnAutoResizer(parent,viewer.getTree().getColumn(0));
	}

	@Override
	public void update() {
		BasicDBList rootIdList = getRootIdList();
		root.setParamValue(IDBConstants.PARAM_INPUT_FBSPARENT_LIST, rootIdList);
		root.rootReload();
		viewer.runSetInput();
	}

	protected abstract BasicDBList getRootIdList();

	@Override
	public int getObjectType() {
		return TYPE_UNKNOW;
	}

	@Override
	public String getDisplayText() {
		return "my folder navigator view";
	}

	@Override
	public String getAuthorityContextCollectionName() {
		return IDBConstants.COLLECTION_FOLDER;
	}

}
