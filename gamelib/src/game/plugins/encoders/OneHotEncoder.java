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

import game.core.Encoding;

public class OneHotEncoder extends LabelEncoder {

	public OneHotEncoder() {
		setInternalOptions("labelMapping");
	}

	@Override
	protected Encoding transform(Object inputData) {
		Encoding ret = new Encoding();
		
		double[] element = new double[template.labels.size()];
		int index = template.labels.indexOf(inputData);
		if (index >= 0)
			element[index] = 1;
		ret.add(element);
		
		return ret;
	}

	@Override
	protected void updateSingleMapping() {
		for(int i = 0; i < template.labels.size(); i++) {
			double[] mapping = new double[template.labels.size()];
			mapping[i] = 1;
			labelMapping.put((String)template.labels.get(i), mapping); 
		}
	}
	
}