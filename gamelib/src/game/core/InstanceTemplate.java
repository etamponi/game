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

import game.core.DataTemplate.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ios.IObject;

public class InstanceTemplate extends IObject {
	
	public DataTemplate inputTemplate;
	
	public DataTemplate outputTemplate;
	
	public Instance newInstance() {
		return new Instance();
	}

	public Instance newInstance(Data input, Data output) {
		return new Instance(input, output);
	}

	public Instance newInstance(Object singleInput, Object singleOutput) {
		Data input = inputTemplate.newData();
		Data output = outputTemplate.newData();
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
		
		Data input = inputTemplate.newData();
		input.addAll(object.input);
		Data output = outputTemplate.newData();
		output.addAll(object.output);
		
		Instance ret = new Instance(input, output);
		
		if (object.prediction != null) {
			Data prediction = outputTemplate.newData();
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
