package game.plugins.evaluations;

import game.core.Dataset;
import game.core.Evaluation;
import game.core.Instance;
import game.core.InstanceTemplate;
import game.plugins.datatemplates.LabelTemplate;

import java.util.LinkedList;
import java.util.List;

public class Recall extends Evaluation {

	public List<Double> recallPerLabel = new LinkedList<>();
	
	public double overallRecall = 0;
	
	public boolean ready;
	
	public Recall() {
		setInternalOptions("recallPerLabel", "overallRecall", "ready");
	}
	
	@Override
	public boolean isCompatible(InstanceTemplate template) {
		return template.outputTemplate instanceof LabelTemplate;
	}

	@Override
	public void evaluate(Dataset dataset) {
		List<String> labels = template.getOption("outputTemplate.labels");
		
		List<Double> singleTP = new LinkedList<>();
		List<Double> singleT = new LinkedList<>();
		for(int k = 0; k < labels.size(); k++) {
			singleTP.add(0.0);
			singleT.add(0.0);
		}
		
		double TP = 0;
		
		for(Instance i: dataset) {
			String trueLabel = (String)i.getOutputData();
			String predLabel = (String)i.getPredictedData();
			int k = labels.indexOf(trueLabel);
			singleT.set(k, singleT.get(k)+1);
			if (trueLabel.equals(predLabel)) {
				singleTP.set(k, singleTP.get(k)+1);
				TP++;
			}
		}
		
		for(int k = 0; k < labels.size(); k++) {
			recallPerLabel.add(singleTP.get(k)/singleT.get(k));
		}
		overallRecall = TP / dataset.size();
		
		ready = true;
	}

	@Override
	public boolean isReady() {
		return ready;
	}

	@Override
	public String prettyPrint() {
		StringBuilder ret = new StringBuilder();
		
		ret.append("Per label recall: ");
		for (String label: (List<String>)template.getOption("outputTemplate.labels"))
			ret.append(String.format("%20s", label));
		ret.append("    -----    Overall\n");
		ret.append("                  ");
		for (double p: recallPerLabel)
			ret.append(String.format("%19.2f%%", 100*p));
		ret.append(String.format("%19.2f%%", 100*overallRecall));
		
		return ret.toString();
	}
	
}