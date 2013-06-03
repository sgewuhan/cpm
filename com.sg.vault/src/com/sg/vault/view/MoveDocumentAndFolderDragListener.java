package com.sg.vault.view;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

import com.mongodb.util.JSON;
import com.sg.db.model.CascadeObject;

public class MoveDocumentAndFolderDragListener implements DragSourceListener {

	private DocumentNavigatorView docNavi;

	public MoveDocumentAndFolderDragListener(DocumentNavigatorView docNavi) {
		this.docNavi = docNavi;
	}

	public void dragFinished(final DragSourceEvent event) {
	}

	public void dragSetData(final DragSourceEvent event) {
		IStructuredSelection sele = docNavi.getViewer().getSelection();
		if (sele == null || sele.isEmpty()) {
			event.data = "#";// 必须要给一个字符串，否则要在javatonative出错。
			docNavi.dragItem = null;
			return;
		}
		CascadeObject so = (CascadeObject) sele.getFirstElement();
		// 如果是根，不可以移动
		if (so.getParent().getParent() == null) {
			event.data = "#";// 必须要给一个字符串，否则要在javatonative出错。
			docNavi.dragItem = null;
			return;
		}

		docNavi.dragItem = so;
		event.data = JSON.serialize(so.getData());
	}

	public void dragStart(final DragSourceEvent event) {
	}
}
