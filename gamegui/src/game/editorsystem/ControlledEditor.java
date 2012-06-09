package game.editorsystem;

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
	public void updateView() {
		controller.updateView();
	}

	@Override
	public void updateModel() {
		controller.updateModel();
	}
	
	protected String getFXML() {
		return getClass().getSimpleName() + "View" + ".fxml";
	}

}
