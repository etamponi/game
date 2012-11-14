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
package game.plugins.encoders;

import game.configuration.Listener;
import game.configuration.Property;
import game.core.DataTemplate;
import game.core.DataTemplate.Data;
import game.core.Encoding;
import game.core.blocks.Encoder;
import game.plugins.datatemplates.LabelTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public abstract class LabelEncoder extends Encoder<LabelTemplate> {
	
	public HashMap<String, RealVector> labelMapping = new HashMap<>();
	
	public LabelEncoder() {
		
		addListener(new Listener() {
			@Override
			public void action(Property changedPath) {
				updateMapping();
			}
			
			@Override
			public boolean isListeningOn(Property path) {
				return new Property(LabelEncoder.this, "template").isPrefix(path, false);
			}
			
			@Override
			public List<Property> getBoundProperties(Property prefixPath) {
				return new ArrayList<>();
			}
		});
		
	}
	
	protected abstract void updateLabelMapping();

	@Override
	public Encoding baseEncode(Data input) {
		Encoding ret = new Encoding(getFeatureNumber(), input.size());
		for (int j = 0; j < input.size(); j++) {
			Object element = input.get(j);
			RealVector enc;
			if (labelMapping.containsKey(element))
				enc = labelMapping.get(element).copy();
			else
				enc = new ArrayRealVector(getBaseFeatureNumber());
			ret.setElement(j, enc);
		}
		return ret;
	}
	
	@Override
	protected int getBaseFeatureNumber() {
		return labelMapping.values().iterator().next().getDimension();
	}
	
	private void updateMapping() {
		for (String label: template.labels) {
			if (label != null && !labelMapping.containsKey(label))
				labelMapping.put(label, new ArrayRealVector());
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
