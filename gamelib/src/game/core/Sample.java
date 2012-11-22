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

public class Sample<I, O> {

	private I input;
	private O output;
	private O prediction;
	private RealVector encodedInput;
	private RealVector encodedOutput;
	private RealVector encodedPrediction;
	
	public Sample(I input, O output) {
		this.input = input;
		this.output = output;
	}
	
	public Sample(I input, O output, O prediction) {
		this.input = input;
		this.output = output;
		this.prediction = prediction;
	}
	
	public Sample(I input, RealVector encodedInput, O output, RealVector encodedOutput) {
		this.input = input;
		this.output = output;
		this.encodedInput = encodedInput;
		this.encodedOutput = encodedOutput;
	}
	
	public Sample(I input, RealVector encodedInput, O output, RealVector encodedOutput, O prediction, RealVector encodedPrediction) {
		this.input = input;
		this.output = output;
		this.prediction = prediction;
		this.encodedInput = encodedInput;
		this.encodedOutput = encodedOutput;
		this.encodedPrediction = encodedPrediction;
	}
	
	public I getInput() {
		return input;
	}
	
	public O getOutput() {
		return output;
	}
	
	public O getPrediction() {
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
