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
package game.editorsystem;

import javafx.fxml.Initializable;

public interface EditorController extends Initializable {
	
	public void setEditor(Editor editor);
	public Editor getEditor();

	public void setModel(Option model);

	public void updateView();

}
