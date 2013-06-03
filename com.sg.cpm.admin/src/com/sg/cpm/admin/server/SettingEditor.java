package com.sg.cpm.admin.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.supplemental.fileupload.DiskFileUploadReceiver;
import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadEvent;
import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadHandler;
import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadListener;
import org.eclipse.rap.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.FormAttachment;
//import org.eclipse.swt.layout.FormData;
//import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.sg.common.BusinessService;
import com.sg.cpm.admin.AdminFunctionEditor;
import com.sg.resource.Resource;
import com.sg.user.UserService;
import com.sg.widget.util.FileUtil;
import com.sg.widget.util.ImageUtil;

public class SettingEditor extends AdminFunctionEditor  {

	private static final int SIZE = 96;
	private FileUploadHandler handler1;
	private FileUploadHandler handler2;
	private FileUploadHandler handler3;
	private FileUpload selectMainLogo;
	private Button refreshButton;
	private FileUpload selectHeadLogo;
	private FileUpload selectProductName;
	private Button schedualButton;
	private Button timeRuleButton;
	private Button workRetrieveIntervalButton;

	@Override
	public void createPartControl(Composite parent) {
	    parent.setLayout( new GridLayout() );
	    ScrolledComposite composite = new ScrolledComposite( parent,SWT.V_SCROLL );
	    composite.setLayoutData( new GridData( GridData.FILL,
	                                           GridData.FILL,
	                                           true,
	                                           true ) );
	    Composite content = new Composite( composite, SWT.NONE );
		
	    content.setLayout(new GridLayout(2,false));
		handler1 = new FileUploadHandler( new DiskFileUploadReceiver());
		handler2 = new FileUploadHandler( new DiskFileUploadReceiver());
		handler3 = new FileUploadHandler( new DiskFileUploadReceiver());
		
		
		//设置刷新的频率
		createRefreshButton(content);
		
		//主屏logo
		createMainScreenLogo(content);
		
		createMainScreenProductName(content);
		
		//设置顶端logo
		createHeadLogo(content);
		
		//设置定制任务
		createTimeSchedual(content);
		
		createJobTimeRule(content);
		
		//设置提示服务器的间隔时间
		createWorkRetriever(content);
		
		
	    composite.setContent( content );
	    composite.setExpandHorizontal( true );
	    composite.setExpandVertical( true );
	    composite.setMinSize( content.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
		super.createPartControl(parent);
	}
	
	

	private void createWorkRetriever(Composite parent) {
		workRetrieveIntervalButton = new Button(parent,SWT.PUSH);
		workRetrieveIntervalButton.setLayoutData(getButtonLayoutData());
//		FormData fd = new FormData();
//		timeRuleButton.setLayoutData(fd);
//		fd.top = new FormAttachment(schedualButton,margin);
//		fd.left = new FormAttachment(0,margin);
//		fd.width = SIZE;
//		fd.height = SIZE;
		
		workRetrieveIntervalButton.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				BusinessService service = BusinessService.getDefault();
				IInputValidator validator = new IInputValidator() {
					
					@Override
					public String isValid(String newText) {
						try{
							int i = Integer.parseInt(newText);
							if(i>=10){
								return null;
							}
						}catch (Exception e){
						}
						return "至少间隔10分钟";
					}
				};
				InputDialog inputDialog = new InputDialog(getSite().getShell(), "设置工作检查服务的时间间隔", "请输入分钟数", ""+service.getWorkRetrieveInterval(), validator );
				int ok = inputDialog.open();
				if(ok==InputDialog.OK){
					String value = inputDialog.getValue();
					service.setWorkRetrieveInterval(Integer.parseInt(value));
				}
			}
			
		});
		
		workRetrieveIntervalButton.setImage(Resource.getImage(Resource.IMAGE_SCHEDUAL96));
		
		Label label = new Label(parent,SWT.NONE);
		label.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		label.setText("<strong>设置服务器工作检查循环时间间隔</strong><br/><br/>服务器根据此项设定按时间检查工作。请输入分钟数，间隔不少于10分钟.<br/>");		
	}



	private void createJobTimeRule(Composite parent) {
		timeRuleButton = new Button(parent,SWT.PUSH);
		timeRuleButton.setLayoutData(getButtonLayoutData());
//		FormData fd = new FormData();
//		timeRuleButton.setLayoutData(fd);
//		fd.top = new FormAttachment(schedualButton,margin);
//		fd.left = new FormAttachment(0,margin);
//		fd.width = SIZE;
//		fd.height = SIZE;
		
		timeRuleButton.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				BusinessService service = BusinessService.getDefault();
				IInputValidator validator = new IInputValidator() {
					
					@Override
					public String isValid(String newText) {
						return null;
					}
				};
				InputDialog inputDialog = new InputDialog(getSite().getShell(), "设置日任务检定时间", "请输入hh:mm:ss格式的时间", ""+service.getJobTrigerTime(), validator );
				int ok = inputDialog.open();
				if(ok==InputDialog.OK){
					String value = inputDialog.getValue();
					service.setJobTrigerTime(value);
				}
			}
			
		});
		
		timeRuleButton.setImage(Resource.getImage(Resource.IMAGE_SCHEDUAL96));
		
		Label label = new Label(parent,SWT.NONE);
		label.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		label.setText("<strong>设置服务器日任务检查时间</strong><br/><br/>服务器根据此项设定按时间检查任务。默认设置为每天4:00:00<br/>");
