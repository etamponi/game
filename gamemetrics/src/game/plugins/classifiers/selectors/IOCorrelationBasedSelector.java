package game.plugins.classifiers.selectors;

import game.core.Block;
import game.core.Dataset;
import game.core.Dataset.SampleIterator;
import game.core.blocks.Encoder;
import game.plugins.classifiers.FeatureSelector;
import game.plugins.correlation.CorrelationCoefficient;
import game.plugins.encoders.IntegerEncoder;
import game.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.analysis.function.Abs;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class IOCorrelationBasedSelector extends FeatureSelector {
	
	public int addedFeatures = 5;
	
	public int runs = 10;
	
	public double datasetPercent = 0.5;
	
	public CorrelationCoefficient coefficient;
	
	private RealVector ioCorrelation;
	
	private List<Integer> range;

	@Override
	public void prepare(Dataset dataset, Block inputEncoder) {
		if (ioCorrelation != null && prepareOnce)
			return; // prepared
		
		ioCorrelation = new ArrayRealVector(inputEncoder.getFeatureNumber());
		
		Encoder outputEncoder = new IntegerEncoder();
		outputEncoder.setOption("template", dataset.template.outputTemplate);
		
		for(int i = 0; i < runs; i++) {
			SampleIterator it = dataset.getRandomSubset(datasetPercent).encodedSampleIterator(inputEncoder, outputEncoder, false);
			RealVector vector = coefficient.computeIOCorrelationMatrix(it).getColumnVector(0);
			ioCorrelation = ioCorrelation.add(vector);
		}
		
		ioCorrelation.mapDivideToSelf(runs).mapToSelf(new Abs());
		
		range = Utils.range(0, inputEncoder.getFeatureNumber());
	}

	@Override
	public List<Integer> select(int n, int[] timesChoosen) {
		if (n > 0) {
			int N = n + addedFeatures;
			Collections.shuffle(range);
			return selectBest(n, N, timesChoosen);
		} else {
			return range;
		}
	}

	private List<Integer> selectBest(int n, int N, int[] timesChoosen) {
		List<Integer> base = range.subList(0, N);
		List<Integer> ret = new ArrayList<>(n);
		RealVector weights = new ArrayRealVector();
		RealVector p = adjust(ioCorrelation, timesChoosen);
		for(int index: base)
			weights = weights.append(p.getEntry(index));
		for(int i = 0; i < n; i++) {
			int index = weights.getMaxIndex();
			weights.setEntry(index, -1);
			ret.add(base.get(index));
		}
		return ret;
	}

}
