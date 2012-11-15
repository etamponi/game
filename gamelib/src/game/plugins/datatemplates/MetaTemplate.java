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

import game.core.DataTemplate;

import java.util.HashMap;
import java.util.Map;

import com.ios.ErrorCheck;
import com.ios.IMap;

public class MetaTemplate extends DataTemplate {
	
	public class MetaData extends Data<Map<String, Data>> {
		
		protected MetaData() {}

		public Map<String, Data> addEmptyElement() {
			Map<String, Data> map = new HashMap<>();
			for (String key: templates.keySet())
				map.put(key, templates.get(key).newData());
			add(map);
			return map;
		}

		@Override
		protected Class getElementType() {
			return Map.class;
		}
		
	}
	
	public IMap<DataTemplate> templates;
	
	public MetaTemplate() {
		setContent("templates", new IMap<>(DataTemplate.class));
		
		addErrorCheck("templates", new ErrorCheck<Map>() {
			@Override
			public String getError(Map value) {
				if (value.isEmpty())
					return "must have at least one template";
				else
					return null;
			}
		});
	}

	@Override
	public int getDescriptionLength() {
		int count = 0;
		for (DataTemplate template: templates.values())
			count += template.getDescriptionLength();
		return count;
	}

	@Override
	public Data newData() {
		return new MetaData();
	}

}
