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

import java.util.ArrayList;

public abstract class DataTemplate extends Configurable {
	
	public static class Data<T, DT> extends ArrayList<T> {
		
		private DT template;
		
		protected Data() {}

		protected Data(DT template) {
			this.template = template;
		}
		
		protected DT getTemplate() {
			return template;
		}
		
	}
	
	public boolean sequence = false;
	
	public abstract int getDescriptionLength();
	
	public abstract Data newDataInstance();
	
}
