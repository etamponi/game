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

import org.apache.commons.math3.linear.RealVector;

public class Sample {

	private Object input;
	private Object output;
	private Object prediction;
	private RealVector encodedInput;
	private RealVector encodedOutput;
	private RealVector encodedPrediction;
	
	public Sample(Object input, Object output) {
		this.input = input;
		this.output = output;
	}
	
	public Sample(Object input, Object output, Object prediction) {
		this.input = input;
		this.output = output;
		this.prediction = prediction;
	}
	
	public Sample(Object input, RealVector encodedInput, Object output, RealVector encodedOutput) {
		this.input = input;
		this.output = output;
		this.encodedInput = encodedInput;
		this.encodedOutput = encodedOutput;
	}
	
	public Sample(Object input, RealVector encodedInput, Object output, RealVector encodedOutput, Object prediction, RealVector encodedPrediction) {
		this.input = input;
		this.output = output;
		this.prediction = prediction;
		this.encodedInput = encodedInput;
		this.encodedOutput = encodedOutput;
		this.encodedPrediction = encodedPrediction;
	}
	
	public Object getInput() {
		return input;
	}
	
	public Object getOutput() {
		return output;
	}
	
	public Object getPrediction() {
		return prediction;
	}

	public RealVector getEncodedInput() {
		return encodedInput;
	}

	public RealVector getEncodedOutput() {
		return encodedOutput;
	}

	public RealVector getEncodedPrediction() {
		return encodedPrediction;
	}
	
}
