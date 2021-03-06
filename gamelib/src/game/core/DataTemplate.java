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

import game.configuration.Configurable;

import java.util.ArrayList;

public abstract class DataTemplate extends Configurable {
	
	public abstract class Data<T> extends ArrayList<T> {
		
		protected Data() {}
		
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
