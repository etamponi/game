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
package game.plugins.editors;

import game.editorsystem.Editor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public abstract class TextFieldEditor extends Editor {
	
	protected TextField textField = new TextField();
	
	public TextFieldEditor() {
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(
					ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				if (getModel() != null) {
					Object content = parseText();
					if (content != null)
						setModelContent(content);
				}
			}
		});
	}
	
	protected abstract Object parseText();

	@Override
	public boolean isInline() {
		return true;
	}

	@Override
	public Node getView() {
		return textField;
	}

	@Override
	public void updateView() {
		if (getModel() != null && getModel().getContent() != null)
			textField.setText(getModel().getContent().toString());
		else
			textField.setText("");
		
		textField.setEditable(!isReadOnly());
	}

}
