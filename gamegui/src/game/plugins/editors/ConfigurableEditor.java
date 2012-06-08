package game.plugins.editors;

import game.configuration.Configurable;
import game.editorsystem.Editor;
import game.editorsystem.Option;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ConfigurableEditor extends Editor {
	
	private GridPane pane = new GridPane();
	
	public ConfigurableEditor() {
		AnchorPane.setTopAnchor(pane, 0.0);
		AnchorPane.setLeftAnchor(pane, 0.0);
		AnchorPane.setRightAnchor(pane, 0.0);
		AnchorPane.setBottomAnchor(pane, 0.0);
		//pane.setStyle("-fx-background-color:#ff0000;");
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
				Option option = new Option(content, optionName);
				Editor editor = option.getBestEditor(); editor.setModel(option);
				if (optionName.equals("name"))
					pane.addRow(0, new Label(optionName), editor.getView());
				else
					pane.addRow(count++, new Label(optionName), editor.getView());
				GridPane.setHgrow(editor.getView(), Priority.ALWAYS);
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

}
