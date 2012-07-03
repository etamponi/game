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
import game.plugins.datatemplates.VectorTemplate;

public class VectorEncoder extends Encoder<VectorTemplate> {

	@Override
	public Encoding transform(Object inputData) {
		Encoding ret = new Encoding();
		ret.add((double[])inputData);
		return ret;
	}

	@Override
	public boolean isCompatible(DataTemplate object) {
		return object instanceof VectorTemplate;
	}

}
