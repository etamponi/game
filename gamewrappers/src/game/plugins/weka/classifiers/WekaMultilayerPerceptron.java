package game.plugins.weka.classifiers;

import game.configuration.ErrorCheck;
import game.core.DataTemplate;
import game.core.Dataset;
import game.core.EncodedSample;
import game.core.Encoding;
import game.core.InstanceTemplate;
import game.core.blocks.Classifier;
import game.core.blocks.Encoder;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.SequenceTemplate;
import game.plugins.encoders.OneValueEncoder;
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
	
	public MultilayerPerceptron nn;
	
	private OneValueEncoder atomicEncoder = new OneValueEncoder();
	private PerAtomSequenceEncoder sequenceEncoder = new PerAtomSequenceEncoder();
	
	public WekaMultilayerPerceptron() {
		sequenceEncoder.setOption("atomEncoder", atomicEncoder);
		
		outputEncoder = new Encoder<DataTemplate>() {
			@Override
			public boolean isCompatible(DataTemplate object) {
				return true;
			}
			@Override
			protected Encoding transform(Object inputData) {
				if (template instanceof SequenceTemplate)
					return sequenceEncoder.startTransform(inputData);
				else
					return atomicEncoder.startTransform(inputData);
			}
		};
		
		setOptionChecks("outputEncoder", new ErrorCheck<Encoder>() {
			@Override
			public String getError(Encoder value) {
				if (!(value instanceof OneValueEncoder))
					return "should be an OneValueEncoder";
				else
					return null;
			}
		});
		
		setInternalOptions("nn", "outputEncoder");
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
				ret.add(new double[]{nn.classifyInstance(i)});
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
		nn.setValidationThreshold((int)(nn.getValidationSetSize()*maxError));
		
		List<EncodedSample> samples = trainingSet.encode(getParent(), outputEncoder);
		int inputSize = samples.get(0).getInput().length;
		FastVector attributes = new FastVector();
		for(int i = 0; i < inputSize; i++) {
			attributes.addElement(new Attribute("a"+i));
		}
		attributes.addElement(new Attribute("class"));
		Instances ts = new Instances("training", attributes, 0);
		for(EncodedSample sample: samples) {
			Instance i = new Instance(1.0, concat(sample.getInput(), sample.getOutput()));
			ts.add(i);
		}
		ts.setClassIndex(samples.get(0).getInput().length);
		
		try {
			nn.buildClassifier(ts);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private double[] concat(double[] v1, double[] v2) {
		double[] ret = new double[v1.length+v2.length];
		for(int i = 0; i < v1.length; i++)
			ret[i] = v1[i];
		for(int i = 0; i < v2.length; i++)
			ret[v1.length+i] = v2[i];
		return ret;
	}

}
