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

import java.util.ArrayList;
import java.util.Collection;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.ios.IOSSerializer;

public class DatasetSerializer extends IOSSerializer<Dataset> {

	@Override
	public void write(Kryo kryo, Output out, Dataset object) {
		kryo.writeObject(out, object.getTemplate());
		out.writeInt(object.size());
		for(Instance i: object) {
			kryo.writeObject(out, new ArrayList(i.getInput()));
			kryo.writeObject(out, new ArrayList(i.getOutput()));
			kryo.writeObjectOrNull(out, i.getPrediction() == null ? null : new ArrayList(i.getPrediction()), ArrayList.class);
			kryo.writeObjectOrNull(out, i.getPredictionEncoding(), Encoding.class);
		}
	}

	@Override
	public Dataset read(Kryo kryo, Input in, Class<Dataset> type) {
		kryo.reference(new Dataset(null));
		
		InstanceTemplate template = kryo.readObject(in, InstanceTemplate.class);
		int size = in.readInt();
		
		Dataset ret = new Dataset(template);
		while(size-- > 0) {
			Instance i = template.newInstance();
			
			Data input = template.inputTemplate.newData();
			input.addAll(kryo.readObject(in, ArrayList.class));
			
			Data output = template.outputTemplate.newData();
			output.addAll(kryo.readObject(in, ArrayList.class));
			
			Data prediction = null;
			Collection temp = kryo.readObjectOrNull(in, ArrayList.class);
			if (temp != null) {
				prediction = template.outputTemplate.newData();
				prediction.addAll(temp);
			}
			
			Encoding encoding = kryo.readObjectOrNull(in, Encoding.class);
			
			i.setInput(input);
			i.setOutput(output);
			i.setPrediction(prediction);
			i.setPredictionEncoding(encoding);
			
			ret.add(i);
		}
		
		return ret;
	}
	
}
