package com.sg.user.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import com.sg.user.AuthorityResponse;

public class AuthorityUI {

	public static void SHOW_NOT_PERMISSION() {
		MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Ȩ�޹���",
				"�ܱ�Ǹ������û��ִ�����������Ȩ�ޡ�\n�������Ҫ��������������������վ��Ĺ���Ա��ϵ�Ի�ø�Ȩ�ޡ�");
	}

	
	public static void SHOW_NOT_PERMISSION(AuthorityResponse resp) {
		MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Ȩ�޹���",
				resp.getMessage());
	}

}
