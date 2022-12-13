package org.prelle.rpgframework.splittermond.data;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
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
import org.prelle.splimo.Spell;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.persist.SpellCostConverter;

/**
 * @author prelle
 *
 */
public class GenerateSpellList {
	
	private static XSSFCellStyle style;

	//-------------------------------------------------------------------
	/**
	 */
	public GenerateSpellList() {
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
		props.getCoreProperties().setTitle("Splittermond Zauber ");
		props.getCoreProperties().setDescription("Übersicht aller Zauber");
		props.getCoreProperties().setCreator("Alexander Schmidt");
		props.getCoreProperties().setCreated(Instant.now().toString());

		// Header
		style = wb.createCellStyle();
		XSSFFont font = wb.createFont();
//		font.setFontHeightInPoints((short) 15);
		font.setBold(true);;
		style.setFont(font);                 
		
		generateSpells(wb);

		String fileName = "Splittermond_Zauber.xlsx";
		String filename = 	"/tmp/"+fileName;
		System.out.println("Write to "+filename);
		try (OutputStream fileOut = new FileOutputStream(filename)) {
			wb.write(fileOut);
		}
		wb.close();
	}
	
	//-------------------------------------------------------------------
	private static void generateSpells(XSSFWorkbook wb) throws IOException {
		Sheet sheet = wb.createSheet("Zaubersprüche");		
		Row sheetRow = sheet.createRow(0);
		
		sheet.addMergedRegion(new CellRangeAddress(0,0,5,7));

	    sheetRow = sheet.createRow(1);
		sheetRow.createCell(0).setCellValue("Genesis ID");
	    sheetRow.createCell(1).setCellValue("1 Zauber");
		sheetRow.createCell(2).setCellValue("2 Quelle");
		sheetRow.createCell(3).setCellValue("3 Seite");
		sheetRow.createCell(4).setCellValue("4 Schule");
		sheetRow.createCell(5).setCellValue("5 Typus");
		sheetRow.createCell(6).setCellValue("6 Schwierigkeit");
		sheetRow.createCell(7).setCellValue("7 Kosten");
		sheetRow.createCell(8).setCellValue("8 Zauberdauer");
		sheetRow.createCell(9).setCellValue("9 Reichweite");
		sheetRow.createCell(10).setCellValue("10 Wirkung");
		sheetRow.createCell(11).setCellValue("11 Wirkungsdauer");
		sheetRow.createCell(12).setCellValue("12 Wirkungsbereich");
		sheetRow.createCell(13).setCellValue("13 Erfolgsgarde");
		sheet.createFreezePane(0, 2);
	    for(int j = 0; j<=13; j++)
	    	sheetRow.getCell(j).setCellStyle(style);

		List<Spell> list =SplitterMondCore.getSpells();
		Collections.sort(list, new Comparator<Spell>() {
			public int compare(Spell o1, Spell o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		
		int i=1;
		
		SpellCostConverter conv = new SpellCostConverter();
		for (Spell master : list) {		
		
		
//		for (ItemTemplate data : list) {
//			if (!(data.getType(ItemType.WEAPON)!=null || data.getType(ItemType.LONG_RANGE_WEAPON)!=null))
//				continue;
//			
			Row row = sheet.createRow(++i);
			row.createCell(0).setCellValue(master.getId());
			row.createCell(1).setCellValue(master.getName());
			row.createCell(2).setCellValue(master.getProductName());
			row.createCell(3).setCellValue(master.getPage());
			row.createCell(4).setCellValue(master.getSchoolName());
			row.createCell(5).setCellValue(String.join(", ", master.getTypes().stream().map(t -> t.getName()).collect(Collectors.toList())));			
			row.createCell(6).setCellValue(master.getDifficulty());

			row.createCell(8).setCellValue(master.getCastDurationString());
			row.createCell(9).setCellValue(master.getCastRangeString());
			row.createCell(10).setCellValue(master.getHelpText());
			row.createCell(11).setCellValue(master.getSpellDurationString());			
			row.createCell(12).setCellValue(master.getCastDurationTicks());
			row.createCell(13).setCellValue(master.getEnhancementString()+ " " +  master.getEnhancementDescription());			

			
//			row.createCell(14).setCellValue(master.getSpellDuration());
//			row.createCell(15).setCellValue(master.getDescription());
			
		    try {
				row.createCell(7).setCellValue(conv.write(master.getCost()));
			} catch (Exception e) {
				row.createCell(7).setCellValue("Error: "+e);
			}
			
			
		}

		// Mark columns auto-width
		for (i=0; i<=13; i++) {
			sheet.autoSizeColumn(i);
		}
	}
	
}
