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

import game.configuration.Change;
import game.core.Block;
import game.editorsystem.Editor;
import game.plugins.editors.ConfigurableEditor;

import java.util.Observable;

import javafx.scene.control.Label;

public class BlockEditor extends ConfigurableEditor {

	public BlockEditor() {
		setHiddenOptions("parents", "position");
	}

	@Override
	public Class getBaseEditableClass() {
		return Block.class;
	}
	
	@Override
	public void updateView() {
		super.updateView();
		
		if (getModel() != null && getModel().getContent() != null) {
			Block content = (Block)getModel().getContent();
			int count = getSubEditorCount();
			for (String optionName: content.trainingAlgorithm.getManagedBlockOptions()) {
				Editor editor = addSubEditor(getSubEditorCount()+2, optionName);
				if (editor != null) {
					editor.setReadOnly(true);
				}
			}
			if (count > 0)
				getPane().addRow(count+1, new Label(""), new Label("Options managed by training algorithm (read-only):"));
		}
		
	}

	@Override
	public void update(Observable observed, Object message) {
		super.update(observed, message);
		if (message instanceof Change) {
			if (((Change)message).getPath().matches("(.)+\\.trainingAlgorithm"))
				updateView();
		}
	}
	
}
