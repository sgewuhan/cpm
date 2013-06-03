package com.sg.widget.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;

public class WordExportor {

	public void doExport(String templatePath, String targetPath, Map<String, Object> parameter) throws XmlException, OpenXML4JException, IOException {

		OPCPackage openPackage = POIXMLDocument.openPackage(templatePath);
		XWPFWordExtractor ocx = new XWPFWordExtractor(openPackage);
		XWPFDocument doc = (XWPFDocument) ocx.getDocument();
		List<IBodyElement> eList = doc.getBodyElements();
		for (int i = 0; i < eList.size(); i++) {
			IBodyElement element = eList.get(i);

			if (element.getElementType().equals(BodyElementType.PARAGRAPH)) {
				// setParagraph(parameter, (XWPFParagraph) element);
			} else if (element.getElementType().equals(BodyElementType.TABLE)) {
				setTable(parameter, (XWPFTable) element, doc);
			}

			FileOutputStream out = new FileOutputStream(targetPath);
			doc.write(out);
			
			out.close();
		}
		openPackage.revert();
	}

	private void setTable(Map<String, Object> parameter, XWPFTable element, XWPFDocument doc) {

		List<XWPFTableRow> rows = element.getRows();
		for (int i = 0; i < rows.size(); i++) {
			XWPFTableRow row = rows.get(i);
			List<XWPFTableCell> cells = row.getTableCells();
			for (int j = 0; j < cells.size(); j++) {
				XWPFTableCell cell = cells.get(j);
				String text = cell.getText();
				if (text.startsWith("[@") && text.endsWith("]")) {

					String key = text.substring(2, text.length() - 1);
					if (parameter.keySet().contains(key)) {
						Object value = parameter.get(key);
						value = value == null ? "" : value;
						text = value.toString();
					} else {
						text = "";
					}

					//
					setCellText(cell, text, doc);
				}
			}
		}
	}

	private void setCellText(XWPFTableCell cell, String text, XWPFDocument doc) {

		String[] parts = text.split("\n");
		
		if(parts.length==1){
			List<XWPFParagraph> paragraphs = cell.getParagraphs();
			for (int k = 0; k < paragraphs.size();) {
				XWPFParagraph p = paragraphs.get(k);
				List<XWPFRun> runs = p.getRuns();
				for (int m = 0; m < runs.size();) {
					if (m == 1) {
						p.removeRun(m);
					} else {
						XWPFRun run = runs.get(m);
						run.setText(parts[0], 0);
						m++;
						k++;
					}
				}
			}
		}else{
			List<XWPFParagraph> paragraphs = cell.getParagraphs();
			XWPFParagraph sampleParagraphs = null;
			for (int k = 0; k < paragraphs.size();) {
				XWPFParagraph p = paragraphs.get(k);
				List<XWPFRun> runs = p.getRuns();
				for (int m = 0; m < runs.size();) {
					if (m == 1) {
						p.removeRun(m);
					} else {
						sampleParagraphs = p;
						XWPFRun run = runs.get(m);
						run.setText("", 0);
						m++;
						k++;
					}
				}
			}
			
			XWPFRun sampleRun = sampleParagraphs.getRuns().get(0);
			for (int i = 0; i < parts.length; i++) {
				// ����sampleParagraph��������
				XWPFParagraph newp = createCellParagraphBy(sampleParagraphs, cell);
				XWPFRun newrun = createRunBy(sampleRun, newp);
				newrun.setText(parts[i]);
				cell.addParagraph(newp);
			}
		}



	}

	private XWPFRun createRunBy(XWPFRun sampleRun, XWPFParagraph p) {

		XWPFRun newRun = p.createRun();

		newRun.setBold(sampleRun.isBold());
		newRun.setColor(sampleRun.getColor());
		newRun.setFontFamily(sampleRun.getFontFamily());
		int fontSize = sampleRun.getFontSize();
		if (fontSize != -1) {
			newRun.setFontSize(fontSize);
		}
		newRun.setItalic(sampleRun.isItalic());
		newRun.setStrike(sampleRun.isStrike());
		newRun.setSubscript(sampleRun.getSubscript());
		newRun.setUnderline(sampleRun.getUnderline());

		return newRun;

	}

