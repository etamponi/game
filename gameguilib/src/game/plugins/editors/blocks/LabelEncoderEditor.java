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
package game.plugins.editors.blocks;

import game.plugins.encoders.LabelEncoder;

public class LabelEncoderEditor extends BlockEditor {

	public LabelEncoderEditor() {
		setSpecificEditor("labelMapping", LabelMappingEditor.class);
	}

	@Override
	public Class getBaseEditableClass() {
		return LabelEncoder.class;
	}
	
}
