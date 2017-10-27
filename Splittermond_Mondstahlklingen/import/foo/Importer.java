/**
 * 
 */
package foo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.prelle.simplepersist.Persister;
import org.prelle.simplepersist.SerializationException;
import org.prelle.splimo.Attribute;
import org.prelle.splimo.DummyRulePlugin;
import org.prelle.splimo.Skill;
import org.prelle.splimo.SkillSpecialization;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.items.Armor;
import org.prelle.splimo.items.Availability;
import org.prelle.splimo.items.Complexity;
import org.prelle.splimo.items.Feature;
import org.prelle.splimo.items.FeatureList;
import org.prelle.splimo.items.FeatureType;
import org.prelle.splimo.items.ItemList;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.items.Light;
import org.prelle.splimo.items.LongRangeWeapon;
import org.prelle.splimo.items.MaterialType;
import org.prelle.splimo.items.Projectile;
import org.prelle.splimo.items.Shield;
import org.prelle.splimo.items.Weapon;
import org.prelle.splimo.persist.WeaponDamageConverter;
import org.prelle.splimo.requirements.AttributeRequirement;
import org.prelle.splimo.requirements.Requirement;
import org.prelle.splimo.requirements.RequirementList;

/**
 * @author prelle
 *
 */
public class Importer {
	
	static List<String[]> pro;
	Persister serializer;
	WeaponDamageConverter dmgConv = new WeaponDamageConverter();

	//-------------------------------------------------------------------
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		SplitterMondCore.initialize(new DummyRulePlugin<>());
		Importer importer = new Importer();
		importer.process("handgemenge", ItemType.WEAPON, SplitterMondCore.getSkill("melee"), 7);
		importer.process("hiebwaffen", ItemType.WEAPON, SplitterMondCore.getSkill("slashing"), 10);
//		importer.process("kettenwaffen", ItemType.WEAPON, SplitterMondCore.getSkill("chains"), 15);
//		importer.process("klingenwaffen", ItemType.WEAPON, SplitterMondCore.getSkill("blades"), 19);
//		importer.process("stangenwaffen", ItemType.WEAPON, SplitterMondCore.getSkill("staffs"), 26);
//		importer.process("schusswaffen", ItemType.LONG_RANGE_WEAPON, SplitterMondCore.getSkill("longrange"), 32);
//		importer.process("projectiles", ItemType.PROJECTILE, null, 35);
//		importer.process("wurfwaffen", ItemType.LONG_RANGE_WEAPON, SplitterMondCore.getSkill("longrange"), 38);
//		importer.process("ruestungen", ItemType.ARMOR, null, 41);
//		importer.process("schilde", ItemType.SHIELD, null, 46);
//		importer.process("weaponitems", ItemType.OTHER, null, 50);
//		importer.process("container", ItemType.CONTAINER, null, 51);
//		importer.process("tools", ItemType.TOOLS, null, 53);
//		importer.process("animals", ItemType.ANIMALS, null, 55);
//		importer.process("clothing", ItemType.CLOTHING, null, 58);
//		importer.process("shady", ItemType.SHADY, null, 62);
//		importer.process("light", ItemType.LIGHT, null, 65);
//		importer.process("travel", ItemType.TRAVEL, null, 67);
//		importer.process("healing", ItemType.TOOLS, null, 69);
//		importer.process("alchemy", ItemType.POTION, null, 70);
//		importer.process("writing", ItemType.OTHER, null, 72);
//		importer.process("climbing", ItemType.OTHER, null, 74);
//		importer.process("cosmetics", ItemType.OTHER, null, 75);
//		importer.process("recreation", ItemType.OTHER, null, 76);
		
