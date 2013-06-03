package com.sg.widget.viewer.sorter;

import java.io.UnsupportedEncodingException;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Item;

public abstract class ColumnSorter extends AbstractColumnViewerSorter {

	public ColumnSorter(ColumnViewer viewer, Item column) {
		super(viewer, column);

	}

	@Override
	protected int doCompare(Viewer viewer, Object e1, Object e2) {

		String s1 = getText(e1);
		String s2 = getText(e2);

		if (s1.length() == s1.getBytes().length
				&& s2.length() == s2.getBytes().length) {
			return s1.compareTo(s2);
		} else {
			return compareZH(s1, s2);
		}
	}

	protected abstract String getText(Object e);

	private int compareZH(String s1, String s2) {
		try {
			byte[] buf1 = s1.getBytes(getCharSetCode());
			byte[] buf2 = s2.getBytes(getCharSetCode());
			int size = Math.min(buf1.length, buf2.length);
			for (int i = 0; i < size; i++) {
				if (buf1[i] < buf2[i])
					return -1;
				else if (buf1[i] > buf2[i])
					return 1;
			}
			return buf1.length - buf2.length;
		} catch (UnsupportedEncodingException ex) {
			return 0;
		}
	}

	protected String getCharSetCode() {
		return "GBK";
	}

}
