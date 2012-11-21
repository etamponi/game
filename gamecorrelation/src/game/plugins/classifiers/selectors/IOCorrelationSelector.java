package game.plugins.classifiers.selectors;

import game.core.Dataset;
import game.core.Dataset.SampleIterator;
import game.core.blocks.Encoder;
import game.plugins.classifiers.FeatureSelector;
import game.plugins.correlation.CorrelationCoefficient;
import game.plugins.encoders.IntegerEncoder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.function.Abs;
import org.apache.commons.math3.distribution.AbstractIntegerDistribution;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class IOCorrelationSelector extends FeatureSelector {
	
	public int runs = 10;
	
	public double datasetPercent = 0.5;
	
	public int minimumSampleNumber = 100;
	
	public CorrelationCoefficient coefficient;
	
	private RealVector evaluateIOCorrelationProbability(Dataset dataset) {
		RealVector ret = new ArrayRealVector(inputEncoder.getFeatureNumber());
		
		if (dataset.size() < minimumSampleNumber) { // FIXME Use option?
			ret.mapAddToSelf(1.0/ret.getDimension());
			return ret;
		}
		
		Encoder outputEncoder = new IntegerEncoder();
		outputEncoder.setContent("template", dataset.getTemplate().outputTemplate);
		
		for(int i = 0; i < runs; i++) {
			SampleIterator it = dataset.getRandomSubset(datasetPercent).encodedSampleIterator(inputEncoder, outputEncoder, false);
			RealVector vector = coefficient.computeIOCorrelationMatrix(it).getColumnVector(0);
			ret = ret.add(vector);
		}
		
		ret.mapToSelf(new Abs()).mapDivideToSelf(ret.getL1Norm());
		
		return ret;
	}

	@Override
	public List<Integer> select(int n, Dataset dataset) {
		List<Integer> ret = new ArrayList<>(n);
		RealVector p = evaluateIOCorrelationProbability(dataset);
		CustomDistribution d = new CustomDistribution();
		for(int i = 0; i < n; i++) {
			d.setProbabilities(p);
			int index = d.sample();
			double scale = 1.0 - p.getEntry(index);
			p.setEntry(index, 0);
			p.mapDivideToSelf(scale);
			ret.add(index);
		}
		return ret;
	}
	
	private static class CustomDistribution extends AbstractIntegerDistribution {
		
		private RealVector probabilities;
		
		public void setProbabilities(RealVector probabilities) {
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

}
