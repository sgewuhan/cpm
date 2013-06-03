package com.sg.widget.viewer.search;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.sg.widget.util.Util;


public class SearchConditionDialog extends Dialog {

	private String key = null;
	private Text text;
	private boolean useFuzzy = true ;
	private boolean useChinese = false;
	private boolean ignoreCase = true;

	private Button bUseFuzzy ;
	private Button bUseChinese;
	private Button bIgnoreCase;

	public SearchConditionDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText("在查询结果中搜索");
		super.configureShell(newShell);
	}
	@Override
	protected Control createDialogArea(Composite parent) {
//		super.createDialogArea(parent);
		Composite comp = new Composite(parent,SWT.NONE);
		GridLayout girdlayout = new GridLayout();
		girdlayout.numColumns = 2;
		comp.setLayout(girdlayout);
		
		new Label(comp,SWT.NONE).setText("(&K)查询关键字:");
		
		text = new Text(comp,SWT.BORDER);
		GridData layoutData = new GridData();
		
		layoutData.grabExcessHorizontalSpace =true;
		layoutData.minimumWidth = 300;
		text.setLayoutData(layoutData);

		bUseFuzzy = new Button(comp,SWT.CHECK);
		layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		bUseFuzzy.setLayoutData(layoutData);
		bUseFuzzy.setText("(&F)模糊匹配");
		bUseFuzzy.setSelection(useFuzzy);

		bIgnoreCase = new Button(comp,SWT.CHECK);
		layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		bIgnoreCase.setLayoutData(layoutData);
		bIgnoreCase.setText("(&I)忽略大小写");
		bIgnoreCase.setSelection(ignoreCase);
		
		bUseChinese = new Button(comp,SWT.CHECK);
		layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		bUseChinese.setLayoutData(layoutData);
		bUseChinese.setText("(&C)拼音首字母匹配");
		bUseChinese.setSelection(useChinese);
		return parent;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if(buttonId==IDialogConstants.OK_ID){
			key = text.getText();
			if(key.equals("")){
				key = null;
			}
			useFuzzy = bUseFuzzy.getSelection();
			useChinese = bUseChinese.getSelection();
			ignoreCase = bIgnoreCase.getSelection();
		}else{
			key = null;
		}
		super.buttonPressed(buttonId);
	}

	public String getValue() {
		return key;
	}

	public boolean isFuzzy() {
		return useFuzzy;
	}

	public boolean isChinese() {
		return useChinese;
	}
	
	public boolean isIgnoreCase(){
		return ignoreCase;
	}
	
	public int getSearchStyle(){
		int style = Util.ACCURATE_MODE;
		if (isFuzzy()) {
			style = style | Util.FUZZY_MODE;
		}
		if (isIgnoreCase()) {
			style = style | Util.IGNORECASE_MODE;
		}
		if (isChinese()) {
			style = style | Util.CHINESE_MODE;
		}
		return style;
	}

}
