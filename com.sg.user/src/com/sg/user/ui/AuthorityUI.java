package com.sg.user.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import com.sg.user.AuthorityResponse;

public class AuthorityUI {

	public static void SHOW_NOT_PERMISSION() {
		MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "权限管理",
				"很抱歉，您还没有执行这个操作的权限。\n如果您需要继续操作，请与您所在站点的管理员联系以获得该权限。");
	}

	
	public static void SHOW_NOT_PERMISSION(AuthorityResponse resp) {
		MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "权限管理",
				resp.getMessage());
	}

}
