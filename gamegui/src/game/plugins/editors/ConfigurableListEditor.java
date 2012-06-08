package game.plugins.editors;

import game.configuration.ConfigurableList;
import game.editorsystem.Editor;
import game.editorsystem.Option;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Parent;

public class ConfigurableListEditor extends Editor {
	
	private ConfigurableListEditorController controller;
	
	public ConfigurableListEditor() {
		try {
			URL location = getClass().getResource("ConfigurableListEditorView.fxml");
	
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(location);
			fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

			Parent root = (Parent)fxmlLoader.load(location.openStream());
			controller = fxmlLoader.getController();
			controller.setView(root);
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
		return controller.getView();
	}

	@Override
	public void connectView() {
		controller.connectView();
	}

	@Override
	public void updateView() {
		controller.updateView();
	}

	@Override
	public void updateModel() {
		controller.updateModel();
	}

	@Override
	public Class getBaseEditableClass() {
		return ConfigurableList.class;
	}

}
