package game.plugins.datatemplates;

import game.configuration.ConfigurableMap;
import game.configuration.errorchecks.SizeCheck;
import game.core.DataTemplate;

public class PropertiesTemplate extends DataTemplate {
	
	public ConfigurableMap properties = new ConfigurableMap(this, DataTemplate.class);
	
	public PropertiesTemplate() {
		setOptionChecks("properties", new SizeCheck(1));
	}

	@Override
	public int getDescriptionLength() {
		int count = 0;
		for (DataTemplate template: properties.values(DataTemplate.class))
			count += template.getDescriptionLength();
		return count;
	}

}
