package com.sg.cpm;

import java.util.GregorianCalendar;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.ui.UIConstants;
import com.sg.db.DBActivator;
import com.sg.db.model.SingleObject;
import com.sg.design.ext.IHeadAreaSupport;
import com.sg.resource.Resource;
import com.sg.user.UserService;
import com.sg.widget.dialog.ISingleObjectEditorDialogCallback;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.dialog.SingleObjectEditorDialogCallback;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.SingleObjectEditorInput;

public class UserHeader implements IHeadAreaSupport {

	private static final int head_height = 64;// 图片的固定高度

	private static final int head_width = 64;// 图片的固定宽度

	private static final int head_margin = 3;// 图片在图片容器内部的距离各个边框的距离

	private static final int bottomMargin = 13;// 整个控件距离控件父区域的底部像素，要考虑与logo的一致，参见logo的initializer

	public UserHeader() {

	}

	@Override
	public Composite creatHeadAreaPart(Composite parent) {

		// 创建用户头像区
		parent.setBackgroundMode(SWT.INHERIT_NONE);

		Composite headPicContainer = new Composite(parent, SWT.NONE);
//		headPicContainer.setData(WidgetUtil.CUSTOM_VARIANT, "headpic");

		headPicContainer.setLayout(new FormLayout());

		FormData fd = new FormData();
		headPicContainer.setLayoutData(fd);
		fd.height = head_height + head_margin * 2;
		fd.width = head_width + head_margin * 2;
		fd.bottom = new FormAttachment(100, -bottomMargin);
		fd.left = new FormAttachment(0);

		Label headerPic = new Label(headPicContainer, SWT.NONE);
		final DBObject user = BusinessService.getOrganizationService().getCurrentUserData();
		Image image = DataUtil.getUserImage(user);
		
		
		headerPic.setImage(image);
		fd = new FormData();
		headerPic.setLayoutData(fd);
		fd.bottom = new FormAttachment(100, -head_margin);
		fd.left = new FormAttachment(0,head_margin);
		fd.top = new FormAttachment(0,head_margin);
		fd.right = new FormAttachment(100, -head_margin);
		headerPic.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				editUserProfile(user);
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});

		Label welcomeMessage = new Label(parent, SWT.NONE);
		welcomeMessage.setData(RWT.CUSTOM_VARIANT, "welcomemessage");
		welcomeMessage.setText(getTimeString() + " " + user.get(IDBConstants.FIELD_NAME)+"");
		fd = new FormData();
		welcomeMessage.setLayoutData(fd);
		fd.bottom = new FormAttachment(100, -bottomMargin);
		fd.left = new FormAttachment(headPicContainer,6);
		
		//在welCome上面添加一个 小的应用图标
		Label logo = new Label(parent,SWT.NONE);
		
		
		Image logoImage = UserService.getDefault().getHeaderLogo();
		
		logo.setImage(logoImage);
		fd = new FormData();
		logo.setLayoutData(fd);
		fd.bottom = new FormAttachment(welcomeMessage,-10);
		fd.right = new FormAttachment(welcomeMessage,0,SWT.RIGHT);
		fd.height = logoImage.getBounds().height;
		fd.width = logoImage.getBounds().width;
		
		return headPicContainer;
	}

	protected void editUserProfile(DBObject user) {

		SingleObjectEditorInput editInput = new SingleObjectEditorInput(new SingleObject(DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_USER),user));
		ISingleObjectEditorDialogCallback call = new SingleObjectEditorDialogCallback() {

			@Override
			public boolean saveBefore(ISingleObjectEditorInput input) {
				SingleObject data = (SingleObject) input.getInputData();

				// 将用户名转换为小写
				String userName = (String) data.getValue(IDBConstants.FIELD_DESC);
				if (userName == null) {
					userName = "";
				}
				data.setValue(IDBConstants.FIELD_DESC, userName);

				return super.saveBefore(input);
			}
		};
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		SingleObjectEditorDialog.OPEN(shell, UIConstants.EDITOR_USER_SELF_EDIT, editInput, call, false);

	}

	private static String getTimeString() {

		GregorianCalendar ca = new GregorianCalendar();
		int h = ca.get(GregorianCalendar.HOUR_OF_DAY);
		switch (h) {
		case 0:
		case 1:
		case 2:
		case 3:
			return "已经是凌晨了，";
		case 4:
		case 5:
			return "已经是清晨了，";
		case 6:
		case 7:
			return "Good Morning, ";
		case 8:
		case 9:
		case 10:
		case 11:
			return "上午好，";
		case 12:
			return "午饭时间到了，";
		case 13:
			return "中午好，";
		case 14:
		case 15:
		case 16:
		case 17:
		case 18:
			return "下午好，";
		case 19:
		case 20:
		case 21:
		case 22:
			return "晚上好，";
		case 23:
		case 24:
			return "zzZ..  ";
		}
		return "";
	}

	@Override
	public Image getCenterLogo() {

		return Resource.getImage(Resource.ENT_LOGO);
	}

}
