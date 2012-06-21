package game.plugins.evaluators;

import game.core.Dataset;
import game.core.Evaluator;
import game.core.Instance;
import game.core.InstanceTemplate;
import game.plugins.datatemplates.LabelTemplate;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RecallEvaluator extends Evaluator {
	
	public RecallEvaluator() {
		name = "Recall";
	}

	@Override
	public boolean isCompatible(InstanceTemplate template) {
		return template.outputTemplate instanceof LabelTemplate;
	}

	@Override
	public Map<String, Double> evaluate(Dataset dataset, String logPrefix) {
		List<String> labels = template.getOption("outputTemplate.labels");
		
		Map<String, Double> ret = new LinkedHashMap<>();
		
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
			if (predLabel.equals(trueLabel)) {
				singleTP.set(k, singleTP.get(k)+1);
				TP++;
			}
		}
		
		for(int k = 0; k < labels.size(); k++) {
			ret.put("recall for " + labels.get(k), singleTP.get(k)/singleT.get(k));
		}
		ret.put("overall recall", TP / dataset.size());
		
		return ret;
	}

}
