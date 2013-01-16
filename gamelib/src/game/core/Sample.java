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

	private Element source;
	private Element target;
	private Element prediction;
	
	public Sample(Element source, Element target) {
		this.source = source;
		this.target = target;
	}
	
	public Sample(Element source, Element target, Element prediction) {
		this.source = source;
		this.target = target;
		this.prediction = prediction;
	}

	public Element getSource() {
		return source;
	}

	public void setSource(Element source) {
		this.source = source;
	}

	public Element getTarget() {
		return target;
	}

	public void setTarget(Element target) {
		this.target = target;
	}

	public Element getPrediction() {
		return prediction;
	}

	public void setPrediction(Element prediction) {
		this.prediction = prediction;
	}
	
}
