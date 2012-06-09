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
	
	private Object inputData;
	
	private Object outputData;
	
	private Object predictedData;

	public Object getInputData() {
		return inputData;
	}

	public void setInputData(Object inputData) {
		this.inputData = inputData;
	}

	public Object getOutputData() {
		return outputData;
	}

	public void setOutputData(Object outputData) {
		this.outputData = outputData;
	}

	public Object getPredictedData() {
		return predictedData;
	}

	public void setPredictedData(Object predictedData) {
		this.predictedData = predictedData;
	}

}
