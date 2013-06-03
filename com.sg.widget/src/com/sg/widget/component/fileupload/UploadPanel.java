/*****************************************************************************************
 * Copyright (c) 2010, 2012 Texas Center for Applied Technology (TEES) (TAMUS) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Austin Riddle (Texas Center for Applied Technology) - initial API and implementation
 *    EclipseSource - ongoing development
 *****************************************************************************************/
package com.sg.widget.component.fileupload;

import java.io.File;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.supplemental.fileupload.DiskFileUploadReceiver;
import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadEvent;
import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadHandler;
import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadListener;
import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadReceiver;
import org.eclipse.rap.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;

import com.sg.widget.IWidgetImage;
import com.sg.widget.Widget;

public class UploadPanel extends Composite implements FileUploadListener {

	public static final String EVENT_FILE_REMOVED = "removeFile";
	public static final String EVENT_FILE_SELECTEDED = "selectFile";
	public static final String EVENT_UPLOAD_FAILED = "uploadFailed";
	public static final String EVENT_UPLOAD_START = "uploadStart";
	public static final String EVENT_UPLOAD_FINISHED = "uploadFinished";
	public static final String EVENT_SET_ENABLE_TURE = "enabletrue";
	public static final String EVENT_STATUS_ENABLE_FALSE = "enablefalse";

	private static final long serialVersionUID = 1L;
	public static final int COMPACT = 1;
	public static final int FULL = 2;
	public static final int REMOVEABLE = 4;
	public static final int PROGRESS = 8;
	public static final int STATE_INPROGRESS = 1;
	public static final int STATE_NOFILE = 0;
	public static final int STATE_HASFILE = 2;
	public static final int STATE_ERROR = 3;
	private final int panelStyle;
	private final FileUploadHandler handler;
	private ValidationHandler validationHandler;
	private ProgressCollector progressCollector;
	private FileUpload browseButton;
	private Button fileControlButton;
	private ProgressBar progressBar;
	// private Label progressLabel;
	private Button removeButton;
	private boolean inProgress;
	private File uploadedFile;
	private String contentType;
	private boolean autoUpload;
	private String selectedFileName = "";
	private ListenerList eventlisteners = new ListenerList();
	private int currentState;
	private boolean hasFile;
	private boolean editable = true;
	private boolean enabled = true;

	public UploadPanel(Composite parent, int style) {
		super(parent, checkStyle(style));
		panelStyle = style;
		FileUploadReceiver receiver = new DiskFileUploadReceiver();
		handler = new FileUploadHandler(receiver);
		createChildren();
		stateChanged("init");
	}

	public void addSelectionListener(SelectionListener listener) {
		checkWidget();
		browseButton.addSelectionListener(listener);
	}

	public void setValidationHandler(ValidationHandler validationHandler) {
		this.validationHandler = validationHandler;
	}
	public ValidationHandler getValidationHandler(){
		return validationHandler;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		checkWidget();
		super.setEnabled(enabled);
		browseButton.setEnabled(enabled);
		fileControlButton.setEnabled(enabled);
		if (removeButton != null) {
			removeButton.setEnabled(enabled);
		}
		updateWidgetStatus();
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		if(isDisposed()){
			return;
		}
		checkWidget();
		browseButton.setEnabled(editable);
		if (removeButton != null) {
			removeButton.setEnabled(editable);
		}
		updateWidgetStatus();
	}
	
	public boolean isFinished() {
		return false;
	}

	public String getSelectedFilename() {
		checkWidget();
		return selectedFileName;
		// return fileControlButton.getText();
	}

	public String getContentType() {
		return contentType;
	}

	public File getUploadedFile() {
		return uploadedFile;
	}

	public void startUpload() {
		checkWidget();
		inProgress = true;
		String url = handler.getUploadUrl();
		handler.addUploadListener(this);
		browseButton.submit(url);
		
		stateChanged(EVENT_UPLOAD_START);
	}

	@Override
	public void dispose() {
		handler.removeUploadListener(this);
		handler.dispose();
		super.dispose();
	}

	public void setProgressCollector(ProgressCollector progressCollector) {
		this.progressCollector = progressCollector;
	}

	public void setAutoUpload(boolean autoUpload) {
		this.autoUpload = autoUpload;
	}

	public boolean isStarted() {
		return inProgress;
	}

