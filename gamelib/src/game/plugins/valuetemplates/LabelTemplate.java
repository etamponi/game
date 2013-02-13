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
package game.plugins.valuetemplates;

import game.core.ValueTemplate;

import java.util.List;

import com.ios.IList;
import com.ios.errorchecks.NoRepetitionCheck;
import com.ios.errorchecks.SizeCheck;

public class LabelTemplate extends ValueTemplate<String> {
	
	@InName
	public IList<String> labels;

	public LabelTemplate() {
		setContent("labels", new IList<>(String.class));
		addErrorCheck("labels", new NoRepetitionCheck());
		addErrorCheck("labels", new SizeCheck(2));
	}
	
	public LabelTemplate(String... ls) {
		this();
		for(String l: ls)
			labels.add(l);
	}

	@Override
	public int getDescriptionLength() {
		return 1;
	}

	@Override
	public String loadValue(List<String> description) {
		return (String) description.get(0);
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof LabelTemplate ? ((LabelTemplate)other).labels.equals(this.labels) : false;
	}

}
