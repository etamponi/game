package game.plugins.datatemplates;

import game.configuration.ConfigurableMap;
import game.configuration.errorchecks.SizeCheck;
import game.core.DataTemplate;

import java.util.HashMap;
import java.util.Map;

public class PropertiesTemplate extends DataTemplate {
	
	public static class PropertiesData extends Data<Map<String, Data>, PropertiesTemplate> {
		
		protected PropertiesData() {}
		
		protected PropertiesData(PropertiesTemplate template) {
			super(template);
		}

		public Map<String, Data> addEmptyElement() {
			Map<String, Data> map = new HashMap<>();
			for (String key: getTemplate().properties.keySet())
				map.put(key, ((DataTemplate)getTemplate().properties.get(key)).newDataInstance());
			add(map);
			return map;
		}
		
	}
	
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

	@Override
	public Data newDataInstance() {
		return new PropertiesData(this);
	}

}
