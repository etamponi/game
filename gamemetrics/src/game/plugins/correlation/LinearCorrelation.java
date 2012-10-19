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
package game.plugins.correlation;

import game.core.Dataset.SampleIterator;
import game.core.Sample;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class LinearCorrelation extends CorrelationMeasure {
	
	public boolean adjust = true;
	
//	public boolean useRegression = false;

	@Override
	public void computeInputCorrelationMatrix(SampleIterator it, int samples) {
		double[][] X = new double[samples][];

		PearsonsCorrelation correlation = new PearsonsCorrelation();
		
		it.reset();
		for(int i = 0; i < samples; i++) {
			Sample sample = it.next();
			X[i] = sample.getEncodedInput().toArray();
		}
		
		inputCorrelationMatrix = correlation.computeCorrelationMatrix(X);
	}

	@Override
	public void computeIOCorrelationMatrix(SampleIterator it, int samples) {
		it.reset();
		
		double[][] X = new double[samples][];
		int outputDim = it.next().getEncodedOutput().getDimension();
		int inputDim = it.next().getEncodedInput().getDimension();
		
		ioCorrelationMatrix = new Array2DRowRealMatrix(inputDim, outputDim);

		for(int col = 0; col < outputDim; col++) {
			it.reset();
			double[] y = new double[samples];
			for(int i = 0; i < samples; i++) {
				Sample sample = it.next();
				y[i] = sample.getEncodedOutput().getEntry(col);
				if (col == 0) // Do it only once
					X[i] = sample.getEncodedInput().toArray();
			}
			
			ioCorrelationMatrix.setColumnVector(col, computeIOCorrelation(X, y, new ArrayList<Integer>()));
		}
	}

	@Override
	public void computeSyntheticValues(SampleIterator it, int samples) {
		if (inputCorrelationMatrix == null)
			computeInputCorrelationMatrix(it, samples);
		
		List<Integer> nanIndices = findNanIndices(inputCorrelationMatrix);
		RealMatrix adjustedInput = adjustInputCorrelation(inputCorrelationMatrix, nanIndices);
		
		if (ioCorrelationMatrix == null)
			computeIOCorrelationMatrix(it, samples);
		
		RealMatrix inverse = new SingularValueDecomposition(adjustedInput).getSolver().getInverse();

		syntheticValues = new ArrayRealVector(ioCorrelationMatrix.getColumnDimension());
		for(int col = 0; col < ioCorrelationMatrix.getColumnDimension(); col++)
			syntheticValues.setEntry(col, determination(inverse, removeNanIndices(ioCorrelationMatrix.getColumnVector(col), nanIndices), samples));
	}
	
	private double determination(RealMatrix inverse, RealVector corrVec, int samples) {
		double R2 = inverse.preMultiply(corrVec).dotProduct(corrVec);
		if (adjust) {
			R2 = 1 - (1-R2)*(samples - 1)/(samples - inputCorrelationMatrix.getColumnDimension() - 1);
		}
		return Math.sqrt(R2);
	}

	private List<Integer> findNanIndices(RealMatrix base) {
		List<Integer> nanIndices = new ArrayList<>();
		for(int i = 0; i < base.getColumnDimension(); i++) {
			if (isAlmostNaN(base.getColumn(i), i))
				nanIndices.add(i);
		}
		return nanIndices;
	}
	
	private boolean isAlmostNaN(double[] col, int diag) {
		for(int i = 0; i < col.length; i++) {
			if (i == diag) {
				continue;
		 	} else {
				if (!Double.isNaN(col[i]))
					return false;
		 	}
		}
		return true;
	}

	private RealVector removeNanIndices(RealVector vec, List<Integer> nanIndices) {
		RealVector ret = new ArrayRealVector();
		for(int i = 0; i < vec.getDimension(); i++)
			if (nanIndices.contains(i))
				continue;
			else
				ret = ret.append(vec.getEntry(i));
		return ret;
	}
	
	public RealMatrix adjustInputCorrelation(RealMatrix base, List<Integer> nanIndices) {
		int adjustedDim = base.getRowDimension()-nanIndices.size();
		RealMatrix adjusted = new Array2DRowRealMatrix(adjustedDim, adjustedDim);
		int i = 0, j = 0;
		for(int basei = 0; basei < base.getRowDimension(); basei++) {
			if (nanIndices.contains(basei))
				continue;
			for(int basej = 0; basej < base.getColumnDimension(); basej++) {
				double baseEntry = base.getEntry(basei, basej);
				if (Double.isNaN(baseEntry))
					continue;
				adjusted.setEntry(i, j++, baseEntry);
			}
			i++;
			j = 0;
		}
		return adjusted;
	}

	private RealVector computeIOCorrelation(double[][] X, double[] y, List<Integer> nanIndices) {
		RealVector ret = new ArrayRealVector(X[0].length-nanIndices.size());
		
		PearsonsCorrelation correlation = new PearsonsCorrelation();
		int pos = 0;
		for(int i = 0; i < X[0].length; i++) {
			if (nanIndices.contains(i))
				continue;
			double[] x = getColumn(X, i);
			if (new ArrayRealVector(x).isNaN())
				System.out.println("NAN IN INPUT!!!");
			double corr = correlation.correlation(x, y);
			if (Double.isNaN(corr))
				corr = 0;
			ret.setEntry(pos++, corr);
		}
		
		return ret;
	}

	private double[] getColumn(double[][] X, int i) {
		double[] ret = new double[X.length];
		int count = 0;
		for(double[] row: X)
			ret[count++] = row[i];
		return ret;
	}
/*
	private RealVector evaluateUsingRegression(SampleIterator it, int samples) {	
		int cols = it.next().getEncodedOutput().getDimension();
		
		RealVector ret = new ArrayRealVector(cols);
		
		double[]   Y = new double[samples];
		double[][] X = new double[samples][];
		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
		
		for(int col = 0; col < cols; col++) {
			it.reset();
			for(int i = 0; i < samples && it.hasNext(); i++) {
				Sample sample = it.next();
				Y[i] = sample.getEncodedOutput().getEntry(col);
				X[i] = sample.getEncodedInput().toArray();
			}
			regression.newSampleData(Y, X);
			double R2 = adjust ? regression.calculateAdjustedRSquared() : regression.calculateRSquared();
			ret.setEntry(col, Math.sqrt(R2));
		}
		
		return ret;
	}
*/
}
