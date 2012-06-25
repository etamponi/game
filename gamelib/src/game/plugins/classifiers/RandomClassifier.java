package game.plugins.classifiers;

import game.configuration.errorchecks.SizeCheck;
import game.core.Block;
import game.core.Dataset;
import game.core.EncodedSample;
import game.core.Encoding;
import game.core.InstanceTemplate;
import game.core.blocks.Classifier;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.SequenceTemplate;

import java.util.LinkedList;
import java.util.List;

public class RandomClassifier extends Classifier {
	
	public List<EncodedSample> reference;
	
	public RandomClassifier() {
		setInternalOptions("reference");
		
		setOptionChecks("parents", new SizeCheck(1, 1));
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
		List list = null;
		if (!(inputData instanceof List)) {
			list = new LinkedList();
			list.add(inputData);
		} else {
			list = (List)inputData;
		}
		Encoding ret = new Encoding();
		for(int i = 0; i < list.size(); i++)
			ret = new Encoding(reference.get((int)(Math.random() * (reference.size()-1))).getOutput());
		return ret;
	}
	
}
