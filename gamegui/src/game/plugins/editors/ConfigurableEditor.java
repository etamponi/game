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
package game.plugins.editors;

import game.configuration.Configurable;
import game.editorsystem.Editor;
import game.editorsystem.Option;
import game.utils.Utils;

import java.util.HashSet;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ConfigurableEditor extends Editor {
	
	private GridPane pane = new GridPane();
	
	private HashSet<String> hiddenOptions = new HashSet<>();
	
	public ConfigurableEditor() {
		AnchorPane.setTopAnchor(pane, 0.0);
		AnchorPane.setLeftAnchor(pane, 0.0);
		AnchorPane.setRightAnchor(pane, 0.0);
		AnchorPane.setBottomAnchor(pane, 0.0);
	}

	@Override
	public Node getView() {
		return pane;
	}

	@Override
	public void connectView() {
		pane.getChildren().clear();
		int count = 1;
		if (getModel() != null && getModel().getContent() != null) {
			Configurable content = getModel().getContent();
			for (String optionName: content.getUnboundOptionNames()) {
//			for (String optionName: content.getOptionNames()) {
				if (hiddenOptions.contains(optionName))
					continue;
				
				Option option = new Option(content, optionName);
				Label label = new Label(optionName+": ");
				Editor editor = option.getBestEditor();
				try {
					if (option.getContent() == null && Utils.isConcrete(option.getType()))
						option.setContent(option.getType().newInstance());
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
				editor.setModel(option);
//				if (option.isBound())
//					editor.getView().setDisable(true);
				pane.addRow(optionName.equals("name") ? 0 : count++, label, editor.getView());
				GridPane.setValignment(label, VPos.TOP);
				GridPane.setHalignment(label, HPos.RIGHT);
				GridPane.setMargin(label, new Insets(5, 2, 2, 2));
				GridPane.setHgrow(editor.getView(), Priority.ALWAYS);
				GridPane.setMargin(editor.getView(), new Insets(2, 2, 2, 2));
			}
		}
	}

	@Override
	public void updateView() {
		// Everything is provided by the sub editors.
	}

	@Override
	public void updateModel() {
		// Everything is provided by the sub editors.
	}

	@Override
	public Class getBaseEditableClass() {
		return Configurable.class;
	}
	
	protected void addHiddenOption(String optionName) {
		hiddenOptions.add(optionName);
	}

}
