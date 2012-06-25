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

import game.core.DataTemplate;
import game.core.Encoding;
import game.core.blocks.Encoder;
import game.plugins.datatemplates.LabelTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

public class LabelEncoder extends Encoder<LabelTemplate> {
	
	public HashMap<String, double[]> labelMapping = new HashMap<>();
	
	public LabelEncoder() {
		
		addObserver(new Observer() {
			
			@Override
			public void update(Observable observed, Object message) {
				if (message instanceof Change) {
					if (((Change)message).pathContains("template"))
						updateMapping((LabelTemplate)template);
				}
			}
		});
		
	}

	@Override
	protected Encoding transform(Object inputData) {
		Encoding ret = new Encoding();
		ret.add(labelMapping.get(inputData).clone());
		return ret;
	}
	
	private void updateMapping(LabelTemplate template) {
		this.template = template;
		
		if (template == null || template.labels == null)
			return;
		
		int i = 0; int n = template.labels.size();
		for (String label: template.labels.getList(String.class)) {
			if (label != null) {
				double[] enc = new double[n]; enc[i] = 1;
				labelMapping.put(label, enc);
			}
			i++;
		}
		for (String key: new HashSet<String>(labelMapping.keySet())) {
			if (!template.labels.contains(key))
				labelMapping.remove(key);
		}
	}

	@Override
	public boolean isCompatible(DataTemplate object) {
		return object instanceof LabelTemplate;
	}

}
