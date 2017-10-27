/**
 *
 */
package org.prelle.splimo.commandbus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.prelle.splimo.Culture;
import org.prelle.splimo.SpliMoNameTable;
import org.prelle.splimo.SplitterMondCore;

import de.rpgframework.core.CommandBusListener;
import de.rpgframework.core.CommandResult;
import de.rpgframework.core.CommandType;
import de.rpgframework.core.RoleplayingSystem;
import de.rpgframework.worldinfo.WorldInformationType;

/**
 * @author Stefan
 *
 */
public class SplittermondCommandBus implements CommandBusListener {

	private static Logger logger = Logger.getLogger("splittermond");

	//--------------------------------------------------------------------
	/**
	 */
	public SplittermondCommandBus() {
		// TODO Auto-generated constructor stub
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.CommandBusListener#getReadableName()
	 */
	@Override
	public String getReadableName() {
		return RoleplayingSystem.SPLITTERMOND.getName()+" Core";
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.CommandBusListener#willProcessCommand(java.lang.Object, de.rpgframework.core.CommandType, java.lang.Object[])
	 */
	@Override
	public boolean willProcessCommand(Object src, CommandType type,
			Object... values) {
		switch (type) {
		case GET_WORLD_INFO_TYPES:
			return values[0] == RoleplayingSystem.SPLITTERMOND;
		case GET_WORLD_INFO_FILTER:
			if (values[0] == RoleplayingSystem.SPLITTERMOND) return false;
			return values[1] == WorldInformationType.NAMES;
		case GET_WORLD_INFO_TABLE:
			if (values[0] == RoleplayingSystem.SPLITTERMOND) return false;
			return values[1] == WorldInformationType.NAMES;
		}
		return false;
	}

	//--------------------------------------------------------------------
	/**
	 * @see de.rpgframework.core.CommandBusListener#handleCommand(java.lang.Object, de.rpgframework.core.CommandType, java.lang.Object[])
	 */
	@Override
	public CommandResult handleCommand(Object src, CommandType type,
			Object... values) {
		switch (type) {
		case GET_WORLD_INFO_TYPES:
			if (RoleplayingSystem.SPLITTERMOND.equals(values[0])) {
				return new CommandResult(type,  Arrays.asList(WorldInformationType.NAMES));
			}
			return new CommandResult(type, false);
		case GET_WORLD_INFO_FILTER:
			// 0 = RoleplayingSystem
			// 1 = WorldInformationType
			if (WorldInformationType.NAMES==values[1]) {
				List<Culture> list = new ArrayList<>();
				for (SpliMoNameTable table : SplitterMondCore.getNameTables()) {
					list.add(table.getCulture());
				}
				return new CommandResult(type,  Arrays.asList(new CultureFilter(list)));
			}
			return new CommandResult(type, false);
		}
		return null;
	}

}
