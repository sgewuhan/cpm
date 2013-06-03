package com.sg.email;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class MailJob extends Job {

	private String from;
	private String to;
	private String subject;
	private String htmlString;

	public MailJob(String from,String to,String subject,String htmlString) {
		super(subject);
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.htmlString = htmlString;
	}
	
	public MailJob(String to,String subject,String htmlString) {
		super(subject);
		this.from = Activator.getSenderEmailAddress();
		this.to = to;
		this.subject = subject;
		this.htmlString = htmlString;
	}
	
	

	public void setFrom(String from) {
		this.from = from;
	}


	public void setTo(String to) {
		this.to = to;
	}


	public void setSubject(String subject) {
		this.subject = subject;
	}


	public void setHtmlString(String htmlString) {
		this.htmlString = htmlString;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		MailSender ms = new MailSender();
		try {
			ms.setFrom(from);
			ms.setTo(to);
			ms.setSubject(subject);
//			ms.addMailBody(htmlString);
//			System.out.println(htmlString);
			ms.setHtml(htmlString);
			ms.send();
		} catch (Exception e) {
			e.printStackTrace();
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage());
		}
		return Status.OK_STATUS;
	}
	
	public static void SENDTEST(String address){
		
		/**
		 * 
		 * email.properties
		 * 
		 * mail.smtp.auth=true
		 * mail.smtp.host=smtp.exmail.qq.com
		 * sender.address=admin@yaozheng.com.cn
		 * sender.password=sbwgia7717
		 * 
		 */
		String htmlString = "<p><TEST CPM EMAIL</p>";
		MailJob mj = new MailJob(address, "²âÊÔÓÊ¼þ·þÎñ", htmlString);
		mj.schedule();
	}

}
