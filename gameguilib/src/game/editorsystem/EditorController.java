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

import com.ios.Property;

import javafx.fxml.Initializable;

public interface EditorController extends Initializable {
	
	public void setEditor(PropertyEditor editor);
	
	public PropertyEditor getEditor();

	public void setModel(Property model);

	public void updateView();

}
