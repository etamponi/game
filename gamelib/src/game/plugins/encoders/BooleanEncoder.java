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
import game.plugins.datatemplates.LabelTemplate;

public class BooleanEncoder extends ProbabilityEncoder {
	
	public BooleanEncoder() {
		setPrivateOptions("labelMapping");
	}

	@Override
	public boolean isCompatible(DataTemplate template) {
		return template instanceof LabelTemplate &&
				((LabelTemplate)template).labels.size() == 2;
	}

	@Override
	protected void updateSingleMapping() {
		labelMapping.put(getPositiveLabel(), new double[]{1});
		labelMapping.put(getNegativeLabel(), new double[]{0});
	}
	
	public String getPositiveLabel() {
		return (String)template.labels.get(0);
	}
	
	public String getNegativeLabel() {
		return (String)template.labels.get(1);
	}

}
