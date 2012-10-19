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

import org.apache.commons.math3.linear.ArrayRealVector;

public class IntegerEncoder extends LabelEncoder {
	
	public IntegerEncoder() {
		setAsInternalOptions("labelMapping");
	}

	@Override
	protected void updateLabelMapping() {
		for(int i = 0; i < template.labels.size(); i++)
			labelMapping.put((String)template.labels.get(i), new ArrayRealVector(new double[]{i+1}));
	}

}
