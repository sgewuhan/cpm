package com.sg.widget.editor.field;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.Assert;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.lifecycle.UICallBack;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IMessageManager;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.RemoteFile;
import com.sg.db.model.RemoteFileSet;
import com.sg.widget.IWidgetImage;
import com.sg.widget.Widget;
import com.sg.widget.component.fileupload.FileDialog;
import com.sg.widget.component.fileupload.IUploadListener;
import com.sg.widget.component.fileupload.UploadPanel;
import com.sg.widget.configuration.FieldConfiguration;
import com.sg.widget.util.FileUtil;

public class FileFieldPart extends AssistantFieldPart implements
		IUploadListener {

	private static final String BINDING_DATA = "controlData";
	private Control control;
	private List<UploadPanel> uploadPanels;
	private ScrolledComposite uploadScroller;
	private Composite scrollChild;
	private Composite uploadsWrapper;
	private Button addFileSelectorButton;

	private RemoteFileSet fileSet;
	private boolean isMulti;
	private int uploadPanelStyle;

	public FileFieldPart(Composite parent, FieldConfiguration cfield,
			IEditorInput input) {
		super(parent, cfield, input);
	}

	private void initializeDefaults() {
		// 传入默认的命名空间
		fileSet = new RemoteFileSet(field.getNamespace());

		uploadPanelStyle = UploadPanel.COMPACT | UploadPanel.PROGRESS
				| UploadPanel.REMOVEABLE;

		uploadPanels = new ArrayList<UploadPanel>();

		// 使背景线程能够更新界面
		UICallBack.activate(FileFieldPart.class.getName() + hashCode());
	}

	@Override
	protected void createControl(Composite parent) {
		String fieldType = field.getType();
		String editorType = field.getEditPart();
		initializeDefaults();

		// 文件类型的字段只能是对应文件类型的数据
		if (!(IFieldTypeConstants.FIELD_FILE.equals(fieldType))) {
			Assert.isLegal(false, TYPE_MISMATCH + field.getId());
		}

		isMulti = IFieldTypeConstants.TYPE_FILE_MULTI.equals(editorType);
		control = createUploadArea(parent, isMulti);

		GridData td = getControlLayoutData();
		if (isMulti)
			td.heightHint = field.getHeightHint() == 0 ? 160 : field
					.getHeightHint();
		control.setLayoutData(td);

		// 设置哪些事件可以设置值,如何设置值
		super.createControl(parent);
	}

	private Control createUploadArea(Composite parentComposite,
			boolean isMultiSelector) {
		Composite main = new Composite(parentComposite, SWT.NONE);
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		GridLayout layout = new GridLayout(1, true);
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		main.setLayout(layout);
		if (isMultiSelector) {
			main.setData(RWT.CUSTOM_VARIANT, "multiFileField");
			createMultiSelector(main);
		} else {
			createSingleSelector(main);
		}
		if (isMultiSelector) {
			createAddSelectorButton(scrollChild);
		}
		return main;
	}

	private void createSingleSelector(Composite main) {
		UploadPanel uploadPanel = new UploadPanel(main, uploadPanelStyle);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		uploadPanel.setLayoutData(layoutData);
		uploadPanel.setAutoUpload(getAutoUpload());
		// 添加上传状态侦听器
		// 绑定数据
		uploadPanel.addUploadListener(this);
		uploadPanel.setData(BINDING_DATA,
				RemoteFile.createEmptyRemoteFile(fileSet.getNamespace()));
		uploadPanels.add(uploadPanel);
	}

	private boolean getAutoUpload() {
		return true;
	}

	private void createMultiSelector(Composite main) {
		uploadScroller = new ScrolledComposite(main, SWT.V_SCROLL);
		uploadScroller.setExpandHorizontal(true);
		uploadScroller.setExpandVertical(true);
		GridData uploadScrollerLayoutData = new GridData(SWT.FILL, SWT.FILL,
				true, true);
		uploadScroller.setLayoutData(uploadScrollerLayoutData);
		scrollChild = new Composite(uploadScroller, SWT.NONE);
		GridLayout scrollChildLayout = new GridLayout(1, true);
		scrollChildLayout.marginBottom = 0;
		scrollChildLayout.marginTop = 0;
		scrollChildLayout.marginLeft = 0;
		scrollChildLayout.marginRight = 0;
		scrollChild.setLayout(scrollChildLayout);
		uploadsWrapper = new Composite(scrollChild, SWT.NONE);
		GridLayout uploadWrapperLayout = new GridLayout(1, true);
		// [if] marginBottom = 1 is needed to avoid default composite size (64)
		// if it is empty
		uploadWrapperLayout.marginBottom = 1;
		uploadWrapperLayout.marginWidth = 0;
		uploadWrapperLayout.marginHeight = 0;
		uploadWrapperLayout.marginLeft = 0;
		uploadsWrapper.setLayout(uploadWrapperLayout);
		uploadsWrapper.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
				false));
		addUploadPanel(RemoteFile.createEmptyRemoteFile(fileSet.getNamespace()));
		uploadScroller.setContent(scrollChild);
		uploadScroller.setMinSize(scrollChild.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
	}

	private void createAddSelectorButton(Composite parent) {
		addFileSelectorButton = new Button(parent, SWT.PUSH);
		addFileSelectorButton.setImage(Widget.getImage(IWidgetImage.IMG_ADD16));
		addFileSelectorButton.setToolTipText("Add file");
		addFileSelectorButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createUploadPanel(RemoteFile.createEmptyRemoteFile(fileSet
						.getNamespace()));
			}
		});

		GridData layoutData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		layoutData.widthHint = 40;
		addFileSelectorButton.setLayoutData(layoutData);
	}

	protected UploadPanel createUploadPanel(Object bindingData) {
		final UploadPanel uploadPanel = addUploadPanel(bindingData);
		uploadScroller.setMinSize(scrollChild.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
		// [if] workaround for ScrolledComposite scroll issue - see bug
		// 349301 and 349301
		uploadPanel.setEnabled(false);
		uploadPanel.setVisible(false);
		uploadScroller.getDisplay().timerExec(10, new Runnable() {
			public void run() {
				uploadPanel.setEnabled(true);
				uploadPanel.setVisible(true);
				int scrollTop = scrollChild.getSize().y
						- uploadScroller.getClientArea().y;
				uploadScroller.setOrigin(0, Math.max(0, scrollTop));
			}
		});
		return uploadPanel;
	}

	private UploadPanel addUploadPanel(Object bindingData) {
		final UploadPanel uploadPanel = new UploadPanel(uploadsWrapper,
				uploadPanelStyle);
		// 添加上传状态侦听器
		uploadPanel.addUploadListener(this);
		// 绑定数据
		uploadPanel.setData(BINDING_DATA, bindingData);
		uploadPanel.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				Display.getCurrent().asyncExec(new Runnable() {
					public void run() {
						if (!uploadsWrapper.isDisposed()) {
							uploadPanels.remove(uploadPanel);
							scrollChild.pack(true);
							uploadScroller.setMinSize(scrollChild.computeSize(
									SWT.DEFAULT, SWT.DEFAULT));
						}
					}
				});
			}
		});
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		uploadPanel.setLayoutData(layoutData);
		uploadPanel.setAutoUpload(getAutoUpload());
		uploadPanels.add(uploadPanel);
		scrollChild.pack(true);
		return uploadPanel;
	}

	@Override
	public Control getControl() {
		return control;
	}

	@Override
	protected Object getInputValue() {
		// 打开文件的时候被调用
		// 需要根据文件的值来设置fileSet
		Object inputValue = super.getInputValue();

		if (inputValue instanceof BasicDBList) {
			BasicDBList fileList = (BasicDBList) inputValue;
			Iterator<Object> iter = fileList.iterator();
			while (iter.hasNext()) {
				DBObject dbo = (DBObject) iter.next();
				String fileName = (String) dbo.get("fileName");
				ObjectId oid = (ObjectId) dbo.get("_id");
				String namespace = (String) dbo.get("namespace");
				RemoteFile rf = RemoteFile.createRemoteFileFromDB(fileName,
						namespace, oid);
				fileSet.initAdd(rf);
			}
		}
		return inputValue;
	}

	@Override
	protected void presentValue(ISingleObject data, Object value,
			String presentValue) {
		Set<RemoteFile> updatedRemoteFileSet = fileSet
				.getUpdatedRemoteFileSet();
		List<RemoteFile> l = new ArrayList<RemoteFile>();
		l.addAll(updatedRemoteFileSet);
		Collections.sort(l);

		Iterator<RemoteFile> iter = l.iterator();
		if (iter.hasNext()) {// uploadpanels have one uploadpanel at least so
								// updated it first!
			RemoteFile rf = iter.next();
			UploadPanel panel = uploadPanels.get(0);
			panel.setData(BINDING_DATA, rf);
			panel.setHasFile(rf.hasFile());
			panel.setDisplayFileName(rf.getFileName());
		}

		if (isMulti) {
			while (iter.hasNext()) {
				RemoteFile rf = iter.next();
				UploadPanel panel = createUploadPanel(rf);
				panel.setHasFile(rf.hasFile());
				panel.setDisplayFileName(rf.getFileName());
			}
		}
	}

	@Override
	protected Object getValueForUpdate(IMessageManager messageManager) {
		// 返回的到准备更新编辑器的值
		// 这个值的格式是remoteFileSet
		BasicDBList output = fileSet.getUpdatedData();

		return output;
	}

	@Override
	public boolean isDirty() {
		return fileSet.isDirty();
	}

	@Override
	protected void setEditable(boolean editable) {
		
		if (control.isDisposed() || control == null) {
			return;
		}

		if (uploadPanels == null) {
			return;
		}

		for (int i = 0; i < uploadPanels.size(); i++) {
			UploadPanel up = uploadPanels.get(i);
			up.setEditable(editable);
		}
		if (addFileSelectorButton != null) {
			addFileSelectorButton.setEnabled(editable);
		}
	}

	@Override
	public void dispose() {
		UICallBack.deactivate(FileDialog.class.getName() + hashCode());
		super.dispose();
	}

	@Override
	public void commit(boolean onSave) {
		if (!onSave) {
			// it will be called before save
			// add code to save file
			fileSet.saveServerFileToDB();
		}
		super.commit(onSave);
	}

	@Override
	public void event(String statusCode, int newState, int oldState,
			File uploadedFile, String selectedFileName,
			UploadPanel uploadControl) {
		// 根据上传状态的改变来修改fileSet

		// 上传完成
		if (UploadPanel.EVENT_UPLOAD_FINISHED.equals(statusCode)) {
			RemoteFile rf = (RemoteFile) uploadControl.getData(BINDING_DATA);
			rf.setFileUploaded(selectedFileName, uploadedFile, new ObjectId());
			fileSet.add(rf);

			// 调用这一句用于更新SingleDataObject
			updateDataValue();

		} else if (UploadPanel.EVENT_FILE_REMOVED.equals(statusCode)) {
			RemoteFile rf = (RemoteFile) uploadControl.getData(BINDING_DATA);
			rf.setFileRemoved();
			fileSet.remove(rf);

			// 调用这一句用于更新SingleDataObject
			updateDataValue();

		} else if (UploadPanel.EVENT_FILE_SELECTEDED.equals(statusCode)) {
			// 选中了一个文件,准备下载
			RemoteFile rf = (RemoteFile) uploadControl.getData(BINDING_DATA);
			if (rf.getStatus() == RemoteFile.FILE_IN_DB) {

				FileUtil.downloadFromGridFS(rf.getNamespace(),
						rf.getObjectId(), rf.getFileName());
			} else if (rf.getStatus() == RemoteFile.FILE_UPLOADED) {
				FileUtil.download(rf.getServerFile().getPath(),
						rf.getFileName());
			}

		}
	}

}
