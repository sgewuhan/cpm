package com.sg.widget.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.eclipse.core.runtime.IProgressMonitor;

public class ExcelExportor {

	private HSSFWorkbook wb;
	private HSSFSheet sheet;
	private ArrayList<HSSFRow> templateRows;
	private ArrayList<Region> templateMergedRegion;
	private ArrayList<Object[]> bodyMap;

	private int maxPageCount = 0;
	private int startRow = 0;

	public ExcelExportor() {
	}

	public void doExport(String templatePath, String outputPath,
			Map<String,Object> headData, List<Object[]> bodyData, int sheetIdx, IProgressMonitor monitor)
			throws FileNotFoundException, IOException {

		int totalWorkCount = 15+((headData!=null)?headData.size():0)+(bodyData!=null?bodyData.size():0);
		monitor.beginTask("导出数据到Excel文件", totalWorkCount);
		wb = null;
		sheet = null;
		templateRows = new ArrayList<HSSFRow>();
		templateMergedRegion = new ArrayList<Region>();
		bodyMap = new ArrayList<Object[]>();
		

		monitor.setTaskName("读取模板文件");
		
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(
				templatePath));
		wb = new HSSFWorkbook(fs);
		sheet = wb.getSheetAt(sheetIdx);
		
		monitor.worked(5);
		monitor.setTaskName("完成数据写入");
		
		fillData(headData, bodyData,monitor);
		
		
		FileOutputStream fos = new FileOutputStream(outputPath);
		wb.write(fos); // 写文件
		fos.close(); // 关闭文件
		
