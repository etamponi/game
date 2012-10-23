/*******************************************************************************
 * Copyright (c) 2012 Emanuele.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele - initial API and implementation
 ******************************************************************************/
package game.plugins.datatemplates;

import game.configuration.ConfigurableMap;
import game.configuration.ErrorCheck;
import game.core.DataTemplate;

import java.util.HashMap;
import java.util.Map;

public class PropertiesTemplate extends DataTemplate {
	
	public class PropertiesData extends Data<Map<String, Data>> {
		
		protected PropertiesData() {}

		public Map<String, Data> addEmptyElement() {
			Map<String, Data> map = new HashMap<>();
			for (String key: properties.keySet())
				map.put(key, ((DataTemplate)properties.get(key)).newDataInstance());
			add(map);
			return map;
		}
		
	}
	
	public ConfigurableMap properties = new ConfigurableMap(this, DataTemplate.class);
	
	public PropertiesTemplate() {
		setOptionChecks("properties", new ErrorCheck<Map>() {
			@Override
			public String getError(Map value) {
				if (value.isEmpty())
					return "must have at least one property";
				else
					return null;
			}
		});
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
		return new PropertiesData();
	}

}
