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


public class ProteinPrimaryStructure extends SequenceTemplate {

	private static final String[] aminoacids = "A R N D C E Q G H I L K M F P S T W Y V".split(" ");
	
	public ProteinPrimaryStructure() {
		atom = new LabelTemplate();
		for (String aminoacid: aminoacids)
			atom.setOption("labels.add", aminoacid);
		
		setInternalOptions("atom");
	}
	
}
