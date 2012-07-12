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

public class Sample {

	private Object input;
	private Object output;
	private Object prediction;
	private double[] encodedInput;
	private double[] encodedOutput;
	private double[] encodedPrediction;
	
	public Sample(Object input, Object output) {
		this.input = input;
		this.output = output;
	}
	
	public Sample(Object input, Object output, Object prediction) {
		this.input = input;
		this.output = output;
		this.prediction = prediction;
	}
	
	public Sample(Object input, double[] encodedInput, Object output, double[] encodedOutput) {
		this.input = input;
		this.output = output;
		this.encodedInput = encodedInput;
		this.encodedOutput = encodedOutput;
	}
	
	public Sample(Object input, double[] encodedInput, Object output, double[] encodedOutput, Object prediction, double[] encodedPrediction) {
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

	public double[] getEncodedInput() {
		return encodedInput;
	}

	public double[] getEncodedOutput() {
		return encodedOutput;
	}

	public double[] getEncodedPrediction() {
		return encodedPrediction;
	}
	
}
