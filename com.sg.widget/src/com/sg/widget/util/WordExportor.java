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
				// 按照sampleParagraph创建段落
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
	// * 可能的情况 1.标签 整体被包含在段落中 2. 标签作为一个整体的run存在于段落中 3.标签被分割成为了多个部分存在与段落中
	// *
	// * @param parameter
	// * @param paragraph
	// */
	// private static void setParagraph(Map<String, String> parameter,
	// XWPFParagraph paragraph) {
	//
	// // 查找本段落包含的参数
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
	// // 1.标签 整体被包含在run中
	// Iterator<String> iter1 = para.keySet().iterator();
	// while (iter1.hasNext()) {
	// String key = iter1.next();
	// if (text.contains("[@" + key + "]")) {
	// text = text.replaceAll("[@" + key + "]", parameter.get(key));
	// }
	// }
	//
	// // 处理被截断的情况
	// if (text.endsWith("[")) {
	// var = "[";
	// // 查找后续的run是否以@开头
	// if (j < (runs.size() - 1)) {
	// int startPos = j + 1;
	// XWPFRun nextRun = runs.get(startPos);
	// if (nextRun.toString().startsWith("@")) {
	// // 向后查找]
	// boolean has = false;
	// while ((!has) && (startPos < (runs.size() - 1))) {
	// nextRun = runs.get(startPos);
	// String nextText = nextRun.toString();
	// int idx = nextText.indexOf("]");
	// if (idx != -1) {// 找到了
	// var = var + nextText.substring(0, idx);
	// if (para.keySet().contains(var.substring(1, var.length() - 1))) {//
	// 确实是个变量
	// has = true;
	// }
	// }
	// startPos++;
	// }
	//
	// if (has) {// 清除
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
	// // 查找本run中的参数
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
	// map.put("a", "测试a");
	// map.put("b", "测试b");
	// map.put("c", "测试c");
	// map.put("d", "测试d");
	// map.put("e", "测试e");
	// map.put("f", "测试f");
	//
	// try {
	// export("d:/test1.docx", "d:/test1_1.docx", map);
	// } catch (XmlException | OpenXML4JException | IOException e) {
	// e.printStackTrace();
	// }
	//
	// }
}
