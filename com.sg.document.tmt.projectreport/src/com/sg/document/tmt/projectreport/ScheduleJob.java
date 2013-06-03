package com.sg.document.tmt.projectreport;

import java.util.Calendar;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

public abstract class ScheduleJob extends Job {

	private String timeRule;
	private JobChangeAdapter listener;

	public ScheduleJob() {
		super("schedule");
		listener = new JobChangeAdapter() {

			@Override
			public void done(IJobChangeEvent event) {
				if(timeRule!=null){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					long delay = getDelay(ScheduleJob.this.timeRule);
					schedule(delay);
				}
			}

		};
		addJobChangeListener(listener);
	}

	public void start(String timeRule) {
		this.timeRule = timeRule;
		long delay = getDelay(timeRule);
		schedule(delay);
	}
	

	public void stop() {
		removeJobChangeListener(listener);
		cancel();
	}

	public void start() {
		this.timeRule = null;
		schedule();
	}

	// 获得当前时间与预订时间的间隔,时间格式为HH:mm:ss
	private static long getDelay(String timeRule) {

		String[] strTime = timeRule.split(":");
		try {
			int h = Integer.parseInt(strTime[0]);
			int m = Integer.parseInt(strTime[1]);
			int s = Integer.parseInt(strTime[2]);
			
			Calendar schedualCal = Calendar.getInstance();
			schedualCal.set(Calendar.HOUR_OF_DAY, h);
			schedualCal.set(Calendar.MINUTE, m);
			schedualCal.set(Calendar.SECOND, s);
			
			Calendar currentCal = Calendar.getInstance();
			if(schedualCal.before(currentCal)){
				schedualCal.add(Calendar.DAY_OF_MONTH, 1);
			}
			long delay = schedualCal.getTimeInMillis()-currentCal.getTimeInMillis();
			return delay;
		} catch (Exception e) {
		}
		return 24 * 60 * 60 * 1000;

	}


}
