package game.plugins.weka.algorithms;

import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import game.core.Block;
import game.core.Dataset;
import game.core.Sample;
import game.core.TrainingAlgorithm;
import game.core.Dataset.SampleIterator;
import game.plugins.weka.classifiers.WekaMultilayerPerceptron;

public class WekaMultilayerPerceptronTraining extends TrainingAlgorithm<WekaMultilayerPerceptron> {
	
	public double momentum = 0.1;
	
	public double learningRate = 0.01;
	
	public int maxIterations = 1000;
	
	public int validationPercent = 10;
	
	public int validationThreshold = 20;
	
	public boolean showGUI = false;

	@Override
	public boolean isCompatible(Block object) {
		return object instanceof WekaMultilayerPerceptron;
	}

	@Override
	protected void train(Dataset dataset) {
		MultilayerPerceptron nn = new MultilayerPerceptron();

		nn.setAutoBuild(true);
		nn.setMomentum(momentum);
		nn.setLearningRate(learningRate);
		nn.setTrainingTime(maxIterations);
		nn.setHiddenLayers(String.valueOf(block.hiddenNeurons));
		nn.setValidationSetSize(validationPercent);
		nn.setValidationThreshold(validationThreshold);
		nn.setGUI(showGUI);
		
		updateStatus(0.01, "preparing Weka format for samples...");
		SampleIterator it = dataset.encodedSampleIterator(block.getParent(0), block.outputEncoder, false);
		Sample sample = it.next();
		int inputSize = sample.getEncodedInput().getDimension();
		FastVector attributes = new FastVector();
		for(int i = 0; i < inputSize; i++) {
			attributes.addElement(new Attribute("a"+i));
		}
		FastVector classes = new FastVector();
		for(String label: (Iterable<String>)block.template.outputTemplate.getOption("labels"))
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
		updateStatus(0.50, "running Weka training, please wait...");
		try {
			nn.buildClassifier(ts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		block.nn = nn;
	}

	@Override
	public String getTaskDescription() {
		return "training Weka Multilayer Perceptron " + block;
	}

	@Override
	protected String[] getBlockFixedOptions() {
		return new String[]{"nn"};
	}

}
