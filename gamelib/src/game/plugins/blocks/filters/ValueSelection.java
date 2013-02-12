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

import com.ios.ErrorCheck;
import com.ios.Property;
import com.ios.listeners.ExactPathListener;
import com.ios.triggers.SimpleTrigger;

public class ValueSelection extends Filter {
	
	public String mask = "";
	
	public ValueSelection() {
		addErrorCheck("mask", new ErrorCheck<String>() {
			private ValueSelection fs = ValueSelection.this;
			@Override public String getError(String value) {
				return fs.maskErrors();
			}
		});
		
		addTrigger(new SimpleTrigger(new ExactPathListener(new Property(this, "mask"))) {
			private ValueSelection fs = ValueSelection.this;
			@Override
			public void action(Property changedPath) {
				fs.updateOutputTemplate();
			}
		});
	}
	
	private String maskErrors() {
		if (Utils.count(mask, '0') + Utils.count(mask, '1') != mask.length())
			return "can contain only 1s and 0s";
		if (datasetTemplate != null && datasetTemplate.sourceTemplate.size() != mask.length())
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
		ElementTemplate tpl = new ElementTemplate();
		if (maskErrors() == null && datasetTemplate != null) {
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
	public boolean isCompatible(DatasetTemplate object) {
		return true;
	}

}
