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
package game.configuration;

import java.util.Collection;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ConfigurableCollectionConverter extends BaseConverter {

	@Override
	public void marshal(Object o, HierarchicalStreamWriter writer,	MarshallingContext context) {
		Configurable col = (Configurable)o;
		
		for (String optionName: col.getAllOptionNames()) {
			/*
			if (col.isOmittedFromConfiguration(optionName))
				continue;
			*/
			Object option = col.getOption(optionName);
			if (option != null) {
				if (optionName.matches("^\\d+$")) {
					writer.startNode("li");
					writer.addAttribute("index", optionName);
				} else
					writer.startNode(optionName);
				
				if (option.getClass() != col.getOptionType(optionName))
					writer.addAttribute("class", option.getClass().getName());
				context.convertAnother(option);
				writer.endNode();
			}
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Configurable col = null;
		
		try {
			col = (Configurable)(context.currentObject() != null ? context.currentObject() 
															     : context.getRequiredType().newInstance());
			((Collection)col).clear();
			
			while(reader.hasMoreChildren()) {
				reader.moveDown();
				String optionName = reader.getNodeName();
				if (optionName.equals("li"))
					optionName = reader.getAttribute("index");
				if (optionName.matches("^item\\d+$"))
					optionName = optionName.substring(4);
				
				String className = reader.getAttribute("class");
				
				Class optionType = className != null ? getClassLoader().loadClass(className) : col.getOptionType(optionName);
				if (optionName.matches("^\\d+$"))
					col.setOption("add", context.convertAnother(col, optionType));
				else
					col.setOption(optionName, context.convertAnother(col, optionType)/*, false, null*/);
				reader.moveUp();
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return col;
	}

	@Override
	public boolean canConvert(Class clazz) {
		return Configurable.class.isAssignableFrom(clazz)
				&& Collection.class.isAssignableFrom(clazz);
	}

}
