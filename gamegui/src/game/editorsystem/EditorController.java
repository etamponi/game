package game.editorsystem;

import javafx.fxml.Initializable;

public interface EditorController extends Initializable {

	public void setModel(Option model);

	public void connectView();

	public void updateView();

	public void updateModel();

}
