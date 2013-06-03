package com.sg.cpm.myworks.viewer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.internal.provisional.action.ToolBarManager2;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.sg.common.db.IDBConstants;
import com.sg.cpm.myworks.view.WorkInBox;

public class MessageSortControl implements IViewActionDelegate {

	private MenuManager menuManager;
	private Action unsortAll;
	private SorterAction sortorCreateDate;
	private SorterAction sortorFinishDate;
	private SorterAction sortorRead;
	private WorkInBox view;

	@Override
	public void run(IAction action) {
		ToolBarManager2 tm = (ToolBarManager2) view.getViewSite().getActionBars().getToolBarManager();
		int index = tm.indexOf(action.getId())-1;
		ToolBar control = (ToolBar) tm.getControl2();
		Menu menu = menuManager.createContextMenu(control);
		
		Point hl = control.toDisplay(0, 0);
		hl.y += control.getBounds().height + 2;
		hl.x += index*42+2 ;
				
		menu.setLocation(hl);
		menu.setVisible(true);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void init(IViewPart view) {
		this.view = (WorkInBox) view;
		//����������ʱ������
		//���ƻ����ʱ������
		//���Ѷ�δ������
		//ȡ��ȫ������
		sortorCreateDate = new SorterAction("������ʱ������","message@"+IDBConstants.FIELD_CREATE_DATE,(WorkInBox)view,this);
		sortorFinishDate = new SorterAction("�ƻ����ʱ������","target@"+IDBConstants.FIELD_PROJECT_PLANFINISH,(WorkInBox)view,this);
		sortorRead = new SorterAction("���Ѷ���δ������","message@"+IDBConstants.FIELD_MARK_READ,(WorkInBox)view,this);
		unsortAll = new UnSortAction(this.view,this);
		
		menuManager = new MenuManager("#PopupMenu");
		menuManager.add(sortorCreateDate);
		menuManager.add(sortorFinishDate);
		menuManager.add(sortorRead);
		menuManager.add(new Separator());
		menuManager.add(unsortAll);
		
	}

	public void updateActions(Object source) {
		if(sortorCreateDate!=source){
			sortorCreateDate.updateDirect(0);
			sortorCreateDate.updateStatus();
		}
		if(sortorFinishDate!=source){
			sortorFinishDate.updateDirect(0);
			sortorFinishDate.updateStatus();
		}
		if(sortorRead!=source){
			sortorRead.updateDirect(0);
			sortorRead.updateStatus();
		}
	}

}
