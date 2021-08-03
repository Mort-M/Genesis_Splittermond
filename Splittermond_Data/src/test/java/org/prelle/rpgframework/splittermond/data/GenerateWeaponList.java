/**
 * 
 */
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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.items.LongRangeWeapon;
import org.prelle.splimo.items.Weapon;
import org.prelle.splimo.persist.WeaponDamageConverter;

/**
 * @author prelle
 *
 */
public class GenerateWeaponList {
	
	private static XSSFCellStyle style;

	//-------------------------------------------------------------------
	/**
	 */
	public GenerateWeaponList() {
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
		props.getCoreProperties().setTitle("Splittermond Waffen ");
		props.getCoreProperties().setDescription("Übersicht aller Waffen");
		props.getCoreProperties().setCreator("Stefan Prelle");
		props.getCoreProperties().setCreated(Instant.now().toString());

		// Header
		style = wb.createCellStyle();
		XSSFFont font = wb.createFont();
//		font.setFontHeightInPoints((short) 15);
		font.setBold(true);;
		style.setFont(font);                 
		
		generateMeleeWeapons(wb);
		generateRangedWeapons(wb);


		String fileName = "Splittermond_Daten.xlsx";
		String filename = 	"/tmp/"+fileName;
		try (OutputStream fileOut = new FileOutputStream(filename)) {
			wb.write(fileOut);
		}
		wb.close();
	}
	
