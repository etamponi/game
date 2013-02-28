/*******************************************************************************
 * Copyright (c) 2012 Emanuele Tamponi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele Tamponi - initial API and implementation
 ******************************************************************************/
package game.plugins.blocks.filters;

import game.core.Data;
import game.core.DatasetTemplate;
import game.core.Element;
import game.core.ElementTemplate;
import game.core.blocks.Filter;
import game.utils.Utils;

import com.ios.errorchecks.PropertyCheck;

public class ValueSelection extends Filter {
	
	@InName
	public String mask = "";
	
	public ValueSelection() {
		addErrorCheck(new PropertyCheck<String>("mask") {
			@Override protected String getError(String value) {
				return getRoot(ValueSelection.class).maskErrors();
			}
		});
	}
	
	private String maskErrors() {
		if (Utils.count(mask, '0') + Utils.count(mask, '1') != mask.length())
			return "can contain only 1s and 0s";
		if (datasetTemplate != null && datasetTemplate.sourceTemplate != null &&
				datasetTemplate.sourceTemplate.size() != mask.length())
			return "must contain extactly " +  datasetTemplate.sourceTemplate.size() + " characters";
		return null;
	}

	@Override
	public Data transform(Data input) {
		Data ret = new Data();
		
		for(int j = 0; j < input.size(); j++) {
			Element baseElement = input.get(j);
			int baseIndex = 0;
			Element retElement = new Element();
			for(char c: mask.toCharArray()) {
				if (c == '1') {
					retElement.add(baseElement.get(baseIndex));
				}
				baseIndex++;
			}
			ret.add(retElement);
		}
		
		return ret;
	}

	@Override
	protected void updateOutputTemplate() {
		ElementTemplate tpl = null;
		if (maskErrors() == null) {
			tpl = new ElementTemplate();
			int baseIndex = 0;
			for(char c: mask.toCharArray()) {
				if (c == '1') {
					if (datasetTemplate.sourceTemplate.size() > baseIndex)
						tpl.add(datasetTemplate.sourceTemplate.get(baseIndex));
				}
				baseIndex++;
			}
		}
		setContent("outputTemplate", tpl);
	}

	@Override
	public String compatibilityError(DatasetTemplate template) {
		return template.sourceTemplate != null ? null : "sourceTemplate is null";
	}

}
