package game.plugins.editors.configurablelist;

import game.configuration.ConfigurableList;
import game.editorsystem.ControlledEditor;

public class ConfigurableListEditor extends ControlledEditor {

	@Override
	public Class getBaseEditableClass() {
		return ConfigurableList.class;
	}

}
