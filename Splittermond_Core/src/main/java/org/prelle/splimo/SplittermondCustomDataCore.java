package org.prelle.splimo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.simplepersist.Persister;
import org.prelle.simplepersist.Serializer;
import org.prelle.splimo.SkillSpecialization.SkillSpecializationType;
import org.prelle.splimo.items.ItemList;
import org.prelle.splimo.items.ItemTemplate;
import org.prelle.splimo.items.ItemType;
import org.prelle.splimo.items.LongRangeWeapon;
import org.prelle.splimo.items.Weapon;

import de.rpgframework.RPGFramework;
import de.rpgframework.RPGFrameworkLoader;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.core.RoleplayingSystem;

/**
 * @author prelle
 *
 */
public class SplittermondCustomDataCore {

	private final static Logger logger = LogManager.getLogger("splittermond");

	static class PerTypeData {
		Path datafile;
		Path i18nPath;
		Path i18nHelpPath;
		WriteablePropertyResourceBundle i18NResources;
		WriteablePropertyResourceBundle i18NHelpResources;

		public PerTypeData(Path srDataDir, String name) {
			datafile  = srDataDir.resolve(name+".xml");
			i18nPath  = srDataDir.resolve(name+".properties");
			i18nHelpPath = srDataDir.resolve(name+"-help.properties");
			try {
				Files.createDirectories(srDataDir);
				if (!Files.exists(i18nPath))
					Files.createFile(i18nPath);
				if (!Files.exists(i18nHelpPath))
					Files.createFile(i18nHelpPath);
				i18NResources = new WriteablePropertyResourceBundle(name, new FileReader(i18nPath.toFile()));
				i18NHelpResources = new WriteablePropertyResourceBundle(name+"-help", new FileReader(i18nHelpPath.toFile()));
			} catch (IOException e) {
				logger.fatal("Failed creating custom directory or file: "+i18nPath+": "+e);
			}
			SplittermondCustomDataCore.logger.debug("For "+name+" i18n is "+i18NResources);
		}
	}

	private static Path splimoDataDir;
	private static Map<Class<? extends BasePluginData>, PerTypeData> definitions;
	private static Serializer serializer;

	private static ItemList items;
	private static EducationList educations;

	//-------------------------------------------------------------------
	static {
		String dataDir = RPGFrameworkLoader.getInstance().getConfiguration().getOption(RPGFramework.PROP_DATADIR).getStringValue();
		splimoDataDir = FileSystems.getDefault().getPath(dataDir, "custom", RoleplayingSystem.SPLITTERMOND.name().toLowerCase());
		logger.info("Search for custom items in "+splimoDataDir);

		definitions = new HashMap<Class<? extends BasePluginData>, SplittermondCustomDataCore.PerTypeData>();
		definitions.put(ItemTemplate.class, new PerTypeData(splimoDataDir,"items"));
		definitions.put(Education.class, new PerTypeData(splimoDataDir,"educations"));

		serializer = new Persister();

		loadEquipment();
		loadEducations();

//		logger.fatal("Stop here");
//		System.exit(0);
	}

	//-------------------------------------------------------------------
	public static WriteablePropertyResourceBundle getI18nResources(Class<? extends BasePluginData> cls) { return definitions.get(cls).i18NResources; }
	public static WriteablePropertyResourceBundle getI18nHelpResources(Class<? extends BasePluginData> cls) { return definitions.get(cls).i18NHelpResources; }

