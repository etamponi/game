package game.core.blocks;

import game.core.Data;
import game.core.ElementTemplate;
import game.plugins.datatemplates.VectorTemplate;


public abstract class Classifier extends Pipe {
	
	public Classifier() {
		setContent("outputTemplate", new ElementTemplate(new VectorTemplate()));
	}
	
	protected abstract Data classify(Data input);
	
	@Override
	protected Data transduce(Data input) {
		return classify(input);
	}
	
	public VectorTemplate getOutputVectorTemplate() {
		return outputTemplate == null ? null : outputTemplate.getSingleton(VectorTemplate.class);
	}

}
