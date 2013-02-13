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


public class Instance {
	
	private Data source;
	
	private Data target;
	
	private Data prediction;
	
	public Instance() {
		
	}
	
	public Instance(Data input) {
		this.source = input;
	}
	
	public Instance(Data source, Data target) {
		this.source = source;
		this.target = target;
	}
	
	public Instance(Data source, Data target, Data prediction) {
		this.source = source;
		this.target = target;
		this.prediction = prediction;
	}

	public Data getSource() {
		return source;
	}

	public void setSource(Data source) {
		this.source = source;
	}

	public Data getTarget() {
		return target;
	}

	public void setTarget(Data target) {
		this.target = target;
	}

	public Data getPrediction() {
		return prediction;
	}

	public void setPrediction(Data prediction) {
		this.prediction = prediction;
	}
	
}