	//-------------------------------------------------------------------
	private static void loadEquipment() {
		PerTypeData typeData = definitions.get(ItemTemplate.class);

		Path readPath = typeData.datafile;
		if (!Files.exists(readPath)) {
			items = new ItemList();
			return;
		}

		logger.debug("Load equipment ("+readPath+")");

		try {
			InputStream in = new FileInputStream(readPath.toFile());
			items = serializer.read(ItemList.class, in);
			logger.info("Successfully loaded "+items.size()+" equipments");

			// Set translation
			for (ItemTemplate tmp : new ArrayList<>(items)) {
				tmp.setResourceBundle(typeData.i18NResources);
				tmp.setHelpResourceBundle(typeData.i18NHelpResources);
				if (logger.isDebugEnabled()) {
					logger.debug("* "+tmp.getName());
					tmp.getPage();
				}
			}


			/*
			 * For all weapons add possible specialization
			 */
			logger.debug("Add weapon specializations to skills");
			for (ItemTemplate templ : new ArrayList<>(items)) {
				Weapon tmp = templ.getType(Weapon.class);
				if (tmp==null)
					continue;
				Skill skill = tmp.getSkill();
				if (skill==null) {
					logger.error("No skill assigned to custom weapon "+templ.getID());
					BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 1, "Cannot load weapon '"+templ.getID()+"' since it has no skill assigned.\nDeleting the file "+readPath+" might help.\nMake sure you delete that item from all your characters");
					items.remove(templ);
					removeItem(templ);
				}
				if (skill.getSpecialization(templ.getID())!=null) {
					logger.debug("ignore already known skill spec "+templ);
					continue;
				}
				SkillSpecialization spec = new SkillSpecialization();
				spec.setType(SkillSpecializationType.WEAPON);
				spec.setId(templ.getID());
				skill.addSpecialization(spec);
				logger.trace("Added skill special "+spec);
			}
			/*
			 * For all range weapons add possible specialization
			 */
			logger.debug("Add range weapon specializations to skills");
			for (ItemTemplate templ : items) {
				LongRangeWeapon tmp = templ.getType(LongRangeWeapon.class);
				if (tmp==null)
					continue;
				Skill skill = tmp.getSkill();
				if (skill.getSpecialization(templ.getID())!=null) {
					logger.warn("ignore already known skill spec "+templ);
					continue;
				}
				SkillSpecialization spec = new SkillSpecialization();
				spec.setType(SkillSpecializationType.WEAPON);
				spec.setId(templ.getID());
				skill.addSpecialization(spec);
				logger.trace("Added skill special "+spec);
			}

			Collections.sort(items);

		} catch (Exception e) {
			logger.fatal("Failed loading equipment: "+e,e);
			items = new ItemList();
		}
	}

	//-------------------------------------------------------------------
	public static void saveEquipment() throws IOException {
		PerTypeData typeData = definitions.get(ItemTemplate.class);

		// Write properties
		try {
			logger.debug("Write "+typeData.i18nPath);
			typeData.i18NResources.write(new FileWriter(typeData.i18nPath.toFile()), "Custom items");
			logger.debug("Write "+typeData.i18nHelpPath);
			typeData.i18NHelpResources.write(new FileWriter(typeData.i18nHelpPath.toFile()), "Custom items");

			logger.debug("Write "+typeData.datafile);
			serializer.write(items, new FileOutputStream(typeData.datafile.toFile()));
		} catch (IOException e) {
			logger.error("Failed writing custom equipment",e);
			throw e;
		}
	}

	//-------------------------------------------------------------------
	public static List<ItemTemplate> getItems() {
		List<ItemTemplate> list = new ArrayList<>(items);
		Collections.sort(items);
		return list;
	}

	//-------------------------------------------------------------------
	public static List<ItemTemplate> getItems(ItemType type) {
		List<ItemTemplate> list = new ArrayList<>();
		for (ItemTemplate item : items) {
			if (item.isType(type))
				list.add(item);
		}

		return list;
	}

	//-------------------------------------------------------------------
	public static ItemTemplate getItem(String key) {
		for (ItemTemplate tmp : items) {
			if (tmp.getID().equals(key))
				return tmp;
		}
		return null;
	}

	//-------------------------------------------------------------------
	public static void addItem(ItemTemplate data) {
		if (getItem(data.getID())!=null)
			throw new IllegalStateException("Item "+data.getID()+" already exists");
		PerTypeData typeData = definitions.get(ItemTemplate.class);
		data.setResourceBundle(typeData.i18NResources);
		data.setHelpResourceBundle(typeData.i18NHelpResources);
		items.add(data);

	}

	//-------------------------------------------------------------------
	public static void removeItem(ItemTemplate data) {
		items.remove(data);
	}

	//-------------------------------------------------------------------
	private static void loadEducations() {
		PerTypeData typeData = definitions.get(Education.class);

		Path readPath = typeData.datafile;
		if (!Files.exists(readPath)) {
			educations = new EducationList();
			return;
		}

		logger.debug("Load educations ("+readPath+")");

		try {
			InputStream in = new FileInputStream(readPath.toFile());
			educations = serializer.read(EducationList.class, in);
			logger.info("Successfully loaded "+items.size()+" educations");

			// Set translation
			for (Education tmp : new ArrayList<>(educations)) {
				tmp.setResourceBundle(typeData.i18NResources);
				tmp.setHelpResourceBundle(typeData.i18NHelpResources);
				if (logger.isDebugEnabled()) {
					logger.debug("* "+tmp.getName());
					tmp.getPage();
				}
			}


			Collections.sort(educations);

		} catch (Exception e) {
			logger.fatal("Failed loading educations: "+e,e);
		}
	}

	//-------------------------------------------------------------------
	public static void saveEducation() throws IOException {
		PerTypeData typeData = definitions.get(ItemTemplate.class);

		// Write properties
		try {
			logger.debug("Write "+typeData.i18nPath);
			typeData.i18NResources.write(new FileWriter(typeData.i18nPath.toFile()), "Custom educations");
			logger.debug("Write "+typeData.i18nHelpPath);
			typeData.i18NHelpResources.write(new FileWriter(typeData.i18nHelpPath.toFile()), "Custom educations");

			logger.debug("Write "+typeData.datafile);
			serializer.write(items, new FileOutputStream(typeData.datafile.toFile()));
		} catch (IOException e) {
			logger.error("Failed writing custom educations",e);
			throw e;
		}
	}

	//-------------------------------------------------------------------
	public static Education getEducation(String key) {
		for (Education tmp : educations) {
			if (tmp.getKey().equals(key))
				return tmp;
		}
		return null;
	}

	//-------------------------------------------------------------------
	public static List<Education> getEducations() {
		List<Education> list = new ArrayList<>(educations);
		Collections.sort(list);
		return list;
	}

	//-------------------------------------------------------------------
	public static void addEducation(Education data) {
		if (getEducation(data.getKey())!=null)
			throw new IllegalStateException("Education "+data.getKey()+" already exists");
		PerTypeData typeData = definitions.get(ItemTemplate.class);
		data.setResourceBundle(typeData.i18NResources);
		data.setHelpResourceBundle(typeData.i18NHelpResources);
		educations.add(data);

	}

	//-------------------------------------------------------------------
	public static void removeEducation(Education data) {
		educations.remove(data);
	}

	//--------------------------------------------------------------------
	private static boolean sendEmail(Session session, String fromEmail, String displayName, String subject, BodyPart...parts) throws IOException, SendFailedException {
		try {
			Multipart multipart = new MimeMultipart();
			for (BodyPart part : parts)
				multipart.addBodyPart(part);

			MimeMessage msg = new MimeMessage(session);
			msg.addHeader("Content-type", "text/plain; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");

			msg.setFrom(new InternetAddress(fromEmail, displayName));
			msg.setSubject(subject, "UTF-8");
			msg.setSentDate(new Date());

			msg.setContent(multipart);
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("splittermond@rpgframework.de", false));
			Transport.send(msg);

			logger.debug("EMail Sent Successfully!!");
			return true;
		} catch (SendFailedException e) {
			logger.error("Foo "+Arrays.toString(e.getInvalidAddresses()));
			throw e;
		} catch (MessagingException e) {
			logger.error("Failed sending mail",e);
			throw new IOException(e);
		}
	}

	//-------------------------------------------------------------------
	public static boolean upload(List<ItemTemplate> list, String mailAddress, String displayName, String textBody) throws IOException {
		logger.info("upload "+list.size()+" items");
		ItemList data = new ItemList(list);

		// Build specific property files
		Properties normPro = new Properties();
		Properties helpPro = new Properties();
		for (ItemTemplate item : list) {
			normPro.put("item."+item.getID(), item.getName());
			normPro.put(item.getPageI18NKey(), String.valueOf(item.getPage()));
			if (item.getHelpText()!=null)
				helpPro.put(item.getHelpI18NKey(), item.getHelpText());
		}

		// Write data
		try {
			StringWriter outNorm = new StringWriter();
			normPro.store(outNorm, "Generated by "+displayName+" ("+mailAddress+")");
			StringWriter outHelp = new StringWriter();
			helpPro.store(outHelp, "Generated by "+displayName+" ("+mailAddress+")");
			StringWriter outXML  = new StringWriter();
			serializer.write(data, outXML);

			// Text
			BodyPart partBody = new MimeBodyPart();
			partBody.setText(textBody);
			// XML
			MimeBodyPart partXML = new MimeBodyPart();
			DataSource ds;
			partXML.setDataHandler(new DataHandler(new ByteArrayDataSource(outXML.toString().getBytes("UTF-8"), "text/plain; charset=UTF-8")));
			partXML.setFileName("items.xml");
			// i18n
			MimeBodyPart partNorm = new MimeBodyPart();
			partNorm.setDataHandler(new DataHandler(new ByteArrayDataSource(outNorm.toString().getBytes("UTF-8"), "text/plain; charset=iso-8859-1")));
			partNorm.setFileName("items.properties");
			// i18n-help
			MimeBodyPart partHelp = new MimeBodyPart();
			partHelp.setDataHandler(new DataHandler(new ByteArrayDataSource(outHelp.toString().getBytes("UTF-8"), "text/plain; charset=iso-8859-1")));
			partHelp.setFileName("items-help.properties");

			String smtpHostServer = "smtp.rpgframework.de";

		    Properties props = System.getProperties();

		    props.put("mail.smtp.host", smtpHostServer);

		    Session session = Session.getInstance(props, null);


		    return sendEmail(session, mailAddress, displayName,"Genesis Item Upload", partBody, partXML, partNorm, partHelp);
		} catch (SendFailedException e) {
			logger.error("Failed sending item mail",e.getNextException());
			throw new IOException("Failed sending mail: "+e.getNextException().getMessage());
		} catch (Exception e) {
			logger.error("Failed sending item mail",e);
		}
		return false;
	}

}
