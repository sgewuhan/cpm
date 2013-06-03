package com.sg.widget.dialog;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class MessageBox implements SelectionListener {

	public static int ICON_ERROR = 1;

	public static int ICON_INFORMATION = 1 << 1;

	public static int ICON_QUESTION = 1 << 2;

	public static int ICON_WARNING = 1 << 3;

	public static int WINDOW_FLOAT = 1 << 4;

	public static int BUTTON_OK = 1 << 5;

	public static int BUTTON_CANCEL = 1 << 6;

//	private Button okButton;
//
//	private Button cancelButton;

	private int style;

	private Shell shell;

	private Composite bg;

	public MessageBox(Shell parentShell, String title, String message, int style) {
//		Display display = parentShell.getDisplay();
		this.style = style;
		// shell 全透明
		shell = new Shell(parentShell, isFloatStyle() ? SWT.NONE : SWT.APPLICATION_MODAL);
		shell.setLayout(new FormLayout());

		// border半透明边框
		bg = new Composite(shell, SWT.NONE);
		bg.setBackgroundMode(SWT.INHERIT_NONE);

		FormData fd = new FormData();
		bg.setLayoutData(fd);
		fd.top = new FormAttachment(0, 9);
		fd.left = new FormAttachment(0,0);
		fd.bottom = new FormAttachment(100,0);
		fd.right = new FormAttachment(100, -9);


		bg.setLayout(new GridLayout(2, false));

		// 显示文本和图标区
		Composite content = new Composite(bg, SWT.NONE);
		content.setData(RWT.CUSTOM_VARIANT, "messageboxcontent");
		if (hasImage()) {
			content.setLayout(new GridLayout(2, false));
		} else {
			content.setLayout(new GridLayout());
		}
		//创建title
		Label titleLabel = new Label(content,SWT.WRAP);
		titleLabel.setText(title);
		GridData layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		titleLabel.setLayoutData(layoutData);
//		
//		if (hasImage()) {
//			// 创建图像
//			Label iconLabel = new Label(content, SWT.NONE);
//			if ((ICON_WARNING & style) != 0) {
//				iconLabel.setImage(display.getSystemImage(SWT.ICON_WARNING));
//			} else if ((ICON_ERROR & style) != 0) {
//				iconLabel.setImage(display.getSystemImage(SWT.ICON_ERROR));
//			} else if ((ICON_INFORMATION & style) != 0) {
//				iconLabel.setImage(display.getSystemImage(SWT.ICON_INFORMATION));
//			} else if ((ICON_QUESTION & style) != 0) {
//				iconLabel.setImage(display.getSystemImage(SWT.ICON_QUESTION));
//			}
//			layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
//			iconLabel.setLayoutData(layoutData);
//		}
//
//		//创建消息
//		Label messageLabel = new Label(content,SWT.WRAP);
//		messageLabel.setText(message);
//		layoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
//		messageLabel.setLayoutData(layoutData);
//		
//		
//		content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
//		
//		if((BUTTON_OK & style) != 0){
//			okButton = new Button(bg, SWT.PUSH);
//			okButton.addSelectionListener(this);
//			okButton.setImage(Widget.getImage(IWidgetImage.IMG_W_OK32));
//		}		
//		
//		if((BUTTON_CANCEL & style) != 0){
//			cancelButton = new Button(bg,SWT.PUSH);
//			cancelButton.addSelectionListener(this);
//			cancelButton.setImage(Widget.getImage(IWidgetImage.IMG_W_CANCEL32));
//		}
//
//		if(((BUTTON_OK & style) != 0) && ((BUTTON_CANCEL & style) != 0)){//有两个按钮
//			layoutData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
//			okButton.setLayoutData(layoutData);
//			layoutData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
//			cancelButton.setLayoutData(layoutData);
//		}else if((BUTTON_OK & style) != 0){
//			layoutData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1);
//			okButton.setLayoutData(layoutData);
//		}else if((BUTTON_CANCEL & style) != 0){
//			layoutData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1);
//			cancelButton.setLayoutData(layoutData);
//		}
		
//		//创建关闭按钮
//		Button close = new Button(shell,SWT.PUSH);
//		Image closeImage = Widget.getImage(IWidgetImage.IMG_W_CLOSE32);
//		close.setImage(closeImage);
//		close.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				shell.dispose();
//			}
//			
//		});
//		fd = new FormData();
//		fd.top = new FormAttachment(0);
//		fd.right = new FormAttachment(0);
//		fd.height = closeImage.getImageData().height;
//		fd.width = closeImage.getImageData().width;
		
		shell.pack();
	}

	private boolean isFloatStyle() {
		return (WINDOW_FLOAT & style) != 0;
	}

	private boolean hasImage() {
		return ((ICON_WARNING & style) != 0) || ((ICON_ERROR & style) != 0) || ((ICON_INFORMATION & style) != 0);
	}

	public void open() {
		shell.setData(RWT.CUSTOM_VARIANT, "navimenu");// 透明
//		bg.setData(WidgetUtil.CUSTOM_VARIANT, "messagebox");// 透明
		shell.open();
	}
	
	
	public static void OPEN(Shell parentShell, String title, String Message, int style){
		MessageBox mb = new MessageBox(parentShell, title, Message, style);
		mb.open();
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}


}
