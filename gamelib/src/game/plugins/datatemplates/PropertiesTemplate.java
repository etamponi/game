package game.plugins.datatemplates;

import game.configuration.ConfigurableList;
import game.configuration.errorchecks.SizeCheck;
import game.core.DataTemplate;

public class PropertiesTemplate extends DataTemplate {
	
	public ConfigurableList properties = new ConfigurableList(this, DataTemplate.class);
	
	public PropertiesTemplate() {
		setOptionChecks("properties", new SizeCheck(1));
	}

	@Override
	public int getDescriptionLength() {
		// TODO Auto-generated method stub
		return 0;
	}

}
