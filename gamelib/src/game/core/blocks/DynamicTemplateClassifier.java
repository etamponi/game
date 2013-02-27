package game.core.blocks;

import game.core.Data;
import game.core.DatasetTemplate;

public class DynamicTemplateClassifier extends Classifier {
	
	public Classifier internal;
	
	public Mapping mapping;
	
	public DynamicTemplateClassifier() {
		omitFromErrorCheck("internal");
	}
	
	@Override
	public boolean isClassifierCompatible(DatasetTemplate template) {
		return true;
	}

	@Override
	public Data classify(Data input) {
		return mapping.map(internal.classify(input));
	}

}
