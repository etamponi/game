package game.core.experiments;

import game.configuration.ConfigurableList;
import game.core.Dataset;
import game.core.Result;
import game.core.blocks.Graph;

public class FullResult extends Result {
	
	public ConfigurableList trainedGraphs = new ConfigurableList(this, Graph.class);
	
	public ConfigurableList testedDatasets = new ConfigurableList(this, Dataset.class);

}
