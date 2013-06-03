package com.sg.widget.viewer.sorter;

import java.io.UnsupportedEncodingException;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Item;

import com.mongodb.DBObject;
import com.sg.db.model.ISingleObject;

public class UniSorter extends AbstractColumnViewerSorter {

	private String name;

	public UniSorter(ColumnViewer viewer, Item column, String columneName) {
		super(viewer, column);
		this.name = columneName;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected int doCompare(Viewer viewer, Object o1, Object o2) {
		
		Object e1 = null;
		Object e2 = null;
		
		if(o1 instanceof ISingleObject){
			e1 = ((ISingleObject)o1).getValue(name);
		}else if(o1 instanceof DBObject){
			e1 = ((DBObject)o1).get(name);
		}else{
			e1 = o1;
		}
		
		if(o2 instanceof ISingleObject){
			e2 = ((ISingleObject)o2).getValue(name);
		}else if(o2 instanceof DBObject){
			e2 = ((DBObject)o2).get(name);
		}else{
			e2 = o2;
		}
		
		if(e1 == null&&e2!=null){
			return -1;
		}else if(e1!=null&&e2==null){
			return 1;
		}else if(e1==null&&e2==null){
			return 0;
		}else if(e1.equals(e2)){
			return 0;
		}else if((e1 instanceof String)&&(e2 instanceof String)){
			return compareString(e1,e2);
		}else if((e1 instanceof Comparable)&&(e2 instanceof Comparable)){
			return ((Comparable)e1).compareTo((Comparable)e2);
		}else{
			return compareString(e1, e2);
		}

		
	}
	
	
	private int compareString(Object e1, Object e2) {
		String s1 = e1.toString();
		String s2 = e2.toString();
		if (s1.length() == s1.getBytes().length
				&& s2.length() == s2.getBytes().length) {
			return s1.compareTo(s2);
		} else {
			return compareZH(s1, s2);
		}
	}

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
