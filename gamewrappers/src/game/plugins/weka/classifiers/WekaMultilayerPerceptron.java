/*******************************************************************************
 * Copyright (c) 2012 Emanuele Tamponi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele Tamponi - initial API and implementation
 ******************************************************************************/
package game.plugins.weka.classifiers;

import game.configuration.ErrorCheck;
import game.core.Dataset;
import game.core.Dataset.SampleIterator;
import game.core.Encoding;
import game.core.InstanceTemplate;
import game.core.Sample;
import game.core.blocks.BaseSequenceEncoder;
import game.core.blocks.Classifier;
import game.core.blocks.Encoder;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.encoders.BooleanEncoder;
import game.plugins.encoders.ProbabilityEncoder;
import game.utils.Utils;
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
		setPrivateOptions("nn");
		
		setOptionChecks("outputEncoder", new ErrorCheck<Encoder>(){
			@Override
			public String getError(Encoder value) {
				if (value instanceof BaseSequenceEncoder) {
					Encoder atomEncoder = value.getOption("atomEncoder");
					if (atomEncoder instanceof ProbabilityEncoder)
						return null;
					if ((int)value.getOption("windowSize") != 1)
						return "only window size = 1 is allowed";
				}
				if (value instanceof ProbabilityEncoder)
					return null;
				return "only ProbabilityEncoders are allowed";
			}
		});
	}

	@Override
	public boolean isCompatible(InstanceTemplate template) {
		return Utils.checkTemplateClass(template.outputTemplate, LabelTemplate.class);
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
		
		updateStatus(0.01, "preparing Weka format for samples...");
		SampleIterator it = trainingSet.encodedSampleIterator(getParent(), outputEncoder, false);
		Sample sample = it.next();
		int inputSize = sample.getEncodedInput().length;
		FastVector attributes = new FastVector();
		for(int i = 0; i < inputSize; i++) {
			attributes.addElement(new Attribute("a"+i));
		}
		FastVector classes = new FastVector();
		for(String label: (Iterable<String>)template.outputTemplate.getOption("labels"))
			classes.addElement(label);
		attributes.addElement(new Attribute("class", classes));
		Instances ts = new Instances("training", attributes, 0);
		
		updateStatus(0.05, "porting samples to Weka format...");
		while(it.hasNext()) {
			Instance i = new Instance(inputSize+1);
			for(int index = 0; index < sample.getEncodedInput().length; index++)
				i.setValue((Attribute)attributes.elementAt(index), sample.getEncodedInput()[index]);
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
	}

}
