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
package game.core;

import java.util.ArrayList;

public class Encoding extends ArrayList<double[]> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3017584392455582111L;
	
	public Encoding() {
		super();
	}
	
	public Encoding(double[] element) {
		super();
		add(element);
	}
	
	public int getElementSize() {
		return !isEmpty() ? get(0).length : 0;
	}
	
	public int length() { return size(); }
	
	public Encoding makeWindowedEncoding(int windowSize) {
		assert(windowSize > 0 && windowSize % 2 == 1);
		
		Encoding ret = new Encoding();
		
		for(int i = 0; i < size(); i++)
			ret.add(makeWindow(i, windowSize));
		
		return ret;
	}
	
	public Encoding makeInterpolatedEncoding(int windowSize) {
		return null; // FIXME
	}
	
	public Encoding makeTrimmedEncoding(int windowSize) {
		assert(windowSize > 0 && windowSize % 2 == 1 && getElementSize() % windowSize == 0);
		
		Encoding ret = new Encoding();
		
		int newElementSize = getElementSize() / windowSize;
		int startingPos = (windowSize / 2)*newElementSize;
		for(double[] element: this) {
			double[] newElement = new double[newElementSize];
			System.arraycopy(element, startingPos, newElement, 0, newElementSize);
			ret.add(newElement);
		}
		
		return ret;
	}
	
	private double[] makeWindow(int center, int windowSize) {
		double[] ret = new double[windowSize*getElementSize()];
		
		int halfWindow = windowSize / 2;
		for(int k = center-halfWindow, j = 0; k < length() && k <= center+halfWindow; k++, j++) {
			if (k < 0)
				continue;
			System.arraycopy(get(k), 0, ret, j*getElementSize(), getElementSize());
		}
		
		return ret;
	}
	
}