//		fd = new FormData();
//		label.setLayoutData(fd);
//		fd.top = new FormAttachment(timeRuleButton,-SIZE);
//		fd.left = new FormAttachment(timeRuleButton,margin);
//		fd.right = new FormAttachment(100,-margin);				
	}

	private Object getButtonLayoutData() {

		GridData gd = new GridData(SWT.CENTER,SWT.CENTER,false,false);
		gd.widthHint = SIZE;
		gd.heightHint = SIZE;
		return gd;
	}



	private void createTimeSchedual(Composite parent) {
		schedualButton = new Button(parent,SWT.PUSH);
		schedualButton.setLayoutData(getButtonLayoutData());
//		FormData fd = new FormData();
//		schedualButton.setLayoutData(fd);
//		fd.top = new FormAttachment(selectHeadLogo,margin);
//		fd.left = new FormAttachment(0,margin);
//		fd.width = SIZE;
//		fd.height = SIZE;
		
		schedualButton.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				BusinessService service = BusinessService.getDefault();
				IInputValidator validator = new IInputValidator() {
					
					@Override
					public String isValid(String newText) {
						try{
							int i = Integer.parseInt(newText);
							if(i>=1&&i<=28){
								return null;
							}
						}catch (Exception e){
						}
						return "请输入1～28之间的一个数字";
					}
				};
				InputDialog inputDialog = new InputDialog(getSite().getShell(), "设置月报工作开始日期", "请输入1～28之间的一个数字", ""+service.getMonthlyReportDate(), validator );
				int ok = inputDialog.open();
				if(ok==InputDialog.OK){
					String value = inputDialog.getValue();
					int i = Integer.parseInt(value);
					service.setMonthlyReportDate(i);
				}
			}
			
		});
		
		schedualButton.setImage(Resource.getImage(Resource.IMAGE_SCHEDUAL96));
		
		Label label = new Label(parent,SWT.NONE);
		label.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		label.setText("<strong>设置项目月报的定期启动时间</strong><br/><br/>服务器根据此项设定定期启动项目月报工作。<br/>");
//		fd = new FormData();
//		label.setLayoutData(fd);
//		fd.top = new FormAttachment(schedualButton,-SIZE);
//		fd.left = new FormAttachment(schedualButton,margin);
//		fd.right = new FormAttachment(100,-margin);		
	}

	private void createHeadLogo(Composite parent) {
		selectHeadLogo = new FileUpload(parent, SWT.NONE);
		selectHeadLogo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		selectHeadLogo.setData(RWT.CUSTOM_VARIANT, "fileUploader");
		setHeadLogoImage();
		selectHeadLogo.setLayoutData(getButtonLayoutData());

//		FormData fd = new FormData();
//		selectHeadLogo.setLayoutData(fd);
//		fd.top = new FormAttachment(selectProductName,margin);
//		fd.left = new FormAttachment(0,margin);
//		fd.width = SIZE;
//		fd.height= SIZE;

		selectHeadLogo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				String filename = selectHeadLogo.getFileName();
				if(!filename.toLowerCase().endsWith(".png")){
					MessageDialog.openInformation(getSite().getShell(),"上传主界面标题栏的图片", "您需要选择后缀名为.png的图片");
					return;
				}
				
				String url = handler1.getUploadUrl();
				handler1.addUploadListener(new FileUploadListener() {
					
					@Override
					public void uploadProgress(FileUploadEvent event) {
					}
					
					@Override
					public void uploadFinished(FileUploadEvent event) {
						DiskFileUploadReceiver receiver = (DiskFileUploadReceiver) handler1.getReceiver();
						File uploadedFile = receiver.getTargetFile();
						try {
							FileInputStream in = new FileInputStream(uploadedFile);
							FileUtil.remove(UserService.HEAD_FILENAME, UserService.CPMSYSTEM_FILE_NAMESPACE);
							FileUtil.upload(in, UserService.HEAD_FILENAME, UserService.CPMSYSTEM_FILE_NAMESPACE);
							in.close();
							selectHeadLogo.getDisplay().asyncExec(new Runnable() {

								@Override
								public void run() {
									setHeadLogoImage();
								}
							});
						} catch (FileNotFoundException e) {
						} catch (IOException e) {
						}
					}
					
					@Override
					public void uploadFailed(FileUploadEvent event) {
						
					}
				});
				selectHeadLogo.submit(url);
			}
		});
		Label label = new Label(parent,SWT.NONE);
		label.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		label.setText("<strong>设置系统主页的产品名称的图片</strong><br/><br/>请选择后缀名称为.png的图片文件作为标题栏Logo。这个图片将显示在主界面的标题栏左方.<br/>图片的尺寸需要限制以确保在标题栏上完整显示.<br/>图片的宽度在100～200之间，高度不超过40");
