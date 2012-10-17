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
	
}
