package game.plugins.evaluations;

import game.core.Dataset;
import game.core.Evaluation;
import game.core.Experiment;
import game.core.Instance;
import game.plugins.datatemplates.ProteinDSSPStructure;
import game.plugins.datatemplates.ProteinHECStructure;
import game.plugins.datatemplates.ProteinPrimaryStructure;

import java.util.LinkedList;
import java.util.List;

public class DetailedFastaEvaluation extends Evaluation {
	
	List<Dataset> folds;

	@Override
	public boolean isCompatible(Experiment object) {
		return object.template.inputTemplate instanceof ProteinPrimaryStructure &&
				(object.template.outputTemplate instanceof ProteinHECStructure ||
				 object.template.outputTemplate instanceof ProteinDSSPStructure);
	}

	@Override
	public boolean isReady() {
		return folds != null;
	}

	@Override
	public void evaluate(Dataset... folds) {
		this.folds = new LinkedList<>();
		for(Dataset fold: folds)
			this.folds.add(fold);
	}

	@Override
	public String prettyPrint() {
		StringBuilder ret = new StringBuilder();
		for(Instance i: folds.get(0)) {
			ret.append("Primary:          ").append(getFasta(i.getInputData())).append("\n");
			ret.append("Secondary (obs):  ").append(getFasta(i.getOutputData())).append("\n");
			ret.append("Secondary (pred): ").append(getFasta(i.getPredictedData())).append("\n\n");
		}
		
		return ret.toString();
	}
	
	public String getFasta(Object data) {
		StringBuilder ret = new StringBuilder();
		for(String e: (List<String>)data)
			ret.append(e);
		return ret.toString();
	}

}
