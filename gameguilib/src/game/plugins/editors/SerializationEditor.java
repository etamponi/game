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

import java.io.File;

import com.ios.IObject;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SerializationEditor extends PropertyEditor {
	
	private final Image SAVECONFIGURATION = new Image(getClass().getResourceAsStream("saveConfiguration.png"));
	private final Image LOADCONFIGURATION = new Image(getClass().getResourceAsStream("loadConfiguration.png"));
	
	private ToolBar line = new ToolBar();
	
	public SerializationEditor() {
		line.setPrefWidth(200);
		line.getItems().addAll(new Button(), new Button());
		line.getItems().set(0, makeSaveAndLoadConfiguration("SAVE"));
		line.getItems().set(1, makeSaveAndLoadConfiguration("LOAD"));
	}

	@Override
	public Node getView() {
		return line;
	}

	@Override
	public boolean isInline() {
		return true;
	}

	@Override
	public void updateView() {
		line.getItems().get(1).setDisable(isReadOnly());
	}
	
	private void messageDialog(String title, String message) {
		final Stage stage = new Stage();
		stage.setTitle(title);
		stage.initModality(Modality.APPLICATION_MODAL);
		VBox container = new VBox();
		Button ok = new Button("Ok");
		ok.setPrefWidth(70);
		ok.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stage.close();
			}
		});
		container.setAlignment(Pos.CENTER);
		container.setSpacing(15);
		container.setPadding(new Insets(15));
		container.getChildren().addAll(new Text(message), ok);
		stage.setScene(new Scene(container));
		stage.setResizable(false);
		stage.show();
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
		
		ret.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				IObject content = getModel().getContent();
				FileChooser chooser = new FileChooser();
				chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
				chooser.getExtensionFilters().add(new ExtensionFilter("GAME configuration file", "*.bin"));
				
				if (what.equals("SAVE")) {
					chooser.setTitle("Save object configuration");
					File out = chooser.showSaveDialog(line.getScene().getWindow());
					if (out != null) {
						if (!out.getName().endsWith(".bin"))
							content.write(new File(out.getPath() + ".bin"));
						else
							content.write(out);
					}
				} else {
					chooser.setTitle("Load object configuration");
					File out = chooser.showOpenDialog(line.getScene().getWindow());
					if (out != null) {
						IObject loaded = IObject.load(out);
						loaded.startEdit(content.getLinkFromEditor());
						if (!loaded.getClass().equals(content.getClass())) {
							messageDialog("Cannot load object configuration", "Object could not be loaded. Check for type and constraints.");
						} else {
							updateModel(loaded);
						}
					}
				}
				event.consume();
			}
		});
		
		return ret;
	}

	@Override
	public Class getBaseEditableClass() {
		return getClass();
	}
	
}
