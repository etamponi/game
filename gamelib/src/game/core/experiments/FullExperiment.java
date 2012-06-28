package game.core.experiments;

import game.core.Experiment;
import game.core.Graph;
import game.core.GraphTrainer;

public abstract class FullExperiment extends Experiment {
	
	public Graph graph;
	
	public GraphTrainer trainer;
	
	public FullExperiment() {
		setOptionBinding("template", "graph.template");
	}

}
