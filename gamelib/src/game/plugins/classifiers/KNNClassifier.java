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
import game.core.Block;
import game.core.Dataset;
import game.core.EncodedSample;
import game.core.Encoding;
import game.core.InstanceTemplate;
import game.core.blocks.Classifier;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.datatemplates.SequenceTemplate;
import game.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KNNClassifier extends Classifier {
	
	private static class DistancedOutput implements Comparable<DistancedOutput> {
		private double distance;
		private double[] output;
		
		public void setData(double distance, double[] output) {
			this.distance = distance;
			this.output = output;
		}

		@Override
		public int compareTo(DistancedOutput other) {
			return Double.compare(distance, other.distance);
		}
		
		public double[] getOutput() {
			return output;
		}
	}
	
	public int k;
	
	public List<EncodedSample> reference;
	
	public KNNClassifier() {
		setOptionChecks("k", new PositivenessCheck(false));
		
		setInternalOptions("reference");
	}

	@Override
	public boolean isCompatible(InstanceTemplate template) {
		return template.outputTemplate instanceof LabelTemplate ||
				(template.outputTemplate instanceof SequenceTemplate
						&& template.getOption("outputTemplate.atom") instanceof LabelTemplate);
	}

	@Override
	protected Encoding classify(Encoding inputEncoded) {
		Encoding ret = new Encoding();
		
		List<DistancedOutput> list = new ArrayList<>(reference.size());
		for (int i = 0; i < reference.size(); i++)
			list.add(new DistancedOutput());
		
		for (double[] currentInput: inputEncoded) {
			for(int i = 0; i < reference.size(); i++) {
				EncodedSample sample = reference.get(i);
				list.get(i).setData(getDistance(currentInput, sample.getInput()), sample.getOutput());
			}
			Collections.sort(list);
			double[] currentOutput = list.get(0).getOutput();
			if (k > 1) {
				for(int i = 1; i < k; i++)
					Utils.sumTo(currentOutput, list.get(i).getOutput());
				Utils.scale(currentOutput, 1.0/k);
			}
			ret.add(currentOutput);
		}
		
		return ret;
	}
	
	private double getDistance(double[] a, double[] b) {
		double ret = 0;
		for (int i = 0; i < a.length; i++) {
			ret += (a[i]-b[i])*(a[i]-b[i]);
		}
		return ret;
	}

	@Override
	public boolean isTrained() {
		return reference != null;
	}

	@Override
	protected void train(Dataset trainingSet) {
		reference = trainingSet.encode((Block)getParents().get(0), outputEncoder);
	}

}
