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
package game.plugins.datatemplates;

import org.apache.commons.math3.linear.RealVector;

import com.ios.errorchecks.PositivenessCheck;

import game.core.DataTemplate;

public class VectorTemplate extends DataTemplate {
	
	public class VectorData extends Data<RealVector> {

		@Override
		protected Class getElementType() {
			return RealVector.class;
		}
		
	}

	public int dimension;
	
	public VectorTemplate() {
		addErrorCheck("dimension", new PositivenessCheck(false));
	}

	@Override
	public int getDescriptionLength() {
		return dimension;
	}

	@Override
	public VectorData newData() {
		return new VectorData();
	}
	
}
