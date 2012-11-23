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
package game.plugins.weka.algorithms;

import game.core.Block;
import game.core.Dataset;
import game.plugins.weka.classifiers.WekaClassifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

public class WekaMultilayerPerceptron extends WekaTrainingAlgorithm {
	
	public double momentum = 0.1;
	
	public double learningRate = 0.01;
	
	public int maxIterations = 1000;
	
	public int validationPercent = 10;
	
	public int validationThreshold = 20;
	
	public int hiddenNeurons = 5;
	
	public boolean showGUI = false;

	@Override
	public boolean isCompatible(Block object) {
		return object instanceof WekaClassifier;
	}

	@Override
	protected weka.classifiers.Classifier setupInternal(Dataset dataset, Instances instances) {
		MultilayerPerceptron nn = new MultilayerPerceptron();

		nn.setAutoBuild(true);
		nn.setMomentum(momentum);
		nn.setLearningRate(learningRate);
		nn.setTrainingTime(maxIterations);
		nn.setHiddenLayers(String.valueOf(hiddenNeurons));
		nn.setValidationSetSize(validationPercent);
		nn.setValidationThreshold(validationThreshold);
		nn.setGUI(showGUI);
		
		return nn;
	}

	@Override
	public String getManagedPropertyNames() {
		return "internal";
	}

}
