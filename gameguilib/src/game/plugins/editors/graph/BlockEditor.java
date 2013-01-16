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
package game.plugins.editors.graph;

import game.core.Block;
import game.plugins.editors.IObjectEditor;

public class BlockEditor extends IObjectEditor {

	public BlockEditor() {
		setHiddenOptions("parents", "position", "outputTemplate");
		getUpdateTrigger().getSubPaths().add("trainingAlgorithm");
	}

	@Override
	public Class getBaseEditableClass() {
		return Block.class;
	}
	
	@Override
	public void updateView() {
		super.updateView();
		
		/* FIXME show training properties
		if (getModel() != null && getModel().getContent() != null) {
			Block content = (Block)getModel().getContent();
			int count = getSubEditorCount();
			for (String optionName: content.trainingAlgorithm.getManagedBlockOptions()) {
				PropertyEditor editor = addSubEditor(getSubEditorCount()+2, optionName);
				if (editor != null) {
					editor.setReadOnly(true);
				}
			}
			if (count > 0)
				getPane().addRow(count+1, new Label(""), new Label("Options managed by training algorithm (read-only):"));
		}
		*/
	}
	
}
