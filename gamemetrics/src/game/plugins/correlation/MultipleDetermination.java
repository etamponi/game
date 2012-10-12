package game.plugins.correlation;

import game.core.Dataset.SampleIterator;
import game.core.Sample;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class MultipleDetermination extends CorrelationMeasure {
	
	public boolean adjust = true;

	@Override
	public RealVector evaluate(SampleIterator it, int samples) {
		int cols = it.next().getEncodedOutput().length;
		
		RealVector ret = new ArrayRealVector(cols);
		double[]   Y = new double[samples];
		double[][] X = new double[samples][];
		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
		
		for(int col = 0; col < cols; col++) {
			it.reset();
			for(int i = 0; i < samples && it.hasNext(); i++) {
				Sample sample = it.next();
				Y[i] = sample.getEncodedOutput()[col];
				X[i] = sample.getEncodedInput();
			}
			regression.newSampleData(Y, X);
			double R2 = adjust ? regression.calculateAdjustedRSquared() : regression.calculateRSquared();
			ret.setEntry(col, Math.sqrt(R2));
		}
		
		return ret;
	}

}
