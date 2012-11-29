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

import game.editorsystem.PropertyEditor;
import game.utils.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import com.ios.IObject;
import com.ios.Property;
import com.ios.listeners.SubPathListener;
import com.ios.triggers.SimpleTrigger;

public class IObjectEditor extends PropertyEditor {
	
	private GridPane pane = new GridPane();
	private TreeView<String> errorList = new TreeView<>();
	
	private HashSet<String> hiddenOptions = new HashSet<>();
	private HashMap<String, Class<? extends PropertyEditor>> specificEditors = new HashMap<>();
	
	private List<PropertyEditor> subEditors = new LinkedList<>();
	
	public IObjectEditor() {
		addTrigger(new SimpleTrigger(new SubPathListener(getProperty("root"))) {
			@Override
			public void action(Property changedPath) {
				updateErrorList();
			}
		});
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
		
		pane.setPrefWidth(500);
	}
	
	protected GridPane getPane() {
		return pane;
	}
	
	protected int getSubEditorCount() {
		return subEditors.size();
	}

	@Override
	public Node getView() {
		return pane;
	}

	@Override
	public void updateView() {
		pane.getChildren().clear();
		for (PropertyEditor editor: subEditors)
			editor.detach();
		subEditors.clear();
		
		int count = 2;
		if (getModel() != null && getModel().getContent() != null) {
			IObject content = getModel().getContent();
			
			PropertyEditor serializationEditor = new SerializationEditor();
			serializationEditor.setReadOnly(isReadOnly());
			serializationEditor.connect(getModel());
			subEditors.add(serializationEditor);
			pane.add(serializationEditor.getView(), 0, 0, 2, 1);
			for (Property property: content.getUnboundProperties()) {
				if (hiddenOptions.contains(property.getPath()))
					continue;
				
				if (property.getPath().equals("name"))
					addSubEditor(1, property);
				else if (addSubEditor(count, property) != null)
					count++;
			}
			
			if (!isReadOnly()) {
				updateErrorList();
				Label label = new Label("errors:");
				pane.addRow(count, label, errorList);
				applyRowLayout(label, errorList, true);
			}
		}
	}
	
	private void updateErrorList() {
		if (getModel() == null)
			return;
		
		IObject content = getModel().getContent();
		if (content == null)
			return;
	
		if (errorList.getRoot() != null)
			errorList.getRoot().getChildren().clear();
		errorList.setRoot(new TreeItem<String>("Errors"));
		errorList.getRoot().setExpanded(true);
		Map<Property, List<String>> errors = content.getErrors();
		for(Property key: errors.keySet()) {
			TreeItem<String> item = new TreeItem<>(key.getPath());
			errorList.getRoot().getChildren().add(item);
			for(String error: errors.get(key))
				item.getChildren().add(new TreeItem<String>(error));
		}
		errorList.setPrefHeight(75);
		errorList.setPrefWidth(75);
		GridPane.setVgrow(errorList, Priority.SOMETIMES);
	}
	
	protected PropertyEditor addSubEditor(int row, Property property) {
		Label label = new Label(property.getPath()+": ");
		
		PropertyEditor editor = prepareEditor(property);
		if (editor == null)
			return null;
		pane.addRow(row, label, editor.getView());
		applyRowLayout(label, editor.getView(), editor.isInline());
		return editor;
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
		return IObject.class;
	}
	
	protected void setHiddenOptions(String... optionNames) {
		for (String optionName: optionNames)
			hiddenOptions.add(optionName);
	}
	
	protected void setSpecificEditor(String optionName, Class<? extends PropertyEditor> editor) {
		specificEditors.put(optionName, editor);
	}
	
	private PropertyEditor prepareEditor(Property property) {
		PropertyEditor editor = null;
		try {
			if (specificEditors.containsKey(property.getPath()))
				editor = specificEditors.get(property.getPath()).newInstance();
			else
				editor = PropertyEditor.getBestEditor(property.getContentType(false));
			if (editor == null)
				return null;
			if (property.getContent() == null && Utils.isConcrete(property.getContentType(true)))
				property.setContent(property.getContentType().newInstance());
			editor.setReadOnly(isReadOnly());
			editor.connect(property);
			subEditors.add(editor);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return editor;
	}

	@Override
	public void detach() {
		super.detach();
		for (PropertyEditor editor: subEditors)
			editor.detach();
	}
	
	

}
