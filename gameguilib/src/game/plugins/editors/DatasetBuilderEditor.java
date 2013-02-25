package game.plugins.editors;

import game.core.DatasetBuilder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class DatasetBuilderEditor extends IObjectEditor {
	
	private Button prepareButton;
	
	public DatasetBuilderEditor() {
		prepareButton = new Button("Prepare");
		prepareButton.setPrefWidth(75);
		prepareButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (getModel() != null) {
					DatasetBuilder builder = getModel().getContent();
					if (builder != null)
						builder.prepare();
				}
			}
		});
	}

	@Override
	public void updateView() {
		super.updateView();
		
		getPane().add(prepareButton, 1, getSubEditorCount()+2);
	}

	@Override
	public Class getBaseEditableClass() {
		return DatasetBuilder.class;
	}
	
}
