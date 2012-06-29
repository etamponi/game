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
package game.neuroph.classifiers;

import game.configuration.errorchecks.PositivenessCheck;
import game.core.Block;
import game.core.Dataset;
import game.core.EncodedSample;
import game.core.Encoding;
import game.core.InstanceTemplate;
import game.core.blocks.Classifier;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.ResilientPropagation;
import org.neuroph.util.TransferFunctionType;

public class NeurophMLNNClassifier extends Classifier {
	
	public int hiddenNeurons = 1;
	public int maxIterations = 1000;
	public double maxError = 0.001;
	public double learningRate = 0.001;
	public double momentum = 0.1;
	
	public MultiLayerPerceptron nn;
	
	public NeurophMLNNClassifier() {
		setOptionChecks("hiddenNeurons", new PositivenessCheck(false));
		setOptionChecks("maxIterations", new PositivenessCheck(false));
		setOptionChecks("maxError", new PositivenessCheck(false));
		
		setInternalOptions("nn");
	}

	@Override
	public boolean isCompatible(InstanceTemplate template) {
		return true;
	}

	@Override
	protected Encoding classify(Encoding inputEncoded) {
		Encoding ret = new Encoding();
		
		for(double[] input: inputEncoded) {
			nn.setInput(input);
			nn.calculate();
			ret.add(nn.getOutput());
			for(double d: input)
				System.out.print(d + " ");
			System.out.print(": ");
			for(double d: nn.getOutput())
				System.out.print(d + " ");
			System.out.println();
		}
		
		return ret;
	}

	@Override
	public boolean isTrained() {
		return nn != null;
	}

	@Override
	protected void train(Dataset trainingSet) {
		List<EncodedSample> samples = trainingSet.encode((Block)getParent(), outputEncoder);
		
		int inputVectorSize = samples.get(0).getInput().length;
		int outputVectorSize = samples.get(0).getOutput().length;
		
		updateStatus(0.01, "converting the dataset to Neuroph format");
		TrainingSet<SupervisedTrainingElement> training = new TrainingSet<>(inputVectorSize, outputVectorSize);
		for(EncodedSample sample: samples) {
			for(double d: sample.getOutput())
				System.out.print(d + " ");
			System.out.println();
			training.addElement(new SupervisedTrainingElement(sample.getInput().clone(), sample.getOutput().clone()));
		}
		
		updateStatus(0.10, "conversion completed. Begin training of neural network");
		
		nn = new MultiLayerPerceptron(TransferFunctionType.TANH, inputVectorSize, hiddenNeurons, outputVectorSize);
		
		/*
		final MomentumBackpropagation sl = new MomentumBackpropagation();
		sl.setMomentum(momentum);
		sl.setLearningRate(learningRate);
		*/
		final ResilientPropagation sl = new ResilientPropagation();
		sl.setMaxIterations(maxIterations);
		sl.setMaxError(maxError);
		sl.addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				if (sl.getCurrentIteration() % 100 != 0)
					return;
				updateStatus(getCurrentPercent() + 0.90*sl.getCurrentIteration()/maxIterations, "iteration " + sl.getCurrentIteration() + "; net error " + sl.getTotalNetworkError());
			}
		});
		nn.setLearningRule(sl);
		
		nn.learn(training);
		updateStatus(1.00, "training completed.");
	}

}
