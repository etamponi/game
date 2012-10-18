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

import game.core.DataTemplate.Data;

public class Instance {
	
	private Data input;
	
	private Data output;
	
	private Data prediction;
	
	private Encoding predictionEncoding;
	
	Instance() {
		
	}
	
	Instance(Data input, Data output) {
		this.input = input;
		this.output = output;
	}
	
	public <D extends Data> D getInput() {
		return (D)input;
	}

	public void setInput(Data input) {
		this.input = input;
	}

	public <D extends Data> D getOutput() {
		return (D)output;
	}

	public void setOutput(Data output) {
		this.output = output;
	}

	public <D extends Data> D getPrediction() {
		return (D)prediction;
	}

	public void setPrediction(Data prediction) {
		this.prediction = prediction;
	}

	public Encoding getPredictionEncoding() {
		return predictionEncoding;
	}

	public void setPredictionEncoding(Encoding predictionEncoding) {
		this.predictionEncoding = predictionEncoding;
	}

}
