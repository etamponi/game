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

import game.configuration.Configurable;
import game.core.DataTemplate.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InstanceTemplate extends Configurable {
	
	public DataTemplate inputTemplate;
	
	public DataTemplate outputTemplate;
	
	public Instance newInstance() {
		return new Instance();
	}

	public Instance newInstance(Data input, Data output) {
		return new Instance(input, output);
	}

	public Instance newInstance(Object singleInput, Object singleOutput) {
		Data input = inputTemplate.newDataInstance();
		Data output = outputTemplate.newDataInstance();
		input.add(singleInput);
		output.add(singleOutput);
		return new Instance(input, output);
	}
	
	public static class SerializableInstance implements Serializable {
		public List input, output, prediction;
		public Encoding predictionEncoding;
		
		private SerializableInstance() {}
	}

	public Instance deserialize(Object obj) {
		SerializableInstance object = (SerializableInstance)obj;
		
		Data input = inputTemplate.newDataInstance();
		input.addAll(object.input);
		Data output = outputTemplate.newDataInstance();
		output.addAll(object.output);
		
		Instance ret = new Instance(input, output);
		
		if (object.prediction != null) {
			Data prediction = outputTemplate.newDataInstance();
			prediction.addAll(object.prediction);
			ret.setPrediction(prediction);
		}
		if (object.predictionEncoding != null) {
			ret.setPredictionEncoding(object.predictionEncoding);
		}
		
		return ret;
	}

	public Object serialize(Instance instance) {
		SerializableInstance object = new SerializableInstance();
		object.input = new ArrayList<>(instance.getInput());
		object.output = new ArrayList<>(instance.getOutput());
		if (instance.getPrediction() != null)
			object.prediction = new ArrayList<>(instance.getPrediction());
		if (instance.getPredictionEncoding() != null)
			object.predictionEncoding = instance.getPredictionEncoding();
		return object;
	}
	
}
