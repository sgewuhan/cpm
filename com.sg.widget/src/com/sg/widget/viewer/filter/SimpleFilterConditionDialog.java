package com.sg.widget.viewer.filter;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SimpleFilterConditionDialog extends Dialog {
	
	private ComboViewer cmb1;
	private ComboViewer cmb2;
	private Text txt;
//	private ArrayList<DTOColumn> columnList;
	
	private SimpleFilterCondition result ;	//��ѯ����
	private String[] nameList;
	private String[] typeList;
	private String[] titleList;

//	public FilterConditionDialog(Shell parentShell,ArrayList<DTOColumn> columnList) {
//		super(parentShell);
//		this.columnList = columnList;
//	}
	
	public SimpleFilterConditionDialog(Shell parentShell,
			String[] nameList,String[] titleList,String[] typeList) {
		super(parentShell);
		this.nameList = nameList;
		this.typeList = typeList;
		this.titleList = titleList;
	}

	
	protected void configureShell(Shell newShell) {
		newShell.setText("���˲�ѯ���");
		super.configureShell(newShell);
	}
	
	/**
	 * ��ֵ���͵������
	 */
	static ArrayList<String> operatorNumber = new ArrayList<String>();
	{
		operatorNumber.add(SimpleFilterCondition.GREATER_THAN);
		operatorNumber.add(SimpleFilterCondition.GREATER_THAN_OR_EQUAL);
		operatorNumber.add(SimpleFilterCondition.LESS_THAN);
		operatorNumber.add(SimpleFilterCondition.LESS_THAN_OR_EQUAL);
		operatorNumber.add(SimpleFilterCondition.EQUALS);
		operatorNumber.add(SimpleFilterCondition.NO_EQUALS);	
	}
	
	/**
	 * �ַ������͵������
	 */
	static ArrayList<String> operatorString = new ArrayList<String>();
	{
		operatorString.add(SimpleFilterCondition.EQUALS);
		operatorString.add(SimpleFilterCondition.INCLUDED);
		operatorString.add(SimpleFilterCondition.NOT_INCLUDED);
	}
	
	/**
	 * �������͵������
	 */
	static ArrayList<String> operatorObject = new ArrayList<String>();
	{
		operatorObject.add(SimpleFilterCondition.INCLUDED);
		operatorObject.add(SimpleFilterCondition.NOT_INCLUDED);
	}
	
	/**
	 * @Override
	 * ���������ϵĿؼ�
	 */
	protected Control createDialogArea(Composite parent) {
		
		Composite comp = new Composite(parent,SWT.NONE);
		GridData layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		comp.setLayoutData(layoutData);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		comp.setLayout(layout);
		cmb1 = new ComboViewer(comp,SWT.NONE);
		cmb1.setContentProvider(new ArrayContentProvider());
		cmb1.setInput(titleList);
		
		cmb1.getCombo().addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = cmb1.getCombo().getSelectionIndex();
				String type = typeList[index];
				if(SimpleFilterCondition.TYPE_NUMBER.equalsIgnoreCase(type)){
					cmb2.setInput(operatorNumber);
				}else if(SimpleFilterCondition.TYPE_DATE.equalsIgnoreCase(type)){
					cmb2.setInput(operatorNumber);
				} else {
					cmb2.setInput(operatorString);
				}
				cmb2.getCombo().select(0);
				cmb2.refresh();
			}
		});
		cmb2 = new ComboViewer(comp,SWT.READ_ONLY);
		cmb2.setContentProvider(new ArrayContentProvider());
		cmb2.setLabelProvider(new  LabelProvider ());
		ArrayList<String> empty = new ArrayList<String>();
		empty.add("       ");
		cmb2.setInput(empty);
		txt = new Text(comp,SWT.BORDER);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 2;
		txt.setLayoutData(layoutData);
		
		return parent;
	}
	
	/**
	 * @Override
	 * ����"ȷ��"��ť,����ò�ѯ����;����"ȡ��"��ť,���ѯ����Ϊ��
	 */
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			setResult();
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			this.result = null;
			cancelPressed();
		}
	}

	/**
	 * ��ȡ��ѯ����
	 */
	private void setResult()
	{
		int index = cmb1.getCombo().getSelectionIndex();
		if(index < 0){
			this.result = null;
			return;
		}
		String name = nameList[index];
		String operator = (String)((StructuredSelection)cmb2.getSelection()).getFirstElement();
		String condition = txt.getText();
		this.result = new SimpleFilterCondition(name,titleList[index],typeList[index],operator,condition);
	}
	
	public SimpleFilterCondition getResult(){
		return this.result;
	}
	
}
