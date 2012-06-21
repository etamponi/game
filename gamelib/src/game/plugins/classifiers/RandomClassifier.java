package game.plugins.classifiers;

import game.configuration.errorchecks.PositivenessCheck;
import game.configuration.errorchecks.SizeCheck;
import game.core.Block;
import game.core.Dataset;
import game.core.EncodedSample;
import game.core.Encoding;
import game.core.InstanceTemplate;
import game.core.blocks.Classifier;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.SequenceTemplate;

import java.util.List;

public class RandomClassifier extends Classifier {
	
	public int k = 1;
	
	private List<EncodedSample> reference;
	
	public RandomClassifier() {
		addOptionChecks("parents", new SizeCheck(1, 1));
		
		addOptionChecks("k", new PositivenessCheck(false));
	}

	@Override
	public boolean isCompatible(InstanceTemplate object) {
		return object.outputTemplate instanceof LabelTemplate ||
				(object.outputTemplate instanceof SequenceTemplate &&
				 object.getOption("outputTemplate.atom") instanceof LabelTemplate);
	}

	@Override
	public boolean isTrained() {
		return reference != null;
	}

	@Override
	protected double train(Dataset trainingSet) {
		reference = trainingSet.encode((Block)getParents().get(0), outputEncoder);
		
		return 1.0;
	}

	@Override
	protected Encoding transform(Object inputData) {
		int random = (int)(Math.random() * (reference.size()-1)); 
		Encoding ret = new Encoding(reference.get(random).getOutput());
		return ret;
	}
	
}
