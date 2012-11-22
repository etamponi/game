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
package game.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import weka.classifiers.Classifier;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.ios.IOSSerializer;



public class WekaClassifierSerializer extends IOSSerializer<Classifier> {

	@Override
	public Classifier read(Kryo kryo, Input input, Class<Classifier> type) {
		try {
			return (Classifier)new ObjectInputStream(new ByteArrayInputStream(input.readBytes(input.readInt()))).readObject();
		}  catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void write(Kryo kryo, Output output, Classifier object) {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(stream);
			oos.writeObject(object);
			oos.flush();
			oos.close();
			output.writeInt(stream.size());
			output.write(stream.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Classifier copy(Kryo kryo, Classifier object) {
		try {
			return Classifier.makeCopy(object);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}

