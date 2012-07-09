package game.plugins.weka.classifiers;

import game.configuration.ErrorCheck;
import game.core.Dataset;
import game.core.Dataset.EncodedSamples;
import game.core.EncodedSample;
import game.core.Encoding;
import game.core.InstanceTemplate;
import game.core.blocks.Classifier;
import game.core.blocks.Encoder;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.SequenceTemplate;
import game.plugins.encoders.BooleanEncoder;
import game.plugins.encoders.BaseSequenceEncoder;
import game.plugins.encoders.ProbabilityEncoder;

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
	
	public int validationPercent = 10;
	
	public int validationThreshold = 20;
	
	public boolean showGUI = false;
	
	public MultilayerPerceptron nn;

	public WekaMultilayerPerceptron() {
		setInternalOptions("nn");
		
		setOptionChecks("outputEncoder", new ErrorCheck<Encoder>(){
			@Override
			public String getError(Encoder value) {
				if (value instanceof BaseSequenceEncoder) {
					Encoder atomEncoder = ((BaseSequenceEncoder) value).atomEncoder;
					if (atomEncoder instanceof ProbabilityEncoder)
						return null;
				}
				if (value instanceof ProbabilityEncoder)
					return null;
				return "only OneHotEncoder and BooleanEncoder are allowed";
			}
		});
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
		Encoder atomEncoder = outputEncoder instanceof BaseSequenceEncoder ? (Encoder)outputEncoder.getOption("atomEncoder") : outputEncoder;
		for(double[] input: inputEncoded) {
			Instance i = new Instance(1.0, input);
			try {
				double[] element = nn.distributionForInstance(i);
				if (atomEncoder instanceof BooleanEncoder)
					ret.add(new double[]{element[0]});
				else
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
		nn.setValidationSetSize(validationPercent);
		nn.setValidationThreshold(validationThreshold);
		nn.setGUI(showGUI);
		
		updateStatus(0.01, "calculating samples...");
		EncodedSamples samples = trainingSet.encode(getParent(), outputEncoder);
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
		
		updateStatus(0.05, "porting samples to Weka format...");
		for(EncodedSample sample: samples) {
			Instance i = new Instance(inputSize+1);
			for(int index = 0; index < sample.getInput().length; index++)
				i.setValue((Attribute)attributes.elementAt(index), sample.getInput()[index]);
			i.setValue((Attribute)attributes.elementAt(inputSize), decode(sample.getOutput()));
			ts.add(i);
		}
		ts.setClassIndex(samples.get(0).getInput().length);
		samples = null;
		System.gc();
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
