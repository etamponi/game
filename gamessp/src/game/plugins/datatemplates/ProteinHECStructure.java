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


public class ProteinHECStructure extends ProteinStructureTemplate {
	
	private static final String[] types = "H E C".split(" ");
	
	public ProteinHECStructure() {
		for (String type: types)
			labels.add(type);
	}
	
}
