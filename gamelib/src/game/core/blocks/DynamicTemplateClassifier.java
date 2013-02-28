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
	public String classifierCompatibilityError(DatasetTemplate template) {
		return null;
	}

	@Override
	public Data classify(Data input) {
		return mapping.map(internal.classify(input));
	}

}
