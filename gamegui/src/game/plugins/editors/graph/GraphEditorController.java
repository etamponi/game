package game.plugins.editors.graph;

import game.core.Dataset;
import game.core.Encoding;
import game.core.Graph;
import game.core.InstanceTemplate;
import game.core.blocks.Classifier;
import game.editorsystem.Editor;
import game.editorsystem.EditorController;
import game.editorsystem.Option;
import game.main.Settings;
import game.utils.Utils;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class GraphEditorController implements EditorController {
	
	private Graph graph;
	
	@FXML
	private AnchorPane root;
	@FXML
	private AnchorPane graphRoot;
	@FXML
	private GridPane confPane;
	@FXML
	private GridPane classifiersPane;
	@FXML
	private GridPane inputEncodersPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		graphRoot.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				HandlePosition handle = (HandlePosition)event.getDragboard().getContent(BlockNode.BLOCKDATA);
				if (handle != null) {
					event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
					
					BlockNode node = Settings.getInstance().getDragging();
					node.setPosition(handle, event.getX(), event.getY());
				}
				
				event.consume();
			}
		});
		
		graphRoot.setOnDragEntered(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if (event.getDragboard().hasContent(BlockNode.BLOCKDATA)) {
					BlockNode node = Settings.getInstance().getDragging();
					
					if (!graphRoot.getChildren().contains(node))
						graphRoot.getChildren().add(node);
				}
				
				event.consume();
			}
		});
		
		graphRoot.setOnDragExited(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if (event.getDragboard().hasContent(BlockNode.BLOCKDATA)) {
					BlockNode node = Settings.getInstance().getDragging();
					if (node != null)
						graphRoot.getChildren().remove(node);
				}
				
				event.consume();
			}
		});
		
		graphRoot.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if (event.getDragboard().hasContent(BlockNode.BLOCKDATA)) {
					Settings.getInstance().setDragging(null);
					event.setDropCompleted(true);
				} else {
					event.setDropCompleted(false);
				}
				
				event.consume();
			}
		});
	}

	@Override
	public void setModel(Option model) {
		if (model != null)
			graph = model.getContent();
		else
			graph = null;
	}

	@Override
	public void connectView() {
		connectConfRoot();
		
		Classifier temp = new Classifier() {
			
			@Override
			protected Encoding transform(Object inputData) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			protected double train(Dataset trainingSet) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public boolean isTrained() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean supportsTemplate(InstanceTemplate template) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		
		classifiersPane.add(new BlockNode(temp, true), 0, 0);
	}

	@Override
	public void updateView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateModel() {
		// TODO Auto-generated method stub
		
	}
	
	private void connectConfRoot() {
		confPane.getChildren().clear();
		if (graph != null) {
			addConfPaneRow("name", 0);
			addConfPaneRow("template", 1);
			addConfPaneRow("decoder", 2);
		}
	}
	
	private void addConfPaneRow(String optionName, int rowIndex) {
		Option option = new Option(graph, optionName);
		Label label = new Label(optionName+": ");
		Editor editor = option.getBestEditor();
		try {
			if (option.getContent() == null && Utils.isConcrete(option.getType()))
				option.setContent(option.getType().newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		editor.setModel(option);
		
		if (option.isBound())
			editor.getView().setDisable(true);
		
		confPane.addRow(rowIndex, label, editor.getView());
		GridPane.setValignment(label, VPos.TOP);
		GridPane.setHalignment(label, HPos.RIGHT);
		GridPane.setMargin(label, new Insets(5, 2, 2, 2));
		GridPane.setHgrow(editor.getView(), Priority.ALWAYS);
		GridPane.setMargin(editor.getView(), new Insets(2, 2, 2, 2));
	}

}
