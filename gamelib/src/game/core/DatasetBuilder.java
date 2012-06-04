package game.core;

import game.configuration.Configurable;

public abstract class DatasetBuilder extends Configurable {
	
	public InstanceTemplate template;
	
	public abstract Dataset buildDataset();

}
