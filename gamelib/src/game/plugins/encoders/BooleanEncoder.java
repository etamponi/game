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

public class BooleanEncoder extends LabelEncoder {
	
	public static final int POSITIVEINDEX = 0;

	public BooleanEncoder() {
		setPrivateOptions("labelMapping");
	}

	@Override
	public boolean isCompatible(DataTemplate template) {
		return template instanceof LabelTemplate &&
				((LabelTemplate)template).labels.size() == 2;
	}

	@Override
	protected void updateLabelMapping() {
		labelMapping.put(positiveLabel(), new double[]{1});
		labelMapping.put(negativeLabel(), new double[]{0});
	}
	
	public String positiveLabel() {
		return (String)template.labels.get(0);
	}
	
	public String negativeLabel() {
		return (String)template.labels.get(1);
	}

}
