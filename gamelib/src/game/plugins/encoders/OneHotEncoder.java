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


import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import com.ios.triggers.BoundProperties;


public class OneHotEncoder extends LabelEncoder {

	public OneHotEncoder() {
		addTrigger(new BoundProperties(this, "labelMapping"));
	}

	@Override
	protected void updateLabelMapping() {
		for(int i = 0; i < template.labels.size(); i++) {
			RealVector mapping = new ArrayRealVector(template.labels.size());
			mapping.setEntry(i, 1);
			labelMapping.put((String)template.labels.get(i), mapping); 
		}
	}

	@Override
	protected FeatureType getBaseFeatureType(int featureIndex) {
		return FeatureType.NOMINAL;
	}
	
}
