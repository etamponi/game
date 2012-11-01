package game.plugins.classifiers.selectors;

import game.core.Dataset;
import game.core.Dataset.SampleIterator;
import game.core.blocks.Encoder;
import game.plugins.classifiers.DecisionTree;
import game.plugins.classifiers.FeatureSelector;
import game.plugins.correlation.CorrelationCoefficient;
import game.plugins.encoders.IntegerEncoder;
import game.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.AbstractIntegerDistribution;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class IOCorrelationSelector extends FeatureSelector {
	
	public int runs = 10;
	
	public double datasetPercent = 0.5;
	
	public CorrelationCoefficient coefficient;
	
	private RealVector ioCorrelation;
	
	private List<Integer> range;
	
	private static class CustomDistribution extends AbstractIntegerDistribution {
		
		private RealVector probabilities;
		
		public CustomDistribution(RealVector probabilities) {
			this.probabilities = probabilities;
		}

		@Override
		public double cumulativeProbability(int x) {
			double ret = 0;
			for(int i = 0; i <= x; i++)
				ret += probabilities.getEntry(i);
			return ret;
		}

		@Override
		public double getNumericalMean() {
			double ret = 0;
			for(int i = 0; i < probabilities.getDimension(); i++)
				ret += i*probabilities.getEntry(i);
			return ret;
		}

		@Override
		public double getNumericalVariance() {
			double mean = getNumericalMean();
			double ret = 0;
			for(int i = 0; i < probabilities.getDimension(); i++)
				ret += (i - mean)*(i - mean)*probabilities.getEntry(i);
			return ret;
		}

		@Override
		public int getSupportLowerBound() {
			return 0;
		}

		@Override
		public int getSupportUpperBound() {
			return probabilities.getDimension()-1;
		}

		@Override
		public boolean isSupportConnected() {
			return false;
		}

		@Override
		public double probability(int x) {
			return probabilities.getEntry(x);
		}
		
	}

	@Override
	public void prepare(Dataset dataset, DecisionTree block) {
		if (ioCorrelation != null)
			return; // prepared
		
		ioCorrelation = new ArrayRealVector(block.getParent(0).getFeatureNumber());
		
		Encoder outputEncoder = new IntegerEncoder();
		outputEncoder.setOption("template", dataset.template.outputTemplate);
		
		for(int i = 0; i < runs; i++) {
			SampleIterator it = dataset.getRandomSubset(datasetPercent).encodedSampleIterator(block.getParent(0), outputEncoder, false);
			RealVector vector = coefficient.computeIOCorrelationMatrix(it).getColumnVector(0);
			ioCorrelation = ioCorrelation.add(vector);
		}
		
		ioCorrelation.mapDivideToSelf(runs);
		
		range = Utils.range(0, block.getParent(0).getFeatureNumber());
	}

	@Override
	public List<Integer> select(int n) {
		if (n > 0) {
			List<Integer> ret = new ArrayList<>(n);
			RealVector p = ioCorrelation.copy();
			for(int i = 0; i < n; i++) {
				CustomDistribution d = new CustomDistribution(p);
				int index = d.sample();
				double scale = 1.0 - p.getEntry(index);
				p.setEntry(index, 0);
				p.mapDivideToSelf(scale);
				ret.add(index);
			}
			return ret;
		} else {
			return range;
		}
	}

}
