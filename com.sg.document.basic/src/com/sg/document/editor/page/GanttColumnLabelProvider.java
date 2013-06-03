package com.sg.document.editor.page;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.resource.Resource;
import com.sg.widget.util.FileUtil;

public class GanttColumnLabelProvider extends ColumnLabelProvider {

	private Date start;

	private Date end;

	public GanttColumnLabelProvider(int startYear, int startMonth) {

		// �õ��еĿ�ʼ�µĵ�һ�������
		Calendar cal = Calendar.getInstance();
		cal.set(startYear, startMonth, 1);
		start = cal.getTime();

		// �õ��еĿ�ʼ�µ����һ�������
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.SECOND, -1);
		end = cal.getTime();
	}

	public boolean showBar(Object element) {

		// �õ��еĽ���

		DBObject dto = (DBObject) element;
		Date rowStart = (Date) dto.get(IDBConstants.FIELD_PROJECT_PLANSTART);
		Date rowFinish = (Date) dto.get(IDBConstants.FIELD_PROJECT_PLANFINISH);
		boolean show = false;

		// �п�ʼ���������䣬��ʾ
		if (rowStart.after(start) && rowStart.before(end)) {
			show = true;
		}
		// �н������������䣬��ʾ
		if (rowFinish.after(start) && rowFinish.before(end)) {
			show = true;
		}

		if (rowStart.before(start) && rowFinish.after(end)) {
			show = true;
		}

		return show;
	}

	@Override
	public String getText(Object element) {

		if(showBar(element)){
			return "<img src='"+getImageURL()+"' style='float:left;padding: 10px 0px 10px 0px' width='40' height='10' />";
		}else{
			return "";
		}

	}

	private String getImageURL() {

		String imgLocation = FileUtil.getImageLocationFromInputStream(Resource.BAR, Resource.getDefault().getImageInputStream(Resource.BAR));
		return FileUtil.getImageUrl(imgLocation);
	}

}
