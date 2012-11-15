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

import com.ios.IList;
import com.ios.errorchecks.NoRepetitionCheck;
import com.ios.errorchecks.SizeCheck;

import game.core.DataTemplate;

public class LabelTemplate extends DataTemplate {
	
	public class LabelData extends Data<String> {
		@Override
		protected Class getElementType() {
			return String.class;
		}
	}
	
	public IList<String> labels;

	public LabelTemplate() {
		setContent("labels", new IList<>(String.class));
		addErrorCheck("labels", new NoRepetitionCheck());
		addErrorCheck("labels", new SizeCheck(2));
	}

	@Override
	public int getDescriptionLength() {
		return 1;
	}

	@Override
	public LabelData newData() {
		return new LabelData();
	}
	
}
