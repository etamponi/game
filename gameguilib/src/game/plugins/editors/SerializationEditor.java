package game.plugins.editors;

import game.configuration.Configurable;
import game.editorsystem.OptionEditor;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class SerializationEditor extends OptionEditor {
	
	private final Image SAVECONFIGURATION = new Image(getClass().getResourceAsStream("saveConfiguration.png"));
	private final Image LOADCONFIGURATION = new Image(getClass().getResourceAsStream("loadConfiguration.png"));
	
	private ToolBar line = new ToolBar();
	
	public SerializationEditor() {
		line.getItems().addAll(new Button(), new Button());
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
		if (getModel() != null) {
			line.getItems().set(0, makeSaveAndLoadConfiguration("SAVE"));
			line.getItems().set(1, makeSaveAndLoadConfiguration("LOAD"));
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
				chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
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
					// TODO Check if the loaded object is compatible with the constraints.
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
	public Class getBaseEditableClass() {
		return getClass();
	}
	
}