package game.plugins.metrics;

import game.core.DataTemplate;
import game.core.Dataset;
import game.core.Dataset.OutputPair;
import game.core.Dataset.OutputPairIterator;
import game.core.Experiment;
import game.core.experiments.FullExperiment;
import game.core.metrics.FullMetric;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.SequenceTemplate;

import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class ConfusionMatrix extends FullMetric {
	
	public RealMatrix matrix;
	public List<String> labels;
	
	public ConfusionMatrix() {
		setPrivateOptions("matrix", "labels");
	}
	
	@Override
	public boolean isCompatible(Experiment exp) {
		return super.isCompatible(exp) &&
				isCompatible(exp.template.outputTemplate);
	}
	
	private boolean isCompatible(DataTemplate template) {
		return template instanceof LabelTemplate ||
				(template instanceof SequenceTemplate && template.getOption("atom") instanceof LabelTemplate);
	}

	@Override
	public boolean isReady() {
		return matrix != null;
	}

	@Override
	public void evaluate(FullExperiment experiment) {
		labels = experiment.template.outputTemplate.getOption("labels");
		
		matrix = new Array2DRowRealMatrix(labels.size(), labels.size());
		Dataset dataset = mergeFolds(experiment.testedDatasets);
		
		OutputPairIterator it = dataset.outputPairIterator();
		while(it.hasNext()) {
			OutputPair pair = it.next();
			int observed = labels.indexOf(pair.getObserved());
			int predicted = labels.indexOf(pair.getPredicted());
			matrix.setEntry(observed, predicted, matrix.getEntry(observed, predicted)+1);
		}
	}

	@Override
	public String prettyPrint() {
		StringBuilder ret = new StringBuilder();
		
		ret.append(String.format("%20s|", ""));
		for(String label: labels)
			ret.append(String.format("%20s|", label+"(P)"));
		ret.append("\n");
		for(int i = 0; i < labels.size(); i++) {
			String observed = labels.get(i);
			ret.append(String.format("%20s|", observed+"(O)"));
			for(int j = 0; j < labels.size(); j++)
				ret.append(String.format("%16f    |", matrix.getEntry(i, j)));
			ret.append("\n");
		}
		
		return ret.toString();
	}

}
