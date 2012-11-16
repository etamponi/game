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


import java.util.ArrayList;
import java.util.Collection;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.ios.IObject;

public abstract class DataTemplate extends IObject {
	
	public static class DataSerializer extends Serializer<Collection> {

		@Override
		public Collection read(Kryo kryo, Input input, Class<Collection> type) {
			Class templateType = type.getDeclaringClass();
			DataTemplate template = kryo.readObject(input, templateType);
			Data data = template.newData();
			data.addAll(kryo.readObject(input, ArrayList.class));
			return data;
		}

		@Override
		public void write(Kryo kryo, Output output, Collection object) {
			Data data = (Data)object;
			kryo.writeObject(output, data.template);
			kryo.writeObject(output, new ArrayList<>(object));
		}

		@Override
		public Collection copy(Kryo kryo, Collection object) {
			DataTemplate template = ((Data)object).template;
			Data data = template.newData();
			data.addAll(object);
			return data;
		}
		
	}
	
	static {
		IObject.getKryo().addDefaultSerializer(Data.class, DataSerializer.class);
	}

	public abstract class Data<T> extends ArrayList<T> {
		
		private DataTemplate template = DataTemplate.this;
		
		protected abstract Class getElementType();
		
		public int length() {
			return size();
		}
		
		@Override
		public String toString() {
			return DataTemplate.this.toString(this);
		}
		
		@Override
		public boolean add(T element) {
			if (!getElementType().isAssignableFrom(element.getClass()))
				return false;
			else
				return super.add(element);
		}
		
	}
	
	public boolean sequence = false;
	
	public abstract int getDescriptionLength();
	
	public abstract Data newData();
	
	protected String toString(Data data) {
		StringBuilder builder = new StringBuilder();
		for (Object o: data)
			builder.append(o).append(" ");
		
		return builder.toString();
	}
	
}
