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
import game.plugins.datatemplates.PreEncodedPrimaryStructure;


public class PreEncodedProfileMatrix extends BaseSequenceEncoder {
	
	public PreEncodedProfileMatrix() {
		setOption("atomEncoder", new VectorEncoder());
		
		setInternalOptions("atomEncoder");
	}

	@Override
	public boolean isCompatible(DataTemplate template) {
		return template instanceof PreEncodedPrimaryStructure;
	}

}
