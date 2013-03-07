package game.plugins.weka.algorithms;

import game.core.Block;
import game.core.Dataset;
import game.core.DatasetTemplate;
import game.core.Dataset.SampleIterator;
import game.core.Sample;
import game.core.trainingalgorithms.ClassifierTrainingAlgorithm;
import game.plugins.valuetemplates.LabelTemplate;
import game.plugins.weka.classifiers.WekaClassifier;

import org.apache.commons.math3.linear.RealVector;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public abstract class WekaTrainingAlgorithm extends ClassifierTrainingAlgorithm<WekaClassifier> {

	protected abstract weka.classifiers.Classifier setupInternal(Dataset dataset, Instances instances);

	@Override
	protected void train(Dataset dataset) {
		updateStatus(0.01, "preparing Weka format for samples...");
		SampleIterator it = dataset.sampleIterator();
		Sample sample = it.next();
		int inputSize = dataset.getTemplate().getContent("sourceTemplate.0.dimension");
		FastVector attributes = new FastVector();
		for(int i = 0; i < inputSize; i++) {
			attributes.addElement(new Attribute("a"+i));
		}
		FastVector classes = new FastVector();
		for(String label: dataset.getTemplate().targetTemplate.getSingleton(LabelTemplate.class).labels)
			classes.addElement(label);
		attributes.addElement(new Attribute("class", classes));
		
		Instances ts = new Instances("training", attributes, 0);
		
		updateStatus(0.05, "porting samples to Weka format...");
		while(it.hasNext()) {
			Instance i = new Instance(inputSize+1);
			for(int index = 0; index < inputSize; index++)
				i.setValue((Attribute)attributes.elementAt(index), sample.getSource().get(RealVector.class).getEntry(index));
			i.setValue((Attribute)attributes.elementAt(inputSize), (String)sample.getTarget().get());
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
	protected String getTrainingPropertyNames() {
		return "outputTemplate dataset internal";
	}

	@Override
	protected String compatibilityError(DatasetTemplate datasetTemplate) {
		return null;
	}
	
	@Override
	public String compatibilityError(Block block) {
		return block instanceof WekaClassifier ? null : "can only handle WekaClassifier";
	}

}
