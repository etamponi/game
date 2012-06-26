package game.plugins.experiments;

import game.configuration.errorchecks.PositivenessCheck;
import game.core.DatasetBuilder;
import game.core.Evaluator;
import game.core.Experiment;
import game.core.Graph;
import game.core.InstanceTemplate;
import game.core.TemplateConstrainedList;
import game.plugins.constraints.CompatibleWith;

public class KFoldCrossValidation extends Experiment {
	
	public int k;
	
	public InstanceTemplate template;
	
	public Graph graph;
	
	public DatasetBuilder dataset;
	
	public TemplateConstrainedList evaluators = new TemplateConstrainedList(this, Evaluator.class);
	
	public KFoldCrossValidation() {
		setOptionBinding("template", "graph.template", 
									 "dataset.template",
									 "evaluators.constraint");
		
		setOptionConstraint("dataset", new CompatibleWith(this, "template"));
		
		setOptionChecks("k", new PositivenessCheck(false));
	}

	@Override
	protected void runExperiment() {
		// TODO Auto-generated method stub

	}

}
