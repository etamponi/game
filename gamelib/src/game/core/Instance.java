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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Instance implements Serializable {
	
	private static final long serialVersionUID = -9123652705641571185L;

	private List input;
	
	private List output;
	
	private List prediction;
	
	private Encoding predictionEncoding;
	
	public Instance() {
		
	}
	
	public Instance(List input, List output) {
		this.input = input;
		this.output = output;
	}
	
	public Instance(Object singleInput, Object singleOutput) {
		this.input = new ArrayList<>();
		this.output = new ArrayList<>();
		this.input.add(singleInput);
		this.output.add(singleOutput);
	}

	public List getInput() {
		return input;
	}

	public void setInput(List input) {
		this.input = input;
	}

	public List getOutput() {
		return output;
	}

	public void setOutput(List output) {
		this.output = output;
	}

	public List getPrediction() {
		return prediction;
	}

	public void setPrediction(List prediction) {
		this.prediction = prediction;
	}

	public Encoding getPredictionEncoding() {
		return predictionEncoding;
	}

	public void setPredictionEncoding(Encoding predictionEncoding) {
		this.predictionEncoding = predictionEncoding;
	}

}
