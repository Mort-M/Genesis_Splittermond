package org.prelle.rpgframework.splittermond.data;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.Mastership;
import org.prelle.splimo.Skill;

/**
 * @author prelle
 *
 */
public class GenerateMastershipList {

	private static XSSFCellStyle style;

	//-------------------------------------------------------------------
	/**
	 */
	public GenerateMastershipList() {
		// TODO Auto-generated constructor stub
	}

	//-------------------------------------------------------------------
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		SplittermondDataPlugin plugin = new SplittermondDataPlugin();
		plugin.init( (percent) -> {});

		/* Prepare Excel sheet */
		XSSFWorkbook wb = new XSSFWorkbook();
		POIXMLProperties props = wb.getProperties();
		props.getCoreProperties().setTitle("Splittermond Meisterschaften ");
		props.getCoreProperties().setDescription("Übersicht aller Meisterschaften");
		props.getCoreProperties().setCreator("Genesis");
		props.getCoreProperties().setCreated(Instant.now().toString());

		// Header
		style = wb.createCellStyle();
		XSSFFont font = wb.createFont();
//		font.setFontHeightInPoints((short) 15);
		font.setBold(true);;
		style.setFont(font);

		generateMasterships(wb);

		String fileName = "Splittermond_Meisterschaften.xlsx";
		String filename = 	"/tmp/"+fileName;
		System.out.println("Write to "+filename);
		try (OutputStream fileOut = new FileOutputStream(filename)) {
			wb.write(fileOut);
		}
		wb.close();
	}

	//-------------------------------------------------------------------
	private static void generateMasterships(XSSFWorkbook wb) throws IOException {
		Sheet sheet = wb.createSheet("Meisterschaften");
		Row sheetRow = sheet.createRow(0);

		sheet.addMergedRegion(new CellRangeAddress(0,0,5,7));

	    sheetRow = sheet.createRow(1);
		sheetRow.createCell(0).setCellValue("Fertigkeit");
	    sheetRow.createCell(1).setCellValue("1 Grad");
	    sheetRow.createCell(2).setCellValue("2 Meisterschaft");
		sheetRow.createCell(3).setCellValue("3 Quelle");
		sheetRow.createCell(4).setCellValue("4 Seite");
		sheetRow.createCell(5).setCellValue("5 Allgemein?");
		sheetRow.createCell(6).setCellValue("6 Wirkung");
		sheet.createFreezePane(0, 2);
	    for(int j = 0; j<=6; j++)
	    	sheetRow.getCell(j).setCellStyle(style);

		List<Skill> list =SplitterMondCore.getSkills();
		Collections.sort(list, new Comparator<Skill>() {
			public int compare(Skill o1, Skill o2) {
				if (o1.getType().ordinal()<o2.getType().ordinal()) return -1;
				if (o1.getType().ordinal()>o2.getType().ordinal()) return  1;
				return o1.getName().compareTo(o2.getName());
			}
		});


		int i=1;

		for (Skill skill: list) {
			List<Mastership> list2 = new ArrayList<>(skill.getMasterships());
			Collections.sort(list2, new Comparator<Mastership>() {
				public int compare(Mastership o1, Mastership o2) {
					int cmp = Integer.compare(o1.getLevel(), o2.getLevel());
					if (cmp!=0) return cmp;
					return o1.getName().compareTo(o2.getName());
				}
			});
			for (Mastership master : list2) {
			Row row = sheet.createRow(++i);
			row.createCell(0).setCellValue(skill.getName());
			row.createCell(1).setCellValue(master.getLevel());
			row.createCell(2).setCellValue(master.getName());
			row.createCell(3).setCellValue(master.getProductName());
			row.createCell(4).setCellValue(master.getPage());
			row.createCell(5).setCellValue(master.isCommon()?"x":null);
			row.createCell(6).setCellValue(master.getHelpText());


//			row.createCell(14).setCellValue(master.getSpellDuration());
//			row.createCell(15).setCellValue(master.getDescription());

			}

		}

		// Mark columns auto-width
		for (i=0; i<=5; i++) {
			sheet.autoSizeColumn(i);
		}
	}

}
