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
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class IOCorrelationBasedSelector extends FeatureSelector {
	
	public double extension = 0.2;
	
	public int runs = 10;
	
	public double datasetPercent = 0.5;
	
	public CorrelationCoefficient coefficient;
	
	private RealVector ioCorrelation;
	
	private List<Integer> range;

	@Override
	public void prepare(Dataset dataset, DecisionTree block) {
		if (ioCorrelation != null)
			return; // prepared
		
		ioCorrelation = new ArrayRealVector(block.getParent(0).getFeatureNumber());
		
		Encoder outputEncoder = new IntegerEncoder();
		outputEncoder.setOption("template", dataset.template.outputTemplate);
		
		for(int i = 0; i < runs; i++) {
			SampleIterator it = dataset.getRandomSubset(datasetPercent).encodedSampleIterator(block.getParent(0), outputEncoder, false);
			ioCorrelation = ioCorrelation.add(coefficient.computeIOCorrelationMatrix(it).getColumnVector(0));
		}
		
		ioCorrelation.mapMultiply(1.0/runs);
		
		range = Utils.range(0, block.getParent(0).getFeatureNumber());
	}

	@Override
	public List<Integer> select(int n) {
		if (n > 0) {
			int N = (int)(n + (range.size() - n)*extension);
			Collections.shuffle(range);
			return selectBest(n, N);
		} else {
			return range;
		}
	}

	private List<Integer> selectBest(int n, int N) {
		List<Integer> base = range.subList(0, N);
		List<Integer> ret = new ArrayList<>(n);
		RealVector weights = new ArrayRealVector();
		for(int index: base)
			weights = weights.append(ioCorrelation.getEntry(index));
		for(int i = 0; i < n; i++) {
			int index = weights.getMaxIndex();
			weights.setEntry(index, -1);
			ret.add(base.get(index));
		}
		return ret;
	}

}
