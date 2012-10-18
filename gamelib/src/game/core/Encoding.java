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
package game.core;

import java.util.Iterator;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class Encoding extends Array2DRowRealMatrix implements Iterable<RealVector> {

	private static final long serialVersionUID = -2301854648600245030L;
	
	@SuppressWarnings("unused")
	private Encoding() {}
	
	public Encoding(int featureNumber, int length) {
		super(featureNumber, length);
	}
	
	public Encoding(RealMatrix clone) {
		super(clone.getData());
	}
	
	public int getFeatureNumber() {
		return this.getRowDimension();
	}
	
	public int length() {
		return this.getColumnDimension();
		}
	
	public RealVector getElement(int index) {
		return this.getColumnVector(index);
	}
	
	public void setElement(int index, RealVector value) {
		this.setColumnVector(index, value);
	}
	
	public Encoding makeWindowedEncoding(int windowSize) {
		Encoding ret = new Encoding(windowSize*getFeatureNumber(), length());
		
		for(int i = 0; i < length(); i++)
			ret.setElement(i, makeWindow(i, windowSize));
		
		return ret;
	}
	
	public Encoding makeInterpolatedEncoding(int windowSize) {
		return null; // FIXME makeInterpolatedEncoding
	}
	
	public Encoding makeTrimmedEncoding(int initialWindowSize, int finalWindowSize) {
		assert(getFeatureNumber() % initialWindowSize == 0);

		int baseFeatureNumber = getFeatureNumber() / initialWindowSize;
		int finalFeatureNumber = baseFeatureNumber * finalWindowSize;
		
		Encoding ret = new Encoding(finalFeatureNumber, length());
		
		int startingPos = ((initialWindowSize - finalWindowSize) / 2 - ((initialWindowSize+finalWindowSize) % 2 == 1 ? 1 : 0))*baseFeatureNumber;
		for(int j = 0; j < length(); j++) {
			RealVector newElement = this.getElement(j).getSubVector(startingPos, finalFeatureNumber);
			ret.setColumnVector(j, newElement);
		}
		
		return ret;
	}
	
	private RealVector makeWindow(int center, int windowSize) {
		RealVector ret = new ArrayRealVector(windowSize*getFeatureNumber());
		
		int halfWindow = windowSize / 2 - (windowSize % 2 == 0 ? 1 : 0);
		for(int k = center-halfWindow, j = 0; k < length() && k < (center-halfWindow+windowSize); k++, j++) {
			if (k < 0)
				continue;
			ret.setSubVector(j*getFeatureNumber(), this.getColumnVector(k));
		}
		
		return ret;
	}

	@Override
	public Iterator<RealVector> iterator() {
		return new Iterator<RealVector>() {
			
			private int column = 0;
			
			@Override
			public boolean hasNext() {
				return column < length();
			}

			@Override
			public RealVector next() {
				return getElement(column++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Cannot remove an element from an Encoding");
			}
		};
	}

}
