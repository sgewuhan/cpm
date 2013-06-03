package com.sg.user.ui;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.sg.user.UserService;

public class LoginPage {


	public static void OpenPage() {
		Shell page = new Shell( SWT.NONE );
		page.setMaximized(true);
		// 设置布局
		page.setLayout(new FormLayout());

		// 设置主体登录区
		Composite loginArea = new Composite(page, SWT.NONE);
		loginArea.setData(RWT.CUSTOM_VARIANT, "loginArea");
		FormData lfd = new FormData();
		loginArea.setLayoutData(lfd);
		loginArea.setBackgroundMode(SWT.INHERIT_FORCE);
		createLoginArea(page,loginArea);

		lfd.top = new FormAttachment(0);
		lfd.left = new FormAttachment(0);
		lfd.right = new FormAttachment(100);
		lfd.bottom = new FormAttachment(85);

		// 添加一个阴影装饰条
		Label trim = new Label(page, SWT.NONE);
		trim.setData(RWT.CUSTOM_VARIANT, "loginTrim");
		FormData tfd = new FormData();
		trim.setLayoutData(tfd);
		tfd.top = new FormAttachment(loginArea);
		tfd.left = new FormAttachment(0);
		tfd.right = new FormAttachment(100);
		tfd.height = 22;

		// 添加一个底部区域
		Composite bottomArea = new Composite(page, SWT.NONE);
		bottomArea.setData(RWT.CUSTOM_VARIANT, "loginBottom");
		FormData bfd = new FormData();
		bottomArea.setLayoutData(bfd);

		bfd.top = new FormAttachment(trim);
		bfd.left = new FormAttachment(0);
		bfd.right = new FormAttachment(100);
		bfd.bottom = new FormAttachment(100);

		createBottomArea(bottomArea);
		page.open();
		
		Display display = page.getDisplay();
		while(page!=null&&!page.isDisposed()){
			try {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			} catch (Throwable e) {
			}
		}
		
	}
	
	private static void createBottomArea(Composite bottomArea) {
		bottomArea.setLayout(new FormLayout());
		Label info = new Label(bottomArea,SWT.NONE);
		info.setData(RWT.CUSTOM_VARIANT, "loginInfo");
		FormData infofd = new FormData();
		
		info.setLayoutData(infofd);
		info.setText(UserService.getDefault().getProductInformation());
		infofd.bottom = new FormAttachment(100,-4);
		infofd.right = new FormAttachment(100,-4);
	}

	// 创建Logo ..登录控件等等
	private static void createLoginArea(Shell page, Composite loginArea) {
		loginArea.setLayout(new FormLayout());

		// 创建LOGO
		Label logo = new Label(loginArea, SWT.NONE);
		
		Image bigLogo = UserService.getDefault().getProductLogo();

		int logoHeight = bigLogo.getBounds().height;
		int logoWidth = bigLogo.getBounds().width;

		logo.setImage(bigLogo);

		FormData logofd = new FormData();
		logo.setLayoutData(logofd);
		logofd.left = new FormAttachment(50, -logoWidth / 2);
		logofd.top = new FormAttachment(50, -logoHeight / 2);
		logofd.width = logoWidth;
		logofd.height = logoHeight;
		
		//创建第二个LOGO 2
		Label applogo = null;
		Image appLogoImg = UserService.getDefault().getAppLogo();
		if(appLogoImg !=null){
			applogo = new Label(loginArea, SWT.NONE);
			int appLogoHeight = appLogoImg.getBounds().height;
			int appLogoWidth = appLogoImg.getBounds().width;
			
			applogo.setImage(appLogoImg);
			FormData appLogofd = new FormData();
			applogo.setLayoutData(appLogofd);
			appLogofd.left = new FormAttachment(50, -appLogoWidth / 2);
			appLogofd.top = new FormAttachment(logo, 6);
			appLogofd.width = appLogoWidth;
			appLogofd.height = appLogoHeight;
		}

		// 创建装饰条
		Label trim = new Label(loginArea, SWT.NONE);
		Image trimImg = UserService.getImage(UserService.IMAGE_SPLIT);
		int trimHeight = trimImg.getBounds().height;
		int trimWidth = trimImg.getBounds().width;

		trim.setImage(trimImg);
		FormData trimfd = new FormData();
		trim.setLayoutData(trimfd);
		trimfd.left = new FormAttachment(50, -trimWidth / 2);
		trimfd.top = new FormAttachment(applogo !=null?applogo:logo, 10);
		trimfd.width = trimWidth;
		trimfd.height = trimHeight;

		// 创建登录容器

		Composite comp = new LoginPanel(page,loginArea);
		
		FormData compData = new FormData();
		comp.setLayoutData(compData);
		compData.left = new FormAttachment(0);
		compData.top = new FormAttachment(trim, 10);
		compData.right = new FormAttachment(100);
		compData.bottom = new FormAttachment(100);
		
	}

	

}