		monitor.done();
		
	}
	

	private void fillData(Map<String,Object> headData, List<Object[]> bodyData, IProgressMonitor monitor) {
		
		monitor.setTaskName("分析模板");
		// get region of template
		ArrayList<Number[]> a1 = getLableIndex("PAGESTART");
		ArrayList<Number[]> a2 = getLableIndex("PAGEEND");
		Number[] n1 = (Number[]) a1.get(0);
		Number[] n2 = (Number[]) a2.get(0);
		HashMap<String, Number> modelRegion = getArea(n1[0].intValue(), n1[1].shortValue(),
				n2[0].intValue(), n2[1].shortValue());
		int r1 = ((Number) modelRegion.get("startRow")).intValue();
		this.startRow = r1;
		short c1 = ((Number) modelRegion.get("startCell")).shortValue();
		int r2 = ((Number) modelRegion.get("endRow")).intValue();
		short c2 = ((Number) modelRegion.get("endCell")).shortValue();

		// clear Start label cells
		sheet.getRow(r1).getCell(c1).setCellValue(new HSSFRichTextString(""));
		sheet.getRow(r2).getCell(c2).setCellValue(new HSSFRichTextString(""));

		// save Template
		for (int i = r1; i <= r2; i++) {
			HSSFRow tempRow = sheet.getRow(i);
			templateRows.add(tempRow);
		}


		// Save Merged Cells
		int nrgn = sheet.getNumMergedRegions();
		for (int i = 0; i < nrgn; i++) {
			templateMergedRegion.add(sheet.getMergedRegionAt(i));
		}
		
		monitor.worked(5);
		monitor.setTaskName("添加表格行数据");
		
		if(bodyData!=null){
			// create full template，preparing for gird and image
			templatePrepare(bodyData, r2 + 1);
			
			// fill bodyData
			for (int i = 0; i < bodyMap.size(); i++) {
				Object[] aMap = bodyMap.get(i);
				Number[] cord = (Number[]) aMap[0];
				String[][] matrix = (String[][]) aMap[1];
				addGrid(cord, matrix);
				
				monitor.worked(1);
			}
		}

		monitor.setTaskName("添加表格头部数据");
		addHead(headData,monitor);
		// clean unusedlabel

		monitor.setTaskName("清理表格");
		cleanSheet();
		monitor.worked(5);

	}

	private void addHead(Map<String,Object> headData, IProgressMonitor monitor) {
		
		
		Iterator<String> it = headData.keySet().iterator();
		String fieldName,celltext;
		Object cellValue;
		while (it.hasNext()) {
			fieldName = it.next();
			cellValue =  headData.get(fieldName);
			celltext = cellValue==null?"":cellValue.toString();
			addTxtHead(getLableIndex(fieldName), celltext);
			monitor.worked(1);
		}
	}

	private void addTxtHead(List<Number[]> lableList, String cellValue) {
		Iterator<?> lableIter = lableList.iterator();// 0--row,1--cell
		while (lableIter.hasNext()) {
			Number[] coordinate = (Number[]) lableIter.next();
			int rowNumber = coordinate[0].intValue();
			short cellNumber = coordinate[1].shortValue();
			HSSFRow row = sheet.getRow(rowNumber);
			HSSFCell cell = row.getCell(cellNumber);
			cell.setCellValue(new HSSFRichTextString( cellValue));
		}
	}

	private void cleanSheet() {
		Iterator<?> rowIter = sheet.rowIterator();
		while (rowIter.hasNext()) {
			HSSFRow row = (HSSFRow) rowIter.next();
			Iterator<?> cellIter = row.cellIterator();
			while (cellIter.hasNext()) {
				HSSFCell cell = (HSSFCell) cellIter.next();
				if (cell.getCellType() != 0) {
					String cellText = cell.getRichStringCellValue().getString()
							.trim();
					if (cellText.startsWith("<") && cellText.endsWith(">")) {
						cell.setCellValue(new HSSFRichTextString());
					}
				}
			}
		}
	}

	private void addGrid(Number[] cord, String[][] matrix) {
		int initStartRow = cord[0].intValue();
		short initStartCell = cord[1].shortValue();
		int initEndRow = cord[2].intValue();
		short initEndCell = cord[3].shortValue();

		int columnCount = initEndCell - initStartCell + 1;
		int pageIdx = 0;
		int pageRowCount = templateRows.size();
		int pageGridRows = initEndRow - initStartRow + 1;
		int step = pageRowCount - (initEndRow - initStartRow) - 1;

		int rowNum = 0;
		for (int x = 0; x < matrix.length; x++) {// row
			int rowCord = initStartRow + pageIdx * step;
			int dataColumn = 0;
			int appendRowNum = 0;
			boolean b = false;

			for (short y = 0; y < columnCount; y++) {// cell
				CellProp cp = new CellProp(sheet, rowCord + rowNum,
						(short) (initStartCell + y));
				if (cp.IS_LEFTTOP || cp.IS_SINGAL) {
					int n = setCell(cp, matrix[x][dataColumn], pageGridRows,
							step, initStartRow, pageRowCount);

					if (n == -1) {// 需换sheet
						b = true;
						appendRowNum = 0;
						break;
					}

					if (n > appendRowNum)
						appendRowNum = n;
					dataColumn++;
				}
			}
			if (b) {// 需换sheet
					// 清空当前数据
				int temp = pageGridRows - rowNum % pageGridRows;
				for (short i = 0; i < temp; i++) {
					for (short y = 0; y < columnCount; y++) {// cell
						sheet.getRow(rowCord + rowNum + i)
								.getCell(initStartCell + y)
								.setCellValue(new HSSFRichTextString(""));
					}
				}
				x--;

				// 换sheet
				rowNum += temp - 1;
				if (((rowNum + 1 + appendRowNum) / pageGridRows > 0)
						&& ((rowNum + 1 + appendRowNum) % pageGridRows == 0)
						|| ((rowNum + 1 + appendRowNum) / pageGridRows) > ((rowNum + 1) / pageGridRows)) {
					pageIdx++;
					if (maxPageCount == 0)
						maxPageCount++;
					if (pageIdx == maxPageCount) {
						addPageFrom(pageRowCount * maxPageCount + this.startRow);
						// 清空Grid
						clearGrid(pageRowCount * maxPageCount + this.startRow);

						maxPageCount++;
					}
				}
			}

			rowNum += appendRowNum;

			rowNum++;
		}

	}

	private void clearGrid(int rowIndex) {
		for (int i = 0; i < bodyMap.size(); i++) {
			Object[] aMap = bodyMap.get(i);
			Number[] cord = (Number[]) aMap[0];

			int srow = rowIndex + cord[0].intValue();
			int erow = rowIndex + cord[2].intValue();
			int scell = cord[1].intValue();
			int ecell = cord[3].intValue();
			// clear
			for (int k = srow; k <= erow; k++) {
				for (int h = scell; h < ecell; h++) {
					this.sheet.getRow(k).getCell(h)
							.setCellValue(new HSSFRichTextString(""));
				}
			}

		}

	}

	/**
	 * 返回插入的行数 若返回-1则表示此表格行数不够，需更换表格
	 */
	private int setCell(CellProp cp, String celldata, int pageGridRows,
			int step, int initStartRow, int pageRowCount) {
		// has a blank label, write a blank
		HSSFCell cell = cp.CELL;
		try {
			String cellLabel = cell.getRichStringCellValue().getString();
			if (cellLabel.equals("<BLANK>")) {
				cell.setCellValue(new HSSFRichTextString());
				return 0;
			}
		} catch (Exception e) {
		}
		return cp.setCellData(celldata, wb, pageGridRows, step, initStartRow,
				pageRowCount);
	}

	private List<Object[]> templatePrepare(List<Object[]> bodyData,
			int pageStart) {
		// get how many page needed.
		// int maxPageCount = 0;
		for (int i = 0; i < bodyData.size(); i++) {
			Object[] elem = bodyData.get(i);
			String label = ((String) elem[0]).trim();
			if (label.startsWith("GRD:")) {
				String[][] valuemap = (String[][]) elem[1];
				int rowCount = valuemap.length;

				ArrayList<Number[]> labelidx = getLableIndex(label);
				Number[] na =  labelidx.get(0);
				Number[] nb =  labelidx.get(1);
				HashMap<String, Number> dataRegion = getArea(na[0].intValue(),
						na[1].shortValue(), nb[0].intValue(),
						nb[1].shortValue());

				Number[] cord = new Number[4];
				cord[0] = dataRegion.get("startRow");
				cord[1] = dataRegion.get("startCell");
				cord[2] = dataRegion.get("endRow");
				cord[3] = dataRegion.get("endCell");

				int startRowIdx = cord[0].intValue();
				int endRowIdx = cord[2].intValue();

				int everyPageSpendRow = endRowIdx - startRowIdx + 1;
				int pageCount = getPageCount(rowCount, everyPageSpendRow);
				if (maxPageCount < pageCount) {
					maxPageCount = pageCount;
				}

				Object[] aBodyMap = new Object[2];
				aBodyMap[0] = cord;
				aBodyMap[1] = valuemap;
				bodyMap.add(aBodyMap);
			}
		}
		// add those pages
		int nextPageStart = pageStart;
		for (int i = 1; i < maxPageCount; i++) {
			nextPageStart = addPageFrom(nextPageStart);
		}
		return bodyMap;
	}

	private int getPageCount(int rowCount, int everyPageSpendRow) {
		int i1 = rowCount / everyPageSpendRow;
		int i2 = rowCount % everyPageSpendRow;
		int pageCount;
		if (i1 > 0 && i2 > 0) {
			pageCount = i1 + 1;
		} else {
			pageCount = i1;
		}
		return pageCount;

	}

	private int addPageFrom(int pageStart) {
		int tc = templateRows.size();
		int j = 0;
		for (int i = pageStart; i < pageStart + tc; i++) {
			HSSFRow trow = templateRows.get(j++);

			HSSFRow nrow = sheet.createRow(i);

			nrow.setHeight(trow.getHeight());

			for (int k = 0; k <= trow.getLastCellNum(); k++) {
				HSSFCell tcell = trow.getCell(k);
				HSSFCell ncell = nrow.createCell(k);
				if (tcell != null) {
					ncell.setCellStyle(tcell.getCellStyle());

					try {
						ncell.setCellValue(tcell.getRichStringCellValue());
					} catch (Exception e) {
						try {
							ncell.setCellValue(tcell.getDateCellValue());
						} catch (Exception e1) {
							try {
								ncell.setCellValue(tcell.getBooleanCellValue());
							} catch (Exception e2) {
								ncell.setCellValue(tcell.getNumericCellValue());
							}
						}
					}

					try {
						ncell.setCellComment(tcell.getCellComment());
					} catch (Exception e) {
					}

					try {
						ncell.setCellFormula(tcell.getCellFormula());
					} catch (Exception e) {
					}

				}
			}
		}
		for (int i = 0; i < templateMergedRegion.size(); i++) {
			Region tr = templateMergedRegion.get(i);
			Region nr = new Region();
			nr.setColumnFrom(tr.getColumnFrom());
			nr.setColumnTo(tr.getColumnTo());
			nr.setRowFrom(tr.getRowFrom() + pageStart);
			nr.setRowTo(tr.getRowTo() + pageStart);
			sheet.addMergedRegion(nr);
		}

		return pageStart + tc;
	}

	private HashMap<String, Number> getArea(int row1, short cell1, int row2, short cell2) {
		HashMap<String, Number> hs = new HashMap<String, Number>();
		if (row1 == row2) {
			if (cell1 < cell2) {
				hs.put("startCell", cell1);
				hs.put("startRow", row1);
				hs.put("endCell", cell2);
				hs.put("endRow", row2);
			} else {
				hs.put("startCell", cell2);
				hs.put("startRow", row2);
				hs.put("endCell", cell1);
				hs.put("endRow", row1);
			}
		} else {
			if (row1 < row2) {
				hs.put("startCell", cell1);
				hs.put("startRow", row1);
				hs.put("endCell", cell2);
				hs.put("endRow", row2);
			} else {
				hs.put("startCell", cell2);
				hs.put("startRow", row2);
				hs.put("endCell", cell1);
				hs.put("endRow", row1);
			}
		}
		return hs;
	}

	/*
	 * return ArrayList<Number[]> [0]-row [1]-column
	 */

	private ArrayList<Number[]> getLableIndex(String lableText) {
		ArrayList<Number[]> cordlist = new ArrayList<Number[]>();

		String aLable = "<" + lableText.trim() + ">";
		Iterator<?> rowIter = sheet.rowIterator();
		while (rowIter.hasNext()) {
			HSSFRow row = (HSSFRow) rowIter.next();
			Iterator<?> cellIter = row.cellIterator();
			while (cellIter.hasNext()) {

				HSSFCell cell = (HSSFCell) cellIter.next();
				if (cell.getCellType() != 0) {
					String cellText = cell.getRichStringCellValue().getString()
							.trim();
					if (cellText.equals(aLable)) {
						Number[] cord = new Number[2];
						cord[0] = row.getRowNum();
						cord[1] = cell.getCellNum();
						cordlist.add(cord);
					}
				}
			}
		}
		return cordlist;
	}

}

