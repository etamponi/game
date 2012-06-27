package game.main;

import game.core.Evaluation;
import game.core.Experiment;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class ResultListController implements Initializable {
	
	@FXML
	private TreeView resultsView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		resultsView.setRoot(new TreeItem("Results"));
	}
	
	public void addCompletedExperiment(Experiment e) {
		TreeItem expItem = new TreeItem(e);
		for(Evaluation eva: e.evaluations.getList(Evaluation.class)) {
			TreeItem evaItem = new TreeItem(eva);
			expItem.getChildren().add(evaItem);
		}
		resultsView.getRoot().getChildren().add(expItem);
	}
	
	@FXML
	public void onShow(ActionEvent event) {
		
	}
	
	@FXML
	public void onRemove(ActionEvent event) {
		
	}
	
	@FXML
	public void onClear(ActionEvent event) {
		resultsView.getRoot().getChildren().clear();
	}

}
