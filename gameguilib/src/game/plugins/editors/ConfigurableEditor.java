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

import game.configuration.Change;
import game.configuration.Configurable;
import game.editorsystem.Editor;
import game.editorsystem.Option;
import game.utils.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ConfigurableEditor extends Editor {
	
	public static class SerializationEditor extends Editor {
		
		private final Image SAVECONFIGURATION = new Image(getClass().getResourceAsStream("saveConfiguration.png"));
		private final Image LOADCONFIGURATION = new Image(getClass().getResourceAsStream("loadConfiguration.png"));
		
		private ToolBar line = new ToolBar();

		@Override
		public Node getView() {
			return line;
		}

		@Override
		public boolean isInline() {
			return true;
		}

		@Override
		public void connectView() {
			if (getModel() != null) {
				line.getItems().clear();
				line.getItems().addAll(makeSaveAndLoadConfiguration("SAVE"),
									   makeSaveAndLoadConfiguration("LOAD"));
			}
		}

		private Button makeSaveAndLoadConfiguration(final String what) {
			Button ret = new Button();
			ImageView graphic = new ImageView();
			if (what.equals("SAVE"))
				graphic.setImage(SAVECONFIGURATION);
			else
				graphic.setImage(LOADCONFIGURATION);
			ret.setGraphic(graphic);
			ret.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			
			final Configurable content = getModel().getContent();
			ret.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					FileChooser chooser = new FileChooser();
					chooser.setInitialDirectory(new File(System.getProperty("user.home")));
					chooser.getExtensionFilters().add(new ExtensionFilter("GAME configuration file", "*.config.xml"));
					
					if (what.equals("SAVE")) {
						chooser.setTitle("Save object configuration");
						File out = chooser.showSaveDialog(line.getScene().getWindow());
						if (out != null) {
							if (!out.getName().endsWith(".config.xml"))
								content.saveConfiguration(out.getPath() + ".config.xml");
							else
								content.saveConfiguration(out.getPath());
						}
					} else {
						// TODO Check if the loaded object is compatible with the current object.
						// TODO Check if the loaded object is compatible with the binding.
						chooser.setTitle("Load object configuration");
						File out = chooser.showOpenDialog(line.getScene().getWindow());
						if (out != null)
							content.loadConfiguration(out.getPath());
					}
					event.consume();
				}
			});
			
			return ret;
		}
		
		@Override
		public void updateView(Change change) {
			// Nothing to update
		}

		@Override
		public Class getBaseEditableClass() {
			return getClass();
		}
		
	}
	
	private GridPane pane = new GridPane();
	private ListView<String> errorList = new ListView<>();
	
	private HashSet<String> hiddenOptions = new HashSet<>();
	private HashMap<String, Class<? extends Editor>> specificEditors = new HashMap<>();
	
	public ConfigurableEditor() {
		AnchorPane.setTopAnchor(pane, 0.0);
		AnchorPane.setLeftAnchor(pane, 0.0);
		AnchorPane.setRightAnchor(pane, 0.0);
		AnchorPane.setBottomAnchor(pane, 0.0);
		
		ColumnConstraints c0 = new ColumnConstraints();
		c0.setMinWidth(120);
		c0.setHgrow(Priority.SOMETIMES);
		ColumnConstraints c1 = new ColumnConstraints();
		c1.setHgrow(Priority.ALWAYS);
		pane.getColumnConstraints().addAll(c0, c1);
		
		pane.setMinWidth(500);
	}

	@Override
	public Node getView() {
		return pane;
	}

	@Override
	public void connectView() {
		pane.getChildren().clear();
		int count = 2;
		if (getModel() != null && getModel().getContent() != null) {
			Configurable content = getModel().getContent();
			
			Editor serializationEditor = new SerializationEditor();
			serializationEditor.setModel(getModel());
			pane.add(serializationEditor.getView(), 0, 0, 2, 1);
			for (String optionName: content.getUnboundOptionNames()) {
				if (hiddenOptions.contains(optionName))
					continue;
				
				Option option = new Option(content, optionName);
				Label label = new Label(optionName+": ");
				
				Editor editor = prepareEditor(option);
				if (editor == null)
					continue;
				pane.addRow(optionName.equals("name") ? 1 : count++, label, editor.getView());
				applyRowLayout(label, editor.getView(), editor.isInline());
			}
			
			errorList.getItems().clear();
			errorList.getItems().addAll(content.getConfigurationErrors());
			errorList.setPrefHeight(75);
			errorList.setPrefWidth(75);
			Label label = new Label("errors:");
			pane.addRow(count, label, errorList);
			applyRowLayout(label, errorList, true);
		}
	}
	
	private void applyRowLayout(Node label, Node view, boolean inline) {
		GridPane.setValignment(label, VPos.TOP);
		GridPane.setHalignment(label, HPos.RIGHT);
		GridPane.setMargin(label, new Insets(5, 2, 2, 2));
		GridPane.setHgrow(view, Priority.ALWAYS);
		GridPane.setMargin(view, new Insets(2, 2, 2, 2));
		if (!inline) {
			view.setStyle("-fx-padding: 0px 0px 5px 0px; -fx-border-color: -fx-color");
		}
	}

	@Override
	public boolean isInline() {
		return false;
	}

	@Override
	public void updateView(Change change) {
		errorList.getItems().clear();
		if (getModel() != null && getModel().getContent() != null)
			errorList.getItems().addAll(((Configurable)getModel().getContent()).getConfigurationErrors());
	}

	@Override
	public Class getBaseEditableClass() {
		return Configurable.class;
	}
	
	protected void setHiddenOptions(String... optionNames) {
		for (String optionName: optionNames)
			hiddenOptions.add(optionName);
	}
	
	protected void setSpecificEditor(String optionName, Class<? extends Editor> editor) {
		specificEditors.put(optionName, editor);
	}
	
	private Editor prepareEditor(Option option) {
		Editor editor = null;
		try {
			if (specificEditors.containsKey(option.getOptionName()))
				editor = specificEditors.get(option.getOptionName()).newInstance();
			else
				editor = option.getBestEditor();
			if (editor == null)
				return null;
			if (option.getContent() == null && Utils.isConcrete(option.getType()))
				option.setContent(option.getType().newInstance());
			editor.setModel(option);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return editor;
	}

}
