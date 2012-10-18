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
package game.configuration;

import com.thoughtworks.xstream.converters.Converter;

public abstract class BaseConverter implements Converter {
	
	private ClassLoader classLoader = getClass().getClassLoader();

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public boolean equals(Object other) {
		return this.getClass().equals(other.getClass());
	}

}
