package game.plugins.editors;

import game.configuration.ConfigurableList;
import game.editorsystem.Editor;
import game.editorsystem.EditorWindow;
import game.editorsystem.Option;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListView;

public class ConfigurableListEditorController implements Initializable {
	
	private Parent root;
	private ConfigurableList list;
	
	@FXML
	private ListView<Option> listView;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	
	public void setModel(Option model) {
		if (model != null)
			this.list = model.getContent();
		else
			this.list = null;
	}
	
	public void setView(Parent root) {
		this.root = root; 
	}

	public Node getView() {
		return root;
	}

	public void connectView() {
		listView.getItems().clear();
		
		if (list == null)
			return;
		
		for (int i = 0; i < list.size(); i++) {
			Option option = new Option(list, String.valueOf(i));
			listView.getItems().add(option);
		}
	}

	public void updateView() {
		connectView();
	}

	public void updateModel() {
		// Done by the single cells and by addAction and removeAction
	}
	
	@FXML
	public void addAction(ActionEvent event) {
		if (list == null)
			return;
		
		list.add(null);
	}
	
	@FXML
	public void removeAction(ActionEvent event) {
		if (list == null)
			return;
		
		int index = listView.getSelectionModel().getSelectedIndex();
		if (index >= 0)
			list.remove(index);
	}
	
	@FXML
	public void editAction(ActionEvent event) {
		if (list == null)
			return;
		
		int index = listView.getSelectionModel().getSelectedIndex();
		if (index >= 0) {
			Editor editor = listView.getItems().get(index).getBestEditor();
			editor.setModel(listView.getItems().get(index));
			new EditorWindow(editor).show();
		}
	}
	
}
