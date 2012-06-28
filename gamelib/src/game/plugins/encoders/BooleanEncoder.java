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
import game.plugins.datatemplates.LabelTemplate;

public class BooleanEncoder extends Encoder<LabelTemplate> {

	@Override
	public boolean isCompatible(DataTemplate template) {
		return template instanceof LabelTemplate &&
				((LabelTemplate)template).labels.size() == 2;
	}

	@Override
	protected Encoding transform(Object inputData) {
		Encoding ret = new Encoding();
		
		double[] element = new double[1];
		if (inputData.equals(template.labels.get(0)))
			element[0] = 1;
		else
			element[0] = 0;
		ret.add(element);
		
		return ret;
	}

}