	private XWPFParagraph createCellParagraphBy(XWPFParagraph sp, XWPFTableCell cell) {

		XWPFParagraph newp = cell.addParagraph();

		newp.setAlignment(sp.getAlignment());
		newp.setBorderBetween(sp.getBorderBetween());
		newp.setBorderBottom(sp.getBorderBottom());
		newp.setBorderLeft(sp.getBorderLeft());
		newp.setBorderRight(sp.getBorderRight());
		newp.setBorderTop(sp.getBorderTop());

		int i = sp.getIndentationFirstLine();
		if (i != -1)
			newp.setIndentationFirstLine(i);

		i = sp.getIndentationHanging();
		if (i != -1)
			newp.setIndentationHanging(i);

		i = sp.getIndentationLeft();
		if (i != -1)
			newp.setIndentationLeft(i);

		i = sp.getIndentationRight();
		if (i != -1)
			newp.setIndentationRight(i);

		i = sp.getSpacingAfter();
		if (i != -1)
			newp.setSpacingAfter(i);

		i = sp.getSpacingBefore();
		if (i != -1)
			newp.setSpacingBefore(i);

		i = sp.getSpacingAfterLines();
		if (i != -1)
			newp.setSpacingAfterLines(i);

		i = sp.getSpacingBeforeLines();
		if (i != -1)
			newp.setSpacingBeforeLines(i);
		
		newp.setSpacingLineRule(sp.getSpacingLineRule());
		newp.setStyle(sp.getStyle());
		newp.setVerticalAlignment(sp.getVerticalAlignment());

		// newp.setWordWrap(sp.isWordWrap());
		// newp.setPageBreak(sp.isPageBreak());
		return newp;
	}

	// /**
	// * ���ܵ���� 1.��ǩ ���屻�����ڶ����� 2. ��ǩ��Ϊһ�������run�����ڶ����� 3.��ǩ���ָ��Ϊ�˶�����ִ����������
	// *
	// * @param parameter
	// * @param paragraph
	// */
	// private static void setParagraph(Map<String, String> parameter,
	// XWPFParagraph paragraph) {
	//
	// // ���ұ���������Ĳ���
	// HashMap<String, String> para = new HashMap<String, String>();
	// String pText = paragraph.getText();
	// Iterator<String> iter = parameter.keySet().iterator();
	// while (iter.hasNext()) {
	// String key = iter.next();
	// if (pText.contains("[@" + key + "]")) {
	// para.put(key, parameter.get(key));
	// }
	// }
	//
	// List<XWPFRun> runs = paragraph.getRuns();
	//
	// String var;
	// for (int j = 0; j < runs.size(); j++) {
	// XWPFRun runsItem = runs.get(j);
	// String text = runsItem.toString();
	// System.out.println(text);
	// // 1.��ǩ ���屻������run��
	// Iterator<String> iter1 = para.keySet().iterator();
	// while (iter1.hasNext()) {
	// String key = iter1.next();
	// if (text.contains("[@" + key + "]")) {
	// text = text.replaceAll("[@" + key + "]", parameter.get(key));
	// }
	// }
	//
	// // �����ضϵ����
	// if (text.endsWith("[")) {
	// var = "[";
	// // ���Һ�����run�Ƿ���@��ͷ
	// if (j < (runs.size() - 1)) {
	// int startPos = j + 1;
	// XWPFRun nextRun = runs.get(startPos);
	// if (nextRun.toString().startsWith("@")) {
	// // ������]
	// boolean has = false;
	// while ((!has) && (startPos < (runs.size() - 1))) {
	// nextRun = runs.get(startPos);
	// String nextText = nextRun.toString();
	// int idx = nextText.indexOf("]");
	// if (idx != -1) {// �ҵ���
	// var = var + nextText.substring(0, idx);
	// if (para.keySet().contains(var.substring(1, var.length() - 1))) {//
	// ȷʵ�Ǹ�����
	// has = true;
	// }
	// }
	// startPos++;
	// }
	//
	// if (has) {// ���
	//
	// }
	// }
	// }
	//
	// text = text.substring(text.length() - 1);
	// } else if (text.endsWith("[@")) {
	// var = "[@";
	// text = text.substring(text.length() - 2);
	// } else {
	// var = null;
	// }
	//
	// if (var != null) {
	// if (text.startsWith("@")) {
	// // ���ұ�run�еĲ���
	// int idx = text.indexOf("]");
	// if (idx == -1) {
	//
	// }
	// }
	// }
	//
	// runsItem.setText(text, 0);
	// }
	//
	// }
	//
	// public static void main(String[] args) {
	//
	// Map<String, String> map = new HashMap<String, String>();
	//
	// map.put("a", "����a");
	// map.put("b", "����b");
	// map.put("c", "����c");
	// map.put("d", "����d");
	// map.put("e", "����e");
	// map.put("f", "����f");
	//
	// try {
	// export("d:/test1.docx", "d:/test1_1.docx", map);
	// } catch (XmlException | OpenXML4JException | IOException e) {
	// e.printStackTrace();
	// }
	//
	// }
}