	//-------------------------------------------------------------------
	private static void generateMeleeWeapons(XSSFWorkbook wb) throws IOException {
		Sheet sheet = wb.createSheet("Nahkampfwaffen");		
		Row sheetRow = sheet.createRow(0);
		
		sheet.addMergedRegion(new CellRangeAddress(0,0,5,7));
		sheetRow.createCell(5).setCellValue("Schaden pro Tick");
	    for(int j = 5; j<=5; j++)
	    	sheetRow.getCell(j).setCellStyle(style);

	    sheetRow = sheet.createRow(1);
		sheetRow.createCell(0).setCellValue("Waffe");
		sheetRow.createCell(1).setCellValue("Quelle");
		sheetRow.createCell(2).setCellValue("Seite");
		sheetRow.createCell(3).setCellValue("Schaden");
		sheetRow.createCell(4).setCellValue("WGS");
		sheetRow.createCell(5).setCellValue("Avg");
		sheetRow.createCell(6).setCellValue("Min");
		sheetRow.createCell(7).setCellValue("Max");
		sheetRow.createCell(8).setCellValue("Reichweite");
		sheetRow.createCell(9).setCellValue("Merkmale");
		sheet.createFreezePane(0, 2);
	    for(int j = 0; j<=7; j++)
	    	sheetRow.getCell(j).setCellStyle(style);

	    
	    
		List<ItemTemplate> list =SplitterMondCore.getItems(ItemType.WEAPON);
		Collections.sort(list, new Comparator<ItemTemplate>() {
			public int compare(ItemTemplate o1, ItemTemplate o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		WeaponDamageConverter dmgConv = new WeaponDamageConverter();
		
		int i=1;
		for (ItemTemplate data : list) {
			if (!(data.getType(ItemType.WEAPON)!=null || data.getType(ItemType.LONG_RANGE_WEAPON)!=null))
				continue;
			
			Row row = sheet.createRow(++i);
			row.createCell(0).setCellValue(data.getName());
			row.createCell(1).setCellValue(data.getProductName());
			row.createCell(2).setCellValue(data.getPage());
			if (data.getType(LongRangeWeapon.class)!=null) {
				LongRangeWeapon weap = data.getType(LongRangeWeapon.class);
				row.createCell(3).setCellValue(dmgConv.write(weap.getDamage()));
				row.createCell(4).setCellValue(weap.getSpeed());
				// Durchschnitt
				double avgDmg = ((Weapon.getDamageDiceType(weap)+1)/2.0 * Weapon.getDamageDiceCount(weap) + Weapon.getDamageModifier(weap)) / (double)weap.getSpeed();
				row.createCell(5).setCellValue(String.format("%.2f",avgDmg));
				// Min
				double minDmg = (1 * Weapon.getDamageDiceCount(weap) + Weapon.getDamageModifier(weap)) / (double)weap.getSpeed();
				row.createCell(6).setCellValue(String.format("%.2f",minDmg));
				// Max
				double maxDmg = (Weapon.getDamageDiceType(weap) * Weapon.getDamageDiceCount(weap) + Weapon.getDamageModifier(weap)) / (double)weap.getSpeed();
				row.createCell(7).setCellValue(String.format("%.2f",maxDmg));
				
				row.createCell(8).setCellValue(weap.getRange());
				row.createCell(9).setCellValue(String.join(", ",weap.getFeatures().stream().map(f -> f.getName()).collect(Collectors.toList())));
			}
		}

		// Mark columns auto-width
		for (i=0; i<9; i++) {
			sheet.autoSizeColumn(i);
		}
	}
	
	//-------------------------------------------------------------------
	private static void generateRangedWeapons(XSSFWorkbook wb) throws IOException {
		Sheet sheet = wb.createSheet("Fernkampfwaffen");		
		Row sheetRow = sheet.createRow(0);
		
		sheet.addMergedRegion(new CellRangeAddress(0,0,5,7));
		sheetRow.createCell(5).setCellValue("Schaden pro Tick");
	    for(int j = 5; j<=5; j++)
	    	sheetRow.getCell(j).setCellStyle(style);

	    sheetRow = sheet.createRow(1);
		sheetRow.createCell(0).setCellValue("Waffe");
		sheetRow.createCell(1).setCellValue("Quelle");
		sheetRow.createCell(2).setCellValue("Seite");
		sheetRow.createCell(3).setCellValue("Schaden");
		sheetRow.createCell(4).setCellValue("WGS");
		sheetRow.createCell(5).setCellValue("Avg");
		sheetRow.createCell(6).setCellValue("Min");
		sheetRow.createCell(7).setCellValue("Max");
		sheetRow.createCell(8).setCellValue("Merkmale");
		sheet.createFreezePane(0, 2);
	    for(int j = 0; j<=7; j++)
	    	sheetRow.getCell(j).setCellStyle(style);

	    
	    
		List<ItemTemplate> list =SplitterMondCore.getItems(ItemType.LONG_RANGE_WEAPON);
		Collections.sort(list, new Comparator<ItemTemplate>() {
			public int compare(ItemTemplate o1, ItemTemplate o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		WeaponDamageConverter dmgConv = new WeaponDamageConverter();
		
		int i=1;
		for (ItemTemplate data : list) {
			if (!(data.getType(ItemType.WEAPON)!=null || data.getType(ItemType.LONG_RANGE_WEAPON)!=null))
				continue;
			
			Row row = sheet.createRow(++i);
			row.createCell(0).setCellValue(data.getName());
			row.createCell(1).setCellValue(data.getProductName());
			row.createCell(2).setCellValue(data.getPage());
			if (data.getType(LongRangeWeapon.class)!=null) {
				LongRangeWeapon weap = data.getType(LongRangeWeapon.class);
				row.createCell(3).setCellValue(dmgConv.write(weap.getDamage()));
				row.createCell(4).setCellValue(weap.getSpeed());
				// Durchschnitt
				double avgDmg = ((Weapon.getDamageDiceType(weap)+1)/2.0 * Weapon.getDamageDiceCount(weap) + Weapon.getDamageModifier(weap)) / (double)weap.getSpeed();
				row.createCell(5).setCellValue(String.format("%.2f",avgDmg));
				// Min
				double minDmg = (1 * Weapon.getDamageDiceCount(weap) + Weapon.getDamageModifier(weap)) / (double)weap.getSpeed();
				row.createCell(6).setCellValue(String.format("%.2f",minDmg));
				// Max
				double maxDmg = (Weapon.getDamageDiceType(weap) * Weapon.getDamageDiceCount(weap) + Weapon.getDamageModifier(weap)) / (double)weap.getSpeed();
				row.createCell(7).setCellValue(String.format("%.2f",maxDmg));
				
				row.createCell(8).setCellValue(String.join(", ",weap.getFeatures().stream().map(f -> f.getName()).collect(Collectors.toList())));
			}
		}

		// Mark columns auto-width
		for (i=0; i<9; i++) {
			sheet.autoSizeColumn(i);
		}
	}
	
	
}
