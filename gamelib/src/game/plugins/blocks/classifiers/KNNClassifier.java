package game.plugins.blocks.classifiers;

import game.core.Data;
import game.core.DatasetTemplate;
import game.core.Element;
import game.core.blocks.Classifier;
import game.plugins.valuetemplates.VectorTemplate;
import game.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.linear.RealVector;

import com.ios.ErrorCheck;
import com.ios.errorchecks.PositivenessCheck;

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
	
	@InName
	public int k = 1;
	@InName
	public String distanceType = "L2";

	public List<ReferenceSample> reference = new ArrayList<>();
	
	public KNNClassifier() {
		addErrorCheck("k", new PositivenessCheck(false));
		
		addErrorCheck("distanceType", new ErrorCheck<String>() {
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
	public Data classify(Data input) {
		Data ret = new Data();
		
		for (int j = 0; j < input.length(); j++) {
			RealVector currentVector = (RealVector) input.get(j).get(0);
			for(ReferenceSample sample: reference)
				sample.setDistance(Utils.getDistance(distanceType, currentVector, sample.getInput()));

			Collections.sort(reference);
			RealVector currentOutput = reference.get(0).getOutput();
			if (k > 1) {
				for(int i = 1; i < k; i++)
					currentOutput = currentOutput.add(reference.get(i).getOutput());
				currentOutput.mapDivideToSelf(k);
			}
			ret.add(new Element(currentOutput));
		}
		
		return ret;
	}

	@Override
	public boolean isClassifierCompatible(DatasetTemplate template) {
		return template.sourceTemplate.isSingletonTemplate(VectorTemplate.class);
	}

}
