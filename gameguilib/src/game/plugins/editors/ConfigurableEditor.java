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
import game.editorsystem.Option;
import game.editorsystem.OptionEditor;
import game.utils.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ConfigurableEditor extends OptionEditor {
	
	private GridPane pane = new GridPane();
	private ListView<String> errorList = new ListView<>();
	
	private HashSet<String> hiddenOptions = new HashSet<>();
	private HashMap<String, Class<? extends OptionEditor>> specificEditors = new HashMap<>();
	
	private List<OptionEditor> subEditors = new LinkedList<>();
	
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
	public void updateView() {
		pane.getChildren().clear();
		for (OptionEditor editor: subEditors)
			editor.disconnect();
		subEditors.clear();
		
		int count = 2;
		if (getModel() != null && getModel().getContent() != null) {
			Configurable content = getModel().getContent();
			
			OptionEditor serializationEditor = new SerializationEditor();
			serializationEditor.connect(getModel());
			subEditors.add(serializationEditor);
			
			pane.add(serializationEditor.getView(), 0, 0, 2, 1);
			for (String optionName: content.getUnboundOptionNames()) {
				if (hiddenOptions.contains(optionName))
					continue;
				
				Option option = new Option(content, optionName);
				Label label = new Label(optionName+": ");
				
				OptionEditor editor = prepareEditor(option);
				if (editor == null)
					continue;
				pane.addRow(optionName.equals("name") ? 1 : count++, label, editor.getView());
				applyRowLayout(label, editor.getView(), editor.isInline());
			}
			
			errorList.getItems().clear();
			errorList.getItems().addAll(content.getConfigurationErrors());
			errorList.setPrefHeight(75);
			errorList.setPrefWidth(75);
			GridPane.setVgrow(errorList, Priority.SOMETIMES);
			Label label = new Label("errors:");
			pane.addRow(count, label, errorList);
			applyRowLayout(label, errorList, true);
		}
	}
	
	@Override
	public void disconnect() {
		for(OptionEditor editor: subEditors)
			editor.disconnect();
		super.disconnect();
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
	public Class getBaseEditableClass() {
		return Configurable.class;
	}
	
	@Override
	public void update(Observable observed, Object m) {
		super.update(observed, m);
		if (m instanceof Change) {
			errorList.getItems().clear();
			if (getModel() != null && getModel().getContent() != null) {
				errorList.getItems().addAll(((Configurable)getModel().getContent()).getConfigurationErrors());
			}
		}
	}

	protected void setHiddenOptions(String... optionNames) {
		for (String optionName: optionNames)
			hiddenOptions.add(optionName);
	}
	
	protected void setSpecificEditor(String optionName, Class<? extends OptionEditor> editor) {
		specificEditors.put(optionName, editor);
	}
	
	private OptionEditor prepareEditor(Option option) {
		OptionEditor editor = null;
		try {
			if (specificEditors.containsKey(option.getOptionName()))
				editor = specificEditors.get(option.getOptionName()).newInstance();
			else
				editor = option.getBestEditor(false);
			if (editor == null)
				return null;
			if (option.getContent() == null && Utils.isConcrete(option.getType()))
				option.setContent(option.getType().newInstance());
			editor.connect(option);
			subEditors.add(editor);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return editor;
	}

}
