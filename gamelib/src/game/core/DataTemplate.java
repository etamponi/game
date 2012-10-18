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
	
	public class Data<T> extends ArrayList<T> {
		
		protected Data() {}
		
		public int length() {
			return size();
		}
		
	}
	
	public boolean sequence = false;
	
	public abstract int getDescriptionLength();
	
	public abstract Data newDataInstance();
	
}
