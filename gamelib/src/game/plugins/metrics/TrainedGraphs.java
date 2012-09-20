package game.plugins.metrics;

import game.core.blocks.Graph;
import game.core.experiments.FullExperiment;
import game.core.metrics.FullMetric;

import java.util.List;

public class TrainedGraphs extends FullMetric {
	
	public List<Graph> trainedGraphs;

	@Override
	public boolean isReady() {
		return trainedGraphs != null;
	}

	@Override
	public void evaluate(FullExperiment e) {
		trainedGraphs = e.trainedGraphs;
	}

	@Override
	public String prettyPrint() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Trained graphs: " + trainedGraphs.size() + "\n");
		for(Graph graph: trainedGraphs) {
			builder.append("\t" + graph);
		}
		
		return builder.toString();
	}

}
