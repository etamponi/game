/*******************************************************************************
 * Copyright (c) 2012 Emanuele.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuele - initial API and implementation
 ******************************************************************************/
package game.plugins.classifiers;

import game.configuration.ConfigurableList;
import game.configuration.ErrorCheck;
import game.configuration.errorchecks.PositivenessCheck;
import game.core.Encoding;
import game.core.InstanceTemplate;
import game.core.blocks.Classifier;
import game.core.blocks.Encoder;
import game.plugins.Constraint;
import game.plugins.datatemplates.LabelTemplate;
import game.plugins.encoders.OneHotEncoder;
import game.utils.Utils;

import java.util.Collections;

import org.apache.commons.math3.linear.RealVector;

public class KNNClassifier extends Classifier {
	
	public static class ReferenceSample implements Comparable<ReferenceSample> {
		private double distance = Double.MAX_VALUE;
		private RealVector input;
		private RealVector output;
		
		public ReferenceSample(RealVector input, RealVector output) {
			this.input = input;
			this.output = output;
		}
		
		public void setDistance(double distance) {
			this.distance = distance;
		}

		@Override
		public int compareTo(ReferenceSample other) {
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
	
	public String distanceType = "L2";

	public ConfigurableList reference = new ConfigurableList(this, ReferenceSample.class);
	
	public KNNClassifier() {
		setOption("outputEncoder", new OneHotEncoder());
		
		setOptionConstraints("outputEncoder", new Constraint<Encoder>() {
			@Override public boolean isValid(Encoder o) {
				return o instanceof OneHotEncoder;
			}
		});
		
		setOptionChecks("k", new PositivenessCheck(false));
		
		setOptionChecks("distanceType", new ErrorCheck<String>() {
			@Override public String getError(String value) {
				value = value.toLowerCase();
				if (value.equals("l2") || value.equals("l1") || value.equals("linf"))
					return null;
				else
					return "can only be L1, L2 or Linf";
			}
			
		});
	}

	@Override
	public boolean isCompatible(InstanceTemplate template) {
		return template.outputTemplate instanceof LabelTemplate;
	}

	@Override
	protected Encoding classify(Encoding inputEncoded) {
		Encoding ret = new Encoding(getFeatureNumber(), inputEncoded.length());
		
		Class<ReferenceSample> cast = ReferenceSample.class;
		
		for (int j = 0; j < inputEncoded.length(); j++) {
			RealVector currentInput = inputEncoded.getElement(j);
			for(ReferenceSample sample: reference.getList(cast))
				sample.setDistance(Utils.getDistance(distanceType, currentInput, sample.getInput()));

			Collections.sort(reference);
			RealVector currentOutput = reference.get(0, cast).getOutput();
			if (k > 1) {
				for(int i = 1; i < k; i++)
					currentOutput = currentOutput.add(reference.get(i, cast).getOutput());
				currentOutput.mapDivideToSelf(k);
			}
			ret.setElement(j, currentOutput);
		}
		
		return ret;
	}

}
