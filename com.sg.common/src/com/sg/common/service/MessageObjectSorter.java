package com.sg.common.service;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class MessageObjectSorter {

	private TreeViewer viewer;
	private Map<String, Integer> keyset = new HashMap<String, Integer>();

	public MessageObjectSorter(TreeViewer viewer, String[] defaultSortKeys) {
		this.viewer = viewer;
		newSort(defaultSortKeys);
	}

	public void appendKey(String keys) {
		String[] keyString = keys.split(",");
		String key = keyString[0];
		int dir = 1;
		try {
			dir = Integer.parseInt(keyString[1]);
		} catch (Exception e) {
		}
		keyset.put(key, dir);
	}

	private void initSortKeySet(String[] defaultSortKeys) {
		for (int i = 0; i < defaultSortKeys.length; i++) {
			appendKey(defaultSortKeys[i]);
		}
	}

	/**
	 * 全部重新排序
	 * 
	 * @param keys
	 */
	public void newSort(String[] keys) {
		if(keys==null){
			keyset = null;
		}else{
			keyset.clear();
			initSortKeySet(keys);
		}
		createSorter();
	}

	/**
	 * 追加排序
	 * 
	 * @param keys
	 */
	public void appendSort(String keys) {
		appendKey(keys);
		createSorter();
	}

	private void createSorter() {
		if (keyset == null) {
			viewer.setSorter(null);
		} else {

			ViewerSorter sorter = new ViewerSorter() {
				@Override
				public int compare(Viewer viewer, Object e1, Object e2) {
					if ((e1 instanceof MessageObject) && (e2 instanceof MessageObject)) {
						return ((MessageObject) e1).compare((MessageObject) e2, keyset);
					}
					return 0;
				}
			};

			viewer.setSorter(sorter);
		}
	}
}