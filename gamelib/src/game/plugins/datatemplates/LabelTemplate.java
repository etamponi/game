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

import game.configuration.ConfigurableList;
import game.configuration.errorchecks.NoNullElementsCheck;
import game.configuration.errorchecks.NoRepetitionCheck;
import game.configuration.errorchecks.SizeCheck;

public class LabelTemplate extends AtomicTemplate {

	public ConfigurableList labels = new ConfigurableList(this, String.class);
	
	public LabelTemplate() {
		addOptionChecks("labels", new NoRepetitionCheck(), new SizeCheck(2), new NoNullElementsCheck());
	}
	
}
