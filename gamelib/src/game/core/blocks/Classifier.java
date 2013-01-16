package game.core.blocks;

import game.core.ElementTemplate;
import game.plugins.datatemplates.VectorTemplate;


public abstract class Classifier extends Pipe {
	
	public Classifier() {
		setContent("outputTemplate", new ElementTemplate(new VectorTemplate()));
	}
	
	public VectorTemplate getOutputVectorTemplate() {
		return outputTemplate == null ? null : outputTemplate.getSingleton(VectorTemplate.class);
	}

}
