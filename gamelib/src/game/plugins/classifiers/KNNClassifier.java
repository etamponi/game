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
package game.plugins.classifiers;

import game.configuration.ErrorCheck;
import game.configuration.errorchecks.PositivenessCheck;
import game.core.Dataset;
import game.core.Dataset.SampleIterator;
import game.core.Encoding;
import game.core.InstanceTemplate;
import game.core.Sample;
import game.core.blocks.Classifier;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.encoders.OneHotEncoder;
import game.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.math3.linear.RealVector;

public class KNNClassifier extends Classifier {
	
	private static class TrainingSampleWithDistance implements Comparable<TrainingSampleWithDistance> {
		private double distance = Double.MAX_VALUE;
		private RealVector input;
		private RealVector output;
		
		public TrainingSampleWithDistance(RealVector input, RealVector output) {
			this.input = input;
			this.output = output;
		}
		
		public void setDistance(double distance) {
			this.distance = distance;
		}

		@Override
		public int compareTo(TrainingSampleWithDistance other) {
			return Double.compare(distance, other.distance);
		}
		
		public RealVector getInput() {
			return input;
		}
		
		public RealVector getOutput() {
			return output;
		}
	}
	
	public int k;
	
	public ArrayList<TrainingSampleWithDistance> reference;
	
	public String distanceType = "L2";
	
	public KNNClassifier() {
		setOption("outputEncoder", new OneHotEncoder());
		
		setOptionChecks("k", new PositivenessCheck(false));
		
		setOptionChecks("distanceType", new ErrorCheck<String>() {
			@Override public String getError(String value) {
				value = value.toLowerCase();
				if (value.equals("l2") || value.equals("l1") || value.equals("linf"))
					return null;
				else
					return "can only be L1, L2 or linf";
			}
			
		});

		setPrivateOptions("reference", "outputEncoder");
	}

	@Override
	public boolean isCompatible(InstanceTemplate template) {
		return template.outputTemplate instanceof LabelTemplate;
	}

	@Override
	protected Encoding classify(Encoding inputEncoded) {
		Encoding ret = new Encoding(getFeatureNumber(), inputEncoded.length());
		
		for (int j = 0; j < inputEncoded.length(); j++) {
			RealVector currentInput = inputEncoded.getElement(j);
			for(int i = 0; i < reference.size(); i++) {
				TrainingSampleWithDistance sample = reference.get(i);
				sample.setDistance(Utils.getDistance(distanceType, currentInput, sample.getInput()));
			}
			Collections.sort(reference);
			RealVector currentOutput = reference.get(0).getOutput();
			if (k > 1) {
				for(int i = 1; i < k; i++)
					currentOutput = currentOutput.add(reference.get(i).getOutput());
				currentOutput.mapDivideToSelf(k);
			}
			ret.setElement(j, currentOutput);
		}
		
		return ret;
	}

	@Override
	public boolean isTrained() {
		return reference != null;
	}

	@Override
	protected void train(Dataset trainingSet) {
		SampleIterator it = trainingSet.encodedSampleIterator(getParent(), outputEncoder, false);
		reference = new ArrayList<>();
		while(it.hasNext()) {
			Sample sample = it.next();
			reference.add(new TrainingSampleWithDistance(sample.getEncodedInput(), sample.getEncodedOutput()));
		}
	}

}
