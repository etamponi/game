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

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import com.ios.IObject;

public abstract class CorrelationCoefficient extends IObject {
	
	public double noiseSd = 1e-10;
	
	public int maxSamples = 10000;
	
	private NormalDistribution distribution = new NormalDistribution(0, noiseSd);
	
	public abstract RealMatrix computeInputCorrelationMatrix(SampleIterator it);
	
	public abstract RealMatrix computeIOCorrelationMatrix(SampleIterator it);
	
	public abstract RealVector computeSyntheticValues(SampleIterator it);
	
	protected double injectNoise(double v) {
		if (distribution.getStandardDeviation() != noiseSd)
			distribution = new NormalDistribution(0, noiseSd); 
		
		return v + distribution.sample();
	}
	
	protected double[] injectNoise(double[] v) {
		for(int i = 0; i < v.length; i++)
			v[i] = injectNoise(v[i]);
		return v;
	}

}