//		fd = new FormData();
//		label.setLayoutData(fd);
//		fd.top = new FormAttachment(selectHeadLogo ,-SIZE);
//		fd.left = new FormAttachment(selectHeadLogo,margin);
//		fd.right = new FormAttachment(100,-margin);
	}

	private void createMainScreenProductName(Composite parent) {
		selectProductName = new FileUpload(parent, SWT.NONE);
		selectProductName.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		selectProductName.setData(RWT.CUSTOM_VARIANT, "fileUploader");
		setProductNameImage();
		selectProductName.setLayoutData(getButtonLayoutData());

//		FormData fd = new FormData();
//		selectProductName.setLayoutData(fd);
//		fd.top = new FormAttachment(selectMainLogo,margin);
//		fd.left = new FormAttachment(0,margin);
//		fd.width = SIZE;
//		fd.height= SIZE;

		selectProductName.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				String filename = selectProductName.getFileName();
				if(!filename.toLowerCase().endsWith(".png")){
					MessageDialog.openInformation(getSite().getShell(),"上传产品名称的图片", "您需要选择后缀名为.png的图片");
					return;
				}
				
				String url = handler2.getUploadUrl();
				handler2.addUploadListener(new FileUploadListener() {
					
					@Override
					public void uploadProgress(FileUploadEvent event) {
					}
					
					@Override
					public void uploadFinished(FileUploadEvent event) {
						DiskFileUploadReceiver receiver = (DiskFileUploadReceiver) handler2.getReceiver();
						File uploadedFile = receiver.getTargetFile();
						try {
							FileInputStream in = new FileInputStream(uploadedFile);
							FileUtil.remove(UserService.APPNAME_FILENAME, UserService.CPMSYSTEM_FILE_NAMESPACE);
							FileUtil.upload(in, UserService.APPNAME_FILENAME, UserService.CPMSYSTEM_FILE_NAMESPACE);
							in.close();
							selectProductName.getDisplay().asyncExec(new Runnable() {

								@Override
								public void run() {
									setProductNameImage();
								}
							});
						} catch (FileNotFoundException e) {
						} catch (IOException e) {
						}
					}
					
					@Override
					public void uploadFailed(FileUploadEvent event) {
						
					}
				});
				selectProductName.submit(url);
			}
		});
		Label label = new Label(parent,SWT.NONE);
		label.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		label.setText("<strong>设置系统主页的产品名称的图片</strong><br/><br/>请选择后缀名称为.png的图片文件。这个图片将在用户登录时显示在LOGO的下方.<br/>系统不限制图片的尺寸，但也不应太小或太大.<br/>建议图片的宽度在200～400之间，高度30～60之间");
