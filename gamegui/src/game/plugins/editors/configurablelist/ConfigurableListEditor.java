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
package game.plugins.editors.configurablelist;

import game.configuration.ConfigurableList;
import game.editorsystem.ControlledEditor;

public class ConfigurableListEditor extends ControlledEditor {

	@Override
	public Class getBaseEditableClass() {
		return ConfigurableList.class;
	}

	@Override
	public boolean isInline() {
		return false;
	}

}
