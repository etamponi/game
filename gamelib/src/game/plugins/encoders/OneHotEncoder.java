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


public class OneHotEncoder extends LabelEncoder {

	public OneHotEncoder() {
		setInternalOptions("labelMapping");
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