//		fd = new FormData();
//		label.setLayoutData(fd);
//		fd.top = new FormAttachment(selectProductName ,-SIZE);
//		fd.left = new FormAttachment(selectProductName,margin);
//		fd.right = new FormAttachment(100,-margin);
	}

	private void createMainScreenLogo(Composite parent) {
		selectMainLogo = new FileUpload(parent, SWT.NONE);
		selectMainLogo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		selectMainLogo.setData(RWT.CUSTOM_VARIANT, "fileUploader");
		setMainLogoImage();
		selectMainLogo.setLayoutData(getButtonLayoutData());

//		FormData fd = new FormData();
//		selectMainLogo.setLayoutData(fd);
//		fd.top = new FormAttachment(refreshButton,margin);
//		fd.left = new FormAttachment(0,margin);
//		fd.width = SIZE;
//		fd.height= SIZE;

		selectMainLogo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				String filename = selectMainLogo.getFileName();
				if(!filename.toLowerCase().endsWith(".png")){
					MessageDialog.openInformation(getSite().getShell(),"上传应用登录图片", "您需要选择后缀名为.png的图片");
					return;
				}
				
				String url = handler3.getUploadUrl();
				handler3.addUploadListener(new FileUploadListener() {
					
					@Override
					public void uploadProgress(FileUploadEvent event) {
					}
					
					@Override
					public void uploadFinished(FileUploadEvent event) {
						DiskFileUploadReceiver receiver = (DiskFileUploadReceiver) handler3.getReceiver();
						File uploadedFile = receiver.getTargetFile();
						try {
							FileInputStream in = new FileInputStream(uploadedFile);
							FileUtil.remove(UserService.BIGLOGO_FILENAME, UserService.CPMSYSTEM_FILE_NAMESPACE);
							FileUtil.upload(in, UserService.BIGLOGO_FILENAME, UserService.CPMSYSTEM_FILE_NAMESPACE);
							in.close();
							selectMainLogo.getDisplay().asyncExec(new Runnable() {

								@Override
								public void run() {
									setMainLogoImage();
								}
							});
						} catch (FileNotFoundException e) {
						} catch (IOException e) {
						}
					}
					
					@Override
					public void uploadFailed(FileUploadEvent event) {
						
					}
				});
				selectMainLogo.submit(url);
			}
		});
		Label label = new Label(parent,SWT.NONE);
		label.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		label.setText("<strong>设置系统主页的Logo</strong><br/><br/>请选择后缀名称为.png的图片文件。这个图片将在用户登录时显示在屏幕的中间.<br/>系统不限制图片的尺寸，但也不应太小或太大.<br/>建议图片的宽度在200～400之间，高度100～200之间");
//		fd = new FormData();
//		label.setLayoutData(fd);
//		fd.top = new FormAttachment(selectMainLogo ,-SIZE);
//		fd.left = new FormAttachment(selectMainLogo,margin);
//		fd.right = new FormAttachment(100,-margin);
		
	}

	private void createRefreshButton(Composite parent) {
		refreshButton = new Button(parent,SWT.PUSH);
		refreshButton.setToolTipText("设置“工作的刷新频率”");
		refreshButton.setLayoutData(getButtonLayoutData());

//		FormData fd = new FormData();
//		refreshButton.setLayoutData(fd);
//		fd.top = new FormAttachment(0,margin);
//		fd.left = new FormAttachment(0,margin);
//		fd.width = SIZE;
//		fd.height = SIZE;
		
		refreshButton.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IInputValidator validator = new IInputValidator() {
					
					@Override
					public String isValid(String newText) {
						try{
							int i = Integer.parseInt(newText);
							if(i>=10){
								return null;
							}
						}catch (Exception e){
						}
						return "您需要输入一个大于等于10的整数";
					}
				};
				
				BusinessService service = BusinessService.getDefault();
				InputDialog inputDialog = new InputDialog(getSite().getShell(), "设置工作的刷新频率", "设置每次刷新工作的间隔（秒）", ""+service.getWorkRefreshInterval(), validator);
				int ok = inputDialog.open();
				if(ok==InputDialog.OK){
					String value = inputDialog.getValue();
					int i = Integer.parseInt(value);
					service.setWorkRefreshInterval(i);
				}
			}
			
		});
		
		refreshButton.setImage(Resource.getImage(Resource.IMAGE_REFRESH96));
		
		Label label = new Label(parent,SWT.NONE);
		label.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		label.setText("<strong>设置“工作区”中“我的工作”的刷新间隔时间</strong><br/><br/>刷新间隔时间越长，对服务器的性能影响越小，反之越大。<br/>使用者也可以直接点击工具栏上的“刷新”按钮来手动刷新。<br/>建议设置为30秒");
//		fd = new FormData();
//		label.setLayoutData(fd);
//		fd.top = new FormAttachment(0,margin);
//		fd.left = new FormAttachment(refreshButton,margin);
//		fd.right = new FormAttachment(100,-margin);
		
	}

	private void setMainLogoImage() {
		Image source = UserService.getDefault().getProductLogo();
		Image image = ImageUtil.scaleFitImage2(source, SIZE, SIZE);
		selectMainLogo.setImage(image);
	}
	
	private void setProductNameImage() {
		Image source = UserService.getDefault().getAppLogo();
		Image image = ImageUtil.scaleFitImage2(source, SIZE, SIZE);
		selectProductName.setImage(image);
	}
	
	private void setHeadLogoImage() {
		Image source = UserService.getDefault().getHeaderLogo();
		Image image = ImageUtil.scaleFitImage2(source, SIZE, SIZE);
		selectHeadLogo.setImage(image);
	}


	@Override
	public void update() {
		
	}


}
