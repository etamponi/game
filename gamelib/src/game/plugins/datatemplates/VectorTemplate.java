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
package game.plugins.datatemplates;


import game.core.ValueTemplate;

import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import com.ios.errorchecks.PositivenessCheck;

public class VectorTemplate extends ValueTemplate<RealVector> {
	
	public int dimension;
	
	public VectorTemplate() {
		addErrorCheck("dimension", new PositivenessCheck(false));
	}
	
	public VectorTemplate(int dimension) {
		this();
		this.dimension = dimension;
	}

	@Override
	public int getDescriptionLength() {
		return dimension;
	}

	@Override
	public RealVector loadValue(List<String> description) {
		RealVector ret = new ArrayRealVector(dimension);
		int i = 0;
		for(String s: description)
			ret.setEntry(i++, Double.parseDouble(s));
		return ret;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof VectorTemplate ? ((VectorTemplate)other).dimension == this.dimension : false;
	}
	
}
