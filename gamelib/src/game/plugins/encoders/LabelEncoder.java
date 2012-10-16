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

import game.configuration.Change;
import game.core.DataTemplate;
import game.core.Encoding;
import game.core.blocks.Encoder;
import game.plugins.datatemplates.LabelTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public abstract class LabelEncoder extends Encoder<LabelTemplate> {
	
	public HashMap<String, double[]> labelMapping = new HashMap<>();
	
	public LabelEncoder() {
		
		addObserver(new Observer() {
			@Override
			public void update(Observable observed, Object message) {
				if (message instanceof Change) {
					if (((Change)message).getPath().startsWith("template"))
						updateMapping(template);
				}
			}
		});
		
	}
	
	protected abstract void updateLabelMapping();

	@Override
	public Encoding baseEncode(List input) {
		Encoding ret = new Encoding();
		for (Object element: input) {
			double[] enc;
			if (labelMapping.containsKey(element))
				enc = labelMapping.get(element).clone();
			else
				enc = new double[getBaseFeatureNumber()];
			ret.add(enc);
		}
		return ret;
	}
	
	@Override
	protected int getBaseFeatureNumber() {
		return labelMapping.values().iterator().next().length;
	}
	
	private void updateMapping(DataTemplate tpl) {
		if (tpl == null || !isCompatible(tpl) || tpl.getOption("labels") == null)
			return;
		
		this.template = (LabelTemplate)tpl;
		
		for (String label: template.labels.getList(String.class)) {
			if (label != null && !labelMapping.containsKey(label))
				labelMapping.put(label, new double[]{});
		}
		for (String key: new HashSet<String>(labelMapping.keySet())) {
			if (!template.labels.contains(key))
				labelMapping.remove(key);
		}
		
		updateLabelMapping();
	}

	@Override
	public boolean isCompatible(DataTemplate object) {
		return object instanceof LabelTemplate;
	}

}