	static int checkStyle(int style) {
		int mask = COMPACT | FULL | REMOVEABLE | PROGRESS;
		return style & mask;
	}

	private boolean hasStyle(int testStyle) {
		return (panelStyle & (testStyle)) != 0;
	}

	private void createChildren() {
		browseButton = new FileUpload(this, SWT.NONE);
		browseButton.setData(RWT.CUSTOM_VARIANT, "fileUploader");
		browseButton.setImage(Widget.getImage(IWidgetImage.IMG_OPEN16));
		browseButton.setToolTipText("Select a file");
		browseButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				String filename = browseButton.getFileName();
				fileControlButton.setText(filename);
				selectedFileName = filename;
				validate();
				if (autoUpload) {
					startUpload();
				}
			}
		});
		fileControlButton = new Button(this, SWT.PUSH);
		fileControlButton.setAlignment(SWT.LEFT);
		fileControlButton.setData(RWT.CUSTOM_VARIANT, "filedownloader");
		fileControlButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				// 此处添加打开文件的代码
				fireFileSelectedEvent();
				
			}
		});
		if (hasStyle(PROGRESS)) {
			progressBar = new ProgressBar(this, SWT.HORIZONTAL | SWT.SMOOTH);
			progressBar.setToolTipText("Upload progress");
			progressBar.setMinimum(0);
			progressBar.setMaximum(100);
			progressBar.setVisible(false);
			// progressLabel = new Label(this, SWT.NONE);
			// progressLabel.setText(progressBar.getSelection() + "%");
		}
		if (hasStyle(REMOVEABLE)) {
			removeButton = new Button(this, SWT.PUSH);
			Image removeIcon = Display.getCurrent().getSystemImage(SWT.ICON_CANCEL);
			removeButton.setImage(removeIcon);
			removeButton.setImage(Widget.getImage(IWidgetImage.IMG_DELETE16));
			removeButton.setToolTipText("Remove file");
			removeButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					if (progressCollector != null) {
						progressCollector.updateProgress(handler, 0);
					}
					removeFile();
					dispose();
				}
			});
		}
		layoutChildren();
	}


	protected void removeFile() {
		if (uploadedFile != null) {
			try {
				uploadedFile.delete();
			} catch (Exception e) {
			}
			uploadedFile = null;
		}

		// 通知侦听者,状态变化
		stateChanged(EVENT_FILE_REMOVED);
	}

	private void initFullLayout() {
		GridLayout layout = new GridLayout(5, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
	}

	private void initCompactLayout() {
		// 使用相对布局使得能够将显示的文本和进度条以及百分比重叠
		FormLayout layout = new FormLayout();
		setLayout(layout);
	}

	private void layoutChildren() {
		checkWidget();
		if (hasStyle(COMPACT)) {
			initCompactLayout();
			// 首先布局打开按钮
			FormData browseBarfd = new FormData();
			browseButton.setLayoutData(browseBarfd);
			browseBarfd.top = new FormAttachment(0, 1);
			browseBarfd.left = new FormAttachment(0, 1);
			browseBarfd.bottom = new FormAttachment(100, -1);
			browseBarfd.width = 40;
			// 布局在最右边的删除按�?
			if (removeButton != null) {
				FormData removeBtnfd = new FormData();
				removeButton.setLayoutData(removeBtnfd);
				removeBtnfd.top = new FormAttachment(0, 1);
				removeBtnfd.right = new FormAttachment(100, -1);
				removeBtnfd.bottom = new FormAttachment(100, -1);
				removeBtnfd.width = 40;
			}
			// 然后布局进度�?
			if (progressBar != null) {
				FormData progressBarfd = new FormData();
				progressBar.setLayoutData(progressBarfd);
				progressBarfd.top = new FormAttachment(0, 1);
				progressBarfd.left = new FormAttachment(browseButton, 3);
				if (removeButton != null) {
					progressBarfd.right = new FormAttachment(removeButton, -3);
				} else {
					progressBarfd.right = new FormAttachment(100, -1);
				}
				progressBarfd.bottom = new FormAttachment(100, -1);

				// 布局进度百分�?
				// FormData progressLbfd = new FormData();
				// progressLabel.setLayoutData(progressLbfd);
				// progressLbfd.top = new FormAttachment(0, 1);
				// progressLbfd.left = new FormAttachment(browseButton, 3);
				// if (removeButton != null) {
				// progressLbfd.right = new FormAttachment(removeButton, -3);
				// } else {
				// progressLbfd.right = new FormAttachment(100, -1);
				// }
				// progressLbfd.bottom = new FormAttachment(100, -1);
				// progressLabel.moveBelow(null);
			}
			// 然后布局打开按钮
			FormData fileTextfd = new FormData();
			fileControlButton.setLayoutData(fileTextfd);
			fileTextfd.top = new FormAttachment(0, 1);
			fileTextfd.left = new FormAttachment(browseButton, 3);
			if (removeButton != null) {
				fileTextfd.right = new FormAttachment(removeButton, -3);
			} else {
				fileTextfd.right = new FormAttachment(100, -1);
			}
			fileTextfd.bottom = new FormAttachment(100, -1);
			fileControlButton.moveAbove(progressBar);
		} else {
			initFullLayout();
			browseButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
			GridData textLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
			textLayoutData.minimumWidth = 186;
			textLayoutData.horizontalSpan = 4;
			fileControlButton.setLayoutData(textLayoutData);
			if (progressBar != null) {
				GridData progressLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
				progressLayoutData.horizontalSpan = 4;
				progressBar.setLayoutData(progressLayoutData);
				// GridData lblLayoutData = new GridData(SWT.FILL, SWT.FILL,
				// false, false);
				// float avgCharWidth =
				// Graphics.getAvgCharWidth(progressLabel.getFont());
				// lblLayoutData.minimumWidth = (int) avgCharWidth * 6;
				// lblLayoutData.widthHint = (int) avgCharWidth * 6;
				// progressLabel.setLayoutData(lblLayoutData);
			}
			if (removeButton != null) {
				removeButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
			}
		}
	}

	private int getState() {
		if (inProgress) {
			return STATE_INPROGRESS;
		} else if (uploadedFile != null||hasFile) {
			return STATE_HASFILE;
		} else if (uploadedFile == null && currentState == STATE_INPROGRESS) {
			return STATE_ERROR;
		} else {
			return STATE_NOFILE;
		}
	}

	public void setHasFile(boolean hasFile){
		this.hasFile = hasFile;
		stateChanged(null);
	}
	
	private void stateChanged(String eventCode) {

		int _currentState = getState();
		if(eventCode!=null){
			if (!eventlisteners.isEmpty()) {
				Object[] _listeners = eventlisteners.getListeners();
				for (int i = 0; i < _listeners.length; i++) {
					IUploadListener lis = (IUploadListener) _listeners[i];
					lis.event(eventCode, _currentState, currentState, getUploadedFile(), selectedFileName, this);
				}
			}
		}

		if (_currentState != currentState) {
			currentState = _currentState;
			updateWidgetStatus();
		}
	}
	

	protected void fireFileSelectedEvent() {
		if (!eventlisteners.isEmpty()) {
			Object[] _listeners = eventlisteners.getListeners();
			for (int i = 0; i < _listeners.length; i++) {
				IUploadListener lis = (IUploadListener) _listeners[i];
				lis.event(EVENT_FILE_SELECTEDED, currentState, currentState, getUploadedFile(), selectedFileName, this);
			}
		}
	}

	private void updateWidgetStatus() {
		if(!editable){
			return;
		}

		if(!enabled){
			return;
		}

		
		if (isDisposed()) {
			return;
		}

		// 如果不是可用的状不修
		if (!getEnabled()) {
			return;
		}

		switch (currentState) {
		// 没有文件的时选择上传文件的按钮可文件控制按钮不可进度条不显示,删除不可百分比不显示
		// setEnabled
		case STATE_NOFILE:
			if (browseButton != null && !browseButton.isDisposed())
				browseButton.setEnabled(true);
			if (progressBar != null && !progressBar.isDisposed())
				progressBar.setVisible(false);
			// if (progressLabel != null && !progressLabel.isDisposed())
			// progressLabel.setVisible(false);
			if (removeButton != null && !removeButton.isDisposed())
				removeButton.setEnabled(false);
			if (fileControlButton != null && !fileControlButton.isDisposed())
				fileControlButton.setEnabled(false);
			break;
		// 有文件的时选择上传文件的按钮可进度条不显示,删除可用,百分比不显示
		case STATE_HASFILE:
			if (browseButton != null && !browseButton.isDisposed())
				browseButton.setEnabled(false);
			if (progressBar != null && !progressBar.isDisposed())
				progressBar.setVisible(false);
			// if (progressLabel != null && !progressLabel.isDisposed())
			// progressLabel.setVisible(false);
			if (removeButton != null && !removeButton.isDisposed())
				removeButton.setEnabled(true);
			if (fileControlButton != null && !fileControlButton.isDisposed())
				fileControlButton.setEnabled(true);
			break;
		// 上传中的时,选择上传文件的按钮不可用,进度条显?删除不可用显�?百分比显�?
		case STATE_INPROGRESS:
			if (browseButton != null && !browseButton.isDisposed())
				browseButton.setEnabled(false);
			if (progressBar != null && !progressBar.isDisposed())
				progressBar.setVisible(true);
			// if (progressLabel != null && !progressLabel.isDisposed())
			// progressLabel.setVisible(true);
			if (removeButton != null && !removeButton.isDisposed())
				removeButton.setEnabled(true);
			if (fileControlButton != null && !fileControlButton.isDisposed())
				fileControlButton.setEnabled(true);
			break;
		}
		// TODO Auto-generated method stub

	}

	public void validate() {
		// if( validationHandler == null || validationHandler.validate(
		// fileControlButton.getText() ) ) {
		// fileControlButton.setToolTipText( "Selected file" );
		// // TODO replace this with something from theming
		// fileControlButton.setBackground( null );
		// } else {
		// fileControlButton.setToolTipText(
		// "Warning: Selected file does not match filter" );
		// // TODO replace this with something from theming
		// fileControlButton.setBackground( Display.getCurrent().getSystemColor(
		// SWT.COLOR_YELLOW ) );
		// validationHandler.updateEnablement();
		// }
	}

	public void uploadProgress(final FileUploadEvent uploadEvent) {
		// checkWidget();
		browseButton.getDisplay().asyncExec(new Runnable() {

			public void run() {
				double fraction = uploadEvent.getBytesRead() / (double) uploadEvent.getContentLength();
				int percent = (int) Math.floor(fraction * 100);
				if (progressBar != null && !progressBar.isDisposed()) {
					progressBar.setSelection(percent);
					progressBar.setToolTipText("Upload progress: " + percent + "%");
					// progressLabel.setText(percent + "%");
					fileControlButton.setText(percent + "% " + selectedFileName);
				}
				// allow the uploadFinished call to notify collector of 100%
				// progress
				// since
				// the file is actually written then
				if (progressCollector != null && percent < 100) {
					progressCollector.updateProgress(handler, percent);
				}
			}
		});
	}

	public void uploadFinished(final FileUploadEvent uploadEvent) {
		// checkWidget();
		DiskFileUploadReceiver receiver = (DiskFileUploadReceiver) handler.getReceiver();
//		//如果本来就有文件,首先触发删除事件
//		if(uploadedFile!=null){
//			stateChanged(EVENT_FILE_REMOVED);
//		}
		uploadedFile = receiver.getTargetFile();
		contentType = uploadEvent.getContentType();
		browseButton.getDisplay().asyncExec(new Runnable() {

			public void run() {
				int percent = 100;
				if (progressBar != null && !progressBar.isDisposed()) {
					progressBar.setSelection(percent);
					progressBar.setToolTipText("Upload progress: " + percent + "%");
					// progressLabel.setText(percent + "%");
					fileControlButton.setText(selectedFileName);
				}
				if (progressCollector != null) {
					progressCollector.updateProgress(handler, percent);
				}
				inProgress = false;
				stateChanged(EVENT_UPLOAD_FINISHED);
			}
		});
	}

	public void uploadFailed(final FileUploadEvent uploadEvent) {
		// checkWidget();
		uploadedFile = null;
		contentType = null;
		browseButton.getDisplay().asyncExec(new Runnable() {

			public void run() {
				if (progressBar != null && !progressBar.isDisposed()) {
					progressBar.setState(SWT.ERROR);
					progressBar.setToolTipText(uploadEvent.getException().getMessage());
				}
				inProgress = false;
				stateChanged(EVENT_UPLOAD_FAILED);
			}
		});
	}

	public void addUploadListener(IUploadListener listener) {
		eventlisteners.add(listener);
	}

	public void removeUploadListener(IUploadListener listener) {
		eventlisteners.remove(listener);
	}

	public void setDisplayFileName(String fileName) {
		this.fileControlButton.setText(fileName);
	}

}