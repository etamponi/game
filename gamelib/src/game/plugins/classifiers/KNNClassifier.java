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

import game.configuration.errorchecks.PositivenessCheck;
import game.core.Dataset;
import game.core.Dataset.SampleIterator;
import game.core.Encoding;
import game.core.InstanceTemplate;
import game.core.Sample;
import game.core.blocks.Classifier;
import game.plugins.datatemplates.LabelTemplate;
import game.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;

public class KNNClassifier extends Classifier {
	
	private static class SampleWithDistance implements Comparable<SampleWithDistance> {
		private double distance = Double.MAX_VALUE;
		private double[] input;
		private double[] output;
		
		public SampleWithDistance(double[] input, double[] output) {
			this.input = input;
			this.output = output;
		}
		
		public void setDistance(double distance) {
			this.distance = distance;
		}

		@Override
		public int compareTo(SampleWithDistance other) {
			return Double.compare(distance, other.distance);
		}
		
		public double[] getInput() {
			return input;
		}
		
		public double[] getOutput() {
			return output;
		}
	}
	
	public int k;
	
	public ArrayList<SampleWithDistance> reference;
	
	public KNNClassifier() {
		setOptionChecks("k", new PositivenessCheck(false));
		
		setPrivateOptions("reference");
	}

	@Override
	public boolean isCompatible(InstanceTemplate template) {
		return Utils.checkTemplateClass(template.outputTemplate, LabelTemplate.class);
	}

	@Override
	protected Encoding classify(Encoding inputEncoded) {
		Encoding ret = new Encoding();
		
		for (double[] currentInput: inputEncoded) {
			for(int i = 0; i < reference.size(); i++) {
				SampleWithDistance sample = reference.get(i);
				sample.setDistance(Utils.getDistance(currentInput, sample.getInput()));
			}
			Collections.sort(reference);
			double[] currentOutput = reference.get(0).getOutput();
			if (k > 1) {
				for(int i = 1; i < k; i++)
					Utils.sumTo(currentOutput, reference.get(i).getOutput());
				Utils.scale(currentOutput, 1.0/k);
			}
			ret.add(currentOutput);
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
			reference.add(new SampleWithDistance(sample.getEncodedInput(), sample.getEncodedOutput()));
		}
	}

}
