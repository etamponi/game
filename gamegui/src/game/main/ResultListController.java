package game.main;

import game.core.Experiment;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;

public class ResultListController implements Initializable {
	
	@FXML
	private TreeView<String> resultsView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}
	
	public void addCompletedExperiment(Experiment e) {
		
	}
	
	@FXML
	public void onShow(ActionEvent event) {
		
	}
	
	@FXML
	public void onRemove(ActionEvent event) {
		
	}
	
	@FXML
	public void onClear(ActionEvent event) {
		
	}

}
