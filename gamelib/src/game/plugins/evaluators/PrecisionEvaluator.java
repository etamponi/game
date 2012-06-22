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

public class PrecisionEvaluator extends Evaluator {

	public PrecisionEvaluator() {
		name = "Precision";
	}
	
	@Override
	public boolean isCompatible(InstanceTemplate template) {
		return template.outputTemplate instanceof LabelTemplate;
	}

	@Override
	public Map<String, Double> evaluate(Dataset dataset) {
		List<String> labels = template.getOption("outputTemplate.labels");
		
		Map<String, Double> ret = new LinkedHashMap<>();
		
		List<Double> singleTP = new LinkedList<>();
		List<Double> singleP = new LinkedList<>();
		for(int k = 0; k < labels.size(); k++) {
			singleTP.add(0.0);
			singleP.add(0.0);
		}
		
		double TP = 0;
		
		for(Instance i: dataset) {
			String trueLabel = (String)i.getOutputData();
			String predLabel = (String)i.getPredictedData();
			int k = labels.indexOf(predLabel);
			singleP.set(k, singleP.get(k)+1);
			if (predLabel.equals(trueLabel)) {
				singleTP.set(k, singleTP.get(k)+1);
				TP++;
			}
		}
		
		for(int k = 0; k < labels.size(); k++) {
			ret.put("precision for " + labels.get(k), singleTP.get(k)/singleP.get(k));
		}
		ret.put("overall precision", TP / dataset.size());
		
		return ret;
	}

}
