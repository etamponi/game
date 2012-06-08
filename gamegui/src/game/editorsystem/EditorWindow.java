package game.editorsystem;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditorWindow extends Stage {
	
	public EditorWindow(Editor editor) {
		initModality(Modality.APPLICATION_MODAL);
		
		AnchorPane root = new AnchorPane();
		root.getChildren().add(editor.getView());
		
		setScene(new Scene(root));
	}

}
