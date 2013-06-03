package com.sg.cpm.myworks.viewer;

import org.eclipse.jface.action.Action;

import com.sg.cpm.myworks.view.WorkInBox;
import com.sg.resource.Resource;

public class SorterAction extends Action {

	public int direct = 0;
	private WorkInBox view;
	private String key;
	private MessageSortControl menu;
	
	public SorterAction(String title, String key, WorkInBox view, MessageSortControl messageSortControl) {
		setText(title);
		this.view = view;
		this.key = key;
		for (int i = 0; i < WorkInBox.defaultSortKeys.length; i++) {
			String[] keyString = WorkInBox.defaultSortKeys[i].split(",");
			if(key.equals(keyString[0])){
				try {
					direct = Integer.parseInt(keyString[1]);
				} catch (Exception e) {
				}
				updateStatus();
				break;
			}
		}
		this.menu= messageSortControl;
		
	}
	

	

	@Override
	public void run() {
		if(direct==0){
			direct = 1;
		}else if(direct == 1){
			direct = -1;
		}else if(direct == -1){
			direct = 0;
		}
		view.setSort(direct,key);
		updateStatus();
		menu.updateActions(this);
	}

	public void updateDirect(int dir){
		this.direct = dir;
	}
	
	public void updateStatus() {
		if(direct==0){
			setImageDescriptor(Resource.getImageDescriptor(Resource.M_BLANK24));
		}else if(direct == 1){
			setImageDescriptor(Resource.getImageDescriptor(Resource.M_ARROW_UP24));
		}else if(direct == -1){
			setImageDescriptor(Resource.getImageDescriptor(Resource.M_ARROW_DOWN24));
		}
	}

}
