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
package game.plugins.correlation;

import game.core.Dataset.SampleIterator;
import game.core.Sample;
import game.utils.Log;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import Jama.Matrix;

public class LinearCorrelation extends CorrelationCoefficient {
	
	public boolean adjust = true;

	@Override
	public RealMatrix computeInputCorrelationMatrix(SampleIterator it) {
		List<double[]> X = new ArrayList<>();

		PearsonsCorrelation correlation = new PearsonsCorrelation();
		
		it.reset();
		for(int i = 0; i < maxSamples && it.hasNext(); i++) {
			Sample sample = it.next();
			X.add(injectNoise(sample.getEncodedInput().toArray()));
		}
		
		return correlation.computeCorrelationMatrix(X.toArray(new double[][]{}));
	}

	@Override
	public RealMatrix computeIOCorrelationMatrix(SampleIterator it) {
		it.reset();
		
		List<double[]> Xlist = new ArrayList<>();
		double[][] X = null;
		int outputDim = it.next().getEncodedOutput().getDimension();
		int inputDim = it.next().getEncodedInput().getDimension();
		
		RealMatrix M = new Array2DRowRealMatrix(inputDim, outputDim);

		for(int col = 0; col < outputDim; col++) {
			it.reset();
			RealVector y = new ArrayRealVector();
			for(int i = 0; i < maxSamples && it.hasNext(); i++) {
				Sample sample = it.next();
				y = y.append(sample.getEncodedOutput().getEntry(col));
				if (col == 0) // Do it only once
					Xlist.add(injectNoise(sample.getEncodedInput().toArray()));
			}
			if (col == 0)
				X = Xlist.toArray(new double[][]{});
			
			M.setColumnVector(col, computeIOCorrelation(X, y.toArray(), new ArrayList<Integer>()));
		}
		
		return M;
	}

	@Override
	public RealVector computeSyntheticValues(SampleIterator it) {
		RealMatrix input = computeInputCorrelationMatrix(it);
		
		List<Integer> nanIndices = findNanIndices(input);
		Matrix adjustedInput = new Matrix(removeNaNIndices(input, nanIndices).getData());
		
		RealMatrix io = computeIOCorrelationMatrix(it);
		
		if (adjustedInput.rank() < adjustedInput.getRowDimension()) {
			Log.write(this, "Some error occourred (input correlation matrix is singular)");
			return null;
		}
		
		Matrix inverse = adjustedInput.inverse();

		RealVector v = new ArrayRealVector(io.getColumnDimension());
		for(int col = 0; col < v.getDimension(); col++)
			v.setEntry(col, determination(inverse, removeNanIndices(io.getColumnVector(col), nanIndices), count(it)));
		
		return v;
	}
	
	private int count(SampleIterator it) {
		it.reset();
		int count = 0;
		for(int i = 0; i < maxSamples && it.hasNext(); i++) {
			it.next();
			count++;
		}
		return count;
	}

	private double determination(Matrix inverse, Matrix vec, int samples) {
		double R2 = vec.transpose().times(inverse).times(vec).get(0, 0);
		if (adjust) {
			R2 = 1 - (1-R2)*(samples - 1)/(samples - inverse.getColumnDimension() - 1);
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

	private Matrix removeNanIndices(RealVector vec, List<Integer> nanIndices) {
		Matrix ret = new Matrix(vec.getDimension()-nanIndices.size(), 1);
		int j = 0;
		for(int i = 0; i < vec.getDimension(); i++)
			if (nanIndices.contains(i))
				continue;
			else
				ret.set(j++, 0, vec.getEntry(i));
		return ret;
	}
	
	private RealMatrix removeNaNIndices(RealMatrix base, List<Integer> nanIndices) {
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
			ret.setEntry(pos++, correlation.correlation(x, y));
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

}
