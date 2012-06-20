package game.plugins.testers;

import game.core.DatasetBuilder;
import game.core.Experiment;
import game.core.Graph;
import game.core.GraphTrainer;
import game.core.InstanceTemplate;
import game.plugins.constraints.CompatibleWith;

public class SimpleExperiment extends Experiment {
	
	public InstanceTemplate template;
	
	public Graph graph;
	
	public GraphTrainer trainer;

	public DatasetBuilder trainingDataset;
	
	public DatasetBuilder testingDataset;
	
	public SimpleExperiment() {
		addOptionBinding("template", "graph.template",
									 "trainingDataset.template",
									 "testingDataset.template");
		
		setOptionConstraint("trainingDataset", new CompatibleWith(this, "template"));
		setOptionConstraint("testingDataset", new CompatibleWith(this, "template"));
	}

	@Override
	protected Object runExperiment() {
		// TODO Auto-generated method stub
		return null;
	}

}
