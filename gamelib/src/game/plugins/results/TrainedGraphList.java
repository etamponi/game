package game.plugins.results;

import game.core.Experiment;
import game.core.Graph;
import game.core.Result;

import java.util.ArrayList;
import java.util.List;

public class TrainedGraphList extends Result<Graph> {
	
	public List<Graph> graphs;
	
	public TrainedGraphList() {
		setInternalOptions("graphs");
	}

	@Override
	public boolean isCompatible(Experiment object) {
		return false;
	}

	@Override
	public boolean isReady() {
		return graphs != null;
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
