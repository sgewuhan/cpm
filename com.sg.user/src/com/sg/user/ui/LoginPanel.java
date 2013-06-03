package com.sg.user.ui;

import java.io.IOException;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;

import com.sg.user.UserSessionContext;

public class LoginPanel extends Composite {

	public static final String ERROR_TIPS = "登录失败，输入的帐号和密码无法通过验证";
	private static final String COOKIE_UI_USERID = "com.sg.user.cookie.uid";
	private static final String COOKIE_UI_PASSWORD = "com.sg.user.cookie.psw";
	private static final String ERROR_TIPS_EMPTYUSERNAME = "请输入登录帐号、用户名或者email";
	private static final String ERROR_TIPS_EMPTYPSW = "请输入密码";
	private static final String ERROR_TIPS_INVALIDUSERNAME = "用户名不能包含空格和这些字符：";
	private static final String INVALID_CHARS = " \\/~!#()^$[]?*{}|&%=";
	private Text userText;
	private Text passwordText;
	private String userId;
	private String password;
	private Button saveIdButton;
	private Button savePassButton;
	private Shell shell;
	private Button okButton;
	private ToolTip loginTooltips;

	public LoginPanel(Shell page, Composite parent) {
		super(parent, SWT.NONE);
		this.shell = page;

		setLayout(new GridLayout());
		// 创建登录区域的用户标签 用户名输入框 密码标签 密码输入框 登录按钮
		Composite panelTop = new Composite(this, SWT.NONE);
		panelTop.setBackgroundMode(SWT.INHERIT_DEFAULT);

		RowLayout layout = new RowLayout();
		layout.spacing = 40;
		layout.marginBottom = 0;
		layout.marginRight = 10;
		layout.marginTop = 0;
		layout.wrap = false;
		layout.pack = true;
		layout.center = true;

		panelTop.setLayout(layout);

		userText = new Text(panelTop, SWT.BORDER);
		RowData rd = new RowData();
		rd.width = 240;
		userText.setMessage("帐号、用户名或者email");
		userText.setLayoutData(rd);
		userText.setData(RWT.CUSTOM_VARIANT, "loginInput");
		userText.setFocus();

		passwordText = new Text(panelTop, SWT.BORDER | SWT.PASSWORD);
		rd = new RowData();
		rd.width = 220;
		passwordText.setMessage("输入登录密码");
		passwordText.setLayoutData(rd);
		passwordText.setData(RWT.CUSTOM_VARIANT, "loginInput");

		okButton = new Button(panelTop, SWT.PUSH);
		okButton.setData(RWT.CUSTOM_VARIANT, "loginInput");
		rd = new RowData();
		rd.width = 50;
		rd.height = 50;
		okButton.setLayoutData(rd);
		page.setDefaultButton(okButton);
		okButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				buttonPressed();
			}

		});

		// 创建保存密码勾选框和保存用户名勾选框
		Composite panelBottom = new Composite(this, SWT.NONE);

		layout = new RowLayout();
		layout.spacing = 4;
		layout.marginBottom = 0;
		layout.marginRight = 10;
		layout.marginTop = 0;
		layout.wrap = false;
		layout.pack = true;

		panelBottom.setLayout(layout);

		saveIdButton = new Button(panelBottom, SWT.CHECK);
		saveIdButton.setText("保存登录帐号");
		saveIdButton.setData(RWT.CUSTOM_VARIANT, "loginCheck");

		savePassButton = new Button(panelBottom, SWT.CHECK);
		savePassButton.setText("保存登录密码");
		savePassButton.setData(RWT.CUSTOM_VARIANT, "loginCheck");

		// cookie save
		String uid = RWT.getSettingStore().getAttribute(COOKIE_UI_USERID);
		if (uid != null && uid.length() > 0) {
			userText.setText(uid);
			saveIdButton.setSelection(true);
		}
		// cookie save
		String psd = RWT.getSettingStore().getAttribute(COOKIE_UI_PASSWORD);
		if (psd != null && psd.length() > 0) {
			passwordText.setText(psd);
			savePassButton.setSelection(true);
		}

		panelTop.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		panelBottom.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false,
				false));

		createTooltips();
	}

	private void createTooltips() {
		loginTooltips = new ToolTip(getShell(), SWT.ICON_INFORMATION);
		loginTooltips.setData(RWT.CUSTOM_VARIANT, "loginTips");
		loginTooltips.setAutoHide(true);
	}

	private Point getMessageLocation(Control control) {
		Point point = control.toDisplay(0, 0);
		point.y += control.getBounds().height + 2;
		return point;
	}

	protected void buttonPressed() {
		userId = userText.getText();
		password = passwordText.getText();

		if (userId.trim().equals("")) {
			setMessage(ERROR_TIPS_EMPTYUSERNAME, userText);
			return;
		}

		if (invalidCharInUserId(userId)) {
			setMessage(ERROR_TIPS_INVALIDUSERNAME + INVALID_CHARS, userText);
			return;
		}
		if (password.trim().equals("")) {
			setMessage(ERROR_TIPS_EMPTYPSW, passwordText);
			return;
		}

		String loginMessage = UserSessionContext.getSession().login(userId,
				password);
		if (loginMessage == null) {
			if (isSavedUserId()) {
				try {
					RWT.getSettingStore()
							.setAttribute(COOKIE_UI_USERID, userId);
				} catch (IOException e) {
				}
			}
			if (isSavedUserPassword()) {
				try {
					RWT.getSettingStore().setAttribute(COOKIE_UI_PASSWORD,
							password);
				} catch (IOException e) {
				}
			}
			// 通过了验证
			shell.dispose();
		} else {
			setMessage(loginMessage, passwordText);
		}
	}

	private boolean invalidCharInUserId(String userId) {
		char[] chars = INVALID_CHARS.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (userId.contains(String.valueOf(chars[i]))) {
				return true;
			}
		}
		return false;
	}

	private void setMessage(String errorTips, Control control) {
		loginTooltips.setLocation(getMessageLocation(control));
		loginTooltips.setMessage(errorTips);
		loginTooltips.setVisible(true);
	}

	private boolean isSavedUserPassword() {
		return savePassButton.getSelection();
	}

	private boolean isSavedUserId() {
		return saveIdButton.getSelection();
	}
}