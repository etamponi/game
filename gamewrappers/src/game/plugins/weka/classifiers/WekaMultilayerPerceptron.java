package game.plugins.weka.classifiers;

import game.core.Dataset;
import game.core.EncodedSample;
import game.core.Encoding;
import game.core.InstanceTemplate;
import game.core.blocks.Classifier;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.SequenceTemplate;
import game.plugins.encoders.OneHotEncoder;
import game.plugins.encoders.PerAtomSequenceEncoder;

import java.util.List;

import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class WekaMultilayerPerceptron extends Classifier {
	
	public int hiddenNeurons = 5;
	
	public double momentum = 0.1;
	
	public double learningRate = 0.01;
	
	public int maxIterations = 1000;
	
	public double maxError = 0.01;
	
	public double validationPercent = 0.1;
	
	public int validationThreshold = 20;
	
	public MultilayerPerceptron nn;

	public WekaMultilayerPerceptron() {
		setInternalOptions("nn", "outputEncoder");
	}
	
	public void setTemplate(InstanceTemplate template) {
		if (template.outputTemplate instanceof SequenceTemplate) {
			outputEncoder = new PerAtomSequenceEncoder();
			outputEncoder.setOption("atomEncoder", new OneHotEncoder());
		}
		if (template.outputTemplate instanceof LabelTemplate) {
			outputEncoder = new OneHotEncoder();
		}
		this.template = template; 
	}

	@Override
	public boolean isCompatible(InstanceTemplate template) {
		return template.outputTemplate instanceof LabelTemplate ||
				(template.outputTemplate instanceof SequenceTemplate &&
						template.outputTemplate.getOption("atom") instanceof LabelTemplate);
	}

	@Override
	protected Encoding classify(Encoding inputEncoded) {
		Encoding ret = new Encoding();
		for(double[] input: inputEncoded) {
			Instance i = new Instance(1.0, input);
			try {
				double[] element = nn.distributionForInstance(i);
				ret.add(element);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	@Override
	public boolean isTrained() {
		return nn != null;
	}

	@Override
	protected void train(Dataset trainingSet) {
		nn = new MultilayerPerceptron();

		nn.setAutoBuild(true);
		nn.setMomentum(momentum);
		nn.setLearningRate(learningRate);
		nn.setTrainingTime(maxIterations);
		nn.setHiddenLayers(String.valueOf(hiddenNeurons));
		nn.setValidationSetSize((int)(validationPercent*trainingSet.size()));
		nn.setValidationThreshold(validationThreshold);
		
		List<EncodedSample> samples = trainingSet.encode(getParent(), outputEncoder);
		int inputSize = samples.get(0).getInput().length;
		FastVector attributes = new FastVector();
		for(int i = 0; i < inputSize; i++) {
			attributes.addElement(new Attribute("a"+i));
		}
		FastVector classes = new FastVector();
		for(String label: getLabels())
			classes.addElement(label);
		attributes.addElement(new Attribute("class", classes));
		Instances ts = new Instances("training", attributes, 0);
		for(EncodedSample sample: samples) {
			Instance i = new Instance(inputSize+1);
			for(int index = 0; index < sample.getInput().length; index++)
				i.setValue((Attribute)attributes.elementAt(index), sample.getInput()[index]);
			i.setValue((Attribute)attributes.elementAt(inputSize), decode(sample.getOutput()));
			ts.add(i);
		}
		ts.setClassIndex(samples.get(0).getInput().length);
		
		updateStatus(0.50, "starting Weka training, please wait...");
		try {
			nn.buildClassifier(ts);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private List<String> getLabels() {
		List<String> ret = null;
		if (template.outputTemplate instanceof LabelTemplate)
			ret = template.outputTemplate.getOption("labels");
		if (template.outputTemplate instanceof SequenceTemplate)
			ret = template.outputTemplate.getOption("atom.labels");
		return ret;
	}
	
	private String decode(double[] encoding) {
		int i = 0;
		for(; i < encoding.length; i++)
			if (encoding[i] != 0)
				break;
		return getLabels().get(i);
	}

}