class CellProp {
	// 输入一个单元格判断它是合并单元格？还是单个的单元格？如果是合并单元格，它是第一个吗？

	public boolean IS_LEFTTOP = false;
	public boolean IS_SINGAL = true;
	public boolean IS_MEGERED = false;
	public Region REGION_BELONGING = new Region();
	public int ROW_IDX = 0;
	public short COL_IDX = 0;
	public HSSFCell CELL = null;
	public HSSFRow ROW = null;


	public CellProp(HSSFSheet sheet, int rownumber, short colnumber) {
		ROW_IDX = rownumber;
		COL_IDX = colnumber;
		ROW = sheet.getRow(ROW_IDX);
		CELL = ROW.getCell(COL_IDX);
		REGION_BELONGING.setColumnFrom(COL_IDX);
		REGION_BELONGING.setRowFrom(ROW_IDX);
		REGION_BELONGING.setColumnTo(COL_IDX);
		REGION_BELONGING.setRowTo(ROW_IDX);


		int m = sheet.getNumMergedRegions();
		for (int i = 0; i < m; i++) {
			Region r = sheet.getMergedRegionAt(i);
			int startRow = r.getRowFrom();
			int endRow = r.getRowTo();
			short startCell = r.getColumnFrom();
			short endCell = r.getColumnTo();
			if (rownumber <= endRow && rownumber >= startRow
					&& colnumber <= endCell && colnumber >= startCell) {
				this.IS_MEGERED = true;
				this.IS_SINGAL = false;
				if (rownumber == startRow && colnumber == startCell) {
					this.IS_LEFTTOP = true;
				} else {
					this.IS_LEFTTOP = false;
				}
				REGION_BELONGING = r;
				break;
			}
		}
	}

	public int setCellData(String celldata, HSSFWorkbook wb, int pageGridRows,
			int step, int initStartRow, int pageRowCount) {

		this.CELL.setCellValue(new HSSFRichTextString(celldata));
		return 0;

	}


}