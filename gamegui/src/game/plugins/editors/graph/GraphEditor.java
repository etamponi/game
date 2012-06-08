package game.plugins.editors.graph;

import game.core.Graph;
import game.editorsystem.ControlledEditor;

public class GraphEditor extends ControlledEditor {

	@Override
	public Class getBaseEditableClass() {
		return Graph.class;
	}

}
