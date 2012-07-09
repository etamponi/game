package game.plugins.metrics;

import game.core.Experiment;
import game.core.Graph;
import game.core.Metric;
import game.core.experiments.FullExperiment;

import java.util.ArrayList;
import java.util.List;

public class TrainedGraphList extends Metric<Graph> {
	
	public List<Graph> graphs = new ArrayList<>();
	
	public TrainedGraphList() {
		setInternalOptions("graphs");
	}

	@Override
	public boolean isCompatible(Experiment object) {
		return object instanceof FullExperiment;
	}

	@Override
	public boolean isReady() {
		return !graphs.isEmpty();
	}

	@Override
	public void evaluate(Graph... params) {
		graphs = new ArrayList<>(params.length);
		for(int i = 0; i < params.length; i++)
			graphs.add(params[i]);
	}

	@Override
	public String prettyPrint() {
		StringBuilder ret = new StringBuilder();
		ret.append("Trained graphs: ").append(graphs.size()).append("\n");
		return ret.toString();
	}

}
