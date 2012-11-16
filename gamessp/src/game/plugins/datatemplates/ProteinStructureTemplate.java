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

import com.ios.triggers.BoundProperties;



public abstract class ProteinStructureTemplate extends LabelTemplate {
	
	public ProteinStructureTemplate() {
		sequence = true;
		addTrigger(new BoundProperties(this, "labels", "sequence"));
	}
	
	@Override
	protected String toString(Data data) {
		StringBuilder builder = new StringBuilder();
		for (Object o: data)
			builder.append(o);
		return builder.toString();
	}

}
