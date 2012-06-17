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
package game.editorsystem;

import game.configuration.Configurable.Change;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

public abstract class ControlledEditor extends Editor {
	
	private Node root;
	
	private EditorController controller;
	
	public ControlledEditor() {
		try {
			URL location = getClass().getResource(getFXML());
	
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(location);
			fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

			root = (Parent)fxmlLoader.load(location.openStream());
			controller = fxmlLoader.getController();

			AnchorPane.setTopAnchor(root, 0.0);
			AnchorPane.setLeftAnchor(root, 0.0);
			AnchorPane.setRightAnchor(root, 0.0);
			AnchorPane.setBottomAnchor(root, 0.0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setModel(Option model) {
		controller.setModel(model);
		super.setModel(model);
	}

	@Override
	public Node getView() {
		return root;
	}

	@Override
	public void connectView() {
		controller.connectView();
	}

	@Override
	public void updateView(Change change) {
		controller.updateView(change);
	}
	
	protected String getFXML() {
		return getClass().getSimpleName() + "View" + ".fxml";
	}

}
