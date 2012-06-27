package game.plugins.experiments;

import game.core.Dataset;
import game.core.DatasetBuilder;
import game.core.Evaluation;
import game.core.Experiment;
import game.core.Graph;
import game.core.GraphTrainer;
import game.plugins.constraints.CompatibleWith;
import game.utils.Msg;

public class SimpleExperiment extends Experiment {
	
	public Graph graph;
	
	public GraphTrainer trainer;

	public DatasetBuilder trainingDataset;
	
	public DatasetBuilder testingDataset;
		
	public SimpleExperiment() {
		setOptionBinding("template", "graph.template",
									 "trainingDataset.template",
									 "testingDataset.template");
		
		setOptionConstraint("trainingDataset", new CompatibleWith(this, "template"));
		setOptionConstraint("testingDataset", new CompatibleWith(this, "template"));
	}

	@Override
	protected void runExperiment() {
		updateStatus(0.01, "start graph training...");
		startAnotherTaskAndWait(0.50, trainer, GraphTrainer.TASKNAME, graph, trainingDataset.buildDataset());
		updateStatus(0.51, "training complete, beginning testing phase...");
		Dataset testedDataset = startAnotherTaskAndWait(0.90, graph, Graph.CLASSIFYALLTASK, testingDataset.buildDataset());
		updateStatus(0.91, "testing complete, beginning evaluation phase...");
		for(Evaluation evaluation: evaluations.getList(Evaluation.class)) {
			evaluation.evaluate(this, testedDataset);
			Msg.data(evaluation.prettyPrint());
		}
		updateStatus(1.00, "experiment completed.");
	}

}
