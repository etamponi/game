package game.plugins.editors.metrics;

import game.editorsystem.Editor;
import game.editorsystem.Option;
import game.plugins.editors.list.ListEditor;
import game.plugins.metrics.TrainedGraphs;
import javafx.scene.Node;

public class TrainedGraphsViewer extends Editor {
	
	private ListEditor listEditor = new ListEditor();
	
	public TrainedGraphsViewer() {
		listEditor.setReadOnly(true);
	}

	@Override
	public Node getView() {
		return listEditor.getView();
	}

	@Override
	public void updateView() {
		listEditor.updateView();
	}

	@Override
	public boolean isInline() {
		return false;
	}

	@Override
	public Class getBaseEditableClass() {
		return TrainedGraphs.class;
	}

	@Override
	public void connect(Option model) {
		listEditor.connect(new Option((TrainedGraphs)model.getContent(), "trainedGraphs"));
		super.connect(model);
	}

	@Override
	public void disconnect() {
		listEditor.disconnect();
		super.disconnect();
	}

}
