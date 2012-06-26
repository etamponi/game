package game.plugins.experiments;

import game.configuration.errorchecks.SizeCheck;
import game.core.Dataset;
import game.core.DatasetBuilder;
import game.core.Evaluator;
import game.core.Experiment;
import game.core.Graph;
import game.core.GraphTrainer;
import game.core.InstanceTemplate;
import game.core.TemplateCompatibleList;
import game.plugins.constraints.CompatibleWith;
import game.utils.Msg;

import java.util.Map;

public class SimpleExperiment extends Experiment {
	
	public InstanceTemplate template;
	
	public Graph graph;
	
	public GraphTrainer trainer;

	public DatasetBuilder trainingDataset;
	
	public DatasetBuilder testingDataset;
	
	public TemplateCompatibleList evaluators = new TemplateCompatibleList(this, Evaluator.class);
	
	public SimpleExperiment() {
		setOptionChecks("evaluators", new SizeCheck(1));
		
		setOptionBinding("template", "graph.template",
									 "trainingDataset.template",
									 "testingDataset.template",
									 "evaluators.constraint");
		
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
		double increase = 0.09 / evaluators.size();
		for(Evaluator evaluator: evaluators.getList(Evaluator.class)) {
			Map<String, Double> results = startAnotherTaskAndWait(getCurrentPercent()+increase, evaluator, Evaluator.TASKNAME, testedDataset);
			for (String key: results.keySet())
				Msg.data("%s.%s: %s = %6.5f", name, evaluator.name, key, results.get(key));
		}
	}

}