		FileWriter out = new FileWriter("generated.propertes");
//		List<Object> keys = new ArrayList<>(pro.keySet());
//		Collections.sort(keys, new Comparator<Object>() {
//			public int compare(Object o1, Object o2) {
//				return o1.toString().compareTo(o2.toString());
//			}
//		});
		PrintWriter pout = new PrintWriter(out);
//		for (Object key : keys)
//			pout.println(key+"="+pro.getProperty((String) key));
		for (String[] pair : pro)
			pout.println(pair[0]+"="+pair[1]);
//		pro.list(new PrintWriter(out));
		out.flush();
		out.close();
	}

	//-------------------------------------------------------------------
	public Importer() {
		pro = new ArrayList<>();
		serializer = new Persister();
	}

	//-------------------------------------------------------------------
	public void process(String file, ItemType type, Skill skill, int page) {
		System.out.println(file);
		pro.add(new String[]{"\n#"+file,""});
		Path csvFile = (new File(file+".csv")).toPath();
		Path xmlFile = (new File(file+".xml")).toPath();
		
		ItemList list = new ItemList();
		try {
			Files.lines(csvFile).forEach( (line) -> processLine(line, type, list, skill, page));
		} catch (Exception e) {
			System.err.println("Error in "+file);
			e.printStackTrace();
			System.exit(0);
		}
		
		try {
			System.out.println("Write "+xmlFile);
			BufferedWriter out = Files.newBufferedWriter(xmlFile);
//			serializer.write(list, new PrintWriter(System.out));
			serializer.write(list, new PrintWriter(out));
			out.close();
		} catch (SerializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//-------------------------------------------------------------------
	private static String convertToId(String name) {
		StringBuffer buf = new StringBuffer();
		StringTokenizer tok = new StringTokenizer(name, " ,()");
		boolean first = true;
		while (tok.hasMoreTokens()) {
			String e = tok.nextToken().toLowerCase();
			if (first) {
				buf.append(e);
				first = false;
			} else {
				buf.append( (new StringBuffer(e)).replace(0, 1, (""+e.charAt(0)).toUpperCase()).toString() );
			}
		}
		return buf.toString();
	}

	//-------------------------------------------------------------------
	private static Skill getSkillByName(String name) {
		for (Skill skill : SplitterMondCore.getSkills()) {
			if (skill.getName().equals(name))
				return skill;
		}
		if ("Diverse".equals(name))
			return null;
		throw new IllegalArgumentException("No such skill '"+name+"'");
	}

	//-------------------------------------------------------------------
	private static SkillSpecialization getSkillSpecial(Skill skill, String name) {
		for (SkillSpecialization spec : skill.getSpecializations()) {
			if (spec.getName().contains(name))
				return spec;
		}
		if ("Diverse".equals(name))
			return null;
		throw new IllegalArgumentException("No such skill specialization '"+name+"'  in "+skill);
	}

	//-------------------------------------------------------------------
	private void processLine(String line, ItemType type, ItemList list, Skill skill, int page) {
		if (line==null || line.isEmpty())
			return;
		try {
			StringTokenizer tok = new StringTokenizer(line,";");
			
			String name = tok.nextToken();
			String id = convertToId(name);
			pro.add(new String[]{"item."+id, name});
			pro.add(new String[]{"item."+id+".page", String.valueOf(page)});
//			pro.setProperty("item."+id, name);
//			pro.setProperty("item."+id+".page", String.valueOf(page));
			ItemTemplate item = new ItemTemplate(id);
			item.addItemType(type);
			list.add(item);

			try { item.setAvailability( Availability.getByName(tok.nextToken()) ); } catch (Exception e) {}
			item.setPrice( priceToTelare(tok.nextToken()) );
			item.setLoad( Integer.parseInt(tok.nextToken()) );
			try { item.setRigidity( Integer.parseInt(tok.nextToken()) ); } catch (NumberFormatException e) {}
			
			switch (type) {
			case WEAPON:
				item.setComplexity(Complexity.getByID(tok.nextToken().trim()) );
				item.setMaterialType(MaterialType.METAL);
				Weapon weapon = new Weapon();
				item.addItemTypeData(weapon);
				weapon.setSkill(skill);
				if (skill==null)
					throw new NullPointerException("No skill for "+line);
				weapon.setDamage( dmgConv.read(tok.nextToken()) ) ;
				weapon.setSpeed( Integer.parseInt(tok.nextToken()) );
				parseAttributePair(weapon, tok.nextToken().trim());
				for (Requirement req : parseRequirements(tok.nextToken()))
					weapon.addRequirement(req);
				for (Feature feat : parseFeatures(tok.nextToken()))
					weapon.addFeature(feat);
				break;
			case LONG_RANGE_WEAPON:
				item.setComplexity(Complexity.getByID(tok.nextToken().trim()) );
				item.setMaterialType(MaterialType.WOOD);
				LongRangeWeapon lrWeapon = new LongRangeWeapon();
				item.addItemTypeData(lrWeapon);
				lrWeapon.setSkill(skill);
				if (skill==null)
					throw new NullPointerException("No skill for "+line);
				lrWeapon.setDamage( dmgConv.read(tok.nextToken()) ) ;
				lrWeapon.setSpeed( Integer.parseInt(tok.nextToken().trim()) );
				parseAttributePair(lrWeapon, tok.nextToken().trim());
				for (Requirement req : parseRequirements(tok.nextToken().trim()))
					lrWeapon.addRequirement(req);
				for (Feature feat : parseFeatures(tok.nextToken().trim()))
					lrWeapon.addFeature(feat);
				lrWeapon.setRange( Integer.parseInt(tok.nextToken().trim()) );
				break;
			case PROJECTILE:
				Projectile project = new Projectile();
				item.addItemTypeData(project);
				item.setMaterialType(MaterialType.METAL);
				project.setSkill(skill);
				break;
			case SHIELD:
				item.setComplexity(Complexity.getByID(tok.nextToken().trim()) );
				item.setMaterialType(MaterialType.METAL);
				Shield shield = new Shield();
				item.addItemTypeData(shield);
				shield.setDefense( Integer.parseInt(tok.nextToken().trim()) );
				shield.setHandicap( Integer.parseInt(tok.nextToken().trim()) );
				shield.setTickMalus( Integer.parseInt(tok.nextToken().trim()) );
				for (Requirement req : parseRequirements(tok.nextToken().trim()))
					shield.addRequirement(req);
				for (Feature feat : parseFeatures(tok.nextToken().trim()))
					shield.addFeature(feat);
				break;
			case ARMOR:
				item.setComplexity(Complexity.getByID(tok.nextToken().trim()) );
				item.setMaterialType(MaterialType.METAL);
				Armor armor = new Armor();
				item.addItemTypeData(armor);
				armor.setDefense( Integer.parseInt(tok.nextToken().trim()) );
				armor.setDamageReduction( Integer.parseInt(tok.nextToken().trim()) );
				armor.setHandicap( Integer.parseInt(tok.nextToken().trim()) );
				armor.setTickMalus( Integer.parseInt(tok.nextToken().trim()) );
				String armReq = tok.nextToken().trim();
				if (!"-".equals(armReq)) {
				for (Requirement req : parseRequirements("STÄ "+armReq))
					armor.addRequirement(req);
				}
				for (Feature feat : parseFeatures(tok.nextToken().trim()))
					armor.addFeature(feat);
				break;
			case LIGHT:
				String lvlOrSkill = tok.nextToken().trim();
				String rangeOrSpec = tok.nextToken().trim();
				try {
					int lvl = Integer.parseInt(lvlOrSkill);
					int rng = Integer.parseInt( rangeOrSpec.substring(0, rangeOrSpec.indexOf('m')).trim());
					Light light = new Light();
					light.setLevel(lvl);
					light.setRange(rng);
					item.addItemTypeData(light);
				} catch (NumberFormatException nfe) {
					
				}
				String cplx = tok.nextToken().trim();
				if ("-".equals(cplx))
					cplx="U";
				item.setComplexity(Complexity.getByID(cplx) );
				break;
			default:
				switch (type) {
				case TOOLS:
				case SHADY:
					item.setMaterialType(MaterialType.METAL); break;
				case CLOTHING: item.setMaterialType(MaterialType.FABRIC); break;
				}
				
				String skillName = tok.nextToken().trim();
				String specName = tok.nextToken().trim();
				if (!"-".equals(skillName)) {
					skill = getSkillByName(skillName);
					item.setSkill(skill);
					if (skill!=null && !"-".equals(specName)) {
						SkillSpecialization spec = getSkillSpecial(skill, specName);
						item.setSpecialization(spec);
					}
				}
				cplx = tok.nextToken().trim();
				if ("-".equals(cplx))
					cplx="U";
				item.setComplexity(Complexity.getByID(cplx) );
				break;
			}
			
//			System.out.println(String.valueOf(item));
		} catch (Exception e) {
			System.err.println("Error in line "+line+": "+e);
			e.printStackTrace();
			System.exit(0);
		}
	}

	//-------------------------------------------------------------------
	public static int priceToTelare(String price) {
		if (price.equals("-"))
			return 0;
		int count = 0;
		String cur = null;
		if (price.contains(" ")) {
			StringTokenizer tok = new StringTokenizer(price);
			count = Integer.parseInt(tok.nextToken());
			cur = tok.nextToken();			
		} else {
			count = Integer.parseInt(price.substring(0, price.length()-1));
			cur   = price.substring(price.length()-1);
		}
		if (cur.equalsIgnoreCase("L"))
			return count*100;
		if (cur.equalsIgnoreCase("T"))
			return count;
		throw new IllegalArgumentException("Unknown currency "+cur);
	}

	//-------------------------------------------------------------------
	private void parseAttributePair(Weapon weapon, String pair) {
		StringTokenizer tok = new StringTokenizer(pair," +");
		String att1 = tok.nextToken();
		boolean found = false;
		for (Attribute tmp : Attribute.values()) {
			if (tmp.getShortName().equalsIgnoreCase(att1)) {
				weapon.setAttribute1(tmp);
				found = true;
			}
		}
		if (!found)
			throw new IllegalArgumentException("Unknown attribute1 '"+att1+"'");
		String att2 = tok.nextToken();
		found = false;
		for (Attribute tmp : Attribute.values()) {
			if (tmp.getShortName().equalsIgnoreCase(att2)) {
				weapon.setAttribute2(tmp);
				found = true;
			}
		}
		if (!found)
			throw new IllegalArgumentException("Unknown attribute2 "+att1);
	}

	//-------------------------------------------------------------------
	private RequirementList parseRequirements(String reqString) {
//		System.err.println("parseReq: '"+reqString+"'");
		RequirementList ret = new RequirementList();
		if (reqString.trim().equals("-"))
			return ret;
		if (reqString.trim().equals("/"))
			return ret;
		StringTokenizer tok = new StringTokenizer(reqString, ",");
		while (tok.hasMoreElements()) {
			String line = tok.nextToken();
			StringTokenizer tok2 = new StringTokenizer(line);
			String att1 = tok2.nextToken().trim();
			if ("-".equals(att1))
				continue;
			Attribute att = null;
			for (Attribute tmp : Attribute.values()) {
				if (tmp.getShortName().equalsIgnoreCase(att1)) {
					att = tmp;
					break;
				}
			}
			int count = Integer.parseInt(tok2.nextToken());
			ret.add(new AttributeRequirement(att, count));
		}
		return ret;
	}

	//-------------------------------------------------------------------
	private Feature parseFeature(String featureString) {
		List<String> tokens = new ArrayList<>();
		
		StringTokenizer tok2 = new StringTokenizer(featureString, " ()");
		while (tok2.hasMoreTokens()) 
			tokens.add(tok2.nextToken().trim());
		
		
		switch (tokens.size()) {
		case 1:
			return new Feature(FeatureType.getByName(tokens.get(0)));
		case 2:
			try {
				int lvl = 0;
				if ("I".equals(tokens.get(1))) 
					return new Feature(FeatureType.getByName(tokens.get(0)),1);
				lvl = Integer.parseInt(tokens.get(1));
				return new Feature(FeatureType.getByName(tokens.get(0)),lvl);
			} catch (NumberFormatException e) {
				// E.g. "Lange Waffe"
				return new Feature(FeatureType.getByName(tokens.get(0)+" "+tokens.get(1)));
				
			}
		case 3:
			String name = tokens.get(0)+" "+tokens.get(1);
			FeatureType type = FeatureType.getByName(name);
			if ("I".equals(tokens.get(2))) 
				return new Feature(type,1);
			int lvl = Integer.parseInt(tokens.get(1));
			return new Feature(FeatureType.getByName(name),lvl);
		}
		throw new IllegalArgumentException(featureString);
	}

	//-------------------------------------------------------------------
	private FeatureList parseFeatures(String reqString) {
		FeatureList ret = new FeatureList();
		if (reqString.trim().equals("-"))
			return ret;
		if (reqString.trim().equals("/"))
			return ret;
		StringTokenizer tok = new StringTokenizer(reqString, ",");
		while (tok.hasMoreElements()) {
			String line = tok.nextToken().trim();
			if (line.length()<1) 
				continue;
			ret.add(parseFeature(line));
		}
		return ret;
	}
	
}
