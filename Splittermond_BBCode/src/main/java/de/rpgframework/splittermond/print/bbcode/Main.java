package de.rpgframework.splittermond.print.bbcode;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.splimo.DummyRulePlugin;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;

import de.rpgframework.core.CommandResult;
import de.rpgframework.core.CommandType;
import de.rpgframework.print.PrintType;

public class Main {

	private static Logger logger = LogManager.getLogger(Main.class);

	public static void main(String[] args) {
		SplitterMondCore.initialize(new DummyRulePlugin<>());
		
//		MondstahlklingenPlugin msk = new MondstahlklingenPlugin();
//		msk.init();

		File path = (new File(
				"../Splittermond_Core/testdata/Lenkan.xml"));
		SpliMoCharacter character = null;
		try {
			character = SplitterMondCore.load(path.toPath());
		} catch (IOException e) {
			logger.error("Error at opening character", e);
		}

//		logger.info("Geladen: " + character.dump());

		BBCodePlugin spliMoBBCode = new BBCodePlugin();
		CommandResult bbcode = spliMoBBCode.handleCommand(null,
				CommandType.PRINT, null, character, null, null,
				PrintType.BBCODE);

		System.out.println(bbcode.getReturnValue());
	}
}
