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
package game.core;

import game.configuration.Configurable;
import game.configuration.ConfigurableList;
import game.plugins.constraints.CompatibleWith;

public class TemplateConstrainedList extends ConfigurableList {
	
	public Object constraint;
	
	public TemplateConstrainedList() {
		// DO NOT NEVER EVER USE (NEVER!) Necessary for ConfigurableConverter
		
		setOptionBinding("constraint", "*.template");
		setOptionConstraint("*", new CompatibleWith(this, "constraint"));
	}
	
	public TemplateConstrainedList(Configurable owner, Class content) {
		super(owner, content);
		
		setOptionBinding("constraint", "*.template");
		setOptionConstraint("*", new CompatibleWith(this, "constraint"));
	}
	
}
