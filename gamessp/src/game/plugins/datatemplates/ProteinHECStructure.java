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


public class ProteinHECStructure extends SequenceTemplate {
	
	private static final String[] types = "H E C".split(" ");
	
	public ProteinHECStructure() {
		atom = new LabelTemplate();
		for (String type: types)
			atom.setOption("labels.add", type);
		
		setInternalOptions("atom");
	}
	
}
