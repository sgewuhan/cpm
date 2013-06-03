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
		
		
		//����ˢ�µ�Ƶ��
		createRefreshButton(content);
		
		//����logo
		createMainScreenLogo(content);
		
		createMainScreenProductName(content);
		
		//���ö���logo
		createHeadLogo(content);
		
		//���ö�������
		createTimeSchedual(content);
		
		createJobTimeRule(content);
		
		//������ʾ�������ļ��ʱ��
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
						return "���ټ��10����";
					}
				};
				InputDialog inputDialog = new InputDialog(getSite().getShell(), "���ù����������ʱ����", "�����������", ""+service.getWorkRetrieveInterval(), validator );
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
		label.setText("<strong>���÷������������ѭ��ʱ����</strong><br/><br/>���������ݴ����趨��ʱ���鹤��������������������������10����.<br/>");		
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
				InputDialog inputDialog = new InputDialog(getSite().getShell(), "����������춨ʱ��", "������hh:mm:ss��ʽ��ʱ��", ""+service.getJobTrigerTime(), validator );
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
		label.setText("<strong>���÷�������������ʱ��</strong><br/><br/>���������ݴ����趨��ʱ��������Ĭ������Ϊÿ��4:00:00<br/>");
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
						return "������1��28֮���һ������";
					}
				};
				InputDialog inputDialog = new InputDialog(getSite().getShell(), "�����±�������ʼ����", "������1��28֮���һ������", ""+service.getMonthlyReportDate(), validator );
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
		label.setText("<strong>������Ŀ�±��Ķ�������ʱ��</strong><br/><br/>���������ݴ����趨����������Ŀ�±�������<br/>");
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
					MessageDialog.openInformation(getSite().getShell(),"�ϴ��������������ͼƬ", "����Ҫѡ���׺��Ϊ.png��ͼƬ");
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
		label.setText("<strong>����ϵͳ��ҳ�Ĳ�Ʒ���Ƶ�ͼƬ</strong><br/><br/>��ѡ���׺����Ϊ.png��ͼƬ�ļ���Ϊ������Logo�����ͼƬ����ʾ��������ı�������.<br/>ͼƬ�ĳߴ���Ҫ������ȷ���ڱ�������������ʾ.<br/>ͼƬ�Ŀ����100��200֮�䣬�߶Ȳ�����40");
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
					MessageDialog.openInformation(getSite().getShell(),"�ϴ���Ʒ���Ƶ�ͼƬ", "����Ҫѡ���׺��Ϊ.png��ͼƬ");
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
		label.setText("<strong>����ϵͳ��ҳ�Ĳ�Ʒ���Ƶ�ͼƬ</strong><br/><br/>��ѡ���׺����Ϊ.png��ͼƬ�ļ������ͼƬ�����û���¼ʱ��ʾ��LOGO���·�.<br/>ϵͳ������ͼƬ�ĳߴ磬��Ҳ��Ӧ̫С��̫��.<br/>����ͼƬ�Ŀ����200��400֮�䣬�߶�30��60֮��");
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
					MessageDialog.openInformation(getSite().getShell(),"�ϴ�Ӧ�õ�¼ͼƬ", "����Ҫѡ���׺��Ϊ.png��ͼƬ");
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
		label.setText("<strong>����ϵͳ��ҳ��Logo</strong><br/><br/>��ѡ���׺����Ϊ.png��ͼƬ�ļ������ͼƬ�����û���¼ʱ��ʾ����Ļ���м�.<br/>ϵͳ������ͼƬ�ĳߴ磬��Ҳ��Ӧ̫С��̫��.<br/>����ͼƬ�Ŀ����200��400֮�䣬�߶�100��200֮��");
//		fd = new FormData();
//		label.setLayoutData(fd);
//		fd.top = new FormAttachment(selectMainLogo ,-SIZE);
//		fd.left = new FormAttachment(selectMainLogo,margin);
//		fd.right = new FormAttachment(100,-margin);
		
	}

	private void createRefreshButton(Composite parent) {
		refreshButton = new Button(parent,SWT.PUSH);
		refreshButton.setToolTipText("���á�������ˢ��Ƶ�ʡ�");
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
						return "����Ҫ����һ�����ڵ���10������";
					}
				};
				
				BusinessService service = BusinessService.getDefault();
				InputDialog inputDialog = new InputDialog(getSite().getShell(), "���ù�����ˢ��Ƶ��", "����ÿ��ˢ�¹����ļ�����룩", ""+service.getWorkRefreshInterval(), validator);
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
		label.setText("<strong>���á����������С��ҵĹ�������ˢ�¼��ʱ��</strong><br/><br/>ˢ�¼��ʱ��Խ�����Է�����������Ӱ��ԽС����֮Խ��<br/>ʹ����Ҳ����ֱ�ӵ���������ϵġ�ˢ�¡���ť���ֶ�ˢ�¡�<br/>��������Ϊ30��");
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
