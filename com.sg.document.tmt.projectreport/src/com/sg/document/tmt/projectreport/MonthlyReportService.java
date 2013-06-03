package com.sg.document.tmt.projectreport;

import com.sg.common.IJobSettingChangeListener;

public class MonthlyReportService implements IJobSettingChangeListener {

	private MonthlyReportJob job;

	public void schedule(final String timeRule) {
		job = new MonthlyReportJob();
		job.start(timeRule);
	}

	public void schedule() {
		job = new MonthlyReportJob();
		job.start();
	}

	@Override
	public void jobSettingChanged(String jobTrigerTime) {
		if (job != null) {
			job.stop();
		}
		schedule(jobTrigerTime);
	}

}
