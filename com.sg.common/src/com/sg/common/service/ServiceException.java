package com.sg.common.service;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class ServiceException extends Exception {

	public static final String NO_WORKFLOWINFO = "�޷���ù�����������������";

	public static final String INCONSISTENT_DATA_CAUSE_BY_DELETE = "��һ�µ��������õ������ѱ�ɾ��";

	public static final String NO_TASK = "�޷����ָ������ŵ���������";

	public static final String NO_WORK_ACTOR_OR_GROUP = "���û��ִ���ߵĶ���";

	public static final String SERVER_IS_BUSY_TRYAGAIN = "�ܱ�Ǹ�������������޷���Ӧ���Զ��������̵Ĳ��������Ժ��ֹ�������";

	private Exception e;

	public ServiceException(String parameter) {

		this.e = new Exception(parameter);
	}

	public ServiceException(Exception e) {

		this.e = e;
	}

	@Override
	public synchronized Throwable fillInStackTrace() {

		return e.fillInStackTrace();
	}

	@Override
	public synchronized Throwable getCause() {

		return e.getCause();
	}

	@Override
	public String getLocalizedMessage() {

		return e.getLocalizedMessage();
	}

	@Override
	public String getMessage() {

		return e.getMessage();
	}

	@Override
	public StackTraceElement[] getStackTrace() {

		return e.getStackTrace();
	}

	@Override
	public synchronized Throwable initCause(Throwable arg0) {

		return e.initCause(arg0);
	}

	@Override
	public void printStackTrace() {

		e.printStackTrace();
	}

	@Override
	public void printStackTrace(PrintStream arg0) {

		e.printStackTrace(arg0);
	}

	@Override
	public void printStackTrace(PrintWriter arg0) {

		e.printStackTrace(arg0);
	}

	@Override
	public void setStackTrace(StackTraceElement[] arg0) {

		e.setStackTrace(arg0);
	}

	@Override
	public String toString() {

		return e.toString();
	}

	public Image getIcon() {

		Display display = Display.getCurrent();
		if (display != null)
			return display.getSystemImage(SWT.ICON_ERROR);
		return null;
	}

	public void openMessageBox() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		MessageDialog.openError(shell, "��ʾ", e.getMessage());
		
	}

}
