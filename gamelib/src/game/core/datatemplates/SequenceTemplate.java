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
package game.core.datatemplates;

import game.core.DataTemplate;

public class SequenceTemplate extends DataTemplate {

	public AtomicTemplate atom;

	@Override
	protected Object getLocalOption(String optionName) {
		Object ret = super.getLocalOption(optionName);
		if (ret == null && atom != null) {
			ret = atom.getOption(optionName);
		}
		return ret;
	}

	@Override
	protected void setLocalOption(String optionName, Object content, Object setter) {
		if (getPublicOptionNames().contains(optionName))
			super.setLocalOption(optionName, content, setter);
		else if (atom != null)
			atom.setOption(optionName, content, notify, setter);
	}
	
}
