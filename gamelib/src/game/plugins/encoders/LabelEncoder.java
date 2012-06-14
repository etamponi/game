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
package game.plugins.encoders;

import game.configuration.ErrorCheck;
import game.core.DataTemplate;
import game.core.Encoding;
import game.core.blocks.Encoder;
import game.plugins.datatemplates.LabelTemplate;

import java.util.Map;
import java.util.TreeMap;

public class LabelEncoder extends Encoder<LabelTemplate> {
	
	private class ContainsAllKeysCheck implements ErrorCheck<Map> {

		@Override
		public String getError(Map value) {
			if (!value.keySet().containsAll(template.labels))
				return "mapping is not valid: does not contain all labels";
			else
				return null;
		}
		
	}
	
	public TreeMap<String, double[]> labelMapping = new TreeMap<>();
	
	public LabelEncoder() {
		
		addOptionChecks("labelMapping", new ContainsAllKeysCheck());
		
	}

	@Override
	protected Encoding transform(Object inputData) {
		Encoding ret = new Encoding();
		ret.add(labelMapping.get(inputData).clone());
		return ret;
	}
	
	public void setTemplate(LabelTemplate template) {
		this.template = template;
		
		int i = 0; int n = template.labels.size();
		for (String label: template.labels.getList(String.class)) {
			double[] enc = new double[n]; enc[i] = 1;
			labelMapping.put(label, enc);
			i++;
		}
	}

	@Override
	public boolean isCompatible(DataTemplate object) {
		return object instanceof LabelTemplate;
	}

}
