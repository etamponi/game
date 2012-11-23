package game.plugins.weka.algorithms;

import game.core.Block;
import game.core.Dataset;
import game.core.Sample;
import game.core.TrainingAlgorithm;
import game.core.Dataset.SampleIterator;
import game.plugins.weka.classifiers.WekaClassifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public abstract class WekaTrainingAlgorithm extends TrainingAlgorithm<WekaClassifier> {

	@Override
	public boolean isCompatible(Block object) {
		return object instanceof WekaClassifier;
	}
	
	protected abstract weka.classifiers.Classifier setupInternal(Dataset dataset, Instances instances);

	@Override
	protected void train(Dataset dataset) {
		updateStatus(0.01, "preparing Weka format for samples...");
		SampleIterator it = dataset.encodedSampleIterator(block.getParent(0), block.outputEncoder, false);
		Sample sample = it.next();
		int inputSize = sample.getEncodedInput().getDimension();
		FastVector attributes = new FastVector();
		for(int i = 0; i < inputSize; i++) {
			attributes.addElement(new Attribute("a"+i));
		}
		FastVector classes = new FastVector();
		for(String label: (Iterable<String>)block.template.outputTemplate.getContent("labels"))
			classes.addElement(label);
		attributes.addElement(new Attribute("class", classes));
		
		Instances ts = new Instances("training", attributes, 0);
		
		updateStatus(0.05, "porting samples to Weka format...");
		while(it.hasNext()) {
			Instance i = new Instance(inputSize+1);
			for(int index = 0; index < sample.getEncodedInput().getDimension(); index++)
				i.setValue((Attribute)attributes.elementAt(index), sample.getEncodedInput().getEntry(index));
			i.setValue((Attribute)attributes.elementAt(inputSize), (String)sample.getOutput());
			ts.add(i);
			sample = it.next();
		} 
		ts.setClassIndex(inputSize);
		
		weka.classifiers.Classifier internal = setupInternal(dataset, ts);
		
		updateStatus(0.10, "running Weka training, please wait...");
		try {
			internal.buildClassifier(ts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		block.setContent("dataset", new Instances("training", attributes, 0));
		block.dataset.setClassIndex(inputSize);
		block.setContent("internal", internal);
	}

	@Override
	protected String getManagedPropertyNames() {
		return "internal dataset";
	}

}
